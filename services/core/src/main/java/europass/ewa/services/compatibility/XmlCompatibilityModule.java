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
package europass.ewa.services.compatibility;

import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.google.inject.name.Names;

import europass.ewa.Constants;
import europass.ewa.services.enums.XmlVersion;

public class XmlCompatibilityModule extends AbstractModule {

    private static final Logger LOG = LoggerFactory.getLogger(XmlCompatibilityModule.class);

    private final ClassLoader clsLoader;

    private Map<XmlVersion, Transformer> xsltTransformers = null;

    public XmlCompatibilityModule() {
        clsLoader = this.getClass().getClassLoader();
    }

    @Override
    protected void configure() {
        // --- XML Compatibility (transforms xml from previous xsd versions to v3.0 ---
        bind(XMLBackwardCompatibility.class).asEagerSingleton();

        //-- XSLTs
        bindConstant().annotatedWith(
                Names.named(Statics.XSL_1_0_TO_2_0)).to(
                clsLoader.getResource("xslt/europass-cv-v1.0-to-v2.0.xsl")
                        .toString());
        bindConstant().annotatedWith(
                Names.named(Statics.XSL_1_1_TO_2_0)).to(
                clsLoader.getResource("xslt/europass-cv-v1.1-to-v2.0.xsl")
                        .toString());
        bindConstant().annotatedWith(
                Names.named(Statics.XSL_1_2_TO_2_0)).to(
                clsLoader.getResource("xslt/europass-cv-v1.2-to-v2.0.xsl")
                        .toString());
        bindConstant().annotatedWith(
                Names.named(Statics.XSL_2_0_TO_3_0)).to(
                clsLoader.getResource("xslt/europass-cv-v2.0-to-v3.0.xsl")
                        .toString());
        bindConstant().annotatedWith(
                Names.named(Statics.XSL_3_0_TO_3_1)).to(
                clsLoader.getResource("xslt/europass-cv-v3.0-to-v3.1.xsl")
                        .toString());
        bindConstant().annotatedWith(
                Names.named(Statics.XSL_3_1_TO_3_2)).to(
                clsLoader.getResource("xslt/europass-cv-v3.1-to-v3.2.xsl")
                        .toString());
        bindConstant().annotatedWith(
                Names.named(Statics.XSL_3_2_TO_3_3)).to(
                clsLoader.getResource("xslt/europass-cv-v3.2-to-v3.3.xsl")
                        .toString());

        bindConstant().annotatedWith(
                Names.named(Statics.XSL_3_3_TO_3_4)).to(
                clsLoader.getResource("xslt/europass-cv-v3.3-to-v3.4.xsl")
                        .toString());

        //xml clean
        bindConstant().annotatedWith(
                Names.named(Statics.XSL_CLEAN_EMPTY_NODES)).to(
                clsLoader.getResource("xslt/clean-empty-nodes.xsl")
                        .toString());

    }

    //-----------------------------------------------------------------------------------
    //---------- XSL TRANSFORMERS -------------------------------------------------------
    @Provides
    @Named(Statics.XSL_V1_0_TO_2_0_TRANSFORMER)
    Transformer xsdV10Transformer(@Named(Statics.XSL_TRANSFORMERS) Map<XmlVersion, Transformer> xsltTransformers) {
        return xsltTransformers.get(XmlVersion.VERSION_1_0);
    }

    @Provides
    @Named(Statics.XSL_V1_1_TO_2_0_TRANSFORMER)
    Transformer xsdV11Transformer(@Named(Statics.XSL_TRANSFORMERS) Map<XmlVersion, Transformer> xsltTransformers) {
        return xsltTransformers.get(XmlVersion.VERSION_1_1);
    }

    @Provides
    @Named(Statics.XSL_V1_2_TO_2_0_TRANSFORMER)
    Transformer xsdV12Transformer(@Named(Statics.XSL_TRANSFORMERS) Map<XmlVersion, Transformer> xsltTransformers) {
        return xsltTransformers.get(XmlVersion.VERSION_1_2);
    }

    @Provides
    @Named(Statics.XSL_2_0_TO_3_0)
    Transformer xsdV20Transformer(@Named(Statics.XSL_TRANSFORMERS) Map<XmlVersion, Transformer> xsltTransformers) {
        return xsltTransformers.get(XmlVersion.VERSION_2_0);
    }

    @Provides
    @Named(Statics.XSL_3_0_TO_3_1)
    Transformer xsdV30Transformer(@Named(Statics.XSL_TRANSFORMERS) Map<XmlVersion, Transformer> xsltTransformers) {
        return xsltTransformers.get(XmlVersion.VERSION_3_0);
    }

    @Provides
    @Named(Statics.XSL_3_1_TO_3_2)
    Transformer xsdV31Transformer(@Named(Statics.XSL_TRANSFORMERS) Map<XmlVersion, Transformer> xsltTransformers) {
        return xsltTransformers.get(XmlVersion.VERSION_3_1);
    }

    @Provides
    @Named(Statics.XSL_3_2_TO_3_3)
    Transformer xsdV32Transformer(@Named(Statics.XSL_TRANSFORMERS) Map<XmlVersion, Transformer> xsltTransformers) {
        return xsltTransformers.get(XmlVersion.VERSION_3_2);
    }

    @Provides
    @Named(Statics.XSL_3_3_TO_3_4)
    Transformer xsdV33Transformer(@Named(Statics.XSL_TRANSFORMERS) Map<XmlVersion, Transformer> xsltTransformers) {
        return xsltTransformers.get(XmlVersion.VERSION_3_3);
    }

