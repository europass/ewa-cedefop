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

import org.hamcrest.CoreMatchers;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Years;
import org.junit.Assert;
import org.junit.Test;

import europass.ewa.enums.ConversionFileType;
import europass.ewa.enums.ExportDestination;
import europass.ewa.model.DocumentInfo;
import europass.ewa.model.MockObjects;
import europass.ewa.model.SkillsPassport;
import europass.ewa.statistics.data.StatsAchievement;
import europass.ewa.statistics.data.StatsDetails;
import europass.ewa.statistics.data.StatsEntry;
import europass.ewa.statistics.data.StatsNationality;

public class StatisticsManagerTest {

	@Test
	public void emptyEsp(){
		StatsEntry actual = new StatisticsManager( MockObjects.emptyCV() ).prepare();
		
		Assert.assertThat( "Empty CV (DocType)", 
				actual.getDocumentType(), CoreMatchers.is( "ECV" ) );
		Assert.assertThat( "Empty CV (Locale)", 
				actual.getLanguage(), CoreMatchers.is( "it" ) );
		Assert.assertThat( "Empty CV (Generator)", 
				actual.getGeneratedBy(), CoreMatchers.is( "JUnit Test" ) );
	}
	
	@Test
	public void newGeneration(){
		SkillsPassport esp = new SkillsPassport();
		DocumentInfo info = new DocumentInfo();
		DateTime now = new DateTime( DateTimeZone.UTC );
		info.setCreationDate( now );
		info.setLastUpdateDate( now );
		esp.setDocumentInfo( info );
		
		StatsEntry actual = new StatisticsManager( esp ).prepare();
		
		Assert.assertThat( "New Document", actual.getIsNew(), CoreMatchers.is( true ) );
	}
	
	@Test
	public void repeatedGeneration(){
		
		SkillsPassport esp = new SkillsPassport();
		DocumentInfo info = new DocumentInfo();
		
		info.setCreationDate( new DateTime( DateTimeZone.UTC ).minusMonths( 3 ) );
		info.setLastUpdateDate( new DateTime( DateTimeZone.UTC ) );
		esp.setDocumentInfo( info );
		
		StatsEntry actual = new StatisticsManager( esp ).prepare();
		
		Assert.assertThat( "Uploaded (not new) Document", actual.getIsNew(), CoreMatchers.is( false ) );
	}
	
	
	@Test
	public void pipedTelephones(){
		SkillsPassport esp = MockObjects.complete();
		
		StatsEntry actual = new StatisticsManager( esp ).prepare();

		Assert.assertThat( "Expected Telephones", 
				actual.getDetails().getTelephoneTypes(), 
				CoreMatchers.is( "work|mobile" ) );
	}
	
	@Test
	public void pipedIMTypes(){
		SkillsPassport esp = MockObjects.complete();
		
		StatsEntry actual = new StatisticsManager( esp ).prepare();
		
		Assert.assertThat( "Expected IMTypes", 
				actual.getDetails().getImTypes(), 
				CoreMatchers.is( "msn|twitter" ) );
	}
	
	
	@Test
	public void cumulativeAttachmentSize(){
		SkillsPassport esp = MockObjects.complete();
		
		StatsEntry actual = new StatisticsManager( esp ).prepare();
		
		Assert.assertNotNull( actual );
		
		StatsDetails actualDetails = actual.getDetails();
		
		Assert.assertNotNull( actualDetails );
		
		Long size = actualDetails.getCumulativeSize();
		
		Assert.assertNotNull( size );
		
		Assert.assertThat( "Expected Attachments Size > 0", 
				size.longValue() > 0 , CoreMatchers.is( true ) );	
		
	}
	
	
	@Test
	public void numberOfAttachments(){
		SkillsPassport esp = MockObjects.espFileDataObj();
		
		StatsEntry actual = new StatisticsManager( esp ).prepare();
		
		Assert.assertNotNull( "Entry" , actual );
		
		StatsDetails actualDetails = actual.getDetails();
		
		Assert.assertNotNull( "Details", actualDetails );
		
		Integer num = actualDetails.getNumberOfFiles();
		
		Assert.assertNotNull( "Count of Attachments" , num );
		
		Assert.assertThat( "Expected Attachments Count to be 2", 
				num.intValue() , CoreMatchers.is( 2 ) );
		
	}
	
