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
package europass.ewa.services.editor.resources.publish;

import com.google.inject.name.Named;
import europass.ewa.CachedUserAgentStringParser;
import europass.ewa.enums.LogFields;
import europass.ewa.logging.ExtraLogInfo;
import europass.ewa.services.Paths;
import europass.ewa.services.annotation.EWAEditor;
import europass.ewa.services.conversion.model.ExportableModelFactory;
import europass.ewa.services.conversion.process.DocumentGeneration;
import europass.ewa.services.editor.modules.EditorServicesModule;
import europass.ewa.services.editor.resources.DownloadDocumentResource;
import europass.ewa.services.enums.HttpErrorCodeLabels;
import europass.ewa.services.exception.ApiException;
import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

/**
 *
 * @author at
 */
@Path(Paths.CONVERSION_BASE + Paths.PATH_POST_CV_LIBRARY)
public class CvLibraryProviderResource extends DownloadDocumentResource {

    private static final Logger LOG = LoggerFactory.getLogger(CvLibraryProviderResource.class);

    private final String cvLibraryPostUrl;
    private final String cvLibrarySiteUser;
    private final String cvLibrarySitePass;

    @Inject
    public CvLibraryProviderResource(@EWAEditor ExportableModelFactory<String> modelFactory,
            @EWAEditor DocumentGeneration generation,
            @Named(COOKIE_CONTEXT_NAME) String contextName,
            @Named(EditorServicesModule.USER_AGENT_CACHE) CachedUserAgentStringParser userAgentCache,
            @Named("europass-ewa-services.cvLibrary.post.url") String cvLibraryPostUrl,
            @Named("europass-ewa-services.cvLibrary.site.username") String cvLibrarySiteUser,
            @Named("europass-ewa-services.cvLibrary.site.password") String cvLibrarySitePass) {

        super(modelFactory, generation, contextName, userAgentCache);

        this.cvLibraryPostUrl = cvLibraryPostUrl;
        this.cvLibrarySiteUser = cvLibrarySiteUser;
        this.cvLibrarySitePass = cvLibrarySitePass;
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response postToCvLibrary(@QueryParam("stats") String stats,
            @FormParam(FORM_PARAM_JSON) String jsonESP,
            @FormParam(FORM_PARAM_DOWNLOAD_TOKEN) String downloadToken,
            @HeaderParam(FORM_PARAM_USER_AGENT) String userAgent, @Context HttpServletRequest request) {

        HttpPost post = new HttpPost(this.cvLibraryPostUrl);

        String authStr = this.cvLibrarySiteUser + ":" + this.cvLibrarySitePass;
        String basicAuth = "Basic " + new Base64().encodeToString(authStr.getBytes());
        post.setHeader("Authorization", basicAuth);

        post.setHeader("Accept", "application/json,text/html");
        //post.setHeader("Content-Type", "multipart/form-data, boundary=boundaryX");

        int status = 0;

        try {
            JSONObject espJson = new JSONObject(jsonESP);
            JSONObject skillsPassportJson = espJson.getJSONObject("SkillsPassport");
            JSONObject learnerInfoJson = skillsPassportJson.getJSONObject("LearnerInfo");
            JSONObject identificationJson = learnerInfoJson.getJSONObject("Identification");
            String firstName = identificationJson.getJSONObject("PersonName").getString("FirstName");
            String lastName = identificationJson.getJSONObject("PersonName").getString("Surname");
            String email = identificationJson.getJSONObject("ContactInfo").getJSONObject("Email").getString("Contact");

            byte[] cvBytes = getPdf(jsonESP, true);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addTextBody("first_name", firstName, ContentType.DEFAULT_TEXT);
            builder.addTextBody("last_name", lastName, ContentType.DEFAULT_TEXT);
            builder.addTextBody("email", email, ContentType.DEFAULT_TEXT);
            builder.addBinaryBody("cv", cvBytes, ContentType.DEFAULT_BINARY, "CV-Europass-" + lastName + "-CV.pdf");

            HttpEntity entity = builder.build();
            post.setEntity(entity);

            HttpClient client = HttpClientBuilder.create().build();
            HttpResponse resp = client.execute(post);
            status = resp.getStatusLine().getStatusCode();
            LOG.debug("cv-library response status: " + status);

            String response = EntityUtils.toString(resp.getEntity());
            JSONObject res = null;
            try {
                res = new JSONObject(response);
            } catch (JSONException ex) {
            }

            if (status == HttpStatus.CREATED.value() || status == HttpStatus.CONFLICT.value()) {
                return Response.ok(res.toString()).build();
            } else {
                throw new ApiException();
            }

        } catch (ApiException | IOException e) {

            HttpErrorCodeLabels errorInfo = HttpErrorCodeLabels.match(status);
            throw ApiException.addInfo(new ApiException(e.getMessage(), errorInfo.getLabel(), errorInfo.getCode()),
                    new ExtraLogInfo().add(LogFields.UA, userAgent).add(LogFields.FILETYPE, "xml").add(LogFields.MESSAGE, "HTTP error status:" + status).
                            add(LogFields.LOCATION, "Post to CV-Library").add(LogFields.MODULE, module));
        } finally {
            post.releaseConnection();
        }
    }
}
