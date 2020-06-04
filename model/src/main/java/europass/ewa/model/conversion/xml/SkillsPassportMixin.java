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
package europass.ewa.model.conversion.xml;

import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import europass.ewa.model.Namespace;
import europass.ewa.model.PrintingPreference;

@JsonPropertyOrder({"xsiNamespace", "schemaLocation", "locale", "documentInfo", "documentPrintingPrefs", "learnerInfo", "attachmentList", "coverLetter"})
public abstract class SkillsPassportMixin {

    public SkillsPassportMixin() {
    }

    @JsonProperty("PrintingPreferences")
    @JacksonXmlProperty(localName = "PrintingPreferences", namespace = Namespace.NAMESPACE)
    @JsonSerialize(using = PrintingPreferencesSerialiser.class)
    public abstract Map<String, List<PrintingPreference>> getDocumentPrintingPrefs();

    @JsonProperty("PrintingPreferences")
    @JacksonXmlProperty(localName = "PrintingPreferences", namespace = Namespace.NAMESPACE)
    @JsonDeserialize(using = PrintingPreferencesDeserialiser.class)
    public abstract void setDocumentPrintingPrefs(Map<String, List<PrintingPreference>> documentPrintingPrefs);

}
