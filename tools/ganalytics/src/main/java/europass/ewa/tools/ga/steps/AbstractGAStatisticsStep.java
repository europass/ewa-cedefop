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

import europass.ewa.tools.ga.executor.GAStepsExecutor;
import europass.ewa.tools.ga.info.GAStatisticsInfo;
import europass.ewa.tools.utils.GAExecuteMailSenderImpl;

public abstract class AbstractGAStatisticsStep implements GAStatisticsStep, GAStepsExecutor{

	private GAStatisticsStep nextStep = null;
	
	private static final String SUBJECT_PREFIX = "GA Execution Errror: [ANALYTICS] Attention: failed to fetch data from GA for ";
	
	protected StringBuilder strBuilder = null;
	protected GAStatisticsInfo gaStatisticsinfo = null;
	
	protected final GAExecuteMailSenderImpl mailSender;
	
	public AbstractGAStatisticsStep(GAExecuteMailSenderImpl sender){
		setStrBuilder(new StringBuilder());
		mailSender = sender;
	}
	
	/* (non-Javadoc)
	 * @see europass.ewa.tools.ga.steps.GAStatisticsStep#setNext(europass.ewa.tools.ga.steps.GAStatisticsStep)
	 */
	@Override
	public void setNext(GAStatisticsStep nextStep) {
		this.nextStep = nextStep;
	}

	/* (non-Javadoc)
	 * @see europass.ewa.tools.ga.steps.GAStatisticsStep#doStep(europass.ewa.tools.ga.info.GAStatisticsInfo)
	 */
	@Override
	public void doStep(GAStatisticsInfo info) throws RuntimeException {
		if ( nextStep != null ){
			nextStep.doStep( info );
		}
	}
	
	public void setGaStatisticsInfo(GAStatisticsInfo info){
		gaStatisticsinfo = info;
	}

	public GAStatisticsInfo getGaStatisticsInfo(){
		return gaStatisticsinfo;
	}

	public StringBuilder getStrBuilder() {
		return strBuilder;
	}

	public void setStrBuilder(StringBuilder strBuilder) {
		this.strBuilder = strBuilder;
	}
	
	public void sendMail(int year, int month, int day) {
		
//		String emailSubjectPrefix = "[STATS] Attention, failed to fetch data from GA for ";
		String emailBody = mailSender.constructFailureMessage(strBuilder);
		mailSender.constructAndSendStandardMailWithPeriod(this.gaStatisticsinfo.getYear(),this.gaStatisticsinfo.getMonth(),this.gaStatisticsinfo.getDay(), SUBJECT_PREFIX, emailBody);
	}
}
