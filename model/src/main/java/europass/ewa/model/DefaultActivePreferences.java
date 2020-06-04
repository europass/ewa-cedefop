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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

class DefaultActivePreferences implements ActivePreferences {

    private String name;

    Map<String, PrintingPreference> defaultPrefs;

    private Map<String, PrintingPreference> prefs;

    public void load(String name, List<PrintingPreference> prefs, Map<String, PrintingPreference> defaults) {
        this.name = name;

        Map<String, PrintingPreference> newPrefs = new HashMap<String, PrintingPreference>();
        for (PrintingPreference pref : prefs) {
            newPrefs.put(pref.getName().toString(), pref);
        }

        this.prefs = newPrefs;
        this.defaultPrefs = defaults;
    }

    @Override
    public PrintingPreference get(String key) {
        PrintingPreference pref = prefs.get(key);
        if (pref == null) {
            String defaultKey = key.replaceAll("\\[\\d+\\]", "[0]");
            pref = defaultPrefs.get(defaultKey);
            if (pref != null) {
                pref = new PrintingPreference(key, pref);
                pref.setDefaultPrefApplied(true);
            }
        }
        return pref;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

}
