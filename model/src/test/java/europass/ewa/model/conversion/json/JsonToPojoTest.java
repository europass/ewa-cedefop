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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.ParseException;
import java.util.Locale;

import org.hamcrest.CoreMatchers;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import europass.ewa.model.SkillsPassport;

public class JsonToPojoTest extends JsonMapperTest {

    @Test
    public void emptyLearnerInfo() throws JsonParseException, JsonMappingException, IOException {
        String json
                = "{"
                + "\"SkillsPassport\" : {"
                + "\"Locale\" : \"el\","
                + "\"LearnerInfo\" : {}"
                + "}"
                + "}";
        SkillsPassport esp = this.getMapper().readValue(json, SkillsPassport.class);

        assertThat("Locale: ", esp.getLocale(), CoreMatchers.is(new Locale("el")));

        assertNotNull("LearnerInfo: ", esp.getLearnerInfo());
    }

    @Test
    public void tabCharacters() throws JsonParseException, JsonMappingException, IOException {
        String json
                = "{"
                + "\"SkillsPassport\" : {"
                + "\"LearnerInfo\" : {"
                + "\"Skills\" : {"
                + "\"Communication\" : {"
                + "\"Description\" : \"This is a line\t\ttabbed\""
                + "}"
                + "}"
                + "}"
                + "}"
                + "}";
        SkillsPassport esp = this.getMapper().readValue(json, SkillsPassport.class);

        assertNotNull("Communication Skills: ", esp.getLearnerInfo().getSkills().getCommunication());

        assertNotNull("Tabbed Text: ", esp.getLearnerInfo().getSkills().getCommunication().getDescription());
    }

    @Test
    public void nullAnnex() throws JsonParseException, JsonMappingException, IOException {
        String json
                = "{"
                + "\"SkillsPassport\" : {"
                + "\"LearnerInfo\" : {"
                + "\"ReferenceTo\" : ["
                + "null"
                + "]"
                + "}"
                + "}"
                + "}";
        SkillsPassport esp = this.getMapper().readValue(json, SkillsPassport.class);

        assertNotNull("LearnerInfo: ", esp.getLearnerInfo());

        assertNotNull("ReferenceTo: ", esp.getLearnerInfo().getDocumentation());

        assertThat("Annex size: ", esp.getLearnerInfo().getDocumentation().size(), CoreMatchers.is(0));

    }

    @Test
    public void emptyAnnex() throws JsonParseException, JsonMappingException, IOException {
        String json
                = "{"
                + "\"SkillsPassport\" : {"
                + "\"LearnerInfo\" : {"
                + "\"ReferenceTo\" : ["
                + "{}"
                + "]"
                + "}"
                + "}"
                + "}";
        SkillsPassport esp = this.getMapper().readValue(json, SkillsPassport.class);

        assertNotNull("LearnerInfo: ", esp.getLearnerInfo());

        assertNotNull("ReferenceTo: ", esp.getLearnerInfo().getDocumentation());

        assertThat("Annex size: ", esp.getLearnerInfo().getDocumentation().size(), CoreMatchers.is(0));

    }

    @Test
    public void readPhoto() throws JsonParseException, JsonMappingException, IOException {
        InputStream in = getClass().getResourceAsStream("/json/cv-photo.json");
        assertNotNull("JSON File is found: ", in);

        SkillsPassport esp = this.getMapper().readValue(in, SkillsPassport.class);

        assertNotNull("Photo: ", esp.getLearnerInfo().getIdentification().getPhoto().getData());

    }

    @Test
    public void readLocale() throws JsonParseException, JsonMappingException, IOException {
        InputStream in = getClass().getResourceAsStream("/json/Locale.json");
        assertNotNull("JSON File is found: ", in);

        SkillsPassport esp = this.getMapper().readValue(in, SkillsPassport.class);

        assertThat("Locale: ", esp.getLocale(), CoreMatchers.is(Locale.ITALIAN));
    }

    @Test
    public void personnameTest() throws JsonParseException, IOException {
        InputStream in = getClass().getResourceAsStream("/json/Personname.json");
        assertNotNull("JSON File is found: ", in);

        SkillsPassport esp = this.getMapper().readValue(in, SkillsPassport.class);

        assertThat("FirstName: ", esp.getLearnerInfo().getIdentification().getPersonName().getFirstName(), CoreMatchers.is("Αλέξια"));

        assertThat("Surname: ", esp.getLearnerInfo().getIdentification().getPersonName().getSurname(), CoreMatchers.is("Αντωνίου"));
    }

