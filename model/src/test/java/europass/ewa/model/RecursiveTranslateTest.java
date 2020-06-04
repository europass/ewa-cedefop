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
package europass.ewa.model;

import europass.ewa.model.reflection.ReflectionUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecursiveTranslateTest {

    static SkillsPassport esp;

    static SkillsPassport espLinguistic;

    static SkillsPassport espHeadline;

    static SkillsPassport espReplace;

    @BeforeClass
    public static void prepare() {

        CodeLabel gender = new CodeLabel("F");
        Demographics demographics = new Demographics();
        demographics.setGender(gender);
        List<CodeLabel> nationality = new ArrayList<CodeLabel>();
        nationality.add(new CodeLabel("EL"));
        nationality.add(new CodeLabel("UK"));
        demographics.setNationalityList(nationality);
        Identification identification = new Identification();
        identification.setDemographics(demographics);
        LearnerInfo learnerinfo = new LearnerInfo();
        learnerinfo.setIdentification(identification);
        Education education = new Education();
        education.setLevel(new CodeLabel("1"));
        OrganisationalContactInfo orgContact = new OrganisationalContactInfo();
        orgContact.setAddress(new ContactAddress(new Address(new CodeLabel("EL"))));
        education.setOrganisation(new Organisation("University of Aegean", orgContact));

        Headline headline = new Headline();
        headline.setType(new CodeLabel("position"));
        headline.setDescription(new CodeLabel("bab5fa79-7f96-4e21-87b6-1eba560b8d9a"));
        learnerinfo.setHeadline(headline);

        WorkExperience work = new WorkExperience();
        work.setPosition(new CodeLabel("52df9d56-efd4-48d0-ad93-59231943fc4c"));
        OrganisationalContactInfo workOrgContact = new OrganisationalContactInfo();
        workOrgContact.setAddress(new ContactAddress(new Address(new CodeLabel("FI"))));
        EmployerOrganisation work1empl = new EmployerOrganisation("School of Ninjutsu", workOrgContact, new CodeLabel("P"));
        work.setEmployer(work1empl);
        List<WorkExperience> worklist = new ArrayList<WorkExperience>();
        worklist.add(work);
        learnerinfo.setWorkExperienceList(worklist);

        List<Education> educationlist = new ArrayList<>(1);
        educationlist.add(education);
        learnerinfo.setEducationList(educationlist);

        List<LinguisticSkill> m = new ArrayList<LinguisticSkill>();
        m.add(new LinguisticSkill(new CodeLabel("el")));
        m.add(new LinguisticSkill(new CodeLabel(null, "Elvish")));

        List<LinguisticSkill> f = new ArrayList<LinguisticSkill>();
        LinguisticSkill f1 = new LinguisticSkill(new CodeLabel("ja"));
        List<LinguisticExperience> listEx = new ArrayList<LinguisticExperience>();
        LinguisticExperience ex = new LinguisticExperience();
        ex.setArea(new CodeLabel("mediating_groups_language"));
        listEx.add(ex);
        f1.setAcquiredDuring(listEx);
        f.add(f1);
        Skills s = new Skills();
        s.setLinguistic(new LinguisticSkills(m, f));

        learnerinfo.setSkills(s);

        List<Achievement> achs = new ArrayList<>();
        achs.add(new Achievement(new CodeLabel("publications"), "Blabla"));
        learnerinfo.setAchievementList(achs);

        esp = new SkillsPassport();
        esp.setLearnerInfo(learnerinfo);

        espLinguistic = new SkillsPassport();
        LearnerInfo learnerLinguistic = new LearnerInfo();
        List<LinguisticSkill> fs = new ArrayList<LinguisticSkill>();
        LinguisticSkill fs1 = new LinguisticSkill(new CodeLabel("ja"));
        List<LinguisticExperience> listExp = new ArrayList<LinguisticExperience>();
        LinguisticExperience exp = new LinguisticExperience();
        exp.setArea(new CodeLabel("mediating_groups_language"));
        listExp.add(ex);
        fs1.setAcquiredDuring(listExp);
        fs.add(fs1);
        Skills ss = new Skills();
        LinguisticSkills lss = new LinguisticSkills();
        lss.setForeignLanguage(fs);
        ss.setLinguistic(lss);
        learnerLinguistic.setSkills(ss);
        espLinguistic.setLearnerInfo(learnerLinguistic);

        espHeadline = new SkillsPassport();
        LearnerInfo learnerHeadline = new LearnerInfo();
        learnerHeadline.setHeadline(headline);
        learnerHeadline.setIdentification(identification);
        espHeadline.setLearnerInfo(learnerHeadline);

        espReplace = new SkillsPassport();
        LearnerInfo learnerReplace = new LearnerInfo();
        learnerReplace.setHeadline(new Headline(new CodeLabel("position", "POSITION"), new CodeLabel("bab5fa79-7f96-4e21-87b6-1eba560b8d9a", "BABIS")));
        espReplace.setLearnerInfo(learnerReplace);
    }

    @Test
    public void translateHeadline() throws IllegalArgumentException, IllegalAccessException {
        ReflectionUtils.deepTranslateTo(espHeadline, espHeadline, new Locale("el"));

        String headlineDesc = espHeadline.getLearnerInfo().getHeadline().getDescription().getLabel();
        Assert.assertThat(headlineDesc, CoreMatchers.is("Προτυποποιητής τριών διαστάσεων/προτυποποιήτρια τριών διαστάσεων"));
    }

    @Test
    public void translateAndReplaceHeadline() throws IllegalArgumentException, IllegalAccessException {
        ReflectionUtils.deepTranslateTo(espReplace, espReplace, new Locale("el"));

        String headlineDesc = espReplace.getLearnerInfo().getHeadline().getDescription().getLabel();
        Assert.assertThat(headlineDesc, CoreMatchers.is("Προτυποποιητής τριών διαστάσεων/προτυποποιήτρια τριών διαστάσεων"));

        String headlineType = espReplace.getLearnerInfo().getHeadline().getType().getLabel();
        Assert.assertThat("Headline Type", headlineType, CoreMatchers.is("ΕΠΑΓΓΕΛΜΑ"));
    }

    @Test
    public void translate() throws IllegalArgumentException, IllegalAccessException {

        ReflectionUtils.deepTranslateTo(esp, esp, new Locale("el"));

        String genderLabel = esp.getLearnerInfo().getIdentification().getDemographics().getGender().getLabel();
        Assert.assertThat("Gender", genderLabel, CoreMatchers.is("Θήλυ"));

        String firstNationality = esp.getLearnerInfo().getIdentification().getDemographics().getNationalityList().get(0).getLabel();
        Assert.assertThat("Nationality 1", firstNationality, CoreMatchers.is("Ελληνική"));

        String secondNationality = esp.getLearnerInfo().getIdentification().getDemographics().getNationalityList().get(1).getLabel();
        Assert.assertThat("Nationality 2", secondNationality, CoreMatchers.is("Βρετανική"));

        String headlineType = esp.getLearnerInfo().getHeadline().getType().getLabel();
        Assert.assertThat("Headline Type", headlineType, CoreMatchers.is("ΕΠΑΓΓΕΛΜΑ"));

        String headlineDesc = esp.getLearnerInfo().getHeadline().getDescription().getLabel();
        Assert.assertThat("Headline Description", headlineDesc, CoreMatchers.is("Προτυποποιητής τριών διαστάσεων/προτυποποιήτρια τριών διαστάσεων"));

        String firstEducation = esp.getLearnerInfo().getEducationList().get(0).getLevel().getLabel();
        Assert.assertThat("Education Level", firstEducation, CoreMatchers.is("ΕΠΠ επίπεδο 1"));

        String firstEducationOrg = esp.getLearnerInfo().getEducationList().get(0).getOrganisation().getContactInfo().getAddress().getContact().getCountry().getLabel();
        Assert.assertThat("Education Country", firstEducationOrg, CoreMatchers.is("Ελλάδα"));

        String firstEmploymentPos = esp.getLearnerInfo().getWorkExperienceList().get(0).getPosition().getLabel();
        Assert.assertThat("Employ Position", firstEmploymentPos, CoreMatchers.is("Δημιουργός τρισδιάστατης κινούμενης εικόνας με ψηφιακά μέσα"));

        String firstEmploymentSect = esp.getLearnerInfo().getWorkExperienceList().get(0).getEmployer().getSector().getLabel();
        Assert.assertThat("Employ Sector", firstEmploymentSect, CoreMatchers.is("Εκπαίδευση"));

        String firstEmploymentOrg = esp.getLearnerInfo().getWorkExperienceList().get(0).getEmployer().getContactInfo().getAddress().getContact().getCountry().getLabel();
        Assert.assertThat("Employ Country", firstEmploymentOrg.trim(), CoreMatchers.is("Φινλανδία"));

        String mother = esp.getLearnerInfo().getSkills().getLinguistic().getMotherTongue().get(0).getDescription().getLabel();
        Assert.assertThat("Mother", mother, CoreMatchers.is("ελληνικά"));

        String foreign = esp.getLearnerInfo().getSkills().getLinguistic().getForeignLanguage().get(0).getDescription().getLabel();
        Assert.assertThat("Foreign", foreign, CoreMatchers.is("ιαπωνικά"));

        String achievement = esp.getLearnerInfo().getAchievementList().get(0).getTitle().getLabel();
        Assert.assertThat("Achievement Title", achievement, CoreMatchers.is("Δημοσιεύσεις"));
    }

    @Test
    public void translateForeignLanguageArea() throws IllegalArgumentException, IllegalAccessException {
        ReflectionUtils.deepTranslateTo(espLinguistic, espLinguistic, new Locale("el"));

        String areaLabel = espLinguistic.getLearnerInfo().getSkills().getLinguistic().getForeignLanguage().get(0).getAcquiredDuring().get(0).getArea().getLabel();
        Assert.assertThat(areaLabel, CoreMatchers.is("Διαμεσολάβηση μεταξύ γλωσσών"));
    }

}
