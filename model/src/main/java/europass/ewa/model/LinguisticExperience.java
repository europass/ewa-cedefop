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

import europass.ewa.enums.EuropassDocumentType;

@JsonPropertyOrder({
    "period",
    "description",
    "referenceToList",
    "area"
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class LinguisticExperience extends Experience {

    private CodeLabel area;

    @JsonProperty("Area")
    @JacksonXmlProperty(localName = "Area", namespace = Namespace.NAMESPACE)
    public CodeLabel getArea() {
        return area;
    }

    public void setArea(CodeLabel area) {
        this.area = area;
    }

    @JsonIgnore
    public void translateTo(SkillsPassport esp, Locale locale) {
        area = translate(esp, locale, "LinguisticExperienceType", area);
    }

    @JsonIgnore
    public void applyDefaultPreferences(List<PrintingPreference> newPrefs) {
        super.applyDefaultPreferences(newPrefs);
    }

    @JsonIgnore
    @Override
    public boolean checkEmpty() {
        return (super.checkEmpty()
                && (area == null || (area != null && area.checkEmpty())));
    }

    @JsonIgnore
    public boolean hasInfo() {
        if (area != null && area.getLabel() != null) {
            return true;
        }
        if (!Strings.isNullOrEmpty(description)) {
            return true;
        }
        return false;
    }

    @JsonIgnore
    public String areaAndDescriptionOdt() {

        if (area != null && area.getLabel() != null) {
            String xmlCleanArea = escapeForXml(area.getLabel());
            String areaWithDescription = description != null ? description : "";
            if (areaWithDescription.startsWith("<p>")) {
                areaWithDescription = areaWithDescription.replaceFirst("<p>", "<p><b>" + xmlCleanArea + ":</b> ");
            } else {
                areaWithDescription = description != null ? "<p><b>" + xmlCleanArea + ":</b> </p>" + description : "<p><b>" + xmlCleanArea + "</b></p>";
            }
            return this.asRichText(areaWithDescription, EuropassDocumentType.ELP, null);
        }
        return this.asRichText(description, EuropassDocumentType.ELP, null);
    }
}
