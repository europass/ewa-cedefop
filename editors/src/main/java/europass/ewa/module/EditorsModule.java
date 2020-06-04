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
package europass.ewa.module;

import com.google.inject.Provides;
import com.google.inject.name.Names;
import com.google.inject.servlet.ServletModule;
import europass.ewa.CachedUserAgentStringParser;
import europass.ewa.enums.UserAgent;
import europass.ewa.filters.*;
import europass.ewa.modules.Default;
import europass.ewa.page.*;
import europass.ewa.servlets.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.ServletContext;
import java.util.EnumSet;
import java.util.Locale;

public class EditorsModule extends ServletModule {

    private static final Logger LOG = LoggerFactory.getLogger(EditorsModule.class);

    public static final String EWA_LOCALE_SESSION_VARIABLE = "locale";

    public static final String EWA_LOCALE_OS_VARIABLE = "operatingSystem";

    public static final String EWA_LOCALE_BROWSER_VARIABLE = "browserName";

    public static final String EWA_LOCALE_BROWSER_DESCRIPTION = "browserDescription";

    public static final String EWA_MODIFIED_URL_VARIABLE = "EUROPASS_EWA_URL_MODIFIED";

    public static final String EWA_DEFAULT_CONTEXT = "context.ewa.editors.default";

    public static final String EWA_PATH_TEMPLATE = "ewa.editors.path.template";

    public static final String EWA_DEFAULT_PATH = "ewa.editors.default.path";

    public static final String EWA_UNSUPPORTED_UAS = "ewa.editors.unsupported.browsers.list";

    public static final String EWA_UNSUPPORTED_UAS_KEY = "ewa.editors.unsupported.browsers";

    public static final String EWA_NON_HTML5_UAS = "ewa.editors.non.html5.browsers.list";

    public static final String EWA_NON_HTML5_UAS_KEY = "ewa.editors.non.html5.browsers";

    public static final String EWA_URL_ROOT_REGEXP = "/";

    private static final String EWA_REMOTE_UPLOAD = "remote-upload";

    private static final String EWA_CLOUD_SHARE = "share";
    private static final String EWA_CLOUD_SHARE_REVIEW = "shareForReview";
    private static final String EWA_CLOUD_SHARE_POSTBACK = "shareReviewPostback";

    public static final String EWA_CLOUD_UPLOAD_REVIEW = "upload-share-review";
    public static final String EWA_CLOUD_UPLOAD_REVIEW_URL = "/" + EWA_CLOUD_UPLOAD_REVIEW;

    public static final String EWA_CLOUD_LOAD = "cloud-load";

    public static final String EWA_URL_ANY_PATH_REGEXP = "/(?!(" + EWA_REMOTE_UPLOAD + "|"
            + EWA_CLOUD_SHARE_REVIEW + "|" + EWA_CLOUD_UPLOAD_REVIEW + "|" + EWA_CLOUD_SHARE_POSTBACK + "|"
            + EWA_CLOUD_SHARE + ".*)|(" + EWA_CLOUD_LOAD + ")|(.*scripts)|(.*localization)|(.*libraries)|(.*images)|(.*styles)|(.*css)|(.*util)|(.*fonts)).*";

    public static final String EWA_REMOTE_UPLOAD_URL = "/" + EWA_REMOTE_UPLOAD;

    // We may need to change regex after changes on https://jira.eworx.gr/browse/EPAS-1124
    public static final String EWA_CLOUD_SHARE_REVIEW_REGEX = "\\/" + EWA_CLOUD_SHARE_REVIEW + "\\/[a-zA-Z]{2}(?:-[a-zA-Z]{3})?\\/(googledrive|dropbox|onedrive).*";
    public static final String EWA_CLOUD_SHARE_POSTBACK_REGEX = "\\/" + EWA_CLOUD_SHARE_POSTBACK + "\\/[a-zA-Z]{2}(?:-[a-zA-Z]{3})?\\/(googledrive|dropbox|onedrive).*";

    public static final String EWA_CLOUD_SHARE_UPLOAD_REGEX = "\\/" + EWA_CLOUD_SHARE + "\\/[a-z]{2}\\/(googledrive|dropbox|onedrive).*";

    public static final String SYSTEM_PATH_FOR_GUILABELS_DIR = "/jsonFiles/";

    public static final String USER_AGENT_CACHE = "europass-userAgent-cache";

    public static final String REDIRECT_URLS_MISSING_PATH_LOCALE = "ewa.editors.rules.missing.locale.url.redirect";
    public static final String REDIRECT_URLS_CUSTOM_PATH = "ewa.editors.rules.custom.path.redirect";

    private CachedUserAgentStringParser userAgentCache = null;

    private final ServletContext ctx;

    public EditorsModule(ServletContext ctx) {
        super();
        this.ctx = ctx;
    }

    @Provides
    @Singleton
    @Named(USER_AGENT_CACHE)
    public CachedUserAgentStringParser userAgentParser() {
        if (userAgentCache == null) {
            userAgentCache = new CachedUserAgentStringParser();
        }
        return userAgentCache;
    }

