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

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonError.ErrorInfo;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.services.analytics.model.GaData;
import com.google.api.services.analytics.model.GaData.ColumnHeaders;
import com.google.inject.Inject;

import europass.ewa.tools.ga.enums.ErrorTypesRecommendations;
import europass.ewa.tools.ga.errors.GAJsonResponseError;
import europass.ewa.tools.ga.exceptions.AbstractGAStepExecuteException;
import europass.ewa.tools.ga.exceptions.InterruptExecutionException;
import europass.ewa.tools.ga.exceptions.RetryExecutionException;
import europass.ewa.tools.ga.executor.GAStepsExecutorVisitor;
import europass.ewa.tools.ga.info.GAStatisticsInfo;
import europass.ewa.tools.ga.logger.DatabaseGAStatisticsLogger;
import europass.ewa.tools.ga.logger.GAStatisticsLogger;
import europass.ewa.tools.ga.manager.TableManager;
import europass.ewa.tools.ga.manager.dao.TableManagerDao;
import europass.ewa.tools.utils.GAExecuteMailSenderImpl;

public class GARetrieveConsumeStoreStep extends AbstractGAStatisticsStep {

	private static final Logger LOG = LoggerFactory.getLogger(GARetrieveConsumeStoreStep.class);
	private static final int MAX_PAGE_RESULTS = 1000;
	private GAStatisticsLogger databaseLogger;
	
	private GAStepsExecutorVisitor visitor;
	private int retries; 
	
	@Inject
	public GARetrieveConsumeStoreStep(DatabaseGAStatisticsLogger databaseLogger, GAStepsExecutorVisitor visitor, GAExecuteMailSenderImpl mailSender){
		
		super(mailSender);
		
		this.databaseLogger = databaseLogger;
		this.visitor = visitor;
		retries = 0;
	}

	public int getRetries() {
		return retries;
	}

	@Override
	public void setRetries(int retries) {
		this.retries = retries;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public boolean execute() throws AbstractGAStepExecuteException {

		if(this.gaStatisticsinfo == null)
			throw new InterruptExecutionException(new NullPointerException("GAStatisticsInfo object is null"));
		
		try{
			
			// Execute query to Google Analytics, get results and store them to the db
			for(TableManager mgr : this.gaStatisticsinfo.getTableManagers()){
		
				TableManagerDao tmdao = (TableManagerDao)mgr.getTableDao();
				
				for(int i = 1; ; i += MAX_PAGE_RESULTS){
				
					GaData gaData = mgr.request(this.gaStatisticsinfo, i, MAX_PAGE_RESULTS);

					if(gaData.getRows() == null)
						break;
					
					mgr.consume(gaData);
					
					if(databaseLogger != null){
						tmdao.persist(databaseLogger);
					}
					
					printGaData(gaData, this.gaStatisticsinfo);
				}
			}
		}
		catch (GoogleJsonResponseException e) {
			
			ErrorInfo errorInfo = e.getDetails().getErrors().get(0);
			
			// throw RetryExecutionException in special cases			
	        if (errorInfo.getReason().equals("rateLimitExceeded") || errorInfo.getReason().equals("userRateLimitExceeded")) {
	        	throw new RetryExecutionException(e);
	        }
	        else{
	        	throw new InterruptExecutionException(e);
	        }
			
		} catch (Exception e) {
			throw new InterruptExecutionException(e);
		}
		
		return onSuccess();
	}

	@Override
	public boolean onSuccess() {
		return true;
	}

	@Override
	public void onFailure(AbstractGAStepExecuteException e) {

		String causeClassName = e.getThrowableName(true);
		String causeMessage = e.getThrowableCauseMessage();

		GAJsonResponseError error = ErrorTypesRecommendations.getError("500", ErrorTypesRecommendations.uknownError, causeMessage);

		switch(causeClassName){
		
		case "GoogleJsonResponseException":
			GoogleJsonError jsonError = ((GoogleJsonResponseException)e.getCause()).getDetails();
		
			if(jsonError != null){
			
				ErrorInfo errorInfo = jsonError.getErrors().get(0);
				
				causeMessage = errorInfo.getMessage();
				
				ErrorTypesRecommendations type = ErrorTypesRecommendations.get(errorInfo.getReason());
				error.configure(""+jsonError.getCode(), type, causeMessage);
				
			}
			break;
		case "IllegalArgumentException":
			ErrorTypesRecommendations type = ErrorTypesRecommendations.get("illegalArguments");
			error.configure("603", type, causeMessage);
			break;
		default:
			break;
		}
		
//		IllegalArgumentException
		
		Map<String, GAJsonResponseError> errorsMap = this.mailSender.getErrorsMap();
		
		if(!errorsMap.containsKey(causeClassName)){
			errorsMap.put(causeClassName, error);
		}
		
		LOG.error("Exception "+causeClassName+" thrown with message '"+causeMessage+"'");
		
		failedExecutionNotify();
	}
	
	@Override
	public void doStep(GAStatisticsInfo info) throws RuntimeException {
		super.setGaStatisticsInfo(info);
		visitor.visit(this);
		super.doStep(info);
	}

	private static void printGaData(GaData results, GAStatisticsInfo info) {
		LOG.info("printing results for profile: " + results.getProfileInfo().getProfileName());

		if (results.getRows() == null || results.getRows().isEmpty()) {
			LOG.info("No results Found.");
			return;
		}
		else {

			StringBuilder builder = new StringBuilder();
			
			String lineSep = System.getProperty("line.separator");
			
			// Print column headers.
			for (ColumnHeaders header : results.getColumnHeaders()) {
				builder.append(String.format("%25s",header.getName()));
			}
			// Print actual data.
			builder.append(lineSep);
			for (List<String> row : results.getRows()) {
				for (String column : row) {
					builder.append(String.format("%25s",column));
				}
				builder.append(lineSep);
			}
			LOG.info(lineSep+builder.toString()+lineSep);
		}
	}

	private void failedExecutionNotify() {
		
		if(this.retries > 0){
			strBuilder.append("<p style=\"font-size:80%\"><i><u>NOTE:</u> Failed after retrying execution "+this.retries+" times</i></p>");
		}

		super.sendMail(this.gaStatisticsinfo.getYear(),this.gaStatisticsinfo.getMonth(),this.gaStatisticsinfo.getDay());
	}
}
