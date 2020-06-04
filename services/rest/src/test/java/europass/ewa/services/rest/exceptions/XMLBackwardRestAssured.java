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

import static com.jayway.restassured.RestAssured.given;
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
import europass.ewa.services.enums.XmlVersion;

public class XMLBackwardRestAssured {
	private final String BASE = "/rest";
	private String xmlCv;
	private String emptyXmlCv;
	private byte[] pdfCv;
	private String jsonCv;
	
	private String XmlEuropassCV_version1_0;
	private String XmlEuropassCV_version1_1;
	private String XmlEuropassCV_version1_2;
	private String XmlEuropassCV_version2_0_corrupt;
	private String XmlEuropassCV_version2_0;
	private String XmlEuropassCV_version2_0_badformat;
	private String XmlEuropassCV_version3_0;
	private String XmlEuropassCV_version3_1_2;

	
	@Before
	public void prepare() throws IOException{
		final File xmlFile = new File(  getClass().getResource("/cv-photo-attachment.xml").getFile());
		xmlCv = FileUtils.readFileToString(xmlFile);
		
		final File emptyXmlFile = new File(  getClass().getResource("/emptyXml.xml").getFile());
		emptyXmlCv = FileUtils.readFileToString(emptyXmlFile);
		
		final File PDFEuropassCVFile = new File(  getClass().getResource("/Europass-CV-20130618-Emmanouilidi-EN.pdf").getFile());
		pdfCv = FileUtils.readFileToByteArray(PDFEuropassCVFile);
		
		
		final File version1_0 = new File(  getClass().getResource("/Europass-CV-version1.0.xml").getFile());
		XmlEuropassCV_version1_0 = FileUtils.readFileToString(version1_0);
		
		final File version1_1 = new File(  getClass().getResource("/Europass-CV-version1.1.xml").getFile());
		XmlEuropassCV_version1_1 = FileUtils.readFileToString(version1_1);
		
		final File version1_2 = new File(  getClass().getResource("/Europass-CV-version1.2.xml").getFile());
		XmlEuropassCV_version1_2 = FileUtils.readFileToString(version1_2);
		
		final File version2_0_corrupt = new File(  getClass().getResource("/Europass-CV-version2.0-corrupt1.xml").getFile());
		XmlEuropassCV_version2_0_corrupt = FileUtils.readFileToString(version2_0_corrupt);
		
		final File version2_0 = new File(  getClass().getResource("/Europass-CV-version2.0.xml").getFile());
		XmlEuropassCV_version2_0 = FileUtils.readFileToString(version2_0);
		
		final File version3_0 = new File(  getClass().getResource("/Europass-CV-version3.0.xml").getFile());
		XmlEuropassCV_version3_0 = FileUtils.readFileToString(version3_0);
		
		final File version2_0_badformat = new File(  getClass().getResource("/Europass-CV-version2.0-badformat.xml").getFile());
		XmlEuropassCV_version2_0_badformat = FileUtils.readFileToString(version2_0_badformat);
	
		final File JsonEuropassCVFile = new File(  getClass().getResource("/cv-photo.json").getFile());
		jsonCv = FileUtils.readFileToString(JsonEuropassCVFile);
		
		final File version3_1_2 = new File(  getClass().getResource("/Europass-CV-version3.1.2.xml").getFile());
		XmlEuropassCV_version3_1_2 = FileUtils.readFileToString(version3_1_2);

	}
	
	
	@Test
	public void upgradeSuccess(){
		
		String xml = 
			given()
			.request()
				.contentType( ContentType.XML + Paths.UTF8_CHARSET )
				.body( xmlCv )
			.expect()
				.statusCode(200)
				.contentType( ContentType.XML )
				.log().everything()
			.when()
				.post(BASE + Paths.UPGRADE_BASE  )
			.asString();
		
		String version = XmlPath.from(xml).get("SkillsPassport.DocumentInfo.XSDVersion");
		
		assertThat("Version", version.trim(), is(XmlVersion.LATEST.getCode()));
	}
	
