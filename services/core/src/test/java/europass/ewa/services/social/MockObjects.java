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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import europass.ewa.model.Demographics;
import europass.ewa.model.Education;
import europass.ewa.model.Identification;
import europass.ewa.model.JDate;
import europass.ewa.model.LearnerInfo;
import europass.ewa.model.PersonName;
import europass.ewa.model.SkillsPassport;
import europass.ewa.services.social.Transformer;
import europass.ewa.services.social.linkedin.AddPrefixHandler;
import europass.ewa.services.social.linkedin.InstantMessagingHandler;
import europass.ewa.services.social.linkedin.SkillHandler;

public class MockObjects {
	
	public static Set<Transformer> emptyHandlers(){
		return Collections.<Transformer>emptySet();
	}
	
	public static Set<Transformer> availableHandlers(){
		Set<Transformer> h = new HashSet<>();
		h.add( prefixHandler() );
		h.add( imTypeHandler() );
		h.add( skillsHandler() );
		h.add( new MockHandlers.CoursesHandler() );
		return h;
	}
	
	public static Transformer skillsHandler(){
		return new SkillHandler();
	}
	
	public static Transformer prefixHandler(){
		return new AddPrefixHandler();
	}
	
	public static Transformer imTypeHandler(){
		return new InstantMessagingHandler();
	}
	
	/**
	 * Mock SkillsPassport Object
	 * @return
	 */
	public static SkillsPassport esp(){
		
		PersonName name = new PersonName();
		name.setFirstName( NAME );
		
		JDate birthdate = new JDate();
		birthdate.setYear( YEAR_OF_BIRTH );
		
		Demographics dem = new Demographics();
		dem.setBirthdate( birthdate );
		
		Identification id = new Identification();
		id.setPersonName( name );
		id.setDemographics( dem );
		
		Education ed0 = new Education();
		ed0.setTitle( EDU_0_TITLE );
		
		Education ed1 = new Education();
		ed1.setTitle( EDU_1_TITLE );
		
		List<Education> edu = new ArrayList<>();
		edu.add( ed0 );
		edu.add( ed1 );
		
		LearnerInfo learner = new LearnerInfo();
		learner.setIdentification( id );
		learner.setEducationList( edu );
				
		SkillsPassport esp = new SkillsPassport();
		esp.setLearnerInfo( learner );
		
		return esp;
	}

	
	
	static final String LEARNERINFO = "learnerInfo";
	
	static final String EDUCATION = "educationList";
	
	static final String TITLE = "title";
	
	static final String EDU_0_TITLE = "Maths";

	static final String EDU_1_TITLE = "Physics";
	
	static final String IDENTIFICATION = "identification";
	
	static final String DEMOGRAPHICS = "demographics";
	
	static final String BIRTHDATE = "birthdate";
	
	static final String YEAR = "year";
	
	static final String PERSONNAME = "personName";
	
	static final String FIRSTNAME = "firstName";
	
	static final String NAME = "Babis";
	
	static final String SURNAME = "Sougias";

	static final int YEAR_OF_BIRTH = 1984;
	
	static final String IM_MSN = "msn";

	static final String IM_GTALK = "gtalk";
	
	static final String IM_0_NAME = "babis.sougias";
	
	static final String IM_1_NAME = "babis83";
	
	static final String SUMMARY = "Spaw autokinita giati exoun a8inaikes pinakides.";
	
	static final String EDU_0_DEGREE = "Diploma of Mathematics";

	static final String EDU_1_DEGREE = "Diploma of Engineering";
}