	@Test
	public void attachmentTypes(){
		
		SkillsPassport esp = MockObjects.espFileDataObj();
		
		StatsEntry actual = new StatisticsManager(esp).prepare();
		
		Assert.assertNotNull( actual );
		
		StatsDetails actualDetails = actual.getDetails();
		
		Assert.assertNotNull( actualDetails );
		
		String types = actualDetails.getTypeOfFiles();
		
		Assert.assertNotNull( types );
		
		Assert.assertThat( "Expected Attachments Types", 
				types, CoreMatchers.is( "application/pdf|application/pdf" ) );
		
	}
	
	@Test
	public void headlineTypeOnly(){
		SkillsPassport esp = MockObjects.headline();
		esp.getLearnerInfo().getHeadline().setDescription( null );
		
		StatsEntry actual = new StatisticsManager(esp).prepare();
		
		Assert.assertThat( "Expected Headline Types", 
				actual.getHeadlineType(), 
				CoreMatchers.is( "personal_statement" ) );
		Assert.assertNull("Headline description is null", actual.getHeadlineDescription());
	}
	
	@Test
	public void headlineDescription(){
		SkillsPassport esp = MockObjects.headline();
		
		StatsEntry actual = new StatisticsManager(esp).prepare();
		
		Assert.assertNull( "Expected Headline Description", 
				actual.getHeadlineDescription() );
	}
	
	@Test
	public void workExperienceDuration(){
		SkillsPassport esp = MockObjects.complete();
		
		StatsEntry actual = new StatisticsManager(esp).prepare();
		
		Assert.assertThat( "WorkExperience Duration", 
				actual.getWorkExperiences().get(0).getDuration().intValue(), 
				CoreMatchers.is( 2 ) );
		
	}
	
	@Test
	public void eduPeriodFrom(){
		SkillsPassport esp = MockObjects.complete();
		
		StatsEntry actual = new StatisticsManager(esp).prepare();
		
		
		Assert.assertThat( "Period From ", 
				actual.getEducationExperiences().get(0).getPeriodFrom(), 
				CoreMatchers.is( new DateTime(2008,5,1,0,0,0, DateTimeZone.UTC) ) );
		
	}
	
	@Test
	public void employerCountry(){
		SkillsPassport esp = MockObjects.complete();
		
		StatsEntry actual = new StatisticsManager(esp).prepare();
		
		Assert.assertNotNull( "Entry: ", actual );
		
		Assert.assertThat( 
				"Work 1: Employer Country", 
				actual.getWorkExperiences().get(0).getEmployerCountry(), 
				CoreMatchers.is( "JP" ) );
		
	}
	
	@Test
	public void employerSector(){
		SkillsPassport esp = MockObjects.complete();
		
		StatsEntry actual = new StatisticsManager(esp).prepare();
		
		Assert.assertThat( "Employer Sector", 
				actual.getWorkExperiences().get(0).getEmployerSector(), 
				CoreMatchers.is( "Q") );
		
	}
	
	@Test
	public void workingPosition(){
		SkillsPassport esp = MockObjects.complete();
		
		StatsEntry actual = new StatisticsManager(esp).prepare();
		
		Assert.assertThat( "Working Position", 
				actual.getWorkExperiences().get(0).getPosition(), 
				CoreMatchers.is( "R" ) );
		
	}
	
	
	@Test
	public void workExperienceYears(){
		SkillsPassport esp = MockObjects.espWorkEduObj();
		
		StatsEntry actual = new StatisticsManager(esp).prepare();
		
		Assert.assertThat( "Work Experience Years", actual.getWorkExperienceYears(), CoreMatchers.is( 3 ) );
		
	}
	
