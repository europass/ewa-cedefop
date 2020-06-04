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
import com.google.common.base.Strings;

@JsonPropertyOrder({
    "title",
    "awardingBody",
    "date",
    "level"
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Certificate<L> extends PrintableObject {

    private String title;

    private String awardingBody;

    private JDate date;

    private L level;

    public Certificate() {
    }

    public Certificate(String title) {
        this.title = title;
    }

    @JsonProperty("Title")
    @JacksonXmlProperty(localName = "Title", namespace = Namespace.NAMESPACE)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @JsonProperty("AwardingBody")
    @JacksonXmlProperty(localName = "AwardingBody", namespace = Namespace.NAMESPACE)
    public String getAwardingBody() {
        return awardingBody;
    }

    public void setAwardingBody(String awardingBody) {
        this.awardingBody = awardingBody;
    }

    @JsonProperty("Date")
    @JacksonXmlProperty(localName = "Date", namespace = Namespace.NAMESPACE)
    public JDate getDate() {
        return date;
    }

    public void setDate(JDate date) {
        this.date = date;
    }

    @JsonProperty("Level")
    @JacksonXmlProperty(localName = "Level", namespace = Namespace.NAMESPACE)
    public L getLevel() {
        return level;
    }

    public void setLevel(L level) {
        this.level = level;
    }

    public PrintableValue<JDate> date() {
        return withPreferences(date, "Date");
    }

    /**
     * Used by ODT Generator to format the birtdate according to the selected
     * format
     *
     * @return
     */
    @JsonIgnore
    public String formatDate() {
        if (date != null) {
            PrintingPreference pref = date().pref() == null ? this.preferencesFromDocument(this.document(), date().prefKey()) : date().pref();
            String formatName = (pref == null) ? JDate.DEFAULT_FORMAT : pref.getFormat();
            return date.format(formatName, locale());
        }
        return "";
    }

    /**
     * ***************************************************************************
     */
    public PrintableValue<String> title() {
        return withPreferences(title, "Title");
    }

    public PrintableValue<String> awardingBody() {
        return withPreferences(awardingBody, "AwardingBody");
    }

    @Override
    public void applyDefaultPreferences(List<PrintingPreference> newPrefs) {

        title().applyDefaultPreferences(newPrefs);

        date().applyDefaultPreferences(newPrefs);

        awardingBody().applyDefaultPreferences(newPrefs);

        super.applyDefaultPreferences(newPrefs);

    }

    @JsonIgnore
    @Override
    public boolean checkEmpty() {
        return (Strings.isNullOrEmpty(title)
                && Strings.isNullOrEmpty(awardingBody)
                && (date == null || (date != null && date.checkEmpty()))
                && level == null);
    }

}
