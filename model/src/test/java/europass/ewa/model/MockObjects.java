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
import java.util.Locale;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import europass.ewa.enums.EuropassDocumentType;
import java.util.LinkedHashMap;

public class MockObjects {

    public static SkillsPassport emptyCV() {
        SkillsPassport esp = new SkillsPassport();
        esp.setLocale(Locale.ITALIAN);

        DocumentInfo info = new DocumentInfo();
        info.setDocumentType(EuropassDocumentType.ECV);
        info.setGenerator("JUnit Test");

        esp.setDocumentInfo(info);

        return esp;
    }

    public static SkillsPassport referenceToESP() {
        SkillsPassport esp = new SkillsPassport();

        esp.updateDocumentType(EuropassDocumentType.ECV_ESP);

        //Printing Preferences
        List<PrintingPreference> prefs = new ArrayList<PrintingPreference>();

        prefs.add(new PrintingPreference("LearnerInfo", true, "Identification Headline WorkExperience Education Skills Achievement ReferenceTo", null, null));
        prefs.add(new PrintingPreference("LearnerInfo.Identification.PersonName", true, "FirstName Surname", null, null));
        prefs.add(new PrintingPreference("LearnerInfo.WorkExperience", true));
        prefs.add(new PrintingPreference("LearnerInfo.WorkExperience[0]", true));
        prefs.add(new PrintingPreference("LearnerInfo.WorkExperience[0].ReferenceTo", true));
        prefs.add(new PrintingPreference("LearnerInfo.WorkExperience[0].ReferenceTo[0]", true));
        prefs.add(new PrintingPreference("LearnerInfo.ReferenceTo", true));
        prefs.add(new PrintingPreference("LearnerInfo.ReferenceTo[0]", true));
        prefs.add(new PrintingPreference("LearnerInfo.ReferenceTo[1]", true));

        Map<String, List<PrintingPreference>> map = new HashMap<String, List<PrintingPreference>>();
        map.put("ECV", prefs);

        esp.setDocumentPrintingPrefs(map);

        return esp;
    }

    public static SkillsPassport completeNoPrefs() {
        SkillsPassport esp = complete();
        esp.setDocumentPrintingPrefs(null);
        return esp;
    }

