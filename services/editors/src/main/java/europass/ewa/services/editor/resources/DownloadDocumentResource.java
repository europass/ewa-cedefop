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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;
import net.sf.uadetector.OperatingSystemFamily;
import net.sf.uadetector.ReadableUserAgent;
import net.sf.uadetector.UserAgentFamily;
import com.google.common.base.Strings;
import com.google.inject.name.Named;
import europass.ewa.CachedUserAgentStringParser;
import europass.ewa.Utils;
import europass.ewa.enums.ContentTypes;
import europass.ewa.enums.ConversionFileType;
import europass.ewa.enums.EuropassDocumentType;
import europass.ewa.enums.ExportDestination;
import europass.ewa.enums.LogFields;
import europass.ewa.enums.ServerModules;
import europass.ewa.logging.ExtraLogInfo;
import europass.ewa.model.SkillsPassport;
import europass.ewa.services.Paths;
import europass.ewa.services.annotation.EWAEditor;
import europass.ewa.services.conversion.model.ExportableModel;
import europass.ewa.services.conversion.model.ExportableModelFactory;
import europass.ewa.services.conversion.process.DocumentGeneration;
import europass.ewa.services.editor.modules.EditorServicesModule;
import europass.ewa.statistics.DocumentGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Includes the REST methods for downloading or e-mailing a CV document in any
 * of the supported file types.
 *
 * This class will require extensive re-factoring when we will need to include
 * the LP services, as several ECV specific logic has been included, although
 * not initially indented so
 *
 * @author ekar
 *
 */
@Path(Paths.CONVERSION_BASE)
public class DownloadDocumentResource {

    private static final Logger LOG = LoggerFactory.getLogger(DownloadDocumentResource.class);

    public static final String FORM_PARAM_USER_AGENT = "user-agent";
    public static final String FORM_PARAM_DOWNLOAD_TOKEN = "downloadToken";
    public static final String FORM_PARAM_JSON = "json";
    public static final String FORM_PARAM_KEEP_CV = "keepCv";
    public static final String FORM_PARAM_REMOTE_UPLOAD_CALLBACK_URL = "remoteUploadCallbackUrl";
    public static final String FORM_PARAM_USER_COOKIE_ID = "cookieUserID";

    private static final int COOKIE_MAX_AGE = 300;

    public static final String COOKIE_CONTEXT_NAME = "editors.webapp.context.name";

    protected static final String module = ServerModules.SERVICES_EDITORS.getModule();

    @Context
    private UriInfo uri;

    private final ExportableModelFactory<String> modelFactory;

    @EWAEditor
    private final DocumentGeneration generation;

    private final String editorsContextName;

    private final CachedUserAgentStringParser userAgentCache;

    @Inject
    private JSONExportResource jsonExportResource;

    @Inject
    public DownloadDocumentResource(
            @EWAEditor ExportableModelFactory<String> modelFactory,
            @EWAEditor DocumentGeneration generation,
            @Named(COOKIE_CONTEXT_NAME) String contextName,
            @Named(EditorServicesModule.USER_AGENT_CACHE) CachedUserAgentStringParser userAgentCache) {

        this.modelFactory = modelFactory;
        this.generation = generation;
        this.editorsContextName = contextName;
        this.userAgentCache = userAgentCache;
    }

    @GET
    @Produces("text/plain")
    public String getGreeting() {
        return "Europass: Multipart Form Data Conversion Services";
    }

    // --------- XML -----------------------------------------------
    @POST
    @Produces({MediaType.APPLICATION_XML, MediaType.TEXT_HTML})
    @Path(Paths.PATH_XML)
    public Response toXml(@QueryParam("stats") String stats,
            @FormParam(FORM_PARAM_JSON) String jsonESP,
            @FormParam(FORM_PARAM_KEEP_CV) Boolean keepCv,
            @FormParam(FORM_PARAM_DOWNLOAD_TOKEN) String downloadToken,
            @FormParam(FORM_PARAM_REMOTE_UPLOAD_CALLBACK_URL) String remoteUploadCallbackUrl,
            @FormParam(FORM_PARAM_USER_COOKIE_ID) String cookieID,
            @HeaderParam(FORM_PARAM_USER_AGENT) String userAgent, @Context HttpServletRequest request) {

        Boolean includeContentDisposition = (remoteUploadCallbackUrl == null ? true : false);
        return process(jsonESP, ConversionFileType.XML, userAgent, downloadToken, stats, request, includeContentDisposition, false, cookieID, keepCv);

    }

    // --------- OPEN DOCUMENT -------------------------------------
    @POST
    @Produces({ContentTypes.OPEN_DOC_CT, MediaType.TEXT_HTML})
    @Path(Paths.PATH_OPEN_DOCUMENT)
    public Response toODT(@QueryParam("stats") String stats,
            @FormParam(FORM_PARAM_JSON) String jsonESP,
            @FormParam(FORM_PARAM_KEEP_CV) Boolean keepCv,
            @FormParam(FORM_PARAM_DOWNLOAD_TOKEN) String downloadToken,
            @FormParam(FORM_PARAM_USER_COOKIE_ID) String cookieID,
            @HeaderParam(FORM_PARAM_USER_AGENT) String userAgent,
            @Context HttpServletRequest request) {

        return process(jsonESP, ConversionFileType.OPEN_DOC, userAgent, downloadToken, stats, request, true, false, cookieID, keepCv);
    }

