/* 
 * Copyright (c) 2002-2020 Cedefop.
 * 
 * This file is part of EWA (Cedefop).
 * 
 * EWA (Cedefop) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * EWA (Cedefop) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with EWA (Cedefop). If not, see <http ://www.gnu.org/licenses/>.
 */
package europass.ewa.services.editor.files;

import com.google.common.base.Strings;
import com.google.inject.name.Named;
import com.itextpdf.text.io.RandomAccessSource;
import com.itextpdf.text.io.RandomAccessSourceFactory;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;
import europass.ewa.enums.ConversionFileType;
import europass.ewa.enums.LogFields;
import europass.ewa.enums.PDFLibrary;
import europass.ewa.enums.ServerModules;
import europass.ewa.logging.ExtraLogInfo;
import europass.ewa.model.FileData;
import europass.ewa.model.Metadata;
import europass.ewa.model.wrapper.Feedback;
import europass.ewa.model.wrapper.FeedbackFactory;
import europass.ewa.model.wrapper.FiledataWrapper;
import europass.ewa.services.Paths;
import europass.ewa.services.annotation.EWAEditor;
import europass.ewa.services.enums.FileStatus;
import europass.ewa.services.enums.PhotoStatus;
import europass.ewa.services.exception.*;
import europass.ewa.services.exception.FileNotFoundException;
import europass.ewa.services.files.*;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response.Status;
import java.io.*;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;

public class SessionDiskFileRepository implements FileRepository {

    private static final int ID_DIGEST_LENGTH = 16;

    private static final int MIN_FILENAME_LENGTH = 40;

    private static final int MIN_COOKIE_ID_LENGTH = 20;

    private static final int MIN_FILE_ID_LENGTH = 20;

    private static final String NOT_VALID_FILENAME = "NOT_VALID_FILENAME";

    private static final Logger LOG = LoggerFactory.getLogger(SessionDiskFileRepository.class);

    private static final String module = ServerModules.SERVICES_EDITORS.getModule();

    private String ua;

    private final Provider<HttpServletRequest> httpRequest;

    private final SizeLimitation sizeLimitationBehavior;
    private final ImageProcessing imageProcessingBehavior;

    private final String repoPath;
    private final String isJPedalEnabled;
    private final String apiBase;

    public final static Object filesLock = new Object();

    @Inject
    public SessionDiskFileRepository(@EWAEditor SizeLimitation sizeLimitationBehavior, ImageProcessing imageProcessingBehavior,
            Provider<HttpServletRequest> httpRequest,
            @Named(FileUploadsModule.FILE_TMP_REPOSITORY_PARAM) String repoPath,
            @Named("europass-ewa-services.api.url") String apiBase,
            @Named("europass-ewa-services.pdf.library.jpedal.enabled") String isJPedalEnabled) {

        this.httpRequest = httpRequest;
        ua = httpRequest.get().getHeader("user-agent");

        this.sizeLimitationBehavior = sizeLimitationBehavior;
        this.imageProcessingBehavior = imageProcessingBehavior;

        this.repoPath = repoPath;
        this.apiBase = apiBase;
        this.isJPedalEnabled = isJPedalEnabled;
    }

    @Override
    public SizeLimitation sizeLimitationBehavior() {
        return sizeLimitationBehavior;
    }

    @Override
    public ImageProcessing imageProcessingBehavior() {
        return imageProcessingBehavior;
    }

    @Override
    public boolean isOnRepository(String fileId) {
        if (Strings.isNullOrEmpty(fileId)) {
            return false;
        }

        String file = getFileName(fileId);

        File fi = new File(file);
        if (!fi.exists()) {
            return false;
        }

        return true;
    }

    @Override
    public boolean isFileOnRepository(String fileId) throws FileNotFoundException {
        if (Strings.isNullOrEmpty(fileId)) {
            return false;
        }

        boolean isOnRepository = isOnRepository(fileId);

        if (!isOnRepository) {
            LOG.debug(String.format("File with id %s is not found on repository.", fileId));
        }

        return isOnRepository;
    }

