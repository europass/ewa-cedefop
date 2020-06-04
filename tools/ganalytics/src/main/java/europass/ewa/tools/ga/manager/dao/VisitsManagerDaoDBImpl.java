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
import europass.ewa.tools.ga.manager.data.HibernateVisits;

public class VisitsManagerDaoDBImpl implements TableManagerDao<HibernateVisits>{

	private List<HibernateVisits> visitsList;
	private DateRange dateRange;
	private HibernateTablesTypes type;
	
	public VisitsManagerDaoDBImpl() {
		this.visitsList = new ArrayList<HibernateVisits>();
		this.setType(HibernateTablesTypes.HibernateVisits);
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
	public void persist(GAStatisticsLogger databaseLogger){

		databaseLogger.log(HibernateTablesTypes.HibernateVisits, this.visitsList, this.dateRange);
		this.visitsList.clear();
	}
	
	@Override
	public void clean(GAStatisticsLogger databaseLogger) throws InterruptExecutionException{
		
		databaseLogger.deleteMassive( HibernateTablesTypes.HibernateVisits, this.dateRange );
	}
	
	@Override
	public List<HibernateVisits> getAllRecords() {

		return this.visitsList;
	}

	@Override
	public void addRecord(HibernateVisits record) {
		
		this.visitsList.add(record);
	}

	@Override
	public HibernateVisits getRecord(int index) {
		return visitsList.get(index);
	}
	
	@Override
	public void updateRecord(HibernateVisits record) {
		
		visitsList.get(record.getId()).setIso_country_code(record.getIso_country_code());
		visitsList.get(record.getId()).setYear(record.getYear());	
		visitsList.get(record.getId()).setMonth(record.getMonth());
		visitsList.get(record.getId()).setDay(record.getDay());
		visitsList.get(record.getId()).setDate(record.getDate());
		visitsList.get(record.getId()).setVolume(record.getVolume());
	}

	@Override
	public void deleteRecord(HibernateVisits record) {
		
		visitsList.remove(record.getId());
	}

	@Override
	public int getRecordsTotalVolume() {
		int total = 0;
		for(HibernateVisits bean : this.getAllRecords())
			total += bean.getVolume();
		
		return total;
	}

	public DateRange getDateRange() {
		return dateRange;
	}

	public void setDateRange(DateRange dateRange) {
		this.dateRange = dateRange;
	}
}
