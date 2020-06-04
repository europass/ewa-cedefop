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
import europass.ewa.services.statistics.enums.request.ValueTypes;

@Singleton
public class ParameterValueTypesMappings implements ValidationMappings <ParameterNames, ValueTypes, List<ValueTypes>, Map<ParameterNames, List<ValueTypes>>> {
	
	private Map<ParameterNames, List<ValueTypes>> mappings;
	
	ParameterValueTypesMappings(){
		configure();
	}
	
	@Override
	public Map<ParameterNames, List<ValueTypes>> getMappings(){
		return mappings;
	}

	@Override
	public List<ValueTypes> getMappingsFor(ParameterNames name) {
		return mappings.get(name);
	}
	
	protected void configure(){
		
		mappings = new HashMap<ParameterNames, List<ValueTypes>>();
		EnumSet<ParameterNames> parameters = ParameterNames.getSet();
		
		for( ParameterNames parameterEnum : parameters){
			
			List<ValueTypes> valueTypesAllowedList = new ArrayList<ValueTypes>();
		
			if( parameterEnum.equals(ParameterNames.DOCUMENT_TYPE) 
					|| parameterEnum.equals(ParameterNames.DOCUMENT_FORMAT) 
					|| parameterEnum.equals(ParameterNames.DOCUMENT) 
					|| parameterEnum.equals(ParameterNames.EXAMPLES_FORMAT) 
					|| parameterEnum.equals(ParameterNames.COUNTRY)
					|| parameterEnum.equals(ParameterNames.LANGUAGE)
					|| parameterEnum.equals(ParameterNames.MLANGUAGE)
					|| parameterEnum.equals(ParameterNames.OLANGUAGE)
					|| parameterEnum.equals(ParameterNames.NATIONALITY)
					|| parameterEnum.equals(ParameterNames.ONATIONALITY)
					|| parameterEnum.equals(ParameterNames.GENDER) 
			){
				valueTypesAllowedList.add(ValueTypes.VALUE);
				valueTypesAllowedList.add(ValueTypes.VALUE_OR);
				valueTypesAllowedList.add(ValueTypes.VALUE_NOT);
				valueTypesAllowedList.add(ValueTypes.VALUE_AND);
			}

			// In the case of the GROUP_BY_PARAM_ORDER we assign only string value  -we validate later
			if( parameterEnum.equals(ParameterNames.GROUP_BY)
					|| parameterEnum.equals(ParameterNames.ORDER_BY)
					|| parameterEnum.equals(ParameterNames.UNIQUE_USERS)
			){
				valueTypesAllowedList.add(ValueTypes.VALUE);
			}
			
			if( parameterEnum.equals(ParameterNames.DATE) ){
				valueTypesAllowedList.add(ValueTypes.DATE);
				valueTypesAllowedList.add(ValueTypes.DATE_RANGE);
				valueTypesAllowedList.add(ValueTypes.DATE_OR);
				valueTypesAllowedList.add(ValueTypes.DATE_NOT);
				valueTypesAllowedList.add(ValueTypes.DATE_AND);
			}
					
			if( parameterEnum.equals(ParameterNames.AGE)
					|| parameterEnum.equals(ParameterNames.WORK_EXPERIENCE)
					|| parameterEnum.equals(ParameterNames.EDUCATION_YEARS) 
			){
				valueTypesAllowedList.add(ValueTypes.NUMBER);
				valueTypesAllowedList.add(ValueTypes.NUMBER_RANGE);
				valueTypesAllowedList.add(ValueTypes.NUMBER_OR);
				valueTypesAllowedList.add(ValueTypes.NUMBER_NOT);
				valueTypesAllowedList.add(ValueTypes.NUMBER_AND);
			}

			if( parameterEnum.equals(ParameterNames.TOP) ){
				valueTypesAllowedList.add(ValueTypes.NUMBER);
			}
			
			mappings.put(parameterEnum,valueTypesAllowedList);
		}
	}
}
