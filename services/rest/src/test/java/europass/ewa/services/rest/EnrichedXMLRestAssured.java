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

import org.apache.commons.io.FileUtils;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;

import europass.ewa.services.Paths;

public class EnrichedXMLRestAssured {
	
	private final String BASE = "/rest";
	
	private String cvPhotoAndAttachment;
	
	private String espPhotoAndAttachment;
	
	@Before
	public void prepare() throws IOException{
		
		final File cv = new File(  getClass().getResource("/cv-photo-attachment.json").getFile());
		cvPhotoAndAttachment = FileUtils.readFileToString(cv);
		final File esp = new File(  getClass().getResource("/esp-photo-attachment.json").getFile());
		espPhotoAndAttachment = FileUtils.readFileToString(esp);
	}
	
	@Test
	public void jsonToXmlCvOnly(){
		String xml =
				given()
					.request()
						.contentType( ContentType.JSON + Paths.UTF8_CHARSET )
						.body( cvPhotoAndAttachment )
					.expect()
						.statusCode(200)
						.contentType( ContentType.XML )
						.log().body()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_XML_CV_ONLY ).asString();
		assertNotNull( xml );
	
		String docType = XmlPath.from(xml).get("SkillsPassport.DocumentInfo.DocumentType");
		assertThat("DocumentType", docType.trim(), CoreMatchers.is("ECV"));
		
	}
	
	@Test
	public void jsonToXmlEspOnly(){

		String xml =
			given()
				.request()
					.contentType( ContentType.JSON + Paths.UTF8_CHARSET )
					.body( espPhotoAndAttachment )
				.expect()
					.statusCode(200)
					.contentType( ContentType.XML )
					.log().body()
				.when()
					.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_XML_ESP_ONLY ).asString();
			
			
		String docType = XmlPath.from(xml).get("SkillsPassport.DocumentInfo.DocumentType");
		assertThat("DocumentType", docType.trim(), CoreMatchers.is("ESP"));
		
	}
}
