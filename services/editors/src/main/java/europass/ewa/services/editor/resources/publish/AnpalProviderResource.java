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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.inject.name.Named;
import europass.ewa.CachedUserAgentStringParser;
import europass.ewa.enums.LogFields;
import europass.ewa.logging.ExtraLogInfo;
import europass.ewa.resources.JsonResourceBundle;
import europass.ewa.services.Paths;
import europass.ewa.services.annotation.EWAEditor;
import europass.ewa.services.conversion.model.ExportableModelFactory;
import europass.ewa.services.conversion.process.DocumentGeneration;
import europass.ewa.services.editor.modules.EditorServicesModule;
import europass.ewa.services.editor.resources.DownloadDocumentResource;
import europass.ewa.services.enums.HttpErrorCodeLabels;
import europass.ewa.services.exception.ApiException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import javax.inject.Inject;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
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
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

/**
 *
 * @author at
 */
@Path(Paths.CONVERSION_BASE + Paths.PATH_POST_ANPAL)
public class AnpalProviderResource extends DownloadDocumentResource {

    private static final Logger LOG = LoggerFactory.getLogger(AnpalProviderResource.class);

    private final String anpalServiceToken;
    private final String anpalPostUrl;

    public static final String FORM_PARAM_FISCAL_CODE = "fiscalCode";
    public static final String FORM_PARAM_EXTRA_UE_CONSENT = "extraUEConsent";

    private static final String TEXTS_VERSION_KEY = "export.wizard.anpal.version";

    @Inject
    public AnpalProviderResource(@EWAEditor ExportableModelFactory<String> modelFactory,
            @EWAEditor DocumentGeneration generation,
            @Named(COOKIE_CONTEXT_NAME) String contextName,
            @Named(EditorServicesModule.USER_AGENT_CACHE) CachedUserAgentStringParser userAgentCache,
            @Named("europass-ewa-services.anpal.service.token") String anpalServiceToken,
            @Named("europass-ewa-services.anpal.post.url") String anpalPostUrl) {

        super(modelFactory, generation, contextName, userAgentCache);

        this.anpalServiceToken = anpalServiceToken;
        this.anpalPostUrl = anpalPostUrl;
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response postToAnpal(@QueryParam("stats") String stats,
            @FormParam(FORM_PARAM_JSON) String jsonESP,
            @FormParam(FORM_PARAM_FISCAL_CODE) String fiscalCode,
            @FormParam(FORM_PARAM_EXTRA_UE_CONSENT) boolean extraUEConsent,
            @HeaderParam(FORM_PARAM_USER_AGENT) String userAgent,
            @Context HttpServletRequest request) {

        JSONObject espJson = new JSONObject(jsonESP);
        JSONObject skillsPassportJson = espJson.getJSONObject("SkillsPassport");
        JSONObject learnerInfoJson = skillsPassportJson.getJSONObject("LearnerInfo");
        JSONObject identificationJson = learnerInfoJson.getJSONObject("Identification");
        String firstName = identificationJson.getJSONObject("PersonName").getString("FirstName");
        String lastName = identificationJson.getJSONObject("PersonName").getString("Surname");
        String email = identificationJson.getJSONObject("ContactInfo").getJSONObject("Email").getString("Contact");

        HttpPost post = new HttpPost(this.anpalPostUrl);

        int status = 0;

        try {
            String textsVersion = "0.0";
            final ResourceBundle bundle = ResourceBundle.getBundle("bundles/GuiLabelExtra_it", new JsonResourceBundle.Control(new ObjectMapper()));
            if (bundle != null && !Strings.isNullOrEmpty(TEXTS_VERSION_KEY)) {
                try {
                    textsVersion = bundle.getString(TEXTS_VERSION_KEY);
                } catch (final MissingResourceException e) {
                }
            }

            byte[] fullCvBytes = getPdf(jsonESP, true);
            byte[] encoded = Base64.encodeBase64(fullCvBytes);
            String encodedString = new String(encoded);
            String json = "{\"data\":\"" + encodedString
                    + "\", \"email\":\"" + email
                    + "\",\"fiscalCode\":\"" + fiscalCode
                    + "\",\"firstname\":\"" + firstName
                    + "\",\"lastname\":\"" + lastName
                    + "\",\"typeEuropassCVData\":\"PDFXML\",\"options\":{\"privacyConsent\":\"true\", "
                    + "\"extraUEConsent\":\"" + extraUEConsent + "\", "
                    + "\"sendEmail\":\"true\", "
                    + "\"version\":\"" + textsVersion + "\"}}";

            final HttpClient client = HttpClientBuilder.create().setSSLSocketFactory(createSSLConnectionFactory()).build();

            StringEntity entityJSON = new StringEntity(json);
            post.setEntity(entityJSON);

            post.setHeader("X-IBM-Client-Id", this.anpalServiceToken);
            post.addHeader("Content-Type", "application/json");

            final HttpResponse resp = client.execute(post);

            status = resp.getStatusLine().getStatusCode();
            LOG.debug("anpal response status: " + status);
            if (status == HttpStatus.OK.value()) {
                String responseString = EntityUtils.toString(resp.getEntity());

                Response newRes = Response.ok(responseString).build();
                return newRes;
            } else {

                throw new ApiException();
            }

        } catch (ApiException | IOException | KeyManagementException | NoSuchAlgorithmException e) {

            HttpErrorCodeLabels errorInfo = HttpErrorCodeLabels.match(status);
            throw ApiException.addInfo(new ApiException(e.getMessage(), errorInfo.getLabel(), errorInfo.getCode()),
                    new ExtraLogInfo().add(LogFields.UA, userAgent).add(LogFields.FILETYPE, "xml").add(LogFields.MESSAGE, "HTTP error status:" + status).
                            add(LogFields.LOCATION, "Post to ANPAL").add(LogFields.MODULE, module));
        } finally {
            post.releaseConnection();
        }
    }

    private SSLConnectionSocketFactory createSSLConnectionFactory() throws KeyManagementException, NoSuchAlgorithmException {
        final SSLContext sslContext = SSLContext.getInstance("SSL");
        sslContext.init(null, new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }}, new SecureRandom());

        HostnameVerifier allowAllHosts = new NoopHostnameVerifier();
        return new SSLConnectionSocketFactory(sslContext, allowAllHosts);
    }

}