    @Override
    public FileData readFile(final String fileId) throws NonApplicableArgument {

        if (Strings.isNullOrEmpty(fileId)) {
            throw ApiException.addInfo(new NonApplicableArgument("Cannot proceed, as no filed id is provided"), LogFields.MODULE, module);
        }

        final byte[] fileDataReadBytes = readFileData(fileId);

        if (fileDataReadBytes != null) {
            final FileData filedata = new FileData();
            filedata.setData(fileDataReadBytes);
            filedata.setMimeType(getMimeTypeFromFileId(fileId));

            return filedata;
        }

        return null;
    }

    private String getMimeTypeFromFileId(String fileId) {
        //get mime type from fileId
        String mimeType = "";

        if (fileId.lastIndexOf(".") > 0) {
            mimeType = fileId.substring(fileId.lastIndexOf(".") + 1).replace("_", "/");
        }

        LOG.debug("Mime type in file id is: " + mimeType);

        return mimeType.equals("") ? null : mimeType;
    }

    @Override
    public FileData readThumb(String id) throws NonApplicableArgument, FileAccessForbiddenException {

        LOG.debug("Inside readThumb");
        if (Strings.isNullOrEmpty(id)) {
            throw ApiException.addInfo(new NonApplicableArgument("Cannot proceed, as no filed id is provided"), LogFields.MODULE, module);
        }

        LOG.debug("Before adding thumb ext, file id: " + id);
        String fileId = id;

        final String mimeType = getMimeTypeFromFileId(fileId);
        if (mimeType.equals(PDF_TYPE)) {
            //remove PDF_TYPE
            final String fileIdNoExtension = fileId.substring(0, fileId.lastIndexOf('.'));
            fileId = fileIdNoExtension + THUMB_EXT;
        }

        final byte[] fileDataBytes = readFileData(fileId);
        final FileData filedata = new FileData();
        if (fileDataBytes != null) {
            filedata.setMimeType(mimeType);
            filedata.setData(fileDataBytes);

            return filedata;
        }
        return null;
    }

    /**
     * Computes a file id for the given data - file id: cookie id + MD5 of data
     * + DateTime
     *
     * @param data
     * @return String
     */
    private String computeFileId(byte[] data, String mimeType, String cookieId) {
        try {
            LOG.debug("Computes a file id with mimeType: " + mimeType);
            //MD5 of data
            MessageDigest complete = MessageDigest.getInstance("MD5");
            int size = data.length;
            complete.update(data, 0, size);

            cookieId += "."; //separate cookieId from fileId

            String md5 = new BigInteger(1, complete.digest()).toString(ID_DIGEST_LENGTH);
            String fileId = cookieId + md5 + (new Date()).getTime();

            LOG.debug("Generated file id: " + fileId.toUpperCase());

            return fileId.toUpperCase() + "." + mimeType.replace("/", "_");
        } catch (NoSuchAlgorithmException e) {
            throw ApiException.addInfo(new ApiException("Failed to compute a file id", e, FileStatus.OTHER.getDescription(), Status.INTERNAL_SERVER_ERROR),
                    new ExtraLogInfo().add(LogFields.UA, ua).add(LogFields.MODULE, module));
        }
    }

    private boolean generateThumb(File pdf, PDFLibrary library) {
        String pdfNoExtension = pdf.getPath().substring(0, pdf.getPath().lastIndexOf('.'));
        String thumbPath = pdfNoExtension + THUMB_EXT;

        LOG.debug("Generated Thumb Path: " + thumbPath);
        return PDFUtils.createThumb(pdf, thumbPath, library, isJPedalEnabled);
    }

