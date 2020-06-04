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
package europass.ewa.statistics;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import europass.ewa.enums.EuropassDocumentType;
import europass.ewa.model.Achievement;
import europass.ewa.model.Address;
import europass.ewa.model.Attachment;
import europass.ewa.model.CEFRLevel;
import europass.ewa.model.Certificate;
import europass.ewa.model.CodeLabel;
import europass.ewa.model.ContactAddress;
import europass.ewa.model.Demographics;
import europass.ewa.model.DocumentInfo;
import europass.ewa.model.DrivingSkill;
import europass.ewa.model.Education;
import europass.ewa.model.EmployerOrganisation;
import europass.ewa.model.Headline;
import europass.ewa.model.Identification;
import europass.ewa.model.JDate;
import europass.ewa.model.LearnerInfo;
import europass.ewa.model.LinguisticSkill;
import europass.ewa.model.LinguisticSkills;
import europass.ewa.model.Organisation;
import europass.ewa.model.OrganisationalContactInfo;
import europass.ewa.model.Period;
import europass.ewa.model.ReferenceTo;
import europass.ewa.model.Skills;
import europass.ewa.model.SkillsPassport;
import europass.ewa.model.WorkExperience;

public class StatisticsMockObjects {
	
	   
	public static SkillsPassport demographics(){
		SkillsPassport esp = new SkillsPassport();
		LearnerInfo info = new LearnerInfo();
		Identification identification = new Identification();
		
		Demographics demographics = new Demographics();
		List<CodeLabel> nationalities = new ArrayList<CodeLabel>();
		nationalities.add(new CodeLabel(null, "Έλληνας") );
		nationalities.add(null);
		JDate birth = new JDate(); birth.setYear(1984); birth.setMonth(1); birth.setDay(1);
		demographics.setBirthdate( birth );
		demographics.setGender( null );
		demographics.setNationalityList(nationalities);
		
		identification.setDemographics(demographics);
		info.setIdentification(identification);
		esp.setLearnerInfo(info);
		return esp;
	}
	
	public static SkillsPassport headlinePS(){
		SkillsPassport esp = new SkillsPassport();
		
		Headline headline = new Headline();
		headline.setType(new CodeLabel("personal_statement", "Personal Statement"));
		headline.setDescription(new CodeLabel(null, "Kunfu Master"));
		
		LearnerInfo learnerinfo = new LearnerInfo();
		learnerinfo.setHeadline(headline);
		esp.setLearnerInfo(learnerinfo);
		return esp;
	}
	
	public static SkillsPassport headline(){
		SkillsPassport esp = new SkillsPassport();
		
		Headline headline = new Headline();
		headline.setType(new CodeLabel("position", "Position"));
		headline.setDescription(new CodeLabel("M", "Senior government official"));
		
		LearnerInfo learnerinfo = new LearnerInfo();
		learnerinfo.setHeadline(headline);
		esp.setLearnerInfo(learnerinfo);
		return esp;
	}
	
	public static SkillsPassport headlineNullDescription(){
		SkillsPassport esp = new SkillsPassport();
		
		Headline headline = new Headline();
		headline.setType(new CodeLabel("personal_statement", "Personal Statement"));
		
		LearnerInfo learnerinfo = new LearnerInfo();
		learnerinfo.setHeadline(headline);
		esp.setLearnerInfo(learnerinfo);
		return esp;
	}
	
	public static SkillsPassport headlineNullTypeCode(){
		SkillsPassport esp = new SkillsPassport();
		
		Headline headline = new Headline();
		CodeLabel healine_type = new CodeLabel(null, "Personal Statement");
		headline.setType(healine_type);
		headline.setDescription(new CodeLabel(null, "Kunfu Master"));
		
		LearnerInfo learnerinfo = new LearnerInfo();
		learnerinfo.setHeadline(headline);
		esp.setLearnerInfo(learnerinfo);
		return esp;
	}
	
