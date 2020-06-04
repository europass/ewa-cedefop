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
package europass.ewa.services.editor.messages;

import javax.inject.Singleton;

import europass.ewa.services.exception.ErrorMessage;

@Singleton
public class JsonHtmlWrapper implements HtmlWrapper {

    /**
     * Static utility to simply wrap a json string inside a script tag within an
     * html document.
     *
     * @param json
     * @return
     */
    public String htmlWrap(String json) {
        return htmlWrap(json, META_SUCCESS_STATUS);
    }

    /**
     * Wrap an error object to an html response including the specific status
     *
     * @param error
     * @param status
     * @return
     */
    public String htmlWrap(ErrorMessage error, String status) {
        return htmlWrap(error.asJsonString(), status);
    }

    /**
     * Wrap an error object to an html response including the specific status
     * and session id (if any)
     *
     * @param json
     * @param status
     * @param sessionId
     * @return
     */
    public String htmlWrap(String json, String status) {
        StringBuilder html = new StringBuilder();

        html.append("<!doctype html>");
        html.append("<html lang='en'>");

        html.append("<head>");
        html.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"/>");
        html.append("<meta name=\"status\" content=\"" + status + "\"/>");

        html.append("</head>");

        html.append("<script type=\"application/json\">");
        html.append(json);
        html.append("</script>");

        html.append("<body>" + IE_FRIENDLY_MESSAGE_BUSTER + "</body>");

        html.append("</html>");

        return html.toString();
    }

}
