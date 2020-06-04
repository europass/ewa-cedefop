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
package europass.ewa.services;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.google.common.base.Strings;

public final class ResponseUtils {

    //Suppress default constructor for noninstantiability
    private ResponseUtils() {
        throw new AssertionError();
    }

    public static Response buildResponse(String content, String mediaType, int status, String... accept) {
        ResponseBuilder r = Response.ok();
        r.header("Content-Type", mediaType + ";charset=utf-8");
        r.status(status);
        if ((accept instanceof String[]) && accept.length > 0) {
            if (!Strings.isNullOrEmpty(accept[0])) {
                r.header("Accept", accept[0]);
            }
        }

        return r.entity(content).build();
    }

}
