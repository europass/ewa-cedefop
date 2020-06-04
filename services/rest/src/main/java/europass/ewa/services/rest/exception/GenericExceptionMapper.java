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

import java.io.CharConversionException;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctc.wstx.exc.WstxException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.inject.Inject;

import europass.ewa.exception.TraceableException;
import europass.ewa.services.exception.ApiException;
import europass.ewa.services.exception.ErrorMessage;

@Provider
@Singleton
public class GenericExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger LOG = LoggerFactory.getLogger(GenericExceptionMapper.class);

    @Context
    private HttpHeaders headers;

    private static String hostID;

    @Inject
    public GenericExceptionMapper(@Named("europass-ewa-services.errcode.host.id") String hostID) {
        //EWA 1561 Add host info to errcode
        this.hostID = hostID + "_";
    }

    @Override
    public Response toResponse(final Exception ex) {

        String trace = hostID + ApiException.randomTrace();

        LOG.error("{\"ErrCode\":\"" + trace + "\"}\n", ex);

        trace = TraceableException.TRACE_PREFIX + trace;

        ErrorMessage error = new ErrorMessage(ex, trace);

        String responseType = MediaType.APPLICATION_XML;

        if (headers != null && headers.getMediaType() != null && headers.getMediaType().toString().contains(MediaType.APPLICATION_JSON)) {
            responseType = MediaType.APPLICATION_JSON;
        }

        if (ex instanceof JsonParseException || ex instanceof JsonMappingException) {
            JsonProcessingException jse = (JsonProcessingException) ex;
            error = new ErrorMessage(jse, trace);
            return Response.status(Status.BAD_REQUEST).entity(error).type(responseType).build();
        }
        if (ex instanceof WebApplicationException) {
            WebApplicationException we = new WebApplicationException(((WebApplicationException) ex).getResponse());
            error = new ErrorMessage(we, trace);
            return Response.status(we.getResponse().getStatus()).entity(error).type(responseType).build();
        }

        if (ex.getCause() != null && ex.getCause() instanceof CharConversionException) {
            CharConversionException che = new CharConversionException(ex.getMessage());
            error = new ErrorMessage(che, trace);
            return Response.status(Status.UNSUPPORTED_MEDIA_TYPE).entity(error).type(responseType).build();
        }

        if (ex.getCause() != null && ex.getCause() instanceof WstxException) {
            WstxException wstxe = new WstxException(ex);
            error = new ErrorMessage(wstxe, trace);
            return Response.status(Status.BAD_REQUEST).entity(error).type(responseType).build();
        }

        return Response.status(Status.INTERNAL_SERVER_ERROR).entity(error).type(responseType).build();

    }

}
