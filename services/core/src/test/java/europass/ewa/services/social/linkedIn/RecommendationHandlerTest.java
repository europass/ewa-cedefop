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
import org.junit.Test;
import org.junit.Assert;
import org.springframework.social.linkedin.api.LinkedInProfile;
import org.springframework.social.linkedin.api.Recommendation;
import org.springframework.social.linkedin.api.Recommendation.RecommendationType;

import com.google.inject.Guice;
import com.google.inject.Injector;

import europass.ewa.model.Achievement;
import europass.ewa.services.social.InstanceClassMismatchException;
import europass.ewa.services.social.linkedin.LinkedInModule;
import europass.ewa.services.social.linkedin.RecommendationHandler;

public class RecommendationHandlerTest{

	private static Injector injector = null;
	
	private static RecommendationHandler handler = null;
	
	public RecommendationHandlerTest(){
		injector = Guice.createInjector(
				new LinkedInModule()
				);
		
		handler = injector.getInstance(RecommendationHandler.class);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void test() throws InstanceClassMismatchException{
		
		List<Recommendation> from = new ArrayList<>();
		
		LinkedInProfile recommender1 = new LinkedInProfile("1", "John", "Smith", null, null, null, null, null);
		Recommendation recommend1 = new Recommendation("1","Recom","I recommend him",(RecommendationType)RecommendationType.COLLEAGUE,recommender1,null);

		LinkedInProfile recommender2 = new LinkedInProfile("2", "Dexter", "Morgan", null, null, null, null, null);
		Recommendation recommend2 = new Recommendation("2","Recom2","I do recommend him strongly",(RecommendationType)RecommendationType.SERVICE_PROVIDER,recommender2,null);
		
		from.add(recommend1);
		from.add(recommend2);
		
		Assert.assertTrue(from.size() == 2);
		
		List<Achievement> to = new ArrayList<>();
		
		to = (List<Achievement>)handler.transform(from, to, "AchievementType", "recommendations");
		
		Assert.assertTrue(to.size() == 1);
		
		Achievement toItem = to.get(0);
		String expected = 
			"<p><strong>John Smith</strong> | <em>COLLEAGUE</em></p>" +
			"<p>I recommend him</p><p><strong>Dexter Morgan</strong> | <em></em></p>" +
			"<p>I do recommend him strongly</p>";
		
		Assert.assertThat("Description", toItem.getDescription(), CoreMatchers.is( expected ));
		
	}
}