    @Test
    public void demographicsTest() throws JsonParseException, IOException {
        InputStream in = getClass().getResourceAsStream("/json/Demographics.json");
        assertNotNull("JSON File is found: ", in);

        SkillsPassport esp = this.getMapper().readValue(in, SkillsPassport.class);

        assertThat("Nationalities: ",
                esp.getLearnerInfo().getIdentification().getDemographics().getNationalityList().size(),
                is(3));

        assertThat("Nationality Last: ",
                esp.getLearnerInfo().getIdentification().getDemographics().getNationalityList().get(2).getLabel(),
                is("Citizen of the world"));

        assertThat("Birtdate - Month: ",
                esp.getLearnerInfo().getIdentification().getDemographics().getBirthdate().getMonth(),
                is(2));

        assertThat("Birtdate - Day: ",
                esp.getLearnerInfo().getIdentification().getDemographics().getBirthdate().getDay(),
                is(10));
    }

    @Test
    public void educationTest() throws JsonParseException, IOException {
        InputStream in = getClass().getResourceAsStream("/json/Education.json");
        assertNotNull("JSON File is found: ", in);

        SkillsPassport esp = this.getMapper().readValue(in, SkillsPassport.class);

        assertThat("Education Items: ",
                esp.getLearnerInfo().getEducationList().size(), CoreMatchers.is(2));

        assertThat("Edu-1: Period From Month: ",
                esp.getLearnerInfo().getEducationList().get(0).getPeriod().getFrom().getMonth(), CoreMatchers.is(5));
        assertThat("Edu-1: Period To is current: ",
                esp.getLearnerInfo().getEducationList().get(0).getPeriod().getCurrent(), CoreMatchers.is(true));
        assertThat("Edu-1: Activities: ",
                esp.getLearnerInfo().getEducationList().get(0).getActivities(),
                is("<ul><li>Programming</li><li>Data structures</li></ul>"));
        assertThat("Edu-1: Linked Docs: ",
                esp.getLearnerInfo().getEducationList().get(0).getReferenceToList().size(), CoreMatchers.is(3));
        assertThat("Edu-1: Country: ",
                esp.getLearnerInfo().getEducationList().get(0).getOrganisation().getContactInfo().getAddress().getContact().getCountry().getLabel(), CoreMatchers.is("Hellas"));

        assertThat("Edu-2: Period From Day: ",
                esp.getLearnerInfo().getEducationList().get(1).getPeriod().getFrom().getDay(), CoreMatchers.is(1));
        assertNull("Edu-2: Period To Day: ",
                esp.getLearnerInfo().getEducationList().get(1).getPeriod().getTo().getDay());
        assertThat("Edu-2: Period From Month: ",
                esp.getLearnerInfo().getEducationList().get(1).getPeriod().getFrom().getMonth(), CoreMatchers.is(12));
        assertThat("Edu-2: Period To Month: ",
                esp.getLearnerInfo().getEducationList().get(1).getPeriod().getTo().getMonth(), CoreMatchers.is(4));
        assertThat("Edu-2: Period From Year: ",
                esp.getLearnerInfo().getEducationList().get(1).getPeriod().getFrom().getYear(), CoreMatchers.is(2005));
        assertThat("Edu-2: Period To Year: ",
                esp.getLearnerInfo().getEducationList().get(1).getPeriod().getTo().getYear(), CoreMatchers.is(2008));
        assertThat("Edu-2: Period To is NOT current: ",
                esp.getLearnerInfo().getEducationList().get(1).getPeriod().getCurrent(), CoreMatchers.is(false));
        assertThat("Edu-2: Title: ",
                esp.getLearnerInfo().getEducationList().get(1).getTitle(), CoreMatchers.is("Business and Economics"));
        assertThat("Edu-2: Linked Docs: ",
                esp.getLearnerInfo().getEducationList().get(1).getReferenceToList().size(), CoreMatchers.is(1));
        assertThat("Edu-2: Country: ",
                esp.getLearnerInfo().getEducationList().get(1).getOrganisation().getContactInfo().getAddress().getContact().getCountry().getCode(), CoreMatchers.is("UK"));

    }

