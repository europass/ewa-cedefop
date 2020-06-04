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

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;

import europass.ewa.collections.Predicates;
import europass.ewa.enums.EuropassDocumentType;
import europass.ewa.model.decorator.TraverseWithDocument;
import europass.ewa.model.decorator.TraverseWithDocumentObject;
import europass.ewa.model.decorator.TraverseWithPreferences;
import europass.ewa.model.decorator.TraverseWithPreferencesObject;
import europass.ewa.model.decorator.WithDocument;
import europass.ewa.model.decorator.WithDocumentObject;
import europass.ewa.model.decorator.WithPreferences;
import europass.ewa.model.decorator.WithPreferencesObject;
import europass.ewa.model.format.OdtDisplayableUtils;
import europass.ewa.model.translation.Translatable;
import europass.ewa.model.translation.TranslatableImpl;
import europass.ewa.model.translation.TranslatableImpl.TaxonomyItemInfo;

/**
 * The base class of all objects that have associated {@link PrintingPreference}
 *
 * @author avah
 */
public class PrintableObject implements WithDocument, TraverseWithDocument, WithPreferences, TraverseWithPreferences, Showable, IndexedListItem, Translatable {

    static final int EN_DASH_CHARACTER = 0x2013;

    static final char[] EN_DASH_CHARACTER_CHAR = Character.toChars(EN_DASH_CHARACTER);

    static final String EN_DASH_CHARACTER_STR = String.valueOf(EN_DASH_CHARACTER_CHAR);

    private WithPreferences withPreferences;

    private TraverseWithPreferences traverseWithPreferences;

    private ListItem asListItem = null;

    private WithDocument withDocument;

    private TraverseWithDocument traverseWithDocument;

    public PrintableObject() {
        withPreferences = new WithPreferencesObject(this);
        traverseWithPreferences = new TraverseWithPreferencesObject(withPreferences);
        withDocument = new WithDocumentObject(this);
        traverseWithDocument = new TraverseWithDocumentObject(withDocument);
    }

    @JsonIgnore
    public String placeholder() {
        return EN_DASH_CHARACTER_STR;
    }

    /**
     * Check if the specific object can be considered empty in relation to
     * applying default preferences to it.
     *
     * @param name
     * @return
     */
    @Override
    public boolean nonEmpty() {
        return !checkEmpty();
    }

    /**
     * Check if the specific object can be considered empty in relation to
     * applying default preferences to it.
     *
     * @param name
     * @return
     */
    @Override
    public boolean checkEmpty() {
        return true;
    }

    /**
     * ************* Local Methods ****************
     */
    protected <E extends PrintableObject> List<E> indexedList(List<E> list) {
        return new IndexedList<E>(list);
    }

    /**
     * **********************************************
     */
    /**
     * **************** Translation *****************
     */
    public void translateTo(SkillsPassport esp, Locale translationLocale, String taxonomyName) {
        //Concrete implementation in classes that extend the PrintableObject
        return;
    }

    public <E extends PrintableObject> void translateTo(SkillsPassport esp, Locale translationLocale, String taxonomyName, List<E> list) {
        for (E item : list) {
            item.translateTo(esp, translationLocale, taxonomyName);
        }
    }

    public <E extends PrintableObject> void translateTo(SkillsPassport esp, Locale translationLocale, String taxonomyName, E obj) {
        obj.translateTo(esp, translationLocale, taxonomyName);
    }

    /**
     * *********** Translatable Interface *************
     */
    @Override
    public CodeLabel translate(SkillsPassport esp, Locale locale, String taxonomyName, CodeLabel codeLabel) {
        return TranslatableImpl.translate(esp, locale, taxonomyName, codeLabel);
    }

    @Override
    public CodeLabel translate(SkillsPassport esp, Locale locale, String taxonomyName, CodeLabel codeLabel, Function<TaxonomyItemInfo, String> adjustObj, Function<String, String> adjustCode) {
        return TranslatableImpl.translate(esp, locale, taxonomyName, codeLabel, adjustObj, adjustCode);
    }

    @Override
    public List<CodeLabel> translate(SkillsPassport esp, Locale locale, String taxonomyName, List<CodeLabel> codeLabelList) {
        return TranslatableImpl.translate(esp, locale, taxonomyName, codeLabelList);
    }