    public static SkillsPassport cleanupRefs() {
        SkillsPassport esp = new SkillsPassport();

        esp.setLocale(Locale.ENGLISH);

        //Work Experience
        WorkExperience work1 = new WorkExperience();
        work1.setPosition(new CodeLabel("R", "Martial Arts Instructor"));
        work1.setActivities("<div class=\"dummy-root\"><b>Activity 1</b><br/><i>Activity 2</i><p>While monitoring other stuff.</p><br/><p>And during supervising some other stuff.</p></div>");
        JDate work1From = new JDate();
        work1From.setYear(2010);
        work1From.setMonth(12);
        work1.setPeriod(new Period(work1From, true));

        OrganisationalContactInfo work1OrgContact = new OrganisationalContactInfo();
        work1OrgContact.setAddress(new ContactAddress(new Address(new CodeLabel("JP", "Japan"))));
        work1OrgContact.setWebsite(new ContactMethod(new CodeLabel("business"), "http://mybusiness.com"));
        EmployerOrganisation work1empl = new EmployerOrganisation("School of Ninjutsu", work1OrgContact, new CodeLabel("Q", "Human health and social work"));
        work1.setEmployer(work1empl);
        List<ReferenceTo> work1Doc = new ArrayList<ReferenceTo>();
        work1Doc.add(new ReferenceTo("ATT_1"));
        work1Doc.add(new ReferenceTo("ATT_3"));
        work1.setReferenceToList(work1Doc);

        List<WorkExperience> worklist = new ArrayList<WorkExperience>();
        worklist.add(work1);

        //Education
        Education edu1 = new Education();
        edu1.setTitle("Computer Science");
        edu1.setActivities("<ul><li>First List Item: <u>underlined</u></li><li>Second List Item:<p><a href=\"http://nested.com\">nested link</a><b> Bold Text:<a href=\"http://nested.com\">Second nested Link <i>click here</i> <em>!!!</em></a></b></p></li></ul>");
        JDate edu1From = new JDate();
        edu1From.setYear(2008);
        edu1From.setMonth(5);
        edu1.setPeriod(new Period(edu1From, true));

        OrganisationalContactInfo edu1OrgContact = new OrganisationalContactInfo();
        edu1OrgContact.setAddress(new ContactAddress(new Address(new CodeLabel("EL", "Hellas"))));
        edu1.setOrganisation(new Organisation("University of Aegean", edu1OrgContact));

        edu1.setField(new CodeLabel("5", "Engineering"));
        List<ReferenceTo> edu1Doc = new ArrayList<ReferenceTo>();
        edu1Doc.add(new ReferenceTo("ATT_1"));
        edu1Doc.add(new ReferenceTo("ATT_2"));
        edu1Doc.add(new ReferenceTo("ATT_3"));
        edu1.setReferenceToList(edu1Doc);

        List<Education> educationlist = new ArrayList<Education>();
        educationlist.add(edu1);

        //Skills
        Skills skills = new Skills();
        skills.setCommunication(new GenericSkill("<div class=\"dummy-root\"><b>Communication skills:</b> <ul><li>bla</li><li>blah</li></ul></div>"));
        skills.setOrganisational(new GenericSkill("<div class=\"dummy-root\"><b>Organisational skills:</b> <ul><li>bla</li><li>blah</li></ul></div>"));
        skills.setJobRelated(new GenericSkill("<div class=\"dummy-root\"><b>Job-Related skills:</b> <ul><li>bla</li><li>blah</li></ul></div>"));

        //skills.setComputer(new ComputerSkill("<div class=\"dummy-root\"><b>Computer skills:</b> <ul><li>bla</li><li>blah</li></ul></div>"));
        //computer skills with ict assessment
        ComputerSkill ictSkills = new ComputerSkill();
        ictSkills.setDescription("Object oriented computer programming skills");
        ictSkills.setProficiencyLevel(new ICTLevel("A", "B", "C", "A", ""));
        List<Certificate<String>> certificatesIct = new ArrayList<Certificate<String>>();
        certificatesIct.add(new Certificate<String>("ACDL"));
        certificatesIct.add(new Certificate<String>("Java Certification"));
        ictSkills.setVerifiedBy(certificatesIct);
        List<ReferenceTo> ictDoc = new ArrayList<ReferenceTo>();
        ictDoc.add(new ReferenceTo("ATT_5"));
        ictSkills.setReferenceToList(ictDoc);
        //end computer skills

        skills.setOther(new GenericSkill("<div class=\"dummy-root\"><b>Other skills:</b> <ul><li>bla</li><li>blah</li></ul></div>"));
        List<String> driving = new ArrayList<String>();
        driving.add("A");
        driving.add("B1");
        skills.setDriving(new DrivingSkill(driving));

        List<LinguisticSkill> mother = new ArrayList<LinguisticSkill>();
        mother.add(new LinguisticSkill(new CodeLabel("el", "Greek")));

        List<LinguisticSkill> foreign = new ArrayList<LinguisticSkill>();
        LinguisticSkill foreign1 = new LinguisticSkill(new CodeLabel("en", "English"));
        foreign1.setProficiencyLevel(new CEFRLevel("C1", "C2", "B2", "B2", "B1"));
        List<Certificate<String>> certificates1 = new ArrayList<Certificate<String>>();
        certificates1.add(new Certificate<String>("Cambridge Certificate of Proficiency in English"));
        certificates1.add(new Certificate<String>("Michigan Certificate of Proficiency in English"));
        foreign1.setVerifiedBy(certificates1);
        List<ReferenceTo> foreignDoc = new ArrayList<ReferenceTo>();
        foreignDoc.add(new ReferenceTo("ATT_5"));
        foreign1.setReferenceToList(foreignDoc);

        foreign.add(foreign1);

        skills.setLinguistic(new LinguisticSkills(mother, foreign));

        //Achievements
        List<Achievement> achievements = new ArrayList<Achievement>();
        achievements.add(new Achievement(new CodeLabel("projects", "Projects"), "<ul><li>Project 1</li><li>Project 2</li><li>Project 3</li></ul>"));
        achievements.add(new Achievement(new CodeLabel("memberships", "memberships"), "<ul><li>Membership 1</li><li>Membership 2</li><li>Membership 3</li></ul>"));
        achievements.add(new Achievement(new CodeLabel("references", "References"), "<ul><li>Reference 1</li><li>Reference 2</li><li>Reference 3</li></ul>"));
        achievements.add(new Achievement(new CodeLabel("seminars", "seminars"), "<ul><li>Seminar 1</li><li>Seminar 2</li><li>Seminar 3</li></ul>"));
        Achievement participations = new Achievement(new CodeLabel(null, "Participations"), "<ul><li>Theatre 1</li><li>Theatre 2</li><li>Theatre 3</li></ul>");
        List<ReferenceTo> partDoc = new ArrayList<ReferenceTo>();
        partDoc.add(new ReferenceTo("ATT_4"));
        partDoc.add(new ReferenceTo("ATT_5"));
        participations.setReferenceToList(partDoc);
        achievements.add(participations);

        //Documentation
        List<ReferenceTo> annexes = new ArrayList<ReferenceTo>();
        annexes.add(new ReferenceTo("ATT_1"));
        annexes.add(new ReferenceTo("ATT_2"));
        annexes.add(new ReferenceTo("ATT_3"));
        annexes.add(new ReferenceTo("ATT_4"));
        annexes.add(new ReferenceTo("ATT_5"));

        LearnerInfo learnerinfo = new LearnerInfo();
        learnerinfo.setEducationList(educationlist);
        learnerinfo.setWorkExperienceList(worklist);
        learnerinfo.setSkills(skills);
        learnerinfo.setAchievementList(achievements);
        learnerinfo.setDocumentation(annexes);

        //Attachments
        List<Attachment> attachments = new ArrayList<Attachment>();
        attachments.add(new Attachment("ATT_1", "My Scanned Diploma", "Diploma.pdf", "application/pdf", "http://europass.instore.gr/ewars/photo/WR3452ER/UTT7534", "My Scanned Diploma".getBytes()));
//		attachments.add( new Attachment( "ATT_2", "My Scanned Msc Degree", "Degree.pdf","application/pdf", "http://europass.instore.gr/ewars/photo/WR3882OO/UPE7434", "My Scanned Msc Degree".getBytes() ) );
        attachments.add(new Attachment("ATT_3", "Volunteeting Experience", "Experience.pdf", "application/pdf", "http://europass.instore.gr/ewars/photo/WT4322OO/UCD2434", "Volunteeting Experience".getBytes()));
        attachments.add(new Attachment("ATT_4", "Participation to WorldWide Conference", "Conference.pdf", "application/pdf", "http://europass.instore.gr/ewars/photo/WT4322OO/OPD2569", "Participation to WorldWide Conference".getBytes()));
//		attachments.add( new Attachment( "ATT_5", "Participation to Hellenic Seminar", "Seminar.pdf","application/pdf", "http://europass.instore.gr/ewars/photo/WT4322OO/DFD2852", "Participation to Hellenic Seminar".getBytes() ) );

        //DocumentInfo
        DateTime d = new DateTime(2012, 6, 6, 0, 0, DateTimeZone.UTC);
        DocumentInfo docInfo = new DocumentInfo(EuropassDocumentType.ECV, d, d, "V3.0", "EWA", "Comments", "true");

        //Printing Preferences
        List<PrintingPreference> prefs = new ArrayList<PrintingPreference>();
        prefs.add(new PrintingPreference("LearnerInfo", true, "Identification Headline WorkExperience Education Skills Achievement Documentation", null, null));
        prefs.add(new PrintingPreference("LearnerInfo.Identification.PersonName", true, "FirstName Surname", null, null));
        prefs.add(new PrintingPreference("LearnerInfo.Identification.ContactInfo.Address", true, null, "s \\n p-z m (c)", null));
        prefs.add(new PrintingPreference("LearnerInfo.Identification.ContactInfo.Telephone", true));
        prefs.add(new PrintingPreference("LearnerInfo.Identification.ContactInfo.Telephone[0]", true));
        prefs.add(new PrintingPreference("LearnerInfo.Identification.ContactInfo.Telephone[1]", true));
        prefs.add(new PrintingPreference("LearnerInfo.Identification.ContactInfo.Email", true));
        prefs.add(new PrintingPreference("LearnerInfo.Identification.ContactInfo.Website", true));
        prefs.add(new PrintingPreference("LearnerInfo.Identification.ContactInfo.Website[0]", true));
        prefs.add(new PrintingPreference("LearnerInfo.Identification.ContactInfo.Website[1]", true));
        prefs.add(new PrintingPreference("LearnerInfo.Identification.ContactInfo.InstantMessaging", true));
        prefs.add(new PrintingPreference("LearnerInfo.Identification.ContactInfo.InstantMessaging[0]", true));
        prefs.add(new PrintingPreference("LearnerInfo.Identification.ContactInfo.InstantMessaging[1]", true));
        prefs.add(new PrintingPreference("LearnerInfo.Identification.Demographics.Gender", true));
        prefs.add(new PrintingPreference("LearnerInfo.Identification.Demographics.Birthdate", true, null, "numeric/long", null));
        prefs.add(new PrintingPreference("LearnerInfo.Identification.Demographics.Nationality", true));
        prefs.add(new PrintingPreference("LearnerInfo.Identification.Demographics.Nationality[0]", true));
        prefs.add(new PrintingPreference("LearnerInfo.Identification.Demographics.Nationality[1]", false));
        prefs.add(new PrintingPreference("LearnerInfo.Headline", true));
        prefs.add(new PrintingPreference("LearnerInfo.WorkExperience", true));
        prefs.add(new PrintingPreference("LearnerInfo.WorkExperience[0]", true));
        prefs.add(new PrintingPreference("LearnerInfo.WorkExperience[0].Position", true));
        prefs.add(new PrintingPreference("LearnerInfo.WorkExperience[0].Period", true, null, "text/short", null));
        prefs.add(new PrintingPreference("LearnerInfo.WorkExperience[0].Activities", true));
        prefs.add(new PrintingPreference("LearnerInfo.WorkExperience[0].Employer", true));
        prefs.add(new PrintingPreference("LearnerInfo.WorkExperience[0].Employer.ContactInfo.Address", true));
        prefs.add(new PrintingPreference("LearnerInfo.WorkExperience[1]", false));
        prefs.add(new PrintingPreference("LearnerInfo.WorkExperience[1].Period", true, null, "text/short", null));
        prefs.add(new PrintingPreference("LearnerInfo.WorkExperience[1].Activities", true));
        prefs.add(new PrintingPreference("LearnerInfo.WorkExperience[1].Employer", true));
        prefs.add(new PrintingPreference("LearnerInfo.WorkExperience[1].Employer.ContactInfo.Address", true));
        prefs.add(new PrintingPreference("LearnerInfo.Education", true));
        prefs.add(new PrintingPreference("LearnerInfo.Education[0]", true));
        prefs.add(new PrintingPreference("LearnerInfo.Education[0].Period", true, null, "text/short", null));
        prefs.add(new PrintingPreference("LearnerInfo.Education[0].Title", true));
        prefs.add(new PrintingPreference("LearnerInfo.Education[0].Activities", true));
        prefs.add(new PrintingPreference("LearnerInfo.Education[0].Organisation", true));
        prefs.add(new PrintingPreference("LearnerInfo.Education[0].Organisation.ContactInfo.Address", false));
        prefs.add(new PrintingPreference("LearnerInfo.Education[0].Field", false));
        prefs.add(new PrintingPreference("LearnerInfo.Education[1]", true));
        prefs.add(new PrintingPreference("LearnerInfo.Education[1].Period", true, null, "text/short", null));
        prefs.add(new PrintingPreference("LearnerInfo.Education[1].Title", true));
        prefs.add(new PrintingPreference("LearnerInfo.Education[1].Activities", true));
        prefs.add(new PrintingPreference("LearnerInfo.Education[1].Organisation", true));
        prefs.add(new PrintingPreference("LearnerInfo.Education[1].Organisation.ContactInfo.Address", false));
        prefs.add(new PrintingPreference("LearnerInfo.Education[1].Level", false));
        prefs.add(new PrintingPreference("LearnerInfo.Skills", true));
        prefs.add(new PrintingPreference("LearnerInfo.Skills.Linguistic", true));
        prefs.add(new PrintingPreference("LearnerInfo.Skills.Linguistic.MotherTongue", true));
        prefs.add(new PrintingPreference("LearnerInfo.Skills.Linguistic.MotherTongue[0]", true));
        prefs.add(new PrintingPreference("LearnerInfo.Skills.Linguistic.MotherTongue[1]", true));
        prefs.add(new PrintingPreference("LearnerInfo.Skills.Linguistic.ForeignLanguage", true));
        prefs.add(new PrintingPreference("LearnerInfo.Skills.Linguistic.ForeignLanguage[0]", true));
        prefs.add(new PrintingPreference("LearnerInfo.Skills.Linguistic.ForeignLanguage[0].VerifiedBy", true));
        prefs.add(new PrintingPreference("LearnerInfo.Skills.Linguistic.ForeignLanguage[0].VerifiedBy[0]", true));
        prefs.add(new PrintingPreference("LearnerInfo.Skills.Linguistic.ForeignLanguage[0].VerifiedBy[1]", true));
        prefs.add(new PrintingPreference("LearnerInfo.Skills.Linguistic.ForeignLanguage[1]", true));
        prefs.add(new PrintingPreference("LearnerInfo.Skills.Linguistic.ForeignLanguage[1].VerifiedBy", true));
        prefs.add(new PrintingPreference("LearnerInfo.Skills.Linguistic.ForeignLanguage[1].VerifiedBy[0]", true));
        prefs.add(new PrintingPreference("LearnerInfo.Skills.Communication", true));
        prefs.add(new PrintingPreference("LearnerInfo.Skills.Organisational", true));
        prefs.add(new PrintingPreference("LearnerInfo.Skills.JobRelated", true));
        prefs.add(new PrintingPreference("LearnerInfo.Skills.Computer", true));
        prefs.add(new PrintingPreference("LearnerInfo.Skills.Driving", true));
        prefs.add(new PrintingPreference("LearnerInfo.Skills.Other", false));
        prefs.add(new PrintingPreference("LearnerInfo.Achievement", true));
        prefs.add(new PrintingPreference("LearnerInfo.Achievement[0]", true));
        prefs.add(new PrintingPreference("LearnerInfo.Achievement[1]", true));
        prefs.add(new PrintingPreference("LearnerInfo.Achievement[2]", true));
        prefs.add(new PrintingPreference("LearnerInfo.Achievement[3]", true));
        prefs.add(new PrintingPreference("LearnerInfo.Achievement[4]", true));

        Map<String, List<PrintingPreference>> prefMap = new HashMap<String, List<PrintingPreference>>();
        prefMap.put("ECV", prefs);

        esp.setDocumentInfo(docInfo);
        esp.setDocumentPrintingPrefs(prefMap);
        esp.setLearnerInfo(learnerinfo);
        esp.setAttachmentList(attachments);

        return esp;

    }