	@Test
	public void upgradeVersion1_0Success(){
		
		String xml = 
			given()
			.request()
				.contentType( ContentType.XML + Paths.UTF8_CHARSET )
				.body( XmlEuropassCV_version1_0 )
			.expect()
				.statusCode(200)
				.contentType( ContentType.XML )
				.log().everything()
			.when()
				.post(BASE + Paths.UPGRADE_BASE  )
			.asString();
		
		String version = XmlPath.from(xml).get("SkillsPassport.DocumentInfo.XSDVersion");
		
		assertThat("Version", version.trim(), is(XmlVersion.LATEST.getCode()));
	}
	
	@Test
	public void upgradeVersion1_1Success(){
		
		String xml = 
			given()
			.request()
				.contentType( ContentType.XML + Paths.UTF8_CHARSET )
				.body( XmlEuropassCV_version1_1 )
			.expect()
				.statusCode(200)
				.contentType( ContentType.XML )
				.log().everything()
			.when()
				.post(BASE + Paths.UPGRADE_BASE  )
			.asString();
		
		String version = XmlPath.from(xml).get("SkillsPassport.DocumentInfo.XSDVersion");
		
		assertThat("Version", version.trim(), is(XmlVersion.LATEST.getCode()));
	}
	
	@Test
	public void upgradeVersion1_2Success(){
		
		String xml = 
			given()
			.request()
				.contentType( ContentType.XML + Paths.UTF8_CHARSET )
				.body( XmlEuropassCV_version1_2 )
			.expect()
				.statusCode(200)
				.contentType( ContentType.XML )
				.log().everything()
			.when()
				.post(BASE + Paths.UPGRADE_BASE  )
			.asString();
		
		String version = XmlPath.from(xml).get("SkillsPassport.DocumentInfo.XSDVersion");
		
		assertThat("Version", version.trim(), is(XmlVersion.LATEST.getCode()));
	}
	
	@Test
	public void upgradeVersion2_0Success(){
		
		String xml = 
			given()
			.request()
				.contentType( ContentType.XML + Paths.UTF8_CHARSET )
				.body( XmlEuropassCV_version2_0 )
			.expect()
				.statusCode(200)
				.contentType( ContentType.XML )
				.log().everything()
			.when()
				.post(BASE + Paths.UPGRADE_BASE  )
			.asString();
		
		String version = XmlPath.from(xml).get("SkillsPassport.DocumentInfo.XSDVersion");
		
		assertThat("Version", version.trim(), is(XmlVersion.LATEST.getCode()));
	}
	
	@Test
	public void upgradeVersion3_0Success(){
		
		String xml = 
			given()
			.request()
				.contentType( ContentType.XML + Paths.UTF8_CHARSET )
				.body( XmlEuropassCV_version3_0 )
			.expect()
				.statusCode(200)
				.contentType( ContentType.XML )
				.log().everything()
			.when()
				.post(BASE + Paths.UPGRADE_BASE  )
			.asString();
		
		String version = XmlPath.from(xml).get("SkillsPassport.DocumentInfo.XSDVersion");
		
		assertThat("Version", version.trim(), is(XmlVersion.LATEST.getCode()));
	}
	
	@Test
	public void upgradeVersion3_1_2Success(){
		
		String xml = 
			given()
			.request()
				.contentType( ContentType.XML + Paths.UTF8_CHARSET )
				.body( XmlEuropassCV_version3_1_2 )
			.expect()
				.statusCode(200)
				.contentType( ContentType.XML )
				.log().everything()
			.when()
				.post(BASE + Paths.UPGRADE_BASE  )
			.asString();
		
		String version = XmlPath.from(xml).get("SkillsPassport.DocumentInfo.XSDVersion");
		assertThat("Version", version.trim(), is(XmlVersion.LATEST.getCode()));
	}
	
	@Test
	public void upgradeVersion2_0_badformat(){
		
		String xml = 
			given()
			.request()
				.contentType( ContentType.XML + Paths.UTF8_CHARSET )
				.body( XmlEuropassCV_version2_0_badformat )
			.expect()
				.statusCode(400)
				.contentType( ContentType.XML )
				.log().everything()
			.when()
				.post(BASE + Paths.UPGRADE_BASE  )
			.asString();
		
		assertNotNull( xml );	
	}
	

	
	@Test
	public void upgradeVersion2_0_corrupt(){
		
		String xml = 
			given()
			.request()
				.contentType( ContentType.XML + Paths.UTF8_CHARSET )
				.body( XmlEuropassCV_version2_0_corrupt )
			.expect()
				.statusCode(200)
				.contentType( ContentType.XML )
				.log().everything()
			.when()
				.post(BASE + Paths.UPGRADE_BASE  )
			.asString();
		
		String version = XmlPath.from(xml).get("SkillsPassport.DocumentInfo.XSDVersion");
		
		assertThat("Version", version.trim(), is(XmlVersion.LATEST.getCode()));
	}
	
