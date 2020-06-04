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
package europass.ewa.services.rest.exception;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.google.inject.Inject;

import europass.ewa.enums.LogFields;
import europass.ewa.services.exception.ApiException;

@Provider
@Singleton
public class ApiExceptionMapper implements ExceptionMapper<ApiException> {

    @Context
    private UriInfo uri;

    @Context
    private HttpHeaders headers;

    @Context
    HttpServletRequest request;

    private final String hostID;

    @Inject
    public ApiExceptionMapper(@Named("europass-ewa-services.errcode.host.id") String hostID) {
        //EWA 1561 Add host info to errcode
        this.hostID = hostID + "_";
    }

    @Override
    public Response toResponse(final ApiException ex) {

        String trace = hostID + ex.getTraceOnly();

        ex.addInfo(ex, LogFields.ERRCODE, trace).
                log();

        String responseType = MediaType.APPLICATION_XML;
        if (headers != null && headers.getMediaType() != null && headers.getMediaType().toString().contains(MediaType.APPLICATION_JSON)) {
            responseType = MediaType.APPLICATION_JSON;
        }
        return Response.status(ex.getStatusCode()).entity(ex.asError(trace)).type(responseType).build();

    }

}
