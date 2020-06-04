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
package europass.ewa.services.rest.resources;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import europass.ewa.enums.ContentTypes;
import europass.ewa.enums.LogFields;
import europass.ewa.enums.ServerModules;
import europass.ewa.logging.ExtraLogInfo;
import europass.ewa.services.MediaTypeUtils;
import europass.ewa.services.Paths;
import europass.ewa.services.conversion.PDFAttachmentExtractor;
import europass.ewa.services.exception.ApiException;
import europass.ewa.services.exception.DisallowedMediaTypeException;
import europass.ewa.services.exception.UndefinedMediaTypeException;

@Path(Paths.EXTRACT_XML_ATTCH_BASE)
public class ExtractDocumentResource {

    @GET
    @Produces("text/plain")
    public String getGreeting() {
        return "Europass: This is a service for extracted europass cv!";
    }

    // ------------ EXTRACT XML FROM PDF (ECV+ESP)
    // --------------------------------
    @POST
    @Consumes({ContentTypes.PDF_CT})
    @Produces({MediaType.APPLICATION_XML + Paths.UTF8_CHARSET})
    public Response extractXmlFromPdf(InputStream in, @Context HttpServletRequest request) throws Exception {

        String requestId = UUID.randomUUID().toString();
        request.setAttribute("X-Request-ID", requestId);

        String xml = "";
        BufferedInputStream bis = new BufferedInputStream(in);

        // Check if media type is allowed.
        MediaType mediaType = MediaType.WILDCARD_TYPE;
        ArrayList<MediaType> listAllowedTypes = new ArrayList<>();
        listAllowedTypes.add(new MediaType("application", "pdf"));

        try {
            mediaType = MediaTypeUtils.readMediaType(bis, listAllowedTypes);
        } // handle UndefinedMediaTypeException
        // this exception is thrown when JSON is given as input
        // readMediaType cannot resolve the media type of JSON input
        catch (final UndefinedMediaTypeException e) {
            throw ApiException.addInfo(new DisallowedMediaTypeException(),
                    new ExtraLogInfo().add(e.getExtraLogInfo()).add(LogFields.MODULE, ServerModules.SERVICES_REST.getModule()).
                            add(LogFields.REQ_ID, requestId));
        }

        if (mediaType.isCompatible(MediaTypeUtils.APPLICATION_PDF)) {
            try {
                xml = PDFAttachmentExtractor.extractAttachment(bis);
            } catch (final ApiException e) {
                throw ApiException.addInfo(e,
                        new ExtraLogInfo().add(e.getExtraLogInfo()).add(LogFields.MODULE, ServerModules.SERVICES_REST.getModule()).
                                add(LogFields.REQ_ID, requestId));
            }
        }

        bis.close();

        ResponseBuilder response = Response.ok();

        response.header("Content-Type", MediaType.APPLICATION_XML + Paths.UTF8_CHARSET);

        return response.entity(xml).build();

    }
}
