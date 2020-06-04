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

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ExtractorUtils {

    public static List<Locale> getLocales(final String zanataRestUrl, final String zanataLocalesPath) throws IOException {

        String localesPath = zanataRestUrl + zanataLocalesPath;
        List<Locale> availableLocales = new ArrayList<>();

        //try {
        Reader in = new BufferedReader(new InputStreamReader(new URL(localesPath).openStream(), StandardCharsets.UTF_8));

        String jsonString = org.apache.commons.io.IOUtils.toString(in);
        JSONArray jsonArray = (JSONArray) new JSONTokener(jsonString).nextValue();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject object = jsonArray.getJSONObject(i);
            String localeId = object.getString("localeId");
            if (!localeId.equals("en-US") && !localeId.equals("sr-Latn-ME")) { //excluding new montenegrin lang for now
                if (localeId.equals("sr")) {
                    availableLocales.add(new Locale("sr-cyr"));
                } else if (localeId.equals("sr-Latn")) {
                    availableLocales.add(new Locale("sr-lat"));
                } else {
                    availableLocales.add(new Locale(localeId));
                }
            }
        }

        //} catch (IOException | JSONException ex) {
        //	LOG.error("Error while getting list of locales from zanata: ", ex);
        //}
        return availableLocales;
    }

    public static List<String> getLanguages(final String zanataRestUrl, final String zanataLocalesPath) throws IOException {

        final List<Locale> locales = ExtractorUtils.getLocales(zanataRestUrl, zanataLocalesPath);
        final List<String> languages = new ArrayList<>();
        for (final Locale loc : locales) {
            languages.add(loc.getLanguage());
        }

        return languages;
    }

    public static List<String> getEscoExcludedOccupations() {

        final List<String> ids = new ArrayList<>();

        try {
            final File escoExcludedFile = new File(ExtractorUtils.class.getResource("/esco/esco_exclusions.csv").toURI());
            final Reader in = new FileReader(escoExcludedFile);
            final String[] HEADERS = {"conceptUri", "iscoGroup", "preferredLabel"};

            final Iterable<CSVRecord> records = CSVFormat.DEFAULT.withHeader(HEADERS).withFirstRecordAsHeader().parse(in);

            for (final CSVRecord record : records) {
                final String uriID = record.get("conceptUri");
                ids.add(uriID);
            }
        } catch (Exception e) {
        }

        return ids;
    }

}
