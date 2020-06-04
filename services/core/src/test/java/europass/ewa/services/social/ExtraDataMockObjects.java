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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@SuppressWarnings("unchecked")
public class ExtraDataMockObjects {

	public static final String PICTURE_URL = "http://assets.coolhunting.com/coolhunting/mt_asset_cache/2012/11/Paris_Photo_2-thumb-620x413-50826.jpg";

	public static final String CERTIFICATIONS_HTML_EXPECTED = 
			"<p><strong>Network Administrator</strong></p><p><strong>Java SE Expert</strong></p><p><strong>Linux Advanced User</strong></p>";

	public static final String COURSES_HTML_EXPECTED = 
			"<p><strong>Course 1</strong> | <em>325</em></p>"
			+ "<p><strong>Course 2</strong> | <em>255</em></p>"
			+ "<p><strong>Course 3</strong> | <em>472</em></p>";

	public static final String HONORS_AWARDS_HTML_EXPECTED = 
			"<p><strong>Annual QA Exhausting Testing - 3rd position</strong> | <em>Quality Testing School of QA</em> </p>"
			+ "<p><strong>Burgers &amp; Beer Festival&#39;s Contest - Finalist&#39;s Award</strong> </p>"
			+ "<p><em>MTV Awards</em></p>";
	
	// EXTRA ATTRIBUTE FOR A HREF ELEMENTS: rel="nofollow" (see http://www.robotstxt.org/faq/relnofollow.html)
	public static final String PATENTS_HTML_EXPECTED = 
			"<p><strong><a href=\"https://patent#10.us.patents.org\" target=\"_blank\" rel=\"nofollow\">Patent #10</a></strong></p>"
			+ "<p>Patent #10 description here</p>10101010 | <em>Patent </em> | <em>10/10/10</em> | <em> us </em>"
			+ "<p><strong><a href=\"https://patent#11.ca.patents.org\" target=\"_blank\" rel=\"nofollow\">Patent #11</a></strong></p>"
			+ "<p>Patent #11 description here</p>11111111 | <em>Patent </em> | <em>11/11/11</em> | <em> ca </em>";	

	// EXTRA ATTRIBUTE FOR A HREF ELEMENTS: rel="nofollow" (see http://www.robotstxt.org/faq/relnofollow.html)
	public static final String PUBLICATIONS_HTML_EXPECTED = 
			"<p><strong><a href=\"https://publication1.paperpublushingfaculty.org\" target=\"_blank\" rel=\"nofollow\">Publication 1</a></strong> | <em> 10/10/10 </em> </p>"
			+ "<p><em> Paper Publishng Faculty </em></p>"
			+ "<p><strong><a href=\"https://publication2.alumnitechnicalpublisher.org\" target=\"_blank\" rel=\"nofollow\">Publication 2</a></strong> | <em> 11/11/11 </em> </p>"
			+ "<p><em> Alumni Technical Publisher </em></p>";
	
	// EXTRA ATTRIBUTE FOR A HREF ELEMENTS: rel="nofollow" (see http://www.robotstxt.org/faq/relnofollow.html)
	public static final String PROJECTS_HTML_EXPECTED = 
			"<p><strong>Testing Project 1</strong><br />Testing Project 1 Description here<br />"
			+ "<a href=\"https://project1.testing.org\" target=\"_blank\" rel=\"nofollow\">https://project1.testing.org</a></p>"
			+ "<p><strong>Testing Project 2</strong>"
			+ "<a href=\"https://project2.testing.org\" target=\"_blank\" rel=\"nofollow\">https://project2.testing.org</a></p>";
	
