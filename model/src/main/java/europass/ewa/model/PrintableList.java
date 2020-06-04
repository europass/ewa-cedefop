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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import europass.ewa.model.decorator.WithPreferences;
import europass.ewa.model.decorator.WithPreferencesList;

public class PrintableList<E extends WithPreferences> extends WithPreferencesList<E> implements Showable {

    public PrintableList(List<E> wrapped) {
        super(wrapped);
    }

    @JsonIgnore
    public String placeholder() {
        return PrintableObject.EN_DASH_CHARACTER_STR;
    }

    //----------------------------------------------------//
    @JsonIgnore
    @Override
    public boolean show() {
        if (super.activePreferences() == null || super.prefKey() == null) {
            return true;
        }
        PrintingPreference pref = super.activePreferences().get(prefKey());
        return pref == null || pref.getShow();
    }

    @JsonIgnore
    public boolean hasOnlyOne() {
        return this.size() == 1;
    }

}
