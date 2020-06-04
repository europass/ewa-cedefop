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

public final class NumericUtils {

    //Suppress default constructor for noninstantiability
    private NumericUtils() {
        throw new AssertionError();
    }

    private static final int ROUND_BASE = 10;

    private static final int ROUND_PRECISION = 5;

    /**
     * Converts a double to int
     *
     * @param number
     * @return
     */
    public static int asInt(double number) {
        return (int) (number + (ROUND_PRECISION * Math.pow(ROUND_BASE, -1)));
    }

    /**
     * Converts a double that is less that zero to an int multiplied by the
     * precision with base 10
     *
     * @param number
     * @param precision
     * @return
     */
    public static int asAugmentedInt(double number, int precision) {
        if (number > 1) {
            return asInt(number);
        }
        return (int) (((number + (ROUND_PRECISION * Math.pow(ROUND_BASE, -(precision + 1)))) * Math.pow(ROUND_BASE, precision)));
    }

}
