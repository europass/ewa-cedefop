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
import com.jayway.restassured.path.xml.XmlPath;

import europass.ewa.services.Paths;
import europass.ewa.services.enums.XmlVersion;

public class UpgradeXmlRestAssured  {
	private final String BASE = "/rest";
	
	private final String xml20 =
"<?xml version='1.0' encoding='UTF-8'?><europass:learnerinfo locale=\"de_DE\" "+
	"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "+
	"xmlns:europass=\"http://europass.cedefop.europa.eu/Europass/V2.0\" "+
	"xsi:schemaLocation=\"http://europass.cedefop.europa.eu/Europass/V2.0 http://europass.cedefop.europa.eu/xml/EuropassSchema_V2.0.xsd\">"+
	"<identification>"+
		"<firstname>Lara</firstname>"+
		"<lastname>Brontër</lastname>"+
	"</identification>"+
"</europass:learnerinfo>";

	
	@Test
	public void upgrade(){
		
		String xml = 
			given()
			.request()
				.contentType( ContentType.XML + Paths.UTF8_CHARSET )
				.body( xml20 )
			.expect()
				.statusCode(200)
				.contentType( ContentType.XML )
				.log().everything()
			.when()
				.post(BASE + Paths.UPGRADE_BASE  )
			.asString();
		
		String surname = XmlPath.from(xml).get("SkillsPassport.LearnerInfo.Identification.PersonName.Surname");
		
		assertThat("Surname", surname.trim(), is("Brontër"));
		
		String version = XmlPath.from(xml).get("SkillsPassport.DocumentInfo.XSDVersion");
		
		assertThat("Version", version.trim(), is(XmlVersion.LATEST.getCode()));
	}
	

}
