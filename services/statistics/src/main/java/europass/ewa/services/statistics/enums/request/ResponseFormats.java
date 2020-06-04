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
package europass.ewa.services.statistics.enums.request;

import java.util.EnumSet;

/**
 * ResponseFormats
 * The type of the query Statistics API response 
 * 
 * @author pgia
 *
 */
public enum ResponseFormats{

	INVALID ("invalid-response-format"),
	
	PATH_RESPONSE_TYPE_JSON ("json"),
	PATH_RESPONSE_TYPE_CSV ("csv");
	
	private String description;
	private static String invalidValue;

	ResponseFormats(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return description;
	}

	public String getDescription() {
		return description;
	}

	public static EnumSet<ResponseFormats> getSet() {
		return EnumSet.allOf(ResponseFormats.class);
	}

	public static ResponseFormats match(String str) {
		if (str == null) {
			return INVALID;
		}

		for (ResponseFormats param : values()) {

			if (str.equals(param.description)) {
				return param;
			}
		}

		invalidValue = str;
		return INVALID;
	}

	public static ResponseFormats fromValue(String str) {
		if (str == null) {
			return INVALID;
		}
		try {
			ResponseFormats ua = ResponseFormats.valueOf(str);
			if (ua != null) {
				return ua;
			}
		} catch (IllegalArgumentException iae) {
			return INVALID;
		}
		return INVALID;
	}

	public static String getInvalidValue() {
		return invalidValue;
	}

}