	/**
	 * 	Extra Data HashMap with certifications
	 */
	public static <A extends Object, B extends Object> HashMap<String,?> certificationsHashMapObject(){

		// Certification 1
		HashMap<String,A> value1 = new LinkedHashMap<String,A>();
		value1.put("id",(A)Integer.valueOf(111));
		value1.put("name",(A)"Network Administrator");

		// Certification 2
		HashMap<String,A> value2 = new LinkedHashMap<String,A>();
		value2.put("id",(A)Integer.valueOf(222));
		value2.put("name",(A)"Java SE Expert");

		// Certification 3
		HashMap<String,A> value3 = new LinkedHashMap<String,A>();
		value3.put("id",(A)Integer.valueOf(333));
		value3.put("name",(A)"Linux Advanced User");
		
		// values
		List<HashMap<String,A>> values = new ArrayList<HashMap<String,A>>();
		values.add(value1);
		values.add(value2);
		values.add(value3);
		
		// certifications
		HashMap<String,B> certifications = new LinkedHashMap<String,B>();
		certifications.put("_total",(B)Integer.valueOf(3));
		certifications.put("values", (B)values);
		
		// extraData
		HashMap<String,HashMap<String,B>> extraData = new LinkedHashMap<String,HashMap<String,B>>();
		extraData.put("certifications",certifications);
		
		return extraData;
	}
	
	/**
	 * 	Extra Data HashMap with courses
	 */
	public static <A extends Object, B extends Object> HashMap<String,?> coursesHashMapObject(){
		
		// Course 1
		HashMap<String,A> value1 = new LinkedHashMap<String,A>();
		value1.put("id",(A)Integer.valueOf(111));
		value1.put("name",(A)"Course 1");
		value1.put("number",(A)"325");

		// Course 2
		HashMap<String,A> value2 = new LinkedHashMap<String,A>();
		value2.put("id",(A)Integer.valueOf(222));
		value2.put("name",(A)"Course 2");
		value2.put("number",(A)"255");

		// Course 3
		HashMap<String,A> value3 = new LinkedHashMap<String,A>();
		value3.put("id",(A)Integer.valueOf(333));
		value3.put("name",(A)"Course 3");
		value3.put("number",(A)"472");
		
		// values
		List<HashMap<String,A>> values = new ArrayList<HashMap<String,A>>();
		values.add(value1);
		values.add(value2);
		values.add(value3);
		
		// courses
		HashMap<String,B> courses = new LinkedHashMap<String,B>();
		courses.put("_total",(B)Integer.valueOf(3));
		courses.put("values", (B)values);
		
		// extraData
		HashMap<String,HashMap<String,B>> extraData = new LinkedHashMap<String,HashMap<String,B>>();
		extraData.put("courses",courses);
		
		return extraData;
	}
	
