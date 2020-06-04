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
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import europass.ewa.enums.ConversionFileType;
import europass.ewa.enums.LogFields;
import europass.ewa.enums.ServerModules;
import europass.ewa.logging.ExtraLogInfo;
import europass.ewa.model.SkillsPassport;
import europass.ewa.services.Paths;
import europass.ewa.services.annotation.EWAEditor;
import europass.ewa.services.conversion.model.ExportableModel;
import europass.ewa.services.conversion.model.ExportableModelFactory;
import europass.ewa.services.conversion.process.DocumentGeneration;
import europass.ewa.services.enums.DownloadStatus;
import europass.ewa.services.exception.ApiException;

@Path(Paths.CLOUD_BASE + "/googledrive")
public class GoogleDriveCloudProviderResource extends CloudProviderResource {

    private static final String GOOGLE_DRIVE_MULTIPART_UPLOAD_URL = "https://www.googleapis.com/upload/drive/v2/files?uploadType=multipart";

    private static final String module = ServerModules.SERVICES_EDITORS.getModule(),
            location = CloudProvider.GOOGLEDRIVE.getDescription();

    @Inject
    public GoogleDriveCloudProviderResource(
            @EWAEditor ExportableModelFactory<String> modelFactory,
            @EWAEditor DocumentGeneration generation) {
        super(modelFactory, generation, CloudProvider.GOOGLEDRIVE);
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(Paths.PATH_XML)
    public Response xml(@QueryParam("stats") String keepstats, StoreInfo storeinfo, @Context HttpServletRequest request) {
        return doStore(storeinfo, ConversionFileType.XML, Boolean.parseBoolean(keepstats), request.getHeader("user-agent"), request.getAttribute("X-Request-ID").toString());
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(Paths.PATH_OPEN_DOCUMENT)
    public Response opendoc(@QueryParam("stats") String keepstats, StoreInfo storeinfo, @Context HttpServletRequest request) {
        return doStore(storeinfo, ConversionFileType.OPEN_DOC, Boolean.parseBoolean(keepstats), request.getHeader("user-agent"), request.getAttribute("X-Request-ID").toString());
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(Paths.PATH_WORD)
    public Response word(@QueryParam("stats") String keepstats, StoreInfo storeinfo, @Context HttpServletRequest request) {
        return doStore(storeinfo, ConversionFileType.WORD_DOC, Boolean.parseBoolean(keepstats), request.getHeader("user-agent"), request.getAttribute("X-Request-ID").toString());
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(Paths.PATH_PDF)
    public Response pdf(@QueryParam("stats") String keepstats, StoreInfo storeinfo, @Context HttpServletRequest request) {
        return doStore(storeinfo, ConversionFileType.PDF, Boolean.parseBoolean(keepstats), request.getHeader("user-agent"), request.getAttribute("X-Request-ID").toString());
    }

    //-------------------------------------------------------------------------//
    @Override
    Response store(ExportableModel modelContainer, ConversionFileType fileType, String folder, String token) {

        SkillsPassport esp = modelContainer.getModel();
        String fname = esp.getFilename();
        String fmimeType = fileType.getMimeType();
        byte[] documentBytes = modelContainer.asBytes();

        CloseableHttpClient httpclient = HttpClients.createDefault();

        int statusCode;

        try {
            // M U L T I P A R T   U P L O A D --- OK			
            String url = GOOGLE_DRIVE_MULTIPART_UPLOAD_URL;
            //See https://developers.google.com/drive/web/folder
            String metadata = "{"
                    + "\"title\" : \"" + fname + "\","
                    + "\"mimeType\" : \"" + fmimeType + "\","
                    + "\"parents\" : ["
                    + "{ "
                    + "\"kind\": \"drive#fileLink\","
                    + "\"id\" : \"" + folder + "\""
                    + "}"
                    + "]"
                    + "}";
            HttpEntity multipartEntity = MultipartEntityBuilder.create()
                    //1. Metadata part: Must come first, and Content-Type must match one of the accepted metadata formats.
                    .addTextBody("metadata", metadata, ContentType.APPLICATION_JSON)
                    //2. Media part: Must come second, and Content-Type must match one the method's accepted media MIME types.
                    .addBinaryBody("media", documentBytes, ContentType.create(fmimeType), fname)
                    .setBoundary("europass-document")
                    .build();

//			//TODO: Consider using https://developers.google.com/drive/web/manage-uploads#resumable for large files
            HttpPost post = new HttpPost(url);
            post.addHeader("Content-Type", "multipart/related; boundary=\"europass-document\"");
            //Authorization: Bearer for Entire PUT
            post.addHeader("Authorization", "Bearer " + token);
            post.setEntity(multipartEntity);

            //Response
            HttpResponse response = httpclient.execute(post);
            statusCode = response.getStatusLine().getStatusCode();
//			printRequestResponse ( put, response );

        } catch (final ApiException e) {
            throw ApiException.addInfo(e, new ExtraLogInfo().add(modelContainer.getExtraLogInfo()).add(LogFields.LOCATION, location).add(LogFields.MODULE, module));
        } catch (final Exception e) {
            throw ApiException.addInfo(new ApiException(e, DownloadStatus.CLOUD_GENERIC.getDescription() + appendProviderName(), Status.INTERNAL_SERVER_ERROR),
                    new ExtraLogInfo().add(modelContainer.getExtraLogInfo()).add(LogFields.LOCATION, location).add(LogFields.MODULE, module));
        } finally {
            try {
                httpclient.close();
            } catch (final Exception e) {
            }
        }

        if (fname != null && !fname.isEmpty()) {
            return handleResponseStatus(statusCode, fname);
        } else {
            return handleResponseStatus(statusCode);
        }

    }

}
