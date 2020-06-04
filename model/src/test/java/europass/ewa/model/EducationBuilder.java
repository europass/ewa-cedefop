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

public class EducationBuilder {

    private final Builder b;

    private EducationBuilder(Builder b) {
        this.b = b;
    }

    public Education get() {
        Education e = new Education();

        if (b.period != null) {
            e.setPeriod(b.period);
        }
        if (b.description != null) {
            e.setDescription(b.description);
        }
        if (b.attachments != null) {
            e.setReferenceToList(b.attachments);
        }
        if (b.title != null) {
            e.setTitle(b.title);
        }
        if (b.activities != null) {
            e.setActivities(b.activities);
        }
        if (b.field != null) {
            e.setField(b.field);
        }
        if (b.level != null) {
            e.setLevel(b.level);
        }
        if (b.organisation != null) {
            e.setOrganisation(b.organisation);
        }
        return e;
    }

    public static class Builder extends ExperienceBuilder {

        private String title;

        private String activities;

        private Organisation organisation;

        private CodeLabel level;

        private CodeLabel field;

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder activities(String activities) {
            this.activities = activities;
            return this;
        }

        public Builder organisation(Organisation organisation) {
            this.organisation = organisation;
            return this;
        }

        public Builder level(String code, String label) {
            this.level = new CodeLabel(code, label);
            return this;
        }

        public Builder field(String code, String label) {
            this.field = new CodeLabel(code, label);
            return this;
        }

        public EducationBuilder build() {
            return new EducationBuilder(this);
        }
    }

}
