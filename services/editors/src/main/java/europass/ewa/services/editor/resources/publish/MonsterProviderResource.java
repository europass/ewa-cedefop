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
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by jos on 7/19/2017.
 */
@Path(Paths.CONVERSION_BASE + Paths.PATH_POST_MONSTER)
public class MonsterProviderResource extends DownloadDocumentResource {

    public static final String FORM_PARAM_JSON = "json";
    public static final String FORM_PARAM_DOWNLOAD_TOKEN = "downloadToken";
    public static final String FORM_PARAM_USER_AGENT = "user-agent";
    public static final String FORM_PARAM_CV_PUBLIC = "isCvPublic";
    public static final String FORM_PARAM_MONSTER_COUNTRY = "monsterCountry";

    private final String monsterTokenUrl;
    private final String monsterClientId;
    private final String monsterClientSecret;
    private final String monsterRegistrationUrl;
    private final ExportableModelFactory<String> modelFactory;

    @EWAEditor
    private final DocumentGeneration generation;

    @Inject
    public MonsterProviderResource(@EWAEditor ExportableModelFactory<String> modelFactory,
            @EWAEditor DocumentGeneration generation,
            @Named(COOKIE_CONTEXT_NAME) String contextName,
            @Named(EditorServicesModule.USER_AGENT_CACHE) CachedUserAgentStringParser userAgentCache,
            @Named("europass-ewa-services.monster.token.url") String monsterTokenEndpoint,
            @Named("europass-ewa-services.monster.client.id") String monsterClientId,
            @Named("europass-ewa-services.monster.client.secret") String monsterClientSecret,
            @Named("europass-ewa-services.monster.registration.url") String monsterRegistrationUrl) {

        super(modelFactory, generation, contextName, userAgentCache);

        this.modelFactory = modelFactory;
        this.generation = generation;
        this.monsterTokenUrl = monsterTokenEndpoint;
        this.monsterClientId = monsterClientId;
        this.monsterClientSecret = monsterClientSecret;
        this.monsterRegistrationUrl = monsterRegistrationUrl;
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response postToMonster(@QueryParam("stats") String stats,
            @FormParam(FORM_PARAM_JSON) String jsonESP,
            @FormParam(FORM_PARAM_DOWNLOAD_TOKEN) String downloadToken,
            @FormParam(FORM_PARAM_CV_PUBLIC) boolean isCvPublic,
            @FormParam(FORM_PARAM_MONSTER_COUNTRY) String monsterCountry,
            @HeaderParam(FORM_PARAM_USER_AGENT) String userAgent,
            @Context HttpServletRequest request) {

        HttpPost post = new HttpPost();
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");

        int status = 0;

        HttpResponse response = null;
        HttpClient client = HttpClientBuilder.create().build();

        try {
            post.setURI(new URI(this.monsterTokenUrl));
            StringEntity body = new StringEntity("grant_type=client_credentials&scope=GatewayAccess&client_id=" + monsterClientId + "&client_secret=" + monsterClientSecret, "UTF-8");
            post.setEntity(body);
            response = client.execute(post);
            status = response.getStatusLine().getStatusCode();

            if (status == HttpStatus.OK.value()) {
                String responseString = EntityUtils.toString(response.getEntity());

                JSONObject responseJson = new JSONObject(responseString);

                String accessToken = (String) responseJson.get("access_token");

                HttpPost registrationPost = new HttpPost();

                registrationPost.setHeader("Content-Type", "application/json");
                registrationPost.setHeader("Authorization", "Bearer " + accessToken);
                registrationPost.setURI(new URI(this.monsterRegistrationUrl));

                JSONObject espJson = new JSONObject(jsonESP);
                JSONObject skillsPassportJson = espJson.getJSONObject("SkillsPassport");
                JSONObject learnerInfoJson = skillsPassportJson.getJSONObject("LearnerInfo");
                JSONObject identificationJson = learnerInfoJson.getJSONObject("Identification");
                String firstName = identificationJson.getJSONObject("PersonName").getString("FirstName");
                String lastName = identificationJson.getJSONObject("PersonName").getString("Surname");
                String email = identificationJson.getJSONObject("ContactInfo").getJSONObject("Email").getString("Contact");

                JSONObject registrationJson = new JSONObject();

                JSONObject userJson = new JSONObject();
                userJson.put("email", email);
                userJson.put("firstName", StringEscapeUtils.escapeJavaScript(firstName));
                userJson.put("lastName", StringEscapeUtils.escapeJavaScript(lastName));
                //TODO: check if true:
                //The two-letter ISO code should be used (ISO 3166 alpha-2), except for Greece
                //and the United Kingdom, for which the abbreviations EL and UK are recommended.
                //Source: http://publications.europa.eu/code/pdf/370000en.htm#pays
                String countryCode = monsterCountry == null || monsterCountry.equals("UK") ? "GB" : monsterCountry;
                userJson.put("countryAbbrev", countryCode);
                registrationJson.put("user", userJson);

                JSONObject resumeJson = new JSONObject();
                resumeJson.put("visibility", isCvPublic ? "1" : "0");
                resumeJson.put("fileType", "3"); //pdf
                //check size
                byte[] fullCvBytes = getPdf(jsonESP, true);
                int fullCvSize = fullCvBytes != null ? fullCvBytes.length : 0;
                byte[] plainCvBytes = getPdf(jsonESP, false, false); //no attachments

                byte[] bytes = fullCvSize <= 5242880 ? fullCvBytes : plainCvBytes;
                byte[] encoded = Base64.encodeBase64(bytes);
                String encodedString = new String(encoded);
                resumeJson.put("file", encodedString);
                registrationJson.put("resume", resumeJson);

                StringEntity registrationBody = new StringEntity(registrationJson.toString());
                registrationPost.setEntity(registrationBody);

                HttpClient client2 = HttpClientBuilder.create().build();
                HttpResponse registrationResponse = client2.execute(registrationPost);
                String registrationResponseString = EntityUtils.toString(registrationResponse.getEntity());
                status = registrationResponse.getStatusLine().getStatusCode();

                if (status == HttpStatus.OK.value()) {
                    Response newRes = Response.ok(registrationResponseString).build();
                    return newRes;
                } else {
                    throw new ApiException();
                }
            } else {

                throw new ApiException();
            }

        } catch (ApiException | IOException | URISyntaxException e) {
            HttpErrorCodeLabels errorInfo = HttpErrorCodeLabels.match(status);
            throw ApiException.addInfo(new ApiException(e, errorInfo.getLabel(), errorInfo.getCode()),
                    new ExtraLogInfo().add(LogFields.UA, userAgent).add(LogFields.FILETYPE, "pdf").add(LogFields.MESSAGE, "HTTP error status:" + status).
                            add(LogFields.LOCATION, "Post to Monster").add(LogFields.MODULE, module));
        } finally {
            post.releaseConnection();
        }
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Path(Paths.PATH_CHECK_CV_SIZE)
    public Response checkCvSize(@FormParam(FORM_PARAM_JSON) String jsonESP,
            @Context HttpServletRequest request) {

        byte[] fullCvBytes = getPdf(jsonESP, false);
        int fullCvSize = fullCvBytes != null ? fullCvBytes.length : 0;
        byte[] plainCvBytes = getPdf(jsonESP, false, false); //no attachments
        int plainCvSize = plainCvBytes != null ? plainCvBytes.length : 0;

        JSONObject responseJson = new JSONObject();
        responseJson.put("fullCvSize", fullCvSize);
        responseJson.put("plainCvSize", plainCvSize);

        Response.ResponseBuilder r = Response.ok(responseJson.toString(), MediaType.APPLICATION_JSON);
        Response response = r.build();

        return response;
    }

}
