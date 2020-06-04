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

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.social.linkedin.api.Education;

import com.google.inject.Guice;
import com.google.inject.Injector;

import europass.ewa.services.social.InstanceClassMismatchException;
import europass.ewa.services.social.linkedin.EducationalFieldHandler;
import europass.ewa.services.social.linkedin.LinkedInModule;

public class EducationalFieldHandlerTest{

	private static Injector injector = null;
	
	private static EducationalFieldHandler handler = null;

	public EducationalFieldHandlerTest(){
		injector = Guice.createInjector(
				new LinkedInModule()
				);
		
		handler = injector.getInstance(EducationalFieldHandler.class);
	}
	
	
	@Test
	public void test() throws InstanceClassMismatchException{
		
		List<Education> from = new ArrayList<>();
		
		from.add( new Education( "activities", "MSc", "Psychology", "1", null, null, null, null ) );
		from.add( new Education( "activities", "MSc", null, "2", null, null, null, null ) );
		from.add( new Education( "activities", null, "Psychology", "3", null, null, null, null ) );
		from.add( new Education( "activities", "MSc", "", "4", null, null, null, null ) );
		from.add( new Education( "activities", "", "Psychology", "5", null, null, null, null ) );
		
		String[] titles = {
			"MSc, Psychology",
			"MSc",
			"Psychology",
			"MSc",
			"Psychology"
		};
		
		Assert.assertTrue(from.size() == 5);
		
		for ( int i = 0; i < from.size(); i ++ ){
			Education item = from.get( i );
			
			String actual = (String)handler.transform(item, "");
			
			String expected = titles[ i ];
			
			Assert.assertThat( "Europass Title for Edu '"+i+"'", actual, CoreMatchers.is( expected ) );
		}
		
	}
}
