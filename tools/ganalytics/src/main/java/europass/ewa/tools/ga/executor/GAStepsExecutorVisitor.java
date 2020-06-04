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
package europass.ewa.tools.ga.executor;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import europass.ewa.tools.ga.exceptions.AbstractGAStepExecuteException;
import europass.ewa.tools.ga.exceptions.FailedAfterRetriesExecutionException;
import europass.ewa.tools.ga.exceptions.InterruptExecutionException;
import europass.ewa.tools.ga.exceptions.RetryExecutionException;
import europass.ewa.tools.ga.steps.AbstractGAStatisticsStep;
import europass.ewa.tools.ga.steps.GAPreperationStep;
import europass.ewa.tools.ga.steps.GARetrieveConsumeStoreStep;

public class GAStepsExecutorVisitor implements Visitor{

	int gaConnectionTimeOut;
	int gaReadTimeOut;
	int gaRetries;
	int gaRetriesDelayBase;
	int gaErrorCode;
	
	private static final Logger LOG = LoggerFactory.getLogger(GARetrieveConsumeStoreStep.class);
	
	@Inject
	public GAStepsExecutorVisitor(
			@Named("europass-ewa-tools-ganalytics.ga.connection.timeout") String gaConnectionTimeOut,
			@Named("europass-ewa-tools-ganalytics.ga.read.timeout") String gaReadTimeOut,
			@Named("europass-ewa-tools-ganalytics.ga.retries") String gaRetries,
			@Named("europass-ewa-tools-ganalytics.ga.retries.delay.base") String gaRetriesDelayBase){

		this.gaConnectionTimeOut = Integer.valueOf(gaConnectionTimeOut); 
		this.gaReadTimeOut = Integer.valueOf(gaReadTimeOut);
		this.gaRetries = Integer.valueOf(gaRetries);
		this.gaRetriesDelayBase = Integer.valueOf(gaRetriesDelayBase);
	}

	private int getRandomMillis(){
		return new Random().nextInt(1001);
	}

	@Override
	public void visit(AbstractGAStatisticsStep step) throws RuntimeException {
		
		if(step instanceof GAPreperationStep)
			step = (GAPreperationStep)step;
		if(step instanceof GARetrieveConsumeStoreStep)
			step = (GARetrieveConsumeStoreStep)step;
		
		int retries = 0;
		boolean succeeded = false;
		
		Exception ex = null;
		
		do{
			try {
				succeeded = step.execute();
			} catch (InterruptExecutionException e) {
				
				step.onFailure(e);
				throw new RuntimeException();

			} catch (RetryExecutionException e) {
	            
				ex = (Exception) e.getCause();
				
	           	// Apply exponential back-off.
            	int randomMillis = ((this.gaRetriesDelayBase << retries) * 1000) + getRandomMillis();
	            
            	LOG.info("Retry #"+retries+" after "+randomMillis);
				try {
					Thread.sleep(randomMillis);
				} catch (InterruptedException e1) {

					step.onFailure(new InterruptExecutionException(e1));
					throw new RuntimeException();
				}
				
				retries++;
			} catch (AbstractGAStepExecuteException e) {
				
				step.onFailure(e);
			}

		}while(!succeeded && retries <= this.gaRetries);

		if(!succeeded){
			step.setRetries(this.gaRetries);
			step.onFailure(new FailedAfterRetriesExecutionException(ex));
			throw new RuntimeException();
		}
	}
}
