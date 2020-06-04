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

public class IdentificationBuilder {

    private final Builder b;

    private IdentificationBuilder(Builder b) {
        this.b = b;
    }

    public Identification get() {
        Identification e = new Identification();

        if (b.personName != null) {
            e.setPersonName(b.personName);
        }
        if (b.contactInfo != null) {
            e.setContactInfo(b.contactInfo);
        }
        if (b.demographics != null) {
            e.setDemographics(b.demographics);
        }
        return e;
    }

    public static class Builder {

        private PersonName personName;

        private ContactInfo contactInfo;

        private Demographics demographics;

        public Builder personName(PersonName personName) {
            this.personName = personName;
            return this;
        }

        public Builder contactInfo(ContactInfo contactInfo) {
            this.contactInfo = contactInfo;
            return this;
        }

        public Builder demographics(Demographics demographics) {
            this.demographics = demographics;
            return this;
        }

        public IdentificationBuilder build() {
            return new IdentificationBuilder(this);
        }
    }

}
