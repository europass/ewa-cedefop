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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import europass.ewa.model.Address;
import europass.ewa.model.CodeLabel;
import europass.ewa.model.ContactAddress;
import europass.ewa.model.EmployerOrganisation;
import europass.ewa.model.LPMockObjects;
import europass.ewa.model.LearnerInfo;
import europass.ewa.model.LinguisticExperience;
import europass.ewa.model.LinguisticSkill;
import europass.ewa.model.LinguisticSkills;
import europass.ewa.model.MockObjects;
import europass.ewa.model.OrganisationalContactInfo;
import europass.ewa.model.ReferenceTo;
import europass.ewa.model.Skills;
import europass.ewa.model.SkillsPassport;
import europass.ewa.model.WorkExperience;

public class PojoToJsonTest extends JsonMapperTest {

    @Test
    public void jsonWrite() throws JsonProcessingException {
        String json = this.getMapper().writeValueAsString(LPMockObjects.elpSkillsObj);

        assertNotNull("Json produced - ", json);
    }

    @Test
    public void noName() throws JsonProcessingException {
        ObjectMapper mapper = this.getMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, false);
        String json = mapper.writeValueAsString(MockObjects.espSimpleObj().getLearnerInfo().getIdentification());

        assertNotNull("Json produced - ", json);

        String actual
                = "{\"Identification\":{"
                + "\"PersonName\":{"
                + "\"FirstName\":\"\","
                + "\"Surname\":\"\","
                + "}"
                + "\"ContactInfo\": {"
                + "\"ContactAddress\":{"
                + "\"Address\":{"
                + "\"Country\":{"
                + "\"Code\":\"EL\","
                + "\"Label\":\"Hellas\","
                + "}"
                + "}"
                + "}"
                + "}"
                + "}}";

