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
import java.util.ResourceBundle;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.common.base.Strings;

import europass.ewa.resources.JsonResourceBundle;

@JsonPropertyOrder({
    "information",
    "communication",
    "contentCreation",
    "safety",
    "problemSolving"
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class ICTLevel extends PrintableObject {

    private String information;

    private String communication;

    private String contentCreation;

    private String safety;

    private String problemSolving;

    public ICTLevel() {
    }

    public ICTLevel(String information, String communication, String contentCreation, String safety, String problemSolving) {
        this.information = information;
        this.communication = communication;
        this.contentCreation = contentCreation;
        this.safety = safety;
        this.problemSolving = problemSolving;
    }

    @JsonProperty("Information")
    @JacksonXmlProperty(localName = "Information", namespace = Namespace.NAMESPACE)
    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    @JsonProperty("Communication")
    @JacksonXmlProperty(localName = "Communication", namespace = Namespace.NAMESPACE)
    public String getCommunication() {
        return communication;
    }

    public void setCommunication(String communication) {
        this.communication = communication;
    }

    @JsonProperty("ContentCreation")
    @JacksonXmlProperty(localName = "ContentCreation", namespace = Namespace.NAMESPACE)
    public String getContentCreation() {
        return contentCreation;
    }

    public void setContentCreation(String contentCreation) {
        this.contentCreation = contentCreation;
    }

    @JsonProperty("Safety")
    @JacksonXmlProperty(localName = "Safety", namespace = Namespace.NAMESPACE)
    public String getSafety() {
        return safety;
    }

    public void setSafety(String safety) {
        this.safety = safety;
    }

    @JsonProperty("ProblemSolving")
    @JacksonXmlProperty(localName = "ProblemSolving", namespace = Namespace.NAMESPACE)
    public String getProblemSolving() {
        return problemSolving;
    }

    public void setProblemSolving(String problemSolving) {
        this.problemSolving = problemSolving;
    }

    /**
     * ***************************************************************************
     */
    public PrintableValue<String> information() {
        return withPreferences(information, "Information");
    }

    public PrintableValue<String> communication() {
        return withPreferences(communication, "Communication");
    }

    public PrintableValue<String> contentCreation() {
        return withPreferences(contentCreation, "ContentCreation");
    }

    public PrintableValue<String> safety() {
        return withPreferences(safety, "Safety");
    }

    public PrintableValue<String> problemSolving() {
        return withPreferences(problemSolving, "ProblemSolving");
    }

    @Override
    public void applyDefaultPreferences(List<PrintingPreference> newPrefs) {

        information().applyDefaultPreferences(newPrefs);

        communication().applyDefaultPreferences(newPrefs);

        contentCreation().applyDefaultPreferences(newPrefs);

        safety().applyDefaultPreferences(newPrefs);

        problemSolving().applyDefaultPreferences(newPrefs);

        super.applyDefaultPreferences(newPrefs);

    }

    public String shortDescOdt(String key) {
        if (Strings.isNullOrEmpty(key)) {
            return "";
        } else {
            Locale locale = locale();
            ResourceBundle bundle = ResourceBundle.getBundle("bundles/IctLevel", locale, new JsonResourceBundle.Control(new ObjectMapper()));
            try {
                return bundle.getString(key);
            } catch (final Exception e) {
                return "";
            }
        }

    }

    public String shortDescInformationOdt() {
        return shortDescOdt(this.information);
    }

    public String shortDescCommunicationOdt() {
        return shortDescOdt(this.communication);
    }

    public String shortDescContentCreationOdt() {
        return shortDescOdt(this.contentCreation);
    }

    public String shortDescSafetyOdt() {
        return shortDescOdt(this.safety);
    }

    public String shortDescProblemSolvingOdt() {
        return shortDescOdt(this.problemSolving);
    }

    public boolean checkEmpty() {
        boolean isEmpty = (this.information == null || (this.information != null && this.information.isEmpty()))
                && (this.communication == null || (this.communication != null && this.communication.isEmpty()))
                && (this.contentCreation == null || (this.contentCreation != null && this.contentCreation.isEmpty()))
                && (this.safety == null || (this.safety != null && this.safety.isEmpty()))
                && (this.problemSolving == null || (this.problemSolving != null && this.problemSolving.isEmpty()));

        return isEmpty;
    }

}
