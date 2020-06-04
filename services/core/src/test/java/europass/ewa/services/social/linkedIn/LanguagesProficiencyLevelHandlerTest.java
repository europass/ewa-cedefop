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

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import europass.ewa.model.CEFRLevel;
import europass.ewa.model.LinguisticSkill;
import europass.ewa.services.social.InstanceClassMismatchException;
import europass.ewa.services.social.linkedin.LanguageProficiencyLevelHandler;

public class LanguagesProficiencyLevelHandlerTest {

	private static LanguageProficiencyLevelHandler handler = null;
	private static List<String> languages = null;
	private static List<String> levels = null;
	private static List<LinguisticSkill> foreignSkills = null;
	
	@BeforeClass
	public static void setup(){
		handler = new LanguageProficiencyLevelHandler();
		languages = new ArrayList<>();
		levels = new ArrayList<>();
		foreignSkills = new ArrayList<>();
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void test() throws InstanceClassMismatchException{
		
		languages.add("Italian");
		levels.add("full-professional");

		languages.add("German");
		levels.add("elementary");

		languages.add("English");
		levels.add("professional-working");

		languages.add("French");
		levels.add("limited-working");

		foreignSkills = (List<LinguisticSkill>) handler.transform(languages, foreignSkills, levels);
		
		assertTrue(foreignSkills.size() == 4);
		
		assertEquals("Italian",foreignSkills.get(0).getDescription().getLabel());
		assertEquals("German",foreignSkills.get(1).getDescription().getLabel());
		assertEquals("English",foreignSkills.get(2).getDescription().getLabel());
		assertEquals("French",foreignSkills.get(3).getDescription().getLabel());
		
		CEFRLevel ItalianLevel = foreignSkills.get(0).getProficiencyLevel();
		
		assertEquals(ItalianLevel.getListening(),"C2");
		assertEquals(ItalianLevel.getReading(),"C2");
		assertEquals(ItalianLevel.getSpokenInteraction(),"C2");
		assertEquals(ItalianLevel.getSpokenProduction(),"C2");
		assertEquals(ItalianLevel.getWriting(),"C2");
		
		CEFRLevel GermanLevel = foreignSkills.get(1).getProficiencyLevel();

		assertEquals(GermanLevel.getListening(),"A1");
		assertEquals(GermanLevel.getReading(),"A1");
		assertEquals(GermanLevel.getSpokenInteraction(),"A1");
		assertEquals(GermanLevel.getSpokenProduction(),"A1");
		assertEquals(GermanLevel.getWriting(),"A1");
		
		CEFRLevel EnglishLevel = foreignSkills.get(2).getProficiencyLevel();
		
		assertEquals(EnglishLevel.getListening(),"C1");
		assertEquals(EnglishLevel.getReading(),"C1");
		assertEquals(EnglishLevel.getSpokenInteraction(),"C1");
		assertEquals(EnglishLevel.getSpokenProduction(),"C1");
		assertEquals(EnglishLevel.getWriting(),"C1");
		
		CEFRLevel FrenchLevel = foreignSkills.get(3).getProficiencyLevel();
		
		assertEquals(FrenchLevel.getListening(),"B1");
		assertEquals(FrenchLevel.getReading(),"B1");
		assertEquals(FrenchLevel.getSpokenInteraction(),"B1");
		assertEquals(FrenchLevel.getSpokenProduction(),"B1");
		assertEquals(FrenchLevel.getWriting(),"B1");		
	}
}