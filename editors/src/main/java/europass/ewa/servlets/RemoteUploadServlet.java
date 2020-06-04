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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import europass.ewa.modules.SupportedLocaleModule;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import europass.ewa.Constants;
import europass.ewa.modules.Default;
import europass.ewa.page.PageKey;
import europass.ewa.page.PageKeyFormat;

import javax.servlet.http.HttpSession;

@Singleton
public class RemoteUploadServlet extends CommonHttpServlet {

    private static final String JSON_MODEL_WRAPPER = "ModelWrapper";
    private static final String JSON_DOCUMENT_TYPE = "DocumentType";
    private static final String JSON_DOCUMENTINFO = "DocumentInfo";

    private static final Logger LOG = LoggerFactory.getLogger(RemoteUploadServlet.class);

    private static final long serialVersionUID = -8094326698318298985L;

    private static final String DEFAULT_DOCUMENT = "cv";

    private static final String REMOTE_UPLOAD_DOCUMENT = "[document]";

    private static final String REMOTE_UPLOAD_REDIRECT_URL = "/" + REMOTE_UPLOAD_DOCUMENT + "/remote-upload";

    private static final String SESSION_POSTED_JSON_NAME = "remoteModel";
    private static final String SESSION_POSTED_FEEDBACK_NAME = "remoteFeedback";

    private static final String CONTEXT_INTEROP_PARTNER_PAIR_PROPERTY_PREFIX = "context.ewa.interoperability.remote-upload.url-key-pair.partner";
    private static final String CONTEXT_INTEROP_PARTNER_KEY = "remoteUploadPartnerKey";
    private static final String CONTEXT_INTEROP_PARTNER_URL = "remoteUploadCallbackUrl";
    private static final String CONTEXT_INTEROP_PARTNER_NAME = "remoteUploadPartnerName";
    private static final String CONTEXT_INTEROP_PARTNER_LOCALE = "remoteUploadPartnerLocale";

    private static final String POSTED_XML_NAME = "europass-xml";

    private static final String POSTED_KEY_NAME = "partner-key";
    private static final String POSTED_URL_NAME = "callback-url";
    private static final String POSTED_NAME_VALUE = "partner-name";
    private static final String POSTED_NAME_LOCALE = "partner-locale";
    private static final String INVALID_POSTED_LOCALE = "invalid-locale";

    public static final String EWA_EDITORS_LOAD_URL = "ewa.editors.rest.load";
    public static final String EWA_USER_COOKIE_ID = "context.ewa.editors.user.cookie.id";
    public static final String EWA_USER_COOKIE_PATTERN = "context.ewa.editors.user.cookie.pattern";

    private final PageKeyFormat keyFormat;
    private final PageKey defaultPage;

    private final HttpClient client;

    private final String loadUrl;

    private final String ewaUserCookieId;
    private final String ewaUserCookiePattern;

    private Set<Locale> supportedLocales;

