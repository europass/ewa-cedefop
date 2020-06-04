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
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.common.base.Strings;

import europass.ewa.model.decorator.WithPreferences;
import europass.ewa.model.format.HtmlSanitizer;

/**
 * Extends the Experience
 *
 * @author ekar
 *
 */
@JsonPropertyOrder({
    "period",
    "description",
    "referenceToList",
    "title",
    "activities",
    "organisation",
    "level",
    "field"
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Education extends Experience implements WithPreferences {

    private String title;

    private String activities;

    private Organisation organisation;

    private CodeLabel level;

    private CodeLabel field;

    public Education() {
    }

    @JsonProperty("Title")
    @JacksonXmlProperty(localName = "Title", namespace = Namespace.NAMESPACE)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("Activities")
    @JacksonXmlProperty(localName = "Activities", namespace = Namespace.NAMESPACE)
    public String getActivities() {
        return activities;
    }

    public void setActivities(String activities) {
        this.activities = escapeNewLineCharacters(HtmlSanitizer.sanitize(activities));
    }

    @JsonProperty("Organisation")
    @JacksonXmlProperty(localName = "Organisation", namespace = Namespace.NAMESPACE)
    public Organisation getOrganisation() {
        return withPreferences(organisation, "Organisation");
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    @JsonProperty("Level")
    @JacksonXmlProperty(localName = "Level", namespace = Namespace.NAMESPACE)
    public CodeLabel getLevel() {
        return level;
    }

    public void setLevel(CodeLabel level) {
        this.level = level;
    }

    @JsonProperty("Field")
    @JacksonXmlProperty(localName = "Field", namespace = Namespace.NAMESPACE)
    public CodeLabel getField() {
        return field;
    }

    public void setField(CodeLabel field) {
        this.field = field;
    }

    /**
     * *********************************************************************
     */
    @JsonIgnore
    public void translateTo(SkillsPassport esp, Locale locale) {
        level = translate(esp, locale, "EducationalLevel", level);
        field = translate(esp, locale, "EducationalField", field);
    }

    /**
     * Prints the activities to the odt
     *
     * @return
     */
    public String activitiesOdt() {
        return this.asRichText(activities);
    }

    /**
     * ********************************************************************************
     */
    @Override
    public void applyDefaultPreferences(List<PrintingPreference> newPrefs) {

        applyDefaultPreferences(getOrganisation(), Organisation.class, "Organisation", newPrefs);

        super.applyDefaultPreferences(newPrefs);

    }

    @JsonIgnore
    @Override
    public boolean checkEmpty() {
        return (super.checkEmpty()
                && Strings.isNullOrEmpty(title)
                && Strings.isNullOrEmpty(activities)
                && (organisation == null || (organisation != null && organisation.checkEmpty()))
                && (level == null || (level != null && level.checkEmpty()))
                && (field == null || (field != null && field.checkEmpty())));
    }

}
