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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import europass.ewa.model.format.HtmlSanitizer;

/**
 * Generic Skill (organisational, communication, etc.) Description: A String
 * showing the specific language code and label. Certificate.Level : The level
 * of the certificate may be expressed as a String.
 *
 * @author ekar
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class GenericSkill extends Skill<String, String, String, Experience> {

    public GenericSkill() {
    }

    public GenericSkill(String description) {
        this.setDescription(description);
    }

    @Override
    public void setDescription(String description) {
        super.setDescription(escapeNewLineCharacters(HtmlSanitizer.sanitize(description)));
    }

    @Override
    public String getDescription() {
        return super.getDescription();
    }

    public String descriptionOdt() {
        return this.asRichText(this.getDescription());
    }
}
