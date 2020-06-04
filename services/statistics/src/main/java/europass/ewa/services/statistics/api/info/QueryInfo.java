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
package europass.ewa.services.statistics.api.info;

import java.util.List;
import java.util.Map;
import java.util.Set;

import europass.ewa.services.statistics.enums.request.QueryPrefixes;
import europass.ewa.services.statistics.enums.request.ResponseFormats;
import europass.ewa.services.statistics.structures.QueryParameter;
import europass.ewa.services.statistics.structures.ValidationResult;

/**
 * QueryInfo
 * 
 * Holds the information inside suitable structers regarding the Statistics API query requested:
 * 
 * - ResponseFormat
 * - QueryPrefix
 * - QueryParameters
 *    > Map containing Query Parameters Names
 *    > Map containing Query Parameters Values
 * 
 * @author pgia
 *
 */
public class QueryInfo {

	ValidationResult validationResult;
	
	boolean isValidated;
	ResponseFormats responseFormat;
	QueryPrefixes queryPrefix;
	Map<String, String> queryParametersValuesMap;
	List<QueryParameter> parameters;
	
	public QueryInfo(){
		this.isValidated = true;
	}
	
	public ResponseFormats getResponseFormat() {
		return responseFormat;
	}
	public void setResponseFormat(ResponseFormats responseFormat) {
		this.responseFormat = responseFormat;
	}
	public QueryPrefixes getQueryPrefix() {
		return queryPrefix;
	}
	public void setQueryPrefix(QueryPrefixes queryPrefix) {
		this.queryPrefix = queryPrefix;
	}
	
	public Set<String> getQueryParameterNames() {
		return queryParametersValuesMap.keySet();
	}
	
	public Map<String, String> getQueryParametersValuesMap() {
		return queryParametersValuesMap;
	}
	public void setQueryParametersValuesMap(
			Map<String, String> queryParametersValuesMap) {
		this.queryParametersValuesMap = queryParametersValuesMap;
	}
	
	public List<QueryParameter> getParameters() {
		return parameters;
	}
	public void setParameters(List<QueryParameter> parameters) {
		this.parameters = parameters;
	}
	
	public ValidationResult getValidationResult() {
		return validationResult;
	}
	public void setValidationResult(ValidationResult result) {
		this.validationResult = result;
		this.isValidated = result.getSuccess();
	}
	public boolean isValidated() {
		return this.isValidated;
	}
}
