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
package europass.ewa.conversion.odt;

import java.awt.color.CMMException;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import javax.inject.Named;
import javax.media.jai.JAI;
import javax.xml.transform.Transformer;

import org.imgscalr.Scalr;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

import europass.ewa.conversion.exception.ODTAssemblyException;
import europass.ewa.conversion.exception.ODTTemplateNotFoundException;
import europass.ewa.conversion.modules.ConversionModule;
import europass.ewa.model.Attachment;
import europass.ewa.model.ByteMetadata;
import europass.ewa.model.FileData;
import europass.ewa.model.Identification;
import europass.ewa.model.Metadata;
import europass.ewa.model.SkillsPassport;

/**
 * Generate an ODT document from a {@link SkillsPassport} model object.
 *
 * @author avah
 *
 */
public abstract class ODTGenerator {

    private final ODTMustacheFactory factory;

    private Map<Locale, ODTMustache> mustaches = new HashMap<Locale, ODTMustache>();

    private final Transformer htmlTransformer;

    private String basePath;

    private static final String PHOTO = "photo";
    private static final String SIGNATURE = "signature";

    @Inject
    public ODTGenerator(ODTMustacheFactory factory,
            @Named(ConversionModule.HTML_TO_ODT_XSLT) Transformer htmlTransformer) {
        super();

        this.factory = factory;

        this.htmlTransformer = htmlTransformer;
    }

    /**
     * Allows to set the Base Path of this Generator.
     *
     * @param basePath
     * @throws ODTTemplateNotFoundException
     */
    protected void setBasePath(String basePath) {
        this.basePath = basePath;
    }

    public ODTMustache get(Locale locale) {
        try {
            ODTMustache mustache = mustaches.get(locale);
            if (mustache == null) {
                String safeBasePath = basePath + (basePath != null ? "/" : "");
                //Relative fail safety in case there is no template for the specific language.
                factory.setDefaultName(safeBasePath + "default");
                mustache = factory.create(safeBasePath + locale.toString());
                mustaches.put(locale, mustache);
            }
            return mustache;
        } catch (Exception e) {
            throw new ODTTemplateNotFoundException("ODTGenerator: Failed to get ODT template for locale: " + locale, e);
        }

    }

    public void generate(OutputStream out, SkillsPassport document) throws ODTTemplateNotFoundException, ODTAssemblyException {
        //Set the injected transformer to be used in order to convert html to open document xml
        document.setTranformer(htmlTransformer);

        ZipOutputStream zout = new ZipOutputStream(out);
        Locale locale = document.getLocale();
        Locale odtLocale = new Locale(locale == null ? "en" : locale.getLanguage());
        //Case of xx_XX locale, use xx
        ODTMustache mustache = get(odtLocale);

        FileData photo = document.personalPhoto();
        byte[] photoDataBytes = photoScaleCrop(photo, PHOTO);
        if (photo != null && photo.getData() != null) {
            mustache = mustache.add(new ODTFileDataEntry("Pictures/photo", photo.getMimeType(), photoDataBytes));
        }

        FileData signature = document.personalSignature();
        byte[] signatureDataBytes = photoScaleCrop(signature, SIGNATURE);
        if (signature != null && signature.getData() != null) {
            mustache = mustache.add(new ODTFileDataEntry("Pictures/signature", signature.getMimeType(), signatureDataBytes));
        }

        if (document.getAttachmentList() != null) {

            //List that holds the IDs of the attachment objects that have already been populated from the document's attachment list
            List<String> multipleAttachments = new ArrayList<String>();

            for (Attachment att : document.getAttachmentList()) {
                if (att == null) {
                    continue;
                }

                //Check and handling of already populated attachments
                String attachmentID = att.getId();
                if (multipleAttachments.contains(attachmentID)) {
                    continue;
                }

                multipleAttachments.add(attachmentID);

                if (att.isPDF()) {
                    List<ByteMetadata> byteMetas = att.getByteMetadaList();
                    if (byteMetas == null || byteMetas.size() == 0) {
                        continue;
                    }

                    int id = 0;
                    for (ByteMetadata byteMeta : byteMetas) {
                        if (byteMeta == null) {
                            continue;
                        }
                        byte[] data = byteMeta.getData();
                        if (data == null || (data != null && data.length == 0)) {
                            continue;
                        }
                        mustache = mustache.add(
                                new ODTFileDataEntry(
                                        "AttachmentsPDF/" + attachmentID + "_PDF" + id,
                                        "image/png",
                                        data));
                        id++;
                    }
                } else if (att.isImage()) {
                    byte[] data = att.getData();
                    if (data == null || (data != null && data.length == 0)) {
                        continue;
                    }
                    mustache = mustache.add(
                            new ODTFileDataEntry("Pictures/" + att.getId(), att.getMimeType(), data));
                }
            }
        }

        try {
            mustache.execute(zout, document);
            zout.close();
        } catch (IOException e) {
            throw new ODTAssemblyException("ODTGenerator: Failed to prepare ODT", e);
        }
    }

