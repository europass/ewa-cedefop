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
import europass.ewa.model.conversion.ModelModule;
import europass.ewa.resources.JsonResourceBundle;
import europass.ewa.templates.modules.ConfigModule;
import europass.ewa.templates.modules.HbsTemplateModule;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import javax.inject.Inject;
import javax.xml.bind.JAXBException;

/**
 *
 * @author at
 */
public class HbsTemplateTranslator extends TemplateTranslator {

    private final HbsMustacheFactory factory;
    private final String editorsBaseDir = Paths.get("").toAbsolutePath().getParent().toAbsolutePath().getParent().toString();
    private final Set<String> hbsTemplatesBaseDirs;
    private final String hbsTemplatesExportPath;

    @Inject
    public HbsTemplateTranslator(
            HbsMustacheFactory factory,
            @Named(ConfigModule.ZANATA_LOCALES_URL) String zanataLocalesUrl,
            @Named(HbsTemplateModule.HBS_TEMPLATES_DIRS) Set<String> hbsTemplatesBaseDirs,
            @Named(ConfigModule.HBS_TEMPLATES_EXPORT_PATH) String hbsTemplatesExportPath) {

        super(zanataLocalesUrl);

        this.factory = factory;
        this.hbsTemplatesBaseDirs = hbsTemplatesBaseDirs;
        this.hbsTemplatesExportPath = hbsTemplatesExportPath;
    }

    /**
     *
     * @param locale
     *
     * @throws URISyntaxException
     * @throws java.io.IOException
     * @throws javax.xml.bind.JAXBException
     */
    @Override
    public void translate(Locale locale) throws URISyntaxException, IOException, JAXBException {

        for (String baseDir : hbsTemplatesBaseDirs) {
            String exportPath = editorsBaseDir + hbsTemplatesExportPath + File.separator + locale.getLanguage();

            HbsMustache template = factory.create(exportPath, baseDir, false);
            Object context = this.getContext(EuropassDocumentType.UNKNOWN, locale);

            template.execute(context);
        }

    }

    @Override
    TemplateContext getContext(EuropassDocumentType docType, Locale locale) {
        Map<String, AdaptedResourceBundleMap> multipleBundleMap = new HashMap<>();
        multipleBundleMap.put("Document", new AdaptedResourceBundleMap(ResourceBundle.getBundle("bundles/DocumentLabel", locale, new JsonResourceBundle.Control())));
        multipleBundleMap.put("EditorPlaceholder", new AdaptedResourceBundleMap(ResourceBundle.getBundle("bundles/EditorPlaceholder", locale, new JsonResourceBundle.Control())));
        multipleBundleMap.put("EditorHelp", new AdaptedResourceBundleMap(ResourceBundle.getBundle("bundles/EditorHelp", locale, new JsonResourceBundle.Control())));
        multipleBundleMap.put("GuiLabel", new AdaptedResourceBundleMap(ResourceBundle.getBundle("bundles/GuiLabel", locale, new JsonResourceBundle.Control())));
        multipleBundleMap.put("EnclosedLabel", new AdaptedResourceBundleMap(ResourceBundle.getBundle("bundles/EnclosedLabel", locale, new JsonResourceBundle.Control())));
        multipleBundleMap.put("DocumentCustomizations", new AdaptedResourceBundleMap(ResourceBundle.getBundle("bundles/DocumentCustomizations", locale, new JsonResourceBundle.Control())));
        multipleBundleMap.put("CLExtraPreferences", new AdaptedResourceBundleMap(ResourceBundle.getBundle("preferences/CLExtraPreferences", locale, new JsonResourceBundle.Control())));
        MultipleResourceBundleMap resources = new MultipleResourceBundleMap(multipleBundleMap);

        PreferencesResourceBundleMap preferences = new PreferencesResourceBundleMap(ResourceBundle.getBundle("preferences/CLDefaultPrintingPreferences", locale, new JsonResourceBundle.Control(ModelModule.bundleObjectMapper())));

        return new WithPreferencesContext(resources, preferences);
    }

}
