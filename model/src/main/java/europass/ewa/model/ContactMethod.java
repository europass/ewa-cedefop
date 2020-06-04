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

@JsonPropertyOrder({
    "contact",
    "use"
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContactMethod extends PrintableObject {

    private CodeLabel use;

    private String contact;

    public ContactMethod() {
    }

    public ContactMethod(CodeLabel use, String contact) {
        this.use = use;
        this.contact = contact;
    }

    public ContactMethod(String contact) {
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
    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    /**
     * ********************************************************************
     */
    @JsonIgnore
    @Override
    public void translateTo(SkillsPassport esp, Locale locale, String taxonomyName) {
        use = translate(esp, locale, taxonomyName, use);
    }

    /**
     * **************************************************************
     */
    public PrintableValue<String> contact() {
        return withPreferences(contact, "Contact");
    }

    @Override
    public void applyDefaultPreferences(List<PrintingPreference> newPrefs) {
        super.applyDefaultPreferences(newPrefs);
    }

    @JsonIgnore
    @Override
    public boolean checkEmpty() {
        return (use == null || (use != null && use.checkEmpty()))
                && Strings.isNullOrEmpty(contact);
    }
}
