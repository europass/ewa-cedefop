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

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.json.JsonPath;

import europass.ewa.services.Paths;

public class XmlInputRestAssured  {
	private final String BASE = "/rest";
	
	private final String xml30 = 
"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
"<SkillsPassport xmlns=\"http://europass.cedefop.europa.eu/Europass\" "+
	"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
	"xsi:schemaLocation=\"http://europass.cedefop.europa.eu/Europass http://europass.cedefop.europa.eu/EuropassSchema_V3.0.xsd\" locale=\"en\">"+
	"<DocumentInfo>"+
		"<XSDVersion>V3.0</XSDVersion>"+
	"</DocumentInfo>"+
	"<LearnerInfo>"+
		"<Identification>"+
			"<PersonName>"+
				"<FirstName>Μπάμπης</FirstName>"+
				"<Surname>Σουγιάς</Surname>"+
			"</PersonName>"+
			"<Photo>"+
				"<MimeType>image/jpeg</MimeType>"+
				"<Data>PHOTODATA</Data>"+
			"</Photo>"+
		"</Identification>"+
	"</LearnerInfo>"+
"</SkillsPassport>";

	
	private final String xmlWithCl =
"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
	"<SkillsPassport xmlns=\"http://europass.cedefop.europa.eu/Europass\" "+
		"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
		"xsi:schemaLocation=\"http://europass.cedefop.europa.eu/Europass http://europass.cedefop.europa.eu/EuropassSchema_V3.0.xsd\" locale=\"en\">"+
		"<DocumentInfo>" +
			"<DocumentType>ECV</DocumentType>" +
			"<Bundle>" +
				"<Document>ECL</Document>" +
			"</Bundle>" +
		"</DocumentInfo>"+
		"<LearnerInfo>"+
			"<Identification>"+
				"<PersonName>"+
					"<FirstName>Μπάμπης</FirstName>"+
					"<Surname>Σουγιάς</Surname>"+
				"</PersonName>"+
			"</Identification>"+
		"</LearnerInfo>"+
		"<CoverLetter>" +
			"<Addressee>" +
				"<PersonName>" +
					"<Title><Code>dr</Code><Label>Δρ.</Label></Title>" +
					"<FirstName>Ιωάννης</FirstName>" +
					"<Surname>Ντάκος</Surname>" +
				"</PersonName>" +
				"<Position><Code>12332</Code><Label>Human resource manager</Label></Position>" +
			"</Addressee>" +
			"<Letter>" +
				"<Localisation>" +
					"<Date year=\"2013\" month=\"--10\" day=\"---15\"/>" +
					"<Place><Municipality>Ηράκλειο Κρήτης</Municipality></Place>" +
				"</Localisation>" +
				"<SubjectLine>Ref. IT support officer/2013/01/AD</SubjectLine>" +
			"</Letter>" +
		"</CoverLetter>"+
	"</SkillsPassport>";
	
	private String jsonWithPhotoAndAttachment;
	
	@Before
	public void prepare() throws IOException{
		
		final File file2 = new File(  getClass().getResource("/cv-photo-attachment.json").getFile());
		jsonWithPhotoAndAttachment = FileUtils.readFileToString(file2);
	}
	
	@Test
	public void helloWorld(){
		expect()
			.statusCode(200).contentType(ContentType.TEXT).body(equalTo("Europass: This is an XML/JSON v3.0 based Conversion Service!")).when().get( BASE + Paths.CONVERSION_BASE);
	}
	
	@Test
	public void latestXmlLatestService(){
		
		String json = 
		given()
		.request()
			.contentType( ContentType.XML + Paths.UTF8_CHARSET )
			.body( xml30 )
		.expect()
			.statusCode(200)
			.contentType( ContentType.JSON )
			.log().everything()
		.when()
			.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_JSON )
		.asString();
		
		String surname = JsonPath.from(json).get("SkillsPassport.LearnerInfo.Identification.PersonName.Surname");
		
		Assert.assertThat("Surname", surname, is("Σουγιάς"));
		
		String photo = JsonPath.from(json).get("SkillsPassport.LearnerInfo.Identification.Photo.Data");
		
