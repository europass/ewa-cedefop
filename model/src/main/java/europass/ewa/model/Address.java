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

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.common.base.Strings;

import europass.ewa.model.format.AddressFormat;
import europass.ewa.resources.JsonResourceBundle;

@JsonPropertyOrder({
    "addressLine",
    "addressLine2",
    "postalCode",
    "municipality",
    "country"
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Address extends Place {

    public static final String DEFAULT_ADDRESS_FORMAT = "s, z m (c)";

    private String addressLine;

    private String addressLine2;

    private String postalCode;

    public Address() {
    }

    public Address(String addressLine, String addressLine2, String postalCode, String municipality, CodeLabel country) {
        super(municipality, country);
        this.addressLine = addressLine;
        this.addressLine2 = addressLine2;
        this.postalCode = postalCode;
    }

    public Address(String addressLine, String postalCode, String municipality, CodeLabel country) {
        super(municipality, country);
        this.addressLine = addressLine;
        this.postalCode = postalCode;
    }

    public Address(String municipality, CodeLabel country) {
        super(municipality, country);
    }

    public Address(CodeLabel country) {
        super(country);
    }

    @JsonProperty("AddressLine")
    @JacksonXmlProperty(localName = "AddressLine", namespace = Namespace.NAMESPACE)
    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    @JsonProperty("AddressLine2")
    @JacksonXmlProperty(localName = "AddressLine2", namespace = Namespace.NAMESPACE)
    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    @JsonProperty("PostalCode")
    @JacksonXmlProperty(localName = "PostalCode", namespace = Namespace.NAMESPACE)
    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * Used to format the address of the educational provider, where the street
     * and postal code are not anymore supported. Will create a new temporary
     * object out of only the municipality and country and format this one
     * instead of the original, which may contain street and postal code if it
     * resulted from a previous XML.
     *
     * @return
     */
    @JsonIgnore
    public String basicAddress() {
        String pattern = getPattern();
        String postalCountryCode = super.getPostalCountryCode(pattern);

        Address temp = new Address();
        temp.setMunicipality(this.getMunicipality());
        temp.setCountry(this.getCountry());

        AddressFormat format = AddressFormat.compile(pattern, temp, postalCountryCode);
        String f = format.format(temp);

        //Escape for XML is handled during formating each Address field.
        return f;
    }

    /**
     * Related to EWA-900 Be able to format only municipality and postal code
     * according to the respective address format
     *
     * @return
     */
    @JsonIgnore
    public String cityPostalCodeOnly() {
        String pattern = getPattern();
        String postalCountryCode = super.getPostalCountryCode(pattern);

        Address temp = new Address();
        temp.setMunicipality(this.getMunicipality());
        temp.setPostalCode(this.getPostalCode());

        AddressFormat format = AddressFormat.compile(pattern, temp, postalCountryCode);
        String f = format.format(temp);

        //eliminate  commas
        return f.replaceAll(",", "");

        //Escape for XML is handled during formating each Address field.
    }

    private String getPattern() {
        PrintingPreference pref = pref();
        if (pref == null || (pref != null && Strings.isNullOrEmpty(pref.getFormat()))) {

            CodeLabel countryCodeLbl = this.getCountry();

            if (countryCodeLbl != null) {

                if (!countryCodeLbl.checkEmpty()) {

                    String formatStr = getAddressFormatFromResource(countryCodeLbl.getCode());
                    if (formatStr != null) {
                        return formatStr;
                    }
                }

                return DEFAULT_ADDRESS_FORMAT;
            }

            return DEFAULT_ADDRESS_FORMAT;
        }
        String pattern = pref.getFormat();
        if (pattern == null) {
            return DEFAULT_ADDRESS_FORMAT;
        }
        return pattern;
    }

    @JsonIgnore
    public boolean detailedAddress() {
        return ((addressLine != null && !addressLine.isEmpty())
                || (postalCode != null && !postalCode.isEmpty()));
    }

    @JsonIgnore
    @Override
    public void applyDefaultPreferences(List<PrintingPreference> newPrefs) {
        super.applyDefaultPreferences(newPrefs);
    }

    @JsonIgnore
    @Override
    public boolean checkEmpty() {
        return (super.checkEmpty()
                && Strings.isNullOrEmpty(addressLine)
                && Strings.isNullOrEmpty(postalCode));
    }

    /**
     * Prints the address in ODT
     *
     * @return
     */
    @JsonIgnore
    public String odt() {
        String pattern = getPattern();
        String postalCountryCode = getPostalCountryCode(pattern);

        AddressFormat format = AddressFormat.compile(pattern, this, postalCountryCode);
        String f = format.format(this);

        //Escape for XML is handled during formating each Address field.
        return f;
    }

    /**
     * Get format using country from the resource AddressFormat
     *
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    @JsonIgnore
    public String getAddressFormatFromResource(String countryCode) {

        ResourceBundle bundle = ResourceBundle.getBundle("bundles/AddressFormat",
                new JsonResourceBundle.Control(new ObjectMapper()));
        try {
            return bundle.getString(countryCode);
        } catch (final Exception e) {
            //case of NullPointer, MissingResource, ClassCast Exceptions
            return null;
        }

    }

}
