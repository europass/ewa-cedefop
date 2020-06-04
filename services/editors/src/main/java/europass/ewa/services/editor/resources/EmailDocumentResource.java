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

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.annotation.JsonRootName;
import com.google.common.base.Strings;

import europass.ewa.enums.ConversionFileType;
import europass.ewa.enums.ExportDestination;
import europass.ewa.enums.LogFields;
import europass.ewa.enums.ServerModules;
import europass.ewa.logging.ExtraLogInfo;
import europass.ewa.services.Paths;
import europass.ewa.services.annotation.EWAEditor;
import europass.ewa.services.annotation.EWAEditorEmail;
import europass.ewa.services.conversion.model.ExportableModel;
import europass.ewa.services.conversion.model.ExportableModelFactory;
import europass.ewa.services.conversion.process.DocumentGeneration;
import europass.ewa.services.enums.DownloadStatus;
import europass.ewa.services.enums.EmailStatus;
import europass.ewa.services.exception.ApiException;
import europass.ewa.statistics.DocumentGenerator;

@Path(Paths.EMAIL_BASE)
public class EmailDocumentResource {

    @Context
    HttpServletRequest context;

    public static final String FORM_PARAM_EMAIL_RECIPIENT = "recipient";

    public static final String FORM_PARAM_JSON = "json";

    private final ExportableModelFactory<String> modelFactory;

    private final DocumentGeneration generation;

    private static final String location = "Export to Email",
            module = ServerModules.SERVICES_EDITORS.getModule();

    @Inject
    public EmailDocumentResource(@EWAEditor ExportableModelFactory<String> modelFactory,
            @EWAEditorEmail DocumentGeneration generation,
            HtmlResponseReporting reporter) {

        this.modelFactory = modelFactory;

        this.generation = generation;
    }

    @GET
    @Produces("text/plain")
    public String getGreeting() {
        return "Europass: Multipart Form Data Email Services";
    }

    @POST
    @Path(Paths.PATH_XML)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_HTML)
    public Response emailXml(@QueryParam("stats") String stats, DataInfo datainfo, @Context HttpServletRequest request) {

        return process(datainfo, ConversionFileType.XML, stats, request.getHeader("user-agent"), request.getAttribute("X-Request-ID").toString());

    }

    @POST
    @Path(Paths.PATH_OPEN_DOCUMENT)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_HTML)
    public Response emailODT(@QueryParam("stats") String stats, DataInfo datainfo, @Context HttpServletRequest request) {

        return process(datainfo, ConversionFileType.OPEN_DOC, stats, request.getHeader("user-agent"), request.getAttribute("X-Request-ID").toString());

    }

    @POST
    @Path(Paths.PATH_WORD)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_HTML)
    public Response emailWord(@QueryParam("stats") String stats, DataInfo datainfo, @Context HttpServletRequest request) {

        return process(datainfo, ConversionFileType.WORD_DOC, stats, request.getHeader("user-agent"), request.getAttribute("X-Request-ID").toString());
    }

    @POST
    @Path(Paths.PATH_PDF)
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_HTML)
    public Response emailPDF(@QueryParam("stats") String stats, DataInfo datainfo, @Context HttpServletRequest request) {

        return process(datainfo, ConversionFileType.PDF, stats, request.getHeader("user-agent"), request.getAttribute("X-Request-ID").toString());
    }


    /* -------------------------------------------------------------------------- */
 /* -------------------------------------------------------------------------- */
    private Response process(DataInfo datainfo, ConversionFileType file, String keepstats, String ua, String requestId) {

        if (datainfo == null) {
            throw ApiException.addInfo(new ApiException("Could not send document via email, because the provided input is null", DownloadStatus.INPUT_EMPTY.getDescription(), Status.BAD_REQUEST),
                    getLogInfo(file.getMimeType(), requestId, ua));
        }

        // Decode the recipient email
        String recipient = datainfo.getRecipient();
        if (Strings.isNullOrEmpty(recipient)) {
            throw ApiException.addInfo(new ApiException("Failed to read recipient's email", EmailStatus.INVALID_EMAIL.getDescription(), Status.BAD_REQUEST),
                    getLogInfo(file.getMimeType(), requestId, ua));
        }

        String json = datainfo.getJson();
        if (Strings.isNullOrEmpty(json)) {
            throw ApiException.addInfo(new ApiException("Could not send document via email, because the provided JSON model is null", DownloadStatus.INPUT_EMPTY.getDescription(), Status.BAD_REQUEST),
                    getLogInfo(file.getMimeType(), requestId, ua));
        }
        ExportableModel modelContainer = modelFactory.getInstance(json, file, DocumentGenerator.EWA_EDITOR);

        modelContainer.setExportDestination(ExportDestination.EMAIL);

        if (keepstats != null) {
            modelContainer.setKeepStats(Boolean.valueOf(keepstats));
        }

        modelContainer.setRecipient(recipient);

        modelContainer.setRecipientIp(context.getRemoteAddr());

        modelContainer.augmentLogInfo(getLogInfo(file.getMimeType(), requestId, ua));

        try {
            generation.process(modelContainer);
        } catch (ApiException ex) {
            if ("download.other.error".equals(ex.getCode())) {
                ex.setCode(DownloadStatus.EXPORT_OTHER.getDescription());   //EWA 1551 point D.2, email export errors should show specific status code
            }
            throw ex;
        }

        return HtmlResponseReporting.report(modelContainer.getFeedback(), modelContainer.getModel().getFilename());

    }

    private static ExtraLogInfo getLogInfo(String fileType, String requestId, String ua) {
        return new ExtraLogInfo().add(LogFields.FILETYPE, fileType).add(LogFields.LOCATION, location)
                .add(LogFields.UA, ua).add(LogFields.MODULE, module).add(LogFields.REQ_ID, requestId);
    }

    @JsonRootName(value = "data")
    static class DataInfo {

        private String json;
        private String recipient;
//		private String filename;

        public String getJson() {
            return json;
        }

        public void setJson(String json) {
            this.json = json;
        }

        public String getRecipient() {
            return recipient;
        }

        public void setRecipient(String recipient) {
            this.recipient = recipient;
        }
//		public String getFilename() {return filename;}
//		public void setFilename(String filename) {this.filename = filename;}
    }
}
