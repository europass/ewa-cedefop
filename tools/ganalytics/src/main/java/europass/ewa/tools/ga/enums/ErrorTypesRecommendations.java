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
package europass.ewa.tools.ga.enums;

import europass.ewa.tools.ga.errors.GAJsonResponseError;

public enum ErrorTypesRecommendations {

	// Error code 400
	invalidParameter("Do not retry without fixing the problem. You need to provide a valid value for the parameter specified in the error response."),
	badRequest("Do not retry without fixing the problem. You need to make changes to the API query in order for it to work."),

	// Error code 401	
	invalidCredentials("Do not retry without fixing the problem. You need to get a new auth token."),

	// Error code 403
	insufficientPermissions("Do not retry without fixing the problem. You need to get sufficient permissions to perform the operation on the specified entity."),
	dailyLimitExceeded("Do not retry without fixing the problem. You have used up your daily quota. See <a href=\"/analytics/devguides/reporting/core/v3/limits-quotas\"> API Limits and Quotas</a>."),
	rateLimitExceeded("Do not retry without fixing the problem. You need to register in Developers Console to get the full API quota."),
	userRateLimitExceeded("Retry using <a href=\"https://developers.google.com/analytics/devguides/config/mgmt/v3/errors#backoff\">exponential back-off</a>. You need to slow down the rate at which you are sending the requests."),
	quotaExceeded("Retry using <a href=\"https://developers.google.com/analytics/devguides/config/mgmt/v3/errors#backoff\">exponential back-off</a>. You need to wait for at least one in-progress request for this profile to complete."),
	
	// Error code 503
	backendError("Do not retry this query more than once."),
	
	// Error code 600
	fileNotFound("Check the filename and path of the configuration files"),
	// Error code 601
	endOfFile("Check if the private key and/or json client secrets files are corrupted"),
	// Error code 602
	IOError("Check the .properties and .json file contents for consistency"),
	// Error code 603	
	illegalArguments("Check GA request arguments for consistency"),	
	
	// Error code 700	
	privateKeyErrors("Private Key related issues, recreate the private key and retry"),
	// Error code 701
	invalid_grant("Check the europass-webapps-tools-ga.properties configuration (Hint: properties pattern application.auth.*)"),
	// Error code 702
	socketConnectionTimeout("Increase the value of the property europass-ewa-tools-ganalytics.ga.connection.timeout (reccommended value 60000) in the europass-webapps-tools-ga.properties file. If the problem persists, retry running the script again after some time."),
	// Error code 703
	socketReadTimeout("Increase the value of the property europass-ewa-tools-ganalytics.ga.read.timeout (reccommended value 60000) in the europass-webapps-tools-ga.properties file. If the problem persists, retry running the script again after some time."),
	
	// Error code 800
	databaseError(""),
	
	// Error code 500
	uknownError("");

	String recommendation;
	
	ErrorTypesRecommendations(String r){
		recommendation = r;
	}

	public String getRecommendation() {
		return recommendation;
	}

	public void setRecommendation(String recommendation) {
		this.recommendation = recommendation;
	}

	public static ErrorTypesRecommendations get(String name){
		
		for(ErrorTypesRecommendations etr : ErrorTypesRecommendations.values()){
			
			if(etr.name().equals(name))
				return etr;
		}
		
		return uknownError;
	}
	
	public static GAJsonResponseError getError(String code, ErrorTypesRecommendations type, String message){
		
		return new GAJsonResponseError(code, type, message);
	}
}
