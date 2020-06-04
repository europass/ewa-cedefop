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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.cli.ParseException;
import org.apache.commons.io.FileUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
//import org.springframework.social.linkedin.api.Course;
import org.springframework.social.linkedin.api.ImAccount;
//import org.springframework.social.linkedin.api.Language;
import org.springframework.social.linkedin.api.LinkedInDate;
import org.springframework.social.linkedin.api.LinkedInProfileFull;
//import org.springframework.social.linkedin.api.Patent;
import org.springframework.social.linkedin.api.PhoneNumber;
import org.springframework.social.linkedin.api.Position;
//import org.springframework.social.linkedin.api.Publication;
import org.springframework.social.linkedin.api.Recommendation;
import org.springframework.social.linkedin.api.TwitterAccount;
import org.springframework.social.linkedin.api.UrlResource;
//import org.springframework.social.linkedin.api.Volunteer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import europass.ewa.model.Achievement;
import europass.ewa.model.AchievementType;
import europass.ewa.model.ContactMethod;
import europass.ewa.model.Education;
import europass.ewa.model.FileData;
import europass.ewa.model.GenericSkill;
import europass.ewa.model.JDate;
import europass.ewa.model.LinguisticSkill;
import europass.ewa.model.LinguisticSkills;
import europass.ewa.model.Period;
import europass.ewa.model.SkillsPassport;
import europass.ewa.model.WorkExperience;
import europass.ewa.model.format.HtmlSanitizer;
import europass.ewa.model.social.MappingListRoot;
import europass.ewa.model.social.SocialMappingsModule;
import europass.ewa.resources.JsonResourceBundle;
import europass.ewa.services.social.EnrichedItem;
import europass.ewa.services.social.MappingParser;
import europass.ewa.services.social.MockLinkedInProfile;
import europass.ewa.services.social.Transformer;
import europass.ewa.services.social.linkedin.CertificationsHandler;
import europass.ewa.services.social.linkedin.CoursesHandler;
import europass.ewa.services.social.linkedin.HonorsAwardsHandler;
import europass.ewa.services.social.linkedin.LanguageHandler;
import europass.ewa.services.social.linkedin.LinkedInModule;
import europass.ewa.services.social.linkedin.PatentsHandler;
import europass.ewa.services.social.linkedin.PhotoHandler;
import europass.ewa.services.social.linkedin.ProjectsHandler;
import europass.ewa.services.social.linkedin.PublicationsHandler;
import europass.ewa.services.social.linkedin.SkillHandler;
import europass.ewa.services.social.linkedin.TaxonomyTranslatorHandler;
import europass.ewa.services.social.linkedin.VolunteerHandler;

@SuppressWarnings("unchecked")
public class FullProfileTest {

	private static Injector injector = null;

	private static ObjectMapper objectMapper = null;

	private LinkedInProfileFull linkedIn;
	private Map<String, Object> extraDataObj;
	
	private static int TWITTER_ACCOUNTS;

	private static MappingListRoot linkedInMapping;

	private static Set<Transformer> linkedInHandlers;
	
	private static List<Achievement> achievements;
	private static GenericSkill otherSkills;
	private static Transformer translationHandler;

	private static Object dummyObject;
	private static EnrichedItem<Object> enrichedItem;
	private static JDate jdate;
	
	private static SkillsPassport esp;

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

	@Before
	public void prepareJson() throws IOException, ParseException, URISyntaxException {
		ClassLoader cl = currentThread().getContextClassLoader();
		final File file = new File( cl.getResource( "json/profile_full.json" ).toURI() );
		String fullJSON = FileUtils.readFileToString( file );
		
		assertNotNull( "JSON string", fullJSON );
		linkedIn = objectMapper.readValue( fullJSON, LinkedInProfileFull.class );
		
		extraDataObj = linkedIn.getExtraData();
		
		MappingParser<LinkedInProfileFull> parser = 
				new MappingParser<LinkedInProfileFull>( linkedInMapping, linkedInHandlers );
		esp = parser.parse( linkedIn, Locale.ENGLISH, MockLinkedInProfile.COOKIE_ID );
		
		TWITTER_ACCOUNTS = linkedIn.getTwitterAccounts().size();
		// for tests that use translationHandler
		translationHandler = injector.getInstance(TaxonomyTranslatorHandler.class);
		achievements = esp.getLearnerInfo().getAchievementList();
		otherSkills = esp.getLearnerInfo().getSkills().getOther();
		
		new ArrayList<>();
		dummyObject = new Object();
		enrichedItem = new EnrichedItem<>(dummyObject);
		jdate = new JDate();
	}

