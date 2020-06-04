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

import europass.ewa.model.decorator.WithDocumentList;
import europass.ewa.model.wrapper.IdRefSafeList;

/**
 *
 * @author ekar
 *
 * @param <D> the type of the Description property
 * @param <L> the type of the Certificate.Level property
 */
@JsonPropertyOrder({
    "description",
    "proficiencyLevel",
    "acquiredDuring",
    "verifiedBy",
    "referenceToList"
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Skill<D, L, C, E extends Experience> extends PrintableObject implements ReferenceToReceptor {

    D description;

    L proficiencyLevel;

    List<E> acquiredDuring;

    List<Certificate<C>> verifiedBy;

    List<ReferenceTo> referenceToList;

    public Skill() {
    }

    public Skill(D description) {
        this.description = description;
    }

    @JsonProperty("Description")
    @JacksonXmlProperty(localName = "Description", namespace = Namespace.NAMESPACE)
    public D getDescription() {
        return description;
    }

    public void setDescription(D description) {
        this.description = description;
    }

    @JsonProperty("ProficiencyLevel")
    @JacksonXmlProperty(localName = "ProficiencyLevel", namespace = Namespace.NAMESPACE)
    public L getProficiencyLevel() {
        return proficiencyLevel;
    }

    public void setProficiencyLevel(L proficiencyLevel) {
        this.proficiencyLevel = proficiencyLevel;
    }

    @JsonProperty("Experience")
    @JacksonXmlProperty(localName = "Experience", namespace = Namespace.NAMESPACE)
    @JacksonXmlElementWrapper(localName = "AcquiredDuring", namespace = Namespace.NAMESPACE)
    public List<E> getAcquiredDuring() {
        return withPreferences(acquiredDuring, "Experience");
    }

    public void setAcquiredDuring(List<E> acquiredDuring) {
        this.acquiredDuring = acquiredDuring;
    }

    @JsonProperty("Certificate")
    @JacksonXmlProperty(localName = "Certificate", namespace = Namespace.NAMESPACE)
    @JacksonXmlElementWrapper(localName = "VerifiedBy", namespace = Namespace.NAMESPACE)
    public List<Certificate<C>> getVerifiedBy() {
        return withPreferences(verifiedBy, "Certificate");
    }

    public void setVerifiedBy(List<Certificate<C>> verifiedBy) {
        this.verifiedBy = verifiedBy;
    }

    @JsonProperty("ReferenceTo")
    @JacksonXmlProperty(localName = "ReferenceTo", namespace = Namespace.NAMESPACE)
    @JacksonXmlElementWrapper(localName = "Documentation", namespace = Namespace.NAMESPACE)
    public List<ReferenceTo> getReferenceToList() {
        return withDocument(referenceToList, getDocument());
    }

    public void setReferenceToList(List<ReferenceTo> referenceToList) {
        this.referenceToList = IdRefSafeList.getInstance(referenceToList);
    }

    /**
     * *********************************************************************
     */
    @Override
    public void applyDefaultPreferences(List<PrintingPreference> newPrefs) {

        super.applyDefaultPreferences(newPrefs);

        //IMPORTANT!!! Experience and Certificate to be handled by the classes that extend Skill.java
    }

    /**
     * augments the list items with extra info such as index, isFirst, etc.
     *
     * @return
     */
    @JsonIgnore
    @Override
    public List<ReferenceTo> referenceToListWithIndex() {
        return withDocument(this.indexedList(referenceToList), getDocument());
    }

    @JsonIgnore
    @Override
    public List<ReferenceTo> listOfReferenceTo() {
        return this.referenceToList;
    }

    @JsonIgnore
    @Override
    public boolean checkEmpty() {
        return ((description == null)
                && (proficiencyLevel == null)
                && (verifiedBy == null || (verifiedBy != null && ((WithDocumentList<Certificate<C>>) getVerifiedBy()).checkEmpty()))
                && (acquiredDuring == null || (acquiredDuring != null && ((WithDocumentList<E>) getAcquiredDuring()).checkEmpty()))
                && (referenceToList == null || (referenceToList != null && ((IdRefSafeList) referenceToList).checkEmpty())));
    }
}