    public static SkillsPassport complete() {
        SkillsPassport esp = new SkillsPassport();

        esp.setLocale(Locale.ENGLISH);

        //Identification
        PersonName name = new PersonName("Αλέξια", "Αντωνίου");
        ContactAddress personalAddress = new ContactAddress(new Address("Konitsis 11B", "15125", "Marousi", new CodeLabel("EL", "Hellas")));
        ContactMethod email = new ContactMethod("alexia.antoniou@provider.com");
        List<ContactMethod> tels = new ArrayList<ContactMethod>();
        tels.add(new ContactMethod(new CodeLabel("work", "Work"), "2108029409"));
        tels.add(new ContactMethod(new CodeLabel("mobile", "Mobile"), "6945000100"));
        List<ContactMethod> webs = new ArrayList<ContactMethod>();
        webs.add(new ContactMethod("www.mytestweb.gr"));
        webs.add(new ContactMethod("www.mytestweb2.gr"));
        List<ContactMethod> ims = new ArrayList<ContactMethod>();
        ims.add(new ContactMethod(new CodeLabel("msn"), "mymsn"));
        ims.add(new ContactMethod(new CodeLabel("twitter"), "mytwitter"));
        ContactInfo personalContact = new ContactInfo();
        personalContact.setAddress(personalAddress);
        personalContact.setEmail(email);
        personalContact.setTelephoneList(tels);
        personalContact.setWebsiteList(webs);
        personalContact.setInstantMessagingList(ims);

        List<CodeLabel> nationalities = new ArrayList<CodeLabel>();
        nationalities.add(new CodeLabel("EL", "Hellenic"));
        nationalities.add(new CodeLabel("UK", "British"));
        nationalities.add(new CodeLabel(null, "Citizen of the world"));
        Demographics demographics = new Demographics();
        JDate birth = new JDate();
        birth.setYear(1984);
        birth.setMonth(2);
        birth.setDay(10);
        demographics.setBirthdate(birth);
        demographics.setNationalityList(nationalities);
        demographics.setGender(new CodeLabel("F", "Female"));

        Identification identification = new Identification();
        identification.setPersonName(name);
        identification.setContactInfo(personalContact);
        identification.setDemographics(demographics);
        identification.setPhoto(new FileData("image/png", "http://europass.instore.gr/api/files/photo/ERWTGFHFGHFG", "MyPhoto".getBytes()));

        Headline headline = new Headline();
        headline.setType(new CodeLabel("position", "Position"));
        headline.setDescription(new CodeLabel("R", "Martial Arts Instructor"));

        //Work Experience
        WorkExperience work1 = new WorkExperience();
        work1.setPosition(new CodeLabel("R", "Martial Arts Instructor"));
        work1.setActivities("<div class=\"dummy-root\"><b>Activity 1</b><br/><i>Activity 2</i><p>While monitoring other stuff.</p><br/><p>And during supervising some other stuff.</p></div>");
        JDate work1From = new JDate();
        work1From.setYear(2010);
        work1From.setMonth(12);
        JDate work1To = new JDate();
        work1To.setYear(2012);
        work1To.setMonth(12);
        work1.setPeriod(new Period(work1From, work1To));

        OrganisationalContactInfo work1OrgContact = new OrganisationalContactInfo();
        work1OrgContact.setAddress(new ContactAddress(new Address(new CodeLabel("JP", "Japan"))));
        work1OrgContact.setWebsite(new ContactMethod(new CodeLabel("business"), "http://mybusiness.com"));
        EmployerOrganisation work1empl = new EmployerOrganisation("School of Ninjutsu", work1OrgContact, new CodeLabel("Q", "Human health and social work"));
        work1.setEmployer(work1empl);
        List<ReferenceTo> work1Doc = new ArrayList<ReferenceTo>();
        work1Doc.add(new ReferenceTo("ATT_1"));
        work1Doc.add(new ReferenceTo("ATT_3"));
        work1.setReferenceToList(work1Doc);

        List<WorkExperience> worklist = new ArrayList<WorkExperience>();
        worklist.add(work1);

        //Education
        Education edu1 = new Education();
        edu1.setTitle("Computer Science");
        edu1.setActivities("<ul><li>First List Item: <u>underlined</u></li><li>Second List Item:<p><a href=\"http://nested.com\">nested link</a><b> Bold Text:<a href=\"http://nested.com\">Second nested Link <i>click here</i> <em>!!!</em></a></b></p></li></ul>");
        JDate edu1From = new JDate();
        edu1From.setYear(2008);
        edu1From.setMonth(5);
        edu1.setPeriod(new Period(edu1From, true));

        OrganisationalContactInfo edu1OrgContact = new OrganisationalContactInfo();
        edu1OrgContact.setAddress(new ContactAddress(new Address(new CodeLabel("EL", "Hellas"))));
        edu1.setOrganisation(new Organisation("University of Aegean", edu1OrgContact));

        edu1.setField(new CodeLabel("5", "Engineering"));
        List<ReferenceTo> edu1Doc = new ArrayList<ReferenceTo>();
        edu1Doc.add(new ReferenceTo("ATT_1"));
        edu1Doc.add(new ReferenceTo("ATT_2"));
        edu1Doc.add(new ReferenceTo("ATT_3"));
        edu1.setReferenceToList(edu1Doc);

        Education edu2 = new Education();
        edu2.setTitle("Business and Economics");
        edu2.setActivities("<div class=\"dummy-root\"><b>Principal Activities</b><ul><li>Micro-economics</li><li>Macro-economics</li><li>Computer Science<ol><li>Programming</li><li>Data structures</li></ol></li></ul></div>");
        JDate edu2From = new JDate();
        edu2From.setYear(2005);
        edu2From.setMonth(12);
        JDate edu2To = new JDate();
        edu2To.setYear(2008);
        edu2To.setMonth(4);
        edu2.setPeriod(new Period(edu2From, edu2To));

        OrganisationalContactInfo edu2OrgContact = new OrganisationalContactInfo();
        edu2OrgContact.setAddress(new ContactAddress(new Address(new CodeLabel("UK", "United Kingdom"))));
        edu2.setOrganisation(new Organisation("University of Plymouth", edu1OrgContact));

        edu2.setLevel(new CodeLabel("5", "ISCED 5"));

        List<ReferenceTo> edu2Doc = new ArrayList<ReferenceTo>();
        edu2Doc.add(new ReferenceTo("ATT_2"));
        edu2.setReferenceToList(edu2Doc);

        List<Education> educationlist = new ArrayList<Education>();
        educationlist.add(edu1);
        educationlist.add(edu2);

        //Skills
        Skills skills = new Skills();
        skills.setCommunication(new GenericSkill("<div class=\"dummy-root\"><b>Communication skills:</b> <ul><li>bla</li><li>blah</li></ul></div>"));
        skills.setOrganisational(new GenericSkill("<div class=\"dummy-root\"><b>Organisational skills:</b> <ul><li>bla</li><li>blah</li></ul></div>"));
        skills.setJobRelated(new GenericSkill("<div class=\"dummy-root\"><b>Job-Related skills:</b> <ul><li>bla</li><li>blah</li></ul></div>"));
        //skills.setComputer(new ComputerSkill("<div class=\"dummy-root\"><b>Computer skills:</b> <ul><li>bla</li><li>blah</li></ul></div>"));
        //computer skills with ict assessment
        ComputerSkill ictSkills = new ComputerSkill();
        ictSkills.setDescription("Object oriented computer programming skills");
        ictSkills.setProficiencyLevel(new ICTLevel("A", "B", "C", "A", "C"));
        List<Certificate<String>> certificatesIct = new ArrayList<Certificate<String>>();
        certificatesIct.add(new Certificate<String>("ACDL"));
        certificatesIct.add(new Certificate<String>("Java Certification"));
        ictSkills.setVerifiedBy(certificatesIct);
        List<ReferenceTo> ictDoc = new ArrayList<ReferenceTo>();
        ictDoc.add(new ReferenceTo("ATT_5"));
        ictSkills.setReferenceToList(ictDoc);
        //end computer skills

        skills.setOther(new GenericSkill("<div class=\"dummy-root\"><b>Other skills:</b> <ul><li>bla</li><li>blah</li></ul></div>"));
        List<String> driving = new ArrayList<String>();
        driving.add("A");
        driving.add("B1");
        skills.setDriving(new DrivingSkill(driving));

        List<LinguisticSkill> mother = new ArrayList<LinguisticSkill>();
        mother.add(new LinguisticSkill(new CodeLabel("el", "Greek")));
        mother.add(new LinguisticSkill(new CodeLabel("es", "Spanish")));

        List<LinguisticSkill> foreign = new ArrayList<LinguisticSkill>();
        LinguisticSkill foreign1 = new LinguisticSkill(new CodeLabel("en", "English"));
        foreign1.setProficiencyLevel(new CEFRLevel("C1", "C2", "B2", "B2", "B1"));
        List<Certificate<String>> certificates1 = new ArrayList<Certificate<String>>();
        certificates1.add(new Certificate<String>("Cambridge Certificate of Proficiency in English"));
        certificates1.add(new Certificate<String>("Michigan Certificate of Proficiency in English"));
        foreign1.setVerifiedBy(certificates1);

        LinguisticSkill foreign2 = new LinguisticSkill(new CodeLabel("it", "Italian"));
        foreign2.setProficiencyLevel(new CEFRLevel("B1", "B2", "B1", "A2", "A1"));
        List<Certificate<String>> certificates2 = new ArrayList<Certificate<String>>();
        certificates2.add(new Certificate<String>("Certificate of Adequacy in Italian"));
        foreign2.setVerifiedBy(certificates2);
        foreign.add(foreign1);
        foreign.add(foreign2);

        skills.setLinguistic(new LinguisticSkills(mother, foreign));

        //Achievements
        List<Achievement> achievements = new ArrayList<Achievement>();
        achievements.add(new Achievement(new CodeLabel("projects", "Projects"), "<ul><li>Project 1</li><li>Project 2</li><li>Project 3</li></ul>"));
        achievements.add(new Achievement(new CodeLabel("memberships", "memberships"), "<ul><li>Membership 1</li><li>Membership 2</li><li>Membership 3</li></ul>"));
        achievements.add(new Achievement(new CodeLabel("references", "References"), "<ul><li>Reference 1</li><li>Reference 2</li><li>Reference 3</li></ul>"));
        achievements.add(new Achievement(new CodeLabel("seminars", "seminars"), "<ul><li>Seminar 1</li><li>Seminar 2</li><li>Seminar 3</li></ul>"));
        Achievement participations = new Achievement(new CodeLabel(null, "Participations"), "<ul><li>Theatre 1</li><li>Theatre 2</li><li>Theatre 3</li></ul>");
        List<ReferenceTo> partDoc = new ArrayList<ReferenceTo>();
        partDoc.add(new ReferenceTo("ATT_4"));
        partDoc.add(new ReferenceTo("ATT_5"));
        participations.setReferenceToList(partDoc);
        achievements.add(participations);

        //Documentation
        List<ReferenceTo> annexes = new ArrayList<ReferenceTo>();
        annexes.add(new ReferenceTo("ATT_1"));
        annexes.add(new ReferenceTo("ATT_2"));
        annexes.add(new ReferenceTo("ATT_3"));
        annexes.add(new ReferenceTo("ATT_4"));
        annexes.add(new ReferenceTo("ATT_5"));

        LearnerInfo learnerinfo = new LearnerInfo();
        learnerinfo.setIdentification(identification);
        learnerinfo.setHeadline(headline);
        learnerinfo.setEducationList(educationlist);
        learnerinfo.setWorkExperienceList(worklist);
        learnerinfo.setSkills(skills);
        learnerinfo.setAchievementList(achievements);
        learnerinfo.setDocumentation(annexes);

        //Attachments
        List<Attachment> attachments = new ArrayList<Attachment>();
        attachments.add(new Attachment("ATT_1", "My Scanned Diploma", "Diploma.pdf", "application/pdf", "http://europass.instore.gr/ewars/photo/WR3452ER/UTT7534", "My Scanned Diploma".getBytes()));
        attachments.add(new Attachment("ATT_2", "My Scanned Msc Degree", "Degree.pdf", "application/pdf", "http://europass.instore.gr/ewars/photo/WR3882OO/UPE7434", "My Scanned Msc Degree".getBytes()));
        attachments.add(new Attachment("ATT_3", "Volunteeting Experience", "Experience.pdf", "application/pdf", "http://europass.instore.gr/ewars/photo/WT4322OO/UCD2434", "Volunteeting Experience".getBytes()));
        attachments.add(new Attachment("ATT_4", "Participation to WorldWide Conference", "Conference.pdf", "application/pdf", "http://europass.instore.gr/ewars/photo/WT4322OO/OPD2569", "Participation to WorldWide Conference".getBytes()));
        attachments.add(new Attachment("ATT_5", "Participation to Hellenic Seminar", "Seminar.pdf", "application/pdf", "http://europass.instore.gr/ewars/photo/WT4322OO/DFD2852", "Participation to Hellenic Seminar".getBytes()));

        //DocumentInfo (With null Europass value, we expect the Europass Logo to be shown)
        DateTime d = new DateTime(2012, 6, 6, 0, 0, DateTimeZone.UTC);
        DocumentInfo docInfo = new DocumentInfo(EuropassDocumentType.ECV_ESP, d, d, "V3.0", "EWA", "Comments", null);

        //Printing Preferences
        List<PrintingPreference> prefs = new ArrayList<PrintingPreference>();
        prefs.add(new PrintingPreference("LearnerInfo", true, "Identification Headline WorkExperience Education Skills Achievement Documentation", null, null));
        prefs.add(new PrintingPreference("LearnerInfo.Identification.PersonName", true, "FirstName Surname", null, null));
        prefs.add(new PrintingPreference("LearnerInfo.Identification.ContactInfo.Address", true, null, "s \\n p-z m (c)", null));
        prefs.add(new PrintingPreference("LearnerInfo.Identification.ContactInfo.Telephone", true));
        prefs.add(new PrintingPreference("LearnerInfo.Identification.ContactInfo.Telephone[0]", true));
        prefs.add(new PrintingPreference("LearnerInfo.Identification.ContactInfo.Telephone[1]", true));
        prefs.add(new PrintingPreference("LearnerInfo.Identification.ContactInfo.Email", true));
        prefs.add(new PrintingPreference("LearnerInfo.Identification.ContactInfo.Website", true));
        prefs.add(new PrintingPreference("LearnerInfo.Identification.ContactInfo.Website[0]", true));
        prefs.add(new PrintingPreference("LearnerInfo.Identification.ContactInfo.Website[1]", true));
        prefs.add(new PrintingPreference("LearnerInfo.Identification.ContactInfo.InstantMessaging", true));
        prefs.add(new PrintingPreference("LearnerInfo.Identification.ContactInfo.InstantMessaging[0]", true));
        prefs.add(new PrintingPreference("LearnerInfo.Identification.ContactInfo.InstantMessaging[1]", true));
        prefs.add(new PrintingPreference("LearnerInfo.Identification.Demographics.Gender", true));
        prefs.add(new PrintingPreference("LearnerInfo.Identification.Demographics.Birthdate", true, null, "numeric/long", null));
        prefs.add(new PrintingPreference("LearnerInfo.Identification.Demographics.Nationality", true));
        prefs.add(new PrintingPreference("LearnerInfo.Identification.Demographics.Nationality[0]", true));
        prefs.add(new PrintingPreference("LearnerInfo.Identification.Demographics.Nationality[1]", false));
        prefs.add(new PrintingPreference("LearnerInfo.Headline", true));
        prefs.add(new PrintingPreference("LearnerInfo.WorkExperience", true));
        prefs.add(new PrintingPreference("LearnerInfo.WorkExperience[0]", true));
        prefs.add(new PrintingPreference("LearnerInfo.WorkExperience[0].Position", true));
        prefs.add(new PrintingPreference("LearnerInfo.WorkExperience[0].Period", true, null, "text/short", null));
        prefs.add(new PrintingPreference("LearnerInfo.WorkExperience[0].Activities", true));
        prefs.add(new PrintingPreference("LearnerInfo.WorkExperience[0].Employer", true));
        prefs.add(new PrintingPreference("LearnerInfo.WorkExperience[0].Employer.ContactInfo.Address", true));
        prefs.add(new PrintingPreference("LearnerInfo.WorkExperience[1]", false));
        prefs.add(new PrintingPreference("LearnerInfo.WorkExperience[1].Period", true, null, "text/short", null));
        prefs.add(new PrintingPreference("LearnerInfo.WorkExperience[1].Activities", true));
        prefs.add(new PrintingPreference("LearnerInfo.WorkExperience[1].Employer", true));
        prefs.add(new PrintingPreference("LearnerInfo.WorkExperience[1].Employer.ContactInfo.Address", true));
        prefs.add(new PrintingPreference("LearnerInfo.Education", true));
        prefs.add(new PrintingPreference("LearnerInfo.Education[0]", true));
        prefs.add(new PrintingPreference("LearnerInfo.Education[0].Period", true, null, "text/short", null));
        prefs.add(new PrintingPreference("LearnerInfo.Education[0].Title", true));
        prefs.add(new PrintingPreference("LearnerInfo.Education[0].Activities", true));
        prefs.add(new PrintingPreference("LearnerInfo.Education[0].Organisation", true));
        prefs.add(new PrintingPreference("LearnerInfo.Education[0].Organisation.ContactInfo.Address", false));
        prefs.add(new PrintingPreference("LearnerInfo.Education[0].Field", false));
        prefs.add(new PrintingPreference("LearnerInfo.Education[1]", true));
        prefs.add(new PrintingPreference("LearnerInfo.Education[1].Period", true, null, "text/short", null));
        prefs.add(new PrintingPreference("LearnerInfo.Education[1].Title", true));
        prefs.add(new PrintingPreference("LearnerInfo.Education[1].Activities", true));
        prefs.add(new PrintingPreference("LearnerInfo.Education[1].Organisation", true));
        prefs.add(new PrintingPreference("LearnerInfo.Education[1].Organisation.ContactInfo.Address", false));
        prefs.add(new PrintingPreference("LearnerInfo.Education[1].Level", false));
        prefs.add(new PrintingPreference("LearnerInfo.Skills", true));
        prefs.add(new PrintingPreference("LearnerInfo.Skills.Linguistic", true));
        prefs.add(new PrintingPreference("LearnerInfo.Skills.Linguistic.MotherTongue", true));
        prefs.add(new PrintingPreference("LearnerInfo.Skills.Linguistic.MotherTongue[0]", true));
        prefs.add(new PrintingPreference("LearnerInfo.Skills.Linguistic.MotherTongue[1]", true));
        prefs.add(new PrintingPreference("LearnerInfo.Skills.Linguistic.ForeignLanguage", true));
        prefs.add(new PrintingPreference("LearnerInfo.Skills.Linguistic.ForeignLanguage[0]", true));
        prefs.add(new PrintingPreference("LearnerInfo.Skills.Linguistic.ForeignLanguage[0].VerifiedBy", true));
        prefs.add(new PrintingPreference("LearnerInfo.Skills.Linguistic.ForeignLanguage[0].VerifiedBy[0]", true));
        prefs.add(new PrintingPreference("LearnerInfo.Skills.Linguistic.ForeignLanguage[0].VerifiedBy[1]", true));
        prefs.add(new PrintingPreference("LearnerInfo.Skills.Linguistic.ForeignLanguage[1]", true));
        prefs.add(new PrintingPreference("LearnerInfo.Skills.Linguistic.ForeignLanguage[1].VerifiedBy", true));
        prefs.add(new PrintingPreference("LearnerInfo.Skills.Linguistic.ForeignLanguage[1].VerifiedBy[0]", true));
        prefs.add(new PrintingPreference("LearnerInfo.Skills.Communication", true));
        prefs.add(new PrintingPreference("LearnerInfo.Skills.Organisational", true));
        prefs.add(new PrintingPreference("LearnerInfo.Skills.JobRelated", true));
        prefs.add(new PrintingPreference("LearnerInfo.Skills.Computer", true));
        prefs.add(new PrintingPreference("LearnerInfo.Skills.Driving", true));
        prefs.add(new PrintingPreference("LearnerInfo.Skills.Other", false));
        prefs.add(new PrintingPreference("LearnerInfo.Achievement", true));
        prefs.add(new PrintingPreference("LearnerInfo.Achievement[0]", true));
        prefs.add(new PrintingPreference("LearnerInfo.Achievement[1]", true));
        prefs.add(new PrintingPreference("LearnerInfo.Achievement[2]", true));
        prefs.add(new PrintingPreference("LearnerInfo.Achievement[3]", true));
        prefs.add(new PrintingPreference("LearnerInfo.Achievement[4]", true));

        Map<String, List<PrintingPreference>> prefMap = new HashMap<String, List<PrintingPreference>>();
        prefMap.put("ECV", prefs);

        esp.setDocumentInfo(docInfo);
        esp.setDocumentPrintingPrefs(prefMap);
        esp.setLearnerInfo(learnerinfo);
        esp.setAttachmentList(attachments);

        return esp;
    }

