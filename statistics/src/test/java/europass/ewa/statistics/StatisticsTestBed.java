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
import java.util.Properties;

import org.hamcrest.CoreMatchers;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.junit.Assert;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Scopes;

import europass.ewa.database.guice.HibernateModule;
import europass.ewa.statistics.data.StatsAchievement;
import europass.ewa.statistics.data.StatsDetails;
import europass.ewa.statistics.data.StatsEducation;
import europass.ewa.statistics.data.StatsEntry;
import europass.ewa.statistics.data.StatsForeignLanguages;
import europass.ewa.statistics.data.StatsLinguisticCertificate;
import europass.ewa.statistics.data.StatsMotherLanguage;
import europass.ewa.statistics.data.StatsNationality;
import europass.ewa.statistics.data.StatsWorkExperience;
import europass.ewa.statistics.guice.StatisticsModule;

public class StatisticsTestBed {


	protected static Injector injector = null;

	protected final DatabaseStatisticsLogger logger;
	
	protected final String STATS_ENTRY = "StatsEntry"; 
	protected final String STATS_ENTRY_DETAILS = "StatsDetails";
	
	protected final String STATS_ENTRY_ID = "id";
	protected final String STATS_ENTRY_DETAILS_ID_FK = "statsEntry.id";

	
	public StatisticsTestBed(){
		Properties hp = new Properties();
		injector = Guice.createInjector(
					new HibernateModule(hp, Scopes.SINGLETON),
					new StatisticsModule()
				);
		
		logger = injector.getInstance(DatabaseStatisticsLogger.class);

		Assert.assertNotNull( logger );
	}
	@Test
	public void testDB(){
		StatsEntry entry = new StatsEntry();
		entry.setDocumentType( "ECV" );
		entry.setGeneratedBy( "JUnit Test" );
		entry.setExportTo("PC");
		logger.store( entry );
		
		StatsEntry logged = (StatsEntry) logger.fetchLatest().get( 0 );
		
		Assert.assertThat( "Document Type: ", logged.getDocumentType(), CoreMatchers.is("ECV"));
		Assert.assertThat( "Generator: ", logged.getGeneratedBy(), CoreMatchers.is("JUnit Test"));
		Assert.assertThat( "Export Destination: ", logged.getExportTo(), CoreMatchers.is("PC"));
	}
	
	@Test
	public void testEntryAndDetails(){
		StatsEntry entry = new StatsEntry();
		entry.setGeneratedBy( "JUnit Test for Details" );
		StatsDetails details = new StatsDetails();
		details.setNumberOfFiles( 5 );
		
		entry.setDetails( details );
		entry.setAge(29);
		entry.setDateOfBirth(new DateTime(new DateTime(2006,1,1,0,0, DateTimeZone.UTC)));
		details.setStatsEntry( entry );
		
		logger.store( entry );
		
		StatsEntry logged = (StatsEntry) logger.fetchLatest().get( 0 );
		
		Assert.assertNotNull( "Logged" , logged );
		
		StatsDetails loggedDetails = (StatsDetails) logger.fetchLatestDetails().get( 0 );
		
		Assert.assertNotNull("Logged details" , loggedDetails );
		Assert.assertThat("Details: Number of Files", loggedDetails.getNumberOfFiles(), CoreMatchers.is( 5 ) );
		Assert.assertThat("Details to StatsEntry: ", loggedDetails.getStatsEntry().getId().longValue(), CoreMatchers.is( logged.getId().longValue() ) );
		
	}
	
	@Test
	public void testEntryAndNationalityDB(){
		StatsEntry entry = new StatsEntry();
		
		String[] names = {"European", "en"};
		StatsNationality item1 = new StatsNationality();
		item1.setNationality( names[0] );
		StatsNationality item2 = new StatsNationality();
		item2.setNationality( names[1] );
		List<StatsNationality> nationalities = new ArrayList<>();
		nationalities.add( item1 );
		nationalities.add( item2 );
		
		entry.setNationalities( nationalities );
		//item1.setStatsEntry( entry );
		//item2.setStatsEntry( entry );
		
		logger.store( entry );
		
		StatsEntry logged = (StatsEntry) logger.fetchLatest().get( 0 );
		
		List<StatsNationality> loggedNationalities = logged.getNationalities();
		Assert.assertNotNull( loggedNationalities );
		int i = 0;
		for ( StatsNationality loggedNat : loggedNationalities ){
			Assert.assertThat( "Nationality name", loggedNat.getNationality(), CoreMatchers.is( names[i] ) );
			i++;
			//Assert.assertThat( "Nationality to StatsEntry: ", loggedNat.getStatsEntry(), CoreMatchers.is( logged ) );
		}
		Assert.assertThat("2 Nationality items", i , CoreMatchers.is( 2 ) );
	}
	
