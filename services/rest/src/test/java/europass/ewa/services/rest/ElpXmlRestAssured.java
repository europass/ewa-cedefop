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

import europass.ewa.services.Paths;

public class ElpXmlRestAssured {
	
	private final String BASE = "/rest";
	
	private String xmlWithNoAttachment;
	
	@Before
	public void prepare() throws IOException{
		
		final File file2 = new File(  getClass().getResource("/elp-test.xml").getFile());
		xmlWithNoAttachment = FileUtils.readFileToString(file2);
	}
	
	@Test
	public void xmlToJsonElpOnly(){
		String json =
				given()
					.request()
						.contentType( ContentType.XML + Paths.UTF8_CHARSET )
						.body( xmlWithNoAttachment )
					.expect()
						.statusCode(200)
						.contentType( MediaType.APPLICATION_JSON )
						.log().everything()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_JSON ).asString();
		
		assertNotNull( json );
	
		String certificateTitle = JsonPath.from(json).get("SkillsPassport.LearnerInfo.Skills.Linguistic.ForeignLanguage[0].Certificate[0].Title");
		
		assertThat("Certificate Title", certificateTitle, CoreMatchers.is("CPE") );
		
		String docType = JsonPath.from(json).get("SkillsPassport.DocumentInfo.DocumentType");
		assertThat("DocumentType", docType.trim(), CoreMatchers.is("ELP"));
		
	}
	
	@Test
	public void xmlToJsonElpOnlyNoStats(){
		String json =
				given()
					.request()
						.contentType( ContentType.XML + Paths.UTF8_CHARSET )
						.body( xmlWithNoAttachment )
						.queryParam("stats", "false")
					.expect()
						.statusCode(200)
						.contentType( (ContentType.JSON + Paths.UTF8_CHARSET).replaceAll( "\\s","" ) )
						.log().body()
					.when()
						.post(BASE + Paths.CONVERSION_BASE + Paths.PATH_JSON ).asString();
		
		assertNotNull( json );
	
		String certificateTitle = JsonPath.from(json).get("SkillsPassport.LearnerInfo.Skills.Linguistic.ForeignLanguage[0].Certificate[0].Title");
		
		assertThat("Certificate Title", certificateTitle, CoreMatchers.is("CPE") );
		
		String docType = JsonPath.from(json).get("SkillsPassport.DocumentInfo.DocumentType");
		assertThat("DocumentType", docType.trim(), CoreMatchers.is("ELP"));
		
	}

}
