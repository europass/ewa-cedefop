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
package europass.ewa.locales;

import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.servlet.http.HttpServletRequest;

import europass.ewa.modules.Default;
import europass.ewa.modules.SupportedLocaleModule;

@Singleton
public class LocaleDetector {

    private final Set<Locale> supportedLocales;

    private final Locale defaultLocale;

    @Inject
    public LocaleDetector(@Named(SupportedLocaleModule.EWA_SUPPORTED_LANGUAGES) Set<Locale> supportedLocales,
            @Default Locale defaultLocale) {
        this.supportedLocales = supportedLocales;
        this.defaultLocale = defaultLocale;
    }

    public Locale detectLocale(HttpServletRequest request) {
        // Iterate the Locales found in the Request for one that matched the
        // supported Locales
        for (Locale requestedLocale : getRequestedLocales(request)) {
            Locale match = LocaleParser.checkSupportedLocale(requestedLocale, supportedLocales);
            if (match != null) {
                return match;
            }
        }
        return defaultLocale;
    }

    public boolean isSupported(Locale requestedLocale) {
        return (LocaleParser.checkSupportedLocale(requestedLocale, supportedLocales) != null);
    }

    /**
     * Return the first supported locale from a list of Locales. Return a Locale
     * with asterisk as language when such a locale is set (which means any
     * locale). Returns null if none matches
     *
     * @param requestedLocales
     * @return
     */
    public Locale getSupported(List<Locale> requestedLocales) {
        if (requestedLocales.size() == 1
                && requestedLocales.get(0) != null
                && isAnyLocale(requestedLocales.get(0))) {
            return requestedLocales.get(0);
        }
        for (Locale requestedLocale : requestedLocales) {
            Locale match = LocaleParser.checkSupportedLocale(requestedLocale, supportedLocales);
            if (match != null) {
                return match;
            }
        }
        return null;
    }

    public boolean isAnyLocale(Locale locale) {
        return (locale != null && "*".equals(locale.getLanguage()));
    }

    private Iterable<Locale> getRequestedLocales(HttpServletRequest request) {
        // Check Accept-Language Header
        if (request.getHeader("Accept-Language") != null) {
            // f the client request doesn't provide an Accept-Language header,
            // this method returns an Enumeration containing one Locale, the
            // default locale for the server.
            Enumeration<Locale> locales = request.getLocales();
            return Collections.list(locales);
        }
        return Collections.emptyList();
    }

}
