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
package europass.ewa.services.statistics.validators.factory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import europass.ewa.services.statistics.mappings.structures.ParameterValueTypesMappings;
import europass.ewa.services.statistics.mappings.structures.QueryPrefixParameterMappings;
import europass.ewa.services.statistics.validators.ParametersValidator;
import europass.ewa.services.statistics.validators.QueryPrefixValidator;
import europass.ewa.services.statistics.validators.ResponseFormatValidator;
import europass.ewa.services.statistics.validators.Validator;
import europass.ewa.services.statistics.validators.structural.ParametersValueEnumsValidator;
import europass.ewa.services.statistics.validators.structural.ParametersValueTypesValidator;
import europass.ewa.services.statistics.validators.structural.QueryPrefixParametersValidator;

@Singleton
public class ValidatorFactoryImpl implements ValidatorFactory{

	private QueryPrefixParameterMappings queryPrefixParameterMappings;
	private ParameterValueTypesMappings parameterValueTypesMappings;
	
	@Inject
	ValidatorFactoryImpl(QueryPrefixParameterMappings qpmappings, ParameterValueTypesMappings pvmappings){
		this.queryPrefixParameterMappings = qpmappings;
		this.parameterValueTypesMappings = pvmappings;
	}
	
	@SuppressWarnings("rawtypes")
	public Validator getValidator(String level){
		
		Validator validator = null;
		
		switch(level){
		
		case "format":
			return new ResponseFormatValidator();
		case "prefix":
			return new QueryPrefixValidator();
		case "parameter":
			return new ParametersValidator();
		case "parameter-prefix":
			return new QueryPrefixParametersValidator(queryPrefixParameterMappings);
		case "parameter-value-types":
			return new ParametersValueTypesValidator(parameterValueTypesMappings);
		case "parameter-value-enums":
			return new ParametersValueEnumsValidator(parameterValueTypesMappings);
		default:
			return validator;
		}
	}

}
