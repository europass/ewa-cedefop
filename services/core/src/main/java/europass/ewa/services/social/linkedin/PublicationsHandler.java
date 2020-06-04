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
public class PublicationsHandler extends ListAsTextHandler<Object, Object> implements Transformer {

    private final TaxonomyTranslatorHandler handler;
    private static final String EXTRA_DATA_PUBLICATIONS_KEY = "publications";

    @Inject
    public PublicationsHandler(@Named(LinkedInModule.PUBLICATIONS_MUSTACHE_TEMPLATE) Mustache template, TaxonomyTranslatorHandler handler) {
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

            ArrayList<LinkedHashMap<String, ?>> publicationsValues = LinkedInUtilities.extraDataFieldValues(from, EXTRA_DATA_PUBLICATIONS_KEY);

            List<Publication> publicationsList = new LinkedList<Publication>();

            List<DateConfig> dateConfigList = new ArrayList<DateConfig>();
            for (LinkedHashMap<String, ?> valuesMap : publicationsValues) {

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

                LinkedHashMap<String, ?> publisherMap = (LinkedHashMap<String, ?>) valuesMap.get("publisher");
                String publisherName = publisherMap != null ? (String) publisherMap.get("name") : null;

                publicationsList.add(
                        new Publication(
                                (Integer) valuesMap.get("id"),
                                (String) valuesMap.get("title"),
                                publisherName,
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
            //the following changes the title...
            String title = (String) handler.transform(from, "", params);

            //Step 2. Set the Achievement item description
            String html = (String) super.transform(publicationsList, this.template, newParams);

            item.setTitle(new CodeLabel(null, title));

            item.setDescription(html);

            //Step 3. Put the Achievement item in to List
            achievementList.add(item);

            return achievementList;
        } catch (final Exception e) {
            return to;
        }

        /*
		try {
			List<Publication> publicationList = (List<Publication>)from;
			List<Achievement> achievementList = (List<Achievement>)to;
			
			Achievement item = new Achievement();
			//Step 1. Set the Achievement Title
			
			//the following changes the title...
			
			String title = (String)handler.transform(from, "", params);
			
			//the following sets the title code...
			String code;
			switch (params.length){
				case 1: case 0: code = null; break;
				default: code = (String) params[1]; break;
			}
			item.setTitle( new CodeLabel(code,title) );
			
			List<DateConfig> dateConfigList = new ArrayList<DateConfig>();
			for(Publication p : publicationList){
				
				LinkedInDate pDate = p.getDate();
				
				DateConfig dateConfig = new DateConfig();
				
				if ( pDate == null ){
					//see MustacheListAsText.transform check for null in start/ end dates
					dateConfig.setStartDate( null );
				} 
				else {
					JDate date = new JDate();
					date.setDay(pDate.getDay());
					date.setMonth(pDate.getMonth());
					date.setYear(pDate.getYear());
					dateConfig.setStartDate(date);
					
				}
				
				dateConfigList.add(dateConfig);
			}

			//Add the List of DateConfig (each one for each List item) in the new params to be used by the HTML transformer
			Object[] newParams = new Object[ params.length + 1 ];
			System.arraycopy( params, 0, newParams, 0, params.length );
			newParams[params.length] = dateConfigList;
			
			//Step 2. Set the Achievement item description
			
			String html = (String)super.transform(publicationList, this.template, newParams);
	
			item.setDescription(html);
			
			//Step 3. Put the Achievement item in to List
			achievementList.add(item);
			
			return achievementList;
		} catch ( final Exception e ){
			return to;
		}
         */
    }

    protected static class Publication {

        protected int id;
        protected String title;
        protected String publisher;
        protected String url;

        public Publication(int id, String title, String publisher, String url) {
            this.id = id;
            this.title = title;
            this.publisher = publisher;
            this.url = url;
        }
    }
}
