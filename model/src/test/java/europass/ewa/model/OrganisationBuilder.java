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

public class OrganisationBuilder {

    protected final Builder b;

    protected OrganisationBuilder(Builder b) {
        this.b = b;
    }

    public Organisation get() {
        Organisation o = new Organisation();

        boolean hasWebsite = b.website != null;
        boolean hasAddress = b.address != null;

        if (hasWebsite || hasAddress) {

            OrganisationalContactInfo contact = new OrganisationalContactInfo();
            if (hasWebsite) {
                contact.setWebsite(new ContactMethod(b.website));
            }
            if (hasAddress) {
                contact.setAddress(new ContactAddress(b.address));
            }
            o.setContactInfo(contact);
        }
        o.setName(b.name);

        return o;
    }

    public static class Builder {

        private String name;

        private Address address;

        private String website;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder website(String website) {
            this.website = website;
            return this;
        }

        public Builder address(Address address) {
            this.address = address;
            return this;
        }

        public OrganisationBuilder build() {
            return new OrganisationBuilder(this);
        }
    }
}
