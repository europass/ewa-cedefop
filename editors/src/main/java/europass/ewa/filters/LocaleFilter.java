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

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import europass.ewa.locales.LocaleDetector;
import europass.ewa.modules.Default;
import europass.ewa.page.PageKey;
import europass.ewa.page.PageKeyFormat;

@Singleton
public class LocaleFilter implements Filter {

    final PageKeyFormat keyFormat;

    final PageKey defaultPage;

    final LocaleDetector languageDetector;

    @Inject
    public LocaleFilter(
            PageKeyFormat keyFormat,
            @Default PageKey defaultPage,
            LocaleDetector languageDetector) {
        this.keyFormat = keyFormat;
        this.defaultPage = defaultPage;
        this.languageDetector = languageDetector;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
            FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //New pageKey based on the default
        PageKey redirectPage = new PageKey(defaultPage.getLocale(), defaultPage.getPath());
        //Detect the locale
        redirectPage.setLocale(languageDetector.detectLocale(request));
        //Redirect
        response.sendRedirect(keyFormat.format(redirectPage));
    }

    @Override
    public void destroy() {
    }

}
