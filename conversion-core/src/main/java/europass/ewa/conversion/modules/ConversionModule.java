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
package europass.ewa.conversion.modules;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.MustacheFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import europass.ewa.conversion.odt.CVODTGenerator;
import europass.ewa.conversion.odt.ECL;
import europass.ewa.conversion.odt.ECLODTGenerator;
import europass.ewa.conversion.odt.ESPODTGenerator;
import europass.ewa.conversion.odt.ELP;
import europass.ewa.conversion.odt.ELPODTGenerator;
import europass.ewa.conversion.odt.ODTGenerator;
import europass.ewa.model.CV;
import europass.ewa.model.ESP;

public class ConversionModule extends AbstractModule {

    public static final String ODT_BASE_PATH_CV = "europass-ewa-services.conversion.odt.base.path.cv";

    public static final String ODT_BASE_PATH_ESP = "europass-ewa-services.conversion.odt.base.path.esp";

    public static final String ODT_BASE_PATH_ELP = "europass-ewa-services.conversion.odt.base.path.elp";

    public static final String ODT_BASE_PATH_ECL = "europass-ewa-services.conversion.odt.base.path.ecl";

    public static final String HTML_TO_ODT_XSLT = "europass-html-to-odt-xslt";

    @Override
    protected void configure() {

        bind(MustacheFactory.class).to(DefaultMustacheFactory.class);

        //CV, ESP and ELP and ECL Generators
        bind(ODTGenerator.class).annotatedWith(CV.class).to(CVODTGenerator.class);
        bind(ODTGenerator.class).annotatedWith(ESP.class).to(ESPODTGenerator.class);
        bind(ODTGenerator.class).annotatedWith(ELP.class).to(ELPODTGenerator.class);
        bind(ODTGenerator.class).annotatedWith(ECL.class).to(ECLODTGenerator.class);
    }

    @Provides
    @Singleton
    @Named(HTML_TO_ODT_XSLT)
    Transformer htmlTransformer() throws TransformerConfigurationException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Source xslt = new StreamSource(getClass().getResourceAsStream("/xslt/html2odt.xsl"));

        Transformer transformer = factory.newTransformer(xslt);
        transformer.setParameter("encoding", "UTF-8");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        return transformer;
    }
}
