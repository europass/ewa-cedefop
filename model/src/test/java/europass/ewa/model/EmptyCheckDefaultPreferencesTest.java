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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import europass.ewa.model.conversion.ModelModule;

@Ignore
@Deprecated
//see EWA-651
public class EmptyCheckDefaultPreferencesTest {

    static Map<String, PrintingPreference> cvBundleMap;

    static Map<String, PrintingPreference> lpBundleMap;

    static Map<String, PrintingPreference> clBundleMap;

    private static Injector injector = null;

    @BeforeClass
    public static void setDefaultPrefs() {
        injector = Guice.createInjector(
                new AbstractModule() {
            @Override
            protected void configure() {
                bindConstant().annotatedWith(Names.named(Namespace.SCHEMA_LOCATION_DEFAULT_PARAM))
                        .to(Namespace.NAMESPACE + " http://europass.cedefop.europa.eu/xml/EuropassSchema_V3.0.xsd");
            }
        },
                new ModelModule()
        );

        cvBundleMap
                = injector.getProvider(Key.get(new TypeLiteral<Map<String, PrintingPreference>>() {
                },
                        Names.named(ModelModule.DEFAULT_CV_PREFS))).get();
        lpBundleMap
                = injector.getProvider(Key.get(new TypeLiteral<Map<String, PrintingPreference>>() {
                },
                        Names.named(ModelModule.DEFAULT_LP_PREFS))).get();

        clBundleMap
                = injector.getProvider(Key.get(new TypeLiteral<Map<String, PrintingPreference>>() {
                },
                        Names.named(ModelModule.DEFAULT_CL_PREFS))).get();
    }

    @Test
    public void emptyCV() {
        SkillsPassport model = new SkillsPassport();
        model.activatePreferences("ECV", cvBundleMap);
        model.applyDefaultPreferences("ECV");

        List<PrintingPreference> prefs = model.getDocumentPrintingPrefs().get("ECV");

        PrintingPreference telListPref = new PrintingPreference("LearnerInfo.Identification.ContactInfo.Telephone", false);
        Assert.assertThat("Telephone List is not visible, although visible by default, because it is left empty", prefs.contains(telListPref), CoreMatchers.is(true));

        PrintingPreference webListPref = new PrintingPreference("LearnerInfo.Identification.ContactInfo.Website", false);
        Assert.assertThat("Website List is not visible because empty", prefs.contains(webListPref), CoreMatchers.is(true));

        PrintingPreference genderPref = new PrintingPreference("LearnerInfo.Identification.Demographics.Birthdate", false, null, "text/short", null);
        Assert.assertThat("Gender is not visible because empty", prefs.contains(genderPref), CoreMatchers.is(true));

        PrintingPreference natListPref = new PrintingPreference("LearnerInfo.Identification.Demographics.Nationality", false);
        Assert.assertThat("Nationality List is not visible because empty", prefs.contains(natListPref), CoreMatchers.is(true));

        PrintingPreference achListPref = new PrintingPreference("LearnerInfo.Achievement", false);
        Assert.assertThat("Achievement List is not visible because empty", prefs.contains(achListPref), CoreMatchers.is(true));

        PrintingPreference achRefListPref = new PrintingPreference("LearnerInfo.Achievement[0].ReferenceTo", false);
        Assert.assertThat("Attachment Lists of Achievement List is not visible because empty", prefs.contains(achRefListPref), CoreMatchers.is(true));

    }