    // ---- UTILITIES
    private static byte[] photoScaleCrop(FileData photodata, String image) {

        byte[] photoDataBytes = {};
        if (photodata == null) {
            return photoDataBytes;
        }

        BufferedImage srcImg = null;
        BufferedImage croppedImg = null;
        photoDataBytes = photodata.getData();

        try {

            String croppingExport = photodata.getMetadata(Metadata.CROPPING_EXPORT);
            srcImg = ImageIO.read(new BufferedInputStream(new ByteArrayInputStream(photodata.getData())));
            if (Strings.isNullOrEmpty(croppingExport)) {

                String cropping = photodata.getMetadata(Metadata.CROPPING);

                if (cropping != null) {

                    croppedImg = imageCrop(photodata, cropping);
                }
                if (croppedImg == null) {
                    int srcWidth = srcImg.getWidth();
                    int srcHeight = srcImg.getHeight();

                    if (image.equals(PHOTO)) {

                        if (srcWidth <= Identification.PHOTO_WIDTH && srcHeight <= Identification.PHOTO_HEIGHT) {
                            croppedImg = srcImg;
                        } else {
                            int[] srcDimensions = {srcWidth, srcHeight};
                            int[] newDimensions = Identification.asCompatiblePhoto(srcDimensions);
                            croppedImg = Scalr.crop(srcImg, 0, 0, newDimensions[0], newDimensions[1], Scalr.OP_ANTIALIAS);
                        }
                    } else if (image.equals(SIGNATURE)) {

                        if (srcWidth <= Identification.SIGNATURE_WIDTH && srcHeight <= Identification.SIGNATURE_HEIGHT) {
                            croppedImg = srcImg;
                        } else {
                            int[] srcDimensions = {srcWidth, srcHeight};
                            int[] newDimensions = Identification.asCompatibleSignature(srcDimensions);
                            croppedImg = Scalr.crop(srcImg, 0, 0, newDimensions[0], newDimensions[1], Scalr.OP_ANTIALIAS);
                        }
                    }
                }
            } else {

                ObjectMapper mapper = new ObjectMapper();
                CroppingInfo cropObj = mapper.readValue(croppingExport, CroppingInfo.class);

                int cropWidthFrom = cropObj.getX();
                int cropHeightFrom = cropObj.getY();

                int cropWidth = cropObj.getWidth();
                int cropHeight = cropObj.getHeight();

                croppedImg = Scalr.crop(srcImg, cropWidthFrom, cropHeightFrom, cropWidth, cropHeight, Scalr.OP_ANTIALIAS);
            }
            String imageType = photodata.getMimeType().substring(photodata.getMimeType().indexOf("/") + 1);
            photoDataBytes = asByteArray(croppedImg, imageType);

        } catch (CMMException ex) {
            // If we failed...use JAI
            srcImg = JAI.create("stream", new BufferedInputStream(new ByteArrayInputStream(photoDataBytes))).getAsBufferedImage();
        } catch (Exception e) {
//			System.out.println("Failed to read bytes as image"+ e);
//			throw ApiException.addInfo( new PhotoReadingException(e), LogFields.MODULE, module);
        }

        return photoDataBytes;
    }

