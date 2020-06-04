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
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import europass.ewa.modules.SupportedLocaleModule;
import europass.ewa.services.annotation.Default;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.google.common.base.Strings;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.security.PdfPKCS7;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataParam;

import europass.ewa.Constants;
import europass.ewa.enums.EuropassDocumentType;
import europass.ewa.enums.LogFields;
import europass.ewa.enums.ServerModules;
import europass.ewa.logging.ExtraLogInfo;
import europass.ewa.model.PrintingPreference;
import europass.ewa.model.SkillsPassport;
import europass.ewa.model.conversion.Converter;
import europass.ewa.model.conversion.ModelModule;
import europass.ewa.model.conversion.exception.PojoToJsonException;
import europass.ewa.model.conversion.exception.XmlToPojoException;
import europass.ewa.model.conversion.json.ModelContainerConverter;
import europass.ewa.model.conversion.xml.XML;
import europass.ewa.model.wrapper.Feedback;
import europass.ewa.model.wrapper.UploadedModelWrapper;
import europass.ewa.services.MediaTypeUtils;
import europass.ewa.services.Paths;
import europass.ewa.services.annotation.EWAEditor;
import europass.ewa.services.compatibility.XMLBackwardCompatibility;
import europass.ewa.services.conversion.PDFAttachmentExtractor;
import europass.ewa.services.editor.messages.HtmlWrapper;
import europass.ewa.services.editor.modules.EditorServicesModule;
import static europass.ewa.services.editor.resources.FileResource.checkAndFetchCookie;
import europass.ewa.services.enums.UploadStatus;
import europass.ewa.services.exception.ApiException;
import europass.ewa.services.exception.DisallowedMediaTypeException;
import europass.ewa.services.files.ModelFileManager;
import europass.ewa.services.modules.CoreServicesModule;
import java.security.Security;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.NewCookie;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

@Path(Paths.LOAD_BASE)
public class LoadResource {

    private final ModelContainerConverter jsonconverter;
    private final Converter<SkillsPassport> xmlconverter;
    private final ModelFileManager fileManager;
    private final HtmlWrapper htmlWrapper;
    private final List<MediaType> uploadTypes;
    private final Map<EuropassDocumentType, Map<String, PrintingPreference>> defaultPrefs;
    private final XMLBackwardCompatibility xmlbackwardcompatibility;
    private final Set<Locale> supportedLocales;
    private final Locale defaultLocale;
    private final String cvnToEpasUrl;

    private static final String module = ServerModules.SERVICES_EDITORS.getModule();

    @Inject
    private FileExportResource fileExportResource;

    private static final Logger LOG = LoggerFactory.getLogger(LoadResource.class);

    @Inject
    public LoadResource(final ModelContainerConverter jsonconverter,
            final @XML Converter<SkillsPassport> xmlconverter,
            final @EWAEditor ModelFileManager fileManager,
            final @EWAEditor HtmlWrapper htmlWrapper,
            final @Named(CoreServicesModule.UPLOAD_CV_ALLOWED_TYPES) List<MediaType> uploadTypes,
            final @Named(ModelModule.DEFAULT_PREFS) Map<EuropassDocumentType, Map<String, PrintingPreference>> defaultPrefs,
            final XMLBackwardCompatibility xmlbackwardcompatibility,
            final @Named(SupportedLocaleModule.EWA_SUPPORTED_LANGUAGES) Set<Locale> supportedLocales,
            final @Default Locale defaultLocale,
            final @Named(EditorServicesModule.CVN_TO_EPAS_URL) String cvnToEpasUrl) {

        this.jsonconverter = jsonconverter;
        this.xmlconverter = xmlconverter;
        this.fileManager = fileManager;
        this.htmlWrapper = htmlWrapper;
        this.defaultPrefs = defaultPrefs;
        this.uploadTypes = uploadTypes;
        this.xmlbackwardcompatibility = xmlbackwardcompatibility;
        this.supportedLocales = supportedLocales;
        this.defaultLocale = defaultLocale;
        this.cvnToEpasUrl = cvnToEpasUrl;
    }

