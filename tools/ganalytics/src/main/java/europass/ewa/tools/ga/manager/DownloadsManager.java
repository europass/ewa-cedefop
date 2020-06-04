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
package europass.ewa.tools.ga.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.analytics.Analytics.Data.Ga.Get;
import com.google.api.services.analytics.model.GaData;

import europass.ewa.tools.ga.enums.EventDataTypes;
import europass.ewa.tools.ga.info.GAStatisticsInfo;
import europass.ewa.tools.ga.info.QueryInfo;
import europass.ewa.tools.ga.manager.dao.DownloadsManagerDaoDBImpl;
import europass.ewa.tools.ga.manager.dao.TableManagerDao;
import europass.ewa.tools.ga.manager.data.HibernateDownloads;
import europass.ewa.tools.utils.Utils;

public class DownloadsManager implements TableManager<TableManagerDao<HibernateDownloads>>{

	private static final String METRICS = "ga:totalEvents";
	private static final String DIMENSIONS = "ga:eventCategory,ga:eventAction,ga:eventLabel,ga:year,ga:month,ga:day";
	private static final String SORT = "ga:year,ga:month,ga:day,ga:eventCategory,ga:eventAction,ga:eventLabel";
	
	private QueryInfo qInfo;
	private GaData gaData;
	private TableManagerDao<HibernateDownloads> tableDao;
	
	private Map<Integer,List<String>> map; 
	
	public DownloadsManager(QueryInfo qInfo){
		
		this.qInfo = qInfo;
		
		qInfo.setMetrics(METRICS);
		qInfo.setDimensions(DIMENSIONS);
		qInfo.setSort(SORT);
		
		this.tableDao = new DownloadsManagerDaoDBImpl();
	}

	@Override
	public GaData getGaData() {
		return gaData;
	}

	@Override
	public void setGaData(GaData gaData) {
		this.gaData = gaData;
	}

	@Override
	public void setQueryInfo(QueryInfo info) {
		this.qInfo = info;
	}

	@Override
	public QueryInfo getQueryInfo() {
		return this.qInfo;
	}

	@Override
	public TableManagerDao<HibernateDownloads> getTableDao() {
		return tableDao;
	}

	@Override
	public void setTableDao(TableManagerDao<HibernateDownloads> downloadsDao) {
		this.tableDao = downloadsDao;
	}
	
	@Override
	public GaData request(GAStatisticsInfo gaInfo, int offset, int rows) throws GoogleJsonResponseException , IOException {

		// construct basic query: profile id, Start Date, End Date, Metrics

		Get response = gaInfo.getAnalytics().data().ga().get("ga:" + gaInfo.getProfileId(), // Table Id. ga: + profile id.
				this.qInfo.asGetText(this.qInfo.getStartDate()), // Start exact_date.
				this.qInfo.asGetText(this.qInfo.getEndDate()), // End exact_date.
				this.qInfo.getMetrics()); // Category. "ga:totalEvents"
		
		// check for extra
		
		// Dimensions. "ga:country,ga:browser"
		if(this.qInfo.getDimensions() != null)
			response.setDimensions(this.qInfo.getDimensions());

		// Sort. "ga:country,ga:browser"
		if(this.qInfo.getSort() != null)
			response.setSort(this.qInfo.getSort());
		
		// Filters. "ga:country==Spain,ga:country==Greece"
		if(this.qInfo.getFilters() != null)
			response.setFilters(this.qInfo.getFilters());
		
		response.setStartIndex(offset);
		response.setMaxResults(rows);
		
		return response.execute();
	}
	
	@Override
	public void consume(GaData results) {

		if (results == null || results.getRows() == null || results.getRows().isEmpty())
			return;
		
		//Get query metrics
		List<String> metrics = results.getQuery().getMetrics();
		
		if (results.getRows() == null || results.getRows().isEmpty())
			return;
		
		// Get query dimensions
		String dimensions = results.getQuery().getDimensions();
		
		if(dimensions == null || metrics == null || metrics.size() == 0)
			return;
		
		if(!dimensions.equals(this.qInfo.getDimensions()) || !metrics.get(0).equals((this.qInfo.getMetrics())))
			return;
		
		map = parseData(results.getRows());
		storeBeans(map);
		
		clearMap(map);
	}
	
	private void storeBeans(Map<Integer, List<String>> map){
		
		for (List<String> list : map.values()) {

			HibernateDownloads dBean = new HibernateDownloads();
			dBean.setDocument(list.get(0));
			dBean.setType(list.get(1));

			DateTime date = new DateTime(Integer.parseInt(list.get(5)),Integer.parseInt(list.get(6)),Integer.parseInt(list.get(7)),0,0);
			dBean.setDate(date.toDate());
			
			dBean.setIso_language_code(list.get(2));
			dBean.setIso_country_code(list.get(3));
			dBean.setIp_country(list.get(4));

			dBean.setYear(Integer.parseInt(list.get(5)));
			dBean.setMonth(Integer.parseInt(list.get(6)));
			dBean.setDay(Integer.parseInt(list.get(7)));

			dBean.setVolume(Integer.parseInt(list.get(8)));
			
			tableDao.addRecord(dBean);
		}
	}
	
	private Map<Integer,List<String>> parseData(List<List<String>> rows){
		
		Map<Integer,List<String>> map = new HashMap<Integer,List<String>>(); 
		
		int index = 0;
		
		// array naming convention with UA function ga('send', 'event' ... )
		
		for(List<String> row : rows){
			
			// Object to put as map key
			List<String> parsed = new ArrayList<String>();
			
			/*
			 * parse document_type
			 * 
			 * array[0] => document
			 * array[1] => type
			 */

			Utils.splitEventData(parsed,row.get(0),EventDataTypes.UAEcategory);

			/*
			 * parse iso-language_iso-country
			 * 
			 * array[0] => iso-language
			 * array[1] => iso-country
			 */

			Utils.splitEventData(parsed,row.get(1),EventDataTypes.UAEaction);

			// retrieve and store country IP geolocation table
			String label = Utils.getCountryIsoCode(row.get(2));
			parsed.add(label);
			
			// Store Year, Month, Day
			parsed.add("" + row.get(3));
			parsed.add("" + row.get(4));
			parsed.add("" + row.get(5));
			
			parsed.add("" + row.get(6));

			map.put(Integer.valueOf(++index),parsed);
		}
		
		return map;
	}
	
	private void clearMap(Map<Integer,List<String>> map){
		
		for(List<String> list : map.values()){
			
			list.clear();
		}
		
		map.clear();
	}
}

