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

import java.util.ArrayList;
import java.util.List;

import europass.ewa.services.statistics.enums.request.ParameterNames;
import europass.ewa.services.statistics.enums.tables.EntityTablesProperties;
import europass.ewa.services.statistics.structures.QueryEntityMatch;
import europass.ewa.services.statistics.structures.QueryProperties;

public class Query5EntityMatcher extends QueryEntityMatcher{

	public Query5EntityMatcher(QueryEntityMatcher next){
		super(next);
	}
	
	@Override
	protected void configure(){
		
		List<ParameterNames> parametersList = new ArrayList<ParameterNames>();
		parametersList.add(ParameterNames.OLANGUAGE);
		parametersList.add(ParameterNames.DATE);
		super.setMatch(new QueryEntityMatch(EntityTablesProperties.CUBE_ENTRY_FLANG_PIVOT, parametersList));
	}
	
	@Override
	protected void populateQueryEntity(QueryEntityMatch matcher, QueryProperties queryProperties){
		matcher.setEntityProperties(EntityTablesProperties.CUBE_ENTRY_FLANG_PIVOT);
	}
}