	public static SkillsPassport headlineNullType(){
		SkillsPassport esp = new SkillsPassport();
		
		Headline headline = new Headline();
		headline.setDescription(new CodeLabel(null, "Kunfu Master"));
		
		LearnerInfo learnerinfo = new LearnerInfo();
		learnerinfo.setHeadline(headline);
		esp.setLearnerInfo(learnerinfo);
		return esp;
	}
	
	public static SkillsPassport headlineNull(){
		SkillsPassport esp = new SkillsPassport();
		
		LearnerInfo learnerinfo = new LearnerInfo();
		esp.setLearnerInfo(learnerinfo);
		return esp;
	}
	
	public static SkillsPassport headlineNullTypeNDesc(){
		SkillsPassport esp = new SkillsPassport();
		
		Headline headline = new Headline();
		
		LearnerInfo learnerinfo = new LearnerInfo();
		learnerinfo.setHeadline(headline);
		esp.setLearnerInfo(learnerinfo);
		return esp;
	}
	
	public static SkillsPassport workExperience(){
		SkillsPassport esp = new SkillsPassport();
		
		//Work Experience
		WorkExperience work1 = new WorkExperience();
		JDate work1From = new JDate(); 
		work1From.setYear(2010); 
		work1From.setMonth(12);
		work1From.setDay(1);
		JDate work1To = new JDate(); 
		work1To.setYear(2010); 
		work1To.setMonth(2);
		work1To.setDay(28);
		work1.setPeriod(new Period( work1From , work1To));
		
		OrganisationalContactInfo work1OrgContact = new OrganisationalContactInfo();
		work1OrgContact.setAddress(new ContactAddress( new Address( new CodeLabel(null, "Japan") ) ));
		EmployerOrganisation work1empl = new EmployerOrganisation( null, work1OrgContact, new CodeLabel("Q", "Human health and social work"));
		work1.setEmployer(work1empl);
		
		List<WorkExperience> worklist = new ArrayList<WorkExperience>();
		worklist.add(work1);
		worklist.add(null);
		
		LearnerInfo learnerinfo = new LearnerInfo();
	    learnerinfo.setWorkExperienceList( worklist );
		esp.setLearnerInfo(learnerinfo);
		
		return esp;
	}
	
	public static SkillsPassport workExperienceOrgIsNull(){
		SkillsPassport esp = new SkillsPassport();
		
		//Work Experience
		WorkExperience work1 = new WorkExperience();
		JDate work1From = new JDate(); 
		work1From.setYear(2010); 
		work1From.setMonth(12);
		JDate work1To = new JDate(); 
		work1To.setYear(2012); 
		work1To.setMonth(12); 
		work1.setPeriod(new Period( work1From , work1To));
		
		OrganisationalContactInfo work1OrgContact = new OrganisationalContactInfo();
		work1OrgContact.setAddress(new ContactAddress( new Address( new CodeLabel(null, "Japan") ) ));
		
		List<WorkExperience> worklist = new ArrayList<WorkExperience>();
		worklist.add(work1);
		
		LearnerInfo learnerinfo = new LearnerInfo();
	    learnerinfo.setWorkExperienceList( worklist );
		esp.setLearnerInfo(learnerinfo);
		
		return esp;
	}
	
	public static SkillsPassport espFileDataObj(){
		SkillsPassport esp = new SkillsPassport(); 
		
		List<Attachment> attachments = new ArrayList<Attachment>();
		attachments.add( new Attachment( "ATT_1", "Certificate of Attendance", "Certificate.pdf","application/pdf","http://europass.instore.gr/files/file/WR3452ERUTT7534",  "FILEBYTES1".getBytes() ) );
		attachments.add( new Attachment( "ATT_2", "Engineering Diploma", "Diploma.pdf","application/pdf","http://europass.instore.gr/files/file/WR3452EPOS3244", "FILEBYTES2".getBytes() ) );
		attachments.add(null);
		
		esp.setAttachmentList(attachments);
		
		return esp;
	};
	
