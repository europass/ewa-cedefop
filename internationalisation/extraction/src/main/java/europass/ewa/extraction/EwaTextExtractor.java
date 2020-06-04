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
package europass.ewa.extraction;

import com.google.inject.name.Named;
import europass.ewa.extraction.modules.EwaTextExtractionModule;
import europass.ewa.extraction.util.ExtractorUtils;
import europass.ewa.extraction.util.JsonConversionUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 *
 * @author at
 */
public class EwaTextExtractor {

    private static final Logger LOG = LoggerFactory.getLogger(EwaTextExtractor.class);

    private final String JSON_EXT = ".json";
    private final String JS_EXT = ".js";
    private final String PROPERTIES_EXT = ".properties";
    private final String UNDERSCORE = "_";
    private final String SEPARATOR_LOCALE_SCRIPT = "-";

    private final String zanataRestUrl;
    private final String zanataFilesPath;
    private final String zanataLocalesPath;
    private final String zanataDocumentsPath;

    private final Locale defaultLocale;

    private final String editorsBaseDir = Paths.get("").toAbsolutePath().getParent().toAbsolutePath().getParent().toString();
    private final String jsResourcesPath;
    private final String conversionResourcesPath;
    private final String modelResourcesPath;
    private final String modelPreferencesPath;
    private final String templatesResourcesPath;
    private final String apiResourcesPath;
    private final String coreResourcesPath;
    private final String restResourcesPath;
    private final String editorsExtraResourcesPath;

    private final List<String> conversionResources;
    private final List<String> modelResources;
    private final List<String> modelPreferences;
    private final List<String> templatesResources;
    private final List<String> apiResources;
    private final List<String> coreResources;
    private final List<String> restResources;
    private final List<String> retainEmptyKeysResources; //Resources that need to retain their empty key-value pairs
    private final List<String> editorsExtraResources; //Resources that are extra json files needed for Editors module

    private final Map localeCodeReplaced = new HashMap<String, String>();

