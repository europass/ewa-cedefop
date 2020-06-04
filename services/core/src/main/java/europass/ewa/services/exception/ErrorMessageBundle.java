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
package europass.ewa.services.exception;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.inject.Singleton;

import europass.ewa.resources.JsonResourceBundle;

@Singleton
public class ErrorMessageBundle {

    private static ResourceBundle bundle = null;

    private static final String DEFAULT_ERROR_MESSAGE = "Internal Server Error.";

    public static ResourceBundle getBundle() {
        return bundle;
    }

    public static String get(String key) {
        return get(key, DEFAULT_ERROR_MESSAGE, Locale.ENGLISH);
    }

    public static String get(String key, String defaultMessage) {
        return get(key, defaultMessage, Locale.ENGLISH);
    }

    public static String get(String key, String defaultMessage, Locale locale) {
        if (bundle == null) {

            try {
                bundle = ResourceBundle.getBundle("bundles/NotificationRest", new JsonResourceBundle.Control());
            } catch (Exception e) {
                return DEFAULT_ERROR_MESSAGE;
            }
        }
        if (bundle.containsKey(key)) {
            return bundle.getString(key);
        } else {
            if (defaultMessage != null) {
                return defaultMessage;
            } else {
                return DEFAULT_ERROR_MESSAGE;
            }
        }

    }
}