    //xml -clean
    @Provides
    @Named(Statics.XSL_CLEAN_EMPTY_NODES)
    Transformer xsdCleanTransformer(@Named(Statics.XSL_TRANSFORMERS) Map<XmlVersion, Transformer> xsltTransformers) {
        return xsltTransformers.get(XmlVersion.LATEST);
    }

    @Provides
    @Named(Statics.XSL_TRANSFORMERS)
    Map<XmlVersion, Transformer> xsdTransformers(
            @Named(Statics.XSL_1_0_TO_2_0) String xslVersion1_0_to_2_0,
            @Named(Statics.XSL_1_1_TO_2_0) String xslVersion1_1_to_2_0,
            @Named(Statics.XSL_1_2_TO_2_0) String xslVersion1_2_to_2_0,
            @Named(Statics.XSL_2_0_TO_3_0) String xslVersion2_0_to_3_0,
            @Named(Statics.XSL_3_0_TO_3_1) String xslVersion3_0_to_3_1,
            @Named(Statics.XSL_3_1_TO_3_2) String xslVersion3_1_to_3_2,
            @Named(Statics.XSL_3_2_TO_3_3) String xslVersion3_2_to_3_3,
            @Named(Statics.XSL_3_3_TO_3_4) String xslVersion3_3_to_3_4,
            @Named(Statics.XSL_CLEAN_EMPTY_NODES) String xslCleaner) {

        if (xsltTransformers == null) {
            xsltTransformers = new HashMap<XmlVersion, Transformer>();
            //V1.0 to V2.0
            Transformer xsltVersion1_0 = xslTransformer(xslVersion1_0_to_2_0);
            if (xsltVersion1_0 != null) {
                xsltTransformers.put(XmlVersion.VERSION_1_0, xsltVersion1_0);
            }
            //V1.1 to V2.0
            Transformer xsltVersion1_1 = xslTransformer(xslVersion1_1_to_2_0);
            if (xsltVersion1_1 != null) {
                xsltTransformers.put(XmlVersion.VERSION_1_1, xsltVersion1_1);
            }
            //V1.2 to V2.0
            Transformer xsltVersion1_2 = xslTransformer(xslVersion1_2_to_2_0);
            if (xsltVersion1_2 != null) {
                xsltTransformers.put(XmlVersion.VERSION_1_2, xsltVersion1_2);
            }
            //V2.0 to V3.0
            Transformer xsltVersion2_0 = xslTransformer(xslVersion2_0_to_3_0);
            if (xsltVersion2_0 != null) {
                xsltTransformers.put(XmlVersion.VERSION_2_0, xsltVersion2_0);
            }
            //V3.0 to V3.1
            Transformer xsltVersion3_0 = xslTransformer(xslVersion3_0_to_3_1);
            if (xsltVersion3_0 != null) {
                xsltTransformers.put(XmlVersion.VERSION_3_0, xsltVersion3_0);
            }
            //V3.1 to V3.2
            Transformer xsltVersion3_1 = xslTransformer(xslVersion3_1_to_3_2);
            if (xsltVersion3_1 != null) {
                xsltTransformers.put(XmlVersion.VERSION_3_1, xsltVersion3_1);
            }
            //V3.2 to V3.3
            Transformer xsltVersion3_2 = xslTransformer(xslVersion3_2_to_3_3);
            if (xsltVersion3_2 != null) {
                xsltTransformers.put(XmlVersion.VERSION_3_2, xsltVersion3_2);
            }
            //V3.3 to V3.4
            Transformer xsltVersion3_3 = xslTransformer(xslVersion3_3_to_3_4);
            if (xsltVersion3_3 != null) {
                xsltTransformers.put(XmlVersion.VERSION_3_3, xsltVersion3_3);
            }

            //clean xml nodes
            Transformer xsltCleanTransformer = xslTransformer(xslCleaner);
            if (xsltCleanTransformer != null) {
                xsltTransformers.put(XmlVersion.LATEST, xsltCleanTransformer);
            }

        }
        return xsltTransformers;

    }

    /**
     * Prepare a Transformer for the specific XSLT file.
     *
     * @param xsdURL
     * @return
     */
    private Transformer xslTransformer(String xsltFilePath) {
        if (Strings.isNullOrEmpty(xsltFilePath)) {
            return null;
        }
        try {
            Transformer transformer = TransformerFactory.newInstance()
                    .newTransformer(new StreamSource(xsltFilePath));

            transformer.setOutputProperty(OutputKeys.ENCODING, Constants.UTF8_ENCODING);
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");

            return transformer;

        } catch (TransformerConfigurationException e) {
            LOG.debug("XmlCompatibilityModule:xslTransformer - Failed to prepare a transformer.", e);
            return null;
        } catch (TransformerFactoryConfigurationError e) {
            LOG.debug("XmlCompatibilityModule:xslTransformer - Failed to prepare a transformer.", e);
            return null;
        }
    }

//	/**
//	 * Prepare a Validator for the specific XSD url.
//	 * 
//	 * @param xsdURL
//	 * @return
//	 */
//	private Validator xsdValidator( String xsdURL){
//		if ( Strings.isNullOrEmpty(xsdURL) ){
//			return null;
//		}
//		try {
//			URL schemaFile = new URL( xsdURL );
//			
//			SchemaFactory schemaFactory = SchemaFactory
//					.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
//			
//			Schema schema = schemaFactory.newSchema(schemaFile);
//			
//			return schema.newValidator();
//			
//		} catch ( MalformedURLException ue){
//			LOG.debug("XmlCompatibilityModule:xsdValidator - The URL of the XSD is not valid " + xsdURL, ue);
//			return null;
//		}
//		 catch (SAXException se) {
//			LOG.debug("XmlCompatibilityModule:xsdValidator - Failed to locate the XSD under the url " + xsdURL, se);
//			return null;
//		}
//
//	}
}
