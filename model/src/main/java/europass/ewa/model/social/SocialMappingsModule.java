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
package europass.ewa.model.social;

import java.io.IOException;

import javax.inject.Named;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import europass.ewa.Utils;
import europass.ewa.model.conversion.xml.CustomWstxInputFactory;
import europass.ewa.model.conversion.xml.CustomWstxOutputFactory;

public class SocialMappingsModule extends AbstractModule {

    public static final String SOCIAL_MAPPING_XML_MAPPER = "social.mapping.xmlmapper";

    public static final String SOCIAL_MAPPING_LINKEDIN = "social.mapping.linkedin";

    private MappingListRoot linkedInMapping = null;

    private XmlMapper mapper = null;

    @Override
    protected void configure() {
    }

    @Provides
    @Named(SOCIAL_MAPPING_XML_MAPPER)
    public XmlMapper xmlMapper() {
        if (mapper == null) {
            XmlFactory xmlFactory = new XmlFactory(new CustomWstxInputFactory(), new CustomWstxOutputFactory());
            mapper = new XmlMapper(xmlFactory);
            mapper.setSerializationInclusion(Include.NON_EMPTY);
            mapper.setSerializationInclusion(Include.NON_NULL);
            mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        }
        return mapper;
    }

    @Provides
    @Named(SOCIAL_MAPPING_LINKEDIN)
    public MappingListRoot linkedInMapping() throws IOException {
        if (linkedInMapping == null) {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            String linkedIn = Utils.readResourceAsString(loader, "social/mappings/LinkedInProfileMapping.xml");
            linkedInMapping = xmlMapper().readValue(linkedIn, MappingListRoot.class);
        }
        return linkedInMapping;
    }

}