    @Inject
    public RemoteUploadServlet(
            PageKeyFormat keyFormat,
            @Default PageKey defaultPage,
            HttpClient client,
            @Named(EWA_EDITORS_LOAD_URL) String loadUrl,
            @Named(EWA_USER_COOKIE_ID) String ewaUserCookieId,
            @Named(EWA_USER_COOKIE_PATTERN) String ewaUserCookiePattern,
            @Named(SupportedLocaleModule.EWA_SUPPORTED_LANGUAGES) Set<Locale> supportedLocales) {
        this.keyFormat = keyFormat;
        this.defaultPage = defaultPage;
        this.client = client;
        this.loadUrl = loadUrl;
        this.ewaUserCookieId = ewaUserCookieId;
        this.ewaUserCookiePattern = ewaUserCookiePattern;
        this.supportedLocales = supportedLocales;
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        this.manageRemoteUpload(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        this.manageRemoteUpload(request, response);
    }

    private void manageRemoteUpload(HttpServletRequest request, HttpServletResponse response) {

        try {

            //HOT! Set the encoding before reading the Parameter
            request.setCharacterEncoding("UTF-8");

            final String xml = request.getParameter(POSTED_XML_NAME);
            String docType = "cv";

            if (StringUtils.isNotEmpty(xml)) {

                final String modifiedLoadUrl = loadUrl + "?id=" + setCookieOrGetValue(request, response, ewaUserCookieId, ewaUserCookiePattern, null, EXPIRATION_MAX_TIME);
                final PostMethod post = new PostMethod(modifiedLoadUrl);
                post.setRequestHeader(new Header("Content-Type", "application/xml; charset=utf-8"));
                post.setRequestEntity(new StringRequestEntity(xml, "application/xml", "utf-8"));

                final int status = client.executeMethod(post);

                final Header contentTypeHeader = post.getResponseHeader("Content-Type");
                final String contentType = contentTypeHeader == null ? "" : contentTypeHeader.getValue();

                String model = "";
                String feedback = "FAILURE";

                if (status == 200 && isJSON(contentType)) {

                    JSONObject json = new JSONObject(post.getResponseBodyAsString());

                    JSONObject properties = (JSONObject) json.get(JSON_MODEL_WRAPPER);

                    JSONObject esp = (JSONObject) properties.get(Constants.SKILLSPASSORT);

                    //Document Type
                    if (esp.has(JSON_DOCUMENTINFO)) {
                        JSONObject docInfo = (JSONObject) esp.get(JSON_DOCUMENTINFO);
                        if (docInfo.has(JSON_DOCUMENT_TYPE)) {
                            docType = docInfo.getString(JSON_DOCUMENT_TYPE);
                        }
                    }

                    model = StringEscapeUtils.escapeJavaScript("{ \"SkillsPassport\" : " + esp + " }");
                    feedback = "SUCCESS";
                } //Not OK status with JSON response
                //So something must be wrong
                else if (isHTML(contentType)) {
                    String responseBody = post.getResponseBodyAsString();
                    feedback = StringEscapeUtils.escapeJavaScript(responseBody);
                }
                //Set the Session Attributes
                HttpSession session = request.getSession();
                session.setAttribute(SESSION_POSTED_JSON_NAME, model);
                session.setAttribute(SESSION_POSTED_FEEDBACK_NAME, feedback);

                //Decide on the locale
                Locale locale = defaultPage.getLocale();
                Header contentLanguage = post.getResponseHeader("Content-Language");
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

                String pageKeyPath = REMOTE_UPLOAD_REDIRECT_URL.replace(REMOTE_UPLOAD_DOCUMENT, finalDocType);
                /*     EWA-1755 REMOVE IE9 */
                // in case of IE9 we construct the default url and append the remote-upload navigation path after the hash
//				String thisAgent = request.getHeader("User-Agent");
//				if ( thisAgent != null && !"".equals(thisAgent)){
//					UserAgent agent = UserAgent.match( thisAgent );
//				
//					String browserDescription = agent.getDescription(); 
//					if(browserDescription != null ){
//						session.setAttribute(EditorsModule.EWA_LOCALE_BROWSER_DESCRIPTION, browserDescription);
//						if(browserDescription.equals(UserAgent.MSIE9.getDescription()))
//							pageKeyPath = defaultPage.getPath()+"#"+pageKeyPath.substring(1);
//					}
//				}

                final Map<String, String> urlskeysMap = getInteroperabilityPartnersList(session.getServletContext());

                // Get keys, urls, names parameters
                final String postedKey = request.getParameter(POSTED_KEY_NAME);
                final String postedUrl = request.getParameter(POSTED_URL_NAME);
                final String postedName = request.getParameter(POSTED_NAME_VALUE);
                final String postedLocale = request.getParameter(POSTED_NAME_LOCALE);

                if (!Strings.isNullOrEmpty(postedKey) && !Strings.isNullOrEmpty(postedUrl)) {
                    if (urlskeysMap.containsKey(postedKey)) {
                        final String value = urlskeysMap.get(postedKey);
                        if (!Strings.isNullOrEmpty(value) && postedUrl.startsWith(value)) {
                            session.setAttribute(CONTEXT_INTEROP_PARTNER_KEY, postedKey);
                            session.setAttribute(CONTEXT_INTEROP_PARTNER_URL, postedUrl);
                            session.setAttribute(CONTEXT_INTEROP_PARTNER_NAME, postedName);
                            session.setAttribute(CONTEXT_INTEROP_PARTNER_LOCALE, INVALID_POSTED_LOCALE);

                            if (postedLocale != null && supportedLocales.contains(new Locale(postedLocale))) {
                                session.setAttribute(CONTEXT_INTEROP_PARTNER_LOCALE, postedLocale);
                                locale = new Locale(postedLocale);
                            }
                        }
                    }
                }
                //New pageKey based on the remote upload path or the remote-download proxy
                final PageKey redirectPage = new PageKey(locale, pageKeyPath);
                response.sendRedirect(keyFormat.format(redirectPage));
            }

        } catch (final Exception e) {
            LOG.error("Failed to complete remote upload", e);
        }
    }

    private static Map<String, String> getInteroperabilityPartnersList(ServletContext context) {

        Map<String, String> urlskeysMap = new HashMap<String, String>();

        String urlKeyPair;
        int i = 1;
        while ((urlKeyPair = (String) context.getAttribute(CONTEXT_INTEROP_PARTNER_PAIR_PROPERTY_PREFIX + (i++))) != null) {

            String[] pairArray = urlKeyPair.split(",");
            urlskeysMap.put(pairArray[0], pairArray[1]);
        }

        return urlskeysMap;
    }

    public static String getValidDocType(String docType) {
        return docType.toLowerCase().replace("_", "-").replaceAll("e(cv|lp|cl)", "$1");
    }

    private static boolean isJSON(String contentType) {
        return (StringUtils.isNotEmpty(contentType) && contentType.indexOf("json") >= 0);
    }

    private static boolean isHTML(String contentType) {
        return (StringUtils.isNotEmpty(contentType) && contentType.indexOf("html") >= 0);
    }
}
