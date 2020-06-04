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
import java.util.List;

import javax.inject.Singleton;

import org.springframework.social.linkedin.api.Education;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

import europass.ewa.services.social.InstanceClassMismatchException;
import europass.ewa.services.social.Transformer;

@Singleton
public class EducationalFieldHandler implements Transformer {

    @SuppressWarnings("unchecked")
    @Override
    /**
     * Concatenated LinkedIn.degree and .fieldOfStudy to Europass title
     */
    public Object transform(Object from, Object to, Object... params)
            throws InstanceClassMismatchException {

        if (!(from instanceof Education)) {
            throw new InstanceClassMismatchException();
        }
        if (!(to instanceof String)) {
            throw new InstanceClassMismatchException();
        }

        Education socialEdu = (Education) from;

        List<String> parts = new ArrayList<>();
        String degree = socialEdu.getDegree();
        if (!Strings.isNullOrEmpty(degree)) {
            parts.add(degree);
        }
        String field = socialEdu.getFieldOfStudy();
        if (!Strings.isNullOrEmpty(field)) {
            parts.add(field);
        }

        return Joiner.on(", ").skipNulls().join(parts);
    }
}