    // --------- WORD ----------------------------------------------
    @POST
    @Produces({ContentTypes.WORD_DOC_CT, MediaType.TEXT_HTML})
    @Path(Paths.PATH_WORD)
    public Response toWord(@QueryParam("stats") String stats,
            @FormParam(FORM_PARAM_JSON) String jsonESP,
            @FormParam(FORM_PARAM_KEEP_CV) Boolean keepCv,
            @FormParam(FORM_PARAM_DOWNLOAD_TOKEN) String downloadToken,
            @FormParam(FORM_PARAM_USER_COOKIE_ID) String cookieID,
            @HeaderParam(FORM_PARAM_USER_AGENT) String userAgent, @Context HttpServletRequest request) {

        return process(jsonESP, ConversionFileType.WORD_DOC, userAgent, downloadToken, stats, request, true, false, cookieID, keepCv);
    }

    // --------- PDF -----------------------------------------------
    @POST
    @Produces({ContentTypes.PDF_CT, MediaType.TEXT_HTML})
    @Path(Paths.PATH_PDF)
    public Response toPDF(@QueryParam("stats") String stats,
            @FormParam(FORM_PARAM_JSON) String jsonESP,
            @FormParam(FORM_PARAM_KEEP_CV) Boolean keepCv,
            @FormParam(FORM_PARAM_DOWNLOAD_TOKEN) String downloadToken,
            @FormParam(FORM_PARAM_USER_COOKIE_ID) String cookieID,
            @HeaderParam(FORM_PARAM_USER_AGENT) String userAgent, @Context HttpServletRequest request) {

        return process(jsonESP, ConversionFileType.PDF, userAgent, downloadToken, stats, request, true, false, cookieID, keepCv);
    }

    // --------- PDF - PREVIEW ---------------------------------------
    @POST
    @Produces({ContentTypes.PDF_CT, MediaType.TEXT_HTML})
    @Path(Paths.PATH_PDF + "/preview")
    public Response toPDFPreview(@QueryParam("stats") String stats,
            @FormParam(FORM_PARAM_JSON) String jsonESP,
            @FormParam(FORM_PARAM_KEEP_CV) Boolean keepCv,
            @FormParam(FORM_PARAM_DOWNLOAD_TOKEN) String downloadToken,
            @FormParam(FORM_PARAM_USER_COOKIE_ID) String cookieID,
            @HeaderParam(FORM_PARAM_USER_AGENT) String userAgent, @Context HttpServletRequest request) {

        return process(jsonESP, ConversionFileType.PDF, userAgent, downloadToken, stats, request, true, true, cookieID, keepCv);
    }

    protected Response process(String json, ConversionFileType file, String userAgent, String downloadToken, String keepstats,
            HttpServletRequest request, boolean includeDisposition, boolean preview, final String cookieUserID) {
        return process(json, file, userAgent, downloadToken, keepstats,
                request, includeDisposition, preview, cookieUserID, Boolean.FALSE);
    }

    protected Response process(String json, ConversionFileType file, String userAgent, String downloadToken, String keepstats,
            HttpServletRequest request, boolean includeDisposition, boolean preview,
            final String cookieUserID, Boolean keepCv) {

        ExportableModel modelContainer = modelFactory.getInstance(json, file, DocumentGenerator.EWA_EDITOR);

        //Set the destination of the export document
        if (preview) {
            modelContainer.setExportDestination(ExportDestination.BROWSER);
        } else {
            modelContainer.setExportDestination(ExportDestination.PC);

            if (keepCv != null && Boolean.valueOf(keepCv) == true) {
                try {
                    jsonExportResource.exportJSON(json, cookieUserID);
                } catch (Exception e) {
                    LOG.error("Exception during exporting JSON files.");
                }
            }
        }
        //set the user agent string to be retrieved later if needed
        modelContainer.augmentLogInfo(new ExtraLogInfo().add(LogFields.UA, userAgent).add(LogFields.REQ_ID, String.valueOf(request.getAttribute("X-Request-ID"))));

        //Set the locale to a request attribute, to be used for translating the error messages		
        request.setAttribute(EditorServicesModule.USER_REQUEST_LOCALE, modelContainer.getModel().getLocale());

        if (keepstats != null) {
            modelContainer.setKeepStats(Boolean.valueOf(keepstats));
        }

        ReadableUserAgent agent = userAgentCache.parse(userAgent);
        UserAgentFamily agentFamily = agent == null ? null : agent.getFamily();
        OperatingSystemFamily agentOSFamily = agent == null ? null : agent.getOperatingSystem().getFamily();

        generation.process(modelContainer);

        return prepareSuccessResponse(modelContainer, agentFamily, agentOSFamily, downloadToken, includeDisposition, preview);
    }

