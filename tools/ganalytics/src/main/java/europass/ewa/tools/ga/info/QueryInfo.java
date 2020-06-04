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
package europass.ewa.tools.ga.info;

import org.joda.time.DateTime;

import europass.ewa.tools.utils.Utils;

public class QueryInfo {
	
	private Long tableId;

	private DateTime startDate;
	private DateTime endDate;

	private String metrics;
	
	/*
	 * Different use depending on pageview or event request
	 */
	
	// dimensions
	private String dimensions;
	
	// sort
	private String sort;
	
	// filters
	private String filters;
	
	// maxResults
	private Integer maxResults;

	public Long getTableId() {
		return tableId;
	}

	public void setTableId(Long tableId) {
		this.tableId = tableId;
	}

	public DateTime getStartDate() {
		return startDate;
	}

	public void setStartDate(DateTime startDate) {
		this.startDate = startDate;
	}

	public DateTime getEndDate() {
		return endDate;
	}

	public void setEndDate(DateTime endDate) {
		this.endDate = endDate;
	}

	public String getDimensions() {
		return dimensions;
	}

	public void setDimensions(String dimensions) {
		this.dimensions = dimensions;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	public String getFilters() {
		return filters;
	}

	public void setFilters(String filters) {
		this.filters = filters;
	}
	
	public Integer getMaxResults() {
		return maxResults;
	}

	public void setMaxResults(Integer value) {
		this.maxResults = value;
	}
	
	public String asGetText(Object obj){
		
		switch(obj.getClass().getName()){
		
			case "java.lang.Integer":
			case "java.lang.Long":
				return ""+obj;
	
			case "java.lang.String":
				return (String)obj;

			case "org.joda.time.DateTime":
				return Utils.formatDateTime((DateTime)obj);
		
			default:
				return ((Object)obj).toString();
		}
	}

	public String getMetrics() {
		return metrics;
	}

	public void setMetrics(String metrics) {
		this.metrics = metrics;
	}
}
