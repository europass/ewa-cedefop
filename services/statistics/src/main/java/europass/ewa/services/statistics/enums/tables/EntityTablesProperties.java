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

import europass.ewa.services.statistics.constants.ServicesStatisticsConstants;

public enum EntityTablesProperties {
	
	CUBE_ENTRY_AGE (ServicesStatisticsConstants.CUBE_ENTRY_AGE_ENTITY),
	CUBE_ENTRY_DOCS (ServicesStatisticsConstants.CUBE_ENTRY_DOCS_ENTITY),
	CUBE_ENTRY_DOCS_LANGS (ServicesStatisticsConstants.CUBE_ENTRY_DOCS_LANGS_ENTITY),
	CUBE_ENTRY_FLANG (ServicesStatisticsConstants.CUBE_ENTRY_FLANG_ENTITY),
	CUBE_ENTRY_FLANG_COUNTER (ServicesStatisticsConstants.CUBE_ENTRY_FLANG_COUNTER_ENTITY),
	CUBE_ENTRY_FLANG_PIVOT (ServicesStatisticsConstants.CUBE_ENTRY_FLANG_PIVOT_ENTITY),
	CUBE_ENTRY_FLANG_SHORT (ServicesStatisticsConstants.CUBE_ENTRY_FLANG_SHORT_ENTITY),
	CUBE_ENTRY_GENDER (ServicesStatisticsConstants.CUBE_ENTRY_GENDER_ENTITY),
	CUBE_ENTRY_LANGS (ServicesStatisticsConstants.CUBE_ENTRY_LANGS_ENTITY),
	CUBE_ENTRY_MLANG (ServicesStatisticsConstants.CUBE_ENTRY_MLANG_ENTITY),
	CUBE_ENTRY_NAT (ServicesStatisticsConstants.CUBE_ENTRY_NAT_ENTITY),
	CUBE_ENTRY_NAT_RANK (ServicesStatisticsConstants.CUBE_ENTRY_NAT_RANK_ENTITY),
	CUBE_ENTRY_NAT_FLANG (ServicesStatisticsConstants.CUBE_ENTRY_NAT_FLANG_ENTITY),
	CUBE_ENTRY_NAT_LANGS (ServicesStatisticsConstants.CUBE_ENTRY_NAT_LANGS_ENTITY),
	CUBE_ENTRY_NAT_MLANG (ServicesStatisticsConstants.CUBE_ENTRY_NAT_MLANG_ENTITY),
	CUBE_ENTRY_SHORT (ServicesStatisticsConstants.CUBE_ENTRY_SHORT_ENTITY),
	CUBE_ENTRY_WORKEXP (ServicesStatisticsConstants.CUBE_ENTRY_WORKEXP_ENTITY),
	CUBE_TOP20NAT (ServicesStatisticsConstants.CUBE_TOP20NAT_ENTITY),
	
	CUBE_ENTRY (ServicesStatisticsConstants.CUBE_ENTRY_ENTITY),
	CUBE_ENTRY_EMAIL_HASH (ServicesStatisticsConstants.CUBE_ENTRY_EMAIL_HASH_ENTITY),
	
	STAT_VISITS (ServicesStatisticsConstants.STAT_VISITS_ENTITY),
	STAT_DOWNLOADS (ServicesStatisticsConstants.STAT_DOWNLOADS_ENTITY),
	
	ISO_COUNTRY (ServicesStatisticsConstants.ISO_COUNTRY_ENTITY),
	ISO_NATIONALITY (ServicesStatisticsConstants.ISO_NATIONALITY_ENTITY);
	
	private String tableName;

	EntityTablesProperties(String description) {
		this.tableName = description;
	}
	
	@Override
	public String toString() {
		return tableName;
	}
	
	public String getDescription() {
		return tableName;
	}
	
	public static EnumSet<EntityTablesProperties> getSet(){
		return EnumSet.allOf(EntityTablesProperties.class);		
	}
	
	public static EntityTablesProperties match( String str ){
		if ( str == null ){
			return CUBE_ENTRY;
		}
		
		for ( EntityTablesProperties param : values() ){

			if ( str.equals( param.tableName ) )
				return param;
		}
		return CUBE_ENTRY;
	}
}