    /**
     * **********************************************
     */

    /**
     * *********** WithDocument/ TraverseWithDocument Interfaces *************
     */
    @JsonIgnore
    @Override
    public void withDocument(SkillsPassport document) {
        withDocument.withDocument(document);
    }

    @JsonIgnore
    @Override
    public Locale getLocale() {
        Locale locale = withDocument.getLocale();
        if (locale == null) {
            locale = withPreferences.locale();
        }
        return locale;
    }

    @JsonIgnore
    @Override
    public SkillsPassport getDocument() {
        SkillsPassport esp = traverseWithDocument.getDocument();
        if (esp == null) {
            esp = withPreferences.document();
        }
        return esp;
    }

    @JsonIgnore
    @Override
    public <E extends WithDocument> E withDocument(E object, SkillsPassport esp) {
        return traverseWithDocument.withDocument(object, esp);
    }

    @JsonIgnore
    @Override
    public <E extends WithDocument> List<E> withDocument(List<E> list, SkillsPassport esp) {
        return traverseWithDocument.withDocument(list, esp);
    }

    /**
     * **********************************************
     */

    /**
     * **********************************************
     */
    /**
     * ********** WithPreferences Interface *********
     */
    @JsonIgnore
    @Override
    public void withPreferences(SkillsPassport document, ActivePreferences prefs, String prefKey, Locale prefLocale) {
        withPreferences.withPreferences(document, prefs, prefKey, prefLocale);
    }

    @JsonIgnore
    @Override
    public String prefKey() {
        return withPreferences.prefKey();
    }

    @JsonIgnore
    @Override
    public PrintingPreference pref() {
        return withPreferences.pref();
    }

    @JsonIgnore
    @Override
    public void applyDefaultPreferences(String name, List<PrintingPreference> newPrefs) {
        withPreferences.applyDefaultPreferences(name, newPrefs);
    }

    @JsonIgnore
    @Override
    public void applyDefaultPreferences(List<PrintingPreference> newPrefs) {
        withPreferences.applyDefaultPreferences(newPrefs);
    }

    @JsonIgnore
    @Override
    public ActivePreferences activePreferences() {
        return withPreferences.activePreferences();
    }

    @Override
    public void setPrefKey(String prefKey) {
        withPreferences.setPrefKey(prefKey);
    }

    /**
     * *********************************************
     */
    /**
     * *** TraverseWithPreferences Interface *******
     */
    @JsonIgnore
    @Override
    public SkillsPassport document() {
        SkillsPassport esp = withPreferences.document();
        if (esp == null) {
            esp = traverseWithDocument.getDocument();
        }
        return esp;
    }

    @JsonIgnore
    @Override
    public Locale locale() {
        Locale locale = withPreferences.locale();
        if (locale == null) {
            locale = withDocument.getLocale();
        }
        return locale;
    }

    @JsonIgnore
    @Override
    public <E extends WithPreferences> E withPreferences(E object, String name) {
        return traverseWithPreferences.withPreferences(object, name);
    }

    @JsonIgnore
    @Override
    public <E extends WithPreferences> E withPreferences(E object) {
        return traverseWithPreferences.withPreferences(object);
    }

    @JsonIgnore
    @Override
    public <E extends WithPreferences> List<E> withPreferences(List<E> list, String name) {
        return traverseWithPreferences.withPreferences(list, name);
    }

    @JsonIgnore
    @Override
    public <E extends WithPreferences> List<E> withPreferences(List<E> list) {
        return traverseWithPreferences.withPreferences(list);
    }

    @JsonIgnore
    @Override
    public PrintableValue<String> withPreferences(String value, String name) {
        return traverseWithPreferences.withPreferences(value, name);
    }

    @JsonIgnore
    @Override
    public PrintableValue<JDate> withPreferences(JDate value, String name) {
        return traverseWithPreferences.withPreferences(value, name);
    }

    @JsonIgnore
    @Override
    public PrintableValue<FileData> withPreferences(FileData value, String name) {
        return traverseWithPreferences.withPreferences(value, name);
    }

