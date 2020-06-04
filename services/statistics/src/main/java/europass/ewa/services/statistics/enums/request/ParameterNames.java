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

import europass.ewa.services.statistics.enums.values.DocumentEnum;
import europass.ewa.services.statistics.enums.values.DocumentFormatEnum;
import europass.ewa.services.statistics.enums.values.DocumentTypeEnum;
import europass.ewa.services.statistics.enums.values.ExamplesFormatEnum;
import europass.ewa.services.statistics.enums.values.GenderGroupEnum;

/**
 * ParameterNames
 * the parameter names used to query the Statistics API
 * 
 * @author pgia
 *
 */
public enum ParameterNames {
	
	INVALID ("invalid-parameter-name"),
	
	DOCUMENT_TYPE ("document-type"), 
	DOCUMENT_FORMAT ("document-format"), 
	DOCUMENT ("document"), 
	EXAMPLES_FORMAT ("examples-format"), 
	COUNTRY ("country"), 
	LANGUAGE ("language"), 
	MLANGUAGE ("mlanguage"), 
	OLANGUAGE ("olanguage"), 
	NATIONALITY ("nationality"), 
	ONATIONALITY ("onationality"), 
	DATE ("date"), 
	GENDER ("gender"), 
	AGE ("age"), 
	WORK_EXPERIENCE ("work-experience"),
	EDUCATION_YEARS ("edu-years"),
	
	GROUP_BY ("groupby"),
	ORDER_BY ("orderby"),
	TOP ("top"),
	UNIQUE_USERS("unique-users");
	
	private String description;

	ParameterNames(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return description;
	}

	public String getDescription() {
		return description;
	}

	public static EnumSet<ParameterNames> getSet() {
		return EnumSet.allOf(ParameterNames.class);
	}

	public static ParameterNames match(String str) {
		if (str == null) {
			return INVALID;
		}

		for (ParameterNames param : values()) {

			if (str.equals(param.description)) {
				return param;
			}
		}
		return INVALID;
	}

	public static ParameterNames fromValue(String str) {
		if (str == null) {
			return INVALID;
		}
		try {
			ParameterNames ua = ParameterNames.valueOf(str);
			if (ua != null) {
				return ua;
			}
		} catch (IllegalArgumentException iae) {
			return INVALID;
		}
		return INVALID;
	}

	public static boolean documentTypeEnumMatcher(String str) {
		if (str == null) {
			return false;
		}

		for (DocumentTypeEnum param : DocumentTypeEnum.values()) {
			if (str.matches(param.getDescription())) {
				return true;
			}
		}

		return false;
	}

	public static boolean documentFormatEnumMatcher(String str) {
		if (str == null) {
			return false;
		}

		for (DocumentFormatEnum param : DocumentFormatEnum.values()) {
			if (str.matches(param.getDescription())) {
				return true;
			}
		}

		return false;
	}
	
	public static boolean documentEnumMatcher(String str) {
		if (str == null) {
			return false;
		}

		for (DocumentEnum param : DocumentEnum.values()) {
			if (str.matches(param.getDescription())) {
				return true;
			}
		}

		return false;
	}

	public static boolean examplesFormatEnumMatcher(String str) {
		if (str == null) {
			return false;
		}

		for (ExamplesFormatEnum param : ExamplesFormatEnum.values()) {
			if (str.matches(param.getDescription())) {
				return true;
			}
		}

		return false;
	}

	public static boolean GenderEnumMatcher(String str) {
		if (str == null) {
			return false;
		}

		for (GenderGroupEnum param : GenderGroupEnum.values()) {
			if (str.matches(param.getDescription())) {
				return true;
			}
		}

		return false;
	}
}