	@Test
	public void eduExperienceYears(){
		SkillsPassport esp = MockObjects.espWorkEduObj();
		
		StatsEntry actual = new StatisticsManager(esp).prepare();
		
		Assert.assertThat( "Education Experience Years", actual.getEducationYears(), 
				CoreMatchers.is( 8 ) );
		
	}
	
	@Test
	public void testIsNew(){
		SkillsPassport esp = new SkillsPassport();
		DocumentInfo info = new DocumentInfo();
		
		info.setCreationDate( new DateTime(2008,5,3,0,0,0, DateTimeZone.UTC) );
		info.setLastUpdateDate( new DateTime(2008,5,3,0,0,0, DateTimeZone.UTC) );
		esp.setDocumentInfo( info );
		
		StatsEntry actual = new StatisticsManager( esp ).prepare();
		
		
		Assert.assertThat( "Uploaded (not new) Document", actual.getIsNew(), CoreMatchers.is( true ) );
	}
	
	@Test
	public void fileFormat(){
		
		ConversionFileType fileType = ConversionFileType.OPEN_DOC;
		
		StatsEntry statsEntry = new StatsEntry();
		
		statsEntry.setFileFormat(fileType.getDescription());
		
		Assert.assertThat( "File Type Format", statsEntry.getFileFormat(), CoreMatchers.is( "odt" ) );

	}
	
	@Test
	public void postalAddressCountry(){
		SkillsPassport esp = MockObjects.espSimpleObj();
		
		StatsEntry actual = new StatisticsManager( esp ).prepare();
		
		Assert.assertThat( "Person Postal Address Country is", actual.getPostalAddressCountry(), CoreMatchers.is( "EL" ) );

	}
	
	@Test
	public void testGender(){
		SkillsPassport esp = MockObjects.complete();
		
		StatsEntry actual = new StatisticsManager( esp ).prepare();
		
		Assert.assertThat( "Person's Gender", actual.getGender(), CoreMatchers.is( 'F' ) );

	}
	
	@Test
	public void testBirthday(){
		SkillsPassport esp = MockObjects.complete();
		
		StatsEntry actual = new StatisticsManager( esp ).prepare();
		
		Assert.assertThat( "Person's Birthday", actual.getDateOfBirth(), CoreMatchers.is( new DateTime(1984,2,10,0,0,0,DateTimeZone.UTC) ) );

	}
	
	@Test
	public void testNationalities(){
		SkillsPassport esp = MockObjects.complete();
		
		StatsEntry actual = new StatisticsManager( esp ).prepare();
		
		StatsNationality stats_nationality = new StatsNationality();
        String code;
        String label;
        String nationality;
		for(int i=0;i<actual.getNationalities().size();i++){
			 code = esp.getLearnerInfo().getIdentification().getDemographics().getNationalityList().get(i).getCode();
			 label = esp.getLearnerInfo().getIdentification().getDemographics().getNationalityList().get(i).getLabel();
			 nationality = code == null ? label : code;
			 stats_nationality.setNationality(nationality);
		     Assert.assertThat( "Person's Nationality", actual.getNationalities().get(i).getNationality(), CoreMatchers.is(stats_nationality.getNationality()) );
		}

	}
	
	@Test
	public void testAchievements(){
		SkillsPassport esp = MockObjects.complete();
		
		StatsEntry actual = new StatisticsManager( esp ).prepare();
		
		StatsAchievement stats_achievement = new StatsAchievement();

        String code;
        String label;
        String category;
		for(int i=0;i<actual.getAchievements().size();i++){
			 code = esp.getLearnerInfo().getAchievementList().get(i).getTitle().getCode();
			 label = esp.getLearnerInfo().getAchievementList().get(i).getTitle().getLabel();
			 category = code == null? label : code;
			 stats_achievement.setCategory(category);
		     Assert.assertThat( "Person's Achievement", actual.getAchievements().get(i).getCategory(), CoreMatchers.is(stats_achievement.getCategory()) );
		}

	}
	