        assertNotSame("Identification: ", json, CoreMatchers.is(actual));
    }

    @Test
    public void noEmployerName() throws JsonProcessingException {
        //Work Experience
        WorkExperience work1 = new WorkExperience();
        work1.setPosition(new CodeLabel("R", "Martial Arts Instructor"));
        EmployerOrganisation work1empl = new EmployerOrganisation();
        OrganisationalContactInfo work1OrgContact = new OrganisationalContactInfo();
        work1OrgContact.setAddress(new ContactAddress(new Address(new CodeLabel("JP", "Japan"))));
        work1empl.setContactInfo(work1OrgContact);
        work1.setEmployer(work1empl);
        List<WorkExperience> worklist = new ArrayList<WorkExperience>();
        worklist.add(work1);

        LearnerInfo learnerinfo = new LearnerInfo();
        learnerinfo.setWorkExperienceList(worklist);

        SkillsPassport esp = new SkillsPassport();
        esp.setLearnerInfo(learnerinfo);

        ObjectMapper mapper = this.getMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, false);
        String json = mapper.writeValueAsString(esp.getLearnerInfo());

        assertNotNull("Json produced - ", json);

        String actual
                = "{\"LearnerInfo\":{"
                + "\"WorkExperience\":[{"
                + "\"Position\":{"
                + "\"Code\":\"R\","
                + "\"Label\":\"Martial Arts Instructor\","
                + "}"
                + "\"Employer\": {"
                + "\"Name\": \"\""
                + "\"ContactAddress\":{"
                + "\"Address\":{"
                + "\"Country\":{"
                + "\"Code\":\"JP\","
                + "\"Label\":\"Japan\","
                + "}"
                + "}"
                + "}"
                + "}"
                + "}]"
                + "}}";

        assertNotSame("Work Experience: ", json, CoreMatchers.is(actual));

    }

    @Test
    public void jsonWriteDocumentInfo() throws JsonProcessingException {

        ObjectMapper mapper = this.getMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, false);

        String json = mapper.writeValueAsString(MockObjects.complete().getDocumentInfo());

        assertNotNull("Json produced - ", json);

        String actual = "{\"DocumentInfo\":{"
                + "\"DocumentType\":\"ECV_ESP\","
                + "\"CreationDate\":\"2012-06-06T00:00:00.000Z\","
                + "\"LastUpdateDate\":\"2012-06-06T00:00:00.000Z\","
                + "\"XSDVersion\":\"V3.0\","
                + "\"Generator\":\"EWA\","
                + "\"Comment\":\"Comments\","
                + "\"EuropassLogo\":true"
                + "}}";

        assertThat("DocumentInfo: ", json, CoreMatchers.is(actual));

    }

    @Test
    public void writeFileData() throws JsonProcessingException {
        ObjectMapper mapper = this.getMapper();

        mapper.configure(SerializationFeature.INDENT_OUTPUT, false);

        String json = mapper.writeValueAsString(MockObjects.espFileDataObj());

        assertNotNull("Json produced - ", json);

        String actualJson = "{"
                + "\"SkillsPassport\":{"
                + "\"Attachment\":[{"
                + "\"Id\":\"ATT_1\","
                + "\"Name\":\"Certificate.pdf\","
                + "\"MimeType\":\"application/pdf\","
                + "\"Data\":\"RklMRUJZVEVTMQ==\","
                + "\"TempURI\":\"http://europass.instore.gr/files/file/WR3452ERUTT7534\","
                + "\"Description\":\"Certificate of Attendance\""
                + "},{"
                + "\"Id\":\"ATT_2\","
                + "\"Name\":\"Diploma.pdf\","
                + "\"MimeType\":\"application/pdf\","
                + "\"Data\":\"RklMRUJZVEVTMg==\","
                + "\"TempURI\":\"http://europass.instore.gr/files/file/WR3452EPOS3244\","
                + "\"Description\":\"Engineering Diploma\""
                + "}]"
                + "}"
                + "}";

        assertThat("FileData: ", json, CoreMatchers.is(actualJson));
    }

    @Test
    public void writeAnnex() throws JsonProcessingException {
        ObjectMapper mapper = this.getMapper();

        mapper.configure(SerializationFeature.INDENT_OUTPUT, false);

        SkillsPassport esp = new SkillsPassport();
        LearnerInfo learner = new LearnerInfo();

        List<ReferenceTo> annex = new ArrayList<ReferenceTo>();
        annex.add(new ReferenceTo("ATT_1"));
        annex.add(null);
        annex.add(null);
        annex.add(new ReferenceTo("ATT_2"));
        annex.add(null);
        learner.setDocumentation(annex);
        esp.setLearnerInfo(learner);

        String json = mapper.writeValueAsString(esp);

        assertNotNull("Json produced - ", json);

        String actualJson = "{"
                + "\"SkillsPassport\":{"
                + "\"LearnerInfo\":{"
                + "\"ReferenceTo\":[{"
                + "\"idref\":\"ATT_1\""
                + "},{"
                + "\"idref\":\"ATT_2\""
                + "}]"
                + "}"
                + "}"
                + "}";

        assertThat("Annex: ", json, CoreMatchers.is(actualJson));
    }

    @Test
    public void foreignLanguageArea() throws JsonProcessingException {
        ObjectMapper mapper = this.getMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, false);
        SkillsPassport esp = new SkillsPassport();
        LearnerInfo learner = new LearnerInfo();
        Skills skills = new Skills();
        LinguisticSkills linguistic = new LinguisticSkills();
        List<LinguisticSkill> foreignLanguageList = new ArrayList<>();
        LinguisticSkill skill = new LinguisticSkill();
        List<LinguisticExperience> experienceList = new ArrayList<>();
        LinguisticExperience experience = new LinguisticExperience();
        CodeLabel area = new CodeLabel();
        area.setCode("option1");
        area.setLabel("area option1");
        experience.setArea(area);
        experienceList.add(experience);
        skill.setAcquiredDuring(experienceList);
        foreignLanguageList.add(skill);
        linguistic.setForeignLanguage(foreignLanguageList);
        skills.setLinguistic(linguistic);
        learner.setSkills(skills);
        esp.setLearnerInfo(learner);

        String json = mapper.writeValueAsString(esp);

        assertNotNull("Json produced - ", esp);

        String actual = "{"
                + "\"SkillsPassport\":{"
                + "\"LearnerInfo\":{"
                + "\"Skills\":{"
                + "\"Linguistic\":{"
                + "\"ForeignLanguage\":[{"
                + "\"Experience\":[{"
                + "\"Area\":{\"Code\":\"option1\",\"Label\":\"area option1\"}"
                + "}]"
                + "}]"
                + "}"
                + "}"
                + "}"
                + "}"
                + "}";

        assertThat("Area: ", json, CoreMatchers.is(actual));
    }

}
