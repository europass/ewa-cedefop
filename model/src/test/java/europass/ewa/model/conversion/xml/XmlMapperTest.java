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

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import europass.ewa.model.FileData;

public class XmlMapperTest {

    private final XmlMapper xmlMapper;

    public XmlMapperTest() {

        //XML MAPPER
        xmlMapper = EWAXmlMapper.get();

        //To enable use of BOTH JAXB annotations AND Jackson annotations:
        /*AnnotationIntrospector primary = new JacksonAnnotationIntrospector();
		AnnotationIntrospector secondary = new JaxbAnnotationIntrospector();
		this.xmlMapper.setAnnotationIntrospector(new AnnotationIntrospector.Pair(primary, secondary) );
		this.xmlMapper.addMixInAnnotations(Attachment.class, AttachmentMixin.class);*/
    }

    public XmlMapper getMapper() {
        return this.xmlMapper;
    }

    public XmlMapper getWithFileDataFilter() {
        //Filter to exclude FileData tempuri from XML serialisation
        this.xmlMapper.addMixInAnnotations(FileData.class, FileDataMixin.class);
        FilterProvider filters = new SimpleFilterProvider().addFilter("xml-filedata-filter",
                SimpleBeanPropertyFilter.serializeAllExcept("TempURI"));
        this.xmlMapper.setFilters(filters);
        return this.xmlMapper;
    }
}
