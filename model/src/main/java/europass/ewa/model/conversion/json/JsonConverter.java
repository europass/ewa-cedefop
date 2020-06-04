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
package europass.ewa.model.conversion.json;

import java.io.IOException;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import europass.ewa.model.SkillsPassport;
import europass.ewa.model.conversion.Converter;
import europass.ewa.model.conversion.exception.JsonToPojoException;
import europass.ewa.model.conversion.exception.PojoToJsonException;
import europass.ewa.model.conversion.exception.PojoToXmlException;

public class JsonConverter implements Converter<SkillsPassport> {

    private static final Logger LOG = LoggerFactory.getLogger(JsonConverter.class);

    private final ObjectMapper mapper;
    private final XmlMapper xmlMapper;

    @Inject
    public JsonConverter(ObjectMapper mapper, XmlMapper xmlMapper) {
        this.mapper = mapper;
        this.xmlMapper = xmlMapper;
    }

    /**
     * Converts a JSON string to the SkillsPassport POJO
     *
     * @param String JSON
     * @return SkillsPassport
     * @throws JsonToPojoException
     */
    @Override
    public SkillsPassport load(String source) {
        // Source is a JSON string
        try {
            //pgia: fix for EWA-1461
            source = source.replaceAll("<plus>", "+");
            source = source.replaceAll("<percentage>", "%");
            return mapper.readValue(source, SkillsPassport.class);
        } catch (final JsonMappingException | JsonParseException e) {
            LOG.error("JsonConverter:load - Failed to load POJO from source.", e);
            LOG.error("JsonConverter:load - Offending JSON: " + source);
            throw new JsonToPojoException(e);
        } catch (final IOException e) {
            LOG.error("JsonConverter:load - Failed to load POJO from source.", e);
            LOG.error("JsonConverter:load - Offending JSON: " + source);
            throw new JsonToPojoException(e);
        }
    }

    /**
     * Converts a SkillsPassport POJO to JSON string
     *
     * @param SkillsPassport
     * @return String JSON
     * @throws PojoToJsonException
     */
    @Override
    public String write(SkillsPassport object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (final JsonMappingException | JsonParseException e) {
            LOG.error(" JsonConverter:write - Failed to write POJO as JSON string.", e);
            throw new PojoToJsonException(e);
        } catch (final IOException e) {
            LOG.error(" JsonConverter:write - Failed to write POJO as JSON string.", e);
            throw new PojoToJsonException(e);
        }
    }

    /**
     * Converts a JSON string to a XML string
     *
     * @param String JSON
     * @return String XML
     * @throws PojoToXmlException
     */
    @Override
    public String convert(String source) {
        SkillsPassport esp = this.load(source);
        try {
            return xmlMapper.writeValueAsString(esp);
        } catch (final JsonMappingException | JsonParseException e) {
            LOG.error("JsonConverter:convert - Failed to convert JSON to XML .", e);
            LOG.error("JsonConverter:convert - Offending JSON: " + source);
            throw new PojoToXmlException(e);
        } catch (final IOException e) {
            LOG.error("JsonConverter:convert - Failed to convert JSON to XML .", e);
            LOG.error("JsonConverter:convert - Offending JSON: " + source);
            throw new PojoToXmlException(e);
        }
    }

    @Override
    public boolean validate(String source) {
        throw new UnsupportedOperationException("Json Validation not yet supported");
    }

}
