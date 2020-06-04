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
package europass.ewa.services.statistics.structures;

import java.util.List;

import europass.ewa.services.statistics.enums.request.ParameterNames;
import europass.ewa.services.statistics.enums.tables.EntityTablesProperties;

public class QueryEntityMatch {

	private EntityTablesProperties entityProperties;
	private List<ParameterNames> parameters;
	
	public QueryEntityMatch(EntityTablesProperties entityProperties, List<ParameterNames> parameters){
		this.entityProperties = entityProperties;
		this.parameters = parameters;
	}
	
	public EntityTablesProperties getEntityProperties() {
		return entityProperties;
	}
	public void setEntityProperties(EntityTablesProperties properties) {
		this.entityProperties = properties;
	}

	public List<ParameterNames> getParameters() {
		return parameters;
	}
	public void setParameters( List<ParameterNames> parameters ) {
		this.parameters = parameters;
	}
	
}
