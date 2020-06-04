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

import java.util.ArrayList;
import java.util.List;

import europass.ewa.services.statistics.enums.request.ParameterNames;
import europass.ewa.services.statistics.enums.request.QueryPrefixes;
import europass.ewa.services.statistics.enums.request.ResponseFormats;
import europass.ewa.services.statistics.enums.tables.EntityTablesProperties;

public class QueryProperties {

	private EntityTablesProperties tableProperties;
	
	private ResponseFormats responseFormat;
	private QueryPrefixes queryPrefix;
	
	private StringBuilder hsqlBuilder;
	
	private List<QueryParameter> parametersList;
	private List<String> orderByList;
	
	private QueryResults results;

	private int top;
	private String order;
	
	public QueryProperties(){
		hsqlBuilder = new StringBuilder();
		top = 0;
		order = "";
	}
	
	public EntityTablesProperties getTableProperties() {
		return tableProperties;
	}
	
	public void setTableProperties(EntityTablesProperties props) {
		this.tableProperties = props;
	}

	public List<QueryParameter> getParameterList(){
		return parametersList;
	}

	public void setParameterList(List<QueryParameter> list){
		this.parametersList = list;
	}

	public ResponseFormats getResponseFormat() {
		return responseFormat;
	}

	public void setResponseFormat(ResponseFormats responseFormat) {
		this.responseFormat = responseFormat;
	}

	public QueryPrefixes getQueryPrefix() {
		return queryPrefix;
	}

	public void setQueryPrefix(QueryPrefixes queryPrefix) {
		this.queryPrefix = queryPrefix;
	}
	
	public List<ParameterNames> getParameterNamesList(){
		
		List<ParameterNames> parameterNamesEnumList = new ArrayList<ParameterNames>();
		for(QueryParameter parameter : parametersList){
			parameterNamesEnumList.add(parameter.getParameterName());
		}
		
		return parameterNamesEnumList;
	}
	
	public QueryParameter getByParameterName(ParameterNames parameterName){
	
		for(QueryParameter queryParameter : parametersList){
			
			if(queryParameter.getParameterName().equals(parameterName))
				return queryParameter;
		}
		return null;
	}

	public StringBuilder getHsqlBuilder() {
		return hsqlBuilder;
	}

	public void setHsqlBuilder(StringBuilder hsqlBuilder) {
		this.hsqlBuilder = hsqlBuilder;
	}

	public QueryResults getResults() {
		return results;
	}

	public void setResults(QueryResults results) {
		this.results = results;
	}
	
	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}	
		
	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public List<String> getOrderByList() {
		return orderByList;
	}

	public void setOrderByList(List<String> orderByList) {
		this.orderByList = orderByList;
	}
}
