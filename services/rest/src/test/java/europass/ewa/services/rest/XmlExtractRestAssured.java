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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;

import europass.ewa.services.Paths;
import europass.ewa.services.enums.HttpErrorCodeLabels;

public class XmlExtractRestAssured {
	private final String BASE = "/rest";
	
	private File PDFEuropassCV;
	private File oldVersionPDFEuropassCV;
	private File XmlEuropassCV;
	private File PDFnoAttachment;
	
	private String notXmlErrorCode = "content.type.not.allowed";
	private String invalidMethodErrorCode ;
	private String notAcceptable ;
	private String notFound;
	private String unsupportedMediaType;


	
	@Before
	public void prepare() throws IOException{

		PDFEuropassCV = new File(  getClass().getResource("/Europass-CV-20130618-Emmanouilidi-EN.pdf").getFile());
		oldVersionPDFEuropassCV = new File(  getClass().getResource("/Europass-CV-130619-Emmanouilidi.pdf").getFile());
		XmlEuropassCV = new File(  getClass().getResource("/cv-photo.xml").getFile());
		PDFnoAttachment = new File(  getClass().getResource("/no-attachments.pdf").getFile());
		
		notFound = HttpErrorCodeLabels.match(404).getLabel();
		invalidMethodErrorCode = HttpErrorCodeLabels.match(405).getLabel();
		notAcceptable = HttpErrorCodeLabels.match(406).getLabel();
		unsupportedMediaType = HttpErrorCodeLabels.match(415).getLabel();
	}	
	
	@Test
	public void helloWorld(){
		expect()
			.statusCode(200)
			.contentType(ContentType.TEXT)
			.body(equalTo("Europass: This is a service for extracted europass cv!"))
			.when()
				.get( BASE + Paths.EXTRACT_XML_ATTCH_BASE);
	}
	
	
	@Test
	public void extractXmlAttch() throws IOException {
		String xml;
			xml = given()
				.request()
					.contentType("application/pdf"  )
					
					.body(FileUtils.readFileToByteArray(PDFEuropassCV))
				.expect()
					.statusCode(200)
					.contentType( ContentType.XML + ";charset=utf-8" )
					.log().body()
				.when()
					.post(BASE + Paths.EXTRACT_XML_ATTCH_BASE).asString();
			String firstName = XmlPath.from(xml).get("SkillsPassport.LearnerInfo.Identification.PersonName.FirstName");
			String surName = XmlPath.from(xml).get("SkillsPassport.LearnerInfo.Identification.PersonName.Surname");
			assertThat(firstName.trim(), is("Sofia") );	
			assertThat(surName.trim(), is("Emmanouilidi") );	
			assertNotNull( xml );	
	}
	
	@Test
	public void extractOldVersionXmlAttch() throws IOException {
		String xml;
			xml = given()
				.request()
					.contentType("application/pdf"  )
					
					.body(FileUtils.readFileToByteArray(oldVersionPDFEuropassCV))
				.expect()
					.statusCode(200)
					.contentType( ContentType.XML + ";charset=utf-8" )
					.log().body()
				.when()
					.post(BASE + Paths.EXTRACT_XML_ATTCH_BASE).asString();
			String firstName = XmlPath.from(xml).get("learnerinfo.identification.firstname");
			String surName = XmlPath.from(xml).get("learnerinfo.identification.lastname");
			assertThat(firstName.trim(), is("Sofia") );	
			assertThat(surName.trim(), is("Emmanouilidi") );	
			assertNotNull( xml );	
	}
	
	@Test
	public void extractFromUnsupportedType() throws IOException {
		String xml;
			xml = given()
				.request()
					.contentType("application/pdf"  )
					
					.body(FileUtils.readFileToByteArray(XmlEuropassCV))
				.expect()
					.statusCode(415)
					.contentType( ContentType.XML)
					.log().everything()
				.when()
					.post(BASE + Paths.EXTRACT_XML_ATTCH_BASE).asString();
			assertNotNull( xml );	
			String errorCode = XmlPath.from(xml).get("Error.code");
			assertThat(errorCode.trim(), is(notXmlErrorCode) );	
	
	}
	
	@Test
	public void wrongContentType() throws IOException {
		String xml;
			xml = given()
				.request()
					.contentType("application/xml"  )
					
					.body(FileUtils.readFileToByteArray(XmlEuropassCV))
				.expect()
					.statusCode(415)
					.contentType( ContentType.XML  )
					.log().everything()
				.when()
					.post(BASE + Paths.EXTRACT_XML_ATTCH_BASE).asString();
			assertNotNull( xml );	
			String errorCode = XmlPath.from(xml).get("Error.code");
			assertThat(errorCode.trim(), is(unsupportedMediaType) );	
	
	}
	
	@Test
	public void extractFromPdfWithNoAttachments() throws IOException {
		String xml;
			xml = given()
				.request()
					.contentType("application/pdf"  )
					
					.body(FileUtils.readFileToByteArray(PDFnoAttachment))
				.expect()
					.statusCode(400)
					.contentType( ContentType.XML  )
					.log().body()
				.when()
					.post(BASE + Paths.EXTRACT_XML_ATTCH_BASE).asString();
			assertNotNull( xml );		
			
	}
	
	
	@Test
	public void extractXmlAttchInvalidMethod() throws IOException {
		String xml;
			xml = given()
				.request()
					.contentType("application/pdf"  )
					
					.body(FileUtils.readFileToByteArray(PDFEuropassCV))
				.expect()
					.statusCode(405)
					.contentType( ContentType.XML /*+ ";charset=utf-8"*/ )
					.log().everything()
				.when()
					.delete(BASE + Paths.EXTRACT_XML_ATTCH_BASE).asString();	
			assertNotNull( xml );	
			
			String errorCode = XmlPath.from(xml).get("Error.code");
			assertThat(errorCode.trim(), is(invalidMethodErrorCode) );	
	
	}
	
	@Test
	public void notAcceptableHeader() throws IOException {
		String xml;
			xml = given()
				.request()
					.contentType("application/pdf"  )
					.header("Accept","sss")
					.body(FileUtils.readFileToByteArray(PDFEuropassCV))
				.expect()
					.statusCode(406)
					.contentType( ContentType.XML  )
					.log().everything()
				.when()
					.post(BASE + Paths.EXTRACT_XML_ATTCH_BASE).asString();
			assertNotNull( xml );	
			String errorCode = XmlPath.from(xml).get("Error.code");
			assertThat(errorCode.trim(), is(notAcceptable) );	
	}
	
	@Test
	public void notFound() throws IOException {
		String xml;
			xml = given()
				.request()
					.contentType("application/pdf"  )
					
					.body(FileUtils.readFileToByteArray(PDFEuropassCV))
				.expect()
					.statusCode(404)
					.contentType( ContentType.XML  )
					.log().body()
				.when()
					.post(BASE + "/wrong_path").asString();

			assertNotNull( xml );	
			String errorCode = XmlPath.from(xml).get("Error.code");
			assertThat(errorCode.trim(), is(notFound) );	
	}
	
	
}