	@Test
	public void loadJson() {
		assertNotNull( "JSON as Object", linkedIn );
	}
	
	@Test
	public void loadToEsp(){
		assertNotNull("Mapping LinkedIn Root", linkedInMapping);
		
		assertNotNull("Mapping Handlers Set", linkedInHandlers);
		
		assertNotNull("SkillsPassport", esp);
	}
	
	@Test
	public void identificationTest(){
		
		assertThat("ESP Firstname", 
				esp.getLearnerInfo().getIdentification().getPersonName().getFirstName(), 
				CoreMatchers.is("Robert"));
		
		assertThat("ESP Lastname", 
				esp.getLearnerInfo().getIdentification().getPersonName().getSurname(), 
				CoreMatchers.is("Drysdale"));
	}
	
	@Test
	public void locationTest(){
		//EWA-924 Municipality same as Country
//		assertThat("ESP Location Municipality name", 
//				esp.getLearnerInfo().getIdentification().getContactInfo().getAddress().getContact().getMunicipality(), 
//				CoreMatchers.is( "Cork" ));
		
		String espCountryCode =
				esp.getLearnerInfo().getIdentification().getContactInfo().getAddress().getContact().getCountry().getCode();; 
		
		assertThat("ESP Location country code", 
				espCountryCode,
				CoreMatchers.is( "IE" ));
		
		String espCountryName =
				esp.getLearnerInfo().getIdentification().getContactInfo().getAddress().getContact().getCountry().getLabel(); 

		ResourceBundle bundle = ResourceBundle.getBundle("bundles/Country", new JsonResourceBundle.Control());
		
		assertThat("ESP Location country code", 
				espCountryName,
				CoreMatchers.is( bundle.getString(espCountryCode) ));
	}

	@Test
	public void mainAddressTest(){

		assertThat("ESP Address", 
				esp.getLearnerInfo().getIdentification().getContactInfo().getAddress().getContact().getAddressLine(), 
				CoreMatchers.is( "Rory Gallagher St, 48" ));
	}

	@Test
	public void emailTest(){

		assertThat("ESP email address", 
				esp.getLearnerInfo().getIdentification().getContactInfo().getEmail().getContact(), 
				CoreMatchers.is( "r.drysdale@gmail.com" ));
	}
	
	@Test
	public void imAccountTest(){

		List<ContactMethod> imAccountsList = esp.getLearnerInfo().getIdentification().getContactInfo().getInstantMessagingList();

		List<ImAccount> linkedInIMAccountsList = linkedIn.getImAccounts();
		
		assertTrue(linkedInIMAccountsList.size() > 0);
		assertTrue(linkedInIMAccountsList.size() == imAccountsList.size());
		
		for(ContactMethod account : imAccountsList){
			
			int index = imAccountsList.indexOf(account);
			
			assertThat("ESP IMAccount Use", 
					account.getUse().getCode(), 
					CoreMatchers.is(linkedInIMAccountsList.get(index).getImAccountType()));
			
			assertThat("ESP IMAccount contact", 
					account.getContact(), 
					CoreMatchers.is(linkedInIMAccountsList.get(index).getImAccountName()));
		}
	}

	@Test
	public void phoneNumberTest(){

		List<ContactMethod> phoneNumbersList = esp.getLearnerInfo().getIdentification().getContactInfo().getTelephoneList();

		List<PhoneNumber> linkedInPhoneNumbersList = linkedIn.getPhoneNumbers();
		
		assertTrue(linkedInPhoneNumbersList.size() > 0);
		assertTrue(phoneNumbersList.size() == linkedInPhoneNumbersList.size());
		
		for(ContactMethod phone : phoneNumbersList){
			
			int index = phoneNumbersList.indexOf(phone);
			
			assertThat("ESP Phone Use", 
					phone.getUse().getCode(), 
					CoreMatchers.is(linkedInPhoneNumbersList.get(index).getPhoneType()));
			
			assertThat("ESP Phone contact", 
					phone.getContact(), 
					CoreMatchers.is(linkedInPhoneNumbersList.get(index).getPhoneNumber()));
		}
	}
	
