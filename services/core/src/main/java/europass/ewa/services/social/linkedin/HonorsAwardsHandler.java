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
import europass.ewa.services.social.InstanceClassMismatchException;
import europass.ewa.services.social.ListAsTextHandler;
import europass.ewa.services.social.Transformer;

@Singleton
public class HonorsAwardsHandler extends ListAsTextHandler<List<?>, Achievement> implements Transformer {

    private final TaxonomyTranslatorHandler handler;

    private static final String EXTRA_HONORS_AWARDS_URLS_KEY = "honorsAwards";

    @Inject
    public HonorsAwardsHandler(@Named(LinkedInModule.HONORS_AWARDS_TEMPLATE) Mustache template, TaxonomyTranslatorHandler handler) {
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

            ArrayList<LinkedHashMap<String, ?>> honorAwardValues = LinkedInUtilities.extraDataFieldValues(from, EXTRA_HONORS_AWARDS_URLS_KEY);

            List<HonorsAwards> honorsAwardsList = new LinkedList<HonorsAwards>();

            for (LinkedHashMap<String, ?> honorAwardMap : honorAwardValues) {

                honorsAwardsList.add(
                        new HonorsAwards(
                                (Integer) honorAwardMap.get("id"),
                                (String) honorAwardMap.get("name"),
                                (String) honorAwardMap.get("issuer")
                        )
                );
            }

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
            String html = (String) super.transform(honorsAwardsList, this.template, params);

            item.setDescription(html);

            //Step 3. Put the Achievement item in to List
            achievementList.add(item);

            return achievementList;

        } catch (final Exception e) {
            return to;
        }
    }

    protected static class HonorsAwards {

        protected int id;
        protected String name;
        protected String issuer;

        public HonorsAwards(int id, String name, String issuer) {
            this.id = id;
            this.name = name;
            this.issuer = issuer;
        }

    }
}