    @Inject
    public EwaTextExtractor(
            @Named(EwaTextExtractionModule.ZANATA_REST_URL) String zanataRestUrl,
            @Named(EwaTextExtractionModule.ZANATA_FILES_PATH) String zanataFilesPath,
            @Named(EwaTextExtractionModule.ZANATA_LOCALES_PATH) String zanataLocalesPath,
            @Named(EwaTextExtractionModule.ZANATA_DOCUMENTS_PATH) String zanataDocumentsPath,
            @Named(EwaTextExtractionModule.DEFAULT_LANGUAGE) String defaultLanguage,
            @Named(EwaTextExtractionModule.EDITORS_RESOURCES_PATH) String jsResourcesPath,
            @Named(EwaTextExtractionModule.CONVERSION_RESOURCES_PATH) String conversionResourcesPath,
            @Named(EwaTextExtractionModule.MODEL_RESOURCES_PATH) String modelResourcesPath,
            @Named(EwaTextExtractionModule.MODEL_PREFERENCES_PATH) String modelPreferencesPath,
            @Named(EwaTextExtractionModule.TEMPLATES_RESOURCES_PATH) String templatesResourcesPath,
            @Named(EwaTextExtractionModule.API_RESOURCES_PATH) String apiResourcesPath,
            @Named(EwaTextExtractionModule.CORE_RESOURCES_PATH) String coreResourcesPath,
            @Named(EwaTextExtractionModule.REST_RESOURCES_PATH) String restResourcesPath,
            @Named(EwaTextExtractionModule.EDITORS_EXTRA_RESOURCES_PATH) String editorsExtraResourcesPath,
            @Named(EwaTextExtractionModule.CONVERSION_RESOURCES) String conversionResourcesString,
            @Named(EwaTextExtractionModule.MODEL_RESOURCES) String modelResourcesString,
            @Named(EwaTextExtractionModule.MODEL_PREFERENCES) String modelPreferencesString,
            @Named(EwaTextExtractionModule.TEMPLATES_RESOURCES) String templatesResourcesString,
            @Named(EwaTextExtractionModule.API_RESOURCES) String apiResourcesString,
            @Named(EwaTextExtractionModule.CORE_RESOURCES) String coreResourcesString,
            @Named(EwaTextExtractionModule.REST_RESOURCES) String restResourcesString,
            @Named(EwaTextExtractionModule.RETAIN_EMPTY_KEYS_RESOURCES) String retainEmptyKeysResourcesString,
            @Named(EwaTextExtractionModule.EDITORS_EXTRA_RESOURCES) String editorsExtraResourcesString) {

        this.zanataRestUrl = zanataRestUrl;
        this.zanataFilesPath = zanataFilesPath;
        this.zanataLocalesPath = zanataLocalesPath;
        this.zanataDocumentsPath = zanataDocumentsPath;

        this.defaultLocale = new Locale(defaultLanguage);

        this.jsResourcesPath = jsResourcesPath;

        this.conversionResourcesPath = conversionResourcesPath;
        this.modelResourcesPath = modelResourcesPath;
        this.modelPreferencesPath = modelPreferencesPath;
        this.templatesResourcesPath = templatesResourcesPath;
        this.apiResourcesPath = apiResourcesPath;
        this.coreResourcesPath = coreResourcesPath;
        this.restResourcesPath = restResourcesPath;
        this.editorsExtraResourcesPath = editorsExtraResourcesPath;

        this.conversionResources = Arrays.asList(conversionResourcesString.split("\\s*,\\s*"));
        this.modelResources = Arrays.asList(modelResourcesString.split("\\s*,\\s*"));
        this.modelPreferences = Arrays.asList(modelPreferencesString.split("\\s*,\\s*"));
        this.templatesResources = Arrays.asList(templatesResourcesString.split("\\s*,\\s*"));
        this.apiResources = Arrays.asList(apiResourcesString.split("\\s*,\\s*"));
        this.coreResources = Arrays.asList(coreResourcesString.split("\\s*,\\s*"));
        this.restResources = Arrays.asList(restResourcesString.split("\\s*,\\s*"));
        this.retainEmptyKeysResources = Arrays.asList(retainEmptyKeysResourcesString.split("\\s*,\\s*"));
        this.editorsExtraResources = Arrays.asList(editorsExtraResourcesString.split("\\s*,\\s*"));

        localeCodeReplaced.put("sr-cyr", "sr");
        localeCodeReplaced.put("sr-lat", "sr-Latn");
    }

    protected String replaceLanguageCodeWithCustom(final Locale locale) {
        if (localeCodeReplaced.containsKey(locale.getLanguage())) {
            return (String) localeCodeReplaced.get(locale.getLanguage());
        }
        return locale.getLanguage();
    }

    private List<String> getLocalisableResources() throws IOException {
        String documentsPath = zanataRestUrl + zanataDocumentsPath;
        List<String> localisableResources = new ArrayList<>();

        //try {
        Reader in = new BufferedReader(new InputStreamReader(new URL(documentsPath).openStream(), StandardCharsets.UTF_8));

        String jsonString = org.apache.commons.io.IOUtils.toString(in);
        JSONArray jsonArray = (JSONArray) new JSONTokener(jsonString).nextValue();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            String documentName = object.getString("name");
            localisableResources.add(documentName);
        }

