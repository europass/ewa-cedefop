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

import java.util.Map;
import java.util.Set;

import com.google.inject.Inject;

import europass.ewa.services.statistics.enums.request.ParameterNames;
import europass.ewa.services.statistics.enums.validation.ValidationErrors;
import europass.ewa.services.statistics.mappings.structures.ParameterValueTypesMappings;
import europass.ewa.services.statistics.structures.ValidationResult;
import europass.ewa.services.statistics.validators.Validator;

public class ParametersValueEnumsValidator implements Validator <Map<String,String>, String, String, String> {

	ParameterValueTypesMappings mappings;
	
	@Inject
	public ParametersValueEnumsValidator(ParameterValueTypesMappings mappings){
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

		Set<String> parameterNames = paramsValuesMap.keySet();
		for(String name : parameterNames){

			ParameterNames paramMatched = ParameterNames.match(name);
			
			if(paramMatched.equals(ParameterNames.INVALID)){
				return new ValidationResult.Builder(false)
													.withValidationErrors(ValidationErrors.QUERY_PARAMETER_INVALID)
													.withFailedOn(name)
													.build();
			}
			
			String value = paramsValuesMap.get(name);
			if(value.equals(""))
				continue;
			
			// Replace any ! in case of NOT and any , in case of RANGE (iterrate)
			
			String strippedNotValue = value.replaceAll("!", "");
			
			String delimiter = strippedNotValue.contains(",") ? "," : "\\+";
			String[] rangedValue = strippedNotValue.split(delimiter);
			
			for(String clearValue : rangedValue){
				if(name.equals(ParameterNames.DOCUMENT_TYPE.getDescription())){
					if(!ParameterNames.documentTypeEnumMatcher(clearValue)){
						return new ValidationResult.Builder(false)
														.withValidationErrors(ValidationErrors.QUERY_PARAMETER_VALUE_ENUM_MISMATCH)
														.withFailedOn(name)
														.withAgainst(value)
														.build();
					}
				}
				if(name.equals(ParameterNames.DOCUMENT_FORMAT.getDescription())){
					if(!ParameterNames.documentFormatEnumMatcher(clearValue)){
						return new ValidationResult.Builder(false)
														.withValidationErrors(ValidationErrors.QUERY_PARAMETER_VALUE_ENUM_MISMATCH)
														.withFailedOn(name)
														.withAgainst(value)
														.build();
					}
				}
				if(name.equals(ParameterNames.DOCUMENT.getDescription())){
					if(!ParameterNames.documentEnumMatcher(clearValue)){
						return new ValidationResult.Builder(false)
														.withValidationErrors(ValidationErrors.QUERY_PARAMETER_VALUE_ENUM_MISMATCH)
														.withFailedOn(name)
														.withAgainst(value)
														.build();
					}
				}
				if(name.equals(ParameterNames.EXAMPLES_FORMAT.getDescription())){
					if(!ParameterNames.examplesFormatEnumMatcher(clearValue)){
						return new ValidationResult.Builder(false)
														.withValidationErrors(ValidationErrors.QUERY_PARAMETER_VALUE_ENUM_MISMATCH)
														.withFailedOn(name)
														.withAgainst(value)
														.build();
					}
				}
				if(name.equals(ParameterNames.GENDER.getDescription())){
					if(!ParameterNames.GenderEnumMatcher(clearValue)){
						return new ValidationResult.Builder(false)
														.withValidationErrors(ValidationErrors.QUERY_PARAMETER_VALUE_ENUM_MISMATCH)
														.withFailedOn(name)
														.withAgainst(value)
														.build();
					}
				}
			}
		}
		
		return new ValidationResult.Builder(true).build();
	}
}
