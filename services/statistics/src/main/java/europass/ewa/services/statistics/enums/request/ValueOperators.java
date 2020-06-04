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
 * ValueOperators Accepted parameter values operators
 *
 * @author pgia
 *
 */
public enum ValueOperators {

	NONE("", ""),
	AND(",", ","),
	OR("+", "\\+"),
	NOT("!", "!");

	private String description;
	private String regex;

	ValueOperators(String description, String regex) {
		this.description = description;
		this.regex = regex;
	}

	@Override
	public String toString() {
		return description;
	}

	public String getDescription() {
		return description;
	}

	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public static EnumSet<ValueOperators> getSet() {
		return EnumSet.allOf(ValueOperators.class);
	}

	public static ValueOperators match(String str) {
		if (str == null) {
			return NONE;
		}

		for (ValueOperators param : values()) {
			
			if (str.equals(param.description)) {
				return param;
			}
		}
		return NONE;
	}

	public static ValueOperators fromValue(String str) {
		if (str == null) {
			return NONE;
		}
		try {
			ValueOperators ua = ValueOperators.valueOf(str);
			if (ua != null) {
				return ua;
			}
		} catch (IllegalArgumentException iae) {
			return NONE;
		}
		return NONE;
	}

	public static ValueOperators detectOperatorByName(String name) {

		// Detect value operator operator
		for (ValueOperators op : ValueOperators.values()) {

			if (!op.equals(ValueOperators.NONE)) {
				if (name.endsWith(op.name())) {
					return op;
				}

				// match here the operator which will be changed to NONE according to value
				if (name.endsWith("RANGE") || name.endsWith("NOT")) {
					return ValueOperators.AND;
				}
			}
		}

		return ValueOperators.NONE;
	}
}
