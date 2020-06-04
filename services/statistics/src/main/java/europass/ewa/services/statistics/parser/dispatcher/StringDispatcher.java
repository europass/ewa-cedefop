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

import europass.ewa.services.statistics.constants.ServicesStatisticsConstants;
import europass.ewa.services.statistics.enums.request.ParameterNames;
import europass.ewa.services.statistics.enums.request.ValueOperators;
import europass.ewa.services.statistics.enums.request.ValueTypes;
import europass.ewa.services.statistics.enums.validation.ValidationErrors;
import europass.ewa.services.statistics.structures.ValidationResult;
import europass.ewa.services.statistics.structures.ValuePropertiesBuilder;

public class StringDispatcher extends ElementsDispatcher {

	public StringDispatcher(ElementsDispatcher nextInChain, String name, String value, ValueOperators operator) {
		super(nextInChain, name, value, operator);
	}

	public ValidationResult dispatch(ValuePropertiesBuilder builder, ValueTypes type) {

		if (type.equals(ValueTypes.VALUE)) {

			// Special handling of groupby parameter which is populated as string value
			if (ParameterNames.match(parameterName).equals(ParameterNames.GROUP_BY)) {
				if (ParameterNames.match(value).equals(ParameterNames.INVALID)) {
					return new ValidationResult.Builder(false)
						.withValidationErrors(ValidationErrors.GROUP_BY_QUERY_PARAMETER_INVALID)
						.withFailedOn(ParameterNames.GROUP_BY.getDescription())
						.withAgainst(value)
						.build();
				}
			}

			// Special handling of orderby parameter which is populated as string value
			if (ParameterNames.match(parameterName).equals(ParameterNames.ORDER_BY)) {

				if (!value.matches(ValueTypes.ORDER_BY.getDescription())) {
					return new ValidationResult.Builder(false)
						.withValidationErrors(ValidationErrors.ORDER_BY_INVALID)
						.withFailedOn(value)
						.build();
				}

				String parameterValue = value.split("\\.")[0];

				if (!parameterValue.equals(ServicesStatisticsConstants.ORDER_BY_RESULTS)) {
					if (ParameterNames.match(parameterValue).equals(ParameterNames.INVALID)) {
						return new ValidationResult.Builder(false)
							.withValidationErrors(ValidationErrors.ORDER_BY_QUERY_PARAMETER_INVALID)
							.withFailedOn(ParameterNames.ORDER_BY.getDescription())
							.withAgainst(parameterValue)
							.build();
					}
				}
			}

			/**
			 * PGIA - EWA-1717 related: replace <GRC-QMARK> with ;
			 *
			 * Used for statistics query ui to avoid bad requests,
			 * as ; is used by the statistics api to seperate the
			 * parameters
			 */
			value = value.replaceAll("<GRC-QMARK>", ";");

			builder.withValueType(type).withValue(value);
			return new ValidationResult.Builder(true).build();
		}

		return super.dispatch(builder, type);
	}
}
