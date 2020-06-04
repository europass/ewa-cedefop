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

import europass.ewa.model.social.Mapping;
import europass.ewa.model.social.MappingListRoot;

public class MockMappings {

//	public static MappingListRoot root(){
//		Mapping root = new Mapping();
//		root.setFrom( "/userProfileFull" );
//		root.setTo( "/learnerInfo" );
//		
//		MappingListRoot rootObj = new MappingListRoot();
//		rootObj.setMapping( root );
//		
//		return rootObj;
//	}
	// ----
	private static MappingListRoot mappingRoot( Mapping mapping ){
		MappingListRoot rootObj = new MappingListRoot();
		List<Mapping> nested = new ArrayList<>();
		nested.add( mapping );
		rootObj.setMappingList( nested );
		return rootObj;
	}
	public static MappingListRoot sampleRoot(){
		Mapping[] mappings = new Mapping[7];
		mappings[0] = firstNameMapping();
		mappings[1] = surnameMapping();
		mappings[2] = summaryMapping();
		mappings[3] = imTypeMapping();
		mappings[4] = educationMapping();
		mappings[5] = skillsMapping();
		mappings[6] = coursesMapping();
		
		return combinedMappingRoot( mappings );
	}
	
	public static MappingListRoot completeRoot(){
		Mapping[] mappings = new Mapping[8];
		mappings[0] = firstNameMapping();
		mappings[1] = surnameMapping();
		mappings[2] = summaryMapping();
		mappings[3] = imTypeMapping();
		mappings[4] = educationMapping();
		mappings[5] = skillsMapping();
		mappings[6] = coursesMapping();
		mappings[7] = contactMapping();
		
		return combinedMappingRoot( mappings );
	}
	// ----
	public static MappingListRoot combinedMappingRoot(Mapping... mappings){
		
		MappingListRoot rootObj = new MappingListRoot();
		List<Mapping> nested = new ArrayList<>();
		for ( Mapping mapping : mappings ){
			nested.add( mapping );
		}
		rootObj.setMappingList( nested );
		
		return rootObj;
	}
	// ----
	public static Mapping skillsMapping(){
		Mapping skills = new Mapping();
		skills.setFrom( "/skills" );
		skills.setTo( "/learnerInfo/skills/other/description" );
		skills.setThrough( "SkillHandler" );
		return skills;
	}
	public static MappingListRoot skillsMappingRoot(){
		return mappingRoot( skillsMapping() );
	}
	
	// ----
	public static Mapping coursesMapping(){
		Mapping skills = new Mapping();
		skills.setFrom( "/courses" );
		skills.setTo( "/learnerInfo/achievementList" );
		skills.setThrough( "CoursesHandler" );
		skills.setParams( "courses" );
		return skills;
	}
	public static MappingListRoot coursesMappingRoot(){
		return mappingRoot( coursesMapping() );
	}
	
	// ----
	public static Mapping firstNameMapping(){
		Mapping firstName = new Mapping();
		firstName.setFrom( "/firstName" );
		firstName.setTo( "/learnerInfo/identification/personName/firstName" );
		return firstName;
	}
	
	public static MappingListRoot firstNameMappingRoot(){
		return mappingRoot( firstNameMapping() );
	}
	// -----
	public static Mapping surnameMapping(){
		Mapping surname = new Mapping();
		surname.setFrom( "/lastName" );
		surname.setTo( "/learnerInfo/identification/personName/surname" );
		surname.setThrough( "AddPrefixHandler" );
		surname.setParams( "TEST" );
		return surname;
	}
	
