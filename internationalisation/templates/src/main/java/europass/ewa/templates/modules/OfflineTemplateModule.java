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
package europass.ewa.templates.modules;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;
import europass.ewa.enums.EuropassDocumentType;
import europass.ewa.templates.DocumentInfo;
import europass.ewa.templates.OfflineTemplateTranslator;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

/**
 *
 * @author at
 */
public class OfflineTemplateModule extends AbstractModule {

    public static final String OFFLINE_TEMPLATES_BASE_PARAM = "offline.templates.base.path";

    public static final String OFFLINE_TEMPLATES_EXPORT_PATH = "offline.templates.export.path";

    public static final String HTML_TO_ODT_XSLT = "europass-html-to-odt-xslt-for-odt-template";

    public static final String PUBLISH_TEMPLATES_TO_PORTAL = "offline.templates.publish";

    public static final String OO_CLIENT_SERVERS = "europass-ewa-services.oo.client.servers";

    Transformer transformer = null;

    @Override
    protected void configure() {
        bind(MustacheFactory.class).to(DefaultMustacheFactory.class);

        DocumentInfo cvInfo
                = new DocumentInfo(EuropassDocumentType.ECV, "odt/cv", "/documents/curriculum-vitae/templates-instructions/templates");
        DocumentInfo lpInfo
                = new DocumentInfo(EuropassDocumentType.ELP, "odt/elp", "/documents/language-passport/templates-instructions/templates");
        //DocumentInfo clInfo = 
        //		new DocumentInfo(EuropassDocumentType.ECL, "odt/ecl", "/documents/cover-letter/templates-instructions/templates");

        Multibinder<DocumentInfo> templateBinder
                = Multibinder.newSetBinder(binder(), DocumentInfo.class, Names.named(OFFLINE_TEMPLATES_BASE_PARAM));
        templateBinder.addBinding().toInstance(cvInfo);
        templateBinder.addBinding().toInstance(lpInfo);
        //templateBinder.addBinding().toInstance(clInfo);

        //--- Offline Template Specifics
        bind(OfflineTemplateTranslator.class);
    }

    @Provides
    @Singleton
    @Named(HTML_TO_ODT_XSLT)
    Transformer htmlTransformer() throws TransformerConfigurationException {
        if (transformer == null) {
            TransformerFactory factory = TransformerFactory.newInstance();
            Source xslt = new StreamSource(getClass().getResourceAsStream("/xslt2/html2odt.xsl"));
            transformer = factory.newTransformer(xslt);
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        }
        return transformer;
    }
}