    @GET
    @Produces("text/plain")
    public String hello() {
        return "Hello World!";
    }

    /**
     * Uploading a PDF+XML or XML by retrieving a file from a URL provided by a
     * cloud service.
     *
     * @param cloudInfo
     * @param ua
     * @param cookieId
     * @param keepNotImportedCv
     * @return
     */
    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/from-cloud")
    public Response load(final ImportFromCloudInfo cloudInfo,
            final @HeaderParam("user-agent") String ua,
            final @QueryParam("id") String cookieId,
            final @QueryParam("keepCv") Boolean keepNotImportedCv) {

        LOG.debug("inside /from-cloud");
        LOG.debug("cookie id: " + cookieId);

        String url = cloudInfo.getUrl();
        final String token = cloudInfo.getToken();

        String location = "Cloud";
        String extension = null;

        int pos = url.lastIndexOf(".");				//Infer the file extension from the cloud URL
        if (pos > 0 && url.substring(pos).length() - 1 == 3) {  //should be 3 chars long
            extension = url.substring(pos);
        }

        if (url.contains("google")) {
            location = "Google Cloud";
        } else if (url.contains("dropbox")) {
            location = "Dropbox Cloud";
            url = url.replaceAll("\\+", "%20"); //need to replace space encoded as + by jquery param with %20
        } else if (url.contains("live") || url.contains("1drv")) {
            location = "One Drive Cloud";
        }

        final ExtraLogInfo extraLogInfo = new ExtraLogInfo()
                .add(LogFields.LOCATION, location)
                .add(LogFields.ACTION, "Document Import")
                .add(LogFields.UA, ua)
                .add(LogFields.EXTENSION, extension)
                .add(LogFields.MODULE, module);

        if (Strings.isNullOrEmpty(url)) {
            throw ApiException.addInfo(new ApiException("Failed to upload the profile, because the supplied File URL is null or the empty string.", UploadStatus.FILE_URL_EMPTY.getDescription(), Status.BAD_REQUEST),
                    extraLogInfo);
        }

        InputStream in = null;

        //Perform an HTTP GET and set the Authorization: Bearer Header according to the token.
        try {

            final CloseableHttpClient httpclient = HttpClients.createDefault();
            final HttpGet get = new HttpGet(url);

            if (!Strings.isNullOrEmpty(token)) {
                get.addHeader("Authorization", "Bearer " + token);
            }

            final HttpResponse response = httpclient.execute(get);
            final int statusCode = response.getStatusLine().getStatusCode();

            if (statusCode >= 200 && statusCode < 300) {
                final HttpEntity entity = response.getEntity();
                if (entity != null) {
                    in = entity.getContent();
                }
            } else if (statusCode >= 400 && statusCode < 500) {
                throw ApiException.addInfo(new ApiException("Failed to upload the profile, due to a permission problem",
                        UploadStatus.FILE_URL_NOT_ALLOWED.getDescription(), Status.UNAUTHORIZED), extraLogInfo);
            } else if (statusCode >= 400 && statusCode < 500) {
                String description = UploadStatus.FILE_URL_NOT_AVAILABLE.getDescription();
                if (statusCode == 429) {
                    description = UploadStatus.REQUEST_NUMBER_QUOTA_EXCEEDED.getDescription();
                }
                throw ApiException.addInfo(new ApiException("Failed to upload the profile, due to a problem with the service provider",
                        description, Status.SERVICE_UNAVAILABLE), extraLogInfo);
            }

            if (in == null) {
                throw ApiException.addInfo(new ApiException("Failed to upload the profile, because the input is empty",
                        UploadStatus.XML_EMPTY.getDescription(), Status.BAD_REQUEST), extraLogInfo);
            }

            return doUpload(in, cookieId, keepNotImportedCv);
        } catch (final IOException | NullPointerException e) {
            throw ApiException.addInfo(new ApiException(e, UploadStatus.OTHER.getDescription(), Status.INTERNAL_SERVER_ERROR), extraLogInfo);
        } catch (final ApiException e) {
            throw ApiException.addInfo(e, extraLogInfo);
        }
    }

    @JsonRootName(value = "data")
    public static class ImportFromCloudInfo {