	@Test
	public void testSkillsDriving(){
		SkillsPassport esp = MockObjects.complete();
		
		StatsEntry actual = new StatisticsManager( esp ).prepare();
		
		Assert.assertThat( "Person's Driving Skills", actual.getSkillsDriving(), CoreMatchers.is("A|B1") );

	}
	
	@Test
	public void testPersonsAge(){
		SkillsPassport esp = MockObjects.complete();
		
		StatsEntry actual = new StatisticsManager( esp ).prepare();
		
		DateTime start = actual.getDateOfBirth().withTimeAtStartOfDay();
		DateTime end = DateTime.now(DateTimeZone.UTC);
		int age = Years.yearsBetween(start, end).getYears();
		
		Assert.assertThat( "Person's Age in Years", actual.getAge(), CoreMatchers.is(age) );
	}
	
	@Test
	public void eduQualification(){
		SkillsPassport esp = MockObjects.espEduObj();
		
		StatsEntry actual = new StatisticsManager( esp ).prepare();
		
		String title;
		for(int i=0;i<actual.getEducationExperiences().size();i++){
			title = esp.getLearnerInfo().getEducationList().get(i).getTitle();
			Assert.assertThat( "Qualification Title", actual.getEducationExperiences().get(i).getQualification(), CoreMatchers.is(title) );
		}

	}
	
	@Test
	public void organisationCountry(){
		SkillsPassport esp = MockObjects.espEduObj();
		
		StatsEntry actual = new StatisticsManager(esp).prepare();
		
		Assert.assertNotNull( "Entry: ", actual );
		
		Assert.assertThat( 
				"Edu 1: Organisation Country", 
				actual.getEducationExperiences().get(0).getOrganisationCountry(), 
				CoreMatchers.is( "EL" ) );
	}
	
	@Test
	public void organisationLevel(){
		SkillsPassport esp = MockObjects.espEduObj();
		
		StatsEntry actual = new StatisticsManager(esp).prepare();
		
		Assert.assertNotNull( "Entry: ", actual );
		
		Assert.assertThat( 
				"Edu 1: Organisation Level", 
				actual.getEducationExperiences().get(0).getQualificationLevel(), 
				CoreMatchers.is( "5" ) );
	}
	
	@Test
	public void educationField(){
		SkillsPassport esp = MockObjects.espEduObj();
		
		StatsEntry actual = new StatisticsManager(esp).prepare();
		
		Assert.assertNotNull( "Entry: ", actual );
		
		Assert.assertThat( 
				"Edu 1: Education field", 
				actual.getEducationExperiences().get(0).getEducationalField(), 
				CoreMatchers.is( "5" ) );
	}
	
	
	@Test
	public void motherLanguages(){
		SkillsPassport esp = MockObjects.espLanguagesObj();
		
		StatsEntry actual = new StatisticsManager(esp).prepare();
		
		Assert.assertThat( "Mother Language #1", 
				actual.getMotherLangs().get(0).getLanguage(), 
				CoreMatchers.is( "el" ) );
		Assert.assertThat( "Mother Language #2", 
				actual.getMotherLangs().get(1).getLanguage(), 
				CoreMatchers.is( "es" ) );
	}

