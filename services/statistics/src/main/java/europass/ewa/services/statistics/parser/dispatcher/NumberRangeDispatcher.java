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

import europass.ewa.services.statistics.enums.request.ValueOperators;
import europass.ewa.services.statistics.enums.request.ValueTypes;
import europass.ewa.services.statistics.enums.validation.ValidationErrors;
import europass.ewa.services.statistics.structures.NumberValueRange;
import europass.ewa.services.statistics.structures.ValidationResult;
import europass.ewa.services.statistics.structures.ValuePropertiesBuilder;

public class NumberRangeDispatcher extends ElementsDispatcher {

	public NumberRangeDispatcher(ElementsDispatcher nextInChain, String parameterName, String value, ValueOperators operator) {
		super(nextInChain, parameterName, value, operator);
	}

	public ValidationResult dispatch(ValuePropertiesBuilder builder, ValueTypes type) {

		if (type.equals(ValueTypes.NUMBER_RANGE)) {

			List<NumberValueRange> intValuesRangeList = new ArrayList<NumberValueRange>();

			// First, split by operator (,) to get the ranges
			String[] rangesArray = value.split(operator.getRegex());

			if (rangesArray.length == 1) {
				builder.withValueOperator(ValueOperators.NONE);
			}

			for (String range : rangesArray) {

				// For each range, split by '-' to get the int values
				String[] intValuesArray = range.split("-");

				int from = (intValuesArray[0].equals("min") ? -1 : Integer.parseInt(intValuesArray[0]));
				int to = (intValuesArray[1].equals("max") ? -1 : Integer.parseInt(intValuesArray[1]));

				if ((from > 0 && to > 0) && to < from) {
					return new ValidationResult.Builder(false)
						.withFailedOn(range)
						.withValidationErrors(ValidationErrors.QUERY_PARAMETER_NUMBER_RANGE_INVALID)
						.build();
				}

				intValuesRangeList.add(new NumberValueRange(from, to));

			}
			builder.withValueType(type).withIntegerValueRangeList(intValuesRangeList);
			return new ValidationResult.Builder(true).build();
		}
		return super.dispatch(builder, type);
	}

}