	@Test
	public void twitterAccountsTest(){

		List<ContactMethod> twAccountsList = esp.getLearnerInfo().getIdentification().getContactInfo().getWebsiteList();

		List<TwitterAccount> linkedInTWAccountsList = linkedIn.getTwitterAccounts();
		
		assertTrue(linkedInTWAccountsList.size() > 0);

		for(TwitterAccount account : linkedInTWAccountsList){
			
			int index = linkedInTWAccountsList.indexOf(account);
			
			assertThat("ESP Twitter Account contact", 
					twAccountsList.get(index).getContact(), 
					CoreMatchers.is("https://twitter.com/"+account.getProviderAccountName()));
		}
	}

	@Test
	public void memberUrlResourcesTest(){

		List<ContactMethod> websitesList = esp.getLearnerInfo().getIdentification().getContactInfo().getWebsiteList();

		List<UrlResource> memberUrlResourcesList = linkedIn.getMemberUrlResources();
		
		assertTrue(memberUrlResourcesList.size() > 0);
		
		for(UrlResource account : memberUrlResourcesList){
			
			int index = memberUrlResourcesList.indexOf(account) + TWITTER_ACCOUNTS;
			
			assertThat("ESP Website contact", 
					websitesList.get(index).getContact(), 
					CoreMatchers.is(account.getUrl()));
		}
	}
	
	@Test
	public void demographicsTest(){

		JDate dateOfBirth = esp.getLearnerInfo().getIdentification().getDemographics().getBirthdate();

		LinkedInDate linkedInDateOfBirth = linkedIn.getDateOfBirth();
		
		dateAssertions("Date of Birth",dateOfBirth,linkedInDateOfBirth);

	}
	
	@Test
	public void profilePictureTest(){

		PhotoHandler handler = injector.getInstance(PhotoHandler.class);
		
//		List<?> pictureUrls = LinkedInUtilities.extraDataFieldValues(extraDataObj, "pictureUrls");
//		
		FileData photoData = esp.getLearnerInfo().getIdentification().getPhoto();
//		String linkedInProfilePicURL = linkedIn.getProfilePictureUrl();

		FileData result = (FileData) handler.transform(extraDataObj, photoData, MockLinkedInProfile.COOKIE_ID);
		
		assertThat("ESP Profile Photo",
				photoData, CoreMatchers.equalTo(result));
	}	

	@Ignore
	@Test
	public void headlineTest(){
		//See HeadlineTest
	}

	@Test
	public void workExperienceTest(){

		List<Position> linkedInPositions = linkedIn.getPositions();
		
		List<WorkExperience> workExperienceList = esp.getLearnerInfo().getWorkExperienceList();
		
		assertTrue(workExperienceList.size() > 0);
		assertTrue(workExperienceList.size() > linkedInPositions.size());	// due to volunteer as work experience
		
		for(Position linkedInPosition : linkedInPositions){
			
			int index = linkedInPositions.indexOf(linkedInPosition);
			
			WorkExperience xp = workExperienceList.get(index);
			
			assertThat("ESP Work Experience title", 
					xp.getPosition().getLabel(), 
					CoreMatchers.is(linkedInPosition.getTitle()));

			Period period = xp.getPeriod();
			if ( period != null ){
				
				JDate from = period.getFrom();
				if ( from != null ){
					dateAssertions("ESP Position '"+linkedInPosition.getTitle()+"' from", 
							from, 
							linkedInPosition.getStartDate());
				}
				JDate to = period.getTo();
				if ( to != null ){
					dateAssertions("ESP Position '"+linkedInPosition.getTitle()+"' to",
							xp.getPeriod().getTo(), 
							linkedInPosition.getEndDate());
				}
			}
			assertThat("ESP Work Experience Company Name", 
					xp.getEmployer().getName(), 
					CoreMatchers.is(linkedInPosition.getCompany().getName()));
		}
	}

