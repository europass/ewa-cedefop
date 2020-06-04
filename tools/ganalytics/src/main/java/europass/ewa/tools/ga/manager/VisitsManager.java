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
import java.util.List;

import org.joda.time.DateTime;

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.analytics.Analytics.Data.Ga.Get;
import com.google.api.services.analytics.model.GaData;

import europass.ewa.tools.ga.info.GAStatisticsInfo;
import europass.ewa.tools.ga.info.QueryInfo;
import europass.ewa.tools.ga.manager.dao.TableManagerDao;
import europass.ewa.tools.ga.manager.dao.VisitsManagerDaoDBImpl;
import europass.ewa.tools.ga.manager.data.HibernateVisits;
import europass.ewa.tools.utils.Utils;

public class VisitsManager implements TableManager<TableManagerDao<HibernateVisits>>{

	private static final String METRICS = "ga:visits";
	private static final String DIMENSIONS = "ga:country,ga:year,ga:month,ga:day";
	private static final String SORT = "ga:year,ga:month,ga:day,ga:country";
	
	private QueryInfo qInfo;
	private GaData gaData;
	private TableManagerDao<HibernateVisits> tableDao;
	
	public VisitsManager(QueryInfo qInfo){
		
		this.qInfo = qInfo;
		
		qInfo.setMetrics(METRICS);
		qInfo.setDimensions(DIMENSIONS);
		qInfo.setSort(SORT);
		
		tableDao = new VisitsManagerDaoDBImpl();
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
	public TableManagerDao<HibernateVisits> getTableDao() {
		return tableDao;
	}

	@Override
	public void setTableDao(TableManagerDao<HibernateVisits> visitsDao) {
		this.tableDao = visitsDao;
	}
	
	@Override
	public GaData request(GAStatisticsInfo gaInfo, int offset, int rows) throws GoogleJsonResponseException, IOException {

		// construct basic query: profile id, Start Date, End Date, Metrics

		Get response = gaInfo.getAnalytics().data().ga().get("ga:" + gaInfo.getProfileId(), // Table Id. ga: + profile id.
				this.qInfo.asGetText(this.qInfo.getStartDate()), // Start exact_date.
				this.qInfo.asGetText(this.qInfo.getEndDate()), // End exact_date.
				this.qInfo.getMetrics()); // Metrics. "ga:pageviews"
		
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

		// Get query dimensions
		String dimensions = results.getQuery().getDimensions();
		
		if(dimensions == null || metrics == null || metrics.size() == 0)
			return;
		
		if(!dimensions.equals(this.qInfo.getDimensions()) || !metrics.get(0).equals((this.qInfo.getMetrics())))
			return;

		storeBeansList(results);
		
	}
	
	private void storeBeansList(GaData results){
		
		// Parse results and place to Beans
		for (List<String> row : results.getRows()) {
			
			String country = row.get(0);
			String visits = row.get(4);
			
			HibernateVisits vBean = new HibernateVisits();

			DateTime date = new DateTime(Integer.valueOf(row.get(1)),Integer.valueOf(row.get(2)),Integer.valueOf(row.get(3)),0,0);
			vBean.setDate(date.toDate());
			
			vBean.setYear(Integer.valueOf(row.get(1)));
			vBean.setMonth(Integer.valueOf(row.get(2)));
			vBean.setDay(Integer.valueOf(row.get(3)));
			vBean.setIso_country_code(Utils.getCountryIsoCode(country));
			vBean.setVolume(Integer.valueOf(visits));
			
			tableDao.addRecord(vBean);
		}
		
	}
}

