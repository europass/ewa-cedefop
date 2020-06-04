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
package org.europass.webapps.tools.literals;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import org.apache.commons.io.IOUtils;
import org.europass.webapps.tools.literals.util.OrderedProperties;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class UpdateZanataEnKeys {

    private static final String ZANATA_DOCUMENTS_PATH = "zanata.documents.path";
    private static final String ZANATA_PROPERTIES_FILES_PATH = "zanata.properties.files.path";
    private static final String ZANATA_EN_DOCUMENT_PATH = "zanata.en.document.path";

    public static void main(String[] args) {
        try {
            Properties configurationProperties = loadConfigurationProperties();

            //Get the list of all documents from zanata
            List<String> documents = getDocuments(configurationProperties.getProperty(ZANATA_DOCUMENTS_PATH));

            StringBuilder modifications = new StringBuilder();

            for (String document : documents) {
                //Get source properties file
                OrderedProperties sourceProps = readPropertiesFile(configurationProperties.getProperty(ZANATA_PROPERTIES_FILES_PATH), document);
                //Save source properties files to use for comparison

                //For the element, leading space characters, but not embedded or trailing space characters, are written with a preceding \ character. 
                //The key and element characters #, !, =, and : are written with a preceding backslash to ensure that they are properly loaded. 
                sourceProps.store(new OutputStreamWriter(new FileOutputStream("files-original/" + document), "UTF-8"), null);

                //Get latest en translations
                OrderedProperties enTranslations = readPropertiesFile(configurationProperties.getProperty(ZANATA_EN_DOCUMENT_PATH), document);

                //Create new updated source file by adding to source keys the new translations
                OrderedProperties updatedProps = new OrderedProperties();

                Enumeration sourceKeys = sourceProps.keys();

                while (sourceKeys.hasMoreElements()) {
                    String sourceKey = (String) sourceKeys.nextElement();
                    String sourceValue = sourceProps.getProperty(sourceKey);
                    String enValue = enTranslations.getProperty(sourceKey);

                    //TODO: do we want this? 
                    //String value = StringUtils.isBlank(enValue) ? sourceValue : enValue;
                    updatedProps.put(sourceKey, enValue);

                    if (!enValue.equals(sourceValue)) {
                        modifications.append(document.replace(".properties", ": ")).append("\n")
                                .append("key: ").append(sourceKey).append("\n")
                                .append("sourceValue: ").append(sourceValue).append("\n")
                                .append("enValue: ").append(enValue).append("\n")
                                .append("--").append("\n");
                    }
                }

                updatedProps.store(new OutputStreamWriter(new FileOutputStream("files-updated/" + document), "UTF-8"), null);
            }

            Files.write(Paths.get("files-updated/updates.txt"),
                    Arrays.asList(new String[]{modifications.toString()}), Charset.forName("UTF-8"));

            System.out.println("the end");
        } catch (Exception ex) {
            System.out.println("Exception while fetching and updating properties files: " + ex.getMessage());
        }
    }

    private static List<String> getDocuments(String path) throws MalformedURLException, IOException {
        List<String> documents = new ArrayList<>();

        Reader in = new BufferedReader(
                new InputStreamReader(new URL(path).openStream(), StandardCharsets.UTF_8));

        String jsonString = IOUtils.toString(in);
        JSONArray jsonArray = (JSONArray) new JSONTokener(jsonString).nextValue();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            String documentName = object.getString("name");
            documents.add(documentName);
        }

        return documents;
    }

    private static OrderedProperties readPropertiesFile(String path, String document) throws IOException {
        Reader in = new BufferedReader(new InputStreamReader(new URL(path + document).openStream(), StandardCharsets.UTF_8));
        OrderedProperties properties = new OrderedProperties();
        properties.load(in);

        return properties;
    }

    private static Properties loadConfigurationProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(UpdateZanataEnKeys.class.getClassLoader().getResourceAsStream("config.properties"));
        return properties;
    }

}
