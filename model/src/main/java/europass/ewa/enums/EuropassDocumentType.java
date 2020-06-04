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
package europass.ewa.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.base.Strings;

public enum EuropassDocumentType {
    //description    , acronym  , prefs, metadata //DocumentLabel.key
    ECV_ESP("CV-ESP-Europass", "ECV_ESP", "ECV", "CV, ESP", "Europass.CV.ESP"),
    ESP("ESP-Europass", "ESP", "ESP", "ESP", "European.Skills.Passport"),
    ECV("CV-Europass", "ECV", "ECV", "CV", "Document.Header.CV"),
    ELP("LP-Europass", "ELP", "ELP", "LP", "Document.Header.ELP"),
    ECL("Cover-Letter-Europass", "ECL", "ECL", "CL", "Document.Header.ECL"),
    UNKNOWN("Europass-Document", "EuropassDocument", "EuropassDocument", "Europass Document", "Europass.Document");

    private String description;

    private String acronym;

    private String preferencesAcronym;

    private String metadata;

    private String documentLabelKey;

    EuropassDocumentType(String description, String acronym, String preferencesAcronym, String metadata, String documentLabelKey) {
        this.description = description;
        this.acronym = acronym;
        this.preferencesAcronym = preferencesAcronym;
        this.metadata = metadata;
        this.documentLabelKey = documentLabelKey;
    }

    public String getDesription() {
        return description;
    }

    public String getAcronym() {
        return acronym;
    }

    public String getPreferencesAcronym() {
        return preferencesAcronym;
    }

    public String getMetadata() {
        return metadata;
    }

    public String getDocumentLabelKey() {
        return documentLabelKey;
    }

    private static EuropassDocumentType matchAcronym(String str) {
        if (Strings.isNullOrEmpty(str)) {
            return UNKNOWN;
        }
        for (EuropassDocumentType agent : values()) {
            if (str.equals(agent.acronym)) {
                return agent;
            }
        }
        return UNKNOWN;
    }

    @JsonCreator
    public static EuropassDocumentType forValue(String str) {
        return matchAcronym(str);
    }
}
