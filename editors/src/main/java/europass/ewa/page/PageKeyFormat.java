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
package europass.ewa.page;

import javax.servlet.http.HttpServletRequest;

public interface PageKeyFormat {

    /**
     * Parse the request into a PageKey consisting of the path parts. Uses the
     * {@link PageKeyFormat.parse} to finally return the PageKey
     *
     * @param request
     * @return
     */
    PageKey parse(HttpServletRequest request);

    /**
     * Parses a String that corresponds to the sitePath and returns a PageKey.
     *
     * @param sitePath
     * @return
     */
    PageKey parse(String sitePath);

    /**
     * Parse the request on demand for specific urls without taking into account
     * the path matching rules
     *
     * @param request
     * @return
     */
    PageKey parseSpecificRequest(HttpServletRequest request);

    /**
     * Formats a pageKey to a site path String that matches the current path
     * template. In case there are path parameters included, they are written
     * un-escaped.
     *
     * @param pageKey
     * @return
     */
    String format(PageKey pageKey);

    /**
     * Formats a pageKey to a site path String that matches the current path
     * template. The escapeXml parameter controls whether the path parameters
     * should be xml escaped or not.
     *
     * @param pageKey
     * @param escapeXml
     * @return
     */
    String format(PageKey pageKey, boolean escapeXml);

    /**
     * Get the site path from a request
     *
     * @param request
     * @return
     */
    String getSitePath(HttpServletRequest request);

    String getSiteContext();
}