	@Test
	public void upgradeWrongContentType(){
		
		String xml = 
			given()
			.request()
				.contentType( ContentType.TEXT + Paths.UTF8_CHARSET )
				.body( xmlCv )
			.expect()
				.statusCode(415)
				.contentType( ContentType.XML )
				.log().everything()
			.when()
				.post(BASE + Paths.UPGRADE_BASE  )
			.asString();
		assertNotNull( xml );	
		
	}
	
	
	@Test
	public void upgradeMissingContentType(){
		
		String xml = 
			given()
			.request()
				.body( xmlCv )
			.expect()
				.statusCode(415)
				.contentType( ContentType.XML )
				.log().everything()
			.when()
				.post(BASE + Paths.UPGRADE_BASE  )
			.asString();
		assertNotNull( xml );	
	}
	
	@Test
	public void upgradeMissingBody(){
		
		String xml = 
			given()
			.request()
				.contentType( ContentType.XML + Paths.UTF8_CHARSET )
			.expect()
				.statusCode(415)
				.contentType( ContentType.XML )
				.log().everything()
			.when()
				.post(BASE + Paths.UPGRADE_BASE  )
			.asString();
		assertNotNull( xml );	
	}
	
	@Test
	public void upgradeEmptyXml(){
		
		String xml = 
			given()
			.request()
				.contentType( ContentType.XML + Paths.UTF8_CHARSET )
				.body( emptyXmlCv )
			.expect()
				.statusCode(415)
				.contentType( ContentType.XML )
				.log().everything()
			.when()
				.post(BASE + Paths.UPGRADE_BASE  )
			.asString();
		assertNotNull( xml );	
	}
	
	@Test
	public void upgradeWrongInput(){
		
		String xml = 
			given()
			.request()
				.contentType( ContentType.XML + Paths.UTF8_CHARSET )
				.body( pdfCv )
			.expect()
				.statusCode(415)
				.contentType( ContentType.XML )
				.log().everything()
			.when()
				.post(BASE + Paths.UPGRADE_BASE  )
			.asString();
		assertNotNull( xml );	
	}
	
	@Test
	public void upgradeWrongInputJson(){
		
		String xml = 
			given()
			.request()
				.contentType( ContentType.XML + Paths.UTF8_CHARSET )
				.body( jsonCv )
			.expect()
				.statusCode(415)
				.contentType( ContentType.XML )
				.log().everything()
			.when()
				.post(BASE + Paths.UPGRADE_BASE  )
			.asString();
		assertNotNull( xml );	
	}
	
	@Test
	public void upgradeInvalidMethod(){
		
		String xml = 
			given()
			.request()
				.contentType( ContentType.XML + Paths.UTF8_CHARSET )
				.body( xmlCv )
			.expect()
				.statusCode(405)
				.contentType( ContentType.XML )
				.log().everything()
			.when()
				.delete(BASE + Paths.UPGRADE_BASE  )
			.asString();
		assertNotNull( xml );		
	}
	
	@Test
	public void upgradeWrongHeader(){
		
		String xml = 
			given()
			.request()
				.contentType( ContentType.XML + Paths.UTF8_CHARSET )
				.header("Accept","text/html")
				.body( xmlCv )
			.expect()
				.statusCode(406)
				.contentType( ContentType.XML )
				.log().everything()
			.when()
				.post(BASE + Paths.UPGRADE_BASE  )
			.asString();
		assertNotNull( xml );	
	}
	
	@Test
	public void upgradeWrongURL(){
		
		String xml = 
			given()
			.request()
				.contentType( ContentType.XML + Paths.UTF8_CHARSET )
				.body( xmlCv )
			.expect()
				.statusCode(404)
				.contentType( ContentType.XML )
				.log().everything()
			.when()
				.post(BASE + "/wrongpath" )
			.asString();
		assertNotNull( xml );	
	}

	
}
