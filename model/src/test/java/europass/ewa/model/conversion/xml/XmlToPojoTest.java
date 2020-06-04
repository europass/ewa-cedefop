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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.InputStream;

import org.joda.time.DateTimeZone;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import europass.ewa.model.JDate;
import europass.ewa.model.Namespace;
import europass.ewa.model.Period;
import europass.ewa.model.SkillsPassport;

public class XmlToPojoTest extends XmlMapperTest {

    @Test
    public void lineBreakPref() throws JsonParseException, JsonMappingException, IOException {
        String xml = "<?xml version='1.0' encoding='UTF-8'?>"
                + "<SkillsPassport xmlns=\"" + Namespace.NAMESPACE + "\" xmlns:xsi=\"" + Namespace.XSI_NAMESPACE + "\" xsi:schemaLocation=\"" + Namespace.NAMESPACE + " " + Namespace.SCHEMA_LOCATION_DEFAULT + "\">"
                + "<PrintingPreferences>"
                + "<Document type=\"ECV\">"
                + "<Field name=\"Address\" show=\"true\" format=\"s&#xa;z m (c)\"></Field>"
                + "</Document>"
                + "</PrintingPreferences>"
                + "</SkillsPassport>";
        XmlMapper xmlMapper = this.getMapper();

        SkillsPassport esp = xmlMapper.readValue(xml, SkillsPassport.class);

        assertThat("Address Format ", esp.getDocumentPrintingPrefs().get("ECV").get(0).getFormat(), is("s\nz m (c)"));
    }

    @Test
    public void loadFileData() throws JsonParseException, JsonMappingException, IOException {
        InputStream in = getClass().getResourceAsStream("/xml/Attachments.xml");

        SkillsPassport esp = this.getMapper().readValue(in, SkillsPassport.class);
        assertNotNull("ESP null: " + esp);

        assertThat(esp.getAttachmentList().size(), is(2));

        assertThat(esp.getAttachmentList().get(0).getId(), is("ATT_1"));

        assertThat(esp.getAttachmentList().get(0).getMimeType(), is("image/png"));

        assertThat(esp.getAttachmentList().get(1).getName(), is("My scanned Diploma copy"));
    }

    @Test
    public void loadAnnex() throws JsonParseException, JsonMappingException, IOException {
        InputStream in = getClass().getResourceAsStream("/xml/Annex.xml");

        SkillsPassport esp = this.getMapper().readValue(in, SkillsPassport.class);
        assertNotNull("ESP null: " + esp);

        assertThat(esp.getLearnerInfo().getDocumentation().size(), is(2));

        assertThat(esp.getLearnerInfo().getDocumentation().get(0).getIdref(), is("ATT_1"));

        assertThat(esp.getLearnerInfo().getDocumentation().get(1).getIdref(), is("ATT_2"));
    }

    @Test
    public void loadMultipleFileData() throws JsonParseException, JsonMappingException, IOException {
        InputStream in = getClass().getResourceAsStream("/xml/Europass-CV-ATTACHMENTS.xml");

        SkillsPassport esp = this.getMapper().readValue(in, SkillsPassport.class);
        assertNotNull("ESP null: " + esp);

        assertThat(esp.getLearnerInfo().getIdentification().getPhoto().getMimeType(), is("image/png"));

        assertThat(esp.getAttachmentList().size(), is(2));

        assertThat(esp.getAttachmentList().get(0).getId(), is("ATT_1"));

        assertThat(esp.getAttachmentList().get(0).getMimeType(), is("image/jpeg"));

        assertThat(esp.getAttachmentList().get(0).getName(), is("DIPLOMA"));

        assertThat(esp.getAttachmentList().get(1).getId(), is("ATT_2"));

        assertThat(esp.getAttachmentList().get(1).getMimeType(), is("image/jpeg"));

        assertThat(esp.getAttachmentList().get(1).getName(), is("CERTIFICATE"));

    }