        private String url;
        private String token;

        public String getUrl() {
            return url;
        }

        public void setUrl(final String url) {
            this.url = url;
        }

        public String getToken() {
            return token;
        }

        public void setToken(final String token) {
            this.token = token;
        }
    }

    /**
     * Uploading a PDF+XML or XML via a normal form upload file field.
     *
     * @param in
     * @param bp
     * @param disposition
     * @param userAgent
     * @param cookieId
     * @param keepNotImportedCv
     * @return
     */
    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response load(final @FormDataParam("file") InputStream in,
            final @FormDataParam("file") FormDataBodyPart bp,
            final @FormDataParam("file") FormDataContentDisposition disposition,
            final @HeaderParam("user-agent") String userAgent,
            final @QueryParam("id") String cookieId,
            final @QueryParam("keepCv") Boolean keepNotImportedCv) {

        return doUpload(in, bp, disposition, userAgent, cookieId, keepNotImportedCv);
    }

    /* ------------------------------------------------------------------------------------------------------------ */
    /**
     * Utility reused when the input is an XML String. Step 1. Read the
     * MediaType and discard if not PDF or not XML Step 2. a. If InputStream is
     * XML, then read it to String with UTF-8 Encoding b. If InputStream is PDF,
     * then read it and try to find a Europass XML attachment to extract as
     * String c. If InputStream is CVN PDF (based on pdf digital signature
     * name), convert to JSON through CVN2EPAS application and go to step 9 Step
     * 3. Null or empty check Step 4. Transformation from previous versions to
     * current, if necessary. Step 5. Conversion from XML string to Java model
     * Step 6. Activation of Default Printing Preferences Step 7. Saving of
     * binary data (e.g. photo, signature, attachments) and replacing them with
     * a temporary URI bound to the current session Step 8. Conversion from Java
     * model to JSON string. Step 9. Return OK response and include an HTML text
     * that wraps the JSON. An ApiException is thrown when something goes wrong
     * in any of the above steps.
     *
     * @param in
     * @param cookieId
     * @return
     */
    private Response doUpload(final InputStream in, final String cookieId) {

        return doUpload(in, null, null, null, cookieId, null);
    }

    private Response doUpload(final InputStream in, final String cookieId, final Boolean keepNotImportedCv) {
        return doUpload(in, null, null, null, cookieId, keepNotImportedCv);
    }

