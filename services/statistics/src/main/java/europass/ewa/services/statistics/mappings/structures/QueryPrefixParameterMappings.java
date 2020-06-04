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
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Singleton;

import europass.ewa.services.statistics.enums.request.ParameterNames;
import europass.ewa.services.statistics.enums.request.QueryPrefixes;

@Singleton
public class QueryPrefixParameterMappings implements ValidationMappings <ParameterNames, QueryPrefixes, List<QueryPrefixes>, Map<ParameterNames, List<QueryPrefixes>>> {
	
	private Map<ParameterNames, List<QueryPrefixes>> mappings;
	
	QueryPrefixParameterMappings(){
		configure();
	}
	
	@Override
	public Map<ParameterNames, List<QueryPrefixes>> getMappings(){
		return mappings;
	}

	@Override
	public List<QueryPrefixes> getMappingsFor(ParameterNames name) {
		return mappings.get(name);
	}
	
	protected void configure(){
		
		mappings = new HashMap<ParameterNames, List<QueryPrefixes>>();
		EnumSet<ParameterNames> parameters = ParameterNames.getSet();
		
		for (ParameterNames parameterEnum : parameters) {

			List<QueryPrefixes> queryPrefixesAllowedList = new ArrayList<QueryPrefixes>();

			if (parameterEnum.equals(ParameterNames.DOCUMENT_FORMAT)
				|| parameterEnum.equals(ParameterNames.DOCUMENT)
				|| parameterEnum.equals(ParameterNames.EXAMPLES_FORMAT)) {

				queryPrefixesAllowedList.add(QueryPrefixes.DOWNLOADS);
				queryPrefixesAllowedList.add(QueryPrefixes.VISITS);
			} else if (parameterEnum.equals(ParameterNames.DATE)
				|| parameterEnum.equals(ParameterNames.COUNTRY)
				|| parameterEnum.equals(ParameterNames.LANGUAGE)
				|| parameterEnum.equals(ParameterNames.ORDER_BY)
				|| parameterEnum.equals(ParameterNames.TOP)
				|| parameterEnum.equals(ParameterNames.GROUP_BY)) {

				queryPrefixesAllowedList.add(QueryPrefixes.GENERATED);
				queryPrefixesAllowedList.add(QueryPrefixes.DOWNLOADS);
				queryPrefixesAllowedList.add(QueryPrefixes.VISITS);
			} else {
				queryPrefixesAllowedList.add(QueryPrefixes.GENERATED);
			}

			mappings.put(parameterEnum, queryPrefixesAllowedList);
		}
	}
}