	@Test
	public void testEntryAndAchievementsDB(){
		StatsEntry entry = new StatsEntry();
		
		String[] names = {"Publications", "References"};
		StatsAchievement item1 = new StatsAchievement();
		item1.setCategory( names[0] );
		StatsAchievement item2 = new StatsAchievement();
		item2.setCategory( names[1] );
		List<StatsAchievement> nationalities = new ArrayList<>();
		nationalities.add( item1 );
		nationalities.add( item2 );
		
		entry.setAchievements( nationalities );
		//item1.setStatsEntry( entry );
		//item2.setStatsEntry( entry );
		
		logger.store( entry );
		
		StatsEntry logged = (StatsEntry) logger.fetchLatest().get( 0 );
		
		List<StatsAchievement> loggedList = logged.getAchievements();
		Assert.assertNotNull( loggedList );
		int i = 0;
		for ( StatsAchievement loggedItem : loggedList ){
			Assert.assertThat( "Achievement category", loggedItem.getCategory(), CoreMatchers.is( names[i] ) );
			i++;
			//Assert.assertThat( "Achievement to StatsEntry: ", loggedItem.getStatsEntry(), CoreMatchers.is( logged ) );
		}
		Assert.assertThat("2 Achievement items", i , CoreMatchers.is( 2 ) );
	}
	
	@Test
	public void testEntryAndMotherTongueDB(){
		StatsEntry entry = new StatsEntry();
		
		String[] names = {"el", "en"};
		StatsMotherLanguage item1 = new StatsMotherLanguage();
		item1.setLanguage( names[0] );
		StatsMotherLanguage item2 = new StatsMotherLanguage();
		item2.setLanguage( names[1] );
		
		List<StatsMotherLanguage> list = new ArrayList<>();
		list.add( item1 );
		list.add( item2 );
		
		entry.setMotherLangs( list );
		//item1.setStatsEntry( entry );
		//item2.setStatsEntry( entry );
		
		logger.store( entry );
		
		StatsEntry logged = (StatsEntry) logger.fetchLatest().get( 0 );
		
		List<StatsMotherLanguage> loggedList = logged.getMotherLangs();
		Assert.assertNotNull( loggedList );

		int i = 0;
		for ( StatsMotherLanguage loggedItem : loggedList ){
			Assert.assertThat( "Mother Tongue name", loggedItem.getLanguage(), CoreMatchers.is( names[i] ) );
			i++;
			//Assert.assertThat( "Nationality to StatsEntry: ", loggedItem.getStatsEntry(), CoreMatchers.is( logged ) );
		}
		Assert.assertThat("2 Nationality items", i , CoreMatchers.is( 2 ) );
	}
	
	@Test
	public void dateTimeTestFormat(){
		
		StatsEntry entry = new StatsEntry();
		
		entry.setGeneratedBy( "Branch: JUnit Test for Work Experience" );
		
		String[] positions = {"Secretary", "Agent"};
		
		StatsWorkExperience item2 = new StatsWorkExperience();
		item2.setPosition( positions[1] );
		
		DateTime periodFrom = new DateTime( DateTimeZone.UTC );
		DateTime periodTo = new DateTime( DateTimeZone.UTC );
		
        try
        {
        	final DateTimeFormatter format = DateTimeFormat.forPattern("MM/dd/yyyy HH:mm:ss");
			//ok.Feburary is leap year in 2000.
            periodFrom = format.parseDateTime("02/12/-12 1:20:40");
            
//        	System.out.println(periodFrom.getEra()); // returns 0 -> that means B.C. era Before Christ
//        	System.out.println(periodFrom.getYear());
        	periodTo = format.parseDateTime("02/12/2010 1:20:40");           
            
        }catch(IllegalArgumentException ex)
        {
//            System.out.println(ex.getMessage());
        }
        
		item2.setPeriodFrom( periodFrom );
		item2.setPeriodTo( new DateTime(periodTo, DateTimeZone.UTC));
		
		List<StatsWorkExperience> list = new ArrayList<>();
		list.add( item2 );
		
		entry.setWorkExperiences( list );
		//item2.setStatsEntry( entry );

		// TODO: assertions
	}
	
