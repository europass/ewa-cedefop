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

import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.analytics.model.GaData;

import europass.ewa.tools.ga.info.GAStatisticsInfo;
import europass.ewa.tools.ga.info.QueryInfo;
import europass.ewa.tools.ga.manager.dao.TableManagerDao;

@SuppressWarnings("hiding")
public interface TableManager<TableManagerDao> {

	public void setQueryInfo(QueryInfo info);
	public QueryInfo getQueryInfo();
	public void setGaData(GaData data);
	public GaData getGaData();
	public void setTableDao(TableManagerDao info);
	public TableManagerDao getTableDao();

	public GaData request(GAStatisticsInfo gaInfo, int offset, int rows) throws GoogleJsonResponseException, IOException;
	public void consume(GaData data);
}
