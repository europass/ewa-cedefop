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

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class SessionAwareJsonHtmlWrapper implements HtmlWrapper {

    private final Provider<HttpServletRequest> httpRequest;

    @Inject
    public SessionAwareJsonHtmlWrapper(Provider<HttpServletRequest> httpRequest) {
        this.httpRequest = httpRequest;
    }

    /**
     * Session - aware utility to wrap a json string inside a script tag within
     * an html document, while adding the session id to a suitable meta tag.
     *
     * @param json, the json as string
     * @return
     */
    public String htmlWrap(String json) {
        return htmlWrap(json, META_SUCCESS_STATUS);
    }

    public String htmlWrap(europass.ewa.services.exception.ErrorMessage error, String status) {
        return htmlWrap(error.asJsonString(), status);
    }

    public String htmlWrap(String json, String status) {
        HttpSession session = httpRequest.get().getSession(true);
        String sessionId = session.getId();

        StringBuilder html = new StringBuilder();

        html.append("<!doctype html>");
        html.append("<html lang='en'>");

        html.append("<head>");
        html.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=utf-8\"/>");
        html.append("<meta name=\"jsessionid\" content=\"" + sessionId + "\">");
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
