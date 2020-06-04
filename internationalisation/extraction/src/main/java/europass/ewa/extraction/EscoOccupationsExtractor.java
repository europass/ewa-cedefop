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
import europass.ewa.extraction.modules.EscoExtractorModule;
import europass.ewa.extraction.modules.EwaTextExtractionModule;
import europass.ewa.extraction.util.ExtractorUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
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

public class EscoOccupationsExtractor {

    private static final Logger LOG = LoggerFactory.getLogger(EscoOccupationsExtractor.class);

    private final String JSON_EXT = ".json";
    private final String JS_EXT = ".js";
    private final String UNDERSCORE = "_";

    private final static String ESCO_GENDER_SEPARATOR = "/";
    private final static String ESCO_GENDER_MALE_KEY = "M";
    private final static String ESCO_GENDER_FEMALE_KEY = "F";

    private final static String OCCUPATION_MAX_LIMIT = "4000";
    private final static String OCCUPATION_URL = "https://ec.europa.eu/esco/api/search?type=occupation&limit=" + OCCUPATION_MAX_LIMIT;

    private final String editorsBaseDir = Paths.get("").toAbsolutePath().getParent().toAbsolutePath().getParent().toString();
    private final Map<String, String> localeMapCodesForEsco = new HashMap();

    private final String jsResourcesPath;
    private final String defaultLanguage;
    private final String modelResourcesPath;
    private final String zanataRestUrl;
    private final String zanataLocalesPath;
    private final String modelEscoResourceName;

    //private final List<String> excludedOccupations;
    @Inject
    public EscoOccupationsExtractor(@Named(EscoExtractorModule.DEFAULT_LANGUAGE) String defaultLanguage,
            @Named(EscoExtractorModule.EDITORS_RESOURCES_PATH) String jsResourcesPath,
            @Named(EscoExtractorModule.MODEL_RESOURCES_PATH) String modelResourcesPath,
            @Named(EwaTextExtractionModule.ZANATA_REST_URL) String zanataRestUrl,
            @Named(EwaTextExtractionModule.ZANATA_LOCALES_PATH) String zanataLocalesPath,
            @Named(EscoExtractorModule.MODEL_ESCO_RESOURCES) String modelEscoResourceName) {

        this.jsResourcesPath = jsResourcesPath;
        this.defaultLanguage = defaultLanguage;
        this.modelResourcesPath = modelResourcesPath;
        this.zanataRestUrl = zanataRestUrl;
        this.zanataLocalesPath = zanataLocalesPath;
        this.modelEscoResourceName = modelEscoResourceName;

        localeMapCodesForEsco.put("nb", "no");
        //excludedOccupations = ExtractorUtils.getEscoExcludedOccupations();
    }

    public void execute() throws IOException {

        final Reader in = new BufferedReader(new InputStreamReader(new URL(OCCUPATION_URL).openStream(), StandardCharsets.UTF_8));
        final String jsonString = org.apache.commons.io.IOUtils.toString(in);

        final JSONObject rootJSON = new JSONObject(jsonString);
        final JSONObject resultsJSON = (JSONObject) rootJSON.get("_embedded");
        final JSONArray resultsArray = resultsJSON.getJSONArray("results");

        // use languages as coming from Zanata
        final List<String> languages = ExtractorUtils.getLanguages(zanataRestUrl, zanataLocalesPath);
        for (final String lang : languages) {

            final StringBuilder occupations = buildEscoOccupationalLabels(resultsArray, lang);
            produceJsOccupationalFields(occupations, lang, languages);
            produceJsonOccupationalFields(occupations, lang);
        }
    }

    private String getEscoSpecificLocale(final String availableLanguage) {

        if (localeMapCodesForEsco.containsKey(availableLanguage)) {
            return localeMapCodesForEsco.get(availableLanguage);
        }
        return availableLanguage;
    }

    protected StringBuilder buildEscoOccupationalLabels(final JSONArray results, final String locale) {

        final StringBuilder occupations = new StringBuilder();
        final List<String> notSupportedLangs = new ArrayList<>();

        occupations.append("{");
        for (int i = 0; i < results.length(); i++) {

            final JSONObject occupation = (JSONObject) results.get(i);
            final JSONObject occupationLabel = (JSONObject) occupation.get("preferredLabel");
            final String occupationUri = (String) occupation.get("uri");

            //if (excludedOccupations.contains(occupationUri)) continue;
            final String occupationUriID = occupationUri.substring(occupationUri.lastIndexOf("/") + 1);
            try {
                final String escoOccupationLabl = (String) occupationLabel.get(getEscoSpecificLocale(locale));
                occupations.append("\"").append(occupationUriID).append("\"").append(":")
                        .append("{").append("\"").append(ESCO_GENDER_MALE_KEY).append("\"").append(":")
                        .append("\"").append(getLabel(escoOccupationLabl)).append("\"").append(",")
                        .append("\"").append(ESCO_GENDER_FEMALE_KEY).append("\"").append(":")
                        .append("\"").append(getLabel(escoOccupationLabl)).append("\"").append("}");
                if (i != results.length() - 1) {
                    occupations.append(",");
                }
            } catch (final JSONException e) {
                notSupportedLangs.add(locale);
            }
        }
        occupations.append("}");

        if (!notSupportedLangs.isEmpty()) {
            LOG.debug("Locale not supported by ESCO {}", locale);
        }

        return occupations;
    }

    protected void produceJsOccupationalFields(final StringBuilder occupationLabels,
            final String locale,
            final List<String> locales) {

        final String localeDirPath = editorsBaseDir + File.separator + jsResourcesPath
                + ((locale.equals(defaultLanguage) ? "" : (File.separator + locale)));

        final StringBuilder occupationLabelFinalJS = new StringBuilder();

        String prefix = "define(";
        String otherLocales = "";
        String suffix = ");";

        if (locale.equals(defaultLanguage)) {
            prefix = prefix + "{\"root\": ";
            for (final String loc : locales) {
                if (!loc.equals(defaultLanguage)) {
                    otherLocales = otherLocales + ",\"" + loc + "\" : true";
                }
            }
            suffix = otherLocales + "}" + suffix;
        }

        occupationLabelFinalJS.append(prefix).append(occupationLabels).append(suffix);

        final File jsFile = new File(localeDirPath + File.separator + modelEscoResourceName + JS_EXT);

        try {
            final FileOutputStream jsOS = new FileOutputStream(jsFile);
            jsOS.write(occupationLabelFinalJS.toString().getBytes("UTF-8"));
            jsOS.close();
        } catch (final IOException e) {
            LOG.error("IO error when trying to write js OccupationField file..");
        }
    }

    protected void produceJsonOccupationalFields(final StringBuilder occupationLabels,
            final String locale) {

        try {
            Path file = Paths.get(editorsBaseDir + File.separator + modelResourcesPath
                    + File.separator + modelEscoResourceName + UNDERSCORE + locale + JSON_EXT);
            Files.write(file, Arrays.asList(new String[]{occupationLabels.toString()}), Charset.forName("UTF-8"));

            if (locale.equals("en")) {
                file = Paths.get(editorsBaseDir + File.separator + modelResourcesPath
                        + File.separator + modelEscoResourceName + JSON_EXT);
                Files.write(file, Arrays.asList(new String[]{occupationLabels.toString()}), Charset.forName("UTF-8"));
            }
        } catch (final IOException e) {
            LOG.error("IO error when trying to write json OccupationField file..");
        }
    }

    private String getLabel(final String occupationLabel) {
        return StringUtils.capitalize(occupationLabel).replaceAll("\"", "\\\\\"");
    }
}
