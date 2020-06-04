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
package europass.ewa.services.statistics.mappings.structures;

import java.util.List;
import java.util.Map;

/**
 * Validation Mappings Interface used to map a given type to a List of another type's items.
 * Utilized by validators to check parameters and value semantics
 * 
 * Currently used for:
 * - mapping parameters against valid values types 
 * - mapping values against valid values expressions
 *  
 * @author pgia
 *
 * @param <S> The map key object class
 * @param <T> The list item object class
 * @param <U> The List<T>
 * @param <V> The Map<S,List<T>>
 */
public interface ValidationMappings <S, T, U extends List<T>, V extends Map<S, U>>{

	V getMappings();
	U getMappingsFor(S s);
}