    private String getContentDisposition(SkillsPassport esp, UserAgentFamily agentFamily, OperatingSystemFamily agentOSFamily, boolean preview) {

        String fileName = aquireFileName(esp, agentFamily);
        return Utils.getContentDisposition(agentFamily, agentOSFamily, fileName, preview);
    }

    /**
     * Get the Filename from esp
     *
     * @param esp
     * @param agentFamily
     * @return the filename
     */
    private String aquireFileName(SkillsPassport esp, UserAgentFamily agentFamily) {
        String fileName = esp.getFilename();

        // EXCEPTION FOR SAFARI BROWSER
        if (agentFamily != null && UserAgentFamily.SAFARI.equals(agentFamily)) {
            fileName = esp.getSimpleFilename();
        }

        return fileName;
    }

    /**
     * Prepare an OK response and include the given Object to the body. At the
     * same type set the mimeType and fileName properly.
     *
     * Uses the agent to determine how to format the content disposition. Also
     * uses the downloadToken to set a cookie, to be used by the client to
     * indicate that the download is completed.
     *
     * @param model
     * @param agentFamily
     * @param agentOSFamily
     * @param downloadToken
     * @return
     */
    private Response prepareSuccessResponse(ExportableModel model, UserAgentFamily agentFamily, OperatingSystemFamily agentOSFamily, String downloadToken, boolean includeDisposition, boolean preview) {

        ConversionFileType fileType = model.getFileType();

        // Create a new ResponseBuilder with an OK status.
        ResponseBuilder r = Response.ok();

        //Content Type same as file type
        r.header("Content-Type", fileType.getMimeType() + Paths.UTF8_CHARSET);

        SkillsPassport esp = model.getModel();

        //content disposition
        if (includeDisposition) {
            r.header("Content-Disposition", getContentDisposition(esp, agentFamily, agentOSFamily, preview));
        }

        String filename = this.aquireFileName(esp, agentFamily);

        // pgia:Encode filename (for cookie value limitations that reproduces the bug described in EWA-1423)
        try {
            filename = URLEncoder.encode(filename, "UTF-8");
        } catch (UnsupportedEncodingException e) {
        }

        String host = uri.getBaseUri().getHost();

        /**
         * * DOWNLOAD TOKEN ***
         */
        if (downloadToken != null && !downloadToken.isEmpty()) {

//			r.cookie(new NewCookie("europass-ewa-temp-download-token", downloadToken, cookieContextName, host,
//					"Temporary Cookie to facilitate Download waiting indication timely removal", COOKIE_MAX_AGE, false));
            /**
             * note from vbla: Due to proxy configuration from ithaki to local
             * tomcat in order to access from europassdevX, there is a url
             * missmatch
             */
            if (host.contains(".intranet")) {
                host = host.replaceAll(".intranet", "");
            }

            if (host.equals("localhost")) {
                // Plain localhost is considered invalid for some browsers like
                // firefox...
                host = "";
            }

            String cookieContextName = this.editorsContextName;
            if (Strings.isNullOrEmpty(cookieContextName)) {
                cookieContextName = "/editors";
            } else {
                cookieContextName = cookieContextName.startsWith("/") ? cookieContextName : ("/" + cookieContextName);
            }

            // Attention!!!
            // The last parameter is need to be set to false in order for the
            // cookie to be readbale from javascript.
            // The int is the age in seconds. Set to 5min
            r.cookie(new NewCookie("europass-ewa-temp-document-filename", filename, cookieContextName, host,
                    "Temporary Cookie to facilitate Download waiting indication timely removal. Also holds the document filename string", COOKIE_MAX_AGE, false));
        }

        Object entity;
        switch (fileType) {
            case XML: {
                entity = model.xmlRepresentation();
                break;
            }
            default: {
                entity = model.asBytes();
                break;
            }
        }

        return r.entity(entity).build();
    }

    protected byte[] getPdf(String json, boolean keepStats) {
        return getPdf(json, keepStats, true);
    }

    protected byte[] getPdf(String json, boolean keepStats, boolean includeAttachments) {

        ExportableModel modelContainer = modelFactory.getInstance(json, ConversionFileType.PDF, DocumentGenerator.EWA_EDITOR);
        modelContainer.setKeepStats(keepStats);
        modelContainer.setExportDestination(ExportDestination.PC);
        modelContainer.getModel().getDocumentInfo().setBundle(null);

        if (modelContainer.getModel().hasAttachments()) {
            modelContainer.setDocumentType(EuropassDocumentType.ECV_ESP);
        }
        if (modelContainer.getDocumentType().equals(EuropassDocumentType.ECV_ESP)
                && !includeAttachments) {
            modelContainer.setDocumentType(EuropassDocumentType.ECV);
        }

        generation.process(modelContainer);
        byte[] entity = modelContainer.asBytes();

        return entity;
    }

}
