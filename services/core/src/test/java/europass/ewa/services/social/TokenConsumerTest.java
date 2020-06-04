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
package europass.ewa.services.social;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.cli.ParseException;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import europass.ewa.model.SkillsPassport;
import europass.ewa.services.social.MockLinkedInProfile.ProfileFull;

public class TokenConsumerTest {

	@Test
	public void consumeName() throws ParseException {
		
		MappingParser<ProfileFull> parser = new MappingParser<ProfileFull>( MockMappings.firstNameMappingRoot(),
				MockObjects.emptyHandlers() );

		SkillsPassport loaded = parser.parse( MockLinkedInProfile.linkedIn(), Locale.ENGLISH, MockLinkedInProfile.COOKIE_ID);

		assertThat( "FirstName is loaded", loaded.getLearnerInfo().getIdentification().getPersonName().getFirstName(),
				CoreMatchers.is( MockObjects.NAME ) );
	}
	
	@Test
	public void consumeNameWithSimpleHandler() throws ParseException {
		
		Set<Transformer> handlers = new HashSet<>();
		handlers.add( MockObjects.prefixHandler() );
		
		MappingParser<ProfileFull> parser = new MappingParser<ProfileFull>( MockMappings.surnameMappingWithThroughRoot(),
				handlers );

		SkillsPassport loaded = parser.parse( MockLinkedInProfile.linkedIn(), Locale.ENGLISH, MockLinkedInProfile.COOKIE_ID );

		assertThat( "Surname is loaded", loaded.getLearnerInfo().getIdentification().getPersonName().getSurname(),
				CoreMatchers.is( "TEST" + MockObjects.SURNAME  ) );
	}
	
	@Test
	public void consumeSummary() throws ParseException{
		
		MappingParser<ProfileFull> parser = new MappingParser<ProfileFull>( MockMappings.summaryMappingRoot(),
				MockObjects.emptyHandlers() );

		SkillsPassport loaded = parser.parse( MockLinkedInProfile.linkedIn(), Locale.ENGLISH, MockLinkedInProfile.COOKIE_ID );

		assertThat( "Summary is loaded", loaded.getLearnerInfo().getAchievementList().get( 0 ).getDescription(),
				CoreMatchers.is( MockObjects.SUMMARY ) );
	}
	
	@Test
	public void consumeImWithHandler() throws ParseException {
		Set<Transformer> handlers = new HashSet<>();
		handlers.add( MockObjects.imTypeHandler() );
		
		MappingParser<ProfileFull> parser = new MappingParser<ProfileFull>( MockMappings.imTypeMappingWithThroughRoot(),
				handlers );

		SkillsPassport loaded = parser.parse( MockLinkedInProfile.linkedIn(), Locale.ENGLISH, MockLinkedInProfile.COOKIE_ID );

		assertThat( "IM Items Size", loaded.getLearnerInfo().getIdentification().getContactInfo().getInstantMessagingList().size(),
				CoreMatchers.is( 2 ) );
		
		assertThat( "IM Type 0 is loaded", 
				loaded.getLearnerInfo().getIdentification().getContactInfo().getInstantMessagingList().get( 0 ).getUse().getCode(),
				CoreMatchers.is( MockObjects.IM_GTALK ) );
		assertThat( "IM Name 0 is loaded", 
				loaded.getLearnerInfo().getIdentification().getContactInfo().getInstantMessagingList().get( 0 ).getContact(),
				CoreMatchers.is( MockObjects.IM_0_NAME ) );
		
		assertThat( "IM Type 1 is loaded", 
				loaded.getLearnerInfo().getIdentification().getContactInfo().getInstantMessagingList().get( 1 ).getUse().getCode(),
				CoreMatchers.is( MockObjects.IM_MSN ) );
		assertThat( "IM Name 1 is loaded", 
				loaded.getLearnerInfo().getIdentification().getContactInfo().getInstantMessagingList().get( 1 ).getContact(),
				CoreMatchers.is( MockObjects.IM_1_NAME ) );
	}
	
	@Test
	public void consumeEducation() throws ParseException{
		MappingParser<ProfileFull> parser = new MappingParser<ProfileFull>( MockMappings.educationMappingRoot(),
				MockObjects.emptyHandlers() );
		
		SkillsPassport loaded = parser.parse( MockLinkedInProfile.linkedIn(), Locale.ENGLISH, MockLinkedInProfile.COOKIE_ID );
		
		assertThat( "Education Item Size", 
				loaded.getLearnerInfo().getEducationList().size(),
				CoreMatchers.is( 2 ) );
		
		assertThat( "Education Degree 0 is loaded", 
				loaded.getLearnerInfo().getEducationList().get( 0 ).getTitle(),
				CoreMatchers.is( MockObjects.EDU_0_DEGREE ) );
		assertThat( "Education Start Year 0 is loaded", 
				loaded.getLearnerInfo().getEducationList().get( 0 ).getPeriod().getFrom().getYear(),
				CoreMatchers.is( 2000 ) );
		assertThat( "Education Start Day 0 is loaded", 
				loaded.getLearnerInfo().getEducationList().get( 0 ).getPeriod().getFrom().getDay(),
				CoreMatchers.is( 15 ) );
		assertThat( "Education End Month 0 is loaded", 
				loaded.getLearnerInfo().getEducationList().get( 0 ).getPeriod().getTo().getMonth(),
				CoreMatchers.is( 12 ) );
		
		assertThat( "Education Degree 1 is loaded", 
				loaded.getLearnerInfo().getEducationList().get( 1 ).getTitle(),
				CoreMatchers.is( MockObjects.EDU_1_DEGREE ) );
		assertThat( "Education Start Year 1 is loaded", 
				loaded.getLearnerInfo().getEducationList().get( 1 ).getPeriod().getFrom().getYear(),
				CoreMatchers.is( 2005 ) );
		assertThat( "Education Start Day 1 is loaded", 
				loaded.getLearnerInfo().getEducationList().get( 1 ).getPeriod().getFrom().getDay(),
				CoreMatchers.is( 15 ) );
		assertThat( "Education End Month 1 is loaded", 
				loaded.getLearnerInfo().getEducationList().get( 1 ).getPeriod().getTo().getMonth(),
				CoreMatchers.is( 12 ) );
	}
	
