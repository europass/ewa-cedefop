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
package europass.ewa.model.decorator;

import java.util.List;
import java.util.Locale;

import europass.ewa.model.ActivePreferences;
import europass.ewa.model.PrintingPreference;
import europass.ewa.model.SkillsPassport;

public interface WithPreferences {

    /**
     * Bootstrap and transmit the details of printing preferences to the
     * hierarchy of objects.
     *
     * @param document
     * @param prefs
     * @param prefKey
     * @param prefLocale
     * @param htmlTransformer
     */
    void withPreferences(SkillsPassport document, ActivePreferences prefs, String prefKey, Locale prefLocale);

    /**
     * Recursively apply default printing preferences identified by appending
     * the keyName to the prefKey
     *
     * @param keyName
     * @param newPrefs
     */
    void applyDefaultPreferences(String keyName, List<PrintingPreference> newPrefs);

    /**
     * Recursively apply default printing preferences
     *
     * @param newPrefs
     */
    void applyDefaultPreferences(List<PrintingPreference> newPrefs);

    String prefKey();

    SkillsPassport document();

    Locale locale();

    PrintingPreference pref();

    ActivePreferences activePreferences();

    void setPrefKey(String prefKey);

    boolean nonEmpty();

    boolean checkEmpty();
}
