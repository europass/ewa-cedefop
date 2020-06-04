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
import europass.ewa.services.social.linkedin.TaxonomyTranslatorHandler;

public class TaxonomyTranslatorHandlerTest {

	private static TaxonomyTranslatorHandler handler = null;
	
	@BeforeClass
	public static void setup(){
		handler = new TaxonomyTranslatorHandler();
	}
	
	@Test
	public void headlines() throws InstanceClassMismatchException{
		
		String label = (String)handler.transform(new Object(), "", "HeadlineType", "position", "el");
		
		assertEquals("ΕΠΑΓΓΕΛΜΑ",label);
		
		label = (String)handler.transform(new Object(), "", "HeadlineType", "position", "es");
		
		//		assertEquals("FUNCIÓN",label);
	}
	
	@Test
	public void achievements() throws InstanceClassMismatchException{
		
		String label = (String)handler.transform(new Object(), "", "AchievementType", "publications");
		assertEquals( "Publications", label );
		
		label = (String)handler.transform(new Object(), "", "AchievementType", "honors_awards");
		assertEquals("Honours and awards", label);
	}
}
