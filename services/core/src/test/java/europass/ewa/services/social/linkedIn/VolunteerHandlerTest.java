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

import com.google.inject.Guice;
import com.google.inject.Injector;

import europass.ewa.model.WorkExperience;
import europass.ewa.services.social.ExtraDataMockObjects;
import europass.ewa.services.social.InstanceClassMismatchException;
import europass.ewa.services.social.linkedin.LinkedInModule;
import europass.ewa.services.social.linkedin.VolunteerHandler;

public class VolunteerHandlerTest {

	private static Injector injector = null;
	private static VolunteerHandler handler = null;
	
	public VolunteerHandlerTest(){
		injector = Guice.createInjector(
				new LinkedInModule()
				);
		
		handler = injector.getInstance(VolunteerHandler.class);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void test() throws InstanceClassMismatchException{

		List<WorkExperience> to = new ArrayList<WorkExperience>();
		
		to = (List<WorkExperience>)handler.transform(ExtraDataMockObjects.volunteerHashMapObject(), to);
		
		Assert.assertTrue(to.size() == 2);
		
		WorkExperience toItem = to.get(0);
		
		Assert.assertThat("Position", toItem.getPosition().getLabel(), CoreMatchers.is( "Enviromentalist specialized on sea wales protection" ));
		Assert.assertThat("Organization", toItem.getEmployer().getName(), CoreMatchers.is( "Green peace" ));
	}
}
