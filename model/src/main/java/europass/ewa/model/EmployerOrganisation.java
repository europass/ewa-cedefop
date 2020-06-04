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

@JsonPropertyOrder({
    "name",
    "contactInfo",
    "sector"
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployerOrganisation extends Organisation {

    private CodeLabel sector;

    public EmployerOrganisation() {
    }

    public EmployerOrganisation(String name, OrganisationalContactInfo contactinfo, CodeLabel sector) {
        super(name, contactinfo);
        this.sector = sector;
    }

    @JsonProperty("Sector")
    @JacksonXmlProperty(localName = "Sector", namespace = Namespace.NAMESPACE)
    public CodeLabel getSector() {
        return sector;
    }

    public void setSector(CodeLabel sector) {
        this.sector = sector;
    }

    /**
     * *********************************************************************
     */
    @JsonIgnore
    public void translateTo(SkillsPassport esp, Locale locale) {
        sector = translate(esp, locale, "BusinessSector", sector);
    }

    @Override
    public void applyDefaultPreferences(List<PrintingPreference> newPrefs) {

        applyDefaultPreferences(getSector(), CodeLabel.class, "Sector", newPrefs);

        super.applyDefaultPreferences(newPrefs);

    }

    @JsonIgnore
    @Override
    public boolean checkEmpty() {
        return (super.checkEmpty()
                && (sector == null || (sector != null && sector.checkEmpty())));
    }
}
