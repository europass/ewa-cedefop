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
package europass.ewa.services.social.linkedin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.inject.Singleton;

import europass.ewa.model.CodeLabel;
import europass.ewa.model.EmployerOrganisation;
import europass.ewa.model.WorkExperience;
import europass.ewa.services.social.InstanceClassMismatchException;
import europass.ewa.services.social.Transformer;

@Singleton
public class VolunteerHandler implements Transformer {

    private static final String EXTRA_DATA_VOLUNTEER_KEY = "volunteer";

    @SuppressWarnings("unchecked")
    @Override
    public Object transform(Object from, Object to, Object... params)
            throws InstanceClassMismatchException {

        if (!(from instanceof HashMap)) {
            throw new InstanceClassMismatchException();
        }
        if (!(to instanceof List)) {
            throw new InstanceClassMismatchException();
        }

        try {

            ArrayList<LinkedHashMap<String, ?>> volunteerValues = LinkedInUtilities.extraDataFieldValues(from, EXTRA_DATA_VOLUNTEER_KEY);

            List<WorkExperience> workExperienceList = (List<WorkExperience>) to;

            for (LinkedHashMap<String, ?> valuesMap : volunteerValues) {

                LinkedHashMap<String, ?> organizationMap = (LinkedHashMap<String, ?>) valuesMap.get("organization");

                WorkExperience exp = new WorkExperience();
                exp.setPosition(new CodeLabel(null, (String) valuesMap.get("role")));

                String organizationName = (String) organizationMap.get("name");
                if (organizationName != null) {
                    exp.setEmployer(new EmployerOrganisation(organizationName, null, null));
                }

                workExperienceList.add(exp);
            }

            return workExperienceList;

        } catch (final Exception e) {
            return to;
        }
    }

    protected static class Volunteer {

        protected int id;
        protected String organization;
        protected String role;

        public Volunteer(int id, String organization, String role) {
            this.id = id;
            this.organization = organization;
            this.role = role;
        }
    }
}
