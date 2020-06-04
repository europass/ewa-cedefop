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

import java.util.Locale;
import java.util.Set;

public final class LocaleParser {

    // Suppress default constructor for noninstantiability
    private LocaleParser() {
        throw new AssertionError();
    }

    /**
     * Parse a specific string to Locale with falling back to default Locale and
     * checking if the locale is amongst the supported locales
     *
     * @param localeString
     * @param defaultLocale
     * @param supportedLocales
     * @return
     */
    public static Locale parse(String localeString, Locale defaultLocale, Set<Locale> supportedLocales) {
        if (localeString == null) {
            return defaultLocale;
        }

        String[] parts = localeString.split("_");
        Locale locale = null;

        switch (parts.length) {
            case 0:
                return defaultLocale;
            case 1:
                locale = new Locale(parts[0]);
                break;
            case 2:
                locale = new Locale(parts[0], parts[1]);
                break;
            default:
                locale = new Locale(parts[0], parts[1], parts[2]);
                break;
        }
        Locale matchedLocale = checkSupportedLocale(locale, supportedLocales);

        if (matchedLocale == null) {
            return defaultLocale;
        }

        return matchedLocale;
    }

    /**
     * Parse a specific String to locale by falling back to a default Locale
     *
     * @param localeString
     * @param defaultLocale
     * @return
     */
    public static Locale parse(String localeString, Locale defaultLocale) {
        if (localeString == null) {
            return defaultLocale;
        }

        String[] parts = localeString.split("_");
        switch (parts.length) {
            case 0:
                return defaultLocale;
            case 1:
                return new Locale(parts[0]);
            case 2:
                return new Locale(parts[0], parts[1]);
            default:
                return new Locale(parts[0], parts[1], parts[2]);
        }

    }

    /**
     * Parse a string to a Locale by throwing an exception if the string is null
     * or empty
     *
     * @param localeString
     * @return
     * @throws IllegalArgumentException
     */
    public static Locale parse(String localeString) {
        if (localeString == null) {
            throw new IllegalArgumentException("LocaleParse:parse - Locale Input String cannot be null");
        }

        String[] parts = localeString.split("_");
        switch (parts.length) {
            case 0:
                throw new IllegalArgumentException("LocaleParse:parse - Locale Input String cannot be empty string");
            case 1:
                return new Locale(parts[0]);
            case 2:
                return new Locale(parts[0], parts[1]);
            default:
                return new Locale(parts[0], parts[1], parts[2]);
        }

    }

    /**
     * Checks whether the specific Locale is amongst the also provided list of
     * supported locales
     *
     * @param locale
     * @param supportedLocales
     * @return
     */
    public static Locale checkSupportedLocale(Locale locale, Set<Locale> supportedLocales) {
        Locale partialMatch = null;
        for (Locale supportedLocale : supportedLocales) {
            if (supportedLocale.equals(locale)) {
                return supportedLocale;
            } else if (supportedLocale.getLanguage().equals(locale.getLanguage())) {
                partialMatch = supportedLocale;
            }
        }
        return partialMatch;
    }
}
