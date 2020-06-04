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
package europass.ewa.services.statistics.mappings.structures.caching;

import org.joda.time.DateTime;

import europass.ewa.services.statistics.structures.QueryResults;

public class CachedQuery {

	private DateTime created;
	private int timesQueried;
	private QueryResults results;

	public CachedQuery(QueryResults res) {
		results = res;
		created = DateTime.now();
		timesQueried = 1;
	}

	public DateTime getCreated() {
		return created;
	}

	public void setCreated(DateTime created) {
		this.created = created;
	}

	public int getTimesQueried() {
		return timesQueried;
	}

	public void setTimesQueried(int timesQueried) {
		this.timesQueried = timesQueried;
	}

	public QueryResults getResults() {
		return results;
	}

	public void setResults(QueryResults results) {
		this.results = results;
	}

	public void increasetimesQueried(int factor) {
		this.timesQueried += factor;
	}

}
