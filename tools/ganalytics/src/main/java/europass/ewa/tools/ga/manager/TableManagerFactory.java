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

import europass.ewa.tools.ga.info.QueryInfo;
import europass.ewa.tools.ga.manager.dao.TableManagerDao;
import europass.ewa.tools.ga.manager.data.HibernateDownloads;
import europass.ewa.tools.ga.manager.data.HibernateVisits;

public abstract class TableManagerFactory {

	@SuppressWarnings("rawtypes")
	public static TableManager getTableManager(String table){
		
		switch(table){
		
			case "visits":
				TableManager<TableManagerDao<HibernateVisits>> vmgr = new VisitsManager(new QueryInfo());
				return vmgr;
	
			case "downloads":
				TableManager<TableManagerDao<HibernateDownloads>> dmgr = new DownloadsManager(new QueryInfo());
				return dmgr;
				
			default: return null;
		}
	}
}
