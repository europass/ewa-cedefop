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

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.security.GeneralSecurityException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.model.Accounts;
import com.google.api.services.analytics.model.Profiles;
import com.google.api.services.analytics.model.Webproperties;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import europass.ewa.tools.ga.enums.ErrorTypesRecommendations;
import europass.ewa.tools.ga.errors.GAJsonResponseError;
import europass.ewa.tools.ga.exceptions.AbstractGAStepExecuteException;
import europass.ewa.tools.ga.exceptions.InterruptExecutionException;
import europass.ewa.tools.ga.exceptions.RetryExecutionException;
import europass.ewa.tools.ga.executor.GAStepsExecutor;
import europass.ewa.tools.ga.executor.GAStepsExecutorVisitor;
import europass.ewa.tools.ga.info.GAStatisticsInfo;
import europass.ewa.tools.utils.GAExecuteMailSenderImpl;

public class GAPreperationStep extends AbstractGAStatisticsStep implements GAStepsExecutor{

	private static final Logger LOG = LoggerFactory.getLogger(GAPreperationStep.class);
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
	
	private static NetHttpTransport HTTP_TRANSPORT;
	
	static int CONNECTION_TIMEOUT;
	static int READ_TIMEOUT;
	
	private GAStepsExecutorVisitor visitor;
	private int retries;
	
	@Inject
	public GAPreperationStep(
			@Named("europass-ewa-tools-ganalytics.ga.connection.timeout") String gaConnectionTimeOut,
			@Named("europass-ewa-tools-ganalytics.ga.read.timeout") String gaReadTimeOut,
			GAStepsExecutorVisitor visitor,
			GAExecuteMailSenderImpl sender ) {

		super(sender);
		
		CONNECTION_TIMEOUT = Integer.valueOf(gaConnectionTimeOut);
		READ_TIMEOUT = Integer.valueOf(gaReadTimeOut);
		
		this.visitor = visitor;
		
	}
	
	public int getRetries() {
		return retries;
	}

	@Override
	public void setRetries(int retries) {
		this.retries = retries;
	}
	
	@Override
	public boolean execute() throws AbstractGAStepExecuteException{

		try {
			Analytics analytics = initializeAnalytics(this.gaStatisticsinfo.getCredentials(), this.gaStatisticsinfo.getApplicationName(), CONNECTION_TIMEOUT, READ_TIMEOUT);
			
			this.gaStatisticsinfo.setAnalytics(analytics);
//			LOG.info("Application Name: "+info.getAnalytics().getApplicationName());
			
			String profileId = getFirstProfileId(analytics);
			this.gaStatisticsinfo.setProfileId(profileId);
			LOG.info("User "+this.gaStatisticsinfo.getServiceAccountID()+" authorized for Google Analytics Application "+this.gaStatisticsinfo.getApplicationName());
			LOG.info("Profile ID: "+this.gaStatisticsinfo.getProfileId());

		} catch (Exception e) {
			
			LOG.error("Error: "+e.getClass().getCanonicalName()+" - "+e.getMessage());

			if(e instanceof SocketTimeoutException)
				throw new RetryExecutionException(e);

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
		
		case "TokenResponseException":
			causeMessage = "";
			error = ErrorTypesRecommendations.getError("701", ErrorTypesRecommendations.invalid_grant, causeMessage);
			break;

		case "SocketTimeoutException":
//			causeMessage = "";
			if(causeMessage.contains("connect"))
				error = ErrorTypesRecommendations.getError("702", ErrorTypesRecommendations.socketConnectionTimeout, causeMessage);
			if(causeMessage.contains("Read"))
				error = ErrorTypesRecommendations.getError("703", ErrorTypesRecommendations.socketReadTimeout, causeMessage);
			break;

		default:
			break;
		}
		
		Map<String, GAJsonResponseError> errorsMap = this.mailSender.getErrorsMap();
		
		if(!errorsMap.containsKey(causeClassName)){
			errorsMap.put(causeClassName, error);
		}

		LOG.error("Exception "+causeClassName+" thrown with message '"+causeMessage+"'");
		
		failedExecutionNotify();
	}
	
	/* (non-Javadoc)
	 * @see europass.ewa.tools.ga.steps.AbstractGAStatisticsStep#doStep(europass.ewa.tools.ga.info.GAStatisticsInfo)
	 */
	@Override
	public void doStep(GAStatisticsInfo info) throws RuntimeException {
		
		super.setGaStatisticsInfo(info);
		visitor.visit(this);
		super.doStep(info);
	}
	
	private static Analytics initializeAnalytics(
			GoogleCredential credentials, 
			String appName,
			int connectionTimeout,
			int readTimeout) throws GeneralSecurityException, IOException{
    
		final GoogleCredential CREDENTIAL = credentials;
		
		HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
		
		final int connection_timeout = connectionTimeout;
		final int read_timeout = readTimeout;	    	    	
		
		// Set up and return Google Analytics API client.
	    return new Analytics.Builder(HTTP_TRANSPORT, JSON_FACTORY, credentials).setApplicationName(appName)
	    	    .setHttpRequestInitializer(new HttpRequestInitializer() {
    			  @Override
	  	          public void initialize(HttpRequest httpRequest) throws IOException {
	  	        	CREDENTIAL.initialize(httpRequest);
	  	            httpRequest.setConnectTimeout(connection_timeout);	  	        	
	  	            httpRequest.setReadTimeout(read_timeout);
	  	          }
	  	        })
	    		.build();
	}
	
	private static String getFirstProfileId(Analytics analytics) throws IOException {
		String profileId = null;

		// Query accounts collection.
		Accounts accounts = analytics.management().accounts().list().execute();

		LOG.info("Found "+accounts.getItems().size()+" accounts.");

		if (accounts.getItems().isEmpty()) {
			LOG.error("No accounts where found");
			throw new RuntimeException();
		} 

		String firstAccountId = accounts.getItems().get(0).getId();
			
		// Query webproperties collection.
		Webproperties webproperties = analytics.management().webproperties().list(firstAccountId).execute();
		
		if (webproperties.getItems().isEmpty()) {
			LOG.error("No Webproperties found");
			throw new RuntimeException();

		} else {
			String firstWebpropertyId = webproperties.getItems().get(0).getId();

			// Query profiles collection.
			Profiles profiles = analytics.management().profiles().list(firstAccountId, firstWebpropertyId).execute();
			
			if (profiles.getItems().isEmpty()) {
				LOG.error("No profiles found");
				throw new RuntimeException();
			} 
			else {
				profileId = profiles.getItems().get(0).getId();
			}
		}
		
		return profileId;
	}

	private void failedExecutionNotify() {
		
		if(this.retries > 0){
			strBuilder.append("<p style=\"font-size:80%\"><i><u>NOTE:</u> Failed after retrying execution "+this.retries+" times</i></p>");
		}
		
		super.sendMail(this.gaStatisticsinfo.getYear(),this.gaStatisticsinfo.getMonth(),this.gaStatisticsinfo.getDay());
	}
}
