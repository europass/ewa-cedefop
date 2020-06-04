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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.inject.Singleton;

import com.google.inject.Inject;

import europass.ewa.modules.Default;

@Singleton
public class NotificationBundleLoader {

    private final Locale defaultLocale;

    private static Map<Locale, ResourceBundle> bundleMap = null;

    private static final String BUNDLE_NAME = "Notification";

    public static final String NOT_LOADING_KEY = "initial.loading.error.msg";

    public static final String NOT_LOADING_DEFAULT_MESSAGE = "Loading the Europass Editor seems to take too long.You may want to refresh the page or try again later.";

    @Inject
    public NotificationBundleLoader(@Default Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    /**
     * Loads a resource bundle for the requested locale, or the default locale.
     *
     * @param requestedLocale
     * @return
     */
    private synchronized ResourceBundle load(Locale requestedLocale) {
        Locale locale = requestedLocale == null ? defaultLocale : requestedLocale;

        ResourceBundle bundle = null;

        if (bundleMap == null) {
            bundleMap = new HashMap<Locale, ResourceBundle>();
        }
        if (bundleMap.containsKey(locale)) {
            bundle = bundleMap.get(locale);

        } else {
            bundle = ResourceBundle.getBundle("bundles/" + BUNDLE_NAME, locale, new SimpleJsonResourceBundle.Control());
            bundleMap.put(locale, bundle);
        }
        return bundle;
    }

    public String getMessage(String key) {
        return this.getMessage(key, null);
    }

    public String getMessage(String key, Locale locale) {

        ResourceBundle bundle = load(locale);

        try {
            return bundle.getString(key);
        } catch (final Exception e) {
            return NOT_LOADING_DEFAULT_MESSAGE;
        }
    }
}
