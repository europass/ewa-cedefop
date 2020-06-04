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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import europass.ewa.model.decorator.WithDocumentList;
import europass.ewa.model.wrapper.IdRefSafeList;
import org.apache.commons.lang.StringUtils;

/**
 * Linguistic Skill (mother tongue and foreign language) Description: A
 * CodeLabel object showing the specific language code and label.
 * Certificate.Level : The level of the certificate may be expressed as a
 * CEFRLevel object.
 *
 * @author ekar
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class LinguisticSkill extends Skill<CodeLabel, CEFRLevel, String, LinguisticExperience> {

    public LinguisticSkill() {
    }

    public LinguisticSkill(CodeLabel description) {
        this.setDescription(description);
    }

    @Override
    @JsonProperty("Description")
    @JacksonXmlProperty(localName = "Description", namespace = Namespace.NAMESPACE)
    public CodeLabel getDescription() {
        return super.getDescription();
    }

    @Override
    @JsonProperty("ProficiencyLevel")
    @JacksonXmlProperty(localName = "ProficiencyLevel", namespace = Namespace.NAMESPACE)
    public CEFRLevel getProficiencyLevel() {
        return withPreferences(super.getProficiencyLevel(), "ProficiencyLevel");
    }

    @Override
    @JsonProperty("Certificate")
    @JacksonXmlProperty(localName = "Certificate", namespace = Namespace.NAMESPACE)
    @JacksonXmlElementWrapper(localName = "VerifiedBy", namespace = Namespace.NAMESPACE)
    public List<Certificate<String>> getVerifiedBy() {
        return withPreferences(super.getVerifiedBy(), "Certificate");
    }

    @Override
    @JsonProperty("Experience")
    @JacksonXmlProperty(localName = "Experience", namespace = Namespace.NAMESPACE)
    @JacksonXmlElementWrapper(localName = "AcquiredDuring", namespace = Namespace.NAMESPACE)
    public List<LinguisticExperience> getAcquiredDuring() {
        return withPreferences(super.getAcquiredDuring(), "Experience");
    }

    @Override
    public void setAcquiredDuring(List<LinguisticExperience> acquiredDuring) {
        super.setAcquiredDuring(acquiredDuring);
    }

    /**
     * *********************************************************************
     */
    @JsonIgnore
    public boolean emptyVerifiedBy() {
        if (this.getVerifiedBy() == null) {
            return true;
        }
        return this.getVerifiedBy().isEmpty();
    }

    @JsonIgnore
    public boolean emptyAcquiredDuring() {
        if (this.getAcquiredDuring() == null) {
            return true;
        }
        return this.getAcquiredDuring().isEmpty();
    }

    @JsonIgnore
    public boolean isEmptyProficiencyLevel() {
        return getProficiencyLevel() == null;
    }

    @JsonIgnore
    public void translateTo(SkillsPassport esp, Locale locale) {
        super.setDescription(translate(esp, locale, "Language", super.getDescription()));
    }

    @JsonIgnore
    public List<Certificate<String>> getVerifiedByWithIndex() {
        return withPreferences(this.indexedList(super.getVerifiedBy()), "Certificate");
    }

    @JsonIgnore
    public List<LinguisticExperience> getAcquiredDuringWithIndex() {
        return withPreferences(this.indexedList(super.getAcquiredDuring()), "Experience");
    }

    @Override
    public void applyDefaultPreferences(List<PrintingPreference> newPrefs) {

        applyDefaultPreferencesToCertificate("Certificate", newPrefs);

        applyDefaultPreferencesToExperience("Experience", newPrefs);

        super.applyDefaultPreferences(newPrefs);

    }

    protected void applyDefaultPreferencesToCertificate(String key, List<PrintingPreference> newPrefs) {
        PrintableList<Certificate<String>> lst = (PrintableList<Certificate<String>>) getVerifiedBy();
        if (lst == null || lst.isEmpty()) {
            Certificate<String> obj = new Certificate<String>();
            lst = (PrintableList<Certificate<String>>) withPreferences(Arrays.asList(obj), key);
        }
        lst.applyDefaultPreferences(newPrefs);
    }

    protected void applyDefaultPreferencesToExperience(String key, List<PrintingPreference> newPrefs) {
        PrintableList<LinguisticExperience> lst = (PrintableList<LinguisticExperience>) getAcquiredDuring();
        if (lst == null || lst.isEmpty()) {
            LinguisticExperience obj = new LinguisticExperience();
            lst = (PrintableList<LinguisticExperience>) withPreferences(Arrays.asList(obj), key);
        }
        lst.applyDefaultPreferences(newPrefs);
    }

    @JsonIgnore
    @Override
    public boolean checkEmpty() {
        CodeLabel description = this.description;
        CEFRLevel proficiencyLevel = this.proficiencyLevel;
        List<ReferenceTo> referenceToList = super.referenceToList;
        return ((description == null || (description != null && description.checkEmpty()))
                && (proficiencyLevel == null || (proficiencyLevel != null && proficiencyLevel.checkEmpty()))
                && (checkEmptyCertificates())
                && (checkEmptyExperience())
                && (referenceToList == null || (referenceToList != null && ((IdRefSafeList) referenceToList).checkEmpty())));
    }

    private boolean checkEmptyCertificates() {
        try {
            List<Certificate<String>> list = super.verifiedBy;
            if (list == null) {
                return true;
            }

            list = (getVerifiedBy() == null ? new ArrayList<Certificate<String>>() : getVerifiedBy());

            WithDocumentList<Certificate<String>> printable = new WithDocumentList<Certificate<String>>(list);
            return printable.checkEmpty();

        } catch (final Exception e) {
            return true;
        }
    }

    private boolean checkEmptyExperience() {
        try {
            List<LinguisticExperience> list = super.acquiredDuring;

            if (list == null) {
                return true;
            }

            WithDocumentList<LinguisticExperience> printable = (WithDocumentList<LinguisticExperience>) getAcquiredDuring();
            if (printable == null) {
                return true;
            }
            return printable.checkEmpty();

        } catch (final Exception e) {
            return true;
        }
    }
}
