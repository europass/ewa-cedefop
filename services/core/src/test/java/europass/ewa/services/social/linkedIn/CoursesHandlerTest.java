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
import java.util.Locale;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import europass.ewa.model.Achievement;
import europass.ewa.services.social.ExtraDataMockObjects;
import europass.ewa.services.social.InstanceClassMismatchException;
import europass.ewa.services.social.linkedin.CoursesHandler;
import europass.ewa.services.social.linkedin.DateConfig;
import europass.ewa.services.social.linkedin.LinkedInModule;

public class CoursesHandlerTest<A extends Object, B extends Object> {

	private static Injector injector = null;
	private static CoursesHandler handler = null;
	
	public CoursesHandlerTest(){
		injector = Guice.createInjector(
				new LinkedInModule()
				);
		
		handler = injector.getInstance(CoursesHandler.class);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void test() throws InstanceClassMismatchException{

		List<Achievement> to = new ArrayList<Achievement>();
		
		to = (List<Achievement>)handler.transform(ExtraDataMockObjects.coursesHashMapObject(), to, "SocialAchievementType", "courses", new Locale("el"), new DateConfig());
		
		Assert.assertTrue(to.size() == 1);
		
		Achievement toItem = to.get(0);
		
		Assert.assertThat("Title Label", toItem.getTitle().getLabel(), CoreMatchers.is( "Μαθήματα" ));
		
		String expected = ExtraDataMockObjects.COURSES_HTML_EXPECTED;
		
		Assert.assertThat("Description", toItem.getDescription(), CoreMatchers.is( expected ));
	}
}
