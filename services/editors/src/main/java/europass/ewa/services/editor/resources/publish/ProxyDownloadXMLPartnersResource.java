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
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;

@Path(Paths.CONVERSION_BASE + Paths.PATH_PARTNER_DOWNLOAD_PROXY)
public class ProxyDownloadXMLPartnersResource extends DownloadDocumentResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProxyDownloadXMLPartnersResource.class);

    @Inject
    public ProxyDownloadXMLPartnersResource(@EWAEditor ExportableModelFactory<String> modelFactory,
            @EWAEditor DocumentGeneration generation,
            @Named(COOKIE_CONTEXT_NAME) String contextName,
            @Named(EditorServicesModule.USER_AGENT_CACHE) CachedUserAgentStringParser userAgentCache) {

        super(modelFactory, generation, contextName, userAgentCache);
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response proxyDownloadXML(@Context HttpServletRequest request, @FormParam("xml") String xml,
            @FormParam("callbackurl") String callbackurl) {

        int responseCode = 404;

        org.apache.commons.httpclient.HttpClient client = new org.apache.commons.httpclient.HttpClient();
        PostMethod post = new PostMethod(callbackurl);
        RequestEntity entity = null;
        try {
            final StringBuilder xmlBuilder = new StringBuilder(xml);
            xmlBuilder.insert(0, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            entity = new StringRequestEntity(xmlBuilder.toString(), "text/xml", "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LOG.error("Unsupported Encoding", e);
        }
        if (entity != null) {
            post.setRequestEntity(entity);
        }

        //execute request
        try {
            responseCode = client.executeMethod(post);
        } catch (Exception e) {
            LOG.error("Failed to execute post-back request", e);
        } finally {
            post.releaseConnection();
        }
        if (responseCode != 200 && responseCode != 204) {
            LOG.error("Invalid Response Code: " + responseCode);
            HttpErrorCodeLabels errorInfo = HttpErrorCodeLabels.match(responseCode);
            throw ApiException.addInfo(new ApiException("Invalid Response Code", errorInfo.getLabel(), errorInfo.getCode()),
                    new ExtraLogInfo().add(LogFields.UA, request.getHeader(FORM_PARAM_USER_AGENT)).add(LogFields.FILETYPE, "xml").
                            add(LogFields.LOCATION, "Interoperability Partners").add(LogFields.MODULE, module));
        }
        return Response.ok().build();
    }
}
