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

@Singleton
public class ValuesParametersParser implements GenericParser<Map<String, String>, String, String, QueryParameter> {

	private ValidationResult result;
	private ParameterValueTypesMappings parameterValueMappings;

	@Inject
	ValuesParametersParser(ParameterValueTypesMappings mappings) {
		parameterValueMappings = mappings;
	}

	@Override
	public List<QueryParameter> parse(Object object) {
		return null;
	}

	@Override
	public List<QueryParameter> parseMap(Map<String, String> map, Object extra) {

		List<QueryParameter> parametersList = new ArrayList<QueryParameter>();

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

	private ValueProperties constructValueProperties(String parameter, String value, ValueTypes type) {

		ValueOperators operator = ValueOperators.detectOperatorByName(type.name());

		ValuePropertiesBuilder builder = new ValuePropertiesBuilder();
		builder.withValueOperator(operator);

		result = DispatcherChain.execute(builder, parameter, value, type, operator);

		return builder.build();
	}

	
/*	private ValueProperties buildValueProperty(ValueTypes type, String value){
		
		result = new ValidationResult.Builder(true).build();
		
		ValueOperators operator = ValueOperators.detectOperatorByName(type.name());
		
		// TODO: make interfaces, classes etc
		// Detect the value-related member of the ValuePropertiesBuilder that will be set
		
		ValuePropertiesBuilder builder = new ValuePropertiesBuilder();
		
		builder.withValueType(type).withValueOperator(operator);
		
		if(type.equals(ValueTypes.DATE)){
			
			builder.withDateValue(getDateFromParameter(value,false));
			if(!result.getSuccess())
				return null;
		}
		if(type.equals(ValueTypes.DATE_AND) || type.equals(ValueTypes.DATE_OR) || type.equals(ValueTypes.DATE_NOT)){
			
			String[] datesArray = value.split(operator.getRegex());
			
			List<DateTime> dateTimeList = new ArrayList<DateTime>();
			for(String dateStr : datesArray){
				
				DateTime dateTime = getDateFromParameter(dateStr,false);
				if(!result.getSuccess())
					return null;
				
				dateTimeList.add(dateTime);
			}
			
			builder.withDateValueList(dateTimeList);
		}
		if(type.equals(ValueTypes.DATE_RANGE)){
			
			List<DateValueRange> dateRangeList = new ArrayList<DateValueRange>();

			// First, split by operator (,) to get the ranges
			String[] rangesArray = value.split(operator.getDescription());

			for(String range : rangesArray){
				
				// For each range, split by '-' to get the from, to dates
				String[] fromToDatesArray = range.split("-");
				
				//TODO: validation of from to
				
				DateTime dateFrom = getDateFromParameter(fromToDatesArray[0],true);
				DateTime dateTo = getDateFromParameter(fromToDatesArray[1],false);
				if(!result.getSuccess())
					return null;
				
				if(dateFrom.isAfter(dateTo)){
					result = new ValidationResult.Builder(false)
													.withFailedOn(range)
													.withValidationErrors(ValidationErrors.QUERY_PARAMETER_DATE_RANGE_INVALID)
													.build();
					return null;
				}
				
				dateRangeList.add(new DateValueRange(dateFrom, dateTo));
				
			}
			builder.withDateValueRangeList(dateRangeList);
		}
		if(type.equals(ValueTypes.NUMBER)){
			builder.withIntValue(Integer.parseInt(value));
		}
		if(type.equals(ValueTypes.NUMBER_AND) || type.equals(ValueTypes.NUMBER_OR) || type.equals(ValueTypes.NUMBER_NOT)){
			
			String[] intValuesArray = value.split(operator.getRegex());
			
			List<Integer> intValuesList = new ArrayList<Integer>();
			for(String intStr : intValuesArray){
				
				intValuesList.add(Integer.parseInt(intStr));
			}
			builder.withIntegerValueList(intValuesList);
		}
		if(type.equals(ValueTypes.NUMBER_RANGE)){
			
			List<NumberValueRange> intValuesRangeList = new ArrayList<NumberValueRange>();

			// First, split by operator (,) to get the ranges
			String[] rangesArray = value.split(operator.getDescription());

			for(String range : rangesArray){
				
				// For each range, split by '-' to get the int values
				String[] intValuesArray = range.split("-");
				
				
				intValuesRangeList.add(
						new NumberValueRange(
								Integer.parseInt(intValuesArray[0]),
								Integer.parseInt(intValuesArray[1])
								));
				
			}
			builder.withIntegerValueRangeList(intValuesRangeList);
		}
		
		if(type.equals(ValueTypes.VALUE)){
			builder.withValue(value);
		}
		if(type.equals(ValueTypes.VALUE_AND) || type.equals(ValueTypes.VALUE_OR) || type.equals(ValueTypes.VALUE_NOT)){
		
			String[] valuesArray = value.split(operator.getDescription());
			
			List<String> valuesList = new ArrayList<String>();
			for(String str : valuesArray){
				
				valuesList.add(str);
			}
			builder.withStrValueList(valuesList);
		}
		
		ValueProperties property = builder.build();
		
		return property;
	}
	
	private DateTime getDateFromParameter(String value, boolean isFrom){
		
		String[] dateParts = value.split("\\.");
		int year, month, day;
		
		year = Integer.parseInt(dateParts[0]);
		if(dateParts.length > 1){
			month = Integer.parseInt(dateParts[1]);
		}else{
			if(isFrom)
				month = 1;
			else
				month = 12;
		}
		
		if(dateParts.length > 2){
			day = Integer.parseInt(dateParts[2]);
		}else{
			if(isFrom){
				day = 1;
			}else{
				
				DateTime givenDate = new DateTime().withYear(Integer.parseInt(dateParts[0])).withMonthOfYear(Integer.parseInt(dateParts[1]));
				day = givenDate.dayOfMonth().withMaximumValue().getDayOfMonth();
			}
		}
		
		DateTime date = null;
		
		try{
			date = new DateTime(year,month,day,0,0);
			
		}catch(org.joda.time.IllegalFieldValueException e){
			result = new ValidationResult.Builder(false)
											.withFailedOn(value)
											.withValidationErrors(ValidationErrors.QUERY_PARAMETER_DATE_INVALID)
											.build();
		}
		
		return date;
	}
*/
	@Override
	public List<QueryParameter> parseMap(Map<String, String> map, Object[] extra) {
		return new ArrayList<QueryParameter>();
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
