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
    "listening",
    "reading",
    "spokenInteraction",
    "spokenProduction",
    "writing"
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class CEFRLevel extends PrintableObject {

    private String listening;

    private String reading;

    private String spokenInteraction;

    private String spokenProduction;

    private String writing;

    public CEFRLevel() {
    }

    public CEFRLevel(String listening, String reading, String spokenInteraction, String spokenProduction, String writing) {
        this.listening = listening;
        this.reading = reading;
        this.spokenInteraction = spokenInteraction;
        this.spokenProduction = spokenProduction;
        this.writing = writing;
    }

    @JsonProperty("Listening")
    @JacksonXmlProperty(localName = "Listening", namespace = Namespace.NAMESPACE)
    public String getListening() {
        return listening;
    }

    public void setListening(String listening) {
        this.listening = listening;
    }

    @JsonProperty("Reading")
    @JacksonXmlProperty(localName = "Reading", namespace = Namespace.NAMESPACE)
    public String getReading() {
        return reading;
    }

    public void setReading(String reading) {
        this.reading = reading;
    }

    @JsonProperty("SpokenInteraction")
    @JacksonXmlProperty(localName = "SpokenInteraction", namespace = Namespace.NAMESPACE)
    public String getSpokenInteraction() {
        return spokenInteraction;
    }

    public void setSpokenInteraction(String spokenInteraction) {
        this.spokenInteraction = spokenInteraction;
    }

    @JsonProperty("SpokenProduction")
    @JacksonXmlProperty(localName = "SpokenProduction", namespace = Namespace.NAMESPACE)
    public String getSpokenProduction() {
        return spokenProduction;
    }

    public void setSpokenProduction(String spokenProduction) {
        this.spokenProduction = spokenProduction;
    }

    @JsonProperty("Writing")
    @JacksonXmlProperty(localName = "Writing", namespace = Namespace.NAMESPACE)
    public String getWriting() {
        return writing;
    }

    public void setWriting(String writing) {
        this.writing = writing;
    }

    /**
     * ***************************************************************************
     */
    public PrintableValue<String> listening() {
        return withPreferences(listening, "Listening");
    }

    public PrintableValue<String> reading() {
        return withPreferences(reading, "Reading");
    }

    public PrintableValue<String> spokenInteraction() {
        return withPreferences(spokenInteraction, "SpokenInteraction");
    }

    public PrintableValue<String> spokenProduction() {
        return withPreferences(spokenProduction, "SpokenProduction");
    }

    public PrintableValue<String> writing() {
        return withPreferences(writing, "Writing");
    }

    @Override
    public void applyDefaultPreferences(List<PrintingPreference> newPrefs) {

        listening().applyDefaultPreferences(newPrefs);

        reading().applyDefaultPreferences(newPrefs);

        spokenInteraction().applyDefaultPreferences(newPrefs);

        spokenProduction().applyDefaultPreferences(newPrefs);

        writing().applyDefaultPreferences(newPrefs);

        super.applyDefaultPreferences(newPrefs);

    }

    public String shortDescOdt(String key) {
        if (Strings.isNullOrEmpty(key)) {
            return "";
        } else {
            Locale locale = locale();
            ResourceBundle bundle = ResourceBundle.getBundle("bundles/LanguageShortLevel", locale,
                    new JsonResourceBundle.Control(new ObjectMapper()));
            try {
                return bundle.getString(key);
            } catch (final Exception e) {
                return "";
            }
        }

    }

    public String shortDescListeningOdt() {
        return shortDescOdt(this.listening);
    }

    public String shortDescReadingOdt() {
        return shortDescOdt(this.reading);
    }

    public String shortDescSpokenInteractionOdt() {
        return shortDescOdt(this.spokenInteraction);
    }

    public String shortDescSpokenProductionOdt() {
        return shortDescOdt(this.spokenProduction);
    }

    public String shortDescWritingOdt() {
        return shortDescOdt(this.writing);
    }

}
