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
package europass.ewa.services.statistics.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import europass.ewa.services.statistics.enums.request.ParameterNames;
import europass.ewa.services.statistics.enums.request.QueryPrefixes;
import europass.ewa.services.statistics.enums.request.ValueOperators;
import europass.ewa.services.statistics.enums.request.ValueTypes;
import europass.ewa.services.statistics.mappings.structures.ParameterValueTypesMappings;
import europass.ewa.services.statistics.parser.dispatcher.DispatcherChain;
import europass.ewa.services.statistics.structures.QueryParameter;
import europass.ewa.services.statistics.structures.ValidationResult;
import europass.ewa.services.statistics.structures.ValueProperties;
import europass.ewa.services.statistics.structures.ValuePropertiesBuilder;

/**
 * ParametersParser
 * Parses the parameters information stored in the QueryInfo object 
 *
 * @author pgia
 *
 */
@Singleton
public class ParametersParser implements GenericParser<Map<String, String>, String, String, QueryParameter> {

	private ValidationResult result;
	private ParameterValueTypesMappings parameterValueMappings;

	@Inject
	ParametersParser(ParameterValueTypesMappings mappings) {
		parameterValueMappings = mappings;
	}

	@Override
	public List<QueryParameter> parse(Object object) {
		return null;
	}

	/**
	 * 3 stepped parsing procedure
	 *
	 * @param map QueryInfo's QueryParameters Map
	 * @param extra QueryInfo's QueryPrefix Object
	 *
	 * @return List of QueryParameter structures that hold the information about
	 * the given Query Parameters
	 */
	@Override
	public List<QueryParameter> parseMap(Map<String, String> map, Object extra) {

		List<QueryParameter> parametersList = new ArrayList<>();

		if (extra instanceof QueryPrefixes) {

			QueryPrefixes queryPrefix = (QueryPrefixes) extra;

			for (String paramName : map.keySet()) {

				QueryParameter qParameter = new QueryParameter(ParameterNames.match(paramName), queryPrefix);

				// Step 1: get value for parameter
				String value = map.get(paramName);

				// Step 2: Locate the value type
				ValueTypes type;
				if (Strings.isNullOrEmpty(value)) {
					type = (parameterValueMappings.getMappingsFor(ParameterNames.match(paramName))).get(0);
				} else {
					type = ValueTypes.match(value);
				}

				// Step 3: Build the ValueProperty of the QueryParameter
				ValueProperties property = constructValueProperties(paramName, value, type);

				if (!result.getSuccess()) {
					return parametersList;
				}

				qParameter.setValueProperties(property);
				parametersList.add(qParameter);
			}

		}
		return parametersList;

	}

	/**
	 * Actual construction of the value properties
	 *
	 * @param parameter Query Parameter's Name
	 * @param value Query Parameter's Value
	 * @param type Query Parameter's Type
	 * @return ValueProperties builder
	 */
	private ValueProperties constructValueProperties(String parameter, String value, ValueTypes type) {

		ValueOperators operator = ValueOperators.detectOperatorByName(type.name());

		ValuePropertiesBuilder builder = new ValuePropertiesBuilder();
		builder.withValueOperator(operator);

		result = DispatcherChain.execute(builder, parameter, value, type, operator);

		return builder.build();
	}

	@Override
	public List<QueryParameter> parseMap(Map<String, String> map, Object[] extra) {
		return new ArrayList<>();
	}

	public ParameterValueTypesMappings getParametersMappings() {
		return parameterValueMappings;
	}

	public void setParametersMappings(ParameterValueTypesMappings parametersMappings) {
		this.parameterValueMappings = parametersMappings;
	}

	public ValidationResult getValidationResult() {
		return result;
	}

	public void setValidationResult(ValidationResult result) {
		this.result = result;
	}

}
