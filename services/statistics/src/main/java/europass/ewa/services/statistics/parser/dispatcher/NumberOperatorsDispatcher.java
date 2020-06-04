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

import europass.ewa.services.statistics.enums.request.ValueOperators;
import europass.ewa.services.statistics.enums.request.ValueTypes;
import europass.ewa.services.statistics.structures.ValidationResult;
import europass.ewa.services.statistics.structures.ValuePropertiesBuilder;

public class NumberOperatorsDispatcher extends ElementsDispatcher {

	public NumberOperatorsDispatcher(ElementsDispatcher nextInChain, String parameterName, String value, ValueOperators operator) {
		super(nextInChain, parameterName, value, operator);
	}

	public ValidationResult dispatch(ValuePropertiesBuilder builder, ValueTypes type) {

		if (type.equals(ValueTypes.NUMBER_AND) || type.equals(ValueTypes.NUMBER_OR) || type.equals(ValueTypes.NUMBER_NOT)) {

			if (type.equals(ValueTypes.NUMBER_NOT)) {
				value = value.replaceAll("!", "");
			}

			String[] intValuesArray = value.split(operator.getRegex());

			if (intValuesArray.length == 1) {
				builder.withValueOperator(ValueOperators.NONE);
			}

			List<Integer> intValuesList = new ArrayList<Integer>();
			for (String intStr : intValuesArray) {

				if (!Strings.isNullOrEmpty(intStr)) {
					intValuesList.add(Integer.parseInt(intStr));
				}
			}

			builder.withValueType(type).withIntegerValueList(intValuesList);
			return new ValidationResult.Builder(true).build();
		}

		return super.dispatch(builder, type);
	}

}
