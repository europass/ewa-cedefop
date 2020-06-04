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
package europass.ewa.tools.ga.process;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import com.google.inject.Inject;

import europass.ewa.tools.ga.enums.TableArgumentsTypes;
import europass.ewa.tools.ga.info.GAStatisticsInfo;
import europass.ewa.tools.ga.manager.TableManager;
import europass.ewa.tools.ga.manager.TableManagerFactory;
import europass.ewa.tools.ga.steps.GAAuthenticationStep;
import europass.ewa.tools.ga.steps.GACleanStep;
import europass.ewa.tools.ga.steps.GAPreperationStep;
import europass.ewa.tools.ga.steps.GARetrieveConsumeStoreStep;

public class GAStatisticsProcess {

	private GAAuthenticationStep step1;
	private GAPreperationStep step2;
	private GACleanStep step3;
	private GARetrieveConsumeStoreStep step4;

	@Inject
	public GAStatisticsProcess(
			GAAuthenticationStep step1,
			GAPreperationStep step2,
			GACleanStep step3,
			GARetrieveConsumeStoreStep step4){
		
		this.step1 = step1;
		this.step2 = step2;
		this.step3 = step3;
		this.step4 = step4;

		prepareScheduleSteps();
	}
	
	public void process(GAStatisticsInfo gaInfo, List<List<String>> args) throws RuntimeException{
		
		// instantiate here gaInfo
		prepareGAStatisticsInfo(gaInfo, args);
		step1.doStep(gaInfo);
	}
	
	private void prepareScheduleSteps(){
		
		this.step1.setNext(this.step2);
		this.step2.setNext(this.step3);
		this.step3.setNext(this.step4);
	}
	
	private void prepareGAStatisticsInfo(GAStatisticsInfo gaInfo, List<List<String>> args){

		setGAStatsInfoFilePaths(gaInfo, args.get(0));
		handleDateArgurments(gaInfo, args.get(1));
		handletablesArgurment(gaInfo, args.get(2));
		
	}

	private void setGAStatsInfoFilePaths(GAStatisticsInfo gaInfo, List<String> args){
		
		if(args.size() == 1){	

			String path = args.get(0);
			gaInfo.setPrivateKeyFileName(path + gaInfo.getPrivateKeyFileName());
			gaInfo.setClientSecretFileName(path + gaInfo.getClientSecretFileName());			
			
		}
	}
	
	@SuppressWarnings("rawtypes")
	private void handletablesArgurment(GAStatisticsInfo gaInfo, List<String> args){
		
		if(!args.contains(TableArgumentsTypes.visits.name()) && !args.contains(TableArgumentsTypes.downloads.name())){
			
			args = new ArrayList<String>();
			args.add(TableArgumentsTypes.visits.toString());
			args.add(TableArgumentsTypes.downloads.toString());
		}
		
		for(String arg : args){
			TableManager mgr = TableManagerFactory.getTableManager(arg);
			this.setQueryInfoDate(gaInfo, mgr);
			gaInfo.getTableManagers().add(mgr);
		}
	}

	private void handleDateArgurments(GAStatisticsInfo gaInfo, List<String> args){

		switch(args.size()){
		
			case 1:
				int[] dateYear = {Integer.valueOf(args.get(0))};
				gaInfo.setDate(dateYear);
				break;
		
			case 2:
				int[] dateYearMonth = {Integer.valueOf(args.get(0)), Integer.valueOf(args.get(1))};
				gaInfo.setDate(dateYearMonth);
				break;

			case 3:
				int[] dateYearMonthDay = {Integer.valueOf(args.get(0)), Integer.valueOf(args.get(1)), Integer.valueOf(args.get(2))};
				gaInfo.setDate(dateYearMonthDay);
				break;

			default:
				break;
				
		}
	}
	
	
	@SuppressWarnings({ "rawtypes" })
	private void setQueryInfoDate(GAStatisticsInfo stats, TableManager mgr){
		
		if(stats.getDay() > 0){

			DateTime date = new DateTime(stats.getYear(), stats.getMonth(), stats.getDay(), 0, 0);
			
			mgr.getQueryInfo().setStartDate(date);
			mgr.getQueryInfo().setEndDate(date);
		}
		else{
			
			DateTime[] dateArray = stats.constructPeriod();

			mgr.getQueryInfo().setStartDate(dateArray[0]);
			mgr.getQueryInfo().setEndDate(dateArray[1]);
		}
		
	}
}
