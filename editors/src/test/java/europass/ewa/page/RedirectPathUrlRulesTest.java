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

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RedirectPathUrlRulesTest {

    @Mock
    private PageKeyFormat keyFormat;

    @Mock
    private PageKey defaultPage;

    Locale locale = new Locale("en");

    @Mock
    private HttpServletRequest request;

    @InjectMocks
    private RedirectPathUrlRules urlRules;

    @Test
    public void shouldRedirectWhenMissingLocale() throws Exception {
        urlRules = new RedirectPathUrlRules(keyFormat, defaultPage, "/editors/cv-esp/upload,/editors/esp/compose,/editors/lp/compose,/editors/lp/upload", null);
        urlRules = spy(urlRules);

        final PageKey pageKey = mock(PageKey.class);
        when(keyFormat.parseSpecificRequest(request)).thenReturn(pageKey);
        when(keyFormat.format(pageKey)).thenReturn("/editors/en/lp/compose");

        final String url = urlRules.getRedirectUrlOnDefaultLocale(request, "/editors/lp/compose", locale);
        assertEquals("/editors/en/lp/compose", url);
    }

    @Test
    public void shouldRedirectWhenMissingLocaleAndHasCvEspUploadPath() throws Exception {
        urlRules = new RedirectPathUrlRules(keyFormat, defaultPage, "/editors/cv-esp/upload,/editors/esp/compose,/editors/lp/compose,/editors/lp/upload", null);
        urlRules = spy(urlRules);

        final PageKey pageKey = mock(PageKey.class);
        when(keyFormat.parseSpecificRequest(request)).thenReturn(pageKey);
        when(pageKey.getPath()).thenReturn("/cv-esp/upload");
        when(keyFormat.format(pageKey)).thenReturn("/editors/en/cv/upload");

        final String url = urlRules.getRedirectUrlOnDefaultLocale(request, "/editors/cv-esp/upload", locale);
        verify(pageKey).setPath("/cv/upload");
        assertEquals("/editors/en/cv/upload", url);
    }

    @Test
    public void shouldRedirectToDefaultPathUrlWhenMissingLocale() throws Exception {
        urlRules = new RedirectPathUrlRules(keyFormat, defaultPage, "/editors/cv-esp/upload", null);
        urlRules = spy(urlRules);

        final PageKey pageKey = mock(PageKey.class);
        when(keyFormat.parseSpecificRequest(request)).thenReturn(pageKey);
        when(keyFormat.format(any(PageKey.class))).thenReturn("/editors/en/cv/compose");

        final String url = urlRules.getRedirectUrlOnDefaultLocale(request, "/editors/lp/compose", locale);
        assertEquals("/editors/en/cv/compose", url);
    }

    @Test
    public void shouldRedirectWhenCustomRuleApplied() throws Exception {
        urlRules = new RedirectPathUrlRules(keyFormat, defaultPage, null, "/cv-esp/upload /cv/upload,/cv-esp/download /cv/download");
        urlRules = spy(urlRules);

        final PageKey pageKey = mock(PageKey.class);
        when(pageKey.getPath()).thenReturn("/cv-esp/download");
        when(keyFormat.format(pageKey)).thenReturn("/editors/en/cv/download");

        final String url = urlRules.getRedirectUrlWhenCustomRuleApplied(pageKey);
        verify(pageKey).setPath("/cv/download");
        assertEquals("/editors/en/cv/download", url);
    }

}
