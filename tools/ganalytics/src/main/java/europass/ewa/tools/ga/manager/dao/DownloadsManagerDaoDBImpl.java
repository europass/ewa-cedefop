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
package europass.ewa.tools.ga.manager.dao;

import java.util.ArrayList;
import java.util.List;

import europass.ewa.tools.ga.enums.HibernateTablesTypes;
import europass.ewa.tools.ga.exceptions.InterruptExecutionException;
import europass.ewa.tools.ga.info.DateRange;
import europass.ewa.tools.ga.logger.GAStatisticsLogger;
import europass.ewa.tools.ga.manager.data.HibernateDownloads;

public class DownloadsManagerDaoDBImpl implements TableManagerDao<HibernateDownloads>{

	private List<HibernateDownloads> downloadsList;
	private DateRange dateRange;
	private HibernateTablesTypes type;
	
	public DownloadsManagerDaoDBImpl() {
		this.downloadsList = new ArrayList<HibernateDownloads>();
		this.setType(HibernateTablesTypes.HibernateDownloads);
	}

	@Override
	public HibernateTablesTypes getType() {
		return this.type;
	}

	@Override
	public void setType(HibernateTablesTypes type) {
		this.type = type;
	}
	
	@Override
	public void persist(GAStatisticsLogger databaseLogger) {
		
		databaseLogger.log( HibernateTablesTypes.HibernateDownloads, this.downloadsList, this.dateRange );
		this.downloadsList.clear();
	}
	
	@Override
	public void clean(GAStatisticsLogger databaseLogger) throws InterruptExecutionException{
		
		databaseLogger.deleteMassive( HibernateTablesTypes.HibernateDownloads, this.dateRange );
	}
	
	@Override
	public List<HibernateDownloads> getAllRecords() {

		return this.downloadsList;
	}

	@Override
	public void addRecord(HibernateDownloads record) {
		
		this.downloadsList.add(record);
	}

	@Override
	public HibernateDownloads getRecord(int index) {
		return downloadsList.get(index);
	}
	
	@Override
	public void updateRecord(HibernateDownloads record) {
		
		downloadsList.get(record.getId()).setDocument(record.getDocument());
		downloadsList.get(record.getId()).setType(record.getType());
		downloadsList.get(record.getId()).setIso_country_code(record.getIso_country_code());
		downloadsList.get(record.getId()).setIso_language_code(record.getIso_language_code());
		downloadsList.get(record.getId()).setYear(record.getYear());		
		downloadsList.get(record.getId()).setMonth(record.getMonth());
		downloadsList.get(record.getId()).setDay(record.getDay());
		downloadsList.get(record.getId()).setDate(record.getDate());
		downloadsList.get(record.getId()).setVolume(record.getVolume());
	}

	@Override
	public void deleteRecord(HibernateDownloads record) {
		downloadsList.remove(record.getId());
	}

	@Override
	public int getRecordsTotalVolume() {
		int total = 0;
		for(HibernateDownloads bean : this.getAllRecords())
			total += bean.getVolume();
		
		return total;
	}

	@Override
	public DateRange getDateRange() {
		return dateRange;
	}

	@Override
	public void setDateRange(DateRange dateRange) {
		this.dateRange = dateRange;
	}
}
