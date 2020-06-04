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
package europass.ewa.modules;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

import javax.inject.Named;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import europass.ewa.locales.LocaleParser;

public class SupportedLocaleModule extends AbstractModule {

    private static final Logger LOG = LoggerFactory.getLogger(SupportedLocaleModule.class);

    public static final String EWA_SUPPORTED_LANGUAGES_KEY = "ewa.supported.languages";

    public static final String EWA_SUPPORTED_LANGUAGES = "ewa.supported.languages.list";

    public static final String EWA_DEFAULT_LANGUAGE_KEY = "ewa.default.locale";

    @Provides
    @Singleton
    @Default
    public Locale defaultLocale(@Named(EWA_DEFAULT_LANGUAGE_KEY) String localeStr) {
        return LocaleParser.parse(localeStr);
    }

    @Provides
    @Singleton
    @Named(EWA_SUPPORTED_LANGUAGES)
    Set<Locale> supportedLocales(@Named(EWA_SUPPORTED_LANGUAGES_KEY) String param, @Default Locale defaultLocale) {
        Set<Locale> locales = new LinkedHashSet<Locale>();
        if (param == null || param.isEmpty()) {
            locales.add(defaultLocale);
            return locales;
        }
        String[] names = param.split(" ");
        if (names.length == 0) {
            locales.add(defaultLocale);
            return locales;
        }

        for (String name : names) {
            name = name.trim();
            try {
                locales.add(new Locale(name));
            } catch (IllegalArgumentException iae) {
                LOG.warn("Invalid locale name in {}:{}", EWA_SUPPORTED_LANGUAGES_KEY, name);
            }
        }
        return locales;
    }

    @Override
    protected void configure() {
    }
}
