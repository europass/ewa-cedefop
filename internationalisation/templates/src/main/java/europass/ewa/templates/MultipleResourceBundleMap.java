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
import java.util.HashSet;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.Set;

public class MultipleResourceBundleMap extends AbstractMap<String, AdaptedValue> implements TemplateContext {

    private final Map<String, AdaptedResourceBundleMap> bundleMap;

    private final Set<String> uniqueKeys;

    public MultipleResourceBundleMap(Map<String, AdaptedResourceBundleMap> resourceBundleMap) {
        this.bundleMap = resourceBundleMap;
        this.uniqueKeys = keySet();
    }

    @Override
    public AdaptedValue get(Object key) {
        KeyInfo keyInfo = getKeyInfo(key);
        if (keyInfo == null) {
            return null;
        }

        String bundle = keyInfo.getBundle();
        String bundleKey = keyInfo.getBundleKey();

        if (bundleMap.containsKey(bundle)) {
            //search in the values for the adapted bundleKey
            AdaptedResourceBundleMap labels = bundleMap.get(bundle);
            try {
                return labels.get(bundleKey);
            } catch (MissingResourceException mre2) {
                return null;
            }
        } else {
            return null;
        }
    }

    protected static String adaptKey(Object key) {
        return BundleValue.adaptKey(key);
    }

    @Override
    public boolean containsKey(Object key) {
        return uniqueKeys.contains(key);
    }

    @Override
    public Set<String> keySet() {
        /*
		 * the set should not contain duplicate keys.... but we may have duplicate keys, unless it is a set of the original keys like Listening_A1
		 * 1.loop through the bundle
		 * 2.while fetching get the map's values => the  ResourceBundles
		 * 3.get the keys from each ResourceBundle
		 * 4.if the map's key entry is NOT Document, then the key should be in the form of Listening_A1, Writing_A1 e.t.c.
		 * 5.else the key after being reversely adapted
		 * 6.add key to the set
         */
        Set<String> uniqueKeys = new HashSet<>();

        for (Entry<String, AdaptedResourceBundleMap> entry : bundleMap.entrySet()) {
            String bundleName = entry.getKey();

            String bundlePrefix = "Document".equals(bundleName) ? "" : (bundleName + "_");
            AdaptedResourceBundleMap bundle = entry.getValue();

            for (String bundleKey : bundle.keySet()) {
                String adaptedBundleKey = BundleValue.reverseKey(bundleKey);

                uniqueKeys.add(bundlePrefix + adaptedBundleKey);
            }
        }
        return uniqueKeys;
    }

    @Override
    public Set<java.util.Map.Entry<String, AdaptedValue>> entrySet() {
        // Note! AT code.. Used to be:
        // return Collections.emptySet();

        Set entrySet = new HashSet<>();
        for (String key : keySet()) {
            AdaptedValue adaptedValue = get(key);
            entrySet.add(new MultipleResourceBundleMap.MyEntry<>(key, adaptedValue));
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

    protected static KeyInfo getKeyInfo(Object key) {
        return getKeyInfo(key, true);
    }

    protected static KeyInfo getKeyInfo(Object key, boolean adapted) {
        if (!(key instanceof String)) {
            return null;
        }

        String originalKey = (String) key;

        //Find out which resource we should be looking at according to the first part of the key:
        //e.g. GuiLabel_key:key
        int partsNo = 0;
        //split the key
        String[] keyParts = originalKey.split("_");
        if (keyParts != null) {
            partsNo = keyParts.length;
        }

        //Decide further about the bundle and key
        String bundle = "Document";
        String bundleKey = null;

        switch (partsNo) {
            case 0: {
                return null;
            }
            case 1: {
                bundleKey = keyParts[0];
                break;
            }
            default: {
                bundle = keyParts[0];
                bundleKey = keyParts[1];
                break;
            }
        }
        return new KeyInfo(bundle, adapted ? adaptKey(bundleKey) : bundleKey);
    }

    static class KeyInfo {

        String bundle;
        String bundleKey;

        public KeyInfo(String bundle, String bundleKey) {
            this.bundle = bundle;
            this.bundleKey = bundleKey;
        }

        public String getBundle() {
            return bundle;
        }

        public void setBundle(String bundle) {
            this.bundle = bundle;
        }

        public String getBundleKey() {
            return bundleKey;
        }

        public void setBundleKey(String bundleKey) {
            this.bundleKey = bundleKey;
        }
    }
}
