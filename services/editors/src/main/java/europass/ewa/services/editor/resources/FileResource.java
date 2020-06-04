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
package europass.ewa.services.editor.resources;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import joptsimple.internal.Strings;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

import europass.ewa.Constants;
import europass.ewa.Utils;
import europass.ewa.enums.EuropassDocumentType;
import europass.ewa.enums.LogFields;
import europass.ewa.enums.ServerModules;
import europass.ewa.logging.ExtraLogInfo;
import europass.ewa.model.FileData;
import europass.ewa.model.wrapper.Feedback;
import europass.ewa.model.wrapper.FeedbackFactory;
import europass.ewa.model.wrapper.FiledataWrapper;
import europass.ewa.services.MediaTypeUtils;
import europass.ewa.services.Paths;
import europass.ewa.services.PhotoUtils;
import europass.ewa.services.annotation.EWAEditor;
import europass.ewa.services.editor.files.FileUploadsModule;
import europass.ewa.services.editor.messages.HtmlWrapper;
import europass.ewa.services.editor.modules.EditorServicesModule;
import europass.ewa.services.enums.FileStatus;
import europass.ewa.services.exception.ApiException;
import europass.ewa.services.files.FileRepository;
import europass.ewa.services.files.FileRepository.Types;
import europass.ewa.services.files.ImageType;
import java.util.UUID;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.NewCookie;
import org.apache.commons.lang.StringUtils;

@Path(Paths.FILES_BASE)
public class FileResource {

    private static final Logger LOG = LoggerFactory.getLogger(FileResource.class);

    private FileRepository repository;

    private final ObjectMapper mapper;
    private final HtmlWrapper htmlWrapper;

    private final List<MediaType> attachmentTypes;
    private final List<MediaType> photoTypes;
    private final List<MediaType> signatureTypes;

    private static final String module = ServerModules.SERVICES_EDITORS.getModule();

    @Inject
    public FileResource(
            FileRepository repository,
            ObjectMapper mapper,
            @EWAEditor HtmlWrapper htmlWrapper,
            @Named(FileUploadsModule.FILE_ATTACHMENT_ALLOWED_TYPES_LIST) List<MediaType> attachmentTypes,
            @Named(FileUploadsModule.FILE_PHOTO_ALLOWED_TYPES_LIST) List<MediaType> photoTypes,
            @Named(FileUploadsModule.FILE_SIGNATURE_ALLOWED_TYPES_LIST) List<MediaType> signatureTypes,
            @HeaderParam("user-agent") String userAgent) {

        this.repository = repository;
        this.mapper = mapper;
        this.htmlWrapper = htmlWrapper;

        this.attachmentTypes = attachmentTypes;
        this.photoTypes = photoTypes;
        this.signatureTypes = signatureTypes;
    }

    @GET
    @Path("/hello")
    public Response helloWorld() {
        return Response.ok().header("Content-Language", Constants.UTF8_ENCODING).build();
    }

    /*
	 * Get a file
     */
    @GET
    @Path(Paths.ATTACHMENT_BASE + "/{fileId}")
    public Response getFile(@PathParam("fileId") String fileId) {
        final FileData filedata = repository.readFile(fileId);
        if (filedata != null) {
            LOG.debug("Filedata fetched with mime type: " + filedata.getMimeType());
            return Response.ok(filedata.getData(), filedata.getMimeType()).build();
        }
        return Response.status(Status.NOT_FOUND).build();
    }

    @GET
    @Path(Paths.PHOTO_BASE + "/{fileId}")
    public Response getPhoto(@PathParam("fileId") String fileId) {
        return getFile(fileId);
    }

    @GET
    @Path(Paths.SIGNATURE_BASE + "/{fileId}")
    public Response getSignature(@PathParam("fileId") String fileId) {
        return getFile(fileId);
    }

    @GET
    @Path(Paths.ATTACHMENT_BASE + "/{fileId}" + Paths.ATTACHMENT_THUMB_PATH)
    public Response getThumb(@PathParam("fileId") String fileId) {
        final FileData filedata = repository.readThumb(fileId);
        if (filedata != null) {
            return Response.ok(filedata.getData(), ImageType.PNG.getBasicMimeType()).build();
        }
        return Response.status(Status.NOT_FOUND).build();
    }

    @DELETE
    @Path(Paths.ATTACHMENT_BASE + "/{fileId}")
    public Response deleteFile(@PathParam("fileId") String fileId) {
        return delete(fileId);

    }

