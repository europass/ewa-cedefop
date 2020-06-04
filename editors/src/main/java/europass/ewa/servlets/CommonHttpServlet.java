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
package europass.ewa.servlets;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

public class CommonHttpServlet extends HttpServlet {

    protected static final String SERVICE_ACCOUNT_CONF_PATH = "/cloud-share-config/";
    protected static final String SERVICE_ACCOUNT_CONF_FILENAME_PREFIX = "google-service-account-key-";
    protected static final String JSON_EXTENSION = ".json";
    protected static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    protected static final HttpTransport TRANSPORT = new NetHttpTransport();

    protected static final String SESSION_POSTED_SHARED_JSON_MODEL_NAME = "sharedRemoteModel";
    protected static final String SESSION_POSTED_FEEDBACK_NAME = "remoteFeedback";

    protected static final String COOKIE_POSTED_REMOTE_UPLOAD_LOCALE = "remoteUploadPartnerLocale";
    protected static final String COOKIE_SHARED_DOCUMENT_ID = "sharedDocumentId";
    protected static final String COOKIE_SHARED_PERMISSION_ID = "sharedPermissionId";
    protected static final String COOKIE_SHARED_RECIPIENT_EMAIL = "shareRecipientEmail";
    protected static final String COOKIE_SHARED_SENDER_EMAIL = "shareSenderEmail";

    protected final static int EXPIRATION_MAX_TIME = 10 * 60 * 60 * 24 * 365; // expires in 10 years
    protected final static int EXPIRATION_SESSION = -1; // expires when browser ends.

    public void createOrUpdateSessionCookie(final HttpServletRequest req, final HttpServletResponse resp,
            final String cookieName, final String cookieValue) {

        final Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setMaxAge(EXPIRATION_SESSION);
        cookie.setPath("/editors/");
        cookie.setValue(cookieValue);

        resp.addCookie(cookie);
    }

    public String setCookieOrGetValue(final HttpServletRequest request, final HttpServletResponse response,
            final String ewaUserCookieId, final String ewaUserCookiePattern,
            final String cookieValueProvided, final int expiration) {
        //Get the europass-editors-user cookie
        final Cookie[] cookies = ((HttpServletRequest) request).getCookies();
        String cookieValue = "";
        boolean ewaUserCookieExists = false;

        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].getName().equals(ewaUserCookieId)) {
                    cookieValue = cookies[i].getValue();
                    ewaUserCookieExists = true;
                    break;
                }
            }
        }

        //create europass-editors-user cookie if it does not exist already
        if (cookies == null || !ewaUserCookieExists) {

            Cookie editorsUserCookie;
            if (cookieValueProvided != null) {
                editorsUserCookie = new Cookie(ewaUserCookieId, cookieValueProvided);
            } else {
                final UUID uidValue = UUID.fromString(ewaUserCookiePattern);
                editorsUserCookie = new Cookie(ewaUserCookieId, uidValue.randomUUID().toString());
            }

            editorsUserCookie.setPath("/editors/");
            editorsUserCookie.setMaxAge(expiration);
            cookieValue = editorsUserCookie.getValue();
            response.addCookie(editorsUserCookie);
        }

        return cookieValue;
    }

}
