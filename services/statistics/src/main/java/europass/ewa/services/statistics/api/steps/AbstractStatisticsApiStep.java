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
package europass.ewa.services.statistics.api.steps;

import com.google.inject.Inject;

import europass.ewa.services.statistics.api.info.QueryInfo;
import europass.ewa.services.statistics.parser.ParametersParser;
import europass.ewa.services.statistics.structures.QueryProperties;
import europass.ewa.services.statistics.structures.QueryResults;
import europass.ewa.services.statistics.validators.factory.ValidatorFactory;

public abstract class AbstractStatisticsApiStep implements StatisticsApiStep {

	protected static ValidatorFactory VALIDATOR_FACTORY;
	
	protected AbstractStatisticsApiStep nextStep;
	protected QueryInfo info;
	protected QueryProperties qProperties;
	protected QueryResults results;
	
	protected ParametersParser parametersParser;

	@Inject
	public AbstractStatisticsApiStep() {}

	@Inject
	public AbstractStatisticsApiStep(ValidatorFactory factory){
		VALIDATOR_FACTORY = factory;
	}
	
	@Inject
	public AbstractStatisticsApiStep(ParametersParser parser) {
		this.parametersParser = parser;
	}

	@Override
	public void setNext(AbstractStatisticsApiStep step) {
		
		this.nextStep = step;
	}

	@Override
	public void doStep() {
		
		if(info.isValidated()){
			if ( nextStep != null ){
				nextStep.setQueryInfo(info);
				nextStep.setQueryProperties(qProperties);
				nextStep.doStep();
			}
		}
	}
	
	public void setQueryInfo(QueryInfo info) {
		this.info = info;
	}

	public void setQueryProperties(QueryProperties props) {
		this.qProperties = props;
	}

	public QueryInfo getStatisticsApiInfo() {
		return info;
	}

	public void setResults(QueryResults res) {
		this.results = res;
	}
	
	public QueryResults getResults() {
		return results;
	}
}
