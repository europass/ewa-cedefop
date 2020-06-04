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
package europass.ewa.resources;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

public class ResourceBundleMap<T> extends AbstractMap<String, T> {

    private final ResourceBundle bundle;

    public ResourceBundleMap(ResourceBundle bundle) {
        this.bundle = bundle;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T get(Object key) {
        try {
            return (T) bundle.getObject(adaptKey(key));
        } catch (MissingResourceException mre) {
            return null;
        }
    }

    @Override
    public boolean containsKey(Object key) {
        return bundle.containsKey(adaptKey(key));
    }

    @Override
    public Set<String> keySet() {
        return bundle.keySet();
    }

    @Override
    public boolean isEmpty() {
        return bundle.getKeys().hasMoreElements();
    }

    @Override
    public Set<Map.Entry<String, T>> entrySet() {
        return Collections.emptySet();
    }

    protected String adaptKey(Object key) {
        return key.toString();
    }
}
