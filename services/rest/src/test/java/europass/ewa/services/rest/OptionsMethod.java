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

import java.io.IOException;

import org.junit.Test;

import europass.ewa.services.Paths;

public class OptionsMethod {
	private final String BASE = "/rest";
	
	@Test
	public void optionsExtractService() throws IOException {
		String xml;
			xml = given()
				.request()
				.expect()
					.log().everything()
				.when()
					.options(BASE + Paths.EXTRACT_XML_ATTCH_BASE).asString();
			assertNotNull( xml );	
	}
	
	@Test
	public void optionsConversionService() throws IOException {
		String xml;
			xml = given()
				.request()
				.expect()
					.log().everything()
				.when()
					.options(BASE +  Paths.CONVERSION_BASE).asString();
			assertNotNull( xml );	
	}

	@Test
	public void optionsXmlBackwardService() throws IOException {
		String xml;
			xml = given()
				.request()
				.expect()
					.log().everything()
				.when()
					.options(BASE +  Paths.UPGRADE_BASE).asString();
			assertNotNull( xml );	
	}
}