    @DELETE
    @Path(Paths.PHOTO_BASE + "/{fileId}")
    public Response deletePhoto(@PathParam("fileId") String fileId) {
        return delete(fileId);
    }

    @DELETE
    @Path(Paths.SIGNATURE_BASE + "/{fileId}")
    public Response deleteSignature(@PathParam("fileId") String fileId) {
        return delete(fileId);
    }

    private Response delete(String fileId) {
        @SuppressWarnings("unused")
        boolean deleted = repository.delete(fileId);
        return Response.ok().build();
    }

    @POST
    @Path(Paths.ATTACHMENT_BASE + "/upload")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadFile(@FormDataParam("file") InputStream in,
            @FormDataParam("file") FormDataContentDisposition disposition,
            @FormDataParam("file") FormDataBodyPart bp,
            @HeaderParam("referer") String referer,
            @HeaderParam("user-agent") String ua,
            @HeaderParam("content-length") int fileLength,
            @QueryParam("id") String cookieId) {

        cookieId = cookieId == null ? "" : cookieId;
        //check if no cookie id has passed and create a cookie
        LOG.debug("/file/upload cookie id: " + cookieId);
        NewCookie editorsUserCookie = checkAndFetchCookie(cookieId);
        cookieId = editorsUserCookie != null ? editorsUserCookie.getValue() : cookieId;

        String fileName = Utils.readAsUTF8(disposition.getFileName()),
                uploadLocation = "Attachment Upload in ",
                fileSize = String.valueOf(fileLength / 1024) + "KB";

        String[] docTypes = EuropassDocumentType.ECV_ESP.getMetadata().split(",");  //EWA-1549 point A. 2. a.  doctypes[0] is cv and doctypes[1] is esp

        if (referer != null && docTypes.length == 2) {
            if (referer.indexOf(docTypes[0].trim().toLowerCase()) > 0) {
                uploadLocation += docTypes[0];
            } else if (referer.indexOf(docTypes[1].trim().toLowerCase()) > 0) {
                uploadLocation += docTypes[1] + " Tab";
            }
        }

        String ext = "";

        if (bp != null && bp.getMediaType() != null && bp.getMediaType().getSubtype() != null) {
            ext = bp.getMediaType().getSubtype();
        }

        ExtraLogInfo logInfo = new ExtraLogInfo().add(LogFields.EXTENSION, ext).add(LogFields.UA, ua).
                add(LogFields.LOCATION, uploadLocation).add(LogFields.FILESIZE, fileSize).add(LogFields.MODULE, module);

        try (BufferedInputStream bis = new BufferedInputStream(in)) {

            //Check if media type is allowed.
            MediaType mediaType = MediaType.WILDCARD_TYPE;
            mediaType = MediaTypeUtils.readMediaType(bp, disposition, bis, attachmentTypes);

            //throws IOException
            byte[] fileBytes = mediaType.getType() != null && mediaType.getType().equals("image")
                    ? PhotoUtils.fixOrientationIfNecessary(IOUtils.toByteArray(bis))
                    : IOUtils.toByteArray(bis);

            LOG.debug("just before saving pdf file to repository");
            FiledataWrapper filedataWrap = repository.save(fileBytes, mediaType.toString(), Types.ATTACHMENT, Paths.ATTACHMENT_BASE, cookieId);
            FileData filedata = filedataWrap.getFiledata();
            filedata.setName(fileName);

            String fdString = mapper.writeValueAsString(filedata);
            JSONObject fdJson = new JSONObject(fdString);

            Feedback feedback = filedataWrap.getFeedback();

            //TODO: ndim Tidy this up, maybe add a filename setter in Feedback.java, since it is not available in repository.save()
            if (feedback != null) {
                LOG.debug("feedback is not null");
                /* log the feedback's cause */
                String errCode = filedataWrap.getErrCode();
                logInfo.add(LogFields.MESSAGE, "Additional Logging Info for the feedback's cause").add(LogFields.ERRCODE, errCode);
                LOG.error(logInfo.getLogInfoAsJson());

                LOG.debug("Feedback code: " + feedback.getCode());
                if (Feedback.Code.UPLOAD_ATTACHMENT_THUMB.equals(feedback.getCode())) {
                    String trace = filedataWrap.getFeedback().getTrace();
                    filedataWrap.setFeedback(FeedbackFactory.attachmentToThumb(filedata.getName()).withError(trace));

                    String fbString = mapper.writeValueAsString(filedataWrap.getFeedback());

                    if (!Strings.isNullOrEmpty(fbString)) {
                        JSONObject fbJson = new JSONObject(fbString);
                        for (String key : JSONObject.getNames(fbJson)) {
                            fdJson.put(key, fbJson.get(key));
                        }
                    }
                }
            }

            String responseAsString = htmlWrapper.htmlWrap(fdJson.toString(), Status.OK.toString());
            LOG.debug("Attachment upload finished");

            if (editorsUserCookie == null) {
                return Response.ok()
                        .type(MediaType.TEXT_HTML)
                        .entity(responseAsString)
                        .build();
            } else {
                return Response.ok()
                        .type(MediaType.TEXT_HTML)
                        .entity(responseAsString)
                        .cookie(editorsUserCookie)
                        .build();
            }

        } catch (final /*JsonGenerationException | JsonMappingException | */ IOException e) {
            throw ApiException.addInfo(new ApiException(e, FileStatus.OTHER.getDescription(), Status.INTERNAL_SERVER_ERROR), logInfo);
        } catch (final ApiException e) {
            throw ApiException.addInfo(e, logInfo);
        }
    }

