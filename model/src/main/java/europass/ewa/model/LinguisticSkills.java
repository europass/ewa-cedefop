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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.common.base.Strings;

import europass.ewa.model.decorator.WithDocumentList;
import europass.ewa.model.decorator.WithPreferencesList;
import europass.ewa.model.wrapper.IdRefSafeList;

@JsonPropertyOrder({
    "motherTongue",
    "foreignLanguage"
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class LinguisticSkills extends PrintableObject {

    private List<LinguisticSkill> motherTongue;

    private List<LinguisticSkill> foreignLanguage;

    public LinguisticSkills() {
    }

    public LinguisticSkills(List<LinguisticSkill> mother, List<LinguisticSkill> foreign) {
        this.motherTongue = mother;
        this.foreignLanguage = foreign;
    }

    @JsonProperty("MotherTongue")
    @JacksonXmlProperty(localName = "MotherTongue", namespace = Namespace.NAMESPACE)
    @JacksonXmlElementWrapper(localName = "MotherTongueList", namespace = Namespace.NAMESPACE)
    public List<LinguisticSkill> getMotherTongue() {
        return withDocument(motherTongue, getDocument());
    }

    public void setMotherTongue(List<LinguisticSkill> mother) {
        this.motherTongue = mother;
    }

    @JsonProperty("ForeignLanguage")
    @JacksonXmlProperty(localName = "ForeignLanguage", namespace = Namespace.NAMESPACE)
    @JacksonXmlElementWrapper(localName = "ForeignLanguageList", namespace = Namespace.NAMESPACE)
    public List<LinguisticSkill> getForeignLanguage() {
        return withPreferences(foreignLanguage, "ForeignLanguage");
    }

    public void setForeignLanguage(List<LinguisticSkill> foreign) {
        this.foreignLanguage = foreign;
    }

    /**
     * **********************************************************************************************
     */
    @JsonIgnore
    public List<LinguisticSkill> foreignLanguagesWithIndex() {
        return withPreferences(this.indexedList(foreignLanguage), "ForeignLanguage");
    }

    /**
     * Used by the ODT generator to prepare a string with the list of mother
     * tongues (comma-separated)
     *
     * @return
     */
    public String motherTongueToString() {
        if (this.motherTongue == null) {
            return "";
        }

        int listSize = this.motherTongue.size();
        boolean skipNext = true;

        StringBuilder builder = new StringBuilder("");
        for (int i = 0; i < listSize; i++) {

            LinguisticSkill lingo = this.motherTongue.get(i);

            if (lingo == null) {
                //if the item is null then skip the comma
                skipNext = skipNext && true;
                continue;
            }
            CodeLabel desc = lingo.getDescription();
            if (desc == null) {
                skipNext = skipNext && true;
                continue;
            }
            String label = desc.getLabel();
            if (Strings.isNullOrEmpty(label)) {
                skipNext = skipNext && true;
                continue;
            }

            // decide when to add the comma
            if (i > 0 && i < listSize && !skipNext) {
                builder.append(", ");
            }

            builder.append(label);
            skipNext = false;
        }
        return builder.toString();
    }

    /**
     * Used by the ODT generator to prepare a string with the list of foreign
     * languages (comma-separated)
     *
     * @return
     */
    public String foreignLanguageToString() {
        if (this.foreignLanguage == null) {
            return "";
        }

        int listSize = this.foreignLanguage.size();
        boolean skipNext = true;

        StringBuilder builder = new StringBuilder("");
        for (int i = 0; i < listSize; i++) {

            LinguisticSkill lingo = this.foreignLanguage.get(i);

            if (lingo == null) {
                //if the item is null then skip the comma
                skipNext = skipNext && true;
                continue;
            }
            CodeLabel desc = lingo.getDescription();
            if (desc == null) {
                skipNext = skipNext && true;
                continue;
            }

            // decide when to add the comma
            if (i > 0 && i < listSize && !skipNext) {
                builder.append(", ");
            }

            builder.append(desc.getLabel());
            skipNext = false;
        }
        return builder.toString();
    }

    @JsonIgnore
    public boolean onlyDisplayLanguages() {
        return emptyProficiencyLevels() && emptyCertificates()
                && emptyReferenceToList();
    }

    @JsonIgnore
    public boolean emptyProficiencyLevels() {
        boolean allEmpty = true;
        if (this.foreignLanguage != null) {
            for (LinguisticSkill linguisticSkill : this.foreignLanguage) {
                if (linguisticSkill != null && !linguisticSkill.isEmptyProficiencyLevel()) {
                    allEmpty = false;
                    break;
                }
            }
        }

        return allEmpty;
    }

    @JsonIgnore
    public boolean emptyCertificates() {
        boolean allEmpty = true;
        if (this.foreignLanguage != null) {
            for (LinguisticSkill linguisticSkill : this.foreignLanguage) {
                if (linguisticSkill != null) {
                    if (!linguisticSkill.emptyVerifiedBy()) {
                        allEmpty = false;
                        break;
                    }
                }
            }
        }
        return allEmpty;
    }

    @JsonIgnore
    public boolean emptyReferenceToList() {
        boolean allEmpty = true;
        if (this.foreignLanguage != null) {
            for (LinguisticSkill linguisticSkill : this.foreignLanguage) {
                if (linguisticSkill != null) {
                    List<ReferenceTo> referenceToList = linguisticSkill.getReferenceToList();
                    if (referenceToList != null && !((WithDocumentList) referenceToList).checkEmpty()) {
                        allEmpty = false;
                        break;
                    }
                }
            }
        }
        return allEmpty;
    }

    @Override
    public void applyDefaultPreferences(List<PrintingPreference> newPrefs) {

        applyDefaultPreferences((PrintableList<LinguisticSkill>) getForeignLanguage(), LinguisticSkill.class, "ForeignLanguage", newPrefs);

        super.applyDefaultPreferences(newPrefs);

    }

    @JsonIgnore
    @Override
    public boolean checkEmpty() {
        return ((motherTongue == null || (motherTongue != null && ((WithDocumentList<LinguisticSkill>) getMotherTongue()).checkEmpty()))
                && (foreignLanguage == null || (foreignLanguage != null && ((WithPreferencesList<LinguisticSkill>) getForeignLanguage()).checkEmpty())));
    }
}
