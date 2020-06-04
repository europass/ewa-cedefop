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
package europass.ewa.services.social.linkedIn;

import static java.lang.Thread.currentThread;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.hamcrest.CoreMatchers;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.social.linkedin.api.LinkedInProfileFull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import europass.ewa.model.Education;
import europass.ewa.model.SkillsPassport;
import europass.ewa.model.social.MappingListRoot;
import europass.ewa.model.social.SocialMappingsModule;
import europass.ewa.services.social.MappingParser;
import europass.ewa.services.social.MockLinkedInProfile;
import europass.ewa.services.social.Transformer;
import europass.ewa.services.social.linkedin.LinkedInModule;

public class EducationTest {

	private static Injector injector = null;

	private static ObjectMapper objectMapper = null;

	private static MappingListRoot linkedInMapping;

	private static Set<Transformer> linkedInHandlers;
	
	
	@BeforeClass
	public static void prepare() {
		injector = Guice.createInjector( new SocialMappingsModule(), new LinkedInModule() );
		objectMapper = MockLinkedInProfile.linkedInMapper();

		linkedInMapping = injector
				.getBinding( Key.get( MappingListRoot.class, Names.named( SocialMappingsModule.SOCIAL_MAPPING_LINKEDIN ) ) ).getProvider()
				.get();

		linkedInHandlers = injector.getProvider( Key.get( new TypeLiteral<Set<Transformer>>() {
		}, Names.named( LinkedInModule.HANDLERS_SET ) ) ).get();
		
	}

	@Test
	public void educationExperienceTest() throws IOException, ParseException, URISyntaxException{
		ClassLoader cl = currentThread().getContextClassLoader();
		final File file = new File( cl.getResource( "json/edu_profile.json" ).toURI() );
		String fullJSON = FileUtils.readFileToString( file );
		
		assertNotNull( "JSON string", fullJSON );
		LinkedInProfileFull linkedIn = objectMapper.readValue( fullJSON, LinkedInProfileFull.class );
		
		MappingParser<LinkedInProfileFull> parser = 
				new MappingParser<LinkedInProfileFull>( linkedInMapping, linkedInHandlers );
		SkillsPassport esp = parser.parse( linkedIn, Locale.ENGLISH, MockLinkedInProfile.COOKIE_ID );
		
		List<Education> educationList = esp.getLearnerInfo().getEducationList();
		
		List<org.springframework.social.linkedin.api.Education> linkedInEducations = linkedIn.getEducations();		
		
		assertTrue(linkedInEducations.size() > 0);
		assertTrue(linkedInEducations.size() == educationList.size());
		
		for(Education edu : educationList){
			
			int index = educationList.indexOf(edu);
			org.springframework.social.linkedin.api.Education linkedInEducation = linkedInEducations.get(index);
			
			if ( edu.getTitle() != null ){
				assertThat("ESP Education title", 
						edu.getTitle(), 
						CoreMatchers.is(linkedInEducation.getDegree()+", "+linkedInEducation.getFieldOfStudy()));
			}
			
			if ( edu.getField() != null ){
				assertThat("ESP Education field", 
					edu.getField().getLabel(), 
					CoreMatchers.is(linkedInEducation.getFieldOfStudy()));
			}
			if ( edu.getActivities() != null ){
				assertThat("ESP Education activities", 
					edu.getActivities(), 
					CoreMatchers.is(linkedInEducation.getActivities()));
			}
			if ( edu.getPeriod() != null && edu.getPeriod().getFrom() != null){
				FullProfileTest.dateAssertions("ESP Education #"+index+" from", 
							edu.getPeriod().getFrom(), 
							linkedInEducation.getStartDate());
			}
			if ( edu.getTitle() != null && edu.getPeriod().getTo() != null){
				FullProfileTest.dateAssertions("ESP Education #"+index+" to",
							edu.getPeriod().getTo(), 
							linkedInEducation.getEndDate());
			}
			if ( edu.getOrganisation() != null ){
				assertThat("ESP Organization name", 
						edu.getOrganisation().getName(), 
						CoreMatchers.is(linkedInEducation.getSchoolName()));
			}
		}
	}
	
}
