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
package europass.ewa.services.rest.exceptions;

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.jayway.restassured.http.ContentType;

import europass.ewa.enums.ContentTypes;
import europass.ewa.services.Paths;

public class RemoteDocumentRestAssured {
	private final String BASE = "/rest";
	private String jsonWithPhotoAndAttachment;
	private byte[] pdfWithPhotoAndAttachment;
	private String emptyJson;
	private String xmlWithPhotoAndAttachment;
	private String emptyXml;
	private File jsonWithPhotoAndAttachmentFile;
	
	@Before
	public void prepare() throws IOException{
		
		final File jsonFile = new File(  getClass().getResource("/cv-photo-attachment.json").getFile());
		jsonWithPhotoAndAttachment = FileUtils.readFileToString(jsonFile);
		final File pfFile = new File(  getClass().getResource("/Europass-CV-20130618-Emmanouilidi-EN.pdf").getFile());
//		pdfWithPhotoAndAttachment = FileUtils.readFileToString(pfFile);
		pdfWithPhotoAndAttachment = FileUtils.readFileToByteArray(pfFile);
		
		final File emptyJsonFile = new File(  getClass().getResource("/empty-json.json").getFile());
		emptyJson = FileUtils.readFileToString(emptyJsonFile);
		
		jsonWithPhotoAndAttachmentFile = new File(  getClass().getResource("/cv-photo-attachment.json").getFile());
		
		final File xmlFile = new File(  getClass().getResource("/cv-photo-attachment.xml").getFile());
		xmlWithPhotoAndAttachment = FileUtils.readFileToString(xmlFile);
		
		final File emptyXmlFile = new File(  getClass().getResource("/emptyXml.xml").getFile());
		emptyXml = FileUtils.readFileToString(emptyXmlFile);
		
		
	}
	@Test
	public void helloWorld(){
		expect()
			.statusCode(200)
			.contentType(ContentType.TEXT)
			.body(equalTo("Europass: This is an XML/JSON v3.0 based Conversion Service!"))
			.when()
				.get( BASE + Paths.CONVERSION_BASE);
	}
	
	// ------------ JSON TO XML --------------------------------------
	
