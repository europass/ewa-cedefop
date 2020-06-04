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

import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

import europass.ewa.Constants;
import europass.ewa.modules.Default;
import europass.ewa.page.PageKey;
import europass.ewa.page.PageKeyFormat;

@Singleton
public class CloudLoadServlet extends HttpServlet {

    private static final long serialVersionUID = -8094326698318298985L;

    private static final String DEFAULT_DOCUMENT = "cv";

    private static final String CLOUD_LOAD_DOCUMENT = "[document]";

    private static final String CLOUD_LOAD_REDIRECT_URL = "/" + CLOUD_LOAD_DOCUMENT + "/google-upload";

    private static final String SESSION_POSTED_JSON_NAME = "remoteModel";

    private static final String SESSION_POSTED_FEEDBACK_NAME = "remoteFeedback";

    private static final String CLOUD_FILE_ID = "cloud-file-id";

    private static final String CLOUD_TOKEN_ID = "cloud-token-id";

    private static final String CLOUD_USER_ID = "cloud-user-id";

    public static final String EWA_EDITORS_CLOUD_LOAD_URL = "ewa.editors.cloud.load";

    private final PageKeyFormat keyFormat;

    private final PageKey defaultPage;

    private final HttpClient client;

    private final String loadUrl;

    @Inject
    public CloudLoadServlet(
            PageKeyFormat keyFormat,
            @Default PageKey defaultPage,
            HttpClient client,
            @Named(EWA_EDITORS_CLOUD_LOAD_URL) String loadUrl) {
        this.keyFormat = keyFormat;
        this.defaultPage = defaultPage;
        this.client = client;
        this.loadUrl = loadUrl;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        this.cloudLoad(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        this.cloudLoad(request, response);
    }

    private void cloudLoad(HttpServletRequest request, HttpServletResponse response) {

        try {
            //HOT! Set the encoding before reading the Parameter
            request.setCharacterEncoding("UTF-8");
            String fileId = request.getParameter(CLOUD_FILE_ID);
            String token = request.getParameter(CLOUD_TOKEN_ID);
            String userId = request.getParameter(CLOUD_USER_ID);

            String docType = null;

            GetMethod get = new GetMethod(loadUrl);
            get.setRequestHeader(new Header("Content-Type", "application/json; charset=utf-8"));
            NameValuePair[] params = {
                new NameValuePair(CLOUD_FILE_ID, fileId),
                new NameValuePair(CLOUD_TOKEN_ID, token),
                new NameValuePair(CLOUD_USER_ID, userId)
            };
            get.setQueryString(params);

            int status = client.executeMethod(get);

            HttpSession session = request.getSession();
            Header contentTypeHeader = get.getResponseHeader("Content-Type");
            String contentType = contentTypeHeader == null ? "" : contentTypeHeader.getValue();
            String model = "";
            String feedback = "FAILURE";

            if (status == 200 && isJSON(contentType)) {

                JSONObject json = new JSONObject(get.getResponseBodyAsString());

                JSONObject properties = json.getJSONObject("Uploaded");

                JSONObject esp = properties.getJSONObject(Constants.SKILLSPASSORT);
                //esp.put( Constants.SESSIONID, properties.get(Constants.SESSIONID).toString() );

                //Document Type
                JSONObject docInfo = esp.getJSONObject("DocumentInfo");
                docType = (docInfo == null) ? "cv" : docInfo.getString("DocumentType");
                model = StringEscapeUtils.escapeJavaScript("{ \"SkillsPassport\" : " + esp + " }");

                feedback = "SUCCESS";
            } else if (isHTML(contentType)) {
                //Not OK status with JSON response
                //So something must be wrong
                feedback = StringEscapeUtils.escapeJavaScript(get.getResponseBodyAsString());
            }

            //Set the Session Attributes
            session.setAttribute(SESSION_POSTED_JSON_NAME, model);
            session.setAttribute(SESSION_POSTED_FEEDBACK_NAME, feedback);

            //Decide on the locale
            Locale locale = defaultPage.getLocale();
            Header contentLanguage = get.getResponseHeader("Content-Language");
            if (contentLanguage != null) {
                String language = contentLanguage.getValue();
                if (StringUtils.isNotEmpty(language)) {
                    locale = new Locale(language);
                }
            }
            //
            if (docType == null) {
                docType = DEFAULT_DOCUMENT;
            }
            String finalDocType = getValidDocType(docType);
            //New pageKey based on the default

            PageKey redirectPage = new PageKey(locale, CLOUD_LOAD_REDIRECT_URL.replace(CLOUD_LOAD_DOCUMENT, finalDocType));
            //Redirect
            response.sendRedirect(keyFormat.format(redirectPage));

        } catch (final Exception e) {
            e.printStackTrace();
        }
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
