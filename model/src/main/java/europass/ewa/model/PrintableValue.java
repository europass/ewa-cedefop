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
package europass.ewa.model;

import java.lang.reflect.Method;

import com.google.common.base.Strings;

import europass.ewa.model.decorator.WithPreferences;

public class PrintableValue<T> extends PrintableObject implements Showable, WithPreferences {

    private final T value;

    public PrintableValue(T value) {
        this.value = value;
    }

    /**
     * @return the toString value or the empty string. returning null is not
     * acceptable by mustache
     */
    @Override
    public String toString() {
        return value != null ? value.toString() : "";
    }

    public T value() {
        return value;
    }

    @Override
    public boolean checkEmpty() {
        if (value == null) {
            return true;
        }
        boolean isEmpty = true;
        //Check if String
        try {
            if (value instanceof String) {
                isEmpty = Strings.isNullOrEmpty((String) value);
            }
        } catch (final Exception e) {
        }
        //Check if value has suitable method
        try {
            Method m = value.getClass().getMethod("checkEmpty");
            Object isEmptyObj = m.invoke(value, new Object[0]);
            isEmpty = (boolean) isEmptyObj;
        } catch (final Exception e) {
        }
        //Or finally return empty;
        return isEmpty;
    }

}
