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
import europass.ewa.enums.ConversionFileType;
import europass.ewa.enums.EuropassDocumentType;
import europass.ewa.oo.client.OfficeClient;
import europass.ewa.resources.JsonResourceBundle;
import europass.ewa.templates.modules.ConfigModule;
import europass.ewa.templates.modules.OfflineTemplateModule;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.zip.ZipOutputStream;
import javax.inject.Inject;
import javax.xml.bind.JAXBException;
import javax.xml.transform.Transformer;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author at
 */
public class OfflineTemplateTranslator extends TemplateTranslator {

    private static final Logger LOG = LoggerFactory.getLogger(OfflineTemplateTranslator.class);

    private static final int BUFFER_SIZE = 1024;

    private final String exportPath;
    private String exportOdtPath;
    private String exportDocPath;

    private final Set<DocumentInfo> documents;

    private final ODTMustacheFactory factory;

    private final Transformer htmlTransformer;

    private final OfficeClient officeClient;

    private final boolean publishable;

    @Inject
    public OfflineTemplateTranslator(
            ODTMustacheFactory factory,
            @Named(ConfigModule.ZANATA_LOCALES_URL) String zanataLocalesUrl,
            @Named(OfflineTemplateModule.OFFLINE_TEMPLATES_BASE_PARAM) Set<DocumentInfo> documents,
            @Named(OfflineTemplateModule.OFFLINE_TEMPLATES_EXPORT_PATH) String exportPath,
            @Named(OfflineTemplateModule.HTML_TO_ODT_XSLT) Transformer htmlTransformer,
            @Named(OfflineTemplateModule.PUBLISH_TEMPLATES_TO_PORTAL) boolean publishable,
            OfficeClient officeClient) {

        super(zanataLocalesUrl);

        this.factory = factory;
        this.documents = documents;
        this.exportPath = exportPath;
        this.htmlTransformer = htmlTransformer;
        this.publishable = publishable;
        this.officeClient = officeClient;
        this.prepareVariables();
    }

    private void prepareVariables() {
        this.exportOdtPath = this.exportPath + File.separator + "ODT";
        new File(this.exportOdtPath).mkdir();
        this.exportDocPath = this.exportPath + File.separator + "DOC";
        new File(this.exportDocPath).mkdir();
    }

    @Override
    public void translate(Locale locale) throws IOException, JAXBException,
            URISyntaxException {

        // Run translate for all templates (cv and esp)
        for (DocumentInfo document : documents) {
            EuropassDocumentType docType = document.getType();
            String documentType = docType.getAcronym();

            LOG.info("=============  " + documentType + "  ==============");
            String srcTemplate = document.getOdtPath();
            LOG.debug("Extract OFFLINE Template (" + srcTemplate + ") in " + locale.toString());

            ODTMustache template = factory.create(srcTemplate + "/template", false);

            Object context = this.getContext(docType, locale);

            String localisedOdt = this.exportOdtPath + File.separator + documentType + "_Template_" + locale.getLanguage() + ".odt";
            String localisedDoc = this.exportDocPath + File.separator + documentType + "_Template_" + locale.getLanguage() + ".doc";

            FileOutputStream out = new FileOutputStream(localisedOdt);

            ZipOutputStream zoutOdt = new ZipOutputStream(out);
            template.execute(zoutOdt, context);
            zoutOdt.flush();
            zoutOdt.close();

            LOG.info("------------ ODT Generation is complete ------------");

            if (publishable) {
                uploadToPortal(localisedOdt, locale, documentType, "");
                LOG.info("------------ ODT templates have been uploaded to portal ------------");
            }

            try {
                FileInputStream fis = new FileInputStream(new File(localisedOdt));
                OutputStream zoutWord = officeClient.startConvert();

                IOUtils.copy(fis, zoutWord);

                InputStream in = officeClient.endConvert(zoutWord, ConversionFileType.WORD_DOC);
                OutputStream outDoc = new FileOutputStream(localisedDoc);
                byte buf[] = new byte[BUFFER_SIZE];
                int len;
                while ((len = in.read(buf)) > 0) {
                    outDoc.write(buf, 0, len);
                }
                outDoc.close();
                in.close();

                LOG.info("---------- DOC Generation is complete ------------");

                fis.close();
                zoutWord.flush();
                zoutWord.close();

                if (publishable) {
                    uploadToPortal(localisedDoc, locale, documentType, "");
                    LOG.info("---------- DOC templates have been uploaded to portal ---------");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (publishable) {
                //this.decache();
                //LOG.info("--------QCMS has been de-cached-----------");
            }
        }
    }

    public void uploadToPortal(String fileURL, Locale locale, String documentType, String portalPath) {
        //TODO
        //note: need to decache after uploading new?
    }

    @Override
    TemplateContext getContext(EuropassDocumentType docType, Locale locale) {

        if (EuropassDocumentType.ELP.equals(docType)) {

            Map<String, AdaptedResourceBundleMap> multipleBundleMap = new HashMap<>();

            multipleBundleMap.put("Listening", new AdaptedResourceBundleMap(ResourceBundle.getBundle("bundles/LanguageListeningLevel", locale, new JsonResourceBundle.Control())));
            multipleBundleMap.put("Reading", new AdaptedResourceBundleMap(ResourceBundle.getBundle("bundles/LanguageReadingLevel", locale, new JsonResourceBundle.Control())));
            multipleBundleMap.put("SpokenInteraction", new AdaptedResourceBundleMap(ResourceBundle.getBundle("bundles/LanguageSpokenInteractionLevel", locale, new JsonResourceBundle.Control())));
            multipleBundleMap.put("SpokenProduction", new AdaptedResourceBundleMap(ResourceBundle.getBundle("bundles/LanguageSpokenProductionLevel", locale, new JsonResourceBundle.Control())));
            multipleBundleMap.put("Writing", new AdaptedResourceBundleMap(ResourceBundle.getBundle("bundles/LanguageWritingLevel", locale, new JsonResourceBundle.Control())));
            multipleBundleMap.put("LanguageShort", new AdaptedResourceBundleMap(ResourceBundle.getBundle("bundles/LanguageShortLevel", locale, new JsonResourceBundle.Control())));
            multipleBundleMap.put("Document", new AdaptedResourceBundleMap(ResourceBundle.getBundle("bundles/DocumentLabel", locale, new JsonResourceBundle.Control())));
            multipleBundleMap.put("Template", new AdaptedResourceBundleMap(ResourceBundle.getBundle("bundles/TemplatePlaceholder", locale, new JsonResourceBundle.Control())));

            return new MultipleResourceBundleMap(multipleBundleMap);

        } else {
            ResourceBundle labels = ResourceBundle.getBundle("bundles.DocumentLabel", locale, new JsonResourceBundle.Control());

            ResourceBundle placeholders = ResourceBundle.getBundle("bundles.TemplatePlaceholder", locale, new JsonResourceBundle.Control());

            return new AdaptedDoubleResourceBundleMap(labels, placeholders, this.htmlTransformer);
        }
    }
}
