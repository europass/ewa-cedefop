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
package europass.ewa.model.conversion.xml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import europass.ewa.model.PrintingPreference;
import europass.ewa.model.SkillsPassport;
import europass.ewa.model.conversion.Converter;
import europass.ewa.model.conversion.exception.PojoToJsonException;
import europass.ewa.model.conversion.exception.PojoToXmlException;
import europass.ewa.model.conversion.exception.XmlToPojoException;

public class XmlConverter implements Converter<SkillsPassport> {

    private static final Logger LOG = LoggerFactory.getLogger(XmlConverter.class);

    private final ObjectMapper mapper;
    private final XmlMapper xmlMapper;

    @Inject
    public XmlConverter(ObjectMapper mapper, XmlMapper xmlMapper) {
        this.mapper = mapper;
        this.xmlMapper = xmlMapper;
    }

    /**
     * Converts a XML string to the SkillsPassport POJO
     *
     * @param String XML
     * @return SkillsPassport
     * @throws XmlToPojoException
     */
    @Override
    public SkillsPassport load(String source) {
        try {
            return xmlMapper.readValue(source, SkillsPassport.class);
        } catch (final JsonMappingException | JsonParseException e) {
            LOG.error(" XMLConverter:load - Failed to load POJO from source.", e);
            throw new XmlToPojoException(e);
        } catch (final IOException e) {
            LOG.error(" XMLConverter:load - Failed to load POJO from source.", e);
            throw new XmlToPojoException(e);
        }
    }

    /**
     * Converts a SkillsPassport POJO to XML string
     *
     * @param SkillsPassport
     * @return String XML
     * @throws PojoToXmlException
     */
    @Override
    public String write(SkillsPassport object) {
        try {

            //if the xml version is 3.2 apply the elimination of unused preferences for each document
            if (object.getDocumentInfo().getXsdversion().startsWith("V3.3")
                    || object.getDocumentInfo().getXsdversion().startsWith("V3.4")) {

                Map<String, List<PrintingPreference>> documentPrintingPrefs = object.getDocumentPrintingPrefs();

                Map<String, List<PrintingPreference>> updatedDocumentPrintingPrefs = new HashMap<String, List<PrintingPreference>>();

                for (String docTypeKey : documentPrintingPrefs.keySet()) {

                    ArrayList<PrintingPreference> currentPrefs = (ArrayList<PrintingPreference>) documentPrintingPrefs.get(docTypeKey);

                    ArrayList<PrintingPreference> updatedPrefs = new ArrayList<PrintingPreference>();

                    for (PrintingPreference pref : currentPrefs) {

                        if (!pref.getName().equals("LearnerInfo.CEFLanguageLevelsGrid")
                                && !pref.getName().equals("CoverLetter.Justification")
                                && !pref.getName().equals("CoverLetter.SignatureName")) {

                            boolean eliminatePreference
                                    = (pref.getFormat() == null && pref.getOrder() == null && pref.getPosition() == null)
                                    || pref.getName().toString().endsWith("ContactInfo.Address");

                            if (!eliminatePreference) {
                                updatedPrefs.add(pref);
                            }
                        } else {
                            updatedPrefs.add(pref);
                        }
                    }

                    updatedDocumentPrintingPrefs.put(docTypeKey, updatedPrefs);
                }

                object.setDocumentPrintingPrefs(updatedDocumentPrintingPrefs);
            }

            return xmlMapper.writeValueAsString(object);
        } catch (final JsonMappingException | JsonParseException e) {
            LOG.error(" XMLConverter:write - Failed to write POJO as XML string.", e);
            throw new PojoToXmlException(e);
        } catch (final IOException e) {
            LOG.error(" XMLConverter:write - Failed to write POJO as XML string.", e);
            throw new PojoToXmlException(e);
        }
    }

    /**
     * Converts a XML string to a JSON string
     *
     * @param String XML
     * @return String JSON
     * @throws XmlToPojoException, PojoToJsonException
     */
    @Override
    public String convert(String source) {
        SkillsPassport esp = this.load(source);
        try {
            return mapper.writeValueAsString(esp);
        } catch (final JsonMappingException | JsonParseException e) {
            LOG.error(" XMLConverter:convert - Failed to convert XML to JSON.", e);
            throw new PojoToJsonException(e);
        } catch (final IOException e) {
            LOG.error(" XMLConverter:convert - Failed to convert XML to JSON.", e);
            throw new PojoToJsonException(e);
        }
    }

    @Override
    public boolean validate(String source) {
        throw new UnsupportedOperationException("Xml Validation not yet supported");
    }

}
