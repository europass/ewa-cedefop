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
package europass.ewa.services.statistics.parser.hsqlconstruct;

import europass.ewa.services.statistics.enums.tables.EntityTablesProperties;
import europass.ewa.services.statistics.structures.QueryParameter;
import europass.ewa.services.statistics.structures.QueryProperties;

public abstract class EntityFieldsResolver {
	
	protected QueryProperties parameterProperties;
	
	protected EntityFieldsResolver next;
	
	public EntityFieldsResolver(EntityFieldsResolver next){
		configure();
		this.next = next;
	}
	
	public void dispatch(HSQLParts hsqlBuilder, QueryParameter queryParamater, EntityTablesProperties entityProperties){
		
		populateHSQL(hsqlBuilder, queryParamater, entityProperties);
		if(this.next != null)
			this.next.dispatch(hsqlBuilder, queryParamater, entityProperties);
	}
	
	protected abstract void configure();

	protected abstract void populateHSQL(HSQLParts hsqlBuilder, QueryParameter queryParamater, EntityTablesProperties entityProperties);
	
	public EntityFieldsResolver getNextInChain() {
		return next;
	}

	public void setNextInChain(EntityFieldsResolver nextInChain) {
		this.next = nextInChain;
	}
	
	public QueryProperties getParameterProperties() {
		return parameterProperties;
	}

	public void setParameterProperties(QueryProperties parameterProperties) {
		this.parameterProperties = parameterProperties;
	}
}
