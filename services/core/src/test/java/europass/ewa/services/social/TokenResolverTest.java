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
package europass.ewa.services.social;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;

import org.apache.commons.cli.ParseException;
import org.junit.BeforeClass;
import org.junit.Test;

import europass.ewa.model.Identification;
import europass.ewa.model.LearnerInfo;
import europass.ewa.model.SkillsPassport;
import europass.ewa.model.reflection.ReflectiveLoadingException;
import europass.ewa.services.social.PathTokenizer;
import europass.ewa.services.social.Token;

public class TokenResolverTest {

	private static SkillsPassport esp ;
	
	@BeforeClass
	public static void prepare(){
		
		esp = MockObjects.esp();
	}
	
	@Test
	public void current() throws ParseException{
		String p = "";
		
		PathTokenizer tokenizer = PathTokenizer.compile( p );
		
		Object current = esp.getLearnerInfo().getIdentification();
		Token token = tokenizer.resolve( esp, current);
		
		assertEquals( "Must return current", token.getObj(), current );
	}
	
	@Test
	public void root() throws ParseException{
		String p = "/";
		
		PathTokenizer tokenizer = PathTokenizer.compile( p );
		
		Token token = tokenizer.resolve( esp, esp.getLearnerInfo().getIdentification());
		
		assertEquals( "Must return root", token.getObj(), esp );
	}
	
	@Test
	public void learner() throws ParseException{
		
		PathTokenizer tokenizer = PathTokenizer.compile( MockObjects.LEARNERINFO );
		
		Token token = tokenizer.resolve( esp, esp );
		
		assertEquals( "Must return LearnerInfo: ", token.getObj(), esp.getLearnerInfo() );
	}
	
	@Test
	public void learnerWhenNull() throws ParseException{
		
		PathTokenizer tokenizer = PathTokenizer.compile( MockObjects.LEARNERINFO );
		
		Token token = tokenizer.resolve( null, null );
		assertNull( "Must be null: ", token.getObj() );
	}
	
	@Test
	public void learnerBelowRoot() throws ParseException{
		
		String p = "/"+MockObjects.LEARNERINFO;
		
		PathTokenizer tokenizer = PathTokenizer.compile( p );
		
		Token token = tokenizer.resolve( esp, esp.getLearnerInfo().getIdentification() );
		
		assertEquals( "Must be LearnerInfo: ", token.getObj().getClass(), LearnerInfo.class );
		
		assertEquals( "LearnerInfo: ", token.getObj(), esp.getLearnerInfo() );
	}
	
	@Test
	public void learnerBelowNewRoot() throws ParseException{
		
		String p = "/"+MockObjects.LEARNERINFO;
		
		PathTokenizer tokenizer = PathTokenizer.compile( p );
		
		SkillsPassport newEsp = new SkillsPassport();
				
		Token token = tokenizer.resolve( newEsp, newEsp );
		
		assertEquals( "Must be LearnerInfo: ", token.getObj().getClass(), LearnerInfo.class );
		
		assertNotEquals( "LearnerInfo: ", token.getObj(), esp.getLearnerInfo() );
	}
	
	@Test
	public void learnerBelowCurrent() throws ParseException{
		String p = "./"+MockObjects.LEARNERINFO;
		
		PathTokenizer tokenizer = PathTokenizer.compile( p );
		
		Token token = tokenizer.resolve( esp, esp );
		
		assertEquals( "Must be LearnerInfo: ", token.getObj().getClass(), LearnerInfo.class );
		
		assertEquals( "LearnerInfo: ", token.getObj(), esp.getLearnerInfo() );
	}
	
	@Test(expected=ReflectiveLoadingException.class)
	public void learnerBelowWrongCurrent() throws ParseException{
		String p = "./"+MockObjects.LEARNERINFO;
		
		PathTokenizer tokenizer = PathTokenizer.compile( p );
		
		Token token = tokenizer.resolve( esp, esp.getLearnerInfo().getIdentification() );
		
		assertEquals( "Must be LearnerInfo: ", token.getObj().getClass(), LearnerInfo.class );
		
		assertEquals( "LearnerInfo: ", token.getObj(), esp.getLearnerInfo() );
	}
	
	@Test
	public void identificationUnderRoot() throws ParseException{
		String p = "/"+MockObjects.LEARNERINFO+"/"+MockObjects.IDENTIFICATION;
		
		PathTokenizer tokenizer = PathTokenizer.compile( p );
		
		Token token = tokenizer.resolve( esp, esp.getLearnerInfo() );
		
		assertEquals( "Must be Identification: ", token.getObj().getClass(), Identification.class );
		
		assertEquals( "Identification: ", token.getObj(), esp.getLearnerInfo().getIdentification() );
	}
	
