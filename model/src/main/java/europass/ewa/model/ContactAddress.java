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

@JsonPropertyOrder({
    "use",
    "contact"
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContactAddress extends PrintableObject {

    private CodeLabel use;

    private Address contact;

    public ContactAddress() {
    }

    public ContactAddress(Address contact) {
        this.contact = contact;
    }

    @JsonProperty("Use")
    @JacksonXmlProperty(localName = "Use", namespace = Namespace.NAMESPACE)
    public CodeLabel getUse() {
        return use;
    }

    public void setUse(CodeLabel use) {
        this.use = use;
    }

    @JsonProperty("Contact")
    @JacksonXmlProperty(localName = "Contact", namespace = Namespace.NAMESPACE)
    public Address getContact() {
        return withDocument(contact, getDocument());
    }

    public void setContact(Address contact) {
        this.contact = contact;
    }

    /**
     * *********************************************************************
     */
    @JsonIgnore
    @Override
    public void applyDefaultPreferences(List<PrintingPreference> newPrefs) {

        super.applyDefaultPreferences(newPrefs);

    }

    @JsonIgnore
    @Override
    public boolean checkEmpty() {
        return ((use == null || (use != null && use.checkEmpty()))
                && (contact == null || (contact != null && contact.checkEmpty())));
    }

}
