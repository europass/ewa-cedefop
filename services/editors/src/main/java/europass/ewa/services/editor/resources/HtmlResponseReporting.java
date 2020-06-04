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

import java.io.IOException;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import europass.ewa.enums.LogFields;
import europass.ewa.enums.ServerModules;
import europass.ewa.logging.ExtraLogInfo;
import europass.ewa.services.annotation.Default;
import europass.ewa.services.annotation.EWAEditor;
import europass.ewa.services.editor.messages.HtmlWrapper;
import europass.ewa.services.enums.DownloadStatus;
import europass.ewa.services.exception.ApiException;

@Singleton
public class HtmlResponseReporting {

    @Inject
    @Default
    private static Locale defaultLocale;

    @Inject
    private static ObjectMapper mapper;

    @Inject
    @EWAEditor
    private static HtmlWrapper htmlWrapper;

    public static Response report(Object obj, String... extra) {

        String json;
        try {
            json = mapper.writeValueAsString(obj);
            String htmlResponse = htmlWrapper.htmlWrap(json, Status.OK.toString());

            // If there is a filename argument, append in a meta element the filename of the document just before the last script tag of the head element  
            if (extra != null && extra instanceof String[] && extra.length > 0) {
                String fileMeta = "<meta name=\"filename\" content=\"" + extra[0] + "\"/>";
                htmlResponse = htmlResponse.replaceAll("<script>", fileMeta + "<script>");
            }

            return Response.ok().type(MediaType.TEXT_HTML).entity(htmlResponse).build();

        } catch (JsonGenerationException | JsonMappingException e) {
            throw ApiException.addInfo(new ApiException(e, DownloadStatus.OTHER.getDescription(), Status.INTERNAL_SERVER_ERROR),
                    new ExtraLogInfo().add(LogFields.MODULE, ServerModules.SERVICES_EDITORS.getModule()));
        } catch (IOException e) {
            throw ApiException.addInfo(new ApiException(e, DownloadStatus.OTHER.getDescription(), Status.INTERNAL_SERVER_ERROR),
                    new ExtraLogInfo().add(LogFields.MODULE, ServerModules.SERVICES_EDITORS.getModule()));
        }
    }
}
