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

import java.util.ArrayList;
import java.util.List;

import org.springframework.social.linkedin.api.impl.json.LinkedInModule;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;


public class MockLinkedInProfile {
	
	private static ObjectMapper objectMapper = null;
	public static final String COOKIE_ID = "1111b111-111f-1111-1111-11aa1111111b";
	
	public static ObjectMapper linkedInMapper(){
		if ( objectMapper == null ){
			objectMapper = new ObjectMapper();	
			SimpleModule module = new LinkedInModule();
			objectMapper.registerModule( module );
			objectMapper.configure(SerializationFeature.WRITE_ENUMS_USING_TO_STRING, true);
			objectMapper.configure(Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
			objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); 
		}
		return objectMapper;
	}
	/**
	 * Mock miscellaneous Test object
	 * 
	 * @return
	 */
	public static ProfileFull linkedIn(){
		ProfileFull profile = new ProfileFull();
		profile.setFirstName( MockObjects.NAME );
		profile.setLastName( MockObjects.SURNAME );
		
		List<IMAccount> ims = new ArrayList<>();
		ims.add( new IMAccount( MockObjects.IM_GTALK, MockObjects.IM_0_NAME ) );
		ims.add( new IMAccount( MockObjects.IM_MSN, MockObjects.IM_1_NAME ) );
		profile.setImAccounts( ims );
		
		profile.setSummary( MockObjects.SUMMARY );
		
		
		EducationInfo edu1 = new EducationInfo();
		edu1.setDegree( MockObjects.EDU_0_DEGREE );
		edu1.setStartDate( new CustomDate( 2000, 1, 15 ) );
		edu1.setEndDate( new CustomDate( 2005, 12, 15 ) );
		EducationInfo edu2 = new EducationInfo();
		edu2.setDegree( MockObjects.EDU_1_DEGREE );
		edu2.setStartDate( new CustomDate( 2005, 1, 15 ) );
		edu2.setEndDate( new CustomDate( 2010, 12, 15 ) );
		
		List<EducationInfo> educations = new ArrayList<>();
		educations.add( edu1 );
		educations.add( edu2 );
		profile.setEducations( educations );
		
		LanguageInfo lang1 = new LanguageInfo();
		lang1.setName("Greek");
		lang1.setProficiencyLevel("native");
		LanguageInfo lang2 = new LanguageInfo();
		lang2.setName("English");
		lang2.setProficiencyLevel("good");
		LanguageInfo lang3 = new LanguageInfo();
		lang3.setName("German");
		lang3.setProficiencyLevel("very-good");
		List<LanguageInfo> languageInfos = new ArrayList<>();
		languageInfos.add(lang1);
		languageInfos.add(lang2);
		languageInfos.add(lang3);
		profile.setLanguages(languageInfos);
		
		List<String> skills = new ArrayList<>();
		skills.add( "JAVA" );
		skills.add( "PHP" );
		skills.add( "XML" );
		profile.setSkills( skills );
		
		List<Course> courses = new ArrayList<>();
		courses.add( new Course("JAVA for dummies") );
		courses.add( new Course("PHP for dummies") );
		courses.add( new Course("XML for dummies") );
		profile.setCourses( courses );
		
		return profile;
	}
	public static ExternalProfileRoot externalProfile(){	
		ExternalProfileRoot root = new ExternalProfileRoot();
		root.setUserProfileFull( linkedIn() );
		return root;
	}
	public static ProfileFull emptyLinkedIn(){
		return new ProfileFull() ;
	}
	public static  ExternalProfileRoot emptyExternalLinkedIn(){
		ExternalProfileRoot root = new ExternalProfileRoot();
		root.setUserProfileFull( new ProfileFull() );
		return root;
	}

	static class ExternalProfileRoot{
		ProfileFull userProfileFull;

		public ProfileFull getUserProfileFull() {
			return userProfileFull;
		}