    public static SkillsPassport espLanguagesObj() {

        SkillsPassport esp = new SkillsPassport();

        //Skills
        Skills skills = new Skills();

        List<LinguisticSkill> mother = new ArrayList<LinguisticSkill>();
        mother.add(new LinguisticSkill(new CodeLabel("el", "Greek")));
        mother.add(new LinguisticSkill(new CodeLabel("es", "Spanish")));

        List<LinguisticSkill> foreign = new ArrayList<LinguisticSkill>();
        LinguisticSkill foreign1 = new LinguisticSkill(new CodeLabel("en", "English"));
        foreign1.setProficiencyLevel(new CEFRLevel("C1", "C2", "B2", "B2", "B1"));

        List<Certificate<String>> certificates1 = new ArrayList<Certificate<String>>();

        Certificate<String> cert1 = new Certificate<String>("Cambridge Certificate of Proficiency in English");
        JDate cert1date = new JDate();
        cert1date.setYear(1998);
        cert1.setDate(cert1date);
        cert1.setLevel("C1");

        Certificate<String> cert2 = new Certificate<String>("Michigan Certificate of Proficiency in English");

        certificates1.add(cert1);
        certificates1.add(cert2);
        foreign1.setVerifiedBy(certificates1);

        LinguisticSkill foreign2 = new LinguisticSkill(new CodeLabel("it", "Italian"));
        foreign2.setProficiencyLevel(new CEFRLevel("B1", "B2", "B1", "A2", "A1"));

        List<Certificate<String>> certificates2 = new ArrayList<Certificate<String>>();
        certificates2.add(new Certificate<String>("Certificate of Adequacy in Italian"));
        foreign2.setVerifiedBy(certificates2);

        foreign.add(foreign1);
        foreign.add(foreign2);

        skills.setLinguistic(new LinguisticSkills(mother, foreign));

        LearnerInfo learnerinfo = new LearnerInfo();
        learnerinfo.setSkills(skills);

        esp.setLearnerInfo(learnerinfo);

        return esp;

    }

