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
@Path(Paths.CONVERSION_BASE + Paths.PATH_POST_EURES)
public class EuresProviderResource extends DownloadDocumentResource {

    public static final String FORM_PARAM_JSON = "json";
    public static final String FORM_PARAM_DOWNLOAD_TOKEN = "downloadToken";
    public static final String FORM_PARAM_USER_AGENT = "user-agent";

    private final String euresRedirectUrl;
    private final String euresBase;
    private final String euresSiteUser;
    private final String euresSitePass;

    @Inject
    public EuresProviderResource(@EWAEditor ExportableModelFactory<String> modelFactory,
            @EWAEditor DocumentGeneration generation,
            @Named(COOKIE_CONTEXT_NAME) String contextName,
            @Named(EditorServicesModule.USER_AGENT_CACHE) CachedUserAgentStringParser userAgentCache,
            @Named("europass-ewa-services.eures.post.url") String euresBase,
            @Named("europass-ewa-services.eures.redirect.url") String euresRedirectUrl,
            @Named("europass-ewa-services.eures.site.username") String euresSiteUser,
            @Named("europass-ewa-services.eures.site.password") String euresSitePass) {

        super(modelFactory, generation, contextName, userAgentCache);

        this.euresRedirectUrl = euresRedirectUrl;
        this.euresBase = euresBase;
        this.euresSiteUser = euresSiteUser;
        this.euresSitePass = euresSitePass;
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response postToEures(@QueryParam("stats") String stats, @FormParam(FORM_PARAM_JSON) String jsonESP,
            @FormParam(FORM_PARAM_DOWNLOAD_TOKEN) String downloadToken,
            @HeaderParam(FORM_PARAM_USER_AGENT) String userAgent,
            @Context HttpServletRequest request) {

        String redirectURL = this.euresRedirectUrl;

        URI euresBaseURI = null;

        int status = 0; //default

        HttpPost post = new HttpPost();
        post.setHeader("Content-Type", "application/xml");
        post.setHeader("Accept", "*/*");
        //post.setHeader("Host", euresBaseURI.getHost() );
        post.setHeader("Expect", "100-continue");

        HttpResponse resp = null;
        HttpClient client = HttpClientBuilder.create().build();

        try {
            String xml = process(jsonESP, ConversionFileType.XML, userAgent, downloadToken,
                    stats, request, true, false, StringUtils.EMPTY).getEntity().toString();

            euresBaseURI = new URI(this.euresBase);
            post.setURI(euresBaseURI);
            StringEntity body = new StringEntity(xml, "UTF-8");
            post.setEntity(body);

            if (!Strings.isNullOrEmpty(this.euresSiteUser) && !Strings.isNullOrEmpty(this.euresSitePass)) {
                String basicAuth = "Basic " + new String(new Base64().encode((this.euresSiteUser + ":" + this.euresSitePass).getBytes()));
                post.setHeader("Authorization", basicAuth);
            }

            resp = client.execute(post);

            status = resp.getStatusLine().getStatusCode();

            if (status == HttpStatus.OK.value()) {
                String response = EntityUtils.toString(resp.getEntity());

                JSONObject res = new JSONObject(response);
                res.get("token");
                res.put("url", redirectURL);
                return Response.ok(res.toString()).build();
            } else {
                throw new ApiException();
            }

        } catch (ApiException | IOException | URISyntaxException e) {
            HttpErrorCodeLabels errorInfo = HttpErrorCodeLabels.match(status);
            throw ApiException.addInfo(new ApiException(e, errorInfo.getLabel(), errorInfo.getCode()),
                    new ExtraLogInfo().add(LogFields.UA, userAgent).add(LogFields.FILETYPE, "xml").add(LogFields.MESSAGE, "HTTP error status:" + status).
                            add(LogFields.LOCATION, "Post to Eures").add(LogFields.MODULE, module));
        } finally {
            post.releaseConnection();
        }
    }

}