    /**
     * Utility to save data on disk based on file-id
     *
     * Synchronized block (create and move file) with checking cumulative size
     * and with the job that cleans up uploads
     *
     * @param fileId
     * @param data
     * @return
     * @throws IOException
     */
    private File saveOnDisk(String fileId, byte[] data) throws IOException {
        // throws NonApplicableArgument if fileId length is less than the limit		
        String path = getFileName(fileId);
        LOG.debug("file path is: " + path);
        if (path.equals(NOT_VALID_FILENAME)) {
            throw ApiException.addInfo(new NonApplicableArgument("Cannot proceed save, as not valid filename/path for saving on disk"), LogFields.MODULE, module);
        }

        File pFile = new File(path);

        LOG.debug("file's parent file: " + pFile.getParentFile().getName());
        FileOutputStream fos = null;

        synchronized (filesLock) {
            try {
                boolean structureCreated = pFile.getParentFile().mkdirs();
                LOG.debug("structure created: " + structureCreated);
                // Write on disk
                fos = new FileOutputStream(pFile);
                fos.write(data);
            } finally {
                if (fos != null) {
                    fos.close();
                }
            }
        }

        return pFile;
    }

    /**
     * Saves the data on disk.
     *
     * Will also handle any size limitations
     *
     */
    @Override
    public FiledataWrapper save(byte[] data, String mimeType, Types type, String prefix, String cookieId) throws ApiException {

        LOG.debug("save method - cookie id: " + cookieId);
        LogFields action = LogFields.ACTION;
        String dimensions = null;

        if (data == null || (data != null && data.length == 0)) {
            throw ApiException.addInfo(new NonApplicableArgument("File bytes are null or empty", FileStatus.PARSING.getDescription(), Status.BAD_REQUEST),
                    new ExtraLogInfo().add(action, "Save").add(LogFields.FILETYPE, mimeType).add(LogFields.MODULE, module));
        }

        // Check permissions
        boolean isPDF = (mimeType.equals(ConversionFileType.PDF.getMimeType()));
        LOG.debug("isPDF: " + isPDF);
        PDFLibrary pdfLibrary = null;
        if (isPDF) {
            //  This will throw an exception if it cannot read:
            //  - FileNotParsableException
            //  - FileNotViewableException
            //  - FileManagePermissionException
            pdfLibrary = PDFUtils.decideLibrary(data, isJPedalEnabled);
            LOG.debug("pdf library name: " + pdfLibrary.name());
        }

        int fileSize = data.length;

        // check if size limits are respected
        // throws FileExceedsLimitException,
        // FileExceedsCummulativeLimitException		
        Path userPath = getUserPath(cookieId);

        int cumulativeSize = (int) getUserFilesCumulativeSize(userPath); // size of users upload directories
        // cannot be larger than Long.MAX_VALUE
        // so casting is ok.					
        LOG.debug("cumulative size for user's upload dir is: " + cumulativeSize);
        boolean acceptableSize = sizeLimitationBehavior.isWithinLimits(fileSize, type, cumulativeSize);
        LOG.debug("acceptable size: " + acceptableSize);

        String fileId = computeFileId(data, mimeType, cookieId);

        FileData fileData = new FileData();
        FiledataWrapper fdWrapper = new FiledataWrapper();
        fileData.setMimeType(mimeType);
        try {

            File pFile = saveOnDisk(fileId, data);

            // Make the thumb of the PDF	
            if (isPDF && pdfLibrary != null) {
                try {
                    LOG.debug("For now not generating any thumbnail for PDFs // This is to be refactored due to heap memory issues with PDFBox.");
                    //generateThumb(pFile, pdfLibrary);
                } catch (ThumbSavingException e) {
                    e.log();		//log the exception and recover					
                    Feedback feedback = FeedbackFactory.attachmentToThumb(fileData.getName()).withError(e);
                    fdWrapper.setFeedback(feedback);	//add the feedback and error trace to the wrapper object
                    fdWrapper.setErrCode(e.getTraceOnly());
                }
            }

            if (fileData.isImage()) {
                try {
                    fileData.setData(data);
                    imageProcessingBehavior.simpleSetDimensions(fileData);
                    fileData.setData(null);
                } catch (NonApplicableArgument | PhotoReadingException e) {
                    throw ApiException.addInfo(new ApiException("Error while resizing Image.", e, PhotoStatus.DIMENSIONS.getDescription(), Status.INTERNAL_SERVER_ERROR),
                            new ExtraLogInfo().add(action, "Save").add(LogFields.FILETYPE, mimeType).add(LogFields.MODULE, module));
                }
            }

            if (fileData.getDimensions() != null) {
                dimensions = fileData.getDimensions().toString();
            }

            // Further fill in filedata			
            String uri = apiBase + Paths.FILES_BASE + prefix + "/" + fileId;
            LOG.debug("temp uri: " + uri);
            fileData.setTmpuri(new URI(uri));
            fileData.setInfo(Metadata.NO_OF_PAGES, String.valueOf(this.countNoPages(mimeType, data)));

        } catch (final URISyntaxException | IllegalArgumentException | IOException e) {
            throw ApiException.addInfo(new ApiException(e, FileStatus.OTHER.getDescription(), Status.INTERNAL_SERVER_ERROR),
                    new ExtraLogInfo().add(action, "Save").add(LogFields.DIMENSIONS, dimensions).add(LogFields.FILETYPE, mimeType).add(LogFields.MODULE, module));
        }
        fdWrapper.setFiledata(fileData);
        return fdWrapper;
    }

