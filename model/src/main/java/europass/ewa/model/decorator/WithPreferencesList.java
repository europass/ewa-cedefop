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

import java.util.AbstractList;
import java.util.List;
import java.util.Locale;

import com.google.common.base.Strings;

import europass.ewa.model.ActivePreferences;
import europass.ewa.model.PrintingPreference;
import europass.ewa.model.SkillsPassport;

public class WithPreferencesList<E extends WithPreferences> extends AbstractList<E> implements WithPreferences {

    protected final List<E> wrapped;

    private SkillsPassport document;

    private ActivePreferences activePrefs;

    private String prefKey;

    private Locale prefLocale;

    // -----------------------------------------//
    public WithPreferencesList(List<E> wrapped) {
        this.wrapped = wrapped;
    }

    // -----------------------------------------//
    // ------------- Abstract List ------------//
    @Override
    public E get(int index) {
        E e = wrapped.get(index);
        if (activePrefs != null && prefKey != null && e != null) {
            e.withPreferences(document, activePrefs, String.format("%s[%d]", prefKey, index), prefLocale);
        }
        return e;
    }

    @Override
    public int size() {
        if (wrapped == null) {
            return 0;
        }
        return wrapped.size();
    }

    @Override
    public E set(int index, E element) {
        return wrapped.set(index, element);
    }

    @Override
    public boolean add(E e) {
        return wrapped.add(e);
    }

    @Override
    public void add(int index, E element) {
        wrapped.add(index, element);
    }

    @Override
    public E remove(int index) {
        return wrapped.remove(index);
    }

    // ----------------------------------------------------//
    // ------------- WithPreferences Interface ------------//
    @Override
    public void withPreferences(SkillsPassport document, ActivePreferences activePrefs, String prefKey, Locale prefLocale) {
        this.document = document;
        this.activePrefs = activePrefs;
        this.prefKey = prefKey;
        this.prefLocale = prefLocale;
    }

    @Override
    public void applyDefaultPreferences(String keyName, List<PrintingPreference> newPrefs) {
        String key = prefKey == null ? "" : prefKey;
        key += (Strings.isNullOrEmpty(keyName) ? "" : "." + keyName);

        PrintingPreference pref = activePrefs != null ? activePrefs.get(key) : null;

        // Attention! A List will add an object even if it is already there -
        // should have used a Set instead
        if (pref != null && newPrefs.contains(pref) == false) {
            newPrefs.add(adjustShowStatus(pref));
        }
        for (E e : this) {
            if (e != null) {
                e.applyDefaultPreferences(newPrefs);
            }
        }
    }

    @Override
    public void applyDefaultPreferences(List<PrintingPreference> newPrefs) {
        PrintingPreference pref = pref();
        // Attention! A List will add an object even if it is already there -
        // should have used a Set instead
        if (pref != null && newPrefs.contains(pref) == false) {
            newPrefs.add(adjustShowStatus(pref));
        }
        for (E e : this) {
            if (e != null) {
                e.applyDefaultPreferences(newPrefs);
            }
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
        boolean isEmpty = this.checkEmpty();
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
        if (wrapped == null) {
            return true;
        }
        if (wrapped.isEmpty()) {
            return true;
        }
        for (E item : wrapped) {
            if (item == null) {
                continue;
            }
            if (!item.checkEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String prefKey() {
        return prefKey;
    }

    @Override
    public void setPrefKey(String prefKey) {
        this.prefKey = prefKey;
    }

    @Override
    public SkillsPassport document() {
        return this.document;
    }

    @Override
    public Locale locale() {
        return this.prefLocale;
    }

    @Override
    public PrintingPreference pref() {
        return activePrefs != null && prefKey != null ? activePrefs.get(prefKey) : null;
    }

    @Override
    public ActivePreferences activePreferences() {
        return this.activePrefs;
    }

}