    private Response doUpload(final InputStream in, final FormDataBodyPart bp,
            final FormDataContentDisposition disposition, final String userAgent, String cookieId,
            final Boolean keepNotImportedCv) {

        cookieId = cookieId == null ? "" : cookieId;
        //check if no cookie id has passed and create a cookie

        LOG.debug("uploading cv - cookie id: " + cookieId);

        final NewCookie editorsUserCookie = checkAndFetchCookie(cookieId);
        cookieId = editorsUserCookie != null ? editorsUserCookie.getValue() : cookieId;

        String fileType = "", location;

        if (bp == null && disposition == null && userAgent == null) {
            location = "Cloud"; // when called from doUpload(in, null, null, null);
        } else {
            location = "My Computer";
            if (bp.getMediaType() != null) {
                fileType = bp.getMediaType().getSubtype();
            }
        }

        final ExtraLogInfo extraLogInfo = new ExtraLogInfo().add(LogFields.UA, userAgent)
                .add(LogFields.EXTENSION, fileType)
                .add(LogFields.ACTION, "Document import")
                .add(LogFields.LOCATION, location)
                .add(LogFields.MODULE, module);

        boolean isXml = false;
        boolean isPDF = false;

        final BufferedInputStream bis = new BufferedInputStream(in);
        byte[] fileBytes = null;
        MediaType mediaType = null;
        try {
            mediaType = MediaTypeUtils.readMediaType(bp, disposition, bis, uploadTypes);

            fileBytes = IOUtils.toByteArray(bis);

            isXml = (mediaType.isCompatible(MediaType.APPLICATION_XML_TYPE) || mediaType.isCompatible(MediaType.TEXT_XML_TYPE));
            isPDF = (mediaType.isCompatible(MediaTypeUtils.APPLICATION_PDF) || mediaType.isCompatible(MediaTypeUtils.APPLICATION_X_PDF));

            if (!isXml && !isPDF) {
                throw new DisallowedMediaTypeException(mediaType);
            }

            // Retrieve the XML... The InputStream will be closed later on
            String xml = null;

            if (isXml) { // When XML, parse to String
                try {
                    xml = IOUtils.toString(fileBytes, Constants.UTF8_ENCODING);
                } catch (final Exception e) {
                    throw new ApiException(e, UploadStatus.XML_READ.getDescription(), Status.INTERNAL_SERVER_ERROR);
                }
            } else if (isPDF) { // When PDF extract XML attachment

                if (isCVN(fileBytes)) {
                    HttpPost post = new HttpPost(cvnToEpasUrl);
                    post.setHeader("Content-type", "application/pdf");
                    post.setEntity(new ByteArrayEntity(fileBytes));
                    HttpClient client = HttpClientBuilder.create().build();
                    HttpResponse response = client.execute(post);
                    int status = response.getStatusLine().getStatusCode();
                    LOG.debug("cvn2epas response status: " + status);
                    String responseEntity = EntityUtils.toString(response.getEntity());

                    if (status == HttpStatus.OK.value()) {
                        final String json = "{\"Uploaded\":" + responseEntity + "}";
                        final String htmlResponse = htmlWrapper.htmlWrap(json, Status.OK.toString());
                        return doUploadResponseOk(editorsUserCookie, htmlResponse);
                    }
                } else {
                    xml = PDFAttachmentExtractor.extractAttachment(fileBytes, userAgent);
                }
            }

            if (Strings.isNullOrEmpty(xml)) {
                final String message = "Failed to upload the existing CV because the xml is null or the empty string.";

                throw new ApiException(message, UploadStatus.XML_READ.getDescription(), Status.BAD_REQUEST);
            }

            // Backward Compatibility
            xml = (String) xmlbackwardcompatibility.transform(xml);
            xml = (String) xmlbackwardcompatibility.transformCleanXml(xml);

            try {

                final SkillsPassport esp = xmlconverter.load(xml);

                setFallbackLocale(esp);

                esp.eliminateOccupationCodes();

                final UploadedModelWrapper espWrapper = new UploadedModelWrapper(esp);

                esp.activatePreferences(this.defaultPrefs);

                // Read bytes from XML and write files to disk, augment POJO with TempURIs				
                final List<Feedback> feedback = fileManager.augmentWithURI(esp, cookieId);
                espWrapper.setInfo(feedback);

                final String json = jsonconverter.write(espWrapper);
                final String htmlResponse = htmlWrapper.htmlWrap(json, Status.OK.toString());
                return doUploadResponseOk(editorsUserCookie, htmlResponse);

            } catch (final XmlToPojoException | PojoToJsonException e) {
                throw new ApiException(e, e.getCode(), Status.INTERNAL_SERVER_ERROR);
            } catch (final Exception e) {
                throw new ApiException(e, UploadStatus.OTHER.getDescription(), Status.INTERNAL_SERVER_ERROR);
            }
        } catch (final NullPointerException | IOException e) {
            throw ApiException.addInfo(new ApiException(e, UploadStatus.OTHER.getDescription(), Status.INTERNAL_SERVER_ERROR), extraLogInfo);
        } catch (final ApiException e) {
            if (keepNotImportedCv != null && Boolean.valueOf(keepNotImportedCv) == true && fileBytes != null) {

                String filename = disposition != null ? disposition.getFileName() : null;
                String fileExtension = (filename != null && filename.lastIndexOf('.') >= 0)
                        ? filename.substring(filename.lastIndexOf('.') + 1)
                        : !fileType.equals("") ? fileType : mediaType != null ? mediaType.getSubtype() : "";

                fileExportResource.exportFile(fileBytes, fileExtension, cookieId);
            }
            throw ApiException.addInfo(e, extraLogInfo);
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
            } catch (IOException e) {
            }
        }
    }

    /* ------------------------------------------------------------------------------------------------------------ */
    /**
     * REMOTE UPLOAD
     *
     * Someone does a form post to a specific URL, submitting a SkillsPassport
     * model. This way the EWA Editor can be populated with data, not
     * necessarily after manual upload from file, import from LinkedIn or
     * another cloud service.
     *
     * @param remoteXml
     * @param request
     * @param cookieId
     * @return
     */
    @POST
    @Produces({MediaType.APPLICATION_JSON + Paths.UTF8_CHARSET, MediaType.TEXT_HTML})
    @Consumes(MediaType.APPLICATION_XML + Paths.UTF8_CHARSET)
    @Path("/remote")
    public Response uploadXML(final String remoteXml,
            final @Context HttpServletRequest request,
            final @QueryParam("id") String cookieId) {

        LOG.debug("inside /remote");
        LOG.debug("cookie ID: " + cookieId);

        final String location = "Remote Upload";
        final String ua = request.getHeader("user-agent");

        if (Strings.isNullOrEmpty(remoteXml)) {
            return Response.status(Status.BAD_REQUEST).build();
        }

        String xml = (String) xmlbackwardcompatibility.transform(remoteXml);
        xml = (String) xmlbackwardcompatibility.transformCleanXml(xml);

        try {

            final SkillsPassport esp = xmlconverter.load(xml);

            setFallbackLocale(esp);

            esp.eliminateOccupationCodes();

            // Apply default printing preferences and Activate Preferences for All documents
            esp.activatePreferences(this.defaultPrefs);

            // Read bytes from XML and write files to disk, augment POJO with TempURIs			
            fileManager.augmentWithURI(esp, cookieId);

            final Locale locale = esp.getLocale();

            return Response.ok(new LoadResource.ModelWrapper(esp)).language(locale == null ? Locale.ENGLISH : locale).build();

        } catch (final XmlToPojoException | PojoToJsonException e) {
            throw ApiException.addInfo(new ApiException(e, e.getCode(), Status.INTERNAL_SERVER_ERROR),
                    new ExtraLogInfo().add(LogFields.UA, ua)
                            .add(LogFields.FILETYPE, "XML")
                            .add(LogFields.LOCATION, location)
                            .add(LogFields.MODULE, module));
        } catch (final Exception e) {
            throw ApiException.addInfo(new ApiException(e, UploadStatus.OTHER.getDescription(), Status.INTERNAL_SERVER_ERROR),
                    new ExtraLogInfo().add(LogFields.UA, ua)
                            .add(LogFields.FILETYPE, "XML")
                            .add(LogFields.LOCATION, location)
                            .add(LogFields.MODULE, module));
        }
    }

    /**
     * SHARE LINK UPLOAD
     *
     * Someone does a form post to a specific URL, submitting a SkillsPassport
     * model. This way the EWA Editor can be populated with data, not
     * necessarily after manual upload from file, import from LinkedIn or
     * another cloud service.
     *
     * @param remoteXml
     * @param request
     * @param cookieId
     * @return
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON + Paths.UTF8_CHARSET)
    @Consumes(MediaType.APPLICATION_XML + Paths.UTF8_CHARSET)
    @Path("/from-share")
    public Response uploadSharedXML(final String remoteXml,
            final @Context HttpServletRequest request,
            @QueryParam("id") String cookieId) {

        cookieId = cookieId == null ? "" : cookieId;
        //check if no cookie id has passed and create a cookie
        LOG.debug("/from-share - cookie id: " + cookieId);

        final NewCookie editorsUserCookie = checkAndFetchCookie(cookieId);
        cookieId = editorsUserCookie != null ? editorsUserCookie.getValue() : cookieId;

        final String location = "Shared xml Upload";
        final String ua = request.getHeader("user-agent");

        if (Strings.isNullOrEmpty(remoteXml)) {
            if (editorsUserCookie == null) {
                return Response.status(Status.BAD_REQUEST).build();
            } else {
                return Response.status(Status.BAD_REQUEST).cookie(editorsUserCookie).build();
            }
        }

        // Backward Compatibility
        String xml = (String) xmlbackwardcompatibility.transform(remoteXml);

        //clean empty nodes
        xml = (String) xmlbackwardcompatibility.transformCleanXml(xml);

        try {
            final SkillsPassport esp = xmlconverter.load(xml);

            setFallbackLocale(esp);

            esp.eliminateOccupationCodes();

            esp.activatePreferences(this.defaultPrefs);

            // Read bytes from XML and write files to disk, augment POJO with TempURIs
            fileManager.augmentWithURI(esp, cookieId);

            LoadResource.ModelWrapper wrapper = new LoadResource.ModelWrapper(esp);

            final Locale locale = esp.getLocale();

            if (editorsUserCookie == null) {
                return Response.ok()
                        .type(MediaType.APPLICATION_JSON)
                        .entity(wrapper)
                        .language(locale == null ? Locale.ENGLISH : locale)
                        .build();
            } else {
                return Response.ok()
                        .type(MediaType.APPLICATION_JSON)
                        .entity(wrapper)
                        .language(locale == null ? Locale.ENGLISH : locale)
                        .cookie(editorsUserCookie)
                        .build();
            }

        } catch (final XmlToPojoException | PojoToJsonException e) {
            throw ApiException.addInfo(new ApiException(e, e.getCode(), Status.INTERNAL_SERVER_ERROR),
                    new ExtraLogInfo().add(LogFields.UA, ua)
                            .add(LogFields.FILETYPE, "XML")
                            .add(LogFields.LOCATION, location)
                            .add(LogFields.MODULE, module));
        } catch (final Exception e) {
            throw ApiException.addInfo(new ApiException(e, UploadStatus.OTHER.getDescription(), Status.INTERNAL_SERVER_ERROR),
                    new ExtraLogInfo().add(LogFields.UA, ua)
                            .add(LogFields.FILETYPE, "XML")
                            .add(LogFields.LOCATION, location)
                            .add(LogFields.MODULE, module));
        }
    }

    /*CHECKS IF DOCUMENT CONTAINS A EUROPASS XML ATTACHMENT
     * @return boolean */
    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Path("/isEuroDoc")
    public Response isEuroDoc(final @FormDataParam("file") InputStream in,
            final @FormDataParam("file") FormDataBodyPart bp,
            final @FormDataParam("file") FormDataContentDisposition disposition,
            final @HeaderParam("user-agent") String userAgent) {

        String fileType = "", location;

        if (bp == null && disposition == null && userAgent == null) {
            location = "Cloud"; // when called from doUpload(in, null, null, null);
        } else {
            location = "My Computer";
            if (bp.getMediaType() != null) {
                fileType = bp.getMediaType().getSubtype();
            }
        }

        final ExtraLogInfo extraLogInfo = new ExtraLogInfo().add(LogFields.UA, userAgent)
                .add(LogFields.EXTENSION, fileType)
                .add(LogFields.ACTION, "Document import")
                .add(LogFields.LOCATION, location)
                .add(LogFields.MODULE, module);

        boolean isXml = false;
        boolean isPDF = false;
        boolean isEuro = false;

        // This means is that you define the resources ahead of time and the runtime automatically closes those resources (if they are not already
        // closed) after the execution of the try block.
        try (BufferedInputStream bis = new BufferedInputStream(in)) {
            final MediaType mediaType = MediaTypeUtils.readMediaType(bp, disposition, bis, uploadTypes);

            // Decide if it is XML or PDF
            isXml = (mediaType.isCompatible(MediaType.APPLICATION_XML_TYPE) || mediaType.isCompatible(MediaType.TEXT_XML_TYPE));
            isPDF = (mediaType.isCompatible(MediaTypeUtils.APPLICATION_PDF) || mediaType.isCompatible(MediaTypeUtils.APPLICATION_X_PDF));

            // When neither of XML or PDF, throw exception
            if (!isXml && !isPDF) {
                //TODO return false
            } else {
                // Retrieve the XML... The InputStream will be closed later on
                String xml = null;
                if (isXml) { // When XML, parse to String
                    try {
                        xml = IOUtils.toString(bis, Constants.UTF8_ENCODING);
                    } catch (final Exception e) {	//any exception while reading TODO find what to return false and what to throw
                        //throw new ApiException(e, UploadStatus.XML_READ.getDescription(), Status.INTERNAL_SERVER_ERROR);
                    }
                } else {
                    byte[] fileBytes = IOUtils.toByteArray(bis);
                    if (isPDF) { // When PDF extract XML attachment
                        if (isCVN(fileBytes)) {
                            isEuro = true;
                        } else {
                            xml = PDFAttachmentExtractor.extractAttachment(fileBytes, userAgent);
                        }
                    }
                }
                if (!Strings.isNullOrEmpty(xml)) {
                    xml = (String) xmlbackwardcompatibility.transform(xml);
                    xml = (String) xmlbackwardcompatibility.transformCleanXml(xml);
                    xmlconverter.load(xml);
                    isEuro = true;
                }
            }
        } catch (final XmlToPojoException | PojoToJsonException e) {
            //throw ApiException.addInfo(new ApiException(e, e.getCode(), Status.INTERNAL_SERVER_ERROR),
            //  new ExtraLogInfo().add(LogFields.UA, ua).add(LogFields.FILETYPE, "XML").add(LogFields.LOCATION, location).add(LogFields.MODULE, module));
        } catch (final NullPointerException | IOException e) {
            throw ApiException.addInfo(new ApiException(e, UploadStatus.OTHER.getDescription(), Status.INTERNAL_SERVER_ERROR), extraLogInfo);
        } catch (final ApiException e) {
            //throw e.addInfo(e, extraLogInfo);
            isEuro = false;
        }
        try {
            final String result = "{\"isEuro\":" + isEuro + "}";
            final String htmlResponse = htmlWrapper.htmlWrap(result, Status.OK.toString());

            return Response.ok().type(MediaType.TEXT_HTML).entity(htmlResponse).build();
        } catch (final XmlToPojoException | PojoToJsonException e) {
            throw new ApiException(e, e.getCode(), Status.INTERNAL_SERVER_ERROR);
        } catch (final Exception e) {
            throw new ApiException(e, UploadStatus.OTHER.getDescription(), Status.INTERNAL_SERVER_ERROR);
        }
    }

    private Response doUploadResponseOk(NewCookie editorsUserCookie, String htmlResponse) {
        if (editorsUserCookie == null) {
            return Response.ok()
                    .type(MediaType.TEXT_HTML)
                    .entity(htmlResponse)
                    .build();
        } else {
            return Response.ok()
                    .type(MediaType.TEXT_HTML)
                    .entity(htmlResponse)
                    .cookie(editorsUserCookie)
                    .build();
        }
    }

    private boolean isCVN(byte[] fileBytes) throws IOException {
        Security.addProvider(new BouncyCastleProvider());
        PdfReader reader = new PdfReader(fileBytes);
        AcroFields acroFields = reader.getAcroFields();
        List<String> signatureNames = acroFields.getSignatureNames();
        if (!signatureNames.isEmpty()) {
            final String SIG_NAME = "FUNDACIÓN ESPAÑOLA PARA LA CIENCIA Y LA TECNOLOGÍA (FECYT)";
            for (String name : signatureNames) {
                PdfPKCS7 pkcs7 = acroFields.verifySignature(name);
                return pkcs7.getSignName().equals(SIG_NAME);
            }
        }
        return false;
    }

    private void setFallbackLocale(final SkillsPassport esp) {

        boolean foundLocale = false;
        for (final Locale locale : supportedLocales) {
            if (locale.equals(esp.getLocale())) {
                foundLocale = true;
                break;
            }
        }
        if (!foundLocale) {
            esp.setLocale(defaultLocale);
        }
    }

    /* ------------------------------------------------------------------------------------------------------------ */
    /**
     * Utility class used to wrap the response in case of Remote Upload
     *
     * @author ekar
     *
     */
    static class ModelWrapper {

        @JsonProperty("SkillsPassport")
        private SkillsPassport esp;

        ModelWrapper(final SkillsPassport esp) {
            this.esp = esp;
        }

        public SkillsPassport getEsp() {
            return esp;
        }

        public void setEsp(final SkillsPassport esp) {
            this.esp = esp;
        }
    }

}
