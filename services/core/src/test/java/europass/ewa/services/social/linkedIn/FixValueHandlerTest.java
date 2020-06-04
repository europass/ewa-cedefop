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

import europass.ewa.services.social.InstanceClassMismatchException;
import europass.ewa.services.social.linkedin.FixValueHandler;

public class FixValueHandlerTest {

	private static FixValueHandler handler = null;
	private static String stringObj = null;
	private static Integer intObj = null;
	
	@BeforeClass
	public static void setup(){
		handler = new FixValueHandler();
		stringObj = "This is a fixed Value";
		intObj = new Integer(12345);
	}
	
	@Test
	public void test() throws InstanceClassMismatchException{
		
		String toString = "";
		
		toString = (String)handler.transform(new Object(), toString, stringObj);
		assertEquals(stringObj, toString);
		
		Integer toInteger = new Integer(0);
		
		toInteger = (Integer)handler.transform(new Object(), toInteger, intObj);
		assertEquals(12345, toInteger.intValue());
	}
}
