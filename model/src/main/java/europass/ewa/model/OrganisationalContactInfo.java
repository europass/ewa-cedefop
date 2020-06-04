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

import static europass.ewa.model.format.OdtDisplayableUtils.formatWebsiteLinks;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonPropertyOrder({
    "address",
    "website"
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrganisationalContactInfo extends PrintableObject {

    private ContactAddress address;

    private ContactMethod website;

    public OrganisationalContactInfo() {
    }

    @JsonProperty("Address")
    @JacksonXmlProperty(localName = "Address", namespace = Namespace.NAMESPACE)
    public ContactAddress getAddress() {
        return withPreferences(address, "Address");
    }

    public void setAddress(ContactAddress address) {
        this.address = address;
    }

    @JsonProperty("Website")
    @JacksonXmlProperty(localName = "Website", namespace = Namespace.NAMESPACE)
    public ContactMethod getWebsite() {
        return website;
    }

    public void setWebsite(ContactMethod website) {
        this.website = website;
    }

    /**
     * *******************************************************************************
     */
    @JsonIgnore
    public Address getAddressContact() {
        if (address == null) {
            return null;
        }
        return address.getContact();
    }

    @Override
    public void applyDefaultPreferences(List<PrintingPreference> newPrefs) {

        applyDefaultPreferences(getAddress(), ContactAddress.class, "Address", newPrefs);

        applyDefaultPreferences(getWebsite(), ContactMethod.class, "Website", newPrefs);

        super.applyDefaultPreferences(newPrefs);

    }

    @JsonIgnore
    @Override
    public boolean checkEmpty() {
        return (address == null || (address != null && address.checkEmpty()))
                && (website == null || (website != null && website.checkEmpty()));
    }

    @JsonIgnore
    public String formatWebsite() {
        return formatWebsiteLinks(website.getContact().trim());
    }
}
