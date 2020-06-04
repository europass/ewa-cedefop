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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.name.Names;

import europass.ewa.model.LPMockObjects;
import europass.ewa.model.Namespace;
import europass.ewa.model.SkillsPassport;

public class LpPojoToXmlTest extends XmlMapperTest {

    @Before
    public void prepareInjections() {
        Guice.createInjector(
                new AbstractModule() {

            @Override
            protected void configure() {
                bindConstant().annotatedWith(Names.named(Namespace.SCHEMA_LOCATION_DEFAULT_PARAM))
                        .to(Namespace.NAMESPACE + " http://europass.cedefop.europa.eu/xml/EuropassSchema_V3.0.xsd");

                requestStaticInjection(Namespace.class);
            }
        }
        );

    }

    @Test
    public void writeXML() throws IOException {
        String xml = this.getMapper().writeValueAsString(LPMockObjects.elpSkillsObj);
        assertNotNull("XML produced - ", xml);

        String expected = "<?xml version='1.0' encoding='UTF-8'?>"
                + "<SkillsPassport "
                + "xmlns=\"http://europass.cedefop.europa.eu/Europass\" "
                + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                + "xsi:schemaLocation=\"http://europass.cedefop.europa.eu/Europass http://europass.cedefop.europa.eu/xml/EuropassSchema_V3.0.xsd\">"
                + "<LearnerInfo>"
                + "<Identification><PersonName><FirstName>Σάκης</FirstName><Surname>Πεταλούδας</Surname></PersonName></Identification>"
                + "<Skills>"
                + "<Linguistic>"
                + "<MotherTongueList>"
                + "<MotherTongue><Description><Code>el</Code><Label>Greek</Label></Description></MotherTongue>"
                + "<MotherTongue><Description><Code>es</Code><Label>Spanish</Label></Description></MotherTongue>"
                + "</MotherTongueList>"
                + "<ForeignLanguageList>"
                + "<ForeignLanguage>"
                + "<Description><Code>en</Code><Label>English</Label></Description>"
                + "<ProficiencyLevel>"
                + "<Listening>C1</Listening>"
                + "<Reading>C2</Reading>"
                + "<SpokenInteraction>B2</SpokenInteraction>"
                + "<SpokenProduction>B2</SpokenProduction>"
                + "<Writing>B1</Writing>"
                + "</ProficiencyLevel>"
                + "<AcquiredDuring>"
                + "<Experience>"
                + "<Period>"
                + "<From "
                + "year=\"2000\" "
                + "month=\"--06\" "
                + "day=\"---10\"/>"
                + "<To "
                + "year=\"2001\" "
                + "month=\"--08\" "
                + "day=\"---15\"/>"
                + "<Current>false</Current>"
                + "</Period>"
                + "<Description>"
                + "&lt;p>&lt;em>Summer&lt;/em> English courses that help me to improve my spoken interaction level&lt;/p>"
                + "</Description>"
                + "</Experience>"
                + "</AcquiredDuring>"
                + "<VerifiedBy>"
                + "<Certificate>"
                + "<Title>CPE (short title)</Title>"
                + "<AwardingBody>British Council</AwardingBody>"
                + "<Date year=\"2013\" month=\"--10\" day=\"---15\"/>"
                + "<Level>C2</Level>"
                + "</Certificate>"
                + "<Certificate>"
                + "<Title>Michigan Certificate of Proficiency in English</Title>"
                + "</Certificate>"
                + "</VerifiedBy>"
                + "</ForeignLanguage>"
                + "<ForeignLanguage>"
                + "<Description><Code>it</Code><Label>Italian</Label></Description>"
                + "<ProficiencyLevel>"
                + "<Listening>B1</Listening>"
                + "<Reading>B2</Reading>"
                + "<SpokenInteraction>B1</SpokenInteraction>"
                + "<SpokenProduction>A2</SpokenProduction>"
                + "<Writing>A1</Writing>"
                + "</ProficiencyLevel>"
                + "<VerifiedBy>"
                + "<Certificate>"
                + "<Title>Certificate of Adequacy in Italian</Title>"
                + "</Certificate>"
                + "</VerifiedBy>"
                + "</ForeignLanguage>"
                + "</ForeignLanguageList>"
                + "</Linguistic>"
                + "</Skills>"
                + "</LearnerInfo>"
                + "</SkillsPassport>";

        assertThat("generic xml ", xml, CoreMatchers.is(expected));
    }

    @Test
    public void writeForeignLanguageXML() throws IOException {
        SkillsPassport sp = LPMockObjects.elpSkillsObjArea;

        String xml = this.getMapper().writeValueAsString(sp);
        assertNotNull("XML produced - ", xml);

        String expected = "<?xml version='1.0' encoding='UTF-8'?>"
                + "<SkillsPassport "
                + "xmlns=\"http://europass.cedefop.europa.eu/Europass\" "
                + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                + "xsi:schemaLocation=\"http://europass.cedefop.europa.eu/Europass http://europass.cedefop.europa.eu/xml/EuropassSchema_V3.0.xsd\">"
                + "<LearnerInfo>"
                + "<Skills>"
                + "<Linguistic>"
                + "<ForeignLanguageList>"
                + "<ForeignLanguage>"
                + "<Description><Code>en</Code><Label>English</Label></Description>"
                + "<ProficiencyLevel>"
                + "<Listening>C1</Listening>"
                + "<Reading>C2</Reading>"
                + "<SpokenInteraction>B2</SpokenInteraction>"
                + "<SpokenProduction>B2</SpokenProduction>"
                + "<Writing>B1</Writing>"
                + "</ProficiencyLevel>"
                + "<AcquiredDuring>"
                + "<Experience>"
                + "<Period>"
                + "<From "
                + "year=\"2000\" "
                + "month=\"--06\" "
                + "day=\"---10\"/>"
                + "<To "
                + "year=\"2001\" "
                + "month=\"--08\" "
                + "day=\"---15\"/>"
                + "<Current>false</Current>"
                + "</Period>"
                + "<Description>Summer English courses that help me to improve my spoken interaction level</Description>"
                + "<Area><Code>option1</Code><Label>area option1</Label></Area>"
                + "</Experience>"
                + "</AcquiredDuring>"
                + "<VerifiedBy>"
                + "<Certificate>"
                + "<Title>CPE (short title)</Title>"
                + "<AwardingBody>British Council</AwardingBody>"
                + "<Date year=\"2013\" month=\"--10\" day=\"---15\"/>"
                + "<Level>C2</Level>"
                + "</Certificate>"
                + "<Certificate>"
                + "<Title>Michigan Certificate of Proficiency in English</Title>"
                + "</Certificate>"
                + "</VerifiedBy>"
                + "</ForeignLanguage>"
                + "</ForeignLanguageList>"
                + "</Linguistic>"
                + "</Skills>"
                + "</LearnerInfo>"
                + "</SkillsPassport>";

        assertThat("generic xml ", xml, CoreMatchers.is(expected));
    }

}