    @JsonIgnore
    @Override
    public <P extends PrintableObject> void applyDefaultPreferences(P obj, Class<P> cls, String key, List<PrintingPreference> newPrefs) {
        traverseWithPreferences.applyDefaultPreferences(obj, cls, key, newPrefs);
    }

    @JsonIgnore
    @Override
    public <P extends PrintableObject> void applyDefaultPreferences(PrintableList<P> lst, Class<P> cls, String key,
            List<PrintingPreference> newPrefs) {
        traverseWithPreferences.applyDefaultPreferences(lst, cls, key, newPrefs);
    }

    /**
     * ***********************************************
     */
    /**
     * ************** Showable Interface *************
     */
    @Override
    public boolean show() {
        PrintingPreference pref = pref();
        return pref == null || pref.getShow();
    }

    /**
     * **********************************************
     */
    /**
     * ********* IndexedListItem Interface *********
     */
//	@JsonIgnore
//	@Override
//	public void withIndex( int index, boolean isFirst, boolean isLast, boolean  isLastShowable ) {
//		if ( asListItem == null ){
//			asListItem = new ListItem( index, isFirst, isLast, isLastShowable );
//		}
//	}
    @JsonIgnore
    @Override
    public void withIndex(int index, boolean isFirst, boolean isLast) {
        if (asListItem == null) {
            asListItem = new ListItem(index, isFirst, isLast);
        }
    }

    @JsonIgnore
    @Override
    public int index() {
        if (asListItem == null) {
            return 0;
        }
        return asListItem.getIndex();
    }

    @JsonIgnore
    @Override
    public boolean isFirst() {
        if (asListItem == null) {
            return false;
        }
        return asListItem.isFirst();
    }

    @JsonIgnore
    @Override
    public boolean isLast() {
        if (asListItem == null) {
            return false;
        }
        return asListItem.isLast();
    }

    /**
     * pgia EWA-1604: Choose the correct active preferences depending on the
     * preference key (in case of bundles)
     *
     * This is happening due to the fact that on the bundled documents the
     * preferences that are imploded in the withPreferences(...) method are the
     * ones of LearnerInfo (basicaly the main document ECV Preferences. So we
     * use this hack to get the date prefrences directly from the
     * documentPrefernces
     *
     * @return
     */
    @JsonIgnore
    public PrintingPreference preferencesFromDocument(SkillsPassport document, String key) {

        if (document != null) {

            Map<String, List<PrintingPreference>> documentPrintingPrefs = document.getDocumentPrintingPrefs();
            if (documentPrintingPrefs != null) {
                List<EuropassDocumentType> bundleList = document.getDocumentInfo().getBundle();
                if (bundleList != null) {
                    for (EuropassDocumentType docType : bundleList) {

                        String prefsAcronym = docType.getPreferencesAcronym();

                        List<PrintingPreference> prefs = documentPrintingPrefs.get(prefsAcronym);;
                        try {
                            String escaped = key.replaceAll("\\[", "\\\\[").replaceAll("\\]", "\\\\]");
                            Collection<PrintingPreference> matchedPrefs = Collections2.filter(prefs, Predicates.containsPattern(escaped));
                            if (matchedPrefs.size() > 0) {
                                for (PrintingPreference preference : matchedPrefs) {
                                    return preference;
                                }
                            }
                        } catch (Exception e) {
                            continue;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * ***************************************************************
     */
    /**
     * ********************* UTILITY METHODS *************************
     */
    @JsonIgnore
    protected String asRichText(String str, EuropassDocumentType docType, String textStyle) {
        return OdtDisplayableUtils.richtext(document().getTransformer(), str, docType, textStyle);
    }

    @JsonIgnore
    protected String asRichText(String str) {
        return OdtDisplayableUtils.richtext(document().getTransformer(), str, null, null);
    }

    @JsonIgnore
    public boolean printPipe(boolean... shows) {
        return OdtDisplayableUtils.printPipe(shows);
    }

    @JsonIgnore
    public String escapeForXml(String txt) {
        return OdtDisplayableUtils.escapeForXml(txt);
    }

    @JsonIgnore
    public String escapeNewLineCharacters(String str) {
        return OdtDisplayableUtils.escapeNewLineCharacters(str);
    }

}
