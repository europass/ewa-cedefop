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
package europass.ewa.services.statistics.api.process;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.PathSegment;

import com.google.common.base.Joiner;
import com.google.inject.Inject;

import europass.ewa.services.statistics.api.info.QueryInfo;
import europass.ewa.services.statistics.api.steps.EntityMatcherDataProduceStep;
import europass.ewa.services.statistics.api.steps.ParameterValuesValidationStep;
import europass.ewa.services.statistics.api.steps.ParserStep;
import europass.ewa.services.statistics.api.steps.RequestValidationStep;
import europass.ewa.services.statistics.enums.request.ParameterNames;
import europass.ewa.services.statistics.enums.request.QueryPrefixes;
import europass.ewa.services.statistics.enums.request.ResponseFormats;
import europass.ewa.services.statistics.enums.request.ValueOperators;
import europass.ewa.services.statistics.enums.validation.ValidationErrors;
import europass.ewa.services.statistics.structures.QueryProperties;
import europass.ewa.services.statistics.structures.ValidationResult;
import java.util.ArrayList;

/**
 * StatisticsApiRequestProcess
 *
 * Configure and  initiate the process of the query validation, execution and result
 *
 *  Step 1: RequestValidationStep - request parameters validation
 *  Step 2: ParameterValuesValidationStep - validation of certain enumeration parameter values
 *  Step 3: Parsing of values and specific value types validation
 *
 * @author pgia
 */
public class StatisticsApiRequestProcess {

	private RequestValidationStep step1;
	private ParameterValuesValidationStep step2;
	private ParserStep step3;
	private EntityMatcherDataProduceStep step4;

	@Inject
	public StatisticsApiRequestProcess(
		RequestValidationStep step1,
		ParameterValuesValidationStep step2,
		ParserStep step3,
		EntityMatcherDataProduceStep step4) {
		this.step1 = step1;
		this.step2 = step2;
		this.step3 = step3;
		this.step4 = step4;

		bindSteps();
	}

	public void process(QueryInfo info, QueryProperties props, String format, PathSegment requestPathSegment) {

		if (prepareStatisticsApiInfo(info, format, requestPathSegment)) {
			this.step1.setQueryInfo(info);

			props.setQueryPrefix(info.getQueryPrefix());
			props.setResponseFormat(info.getResponseFormat());

			this.step1.setQueryProperties(props);
			this.step1.doStep();
		}
	}

	private void bindSteps() {

		this.step1.setNext(this.step2);
		this.step2.setNext(this.step3);
		this.step3.setNext(this.step4);
	}

	/**
	 * Prepares the Statistics API Information object that is used by the steps and returns success status
	 *
	 * @param info
	 * @param format
	 * @param pathSegment
	 */
	private boolean prepareStatisticsApiInfo(QueryInfo info, String format, PathSegment pathSegment) {

		info.setResponseFormat(ResponseFormats.match(format));
		info.setQueryPrefix(QueryPrefixes.match(pathSegment.getPath()));

		MultivaluedMap<String, String> pathSegmentMap = pathSegment.getMatrixParameters();

		if (pathSegmentMap == null || pathSegmentMap.size() == 0) {
			info.setValidationResult(new ValidationResult.Builder(false)
				.withValidationErrors(ValidationErrors.QUERY_PARAMETERS_MISSING)
				.withFailedOn("missing-params")
				.build()
			);
			return false;
		} else {
			info.setQueryParametersValuesMap(normalizeQueryParameters(pathSegmentMap));
			return true;
		}
	}

	/**
	 * normalizeQueryParameters: eliminates duplicate query parameter values:
	 *
	 * - Introduces a Map to hold parameters and values
	 * - Iterates MultiValuedMap
	 * - Concatenates values List into value1,value2...valueN (AND operator)
	 * - Inserts parameters & concatenated List values to Map
	 *
	 * @param multiValuedMap the pathSegment Matrix Parameters
	 * @return Map
	 */
	private Map<String, String> normalizeQueryParameters(MultivaluedMap<String, String> multiValuedMap) {

		Map<String, String> parametersValuesMap = new HashMap<>();

		for (String paramName : multiValuedMap.keySet()) {

			List<String> paramValues = multiValuedMap.get(paramName);

			//combine all values for same param
			Joiner joiner = Joiner.on(ValueOperators.AND.getDescription());
			String value = joiner.join(paramValues);

			if (paramValues != null && paramValues.size() > 1) {
				//check and remove duplicates
				List<String> singleValues = new ArrayList<>();
				String[] allValues = value.split(ValueOperators.AND.getDescription());
				for (int i = 0; i < allValues.length; i++) {
					String value1 = allValues[i];
					if (!singleValues.contains(value1)) {
						singleValues.add(value1);
					}
				}
				value = joiner.join(singleValues);
			}

			parametersValuesMap.put(paramName, value);

			if (paramName.equals(ParameterNames.GROUP_BY.getDescription()) && !multiValuedMap.containsKey(value)) {
				parametersValuesMap.put(value, "");
			}
		}

		return parametersValuesMap;
	}

}
