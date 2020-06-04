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
import europass.ewa.page.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.cglib.core.Local;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.EnumSet;
import java.util.Locale;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MainPageFilterTest {

    @Mock
    private PageKeyFormat keyFormat;

    @Mock
    private PageKey pageKey;

    @Mock
    private NotificationBundleLoader notificationBundleLoader;

    @Mock
    private EnumSet<UserAgent> nonHtml5Agents;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private HttpSession session;

    @Mock
    private RedirectPathUrlRules pathUrlRules;

    @InjectMocks
    private MainPageFilter filter;

    private static final String URL_MODIFIED_ALREADY = "urlmodifiedonce";
    private static final String CLIENT_REQUESTED_RELOAD = "CLIENT_REQUESTED_RELOAD";

    @Before
    public void setUp() throws Exception {

        when(request.getSession()).thenReturn(session);
        when(keyFormat.parse(request)).thenReturn(pageKey);
        when(pageKey.getUserAgent()).thenReturn(UserAgent.CHROME);
        when(pageKey.getLocale()).thenReturn(new Locale("en"));
        when(notificationBundleLoader.getMessage(NotificationBundleLoader.NOT_LOADING_KEY, pageKey.getLocale())).thenReturn("message");

        when(session.getAttribute(EditorsModule.EWA_MODIFIED_URL_VARIABLE)).thenReturn(null);
        when(session.getAttribute(URL_MODIFIED_ALREADY)).thenReturn(null);
        when(session.getAttribute(CLIENT_REQUESTED_RELOAD)).thenReturn(null);
    }

    @Test
    public void testRedirectWhenUrlContains_CV_ESP_UPLOAD_PATH() throws Exception {
        final String SITE_CONTEXT = "/editors";
        final String SITE_PATH = "/en/cv-esp/upload";
        final String URL = "/editors/en/cv/upload";

        when(keyFormat.getSiteContext()).thenReturn(SITE_CONTEXT);
        when(keyFormat.getSitePath(request)).thenReturn(SITE_PATH);

        when(keyFormat.format(pageKey)).thenReturn(SITE_CONTEXT + SITE_PATH);

        when(pathUrlRules.shouldRedirectPath(pageKey)).thenReturn(true);
        when(pathUrlRules.getRedirectUrlWhenCustomRuleApplied(pageKey)).thenReturn(URL);

        filter.doFilter(request, response, filterChain);

        verify(session).setAttribute(EditorsModule.EWA_MODIFIED_URL_VARIABLE, "true");
        verify(response).sendRedirect(URL);
    }

    @Test
    public void testRedirectWhenUrlIsMissingLocale() throws Exception {
        final String SITE_CONTEXT = "/editors";
        final String SITE_PATH = "/lp/compose";
        final Locale locale = new Locale("en");

        when(keyFormat.getSiteContext()).thenReturn(SITE_CONTEXT);
        when(keyFormat.getSitePath(request)).thenReturn(SITE_PATH);

        when(keyFormat.format(pageKey)).thenReturn(SITE_CONTEXT + "/en" + SITE_PATH);

        when(pathUrlRules.getRedirectUrlOnDefaultLocale(request, SITE_CONTEXT + SITE_PATH, locale)).thenReturn(SITE_CONTEXT + "/en" + SITE_PATH);

        filter.doFilter(request, response, filterChain);

        verify(session).setAttribute(EditorsModule.EWA_MODIFIED_URL_VARIABLE, "true");
        verify(response).sendRedirect("/editors/en/lp/compose");
    }
}
