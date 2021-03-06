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
package europass.ewa.services.statistics.enums.values;

import java.util.EnumSet;

public enum DocumentFormatEnum {
	
	INVALID ("invalid-document-type-value"),
	
	TEMPLATE ("template"),
	INSTRUCTIONS ("instructions"),
	EXAMPLES ("examples"),
	
	ALL_TYPES_OR ("("+TEMPLATE.getDescription()+"|"+INSTRUCTIONS.getDescription()+"|"+EXAMPLES.getDescription()+")"),
	
	VALUE_NOT ("!"+ALL_TYPES_OR.getDescription()),
	VALUE_OR (ALL_TYPES_OR.getDescription()+"(\\+"+ALL_TYPES_OR.getDescription()+")+"),
	VALUE_AND (ALL_TYPES_OR.getDescription()+"(,"+ALL_TYPES_OR.getDescription()+")+");
	
	private String description;

	DocumentFormatEnum(String description) {
		this.description = description;
	}
	
	@Override
	public String toString() {
		return description;
	}
	
	public String getDescription() {
		return description;
	}
	
	public static EnumSet<DocumentFormatEnum> getSet(){
		return EnumSet.allOf(DocumentFormatEnum.class);		
	}
	
	public static DocumentFormatEnum match( String str ){
		if ( str == null ){
			return INVALID;
		}
		
		for ( DocumentFormatEnum param : values() ){

			if ( str.equals( param.description ) )
				return param;
		}
		return INVALID;
	}
}