    @Test
    public void skillsTest() throws JsonParseException, IOException {
        InputStream in = getClass().getResourceAsStream("/json/Skills.json");
        assertNotNull("JSON File is found: ", in);

        SkillsPassport esp = this.getMapper().readValue(in, SkillsPassport.class);

        assertThat("Communication: ",
                esp.getLearnerInfo().getSkills().getCommunication().getDescription(),
                is("Communication skills: <ul><li>bla</li><li>blah</li></ul>"));

        assertNull("Other: ",
                esp.getLearnerInfo().getSkills().getOther());

        assertThat("Driving: ",
                esp.getLearnerInfo().getSkills().getDriving().getDescription().size(),
                is(2));
        assertThat("Driving: ",
                esp.getLearnerInfo().getSkills().getDriving().getDescription().get(1),
                is("B1"));

        assertThat("Mothers: ",
                esp.getLearnerInfo().getSkills().getLinguistic().getMotherTongue().size(),
                is(2));

        assertThat("Mother 2nd: ",
                esp.getLearnerInfo().getSkills().getLinguistic().getMotherTongue().get(1).getDescription().getLabel(),
                is("Spanish"));

        assertThat("Foreigns: ",
                esp.getLearnerInfo().getSkills().getLinguistic().getForeignLanguage().size(),
                is(2));

        assertThat("Foreign-1 Label: ",
                esp.getLearnerInfo().getSkills().getLinguistic().getForeignLanguage().get(0).getDescription().getLabel(),
                is("English"));

        assertThat("Foreign-1 Listening: ",
                esp.getLearnerInfo().getSkills().getLinguistic().getForeignLanguage().get(0).getProficiencyLevel().getListening(),
                is("C1"));

        assertThat("Foreign-1 Certificate -2 : ",
                esp.getLearnerInfo().getSkills().getLinguistic().getForeignLanguage().get(0).getVerifiedBy().get(1).getTitle(),
                is("Michigan Certificate of Proficiency in English"));

        assertThat("Foreign-2 Certificates: ",
                esp.getLearnerInfo().getSkills().getLinguistic().getForeignLanguage().get(1).getVerifiedBy().size(),
                is(1));
        assertThat("Foreign-1 Area Code: ",
                esp.getLearnerInfo().getSkills().getLinguistic().getForeignLanguage().get(0).getAcquiredDuring().get(0).getArea().getCode(),
                is("option1"));
    }

    @Test
    public void achievementsTest() throws JsonParseException, IOException {
        InputStream in = getClass().getResourceAsStream("/json/Achievements.json");
        assertNotNull("JSON File is found: ", in);

        SkillsPassport esp = this.getMapper().readValue(in, SkillsPassport.class);

        assertThat("Achievements : ",
                esp.getLearnerInfo().getAchievementList().size(),
                is(5));

        assertThat("Achievement-1 : ",
                esp.getLearnerInfo().getAchievementList().get(0).getTitle().getCode(),
                is("projects"));

        assertThat("Achievement-2 : ",
                esp.getLearnerInfo().getAchievementList().get(1).getDescription(),
                is("<ul><li>Membership 1</li><li>Membership 2</li><li>Membership 3</li></ul>"));

        assertThat("Achievement-5 : ",
                esp.getLearnerInfo().getAchievementList().get(4).getTitle().getLabel(),
                is("Participations"));

        assertThat("Achievement-5 - Docs: ",
                esp.getLearnerInfo().getAchievementList().get(4).getReferenceToList().size(),
                is(2));

        assertThat("Achievement-5 - Doc 1: ",
                esp.getLearnerInfo().getAchievementList().get(4).getReferenceToList().get(0).getIdref(),
                is("ATT_4"));
    }

    @Test
    public void documentInfoTest() throws JsonParseException, IOException, ParseException {
        InputStream in = getClass().getResourceAsStream("/json/DocumentInfo.json");
        assertNotNull("JSON File is found: ", in);

        SkillsPassport esp = this.getMapper().readValue(in, SkillsPassport.class);

        assertThat("Generator : ",
                esp.getDocumentInfo().getGenerator(),
                is("EWA"));

        DateTime actualDate = new DateTime(2012, 6, 5, 21, 0, DateTimeZone.UTC);

        assertThat("CreationDate : ",
                esp.getDocumentInfo().getCreationDate(),
                is(actualDate));

        assertThat("Lastupdate Date : ",
                esp.getDocumentInfo().getLastUpdateDate(),
                is(actualDate));
    }

