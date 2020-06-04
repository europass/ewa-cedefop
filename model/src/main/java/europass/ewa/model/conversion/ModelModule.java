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
package europass.ewa.model.conversion;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.inject.Named;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import europass.ewa.enums.EuropassDocumentType;
import europass.ewa.model.Namespace;
import europass.ewa.model.PrintingPreference;
import europass.ewa.model.conversion.json.ObjectMapperProvider;
import europass.ewa.model.conversion.xml.XmlMapperProvider;
import europass.ewa.model.format.JDateFormat;
import europass.ewa.resources.JsonResourceBundle;
import europass.ewa.resources.ResourceBundleMap;

public class ModelModule extends AbstractModule {

    public static final String DEFAULT_PREFS = "europass-default-prefs";

    public static final String DEFAULT_CV_PREFS = "europass-default-cv-prefs";

    public static final String DEFAULT_LP_PREFS = "europass-default-elp-prefs";

    public static final String DEFAULT_CL_PREFS = "europass-default-ecl-prefs";

    ObjectMapper bundleMapper = null;

    Map<String, PrintingPreference> cvBundleMap = null;

    Map<String, PrintingPreference> elpBundleMap = null;

    Map<String, PrintingPreference> eclBundleMap = null;

    Map<EuropassDocumentType, Map<String, PrintingPreference>> defaultPrefs = null;

    @Override
    protected void configure() {
        //--- Mapper Providers ---
        bind(ObjectMapper.class).toProvider(ObjectMapperProvider.class).in(Singleton.class);
        bind(XmlMapper.class).toProvider(XmlMapperProvider.class).in(Singleton.class);

        requestStaticInjection(Namespace.class);

    }

    public static ObjectMapper bundleObjectMapper() {
        ObjectMapper bundleMapper = new ObjectMapper();
        bundleMapper.addMixInAnnotations(Object.class, BundleMixin.class);
        return bundleMapper;
    }

    @Provides
    @Singleton
    @Named(DEFAULT_PREFS)
    Map<EuropassDocumentType, Map<String, PrintingPreference>> defaultPrintingPrefsAllDocs() throws IOException {
        if (defaultPrefs == null) {
            defaultPrefs = new HashMap<EuropassDocumentType, Map<String, PrintingPreference>>();
            defaultPrefs.put(EuropassDocumentType.ECV, defaultPrintingPrefs());
            defaultPrefs.put(EuropassDocumentType.ELP, defaultElpPrintingPrefs());
            defaultPrefs.put(EuropassDocumentType.ECL, defaultECLPrintingPrefs());
        }
        return defaultPrefs;
    }

    @Provides
    @Singleton
    @Named(DEFAULT_LP_PREFS)
    Map<String, PrintingPreference> defaultElpPrintingPrefs() throws IOException {
        if (bundleMapper == null) {
            bundleMapper = new ObjectMapper();
            bundleMapper.addMixInAnnotations(Object.class, BundleMixin.class);
        }
        if (elpBundleMap == null) {
            ResourceBundle bundle = ResourceBundle.getBundle(
                    "preferences/LPDefaultPrintingPreferences",
                    new JsonResourceBundle.Control(bundleMapper));

            elpBundleMap = new ResourceBundleMap<PrintingPreference>(bundle);
        }
        return elpBundleMap;
    }

    /**
     * Provides a Map<String, Object> that is parsed from a JSON resource
     *
     * @return
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    @Provides
    @Singleton
    @Named(DEFAULT_CV_PREFS)
    Map<String, PrintingPreference> defaultPrintingPrefs() throws IOException {
        if (bundleMapper == null) {
            bundleMapper = new ObjectMapper();
            bundleMapper.addMixInAnnotations(Object.class, BundleMixin.class);
        }
        if (cvBundleMap == null) {
            ResourceBundle bundle = ResourceBundle.getBundle(
                    "preferences/CVDefaultPrintingPreferences",
                    new JsonResourceBundle.Control(bundleMapper));

            cvBundleMap = new ResourceBundleMap<PrintingPreference>(bundle);
        }
        return cvBundleMap;
    }

    @Provides
    @Singleton
    @Named(DEFAULT_CL_PREFS)
    Map<String, PrintingPreference> defaultECLPrintingPrefs() throws IOException {
        if (bundleMapper == null) {
            bundleMapper = new ObjectMapper();
            bundleMapper.addMixInAnnotations(Object.class, BundleMixin.class);
        }
        if (eclBundleMap == null) {
            ResourceBundle bundle = ResourceBundle.getBundle(
                    "preferences/CLDefaultPrintingPreferences",
                    new JsonResourceBundle.Control(bundleMapper));

            eclBundleMap = new ResourceBundleMap<PrintingPreference>(bundle);
        }
        return eclBundleMap;
    }

    @JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "@type", defaultImpl = java.util.LinkedHashMap.class)
    @JsonSubTypes({
        @Type(name = "PrintingPreference", value = PrintingPreference.class)
        ,
		@Type(name = "JDateFormat", value = JDateFormat.class)
    })
    public static class BundleMixin {
    }

    public static boolean parseBoolean(String str) {
        try {
            Integer asInt = Integer.parseInt(str);
            switch (asInt) {
                case 0: {
                    return false;
                }
                case 1: {
                    return true;
                }
            }
        } catch (final NumberFormatException nfe) {
            return Boolean.valueOf(str);
        }

        return Boolean.valueOf(str);
    }
}
