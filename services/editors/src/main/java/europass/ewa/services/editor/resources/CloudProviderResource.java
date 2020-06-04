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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import joptsimple.internal.Strings;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonRootName;

import europass.ewa.enums.ConversionFileType;
import europass.ewa.enums.ExportDestination;
import europass.ewa.enums.LogFields;
import europass.ewa.enums.ServerModules;
import europass.ewa.logging.ExtraLogInfo;
import europass.ewa.services.conversion.model.ExportableModel;
import europass.ewa.services.conversion.model.ExportableModelFactory;
import europass.ewa.services.conversion.process.DocumentGeneration;
import europass.ewa.services.enums.DownloadStatus;
import europass.ewa.services.exception.ApiException;
import europass.ewa.statistics.DocumentGenerator;

public abstract class CloudProviderResource {

    final ExportableModelFactory<String> modelFactory;

    final DocumentGeneration generation;

    final CloudProvider providerName;

    private static String module = ServerModules.SERVICES_EDITORS.getModule();

    public CloudProviderResource(ExportableModelFactory<String> modelFactory,
            DocumentGeneration generation,
            CloudProvider providerName) {
        this.modelFactory = modelFactory;
        this.generation = generation;
        this.providerName = providerName;
    }

    @GET
    @Produces("text/plain")
    public String getGreeting() {
        return "Europass: Cloud Service Provider" + this.getClass().getSimpleName();
    }

    /**
     * *********************************************************************************************
     */
    /**
     * Method reused by all cloud providers.
     *
     * Prepares the model from the provided input and throws necessary
     * exceptions. Delegates to each provider's store method for the actual
     *
     * @param storeinfo
     * @param fileType
     * @param keepstats
     * @return
     */
    Response doStore(StoreInfo storeinfo, ConversionFileType fileType, boolean keepstats, String userAgent) {
        return doStore(storeinfo, fileType, keepstats, userAgent, null);
    }

    Response doStore(StoreInfo storeinfo, ConversionFileType fileType, boolean keepstats, String userAgent, String requestId) {

        String location = this.providerName.getDescription();

        if (storeinfo == null) {
            throw ApiException.addInfo(new ApiException("Could not store document to cloud provider, because the provided input is null", DownloadStatus.INPUT_EMPTY.getDescription(), Status.BAD_REQUEST),
                    new ExtraLogInfo().add(LogFields.LOCATION, location + " Export").
                            add(LogFields.UA, userAgent).add(LogFields.FILETYPE, fileType.getExtension()).add(LogFields.MODULE, module));
        }
        String jsonESP = storeinfo.getJson();
        String folder = storeinfo.getFolder();
        String token = storeinfo.getToken();

        if (Strings.isNullOrEmpty(jsonESP) || Strings.isNullOrEmpty(folder) || Strings.isNullOrEmpty(token)) {
            throw ApiException.addInfo(new ApiException("Could not store document to cloud provider, because the provided input is null or empty", DownloadStatus.INPUT_EMPTY.getDescription(), Status.BAD_REQUEST),
                    new ExtraLogInfo().add(LogFields.LOCATION, location + " Export").
                            add(LogFields.UA, userAgent).add(LogFields.FILETYPE, fileType.getExtension()).add(LogFields.MODULE, module));
        }
        ExportableModel modelContainer = null;

        try {
            modelContainer = modelFactory.getInstance(jsonESP, fileType, DocumentGenerator.EWA_EDITOR);
            modelContainer.setKeepStats(keepstats);
            switch (this.providerName) {
                case DROPBOX:
                    modelContainer.setExportDestination(ExportDestination.DROPBOX);
                    break;
                case GOOGLEDRIVE:
                    modelContainer.setExportDestination(ExportDestination.GDRIVE);
                    break;
                case ONEDRIVE:
                    modelContainer.setExportDestination(ExportDestination.ONEDRIVE);
                    break;
                default:
                    modelContainer.setExportDestination(ExportDestination.UNKNOWN);
                    break;
            }
            //add the request Id 
            modelContainer.augmentLogInfo(LogFields.REQ_ID, requestId);
            generation.process(modelContainer);
        } catch (final ApiException e) {
            //change the status returned and wrap the extra logging info
            e.setStatus(Status.INTERNAL_SERVER_ERROR);
            e.setCode(DownloadStatus.CLOUD_GENERIC.getDescription() + appendProviderName());;
            throw ApiException.addInfo(e, new ExtraLogInfo().add(LogFields.LOCATION, location + " Export").
                    add(LogFields.UA, userAgent).add(LogFields.FILETYPE, fileType.getExtension()).add(LogFields.MODULE, module));
        } catch (final Exception e) {
            throw ApiException.addInfo(new ApiException(e, DownloadStatus.CLOUD_GENERIC.getDescription() + appendProviderName(), Status.INTERNAL_SERVER_ERROR),
                    new ExtraLogInfo().add(LogFields.LOCATION, location + " Export").
                            add(LogFields.UA, userAgent).add(LogFields.FILETYPE, fileType.getExtension()).add(LogFields.MODULE, module));
        }

        //add User agent to model container for future reference
        modelContainer.augmentLogInfo(LogFields.UA, userAgent);

        //abstract method implemented by the extending classes
        return store(modelContainer, fileType, folder, token);

    }

