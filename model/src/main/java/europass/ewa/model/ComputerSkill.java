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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import europass.ewa.model.decorator.WithDocumentList;
import europass.ewa.model.format.HtmlSanitizer;
import europass.ewa.model.wrapper.IdRefSafeList;

/**
 * Computer Skill Description: A String object describing the computer skills
 * Proficiency.Level : The assessment of the computer skills may be expressed as
 * a ICTLevel object. VerifiedBy.Certificate.Title: The list of strings
 * representing the certifications Documentation.ReferenceTo: The attachments.
 *
 * @author vpol
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComputerSkill extends Skill<String, ICTLevel, String, Experience> {

    public ComputerSkill() {
    }

    public ComputerSkill(String description) {
        this.setDescription(description);
    }

    @Override
    public void setDescription(String description) {
        super.setDescription(escapeNewLineCharacters(HtmlSanitizer.sanitize(description)));
    }

    @Override
    @JsonProperty("Description")
    @JacksonXmlProperty(localName = "Description", namespace = Namespace.NAMESPACE)
    public String getDescription() {
        return super.getDescription();
    }

    public String descriptionOdt() {
        return this.asRichText(this.getDescription());
    }

    @Override
    @JsonProperty("ProficiencyLevel")
    @JacksonXmlProperty(localName = "ProficiencyLevel", namespace = Namespace.NAMESPACE)
    public ICTLevel getProficiencyLevel() {
        return withPreferences(super.getProficiencyLevel(), "ProficiencyLevel");
    }

    @Override
    @JsonProperty("Certificate")
    @JacksonXmlProperty(localName = "Certificate", namespace = Namespace.NAMESPACE)
    @JacksonXmlElementWrapper(localName = "VerifiedBy", namespace = Namespace.NAMESPACE)
    public List<Certificate<String>> getVerifiedBy() {
        return withPreferences(super.getVerifiedBy(), "Certificate");
    }

    /**
     * *********************************************************************
     */
    @JsonIgnore
    public List<Certificate<String>> getVerifiedByWithIndex() {
        return withPreferences(this.indexedList(super.getVerifiedBy()), "Certificate");
    }

    @Override
    public void applyDefaultPreferences(List<PrintingPreference> newPrefs) {

        applyDefaultPreferencesToCertificate("Certificate", newPrefs);

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

    @JsonIgnore
    @Override
    public boolean checkEmpty() {
        String description = this.description;
        ICTLevel proficiencyLevel = this.proficiencyLevel;
        List<ReferenceTo> referenceToList = super.referenceToList;
        boolean isEmpty = (description == null || (description != null && description.isEmpty()))
                && (proficiencyLevel == null || (proficiencyLevel != null && proficiencyLevel.checkEmpty()))
                && (checkEmptyCertificates())
                && (referenceToList == null || (referenceToList != null && ((IdRefSafeList) referenceToList).checkEmpty()));
        return isEmpty;
    }

    /**
     * should return true for non-empty false for empty
     */
    @JsonIgnore
    public boolean checkNonEmptyIctGrid() {
        ICTLevel proficiencyLevel = this.proficiencyLevel;
        List<ReferenceTo> referenceToList = super.referenceToList;
        return (proficiencyLevel != null || (proficiencyLevel != null && !proficiencyLevel.checkEmpty())
                || !checkEmptyCertificates()
                || (referenceToList != null && !((IdRefSafeList) referenceToList).checkEmpty()));

    }

    @JsonIgnore
    public boolean checkNonEmptySelfAssessmentIctGrid() {

        final ICTLevel proficiencyLevel = this.proficiencyLevel;

        return (proficiencyLevel != null || (proficiencyLevel != null && !proficiencyLevel.checkEmpty()));
    }

    /**
     * should return true for non-empty false for empty
     */
    @JsonIgnore
    public boolean checkNonEmptyDescription() {
        String description = this.description;

        return (description != null && !description.isEmpty());
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
}
