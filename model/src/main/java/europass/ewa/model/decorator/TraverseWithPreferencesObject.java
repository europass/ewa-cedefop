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

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import europass.ewa.model.ActivePreferences;
import europass.ewa.model.FileData;
import europass.ewa.model.JDate;
import europass.ewa.model.PrintableList;
import europass.ewa.model.PrintableObject;
import europass.ewa.model.PrintableValue;
import europass.ewa.model.PrintingPreference;
import europass.ewa.model.SkillsPassport;

/**
 * Concrete implementation of the @WithPreferences interface.
 *
 * @author ekar
 *
 */
public class TraverseWithPreferencesObject implements TraverseWithPreferences {

    private static final Logger LOG = LoggerFactory.getLogger(TraverseWithPreferencesObject.class);

    private WithPreferences withPreferences;

    //--- Constructor ---//
    public TraverseWithPreferencesObject(WithPreferences withPreferences) {
        this.withPreferences = withPreferences;
    }

    // ----- APPLY PRINTING PREFERENCES ---- //
    @Override
    public <E extends WithPreferences> E withPreferences(E object, String name) {
        if (prefs() == null || prefKey() == null || object == null) {
            return object;
        }
        String key = prefKey() + (Strings.isNullOrEmpty(name) ? "" : ("." + name));
        object.withPreferences(document(), prefs(), key, locale());
        return object;
    }

    @Override
    public <E extends WithPreferences> E withPreferences(E object) {
        return withPreferences(object, null);
    }

    @Override
    public <E extends WithPreferences> List<E> withPreferences(List<E> list, String name) {
        if (prefs() == null || prefKey() == null || list == null) {
//			NOTE: If we return an empty PrintableList it cannot be considered empty and the XML element for the list will appear in the XML empty.
            return list;
        }
        PrintableList<E> prefList = new PrintableList<E>(list);
        String key = prefKey() + (Strings.isNullOrEmpty(name) ? "" : ("." + name));
        prefList.withPreferences(document(), prefs(), key, locale());
        return prefList;
    }

    @Override
    public <E extends WithPreferences> List<E> withPreferences(List<E> list) {
        return withPreferences(list, null);
    }

    @Override
    public PrintableValue<String> withPreferences(String value, String name) {
        PrintableValue<String> pvalue = new PrintableValue<String>(value);

        if (prefs() != null && prefKey() != null && name != null) {

            String key = prefKey() + (Strings.isNullOrEmpty(name) ? "" : ("." + name));
            pvalue.withPreferences(document(), prefs(), key, locale());
        }
        return pvalue;
    }

    @Override
    public PrintableValue<JDate> withPreferences(JDate value, String name) {
        PrintableValue<JDate> pvalue = new PrintableValue<JDate>(value);

        if (prefs() != null && prefKey() != null && name != null) {

            String key = prefKey() + (Strings.isNullOrEmpty(name) ? "" : ("." + name));
            pvalue.withPreferences(document(), prefs(), key, locale());
        }
        return pvalue;
    }

    @Override
    public PrintableValue<FileData> withPreferences(FileData value, String name) {
        PrintableValue<FileData> pvalue = new PrintableValue<FileData>(value);

        if (prefs() != null && prefKey() != null && name != null) {

            String key = prefKey() + (Strings.isNullOrEmpty(name) ? "" : ("." + name));
            pvalue.withPreferences(document(), prefs(), key, locale());
        }
        return pvalue;
    }

    // ----- APPLY DEFAULT PRINTING PREFERENCES ---- //
    @Override
    public <P extends PrintableObject> void applyDefaultPreferences(P obj, Class<P> cls, String key, List<PrintingPreference> newPrefs) {
        if (obj == null) {
            try {
                obj = withPreferences(cls.newInstance(), key);
            } catch (Exception e) {
                LOG.error("PrintableObject:applyDefaultPreferences - unable to instantiate object from class '" + cls.getName() + "'", e);
            }
        }
        if (obj != null) {
            obj.applyDefaultPreferences(newPrefs);
        }
    }

    @Override
    public <P extends PrintableObject> void applyDefaultPreferences(PrintableList<P> lst, Class<P> cls, String key, List<PrintingPreference> newPrefs) {
        if (lst == null || lst.isEmpty()) {
            try {
                P obj = cls.newInstance();
                lst = (PrintableList<P>) withPreferences(Arrays.asList(obj), key);
            } catch (Exception e) {
                LOG.error("PrintableObject:applyDefaultPreferences - unable to instantiate list object from class '" + cls.getName() + "'", e);
            }
        }
        lst.applyDefaultPreferences(newPrefs);
    }

    //----- GETTERS ----
    @Override
    public SkillsPassport document() {
        return this.withPreferences.document();
    }

    private ActivePreferences prefs() {
        return this.withPreferences.activePreferences();
    }

    private Locale locale() {
        return this.withPreferences.locale();
    }

    private String prefKey() {
        return this.withPreferences.prefKey();
    }

}