    @Override
    public boolean delete(String fileId) throws NonApplicableArgument {
        if (Strings.isNullOrEmpty(fileId)) {
            throw ApiException.addInfo(new NonApplicableArgument("Cannot proceed, as no filed id is provided"),
                    LogFields.MODULE, module);
        }

        boolean deleted = false;
        // Deprecated due to new Stateless API architecture
        // Proceed only if the file is in scope and also on repository
        // otherwise throw exception
        if (isFileOnRepository(fileId)) {
            // Try to remove the file from disk
            String file = getFileName(fileId);
            File fi = new File(file);
            if (!fi.exists()) {
                return false;
            }

            // Delete thumbs if they exist. Use a FilenameFilter to find those.
            // The thumb name should start with the fileId and be followed by
            // the
            // thumb suffix
            if (fi.getParentFile().isDirectory()) {
                File parent = fi.getParentFile();
                List<File> thumbs = Arrays.asList(parent.listFiles(new ThumbFilenameFilter(fileId)));
                for (File thumb : thumbs) {
                    try {
                        FileUtils.forceDelete(thumb);
                    } catch (IOException e) {
                        LOG.debug(String.format("Failed to delete thumb for the file with id %s ", fileId));
                    }
                }
            }
            try {
                deleted = fi.delete();
            } catch (SecurityException e) {
                LOG.debug(String.format("Failed to delete thumb the file with id %s ", fileId));
            }
        }
        return deleted;
    }

    /**
     * Utility to find the file name on disk
     *
     * @param id
     * @return
     */
    private String getFileName(String id) {
        if (id == null || id != null && id.length() < MIN_FILENAME_LENGTH) {
            throw ApiException.addInfo(new NonApplicableArgument(String.format("The minimum file id length is %d", MIN_FILENAME_LENGTH)),
                    LogFields.MODULE, module);
        }

        String cookieId = id.substring(0, id.indexOf('.'));

        String fileId = id.substring(id.indexOf('.') + 1);

        if (cookieId.length() < MIN_COOKIE_ID_LENGTH) {
            throw ApiException.addInfo(new NonApplicableArgument(String.format("The minimum cookie id length is %d", MIN_COOKIE_ID_LENGTH)),
                    LogFields.MODULE, module);
        }

        String fileName = StringUtils.EMPTY;
        if (fileId.length() < MIN_FILE_ID_LENGTH) {
            fileName = NOT_VALID_FILENAME;
            LOG.debug(String.format("Not a valid path/filename - possibly due to previous old filepath format ... fileId = %s, cookieId= %s", fileId, cookieId));
        } else {
            fileName = getUserUploadsRepo(cookieId) + File.separator + fileId;
            LOG.debug("Getting valid file name: " + fileName);
        }

        return fileName;
    }