	@Test
	public void jsonToXmlSuccess(){
		String xml =
				given() 
					.request()
						.contentType( ContentType.JSON + Paths.UTF8_CHARSET )
						.body( jsonWithPhotoAndAttachment )
					.expect()
						.statusCode(200)
						.contentType( ContentType.XML )
						.log().body()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_XML ).asString();
		assertNotNull( xml );
	}
	
	@Test
	public void jsonToXmlWrongAcceptLanguageJsonResponse(){
		String xml =
				given()
					.request()
						.contentType( ContentType.JSON + Paths.UTF8_CHARSET )
						.header("Accept-Language", "xx","yy" )
						.body( jsonWithPhotoAndAttachment )
					.expect()
						.statusCode(400)
						.contentType( ContentType.JSON )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_XML ).asString();
		assertNotNull( xml );
	}
	
	
	@Test
	public void jsonToXmlInvalidRequestContentType(){
		String xml =
				given()
					.request()
						.contentType( ContentType.XML + Paths.UTF8_CHARSET )
						.body( jsonWithPhotoAndAttachment )
					.expect()
						.statusCode(415)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_XML ).asString();
		assertNotNull( xml );
	}
	
	
	@Test
	public void jsonToXmlInvalidInputFileContentType(){
		String xml =
				given()
					.request()
						.contentType( ContentType.JSON + Paths.UTF8_CHARSET )
						.body( pdfWithPhotoAndAttachment )
					.expect()
						.statusCode(400)
						.contentType( ContentType.JSON )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_XML ).asString();
		assertNotNull( xml );
	}
	

	@Test
	public void jsonToXmlNoInput(){
		String xml =
				given()
					.request()
						.contentType( ContentType.JSON + Paths.UTF8_CHARSET )
					.expect()
						.statusCode(415)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_XML ).asString();
		assertNotNull( xml );
	}
	
	
	@Test
	public void jsonToXmlInvalidMethod(){
		String xml =
				given()
					.request()
						.contentType( ContentType.JSON + Paths.UTF8_CHARSET )
						.body( jsonWithPhotoAndAttachment )
					.expect()
						.statusCode(405)
						.contentType( ContentType.JSON )
						.log().everything()
					.when()
						.put(BASE + Paths.CONVERSION_BASE + Paths.PATH_XML ).asString();
		assertNotNull( xml );
	}
	
	@Test
	public void jsonToXmlInvalidHeaders(){
		String xml =
				given()
					.request()
						.contentType( ContentType.JSON + Paths.UTF8_CHARSET )
						.header("Accept", "text/html")
						.body( jsonWithPhotoAndAttachment )
					.expect()
						.statusCode(406)
						.contentType( ContentType.JSON )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_XML ).asString();
		assertNotNull( xml );
	}
	
	@Test
	public void jsonToXmlInvalidRestUrl(){
		String xml =
				given()
					.request()
						.contentType( ContentType.JSON + Paths.UTF8_CHARSET )
						.body( jsonWithPhotoAndAttachment )
					.expect()
						.statusCode(404)
						.contentType( ContentType.JSON )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_XML + "/wrongPath" ).asString();
		assertNotNull( xml );
	}
	
	
	@Test
	public void jsonToXmlEmptyInputJson(){
		String xml =
				given()
					.request()
						.contentType( ContentType.JSON + Paths.UTF8_CHARSET )
						.body( emptyJson )
					.expect()
						.statusCode(400)
						.contentType( ContentType.JSON )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_XML ).asString();
		assertNotNull( xml );
	}
	
	@Test
	public void jsonToXmlMissingContentType() throws Exception{
		String xml =
				given()
					.request()						
						.body(jsonWithPhotoAndAttachment)
					.expect()
						.statusCode(415)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_XML ).asString();
		assertNotNull( xml );
	}
	
	
	@Test
	public void jsonToXmlSuccessInputAsFileToByte() throws IOException{
		String xml =
				given()
					.request()
						.contentType( ContentType.JSON + Paths.UTF8_CHARSET )
						.body(FileUtils.readFileToByteArray(jsonWithPhotoAndAttachmentFile) )
					.expect()
						.statusCode(200)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_XML ).asString();
		assertNotNull( xml );
	}
	

	
	// ------------ JSON TO XML CV ONLY ----------------------------
	// Replace Paths.PATH_XML with Paths.PATH_XML_CV_ONLY in all tests of "JSON TO XML"
	// and then run tests
	
	// ------------ JSON TO XML ESP ONLY-----------------------------
	// Replace Paths.PATH_XML with Paths.PATH_XML_ESP_ONLY in all tests of "JSON TO XML"
	// and then run tests
	
	// ------------ XML TO JSON -----------------------------------------------
	@Test
	public void xmlToJsonSuccess(){
		String xml =
				given()
					.request()
						.contentType( ContentType.XML + Paths.UTF8_CHARSET )
						.body( xmlWithPhotoAndAttachment )
					.expect()
						.statusCode(200)
						.contentType( ContentType.JSON )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_JSON ).asString();
		assertNotNull( xml );
	}
	
	@Test
	public void xmlToJsonSuccessWrongAcceptLanguage(){
		String xml =
				given()
					.request()
					.header("Accept-Language", "elll"," kk" )
						.contentType( ContentType.XML + Paths.UTF8_CHARSET )
						.body( xmlWithPhotoAndAttachment )
					.expect()
						.statusCode(400)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_JSON ).asString();
		assertNotNull( xml );
	}
	
	
	@Test
	public void xmlToJsonWrongContentType(){
		String xml =
				given()
					.request()
						.contentType( ContentType.TEXT + Paths.UTF8_CHARSET )
						.body( xmlWithPhotoAndAttachment )
					.expect()
						.statusCode(415)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_JSON ).asString();
		assertNotNull( xml );
	}
	
	@Test
	public void xmlToJsonNoInput(){
		String xml =
				given()
					.request()
						.contentType( ContentType.XML + Paths.UTF8_CHARSET )
					.expect()
						.statusCode(415)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_JSON ).asString();
		assertNotNull( xml );
	}
	
	@Test
	public void xmlToJsonMissingContentType(){
		String xml =
				given()
					.request()
						.body( xmlWithPhotoAndAttachment )
					.expect()
						.statusCode(415)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_JSON ).asString();
		assertNotNull( xml );
	}
	
	@Test
	public void xmlToJsonInvalidInputFileContentType(){
		String xml =
				given()
					.request()
						.contentType( ContentType.XML + Paths.UTF8_CHARSET )
						.body( pdfWithPhotoAndAttachment )
					.expect()
						.statusCode(415)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_JSON ).asString();
		assertNotNull( xml );
	}
	
	@Test
	public void xmlToJsonInvalidInputFileContentTypeJSON(){
		String xml =
				given()
					.request()
						.contentType( ContentType.XML + Paths.UTF8_CHARSET )
						.body( jsonWithPhotoAndAttachment )
					.expect()
						.statusCode(400)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_JSON ).asString();
		assertNotNull( xml );
	}
	@Test
	public void xmlToJsonEmptyInputFile(){
		String xml =
				given()
					.request()
						.contentType( ContentType.XML + Paths.UTF8_CHARSET )
						.body( emptyXml )
					.expect()
						.statusCode(400)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_JSON ).asString();
		assertNotNull( xml );
	}
	
	@Test
	public void xmlToJsonWrongHeader(){
		String xml =
				given()
					.request()
						.contentType( ContentType.XML + Paths.UTF8_CHARSET )
						.header("Accept", "text/html")
						.body( xmlWithPhotoAndAttachment )
					.expect()
						.statusCode(406)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_JSON ).asString();
		assertNotNull( xml );
	}
	
	@Test
	public void xmlToJsonInvalidUrl(){
		String xml =
				given()
					.request()
						.contentType( ContentType.XML + Paths.UTF8_CHARSET )
						.body( xmlWithPhotoAndAttachment )
					.expect()
						.statusCode(404)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_JSON + "/test").asString();
		assertNotNull( xml );
	}
	
	@Test
	public void xmlToJsonInvalidMethod(){
		String xml =
				given()
					.request()
						.contentType( ContentType.XML + Paths.UTF8_CHARSET )
						.body( xmlWithPhotoAndAttachment )
					.expect()
						.statusCode(405)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.put(BASE + Paths.CONVERSION_BASE + Paths.PATH_JSON ).asString();
		assertNotNull( xml );
	}
	// ------------ XML/JSON TO OPEN DOCUMENT --------------------------------
	@Test
	public void xmlToODTSuccess(){
		String xml =
				given()
					.request()
						.contentType( ContentType.XML + Paths.UTF8_CHARSET )
						.body( xmlWithPhotoAndAttachment )
					.expect()
						.statusCode(200)
						.contentType( ContentTypes.OPEN_DOC_CT )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_OPEN_DOCUMENT ).asString();
		assertNotNull( xml );
	}
	
	@Test
	public void xmlToODTUnsupportedLanguageResponseXml(){
		String xml =
				given()
					.request()
					.header("Accept-Language", "elll"," kk" )
						.contentType( ContentType.XML + Paths.UTF8_CHARSET )
						.body( xmlWithPhotoAndAttachment )
					.expect()
						.statusCode(400)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_OPEN_DOCUMENT ).asString();
		assertNotNull( xml );
	}
	
	@Test
	public void xmlToODTUnsupportedLanguageResponseJSON(){
		String xml =
				given()
					.request()
					.header("Accept-Language", "elll"," kk" )
						.contentType( ContentType.JSON + Paths.UTF8_CHARSET )
						.body( jsonWithPhotoAndAttachment )
					.expect()
						.statusCode(400)
						.contentType( ContentType.JSON )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_OPEN_DOCUMENT ).asString();
		assertNotNull( xml );
	}
	@Test
	public void xmlToODTWrongHeader(){
		String xml =
				given()
					.request()
						.contentType( ContentType.XML + Paths.UTF8_CHARSET )
						.header("Accept", "text/html")
						.body( xmlWithPhotoAndAttachment )
					.expect()
						.statusCode(406)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_OPEN_DOCUMENT ).asString();
		assertNotNull( xml );
	}
	
	@Test
	public void xmlToODTWrongContentType(){
		String xml =
				given()
					.request()
						.contentType( ContentType.TEXT + Paths.UTF8_CHARSET )
						.body( xmlWithPhotoAndAttachment )
					.expect()
						.statusCode(415)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_OPEN_DOCUMENT ).asString();
		assertNotNull( xml );
	}
	
	@Test
	public void xmlToODTEmptyInput(){
		String xml =
				given()
					.request()
						.contentType( ContentType.XML + Paths.UTF8_CHARSET )
					.expect()
						.statusCode(415)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_OPEN_DOCUMENT ).asString();
		assertNotNull( xml );
	}
	
	@Test
	public void xmlToODTMissingContentType(){
		String xml =
				given()
					.request()
						.body( xmlWithPhotoAndAttachment )
					.expect()
						.statusCode(415)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_OPEN_DOCUMENT ).asString();
		assertNotNull( xml );
	}
	
	@Test
	public void xmlToODTInvalidBinaryInputFileContentType(){
		String xml =
				given()
					.request()
						.contentType( ContentType.XML + Paths.UTF8_CHARSET )
						.body( pdfWithPhotoAndAttachment )
					.expect()
						.statusCode(415)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_OPEN_DOCUMENT ).asString();
		assertNotNull( xml );
	}
	
	@Test
	public void xmlToODTInvalidNonBinaryInputFileContentType(){
		String xml =
				given()
					.request()
						.contentType( ContentType.XML + Paths.UTF8_CHARSET )
						.body( jsonWithPhotoAndAttachment )
					.expect()
						.statusCode(400)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_OPEN_DOCUMENT ).asString();
		assertNotNull( xml );
	}
	
	@Test
	public void xmlToODTInvalidInputFileContentType(){
		String xml =
				given()
					.request()
						.contentType( ContentType.JSON + Paths.UTF8_CHARSET )
						.body( pdfWithPhotoAndAttachment )
					.expect()
						.statusCode(400)
						.contentType( ContentType.JSON )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_OPEN_DOCUMENT ).asString();
		assertNotNull( xml );
	}
	
	@Test
	public void xmlToODTEmptyInputFile(){
		String xml =
				given()
					.request()
						.contentType( ContentType.XML + Paths.UTF8_CHARSET )
						.body( emptyXml )
					.expect()
						.statusCode(400)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_OPEN_DOCUMENT ).asString();
		assertNotNull( xml );
	}
	
	@Test
	public void xmlToODTEmptyJSONInputFile(){
		String xml =
				given()
					.request()
						.contentType( ContentType.JSON + Paths.UTF8_CHARSET )
						.body( emptyJson)
					.expect()
						.statusCode(400)
						.contentType( ContentType.JSON )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_OPEN_DOCUMENT ).asString();
		assertNotNull( xml );
	}
	
	@Test
	public void xmlToODTInvalidUrl(){
		String xml =
				given()
					.request()
						.contentType( ContentType.XML + Paths.UTF8_CHARSET )
						.body( xmlWithPhotoAndAttachment )
					.expect()
						.statusCode(404)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_OPEN_DOCUMENT + "/test").asString();
		assertNotNull( xml );
	}
	
	@Test
	public void xmlToODTInvalidMethod(){
		String xml =
				given()
					.request()
						.contentType( ContentType.XML + Paths.UTF8_CHARSET )
						.body( xmlWithPhotoAndAttachment )
					.expect()
						.statusCode(405)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.delete(BASE + Paths.CONVERSION_BASE + Paths.PATH_OPEN_DOCUMENT ).asString();
		assertNotNull( xml );
	}

	// ------------ XML/JSON TO WORD ------------------------------------------
	//this test will fail if the office server is not available
	@Test
	public void xmlToWordSuccess(){
		String xml =
				given()
					.request()
						.contentType( ContentType.XML + Paths.UTF8_CHARSET )
						.body( xmlWithPhotoAndAttachment )
					.expect()
						.statusCode(200)
						.contentType( ContentTypes.WORD_DOC_CT )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_WORD ).asString();
		assertNotNull( xml );
	}
	
	@Test
	public void xmlToWordSuccessUnsupportedAcceptLanguage(){
		String xml =
				given()
					.request()
					.header("Accept-Language", "elll"," kk" )
						.contentType( ContentType.XML + Paths.UTF8_CHARSET )
						.body( xmlWithPhotoAndAttachment )
					.expect()
						.statusCode(400)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_WORD ).asString();
		assertNotNull( xml );
	}
	
	@Test
	public void xmlToWordSuccessUnsupportedAcceptLanguageResponseJson(){
		String xml =
				given()
					.request()
					.header("Accept-Language", "elll"," kk" )
						.contentType( ContentType.JSON + Paths.UTF8_CHARSET )
						.body( jsonWithPhotoAndAttachment )
					.expect()
						.statusCode(400)
						.contentType( ContentType.JSON )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_WORD ).asString();
		assertNotNull( xml );
	}

	
	//this test will fail if the Office Server is available
	@Ignore
	@Test
	public void xmlToWordOfficeServerNotAvailable(){
		String xml =
				given()
					.request()
						.contentType( ContentType.XML + Paths.UTF8_CHARSET )
						.body( xmlWithPhotoAndAttachment )
					.expect()
						.statusCode(500)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_WORD ).asString();
		assertNotNull( xml );
	}
	
	@Test
	public void xmlToWordWrongContentType(){
		String xml =
				given()
					.request()
						.contentType( ContentType.TEXT + Paths.UTF8_CHARSET )
						.body( xmlWithPhotoAndAttachment )
					.expect()
						.statusCode(415)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_WORD ).asString();
		assertNotNull( xml );
	}
	
	@Test
	public void xmlToWordEmptyInput(){
		String xml =
				given()
					.request()
						.contentType( ContentType.XML + Paths.UTF8_CHARSET )
					.expect()
						.statusCode(415)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_WORD ).asString();
		assertNotNull( xml );
	}
	
	@Test
	public void xmlToWordMissingContentType(){
		String xml =
				given()
					.request()
						.body( xmlWithPhotoAndAttachment )
					.expect()
						.statusCode(415)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_WORD ).asString();
		assertNotNull( xml );
	}
	
	@Test
	public void xmlToWordInvalidBinaryInputFileContentType(){
		String xml =
				given()
					.request()
						.contentType( ContentType.XML + Paths.UTF8_CHARSET )
						.body( pdfWithPhotoAndAttachment )
					.expect()
						.statusCode(415)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_WORD ).asString();
		assertNotNull( xml );
	}
	
	@Test
	public void xmlToWordInvalidNonBinaryInputFileContentType(){
		String xml =
				given()
					.request()
						.contentType( ContentType.XML + Paths.UTF8_CHARSET )
						.body( jsonWithPhotoAndAttachment )
					.expect()
						.statusCode(400)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_WORD ).asString();
		assertNotNull( xml );
	}
	
	
	@Test
	public void xmlToWordInvalidInputFileContentTypeResponseJSON(){
		String xml =
				given()
					.request()
						.contentType( ContentType.JSON + Paths.UTF8_CHARSET )
						.body( pdfWithPhotoAndAttachment )
					.expect()
						.statusCode(400)
						.contentType( ContentType.JSON )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_WORD ).asString();
		assertNotNull( xml );
	}
	
	@Test
	public void xmlToWordEmptyInputFile(){
		String xml =
				given()
					.request()
						.contentType( ContentType.XML + Paths.UTF8_CHARSET )
						.body( emptyXml )
					.expect()
						.statusCode(400)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_WORD ).asString();
		assertNotNull( xml );
	}
	
	@Test
	public void xmlToWordWrongHeaders(){
		String xml =
				given()
					.request()
						.contentType( ContentType.XML + Paths.UTF8_CHARSET )
						.header("Accept", "text/html")
						.body( xmlWithPhotoAndAttachment )
					.expect()
						.statusCode(406)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_WORD ).asString();
		assertNotNull( xml );
	}
	
	@Test
	public void xmlToWordInvalidUrl(){
		String xml =
				given()
					.request()
						.contentType( ContentType.XML + Paths.UTF8_CHARSET )
						.body( xmlWithPhotoAndAttachment )
					.expect()
						.statusCode(404)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_WORD + "/test").asString();
		assertNotNull( xml );
	}
	
	@Test
	public void xmlToWordInvalidMethod(){
		String xml =
				given()
					.request()
						.contentType( ContentType.XML + Paths.UTF8_CHARSET )
						.body( xmlWithPhotoAndAttachment )
					.expect()
						.statusCode(405)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.delete(BASE + Paths.CONVERSION_BASE + Paths.PATH_WORD ).asString();
		assertNotNull( xml );
	}
	
	// ------------ XML/JSON TO PDF (ECV+ESP) --------------------------------
	//this test will fail if the office server is not available
	@Test
	public void xmlToPdfSuccess(){
		String xml =
				given()
					.request()
						.contentType( ContentType.XML + Paths.UTF8_CHARSET )
						.body( xmlWithPhotoAndAttachment )
					.expect()
						.statusCode(200)
						.contentType( ContentTypes.PDF_CT )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_PDF ).asString();
		assertNotNull( xml );
	}
	
	@Test
	public void xmlToPdfUnsupportedAcceptLanguage(){
		String xml =
				given()
					.header("Accept-Language", "elll"," kk" )
					.request()
						.contentType( ContentType.XML + Paths.UTF8_CHARSET )
						.body( xmlWithPhotoAndAttachment )
					.expect()
						.statusCode(400)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_PDF ).asString();
		assertNotNull( xml );
	}
	

	//this test will fail if the Office Server is available
	@Ignore
	@Test
	public void xmlToPdfOfficeServerNotAvailable(){
		String xml =
				given()
					.request()
						.contentType( ContentType.XML + Paths.UTF8_CHARSET )
						.body( xmlWithPhotoAndAttachment )
					.expect()
						.statusCode(500)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_PDF ).asString();
		assertNotNull( xml );
	}
	
	@Test
	public void xmlToPdfWrongContentType(){
		String xml =
				given()
					.request()
						.contentType( ContentType.TEXT + Paths.UTF8_CHARSET )
						.body( xmlWithPhotoAndAttachment )
					.expect()
						.statusCode(415)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_PDF ).asString();
		assertNotNull( xml );
	}
	
	@Test
	public void xmlToPdfNoInput(){
		String xml =
				given()
					.request()
						.contentType( ContentType.XML + Paths.UTF8_CHARSET )
					.expect()
						.statusCode(415)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_PDF ).asString();
		assertNotNull( xml );
	}
	
	@Test
	public void xmlToPdfMissingContentType(){
		String xml =
				given()
					.request()
						.body( xmlWithPhotoAndAttachment )
					.expect()
						.statusCode(415)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_PDF ).asString();
		assertNotNull( xml );
	}
	
	@Test
	public void xmlToPdfInvalidBinaryInputFileContentType(){
		String xml =
				given()
					.request()
						.contentType( ContentType.XML + Paths.UTF8_CHARSET )
						.body( pdfWithPhotoAndAttachment )
					.expect()
						.statusCode(415)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_PDF ).asString();
		assertNotNull( xml );
	}
	
	@Test
	public void xmlToPdfInvalidNonBinaryInputFileContentType(){
		String xml =
				given()
					.request()
						.contentType( ContentType.XML + Paths.UTF8_CHARSET )
						.body( jsonWithPhotoAndAttachment )
					.expect()
						.statusCode(400)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_PDF ).asString();
		assertNotNull( xml );
	}
	
	@Test
	public void xmlToPdfEmptyInputFile(){
		String xml =
				given()
					.request()
						.contentType( ContentType.XML + Paths.UTF8_CHARSET )
						.body( emptyXml )
					.expect()
						.statusCode(400)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_PDF ).asString();
		assertNotNull( xml );
	}
	
	@Test
	public void xmlToPdfInvalidnputFileContentType(){
		String xml =
				given()
					.request()
						.contentType( ContentType.JSON + Paths.UTF8_CHARSET )
						.body( pdfWithPhotoAndAttachment )
					.expect()
						.statusCode(400)
						.contentType( ContentType.JSON )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_PDF ).asString();
		assertNotNull( xml );
	}
	
	@Test
	public void xmlToPdfWrongHeaders(){
		String xml =
				given()
					.request()
						.contentType( ContentType.XML + Paths.UTF8_CHARSET )
						.header("Accept", "text/html")
						.body( xmlWithPhotoAndAttachment )
					.expect()
						.statusCode(406)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_PDF ).asString();
		assertNotNull( xml );
	}
	
	@Test
	public void xmlToPdfInvalidUrl(){
		String xml =
				given()
					.request()
						.contentType( ContentType.XML + Paths.UTF8_CHARSET )
						.body( xmlWithPhotoAndAttachment )
					.expect()
						.statusCode(404)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_PDF + "/test").asString();
		assertNotNull( xml );
	}
	
	@Test
	public void xmlToPdfInvalidMethod(){
		String xml =
				given()
					.request()
						.contentType( ContentType.XML + Paths.UTF8_CHARSET )
						.body( xmlWithPhotoAndAttachment )
					.expect()
						.statusCode(405)
						.contentType( ContentType.XML )
						.log().everything()
					.when()
						.delete(BASE + Paths.CONVERSION_BASE + Paths.PATH_PDF ).asString();
		assertNotNull( xml );
	}
	
	
	// ------------ XML/JSON TO PDF (ESP ONLY) --------------------------------
	// Replace Paths.PATH_PDF with Paths.PATH_PDF_ESP_ONLY in all tests of "XML/JSON TO PDF (ECV+ESP)"
	// and then run the tests
	
	// ------------ XML/JSON TO ODF ( CV ONLY) --------------------------------
	// Replace Paths.PATH_PDF with Paths.PATH_PDF_CV_ONLY in all tests of "XML/JSON TO PDF (ECV+ESP)"
	// and then run the tests
}

