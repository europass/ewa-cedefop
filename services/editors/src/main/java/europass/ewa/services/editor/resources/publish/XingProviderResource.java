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

import com.google.common.base.Strings;
import com.google.inject.name.Named;
import europass.ewa.CachedUserAgentStringParser;
import europass.ewa.enums.ConversionFileType;
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
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jos on 7/19/2017.
 */
@Path(Paths.CONVERSION_BASE + Paths.PATH_POST_XING)
public class XingProviderResource extends DownloadDocumentResource {

    private static final Logger LOG = LoggerFactory.getLogger(XingProviderResource.class);

    private final String xingBase;
    private final String xingSiteUser;
    private final String xingSitePass;
    private final String xingRedirectSignupUrl;

    public static final String FORM_PARAM_JSON = "json";
    public static final String FORM_PARAM_DOWNLOAD_TOKEN = "downloadToken";
    public static final String FORM_PARAM_USER_AGENT = "user-agent";

    @Inject
    public XingProviderResource(@EWAEditor ExportableModelFactory<String> modelFactory,
            @EWAEditor DocumentGeneration generation,
            @Named(COOKIE_CONTEXT_NAME) String contextName,
            @Named(EditorServicesModule.USER_AGENT_CACHE) CachedUserAgentStringParser userAgentCache,
            @Named("europass-ewa-services.xing.post.url") String xingBase,
            @Named("europass-ewa-services.xing.redirect.url") String xingRedirectSignupUrl,
            @Named("europass-ewa-services.xing.site.username") String xingSiteUser,
            @Named("europass-ewa-services.xing.site.password") String xingSitePass) {

        super(modelFactory, generation, contextName, userAgentCache);

        this.xingBase = xingBase;
        this.xingSiteUser = xingSiteUser;
        this.xingSitePass = xingSitePass;
        this.xingRedirectSignupUrl = xingRedirectSignupUrl;
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response postToXing(@QueryParam("stats") String stats,
            @FormParam(FORM_PARAM_JSON) String jsonESP,
            @FormParam(FORM_PARAM_DOWNLOAD_TOKEN) String downloadToken,
            @HeaderParam(FORM_PARAM_USER_AGENT) String userAgent, @Context HttpServletRequest request) {

        HttpPost post = new HttpPost();
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");
        post.setHeader("Accept", "application/json,text/html");
        //post.setHeader("Host", xingBaseURI.getHost());
        post.setHeader("Expect", "100-continue");

        int status = 0;

        try {
            String xml = process(jsonESP, ConversionFileType.XML, userAgent, downloadToken,
                    stats, request, true, false, StringUtils.EMPTY).getEntity().toString();

            post.setURI(new URI(this.xingBase));

            //StringEntity body = new StringEntity(xml, "UTF-8");
            //post.setEntity(body);
            List<NameValuePair> postParameters = new ArrayList<NameValuePair>();
            postParameters.add(new BasicNameValuePair("europass", xml));
            final String ipAddress = request.getRemoteAddr();
            postParameters.add(new BasicNameValuePair("client_remote_address", ipAddress));

            post.setEntity(new UrlEncodedFormEntity(postParameters, StandardCharsets.UTF_8));

            if (!Strings.isNullOrEmpty(this.xingSiteUser) && !Strings.isNullOrEmpty(this.xingSitePass)) {

                String basicAuth = "Basic " + new String(new Base64().encode((this.xingSiteUser + ":" + this.xingSitePass).getBytes()));
                post.setHeader("Authorization", basicAuth);
            }

            HttpClient client = HttpClientBuilder.create().build();
            HttpResponse resp = client.execute(post);

            status = resp.getStatusLine().getStatusCode();
            LOG.debug("xing response status: " + status);

            String response = EntityUtils.toString(resp.getEntity());
            JSONObject res = null;
            try {
                res = new JSONObject(response);
            } catch (JSONException ex) {
            }

            if (status == HttpStatus.OK.value()) {
                res.get("token");

                String redirectPage = res.getString("redirect-page");
                String redirectSignupUrl = this.xingRedirectSignupUrl;
                String redirectURL = redirectPage.contains(redirectSignupUrl) ? redirectPage : redirectPage + "/";
                res.put("url", redirectURL);
                Response newRes = Response.ok(res.toString()).build();
                return newRes;
            } else {
                String errorMessage;
                try {
                    errorMessage = res != null ? res.getJSONObject("message").getString("message") : String.valueOf(status);
                } catch (JSONException e) {
                    errorMessage = "";
                }
                throw new ApiException(errorMessage);
            }

        } catch (ApiException | IOException | URISyntaxException e) {
            HttpErrorCodeLabels errorInfo = HttpErrorCodeLabels.match(status);
            throw ApiException.addInfo(new ApiException(e.getMessage(), errorInfo.getLabel(), errorInfo.getCode()),
                    new ExtraLogInfo().add(LogFields.UA, userAgent).add(LogFields.FILETYPE, "xml").add(LogFields.MESSAGE, "HTTP error status:" + status).
                            add(LogFields.LOCATION, "Post to XING").add(LogFields.MODULE, module));
        } finally {
            post.releaseConnection();
        }
    }

}
