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

/**
 * Driving Skill: Description: List of String values corresponding to specific
 * driving license categories. Certificate.Level : The level of the certificate
 * may be expressed as a simple String.
 *
 * @author ekar
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DrivingSkill extends Skill<List<String>, String, String, Experience> {

    public DrivingSkill() {
    }

    public DrivingSkill(List<String> description) {
        this.setDescription(description);
    }

    @JsonIgnore
    public void applyDefaultPreferences(List<PrintingPreference> newPrefs) {
        super.applyDefaultPreferences(newPrefs);
    }

    @JsonIgnore
    public boolean hasOnlyOne() {
        List<String> description = this.getDescription();
        if (description == null || description.isEmpty()) {
            return true;
        }
        return description.size() == 1;
    }

    /**
     * Used by the ODT generator to prepare a string with the list of driving
     * diplomas(comma-separated)
     *
     * @return
     */
    public String drivingToString() {
        String commaList = "";

        List<String> description = this.getDescription();
        if (description == null) {
            return "";
        }

        int size = description.size();
        boolean skipNext = true;

        for (int i = 0; i < size; i++) {

            String licence = description.get(i);

            if (licence == null) {
                skipNext = skipNext && true;//if the item is null then skip the comma
                continue;
            }

            if (i > 0 && i < size && !skipNext) {
                commaList += ", ";
            }
            commaList += description.get(i);
            skipNext = false;
        }
        return commaList;
    }

}
