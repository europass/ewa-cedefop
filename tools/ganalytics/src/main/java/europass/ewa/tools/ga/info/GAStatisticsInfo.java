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
package europass.ewa.tools.ga.info;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.IllegalFieldValueException;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.services.analytics.Analytics;
import com.google.api.services.analytics.model.GaData;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import europass.ewa.tools.ga.manager.TableManager;

public class GAStatisticsInfo {

	private String applicationName;
	private GoogleCredential credentials;
	private Analytics analytics;
	private String profileId;
	
	private GaData results;
	
	private String privateKeyFileName;
	private String clientSecretFileName;
	private String serviceAccountID;
	
	private int day = 0;
	private int month = 0;
	private int year = 0;
	
	@SuppressWarnings("rawtypes")
	private List<TableManager> tableManagers;

	@SuppressWarnings("rawtypes")
	@Inject
	public GAStatisticsInfo(
			@Named("application.name") String applicationName, 
			@Named("application.auth.key.path") String privateKeyFileName, 
			@Named("application.auth.user") String serviceAccountID, 
			@Named("application.auth.client.secret") String clientSecretFileName) {
		
		this.applicationName = applicationName;
		this.setPrivateKeyFileName(privateKeyFileName);
		this.setServiceAccountID(serviceAccountID);
		this.setClientSecretFileName(clientSecretFileName);
		
		tableManagers = new ArrayList<TableManager>();
	}
	
	public GoogleCredential getCredentials() {
		return credentials;
	}

	public void setCredentials(GoogleCredential credentials) {
		this.credentials = credentials;
	}

	public Analytics getAnalytics() {
		return analytics;
	}

	public void setAnalytics(Analytics analytics) {
		this.analytics = analytics;
	}

	public String getApplicationName() {
		return applicationName;
	}

	public void setApplicationName(String applicationName) {
		this.applicationName = applicationName;
	}

	public String getProfileId() {
		return profileId;
	}

	public GaData getResults() {
		return results;
	}

	@SuppressWarnings("rawtypes")
	public List<TableManager> getTableManagers() {
		return tableManagers;
	}

	@SuppressWarnings("rawtypes")
	public void setTableManagers(List<TableManager> tableManagers) {
		this.tableManagers = tableManagers;
	}
	
