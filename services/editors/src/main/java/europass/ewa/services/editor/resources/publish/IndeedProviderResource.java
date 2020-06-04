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
import europass.ewa.enums.ConversionFileType;
import europass.ewa.enums.LogFields;
import europass.ewa.logging.ExtraLogInfo;
import europass.ewa.services.Paths;
import europass.ewa.services.annotation.EWAEditor;
import europass.ewa.services.conversion.model.ExportableModelFactory;
import europass.ewa.services.conversion.process.DocumentGeneration;
import europass.ewa.services.editor.modules.EditorServicesModule;
import europass.ewa.services.editor.resources.DownloadDocumentResource;
import static europass.ewa.services.editor.resources.publish.EuresProviderResource.FORM_PARAM_DOWNLOAD_TOKEN;
import europass.ewa.services.enums.HttpErrorCodeLabels;
import europass.ewa.services.exception.ApiException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
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
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

/**
 *
 * @author js
 */
@Path(Paths.CONVERSION_BASE + Paths.PATH_POST_INDEED)
public class IndeedProviderResource extends DownloadDocumentResource {

    private static final Logger LOG = LoggerFactory.getLogger(IndeedProviderResource.class);

    private final String indeedAuthorizationKey;
    private final String indeedPostUrl;

    @Inject
    public IndeedProviderResource(@EWAEditor ExportableModelFactory<String> modelFactory,
            @EWAEditor DocumentGeneration generation,
            @Named(COOKIE_CONTEXT_NAME) String contextName,
            @Named(EditorServicesModule.USER_AGENT_CACHE) CachedUserAgentStringParser userAgentCache,
            @Named("europass-ewa-services.indeed.authorization.key") String indeedAuthorizationKey,
            @Named("europass-ewa-services.indeed.post.url") String indeedPostUrl) {

        super(modelFactory, generation, contextName, userAgentCache);

        this.indeedAuthorizationKey = indeedAuthorizationKey;
        this.indeedPostUrl = indeedPostUrl;
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response postToIndeed(@QueryParam("stats") String stats,
            @FormParam(FORM_PARAM_JSON) String jsonESP,
            @FormParam(FORM_PARAM_DOWNLOAD_TOKEN) String downloadToken,
            @HeaderParam(FORM_PARAM_USER_AGENT) String userAgent,
            @Context HttpServletRequest request) {

        HttpPost post = new HttpPost(this.indeedPostUrl);
        post.setHeader("Authorization", this.indeedAuthorizationKey);

        String xml = process(jsonESP, ConversionFileType.XML, userAgent, downloadToken,
                stats, request, true, false, StringUtils.EMPTY).getEntity().toString();

        List<NameValuePair> postParameters = new ArrayList<>();
        postParameters.add(new BasicNameValuePair("resume", xml));

        post.setEntity(new UrlEncodedFormEntity(postParameters, StandardCharsets.UTF_8));

        HttpClient client = HttpClientBuilder.create().build();
        int status = 0;

        try {
            final HttpResponse resp = client.execute(post);

            status = resp.getStatusLine().getStatusCode();
            LOG.debug("indeed response status: " + status);
            if (status == HttpStatus.OK.value()) {
                String responseString = EntityUtils.toString(resp.getEntity());
                return Response.ok(responseString).build();
            } else {
                throw new ApiException();
            }
        } catch (ApiException | IOException e) {
            HttpErrorCodeLabels errorInfo = HttpErrorCodeLabels.match(status);
            throw ApiException.addInfo(new ApiException(e.getMessage(), errorInfo.getLabel(), errorInfo.getCode()),
                    new ExtraLogInfo().add(LogFields.UA, userAgent).add(LogFields.FILETYPE, "xml").add(LogFields.MESSAGE, "HTTP error status:" + status).
                            add(LogFields.LOCATION, "Post to Indeed").add(LogFields.MODULE, module));
        } finally {
            post.releaseConnection();
        }
    }

}
