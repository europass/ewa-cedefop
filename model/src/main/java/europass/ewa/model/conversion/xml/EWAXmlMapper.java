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

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator.Feature;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import europass.ewa.model.DrivingSkill;
import europass.ewa.model.JDate;
import europass.ewa.model.Period;
import europass.ewa.model.SkillsPassport;

public class EWAXmlMapper {

    private EWAXmlMapper() {
        throw new AssertionError();
    }

    public static XmlMapper get() {

        XmlFactory xmlFactory = new XmlFactory(new CustomWstxInputFactory(), new CustomWstxOutputFactory());
        XmlMapper xmlMapper = new XmlMapper(xmlFactory);

        xmlMapper.setSerializationInclusion(Include.NON_EMPTY);
        xmlMapper.setSerializationInclusion(Include.NON_NULL);
        xmlMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);

        xmlMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, false);

        xmlMapper.configure(Feature.WRITE_XML_DECLARATION, true);

        xmlMapper.registerModule(new JodaModule());
        xmlMapper.configure(
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        xmlMapper.addMixInAnnotations(SkillsPassport.class, SkillsPassportMixin.class);

        xmlMapper.addMixInAnnotations(JDate.class, DateMixin.class);

        xmlMapper.addMixInAnnotations(DrivingSkill.class, DrivingSkillMixin.class);

        //EWA-934
        xmlMapper.addMixInAnnotations(Period.class, PeriodMixin.class);

        return xmlMapper;
    }
}
