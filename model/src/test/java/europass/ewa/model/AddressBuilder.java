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

public class AddressBuilder {

    private final Builder b;

    private AddressBuilder(Builder b) {
        this.b = b;
    }

    public Address get() {
        Address o = new Address();
        if (b.addressLine != null) {
            o.setAddressLine(b.addressLine);
        }
        if (b.postalCode != null) {
            o.setPostalCode(b.postalCode);
        }
        if (b.municipality != null) {
            o.setMunicipality(b.municipality);
        }
        if (b.country != null) {
            o.setCountry(b.country);
        }

        return o;
    }

    public static class Builder {

        private String addressLine;

        private String postalCode;

        private String municipality;

        private CodeLabel country;

        public Builder addressLine(String addressLine) {
            this.addressLine = addressLine;
            return this;
        }

        public Builder postalCode(String postalCode) {
            this.postalCode = postalCode;
            return this;
        }

        public Builder municipality(String municipality) {
            this.municipality = municipality;
            return this;
        }

        public Builder country(String code, String label) {
            this.country = new CodeLabel(code, label);
            return this;
        }

        public AddressBuilder build() {
            return new AddressBuilder(this);
        }
    }

}
