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

import europass.ewa.services.statistics.constants.ServicesStatisticsConstants;

/**
 * ValueTypes
 * Parameter value types regular expressions and order by regexp
 * 
 * @author pgia
 *
 */
public enum ValueTypes {

	DATE (ServicesStatisticsConstants.DATE_REGEXP),
	DATE_RANGE ("("+ServicesStatisticsConstants.DATE_RANGE_REGEXP+"{1})(,"+ServicesStatisticsConstants.DATE_RANGE_REGEXP+")*"),
	DATE_NOT ("("+ServicesStatisticsConstants.DATE_NOT_REGEXP+"{1})(,"+ServicesStatisticsConstants.DATE_NOT_REGEXP+")*"),
//	DATE_NOT ("!{1}"+DATE.getDescription()),
	DATE_OR (ServicesStatisticsConstants.DATE_OR_REGEXP),
	DATE_AND (ServicesStatisticsConstants.DATE_AND_REGEXP),
	
	NUMBER (ServicesStatisticsConstants.NUMBER_REGEXP),
	NUMBER_RANGE (ServicesStatisticsConstants.NUMBER_RANGE_REGEXP),
	NUMBER_NOT ("(!{1}"+NUMBER.getDescription()+"{1})(,!{1}"+NUMBER.getDescription()+")*"),
	NUMBER_OR (ServicesStatisticsConstants.NUMBER_REGEXP+"(\\+"+ServicesStatisticsConstants.NUMBER_REGEXP+")+"),
	NUMBER_AND (ServicesStatisticsConstants.NUMBER_REGEXP+"(,"+ServicesStatisticsConstants.NUMBER_REGEXP+")+"),

	VALUE_NOT ("!{1}(.)+(,!{1}(.^!)+)*"),
	VALUE_OR ("[^\\+]+(\\+[^\\+]+)+"),
	VALUE_AND ("[^(!,)]+(,[^(!,)]+)+"),
	VALUE (".+"),

	ORDER_BY ("((([a-z])+(-[a-z]+)*)|("+ServicesStatisticsConstants.ORDER_BY_RESULTS+"))\\.(ASC|DESC)"),
	
//	GROUP_BY_PARAM_ORDER ("(([a-z])+(-[a-z]+)*)?(,(ASC|DESC))?"),
	
	UNIQUE_USERS("true"),
	
	INVALID ("invalid-parameter-value-type");
	
	private String description;

	ValueTypes(String description) {
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public static EnumSet<ValueTypes> getSet() {
		return EnumSet.allOf(ValueTypes.class);
	}

	public static ValueTypes match(String str) {
		if (str == null) {
			return INVALID;
		}

		for (ValueTypes param : values()) {

			if (str.matches("^" + param.description + "$")) {
				return param;
			}
		}
		return INVALID;
	}
}
