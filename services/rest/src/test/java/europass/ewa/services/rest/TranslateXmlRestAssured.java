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
package europass.ewa.services.rest;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.path.xml.XmlPath;
import com.jayway.restassured.response.Header;

import europass.ewa.services.Paths;

public class TranslateXmlRestAssured  {
	private final String BASE = "/rest";
	
	private final String xml30 = 
"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
"<SkillsPassport xmlns=\"http://europass.cedefop.europa.eu/Europass\" "+
    "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
    "xsi:schemaLocation=\"http://europass.cedefop.europa.eu/Europass http://europass.cedefop.europa.eu/EuropassSchema_V3.0.xsd\" " +
    "locale=\"el\">"+
	"<DocumentInfo>"+
		"<XSDVersion>V3.0</XSDVersion>"+
	"</DocumentInfo>"+
    "<LearnerInfo>"+
		"<Identification>"+
			"<PersonName>"+
				"<FirstName>Μπάμπης</FirstName>"+
				"<Surname>Σουγιάς</Surname>"+
			"</PersonName>"+
		"</Identification>"+
		"<Headline>" +
			"<Type><Code>job_applied_for</Code><Label>Αιτούμενη θέση</Label></Type>" +
			"<Description><Label>Μαεστρος</Label></Description>" +
		"</Headline>"+
	"</LearnerInfo>"+
"</SkillsPassport>";
	
	private final String HEADLINE_EN = "JOB APPLIED FOR";
	private final String HEADLINE_EL = "ΑΙΤΟΥΜΕΝΗ ΘΕΣΗ ΕΡΓΑΣΙΑΣ";
	
	@Test
	public void translateToEn(){
		
		String json = 
		given()
		.request()
			.contentType( ContentType.XML + Paths.UTF8_CHARSET )
			.header( new Header( "Accept-Language" , "en" ) )
			.body( xml30 )
			.log().everything()
		.expect()
			.statusCode(200)
			.contentType( ContentType.JSON )
			.log().everything()
		.when()
			.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_JSON )
		.asString();
		
		String surname = JsonPath.from(json).get("SkillsPassport.LearnerInfo.Identification.PersonName.Surname");
		
		assertThat("Surname", surname, is("Σουγιάς"));
		
		String country = JsonPath.from(json).get("SkillsPassport.LearnerInfo.Headline.Type.Label");
		//translated!
		assertThat("Headline Type", country, is(HEADLINE_EN));
	}
	
	@Test
	public void invalidLang(){
		
		String json = 
		given()
		.request()
			.contentType( ContentType.XML + Paths.UTF8_CHARSET )
			.header( new Header( "Accept-Language" , "ff" ) )
			.body( xml30 )
			.log().everything()
		.expect()
			.statusCode(400)
			.contentType( ContentType.XML )
			.log().everything()
		.when()
			.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_JSON )
		.asString();

		String errorCode = XmlPath.from(json).get("Error.code");
		//error invalid locale
		assertThat("Error Code", errorCode.trim(), is("unsupported.locale"));
		
	}
	
	@Test
	public void multiLang(){
		
		String json = 
		given()
		.request()
			.contentType( ContentType.XML + Paths.UTF8_CHARSET )
			.header( new Header( "Accept-Language" , "en-gb, da-dk;q=0.8, en;q=0.7" ) )
			.body( xml30 )
			.log().everything()
		.expect()
			.statusCode(200)
			.contentType( ContentType.JSON )
			.log().everything()
		.when()
			.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_JSON )
		.asString();

		String country = JsonPath.from(json).get("SkillsPassport.LearnerInfo.Headline.Type.Label");
		//indeed translated!
		assertThat("Headline Type", country, is(HEADLINE_EN));
		
	}
	
	@Test
	public void noLang(){
		
		String json = 
		given()
		.request()
			.contentType( ContentType.XML + Paths.UTF8_CHARSET )
			.body( xml30 )
			.log().everything()
		.expect()
			.statusCode(200)
			.contentType( ContentType.JSON )
			.log().everything()
		.when()
			.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_JSON )
		.asString();

		String country = JsonPath.from(json).get("SkillsPassport.LearnerInfo.Headline.Type.Label");
		//no translation
		assertThat("Headline Type", country, is(HEADLINE_EL));
		
	}
	
	@Test
	public void sameLanguage(){
		
		String json = 
		given()
		.request()
			.contentType( ContentType.XML + Paths.UTF8_CHARSET )
			.header( new Header( "Accept-Language" , "el" ) )
			.body( xml30 )
			.log().everything()
		.expect()
			.statusCode(200)
			.contentType( ContentType.JSON )
			.log().everything()
		.when()
			.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_JSON )
		.asString();
		
		String surname = JsonPath.from(json).get("SkillsPassport.LearnerInfo.Identification.PersonName.Surname");
		
		assertThat("Surname", surname, is("Σουγιάς"));
		
		String country = JsonPath.from(json).get("SkillsPassport.LearnerInfo.Headline.Type.Label");
		//same language - no translation
		assertThat("Headline Type", country, is(HEADLINE_EL));
	}
	

}	

