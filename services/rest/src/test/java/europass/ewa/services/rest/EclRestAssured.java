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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.path.xml.XmlPath;

import europass.ewa.enums.ContentTypes;
import europass.ewa.services.Paths;

public class EclRestAssured {
	
	private final String BASE = "/rest";
	
	private String xmlWithAttachment;
	
	private String jsonWithAttachment;
	
	@Before
	public void prepare() throws IOException{
		
		final File file1 = new File(  getClass().getResource("/ecl-example.xml").getFile());
		xmlWithAttachment = FileUtils.readFileToString(file1);
		
		final File file2 = new File(  getClass().getResource("/ecl-example.json").getFile());
		jsonWithAttachment = FileUtils.readFileToString(file2);
	}
	
	@Test
	public void xmlToJson(){
		String json =
				given()
					.request()
						.contentType( ContentType.XML + Paths.UTF8_CHARSET )
						.body( xmlWithAttachment )
					.expect()
						.statusCode(200)
						.contentType( MediaType.APPLICATION_JSON )
						.log().body()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_JSON ).asString();
		
		assertNotNull( json );
			
		String addressee = JsonPath.from(json).get("SkillsPassport.CoverLetter.Addressee.PersonName.Surname").toString().trim();
		
		assertThat("Addresssee surname", addressee, CoreMatchers.is("Stuart") );
		
		String docType = JsonPath.from(json).get("SkillsPassport.DocumentInfo.DocumentType");
		assertThat("DocumentType", docType.trim(), CoreMatchers.is("ECL"));
		
	}
	
	
	@Test
	public void jsonToXmlEcl(){
		String xml =
				given()
					.request()
						.contentType( MediaType.APPLICATION_JSON + Paths.UTF8_CHARSET )
						.body( jsonWithAttachment )
					.expect()
						.statusCode(200)
						.contentType( ContentType.XML + Paths.UTF8_CHARSET )
						.log().body()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_XML ).asString();
		
		String addresseeFN = XmlPath.from(xml).get("SkillsPassport.CoverLetter.Addressee.PersonName.FirstName").toString().trim();
		String addresseeSN = XmlPath.from(xml).get("SkillsPassport.CoverLetter.Addressee.PersonName.Surname").toString().trim();
		
		assertThat("Addresssee firstname", addresseeFN, CoreMatchers.is("John") );
		assertThat("Addresssee surname", addresseeSN, CoreMatchers.is("Stuart") );
		
		String docType = XmlPath.from(xml).get("SkillsPassport.DocumentInfo.DocumentType");
		assertThat("DocumentType", docType.trim(), CoreMatchers.is("ECL"));
		
		assertNotNull( xml );
			
	}
	
	@Test
	public void jsonToODT(){
		String odt =
				given()
					.request()
						.contentType( MediaType.APPLICATION_JSON + Paths.UTF8_CHARSET )
						.body( jsonWithAttachment )
//									.contentType("application/x-www-form-urlencoded"+ Paths.UTF8_CHARSET)
//				.formParam("json", jsonWithAttachment)
//				.formParam("user-agent", UserAgent.FF19.getDescription())
					.expect()
						.statusCode(200)
						.contentType( ContentTypes.OPEN_DOC_CT + Paths.UTF8_CHARSET )
						.log().body()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_OPEN_DOCUMENT ).asString();
		
		assertNotNull( odt );
		
	}
	
	
	@Test
	public void jsonToWord(){
		String word =
				given()
					.request()
						.contentType( MediaType.APPLICATION_JSON )
						.body( jsonWithAttachment )
					.expect()
						.statusCode(200)
						.contentType( ContentTypes.WORD_DOC_CT + Paths.UTF8_CHARSET )
						.log().body()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_WORD ).asString();
		
		assertNotNull( word );
		
	}
}