    private String getUserUploadsRepo(String cookieId) {
        String userUploadsRepo;
        if (cookieId != null && StringUtils.isNotBlank(cookieId) && cookieId.length() == 36) {//cookie ids consist of 36 characters
            userUploadsRepo = repoPath + File.separator + cookieId.charAt(0) + File.separator + cookieId.charAt(1)
                    + File.separator + cookieId.charAt(2) + File.separator + cookieId;
        } else if (cookieId == null) {
            LOG.info("cookie id: null");
            userUploadsRepo = "";
        } else {
            LOG.info("Cookie id does not minimum length requirements - cookie id: " + cookieId);
            userUploadsRepo = "";
        }
        return userUploadsRepo;
    }

    /**
     * Utility to find a User's root uploads directory
     *
     * @param cookieId
     * @return
     */
    private Path getUserPath(String cookieId) {
        String userUploadsRepo = getUserUploadsRepo(cookieId);
        LOG.debug("users upload Repo: " + userUploadsRepo);

        if (userUploadsRepo.length() < MIN_COOKIE_ID_LENGTH) {
            throw ApiException.addInfo(new NonApplicableArgument(String.format("The minimum cookie id length is %d", MIN_COOKIE_ID_LENGTH)),
                    LogFields.MODULE, module);
        }

        Path userPath = java.nio.file.Paths.get(userUploadsRepo);

        if (userPath == null) {
            throw ApiException.addInfo(new FileNotFoundException(String.format("Cannot get Path from file %s", userPath)),
                    LogFields.MODULE, module);
        }

        return userPath;
    }

    /**
     * Utility to calculate the size of a user's root file uploads directory
     *
     * @param path
     * @return
     */
    private long getUserFilesCumulativeSize(Path path) {
        long size = 0;
        File usersRoot = path.toFile();

        //check if usersRoot exists - It does not at 1st time a user uploads a file
        if (usersRoot.exists()) {
            LOG.debug("user's directory " + usersRoot.getName() + "exists");
            synchronized (filesLock) {
                for (File file : usersRoot.listFiles()) { //iterate through user's files
                    if (file.isFile()) {
                        size += file.length();
                    }
                }
            }
        }
        return size;
    }

    /**
     * Returns the number of pages the specific upload will occupy. For the case
     * of images, this is 1, while for the case of PDF uploads, it varies.
     *
     * For specifically we use the RandomAccessFileOrArray implementation to
     * open a PdfReader, as this improves efficiency, as it will only read the
     * xrefs (mostly required), but not parse anything until we start requesting
     * specific objects.
     *
     * @param fileBytes
     * @return
     * @throws IOException
     */
    private int countNoPages(String fileType, byte[] fileBytes) throws IOException {

        if (fileType.equals(ConversionFileType.PDF.getMimeType())) {
            RandomAccessSourceFactory rasf = new RandomAccessSourceFactory();
            RandomAccessSource source = rasf.createSource(fileBytes);
            RandomAccessFileOrArray file = new RandomAccessFileOrArray(source);
            PdfReader reader = new PdfReader(file, null);
            int noPages = reader.getNumberOfPages();
            reader.close();
            file.close();

            return noPages;
        } else {
            return 1;
        }
    }