		public void setUserProfileFull( ProfileFull userProfileFull ) {
			this.userProfileFull = userProfileFull;
		}
		
	}
	
	static class ProfileFull{
		String firstName;
		
		String lastName;
		
		List<IMAccount> imAccounts;
		
		String summary;
		
		List<EducationInfo> educations;
		
		List<LanguageInfo> languages;
		
		List<String> skills;
		
		List<Course> courses;
		
		public String getFirstName() {
			return firstName;
		}

		public void setFirstName( String firstName ) {
			this.firstName = firstName;
		}

		public String getLastName() {
			return lastName;
		}

		public void setLastName( String lastName ) {
			this.lastName = lastName;
		}

		public List<IMAccount> getImAccounts() {
			return imAccounts;
		}

		public void setImAccounts( List<IMAccount> imAccounts ) {
			this.imAccounts = imAccounts;
		}

		public String getSummary() {
			return summary;
		}

		public void setSummary( String summary ) {
			this.summary = summary;
		}

		public List<EducationInfo> getEducations() {
			return educations;
		}

		public void setEducations( List<EducationInfo> educations ) {
			this.educations = educations;
		}

		public List<LanguageInfo> getLanguages() {
			return languages;
		}

		public void setLanguages(List<LanguageInfo> languages) {
			this.languages = languages;
		}

		public List<String> getSkills() {
			return skills;
		}

		public void setSkills( List<String> skills ) {
			this.skills = skills;
		}

		public List<Course> getCourses() {
			return courses;
		}

		public void setCourses( List<Course> courses ) {
			this.courses = courses;
		}
		
	}
	
	static class LanguageInfo {
		String name;
		
		String proficiencyLevel;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getProficiencyLevel() {
			return proficiencyLevel;
		}

		public void setProficiencyLevel(String proficiencyLevel) {
			this.proficiencyLevel = proficiencyLevel;
		}
	}
	static class IMAccount{
		
		String imAccountType;
		
		String imAccountName;
		
		public IMAccount( String imAccountType, String imAccountName ) {
			this.imAccountType = imAccountType;
			this.imAccountName = imAccountName;
		}

		public String getImAccountType() {
			return imAccountType;
		}

		public void setImAccountType( String imAccountType ) {
			this.imAccountType = imAccountType;
		}

		public String getImAccountName() {
			return imAccountName;
		}

		public void setImAccountName( String imAccountName ) {
			this.imAccountName = imAccountName;
		}
		
	}
	static class EducationInfo {
		
		private String degree;
		
		private CustomDate startDate;
		
		private CustomDate endDate;

		public String getDegree() {
			return degree;
		}

		public void setDegree( String degree ) {
			this.degree = degree;
		}

		public CustomDate getStartDate() {
			return startDate;
		}

		public void setStartDate( CustomDate startDate ) {
			this.startDate = startDate;
		}

		public CustomDate getEndDate() {
			return endDate;
		}

		public void setEndDate( CustomDate endDate ) {
			this.endDate = endDate;
		}
	}
	static class CustomSkill{
		private String name;
		
		public CustomSkill(){}
		
		public CustomSkill( String name ){
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName( String name ) {
			this.name = name;
		}
		
	}
	static class Course{
		private String name;
		
		public Course( String name ){
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName( String name ) {
			this.name = name;
		}
	}
	static class CustomDate{
		
		private int year;
		
		private int month;
		
		private int day;
		
		public CustomDate(){}
		
		public CustomDate(int year, int month, int day){
			this.year = year;
			this.month = month;
			this.day = day;
		}

		public int getYear() {
			return year;
		}

		public void setYear( int year ) {
			this.year = year;
		}

		public int getMonth() {
			return month;
		}

		public void setMonth( int month ) {
			this.month = month;
		}

		public int getDay() {
			return day;
		}

		public void setDay( int day ) {
			this.day = day;
		}
	}
}