    @Test
    public void cvWithNonDefaultSections() {
        SkillsPassport model = MockObjects.espHiddenOnly();
        model.activatePreferences("ECV", cvBundleMap);
        model.applyDefaultPreferences("ECV");

        List<PrintingPreference> prefs = model.getDocumentPrintingPrefs().get("ECV");

        PrintingPreference photoPref = new PrintingPreference("LearnerInfo.Identification.Photo", true);
        Assert.assertThat("Photo is visible, although not visible by default, because it is not empty", prefs.contains(photoPref), CoreMatchers.is(true));

        PrintingPreference telListPref = new PrintingPreference("LearnerInfo.Identification.ContactInfo.Telephone", false);
        Assert.assertThat("Telephone List is not visible, although visible by default, because it is empty", prefs.contains(telListPref), CoreMatchers.is(true));

        PrintingPreference webListPref = new PrintingPreference("LearnerInfo.Identification.ContactInfo.Website", true);
        Assert.assertThat("Website List is visible because is non empty", prefs.contains(webListPref), CoreMatchers.is(true));

        PrintingPreference imListPref = new PrintingPreference("LearnerInfo.Identification.ContactInfo.InstantMessaging", true);
        Assert.assertThat("InstantMessaging List is visible because is non empty", prefs.contains(imListPref), CoreMatchers.is(true));

        PrintingPreference genderPref = new PrintingPreference("LearnerInfo.Identification.Demographics.Birthdate", true, null, "text/short", null);
        Assert.assertThat("Gender is visible because is non empty", prefs.contains(genderPref), CoreMatchers.is(true));

        Assert.assertThat("Three nationalities", model.getLearnerInfo().getIdentification().getDemographics().getNationalityList().size(), CoreMatchers.is(3));

        PrintingPreference natListPref = new PrintingPreference("LearnerInfo.Identification.Demographics.Nationality", true);
        Assert.assertThat("Nationality List is visible because is non empty", prefs.contains(natListPref), CoreMatchers.is(true));

        PrintingPreference nat2Pref = new PrintingPreference("LearnerInfo.Identification.Demographics.Nationality[2]", true);
        Assert.assertThat("3rd Nationality is visible because is non empty", prefs.contains(nat2Pref), CoreMatchers.is(true));

        PrintingPreference workEmplAddr = new PrintingPreference("LearnerInfo.WorkExperience[0].Employer.ContactInfo.Address", true, null, "s p-z m (c)", null);
        Assert.assertThat("Employer Address of 1st Work Experience because is non empty", prefs.contains(workEmplAddr), CoreMatchers.is(true));

        PrintingPreference workEmplWebsite = new PrintingPreference("LearnerInfo.WorkExperience[0].Employer.ContactInfo.Website", true);
        Assert.assertThat("Employer ContactInfo Website of 1st Work Experience because is non empty", prefs.contains(workEmplWebsite), CoreMatchers.is(true));

        PrintingPreference workEmpl = new PrintingPreference("LearnerInfo.WorkExperience[0].Employer", true);
        Assert.assertThat("Employer of 1st Work Experience because is non empty", prefs.contains(workEmpl), CoreMatchers.is(true));

        PrintingPreference workEmplSector = new PrintingPreference("LearnerInfo.WorkExperience[0].Employer.Sector", true);
        Assert.assertThat("Employer Sector of 1st Work Experience because is non empty", prefs.contains(workEmplSector), CoreMatchers.is(true));

        PrintingPreference refEmpl = new PrintingPreference("LearnerInfo.WorkExperience[0].ReferenceTo", true);
        Assert.assertThat("Documentation of 1st Work Experience because is non empty", prefs.contains(refEmpl), CoreMatchers.is(true));

        PrintingPreference educOrgAddr = new PrintingPreference("LearnerInfo.Education[0].Organisation.ContactInfo.Address", true, null, "s p-z m (c)", null);
        Assert.assertThat("Education Organization Address of 1st Education Experience because is non empty", prefs.contains(educOrgAddr), CoreMatchers.is(true));

        PrintingPreference eduTitle = new PrintingPreference("LearnerInfo.Education[0].Title", true);
        Assert.assertThat("Education Title of 1st Education Experience because is non empty", prefs.contains(eduTitle), CoreMatchers.is(true));

        PrintingPreference educOrgLevel = new PrintingPreference("LearnerInfo.Education[0].Level", true);
        Assert.assertThat("Education Organization Level of 1st Education Experience because is non empty", prefs.contains(educOrgLevel), CoreMatchers.is(true));

        PrintingPreference educField = new PrintingPreference("LearnerInfo.Education[0].Field", true);
        Assert.assertThat("Education Field of 1st Education Experience because is non empty", prefs.contains(educField), CoreMatchers.is(true));

        PrintingPreference refEdu = new PrintingPreference("LearnerInfo.Education[0].ReferenceTo", true);
        Assert.assertThat("Related Documents of 1st Education Experience because is non empty", prefs.contains(refEdu), CoreMatchers.is(true));

        PrintingPreference refEdu1 = new PrintingPreference("LearnerInfo.Education[0].ReferenceTo[0]", true);
        Assert.assertThat("1st Related Document of 1st Education Experience because is non empty", prefs.contains(refEdu1), CoreMatchers.is(true));

        PrintingPreference refEdu2 = new PrintingPreference("LearnerInfo.Education[0].ReferenceTo[1]", true);
        Assert.assertThat("2nd Related Document of 1st Education Experience because is non empty", prefs.contains(refEdu2), CoreMatchers.is(true));

        PrintingPreference cert1Lang1 = new PrintingPreference("LearnerInfo.Skills.Linguistic.ForeignLanguage[0].Certificate[0]", true);
        Assert.assertThat("1st Certificate of 1st Foreign Language because is non empty", prefs.contains(cert1Lang1), CoreMatchers.is(true));

        PrintingPreference exp1Lang1 = new PrintingPreference("LearnerInfo.Skills.Linguistic.ForeignLanguage[0].Experience[0]", false);
        Assert.assertThat("1st Experience of 1st Foreign Language because is non empty", prefs.contains(exp1Lang1), CoreMatchers.is(true));

        PrintingPreference refLang = new PrintingPreference("LearnerInfo.Skills.Linguistic.ForeignLanguage[0].ReferenceTo", true);
        Assert.assertThat("Related Documents of 1st Foreign Language because is non empty", prefs.contains(refLang), CoreMatchers.is(true));

        PrintingPreference refLang1 = new PrintingPreference("LearnerInfo.Skills.Linguistic.ForeignLanguage[0].ReferenceTo[0]", true);
        Assert.assertThat("1st Related Document of 1st Foreign Language because is non empty", prefs.contains(refLang1), CoreMatchers.is(true));

        PrintingPreference skillComm = new PrintingPreference("LearnerInfo.Skills.Communication", true);
        Assert.assertThat("Communication Skill because is non empty", prefs.contains(skillComm), CoreMatchers.is(true));

        PrintingPreference skillCommRef = new PrintingPreference("LearnerInfo.Skills.Communication.ReferenceTo", true);
        Assert.assertThat("Communication Skill References  because is non empty", prefs.contains(skillCommRef), CoreMatchers.is(true));

        PrintingPreference skillCommRef1 = new PrintingPreference("LearnerInfo.Skills.Communication.ReferenceTo[0]", true);
        Assert.assertThat("Communication Skill References 1 because is non empty", prefs.contains(skillCommRef1), CoreMatchers.is(true));

        PrintingPreference skillOrg = new PrintingPreference("LearnerInfo.Skills.Organisational", true);
        Assert.assertThat("Organisational Skill because is non empty", prefs.contains(skillOrg), CoreMatchers.is(true));

        PrintingPreference skillOrgRef = new PrintingPreference("LearnerInfo.Skills.Organisational.ReferenceTo", true);
        Assert.assertThat("Organisational Skill References because is non empty", prefs.contains(skillOrgRef), CoreMatchers.is(true));

        PrintingPreference skillOrgRef1 = new PrintingPreference("LearnerInfo.Skills.Organisational.ReferenceTo[0]", true);
        Assert.assertThat("Organisational Skill References 1 because is non empty", prefs.contains(skillOrgRef1), CoreMatchers.is(true));

        PrintingPreference skillJob = new PrintingPreference("LearnerInfo.Skills.JobRelated", true);
        Assert.assertThat("Job Related Skill because is non empty", prefs.contains(skillJob), CoreMatchers.is(true));

        PrintingPreference skillJobRef = new PrintingPreference("LearnerInfo.Skills.JobRelated.ReferenceTo", true);
        Assert.assertThat("Job Related Skill References because is non empty", prefs.contains(skillJobRef), CoreMatchers.is(true));

        PrintingPreference skillJobRef1 = new PrintingPreference("LearnerInfo.Skills.JobRelated.ReferenceTo[0]", true);
        Assert.assertThat("Job Related Skill References 1 because is non empty", prefs.contains(skillJobRef1), CoreMatchers.is(true));

        PrintingPreference skillComp = new PrintingPreference("LearnerInfo.Skills.Computer", true);
        Assert.assertThat("Computer Skill because is non empty", prefs.contains(skillComp), CoreMatchers.is(true));

        PrintingPreference skillCompRef = new PrintingPreference("LearnerInfo.Skills.Computer.ReferenceTo", true);
        Assert.assertThat("Computer Skill References because is non empty", prefs.contains(skillCompRef), CoreMatchers.is(true));

        PrintingPreference skillCompRef1 = new PrintingPreference("LearnerInfo.Skills.Computer.ReferenceTo[0]", true);
        Assert.assertThat("Computer Skill References 1 because is non empty", prefs.contains(skillCompRef1), CoreMatchers.is(true));

        PrintingPreference skillOther = new PrintingPreference("LearnerInfo.Skills.Other", true);
        Assert.assertThat("Other Skill because is non empty", prefs.contains(skillOther), CoreMatchers.is(true));

        PrintingPreference skillOtherRef = new PrintingPreference("LearnerInfo.Skills.Other.ReferenceTo", true);
        Assert.assertThat("Other Skill References because is non empty", prefs.contains(skillOtherRef), CoreMatchers.is(true));

        PrintingPreference skillOtherRef1 = new PrintingPreference("LearnerInfo.Skills.Other.ReferenceTo[0]", true);
        Assert.assertThat("Other Skill References 1 because is non empty", prefs.contains(skillOtherRef1), CoreMatchers.is(true));

        PrintingPreference skillDriving = new PrintingPreference("LearnerInfo.Skills.Driving", true);
        Assert.assertThat("Driving Skill because is non empty", prefs.contains(skillDriving), CoreMatchers.is(true));

        PrintingPreference skillDrivingRef = new PrintingPreference("LearnerInfo.Skills.Driving.ReferenceTo", true);
        Assert.assertThat("Driving Skill References because is non empty", prefs.contains(skillDrivingRef), CoreMatchers.is(true));

        PrintingPreference skillDrivingRef1 = new PrintingPreference("LearnerInfo.Skills.Driving.ReferenceTo[0]", true);
        Assert.assertThat("Driving Skill References 1 because is non empty", prefs.contains(skillDrivingRef1), CoreMatchers.is(true));

        PrintingPreference achListPref = new PrintingPreference("LearnerInfo.Achievement", true);
        Assert.assertThat("Achievement List is visible because is non empty", prefs.contains(achListPref), CoreMatchers.is(true));

        PrintingPreference ach2Pref = new PrintingPreference("LearnerInfo.Achievement[1]", true);
        Assert.assertThat("2nd Achievement is visible because is non empty", prefs.contains(ach2Pref), CoreMatchers.is(true));

        PrintingPreference achRefListPref = new PrintingPreference("LearnerInfo.Achievement[1].ReferenceTo", true);
        Assert.assertThat("Attachment Lists of Achievement List is visible because is non empty", prefs.contains(achRefListPref), CoreMatchers.is(true));

        PrintingPreference ach2Ref2Pref = new PrintingPreference("LearnerInfo.Achievement[1].ReferenceTo[1]", true);
        Assert.assertThat("2nd Attachment of 2nd Achievement is visible because is non empty", prefs.contains(ach2Ref2Pref), CoreMatchers.is(true));

        PrintingPreference refList = new PrintingPreference("LearnerInfo.ReferenceTo", true);
        Assert.assertThat("ReferenceTo List is visible because is non empty", prefs.contains(refList), CoreMatchers.is(true));

        PrintingPreference cefl = new PrintingPreference("LearnerInfo.CEFLanguageLevelsGrid", false);
        Assert.assertThat("Self Assesment for Foreign Language 1 is visible because is non empty", prefs.contains(cefl), CoreMatchers.is(true));

    }

