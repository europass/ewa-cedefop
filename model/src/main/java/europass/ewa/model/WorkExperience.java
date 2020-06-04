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

import europass.ewa.model.format.HtmlSanitizer;
import europass.ewa.model.reflection.ReflectionUtils;

@JsonPropertyOrder({
    "period",
    "description",
    "referenceToList",
    "position",
    "activities",
    "employer"
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkExperience extends Experience {

    private CodeLabel position;

    private String activities;

    private EmployerOrganisation employer;

    public WorkExperience() {
    }

    @JsonProperty("Position")
    @JacksonXmlProperty(localName = "Position", namespace = Namespace.NAMESPACE)
    public CodeLabel getPosition() {
        return position;
    }

    public void setPosition(CodeLabel position) {
        this.position = position;
    }

    @JsonProperty("Activities")
    @JacksonXmlProperty(localName = "Activities", namespace = Namespace.NAMESPACE)
    public String getActivities() {
        return activities;
    }

    public void setActivities(String activities) {
        this.activities = escapeNewLineCharacters(HtmlSanitizer.sanitize(activities));
    }

    @JsonProperty("Employer")
    @JacksonXmlProperty(localName = "Employer", namespace = Namespace.NAMESPACE)
    public EmployerOrganisation getEmployer() {
        return withPreferences(employer, "Employer");
    }

    public void setEmployer(EmployerOrganisation employer) {
        this.employer = employer;
    }

    /**
     * *********************************************************************
     */
    @JsonIgnore
    public void translateTo(SkillsPassport esp, Locale locale) {
        position = translate(esp, locale, "OccupationalField", position, ReflectionUtils.ADJUST_OCCUPATION_LABEL, null);
    }

    public String activitiesOdt() {
        return this.asRichText(activities);
    }

    @Override
    public void applyDefaultPreferences(List<PrintingPreference> newPrefs) {

        applyDefaultPreferences(getEmployer(), EmployerOrganisation.class, "Employer", newPrefs);

        super.applyDefaultPreferences(newPrefs);

    }

    @JsonIgnore
    @Override
    public boolean checkEmpty() {
        return (super.checkEmpty()
                && Strings.isNullOrEmpty(activities)
                && (position == null || (position != null && position.checkEmpty()))
                && (employer == null || (employer != null && employer.checkEmpty())));
    }

}