        //} catch (IOException | JSONException ex) {
        //	LOG.error("Error while getting list of localisable resources from zanata: ", ex);
        //}
        return localisableResources;
    }

    private String getResourceUrl(String resourceName, Locale locale) {
        return zanataRestUrl + zanataFilesPath + replaceLanguageCodeWithCustom(locale)
                + "/baked?docId=" + resourceName;
    }

    public void extractLocalisableResources() throws IOException {

        List<String> localisableResources = getLocalisableResources();
        List<Locale> locales = ExtractorUtils.getLocales(zanataRestUrl, zanataLocalesPath);

        LOG.info("Extracting localisable resources");

        for (String localisableResource : localisableResources) {
            String fileName = localisableResource.replace(PROPERTIES_EXT, "");

            for (Locale locale : locales) {

                //LOG.info("== Extracting localisable resources for " + localisableResource + " in " + locale.getLanguage() + " ==");
                String url = getResourceUrl(localisableResource, locale);

                FileOutputStream jsOS = null;

                //try {
                //produce jsonObject file
                boolean retainEmptyKeysInFile = retainEmptyKeysResources.contains(localisableResource.replace(".properties", ""));

                String json = JsonConversionUtils.getJsonStringFromProperties(url, retainEmptyKeysInFile);
                String jsonFileName = fileName + UNDERSCORE + locale.getLanguage() + JSON_EXT;

                List<String> modulesPaths = new ArrayList<>();
                if (conversionResources.contains(fileName)) {
                    modulesPaths.add(conversionResourcesPath);
                }
                if (modelResources.contains(fileName)) {
                    modulesPaths.add(modelResourcesPath);
                }
                if (modelPreferences.contains(fileName)) {
                    modulesPaths.add(modelPreferencesPath);
                }
                if (templatesResources.contains(fileName)) {
                    modulesPaths.add(templatesResourcesPath);
                }
                if (apiResources.contains(fileName)) {
                    modulesPaths.add(apiResourcesPath);
                }
                if (coreResources.contains(fileName)) {
                    modulesPaths.add(coreResourcesPath);
                }
                if (restResources.contains(fileName)) {
                    modulesPaths.add(restResourcesPath);
                }
                if (editorsExtraResources.contains(fileName)) {
                    modulesPaths.add(editorsExtraResourcesPath);
                }

                for (String path : modulesPaths) {
                    Path file = Paths.get(editorsBaseDir + File.separator + path + File.separator + jsonFileName);
                    Files.write(file, Arrays.asList(new String[]{json}), Charset.forName("UTF-8"));

                    if (locale.getLanguage().equals("en")) {
                        file = Paths.get(editorsBaseDir + File.separator + path + File.separator + fileName + JSON_EXT);
                        Files.write(file, Arrays.asList(new String[]{json}), Charset.forName("UTF-8"));
                    }
                }

                //produce js files to be used by editors front-end
                String localeDirPath = editorsBaseDir + File.separator + jsResourcesPath
                        + (locale.equals(defaultLocale) ? "" : (File.separator + locale.getLanguage()));

                File localeDir = new File(localeDirPath);
                if (!localeDir.exists()) {
                    localeDir.mkdir();
                }

                File jsFile = new File(localeDirPath + File.separator + fileName + JS_EXT);
                jsOS = new FileOutputStream(jsFile);

                // Write the module definition.
                // If the default documentName, write root and close accordingly
                String prefix = "define(";
                String otherLocales = "";
                String suffix = ");";

                if (locale.equals(defaultLocale)) {
                    prefix = prefix + "{\"root\": ";
                    for (Locale loc : locales) {
                        if (!loc.equals(defaultLocale)) {
                            otherLocales = otherLocales + ",\"" + loc + "\" : true";
                        }
                    }
                    suffix = "}" + suffix;
                }
                // a) write prefix
                jsOS.write(prefix.getBytes("UTF-8"));
                // b) write body 
                jsOS.write(json.getBytes("UTF-8"));
                // c) if default documentName write the rest locales
                if (!otherLocales.isEmpty()) {
                    jsOS.write(otherLocales.getBytes("UTF-8"));
                }
                // d) write suffix
                jsOS.write(suffix.getBytes("UTF-8"));

                //} catch (IOException ex) {
                //	LOG.error("Exception extracting localisable resource: " + localisableResource + " in " + locale + " - " + ex.getMessage());
                //} finally {
                try {
                    if (jsOS != null) {
                        jsOS.close();
                    }
                } catch (IOException e) {
                    LOG.error("Failed to close File output streams ", e);
                }
                //}
            }
        }
    }
}