	@Test
	public void educationExperienceTest(){

		List<Education> educationList = esp.getLearnerInfo().getEducationList();
		
		List<org.springframework.social.linkedin.api.Education> linkedInEducations = linkedIn.getEducations();		
		
		assertTrue(linkedInEducations.size() > 0);
		assertTrue(linkedInEducations.size() == educationList.size());
		
		for(Education edu : educationList){
			
			int index = educationList.indexOf(edu);
			org.springframework.social.linkedin.api.Education linkedInEducation = linkedInEducations.get(index);
			
			assertThat("ESP Education title", 
					edu.getTitle(), 
					CoreMatchers.is(linkedInEducation.getDegree()+
							(Strings.isNullOrEmpty( linkedInEducation.getFieldOfStudy() )
							? "" : ", "+linkedInEducation.getFieldOfStudy()) ));

			Assert.assertNull("ESP Education field", 
					edu.getField());
			
			assertThat("ESP Education activities", 
					edu.getActivities(), 
					CoreMatchers.is(linkedInEducation.getActivities()));

			dateAssertions("ESP Education #"+index+" from", 
						edu.getPeriod().getFrom(), 
						linkedInEducation.getStartDate());
				
			dateAssertions("ESP Education #"+index+" to",
						edu.getPeriod().getTo(), 
						linkedInEducation.getEndDate());
			
			assertThat("ESP Organization name", 
					edu.getOrganisation().getName(), 
					CoreMatchers.is(linkedInEducation.getSchoolName()));
		}
	}
	
	@Test
	public void foreignLanguagesTest(){

		LanguageHandler handler = injector.getInstance(LanguageHandler.class);
		
		List<LinguisticSkill> languages = esp.getLearnerInfo().getSkills().getLinguistic().getForeignLanguage();

		LinguisticSkills skillsFromLinkedIn = (LinguisticSkills) handler.transform(extraDataObj, new LinguisticSkills() );
		
		assertTrue(skillsFromLinkedIn.getForeignLanguage().size() > 0);
		assertTrue(languages.size() == skillsFromLinkedIn.getForeignLanguage().size());
		
		int i = 0;
		for( LinguisticSkill languageFromLinkedIn : skillsFromLinkedIn.getForeignLanguage()){
			
			languageFromLinkedIn.getProficiencyLevel();
			
			languages.get(i);
			
			assertThat("ESP Foreign Language name", 
					languageFromLinkedIn.getDescription().getLabel(), 
					CoreMatchers.is(languages.get(i).getDescription().getLabel()));

			assertThat("ESP Foreign Language Spoken Interaction", 
					languageFromLinkedIn.getProficiencyLevel().getSpokenInteraction(), 
					CoreMatchers.is(languages.get(i).getProficiencyLevel().getSpokenInteraction()));

			assertThat("ESP Foreign Language Listening", 
					languageFromLinkedIn.getProficiencyLevel().getListening(), 
					CoreMatchers.is(languages.get(i).getProficiencyLevel().getListening()));
			
			i++;
		}
	}
	
	@Test
	public void motherLanguagesTest(){

		LanguageHandler handler = injector.getInstance(LanguageHandler.class);
		
		List<LinguisticSkill> languages = esp.getLearnerInfo().getSkills().getLinguistic().getMotherTongue();
		
		LinguisticSkills skillsFromLinkedIn = (LinguisticSkills) handler.transform(extraDataObj, new LinguisticSkills() );
		
		assertTrue(skillsFromLinkedIn.getMotherTongue().size() > 0);
		assertTrue(languages.size() == skillsFromLinkedIn.getMotherTongue().size());
		
		int i = 0;
		for( LinguisticSkill languageFromLinkedIn : skillsFromLinkedIn.getMotherTongue()){
			
			languages.get(i);
			
			assertThat("ESP Foreign Language name", 
					languageFromLinkedIn.getDescription().getLabel(), 
					CoreMatchers.is(languages.get(i).getDescription().getLabel()));
			i++;
		}
	}

	@Test
	public void jobRelatedSkillsTest(){

		String skills = esp.getLearnerInfo().getSkills().getJobRelated().getDescription();
		
		List<String> linkedInSkills = linkedIn.getSkills();
		
		assertTrue(linkedInSkills.size() > 0);
		
		SkillHandler handler = injector.getInstance(SkillHandler.class);
		String result = (String) handler.transform(linkedInSkills, "");
		
		assertThat("ESP job-related skills", 
				skills, 
				CoreMatchers.equalTo(result));
		
	}

