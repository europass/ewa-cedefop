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
 * QueryPrefixes
 * The query prefix acceptable names regarding the pool of information we want to request via the Statistics API
 * 
 * @author pgia
 *
 */
public enum QueryPrefixes {

	INVALID ("invalid-query-prefix"),
	
	GENERATED ("generated"),
	VISITS ("visits"),
	DOWNLOADS ("downloads");
	
	private String description;
	private static String invalidValue;

	QueryPrefixes(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return description;
	}

	public String getDescription() {
		return description;
	}

	public static EnumSet<QueryPrefixes> getSet() {
		return EnumSet.allOf(QueryPrefixes.class);
	}

	public static QueryPrefixes match(String str) {
		if (str == null) {
			return INVALID;
		}

		for (QueryPrefixes param : values()) {

			if (str.equals(param.description)) {
				return param;
			}
		}

		invalidValue = str;
		return INVALID;
	}

	public static QueryPrefixes fromValue(String str) {
		if (str == null) {
			return INVALID;
		}
		try {
			QueryPrefixes ua = QueryPrefixes.valueOf(str);
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
