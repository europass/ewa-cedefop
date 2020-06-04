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
package europass.ewa.model.format;

import org.apache.commons.text.StringEscapeUtils;

import europass.ewa.model.Address;
import europass.ewa.model.CodeLabel;

abstract class AddressField extends Field<Address> {

    static final class Text extends AddressField {

        private final String text;

        Text(String text) {
            this.text = text;
        }

        @Override
        boolean format(StringBuilder into, boolean previous, Address address) {
            return previous && format(into, address);
        }

        @Override
        boolean format(StringBuilder sb, Address address) {
            sb.append(StringEscapeUtils.escapeXml10(text));
            return true;
        }

    }

    static final class Street extends AddressField {

        @Override
        boolean format(StringBuilder sb, Address address) {
            return appendStringNotEmpty(sb, StringEscapeUtils.escapeXml10(address.getAddressLine()));
        }

    }

    static final class PostalCode extends AddressField {

        @Override
        boolean format(StringBuilder sb, Address address) {
            return appendStringNotEmpty(sb, StringEscapeUtils.escapeXml10(address.getPostalCode()));
        }

    }

    static final class Municipality extends AddressField {

        @Override
        boolean format(StringBuilder sb, Address address) {
            return appendStringNotEmpty(sb, StringEscapeUtils.escapeXml10(address.getMunicipality()));
        }
    }

    static final class Country extends AddressField {

        @Override
        boolean format(StringBuilder sb, Address address) {
            CodeLabel country = address.getCountry();
            if (country != null && country.getLabel() != null) {
                sb.append(StringEscapeUtils.escapeXml10(country.getLabel()));
                return true;
            }
            return false;
        }

    }

    static final class NewLine extends AddressField {

        @Override
        boolean format(StringBuilder into, boolean previous, Address address) {
            return previous && format(into, address);
        }

        @Override
        boolean format(StringBuilder sb, Address address) {
            sb.append("<text:line-break />");
            return true;
        }

    }

}
