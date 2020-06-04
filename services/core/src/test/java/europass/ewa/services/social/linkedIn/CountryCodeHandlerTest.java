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
package europass.ewa.services.social.linkedIn;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import europass.ewa.model.CodeLabel;
import europass.ewa.services.social.InstanceClassMismatchException;
import europass.ewa.services.social.linkedin.CountryCodeHandler;

public class CountryCodeHandlerTest {

	private static CountryCodeHandler handler = null;
	private static CodeLabel cLabel = null;
	
	@BeforeClass
	public static void setup(){
		handler = new CountryCodeHandler();
	}
	
	@Test
	public void test() throws InstanceClassMismatchException{
		
		cLabel = (CodeLabel)handler.transform("gr", new CodeLabel());
		
		assertEquals("EL",cLabel.getCode());
		assertEquals("Greece",cLabel.getLabel());
		
		cLabel = (CodeLabel)handler.transform("gb", new CodeLabel());
		
		assertEquals("UK",cLabel.getCode());
		assertEquals("United Kingdom",cLabel.getLabel());
		
		cLabel = (CodeLabel)handler.transform("ie", new CodeLabel());
		
		assertEquals("IE",cLabel.getCode());
		assertEquals("Ireland",cLabel.getLabel());
		
		cLabel = (CodeLabel)handler.transform("abc123", new CodeLabel());
		
		assertEquals("abc123",cLabel.getCode());
		assertEquals(null,cLabel.getLabel());
	}
}
