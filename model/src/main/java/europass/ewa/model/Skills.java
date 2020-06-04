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
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonPropertyOrder({
    "linguistic",
    "communication",
    "organisational",
    "jobRelated",
    "computer",
    "driving",
    "other"
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Skills extends PrintableObject {

    private LinguisticSkills linguistic;
    private GenericSkill communication;
    private GenericSkill organisational;
    private GenericSkill jobRelated;
    private ComputerSkill computer;
    private DrivingSkill driving;
    private GenericSkill other;

    public Skills() {
    }

    @JsonProperty("Linguistic")
    @JacksonXmlProperty(localName = "Linguistic", namespace = Namespace.NAMESPACE)
    public LinguisticSkills getLinguistic() {
        return withPreferences(linguistic, "Linguistic");
    }

    public void setLinguistic(LinguisticSkills linguistic) {
        this.linguistic = linguistic;
    }

    @JsonProperty("Communication")
    @JacksonXmlProperty(localName = "Communication", namespace = Namespace.NAMESPACE)
    public GenericSkill getCommunication() {
        return withDocument(communication, getDocument());
    }

    public void setCommunication(GenericSkill communication) {
        this.communication = communication;
    }

    @JsonProperty("Organisational")
    @JacksonXmlProperty(localName = "Organisational", namespace = Namespace.NAMESPACE)
    public GenericSkill getOrganisational() {
        return withDocument(organisational, getDocument());
    }

    public void setOrganisational(GenericSkill organisational) {
        this.organisational = organisational;
    }

    @JsonProperty("JobRelated")
    @JacksonXmlProperty(localName = "JobRelated", namespace = Namespace.NAMESPACE)
    public GenericSkill getJobRelated() {
        return withDocument(jobRelated, getDocument());
    }

    public void setJobRelated(GenericSkill jobRelated) {
        this.jobRelated = jobRelated;
    }

    @JsonProperty("Computer")
    @JacksonXmlProperty(localName = "Computer", namespace = Namespace.NAMESPACE)
    public ComputerSkill getComputer() {
        return withPreferences(computer, "Computer");
    }

    public void setComputer(ComputerSkill computer) {
        this.computer = computer;
    }

    @JsonProperty("Driving")
    @JacksonXmlProperty(localName = "Driving", namespace = Namespace.NAMESPACE)
    public DrivingSkill getDriving() {
        return withDocument(driving, getDocument());
    }

    public void setDriving(DrivingSkill driving) {
        this.driving = driving;
    }

    @JsonProperty("Other")
    @JacksonXmlProperty(localName = "Other", namespace = Namespace.NAMESPACE)
    public GenericSkill getOther() {
        return withDocument(other, getDocument());
    }

    public void setOther(GenericSkill other) {
        this.other = other;
    }

    /**
     * **************************************************************************************************
     */
    @Override
    public void applyDefaultPreferences(List<PrintingPreference> newPrefs) {

        applyDefaultPreferences(getLinguistic(), LinguisticSkills.class, "Linguistic", newPrefs);

        super.applyDefaultPreferences(newPrefs);

    }

    @JsonIgnore
    @Override
    public boolean checkEmpty() {
        return ((linguistic == null || (linguistic != null && linguistic.checkEmpty()))
                && (communication == null || (communication != null && communication.checkEmpty()))
                && (organisational == null || (organisational != null && organisational.checkEmpty()))
                && (jobRelated == null || (jobRelated != null && jobRelated.checkEmpty()))
                && (computer == null || (computer != null && computer.checkEmpty()))
                && (other == null || (other != null && other.checkEmpty()))
                && (driving == null || (driving != null && driving.checkEmpty())));
    }
}
