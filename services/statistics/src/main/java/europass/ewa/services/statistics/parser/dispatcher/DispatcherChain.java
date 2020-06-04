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

import europass.ewa.services.statistics.enums.request.ValueOperators;
import europass.ewa.services.statistics.enums.request.ValueTypes;
import europass.ewa.services.statistics.structures.ValidationResult;
import europass.ewa.services.statistics.structures.ValuePropertiesBuilder;

public class DispatcherChain {

	public static ValidationResult execute(ValuePropertiesBuilder builder, String parameter, String value, ValueTypes type, ValueOperators operator) {
		
		StringOperatorsDispatcher stringOperatorsDispatcher = new StringOperatorsDispatcher(null, parameter, value, operator);
		StringDispatcher stringDispatcher                   = new StringDispatcher(stringOperatorsDispatcher, parameter, value, operator);
		NumberRangeDispatcher numberRangeDispatcher         = new NumberRangeDispatcher(stringDispatcher, parameter, value, operator);
		NumberOperatorsDispatcher numberOperatorsDispatcher = new NumberOperatorsDispatcher(numberRangeDispatcher, parameter, value, operator);
		NumberDispatcher numberDispatcher                   = new NumberDispatcher(numberOperatorsDispatcher, parameter, value, operator);
		DateRangeDispatcher dateRangeDispatcher             = new DateRangeDispatcher(numberDispatcher, parameter, value, operator);
		DateOperatorsDispatcher dateOperatorsDispatcher     = new DateOperatorsDispatcher(dateRangeDispatcher, parameter, value, operator);
		DateDispatcher dateDispatcher                       = new DateDispatcher(dateOperatorsDispatcher, parameter, value, operator);

		return dateDispatcher.dispatch(builder, type);

	}
}
