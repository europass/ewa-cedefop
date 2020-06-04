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
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.name.Named;

import europass.ewa.exception.TraceableException;
import europass.ewa.services.annotation.EWAEditor;
import europass.ewa.services.editor.messages.HtmlWrapper;
import europass.ewa.services.exception.ApiException;
import europass.ewa.services.exception.ErrorMessage;

@Provider
@Singleton
public class GenericExceptionMapper implements ExceptionMapper<Exception> {

    private static final Logger LOG = LoggerFactory.getLogger(GenericExceptionMapper.class);

    private final HtmlWrapper htmlWrapper;

    private final String hostID;

    @Inject
    public GenericExceptionMapper(@EWAEditor HtmlWrapper htmlWrapper,
            @Named("europass-ewa-services.errcode.host.id") String hostID) {
        this.htmlWrapper = htmlWrapper;
        //EWA 1561 Add host info to errcode
        this.hostID = hostID + "_";
    }

    @Override
    public Response toResponse(final Exception ex) {

        String trace = hostID + ApiException.randomTrace();

        LOG.error("{\"ErrCode\":\"" + trace + "\"}\n", ex);  // Log the trace code (without a prefix)

        trace = TraceableException.TRACE_PREFIX + trace;   // add the prefix

        ErrorMessage error = new ErrorMessage(ex, trace);

        return Response.status(Status.INTERNAL_SERVER_ERROR)
                .type(MediaType.TEXT_HTML)
                .entity(htmlWrapper.htmlWrap(error, String.valueOf(Status.INTERNAL_SERVER_ERROR.getStatusCode())))
                .build();
    }
}
