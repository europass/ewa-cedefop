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
package europass.ewa.services.statistics.validators;

import java.util.Map;
import java.util.Set;

import europass.ewa.services.statistics.enums.request.ParameterNames;
import europass.ewa.services.statistics.enums.validation.ValidationErrors;
import europass.ewa.services.statistics.structures.ValidationResult;

public class ParametersValidator implements Validator <Map<String,String>, String, String, String> {

	@Override
	public ValidationResult validate(String s) {
		return new ValidationResult.Builder(true).build();
	}

	@Override
	public ValidationResult validateAll(Set<String> params) {
		
		for(String name : params){
		
			ParameterNames paramMatched = ParameterNames.match(name);
			
			if(paramMatched.equals(ParameterNames.INVALID)){
				return new ValidationResult.Builder(false)
													.withValidationErrors(ValidationErrors.QUERY_PARAMETER_INVALID)
													.withFailedOn(name)
													.build();
			}
							
		}
		
		return new ValidationResult.Builder(true).build();
	}

	@Override
	public ValidationResult validateAllAgainst(Set<String> value, String against) {
		return new ValidationResult.Builder(true).build();
	}

	@Override
	public ValidationResult validateAllAgainstValues(Map<String, String> map) {
		return new ValidationResult.Builder(true).build();
	}
}