    @Test
    public void simpleESP() throws JsonParseException, JsonMappingException, IOException {
        InputStream in = getClass().getResourceAsStream("/xml/CV-Example-v3.0-TEST-SIMPLE.xml");
        /*
		 * Includes
		 * <CreationDate>2012-03-01T00:00:00.000+03:00</CreationDate>
         */
        SkillsPassport esp = this.getMapper().readValue(in, SkillsPassport.class);
        assertNotNull("ESP null: " + esp);

        DateTimeZone dtz = esp.getDocumentInfo().getCreationDate().getZone();
        assertThat(dtz.toString(), is("UTC"));

        assertThat("Creation Date: ",
                esp.getDocumentInfo().getCreationDate().toString(),
                is("2012-02-29T21:00:00.000Z"));
        assertThat("Last update Date: ",
                esp.getDocumentInfo().getLastUpdateDate().toString(),
                is("2012-02-29T21:00:00.000Z"));

        assertThat("Prefs",
                esp.getDocumentPrintingPrefs().size(),
                is(1));
        assertThat("Prefs",
                esp.getDocumentPrintingPrefs().get("ECV").get(0).getName().toString(),
                is("LearnerInfo"));

        assertThat("Birthdate",
                esp.getLearnerInfo().getIdentification().getDemographics().getBirthdate().getYear(),
                is(1940));
    }

    @Test
    public void personnameTest() throws JsonParseException, JsonMappingException, IOException {
        InputStream in = getClass().getResourceAsStream("/xml/Personname.xml");

        SkillsPassport esp = this.getMapper().readValue(in, SkillsPassport.class);

        assertThat("FirstName: ", esp.getLearnerInfo().getIdentification().getPersonName().getFirstName(), is("Eleni"));

        assertThat("Surname: ", esp.getLearnerInfo().getIdentification().getPersonName().getSurname(), is("Kargioti"));

    }

    @Test
    public void newLineCharactersTest() throws JsonParseException, JsonMappingException, IOException {
        InputStream in = getClass().getResourceAsStream("/xml/NewLineCharacters.xml");

        SkillsPassport esp = this.getMapper().readValue(in, SkillsPassport.class);

        assertFalse(esp.getLearnerInfo().getWorkExperienceList().get(0).getActivities().contains("\n"));

        assertFalse(esp.getLearnerInfo().getEducationList().get(0).getActivities().contains("\n"));

        assertFalse(esp.getLearnerInfo().getSkills().getCommunication().getDescription().contains("\n"));

        assertFalse(esp.getLearnerInfo().getSkills().getOrganisational().getDescription().contains("\n"));

        assertFalse(esp.getLearnerInfo().getSkills().getComputer().getDescription().contains("\n"));

        assertFalse(esp.getLearnerInfo().getSkills().getJobRelated().getDescription().contains("\n"));

        assertFalse(esp.getLearnerInfo().getSkills().getOther().getDescription().contains("\n"));

        assertFalse(esp.getLearnerInfo().getAchievementList().get(0).getDescription().contains("\n"));

    }

    @Test
    public void educationTest() throws JsonParseException, JsonMappingException, IOException {
        InputStream in = getClass().getResourceAsStream("/xml/Education.xml");

        SkillsPassport esp = this.getMapper().readValue(in, SkillsPassport.class);

        assertThat("Education Experiences Size ",
                esp.getLearnerInfo().getEducationList().size(),
                is(1));

        assertThat("Education - 1 - Uni Name",
                esp.getLearnerInfo().getEducationList().get(0).getOrganisation().getName(),
                is("University of Aegean"));

        assertThat("Education Experiences Period From Moneth ",
                esp.getLearnerInfo().getEducationList().get(0).getPeriod().getFrom().getMonth(),
                is(5));

        assertThat("Education Experiences Period From Moneth ",
                esp.getLearnerInfo().getEducationList().get(0).getPeriod().getFrom().getYear(),
                is(2008));
    }