    @Test
    public void lpWithNonDefaultSections() {
        SkillsPassport model = MockObjects.espHiddenOnly();
        model.activatePreferences("ELP", lpBundleMap);
        model.applyDefaultPreferences("ELP");

        List<PrintingPreference> prefs = model.getDocumentPrintingPrefs().get("ELP");

        PrintingPreference genderPref = new PrintingPreference("LearnerInfo.Identification.Demographics.Birthdate", true, null, "text/short", null);
        Assert.assertThat("Gender is visible because non empty", prefs.contains(genderPref), CoreMatchers.is(true));

        PrintingPreference nat2Pref = new PrintingPreference("LearnerInfo.Identification.Demographics.Nationality[2]", true);
        Assert.assertThat("3rd Nationality is visible because non empty", prefs.contains(nat2Pref), CoreMatchers.is(true));

        //-- Foreign Lang 1
        PrintingPreference lang1 = new PrintingPreference("LearnerInfo.Skills.Linguistic.ForeignLanguage[0]", true);
        Assert.assertThat("1st Foreign Language because non empty", prefs.contains(lang1), CoreMatchers.is(true));

        PrintingPreference cert1Lang1 = new PrintingPreference("LearnerInfo.Skills.Linguistic.ForeignLanguage[0].Certificate[0]", true);
        Assert.assertThat("1st Certificate of 1st Foreign Language because non empty", prefs.contains(cert1Lang1), CoreMatchers.is(true));

        PrintingPreference exp1Lang1 = new PrintingPreference("LearnerInfo.Skills.Linguistic.ForeignLanguage[0].Experience[0]", false);
        Assert.assertThat("1st Experience of 1st Foreign Language is hidden because empty", prefs.contains(exp1Lang1), CoreMatchers.is(true));

        PrintingPreference refLang1 = new PrintingPreference("LearnerInfo.Skills.Linguistic.ForeignLanguage[0].ReferenceTo", true);
        Assert.assertThat("Related Documents of 1st Foreign Language because non empty", prefs.contains(refLang1), CoreMatchers.is(true));

        PrintingPreference ref1Lang1 = new PrintingPreference("LearnerInfo.Skills.Linguistic.ForeignLanguage[0].ReferenceTo[0]", true);
        Assert.assertThat("1st Related Document of 1st Foreign Language because non empty", prefs.contains(ref1Lang1), CoreMatchers.is(true));

        //-- Foreign Lang 2
        PrintingPreference lang2 = new PrintingPreference("LearnerInfo.Skills.Linguistic.ForeignLanguage[1]", true);
        Assert.assertThat("2nd Foreign Language because non empty", prefs.contains(lang2), CoreMatchers.is(true));

        PrintingPreference expLang2 = new PrintingPreference("LearnerInfo.Skills.Linguistic.ForeignLanguage[1].Experience", false);
        Assert.assertThat("Experience of 2nd Foreign Language because empty", prefs.contains(expLang2), CoreMatchers.is(true));

        PrintingPreference exp1Lang2 = new PrintingPreference("LearnerInfo.Skills.Linguistic.ForeignLanguage[1].Experience[0]", false);
        Assert.assertThat("1st Experience of 2nd Foreign Language because empty", prefs.contains(exp1Lang2), CoreMatchers.is(true));

        PrintingPreference refLang2 = new PrintingPreference("LearnerInfo.Skills.Linguistic.ForeignLanguage[1].ReferenceTo", false);
        Assert.assertThat("Related Documents of 1st Foreign Language because empty", prefs.contains(refLang2), CoreMatchers.is(true));

        PrintingPreference ref1Lang2 = new PrintingPreference("LearnerInfo.Skills.Linguistic.ForeignLanguage[1].ReferenceTo[0]", false);
        Assert.assertThat("1st Related Document of 1st Foreign Language because empty", prefs.contains(ref1Lang2), CoreMatchers.is(true));

        PrintingPreference cefl = new PrintingPreference("LearnerInfo.CEFLanguageLevelsGrid", true);
        Assert.assertThat("Self Assesment for Foreign Language 1 is visible because non empty", prefs.contains(cefl), CoreMatchers.is(true));

    }

