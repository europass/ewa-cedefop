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
package europass.ewa.model.format;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import com.fasterxml.jackson.databind.ObjectMapper;

public final class JDateFormatBundle extends ResourceBundle {

    public static final String KEY_CURRENT = "current";

    public static final String KEY_DEFAULT_FORMAT = "text/short";

    Map<String, JDateFormat> formats;

    String current;

    private JDateFormatBundle(JDateFormatSet config) {
        current = config.getCurrent();
        formats = new HashMap<String, JDateFormat>();
        for (Map.Entry<String, String> pattern : config.getPatterns().entrySet()) {
            JDateFormat fmt = JDateFormat.compile(pattern.getValue(), config.getNameShort(), config.getNameWithDay(), config.getNameNoDay(), config.getDaySuffix());
            formats.put(pattern.getKey(), fmt);
        }
    }

    @Override
    public Enumeration<String> getKeys() {
        return Collections.enumeration(formats.keySet());
    }

    @Override
    protected Object handleGetObject(String name) {
        if (KEY_CURRENT.equals(name)) {
            return current;
        }
        return formats.get(name);
    }

    public JDateFormat getJDateFormat(String name) {
        return (JDateFormat) getObject(name);
    }

    public static JDateFormatBundle getBundle(Locale locale) {
        return (JDateFormatBundle) ResourceBundle.getBundle("bundles/DateFormat", locale, new Control());
    }

    public static class Control extends ResourceBundle.Control {

        private final ObjectMapper mapper;

        public Control() {
            this(new ObjectMapper());
        }

        public Control(ObjectMapper mapper) {
            this.mapper = mapper;
        }

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
                    bundle = new JDateFormatBundle(mapper.readValue(stream, JDateFormatSet.class));
                    stream.close();
                }
            }
            return bundle;
        }

    }

    public static class JDateFormatSet {

        private Map<String, String> patterns;

        private String current;

        private String[] nameShort;

        private String[] nameNoDay;

        private String[] nameWithDay;

        private String[] daySuffix;

        public Map<String, String> getPatterns() {
            return patterns;
        }

        public String getCurrent() {
            return current;
        }

        public String[] getNameShort() {
            return nameShort;
        }

        public String[] getNameNoDay() {
            return nameNoDay;
        }

        public String[] getNameWithDay() {
            return nameWithDay;
        }

        public String[] getDaySuffix() {
            return daySuffix;
        }

        public void setPatterns(Map<String, String> patterns) {
            this.patterns = patterns;
        }

        public void setCurrent(String current) {
            this.current = current;
        }

        public void setNameShort(String[] nameShort) {
            this.nameShort = nameShort;
        }

        public void setNameNoDay(String[] nameNoDay) {
            this.nameNoDay = nameNoDay;
        }

        public void setNameWithDay(String[] nameWithDay) {
            this.nameWithDay = nameWithDay;
        }

        public void setDaySuffix(String[] daySuffix) {
            this.daySuffix = daySuffix;
        }

    }
}