	/**
	 * 	Extra Data HashMap with honors and awards
	 */
	public static <A extends Object, B extends Object> HashMap<String,?> honorsAwardsHashMapObject(){

		// Honor 1
		HashMap<String,A> value1 = new LinkedHashMap<String,A>();
		value1.put("id",(A)Integer.valueOf(111));
		value1.put("issuer",(A)"Quality Testing School of QA");
		value1.put("name",(A)"Annual QA Exhausting Testing - 3rd position");

		// Honor 2
		HashMap<String,A> value2 = new LinkedHashMap<String,A>();
		value2.put("id",(A)Integer.valueOf(222));
		value2.put("name",(A)"Burgers & Beer Festival's Contest - Finalist's Award");

		// Honor 3
		HashMap<String,A> value3 = new LinkedHashMap<String,A>();
		value3.put("id",(A)Integer.valueOf(333));
		value3.put("issuer",(A)"MTV Awards");
		
		// values
		List<HashMap<String,A>> values = new ArrayList<HashMap<String,A>>();
		values.add(value1);
		values.add(value2);
		values.add(value3);
		
		// honorsAwards
		HashMap<String,B> honorsAwards = new LinkedHashMap<String,B>();
		honorsAwards.put("_total",(B)Integer.valueOf(3));
		honorsAwards.put("values", (B)values);
		
		// extraData
		HashMap<String,HashMap<String,B>> extraData = new LinkedHashMap<String,HashMap<String,B>>();
		extraData.put("honorsAwards",honorsAwards);
		
		return extraData;
	}	
	/**
	 * 	Extra Data HashMap with languages
	 */
	public static <A extends Object, B extends Object> HashMap<String,?> languagesHashMapObject(){
		
		// Language 1
		HashMap<String,A> value1 = new LinkedHashMap<String,A>();
		value1.put("id",(A)Integer.valueOf(111));

		HashMap<String,String> languageMap1 = new LinkedHashMap<String,String>();
		languageMap1.put("name","Greek");
		value1.put("language",(A)languageMap1);
		
		HashMap<String,String> proficiencyMap1 = new LinkedHashMap<String,String>();
		proficiencyMap1.put("level","native_or_bilingual");
		proficiencyMap1.put("name","Native or bilingual proficiency");
		value1.put("proficiency",(A)proficiencyMap1);
		
		// Language 2
		HashMap<String,A> value2 = new LinkedHashMap<String,A>();
		value2.put("id",(A)Integer.valueOf(111));
		
		HashMap<String,String> languageMap2 = new LinkedHashMap<String,String>();
		languageMap2.put("name","English");
		value2.put("language",(A)languageMap2);
		
		HashMap<String,String> proficiencyMap2 = new LinkedHashMap<String,String>();
		proficiencyMap2.put("level","professional_working");
		proficiencyMap2.put("name","Professional working proficiency");
		value2.put("proficiency",(A)proficiencyMap2);

		// Language 3
		HashMap<String,A> value3 = new LinkedHashMap<String,A>();
		value3.put("id",(A)Integer.valueOf(111));
		
		HashMap<String,String> languageMap3 = new LinkedHashMap<String,String>();
		languageMap3.put("name","French");
		value3.put("language",(A)languageMap3);
		
		HashMap<String,String> proficiencyMap3 = new LinkedHashMap<String,String>();
		proficiencyMap3.put("level","limited_working");
		proficiencyMap3.put("name","Limited working proficiency");
		value3.put("proficiency",(A)proficiencyMap3);
		
		// values
		List<HashMap<String,A>> values = new ArrayList<HashMap<String,A>>();
		values.add(value1);
		values.add(value2);
		values.add(value3);
		
		// languages
		HashMap<String,B> languages = new LinkedHashMap<String,B>();
		languages.put("_total",(B)Integer.valueOf(3));
		languages.put("values", (B)values);
		
		// extraData
		HashMap<String,HashMap<String,B>> extraData = new LinkedHashMap<String,HashMap<String,B>>();
		extraData.put("languages",languages);
		
		return extraData;
	}	
	
	/**
	 * 	Extra Data HashMap with patents (inventors field is omitted as its data are incomplete) 
	 */
	public static <A extends Object, B extends Object, C extends Object> HashMap<String,?> patentsHashMapObject(){
		
		// Patent 1
		HashMap<String,A> value1 = new LinkedHashMap<String,A>();
		value1.put("id",(A)Integer.valueOf(111));
	
		// title, summary, url
		value1.put("title", (A)"Patent #10");
		value1.put("summary", (A)"Patent #10 description here");
		value1.put("url", (A)"https://patent#10.us.patents.org");
		
		// date
		HashMap<String,Integer> date1 = new LinkedHashMap<String,Integer>();
		date1.put("day",Integer.valueOf(10));
		date1.put("month",Integer.valueOf(10));
		date1.put("year",Integer.valueOf(2010));
		value1.put("date", (A)date1);
		
		// number
		value1.put("number", (A)"10101010");
	
		// office
		HashMap<String,String> office1 = new LinkedHashMap<String,String>();
		office1.put("name","us");
		value1.put("office", (A)office1);
	
		// status
		HashMap<String,B> status1 = new LinkedHashMap<String,B>();
		status1.put("id",(B)Integer.valueOf(1));
		status1.put("name",(B)"Patent");
		value1.put("status", (A)status1);
		
		// Patent 2
		HashMap<String,A> value2 = new LinkedHashMap<String,A>();
		value2.put("id",(A)Integer.valueOf(222));
	
		// title, summary, url
		value2.put("title", (A)"Patent #11");
		value2.put("summary", (A)"Patent #11 description here");
		value2.put("url", (A)"https://patent#11.ca.patents.org");
		
		// date
		HashMap<String,Integer> date2 = new LinkedHashMap<String,Integer>();
		date2.put("day",Integer.valueOf(11));
		date2.put("month",Integer.valueOf(11));
		date2.put("year",Integer.valueOf(2011));
		value2.put("date", (A)date2);
		
		// number
		value2.put("number", (A)"11111111");
	
		// office
		HashMap<String,String> office2 = new LinkedHashMap<String,String>();
		office2.put("name","ca");
		value2.put("office", (A)office2);
	
		// status
		HashMap<String,B> status2 = new LinkedHashMap<String,B>();
		status2.put("id",(B)Integer.valueOf(1));
		status2.put("name",(B)"Patent");
		value2.put("status",(A)status2);
		
		// values
		List<HashMap<String,A>> values = new ArrayList<HashMap<String,A>>();
		values.add(value1);
		values.add(value2);
		
		// patents
		HashMap<String,C> patents = new LinkedHashMap<String,C>();
		patents.put("_total",(C)Integer.valueOf(2));
		patents.put("values", (C)values);
		
		// extraData
		HashMap<String,HashMap<String,C>> extraData = new LinkedHashMap<String,HashMap<String,C>>();
		extraData.put("patents",patents);
		
		return extraData;
	}

