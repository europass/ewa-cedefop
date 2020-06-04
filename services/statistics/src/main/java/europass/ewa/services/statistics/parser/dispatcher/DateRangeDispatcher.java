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

public class DateRangeDispatcher extends ElementsDispatcher {

	public DateRangeDispatcher(ElementsDispatcher nextInChain, String parameterName, String value, ValueOperators operator) {
		super(nextInChain, parameterName, value, operator);
	}

	public ValidationResult dispatch(ValuePropertiesBuilder builder, ValueTypes type) {

		if (type.equals(ValueTypes.DATE_RANGE)) {

			List<DateValueRange> dateRangeList = new ArrayList<>();
			DateValueDepthEnum dateValueDepth = null;

			// First, split by operator (,) to get the ranges
			String[] rangesArray = value.split(operator.getRegex());

			if (rangesArray.length == 1) {
				builder.withValueOperator(ValueOperators.NONE);
			}

			for (String range : rangesArray) {

				// For each range, split by '-' to get the from, to dates
				String[] fromToDatesArray = range.split("-");

				DateTime dateFrom = super.getDateTimeValue(fromToDatesArray[0], true);
				DateTime dateTo = super.getDateTimeValue(fromToDatesArray[1], false);
				if (dateFrom == null || dateTo == null) {
					return new ValidationResult.Builder(false)
						.withFailedOn(range)
						.withValidationErrors(ValidationErrors.QUERY_PARAMETER_DATE_RANGE_INVALID)
						.build();
				}

				if (dateFrom.isAfter(dateTo)) {
					return new ValidationResult.Builder(false)
						.withFailedOn(range)
						.withValidationErrors(ValidationErrors.QUERY_PARAMETER_DATE_RANGE_INVALID)
						.build();
				}
				
				DateValueDepthEnum rangeDateValueDepth = null;
				if (range.matches(ServicesStatisticsConstants.DATE_YEAR_RANGE_REGEXP)) {
					rangeDateValueDepth = DateValueDepthEnum.YEAR;
				} else if (range.matches(ServicesStatisticsConstants.DATE_YEAR_MONTH_RANGE_REGEXP)) {
					rangeDateValueDepth = DateValueDepthEnum.YEAR_MONTH;
				} else if (range.matches(ServicesStatisticsConstants.DATE_YEAR_MONTH_DAY_RANGE_REGEXP)) {
					rangeDateValueDepth = DateValueDepthEnum.YEAR_MONTH_DAY;
				}
				
				if (dateValueDepth == null) {
					dateValueDepth = rangeDateValueDepth;
				} else if (rangeDateValueDepth != null && rangeDateValueDepth.compareTo(dateValueDepth) > 0) { 
					dateValueDepth = rangeDateValueDepth;
				}
				
				dateRangeList.add(new DateValueRange(dateFrom, dateTo));
			}
			
			builder.withValueType(type).withDateValueRangeList(dateRangeList).withDateValueDepth(dateValueDepth);
			return new ValidationResult.Builder(true).build();
		}
		return super.dispatch(builder, type);
	}

}
