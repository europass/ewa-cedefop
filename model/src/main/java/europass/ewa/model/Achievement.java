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
import java.util.Locale;

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
    "title",
    "description",
    "referenceToList"
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Achievement extends PrintableObject implements ReferenceToReceptor {

    private CodeLabel title;

    private String description;

    private List<ReferenceTo> referenceToList;

    public Achievement() {
    }

    public Achievement(CodeLabel title, String description) {
        this.title = title;
        this.description = description;
    }

    @JsonProperty("Title")
    @JacksonXmlProperty(localName = "Title", namespace = Namespace.NAMESPACE)
    public CodeLabel getTitle() {
        return title;
    }

    public void setTitle(CodeLabel title) {
        this.title = title;
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
     * *************************************************************************
     */
    @JsonIgnore
    public void translateTo(SkillsPassport esp, Locale locale) {
        title = translate(esp, locale, "AchievementType", title);
    }

    @JsonIgnore
    public String descriptionOdt() {
        return this.asRichText(description);
    }

    @JsonIgnore
    @Override
    public List<ReferenceTo> listOfReferenceTo() {
        return this.referenceToList;
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

    @Override
    public void applyDefaultPreferences(List<PrintingPreference> newPrefs) {

        super.applyDefaultPreferences(newPrefs);

    }

    @Override
    public boolean checkEmpty() {
        return ((title == null || (title != null && title.checkEmpty()))
                && Strings.isNullOrEmpty(description)
                && (referenceToList == null || (referenceToList != null && ((IdRefSafeList) referenceToList).checkEmpty())));
    }
}
