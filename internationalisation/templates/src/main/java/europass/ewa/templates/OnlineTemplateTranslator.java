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
import europass.ewa.conversion.odt.ODTMustache;
import europass.ewa.conversion.odt.ODTMustacheFactory;
import europass.ewa.enums.EuropassDocumentType;
import europass.ewa.model.conversion.ModelModule;
import europass.ewa.resources.JsonResourceBundle;
import europass.ewa.templates.modules.ConfigModule;
import europass.ewa.templates.modules.OnlineTemplateModule;
import europass.ewa.templates.util.FileUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.zip.ZipOutputStream;
import javax.inject.Inject;
import javax.xml.bind.JAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author at
 */
public class OnlineTemplateTranslator extends TemplateTranslator {

    private static final Logger LOG = LoggerFactory.getLogger(OnlineTemplateTranslator.class);

    private static final String LOCALISED_TMP_JAR_NAME = "tmp_europass-odt-template";

    private static final String ZIP_EXT = ".zip";

    private final String editorsBaseDir = Paths.get("").toAbsolutePath().getParent().toAbsolutePath().getParent().toString();

    private final Set<DocumentInfo> documents;

    private final ODTMustacheFactory factory;

    // odt templates go under conversion/src/main/resources/odt 
    private final String onlineTemplatesExportPath;

    @Inject
    public OnlineTemplateTranslator(
            ODTMustacheFactory factory,
            @Named(ConfigModule.ZANATA_LOCALES_URL) String zanataLocalesUrl,
            @Named(OnlineTemplateModule.ONLINE_TEMPLATES_DIRS) Set<DocumentInfo> documents,
            @Named(ConfigModule.ONLINE_TEMPLATES_EXPORT_PATH) String onlineTemplatesExportPath) {

        super(zanataLocalesUrl);

        this.factory = factory;
        this.documents = documents;
        this.onlineTemplatesExportPath = onlineTemplatesExportPath;
    }

    @Override
    public void translate(Locale locale) throws URISyntaxException, IOException, JAXBException {

        // Run translate for all templates (cv and esp)
        for (DocumentInfo document : documents) {
            EuropassDocumentType docType = document.getType();
            String documentType = docType.getAcronym();
            String srcTemplate = document.getOdtPath();

            //LOG.info("=============  " + documentType + "  ==============");
            //LOG.info("== Extract ONLINE Template (" + srcTemplate + ") in " + locale);
            String[] parts = srcTemplate.split("/");

            ODTMustache template = factory.create(srcTemplate + "/src", true);

            Object context = this.getContext(docType, locale);

            String tempZipPerLocaleName = editorsBaseDir + onlineTemplatesExportPath + File.separator
                    + LOCALISED_TMP_JAR_NAME + "_" + documentType + "_" + locale.getLanguage() + ZIP_EXT;
            FileOutputStream out = new FileOutputStream(tempZipPerLocaleName);

            // Execute the Mustach compilation. This will fill out the above created
            // file with anything that comprises a CV or ESP ODT document
            ZipOutputStream zout = new ZipOutputStream(out);
            template.execute(zout, context);
            zout.flush();
            zout.close();

            //Now create a directory structure e.g. /odt/cv/fr
            String tempDirPerLocale = editorsBaseDir + onlineTemplatesExportPath + File.separator;
            for (String part : parts) {
                tempDirPerLocale = tempDirPerLocale + part + File.separator;
                FileUtils.createDir(tempDirPerLocale);
            }
            tempDirPerLocale = tempDirPerLocale + locale.getLanguage();
            FileUtils.createDir(tempDirPerLocale);

            FileUtils.unZipIt(tempZipPerLocaleName, tempDirPerLocale);

            //For each of the document (cv, ecl, elp, esp) , copy the contents of "en" over the "default"
        }
    }

