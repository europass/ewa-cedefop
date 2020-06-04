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
package europass.ewa.services.statistics.hibernate;

import europass.ewa.services.statistics.constants.ServicesStatisticsConstants;
import europass.ewa.services.statistics.enums.request.ParameterNames;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import europass.ewa.services.statistics.enums.response.ResponseStatusMessage;
import europass.ewa.services.statistics.enums.tables.EntityTablesProperties;
import europass.ewa.services.statistics.enums.validation.ResponseResult;
import europass.ewa.services.statistics.enums.validation.ResponseResult.ResponseInfo;
import europass.ewa.services.statistics.parser.hsqlconstruct.HSQLParts;
import europass.ewa.services.statistics.structures.QueryParameter;
import europass.ewa.services.statistics.structures.QueryProperties;
import europass.ewa.services.statistics.structures.QueryResults;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import org.apache.commons.lang.StringUtils;

/**
 * HibernateStatisticsServicesFetcher
 * Uses the QueryProperties to get the hsql String and executes it to fetch the data
 *
 * @author pgia
 *
 */
public class HibernateStatisticsServicesFetcher implements StatisticsServicesFetcher {

	private static final Logger LOG = LoggerFactory.getLogger(HibernateStatisticsServicesFetcher.class);

	private final Provider<Session> sessionProvider;

	@Inject
	public HibernateStatisticsServicesFetcher(Provider<Session> sessionProvider) {
		this.sessionProvider = sessionProvider;
	}

