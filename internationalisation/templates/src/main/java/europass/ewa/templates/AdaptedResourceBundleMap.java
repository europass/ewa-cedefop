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

import java.util.ResourceBundle;

import europass.ewa.resources.ResourceBundleMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class AdaptedResourceBundleMap extends ResourceBundleMap<AdaptedValue> implements TemplateContext {

    public AdaptedResourceBundleMap(ResourceBundle bundle) {
        super(bundle);
    }

    @Override
    public AdaptedValue get(Object key) {
        Object obj = super.get(key);
        return new AdaptedValue(obj);
    }

    @Override
    protected String adaptKey(Object key) {
        return BundleValue.adaptKey(key);
    }

    // Note! here on is AT code.. was not in the original class
    @Override
    public Set<Entry<String, AdaptedValue>> entrySet() {
        Set entrySet = new HashSet<>();
        for (String key : keySet()) {
            AdaptedValue adaptedValue = get(key);
            entrySet.add(new MyEntry<>(key, adaptedValue));
        }
        return entrySet;
    }

    final class MyEntry<K, V> implements Map.Entry<K, V> {

        private final K key;
        private V value;

        public MyEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V old = this.value;
            this.value = value;
            return old;
        }
    }
}
