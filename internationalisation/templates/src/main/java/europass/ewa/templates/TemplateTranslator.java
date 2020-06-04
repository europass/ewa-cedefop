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
package europass.ewa.templates;

import com.google.inject.name.Named;
import europass.ewa.enums.EuropassDocumentType;
import europass.ewa.templates.modules.ConfigModule;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Abstract class to be extended by the Online and Offline Template Translators
 *
 * Provides some utility methods and other methods that need to be overridden in
 * the classes that extend it.
 *
 * @author at
 */
public abstract class TemplateTranslator {

    private static final Logger LOG = LoggerFactory.getLogger(TemplateTranslator.class);

    protected static final String ALIAS = "n";
    protected static final String PTYPE_ALIAS = "t";
    protected static final String STYPE_ALIAS = "s";

    private final String zanataLocalesUrl;

    public TemplateTranslator(
            @Named(ConfigModule.ZANATA_LOCALES_URL) String zanataRestUrl) {

        this.zanataLocalesUrl = zanataRestUrl;
    }

    public void convert() throws URISyntaxException, IOException, JAXBException {
        this.convert(null);
    }

    public void convert(Locale locale) throws URISyntaxException, IOException, JAXBException {
        if (locale != null) {
            translate(locale);
        } else {
            translateAll();
        }
    }

    public abstract void translate(Locale locale) throws URISyntaxException, IOException, JAXBException;

    public void translateAll() throws URISyntaxException, IOException, JAXBException {

        LOG.info("== Extracting Templates ==");

        for (Locale locale : this.getLocales()) {
            translate(locale);
        }

    }

    public List<Locale> getLocales() {

        List<Locale> availableLocales = new ArrayList<>();

        try {
            Reader in = new BufferedReader(new InputStreamReader(new URL(zanataLocalesUrl).openStream(), StandardCharsets.UTF_8));

            String jsonString = org.apache.commons.io.IOUtils.toString(in);
            JSONArray jsonArray = (JSONArray) new JSONTokener(jsonString).nextValue();

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                String localeId = object.getString("localeId");
                if (!localeId.equals("en-US")) {
                    if (localeId.equals("sr")) {
                        availableLocales.add(new Locale("sr-cyr"));
                    } else if (localeId.equals("sr-Latn")) {
                        availableLocales.add(new Locale("sr-lat"));
                    } else {
                        availableLocales.add(new Locale(localeId));
                    }
                }
            }

        } catch (IOException | JSONException ex) {
            LOG.error("Error while getting list of locales from zanata: ", ex);
        }

        return availableLocales;
    }

    abstract TemplateContext getContext(EuropassDocumentType docType, Locale locale);

}