	/**
	 * 	Extra Data HashMap with picture
	 */	
	public static <A extends Object, B extends Object> HashMap<String,?> pictureHashMapObject(){

		// values
		List<String> values = new ArrayList<String>();
		values.add(PICTURE_URL);
		
		// picturesUrls
		HashMap<String,A> pictureUrls = new LinkedHashMap<String,A>();
		pictureUrls.put("_total",(A)Integer.valueOf(1));
		pictureUrls.put("values", (A)values);
		
		// extraData
		HashMap<String,B> extraData = new LinkedHashMap<String,B>();
		extraData.put("pictureUrls",(B)pictureUrls);
		
		return extraData;
	}

	/**
	 * 	Extra Data HashMap with projects
	 */
	public static <A extends Object, B extends Object, C extends Object> HashMap<String,?> projectsHashMapObject(){
		
		// Project 1
		HashMap<String,A> value1 = new LinkedHashMap<String,A>();
		value1.put("id",(A)Integer.valueOf(111));
	
		// name, description, url
		value1.put("name", (A)"Testing Project 1");
		value1.put("description", (A)"Testing Project 1 Description here");
		value1.put("url", (A)"https://project1.testing.org");
		
		// date
		HashMap<String,Integer> date1 = new LinkedHashMap<String,Integer>();
		date1.put("month",Integer.valueOf(10));
		date1.put("year",Integer.valueOf(2010));
		value1.put("date", (A)date1);
		
		// Project 2
		HashMap<String,A> value2 = new LinkedHashMap<String,A>();
		value2.put("id",(A)Integer.valueOf(222));
	
		// name, description, url
		value2.put("name", (A)"Testing Project 2");
		value2.put("url", (A)"https://project2.testing.org");
		
		// date
		HashMap<String,Integer> date2 = new LinkedHashMap<String,Integer>();
		date2.put("month",Integer.valueOf(5));
		date2.put("year",Integer.valueOf(2011));
		value2.put("date", (A)date2);
	
		// values
		List<HashMap<String,A>> values = new ArrayList<HashMap<String,A>>();
		values.add(value1);
		values.add(value2);
		
		// publications
		HashMap<String,C> projects = new LinkedHashMap<String,C>();
		projects.put("_total",(C)Integer.valueOf(2));
		projects.put("values", (C)values);
		
		// extraData
		HashMap<String,HashMap<String,C>> extraData = new LinkedHashMap<String,HashMap<String,C>>();
		extraData.put("projects",projects);
		
		return extraData;
	}	