	public static MappingListRoot surnameMappingWithThroughRoot(){
		return mappingRoot(  surnameMapping() );
	}
	// ----
	public static Mapping imTypeMapping(){
		Mapping im = new Mapping();
		im.setFrom( "/imAccounts" );
		im.setTo( "/learnerInfo/identification/contactInfo/instantMessagingList" );
		
		Mapping imType = new Mapping();
		imType.setFrom( "./imAccountType" );
		imType.setTo( "./use" );
		imType.setThrough( "InstantMessagingHandler" );
		
		Mapping imContact = new Mapping();
		imContact.setFrom( "./imAccountName" );
		imContact.setTo( "./contact" );
		
		List<Mapping> nestedIm = new ArrayList<>();
		nestedIm.add( imType );
		nestedIm.add( imContact );
		im.setMappingList( nestedIm );
		
		return im;
	}
	public static MappingListRoot imTypeMappingWithThroughRoot(){
		return mappingRoot(  imTypeMapping() );
	}
	// ----
	public static Mapping summaryMapping(){
		Mapping summary = new Mapping();
		summary.setFrom( "/summary" );
		summary.setTo( "/learnerInfo/achievementList" );
		
		Mapping desc = new Mapping();
		desc.setFrom( "." );
		desc.setTo( "./description" );
		List<Mapping> inner = new ArrayList<>();
		inner.add( desc );
		
		summary.setMappingList( inner );
		
		return summary;
	}
	public static MappingListRoot summaryMappingRoot(){
		return mappingRoot(  summaryMapping() );
	}
	// ----
	public static Mapping languagesMapping(){
		Mapping languages = new Mapping();
		languages.setFrom( "/languages" );
		languages.setTo( "/learnerInfo/skills/linguistic/foreignLanguagesList" );
		
		//When we want to test the existing Language Object, which is a List of String
//		Mapping name = new Mapping();
//		name.setFrom( "./name" );
//		name.setTo( "./description/label" );
//		Mapping level = new Mapping();
//		level.setFrom( "./proficiencyLevel" );
//		level.setTo( "./proficiencyLevel" );
//		level.setThrough("LanguageProficiencyLevelHandler");
//		
//		List<Mapping> inner = new ArrayList<>();
//		inner.add( name );
//		inner.add( level );
		
//		languages.setMappingList( inner );
		
		return languages;
	}
	public static MappingListRoot languagesMappingRoot(){
		return mappingRoot(  summaryMapping() );
	}
	// ---
	public static Mapping year(){
		Mapping year = new Mapping();
		year.setFrom( "./year" );
		year.setTo( "./year" );
		return year;
	}
	public static Mapping month(){
		Mapping month = new Mapping();
		month.setFrom( "./month" );
		month.setTo( "./month" );
		return month;
	}
	public static Mapping day(){
		Mapping day = new Mapping();
		day.setFrom( "./day" );
		day.setTo( "./day" );
		return day;
	}
	// ----
	public static Mapping educationMapping(){
		Mapping educations = new Mapping();
		educations.setFrom( "/educations" );
		educations.setTo( "/learnerInfo/educationList" );
		
		Mapping degree = new Mapping();
		degree.setFrom( "./degree" );
		degree.setTo( "./title" );
		
		
		Mapping from = new Mapping();
		from.setFrom( "./startDate" );
		from.setTo( "./period/from" );
		List<Mapping> innerFrom = new ArrayList<>();
		innerFrom.add( year() );
		innerFrom.add( month() );
		innerFrom.add( day() );
		from.setMappingList( innerFrom );
		
		Mapping to = new Mapping();
		to.setFrom( "./endDate" );
		to.setTo( "./period/to" );
		List<Mapping> innerTo = new ArrayList<>();
		innerTo.add( year() );
		innerTo.add( month() );
		innerTo.add( day() );
		to.setMappingList( innerTo );
		
		List<Mapping> inner = new ArrayList<>();
		inner.add( degree );
		inner.add( from );
		inner.add( to );
		
		educations.setMappingList( inner );
		
		return educations;
	}
	
	public static Mapping contactMapping(){
		Mapping email = new Mapping();
		email.setFrom( "/emailAddress" );
		email.setTo( "/learnerInfo/identification/contactInfo/contactMethod/contact" );
		
		return email;
	}
	
	public static MappingListRoot educationMappingRoot(){
		return mappingRoot(  educationMapping() );
	}
}