    @Override
    TemplateContext getContext(EuropassDocumentType docType, Locale locale) {
        switch (docType) {
            case ECL: {
                Map<String, AdaptedResourceBundleMap> multipleBundleMap = new HashMap<>();
                multipleBundleMap.put("Document", new AdaptedResourceBundleMap(ResourceBundle.getBundle("bundles/DocumentLabel", locale, new JsonResourceBundle.Control())));
                multipleBundleMap.put("CLExtraPreferences", new AdaptedResourceBundleMap(ResourceBundle.getBundle("preferences/CLExtraPreferences", locale, new JsonResourceBundle.Control())));
                MultipleResourceBundleMap resources = new MultipleResourceBundleMap(multipleBundleMap);

                PreferencesResourceBundleMap preferences = new PreferencesResourceBundleMap(ResourceBundle.getBundle("preferences/CLDefaultPrintingPreferences", locale, new JsonResourceBundle.Control(ModelModule.bundleObjectMapper())));

                return new WithPreferencesContext(resources, preferences);
            }
            case ELP: {
                Map<String, AdaptedResourceBundleMap> multipleBundleMap = new HashMap<>();

                multipleBundleMap.put("Listening", new AdaptedResourceBundleMap(ResourceBundle.getBundle("bundles/LanguageListeningLevel", locale, new JsonResourceBundle.Control())));
                multipleBundleMap.put("Reading", new AdaptedResourceBundleMap(ResourceBundle.getBundle("bundles/LanguageReadingLevel", locale, new JsonResourceBundle.Control())));
                multipleBundleMap.put("SpokenInteraction", new AdaptedResourceBundleMap(ResourceBundle.getBundle("bundles/LanguageSpokenInteractionLevel", locale, new JsonResourceBundle.Control())));
                multipleBundleMap.put("SpokenProduction", new AdaptedResourceBundleMap(ResourceBundle.getBundle("bundles/LanguageSpokenProductionLevel", locale, new JsonResourceBundle.Control())));
                multipleBundleMap.put("Writing", new AdaptedResourceBundleMap(ResourceBundle.getBundle("bundles/LanguageWritingLevel", locale, new JsonResourceBundle.Control())));
                multipleBundleMap.put("LanguageShort", new AdaptedResourceBundleMap(ResourceBundle.getBundle("bundles/LanguageShortLevel", locale, new JsonResourceBundle.Control())));
                multipleBundleMap.put("Document", new AdaptedResourceBundleMap(ResourceBundle.getBundle("bundles/DocumentLabel", locale, new JsonResourceBundle.Control())));

                MultipleResourceBundleMap resources = new MultipleResourceBundleMap(multipleBundleMap);

                PreferencesResourceBundleMap preferences = new PreferencesResourceBundleMap(ResourceBundle.getBundle("preferences/CLDefaultPrintingPreferences", locale, new JsonResourceBundle.Control(ModelModule.bundleObjectMapper())));

                return new WithPreferencesContext(resources, preferences);
            }
            default: {
                Map<String, AdaptedResourceBundleMap> multipleBundleMap = new HashMap<>();
                multipleBundleMap.put("Document", new AdaptedResourceBundleMap(ResourceBundle.getBundle("bundles/DocumentLabel", locale, new JsonResourceBundle.Control())));
                multipleBundleMap.put("Listening", new AdaptedResourceBundleMap(ResourceBundle.getBundle("bundles/LanguageListeningLevel", locale, new JsonResourceBundle.Control())));
                multipleBundleMap.put("Reading", new AdaptedResourceBundleMap(ResourceBundle.getBundle("bundles/LanguageReadingLevel", locale, new JsonResourceBundle.Control())));
                multipleBundleMap.put("SpokenInteraction", new AdaptedResourceBundleMap(ResourceBundle.getBundle("bundles/LanguageSpokenInteractionLevel", locale, new JsonResourceBundle.Control())));
                multipleBundleMap.put("SpokenProduction", new AdaptedResourceBundleMap(ResourceBundle.getBundle("bundles/LanguageSpokenProductionLevel", locale, new JsonResourceBundle.Control())));
                multipleBundleMap.put("Writing", new AdaptedResourceBundleMap(ResourceBundle.getBundle("bundles/LanguageWritingLevel", locale, new JsonResourceBundle.Control())));
                multipleBundleMap.put("LanguageShort", new AdaptedResourceBundleMap(ResourceBundle.getBundle("bundles/LanguageShortLevel", locale, new JsonResourceBundle.Control())));
                multipleBundleMap.put("Information", new AdaptedResourceBundleMap(ResourceBundle.getBundle("bundles/IctInformationProcessingLevel", locale, new JsonResourceBundle.Control())));
                multipleBundleMap.put("Communication", new AdaptedResourceBundleMap(ResourceBundle.getBundle("bundles/IctCommunicationLevel", locale, new JsonResourceBundle.Control())));
                multipleBundleMap.put("ContentCreation", new AdaptedResourceBundleMap(ResourceBundle.getBundle("bundles/IctContentCreationLevel", locale, new JsonResourceBundle.Control())));
                multipleBundleMap.put("Safety", new AdaptedResourceBundleMap(ResourceBundle.getBundle("bundles/IctSafetyLevel", locale, new JsonResourceBundle.Control())));
                multipleBundleMap.put("ProblemSolving", new AdaptedResourceBundleMap(ResourceBundle.getBundle("bundles/IctProblemSolvingLevel", locale, new JsonResourceBundle.Control())));

                MultipleResourceBundleMap resources = new MultipleResourceBundleMap(multipleBundleMap);

                PreferencesResourceBundleMap preferences = new PreferencesResourceBundleMap(ResourceBundle.getBundle("preferences/CLDefaultPrintingPreferences", locale, new JsonResourceBundle.Control(ModelModule.bundleObjectMapper())));

                return new WithPreferencesContext(resources, preferences);
            }
        }
    }
}