    @Test
    public void cvWithNonDefaultSectionsAndSomePrefs() {
        SkillsPassport model = MockObjects.espHiddenOnly();

        //-- Add telephones
        ContactInfo contactInfo = model.getLearnerInfo().getIdentification().getContactInfo();
        List<ContactMethod> tels = new ArrayList<ContactMethod>();
        tels.add(new ContactMethod(new CodeLabel("work", "Work"), "2108029409"));
        tels.add(new ContactMethod(new CodeLabel("mobile", "Mobile"), "6945000100"));
        contactInfo.setTelephoneList(tels);

        //-- Add some initial prefs
        List<PrintingPreference> prefs = new ArrayList<PrintingPreference>();
        prefs.add(new PrintingPreference("LearnerInfo.Identification.ContactInfo.Telephone", false));
        //prefs.add( new PrintingPreference( "LearnerInfo.Identification.ContactInfo.Telephone[0]", false ) );
        prefs.add(new PrintingPreference("LearnerInfo.Identification.ContactInfo.Telephone[1]", false));
        prefs.add(new PrintingPreference("LearnerInfo.Identification.ContactInfo.Website", false));
        //prefs.add( new PrintingPreference( "LearnerInfo.Identification.ContactInfo.Website[0]", false ) );
        prefs.add(new PrintingPreference("LearnerInfo.Identification.ContactInfo.Website[1]", false));
        prefs.add(new PrintingPreference("LearnerInfo.Skills.Other", false));
        prefs.add(new PrintingPreference("LearnerInfo.Skills.Communication.ReferenceTo[0]", false));

        Map<String, List<PrintingPreference>> prefMap = new HashMap<String, List<PrintingPreference>>();
        prefMap.put("ECV", prefs);
        model.setDocumentPrintingPrefs(prefMap);

        model.activatePreferences("ECV", cvBundleMap);
        model.applyDefaultPreferences("ECV");

        // -- Assertions
        List<PrintingPreference> finalPrefs = model.getDocumentPrintingPrefs().get("ECV");

        PrintingPreference telListPref = new PrintingPreference("LearnerInfo.Identification.ContactInfo.Telephone", false);
        Assert.assertThat("Telephone List is NOT visible, as stated in the Prefs", finalPrefs.contains(telListPref), CoreMatchers.is(true));

        PrintingPreference tel1Pref = new PrintingPreference("LearnerInfo.Identification.ContactInfo.Telephone[0]", true);
        Assert.assertThat("First Telephone is visible, from default", finalPrefs.contains(tel1Pref), CoreMatchers.is(true));

        PrintingPreference tel2Pref = new PrintingPreference("LearnerInfo.Identification.ContactInfo.Telephone[1]", false);
        Assert.assertThat("Second Telephone is NOT visible, as stated in the Prefs", finalPrefs.contains(tel2Pref), CoreMatchers.is(true));

        PrintingPreference webListPref = new PrintingPreference("LearnerInfo.Identification.ContactInfo.Website", false);
        Assert.assertThat("Website List is NOT visible, as stated in the Prefs", finalPrefs.contains(webListPref), CoreMatchers.is(true));

        PrintingPreference web1Pref = new PrintingPreference("LearnerInfo.Identification.ContactInfo.Website[0]", true);
        Assert.assertThat("First Website is visible, because non empty", finalPrefs.contains(web1Pref), CoreMatchers.is(true));

        PrintingPreference web2Pref = new PrintingPreference("LearnerInfo.Identification.ContactInfo.Website[1]", false);
        Assert.assertThat("Second Website is NOT visible, as stated in the Prefs", finalPrefs.contains(web2Pref), CoreMatchers.is(true));

        PrintingPreference otherSkillPref = new PrintingPreference("LearnerInfo.Skills.Other", false);
        Assert.assertThat("Other Skills NOT visible, as stated in the Prefs", finalPrefs.contains(otherSkillPref), CoreMatchers.is(true));

        PrintingPreference ref1CommSkillPref = new PrintingPreference("LearnerInfo.Skills.Communication.ReferenceTo[0]", false);
        Assert.assertThat("Reference 1 of Communication Skills NOT visible, as stated in the Prefs", finalPrefs.contains(ref1CommSkillPref), CoreMatchers.is(true));
    }

    @Test
    public void emptyCL() {
        SkillsPassport model = new SkillsPassport();
        model.activatePreferences("ECL", clBundleMap);
        model.applyDefaultPreferences("ECL");

        List<PrintingPreference> prefs = model.getDocumentPrintingPrefs().get("ECL");

        PrintingPreference personName = new PrintingPreference("LearnerInfo.Identification.PersonName", false, "FirstName Surname", null, null);
        Assert.assertThat("PersonName not visible, because it is left empty", prefs.contains(personName), CoreMatchers.is(true));

        PrintingPreference address = new PrintingPreference("CoverLetter.Addressee", false, null, null, "left-align");
        Assert.assertThat("Address not visible, because it is left empty", prefs.contains(address), CoreMatchers.is(true));
    }
}