	public static SkillsPassport education(){
		SkillsPassport esp = new SkillsPassport();
		//Education
		Education ed1 = new Education();
		ed1.setTitle("Computer Science");
		ed1.setActivities("<ul><li>Programming</li><li>Data structures</li></ul>");
		JDate ed1From = new JDate(); 
		ed1From.setYear(2008);
		JDate ed1To = new JDate(); 
		ed1To.setYear(2010);
		ed1.setPeriod( new Period( ed1From , ed1To) );
		
		OrganisationalContactInfo ed1OrgContact = new OrganisationalContactInfo();
		ed1OrgContact.setAddress(new ContactAddress( new Address( new CodeLabel("EL", "Hellas") ) ));
		ed1.setOrganisation( new Organisation( "University of Aegean", ed1OrgContact) );
		ed1.setLevel(new CodeLabel("5", "ISCED 5"));
		
		ed1.setField(new CodeLabel("5", "Engineering"));
		List<ReferenceTo> ed1Doc = new ArrayList<ReferenceTo>();
		ed1Doc.add( new ReferenceTo("ATT_1") );
		ed1.setReferenceToList(ed1Doc);
		
		List<Education> edlist = new ArrayList<Education>();
		edlist.add(ed1);
		edlist.add(null);
		LearnerInfo learnerEdu = new LearnerInfo();
		learnerEdu.setEducationList(edlist);
		esp.setLearnerInfo(learnerEdu);
		
		return esp;
	}
	
	public static SkillsPassport educationOrgIsNull(){
		SkillsPassport esp = new SkillsPassport();
		//Education
		Education ed1 = new Education();
		ed1.setTitle("Computer Science");
		ed1.setActivities("<ul><li>Programming</li><li>Data structures</li></ul>");
		JDate ed1From = new JDate(); 
		ed1From.setYear(2008);
		JDate ed1To = new JDate(); 
		ed1To.setYear(2010);
		ed1.setPeriod( new Period( ed1From , ed1To) );
		
		OrganisationalContactInfo ed1OrgContact = new OrganisationalContactInfo();
		ed1OrgContact.setAddress(new ContactAddress( new Address( new CodeLabel("EL", "Hellas") ) ));
		ed1.setLevel(new CodeLabel("5", "ISCED 5"));
		
		List<ReferenceTo> ed1Doc = new ArrayList<ReferenceTo>();
		ed1Doc.add( new ReferenceTo("ATT_1") );
		ed1.setReferenceToList(ed1Doc);
		
		List<Education> edlist = new ArrayList<Education>();
		edlist.add(ed1);
		LearnerInfo learnerEdu = new LearnerInfo();
		learnerEdu.setEducationList(edlist);
		esp.setLearnerInfo(learnerEdu);
		
		return esp;
	}
	
	public static SkillsPassport espMetaData(){
		
		SkillsPassport esp = new SkillsPassport();
		
		return esp;
	}
	
	public static SkillsPassport espDocumentInfo(){

		DateTime today = new DateTime(2013,4,5,0,0,0,DateTimeZone.UTC);
		
		SkillsPassport esp = new SkillsPassport();
		DocumentInfo docInfo = new DocumentInfo();
		
		docInfo.setCreationDate(today);
		
		esp.setDocumentInfo(docInfo);
		
		return esp;
	}

	public static SkillsPassport espWithBundlesECLandELP(){

		DateTime today = new DateTime(2013,4,5,0,0,0,DateTimeZone.UTC);
		
		SkillsPassport esp = new SkillsPassport();
		DocumentInfo docInfo = new DocumentInfo();
		
		docInfo.setCreationDate(today);
		
		docInfo.setDocumentType(EuropassDocumentType.ECV);
		
		List<EuropassDocumentType> bundlesList = new ArrayList<EuropassDocumentType>();
		bundlesList.add(EuropassDocumentType.ELP);
		bundlesList.add(EuropassDocumentType.ECL);
		
		docInfo.setBundle(bundlesList);
		
		esp.setDocumentInfo(docInfo);
		
		return esp;
	}	
	
