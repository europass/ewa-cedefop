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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Inject;
import europass.ewa.modules.Default;
import europass.ewa.resources.JsonResourceBundle;
import org.apache.commons.lang.StringUtils;

import javax.inject.Singleton;
import java.util.*;

@Singleton
public class GuiLabelBundleLoader {

    private final Locale defaultLocale;

    private static Map<Locale, ResourceBundle> bundleMap = null;

    private static final String BUNDLE_NAME = "GuiLabel";

    @Inject
    public GuiLabelBundleLoader(@Default Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    /**
     * Loads a resource bundle for the requested locale, or the default locale.
     *
     * @param requestedLocale
     * @return
     */
    public synchronized ResourceBundle load(final Locale requestedLocale) {

        final Locale locale = requestedLocale == null ? defaultLocale : requestedLocale;

        String resourceSuffix = StringUtils.EMPTY;
        if (!locale.equals(Locale.getDefault())) {
            resourceSuffix = "_" + locale;
        }

        ResourceBundle bundle = null;
        if (bundleMap == null) {
            bundleMap = new HashMap<>();
        }
        if (bundleMap.containsKey(locale)) {
            bundle = bundleMap.get(locale);

        } else {
            bundle = ResourceBundle.getBundle("bundles/" + BUNDLE_NAME + resourceSuffix, locale, new SimpleJsonResourceBundle.Control());
            bundleMap.put(locale, bundle);
        }

        return bundle;
    }

    public String getGuiLabelTextFromBundle(final ResourceBundle bundle, final String propertyKey) {

        final ResourceBundle defaultBundle = ResourceBundle.getBundle("bundles/" + BUNDLE_NAME, new JsonResourceBundle.Control(new ObjectMapper()));

        String value = StringUtils.EMPTY;
        try {
            value = bundle.getString(propertyKey);
        } catch (final MissingResourceException e) {
            value = defaultBundle.getString(propertyKey);
        }

        if (value == null) {
            value = StringUtils.EMPTY;
        }

        return value;
    }

}
