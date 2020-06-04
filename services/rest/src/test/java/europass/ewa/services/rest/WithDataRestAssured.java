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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.json.JsonPath;

import europass.ewa.enums.ContentTypes;
import europass.ewa.services.Paths;
import europass.ewa.services.enums.XmlVersion;

public class WithDataRestAssured  {
	
	private final String BASE = "/rest";
	
	private String xmlWithPhoto;
	
	private String xmlWithPhotoAndAttachment;
	
	private String xmlV34;

	private String nil = null;
	
	@Before
	public void prepare() throws IOException{
		final File file = new File(  getClass().getResource("/cv-photo.xml").getFile());
		xmlWithPhoto = FileUtils.readFileToString(file);
		
		final File file2 = new File(  getClass().getResource("/cv_esp-photo-attachment.xml").getFile());
		xmlWithPhotoAndAttachment = FileUtils.readFileToString(file2);
		
		final File file3 = new File(  getClass().getResource("/Europass_CV_V3.4.0_Example.xml").getFile());
		xmlV34 = FileUtils.readFileToString(file3);
	}
	@Test
	public void xmlToJsonWithPhoto() {
		String json =
		given()
			.request()
				.contentType( ContentType.XML + Paths.UTF8_CHARSET )
				.body( xmlWithPhoto )
				.log().everything()
			.expect()
				.statusCode(200)
				.contentType( ContentType.JSON )
				.log().everything()
			.when()
				.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_JSON ).asString();
		assertNotNull( json );
		
		String name = JsonPath.from(json).get("SkillsPassport.LearnerInfo.Identification.PersonName.FirstName");
		assertThat("Name", name, is("Λουκία") );
		
		String photo = JsonPath.from(json).get("SkillsPassport.LearnerInfo.Identification.Photo.Data");
		assertNotNull("Photo", photo );
		
		String version = JsonPath.from(json).get("SkillsPassport.DocumentInfo.XSDVersion");
		assertThat("Version", version.trim(), is(XmlVersion.LATEST.getCode()));
		
		String generator = JsonPath.from(json).get("SkillsPassport.DocumentInfo.Generator");
		assertThat("Generator", generator.trim(), is("REST_WS"));
		
		String docType = JsonPath.from(json).get("SkillsPassport.DocumentInfo.DocumentType");
		assertThat("DocumentType", docType.trim(), is("ECV"));
	}

	/**
	 * Cannot correctly inline byte data to JSON
	 */
	@Test
	public void xmlToJsonWithAttachment() {
		
		String json =
				given()
					.request()
						.contentType( ContentType.XML + Paths.UTF8_CHARSET )
						.body( xmlWithPhotoAndAttachment )
					.expect()
						.statusCode(200)
						.contentType( ContentType.JSON )
						.log().body()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_JSON ).asString();
		assertNotNull( json );
		
		String name = JsonPath.from(json).get("SkillsPassport.LearnerInfo.Identification.PersonName.FirstName");
		assertThat("Name", name, is("Τάκης") );
		
		String photo = JsonPath.from(json).get("SkillsPassport.LearnerInfo.Identification.Photo.Data");
		assertNotNull("Photo", photo );
		
		String attachment = JsonPath.from(json).get("SkillsPassport.Attachment[0].Name");
		assertThat( attachment, is("Download.png") );
		
		String linkedAttachment = JsonPath.from(json).get("SkillsPassport.LearnerInfo.Achievement[0].ReferenceTo[0].idref");
		assertThat( linkedAttachment, is("ATT_1272263651542") );
		
		String annexAttachment = JsonPath.from(json).get("SkillsPassport.LearnerInfo.ReferenceTo[0].idref");
		assertThat( annexAttachment, is("ATT_1272263651542") );
		
		String docType = JsonPath.from(json).get("SkillsPassport.DocumentInfo.DocumentType");
		assertThat("DocumentType", docType.trim(), is("ECV_ESP"));
	}
	
	@Test
	public void test33() {
		String json =
		given()
			.request()
				.contentType( ContentType.XML + Paths.UTF8_CHARSET )
				.body( xmlV34 )
				.log().everything()
			.expect()
				.statusCode(200)
				.contentType( ContentType.JSON )
				.log().everything()
			.when()
				.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_JSON ).asString();
		assertNotNull( json );
	
		boolean logo = JsonPath.from(json).get("SkillsPassport.DocumentInfo.EuropassLogo");
		assertThat("Logo", logo, is(true) );

		String bundle = JsonPath.from(json).get("SkillsPassport.DocumentInfo.Bundle");
		assertThat("bundle", bundle, is(nil) );

		String name = JsonPath.from(json).get("SkillsPassport.LearnerInfo.Identification.PersonName.FirstName");
		assertThat("Name", name, is("Chuck") );
		
		String version = JsonPath.from(json).get("SkillsPassport.DocumentInfo.XSDVersion");
		assertThat("Version", version.trim(), is(XmlVersion.LATEST.getCode()));
		
		String generator = JsonPath.from(json).get("SkillsPassport.DocumentInfo.Generator");
		assertThat("Generator", generator.trim(), is("REST_WS"));
		
		String docType = JsonPath.from(json).get("SkillsPassport.DocumentInfo.DocumentType");
		assertThat("DocumentType", docType.trim(), is("ECV_ESP"));
		
		//COMPUTER SKILLS ASSERTIONS
		String information = JsonPath.from(json).get("SkillsPassport.LearnerInfo.Skills.Computer.ProficiencyLevel.Information");
		assertThat("information", information, is("A") );
		
		String communication = JsonPath.from(json).get("SkillsPassport.LearnerInfo.Skills.Computer.ProficiencyLevel.Communication");
		assertThat("communication", communication, is("B") );

		String contentCreation = JsonPath.from(json).get("SkillsPassport.LearnerInfo.Skills.Computer.ProficiencyLevel.ContentCreation");
		assertThat("contentCreation", contentCreation, is("C") );

		String safety = JsonPath.from(json).get("SkillsPassport.LearnerInfo.Skills.Computer.ProficiencyLevel.Safety");
		assertThat("safety", safety, is("C") );

		String problemSolving = JsonPath.from(json).get("SkillsPassport.LearnerInfo.Skills.Computer.ProficiencyLevel.ProblemSolving");
		assertThat("problemSolving", problemSolving, is("C") );

		List<String> title = JsonPath.from(json).get("SkillsPassport.LearnerInfo.Skills.Computer.Certificate.Title");
		assertThat("title", title.toString(), is("[ACDL Certificate]") );
	}
	
	//@Ignore
	@Test
	public void xmlToOdt(){
		given()
			.request()
				.contentType( ContentType.XML + Paths.UTF8_CHARSET )
				.body( xmlWithPhoto )
			.expect()
				.statusCode( 200)
				.contentType( ContentTypes.OPEN_DOC_CT )
			.when()
				.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_OPEN_DOCUMENT );
		
	}
	

}
