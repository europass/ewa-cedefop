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
package europass.ewa.services.statistics.mappings.structures;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Singleton;

import europass.ewa.services.statistics.enums.request.ParameterNames;
import europass.ewa.services.statistics.enums.tables.EntityTablesProperties;
import europass.ewa.services.statistics.structures.NumberValueRange;
import europass.ewa.services.statistics.structures.QueryEntityMatch;

@Singleton
public class QueryEntityMatchings {

	private List<QueryEntityMatch> matches;
	
	private Map<EntityTablesProperties,List<NumberValueRange>> matchingRangeValues;
	
	public QueryEntityMatchings() {
		configure();
	}
	
	public List<QueryEntityMatch> getMatches(){
		return matches;
	}

	public Map<EntityTablesProperties,List<NumberValueRange>> getMatchingRangeValues(){
		return matchingRangeValues;
	}
	
	private void configure(){
		matches = new ArrayList<QueryEntityMatch>();
		matchingRangeValues = new HashMap<EntityTablesProperties, List<NumberValueRange>>();
				
		List<ParameterNames> parametersList = new ArrayList<ParameterNames>();

		// 1
		parametersList.add(ParameterNames.DOCUMENT_TYPE);
		parametersList.add(ParameterNames.DATE);
		matches.add(new QueryEntityMatch(EntityTablesProperties.CUBE_ENTRY_DOCS, parametersList));
		
		// 2
		parametersList = new ArrayList<ParameterNames>();
		parametersList.add(ParameterNames.WORK_EXPERIENCE);
		parametersList.add(ParameterNames.DATE);
		matches.add(new QueryEntityMatch(EntityTablesProperties.CUBE_ENTRY_WORKEXP, parametersList));
		
		// Add predefined range values of Integer
		List<NumberValueRange> workExperienceRangesList = new ArrayList<NumberValueRange>();
		workExperienceRangesList.add(new NumberValueRange(0,2));
		workExperienceRangesList.add(new NumberValueRange(3,5));
		workExperienceRangesList.add(new NumberValueRange(6,10));
		workExperienceRangesList.add(new NumberValueRange(11,20));
		workExperienceRangesList.add(new NumberValueRange(21,100));
		matchingRangeValues.put(EntityTablesProperties.CUBE_ENTRY_WORKEXP, workExperienceRangesList);
		
		// 3
		parametersList = new ArrayList<ParameterNames>();
		parametersList.add(ParameterNames.AGE);
		parametersList.add(ParameterNames.DATE);
		matches.add(new QueryEntityMatch(EntityTablesProperties.CUBE_ENTRY_AGE, parametersList));
		
		// Add predefined range values of Integer
		List<NumberValueRange> ageRangesList = new ArrayList<NumberValueRange>();
		ageRangesList.add(new NumberValueRange(0,20));
		ageRangesList.add(new NumberValueRange(21,25));
		ageRangesList.add(new NumberValueRange(26,30));
		ageRangesList.add(new NumberValueRange(31,35));
		ageRangesList.add(new NumberValueRange(36,100));
		matchingRangeValues.put(EntityTablesProperties.CUBE_ENTRY_AGE, ageRangesList);
		
		// 4
		parametersList = new ArrayList<ParameterNames>();
		parametersList.add(ParameterNames.GENDER);
		parametersList.add(ParameterNames.DATE);
		matches.add(new QueryEntityMatch(EntityTablesProperties.CUBE_ENTRY_GENDER, parametersList));
		
		// 5
		parametersList = new ArrayList<ParameterNames>();
		parametersList.add(ParameterNames.OLANGUAGE);
		parametersList.add(ParameterNames.DATE);
		matches.add(new QueryEntityMatch(EntityTablesProperties.CUBE_ENTRY_FLANG_COUNTER, parametersList));

		// 6
		parametersList = new ArrayList<ParameterNames>();
		parametersList.add(ParameterNames.DOCUMENT_TYPE);
		parametersList.add(ParameterNames.LANGUAGE);
		parametersList.add(ParameterNames.DATE);
		matches.add(new QueryEntityMatch(EntityTablesProperties.CUBE_ENTRY_DOCS_LANGS, parametersList));

		// 7
		parametersList = new ArrayList<ParameterNames>();
		parametersList.add(ParameterNames.DOCUMENT_TYPE);
		parametersList.add(ParameterNames.NATIONALITY);
		parametersList.add(ParameterNames.DATE);
		matches.add(new QueryEntityMatch(EntityTablesProperties.CUBE_ENTRY_NAT_RANK, parametersList));

		// 8
		parametersList = new ArrayList<ParameterNames>();
		parametersList.add(ParameterNames.DOCUMENT_TYPE);
		parametersList.add(ParameterNames.MLANGUAGE);
		parametersList.add(ParameterNames.DATE);
		matches.add(new QueryEntityMatch(EntityTablesProperties.CUBE_ENTRY_MLANG, parametersList));

		// 9
		parametersList = new ArrayList<ParameterNames>();
		parametersList.add(ParameterNames.DOCUMENT_TYPE);
		parametersList.add(ParameterNames.OLANGUAGE);
		parametersList.add(ParameterNames.DATE);
		matches.add(new QueryEntityMatch(EntityTablesProperties.CUBE_ENTRY_FLANG_SHORT, parametersList));
		
		// 10
		parametersList = new ArrayList<ParameterNames>();
		parametersList.add(ParameterNames.DOCUMENT_TYPE);
		parametersList.add(ParameterNames.COUNTRY);
		parametersList.add(ParameterNames.DATE);
		matches.add(new QueryEntityMatch(EntityTablesProperties.CUBE_ENTRY_SHORT, parametersList));

		// 11
		parametersList = new ArrayList<ParameterNames>();
		parametersList.add(ParameterNames.DOCUMENT_TYPE);
		parametersList.add(ParameterNames.COUNTRY);
		parametersList.add(ParameterNames.AGE);
		parametersList.add(ParameterNames.DATE);
		matches.add(new QueryEntityMatch(EntityTablesProperties.CUBE_ENTRY_AGE, parametersList));

		// 12
		parametersList = new ArrayList<ParameterNames>();
		parametersList.add(ParameterNames.DOCUMENT_TYPE);
		parametersList.add(ParameterNames.COUNTRY);
		parametersList.add(ParameterNames.WORK_EXPERIENCE);
		parametersList.add(ParameterNames.DATE);
		matches.add(new QueryEntityMatch(EntityTablesProperties.CUBE_ENTRY_WORKEXP, parametersList));
	
		// 13
		parametersList = new ArrayList<ParameterNames>();
		parametersList.add(ParameterNames.DOCUMENT_TYPE);
		parametersList.add(ParameterNames.COUNTRY);
		parametersList.add(ParameterNames.GENDER);
		parametersList.add(ParameterNames.DATE);
		matches.add(new QueryEntityMatch(EntityTablesProperties.CUBE_ENTRY_GENDER, parametersList));
	}
}
