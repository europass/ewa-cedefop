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

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import com.google.inject.name.Named;

import europass.ewa.Constants;
import europass.ewa.modules.Default;
import europass.ewa.page.PageKey;
import europass.ewa.page.PageKeyFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class CloudShareLoadServlet extends CommonHttpServlet {

    private static final long serialVersionUID = -8094326698318298985L;

    private static final String JSON_MODEL_WRAPPER = "ModelWrapper";

    private static final String DEFAULT_DOCUMENT = "cv";

    private static final String SESSION_POSTED_JSON_NAME = "remoteModel";

    private static final String SESSION_POSTED_FEEDBACK_NAME = "remoteFeedback";

    private static final String REMOTE_UPLOAD_DOCUMENT = "[document]";

    private static final String REMOTE_UPLOAD_REDIRECT_URL = "/" + REMOTE_UPLOAD_DOCUMENT + "/remote-upload";

    public static final String EWA_EDITORS_CLOUD_LOAD_URL = "ewa.editors.cloud.load";

    public static final String EWA_EDITORS_SHARE_CLOUD_LOAD = "ewa.editors.share.cloud.load";

    private final PageKeyFormat keyFormat;

    private final PageKey defaultPage;

    private final HttpClient client;

    private final HttpClient clientCloud;

    public static final String EWA_EDITORS_LOAD_URL = "ewa.editors.rest.load";

    public static final String EWA_USER_COOKIE_ID = "context.ewa.editors.user.cookie.id";

    public static final String EWA_USER_COOKIE_PATTERN = "context.ewa.editors.user.cookie.pattern";

    private final String loadUrl;

    private final String ewaUserCookieId;

    private final String ewaUserCookiePattern;

    private final String DROPBOX_ATTRIBUTE_NAME = "dropbox";
    private final String ONEDRIVE_ATTRIBUTE_NAME = "onedrive";
    private final String DROPBOX_CONTENT_DOWNLOAD_URL = "https://content.dropboxapi.com/2/files/download";

    private static final Logger LOG = LoggerFactory.getLogger(CloudShareLoadServlet.class);

    @Inject
    public CloudShareLoadServlet(
            PageKeyFormat keyFormat,
            @Default PageKey defaultPage,
            HttpClient clientCloud,
            HttpClient client,
            @Named(EWA_EDITORS_SHARE_CLOUD_LOAD) String loadUrl,
            @Named(EWA_USER_COOKIE_ID) String ewaUserCookieId,
            @Named(EWA_USER_COOKIE_PATTERN) String ewaUserCookiePattern) {
        this.keyFormat = keyFormat;
        this.defaultPage = defaultPage;
        this.clientCloud = clientCloud;
        this.client = client;
        this.loadUrl = loadUrl;
        this.ewaUserCookieId = ewaUserCookieId;
        this.ewaUserCookiePattern = ewaUserCookiePattern;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        this.cloudLoad(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        this.cloudLoad(request, response);
    }

    private void cloudLoad(final HttpServletRequest request, final HttpServletResponse response) {

        try {
            //HOT! Set the encoding before reading the Parameter
            request.setCharacterEncoding("UTF-8");

            final HttpSession session = request.getSession();

            final String token = request.getParameter("token");
            final String url = request.getParameter("url");

            final List<String> cloudInfo = getXMLInfoFromCloudProviderRequest(url, token, request);
            final String xml = cloudInfo.get(0);
            final String statusLine = cloudInfo.get(1);

            redirectAfterResponse(request, response, session, xml, statusLine);

        } catch (final Exception e) {
            LOG.error("Failed to complete loading from share", e);
        }
    }

    private void redirectAfterResponse(final HttpServletRequest request, final HttpServletResponse response,
            final HttpSession session, final String xml,
            final String statusLine) throws IOException {
        int status = 500;
        String docType = DEFAULT_DOCUMENT;
        String feedback = "FAILURE";

        if (StringUtils.isNotEmpty(xml)) {

            final String modifiedLoadUrl = loadUrl + "?id=" + setCookieOrGetValue(request, response, ewaUserCookieId, ewaUserCookiePattern, null, EXPIRATION_MAX_TIME);
            final PostMethod post = new PostMethod(modifiedLoadUrl);
            post.setRequestHeader(new Header("Content-Type", "application/xml; charset=utf-8"));
            post.setRequestEntity(new StringRequestEntity(xml, "application/xml", "utf-8"));

            status = client.executeMethod(post);

            final Header contentTypeHeader = post.getResponseHeader("Content-Type");
            final String contentType = contentTypeHeader == null ? "" : contentTypeHeader.getValue();
            String model = "";

            if (status == 200 && isJSON(contentType)) {

                final JSONObject json = new JSONObject(new String(post.getResponseBody(), "utf-8"));
                final JSONObject properties = (JSONObject) json.get(JSON_MODEL_WRAPPER);
                final JSONObject esp = properties.getJSONObject(Constants.SKILLSPASSORT);
                final JSONObject docInfo = esp.getJSONObject("DocumentInfo");
                docType = (docInfo == null) ? DEFAULT_DOCUMENT : docInfo.getString("DocumentType");

                model = StringEscapeUtils.escapeJavaScript("{ \"SkillsPassport\" : " + esp + " }");

                feedback = "SUCCESS";
            } else if (isHTML(contentType)) {
                //Not OK status with JSON response
                //So something must be wrong
                feedback = StringEscapeUtils.escapeJavaScript(post.getResponseBodyAsString());
            }

            //Set the Session Attributes
            session.setAttribute(SESSION_POSTED_JSON_NAME, model);
            session.setAttribute(SESSION_POSTED_FEEDBACK_NAME, feedback);

            //Decide on the locale
            Locale locale = defaultPage.getLocale();
            final Header contentLanguage = post.getResponseHeader("Content-Language");
            if (contentLanguage != null) {
                final String language = contentLanguage.getValue();
                if (StringUtils.isNotEmpty(language)) {
                    locale = new Locale(language);
                }
            }

            if (docType == null) {
                docType = DEFAULT_DOCUMENT;
            }
            String finalDocType = getValidDocType(docType);
            //New pageKey based on the default

            String pageKeyPath = REMOTE_UPLOAD_REDIRECT_URL.replace(REMOTE_UPLOAD_DOCUMENT, finalDocType);
//				String pageKeyPath = "/cv/upload";

            PageKey redirectPage = new PageKey(locale, pageKeyPath);
            response.sendRedirect(keyFormat.format(redirectPage));
        } else {
            feedback = StringEscapeUtils.escapeJavaScript(statusLine);
            session.setAttribute(SESSION_POSTED_FEEDBACK_NAME, feedback);
            response.sendRedirect(keyFormat.format(this.defaultPage));
        }
    }

    private List<String> getXMLInfoFromCloudProviderRequest(final String url, final String token,
            final HttpServletRequest request) throws IOException {

        HttpMethodBase httpMethod;
        boolean oneDriveProvider = false;

        if (request.getParameter("cloudProvider") != null
                && ONEDRIVE_ATTRIBUTE_NAME.equals(request.getParameter("cloudProvider"))) {
            httpMethod = prepareOneDriveHttpMethod(url, token);
            oneDriveProvider = true;
        } else if (request.getParameter("cloudProvider") != null
                && DROPBOX_ATTRIBUTE_NAME.equals(request.getParameter("cloudProvider"))) {
            httpMethod = prepareDropboxHttpMethod(request, token);
        } else {
            httpMethod = prepareGoogleDriveHttpMethod(url, token);
        }

        if (!oneDriveProvider) {
            clientCloud.executeMethod(httpMethod);
        }
        final String xml = new String(httpMethod.getResponseBody(), "utf-8");

        if (httpMethod.getStatusCode() == 200 && StringUtils.isNotEmpty(xml)) {
            return Arrays.asList(xml, httpMethod.getStatusLine().toString());
        } else {
            return null;
        }
    }

    private HttpMethodBase prepareOneDriveHttpMethod(final String url, final String token) throws IOException {

        final String accessToken = URLEncoder.encode(token, "utf-8");
        HttpMethodBase httpMethod = new GetMethod(url);

        final NameValuePair[] queryParams = new NameValuePair[1];
        queryParams[0] = new NameValuePair("access_token", accessToken);
        httpMethod.setQueryString(queryParams);

        clientCloud.executeMethod(httpMethod);

        return httpMethod;
    }

    private HttpMethodBase prepareDropboxHttpMethod(final HttpServletRequest request, final String token) throws IOException {

        final String fPath = request.getParameter("fpath");
        final HttpMethodBase httpMethod = new PostMethod(DROPBOX_CONTENT_DOWNLOAD_URL);
        httpMethod.setRequestHeader(new Header("Authorization", "Bearer " + token));
        httpMethod.setRequestHeader(new Header("Dropbox-API-Arg", "{\"path\" : \"" + fPath + "\"}"));

        return httpMethod;
    }

    private HttpMethodBase prepareGoogleDriveHttpMethod(final String url, final String token) throws IOException {

        final HttpMethodBase httpMethod = new GetMethod(url);

        httpMethod.setURI(new org.apache.commons.httpclient.URI(url, false, "UTF-8"));
        httpMethod.setRequestHeader(new Header("Content-Type", "application/xml; charset=utf-8"));
        httpMethod.setRequestHeader(new Header("Authorization", "Bearer " + token));

        return httpMethod;
    }

    public static String getValidDocType(String docType) {
        return docType.toLowerCase().replace("_", "-").replaceAll("e(cv|lp)", "$1");
    }

    private static boolean isJSON(String contentType) {
        return (StringUtils.isNotEmpty(contentType) && contentType.indexOf("json") >= 0);
    }

    private static boolean isHTML(String contentType) {
        return (StringUtils.isNotEmpty(contentType) && contentType.indexOf("html") >= 0);
    }
}
