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
package europass.ewa.tools.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import europass.ewa.resources.JsonResourceBundle;
import europass.ewa.tools.ga.enums.EventDataTypes;

public class Utils {
	
	private static final Logger LOG  = LoggerFactory.getLogger(Utils.class);
	
	private static final String COUNTRIES_JSON = "bundles/GCountryISO";

	private static final String SPLIT_DELIMITER = "_";
	
	public static Properties getProperties(String resource, boolean external, Class<?> clazz){
		
		Properties prop = new Properties();
		InputStream in = null;
		
		try {
						
			if(external)
				in = new FileInputStream("/"+resource);
			else
				in = clazz.getResourceAsStream("/"+resource);
			
			prop.load(in);
		} catch (Exception e) {
			LOG.error("Could not load from input stream: "+e.getMessage());
			return null;
		}finally{
			try {
				in.close();
			} catch (IOException ex) {
				LOG.error("Could not close input stream: "+ex.getMessage());
			}	
		}
		
		return prop;
	}
	
	public static String getCountryIsoCode(String countryName){
		
		ResourceBundle bundle = ResourceBundle.getBundle(COUNTRIES_JSON, new JsonResourceBundle.Control(new ObjectMapper()));
		
		if(countryName != null)
			if(bundle.containsKey(countryName))
				return bundle.getString(countryName);

		return countryName;
	}
	
	public static String formatDateTime(DateTime date){
		
		return date.getYear() + "-" +
				( date.getMonthOfYear() < 10 ? "0" + date.getMonthOfYear() : date.getMonthOfYear() ) + "-" +
				( date.getDayOfMonth() < 10 ? "0" + date.getDayOfMonth() : date.getDayOfMonth() );
	}

	public static void splitEventData(List<String> parsed, String eventData, EventDataTypes type){
		
		// In case it is not the event category or action return
		if(!type.equals(EventDataTypes.UAEcategory) && !type.equals(EventDataTypes.UAEaction))
			return;
		
		String[] splitArray = eventData.split(SPLIT_DELIMITER);
		
		switch(splitArray.length){
		
			// Case where eventData contains exactly one SPLIT_DELIMITER
			case 2:
				parsed.add(splitArray[0]);
				parsed.add(splitArray[1]);
				break;
				
			// Case where eventData does not contain SPLIT_DELIMITER
			case 1:
				parsed.add(splitArray[0]);
				parsed.add("");
				break;
	
			// Case where eventData equals SPLIT_DELIMITER
			case 0:
			// Any other case
			default:
				parsed.add("");
				parsed.add("");
				break;
		}
	}

}