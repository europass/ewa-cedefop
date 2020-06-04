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

import java.util.List;
import java.util.Locale;

import com.google.common.base.Function;

import europass.ewa.model.CodeLabel;
import europass.ewa.model.SkillsPassport;
import europass.ewa.model.translation.TranslatableImpl.TaxonomyItemInfo;

public interface Translatable {

    /**
     * Translate the code label in the requested locale. It will replace the
     * label, even when it already has some text.
     *
     * @param esp
     * @param locale
     * @param taxonomyName
     * @param codeLabel
     *
     * @return CodeLabel
     */
    CodeLabel translate(SkillsPassport esp, Locale locale, String taxonomyName, CodeLabel codeLabel);

    /**
     * Translate the list of code label in the requested locale. It will replace
     * the label, even when it already has some text.
     *
     * @param esp
     * @param locale
     * @param taxonomyName
     * @param codeLabel
     * @return
     */
    List<CodeLabel> translate(SkillsPassport esp, Locale locale, String taxonomyName, List<CodeLabel> codeLabelList);

    /**
     * Translate the code label in the requested locale. It will replace the
     * label, even when it already has some text. The first function is used to
     * properly adjust the object that is returned by the resource bundle. The
     * second function is used to accommodate the cases where the code needs to
     * be adjusted before searching into the taxonomy
     *
     * @param esp
     * @param locale
     * @param taxonomyName
     * @param codeLabel
     * @param adjustObj
     * @param adjustCode
     * @return
     */
    CodeLabel translate(SkillsPassport esp, Locale locale, String taxonomyName, CodeLabel codeLabel, Function<TaxonomyItemInfo, String> adjustObj, Function<String, String> adjustCode);

}
