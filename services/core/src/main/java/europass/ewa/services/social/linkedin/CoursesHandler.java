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
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import com.github.mustachejava.Mustache;

import europass.ewa.model.Achievement;
import europass.ewa.model.CodeLabel;
import europass.ewa.services.social.InstanceClassMismatchException;
import europass.ewa.services.social.ListAsTextHandler;
import europass.ewa.services.social.Transformer;

public class CoursesHandler extends ListAsTextHandler<Object, Object> implements Transformer {

    private final TaxonomyTranslatorHandler handler;
    private static final String EXTRA_DATA_COURSES_KEY = "courses";

    @Inject
    public CoursesHandler(@Named(LinkedInModule.COURSES_MUSTACHE_TEMPLATE) Mustache template, TaxonomyTranslatorHandler handler) {
        this.template = template;
        this.handler = handler;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object transform(Object from, Object to, Object... params)
            throws InstanceClassMismatchException {

        if (params == null || (params != null && params.length == 0)) {
            return to;
        }

        if (!(from instanceof HashMap)) {
            throw new InstanceClassMismatchException();
        }
        if (!(to instanceof List<?>)) {
            throw new InstanceClassMismatchException();
        }

        try {

            ArrayList<LinkedHashMap<String, ?>> coursesValues = LinkedInUtilities.extraDataFieldValues(from, EXTRA_DATA_COURSES_KEY);

            List<Course> coursesList = new LinkedList<Course>();

            for (LinkedHashMap<String, ?> valuesMap : coursesValues) {

                coursesList.add(
                        new Course(
                                (Integer) valuesMap.get("id"),
                                (String) valuesMap.get("name"),
                                (String) valuesMap.get("number")
                        )
                );
            }

            List<Achievement> achievementList = (List<Achievement>) to;

            Achievement item = new Achievement();
            //Step 1. Set the Achievement Title

            //the following changes the title...
            String title = (String) handler.transform(from, "", params);
            item.setTitle(new CodeLabel("courses", title));

            //Step 2. Set the Achievement item description
            String html = (String) super.transform(coursesList, this.template, params);

            item.setDescription(html);

            //Step 3. Put the Achievement item in to List
            achievementList.add(item);

            return achievementList;
        } catch (final Exception e) {
            return to;
        }
    }

    protected static class Course {

        protected int id;
        protected String name;
        protected String number;

        public Course(int id, String name, String number) {
            this.id = id;
            this.name = name;
            this.number = number;
        }
    }
}
