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
import com.google.api.client.http.ByteArrayContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.Property;
import europass.ewa.mail.TemplateMerger;
import europass.ewa.modules.Default;
import europass.ewa.page.GuiLabelBundleLoader;
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
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

@Singleton
public class CloudShareReviewUploadServlet extends CommonHttpServlet {

    private final Locale defaultLocale;
    private final String environmentName;
    private final String googleCloudAppName;

    private final GuiLabelBundleLoader guiLabelBundleLoader;
    private final TemplateMerger templateMerger;

    private final String EWA_EDITORS_ENVIRONMENT_NAME = "context.project.current.environment";
    private final String EWA_EDITORS_CLOUD_GOOGLE_APPLICATION_NAME = "context.ewa.editors.googledrive.appname";

    private static final String JSON_POST_PARAM = "json";
    private static final String SHARED_REVIEW_FINALIZE_PARAM = "keepReviewSession";

    private static final String TITLE_DOCUMENT_PREFIX = "-";

    private static final Logger LOG = LoggerFactory.getLogger(CloudShareReviewUploadServlet.class);

    @Inject
    public CloudShareReviewUploadServlet(@Named(EWA_EDITORS_ENVIRONMENT_NAME) String environmentName,
            @Named(EWA_EDITORS_CLOUD_GOOGLE_APPLICATION_NAME) String googleCloudAppName,
            @Default Locale defaultLocale,
            final GuiLabelBundleLoader guiLabelBundleLoader,
            final TemplateMerger templateMerger) {
        this.environmentName = environmentName;
        this.googleCloudAppName = googleCloudAppName;
        this.defaultLocale = defaultLocale;
        this.guiLabelBundleLoader = guiLabelBundleLoader;
        this.templateMerger = templateMerger;
    }

    @Override
    public void doPost(final HttpServletRequest request, final HttpServletResponse response) {
        this.cloudUpload(request, response);
    }

    private void cloudUpload(final HttpServletRequest request, final HttpServletResponse response) {

        try {

            final String lang = setCookieCurrentLocale(request, response);

            final File privateKeyInfoJson = new File(getClass().getResource(SERVICE_ACCOUNT_CONF_PATH + SERVICE_ACCOUNT_CONF_FILENAME_PREFIX + environmentName + JSON_EXTENSION).toURI());
            final InputStream inputStream = new FileInputStream(privateKeyInfoJson);
            final GoogleCredential credential = GoogleCredential.fromStream(inputStream, TRANSPORT, JSON_FACTORY)
                    .createScoped(DriveScopes.all());
            final Drive driveService = new Drive.Builder(TRANSPORT, JSON_FACTORY, credential)
                    .setApplicationName(googleCloudAppName)
                    .build();

            final String documentID = setCookieOrGetValue(request, response, COOKIE_SHARED_DOCUMENT_ID, null, null, EXPIRATION_SESSION);;
            final String reviewerEmail = setCookieOrGetValue(request, response, COOKIE_SHARED_RECIPIENT_EMAIL, null, null, EXPIRATION_SESSION);;
            final com.google.api.services.drive.model.File driveFile = driveService.files().get(documentID).execute();

            if (request.getParameter(SHARED_REVIEW_FINALIZE_PARAM).equals("false")) {
                driveFile.setTitle(renameReviewedDocument(driveFile, reviewerEmail, lang));
            }

            final String modelAsJSON = request.getParameter(JSON_POST_PARAM);
            final ByteArrayContent byteContent = new ByteArrayContent(driveFile.getMimeType(), modelAsJSON.getBytes(StandardCharsets.UTF_8));
            driveService.files().update(documentID, driveFile, byteContent).execute();

            updateMetadataProperties(request, response, driveService, documentID);
            cleanReviewSessionAndCookies(request, response, request.getSession(), documentID, driveService);

            response.getWriter().write("success");
        } catch (final Exception e) {

            LOG.error("There is an error during post back of reviewed CV ... " + e.toString() + " more details : " + e.getMessage());
            try {
                response.getWriter().write("failure");
            } catch (IOException e1) {
                LOG.error("Error getting writter from response");
            }
        }
    }

    private String renameReviewedDocument(final com.google.api.services.drive.model.File driveFile, final String reviewerEmail, final String lang) {

        Locale locale = defaultLocale;
        if (lang != null) {
            locale = Locale.forLanguageTag(lang);
        }

        final ResourceBundle bundle = guiLabelBundleLoader.load(locale);

        final Map model = new LinkedHashMap();
        model.put("--reviewerEmail--", reviewerEmail);
        final String partOfDocumentFilename = TITLE_DOCUMENT_PREFIX
                + templateMerger.merge(model, guiLabelBundleLoader.getGuiLabelTextFromBundle(bundle, "cloudLogin.drawer.connected.document.share.document.name.prefix.part"));

        String newDocumentTitle = driveFile.getTitle();

        try {
            final String originalDocumentTitle = (driveFile.getTitle()).substring(0, driveFile.getTitle().lastIndexOf("-"));
            newDocumentTitle = originalDocumentTitle + partOfDocumentFilename + JSON_EXTENSION;

            LOG.debug("Renaming document after it has been reviewed - new filename is: {}", newDocumentTitle);
        } catch (final Exception e) {
            LOG.error("Cannot get title so to rename previewed document !");
        } finally {
            return newDocumentTitle;
        }
    }

    private String setCookieCurrentLocale(final HttpServletRequest request, final HttpServletResponse response) {
        String language = "en";
        final String lang = setCookieOrGetValue(request, response, COOKIE_POSTED_REMOTE_UPLOAD_LOCALE, null, null, EXPIRATION_SESSION);
        if (lang != null) {
            language = lang;
        }
        return setCookieOrGetValue(request, response, COOKIE_POSTED_REMOTE_UPLOAD_LOCALE, null, language.toUpperCase(), EXPIRATION_SESSION);
    }

    private void updateMetadataProperties(final HttpServletRequest request, final HttpServletResponse response,
            final Drive driveService, final String documentID) throws IOException {

        if (request.getParameter(SHARED_REVIEW_FINALIZE_PARAM).equals("false")) {

            final String prop = "{\"enabledWhenSharing\":true}";
            final Property mainProperty = new Property();
            mainProperty.setKey("sharingDocument");
            mainProperty.setValue(prop);
            mainProperty.setVisibility("PUBLIC");

            driveService.properties().insert(documentID, mainProperty).execute();
        }
    }

    // Cleans review session // also stops sharing document !!!
    private void cleanReviewSessionAndCookies(final HttpServletRequest request, final HttpServletResponse response, final HttpSession session,
            final String documentId, final Drive driveService) throws IOException {

        if (request.getParameter(SHARED_REVIEW_FINALIZE_PARAM).equals("false")) {
            // Stop sharing document now !
            final String permissionId
                    = setCookieOrGetValue(request, response, COOKIE_SHARED_PERMISSION_ID, null, null, EXPIRATION_SESSION);

            driveService.permissions().delete(documentId, permissionId).execute();

            session.removeAttribute(SESSION_POSTED_SHARED_JSON_MODEL_NAME);
            session.removeAttribute(SESSION_POSTED_FEEDBACK_NAME);
        }
    }
}
