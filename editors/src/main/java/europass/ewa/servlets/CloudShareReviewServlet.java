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

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import europass.ewa.modules.Default;
import europass.ewa.page.PageKey;
import europass.ewa.page.PageKeyFormat;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Singleton
public class CloudShareReviewServlet extends CommonHttpServlet {

    private final PageKeyFormat keyFormat;
    private final PageKey defaultPage;
    private final String environmentName;
    private final String googleCloudAppName;

    private final String EWA_EDITORS_ENVIRONMENT_NAME = "context.project.current.environment";
    private final String EWA_EDITORS_CLOUD_GOOGLE_APPLICATION_NAME = "context.ewa.editors.googledrive.appname";

    private static final String DEFAULT_DOCUMENT = "cv";
    private static final String JSON_DOCUMENT_TYPE = "DocumentType";
    private static final String JSON_DOCUMENT_INFO = "DocumentInfo";

    private static final String REMOTE_UPLOAD_DOCUMENT = "[document]";
    private static final String REMOTE_UPLOAD_REDIRECT_URL = "/" + REMOTE_UPLOAD_DOCUMENT + "/remote-upload";

    private static final String SESSION_FEEDBACK_SUCCESS = "SUCCESS";
    private static final String SESSION_FEEDBACK_FAILURE = "FAILURE";

    private static final Logger LOG = LoggerFactory.getLogger(CloudShareReviewServlet.class);

    @Inject
    public CloudShareReviewServlet(final PageKeyFormat keyFormat,
            @Default PageKey defaultPage,
            @Named(EWA_EDITORS_ENVIRONMENT_NAME) String environmentName,
            @Named(EWA_EDITORS_CLOUD_GOOGLE_APPLICATION_NAME) String googleCloudAppName) {
        this.keyFormat = keyFormat;
        this.defaultPage = defaultPage;
        this.environmentName = environmentName;
        this.googleCloudAppName = googleCloudAppName;
    }

    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) {
        this.cloudLoad(request, response);
    }

    @Override
    public void doPost(final HttpServletRequest request, final HttpServletResponse response) {
        this.cloudLoad(request, response);
    }

    private void cloudLoad(final HttpServletRequest request, final HttpServletResponse response) {

        String json = StringUtils.EMPTY;
        String fileId = StringUtils.EMPTY;

        try {

            final File privateKeyInfoJson = new File(getClass().getResource(SERVICE_ACCOUNT_CONF_PATH + SERVICE_ACCOUNT_CONF_FILENAME_PREFIX + environmentName + JSON_EXTENSION).toURI());
            final InputStream inputStream = new FileInputStream(privateKeyInfoJson);

            final GoogleCredential credential = GoogleCredential.fromStream(inputStream, TRANSPORT, JSON_FACTORY)
                    .createScoped(DriveScopes.all());
            final Drive driveService = new Drive.Builder(TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName(googleCloudAppName)
                    .build();

            fileId = request.getParameter("fileId");
            json = StringUtils.EMPTY;

            if (StringUtils.isNotBlank(fileId)) {
                final com.google.api.services.drive.model.File driveFile = driveService.files().get(fileId).execute();
                final String downloadURL = driveFile.getDownloadUrl();
                final InputStream str = downloadFile(downloadURL, driveService);
                json = IOUtils.toString(str, StandardCharsets.UTF_8);
            }

            redirectAndSetSession(request, response, fileId, json);
        } catch (final Exception e) {
            redirectAndSetSession(request, response, fileId, json);
            LOG.error("Failed to complete loading from share for review, redirecting.." + e);
        }
    }

    private static InputStream downloadFile(final String downloadURL, final Drive driveService) {

        if (downloadURL != null && downloadURL.length() > 0) {
            try {
                final HttpResponse resp = driveService.getRequestFactory().buildGetRequest(new GenericUrl(downloadURL)).execute();
                return resp.getContent();
            } catch (IOException e) {
                LOG.error("IO Error during getting content of document " + e);
                return null;
            }
        } else {
            // The file doesn't have any content stored on Drive.
            return null;
        }
    }

    private void redirectAndSetSession(final HttpServletRequest request, final HttpServletResponse response,
            final String fileId, final String json) {

        final HttpSession session = request.getSession();

        //Decide on locale
        Locale locale = defaultPage.getLocale();
        final String language = request.getParameter("language");
        if (StringUtils.isNotEmpty(language)) {
            locale = new Locale(language);
        }

        if (StringUtils.isNotEmpty(json)) {

            final String escapedJSON = StringEscapeUtils.escapeJavaScript(json);

            setCookieAndSessionAttributes(request, response, session, escapedJSON, language, fileId);

            final String pageKeyPath = REMOTE_UPLOAD_REDIRECT_URL.replace(REMOTE_UPLOAD_DOCUMENT, getValidDocType(json));

            final PageKey redirectPage = new PageKey(locale, pageKeyPath);

            try {
                response.sendRedirect(keyFormat.format(redirectPage));
            } catch (final IOException e) {
                LOG.error("Error redirecting !! .. ", e);
            }
        } else {
            session.setAttribute(SESSION_POSTED_FEEDBACK_NAME, SESSION_FEEDBACK_FAILURE);

            try {
                response.sendRedirect(keyFormat.format(this.defaultPage));
            } catch (final IOException e) {
                LOG.error("Error redirecting !!! ", e);
            }
        }
    }

    private void setCookieAndSessionAttributes(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session,
            final String json, final String language, final String fileId) {

        final String reviewerEmail = request.getParameter("reviewerEmail");
        final String senderEmail = request.getParameter("senderEmail");
        final String permissionId = request.getParameter("permissionId");

        createOrUpdateSessionCookie(request, response, COOKIE_POSTED_REMOTE_UPLOAD_LOCALE, language);
        createOrUpdateSessionCookie(request, response, COOKIE_SHARED_DOCUMENT_ID, fileId);
        createOrUpdateSessionCookie(request, response, COOKIE_SHARED_RECIPIENT_EMAIL, reviewerEmail);
        createOrUpdateSessionCookie(request, response, COOKIE_SHARED_PERMISSION_ID, permissionId);
        createOrUpdateSessionCookie(request, response, COOKIE_SHARED_SENDER_EMAIL, senderEmail);

        session.setAttribute(SESSION_POSTED_SHARED_JSON_MODEL_NAME, json);
        session.setAttribute(SESSION_POSTED_FEEDBACK_NAME, SESSION_FEEDBACK_SUCCESS);
    }

    private String getValidDocType(final String json) {

        String docType = DEFAULT_DOCUMENT;

        final JSONObject jsonObj = new JSONObject(json);
        if (jsonObj.has(JSON_DOCUMENT_INFO)) {
            final JSONObject docInfo = (JSONObject) jsonObj.get(JSON_DOCUMENT_INFO);
            if (docInfo.has(JSON_DOCUMENT_TYPE)) {
                docType = docInfo.getString(JSON_DOCUMENT_TYPE);
            }
        }
        if (docType == null) {
            docType = DEFAULT_DOCUMENT;
        }

        return docType.toLowerCase().replace("_", "-").replaceAll("e(cv|lp)", "$1");
    }
}
