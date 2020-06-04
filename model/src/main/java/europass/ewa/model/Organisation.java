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

@JsonPropertyOrder({"name", "contactInfo"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Organisation extends PrintableObject {

    private String name;

    private OrganisationalContactInfo contactInfo;

    public Organisation() {
    }

    public Organisation(String name, OrganisationalContactInfo contactinfo) {
        this.name = name;
        this.contactInfo = contactinfo;
    }

    @JsonProperty("Name")
    @JacksonXmlProperty(localName = "Name", namespace = Namespace.NAMESPACE)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("ContactInfo")
    @JacksonXmlProperty(localName = "ContactInfo", namespace = Namespace.NAMESPACE)
    public OrganisationalContactInfo getContactInfo() {
        return withPreferences(contactInfo, "ContactInfo");
    }

    public void setContactInfo(OrganisationalContactInfo contactinfo) {
        this.contactInfo = contactinfo;
    }

    /**
     * Decides on whether the organisational info contains both the name and at
     * least one part of the Address information.
     *
     * @return boolean
     */
    @JsonIgnore
    public boolean nameAndAddress() {
        if (Strings.isNullOrEmpty(name)) {
            return false;
        }
        Address address = this.getAddress();
        if (address == null) {
            return false;
        }
        String addressLine = address.getAddressLine();
        String postalCode = address.getPostalCode();
        String municipality = address.getMunicipality();
        CodeLabel country = address.getCountry();

        // if at least one is not null and not empty string the show
        if (!Strings.isNullOrEmpty(addressLine)
                || !Strings.isNullOrEmpty(postalCode)
                || !Strings.isNullOrEmpty(municipality)
                || (country != null && !country.checkEmpty())) {
            return true;
        }
        return false;
    }

    /**
     * Decides whether the organisational info contains both the name and at
     * least one of City or Country of the Address information, while the Street
     * and PostalCode are empty.
     *
     * @return boolean
     */
    @JsonIgnore
    public boolean nameAndShortAddress() {
        if (Strings.isNullOrEmpty(name)) {
            return false;
        }
        Address address = this.getAddress();
        if (address == null) {
            return false;
        }
        String municipality = address.getMunicipality();
        CodeLabel country = address.getCountry();
        String street = address.getAddressLine();
        String postalCode = address.getPostalCode();
        // if basic are full and address and postal missing we have shortaddress
        if ((!Strings.isNullOrEmpty(municipality) || (country != null && !country.checkEmpty()))
                && (Strings.isNullOrEmpty(street) && Strings.isNullOrEmpty(postalCode))) {
            return true;
        }
        return false;
    }

    /**
     * Decides whether the Organisational info contains only Country AND NOT 1.
     * Municipality 2. Street 3. PostalCode
     *
     * @return boolean
     */
    @JsonIgnore
    public boolean hasOnlyCountry() {
        if (Strings.isNullOrEmpty(name)) {
            return false;
        }
        Address address = this.getAddress();
        if (address == null) {
            return false;
        }
        String municipality = address.getMunicipality();
        CodeLabel country = address.getCountry();
        String street = address.getAddressLine();
        String postalCode = address.getPostalCode();
        // if only the Country is not empty and the rest of the address options are missing then it hasOnlyCountry
        if (Strings.isNullOrEmpty(municipality) && (Strings.isNullOrEmpty(street) && Strings.isNullOrEmpty(postalCode))
                && (country != null && !country.checkEmpty())) {
            return true;
        }
        return false;
    }

    @JsonIgnore
    private Address getAddress() {
        if (contactInfo == null) {
            return null;
        }
        return contactInfo.getAddressContact();
    }

    /**
     * *******************************************************************************
     */
    @Override
    public void applyDefaultPreferences(List<PrintingPreference> newPrefs) {

        applyDefaultPreferences(getContactInfo(), OrganisationalContactInfo.class, "ContactInfo", newPrefs);

        super.applyDefaultPreferences(newPrefs);

    }

    @JsonIgnore
    @Override
    public boolean checkEmpty() {
        return (Strings.isNullOrEmpty(name)
                && (contactInfo == null || (contactInfo != null && contactInfo.checkEmpty())));
    }
}
