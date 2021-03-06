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
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ByteArrayEntity;
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
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Path(Paths.CLOUD_BASE + Paths.SHARE + "/onedrive")
public class OneDriveCloudShareResource extends CloudProviderResource {

    private static final String ONE_DRIVE_API_PATH = "https://api.onedrive.com/v1.0/drive/items/";
    private static final String ONE_DRIVE_API_ROOT = "https://api.onedrive.com/v1.0/drive/root:/";
    private static final String ONEDRIVE_DEFAULT_SHARE_FOLDER = "Europass/shares";

    @Context
    HttpServletRequest context;

    @Inject
    public OneDriveCloudShareResource(@EWAEditor ExportableModelFactory<String> modelFactory, @EWAEditor DocumentGeneration generation) {
        super(modelFactory, generation, CloudProvider.ONEDRIVE);
    }

    @POST
    @Produces(MediaType.TEXT_HTML)
    @Consumes(MediaType.APPLICATION_JSON)
    @Path(Paths.PATH_XML)
    public Response xml(@QueryParam("stats") String keepstats, StoreInfo storeinfo, @Context HttpServletRequest request) {
        return doStore(storeinfo, ConversionFileType.XML, Boolean.parseBoolean(keepstats), request.getHeader("user-agent"), request.getAttribute("X-Request-ID").toString());
    }

    //-------------------------------------------------------------------------//
    @Override
    Response store(final ExportableModel modelContainer, final ConversionFileType fileType,
            final String folder, final String token) {

        final SkillsPassport esp = modelContainer.getModel();
        final CloseableHttpClient httpclient = HttpClients.createDefault();
        final String fname = this.filenameTimeAppend(esp.getFilename(), "xml");

        final String shareUrl = Paths.SHARE + "/" + esp.getLocale() + "/onedrive";

        String fileLocation = "";

        String url = StringUtils.EMPTY;
        try {
            url = ONE_DRIVE_API_PATH + folder + "/children/" + fname + "/content" + "?access_token=" + URLEncoder.encode(token, "utf-8");
        } catch (final UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        int statusCode = 500;

        if (StringUtils.isNotEmpty(url)) {
            try {
                final HttpPut put = new HttpPut(url);
                final ByteArrayEntity reqEntity = new ByteArrayEntity(modelContainer.asBytes());
                put.setEntity(reqEntity);

                final HttpResponse response = httpclient.execute(put);
                statusCode = response.getStatusLine().getStatusCode();

                fileLocation = getDownloadLocationRequest(statusCode, fname, token);

            } catch (final Exception e) {
                throw ApiException.addInfo(new ApiException(e, DownloadStatus.CLOUD_GENERIC.getDescription() + appendProviderName(),
                        Status.INTERNAL_SERVER_ERROR),
                        new ExtraLogInfo().add(modelContainer.getExtraLogInfo())
                                .add(LogFields.MODULE, ServerModules.SERVICES_EDITORS.getModule()));
            } finally {
                try {
                    httpclient.close();
                } catch (final Exception e) {
                }
            }
        }

        final String sessionID = (context.getSession().getId() != null ? context.getSession().getId() : "");
        if (fname != null && !fname.isEmpty()) {
            return handleResponseStatus(statusCode, "onedrive-share", fname, shareUrl, sessionID, fileLocation);
        } else {
            return handleResponseStatus(statusCode, "onedrive-share", "", shareUrl, sessionID, fileLocation);
        }
    }

    private String getDownloadLocationRequest(final int statusCode, final String fname, final String token) {

        final CloseableHttpClient client = HttpClients.createDefault();
        String fileLocation = StringUtils.EMPTY;

        try {
            if (statusCode >= 200 && statusCode < 300) {

                final String urlForItem = ONE_DRIVE_API_ROOT + ONEDRIVE_DEFAULT_SHARE_FOLDER + "/" + fname + "?access_token="
                        + URLEncoder.encode(token, "utf-8");
                final HttpGet get = new HttpGet(urlForItem);
                final HttpResponse responseForDownload = client.execute(get);

                final HttpEntity httpEntity = responseForDownload.getEntity();
                final String responseBody = EntityUtils.toString(httpEntity);
                final JSONObject jsonResponse = new JSONObject(responseBody);

                fileLocation = (String) jsonResponse.get("@content.downloadUrl");
            }
        } catch (final Exception e) {
            final String str = e.getMessage();
            System.out.println(str);

        } finally {
            try {
                client.close();
            } catch (final Exception e) {
            }
        }
        return fileLocation;
    }

}