	/**
	 * 	Extra Data HashMap with publication ( authors field is omitted as its data are incomplete )
	 */
	public static <A extends Object, B extends Object, C extends Object> HashMap<String,?> publicationsHashMapObject(){
		
		// Publication 1
		HashMap<String,A> value1 = new LinkedHashMap<String,A>();
		value1.put("id",(A)Integer.valueOf(111));
	
		// title, summary, url
		value1.put("title", (A)"Publication 1");
		value1.put("url", (A)"https://publication1.paperpublushingfaculty.org");
		
		// date
		HashMap<String,Integer> date1 = new LinkedHashMap<String,Integer>();
		date1.put("day",Integer.valueOf(10));
		date1.put("month",Integer.valueOf(10));
		date1.put("year",Integer.valueOf(2010));
		value1.put("date", (A)date1);
		
		// publisher
		HashMap<String,String> publisher1 = new LinkedHashMap<String,String>();
		publisher1.put("name","Paper Publishng Faculty");
		value1.put("publisher", (A)publisher1);
	
		// Publication 2
		HashMap<String,A> value2 = new LinkedHashMap<String,A>();
		value2.put("id",(A)Integer.valueOf(222));
	
		// title, summary, url
		value2.put("title", (A)"Publication 2");
		value2.put("url", (A)"https://publication2.alumnitechnicalpublisher.org");
		
		// date
		HashMap<String,Integer> date2 = new LinkedHashMap<String,Integer>();
		date2.put("day",Integer.valueOf(11));
		date2.put("month",Integer.valueOf(11));
		date2.put("year",Integer.valueOf(2011));
		value2.put("date", (A)date2);
		
		// publisher
		HashMap<String,String> publisher2 = new LinkedHashMap<String,String>();
		publisher2.put("name","Alumni Technical Publisher");
		value2.put("publisher", (A)publisher2);
	
		// values
		List<HashMap<String,A>> values = new ArrayList<HashMap<String,A>>();
		values.add(value1);
		values.add(value2);
		
		// publications
		HashMap<String,C> publications = new LinkedHashMap<String,C>();
		publications.put("_total",(C)Integer.valueOf(2));
		publications.put("values", (C)values);
		
		// extraData
		HashMap<String,HashMap<String,C>> extraData = new LinkedHashMap<String,HashMap<String,C>>();
		extraData.put("publications",publications);
		
		return extraData;
	}
	
	/**
	 * 	Extra Data HashMap with volunteer
	 */
	public static <A extends Object, B extends Object, C extends Object> HashMap<String,?> volunteerHashMapObject(){
		
		// Volunteer 1
		HashMap<String,A> value1 = new LinkedHashMap<String,A>();
		value1.put("id",(A)Integer.valueOf(111));
	
		HashMap<String,String> organizationMap1 = new LinkedHashMap<String,String>();
		organizationMap1.put("name","Green peace");
		value1.put("organization",(A)organizationMap1);
		
		value1.put("role",(A)"Enviromentalist specialized on sea wales protection");
	
		// Volunteer 2
		HashMap<String,A> value2 = new LinkedHashMap<String,A>();
		value2.put("id",(A)Integer.valueOf(222));
	
		HashMap<String,String> organizationMap2 = new LinkedHashMap<String,String>();
		organizationMap2.put("name","Medicenes sans Frontiers");
		value2.put("organization",(A)organizationMap2);
		
		value2.put("role",(A)"Doctor specialized in amputee care");
	
		// values
		List<HashMap<String,A>> values = new ArrayList<HashMap<String,A>>();
		values.add(value1);
		values.add(value2);
		
		// volunteerExperiences
		HashMap<String,B> volunteerExperiences = new LinkedHashMap<String,B>();
		volunteerExperiences.put("_total",(B)Integer.valueOf(2));
		volunteerExperiences.put("values", (B)values);
		
		// volunteer
		HashMap<String,HashMap<String,B>> volunteer = new LinkedHashMap<String,HashMap<String,B>>();
		volunteer.put("volunteerExperiences",volunteerExperiences);
		
		// extraData
		HashMap<String,C> extraData = new LinkedHashMap<String,C>();
		extraData.put("volunteer",(C)volunteer);
		
		return extraData;
	}	
}
