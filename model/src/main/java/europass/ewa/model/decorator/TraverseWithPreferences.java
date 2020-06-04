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

import europass.ewa.model.FileData;
import europass.ewa.model.JDate;
import europass.ewa.model.PrintableList;
import europass.ewa.model.PrintableObject;
import europass.ewa.model.PrintableValue;
import europass.ewa.model.PrintingPreference;
import europass.ewa.model.SkillsPassport;

public interface TraverseWithPreferences {

    /**
     * Get an object augmented with preferences information. The name will be
     * appended to the existing preference key.
     *
     * @param object
     * @param name
     * @return
     */
    <E extends WithPreferences> E withPreferences(E object, String name);

    /**
     * Get an object augmented with preferences information. The key to find the
     * preference is the existing preference key.
     *
     * @param object
     * @param name
     * @return
     */
    <E extends WithPreferences> E withPreferences(E object);

    /**
     * Get a list of objects augmented with preferences information. The name
     * will be appended to the existing preference key.
     *
     * @param object
     * @param name
     * @return
     */
    <E extends WithPreferences> List<E> withPreferences(List<E> list, String name);

    /**
     * Get a list of objects augmented with preferences information. The key to
     * find the preference is the existing preference key.
     *
     * @param object
     * @param name
     * @return
     */
    <E extends WithPreferences> List<E> withPreferences(List<E> list);

    /**
     * Get an simple String augmented with information about preferences. The
     * name will be appended to the existing preference key.
     *
     * @param value
     * @param name
     * @return
     */
    PrintableValue<String> withPreferences(String value, String name);

    PrintableValue<FileData> withPreferences(FileData value, String name);

    PrintableValue<JDate> withPreferences(JDate value, String name);

    /**
     * Handle the case where the PrintableObject might be null. We still need to
     * set the Default Preferences.
     *
     * @param obj, the Printable Object
     * @param cls, the Class of the Printable Object
     * @param key, the Key to be appended to the Printing Preference name
     * @param newPrefs, the List of Preferences
     */
    <P extends PrintableObject> void applyDefaultPreferences(P obj, Class<P> cls, String key, List<PrintingPreference> newPrefs);

    /**
     * Handle the case where the PrintableList might be null or empty. We still
     * need to set the Default Preferences.
     *
     * @param lst, the Printable List of Objects
     * @param cls, the Class of List Item Object
     * @param key, the Key to be appended to the Printing Preference name
     * @param newPrefs, the List of Preferences
     */
    <P extends PrintableObject> void applyDefaultPreferences(PrintableList<P> lst, Class<P> cls, String key, List<PrintingPreference> newPrefs);

    SkillsPassport document();
}