    /**
     * *********************************************************************************************
     */

    abstract Response store(ExportableModel modelContainer, ConversionFileType fileType, String folder, String token);

    /**
     * *********************************************************************************************
     */
    enum CloudProvider {
        GOOGLEDRIVE("googledrive"),
        DROPBOX("dropbox"),
        ONEDRIVE("onedrive");

        private String description;

        CloudProvider(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    /**
     * *********************************************************************************************
     */

    String filenameTimeAppend(String fname, String ext) {

        int hours = DateTime.now().getHourOfDay();
        int minutes = DateTime.now().getMinuteOfHour();
        int seconds = DateTime.now().getSecondOfMinute();
        String exactTime = (hours > 9 ? hours : "0" + hours) + "_" + (minutes > 9 ? minutes : "0" + minutes) + "_" + (seconds > 9 ? seconds : "0" + seconds);

        if (!Strings.isNullOrEmpty(fname)) {
            if (fname.endsWith("." + ext)) {
                fname = fname.replaceAll("\\." + ext, "-" + exactTime + "." + ext);
            }
        } else {
            fname = "CV-Europass-" + exactTime + "." + ext;
        }

        return fname;
    }

    String appendProviderName() {
        return "." + providerName.getDescription();
    }

    /**
     * *********************************************************************************************
     */
    Response handleResponseStatus(int statusCode, String... extra) {

        String location = this.providerName.getDescription();

        ExtraLogInfo logInfo = new ExtraLogInfo().add(LogFields.LOCATION, location + " Export").add(LogFields.MODULE, module);

        if (statusCode >= 200 && statusCode < 300) {

            if (extra != null && extra instanceof String[] && extra.length > 0) {

                // Filename
                if (extra.length == 1) {
                    String htmlResponse = "<html><head><meta name=\"filename\" content=\"" + extra[0] + "\"/></head><body></body></html>";
                    return Response.ok().type(MediaType.TEXT_HTML).entity(htmlResponse).build();
                }
                // Filename, flag, content
                if (extra.length == 2) {
                    String htmlResponse
                            = "<html>"
                            + "<head>"
                            + "<meta name=\"filename\" content=\"" + extra[0] + "\"/>"
                            + "<meta name=\"shareUrlBase\" content=\"" + extra[1] + "\"/>"
                            + "</head><body></body></html>";
                    return Response.ok().type(MediaType.TEXT_HTML).entity(htmlResponse).build();
                }
                // Filename, share url base, content, session id | google share
                // Filename, share url base, fileUrl, session id | onedrive share
                if (extra.length == 5) {

                    String htmlResponse = "";

                    if (extra[0].equals("google-share")) {

                        htmlResponse
                                += "<html>"
                                + "<head>"
                                + "<meta name=\"filename\" content=\"" + extra[1] + "\"/>"
                                + "<meta name=\"shareUrlBase\" content=\"" + extra[2] + "\"/>"
                                + "<meta name=\"jsessionid\" content=\"" + extra[4] + "\"/>"
                                + "</head><body><iframe id=\"xml-content\">" + extra[3] + "</iframe></body></html>";
                    }
                    if (extra[0].equals("onedrive-share")) {

                        htmlResponse
                                += "<html>"
                                + "<head>"
                                + "<meta name=\"filename\" content=\"" + extra[1] + "\"/>"
                                + "<meta name=\"shareUrlBase\" content=\"" + extra[2] + "\"/>"
                                + "<meta name=\"jsessionid\" content=\"" + extra[3] + "\"/>"
                                + "<meta name=\"fileUrl\" content=\"" + extra[4] + "\"/>"
                                + "</head><body></body></html>";
                    }

                    return Response.ok().header("Content-Type", MediaType.TEXT_HTML).type(MediaType.TEXT_HTML).entity(htmlResponse).build();
                }
                // Filename, share url base, content, session id | google share
                if (extra.length == 4 && (extra[0].equals("dropbox-share") || extra[0].equals("onedrive-share"))) {

                    String htmlResponse
                            = "<html>"
                            + "<head>"
                            + "<meta name=\"filename\" content=\"" + extra[1] + "\"/>"
                            + "<meta name=\"shareUrlBase\" content=\"" + extra[2] + "\"/>"
                            + "<meta name=\"jsessionid\" content=\"" + extra[3] + "\"/>"
                            + "</head><body></body></html>";
                    return Response.ok().type(MediaType.TEXT_HTML).entity(htmlResponse).build();
                }

                return Response.ok().build();

            }
            return Response.ok().build();

        } else if (statusCode >= 300 && statusCode < 400) {
            throw ApiException.addInfo(new ApiException(
                    "Failed to upload the generated document due to an HTTP redirection problem",
                    DownloadStatus.CLOUD_REMOTE_REDIRECTION.getDescription() + appendProviderName(), Status.NOT_FOUND), logInfo);
        } else if (statusCode >= 400 && statusCode < 500) {
            throw ApiException.addInfo(new ApiException(
                    "Failed to upload the generated document due to an authorization problem",
                    DownloadStatus.CLOUD_REMOTE_UNAUTHORIZED.getDescription() + appendProviderName(), Status.UNAUTHORIZED), logInfo);
        } else {
            throw ApiException.addInfo(new ApiException(
                    "Failed to upload the generated document due to a server problem with the service provider", DownloadStatus.CLOUD_REMOTE_SERVERERROR.getDescription() + appendProviderName(),
                    Status.SERVICE_UNAVAILABLE), logInfo);
        }
    }

    /**
     * *********************************************************************************************
     */

    @JsonRootName(value = "data")
    static class StoreInfo {

        private String json;
        private String folder;
        private String token;

        public String getJson() {
            return json;
        }

        public void setJson(String json) {
            this.json = json;
        }

        public String getFolder() {
            return folder;
        }

        public void setFolder(String folder) {
            this.folder = folder;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }

    /**
     * *********************************************************************************************
     */
    void printRequestResponse(HttpEntityEnclosingRequestBase request, HttpResponse response) throws IOException {
        System.out.println("request:-------------------");
        System.out.println(request.getRequestLine());
        Header headers[] = request.getAllHeaders();
        for (Header h : headers) {
            System.out.println(h.getName() + ": " + h.getValue());
        }
        System.out.println("content:-------------------");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        request.getEntity().writeTo(bytes);
        String content = bytes.toString("UTF-8");
        System.out.println(content);
        System.out.println("response:-------------------");
        System.out.println(response.getStatusLine());
        System.out.println("content:-------------------");
        String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
        System.out.println(responseString);
    }

    protected int uploadToDropboxAction(final ExportableModel modelContainer, final String authToken,
            final String folder, final String filename,
            final CloseableHttpClient httpclient) {

        final String DROPBOX_CONTENT_UPLOAD_URL = "https://content.dropboxapi.com/2/files/upload";
        final HttpPost post = new HttpPost(DROPBOX_CONTENT_UPLOAD_URL);
        final ByteArrayEntity reqEntity = new ByteArrayEntity(modelContainer.asBytes());

        post.setEntity(reqEntity);
        post.addHeader("Authorization", "Bearer " + authToken);
        post.addHeader("Content-Type", "application/octet-stream");
        post.addHeader("Dropbox-API-Arg", "{\"path\" : \"" + ("/" + folder + "/" + StringEscapeUtils.escapeJava(filename)) + "\", "
                + "\"mode\": \"add\", \"autorename\": true}");

        int statusCode = 500;

        try {
            final HttpResponse response = httpclient.execute(post);
            statusCode = response.getStatusLine().getStatusCode();

            return statusCode;

        } catch (final Exception e) {
            throw ApiException.addInfo(new ApiException(e, DownloadStatus.CLOUD_GENERIC.getDescription() + appendProviderName(),
                    Status.INTERNAL_SERVER_ERROR),
                    new ExtraLogInfo().add(modelContainer.getExtraLogInfo()).add(LogFields.LOCATION, "Dropbox")
                            .add(LogFields.MODULE, ServerModules.SERVICES_EDITORS.getModule()));
        } finally {
            try {
                httpclient.close();
                return statusCode;
            } catch (final Exception e) {
            }
        }
    }
}