    @POST
    @Path(Paths.PHOTO_BASE + "/upload")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadPhoto(@FormDataParam("file") InputStream in,
            @FormDataParam("file") FormDataContentDisposition disposition,
            @FormDataParam("file") FormDataBodyPart bp,
            @HeaderParam("user-agent") String ua,
            @HeaderParam("content-length") int fileLength,
            @QueryParam("id") String cookieId) {

        cookieId = cookieId == null ? "" : cookieId;
        //check if no cookie id has passed and create a cookie
        LOG.debug("/photo/upload cookie id: " + cookieId);
        NewCookie editorsUserCookie = checkAndFetchCookie(cookieId);
        cookieId = editorsUserCookie != null ? editorsUserCookie.getValue() : cookieId;

        String fileSize = String.valueOf(fileLength / 1024) + "KB";

        String ext = "";

        if (bp != null && bp.getMediaType() != null && bp.getMediaType().getSubtype() != null) {
            ext = bp.getMediaType().getSubtype();
        }

        LOG.debug("ext: " + ext);
        ExtraLogInfo logInfo = new ExtraLogInfo().add(LogFields.EXTENSION, ext).add(LogFields.UA, ua).
                add(LogFields.LOCATION, "CV Photo Upload").add(LogFields.FILESIZE, fileSize).add(LogFields.MODULE, module);

        try (BufferedInputStream bis = new BufferedInputStream(in)) {

            MediaType mediaType = MediaType.WILDCARD_TYPE;
            //Read the media type and throws exception if undefined or disallowed
            mediaType = MediaTypeUtils.readMediaType(bp, disposition, bis, photoTypes);

            LOG.debug("Media type is: " + mediaType);
            //throws IOException
            //byte[] fileBytes = IOUtils.toByteArray(bis);
            byte[] fileBytes = PhotoUtils.fixOrientationIfNecessary(IOUtils.toByteArray(bis));

            LOG.debug("Just before creating FileData");
            FileData filedata = repository.save(fileBytes, mediaType.toString(), Types.PHOTO, Paths.PHOTO_BASE, cookieId).getFiledata();
            LOG.debug("FileData saved");
            String jsonString = mapper.writeValueAsString(filedata);
            LOG.debug("Json string: " + jsonString);
            String responseAsString = htmlWrapper.htmlWrap(jsonString, Status.OK.toString());
            LOG.debug("Photo upload finished");

            if (editorsUserCookie == null) {
                return Response.ok()
                        .type(MediaType.TEXT_HTML)
                        .entity(responseAsString)
                        .build();
            } else {
                return Response.ok()
                        .type(MediaType.TEXT_HTML)
                        .entity(responseAsString)
                        .cookie(editorsUserCookie)
                        .build();
            }

        } catch (final /*JsonGenerationException | JsonMappingException | */ IOException e) {
            throw ApiException.addInfo(new ApiException(e, FileStatus.OTHER.getDescription(), Status.INTERNAL_SERVER_ERROR), logInfo);
        } catch (final ApiException e) {
            throw ApiException.addInfo(e, logInfo);
        }
    }

