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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

public class SimpleJsonResourceBundle extends ResourceBundle {

    private final JSONObject bundle;

    private SimpleJsonResourceBundle(InputStream in) throws UnsupportedEncodingException, JSONException {
        JSONTokener tokener = new JSONTokener(new InputStreamReader(in, "UTF-8"));
        bundle = new JSONObject(tokener);
    }

    private Set<String> getKeySet() {
        Set<String> keys = new HashSet<String>();

        @SuppressWarnings("unchecked")
        Iterator<String> keysIt = bundle.keys();

        while (keysIt.hasNext()) {
            keys.add(keysIt.next());
        }

        return keys;

    }

    @Override
    public Enumeration<String> getKeys() {
        if (parent != null) {
            return new ResourceBundleEnumeration(getKeySet(), parent.getKeys());
        }
        return Collections.enumeration(getKeySet());
    }

    @Override
    protected Object handleGetObject(String key) {
        try {
            return bundle.get(key);
        } catch (JSONException e) {
            return null;
        }
//			return properties.get(key);
    }

    public static class Control extends ResourceBundle.Control {

        @Override
        public List<String> getFormats(String baseName) {
            return Arrays.asList("json");
        }

        @Override
        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
                throws IllegalAccessException, InstantiationException, IOException {

            if (baseName == null || locale == null
                    || format == null || loader == null) {
                throw new NullPointerException();
            }
            ResourceBundle bundle = null;
            if (format.equals("json")) {
                String bundleName = toBundleName(baseName, locale);
                String resourceName = toResourceName(bundleName, format);
                InputStream stream = null;
                if (reload) {
                    URL url = loader.getResource(resourceName);
                    if (url != null) {
                        URLConnection connection = url.openConnection();
                        if (connection != null) {
                            // Disable caches to get fresh data for
                            // reloading.
                            connection.setUseCaches(false);
                            stream = connection.getInputStream();
                        }
                    }
                } else {
                    stream = loader.getResourceAsStream(resourceName);
                }
                if (stream != null) {
                    BufferedInputStream bis = new BufferedInputStream(stream);
                    try {
                        bundle = new SimpleJsonResourceBundle(bis);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    bis.close();
                }
            }
            return bundle;
        }
    }

    /**
     * Copied from jdk....
     */
    private static class ResourceBundleEnumeration implements Enumeration<String> {

        Set<String> set;
        Iterator<String> iterator;
        Enumeration<String> enumeration; // may remain null

        /**
         * Constructs a resource bundle enumeration.
         *
         * @param set an set providing some elements of the enumeration
         * @param enumeration an enumeration providing more elements of the
         * enumeration. enumeration may be null.
         */
        public ResourceBundleEnumeration(Set<String> set, Enumeration<String> enumeration) {
            this.set = set;
            this.iterator = set.iterator();
            this.enumeration = enumeration;
        }

        String next = null;

        public boolean hasMoreElements() {
            if (next == null) {
                if (iterator.hasNext()) {
                    next = iterator.next();
                } else if (enumeration != null) {
                    while (next == null && enumeration.hasMoreElements()) {
                        next = enumeration.nextElement();
                        if (set.contains(next)) {
                            next = null;
                        }
                    }
                }
            }
            return next != null;
        }

        public String nextElement() {
            if (hasMoreElements()) {
                String result = next;
                next = null;
                return result;
            } else {
                throw new NoSuchElementException();
            }
        }
    }
}