    private static BufferedImage imageCrop(FileData photodata, String cropping) throws JsonParseException, JsonMappingException, IOException {

        byte[] bytes = photodata.getData();
        BufferedImage croppedImg = null;

        ObjectMapper mapper = new ObjectMapper();
        CroppingInfo cropObj = mapper.readValue(cropping, CroppingInfo.class);

        int x = cropObj.getOx();
        int y = cropObj.getOy();

        double proportionX = cropObj.getX() == 0 ? 0 : (double) x / (double) cropObj.getX();
        double proportionY = cropObj.getY() == 0 ? 0 : (double) y / (double) cropObj.getY();

        // If proportionX,proportionY = 0 and croppingX,croppingY != 0 then the image is smaller than the cropping frame
        if (proportionX == 0 && proportionY == 0) {

            if (cropObj.getX() != 0 && cropObj.getY() != 0) {
                photodata.getMetadata().remove(cropping);
                return null;
            }

            int factorX = cropObj.getWidth() - cropObj.getX2();
            if (factorX > 0) {
                proportionX = (double) cropObj.getWidth() / (double) factorX;
            }
            int factorY = cropObj.getHeight() - cropObj.getY2();
            if (factorY > 0) {
                proportionY = (double) cropObj.getHeight() / (double) factorY;
            }

            proportionX = proportionX > proportionY ? proportionX : proportionY;
            proportionY = proportionX > proportionY ? proportionX : proportionY;

        }

        int spaceAfterX2 = cropObj.getWidth() - cropObj.getX2();
        int spaceAfterY2 = cropObj.getHeight() - cropObj.getY2();

        int x2 = cropObj.getOx2() - (int) ((proportionX == 0 ? 1 : proportionX) * spaceAfterX2);
        int y2 = cropObj.getOy2() - (int) ((proportionY == 0 ? 1 : proportionY) * spaceAfterY2);

        int startX = (x < 0) ? 0 : x;
        int startY = (y < 0) ? 0 : y;
        int endX = (x < 0) ? (x2 + (0 - x)) : x2;
        int endY = (y < 0) ? (y2 + (0 - y)) : y2;

        BufferedImage srcImg = null;
        srcImg = asBufferedImage(bytes);

        int cropWidth = endX - startX;
        int cropHeight = endY - startY;

        croppedImg = Scalr.crop(srcImg, x, y, cropWidth, cropHeight, Scalr.OP_ANTIALIAS);

        if (srcImg != null) {
            srcImg.flush();
        }

        return croppedImg;

    }

    /**
     * Read bytes as BufferedImage If ImageIO fails, use JAI
     *
     * @param bytes
     * @return
     */
    private static BufferedImage asBufferedImage(byte[] bytes) {
        BufferedImage srcImg = null;

        try {
            // We try it with ImageIO
            srcImg = ImageIO.read(new BufferedInputStream(new ByteArrayInputStream(bytes)));

        } catch (CMMException ex) {
            // If we failed...use JAI
            srcImg = JAI.create("stream", new BufferedInputStream(new ByteArrayInputStream(bytes))).getAsBufferedImage();
        } catch (Exception e) {
//			LOG.error("Failed to read bytes as image", e);
//			throw ApiException.addInfo( new PhotoReadingException(e), LogFields.MODULE, module);
        }

        return srcImg;
    }

    /**
     * Reads a Buffered Image as array of byte
     *
     * @param img
     * @param imgType
     * @return
     * @throws IOException
     */
    private static byte[] asByteArray(BufferedImage img, String imgType) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, imgType, baos);
        baos.flush();
        byte[] data = baos.toByteArray();
        baos.close();

        return data;
    }

    public static class CroppingInfo {

        int width;
        int height;
        int x, y, x2, y2, ox, oy, ox2, oy2;

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public int getX2() {
            return x2;
        }

        public int getY2() {
            return y2;
        }

        public int getOx() {
            return ox;
        }

        public int getOy() {
            return oy;
        }

        public int getOx2() {
            return ox2;
        }

        public int getOy2() {
            return oy2;
        }
    }
}
