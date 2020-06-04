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
package europass.ewa.services.statistics.structures;

import europass.ewa.services.statistics.enums.request.ParameterNames;
import europass.ewa.services.statistics.enums.request.QueryPrefixes;

/**
 * QueryParameter
 * Structure that holds the objects related with the Query Parameters:
 * - ParameterNames ( Enumeration )
 * - ValueProperties ( Builder )
 *  
 * @author pgia
 *
 */
public class QueryParameter {
	
	private ParameterNames parameterName;
//	private QueryPrefixes boundToPrefix;
	private ValueProperties valueProperties;
	
	public QueryParameter(ParameterNames parameterName, QueryPrefixes boundToPrefix){
		
		this.parameterName = parameterName;
//		this.boundToPrefix = boundToPrefix;
	}
	
	public ParameterNames getParameterName() {
		return parameterName;
	}

	public void setParameterName(ParameterNames parameterName) {
		this.parameterName = parameterName;
	}

//	public QueryPrefixes getBoundToPrefix() {
//		return boundToPrefix;
//	}
//
//	public void setBoundToPrefix(QueryPrefixes boundToPrefix) {
//		this.boundToPrefix = boundToPrefix;
//	}

	public ValueProperties getValueProperties() {
		return valueProperties;
	}

	public void setValueProperties(ValueProperties valueProperties) {
		this.valueProperties = valueProperties;
	}

	
	
}
