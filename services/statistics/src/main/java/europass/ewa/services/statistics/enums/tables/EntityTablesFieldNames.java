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
package europass.ewa.services.statistics.enums.tables;

import java.util.EnumSet;

public enum EntityTablesFieldNames {
	
	YEAR_NO ("year_no"),
	MONTH_NO ("month_no"),
	DAY_NO ("day_no"),
	DOC_TYPE ("doc_type"),
	DOC_LANG ("doc_lang"),
	ADDRESS_COUNTRY ("address_country"),
	GENDER_GROUP ("gender_group"),
	AGE ("male"),
	WORK_YEARS ("female"),
	EDUC_YEARS ("other"),
	
	REC_COUNT ("rec_count"),
	EMAIL_HASH_CODE ("email_hash_code"),
	RANK_NO ("rank_no"),
	
	VOLUME ("volume"),
	YEAR ("year"),
	MONTH ("month"),
	ISO_COUTRY_CODE ("iso_country_code");
	
	private String fieldName;

	EntityTablesFieldNames(String fieldName) {
		this.fieldName = fieldName;
	}
	
	@Override
	public String toString() {
		return fieldName;
	}
	
	public String getDescription() {
		return fieldName;
	}
	
	public static EnumSet<EntityTablesFieldNames> getSet(){
		return EnumSet.allOf(EntityTablesFieldNames.class);		
	}
	
	public static EntityTablesFieldNames match( String str ){
		if ( str == null ){
			return REC_COUNT;
		}
		
		for ( EntityTablesFieldNames param : values() ){

			if ( str.equals( param.fieldName ) )
				return param;
		}
		return REC_COUNT;
	}
}