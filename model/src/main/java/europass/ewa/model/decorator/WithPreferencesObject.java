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

import com.google.common.base.Strings;

import europass.ewa.model.ActivePreferences;
import europass.ewa.model.PrintableObject;
import europass.ewa.model.PrintingPreference;
import europass.ewa.model.SkillsPassport;

/**
 * Concrete implementation of the @WithPreferences interface.
 *
 * @author ekar
 *
 */
public class WithPreferencesObject implements WithPreferences {

    private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    protected SkillsPassport document;

    protected ActivePreferences activePrefs;

    protected String prefKey;

    protected Locale prefLocale;

    private final PrintableObject obj;

    public WithPreferencesObject(PrintableObject obj) {
        this.obj = obj;
    }
    // ----- APPLY PRINTING PREFERENCES ---- //

    @Override
    public void withPreferences(SkillsPassport document, ActivePreferences activePrefs, String prefKey, Locale prefLocale) {
        this.document = document;
        this.activePrefs = activePrefs;
        this.prefKey = prefKey;
        this.prefLocale = prefLocale;
    }

    // ----- APPLY DEFAULT PRINTING PREFERENCES ---- //
    @Override
    public void applyDefaultPreferences(String keyName, List<PrintingPreference> newPrefs) {
        String key = prefKey == null ? "" : prefKey;
        key += (Strings.isNullOrEmpty(keyName) ? "" : "." + keyName);

        PrintingPreference pref = activePrefs != null ? activePrefs.get(key) : null;
        if (pref != null && !newPrefs.contains(pref)) {
            newPrefs.add(pref);
        }

    }

    @Override
    public void applyDefaultPreferences(List<PrintingPreference> newPrefs) {
        PrintingPreference pref = pref();
        //Attention! A List will add an object even if it is already there - should have used a Set instead
        if (pref != null && !newPrefs.contains(pref)) {
            newPrefs.add(adjustShowStatus(pref));
        }

    }

    /**
     * When Default Preferences are applied, the show status will be set to: -
     * true when the object is non-empty - false when the object is empty
     *
     * @param pref
     * @return the potentially modified pref
     */
    private PrintingPreference adjustShowStatus(PrintingPreference pref) {
        boolean defaultPrefApplied = pref.isDefaultPrefApplied();
        /*
		 * When Default Preferences are applied, the show status will be set to:
		 * - true when the object is non-empty
		 * - false when the object is empty
         */
        boolean isEmpty = this.obj == null || (this.obj != null && this.obj.checkEmpty());
        if (defaultPrefApplied) {
            pref.setShow(!isEmpty);
        }
        return pref;
    }

    @Override
    public boolean nonEmpty() {
        return !checkEmpty();
    }

    /**
     * Overrides checkEmpty from WithPreferences interface
     */
    @Override
    public boolean checkEmpty() {
        if (this.obj == null) {
            return true;
        }
        return this.obj.checkEmpty();
    }

    //----- GETTERS ----
    @Override
    public String prefKey() {
        return this.prefKey;
    }

    @Override
    public SkillsPassport document() {
        return this.document;
    }

    @Override
    public Locale locale() {
        if (this.document == null) {
            return DEFAULT_LOCALE;
        }

        Locale locale = this.document.getLocale();

        if (locale == null) {
            return DEFAULT_LOCALE;
        }

        return locale;
    }

    @Override
    public PrintingPreference pref() {
        return activePrefs != null && prefKey != null ? activePrefs.get(prefKey) : null;
    }

    @Override
    public ActivePreferences activePreferences() {

        return this.activePrefs;
    }

    //----- SETTERS ----
    @Override
    public void setPrefKey(String prefKey) {
        this.prefKey = prefKey;
    }

}
