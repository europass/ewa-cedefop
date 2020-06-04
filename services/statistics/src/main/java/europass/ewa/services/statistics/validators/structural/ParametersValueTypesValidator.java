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
package europass.ewa.services.statistics.validators.structural;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.inject.Inject;

import europass.ewa.services.statistics.enums.request.ParameterNames;
import europass.ewa.services.statistics.enums.request.ValueTypes;
import europass.ewa.services.statistics.enums.validation.ValidationErrors;
import europass.ewa.services.statistics.mappings.structures.ParameterValueTypesMappings;
import europass.ewa.services.statistics.structures.ValidationResult;
import europass.ewa.services.statistics.validators.Validator;

public class ParametersValueTypesValidator implements Validator <Map<String,String>, String, String, String> {

	ParameterValueTypesMappings mappings;
	
	@Inject
	public ParametersValueTypesValidator(ParameterValueTypesMappings mappings){
		this.mappings = mappings;
	}

	@Override
	public ValidationResult validate(String value) {
		return new ValidationResult.Builder(true).build();
	}

	@Override
	public ValidationResult validateAll(Set<String> params) {
		return new ValidationResult.Builder(true).build();
	}

	@Override
	public ValidationResult validateAllAgainst(Set<String> values, String against) {
		return new ValidationResult.Builder(true).build();
	}

	@Override
	public ValidationResult validateAllAgainstValues(Map<String, String> paramsValuesMap) {

		for(String name : paramsValuesMap.keySet()){
			
			ParameterNames paramMatched = ParameterNames.match(name);
			
			if(paramMatched.equals(ParameterNames.INVALID)){
				return new ValidationResult.Builder(false)
													.withValidationErrors(ValidationErrors.QUERY_PARAMETER_INVALID)
													.withFailedOn(name)
													.build();
			}
			
			String value = paramsValuesMap.get(name);
			
			List<ValueTypes> valueTypesAllowed = mappings.getMappingsFor(paramMatched);
			
			if(!value.equals("")){
				
				ValueTypes valueTypesMatched = ValueTypes.match(value);
					
				if(valueTypesAllowed.isEmpty() || !valueTypesAllowed.contains(valueTypesMatched)){
					return new ValidationResult.Builder(false)
														.withValidationErrors(ValidationErrors.QUERY_PARAMETER_VALUE_TYPE_MISMATCH)
														.withFailedOn(name)
														.withValueType(valueTypesMatched.name())
														.withAgainst(value)
														.build();
				}
			}
		}
	
		return new ValidationResult.Builder(true).build();
	}


}
