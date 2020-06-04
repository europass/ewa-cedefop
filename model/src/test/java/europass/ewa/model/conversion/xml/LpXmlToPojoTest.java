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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import europass.ewa.model.Namespace;
import europass.ewa.model.SkillsPassport;

public class LpXmlToPojoTest extends XmlMapperTest {

    @Test
    public void linguisticDetails() throws JsonParseException, JsonMappingException, IOException {
        String xml = "<?xml version='1.0' encoding='UTF-8'?>"
                + "<SkillsPassport xmlns=\"" + Namespace.NAMESPACE + "\" xmlns:xsi=\"" + Namespace.XSI_NAMESPACE + "\" xsi:schemaLocation=\"" + Namespace.NAMESPACE + " " + Namespace.SCHEMA_LOCATION_DEFAULT + "\">"
                + "<LearnerInfo>"
                + "<Skills>"
                + "<Linguistic>"
                + "<ForeignLanguageList>"
                + "<ForeignLanguage>"
                + "<Description>"
                + "<Code>de</Code>"
                + "<Label>German</Label>"
                + "</Description>"
                + "<AcquiredDuring>"
                + "<Experience>"
                + "<Period>"
                + "<From year=\"2007\" month=\"--09\" day=\"---09\"/>"
                + "<To year=\"2011\" month=\"--07\" day=\"---06\"/>"
                + "<Current>false</Current>"
                + "</Period>"
                + "<Description>some experience</Description>"
                + "<Area>"
                + "<Label>Interaction</Label>"
                + "</Area>"
                + "</Experience>"
                + "</AcquiredDuring>"
                + "<VerifiedBy>"
                + "<Certificate>"
                + "<Title>Mittelstuffe</Title>"
                + "<AwardingBody>Goethe</AwardingBody>"
                + "<Date year=\"2006\" month=\"--09\" day=\"---10\"/>"
                + "<Level>B2</Level>"
                + "</Certificate>"
                + "<Certificate>"
                + "<Title>Kleines</Title>"
                + "<AwardingBody>Goethe</AwardingBody>"
                + "<Date year=\"2008\" month=\"--09\" day=\"---10\"/>"
                + "<Level>C1</Level>"
                + "</Certificate>"
                + "</VerifiedBy>"
                + "</ForeignLanguage>"
                + "</ForeignLanguageList>"
                + "</Linguistic>"
                + "</Skills>"
                + "</LearnerInfo>"
                + "</SkillsPassport>";

        XmlMapper xmlMapper = this.getMapper();

        SkillsPassport esp = xmlMapper.readValue(xml, SkillsPassport.class);

        assertThat("certificate 0 title ",
                esp.getLearnerInfo().getSkills().getLinguistic().getForeignLanguage().get(0).getVerifiedBy().get(0).getTitle(),
                is("Mittelstuffe"));

        assertThat("certificate 0 level ",
                esp.getLearnerInfo().getSkills().getLinguistic().getForeignLanguage().get(0).getVerifiedBy().get(0).getLevel(),
                is("B2"));

        assertThat("certificate 1 title ",
                esp.getLearnerInfo().getSkills().getLinguistic().getForeignLanguage().get(0).getVerifiedBy().get(1).getTitle(),
                is("Kleines"));

        assertThat("certificate 1 level ",
                esp.getLearnerInfo().getSkills().getLinguistic().getForeignLanguage().get(0).getVerifiedBy().get(1).getLevel(),
                is("C1"));

        assertThat("experience 0 Description ",
                esp.getLearnerInfo().getSkills().getLinguistic().getForeignLanguage().get(0).getAcquiredDuring().get(0).getDescription(),
                is("some experience"));

        assertThat("experience 0 area ",
                esp.getLearnerInfo().getSkills().getLinguistic().getForeignLanguage().get(0).getAcquiredDuring().get(0).getArea().getLabel(),
                is("Interaction"));

    }
}