	@Test
	public void honorsTest(){

		HonorsAwardsHandler handler = injector.getInstance(HonorsAwardsHandler.class);
		
		List<Achievement> honorsAwardsMap = (List<Achievement>)handler.transform(extraDataObj, new ArrayList<Achievement>(), "AchievementType", "honors_awards");
		
		assertTrue(honorsAwardsMap.size() > 0);
		assertTrue(achievements.size() > honorsAwardsMap.size());
		
		boolean found = false;
		for( Achievement honorAward : honorsAwardsMap){
			
			for(Achievement achievement : achievements){
				
				String titleCode = honorAward.getTitle().getCode();
				String titleLabel = honorAward.getTitle().getLabel();
				
				if(achievement.getTitle().getCode().equals(titleCode) &&
					achievement.getTitle().getLabel().equals(titleLabel) &&
					achievement.getDescription().equals(honorAward.getDescription())
				){
					found = true;
					break;
				}
			}
			
		    assertTrue(found);
		    found = false;
		}
	}
	
	@Test
	public void interestsTest(){

		String linkedInInterests = linkedIn.getInterests();
		String to = "";
		
		to = translationHandler.transform(otherSkills, to);
		assertThat("ESP Other Skills interests description", 
					linkedInInterests, 
					CoreMatchers.containsString(otherSkills.getDescription()));
	}
	
	@Test
	public void recommendationsTest(){

		List<Recommendation> linkedInRecommendations = linkedIn.getRecommendationsReceived();
		
		assertTrue(linkedInRecommendations.size() > 0);
		
		String to = "";
		
		Iterable<Achievement> filtered = Iterables.filter(achievements, new Predicate<Achievement>() {
			@Override
			public boolean apply(Achievement a) {
				return AchievementType.references.name().equals(a.getTitle().getCode());
			}
		});
		List<Achievement> theseAchievements = Lists.newArrayList(filtered);
		Assert.assertThat("Recommentations to Achievements: ", theseAchievements.size() > 0, CoreMatchers.is(  true ));
		
		for(Achievement achievement : theseAchievements){	
	
			to = translationHandler.transform(dummyObject, to, "AchievementType", AchievementType.references.name());
			assertThat("ESP Achievement recommendations title label", 
					to, 
					CoreMatchers.containsString(achievement.getTitle().getLabel()));
			
			String achievementDesc = achievement.getDescription();
			
			for(Recommendation linkedInItem : linkedInRecommendations){
				
				String recommender = linkedInItem.getRecommender().getFirstName()+
						" "+linkedInItem.getRecommender().getLastName();
				
				assertThat("ESP Achievement recommendations html contains recommender's first and last name", 
						achievementDesc, 
						CoreMatchers.containsString(recommender));
				
				assertThat("ESP Achievement recommendations html contains recommendation type", 
						achievementDesc, 
						CoreMatchers.containsString(linkedInItem.getRecommendationType().name()));

				//Achievement description get's HTML sanitized.
				String sanitized = HtmlSanitizer.sanitize( linkedInItem.getRecommendationText() ) ;
				assertThat("ESP Achievement recommendations html contains recommendation text", 
						achievementDesc, 
						CoreMatchers.containsString(sanitized));

			}
		}
	}

	// COURSES, PATENTS, PUBLICATION & VOLUNTEER TESTS ARE IGNORED ###
	
	@Test
	public void coursesTest(){
		
		CoursesHandler handler = injector.getInstance(CoursesHandler.class);
		
		List<Achievement> coursesMap = (List<Achievement>)handler.transform(extraDataObj, new ArrayList<Achievement>(), "SocialAchievementType", "courses");		

		assertTrue(coursesMap.size() > 0);
		assertTrue(achievements.size() > coursesMap.size());
		
		boolean found = false;
		for( Achievement course : coursesMap){
			
			for(Achievement achievement : achievements){
				
				String titleLabel = course.getTitle().getLabel();
				
				if(	achievement.getTitle().getLabel().equals(titleLabel) &&
					achievement.getDescription().equals(course.getDescription())
				){
					found = true;
					break;
				}
			}
			
		    assertTrue(found);
		    found = false;
		}		
	}
	
	@Test
	public void certificationsTest(){

		CertificationsHandler handler = injector.getInstance(CertificationsHandler.class);
		
		List<Achievement> certificationsMap = (List<Achievement>)handler.transform(extraDataObj, new ArrayList<Achievement>(), "SocialAchievementType", "certifications");		

		assertTrue(certificationsMap.size() > 0);
		assertTrue(achievements.size() > certificationsMap.size());
		
		boolean found = false;
		for( Achievement certification : certificationsMap){
			
			for(Achievement achievement : achievements){
				
				String titleLabel = certification.getTitle().getLabel();
				
				if(	achievement.getTitle().getLabel().equals(titleLabel) &&
					achievement.getDescription().equals(certification.getDescription())
				){
					found = true;
					break;
				}
			}
			
		    assertTrue(found);
		    found = false;
		}
	}	


