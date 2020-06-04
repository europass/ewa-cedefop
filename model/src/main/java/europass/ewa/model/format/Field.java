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
package europass.ewa.model.format;

abstract class Field<T> {

    String format(T object) {
        StringBuilder sb = new StringBuilder();
        format(sb, object);
        return sb.toString();
    }

    boolean format(StringBuilder into, boolean previous, T object) {
        return format(into, object);
    }

    boolean appendStringNotEmpty(StringBuilder sb, String str) {
        if (str != null && !str.isEmpty()) {
            sb.append(str);
            return true;
        }
        return false;
    }

    abstract boolean format(StringBuilder sb, T object);

}
