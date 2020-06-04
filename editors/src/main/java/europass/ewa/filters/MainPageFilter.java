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
package europass.ewa.filters;

import europass.ewa.enums.UserAgent;
import europass.ewa.module.EditorsModule;
import europass.ewa.modules.Default;
import europass.ewa.page.NotificationBundleLoader;
import europass.ewa.page.PageKey;
import europass.ewa.page.PageKeyFormat;
import europass.ewa.page.RedirectPathUrlRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Locale;

@Singleton
public class MainPageFilter implements Filter {

    private static final String URL_MODIFIED_ALREADY = "urlmodifiedonce";
    private static final String SUPPORTS_HTML5 = "SUPPORTS_HTML5";
    private static final String CLIENT_REQUESTED_RELOAD = "CLIENT_REQUESTED_RELOAD";

    private static final Logger LOG = LoggerFactory.getLogger(MainPageFilter.class);

    final PageKeyFormat keyFormat;
    final PageKey defaultPage;
    final EnumSet<UserAgent> nonHtml5Agents;
    final NotificationBundleLoader notificationBundle;
    final RedirectPathUrlRules pathUrlRules;

    @Inject
    public MainPageFilter(final PageKeyFormat keyFormat,
            final @Default PageKey defaultPage,
            final @Named(EditorsModule.EWA_NON_HTML5_UAS) EnumSet<UserAgent> nonHtml5Agents,
            final @Default RedirectPathUrlRules pathUrlRules,
            final NotificationBundleLoader notificationBundle
    ) {
        this.keyFormat = keyFormat;
        this.defaultPage = defaultPage;
        this.nonHtml5Agents = nonHtml5Agents;
        this.notificationBundle = notificationBundle;
        this.pathUrlRules = pathUrlRules;
    }

    @Override
    public void init(final FilterConfig config) throws ServletException {
        config.getServletContext().setAttribute(PageKeyFormat.class.getName(), keyFormat);
    }

    @Override
    public void destroy() {
    }

    private static boolean isTrue(final String str) {
        return (str != null && !str.isEmpty() && Boolean.parseBoolean(str) == true);
    }

    private boolean supportsHtml5(final PageKey pageKey) {
        return !(nonHtml5Agents.size() > 0 && nonHtml5Agents.contains(pageKey.getUserAgent()));
    }

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse,
            final FilterChain chain) throws IOException, ServletException {

        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;

        final HttpSession session = request.getSession();

        // If the request includes the modified url session attribute do not handle this!
        final String urlModified = (String) session.getAttribute(EditorsModule.EWA_MODIFIED_URL_VARIABLE);
        final String urlModifiedAlready = (String) session.getAttribute(URL_MODIFIED_ALREADY);
        final String browserRequestedReload = (String) session.getAttribute(CLIENT_REQUESTED_RELOAD);

        // If URL is modified, which happens when we change the URL and call redirect...
        if (isTrue(urlModified)) {
            session.removeAttribute(EditorsModule.EWA_MODIFIED_URL_VARIABLE);
            session.setAttribute(URL_MODIFIED_ALREADY, "true");
            chain.doFilter(request, response);
        } //This happens when the browser reloads the page (work-around for IE and HTML 5 History)
        //and the filter has already once run, changed the URL and redirected 
        else if (isTrue(browserRequestedReload) && isTrue(urlModifiedAlready)) {
            session.removeAttribute(URL_MODIFIED_ALREADY);
            session.removeAttribute(CLIENT_REQUESTED_RELOAD);
            chain.doFilter(request, response);
        } else { //URL is NOT modified
            final PageKey pageKey = keyFormat.parse(request);
            if (pageKey == null) {
                LOG.info("PageKey parsing failed. Will use the default PageKey.");
                response.sendRedirect(keyFormat.format(new PageKey(defaultPage.getLocale(), defaultPage.getPath())));
            }

            setSessionAttributes(session, pageKey);

            // if the url that comes from the parsing to PageKey is different from the original, Page Key Formatter has changed something.
            // E.g. the locale if not acceptable, or the path if empty, so we need to ignore the previous url and redirect to the new one.
            String originalUrl = keyFormat.getSiteContext() + keyFormat.getSitePath(request);
            String url = keyFormat.format(pageKey);

            if (originalUrl.equalsIgnoreCase(url)) {
                if (pathUrlRules.shouldRedirectPath(pageKey) || !originalUrl.equals(url)) {
                    url = pathUrlRules.getRedirectUrlWhenCustomRuleApplied(pageKey);
                    doRedirectAndSetSession(session, response, url, pageKey.getLocale());
                } else {
                    chain.doFilter(request, response);
                }
            } else { // the PageKey is changed from the original...
                url = pathUrlRules.getRedirectUrlOnDefaultLocale(request, originalUrl, pageKey.getLocale());
                doRedirectAndSetSession(session, response, url, pageKey.getLocale());
            }
        }
    }

    private void setSessionAttributes(final HttpSession session, final PageKey pageKey) {

        //Set info on whether the UserAgent supports HTML5 - History
        session.setAttribute(SUPPORTS_HTML5, String.valueOf(this.supportsHtml5(pageKey)));
        session.setAttribute(EditorsModule.EWA_LOCALE_SESSION_VARIABLE, pageKey.getLocale().toString());
        session.setAttribute(NotificationBundleLoader.NOT_LOADING_KEY,
                notificationBundle.getMessage(NotificationBundleLoader.NOT_LOADING_KEY, pageKey.getLocale()));
        session.setAttribute(EditorsModule.EWA_LOCALE_OS_VARIABLE, pageKey.getUserAgent().name());
    }

    protected void doRedirectAndSetSession(final HttpSession session, final HttpServletResponse response,
            final String url, final Locale locale)
            throws IOException {
        // Change the URL of the browser to the PageKey URL and do a redirect
        session.setAttribute(EditorsModule.EWA_MODIFIED_URL_VARIABLE, "true");
        session.setAttribute(EditorsModule.EWA_LOCALE_SESSION_VARIABLE, locale.toString());
        response.sendRedirect(url);
    }
}
