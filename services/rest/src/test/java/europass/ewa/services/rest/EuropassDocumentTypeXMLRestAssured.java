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

import europass.ewa.enums.EuropassDocumentType;
import europass.ewa.services.Paths;

/**
 * Test conversion to open document and word with curl commands as described below and examine the outputs:
 * 
 * to Open Document: 
 * 
 *   ECV, ECV-ESP, ESP, ECL to Open Document CV:
 *   > curl -k http://localhost:8080/rest/document/to/opendoc-cv -H "Content-Type: application/json" @input-document-type-XXXX.json -o cv-outpout.odt
 * 
 *   ECV, ECV-ESP, ESP, ECL to Open Document ESP:
 *   > curl -k http://localhost:8080/rest/document/to/opendoc-esp -H "Content-Type: application/json" @input-document-type-XXXX.json -o cv-esp-outpout.odt
 * 
 *   ECV, ECV-ESP, ESP, ECL to Open Document ELP:
 *   > curl -k http://localhost:8080/rest/document/to/opendoc-elp -H "Content-Type: application/json" @input-document-type-XXXX.json -o esp-outpout.odt
 *   
 *   
 * to Word: 
 * 
 *   ECV, ECV-ESP, ESP, ECL to Word CV:
 *   > curl -k http://localhost:8080/rest/document/to/word-cv -H "Content-Type: application/json" @input-document-type-XXXX.json -o cv-outpout.doc
 * 
 *   ECV, ECV-ESP, ESP, ECL to Word ESP:
 *   > curl -k http://localhost:8080/rest/document/to/word-esp -H "Content-Type: application/json" @input-document-type-XXXX.json -o cv-esp-outpout.doc
 * 
 *   ECV, ECV-ESP, ESP, ECL to Word ELP:
 *   > curl -k http://localhost:8080/rest/document/to/word-elp -H "Content-Type: application/json" @input-document-type-XXXX.json -o esp-outpout.doc
 *   

 * 
 * @author pgia
 *
 */

public class EuropassDocumentTypeXMLRestAssured {
	
	private final String BASE = "/rest";
	
	private String documentTypeECV;
	private String documentTypeECV_ESP;
	private String documentTypeESP;
	private String documentTypeELP;
	private String documentTypeECL;
	
	@Before
	public void prepare() throws IOException{
		
		final File cvfile = new File(  getClass().getResource("/document-type-cv.json").getFile());
		documentTypeECV = FileUtils.readFileToString(cvfile);

		final File cvEspFile = new File(  getClass().getResource("/document-type-cv-esp.json").getFile());
		documentTypeECV_ESP = FileUtils.readFileToString(cvEspFile);

		final File espFile = new File(  getClass().getResource("/document-type-esp.json").getFile());
		documentTypeESP = FileUtils.readFileToString(espFile);

		final File elpFile = new File(  getClass().getResource("/document-type-elp.json").getFile());
		documentTypeELP = FileUtils.readFileToString(elpFile);

		final File eclFile = new File(  getClass().getResource("/document-type-ecl.json").getFile());
		documentTypeECL = FileUtils.readFileToString(eclFile);
		
	}
	
	@Test
	public void jsonCvEspToXmlCvOnly(){

		this.assertDocumentTypeJsonToXml(documentTypeECV,EuropassDocumentType.ECV.getAcronym(), Paths.PATH_XML_CV_ONLY);
		this.assertDocumentTypeJsonToXml(documentTypeESP,EuropassDocumentType.ECV.getAcronym(), Paths.PATH_XML_CV_ONLY);
		this.assertDocumentTypeJsonToXml(documentTypeECV_ESP,EuropassDocumentType.ECV.getAcronym(), Paths.PATH_XML_CV_ONLY);
		this.assertDocumentTypeJsonToXml(documentTypeELP,EuropassDocumentType.ECV.getAcronym(), Paths.PATH_XML_CV_ONLY);
		this.assertDocumentTypeJsonToXml(documentTypeECL,EuropassDocumentType.ECV.getAcronym(), Paths.PATH_XML_CV_ONLY);
	}

	@Test
	public void jsonCvEspToXmlEspOnly(){

		this.assertDocumentTypeJsonToXml(documentTypeECV,EuropassDocumentType.ESP.getAcronym(), Paths.PATH_XML_ESP_ONLY);
		this.assertDocumentTypeJsonToXml(documentTypeESP,EuropassDocumentType.ESP.getAcronym(), Paths.PATH_XML_ESP_ONLY);
		this.assertDocumentTypeJsonToXml(documentTypeECV_ESP,EuropassDocumentType.ESP.getAcronym(), Paths.PATH_XML_ESP_ONLY);
		this.assertDocumentTypeJsonToXml(documentTypeELP,EuropassDocumentType.ESP.getAcronym(), Paths.PATH_XML_ESP_ONLY);
		this.assertDocumentTypeJsonToXml(documentTypeECL,EuropassDocumentType.ESP.getAcronym(), Paths.PATH_XML_ESP_ONLY);
	}
	
	@Test
	public void jsonCvEspToXml(){

		this.assertDocumentTypeJsonToXml(documentTypeECV,EuropassDocumentType.ECV.getAcronym(), Paths.PATH_XML);
		this.assertDocumentTypeJsonToXml(documentTypeESP,EuropassDocumentType.ESP.getAcronym(), Paths.PATH_XML);
		this.assertDocumentTypeJsonToXml(documentTypeECV_ESP,EuropassDocumentType.ECV_ESP.getAcronym(), Paths.PATH_XML);
		this.assertDocumentTypeJsonToXml(documentTypeELP,EuropassDocumentType.ELP.getAcronym(), Paths.PATH_XML);
		this.assertDocumentTypeJsonToXml(documentTypeECL,EuropassDocumentType.ECL.getAcronym(), Paths.PATH_XML);
	}

	private void assertDocumentTypeJsonToXml(String jsonContents, String DocumentType, String endpoint){
		
		String xml =
				given()
					.request()
						.contentType( ContentType.JSON + Paths.UTF8_CHARSET )
						.body( jsonContents )
					.expect()
						.statusCode(200)
						.contentType( ContentType.XML )
						.log().body()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + endpoint ).asString();
		assertNotNull( xml );
	
		String firstName = XmlPath.from(xml).get("SkillsPassport.LearnerInfo.Identification.PersonName.FirstName");
		String lastName = XmlPath.from(xml).get("SkillsPassport.LearnerInfo.Identification.PersonName.Surname");

		assertThat("FirstName",firstName.trim(), CoreMatchers.is("Panagiotis"));
		assertThat("LastName", lastName.trim(), CoreMatchers.is("Giannelos"));
		
		String docType = XmlPath.from(xml).get("SkillsPassport.DocumentInfo.DocumentType");
		assertThat("DocumentType", docType.trim(), CoreMatchers.is(DocumentType));
		
	}
	
}