    public static SkillsPassport espHeadlineObj() {
        SkillsPassport esp = new SkillsPassport();

        Headline headline = new Headline();
        headline.setType(new CodeLabel("position", "Position"));
        headline.setDescription(new CodeLabel("R", "Martial Arts Instructor"));

        LearnerInfo learnerinfo = new LearnerInfo();
        learnerinfo.setHeadline(headline);
        esp.setLearnerInfo(learnerinfo);
        return esp;
    }

    public static SkillsPassport espHiddenOnly() {
        SkillsPassport esp = new SkillsPassport();

        List<ContactMethod> webs = new ArrayList<ContactMethod>();
        webs.add(new ContactMethod("www.mytestweb.gr"));
        webs.add(new ContactMethod("www.mysecondtestweb.gr"));
        List<ContactMethod> ims = new ArrayList<ContactMethod>();
        ims.add(new ContactMethod(new CodeLabel("msn"), "mymsn"));
        ims.add(new ContactMethod(new CodeLabel("twitter"), "mytwitter"));
        ContactInfo personalContact = new ContactInfo();
        personalContact.setWebsiteList(webs);
        personalContact.setInstantMessagingList(ims);

        Demographics demographics = new Demographics();

        List<CodeLabel> nationalities = new ArrayList<CodeLabel>();
        nationalities.add(new CodeLabel("EL", "Hellenic"));
        nationalities.add(new CodeLabel("UK", "British"));
        nationalities.add(new CodeLabel(null, "Citizen of the world"));
        JDate birth = new JDate();
        birth.setYear(1984);
        birth.setMonth(2);
        birth.setDay(10);
        demographics.setBirthdate(birth);
        demographics.setNationalityList(nationalities);
        demographics.setGender(new CodeLabel("F", "Female"));

        FileData photo = new FileData();
        photo.setData("Photo".getBytes());

        Identification identification = new Identification();
        identification.setContactInfo(personalContact);
        identification.setDemographics(demographics);
        identification.setPhoto(photo);

        //Work Experience
        WorkExperience work1 = new WorkExperience();
        work1.setPosition(new CodeLabel("R", "Martial Arts Instructor"));
        OrganisationalContactInfo work1OrgContact = new OrganisationalContactInfo();
        work1OrgContact.setAddress(new ContactAddress(new Address(new CodeLabel("JP", "Japan"))));
        work1OrgContact.setWebsite(new ContactMethod(new CodeLabel("business"), "http://mybusiness.com"));
        EmployerOrganisation work1empl = new EmployerOrganisation("School of Ninjutsu", work1OrgContact, new CodeLabel("Q", "Human health and social work"));

        work1.setEmployer(work1empl);

        work1.setActivities("List of work activities and responsibilities ... ");

        List<ReferenceTo> work1Doc = new ArrayList<ReferenceTo>();
        work1Doc.add(new ReferenceTo("ATT_1"));
        work1Doc.add(new ReferenceTo("ATT_3"));
        work1.setReferenceToList(work1Doc);
        List<WorkExperience> worklist = new ArrayList<WorkExperience>();
        worklist.add(work1);

        //Education
        Education edu1 = new Education();
        edu1.setTitle("Computer Science");
        edu1.setActivities("<ul><li>First List Item: <u>underlined</u></li><li>Second List Item:<p><a href=\"http://nested.com\">nested link</a><b> Bold Text:<a href=\"http://nested.com\">Second nested Link <i>click here</i> <em>!!!</em></a></b></p></li></ul>");
        JDate edu1From = new JDate();
        edu1From.setYear(2008);
        edu1From.setMonth(5);
        edu1.setPeriod(new Period(edu1From, true));
        OrganisationalContactInfo edu1OrgContact = new OrganisationalContactInfo();
        edu1OrgContact.setAddress(new ContactAddress(new Address(new CodeLabel("EL", "Hellas"))));
        edu1.setOrganisation(new Organisation("University of Aegean", edu1OrgContact));
        edu1.setLevel(new CodeLabel("1", "EQF level 1"));
        edu1.setField(new CodeLabel("5", "Engineering"));
        List<ReferenceTo> edu1Doc = new ArrayList<ReferenceTo>();
        edu1Doc.add(new ReferenceTo("ATT_1"));
        edu1Doc.add(new ReferenceTo("ATT_2"));
        edu1Doc.add(new ReferenceTo("ATT_3"));
        edu1.setReferenceToList(edu1Doc);
        List<Education> educationlist = new ArrayList<Education>();
        educationlist.add(edu1);

        //Skills
        Skills skills = new Skills();

        List<LinguisticSkill> mother = new ArrayList<LinguisticSkill>();
        mother.add(new LinguisticSkill(new CodeLabel("el", "Greek")));
        mother.add(new LinguisticSkill(new CodeLabel("es", "Spanish")));

        List<LinguisticSkill> foreign = new ArrayList<LinguisticSkill>();
        LinguisticSkill foreign1 = new LinguisticSkill(new CodeLabel("en", "English"));
        foreign1.setProficiencyLevel(new CEFRLevel("C1", "C2", "B1", "A2", "C1"));
        List<Certificate<String>> certificates1 = new ArrayList<Certificate<String>>();
        certificates1.add(new Certificate<String>("Cambridge Certificate of Proficiency in English"));
        certificates1.add(new Certificate<String>("Michigan Certificate of Proficiency in English"));
        foreign1.setVerifiedBy(certificates1);
        List<ReferenceTo> flang1Doc = new ArrayList<ReferenceTo>();
        flang1Doc.add(new ReferenceTo("foreign language diploma photo"));
        foreign1.setReferenceToList(flang1Doc);
        //foreign1.s
        foreign.add(foreign1);
        foreign.add(new LinguisticSkill(new CodeLabel("de", "German")));

        skills.setLinguistic(new LinguisticSkills(mother, foreign));

        GenericSkill skill = new GenericSkill("<p>Generic skill description text ... </p>");
        GenericSkill skillOther = new GenericSkill("<div class=\"dummy-root\"><b>Other skills:</b> <ul><li>bla</li><li>blah</li></ul></div>");

        List<ReferenceTo> refSkill = new ArrayList<ReferenceTo>();
        refSkill.add(new ReferenceTo("skill reference document"));
        skill.setReferenceToList(refSkill);
        skillOther.setReferenceToList(refSkill);

        skills.setCommunication(skill);
        skills.setOrganisational(skill);
        skills.setJobRelated(skill);
        //skills.setComputer(skill);
        //skills.setComputer(new ComputerSkill("<div class=\"dummy-root\"><b>Computer skills:</b> <ul><li>bla</li><li>blah</li></ul></div>"));
        //computer skills with ict assessment
        ComputerSkill ictSkills = new ComputerSkill();
        ictSkills.setDescription("Object oriented computer programming skills");
        ictSkills.setProficiencyLevel(new ICTLevel("A", "B", "C", "A", "A"));
        List<Certificate<String>> certificatesIct = new ArrayList<Certificate<String>>();
        certificatesIct.add(new Certificate<String>("ACDL"));
        certificatesIct.add(new Certificate<String>("Java Certification"));
        ictSkills.setVerifiedBy(certificatesIct);
        List<ReferenceTo> ictDoc = new ArrayList<ReferenceTo>();
        ictDoc.add(new ReferenceTo("ATT_5"));
        ictSkills.setReferenceToList(ictDoc);
        //end computer skills
        skills.setOther(skillOther);

        List<String> driving = new ArrayList<String>();
        driving.add("A");
        driving.add("B1");
        List<ReferenceTo> refDriving = new ArrayList<ReferenceTo>();
        refDriving.add(new ReferenceTo("driving licence image"));
        DrivingSkill drivingSkill = new DrivingSkill(driving);
        drivingSkill.setReferenceToList(refDriving);
        skills.setDriving(drivingSkill);

        //Achievements
        List<Achievement> achievements = new ArrayList<Achievement>();
        achievements.add(new Achievement(new CodeLabel("projects", "Projects"), "<ul><li>Project 1</li><li>Project 2</li><li>Project 3</li></ul>"));
        Achievement participations = new Achievement(new CodeLabel(null, "Participations"), "<ul><li>Theatre 1</li><li>Theatre 2</li><li>Theatre 3</li></ul>");
        List<ReferenceTo> partDoc = new ArrayList<ReferenceTo>();
        partDoc.add(new ReferenceTo("ATT_4"));
        partDoc.add(new ReferenceTo("ATT_5"));
        participations.setReferenceToList(partDoc);
        achievements.add(participations);

        //Documentation
        List<ReferenceTo> annexes = new ArrayList<ReferenceTo>();
        annexes.add(new ReferenceTo("ATT_1"));

        LearnerInfo learnerinfo = new LearnerInfo();
        learnerinfo.setIdentification(identification);
        learnerinfo.setWorkExperienceList(worklist);
        learnerinfo.setEducationList(educationlist);
        learnerinfo.setSkills(skills);
        learnerinfo.setAchievementList(achievements);
        learnerinfo.setDocumentation(annexes);
        esp.setLearnerInfo(learnerinfo);
        return esp;
    }