    @Test
    public void prefsTest() throws JsonParseException, IOException {
        InputStream in = getClass().getResourceAsStream("/json/PrintingPreferences.json");
        assertNotNull("JSON File is found: ", in);

        SkillsPassport esp = this.getMapper().readValue(in, SkillsPassport.class);

        assertThat("Prefs : ",
                esp.getDocumentPrintingPrefs().get("ECV").size(),
                is(73));

        assertThat("Last Pref Name: ",
                esp.getDocumentPrintingPrefs().get("ECV").get(72).getName().toString(),
                is("LearnerInfo.Documentation"));

        assertThat("Last Pref Show: ",
                esp.getDocumentPrintingPrefs().get("ECV").get(72).getShow(),
                is(false));

        assertThat("First Pref Order: ",
                esp.getDocumentPrintingPrefs().get("ECV").get(0).getOrder(),
                is("FirstName Surname"));

        assertNull("Second Pref Order: ",
                esp.getDocumentPrintingPrefs().get("ECV").get(1).getOrder());
    }

    @Test
    public void attachmentsTest() throws JsonParseException, IOException {
        InputStream in = getClass().getResourceAsStream("/json/Attachments.json");
        assertNotNull("JSON File is found: ", in);

        SkillsPassport esp = this.getMapper().readValue(in, SkillsPassport.class);

        assertThat("Attachments : ",
                esp.getAttachmentList().size(),
                is(5));

        assertThat("Attachments Last Url: ",
                esp.getAttachmentList().get(4).getName(),
                is("Participation to Hellenic Conference.pdf"));

        assertThat("Attachments Last Name: ",
                esp.getAttachmentList().get(4).getTmpuri(),
                is(URI.create("http://europass.instore.gr/ewars/photo/WT4322OO/DFD2852")));

        assertNull("Attachments Last Data: ",
                esp.getAttachmentList().get(4).getData());
    }

    @Test
    public void wrongPrefsTest() throws JsonParseException, IOException {
        InputStream in = getClass().getResourceAsStream("/json/WrongPrintingPreferences.json");
        assertNotNull("JSON File is found: ", in);

        SkillsPassport esp = this.getMapper().readValue(in, SkillsPassport.class);

        assertThat("Prefs : ",
                esp.getDocumentPrintingPrefs().get("ECV").size(),
                is(6));

        assertThat("Last Pref Name: ",
                esp.getDocumentPrintingPrefs().get("ECV").get(0).getName().toString(),
                is("LearnerInfo.Identification.PersonName"));

        assertThat("Education Period: ",
                esp.getDocumentPrintingPrefs().get("ECV").get(1).getName().toString(),
                is("LearnerInfo.Education[0].Period"));

        assertNull("Empty: ", esp.getDocumentPrintingPrefs().get("ECV").get(3));

        assertNotNull("Non null pref: ", esp.getDocumentPrintingPrefs().get("ECV").get(4));
        assertNotNull("Non null name: ", esp.getDocumentPrintingPrefs().get("ECV").get(4).getName());
        assertThat("Empty name: ", esp.getDocumentPrintingPrefs().get("ECV").get(4).getName().toString(), CoreMatchers.is(""));

        assertNotNull("Non null pref: ", esp.getDocumentPrintingPrefs().get("ECV").get(5));
        assertNull("Null name: ", esp.getDocumentPrintingPrefs().get("ECV").get(5).getName());

    }

// DO NOT DELETE - USED OCCASIONALLY FOR TROUBLESHOOTING
//	@Test
//	public void failedCV1() throws JsonParseException, JsonMappingException, IOException{
//		InputStream in = getClass().getResourceAsStream("/json/feedback-1.json");
//		assertNotNull("JSON File is found: ", in);
//		
//		SkillsPassport esp = this.getMapper().readValue(in, SkillsPassport.class);
//		
//		assertNotNull("ESP: ", esp );
//		
//		//WRITE TO XML TO BE SENT TO USERS FOR UPLOAD
//		XmlMapper xmlMapper =  new XmlMapperTest().getMapper();
//		String xml = xmlMapper.writeValueAsString(esp);
//		System.out.print(xml);
//	}
}
