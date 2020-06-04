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
public class PatentsHandler extends ListAsTextHandler<Object, Object> implements Transformer {

    private final TaxonomyTranslatorHandler handler;
    private static final String EXTRA_DATA_PATENTS_KEY = "patents";

    @Inject
    public PatentsHandler(@Named(LinkedInModule.PATENTS_MUSTACHE_TEMPLATE) Mustache template, TaxonomyTranslatorHandler handler) {
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

            ArrayList<LinkedHashMap<String, ?>> patentsValues = LinkedInUtilities.extraDataFieldValues(from, EXTRA_DATA_PATENTS_KEY);

            List<Patent> patentsList = new LinkedList<Patent>();

            List<DateConfig> dateConfigList = new ArrayList<DateConfig>();
            for (LinkedHashMap<String, ?> valuesMap : patentsValues) {

                DateConfig dateConfig = new DateConfig();
                LinkedHashMap<String, Integer> dateMap = (LinkedHashMap<String, Integer>) valuesMap.get("date");
                if (dateMap != null) {

                    JDate issueDate = new JDate();
                    issueDate.setDay(dateMap.get("day"));
                    issueDate.setMonth(dateMap.get("month"));
                    issueDate.setYear(dateMap.get("year"));

                    dateConfig.setStartDate(issueDate);
                }
                dateConfigList.add(dateConfig);

                LinkedHashMap<String, ?> officeMap = (LinkedHashMap<String, ?>) valuesMap.get("office");
                String officeName = officeMap != null ? (String) officeMap.get("name") : null;

                LinkedHashMap<String, ?> statusMap = (LinkedHashMap<String, ?>) valuesMap.get("status");
                String statusName = statusMap != null ? (String) statusMap.get("name") : null;

                patentsList.add(
                        new Patent(
                                (Integer) valuesMap.get("id"),
                                (String) valuesMap.get("number"),
                                officeName,
                                statusName,
                                (String) valuesMap.get("title"),
                                (String) valuesMap.get("url"),
                                (String) valuesMap.get("summary")
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
            //the following changes the title...
            String title = (String) handler.transform(from, "", params);

            //Step 2. Set the Achievement item description
            String html = (String) super.transform(patentsList, this.template, newParams);

            item.setTitle(new CodeLabel(null, title));

            item.setDescription(html);

            //Step 3. Put the Achievement item in to List
            achievementList.add(item);

            return achievementList;
        } catch (final Exception e) {
            return to;
        }
    }

    protected static class Patent {

        protected int id;
        protected String number;
        protected String office;
        protected String status;
        protected String title;
        protected String url;
        protected String summary;

        public Patent(int id, String number, String office, String status, String title, String url, String summary) {
            this.id = id;
            this.number = number;
            this.office = office;
            this.status = status;
            this.title = title;
            this.url = url;
            this.summary = summary;
        }
    }
}