	public static SkillsPassport espSetAttachmentInfo(){

		SkillsPassport esp = new SkillsPassport();
		
		//Attachments
		List<Attachment> attachments = new ArrayList<Attachment>();
		
		Attachment attachment = new Attachment();
		attachment.setDescription("My Scanned Diploma");
		attachment.setMimeType("application/pdf");
		
		attachments.add( attachment );
		attachments.add( new Attachment( "ATT_2", "My Scanned Msc Degree", "Degree.pdf","application/pdf", "http://europass.instore.gr/ewars/photo/WR3882OO/UPE7434", "My Scanned Msc Degree".getBytes() ) );
		
		esp.setAttachmentList(attachments);
		
		return esp;
	}
	
	public static SkillsPassport espSetAchievements(){
		
		SkillsPassport esp = new SkillsPassport();
		LearnerInfo info = new LearnerInfo();
		
		//Achievements
		List<Achievement> achievements = new ArrayList<Achievement>();
		achievements.add( new Achievement( new CodeLabel(null, "Projects") , null ) );
		achievements.add( new Achievement( new CodeLabel("references", null), "<ul><li>Reference 1</li><li>Reference 2</li><li>Reference 3</li></ul>") ) ;
		
		info.setAchievementList(achievements);
		
		esp.setLearnerInfo(info);
		
		return esp;
	}

	public static SkillsPassport espSetDrivingSkills(){
		
		SkillsPassport esp = new SkillsPassport();
		LearnerInfo info = new LearnerInfo();
		Skills skills = new Skills();
		
		List<String> driving = new ArrayList<String>();
		driving.add("A"); driving.add("B1"); driving.add(null);
		skills.setDriving(new DrivingSkill( driving ));
		
		info.setSkills(skills);
		esp.setLearnerInfo(info);
		
		return esp;
	}
	
	public static SkillsPassport espSetMotherLanguages(){
		
		SkillsPassport esp = new SkillsPassport();
		LearnerInfo info = new LearnerInfo();
		Skills skills = new Skills();
		
		List<LinguisticSkill> mother = new ArrayList<LinguisticSkill>();
		mother.add( new LinguisticSkill(new CodeLabel( null, "Greek" )));
		mother.add( new LinguisticSkill(new CodeLabel( "es", null )));
		mother.add(null);
		
		skills.setLinguistic( new LinguisticSkills(mother, null));
		
		info.setSkills(skills);
		esp.setLearnerInfo(info);
		
		return esp;
	}

	public static SkillsPassport espSetForeignLanguages(){
		
		SkillsPassport esp = new SkillsPassport();
		LearnerInfo info = new LearnerInfo();
		Skills skills = new Skills();
		
		List<LinguisticSkill> foreign = new ArrayList<LinguisticSkill>();
		
		LinguisticSkill foreign1 = new LinguisticSkill(new CodeLabel( null, "English" ));
		foreign1.setProficiencyLevel(new CEFRLevel( "C1", null, "B2", null, null ) );
		
		LinguisticSkill foreign2 = new LinguisticSkill(new CodeLabel( "it", null ));
		List<Certificate<String>> certificates1 = new ArrayList<Certificate<String>>(); 
		certificates1.add(new Certificate<String>( "Certificate in Italics" ) );
		foreign2.setVerifiedBy(certificates1);
		
		foreign.add(foreign1); 
		foreign.add(foreign2);
		foreign.add(null);
		
		skills.setLinguistic( new LinguisticSkills(null, foreign));
		
		info.setSkills(skills);
		esp.setLearnerInfo(info);
		
		return esp;
	}
	
}
