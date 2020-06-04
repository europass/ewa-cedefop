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

import europass.ewa.modules.Default;
import europass.ewa.page.PageKey;
import europass.ewa.page.PageKeyFormat;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

@Singleton
public class CloudSharePostbackServlet extends CommonHttpServlet {

    private final PageKeyFormat keyFormat;
    private final PageKey defaultPage;

    private static final Logger LOG = LoggerFactory.getLogger(CloudSharePostbackServlet.class);

    @Inject
    public CloudSharePostbackServlet(final PageKeyFormat keyFormat,
            @Default PageKey defaultPage) {

        this.keyFormat = keyFormat;
        this.defaultPage = defaultPage;
    }

    @Override
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) {
        this.load(request, response);
    }

    @Override
    public void doPost(final HttpServletRequest request, final HttpServletResponse response) {
        this.load(request, response);
    }

    private void load(final HttpServletRequest request, final HttpServletResponse response) {

        createOrUpdateSessionCookie(request, response, "share-review-postback", "enabled");

        redirectAfterResponse(request, response);
    }

    private void redirectAfterResponse(final HttpServletRequest request, final HttpServletResponse response) {

        //Decide on the locale
        Locale locale = defaultPage.getLocale();
        final String language = request.getParameter("language");
        if (StringUtils.isNotEmpty(language)) {
            locale = new Locale(language);
        }

        try {
            final PageKey redirectPage = new PageKey(defaultPage.getLocale(), defaultPage.getPath());
            redirectPage.setLocale(locale);
            response.sendRedirect(keyFormat.format(redirectPage));
        } catch (final IOException e) {
            LOG.error("Cannot redirect after posting back reviewed email.. ", e);
        }
    }
}
