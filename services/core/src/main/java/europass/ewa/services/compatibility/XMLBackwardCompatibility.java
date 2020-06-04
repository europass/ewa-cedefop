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

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.google.common.base.Strings;
import com.google.inject.name.Named;

import europass.ewa.services.enums.XmlVersion;
import europass.ewa.services.exception.XMLCompatibilityException;
import europass.ewa.services.exception.XMLInvalidException;
import europass.ewa.services.exception.XMLInvalidFromWordException;
import europass.ewa.services.exception.XMLUndefinedException;
import europass.ewa.services.exception.XMLVersionException;

public class XMLBackwardCompatibility {

    private static final Logger LOG = LoggerFactory.getLogger(XMLBackwardCompatibility.class);

    private static final String ROOT_LEARNERINFO = "europass:learnerinfo";

    private static final String ROOT_SKILLS_PASSPORT = "SkillsPassport";

    private static final String ROOT_WORD_DOCUMENT = "w:wordDocument";

    private static final String ROOT_WORD_2010_DOCUMENT = "pkg:package";

    private static final String XML_VERSION_PATTERN = "V[0-9]\\.[0-9]";

    private final Map<XmlVersion, Transformer> xsltTransformers;

    @Inject
    public XMLBackwardCompatibility(
            @Named(Statics.XSL_TRANSFORMERS) Map<XmlVersion, Transformer> xsltTransformers) {

        this.xsltTransformers = xsltTransformers;
    }

    /**
     * Will return the given XML, unless the xml has a specific version which is
     * one of 1.0, 1.1, 1.2 or 2.0
     *
     * @param xml
     * @return
     * @throws XMLUndefinedException, which is not recoverable
     */
    public String transform(String xml) {
        if (Strings.isNullOrEmpty(xml)) {
            throw new XMLUndefinedException();
        }

        Document doc = getXmlDoc(xml);
        //The following will throws an exception while trying to find the version
        XmlVersion version = returnXmlVersion(doc);
        //The following throws an exception while trying to transform
        String finalXml = xml;
        boolean isPreviousVersion = false;
        switch (version) {
            case VERSION_1_0:
            case VERSION_1_1:
            case VERSION_1_2: {
                isPreviousVersion = true;
                finalXml = tranformToOtherVersion(doc, version);
                //and continue to v2.0 compatibility
            }
            case VERSION_2_0: {
                if (isPreviousVersion) {
                    doc = getXmlDoc(finalXml);
                }
                isPreviousVersion = true;
                finalXml = tranformToOtherVersion(doc, XmlVersion.VERSION_2_0);
                //and continue to v3.0 compatibility
            }
            case VERSION_3_0: {
                if (isPreviousVersion) {
                    doc = getXmlDoc(finalXml);
                }
                finalXml = tranformToOtherVersion(doc, XmlVersion.VERSION_3_0);
            }
            case VERSION_3_1: {
                if (isPreviousVersion) {
                    doc = getXmlDoc(finalXml);
                }
                finalXml = tranformToOtherVersion(doc, XmlVersion.VERSION_3_1);
                //break;
            }
            case VERSION_3_2: {
                if (isPreviousVersion) {
                    doc = getXmlDoc(finalXml);
                }
                finalXml = tranformToOtherVersion(doc, XmlVersion.VERSION_3_2);
                break;
            }
            case VERSION_3_3: {
                if (isPreviousVersion) {
                    doc = getXmlDoc(finalXml);
                }
                finalXml = tranformToOtherVersion(doc, XmlVersion.VERSION_3_3);
                break;
            }
            default: {
                //No transformation
            }
        }
        return finalXml;
    }

