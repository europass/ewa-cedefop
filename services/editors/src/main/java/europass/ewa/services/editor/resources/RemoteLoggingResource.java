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
package europass.ewa.services.editor.resources;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.name.Named;

import europass.ewa.services.Paths;
import europass.ewa.services.exception.ApiException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;

@Path(Paths.LOGGING_BASE)
public class RemoteLoggingResource {

    private static final Logger LOG = LoggerFactory.getLogger(RemoteLoggingResource.class);

    private final String hostID;

    @Inject
    public RemoteLoggingResource(
            @Named("europass-ewa-services.errcode.host.id") String hostID) {
        this.hostID = hostID + "_";
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response keepLogging(LoggingInfo info, @Context HttpServletRequest request) {
        if (info != null) {
            String trace = ApiException.TRACE_PREFIX + this.hostID + info.getErrCode();

            if (info.getErrorMessage() != null && info.getErrorMessage().contains("Error:") && info.getErrorMessage().contains("url:")) {
                String errorSpecificMessage = info.getErrorMessage().substring(info.getErrorMessage().indexOf("Error:") + 6, info.getErrorMessage().indexOf("url:"));
                info.setSpecificErrorMessage(errorSpecificMessage);
            }

            info.setCompleteErrorCode(trace);

            LOG.error("REMOTE LOGGING for IP: " + request.getRemoteAddr() + " " + info.asString() + " " + trace);
        }
        return Response.ok(info).build();
    }

}
