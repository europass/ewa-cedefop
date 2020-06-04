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

import com.google.common.base.Strings;

import europass.ewa.services.statistics.constants.ServicesStatisticsConstants;
import europass.ewa.services.statistics.enums.request.ValueOperators;
import europass.ewa.services.statistics.enums.request.ValueTypes;
import europass.ewa.services.statistics.enums.validation.ValidationErrors;
import europass.ewa.services.statistics.enums.values.DateValueDepthEnum;
import europass.ewa.services.statistics.structures.DateValueRange;
import europass.ewa.services.statistics.structures.ValidationResult;
import europass.ewa.services.statistics.structures.ValuePropertiesBuilder;

public class DateDispatcher extends ElementsDispatcher {

	public DateDispatcher(ElementsDispatcher nextInChain, String parameterName, String value, ValueOperators operator) {
		super(nextInChain, parameterName, value, operator);
	}

	public ValidationResult dispatch(ValuePropertiesBuilder builder, ValueTypes type) {

		if (type.equals(ValueTypes.DATE)) {

			DateValueRange dateTimeRange = super.makeDateRange(value, null);

			if (!dateTimeRange.isValid()) {

				ValidationResult.Builder resultBuilder = new ValidationResult.Builder(false).withFailedOn(value);

//				if(dateTimeRange.isSingle())
				resultBuilder.withValidationErrors(ValidationErrors.QUERY_PARAMETER_DATE_INVALID);
//				else
//					resultBuilder.withValidationErrors(ValidationErrors.QUERY_PARAMETER_DATE_RANGE_INVALID);

				return resultBuilder.build();
			}

			// If value is empty, convert to date range
			if (Strings.isNullOrEmpty(value)) {
				List<DateValueRange> dateRangeList = new ArrayList<>();
				dateRangeList.add(dateTimeRange);
				builder.withValueType(ValueTypes.DATE_RANGE).withDateValueRangeList(dateRangeList).withDateValueDepth(DateValueDepthEnum.YEAR).build();
			} else if (value.matches(ServicesStatisticsConstants.DATE_YEAR_REGEXP)) {
				builder.withValueType(type).withDateValue(dateTimeRange.getFrom()).withDateValueDepth(DateValueDepthEnum.YEAR).build();
			} else if (value.matches(ServicesStatisticsConstants.DATE_YEAR_MONTH_REGEXP)) {
				builder.withValueType(type).withDateValue(dateTimeRange.getFrom()).withDateValueDepth(DateValueDepthEnum.YEAR_MONTH).build();
			} else if (value.matches(ServicesStatisticsConstants.DATE_YEAR_MONTH_DAY_REGEXP)) {
				builder.withValueType(type).withDateValue(dateTimeRange.getFrom()).withDateValueDepth(DateValueDepthEnum.YEAR_MONTH_DAY).build();
			} else {
				builder.withValueType(type).withDateValue(dateTimeRange.getFrom()).build();
			}

//			if(dateTimeRange.isSingle()){
//				builder.withValueType(type).withDateValue(dateTimeRange.getFrom()).build();
//			}else{
//				
//				List<DateValueRange> dateRangeList = new ArrayList<DateValueRange>();
//				dateRangeList.add(dateTimeRange);
//				builder.withValueType(ValueTypes.DATE_RANGE).withDateValueRangeList(dateRangeList).build();
//			}
			return new ValidationResult.Builder(true).build();
		} else if (type.equals(ValueTypes.DATE_RANGE)) {

			String[] fromToArray = value.split("-");
			if (fromToArray.length == 2) { // only one date range
				DateValueRange dateTimeRange = super.makeDateRange(fromToArray[0], fromToArray[1]);

				List<DateValueRange> dateRangeList = new ArrayList<>();
				dateRangeList.add(dateTimeRange);

				if (value.matches(ServicesStatisticsConstants.DATE_YEAR_RANGE_REGEXP)) {
					builder.withValueType(type).withDateValue(dateTimeRange.getFrom()).withDateValueDepth(DateValueDepthEnum.YEAR).build();
				} else if (value.matches(ServicesStatisticsConstants.DATE_YEAR_MONTH_RANGE_REGEXP)) {
					builder.withValueType(type).withDateValue(dateTimeRange.getFrom()).withDateValueDepth(DateValueDepthEnum.YEAR_MONTH).build();
				} else if (value.matches(ServicesStatisticsConstants.DATE_YEAR_MONTH_DAY_RANGE_REGEXP)) {
					builder.withValueType(type).withDateValue(dateTimeRange.getFrom()).withDateValueDepth(DateValueDepthEnum.YEAR_MONTH_DAY).build();
				} else {
					builder.withValueType(ValueTypes.DATE_RANGE).withDateValueRangeList(dateRangeList).build();
				}
			}
		}

		return super.dispatch(builder, type);
	}
}