    /**
     * Cleans the xml from empty nodes.(EWA-1420 Empty XML tags are not parsed
     * correctly) Should always return the xml, even if it fails to clean the
     * empty nodes.
     *
     * @param xml
     * @return
     */
    public String transformCleanXml(String xml) {

        String finalXml = xml;

        Transformer transformer = xsltTransformers.get(XmlVersion.LATEST);
        if (transformer == null) {
            LOG.debug("XmlBackCompatibilityModule:xslTransformer - Failed to prepare a transformer for clean xml nodes.");
            return xml;
        }

        /*keep my manual Translator commented for a while
		 * //Transformer transformer = null;
		 * try {
			//Source xslt = new StreamSource( getClass().getResourceAsStream("/xslt/html2odt.xsl") );
			//transformer = factory.newTransformer(xslt);
			ClassLoader clsLoader = this.getClass().getClassLoader();
			transformer = TransformerFactory.newInstance().newTransformer( new StreamSource( clsLoader.getResource("xslt/clean-empty-nodes.xsl").toString() ) );
			transformer.setOutputProperty(OutputKeys.ENCODING, Constants.UTF8_ENCODING );
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		} catch (TransformerConfigurationException e) {
			LOG.debug("XmlBackCompatibilityModule:xslTransformer - Failed to prepare a transformer for clean xml nodes.", e);
			return xml;
		} catch (TransformerFactoryConfigurationError e) {
			LOG.debug("XmlBackCompatibilityModule:xslTransformer - Failed to prepare a transformer for clean xml nodes.", e);
			return xml;
		}*/
        Reader reader = null;
        try {
            StringWriter stringWriter = new StringWriter();
            StreamResult streamResult = new StreamResult(stringWriter);
            Document doc = getXmlDoc(xml);
            // Perform the transformation.
            transformer.transform(new DOMSource(doc), streamResult);
            finalXml = stringWriter.toString();
        } catch (final Exception e) {
            LOG.debug("XmlBackCompatibilityModule:xslTransformer - Failed to transform for clean xml nodes.", e);
            return xml;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    return xml;
                }
            }
        }
        return finalXml;
    }

    /**
     * Returns the xml version based on the XML as string.
     *
     * @param xml
     * @return
     * @throws XMLVersionException, which is not recoverable
     */
    private static XmlVersion returnXmlVersion(Document doc) {

        XmlVersion version = XmlVersion.UNKNOWN;

        try {
            NodeList nodes = doc.getElementsByTagName("xsdversion");
            if (nodes.getLength() == 0) {
                nodes = doc.getElementsByTagName("XSDVersion");
            }
            //no valid xsdversion element
            if (nodes != null && nodes.getLength() > 0) {

                Node versionNode = nodes.item(0);
                NodeList versionContents = versionNode.getChildNodes();

                if (versionContents != null && versionContents.getLength() > 0) {
                    String versionStr = versionContents.item(0).getNodeValue();
                    version = patternCompatible(versionStr);
                }
            }
        } catch (final Exception e) {
            throw new XMLVersionException();
        }

        //Try fallback if still no luck
        if (XmlVersion.UNKNOWN.equals(version)) {
            Element rootElement = doc.getDocumentElement();

            if (rootElement == null) {
                throw new XMLVersionException();
            }

            String rootName = rootElement.getNodeName();

            switch (rootName) {
                case ROOT_LEARNERINFO: {
                    return XmlVersion.VERSION_2_0;
                }
                case ROOT_SKILLS_PASSPORT: {
                    return XmlVersion.LATEST;
                }
                case ROOT_WORD_DOCUMENT:
                case ROOT_WORD_2010_DOCUMENT: {
                    throw new XMLInvalidFromWordException();
                }
                default: {
                    throw new XMLVersionException();
                }
            }
        }
        //otherwise return the found version
        return version;
    }

    private static XmlVersion patternCompatible(String version) {
        if (Strings.isNullOrEmpty(version)
                || (version != null && !version.matches(XML_VERSION_PATTERN))) {

            return XmlVersion.UNKNOWN;
        }
        return XmlVersion.match(version);
    }

    /**
     * Transforms an xml of a specific version to another of a different
     * version.
     *
     * @param xml
     * @param xsltFilename
     * @return
     * @throws XMLCompatibilityException, which is not recoverable
     */
    private String tranformToOtherVersion(Document doc, XmlVersion xmlVersion) {
        Transformer transformer = xsltTransformers.get(xmlVersion);
        if (transformer == null) {
            throw new XMLCompatibilityException("The transformer for version '" + xmlVersion.getCode() + "' is not properly loaded");
        }

        Reader reader = null;
        try {
            StringWriter stringWriter = new StringWriter();
            StreamResult streamResult = new StreamResult(stringWriter);

            // Perform the transformation.
            transformer.transform(new DOMSource(doc), streamResult);
            return stringWriter.toString();

        } catch (final Exception e) {
            throw new XMLCompatibilityException(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }

    }

    /**
     * Utility to re-use the Document
     *
     * @param xml
     * @return
     */
    private Document getXmlDoc(String xml) {

        Document doc = null;
        Reader reader = null;
        try {
            DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();

            /**
             * EWA-1437: Fix XML External Entity (XXE) security issue -
             * Disallowing doctype, external entites & parameters elements in
             * xml body
             *
             * - see
             * https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Processing
             */
            // Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-general-entities
            // Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-general-entities
            String FEATURE = "http://xml.org/sax/features/external-general-entities";
            dbfac.setFeature(FEATURE, false);

            // Xerces 1 - http://xerces.apache.org/xerces-j/features.html#external-parameter-entities
            // Xerces 2 - http://xerces.apache.org/xerces2-j/features.html#external-parameter-entities
            FEATURE = "http://xml.org/sax/features/external-parameter-entities";
            dbfac.setFeature(FEATURE, false);

            // Xerces 2 only - http://xerces.apache.org/xerces2-j/features.html#disallow-doctype-decl
            FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
            dbfac.setFeature(FEATURE, true);

            DocumentBuilder docBuilder = dbfac.newDocumentBuilder();

            reader = new StringReader(xml);
            doc = docBuilder.parse(new InputSource(reader));

            return doc;

        } catch (Exception e) {

//			if ( e instanceof ParserConfigurationException )
            throw new XMLInvalidException(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                }
            }
        }
    }

}
