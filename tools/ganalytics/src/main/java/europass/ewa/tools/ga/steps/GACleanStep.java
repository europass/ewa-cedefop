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
package europass.ewa.tools.ga.steps;

import java.util.Map;

import com.google.inject.Inject;

import europass.ewa.tools.ga.enums.ErrorTypesRecommendations;
import europass.ewa.tools.ga.errors.GAJsonResponseError;
import europass.ewa.tools.ga.exceptions.AbstractGAStepExecuteException;
import europass.ewa.tools.ga.exceptions.InterruptExecutionException;
import europass.ewa.tools.ga.info.DateRange;
import europass.ewa.tools.ga.info.GAStatisticsInfo;
import europass.ewa.tools.ga.logger.DatabaseGAStatisticsLogger;
import europass.ewa.tools.ga.manager.TableManager;
import europass.ewa.tools.ga.manager.dao.TableManagerDao;
import europass.ewa.tools.utils.GAExecuteMailSenderImpl;

public class GACleanStep extends AbstractGAStatisticsStep{

	private DatabaseGAStatisticsLogger databaseLogger; 
	
	@Inject
	public GACleanStep(
			DatabaseGAStatisticsLogger databaseLogger, 
			GAExecuteMailSenderImpl sender){
		
		super(sender);
		
		this.databaseLogger = databaseLogger;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public boolean execute() throws AbstractGAStepExecuteException {

		if(this.databaseLogger == null)
			throw new InterruptExecutionException(new NullPointerException("DatabaseGAStatisticsLogger object is null"));
		
		for(TableManager mgr : this.gaStatisticsinfo.getTableManagers()){
				
			TableManagerDao tmdao = (TableManagerDao)mgr.getTableDao();
	
			// Set the DateRange of the tableDao (used for the DatabaseGAStatistics deleteMassive)
			tmdao.setDateRange(new DateRange(mgr.getQueryInfo()));
		
			databaseLogger.deleteMassive(tmdao.getType(), tmdao.getDateRange());
		}

		return onSuccess();
	}

	@Override
	public boolean onSuccess() {
		return true;
	}

	@Override
	public void onFailure(AbstractGAStepExecuteException e) {

		String causeClassName = e.getClass().getCanonicalName();
		String causeMessage = e.getMessage();

		GAJsonResponseError error = ErrorTypesRecommendations.getError("800", ErrorTypesRecommendations.databaseError, causeMessage);
		
		Map<String, GAJsonResponseError> errorsMap = databaseLogger.getMailSender().getErrorsMap();
		
		if(!errorsMap.containsKey(causeClassName)){
			errorsMap.put(causeClassName, error);
		}
		
	}
	
	@Override
	public void doStep(GAStatisticsInfo info) throws RuntimeException {
		super.setGaStatisticsInfo(info);
		try {
			execute();
		} catch (AbstractGAStepExecuteException e) {
			onFailure(e);
		}
		super.doStep(info);
	}

	public void failedExecutionNotify() {
		
		super.sendMail(this.gaStatisticsinfo.getYear(),this.gaStatisticsinfo.getMonth(),this.gaStatisticsinfo.getDay());
	}

	@Override
	public void setRetries(int retries) {}
}
