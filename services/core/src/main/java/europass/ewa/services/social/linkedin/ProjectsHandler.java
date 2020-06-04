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
import javax.inject.Singleton;

import com.github.mustachejava.Mustache;

import europass.ewa.model.Achievement;
import europass.ewa.model.CodeLabel;
import europass.ewa.model.JDate;
import europass.ewa.services.social.InstanceClassMismatchException;
import europass.ewa.services.social.ListAsTextHandler;
import europass.ewa.services.social.Transformer;

@Singleton
public class ProjectsHandler extends ListAsTextHandler<Object, Object> implements Transformer {

    private final TaxonomyTranslatorHandler handler;
    private static final String EXTRA_DATA_PROJECTS_KEY = "projects";

    @Inject
    public ProjectsHandler(@Named(LinkedInModule.PROJECTS_TEMPLATE) Mustache template, TaxonomyTranslatorHandler handler) {
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

            ArrayList<LinkedHashMap<String, ?>> projectsValues = LinkedInUtilities.extraDataFieldValues(from, EXTRA_DATA_PROJECTS_KEY);

            List<Project> projectsList = new LinkedList<Project>();
            List<DateConfig> dateConfigList = new ArrayList<DateConfig>();

            for (LinkedHashMap<String, ?> valuesMap : projectsValues) {

                JDate startDate = null;
                JDate endDate = null;

                DateConfig dateConfig = new DateConfig();

                if (valuesMap.get("startDate") != null) {

                    startDate = new JDate();
                    startDate.setDay(((LinkedHashMap<String, Integer>) valuesMap.get("startDate")).get("day"));
                    startDate.setMonth(((LinkedHashMap<String, Integer>) valuesMap.get("startDate")).get("month"));
                    startDate.setYear(((LinkedHashMap<String, Integer>) valuesMap.get("startDate")).get("year"));

                    dateConfig.setStartDate(startDate);

                    if (valuesMap.get("endDate") != null) {

                        endDate = new JDate();
                        endDate.setDay(((LinkedHashMap<String, Integer>) valuesMap.get("endDate")).get("day"));
                        endDate.setMonth(((LinkedHashMap<String, Integer>) valuesMap.get("endDate")).get("month"));
                        endDate.setYear(((LinkedHashMap<String, Integer>) valuesMap.get("endDate")).get("year"));

                        dateConfig.setEndDate(endDate);
                    }
                }
                dateConfigList.add(dateConfig);

                projectsList.add(
                        new Project(
                                (Integer) valuesMap.get("id"),
                                (String) valuesMap.get("name"),
                                (String) valuesMap.get("description"),
                                (String) valuesMap.get("url")
                        )
                );
            }

            //Add the List of DateConfig (each one for each List item) in the new params to be used by the HTML transformer
            Object[] newParams = new Object[params.length + 1];
            System.arraycopy(params, 0, newParams, 0, params.length);
            newParams[params.length] = dateConfigList;

            List<Achievement> achievementList = (List<Achievement>) to;
            Achievement item = new Achievement();

            //Step 1. Set the Achievement Title
            //the following changes the title label...
            String title = (String) handler.transform(from, "", params);

            //the following sets the title code...
            String code;
            switch (params.length) {
                case 1:
                case 0:
                    code = null;
                    break;
                default:
                    code = (String) params[1];
                    break;
            }

            item.setTitle(new CodeLabel(code, title));

            //Step 2. Set the Achievement item description
            String html = (String) super.transform(projectsList, this.template, newParams);

            item.setDescription(html);

            //Step 3. Put the Achievement item in to List
            achievementList.add(item);

            return achievementList;

        } catch (final Exception e) {
            return to;
        }
    }

    protected static class Project {

        protected int id;
        protected String name;
        protected String description;
        protected String url;
//		protected DateConfig dateConfig;

        public Project(int id, String name, String description, String url/*, DateConfig dateConfig*/) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.url = url;
//			this.dateConfig = dateConfig;
        }
    }
}
