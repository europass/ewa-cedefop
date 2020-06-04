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
package europass.ewa.templates;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

public class DoubleResourceBundleMap<T> extends AbstractMap<String, T> {

    private final ResourceBundle bundle1;
    private final ResourceBundle bundle2;

    public DoubleResourceBundleMap(ResourceBundle bundle1, ResourceBundle bundle2) {
        this.bundle1 = bundle1;
        this.bundle2 = bundle2;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T get(Object key) {
        String adaptedKey = adaptKey(key);
        try {
            //First try to bundle1
            return (T) bundle1.getObject(adaptedKey);
        } catch (MissingResourceException mre1) {
            //When not found it throws exception, so try bundle2
            try {
                return (T) bundle2.getObject(adaptedKey);
            } catch (MissingResourceException mre2) {
                return null;
            }
        }
    }

    @Override
    public boolean containsKey(Object key) {
        boolean in1 = bundle1.containsKey(adaptKey(key));
        boolean in2 = bundle2.containsKey(adaptKey(key));
        return in1 || in2;
    }

    @Override
    public Set<String> keySet() {
        Set<String> set1 = bundle1.keySet();
        Set<String> set2 = bundle2.keySet();
        set1.addAll(set2);
        return set1;
    }

    @Override
    public boolean isEmpty() {
        boolean hasMore1 = bundle1.getKeys().hasMoreElements();
        boolean hasMore2 = bundle2.getKeys().hasMoreElements();
        return hasMore1 || hasMore2;
    }

    @Override
    public Set<Map.Entry<String, T>> entrySet() {
        return Collections.emptySet();
    }

    protected String adaptKey(Object key) {
        return key.toString();
    }

}