		Assert.assertNotNull("Photo", photo );
	}
	
	@Test
	public void xmlToCVOnlyService(){
		
		String json = 
		given()
		.request()
			.contentType( ContentType.JSON + Paths.UTF8_CHARSET )
			.body( jsonWithPhotoAndAttachment )
		.expect()
			.statusCode(200)
			.contentType( ContentType.JSON + ";charset=utf-8" )
			.log().everything()
		.when()
			.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_XML_CV_ONLY )
		.asString();
		
		String surname = JsonPath.from(json).get("SkillsPassport.LearnerInfo.Identification.PersonName.Surname");
		
		Assert.assertThat("Surname", surname, is("Γκοτζίλας"));
		
		String photo = JsonPath.from(json).get("SkillsPassport.LearnerInfo.Identification.Photo.Data");
		
		Assert.assertNotNull("Photo", photo );
	}
	
	@Test
	public void xmlToESPOnlyService(){
		
		String json = 
		given()
		.request()
			.contentType( ContentType.JSON + Paths.UTF8_CHARSET )
			.body( jsonWithPhotoAndAttachment )
		.expect()
			.statusCode(200)
			.contentType( ContentType.JSON + ";charset=utf-8" )
			.log().everything()
		.when()
			.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_XML_ESP_ONLY )
		.asString();
		
		String surname = JsonPath.from(json).get("SkillsPassport.LearnerInfo.Identification.PersonName.Surname");
		
		Assert.assertThat("Surname", surname, is("Γκοτζίλας"));
		
		String photo = JsonPath.from(json).get("SkillsPassport.LearnerInfo.Identification.Photo.Data");
		
		Assert.assertNotNull("Photo", photo );
	}
	//EWA-983
	@Test
	public void xmlWithClToJson(){
		
		String json = 
			given()
			.request()
				.contentType( ContentType.XML + Paths.UTF8_CHARSET )
				.body( xmlWithCl )
			.expect()
				.statusCode(200)
				.contentType( ContentType.JSON + ";charset=utf-8" )
				.log().everything()
			.when()
				.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_JSON )
			.asString();
		
		String docType = JsonPath.from(json).get("SkillsPassport.DocumentInfo.DocumentType");
		Assert.assertThat("DocType", docType, is("ECV"));
		
		List<String> bundle = JsonPath.from(json).get("SkillsPassport.DocumentInfo.Document");
		Assert.assertNotNull("Bundle", bundle );
		
		List<String> cvPrefs = JsonPath.from(json).get("SkillsPassport.PrintingPreferences.ECV");
		Assert.assertNotNull("CV Prefs", cvPrefs );

		List<String> clPrefs = JsonPath.from(json).get("SkillsPassport.PrintingPreferences.ECL");
		Assert.assertNotNull("CL Prefs", clPrefs );

		String surname = JsonPath.from(json).get("SkillsPassport.LearnerInfo.Identification.PersonName.Surname");
		Assert.assertThat("Surname", surname, is("Σουγιάς"));
		
		String addressee = JsonPath.from(json).get("SkillsPassport.CoverLetter.Addressee.PersonName.Surname");
		Assert.assertThat("Addressee Surname", addressee, is("Ντάκος"));
		
		String localisationPlace = JsonPath.from(json).get("SkillsPassport.CoverLetter.Letter.Localisation.Place.Municipality");
		Assert.assertThat("Addressee Surname", localisationPlace, is("Ηράκλειο Κρήτης"));
	}
	
	//EWA-983
	@Test
	public void xmlWithClToJsonCVOnly(){
		
		String json = 
			given()
			.request()
				.contentType( ContentType.XML + Paths.UTF8_CHARSET )
				.body( xmlWithCl )
			.expect()
				.statusCode(200)
				.contentType( ContentType.JSON + ";charset=utf-8" )
				.log().everything()
			.when()
				.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_JSON_CV_ONLY )
			.asString();
		
		String docType = JsonPath.from(json).get("SkillsPassport.DocumentInfo.DocumentType");
		Assert.assertThat("DocType", docType, is("ECV"));
		
		List<String> bundle = JsonPath.from(json).get("SkillsPassport.DocumentInfo.Document");
		Assert.assertNull("Bundle", bundle );
		
		List<String> cvPrefs = JsonPath.from(json).get("SkillsPassport.PrintingPreferences.ECV");
		Assert.assertNotNull("CV Prefs", cvPrefs );

		List<String> clPrefs = JsonPath.from(json).get("SkillsPassport.PrintingPreferences.ECL");
		Assert.assertNull("CL Prefs", clPrefs );

		String surname = JsonPath.from(json).get("SkillsPassport.LearnerInfo.Identification.PersonName.Surname");
		Assert.assertThat("Surname", surname, is("Σουγιάς"));
		
		String addressee = JsonPath.from(json).get("SkillsPassport.CoverLetter.Addressee.PersonName.Surname");
		Assert.assertThat("Addressee Surname", addressee, is("Ντάκος"));
		
		String localisationPlace = JsonPath.from(json).get("SkillsPassport.CoverLetter.Letter.Localisation.Place.Municipality");
		Assert.assertThat("Addressee Surname", localisationPlace, is("Ηράκλειο Κρήτης"));
	}

}