	@Test
	public void identificationUnderRootNEW() throws ParseException{
		String p = "/"+MockObjects.LEARNERINFO+"/"+MockObjects.IDENTIFICATION;
		
		PathTokenizer tokenizer = PathTokenizer.compile( p );
		
		SkillsPassport newEsp = new SkillsPassport();
		
		Token token = tokenizer.resolve( newEsp, newEsp.getLearnerInfo() );
		
		assertEquals( "Must be Identification: ", token.getObj().getClass(), Identification.class );
		
		assertEquals( "Identification: ", token.getObj(), newEsp.getLearnerInfo().getIdentification() );
	}
	
	@Test
	public void nameUnderCurrent() throws ParseException{
		String p = "./"+MockObjects.PERSONNAME+"/"+MockObjects.FIRSTNAME;
		
		PathTokenizer tokenizer = PathTokenizer.compile( p );
		
		Token token = tokenizer.resolve( esp, esp.getLearnerInfo().getIdentification() );
		
		assertEquals( "Must be String: ", token.getObj().getClass(), String.class );
		
		assertEquals( "Name: ", token.getObj(), MockObjects.NAME );
	}

	@Test
	public void nameUnderRootNew() throws ParseException{
		String p = "/"+MockObjects.LEARNERINFO + "/" + MockObjects.IDENTIFICATION +"/" + MockObjects.PERSONNAME+"/"+MockObjects.FIRSTNAME;
		
		PathTokenizer tokenizer = PathTokenizer.compile( p );
		
		SkillsPassport newEsp = new SkillsPassport();
		
		Token token = tokenizer.resolve( newEsp, newEsp );
		
		assertEquals( "Must be String: ", token.getObj().getClass(), String.class );
		
		assertEquals( "Name: ", token.getObj(), "" );
	}
	
	@Test
	public void yearUnderCurrent() throws ParseException{
		String p = "./"+MockObjects.DEMOGRAPHICS+"/"+MockObjects.BIRTHDATE+"/"+MockObjects.YEAR;
		
		PathTokenizer tokenizer = PathTokenizer.compile( p );
		
		Token token = tokenizer.resolve( esp, esp.getLearnerInfo().getIdentification() );
		
		assertEquals( "Must be int: ", token.getObj().getClass(), Integer.class );
		
		assertEquals( "Year: ", token.getObj(), MockObjects.YEAR_OF_BIRTH );
	}
	
	@Test
	public void educationItem() throws ParseException{
		String p = "./"+MockObjects.EDUCATION;
		
		PathTokenizer tokenizer = PathTokenizer.compile( p );
		
		Token token = tokenizer.resolve( esp, esp.getLearnerInfo());
		
		assertEquals( "Must be List of Education: ", token.getObj().getClass(), ArrayList.class );
		
		assertEquals( "Still two Educations: ", esp.getLearnerInfo().getEducationList().size(), 2 );
		
		
	}
	
	@Test
	public void educationNew() throws ParseException{
		String p = "./"+MockObjects.EDUCATION ;
		
		PathTokenizer tokenizer = PathTokenizer.compile( p );
		
		LearnerInfo info = new LearnerInfo();
		SkillsPassport newEsp = new SkillsPassport();
		newEsp.setLearnerInfo( info );
		
		Token token = tokenizer.resolve( newEsp, newEsp.getLearnerInfo());
		
		assertEquals( "Must be List of Education: ", token.getObj().getClass(), ArrayList.class );
		
		assertEquals( "Empty List of Educations: ", newEsp.getLearnerInfo().getEducationList().size(), 0 );
		
	}
	
	@Test
	public void educationTitle() throws ParseException{
		String p = "./"+MockObjects.EDUCATION + "/" + MockObjects.TITLE;
		
		PathTokenizer tokenizer = PathTokenizer.compile( p );
		
		Token token = tokenizer.resolve( esp, esp.getLearnerInfo());
		
		assertEquals( "Must be String: ", token.getObj().getClass(), String.class );
		
		assertEquals( "Now three Educations: ", esp.getLearnerInfo().getEducationList().size(), 3 );
		
	}
	
	@Test
	public void educationTitleNew() throws ParseException{
		String p = "./"+MockObjects.EDUCATION + "/" + MockObjects.TITLE;
		
		PathTokenizer tokenizer = PathTokenizer.compile( p );
		
		LearnerInfo info = new LearnerInfo();
		SkillsPassport newEsp = new SkillsPassport();
		newEsp.setLearnerInfo( info );
		
		Token token = tokenizer.resolve( newEsp, newEsp.getLearnerInfo());
		
		assertEquals( "Must be String: ", token.getObj().getClass(), String.class );
		
		assertEquals( "Now one Educations with Title: ", newEsp.getLearnerInfo().getEducationList().get( 0 ).getTitle(), "");
		
	}
}
