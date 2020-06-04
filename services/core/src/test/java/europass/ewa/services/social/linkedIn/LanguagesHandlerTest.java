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
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import europass.ewa.model.LinguisticSkill;
import europass.ewa.model.LinguisticSkills;
import europass.ewa.services.social.ExtraDataMockObjects;
import europass.ewa.services.social.InstanceClassMismatchException;
import europass.ewa.services.social.linkedin.LanguageHandler;

public class LanguagesHandlerTest {

	
	@Test
	public void test() throws InstanceClassMismatchException{
		LanguageHandler handler = new LanguageHandler();
		
		LinguisticSkills skills = (LinguisticSkills) handler.transform(ExtraDataMockObjects.languagesHashMapObject(), new LinguisticSkills() );
		List<LinguisticSkill> mother = skills.getMotherTongue();
		List<LinguisticSkill> foreign = skills.getForeignLanguage();
		
		assertTrue(mother.size() == 1);
		assertTrue(foreign.size() == 2);
		
		assertEquals("Greek",mother.get(0).getDescription().getLabel());
		
		assertEquals("English",foreign.get(0).getDescription().getLabel());
		assertEquals("C1",foreign.get(0).getProficiencyLevel().getSpokenInteraction());
		assertEquals("C1",foreign.get(0).getProficiencyLevel().getListening());
		
		assertEquals("French",foreign.get(1).getDescription().getLabel());
		assertEquals("B1",foreign.get(1).getProficiencyLevel().getWriting());
		assertEquals("B1",foreign.get(1).getProficiencyLevel().getSpokenProduction());
		
	}
}