	@Test
	public void testEntryAndWorkDB(){
		StatsEntry entry = new StatsEntry();
		
		entry.setGeneratedBy( "JUnit Test for Work Experience" );
		
		String[] positions = {"Secretary", "Agent"};
		StatsWorkExperience item1 = new StatsWorkExperience();
		item1.setPosition( positions[0] );
		item1.setPeriodFrom( new DateTime(2000,1,1,0,0, DateTimeZone.UTC) );
		item1.setPeriodTo( new DateTime(2005,1,1,0,0, DateTimeZone.UTC) );
		
		StatsWorkExperience item2 = new StatsWorkExperience();
		item2.setPosition( positions[1] );
		item2.setPeriodFrom( new DateTime(1754,1,1,0,0, DateTimeZone.UTC) );
		item2.setPeriodTo( new DateTime(2010,1,1,0,0, DateTimeZone.UTC) );
		
		List<StatsWorkExperience> list = new ArrayList<>();
		list.add( item1 );
		list.add( item2 );
		
		entry.setWorkExperiences( list );
		//item1.setStatsEntry( entry );
		//item2.setStatsEntry( entry );
		
		logger.store( entry );
		
		StatsEntry logged = (StatsEntry) logger.fetchLatest().get( 0 );
		
		List<StatsWorkExperience> loggedList = logged.getWorkExperiences();
		Assert.assertNotNull( loggedList );
		int i = 0;
		for ( StatsWorkExperience loggedItem : loggedList ){
			Assert.assertThat( "Work name", loggedItem.getPosition(), CoreMatchers.is( positions[i] ) );
			i++;
			//Assert.assertThat( "Work to StatsEntry: ", loggedItem.getStatsEntry(), CoreMatchers.is( logged ) );
		}
		
		Assert.assertThat("2 Work items", i , CoreMatchers.is( 2 ) );
	}
	
	@Test
	public void testEntryAndEduDB(){
		StatsEntry entry = new StatsEntry();
		
		entry.setGeneratedBy( "JUnit Test for Education Experience" );
		
		String[] titles = {"Bachelor", "Master", "PhD"};
		StatsEducation item1 = new StatsEducation();
		item1.setQualification( titles[0] );
		item1.setQualificationLevel( "0" );
		item1.setEducationalField( "IT" );
		item1.setPeriodFrom( new DateTime(2000,1,1,0,0, DateTimeZone.UTC) );
		item1.setPeriodTo( new DateTime(2005,1,1,0,0, DateTimeZone.UTC) );
		
		StatsEducation item2 = new StatsEducation();
		item2.setQualification( titles[1] );
		item2.setQualificationLevel( "1" );
		item2.setOrganisationCountry( "Poland" );
		item2.setPeriodFrom( new DateTime(2004,1,1,0,0, DateTimeZone.UTC) );
		item2.setPeriodTo( new DateTime(2006,1,1,0,0, DateTimeZone.UTC) );
		
		StatsEducation item3 = new StatsEducation();
		item3.setQualification( titles[2] );
		item3.setQualificationLevel( "3" );
		item3.setPeriodFrom( new DateTime(2006,1,1,0,0, DateTimeZone.UTC) );
		item3.setPeriodTo( new DateTime(2012,1,1,0,0, DateTimeZone.UTC) );
		
		List<StatsEducation> list = new ArrayList<>();
		list.add( item1 );
		list.add( item2 );
		list.add( item3 );
		
		entry.setEducationExperiences( list );
		//item1.setStatsEntry( entry );
		//item2.setStatsEntry( entry );
		//item3.setStatsEntry( entry );
		
		logger.store( entry );
		
		StatsEntry logged = (StatsEntry) logger.fetchLatest().get( 0 );
		
		List<StatsEducation> loggedList = logged.getEducationExperiences();
		Assert.assertNotNull( loggedList );
		int i = 0;
		for ( StatsEducation loggedItem : loggedList ){
			Assert.assertThat( "Edu name", loggedItem.getQualification(), CoreMatchers.is( titles[i] ) );
			Assert.assertThat( "Edu level", loggedItem.getQualificationLevel(), CoreMatchers.is( String.valueOf( list.get(i).getQualificationLevel() ) ) );
			
			switch( i ){
				case 0: {
					Assert.assertThat( "Edu 0 Field", loggedItem.getEducationalField(), CoreMatchers.is( "IT" ) );
					break;
				}
				case 1:{
					Assert.assertThat( "Edu 1 Organisation Country", loggedItem.getOrganisationCountry(), CoreMatchers.is( "Poland" ) );
					break;
				}
			}
			i++;
			//Assert.assertThat( "Edu to StatsEntry: ", loggedItem.getStatsEntry(), CoreMatchers.is( logged ) );
		}
		Assert.assertThat("3 Edu items", i , CoreMatchers.is( 3 ) );
	}
	
