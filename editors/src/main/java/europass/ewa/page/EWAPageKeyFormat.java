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

import com.google.common.base.Strings;
import europass.ewa.CachedUserAgentStringParser;
import europass.ewa.enums.UserAgent;
import europass.ewa.locales.LocaleParser;
import europass.ewa.module.ContextPath;
import europass.ewa.module.EditorsModule;
import europass.ewa.modules.Default;
import europass.ewa.modules.SupportedLocaleModule;
import europass.ewa.page.PathTemplateTokenizer.TokenType;
import europass.ewa.servlet.ServletUtils;
import net.sf.uadetector.OperatingSystem;
import net.sf.uadetector.OperatingSystemFamily;
import net.sf.uadetector.ReadableUserAgent;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EWAPageKeyFormat implements PageKeyFormat {

    private final String contextPath;

    private final String pathTemplate;

    private final PageKey defaultPage;

    private final Locale defaultLocale;

    private final String defaultPath;

    private final Set<Locale> supportedLocales;

    private final EnumSet<UserAgent> nonHtml5Agents;

    private final CachedUserAgentStringParser userAgentCache;

    private Pattern pathPattern;

    private int channelGroup = -1;
    private int pathGroup = -1;
    private int localeGroup = -1;

    private static final String CHANNEL = "channel";
    private static final String LOCALE = "locale";
    private static final String PATH = "path";

    //EWA-926: To support interoperability with social application we need to include the jsession id inside the URL.
    //The jsession id may start from any number. So the regexp controlling the path parts needs to be adjusted to allow the first character to be either letter or digit
    private static final String FIRST_NAME_PART = "[\\p{L}0-9][\\p{L}0-9@_\\-\\:]*";
    private static final String NEXT_NAME_PART = "[\\p{L}0-9][\\p{L}0-9@_\\-\\:]*";
    private static final String NAME_REGEXP = FIRST_NAME_PART + "(?:" + "\\." + NEXT_NAME_PART + ")*?";
    private static final String CHANNEL_REGEXP = FIRST_NAME_PART;
    //one groupId for locale: language_COUNTRY
    public static final String LOCALE_REGEXP = "[a-zA-Z]{2}(?:-[a-zA-Z]{3})?";
    private static final String HASH_REGEXP = "(?:#/)?";
    //without the leading '/'
    private static final String PATH_REGEXP = HASH_REGEXP + NAME_REGEXP + "(?:/" + NAME_REGEXP + ")*";

    @Inject
    public EWAPageKeyFormat(
            @ContextPath String contextPath,
            @Named(EditorsModule.EWA_PATH_TEMPLATE) String pathTemplate,
            @Default PageKey defaultPage,
            @Default Locale defaultLocale,
            @Named(EditorsModule.EWA_DEFAULT_PATH) String defaultPath,
            @Named(SupportedLocaleModule.EWA_SUPPORTED_LANGUAGES) Set<Locale> supportedLocales,
            @Named(EditorsModule.EWA_NON_HTML5_UAS) EnumSet<UserAgent> nonHtml5Agents,
            @Named(EditorsModule.USER_AGENT_CACHE) CachedUserAgentStringParser userAgentCache) throws ParseException {
        this.contextPath = contextPath;
        this.pathTemplate = pathTemplate;
        this.defaultPage = defaultPage;
        this.defaultLocale = defaultLocale;
        this.defaultPath = defaultPath;
        this.supportedLocales = supportedLocales;
        this.nonHtml5Agents = nonHtml5Agents;
        this.userAgentCache = userAgentCache;

        this.pathPattern = parseTemplate(pathTemplate);
    }

    /**
     * Prepares a PageKey from an Http request. Sets the user-agent and
     * parameters accordingly. Any user-agent or parameters - sensitive parsing
     * may be applied here.
     */
    @Override
    public PageKey parse(HttpServletRequest request) {

        String path = getSitePath(request);
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        final PageKey pageKey = setBasicPageKeyInfo(request);

        return addPathInfo(path, pageKey);

    }

    /**
     *
     * @param request
     * @return
     */
    @Override
    public PageKey parseSpecificRequest(HttpServletRequest request) {

        String path = getSitePath(request);

        PageKey pageKey = setBasicPageKeyInfo(request);

        pageKey.setLocale(defaultLocale);

        pageKey.setPath(path);

        return pageKey;
    }

    private PageKey setBasicPageKeyInfo(HttpServletRequest request) {
        PageKey pageKey = new PageKey();
        Map<String, String[]> params = (Map<String, String[]>) request.getParameterMap();
        pageKey.setParameters(params);

        String thisUserAgent = request.getHeader("User-Agent");

        ReadableUserAgent readableUserAgent = userAgentCache.parse(thisUserAgent);
        OperatingSystem os = readableUserAgent.getOperatingSystem();
        OperatingSystemFamily osFamily = os.getFamily();

        // pgia:EWA-1618 - Hack for UserAgent iOS/iPad String
        String osName = osFamily.getName();
        if (Strings.isNullOrEmpty(osName)) {
            if (thisUserAgent.indexOf(UserAgent.IOS.getDescription()) > -1 || thisUserAgent.indexOf(UserAgent.IPAD.getDescription()) > -1) {
                osName = UserAgent.IOS.getDescription();
            }
        }

        pageKey.setUserAgent(UserAgent.match(osName));

        if (!Strings.isNullOrEmpty(readableUserAgent.getTypeName()) && readableUserAgent.getTypeName().equals("Browser")) {
            pageKey.setBrowser(readableUserAgent.getName());
        }

        return pageKey;
    }

    /**
     * Utility used by parse from Http Request
     *
     * @param path
     * @param pgKey
     * @return
     */
    public PageKey addPathInfo(String path, PageKey pgKey) {
        PageKey pageKey = pgKey;
        if (pageKey == null) {
            pageKey = new PageKey();
        }

        Matcher pathMatcher = matchPath(path);

        if (pathMatcher.matches()) {

            parsePagePath(pageKey, pathMatcher);

            return pageKey;
        }
        //Pattern does not match!
        //Return the default pagekey
        return defaultPage;
    }

    /**
     * Prepares a PageKey based on a specific path
     */
    @Override
    public PageKey parse(String path) {
        PageKey pageKey = null;

        Matcher pathMatcher = matchPath(path);

        if (pathMatcher.matches()) {
            pageKey = new PageKey();

            parsePagePath(pageKey, pathMatcher);

            return pageKey;
        }
        //Pattern does not match!
        //Return the default pagekey
        return defaultPage;
    }

    @Override
    public String format(PageKey pageKey) {
        return format(pageKey, false);
    }

    @Override
    public String format(PageKey pageKey, boolean escapeXml) {
        StringBuilder sb = new StringBuilder();
        format(sb, pageKey, escapeXml);
        return sb.toString();
    }

    // ========== PARSE ====================================================
    /**
     * Parse the pageKey path to match channel, locale and path
     *
     * @param pageKey
     * @param pathMatcher
     */
    protected void parsePagePath(PageKey pageKey, Matcher pathMatcher) {
        //channel
        if (channelGroup > 0 && pathMatcher.group(channelGroup) != null) {
            pageKey.setChannel(pathMatcher.group(channelGroup));
        }
        //locale
        if (localeGroup > 0 && pathMatcher.group(localeGroup) != null) {
            String localeStr = pathMatcher.group(localeGroup);

            pageKey.setLocale(LocaleParser.parse(localeStr, defaultLocale, supportedLocales));
        } else {
            pageKey.setLocale(defaultLocale);
        }
        //path
        if (pathGroup > 0 && pathMatcher.group(pathGroup) != null) {
            pageKey.setPath("/" + pathMatcher.group(pathGroup));
        } else {
            pageKey.setPath(defaultPath);
        }
    }

    /**
     * Parse the path template regexp to a Pattern
     *
     * @param template
     * @return
     * @throws ParseException
     */
    protected Pattern parseTemplate(CharSequence template) throws ParseException {

        final StringBuilder regex = new StringBuilder();

        final AtomicInteger groupCount = new AtomicInteger();

        PathTemplateTokenizer tokenizer = new PathTemplateTokenizer(new PathTemplateParser() {

            private boolean inExtension = false;

            @Override
            public void parse(TokenType type, String value, int index) throws ParseException {
                switch (type) {
                    case PATH_ELEMENT:
                        regex.append('/');
                        break;
                    case EXTENSION_ELEMENT:
                        regex.append("\\.");
                        inExtension = true;
                        break;
                    case TEXT:
                        regex.append(value);
                        break;
                    case TOKEN: {
                        regex.append('(');
                        int g = groupCount.incrementAndGet();
                        if (value.equals(CHANNEL)) {
                            regex.append(inExtension ? CHANNEL_REGEXP : NAME_REGEXP);
                            channelGroup = g;
                        } else if (value.equals(LOCALE)) {
                            regex.append(LOCALE_REGEXP);
                            localeGroup = g;
                        } else if (value.equals(PATH)) {
                            regex.append(PATH_REGEXP);
                            pathGroup = g;
                        } else {
                            throw new ParseException("Unexpected token " + value + " in the path template.", index);
                        }
                        regex.append(')');

                        break;
                    }
                    case OPTIONAL_ELEMENT_BEGIN:
                        regex.append("(?:");
                        break;
                    case OPTIONAL_ELEMENT_END:
                        regex.append(")?");
                        break;
                }
            }
        });
        tokenizer.parse(template);
        return Pattern.compile(regex.toString());
    }

    /**
     * Retrieve a matcher based on the pathPattern and the path
     *
     * @param path
     * @return
     */
    protected Matcher matchPath(String path) {
        return pathPattern.matcher(path);
    }

    @Override
    public String getSiteContext() {
        return contextPath.startsWith("/") ? contextPath : "/" + contextPath;
    }

    /**
     * Extracts the site path to be parsed by the pagekeyformat
     *
     * @param request the request whose path is to be converted.
     * @return a CMS path
     */
    @Override
    public String getSitePath(HttpServletRequest request) {

        String path = null;

        if (request.getAttribute(ServletUtils.INCLUDE_REQUEST_URI_ATTR) != null) {

            path = (String) request
                    .getAttribute(ServletUtils.INCLUDE_PATH_INFO_ATTR);
            if (path == null) {
                path = (String) request
                        .getAttribute(ServletUtils.INCLUDE_SERVLET_PATH_ATTR);
            }

        } else if (request.getAttribute(ServletUtils.FORWARD_REQUEST_URI_ATTR) != null) {

            path = (String) request
                    .getAttribute(ServletUtils.FORWARD_PATH_INFO_ATTR);
            if (path == null) {
                path = (String) request
                        .getAttribute(ServletUtils.FORWARD_SERVLET_PATH_ATTR);
            }

        } else {

            path = request.getPathInfo();
            if (path == null) {
                path = request.getServletPath();
            }

        }

        return path;
    }

    // ======== FORMAT =====================================================
    protected void format(StringBuilder sb, PageKey pageKey) {
        format(sb, pageKey, false);
    }

    ;
	
	protected void format(StringBuilder sb, PageKey pageKey, boolean escapeXml) {
        boolean firstParam = true;
        sb.append(contextPath);
        try {
            formatPagePath(sb, pageKey);
        } catch (ParseException pe) {
            throw new IllegalArgumentException(pe);
        }

        formatParameters(sb, firstParam, pageKey, escapeXml);
    }

    /**
     * Formats the part of the URI that corresponds to trivahe
     * {@link PageKey#getChannel channel}, {@link PageKey#getLocale locale} and
     * {@link PageKey#getPath} components of a PageKey.
     *
     * @param sb the string builder to which the formatted output will be
     * appended.
     * @param pageKey the PageKey to be formatted.
     * @throws ParseException If the path template cannot be parsed. Normally an
     * unparsable path template should have triggered a ParseException during
     * construction, so that this exception should never be thrown.
     */
    protected void formatPagePath(final StringBuilder sb, final PageKey pageKey) throws ParseException {

        PathTemplateTokenizer tokenizer = new PathTemplateTokenizer(new PathTemplateParser() {

            private boolean inOptional = false;

            private StringBuilder option = new StringBuilder();
            private boolean writeOption = false;

            @Override
            public void parse(TokenType type, String value, int index) throws ParseException {

                switch (type) {
                    case PATH_ELEMENT: {
                        if (inOptional) {
                            option.append(value);
                        } else {
                            sb.append(value);
                        }
                        break;
                    }
                    case EXTENSION_ELEMENT: {
                        if (inOptional) {
                            option.append(value);
                        } else {
                            sb.append(value);
                        }
                        break;
                    }
                    case TEXT: {
                        if (inOptional) {
                            option.append(value);
                        } else {
                            sb.append(value);
                        }
                        break;
                    }
                    case TOKEN: {
                        if (value.equals(CHANNEL)) {
                            if (inOptional) {
                                option.append(pageKey.getChannel());
                                if (pageKey.getChannel() != null) {
                                    writeOption = true;
                                }
                            } else {
                                sb.append(pageKey.getChannel());
                            }

                        } else if (value.equals(LOCALE)) {
                            Locale locale = pageKey.getLocale();
                            if (locale == null || (locale != null && locale.getLanguage().isEmpty())) {
                                locale = defaultLocale;
                            } else {
                                locale = LocaleParser.parse(locale.toString(), defaultLocale, supportedLocales);
                            }
                            if (inOptional) {
                                writeOption = true;
                                option.append(locale);

                            } else {
                                sb.append(locale);
                            }
                        } else if (value.equals(PATH)) {
                            String path = pageKey.getPath();
                            if (path == null || path.isEmpty()) {
                                path = defaultPath;
                            }
                            if (inOptional) {
                                writeOption = true;
                                option.append(userAgentAware(path.substring(1), pageKey));

                            } else {
                                sb.append(userAgentAware(path.substring(1), pageKey));
                            }
                        } else {
                            throw new ParseException("Unexpected token " + value + " in the path template.", 0);
                        }

                        break;
                    }
                    case OPTIONAL_ELEMENT_BEGIN: {
                        inOptional = true;
                        option = new StringBuilder();
                        writeOption = false;
                        break;
                    }
                    case OPTIONAL_ELEMENT_END: {
                        inOptional = false;
                        if (writeOption) {
                            sb.append(option);
                        }
                        break;
                    }
                }
            }
        });

        tokenizer.parse(pathTemplate);
    }

    protected boolean formatParameters(StringBuilder sb, boolean firstParam, PageKey pageKey, boolean escapeXml) {
        boolean theFirstParam = firstParam;

        for (Map.Entry<String, String[]> param : pageKey.getParameters().entrySet()) {
            String name = param.getKey();
            String[] values = param.getValue();
            if (values == null || values.length == 0) {
                theFirstParam = appendParamSeparator(sb, firstParam, escapeXml);
                sb.append(name);
            } else {
                for (String value : values) {
                    theFirstParam = appendParamSeparator(sb, firstParam, escapeXml);
                    sb.append(name).append('=').append(value);
                }
            }
        }
        return theFirstParam;
    }

    protected boolean appendParamSeparator(StringBuilder sb, boolean firstParam, boolean escapeXml) {
        if (firstParam) {
            sb.append('?');
        } else {
            sb.append(escapeXml ? "&amp;" : "&");
        }
        return false;
    }

    /**
     * Adds a hash to the url
     *
     * @param path
     * @param pageKey
     * @return
     */
    protected String userAgentAware(String path, PageKey pageKey) {
        if (pageKey == null || pageKey.getUserAgent() == null || pageKey.getUserAgent().equals(UserAgent.UNKNOWN)) {
            return path;
        }

        if (path == null || (path != null && path.startsWith("#/"))) {
            return path;
        }

        String finalPath = path;

        if (nonHtml5Agents.size() > 0 && nonHtml5Agents.contains(pageKey.getUserAgent())) {
            if (path.startsWith("/")) {
                finalPath = "/" + "#/" + path.substring(1);
            } else {
                finalPath = "#/" + path;
            }
        }
        return finalPath;
    }
}