    /**
     * Read byte[] from disk
     *
     * @param fileId
     * @return
     */
    private byte[] readFileData(String fileId) throws FileNotFoundException {
        FileInputStream fis = null;
        try {
            // Find the file on disk
            LOG.debug("just before finding the file on disk");
            String fileName = getFileName(fileId);
            File file = new File(fileName);

            if (!file.exists()) {
                LOG.debug(String.format("File with id %s is not found on repository.", fileId));
                return null;
            } else {
                //modify last access time
                long currentTime = System.currentTimeMillis();
                Path path = file.toPath();
                FileTime fileTime = FileTime.fromMillis(currentTime);
                Files.setAttribute(path, "basic:lastAccessTime", fileTime, NOFOLLOW_LINKS);
            }

            fis = new FileInputStream(file);

            byte fileBytes[] = new byte[(int) file.length()];
            fis.read(fileBytes);

            return fileBytes;

        } catch (IOException e) {
            LOG.debug(String.format("Failed to read the bytes from file with id %s", fileId), e);
            return null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                }
            }
        }
    }

    static class ThumbFilenameFilter implements FilenameFilter {

        private final String originalFileId;

        ThumbFilenameFilter(String originalFileId) {
            this.originalFileId = originalFileId;
        }

        /**
         * @param directory : the directory in which the file was found
         * @param filename : the name of the file
         */
        @Override
        public boolean accept(File directory, String filename) {

            boolean accept = false;

            try {
                final String fileId = originalFileId.substring(originalFileId.indexOf('.') + 1);
                final String fileIdWithoutExt = fileId.substring(0, fileId.indexOf("."));
                final String cookieId = originalFileId.substring(0, originalFileId.indexOf('.'));

                accept = directory.getAbsolutePath().endsWith(cookieId)
                        && filename.endsWith(THUMB_EXT)
                        && filename.startsWith(fileIdWithoutExt);
            } catch (Exception e) {
                LOG.error("Exception during ThumbFilenameFilter .. ", e);
            }

            return accept;
        }
    }

    @Override
    public FileData readPhotoData(final FileData photodata) throws FileNotFoundException, FileAccessForbiddenException,
            PhotoCroppingException, ThumbSavingException {
        final String fileId = photodata.fileID();
        final String mimeType = photodata.getMimeType();
        final String dimensions = Arrays.toString(photodata.getDimensions());

        LOG.debug("filedata tempURI: " + photodata.getTmpuri() + "\t"
                + "id: " + fileId + "\t"
                + "mime: " + mimeType + "\t"
                + "dimensions: " + dimensions + "\t"
                + "data name: " + photodata.getName());

        try {
            final Metadata croppingObj = photodata.getMetadataObj(Metadata.CROPPING);
            // Before checking for cropping info, read data as is from disk
            photodata.setData(readFileData(fileId));

            if (photodata.getData() == null || photodata.getData().length == 0) {
                LOG.debug("Cannot crop a null or empty byte array.");
            } else {
                if (croppingObj != null) {
                    // CROP!
                    // Parse the cropping value as json
                    final JSONObject cropping = new JSONObject(croppingObj.getValue());

                    /**
                     * The cropping information exploits the start and end
                     * cropping points as: Starting point: (x,y) Ending point:
                     * (x2,y2)
                     */
                    final TwoDimensionalPoints points = new TwoDimensionalPoints(cropping.getInt(Metadata.CROPPING_START_X),
                            cropping.getInt(Metadata.CROPPING_START_Y),
                            cropping.getInt(Metadata.CROPPING_START_X2),
                            cropping.getInt(Metadata.CROPPING_START_Y2));
                    imageProcessingBehavior.crop(photodata, points);
                }
            }
        } catch (JSONException e) {
            throw ApiException.addInfo(new PhotoCroppingException(e),
                    new ExtraLogInfo().add(LogFields.ACTION, "Photo Data Read").add(LogFields.FILETYPE, mimeType).add(LogFields.MODULE, module).add(LogFields.DIMENSIONS, dimensions));
        }
        return photodata;
    }
}