	@SuppressWarnings("unchecked")
	public void fetchData(List<String> headers, QueryProperties properties) {

		QueryResults results = new QueryResults();
		EntityTablesProperties entityTableProperties = properties.getTableProperties();
		String entityName = entityTableProperties.getDescription();

		try {
			//LOG.info("HibernateStatisticsServicesFetcher:log - fetch from " + entityName);

			Session session = sessionProvider.get();
			session.clear();

			String hql = properties.getHsqlBuilder().toString();

			LOG.info("HQL Query String: \"" + hql + "\"");

			Query query = session.createQuery(hql);

			long startTime = System.nanoTime();

			List<Object> dataList = query.list();

			long endTime = System.nanoTime();
			LOG.info("Query took " + (float) (endTime - startTime) / 1000000000 + " seconds");

			int top = properties.getTop();
			boolean isVisitsOrDownloadsTable = HSQLParts.isVisitsTable(entityTableProperties) || HSQLParts.isDownloadsTable(entityTableProperties);

			String groupByParameter = getGroupByParameter(properties, isVisitsOrDownloadsTable);
			int groupByParameterIndex = groupByParameter != null ? headers.indexOf(groupByParameter) : 1;

			if (top > 0 && top < dataList.size()
				&& (HSQLParts.isGeneralCube(entityTableProperties)
				|| entityTableProperties.equals(EntityTablesProperties.CUBE_ENTRY_AGE)
				|| entityTableProperties.equals(EntityTablesProperties.CUBE_ENTRY_WORKEXP)
				|| isVisitsOrDownloadsTable)) {

				//order is based on sum column
				
				//for general cubes 
				//(CUBE_ENTRY_LANGS, CUBE_ENTRY_MLANG, CUBE_ENTRY_FLANG, CUBE_ENTRY, CUBE_ENTRY_SHORT, CUBE_ENTRY_NAT, CUBE_ENTRY_NAT_LANGS, CUBE_ENTRY_NAT_RANK)
				//rec_count is always in the first position of the results
				
				//for CUBE_ENTRY_AGE and CUBE_ENTRY_WORKEXP we need to find the position of the SUM column 
				//(works ok with single select filters but cannot work with multi select age and work-experiece cubes)
				int sumParamIndex = 0;
				for (String header : headers) {
					if (header.toUpperCase().startsWith("SUM")) {
						sumParamIndex = headers.indexOf(header);
						break;
					}
				}

				List<Object> topList = new ArrayList(dataList.subList(0, top));
				List otherValuesList = new ArrayList(dataList.subList(top, dataList.size()));

				long otherSum = 0;

				List indexesToRemove = new ArrayList();
				List indexesToMove = new ArrayList();

				int i = 0;
				for (Iterator iterator = topList.iterator(); iterator.hasNext();) {
					Object[] next = (Object[]) iterator.next();
					String groupByValue = (String) next[groupByParameterIndex];
					if (StringUtils.isBlank(groupByValue)) {
						indexesToRemove.add(i);
					} else if (HSQLParts.isVisitsTable(entityTableProperties) && groupByValue.equals("UN")) {
						indexesToMove.add(i);
					}
					i++;
				}

				Collections.reverse(indexesToRemove);
				for (Iterator iterator = indexesToRemove.iterator(); iterator.hasNext();) {
					Object indexToRemove = iterator.next();
					topList.remove(((Integer) indexToRemove).intValue());
					if (!otherValuesList.isEmpty()) {
						topList.add(otherValuesList.remove(0));
					}
				}

				Collections.reverse(indexesToMove);
				for (Iterator iterator = indexesToMove.iterator(); iterator.hasNext();) {
					Object indexToMove = iterator.next();
					otherValuesList.add(topList.remove(((Integer) indexToMove).intValue()));
					if (!otherValuesList.isEmpty()) {
						topList.add(otherValuesList.remove(0));
					}
				}

				results.setResultData(topList);

				for (Iterator iterator = otherValuesList.iterator(); iterator.hasNext();) {
					Object[] next = (Object[]) iterator.next();
					long recCount = next[sumParamIndex] != null ? (long) next[sumParamIndex] : 0;
					otherSum += recCount;
				}

				Object[] any = (Object[]) dataList.get(0);
				Object[] other = new Object[any.length];
				other[sumParamIndex] = otherSum;
				other[groupByParameterIndex] = "Other";

				results.getResultData().add(other);
			} else if (groupByParameter != null
				&& groupByParameter.equals(HSQLParts.getEntityFieldbyParameter(ParameterNames.LANGUAGE))) {

				if (entityTableProperties.equals(EntityTablesProperties.CUBE_ENTRY_DOCS_LANGS)) {
					emptyIfAllResultsAreZero(dataList);
				}
				
				//If group by language 
				//display only editors languages and group everything else under 'Other' value
				int sumParamIndex = 0;
				for (String header : headers) {
					if (header.toUpperCase().startsWith("SUM")) {
						sumParamIndex = headers.indexOf(header);
						break;
					}
				}

				List indexesToMove = new ArrayList();
				List otherValuesList = new ArrayList();

				int i = 0;
				for (Iterator iterator = dataList.iterator(); iterator.hasNext();) {
					Object[] dataListItem = (Object[]) iterator.next();
					String groupByValue = (String) dataListItem[groupByParameterIndex];
					if (groupByValue == null
						|| !ServicesStatisticsConstants.EDITORS_LANGUAGES.contains(groupByValue)) {
						indexesToMove.add(i);
					}
					i++;
				}

				Collections.reverse(indexesToMove);
				for (Iterator iterator = indexesToMove.iterator(); iterator.hasNext();) {
					Object indexToMove = iterator.next();
					otherValuesList.add(dataList.remove(((Integer) indexToMove).intValue()));
				}

				results.setResultData(dataList);

				long otherSum = 0;
				for (Iterator iterator = otherValuesList.iterator(); iterator.hasNext();) {
					Object[] item = (Object[]) iterator.next();
					long recCount = item[sumParamIndex] != null ? (long) item[sumParamIndex] : 0;
					otherSum += recCount;
				}

				if (otherSum > 0 && !dataList.isEmpty()) {
					Object[] any = (Object[]) dataList.get(0);
					Object[] other = new Object[any.length];
					other[sumParamIndex] = otherSum;
					other[groupByParameterIndex] = "Other";

					results.getResultData().add(other);
				}
			} else if (groupByParameter != null
				&& (groupByParameter.equals(HSQLParts.getEntityFieldbyParameter(ParameterNames.MLANGUAGE))
				|| groupByParameter.equals(HSQLParts.getEntityFieldbyParameter(ParameterNames.OLANGUAGE)))) {
				
				//Replace NULL by 'None' in view by mlang and view by olang charts
				for (Iterator iterator = dataList.iterator(); iterator.hasNext();) {
					Object[] next = (Object[]) iterator.next();
					String groupByValue = (String) next[groupByParameterIndex];
					if (StringUtils.isBlank(groupByValue)) {
						next[groupByParameterIndex] = "None";
					}
				}
				
				results.setResultData(dataList);
				
			} else if (entityTableProperties.equals(EntityTablesProperties.CUBE_ENTRY_DOCS)
				|| entityTableProperties.equals(EntityTablesProperties.CUBE_ENTRY_DOCS_LANGS)) {
			
				emptyIfAllResultsAreZero(dataList);
				
				results.setResultData(dataList);

			} else {
				results.setResultData(dataList);
			}

			results.setResultHeaders(headers);
			results.setResponseResult(new ResponseResult(ResponseStatusMessage.RESPONSE_200, ""));

		} catch (Exception e) {
			LOG.error("HibernateStatisticsServicesFetcher:log - Failed to fetch from " + entityName, e);
			results.setResponseResult(new ResponseResult(ResponseStatusMessage.RESPONSE_500, "Failed to fetch data"));
		}

		properties.setResults(results);

		LogResponseResult(results.getResponseResult());
	}

	/**
	 * if all results are 0s then empty results list to display no results message
	 * @param dataList 
	 */
	private void emptyIfAllResultsAreZero(List<Object> dataList) {
		int zeroResultsCount = 0;
		for (Iterator iterator = dataList.iterator(); iterator.hasNext();) {
			Object[] next = (Object[]) iterator.next();
			Long value = (Long) next[0];
			if (value == 0) {
				zeroResultsCount++;
			}
		}
		
		if (dataList.size() == zeroResultsCount) {
			dataList.clear();
		}
	}

	private String getGroupByParameter(QueryProperties properties, boolean isVisitsDownloadsTable) {
		String groupByParameter = null;
		for (QueryParameter parameter : properties.getParameterList()) {
			if (parameter.getParameterName().equals(ParameterNames.GROUP_BY)) {
				String parameterValue = parameter.getValueProperties().getValue();
				ParameterNames param = ParameterNames.match(parameterValue);
				groupByParameter = isVisitsDownloadsTable
					? HSQLParts.getVisitsDownloadsEntityFieldbyParameter(param)
					: HSQLParts.getEntityFieldbyParameter(param);
				break;
			}
		}
		return groupByParameter;
	}

	private static void LogResponseResult(ResponseResult result) {
		ResponseInfo info = result.getResponseInfo();
		LOG.info("Result: " + result.getDetails().getMessage() + "|" + info.getCode() + "|" + info.getCause() + "|" + info.getStatus());
	}
}