    public static SkillsPassport bothPrefs() {
        SkillsPassport esp = new SkillsPassport();

        //Printing Preferences
        List<PrintingPreference> ecvPrefs = new ArrayList<PrintingPreference>();
        ecvPrefs.add(new PrintingPreference("LearnerInfo", true, "Identification Headline WorkExperience Education Skills Achievement ReferenceTo", null, null));
        ecvPrefs.add(new PrintingPreference("LearnerInfo.Identification.PersonName", true, "FirstName Surname", null, null));
        ecvPrefs.add(new PrintingPreference("LearnerInfo.Identification.ContactInfo.Address", true, null, "s \\n p-z m (c)", null));
        ecvPrefs.add(new PrintingPreference("LearnerInfo.Identification.ContactInfo.Telephone", true));

        //Printing Preferences
        List<PrintingPreference> elpPrefs = new ArrayList<PrintingPreference>();
        elpPrefs.add(new PrintingPreference("LearnerInfo", true, "Identification LinguisticSkills", null, null));

        Map<String, List<PrintingPreference>> map = new LinkedHashMap<String, List<PrintingPreference>>();
        map.put("ECV", ecvPrefs);
        map.put("ELP", elpPrefs);

        esp.setDocumentPrintingPrefs(map);

        return esp;
    }

