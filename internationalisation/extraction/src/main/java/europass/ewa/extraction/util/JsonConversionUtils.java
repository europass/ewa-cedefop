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
package europass.ewa.extraction.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.Properties;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author at
 */
public class JsonConversionUtils {

    private static final Logger LOG = LoggerFactory.getLogger(JsonConversionUtils.class);

    private static final String OPEN_DELIMETER = "{";
    private static final String CLOSE_DELIMETER = "}";

    /**
     * Method reads java properties file from Path and converts it to json
     * string
     *
     * @param propertiesPath
     * @return
     * @throws IOException
     */
    public static String getJsonStringFromProperties(Path propertiesPath)
            throws IOException {

        InputStream in = Files.newInputStream(propertiesPath);

        Properties properties = new Properties();
        properties.load(in);

        return convertJavaPropertiesToJsonString(properties, false);
    }

    /**
     * Method reads java properties file from url and converts it to json string
     *
     * @param propertiesUrl
     * @return
     * @throws MalformedURLException
     * @throws IOException
     */
    public static String getJsonStringFromProperties(String propertiesUrl, boolean retainEmptyKeys)
            throws MalformedURLException, IOException {

        Reader in = new BufferedReader(new InputStreamReader(new URL(propertiesUrl).openStream(), StandardCharsets.UTF_8));

        OrderedProperties properties = new OrderedProperties();
        properties.load(in);

        return convertJavaPropertiesToJsonString(properties, retainEmptyKeys);
    }

    /**
     *
     * @param in
     * @return
     * @throws IOException
     */
    private static String convertJavaPropertiesToJsonString(Properties properties, boolean retainEmptyKeys)
            throws IOException {
        Enumeration enuKeys = properties.propertyNames();

        StringBuilder sb = new StringBuilder();
        sb.append(OPEN_DELIMETER);

        while (enuKeys.hasMoreElements()) {
            String key = (String) enuKeys.nextElement();
            String value = properties.getProperty(key);
            // this was added so as not to have empty strings for keys that exist in en 
            // but not in other languages 
            //need to keep the empty "html" keys thought in *Recommendations.js files
            //also Title.js, OpeningSalutation.js etc need their empty properties 
            //in order not to display default en values in drop down menus
            if (retainEmptyKeys || key.equals("html") || StringUtils.isNotBlank(value)) {
                sb.append("\"").append(key).append("\"")
                        .append(":")
                        .append(quoteJsonString(value))
                        .append(",");
            }
        }
        if (sb.length() != 1) {//some properties with values exist
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append(CLOSE_DELIMETER);
        return sb.toString();
    }

    /*
	 * Extract From Jettison (org.codehaus.jettison.json.JSONObject):
	 * Produce a string in double quotes with backslash sequences in all the right places. 
	 * A backslash will be inserted within </, allowing JSON text to be delivered in HTML. 
	 * In JSON text, a string cannot contain a control character or an unescaped quote or backslash. 
     */
    public static String quoteJsonString(String string) {
        if (string == null || string.length() == 0) {
            return "\"\"";
        }

        char c = 0;
        int i;
        int len = string.length();
        StringBuilder sb = new StringBuilder(len + 4);
        String t;

        sb.append('"');
        for (i = 0; i < len; i += 1) {
            c = string.charAt(i);
            switch (c) {
                case '\\':
                case '"':
                    sb.append('\\');
                    sb.append(c);
                    break;
//				case '/':
//					// if (b == '<') {
//					sb.append('\\');
//					//                }
//					sb.append(c);
//					break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                default:
                    if (c < ' ') {
                        t = "000" + Integer.toHexString(c);
                        sb.append("\\u" + t.substring(t.length() - 4));
                    } else {
                        sb.append(c);
                    }
            }
        }
        sb.append('"');
        return sb.toString();
    }
}