	@Test
	public void patentsTest(){

		PatentsHandler handler = injector.getInstance(PatentsHandler.class);
		
		List<Achievement> patentsMap = (List<Achievement>)handler.transform(extraDataObj, new ArrayList<Achievement>(), "SocialAchievementType", "patents");		

		assertTrue(patentsMap.size() > 0);
		assertTrue(achievements.size() > patentsMap.size());
		
		boolean found = false;
		for( Achievement patent : patentsMap){
			
			for(Achievement achievement : achievements){
				
				String titleLabel = patent.getTitle().getLabel();
				
				if(	achievement.getTitle().getLabel().equals(titleLabel) &&
					achievement.getDescription().equals(patent.getDescription())
				){
					found = true;
					break;
				}
			}
			
		    assertTrue(found);
		    found = false;
		}
	}	

	@Test
	public void publicationTest(){

		PublicationsHandler handler = injector.getInstance(PublicationsHandler.class);
		
		List<Achievement> publicationsMap = (List<Achievement>)handler.transform(extraDataObj, new ArrayList<Achievement>(), "AchievementType", "publications");		

		assertTrue(publicationsMap.size() > 0);
		assertTrue(achievements.size() > publicationsMap.size());
		
		boolean found = false;
		for( Achievement publication : publicationsMap){
			
			for(Achievement achievement : achievements){
				
				String titleLabel = publication.getTitle().getLabel();
				
				if(achievement.getTitle().getLabel().equals(titleLabel) &&
					achievement.getDescription().equals(publication.getDescription())
				){
					found = true;
					break;
				}
			}
			
		    assertTrue(found);
		    found = false;
		}
	}	

	@Test
	public void projectsTest(){

		ProjectsHandler handler = injector.getInstance(ProjectsHandler.class);
		
		List<Achievement> projectsMap = (List<Achievement>)handler.transform(extraDataObj, new ArrayList<Achievement>(), "AchievementType", "projects");		

		assertTrue(projectsMap.size() > 0);
		assertTrue(achievements.size() > projectsMap.size());
		
		boolean found = false;
		for( Achievement projects : projectsMap){
			
			for(Achievement achievement : achievements){
				
				String titleLabel = projects.getTitle().getLabel();
				
				if(achievement.getTitle().getLabel().equals(titleLabel) &&
					achievement.getDescription().equals(projects.getDescription())
				){
					found = true;
					break;
				}
			}
			
		    assertTrue(found);
		    found = false;
		}
	}		
	@Test
	public void volunteerTest(){

		VolunteerHandler handler = injector.getInstance(VolunteerHandler.class);
		
		List<WorkExperience> volunteerMap = (List<WorkExperience>)handler.transform(extraDataObj, new ArrayList<Achievement>());		

		List<WorkExperience> workExperienceList = esp.getLearnerInfo().getWorkExperienceList();
		
		assertTrue(volunteerMap.size() > 0);
		assertTrue(workExperienceList.size() > volunteerMap.size());
		
		boolean found = false;
		for( WorkExperience volunteer : volunteerMap){
			
			for(WorkExperience workExp : workExperienceList){
				
				String volunteerOrganization = volunteer.getEmployer().getName();
				
				if(workExp.getPosition().getLabel().equals(volunteer.getPosition().getLabel()) &&
					workExp.getEmployer().getName().equals(volunteerOrganization)
				){
					found = true;
					break;
				}
			}
			
		    assertTrue(found);
		    found = false;
		}		
	}	

	static void dateAssertions(String description, JDate actual, LinkedInDate comparison ){
		
		if ( actual.getDay() != null ){
			assertThat("ESP "+description+" - Day", 
					actual.getDay(), 
						CoreMatchers.is(comparison.getDay()));
		}
		if ( actual.getMonth() != null ){
		assertThat("ESP "+description+" - Month", 
				actual.getMonth(), 
					CoreMatchers.is(comparison.getMonth()));
		}
		if ( actual.getYear() != null ){
		assertThat("ESP "+description+" - Year", 
				actual.getYear(), 
					CoreMatchers.is(comparison.getYear()));
		}
	}
}