	public void setResults(GaData results) {
		this.results = results;
	}

	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}

	public String getPrivateKeyFileName() {
		return privateKeyFileName;
	}

	public void setPrivateKeyFileName(String privateKeyFileName) {
		this.privateKeyFileName = privateKeyFileName;
	}

	public String getClientSecretFileName() {
		return clientSecretFileName;
	}

	public void setClientSecretFileName(String clientSecretFileName) {
		this.clientSecretFileName = clientSecretFileName;
	}
	
	public String getServiceAccountID() {
		return serviceAccountID;
	}

	public void setServiceAccountID(String serviceAccountID) {
		this.serviceAccountID = serviceAccountID;
	}

	public int getDay() {
		return day;
	}

	public void setDay(int day) {
		this.day = day;
	}
	
	public int getMonth() {
		return month;
	}

	public void setMonth(int month) {
		this.month = month;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public void setDate(int[] date){
		
		// check nulls and size
		if(date != null){
			
			switch(date.length){
			
			case 3:
				handleDate(date[0],date[1],date[2]);
				break;

			case 2:
				handleDate(date[0],date[1],0);
				break;

			case 1:
				handleDate(date[0],0,0);
				break;
				
			default:
				handleDate(0,0,0);
				break;
			
			}
		}
	}
	
	/**
	 * If only month and year is given it will construct the period
	 * between the first and the last day of the month
	 * 
	 * @return String[]
	 */
	
	public DateTime[] constructPeriod(){
		
		DateTime[] periodArray = new DateTime[2];

		DateTime dateFrom = new DateTime(2005,1,1,0,0);
		DateTime dateTo = dateFrom;

		// When there is year defined
		if(this.year > 0){
			
			// When there is month defined
			if(this.month > 0){
				
				// When there is day defined
				if(this.day > 0){
					
					dateFrom = dateFrom.withYear(this.year).withMonthOfYear(this.month).withDayOfMonth(this.day);
					dateTo = dateFrom;
				}
				else{	
					
					dateFrom = dateFrom.withYear(this.year).withMonthOfYear(this.month);
					
					if(this.year == DateTime.now().getYear() && this.month == DateTime.now().getMonthOfYear()){
						
						// case is the first day of month
						if(DateTime.now().getDayOfMonth() == 1)
							dateTo = dateFrom;
						else
							dateTo = dateFrom.withDayOfMonth(DateTime.now().minusDays(1).getDayOfMonth());
						
						dateFrom = dateFrom.withDayOfMonth(1);
					}
					else
						dateTo = dateFrom.dayOfMonth().withMaximumValue();
				}
			}
			else{
				dateFrom = dateFrom.withYear(this.year).withMonthOfYear(1).withDayOfMonth(1);
				
				
				if(this.year == DateTime.now().getYear()){
					dateTo = DateTime.now().minusDays(1);
				}
				else{
					dateTo = dateFrom.monthOfYear().withMaximumValue();
					dateTo = dateTo.dayOfMonth().withMaximumValue();
				}
			}
		}
		
		periodArray[0] = dateFrom;
		periodArray[1] = dateTo;
		
		return periodArray;
	}
	
	/**
	 * Used when year is current and month, day are not set
	 *  - in case the month is January, it will set month to December (12) and year to current-1 (Last Year)
	 *  - requires that this.year and this.month are set
	 */

	@SuppressWarnings("unused")
	private void setToPreviousYear(){
		
		if(this.year == 0)
			return;
		
		this.year = this.year - 1;
		
	}
	
	
	/**
	 * Used when year and month is current and day is not set
	 *  - in case the month is January, it will set month to December (12) and year to current-1 (Last Year)
	 *  - requires that this.year and this.month are set
	 */

	@SuppressWarnings("unused")
	private void setToPreviousMonth(){
		
		if(this.year == 0 || this.month == 0)
			return;
		
		if(this.month == 1){
			this.month = 12;
			this.year = this.year - 1;
		}
		else{
			this.month = this.month - 1;
		}
	}

	/**
	 * Used when year, month and day is current
	 *  - in case the month is January the 1st, it will set day to 31st, month to December (12) and year to current-1 (Last Year)
	 *  - in case the day is the 1st, it will set the month to the previous and the day to the last day of this month
	 *  - requires that this.year, this.month and this.day are set
	 */

	@SuppressWarnings("unused")
	private void setToPreviousDay(){
		
		if(this.year == 0 || this.month == 0 || this.day == 0)
			return;
		
		if(this.day == 1){
			if(this.month == 1){
				this.day = 31;
				this.month = 12;
				this.year = this.year - 1;
			}
			else{
				this.month = this.month - 1;
				
				DateTime dateTo = new DateTime(this.year,this.month,1,0,0).dayOfMonth().withMaximumValue();
				this.day = dateTo.getDayOfMonth();
			}
		}
		else{
			this.day = this.day - 1;
		}
	}
	
	private void handleDate(int year, int month, int day){
		
		DateTime yesterday = new DateTime().minusDays(1); 
 
		// If wrong exact_date arguments are passed, handle the exception and set the exact_date with starting date 1
		try{
			new DateTime(year,month,day,0,0);
			
			this.year = year;
			this.month = month;
			this.day = day;
			
		}catch(IllegalFieldValueException e){
		
			if(day == 0 && month != 0 && year >= 2005){
				this.year = year;
				this.month = month;
			}
			else if(month == 0  && year >= 2005){
				this.year = year;
			}
			else{
				this.year = yesterday.getYear();
				this.month = yesterday.getMonthOfYear();
				this.day = yesterday.getDayOfMonth();
			}

			return;
		}
	}
	
}