    @Override
    protected void configureServlets() {

        bind(PageKeyFormat.class).to(EWAPageKeyFormat.class);

        bind(NotificationBundleLoader.class).asEagerSingleton();
        bind(GuiLabelBundleLoader.class).asEagerSingleton();

        bind(String.class).annotatedWith(Names.named(SYSTEM_PATH_FOR_GUILABELS_DIR)).toInstance("/jsonFiles/");

        //Remote Upload
        serve(EWA_REMOTE_UPLOAD_URL).with(RemoteUploadServlet.class);

        serve(EWA_CLOUD_UPLOAD_REVIEW_URL).with(CloudShareReviewUploadServlet.class);
        serveRegex(EWA_CLOUD_SHARE_REVIEW_REGEX).with(CloudShareReviewServlet.class);
        serveRegex(EWA_CLOUD_SHARE_POSTBACK_REGEX).with(CloudSharePostbackServlet.class);

        serveRegex(EWA_CLOUD_SHARE_UPLOAD_REGEX).with(CloudShareLoadServlet.class);

        // ORDER IS IMPORTANT !!!
        // 1. Will skip filter chain for pages given
        filter("/*").through(SkipFilesFilter.class);
        // 2.1 Will redirect the url to the socialRedirect.jsp
        filter("/*").through(LinkedInCallbackFilter.class);
        // 3. Will try to add a locale based on the browser's specified locales.
        filterRegex(EWA_URL_ROOT_REGEXP).through(LocaleFilter.class);
        // 4. Will try to translate the path to a valid url according to the EWA template
        filterRegex(EWA_URL_ANY_PATH_REGEXP).through(MainPageFilter.class);
        // 5. Will redirect to the unsupported browser jsp, if this is the case...
        filterRegex(EWA_URL_ANY_PATH_REGEXP).through(BrowserFilter.class);
        // 6. Will redirect the url to the editor.jsp
        filterRegex(EWA_URL_ANY_PATH_REGEXP).through(EditorsPageFilter.class);

    }

    //----------- PROVIDERS -------------
    @Provides
    @Singleton
    @Default
    public PageKey defaultPage(@Default Locale locale,
            @Named(EWA_DEFAULT_PATH) String path) {
        return new PageKey(locale, path);
    }

    @Provides
    @Singleton
    @ContextPath
    public String contextPath(@Named(EWA_DEFAULT_CONTEXT) String defaultContext) {
        if (ctx == null) {
            return defaultContext.startsWith("/") ? defaultContext : "/" + defaultContext;
        } else {
            return ctx.getContextPath();
        }

    }

    /**
     * Non-HTML5 User Agents 
	*
     */
    @Provides
    @Singleton
    @Named(EWA_NON_HTML5_UAS)
    EnumSet<UserAgent> nonHtml5Agents(@Named(EWA_NON_HTML5_UAS_KEY) String param) {

        EnumSet<UserAgent> nonHtml5AgentsSet = EnumSet.noneOf(UserAgent.class);//empty set

        if (param == null || param.isEmpty()) {
            return nonHtml5AgentsSet;//return no agents
        }
        String[] names = param.split(" ");
        if (names.length == 0) {
            return nonHtml5AgentsSet;
        }

        for (String name : names) {
            name = name.trim();
            try {
                UserAgent ua = UserAgent.fromValue(name);
                if (!UserAgent.MODERN.equals(ua) && !UserAgent.UNKNOWN.equals(ua)) {
                    nonHtml5AgentsSet.add(ua);
                }

            } catch (IllegalArgumentException iae) {
                LOG.warn("Invalid user agent name in {}:{}", EWA_NON_HTML5_UAS_KEY, name);
            }
        }

        //NO PERFORMANCE BENEFITS: Set<UserAgent> test = new HashSet<UserAgent>(EnumSet.allOf(UserAgent.class));
        return nonHtml5AgentsSet;
    }

    /**
     * Unsupported User Agents reads the context parameter and creates a EnumSet
     * with the unsupported browsers
	*
     */
    @Provides
    @Singleton
    @Named(EWA_UNSUPPORTED_UAS)
    EnumSet<UserAgent> unsupportedAgents(@Named(EWA_UNSUPPORTED_UAS_KEY) String param) {

        EnumSet<UserAgent> unsupportedAgentsSet = EnumSet.noneOf(UserAgent.class);//empty set

        if (param == null || param.isEmpty()) {
            return unsupportedAgentsSet;//return no agents
        }
        String[] names = param.split(" ");
        if (names.length == 0) {
            return unsupportedAgentsSet;
        }

        for (String name : names) {
            name = name.trim();
            try {
                UserAgent ua = UserAgent.fromValue(name);
                if (!UserAgent.MODERN.equals(ua) && !UserAgent.UNKNOWN.equals(ua)) {
                    unsupportedAgentsSet.add(ua);
                }

            } catch (IllegalArgumentException iae) {
                LOG.warn("Invalid user agent name in {}:{}", EWA_UNSUPPORTED_UAS_KEY, name);
            }
        }

        //NO PERFORMANCE BENEFITS: Set<UserAgent> test = new HashSet<UserAgent>(EnumSet.allOf(UserAgent.class));
        return unsupportedAgentsSet;
    }

    @Provides
    @Singleton
    @Default
    RedirectPathUrlRules pathUrlRules(final PageKeyFormat pageKeyFormat, final @Default PageKey defaultPage,
            @Named(REDIRECT_URLS_MISSING_PATH_LOCALE) String paramNoLocale,
            @Named(REDIRECT_URLS_CUSTOM_PATH) String paramCustomPath) {
        return new RedirectPathUrlRules(pageKeyFormat, defaultPage, paramNoLocale, paramCustomPath);
    }
}
