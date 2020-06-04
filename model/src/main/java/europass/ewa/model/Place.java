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
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.common.base.Strings;

import europass.ewa.resources.JsonResourceBundle;

@JsonPropertyOrder({
    "municipality",
    "country"
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Place extends PrintableObject {

    private String municipality;

    private CodeLabel country;

    public Place() {
    }

    public Place(String municipality, CodeLabel country) {
        this.municipality = municipality;
        this.country = country;
    }

    public Place(CodeLabel country) {
        this.country = country;
    }

    @JsonProperty("Municipality")
    @JacksonXmlProperty(localName = "Municipality", namespace = Namespace.NAMESPACE)
    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    @JsonProperty("Country")
    @JacksonXmlProperty(localName = "Country", namespace = Namespace.NAMESPACE)
    public CodeLabel getCountry() {
        return country;
    }

    public void setCountry(CodeLabel country) {
        this.country = country;
    }

    /**
     * *********************************************************************
     */
    @JsonIgnore
    public void translateTo(SkillsPassport esp, Locale locale) {
        country = translate(esp, locale, "Country", country);
    }

    protected String getPostalCountryCode(String pattern) {
        boolean patternHasPostal = pattern.contains("p");
        String postalCountryCode = "";
        if (country != null) {
            String code = country.getCode();
            ResourceBundle postalCountryCodes = ResourceBundle.getBundle("bundles/PostalCode", new JsonResourceBundle.Control());
            try {
                postalCountryCode = postalCountryCodes.getString(code);
            } catch (MissingResourceException mre) {
                postalCountryCode = (patternHasPostal) ? code : "";
            } catch (NullPointerException npe) {
                postalCountryCode = (patternHasPostal) ? code : "";
            }
        }
        return postalCountryCode;
    }

    @JsonIgnore
    @Override
    public void applyDefaultPreferences(List<PrintingPreference> newPrefs) {
        super.applyDefaultPreferences(newPrefs);
    }

    @JsonIgnore
    @Override
    public boolean checkEmpty() {
        return (Strings.isNullOrEmpty(municipality)
                && (country == null || (country != null && country.checkEmpty())));
    }

}