    public static SkillsPassport generalSkills() {
        SkillsPassport esp = new SkillsPassport();

        LearnerInfo skillsLearner = new LearnerInfo();

        Skills skill = new Skills();

        GenericSkill gs = new GenericSkill();

        gs.setDescription("<p class=\"indent1\">Είναι πλέον κοινά παραδεκτό ότι ένας αναγνώστης αποσπάται από το περιεχόμενο που διαβάζει, όταν εξετάζει τη διαμόρφωση μίας σελίδας.Η ουσία της χρήσης του Lorem Ipsum είναι ότι έχει λίγο-πολύ μία ομαλή κατανομή γραμμάτων, αντίθετα με το να βάλει κανείς κείμενο όπως</p><p class=\"indent2\">"
                + "Εδώ θα μπει κείμενο, εδώ θα μπει κείμενο, κάνοντάς το να φαίνεται σαν κανονικό κείμενο.Πολλά λογισμικά πακέτα ηλεκτρονικής σελιδοποίησης και επεξεργαστές ιστότοπων πλέον χρησιμοποιούν το Lorem Ipsum σαν προκαθορισμένο δείγμα κειμένου, και η αναζήτησ για τις λέξεις στο διαδίκτυο θα αποκαλύψει πολλά web site που βρίσκονται στο στάδιο της δημιουργίας.</p>");

        skill.setCommunication(gs);

        skillsLearner.setSkills(skill);

        esp.setLearnerInfo(skillsLearner);

        return esp;
    }

    public static SkillsPassport headline() {
        SkillsPassport esp = new SkillsPassport();

        Headline h = new Headline();
        h.setType(new CodeLabel("personal_statement", "Personal Statement"));
        h.setDescription(new CodeLabel(null, "This is my personal statement"));

        LearnerInfo learnerinfo = new LearnerInfo();
        learnerinfo.setHeadline(h);
        esp.setLearnerInfo(learnerinfo);

        return esp;
    }

    public static SkillsPassport espSimpleObj() {
        SkillsPassport esp = new SkillsPassport();

        ContactInfo personalContact = new ContactInfo();
        personalContact.setAddress(new ContactAddress(new Address(new CodeLabel("EL", "Hellas"))));

        Identification identification = new Identification();
        identification.setContactInfo(personalContact);

        LearnerInfo learnerinfo = new LearnerInfo();
        learnerinfo.setIdentification(identification);
        esp.setLearnerInfo(learnerinfo);

        return esp;
    }

    ;
	
