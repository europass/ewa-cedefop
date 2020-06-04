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

import europass.ewa.model.format.HtmlSanitizer;
import europass.ewa.model.wrapper.IdRefSafeList;

@JsonPropertyOrder({
    "period",
    "description",
    "referenceToList"
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Experience extends PrintableObject implements ReferenceToReceptor {

    private Period period;

    protected String description;

    private List<ReferenceTo> referenceToList;

    public Experience() {
    }

    @JsonProperty("Period")
    @JacksonXmlProperty(localName = "Period", namespace = Namespace.NAMESPACE)
    public Period getPeriod() {
        return withPreferences(period, "Period");
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    @JsonProperty("Description")
    @JacksonXmlProperty(localName = "Description", namespace = Namespace.NAMESPACE)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = escapeNewLineCharacters(HtmlSanitizer.sanitize(description));
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
     * **********************************************************************************************
     */
    @JsonIgnore
    @Override
    public List<ReferenceTo> listOfReferenceTo() {
        return this.referenceToList;
    }

    @JsonIgnore
    public String descriptionOdt() {
        return this.asRichText(description);
    }

    @Override
    public void applyDefaultPreferences(List<PrintingPreference> newPrefs) {

        applyDefaultPreferences(getPeriod(), Period.class, "Period", newPrefs);

        super.applyDefaultPreferences(newPrefs);

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
    public boolean checkEmpty() {
        return ((Strings.isNullOrEmpty(description))
                && (period == null || (period != null && period.checkEmpty()))
                && (referenceToList == null || (referenceToList != null && ((IdRefSafeList) referenceToList).checkEmpty())));
    }
}
