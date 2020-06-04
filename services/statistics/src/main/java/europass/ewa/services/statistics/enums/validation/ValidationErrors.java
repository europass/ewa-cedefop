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
package europass.ewa.services.statistics.enums.validation;

public enum ValidationErrors {

	RESPONSE_FORMAT_INVALID ("Response Format '<case>' is invalid."),
	QUERY_PREFIX_INVALID ("Query prefix '<case>' is invalid."),
	QUERY_PARAMETER_INVALID ("Query parameter '<case>' is invalid."),
	QUERY_PARAMETER_PREFIX_MISMATCH ("Query parameter '<case>' is not applicable for query prefix '<against>'."),
	QUERY_PARAMETER_VALUE_TYPE_MISMATCH ("Value '<value>' for query parameter '<case>' is not of a valid <type>."),
	QUERY_PARAMETER_VALUE_ENUM_MISMATCH ("Value '<against>' for query parameter '<case>' is not valid."),
	QUERY_PARAMETERS_MISSING("Query parameters are missing."),
	
	GROUP_BY_QUERY_PARAMETER_INVALID ("'<case>' parameter value '<against>' is not a valid query parameter"),
	
	ORDER_BY_INVALID ("order by parameter value '<case>' is not valid"),
	ORDER_BY_QUERY_PARAMETER_INVALID ("'<case>' parameter value '<against>' is not a valid query parameter"),

	// DATE SPECIFIC
	QUERY_PARAMETER_DATE_INVALID ("Date '<case>' is not valid."),
	QUERY_PARAMETER_DATE_RANGE_INVALID ("Date range '<case>' is not valid."),
	
	// NUMBER SPECIFIC
	QUERY_PARAMETER_NUMBER_RANGE_INVALID ("Number range '<case>' is not valid."),
	
	UKNOWN_ERROR("Uknown error");
	
	private String description;
	
	ValidationErrors(String description){
		
		this.description = description;
	}

	public String getDescription() {
		return description;
	}
	
	public String invalidErrorOn(String str){
		return this.description.replaceFirst("<case>", str);
	}

	public String mismatchErrorOn(String str, String against){
		return this.description.replaceFirst("<case>", str).replaceFirst("<against>", against);
	}

	public String mismatchTypeErrorOn(String str, String type, String value){
		return this.description.replaceFirst("<case>", str).replaceFirst("<value>", value).replaceFirst("<type>", type);
	}
}
