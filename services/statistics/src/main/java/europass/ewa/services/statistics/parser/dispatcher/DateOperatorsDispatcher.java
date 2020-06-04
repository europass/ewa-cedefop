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
package europass.ewa.services.statistics.parser.dispatcher;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import europass.ewa.services.statistics.constants.ServicesStatisticsConstants;
import europass.ewa.services.statistics.enums.request.ValueOperators;
import europass.ewa.services.statistics.enums.request.ValueTypes;
import europass.ewa.services.statistics.enums.validation.ValidationErrors;
import europass.ewa.services.statistics.enums.values.DateValueDepthEnum;
import europass.ewa.services.statistics.structures.DateValueRange;
import europass.ewa.services.statistics.structures.ValidationResult;
import europass.ewa.services.statistics.structures.ValuePropertiesBuilder;

public class DateOperatorsDispatcher extends ElementsDispatcher {

	public DateOperatorsDispatcher(ElementsDispatcher nextInChain, String parameterName, String value, ValueOperators operator) {

		super(nextInChain, parameterName, value, operator);
	}

	public ValidationResult dispatch(ValuePropertiesBuilder builder, ValueTypes type) {

		if (type.equals(ValueTypes.DATE_AND) || type.equals(ValueTypes.DATE_OR) || type.equals(ValueTypes.DATE_NOT)) {

			List<DateTime> dateTimeList = new ArrayList<>();

			List<DateValueRange> dateRangeList = new ArrayList<>();
			DateValueDepthEnum dateValueDepth = null;

			if (type.equals(ValueTypes.DATE_NOT)) {
				value = value.replaceAll("!", "");
			}

			String[] datesArray = value.split(operator.getRegex());

			if (datesArray.length == 1) {
				builder.withValueOperator(ValueOperators.NONE);
			}

			for (String dateStr : datesArray) {

				DateValueRange range = super.makeDateRange(dateStr, null);

				if (!dateRangeList.contains(range)) {
					dateRangeList.add(range);
				}
				
				DateValueDepthEnum rangeDateValueDepth = null;
				if (dateStr.matches(ServicesStatisticsConstants.DATE_YEAR_REGEXP)) {
					rangeDateValueDepth = DateValueDepthEnum.YEAR;
				} else if (dateStr.matches(ServicesStatisticsConstants.DATE_YEAR_MONTH_REGEXP)) {
					rangeDateValueDepth = DateValueDepthEnum.YEAR_MONTH;
				} else if (dateStr.matches(ServicesStatisticsConstants.DATE_YEAR_MONTH_DAY_REGEXP)) {
					rangeDateValueDepth = DateValueDepthEnum.YEAR_MONTH_DAY;
				}

				if (dateValueDepth == null) {
					dateValueDepth = rangeDateValueDepth;
				} else if (rangeDateValueDepth != null && rangeDateValueDepth.compareTo(dateValueDepth) > 0) {
					dateValueDepth = rangeDateValueDepth;
				}
			}

			if (DateValueRange.isSingleDateValueList(dateRangeList)) {
				
				for (DateValueRange d : dateRangeList) {

					if (!(d.isValid())) {
						return new ValidationResult.Builder(false)
							.withFailedOn(value)
							.withValidationErrors(ValidationErrors.QUERY_PARAMETER_DATE_INVALID)
							.build();
					}

					DateTime dateTime = d.getFrom();

					if (!dateTimeList.contains(dateTime)) {
						dateTimeList.add(dateTime);
					}
				}
				
				builder.withValueType(type).withDateValueList(dateTimeList).withDateValueDepth(dateValueDepth);
			} else {
				builder.withValueType(ValueTypes.DATE_RANGE).withDateValueRangeList(dateRangeList).withDateValueDepth(dateValueDepth);
			}
				
			/*if(!Strings.isNullOrEmpty(dateStr)){
				DateTime dateTime = super.getDateTimeValue(dateStr,true);
				if(dateTime == null){
					return new ValidationResult.Builder(false)
					.withFailedOn(value)
					.withValidationErrors(ValidationErrors.QUERY_PARAMETER_DATE_INVALID)
					.build();
				}

				dateTimeList.add(dateTime);
			}*/
			
			// TODO: change type to date range with calculations
			
			return new ValidationResult.Builder(true).build();
		}	
			
		return super.dispatch(builder, type);
	}
}
