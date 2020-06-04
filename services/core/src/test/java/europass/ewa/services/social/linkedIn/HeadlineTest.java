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

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.social.linkedin.api.LinkedInProfileFull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import europass.ewa.model.Headline;
import europass.ewa.model.SkillsPassport;
import europass.ewa.model.social.MappingListRoot;
import europass.ewa.model.social.SocialMappingsModule;
import europass.ewa.services.social.MappingParser;
import europass.ewa.services.social.MockLinkedInProfile;
import europass.ewa.services.social.Transformer;
import europass.ewa.services.social.linkedin.LinkedInModule;
import europass.ewa.services.social.linkedin.TaxonomyTranslatorHandler;


public class HeadlineTest {

	private static Injector injector = null;

	private static ObjectMapper objectMapper = null;

	private LinkedInProfileFull linkedIn;

	private static MappingListRoot linkedInMapping;

	private static Set<Transformer> linkedInHandlers;
	
	private static TaxonomyTranslatorHandler handler;
	
	Locale espLocale = new Locale("el");

	@BeforeClass
	public static void prepare() {
		injector = Guice.createInjector( new SocialMappingsModule(), new LinkedInModule() );
		objectMapper = MockLinkedInProfile.linkedInMapper();

		linkedInMapping = injector
				.getBinding( Key.get( MappingListRoot.class, Names.named( SocialMappingsModule.SOCIAL_MAPPING_LINKEDIN ) ) ).getProvider()
				.get();

		linkedInHandlers = injector.getProvider( Key.get( new TypeLiteral<Set<Transformer>>() {
		}, Names.named( LinkedInModule.HANDLERS_SET ) ) ).get();
		
		handler = injector.getInstance(TaxonomyTranslatorHandler.class);
		
	}	
	@Test
	public void headlineOnlyTest() throws IOException, ParseException, URISyntaxException{
		SkillsPassport esp = getEsp("headline-only");
		
		Headline headline = esp.getLearnerInfo().getHeadline();
		System.out.println("=============> label before " + headline.getType().getLabel() );
		
		assertThat("ESP Headline code from Headline", 
				headline.getType().getCode(), 
				CoreMatchers.equalTo("position"));
		
		String result = (String)handler.transform(new Object(), "", "HeadlineType", "position", espLocale);
		
		System.out.println("=============> result " + result );
		System.out.println("=============> label after " + headline.getType().getLabel() );
		
		assertThat("ESP Headline label from Headline", 
				headline.getType().getLabel(), 
				CoreMatchers.equalTo(result));
		
		assertThat("ESP Headline description from Headline", 
				headline.getDescription().getLabel(), 
				CoreMatchers.equalTo(linkedIn.getHeadline()));
	}
	@Test
	public void summaryOnlyTest() throws IOException, ParseException, URISyntaxException{
		SkillsPassport esp = getEsp("summary-only");
		
		Headline headline = esp.getLearnerInfo().getHeadline();

		assertThat("ESP Headline code from Summary", 
				headline.getType().getCode(), 
				CoreMatchers.equalTo("personal_statement"));
		
		String result = (String)handler.transform(new Object(), "", "HeadlineType", "personal_statement", espLocale);
		
		assertThat("ESP Headline label from Summary", 
				"ΠΡΟΣΩΠΙΚΗ ΔΗΛΩΣΗ", 
				CoreMatchers.equalTo(result));
		
		assertThat("ESP Headline description from Summary", 
				headline.getDescription().getLabel(), 
				CoreMatchers.equalTo( linkedIn.getSummary() ));
	}
	@Test
	public void headlineAndsummaryTest() throws IOException, ParseException, URISyntaxException{
		SkillsPassport esp = getEsp("headline-and-summary");
		
		Headline headline = esp.getLearnerInfo().getHeadline();

		assertThat("ESP Headline code from Summary", 
				headline.getType().getCode(), 
				CoreMatchers.equalTo("personal_statement"));
		
		String result = (String)handler.transform(new Object(), "", "HeadlineType", "personal_statement", espLocale);
		assertThat("ESP Headline label from Summary", 
				"ΠΡΟΣΩΠΙΚΗ ΔΗΛΩΣΗ", 
				CoreMatchers.equalTo(result));
		
		assertThat("ESP Headline description from Summary", 
				headline.getDescription().getLabel(), 
				CoreMatchers.equalTo( linkedIn.getSummary() ));
	}
	@Test
	public void headlineAndsummaryEmptyStringTest() throws IOException, ParseException, URISyntaxException{
		SkillsPassport esp = getEsp("headline-and-summary-emptystring");
		
		Headline headline = esp.getLearnerInfo().getHeadline();

		assertThat("ESP Headline code from Summary", 
				headline.getType().getCode(), 
				CoreMatchers.equalTo("personal_statement"));
		
		String result = (String)handler.transform(new Object(), "", "HeadlineType", "personal_statement", espLocale);
		assertThat("ESP Headline label from Summary", 
				"ΠΡΟΣΩΠΙΚΗ ΔΗΛΩΣΗ", 
				CoreMatchers.equalTo(result));
		
		assertThat("ESP Headline description from Summary", 
				headline.getDescription().getLabel(), 
				CoreMatchers.equalTo( linkedIn.getSummary() ));
	}
	@Test
	public void namesOnlyTest() throws IOException, ParseException, URISyntaxException{
		SkillsPassport esp = getEsp("names-only");
		
		Headline headline = esp.getLearnerInfo().getHeadline();

		Assert.assertNull("ESP Headline ", headline);
		
	}
	
	private SkillsPassport getEsp( String jsonFileName ) throws IOException, ParseException, URISyntaxException {
		ClassLoader cl = currentThread().getContextClassLoader();
		final File file = new File( cl.getResource( "json/"+jsonFileName+".json" ).toURI() );
		String fullJSON = FileUtils.readFileToString( file );
		
		assertNotNull( "JSON string", fullJSON );
		linkedIn = objectMapper.readValue( fullJSON, LinkedInProfileFull.class );
		
		MappingParser<LinkedInProfileFull> parser = 
				new MappingParser<LinkedInProfileFull>( linkedInMapping, linkedInHandlers );
		return parser.parse( linkedIn, espLocale, MockLinkedInProfile.COOKIE_ID );
	}
}
