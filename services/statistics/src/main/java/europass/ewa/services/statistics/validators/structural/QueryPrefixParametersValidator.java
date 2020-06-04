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
import europass.ewa.services.statistics.enums.request.QueryPrefixes;
import europass.ewa.services.statistics.enums.validation.ValidationErrors;
import europass.ewa.services.statistics.mappings.structures.QueryPrefixParameterMappings;
import europass.ewa.services.statistics.structures.ValidationResult;
import europass.ewa.services.statistics.validators.Validator;

public class QueryPrefixParametersValidator implements Validator <Map<String,String>, String, String, QueryPrefixes> {

	QueryPrefixParameterMappings mappings;
	
	@Inject
	public QueryPrefixParametersValidator(QueryPrefixParameterMappings mappings){
		this.mappings = mappings;
	}

	@Override
	public ValidationResult validate(QueryPrefixes value) {
		return new ValidationResult.Builder(true).build();
	}
	
	@Override
	public ValidationResult validateAll(Set<QueryPrefixes> value) {
		return new ValidationResult.Builder(true).build();
	}

	@Override
	public ValidationResult validateAllAgainst(Set<String> params, QueryPrefixes against) {


		if(against.equals(QueryPrefixes.INVALID)){
			return new ValidationResult.Builder(false)
											.withValidationErrors(ValidationErrors.QUERY_PREFIX_INVALID)
											.withFailedOn(against.getDescription())
											.build();			
		}

		for(String name : params){
			
			ParameterNames paramMatched = ParameterNames.match(name);
			
			if(paramMatched.equals(ParameterNames.INVALID)){
				return new ValidationResult.Builder(false)
													.withValidationErrors(ValidationErrors.QUERY_PARAMETER_INVALID)
													.withFailedOn(against.getDescription())
													.build();
			}
			
			List<QueryPrefixes> queryPrefixesAllowed = mappings.getMappingsFor(paramMatched);
			
			if(queryPrefixesAllowed.isEmpty() || !queryPrefixesAllowed.contains(against)){
				return new ValidationResult.Builder(false)
													.withValidationErrors(ValidationErrors.QUERY_PARAMETER_PREFIX_MISMATCH)
													.withFailedOn(name)
													.withAgainst(against.getDescription())
													.build();
			}
		}
		
		return new ValidationResult.Builder(true).build();
	}

	@Override
	public ValidationResult validateAllAgainstValues(Map<String, String> map) {
		return new ValidationResult.Builder(true).build();
	}
}