	@Test
	public void testEntryAndForeignAndCertificateDB(){
		StatsEntry entry = new StatsEntry();
		
		DateTime certDate = new DateTime(2010,1,1,0,0, DateTimeZone.UTC);
		entry.setGeneratedBy( "JUnit Test for Foreign Languages" );
		
		String[] titles = {"en", "de", "fr"};
		String[] certs = {"Mittel", "Kleines", "Delf"};
		StatsForeignLanguages item1 = new StatsForeignLanguages();
		item1.setLanguageType(  titles[0] );
		item1.setListeningLevel( "A1" );
		
		StatsForeignLanguages item2 = new StatsForeignLanguages();
		item2.setLanguageType( titles[1] );
		
		List<StatsLinguisticCertificate> list2 = new ArrayList<>();
		StatsLinguisticCertificate cert1 = new StatsLinguisticCertificate();
		cert1.setTitle( certs[0] );
		cert1.setCefrLevel( "B1" );
		//cert1.setStatsForeignLanguages( item2 );
		
		StatsLinguisticCertificate cert2 = new StatsLinguisticCertificate();
		cert2.setTitle( certs[1] );
		cert2.setIssueDate(certDate );
		//cert2.setStatsForeignLanguages( item2 );
		
		list2.add( cert1 );
		list2.add( cert2 );
		
		item2.setCertificates( list2 );
		
		StatsForeignLanguages item3 = new StatsForeignLanguages();
		item3.setLanguageType( titles[2] );
		
		List<StatsForeignLanguages> list = new ArrayList<>();
		list.add( item1 );
		list.add( item2 );
		list.add( item3 );
		
		entry.setForeignLanguages( list );
		//item1.setStatsEntry( entry );
		//item2.setStatsEntry( entry );
		//item3.setStatsEntry( entry );
		
		logger.store( entry );
		
		StatsEntry logged = (StatsEntry) logger.fetchLatest().get( 0 );
		
		List<StatsForeignLanguages> loggedList = logged.getForeignLanguages();
		Assert.assertNotNull( loggedList );
		int i = 0;
		for ( StatsForeignLanguages loggedItem : loggedList ){
			Assert.assertThat( "Foreign Language name", loggedItem.getLanguageType(), CoreMatchers.is( titles[i] ) );
			
			switch( i ){
				case 0: {
					Assert.assertThat( "Foreign Language 0 Listening Level", loggedItem.getListeningLevel(), CoreMatchers.is( "A1" ) );
					break;
				}
				case 1:{
					List<StatsLinguisticCertificate> loggedCerts = loggedItem.getCertificates();
					Assert.assertNotNull( "Linguistic Certificates", loggedCerts );
					Assert.assertThat( "Linguistic Certificates are 2", loggedCerts.size(), CoreMatchers.is( 2 ) );
					Assert.assertThat( "1st Linguistic Certificates Level", loggedCerts.get( 0 ).getCefrLevel(), CoreMatchers.is( "B1" ) );
					Assert.assertThat( "2nd Linguistic Certificates Date", loggedCerts.get( 1 ).getIssueDate(), CoreMatchers.is( certDate ) );
					break;
				}
			}
			i++;
			//Assert.assertThat( "Foreign Language to StatsEntry: ", loggedItem.getStatsEntry(), CoreMatchers.is( logged ) );
		}
		Assert.assertThat("3 Foreign Language items", i , CoreMatchers.is( 3 ) );
	}
}
