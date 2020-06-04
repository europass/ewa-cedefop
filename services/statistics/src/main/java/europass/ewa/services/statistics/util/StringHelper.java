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
package europass.ewa.services.statistics.util;

/**
 *
 * @author at
 */
public class StringHelper {

	/**
	 * Returns a new string resulting from replacing the last occurrence of
	 * toReplace in this string with replacement.
	 */
	public static String replaceLast(String string, String toReplace, String replacement) {
		StringBuffer sb = new StringBuffer(string);
		int index = sb.lastIndexOf(toReplace);
		if (index != -1 && replacement != null) {
			sb.delete(index, index + toReplace.length());
			sb.insert(index, replacement);
		}
		return sb.toString();
	}
}