	public static SkillsPassport espWorkEduObj() {
        SkillsPassport esp = new SkillsPassport();

        //Work Experience
        WorkExperience work1 = new WorkExperience();
        work1.setPosition(new CodeLabel("R", "Martial Arts Instructor"));
        work1.setActivities("<div class=\"dummy-root\"><b>Activity 1</b><br/><i>Activity 2</i><p>While monitoring other stuff.</p><br/><p>And during supervising some other stuff.</p></div>");
        JDate work1From = new JDate();
        work1From.setYear(2010);
        work1From.setMonth(12);
        JDate work1To = new JDate();
        work1To.setYear(2013);
        work1To.setMonth(12);
        work1.setPeriod(new Period(work1From, work1To));

        OrganisationalContactInfo work1OrgContact = new OrganisationalContactInfo();
        work1OrgContact.setAddress(new ContactAddress(new Address(new CodeLabel("JP", "Japan"))));
        work1OrgContact.setWebsite(new ContactMethod(new CodeLabel("business"), "http://mybusiness.com"));
        EmployerOrganisation work1empl = new EmployerOrganisation("School of Ninjutsu", work1OrgContact, new CodeLabel("Q", "Human health and social work"));
        work1.setEmployer(work1empl);
        List<ReferenceTo> work1Doc = new ArrayList<ReferenceTo>();
        work1Doc.add(new ReferenceTo("ATT_1"));
        work1Doc.add(new ReferenceTo("ATT_3"));
        work1.setReferenceToList(work1Doc);

        List<WorkExperience> worklist = new ArrayList<WorkExperience>();
        worklist.add(work1);

        //Education
        Education edu1 = new Education();
        edu1.setTitle("Computer Science");
        edu1.setActivities("<ul><li>First List Item: <u>underlined</u></li><li>Second List Item:<p><a href=\"http://nested.com\">nested link</a><b> Bold Text:<a href=\"http://nested.com\">Second nested Link <i>click here</i> <em>!!!</em></a></b></p></li></ul>");
        JDate edu1From = new JDate();
        edu1From.setYear(2000);
        edu1From.setMonth(5);
        JDate edu1To = new JDate();
        edu1To.setYear(2005);
        edu1To.setMonth(5);
        edu1.setPeriod(new Period(edu1From, edu1To));

        OrganisationalContactInfo edu1OrgContact = new OrganisationalContactInfo();
        edu1OrgContact.setAddress(new ContactAddress(new Address(new CodeLabel("EL", "Hellas"))));
        edu1.setOrganisation(new Organisation("University of Aegean", edu1OrgContact));

        edu1.setField(new CodeLabel("5", "Engineering"));
        List<ReferenceTo> edu1Doc = new ArrayList<ReferenceTo>();
        edu1Doc.add(new ReferenceTo("ATT_1"));
        edu1Doc.add(new ReferenceTo("ATT_2"));
        edu1Doc.add(new ReferenceTo("ATT_3"));
        edu1.setReferenceToList(edu1Doc);

        Education edu2 = new Education();
        edu2.setTitle("Business and Economics");
        edu2.setActivities("<div class=\"dummy-root\"><b>Principal Activities</b><ul><li>Micro-economics</li><li>Macro-economics</li><li>Computer Science<ol><li>Programming</li><li>Data structures</li></ol></li></ul></div>");
        JDate edu2From = new JDate();
        edu2From.setYear(2005);
        edu2From.setMonth(12);
        JDate edu2To = new JDate();
        edu2To.setYear(2008);
        edu2To.setMonth(12);
        edu2.setPeriod(new Period(edu2From, edu2To));

        List<Education> educationlist = new ArrayList<Education>();
        educationlist.add(edu1);
        educationlist.add(edu2);

        LearnerInfo learnerinfo = new LearnerInfo();
        learnerinfo.setWorkExperienceList(worklist);
        learnerinfo.setEducationList(educationlist);
        esp.setLearnerInfo(learnerinfo);

        return esp;
    }

    public static SkillsPassport espFileDataObj() {
        SkillsPassport esp = new SkillsPassport();

        List<Attachment> attachments = new ArrayList<Attachment>();
        attachments.add(new Attachment("ATT_1", "Certificate of Attendance", "Certificate.pdf", "application/pdf", "http://europass.instore.gr/files/file/WR3452ERUTT7534", "FILEBYTES1".getBytes()));
        attachments.add(new Attachment("ATT_2", "Engineering Diploma", "Diploma.pdf", "application/pdf", "http://europass.instore.gr/files/file/WR3452EPOS3244", "FILEBYTES2".getBytes()));

        esp.setAttachmentList(attachments);

        return esp;
    }

    ;
	
	public static SkillsPassport espSkillsObj() {
        SkillsPassport esp = new SkillsPassport();
        //Skills
        Skills s = new Skills();
        s.setCommunication(new GenericSkill("<div class=\"dummy-root\">Communication skills: <ul><li>bla</li><li>blah</li></ul></div>"));
        s.setOrganisational(new GenericSkill("<div class=\"dummy-root\">Organisational skills: <ul><li>bla</li><li>blah</li></ul></div>"));
        s.setJobRelated(new GenericSkill("<div class=\"dummy-root\">Job-Related skills: <ul><li>bla</li><li>blah</li></ul></div>"));
        //s.setComputer(new GenericSkill("<div class=\"dummy-root\">Computer skills: <ul><li>bla</li><li>blah</li></ul></div>"));

        //skills.setComputer(new ComputerSkill("<div class=\"dummy-root\"><b>Computer skills:</b> <ul><li>bla</li><li>blah</li></ul></div>"));
        //computer skills with ict assessment
        ComputerSkill ictSkills = new ComputerSkill();
        ictSkills.setDescription("Object oriented computer programming skills");
        ictSkills.setProficiencyLevel(new ICTLevel("A", "B", "C", "A", ""));
        List<Certificate<String>> certificatesIct = new ArrayList<Certificate<String>>();
        certificatesIct.add(new Certificate<String>("ACDL"));
        certificatesIct.add(new Certificate<String>("Java Certification"));
        ictSkills.setVerifiedBy(certificatesIct);
        List<ReferenceTo> ictDoc = new ArrayList<ReferenceTo>();
        ictDoc.add(new ReferenceTo("ATT_5"));
        ictSkills.setReferenceToList(ictDoc);
        s.setComputer(ictSkills);
        //end computer skills

        s.setOther(new GenericSkill("<div class=\"dummy-root\">Other skills: <ul><li>bla</li><li>blah</li></ul></div>"));
        List<String> dr = new ArrayList<String>();
        dr.add("A");
        dr.add("B");
        dr.add("BE");
        s.setDriving(new DrivingSkill(dr));

        List<LinguisticSkill> m = new ArrayList<LinguisticSkill>();
        m.add(new LinguisticSkill(new CodeLabel("el", "Greek")));
        m.add(new LinguisticSkill(new CodeLabel(null, "Elvish")));

        List<LinguisticSkill> f = new ArrayList<LinguisticSkill>();
        LinguisticSkill f1 = new LinguisticSkill(new CodeLabel("ja", "Japanese"));
        f1.setProficiencyLevel(new CEFRLevel("C1", "C1", "C1", "C1", "C1"));
        List<Certificate<String>> c1 = new ArrayList<Certificate<String>>();
        c1.add(new Certificate<String>("Cambridge Certificate of Proficiency"));
        c1.add(new Certificate<String>("Michigan Certificate of Proficiency"));
        f1.setVerifiedBy(c1);
        f.add(f1);

        s.setLinguistic(new LinguisticSkills(m, f));
        LearnerInfo skillsLearner = new LearnerInfo();
        skillsLearner.setSkills(s);
        esp.setLearnerInfo(skillsLearner);

        return esp;
    }

    public static SkillsPassport espEduObj() {
        SkillsPassport esp = new SkillsPassport();
        //Education
        Education ed1 = new Education();
        ed1.setTitle("Computer Science");
        ed1.setActivities("<ul><li>Programming</li><li>Data structures</li></ul>");
        JDate ed1From = new JDate();
        ed1From.setYear(2008);
        ed1From.setMonth(5);
        JDate ed1To = new JDate();
        ed1To.setYear(2010);
        ed1To.setMonth(5);
        ed1.setPeriod(new Period(ed1From, ed1To));

        OrganisationalContactInfo ed1OrgContact = new OrganisationalContactInfo();
        ed1OrgContact.setAddress(new ContactAddress(new Address(new CodeLabel("EL", "Hellas"))));
        ed1.setOrganisation(new Organisation("University of Aegean", ed1OrgContact));
        ed1.setLevel(new CodeLabel("5", "ISCED 5"));

        ed1.setField(new CodeLabel("5", "Engineering"));
        List<ReferenceTo> ed1Doc = new ArrayList<ReferenceTo>();
        ed1Doc.add(new ReferenceTo("ATT_1"));
        ed1Doc.add(new ReferenceTo("ATT_2"));
        ed1Doc.add(new ReferenceTo("ATT_3"));
        ed1.setReferenceToList(ed1Doc);

        List<Education> edlist = new ArrayList<Education>();
        edlist.add(ed1);
        LearnerInfo learnerEdu = new LearnerInfo();
        learnerEdu.setEducationList(edlist);
        esp.setLearnerInfo(learnerEdu);

        return esp;
    }
}
