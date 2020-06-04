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

import java.util.List;

import europass.ewa.tools.ga.enums.HibernateTablesTypes;
import europass.ewa.tools.ga.exceptions.InterruptExecutionException;
import europass.ewa.tools.ga.info.DateRange;
import europass.ewa.tools.ga.logger.GAStatisticsLogger;

public interface TableManagerDao<T>{

	public void persist(GAStatisticsLogger logger);
	public void clean(GAStatisticsLogger logger) throws InterruptExecutionException;
	
	public List<T> getAllRecords();
	
	public void addRecord(T record);
	public T getRecord(int index);
	public void updateRecord(T record);
	public void deleteRecord(T record);
	public int getRecordsTotalVolume();
	
	public HibernateTablesTypes getType();
	public void setType(HibernateTablesTypes type);
	
	public DateRange getDateRange();
	public void setDateRange(DateRange dateRange);

}
