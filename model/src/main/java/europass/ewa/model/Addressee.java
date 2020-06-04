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
import java.util.ResourceBundle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.common.base.Strings;

import europass.ewa.enums.ODTElements;
import europass.ewa.resources.JsonResourceBundle;

@JsonPropertyOrder({"personName", "position", "organisation"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Addressee extends PrintableObject {

    private PersonName personName;

    private CodeLabel position;

    private Organisation organisation;

    private final static String LINE_SEPERATOR = System.getProperty("line.separator");

    @JsonProperty("PersonName")
    @JacksonXmlProperty(localName = "PersonName", namespace = Namespace.NAMESPACE)
    public PersonName getPersonName() {
        return withPreferences(personName, "PersonName");
    }

    public void setPersonName(PersonName personName) {
        this.personName = personName;
    }

    @JsonIgnore
    public String nameTxt() {
        if (personName == null) {
            return "";
        }
        return personName.fullName();
    }

    @JsonProperty("Position")
    @JacksonXmlProperty(localName = "Position", namespace = Namespace.NAMESPACE)
    public CodeLabel getPosition() {
        return position;
    }

    public void setPosition(CodeLabel position) {
        this.position = position;
    }

    @JsonProperty("Organisation")
    @JacksonXmlProperty(localName = "Organisation", namespace = Namespace.NAMESPACE)
    public Organisation getOrganisation() {
        return withPreferences(organisation, "Organisation");
    }

    public void setOrganisation(Organisation organisation) {
        this.organisation = organisation;
    }

    @JsonIgnore
    public String organisationTxt() {

        if (organisation == null || (organisation != null && organisation.checkEmpty())) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        // flag to hold the information about when an ODT text:line-break will be added
        boolean addNewLine = false;

        // check whether addressee name or position exists so to flag that
        // a new ODT text:line-break will be added
        boolean hasName = (personName != null && !personName.checkEmpty());
        boolean hasPosition = (position != null && !position.checkEmpty());

        if (hasName || hasPosition) {
            addNewLine = true;
        }

        //Organisation name
        String name = escapeForXml(organisation.getName());
        if (!Strings.isNullOrEmpty(name)) {
            sb.append(addNewLine ? LINE_SEPERATOR : "");
            sb.append(ODTElements.SPAN_START + (addNewLine ? ODTElements.LINE_BREAK : "") + name + ODTElements.SPAN_END);
            addNewLine = true;
        }

        //Organisation contact info
        OrganisationalContactInfo contactInfo = organisation.getContactInfo();
        if (contactInfo == null) {
            return sb.toString();
        }

        ContactAddress contactAddress = contactInfo.getAddress();
        if (contactAddress == null) {
            return sb.toString();
        }

        Address address = contactAddress.getContact();
        if (address == null) {
            return sb.toString();
        }

        String addressLine = escapeForXml(address.getAddressLine());
        if (!Strings.isNullOrEmpty(addressLine)) {
            //Although it is strange in order to have a proper alignment
            //each odt <text:span> should start in a new line rather than to have them all in one line. 
            sb.append(addNewLine ? LINE_SEPERATOR : "");
            sb.append(ODTElements.SPAN_START + (addNewLine ? ODTElements.LINE_BREAK : "") + addressLine + ODTElements.SPAN_END);
            addNewLine = true;
        }

        String addressLine2 = escapeForXml(address.getAddressLine2());
        if (!Strings.isNullOrEmpty(addressLine2)) {
            sb.append(addNewLine ? LINE_SEPERATOR : "");
            sb.append(ODTElements.SPAN_START + (addNewLine ? ODTElements.LINE_BREAK : "") + addressLine2 + ODTElements.SPAN_END);
            addNewLine = true;
        }

        //municipality and postal code formatted according to address format
        if (!Strings.isNullOrEmpty(address.getMunicipality())
                || !Strings.isNullOrEmpty(address.getPostalCode())) {
            sb.append(addNewLine ? LINE_SEPERATOR : "");
            sb.append(ODTElements.SPAN_START + (addNewLine ? ODTElements.LINE_BREAK : "") + address.cityPostalCodeOnly() + ODTElements.SPAN_END);
            addNewLine = true;
        }

        //country at the end
        CodeLabel country = address.getCountry();
        if (country == null) {
            return sb.toString();
        }

        String countryLabel = escapeForXml(country.getLabel());
        if (!Strings.isNullOrEmpty(countryLabel)) {
            sb.append(addNewLine ? LINE_SEPERATOR : "");
            sb.append(ODTElements.SPAN_START + (addNewLine ? ODTElements.LINE_BREAK : "") + countryLabel + ODTElements.SPAN_END);
        }

        return sb.toString();

    }

    @JsonIgnore
    public String organisationName() {

        //Organisation name
        String name = escapeForXml(organisation.getName());

        return !Strings.isNullOrEmpty(name) ? name + " " : "";

    }

    @JsonIgnore
    public String organisationContactInfo() {

        if (organisation == null || (organisation != null && organisation.checkEmpty())) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        // flag to hold the information about when an ODT text:line-break will be added
        boolean addNewLine = false;

        // check whether addressee position exists so to flag that
        // a new ODT text:line-break will be added
        boolean hasPosition = (position != null && !position.checkEmpty()),
                hasName = (personName != null && !personName.checkEmpty());

        if (hasPosition || hasName) {
            addNewLine = true;
        }

        //Organisation contact info
        OrganisationalContactInfo contactInfo = organisation.getContactInfo();
        if (contactInfo == null) {
            return sb.toString();
        }

        ContactAddress contactAddress = contactInfo.getAddress();
        if (contactAddress == null) {
            return sb.toString();
        }

        Address address = contactAddress.getContact();
        if (address == null) {
            return sb.toString();
        }

        String addressLine = escapeForXml(address.getAddressLine());
        if (!Strings.isNullOrEmpty(addressLine)) {
            //Although it is strange in order to have a proper alignment
            //each odt <text:span> should start in a new line rather than to have them all in one line. 
            sb.append(addNewLine ? LINE_SEPERATOR : "");
            sb.append(ODTElements.SPAN_START + (addNewLine ? ODTElements.LINE_BREAK : "") + addressLine + ODTElements.SPAN_END);
            addNewLine = true;
        }

        String addressLine2 = escapeForXml(address.getAddressLine2());
        if (!Strings.isNullOrEmpty(addressLine2)) {
            sb.append(addNewLine ? LINE_SEPERATOR : "");
            sb.append(ODTElements.SPAN_START + (addNewLine ? ODTElements.LINE_BREAK : "") + addressLine2 + ODTElements.SPAN_END);
            addNewLine = true;
        }

        //municipality and postal code formatted according to address format
        if (!Strings.isNullOrEmpty(address.getMunicipality())
                || !Strings.isNullOrEmpty(address.getPostalCode())) {
            sb.append(addNewLine ? LINE_SEPERATOR : "");
            sb.append(ODTElements.SPAN_START + (addNewLine ? ODTElements.LINE_BREAK : "") + address.cityPostalCodeOnly() + ODTElements.SPAN_END);
            addNewLine = true;
        }

        //country at the end
        CodeLabel country = address.getCountry();
        if (country == null) {
            return sb.toString();
        }

        String countryLabel = escapeForXml(country.getLabel());
        if (!Strings.isNullOrEmpty(countryLabel)) {
            sb.append(addNewLine ? LINE_SEPERATOR : "");
            sb.append(ODTElements.SPAN_START + (addNewLine ? ODTElements.LINE_BREAK : "") + countryLabel + ODTElements.SPAN_END);
        }

        return sb.toString();
    }

    @Override
    public void applyDefaultPreferences(List<PrintingPreference> newPrefs) {
        applyDefaultPreferences(getPersonName(), PersonName.class,
                "PersonName", newPrefs);

        //no pref for position
        applyDefaultPreferences(getOrganisation(), Organisation.class, "Organisation", newPrefs);

        super.applyDefaultPreferences(newPrefs);
    }

    @JsonIgnore
    @Override
    public boolean checkEmpty() {
        return ((personName == null || (personName != null && personName.checkEmpty()))
                && (position == null || (position != null && position.checkEmpty()))
                && (organisation == null || (organisation != null && organisation.checkEmpty())));
    }

    @JsonIgnore
    public boolean isPersonNameFirst() {

        String order = "PersonName Organisation",
                orderKey = "CoverLetter.Addressee.order";

        try {		//get the localisation Delimiter from the Document Customizations bundle
            Locale locale = locale();

            ResourceBundle bundle = ResourceBundle.getBundle("preferences/CLExtraPreferences", locale, new JsonResourceBundle.Control(new ObjectMapper()));
            order = bundle.getString((orderKey));
        } catch (final Exception e) {
            throw e;
        }
        if ("PersonName Organisation".indexOf(order) >= 0) {
            return true;
        } else {
            return false;
        }

    }
}