    @POST
    @Path(Paths.SIGNATURE_BASE + "/upload")
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response uploadSignature(@FormDataParam("file") InputStream in,
            @FormDataParam("file") FormDataContentDisposition disposition,
            @FormDataParam("file") FormDataBodyPart bp,
            @HeaderParam("user-agent") String ua,
            @HeaderParam("content-length") int fileLength,
            @QueryParam("id") String cookieId) {

        cookieId = cookieId == null ? "" : cookieId;
        //check if no cookie id has passed and create a cookie
        LOG.debug("/signature/upload cookie id: " + cookieId);
        NewCookie editorsUserCookie = checkAndFetchCookie(cookieId);
        cookieId = editorsUserCookie != null ? editorsUserCookie.getValue() : cookieId;

        String fileSize = String.valueOf(fileLength / 1024) + "KB";

        String size = "";
        if (disposition != null) {
            size = "";
        }

        String ext = "";

        if (bp != null && bp.getMediaType() != null && bp.getMediaType().getSubtype() != null) {
            ext = bp.getMediaType().getSubtype();
        }

        ExtraLogInfo logInfo = new ExtraLogInfo().add(LogFields.EXTENSION, ext).add(LogFields.UA, ua).
                add(LogFields.LOCATION, "CL Signature Upload").add(LogFields.FILESIZE, fileSize).add(LogFields.FILESIZE, size).add(LogFields.MODULE, module);

        try (BufferedInputStream bis = new BufferedInputStream(in)) {

            MediaType mediaType = MediaType.WILDCARD_TYPE;
            //Read the media type and throws exception if undefined or disallowed
            mediaType = MediaTypeUtils.readMediaType(bp, disposition, bis, signatureTypes);

            //throws IOException
            byte[] fileBytes = IOUtils.toByteArray(bis);

            FileData filedata = repository.save(fileBytes, mediaType.toString(), Types.SIGNATURE, Paths.SIGNATURE_BASE, cookieId).getFiledata();
            String jsonString = mapper.writeValueAsString(filedata);
            LOG.debug("Json string: " + jsonString);
            String responseAsString = htmlWrapper.htmlWrap(jsonString, Status.OK.toString());
            LOG.debug("Signature upload finished");

            if (editorsUserCookie == null) {
                return Response.ok()
                        .type(MediaType.TEXT_HTML)
                        .entity(responseAsString)
                        .build();
            } else {
                return Response.ok()
                        .type(MediaType.TEXT_HTML)
                        .entity(responseAsString)
                        .cookie(editorsUserCookie)
                        .build();
            }

        } catch (final /*JsonGenerationException | JsonMappingException | */ IOException e) {
            throw ApiException.addInfo(new ApiException(e, FileStatus.OTHER.getDescription(), Status.INTERNAL_SERVER_ERROR), logInfo);
        } catch (final ApiException e) {
            throw ApiException.addInfo(e, logInfo); //rethrow adding extra logging info
        }
    }

    /* --- UTILITIES METHODS --- */
    // Check mime types against a "," separated list
    boolean checkMimeTypes(String type, String mimeList) {
        if (mimeList == null) {
            return true;
        }
        if (type == null) {
            return false;
        }

        String[] mimes = mimeList.split(",");
        if (mimes == null) {
            return true;
        }

        for (int i = 0; i < mimes.length; i++) {
            if (type.equalsIgnoreCase(mimes[i])) {
                return true;
            }
        }

        return false;
    }

    /**
     * This method checks if a cookie exists based on the @param requirements
     * (not null, length = 36) If a cookie exists, it returns null If a cookie
     * does not exist, it returns a NewCookie If a cookie does not exist and
     * something goes wrong in the NewCookie instantiation, it returns a
     * NewCookie with an empty value
     *
     * @param cookieId
     * @return
     */
    protected static NewCookie checkAndFetchCookie(String cookieId) {
        boolean cookieNotExists = (StringUtils.isBlank(cookieId) || cookieId.length() < 36);
        if (cookieNotExists) {
            try {
                UUID uidValue = UUID.fromString(EditorServicesModule.USER_COOKIE_PATTERN);
                String cookiePath = "/editors/";
                NewCookie editorsUserCookie = new NewCookie(EditorServicesModule.USER_COOKIE_ID, uidValue.randomUUID().toString(), cookiePath, "", "", 10 * 60 * 60 * 24 * 365, false);
                //cookieId = editorsUserCookie.getValue();
                //cookieExists = false;
                return editorsUserCookie;
            } catch (Exception e) {
                LOG.error("Failed to create new cookie", e);
                return new NewCookie(EditorServicesModule.USER_COOKIE_ID, "");
            }
        } else { //return null if cookie exists
            return null;
        }
    }
}