    @Test
    public void loadPrefsXML() throws JsonParseException, JsonMappingException, IOException {
        String xml = "<?xml version='1.0' encoding='UTF-8'?>"
                + "<SkillsPassport xmlns=\"" + Namespace.NAMESPACE + "\" xmlns:xsi=\"" + Namespace.XSI_NAMESPACE + "\" xsi:schemaLocation=\"" + Namespace.NAMESPACE + " " + Namespace.SCHEMA_LOCATION_DEFAULT + "\">"
                + "<PrintingPreferences>"
                + "<Document type=\"ECV\">"
                + "<Field name=\"LearnerInfo.PersonName\" show=\"true\" order=\"FirstName Surname\"></Field>"
                + "<Field name=\"Address\" show=\"true\" format=\"s \\n p-z m (c)\"></Field>"
                + "<Field name=\"Telephone\" show=\"true\"></Field>"
                + "</Document>"
                + "</PrintingPreferences>"
                + "<LearnerInfo>"
                + "<Identification>"
                + "<PersonName><FirstName>Eleni</FirstName><Surname>Kargioti</Surname></PersonName>"
                + "</Identification>"
                + "</LearnerInfo>"
                + "</SkillsPassport>";
        XmlMapper xmlMapper = this.getMapper();
        xmlMapper.addMixInAnnotations(SkillsPassport.class, SkillsPassportMixin.class);

        SkillsPassport esp = xmlMapper.readValue(xml, SkillsPassport.class);

        assertNotNull(esp.getDocumentPrintingPrefs().get("ECV"));
        assertThat(esp.getDocumentPrintingPrefs().get("ECV").size(), is(3));
        assertThat(esp.getDocumentPrintingPrefs().get("ECV").get(0).getName().toString(), is("LearnerInfo.PersonName"));
        assertThat(esp.getDocumentPrintingPrefs().get("ECV").get(0).getShow(), is(true));
        assertThat(esp.getDocumentPrintingPrefs().get("ECV").get(0).getOrder(), is("FirstName Surname"));

        assertThat(esp.getDocumentPrintingPrefs().get("ECV").get(1).getName().toString(), is("Address"));
        assertThat(esp.getDocumentPrintingPrefs().get("ECV").get(1).getShow(), is(true));
        assertThat(esp.getDocumentPrintingPrefs().get("ECV").get(1).getFormat(), is("s \\n p-z m (c)"));
        assertNull(esp.getDocumentPrintingPrefs().get("ECV").get(1).getOrder());

        assertThat(esp.getDocumentPrintingPrefs().get("ECV").get(2).getName().toString(), is("Telephone"));
        assertThat(esp.getDocumentPrintingPrefs().get("ECV").get(2).getShow(), is(true));
        assertNull(esp.getDocumentPrintingPrefs().get("ECV").get(2).getOrder());

        assertNull(esp.getDocumentPrintingPrefs().get("ELP"));

        assertThat("Name: ",
                esp.getLearnerInfo().getIdentification().getPersonName().getFirstName(),
                is("Eleni"));
    }

    @Test
    public void emptyLearnerInfo() throws JsonParseException, JsonMappingException, IOException {
        String xml = "<?xml version='1.0' encoding='UTF-8'?><SkillsPassport xmlns=\"http://europass.cedefop.europa.eu/Europass\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://europass.cedefop.europa.eu/Europass http://europass.cedefop.europa.eu/xml/EuropassSchema_V3.0-rc7.xsd\" locale=\"de\"><DocumentInfo/><PrintingPreferences/><LearnerInfo/></SkillsPassport>";

        SkillsPassport esp = this.getMapper().readValue(xml, SkillsPassport.class);

        assertNotNull("LearnerInfo: " + esp.getLearnerInfo());

    }

    @Test
    public void loadDate() throws JsonParseException, JsonMappingException, IOException {
        InputStream in = getClass().getResourceAsStream("/xml/EducationalPeriod.xml");

        SkillsPassport esp = this.getMapper().readValue(in, SkillsPassport.class);

        Period period = esp.getLearnerInfo().getEducationList().get(0).getPeriod();

        JDate from = period.getFrom();
        JDate to = period.getTo();

        assertThat("Educational Period From Year",
                from.getYear(),
                is(2000));
        assertThat("Educational Period To Year",
                to.getYear(),
                is(2005));

        assertNull("Educational Period From Month",
                from.getMonth());
        assertNull("Educational Period To Month",
                to.getMonth());
    }

    @Test
    public void loadForeignLanguageAreaXML() throws JsonParseException, JsonMappingException, IOException {
        String xml = "<?xml version='1.0' encoding='UTF-8'?>"
                + "<SkillsPassport xmlns=\"" + Namespace.NAMESPACE + "\" xmlns:xsi=\"" + Namespace.XSI_NAMESPACE + "\" xsi:schemaLocation=\"" + Namespace.NAMESPACE + " " + Namespace.SCHEMA_LOCATION_DEFAULT + "\">"
                + "<PrintingPreferences/>"
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
        XmlMapper xmlMapper = this.getMapper();
        xmlMapper.addMixInAnnotations(SkillsPassport.class, SkillsPassportMixin.class);

        SkillsPassport esp = xmlMapper.readValue(xml, SkillsPassport.class);

        assertThat("Area Code: ",
                esp.getLearnerInfo().getSkills().getLinguistic().getForeignLanguage().get(0).getAcquiredDuring().get(0).getArea().getCode(),
                is("option1"));
        assertThat("Area Label: ",
                esp.getLearnerInfo().getSkills().getLinguistic().getForeignLanguage().get(0).getAcquiredDuring().get(0).getArea().getLabel(),
                is("area option1"));
    }
}
