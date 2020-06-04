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

import europass.ewa.services.statistics.enums.values.DocumentFormatEnum;
import europass.ewa.services.statistics.enums.values.DocumentTypeEnum;
import europass.ewa.services.statistics.enums.values.ExamplesFormatEnum;

/**
 * EntitiesFieldsNames
 * Names of the field names of the hibernate entities that represent the database tables
 * 
 * @author pgia
 *
 */
public enum EntitiesFieldsNames {
	
	INVALID ("invalid-entity-field-name"),
	
	YEAR_NO ("year_no"), 
	MONTH_NO ("month_no"), 
	DAY_NO ("day_no"), 
	DOC_TYPE ("doc_type"), 
	DOC_LANG ("doc_lang"), 
	ADDRESS_COUNTRY ("address_country"), 
	MLANGUAGE ("m_lang"), 
	OLANGUAGE ("f_lang"), 
	NATIONALITY ("nationality"), 
	AGE ("age"), 
	GENDER ("gender_group"), 
	WORK_YEARS ("work_years"), 
	EDUC_YEARS ("educ_years"),
	 
	REC_COUNT ("rec_count"),
	EMAIL_HASH ("email_hash_code"),
	
	COUNTRY_CODE ("iso_country_code"),
	LANGUAGE_CODE ("iso_language_code"),
	DOCUMENT_FORMAT ("type"),
	DOCUMENT ("document"),
	VOLUME ("volume"),
	
	ISO_LABEL("label"),
	ISO_COUNTRY_CODE("country_code");
	
	private String description;

	EntitiesFieldsNames(String description) {
		this.description = description;
	}
	
	@Override
	public String toString() {
		return description;
	}
	
	public String getDescription() {
		return description;
	}

	public static EnumSet<EntitiesFieldsNames> getSet() {
		return EnumSet.allOf(EntitiesFieldsNames.class);
	}

	public static EntitiesFieldsNames match(String str) {
		if (str == null) {
			return INVALID;
		}

		for (EntitiesFieldsNames param : values()) {

			if (str.equals(param.description)) {
				return param;
			}
		}
		return INVALID;
	}

	public static EntitiesFieldsNames fromValue(String str) {
		if (str == null) {
			return INVALID;
		}
		try {
			EntitiesFieldsNames ua = EntitiesFieldsNames.valueOf(str);
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
}
