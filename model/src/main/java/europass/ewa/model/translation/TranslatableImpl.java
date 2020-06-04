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
package europass.ewa.model.translation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.base.Strings;

import europass.ewa.model.CodeLabel;
import europass.ewa.model.SkillsPassport;
import europass.ewa.resources.JsonResourceBundle;

public class TranslatableImpl {

    public static CodeLabel translate(SkillsPassport esp, Locale locale, String taxonomyName, CodeLabel codeLabel) {
        return translate(esp, locale, taxonomyName, codeLabel, null, null);
    }

    public static List<CodeLabel> translate(SkillsPassport esp, Locale locale, String taxonomyName, List<CodeLabel> codeLabelList) {
        if (codeLabelList == null) {
            return null;
        }

        int size = codeLabelList.size();
        if (size == 0) {
            return codeLabelList;
        }

        List<CodeLabel> translatedList = new ArrayList<>(size);

        for (CodeLabel codeLabel : codeLabelList) {
            if (codeLabel == null) {
                continue;
            }
            CodeLabel translated = translate(esp, locale, taxonomyName, codeLabel);
            translatedList.add(translated);
        }
        return translatedList;
    }

    public static CodeLabel translate(SkillsPassport esp, Locale locale, String taxonomyName, CodeLabel codeLabel, Function<TaxonomyItemInfo, String> adjustObj,
            Function<String, String> adjustCodeF) {

        // When the codelabel is null, then do NOT translate
        if (codeLabel == null) {
            return null;
        }

        return doTranslation(esp, locale, taxonomyName, adjustObj, codeLabel, adjustCodeF);
    }

    /**
     * Will set the label taking into consideration the taxonomy if applicable.
     * Note that if the label is already set it will not perform any
     * translation.
     *
     * If no taxonomy is set, or no taxonomy code exists, then the label will be
     * set as is. If there are both taxonomy and and code, but the code is not
     * valid, then again the label will be set as is. Finally, if all the above
     * restrictions are overcome, then the label is set based on the taxonomy.
     */
    private static CodeLabel doTranslation(SkillsPassport esp, Locale locale, String taxonomyName, Function<TaxonomyItemInfo, String> adjustObj, CodeLabel codeLabel,
            Function<String, String> adjustCodeF) {

        String initCode = codeLabel.getCode();
        String initLabel = codeLabel.getLabel();

        if (Strings.isNullOrEmpty(initCode)) {
            return codeLabel;
        }
        ResourceBundle taxonomy = prepareTaxonomy(taxonomyName, locale);
        if ((taxonomy == null)) {
            return codeLabel;
        }

        String code = (adjustCodeF == null) ? initCode : adjustCodeF.apply(initCode);
        if (!validCode(taxonomy, code)) {
            return codeLabel;
        }
        return new CodeLabel(initCode, fromTaxonomy(esp, taxonomy, code, initLabel, adjustObj));
    }

    /**
     * Prepares and sets a taxonomy bundle from a taxonomy name. If no bundle is
     * found or any exception occurs the taxonomy is set to null
     *
     * @param taxonomyName
     * @param locale
     * @return
     */
    private static ResourceBundle prepareTaxonomy(String taxonomyName, Locale locale) {
        try {
            return ResourceBundle.getBundle("bundles/" + taxonomyName, locale, new JsonResourceBundle.Control(new ObjectMapper()));
        } catch (final Exception e) {
            return null;
        }
    }

    /**
     * Informs whether there exists an entry in the taxonomy with the given code
     * as key
     *
     * @param taxonomy
     * @param code
     * @return
     */
    private static boolean validCode(ResourceBundle taxonomy, String code) {
        if ((taxonomy == null) || Strings.isNullOrEmpty(code)) {
            return false;
        }
        return taxonomy.containsKey(code);
    }

    /**
     * Retrieves a value from the taxonomy bundle based on the given code
     *
     * @param esp
     * @param taxonomy
     * @param code
     * @return
     */
    private static String fromTaxonomy(SkillsPassport esp, ResourceBundle taxonomy, String code, String initLabel, Function<TaxonomyItemInfo, String> adjustObj) {

        if (taxonomy == null || Strings.isNullOrEmpty(code)) {
            return null;
        }
        Object obj = taxonomy.getObject(code);
        if (obj.equals("-")) {
            obj = initLabel;
        }

        TaxonomyItemInfo itemInfo = new TaxonomyItemInfo(esp, obj);
        return (adjustObj == null) ? (String) obj : adjustObj.apply(itemInfo);
    }

    public static class TaxonomyItemInfo {

        private Object item;
        private SkillsPassport document;

        TaxonomyItemInfo(SkillsPassport document, Object item) {
            this.document = document;
            this.item = item;
        }

        public Object getItem() {
            return item;
        }

        public void setItem(Object item) {
            this.item = item;
        }

        public SkillsPassport getDocument() {
            return document;
        }

        public void setDocument(SkillsPassport document) {
            this.document = document;
        }

    }

}
