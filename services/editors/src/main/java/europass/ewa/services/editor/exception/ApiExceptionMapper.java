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
package europass.ewa.services.editor.exception;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.google.inject.name.Named;

import europass.ewa.enums.LogFields;
import europass.ewa.services.annotation.EWAEditor;
import europass.ewa.services.editor.messages.HtmlWrapper;
import europass.ewa.services.exception.ApiException;

@Provider
@Singleton
public class ApiExceptionMapper implements ExceptionMapper<ApiException> {

    private final HtmlWrapper htmlWrapper;

    private final String hostID;

    private @Context
    HttpServletRequest request;

    @Inject
    public ApiExceptionMapper(@EWAEditor HtmlWrapper htmlWrapper,
            @Named("europass-ewa-services.errcode.host.id") String hostID) {
        this.htmlWrapper = htmlWrapper;
        //EWA 1561 Add host info to errcode
        this.hostID = hostID + "_";
    }

    @Override
    public Response toResponse(final ApiException ex) {

        String requestId = request.getAttribute("X-Request-ID").toString();
        String trace = hostID + ex.getTraceOnly();

        ex.addInfo(ex, LogFields.ERRCODE, trace).addInfo(ex, LogFields.REQ_ID, requestId);
        ex.log();

        int statusCode = ex.getStatusCode();

        return Response.status(statusCode)
                .type(MediaType.TEXT_HTML)
                .entity(htmlWrapper.htmlWrap(ex.asError(trace), String.valueOf(statusCode)))
                .build();
    }

}