	@Test
	public void foreignLanguages(){
		SkillsPassport esp = MockObjects.espLanguagesObj();
		
		StatsEntry actual = new StatisticsManager(esp).prepare();
		
		Assert.assertThat( "Foreign Language #1", 
				actual.getForeignLanguages().get(0).getLanguageType(), 
				CoreMatchers.is( "en" ) );
		Assert.assertThat( "Foreign Language #1 Listening Level", 
				actual.getForeignLanguages().get(0).getListeningLevel(),
				CoreMatchers.is( "C1" ) );
		Assert.assertThat( "Foreign Language #1 Reading Level", 
				actual.getForeignLanguages().get(0).getReadingLevel(),
				CoreMatchers.is( "C2" ) );
		Assert.assertThat( "Foreign Language #1 Spoken Interaction Level", 
				actual.getForeignLanguages().get(0).getSpokenInteractionLevel(),
				CoreMatchers.is( "B2" ) );
		Assert.assertThat( "Foreign Language #1 Spoken Production Level", 
				actual.getForeignLanguages().get(0).getSpokenProductionLevel(),
				CoreMatchers.is( "B2" ) );
		Assert.assertThat( "Foreign Language #1 Writing Level", 
				actual.getForeignLanguages().get(0).getWritingLevel(),
				CoreMatchers.is( "B1" ) );
		
		
		Assert.assertThat( "Foreign Language #2", 
				actual.getForeignLanguages().get(1).getLanguageType(), 
				CoreMatchers.is( "it" ) );
		Assert.assertThat( "Foreign Language #2 Listening Level", 
				actual.getForeignLanguages().get(1).getListeningLevel(),
				CoreMatchers.is( "B1" ) );
		Assert.assertThat( "Foreign Language #2 Reading Level", 
				actual.getForeignLanguages().get(1).getReadingLevel(),
				CoreMatchers.is( "B2" ) );
		Assert.assertThat( "Foreign Language #2 Spoken Interaction Level", 
				actual.getForeignLanguages().get(1).getSpokenInteractionLevel(),
				CoreMatchers.is( "B1" ) );
		Assert.assertThat( "Foreign Language #2 Spoken Production Level", 
				actual.getForeignLanguages().get(1).getSpokenProductionLevel(),
				CoreMatchers.is( "A2" ) );
		Assert.assertThat( "Foreign Language #2 Writing Level", 
				actual.getForeignLanguages().get(1).getWritingLevel(),
				CoreMatchers.is( "A1" ) );
	}

	@Test
	public void foreignLanguagesCertificates(){
		SkillsPassport esp = MockObjects.espLanguagesObj();
		
		StatsEntry actual = new StatisticsManager(esp).prepare();
		
		Assert.assertThat( "Foreign Language #1 Certificate #1 Title", 
				actual.getForeignLanguages().get(0).getCertificates().get(0).getTitle(), 
				CoreMatchers.is( "Cambridge Certificate of Proficiency in English" ) );

		Assert.assertThat( "Foreign Language #1 Certificate #1 Issue Date: Year", 
				actual.getForeignLanguages().get(0).getCertificates().get(0).getIssueDate().getYear(), 
				CoreMatchers.is( 1998 ) );
		
		Assert.assertThat( "Foreign Language #1 Certificate #1 Level", 
				actual.getForeignLanguages().get(0).getCertificates().get(0).getCefrLevel(), 
				CoreMatchers.is( "C1" ) );

		Assert.assertThat( "Foreign Language #1 Certificate #2 Title", 
				actual.getForeignLanguages().get(0).getCertificates().get(1).getTitle(), 
				CoreMatchers.is( "Michigan Certificate of Proficiency in English" ) );
		
		
		Assert.assertThat( "Foreign Language #2 Certificate #1 Title", 
				actual.getForeignLanguages().get(1).getCertificates().get(0).getTitle(), 
				CoreMatchers.is( "Certificate of Adequacy in Italian" ) );
	}
	
	@Test
	public void exportToDestination(){
		
		ExportDestination exportTo = ExportDestination.DROPBOX;
		
		StatsEntry statsEntry = new StatsEntry();
		
		statsEntry.setExportTo(exportTo.getDescription());
		
		Assert.assertThat( "Export Destination is", statsEntry.getExportTo(), CoreMatchers.is( "DROPBOX" ) );

	}
	
}