	@Test
	public void skills() throws ParseException{
		MappingParser<ProfileFull> parser = 
				new MappingParser<ProfileFull>( MockMappings.skillsMappingRoot(),
				MockObjects.availableHandlers() );
		
		SkillsPassport loaded = parser.parse( MockLinkedInProfile.linkedIn(), Locale.ENGLISH, MockLinkedInProfile.COOKIE_ID );
		
		assertThat( "Skills are loaded", loaded.getLearnerInfo().getSkills().getOther().getDescription(),
				CoreMatchers.containsString( "JAVA, PHP" ) );
		
	}
	
	@Test
	public void courses() throws ParseException{
		MappingParser<ProfileFull> parser = 
				new MappingParser<ProfileFull>( MockMappings.coursesMappingRoot(),
				MockObjects.availableHandlers() );
		
		SkillsPassport loaded = parser.parse( MockLinkedInProfile.linkedIn(), Locale.ENGLISH, MockLinkedInProfile.COOKIE_ID );

		String desc = loaded.getLearnerInfo().getAchievementList().get( 0 ).getDescription();
		assertThat( "Courses are loaded", desc,
				CoreMatchers.containsString( "<li>PHP for dummies</li>" ) );
		assertThat( "Courses are loaded", desc,
				CoreMatchers.startsWith( "<ul>" ) );
		assertThat( "Courses are loaded", desc,
				CoreMatchers.endsWith( "</ul>" ) );
		
		assertThat( "Courses are loaded", loaded.getLearnerInfo().getAchievementList().get( 0 ).getTitle().getCode(),
				CoreMatchers.is( "courses" ) );
	}
	
	
	@Test
	public void emptyModel() throws ParseException{
		MappingParser<ProfileFull> parser = 
				new MappingParser<ProfileFull>( MockMappings.firstNameMappingRoot(),
				MockObjects.emptyHandlers() );
		
		SkillsPassport loaded = parser.parse( MockLinkedInProfile.emptyLinkedIn(), Locale.ENGLISH, MockLinkedInProfile.COOKIE_ID );
		
		assertThat( "FirstName is loaded", loaded.getLearnerInfo().getIdentification().getPersonName().getFirstName(),
				CoreMatchers.is( "" ) );
		
		assertNotNull( "LearnerInfo is not null", loaded.getLearnerInfo());
		assertNotNull( "Identification is null", loaded.getLearnerInfo().getIdentification());
	}
	
	@Test
	public void combined() throws ParseException{
		MappingParser<ProfileFull> parser = 
				new MappingParser<ProfileFull>( MockMappings.sampleRoot(),
				MockObjects.availableHandlers() );
		
		SkillsPassport loaded = parser.parse( MockLinkedInProfile.linkedIn(), Locale.ENGLISH, MockLinkedInProfile.COOKIE_ID );
		
		assertThat( "FirstName is loaded", loaded.getLearnerInfo().getIdentification().getPersonName().getFirstName(),
				CoreMatchers.is( MockObjects.NAME ) );
		
		assertThat( "Surname is loaded", loaded.getLearnerInfo().getIdentification().getPersonName().getSurname(),
				CoreMatchers.is( "TEST" + MockObjects.SURNAME ) );
		
		assertThat( "Summary is loaded", loaded.getLearnerInfo().getAchievementList().get( 0 ).getDescription(),
				CoreMatchers.is( MockObjects.SUMMARY ) );
		
		assertThat( "IM Type 1 is loaded", 
				loaded.getLearnerInfo().getIdentification().getContactInfo().getInstantMessagingList().get( 1 ).getUse().getCode(),
				CoreMatchers.is( MockObjects.IM_MSN ) );
		
		assertThat( "Education End Month 1 is loaded", 
				loaded.getLearnerInfo().getEducationList().get( 1 ).getPeriod().getTo().getMonth(),
				CoreMatchers.is( 12 ) );
		
		assertThat( "Courses And Summary are loaded As Achievements", loaded.getLearnerInfo().getAchievementList().size(),
				CoreMatchers.is( 2 ) );
	}
}
