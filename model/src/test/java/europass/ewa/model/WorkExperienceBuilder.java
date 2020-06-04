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

public class WorkExperienceBuilder {

    private final Builder b;

    private WorkExperienceBuilder(Builder b) {
        this.b = b;
    }

    public WorkExperience get() {
        WorkExperience e = new WorkExperience();
        if (b.period != null) {
            e.setPeriod(b.period);
        }
        if (b.description != null) {
            e.setDescription(b.description);
        }
        if (b.attachments != null) {
            e.setReferenceToList(b.attachments);
        }
        if (b.position != null) {
            e.setPosition(b.position);
        }
        if (b.activities != null) {
            e.setActivities(b.activities);
        }
        if (b.employer != null) {
            e.setEmployer(b.employer);
        }
        return e;
    }

    public static class Builder extends ExperienceBuilder {

        private CodeLabel position;

        private String activities;

        private EmployerOrganisation employer;

        public Builder level(String code, String label) {
            this.position = new CodeLabel(code, label);
            return this;
        }

        public Builder activities(String activities) {
            this.activities = activities;
            return this;
        }

        public Builder employer(EmployerOrganisation employer) {
            this.employer = employer;
            return this;
        }

    }
}
