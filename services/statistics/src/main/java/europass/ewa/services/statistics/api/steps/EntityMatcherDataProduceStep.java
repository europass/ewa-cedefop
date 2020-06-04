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
package europass.ewa.services.statistics.api.steps;

import com.google.inject.Inject;
import europass.ewa.services.statistics.enums.request.ParameterNames;
import europass.ewa.services.statistics.enums.request.QueryPrefixes;
import europass.ewa.services.statistics.enums.tables.EntityTablesProperties;
import europass.ewa.services.statistics.hibernate.HibernateStatisticsServicesFetcher;
import europass.ewa.services.statistics.mappings.structures.caching.CachedQueries;
import europass.ewa.services.statistics.parser.hsqlconstruct.HQLConstructChain;
import europass.ewa.services.statistics.parser.hsqlconstruct.HSQLParts;
import europass.ewa.services.statistics.parser.matcher.MatcherChain;
import europass.ewa.services.statistics.structures.QueryEntityMatch;
import europass.ewa.services.statistics.structures.QueryParameter;

/**
 * EntityMatcherDataProduceStep
 *
 * - Matches the entity that will be used to pull the statistics data by checking the parameters given
 * - Constructs the corresponding hsql query that will fetch teh data
 * - Use of a HibernateStatisticsServicesFetcher object to execute the hsql query and fetch the data
 *
 * @author pgia
 *
 */
public class EntityMatcherDataProduceStep extends AbstractStatisticsApiStep {

	private HibernateStatisticsServicesFetcher statisticsDataFetcher;
	private static CachedQueries cachedQueries;

	@Inject
	public EntityMatcherDataProduceStep(HibernateStatisticsServicesFetcher fetcher, CachedQueries cQueries) {

		statisticsDataFetcher = fetcher;
		cachedQueries = cQueries;
	}

	@Override
	public void setNext(AbstractStatisticsApiStep step) {
		super.setNext(step);
	}

	@Override
	public void doStep() {
		EntityTablesProperties entityProperties = null;
		if (this.info.getQueryPrefix().equals(QueryPrefixes.GENERATED)) {
			if (this.qProperties.getParameterNamesList().contains(ParameterNames.UNIQUE_USERS)) {
				entityProperties = EntityTablesProperties.CUBE_ENTRY_EMAIL_HASH;
			} else {
				entityProperties = getEntityTablesFromParameters();
			}
		} else if (this.info.getQueryPrefix().equals(QueryPrefixes.VISITS)) {
			entityProperties = EntityTablesProperties.STAT_VISITS;
		} else if (this.info.getQueryPrefix().equals(QueryPrefixes.DOWNLOADS)) {
			entityProperties = EntityTablesProperties.STAT_DOWNLOADS;
		}
		if (entityProperties != null) {
			getStatisticsResults(entityProperties);
		}
		super.doStep();
	}

	private EntityTablesProperties getEntityTablesFromParameters() {
		QueryEntityMatch matcher = new QueryEntityMatch(EntityTablesProperties.CUBE_ENTRY, this.qProperties.getParameterNamesList());
		MatcherChain.execute(matcher, this.qProperties);
		EntityTablesProperties entityProperties = matcher.getEntityProperties();
		return entityProperties;
	}

	/**
	 * Four stepped procedure to construct and execute the hsql query in order
	 * to fetch the data
	 */
	private void getStatisticsResults(EntityTablesProperties entityProperties) {

		// Step 1: Store of the entity properties on the QueryProperties
		this.qProperties.setTableProperties(entityProperties);

		// Step 2: Construction of the HSQL parts of the queryParametersNumber
		// Also, declare the number of parameters so if there is only one to avoid the group by clause
		HSQLParts parts = new HSQLParts(entityProperties, this.qProperties.getParameterList().size());

		// Step 3a: For every parameter, construction of the accordingly hsql clause depending on its value type
		for (QueryParameter parameter : this.qProperties.getParameterList()) {
			HQLConstructChain.execute(parts, parameter, entityProperties);
		}

		parts.addDefaultOrderConditionally(entityProperties);

		// Step 3b: Finalizing the hsql query String assembling (top / order clauses) 
		this.qProperties.getHsqlBuilder().append(parts.getHSQLQuery());
		if (parts.getTop() > 0) {
			this.qProperties.setTop(parts.getTop());
		}
		//AT: I think this is not used in the following methods
		//if (!Strings.isNullOrEmpty(parts.getOrder())) {
		//	this.qProperties.setOrderByList(parts.getOrderByList());
		//	this.qProperties.setOrder(parts.getOrder());
		//}

		// Appliance of query caching / check for results
		String query = qProperties.getHsqlBuilder().toString();
		boolean isAlreadyCached = cachedQueries.isAlreadyCached(query);

		if (!isAlreadyCached) {
			// Step 4: fetching of the data by use of the QueryProperties structure and appending of the fields names (headers)
			statisticsDataFetcher.fetchData(parts.getHeaders(), this.qProperties);
			if (!parts.getHeaders().contains("error_code")) {
				cachedQueries.cleanUp();
				cachedQueries.cacheQuery(query, qProperties.getResults());
			}
		} else {
			cachedQueries.getCachedQuery(query).increasetimesQueried(1);
			this.qProperties.setResults(cachedQueries.getCachedQueryResults(query));
		}

	}
}
