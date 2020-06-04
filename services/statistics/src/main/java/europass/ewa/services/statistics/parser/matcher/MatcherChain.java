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
package europass.ewa.services.statistics.parser.matcher;

import europass.ewa.services.statistics.structures.QueryEntityMatch;
import europass.ewa.services.statistics.structures.QueryProperties;

/**
 * MatcherChain
 * Implementation of chain that uses implementations of the QueryEntityMatcher to match entities versus the parameters given in the following order:
 * - 13 preset queries concerning the Europass Statistics Reports
 * 
 * On failed matching of the preset queries, these are the matchings depending on the parameters given: 
 * - CUBE_ENTRY_NAT_LANGS
 * - CUBE_ENTRY_LANGS
 * - CUBE_ENTRY_DOCS_LANGS
 * - CUBE_ENTRY_GENDER
 * - CUBE_ENTRY_DOCS
 * - CUBE_ENTRY
 * 
 * @author pgia
 *
 */
public class MatcherChain {

	public static void execute(QueryEntityMatch matcher, QueryProperties queryProperties) {

//		LinguisticNationalitiesQueryEntityMatcher languagesNationalities = new LinguisticNationalitiesQueryEntityMatcher(null);
/*		CubesQueryEntityMatcher languagesNationalities = new CubesQueryEntityMatcher(null);
		Query13EntityMatcher thirteenth = new Query13EntityMatcher(languagesNationalities);
		Query12EntityMatcher twelveth = new Query12EntityMatcher(thirteenth);
		Query11EntityMatcher eleventh = new Query11EntityMatcher(twelveth);
		Query10EntityMatcher tenth = new Query10EntityMatcher(eleventh);
		Query9EntityMatcher ninth = new Query9EntityMatcher(tenth);
		Query8EntityMatcher eighth = new Query8EntityMatcher(ninth);
		Query7EntityMatcher seventh = new Query7EntityMatcher(eighth);
		Query6EntityMatcher sixth = new Query6EntityMatcher(seventh);
		Query5EntityMatcher fifth = new Query5EntityMatcher(sixth);
		Query4EntityMatcher fourth = new Query4EntityMatcher(fifth);
		Query3EntityMatcher third = new Query3EntityMatcher(fourth);
		Query2EntityMatcher second = new Query2EntityMatcher(third);
		Query1EntityMatcher first = new Query1EntityMatcher(second);*/

		CubesQueryEntityMatcher languagesNationalities = new CubesQueryEntityMatcher(null);
		WorkExperienceQueryEntityMatcher workExperience = new WorkExperienceQueryEntityMatcher(languagesNationalities);
		AgeQueryEntityMatcher age = new AgeQueryEntityMatcher(workExperience);
		Query20EntityMatcher twentieth = new Query20EntityMatcher(age);
		Query13EntityMatcher thirteenth = new Query13EntityMatcher(twentieth);
		//Query12EntityMatcher twelveth = new Query12EntityMatcher(thirteenth);
		//Query11EntityMatcher eleventh = new Query11EntityMatcher(twelveth);
		Query10EntityMatcher tenth = new Query10EntityMatcher(thirteenth);
		Query9EntityMatcher ninth = new Query9EntityMatcher(tenth);
		Query8EntityMatcher eighth = new Query8EntityMatcher(ninth);
		//Query7EntityMatcher seventh = new Query7EntityMatcher(eighth);
		Query6EntityMatcher sixth = new Query6EntityMatcher(eighth);
		Query5EntityMatcher fifth = new Query5EntityMatcher(sixth);
		Query4EntityMatcher fourth = new Query4EntityMatcher(fifth);
		//Query3EntityMatcher third = new Query3EntityMatcher(fourth);
		//Query2EntityMatcher second = new Query2EntityMatcher(third);
		Query1EntityMatcher first = new Query1EntityMatcher(fourth);
		
		first.dispatch(matcher, queryProperties);
	}
}
