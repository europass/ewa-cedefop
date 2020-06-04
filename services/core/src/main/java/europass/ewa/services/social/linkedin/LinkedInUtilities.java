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

import europass.ewa.services.social.ExtraDataFieldNotPresentException;
import europass.ewa.services.social.InstanceClassMismatchException;

public class LinkedInUtilities {

    private static final String EXTRA_MAP_TOTAL_FIELD = "_total";
    private static final String EXTRA_MAP_VALUES_FIELD = "values";
    private static final String EXTRA_MAP_VOLUNTEER_EXP_FIELD = "volunteerExperiences";

    protected static String CountryCodeHandler(String code) {
        if (code == null) {
            return code;
        }
        code = code.toUpperCase();
        if ("GR".equals(code)) {
            return "EL";
        }
        if ("GB".equals(code)) {
            return "UK";
        }
        return code;
    }

    /**
     * Utility method to retrieve value(s) from the extraData HashMap
     *
     * @author pgia
     * @param <T>
     * @param extraDataObj
     * @param fieldName
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> ArrayList<T> extraDataFieldValues(Object extraDataObj, String fieldName) {

        try {
            HashMap<String, Object> extraDataMap = (HashMap<String, Object>) extraDataObj;
            if (!extraDataMap.containsKey(fieldName)) {
                throw new ExtraDataFieldNotPresentException(fieldName);
            }

            Object extraDataMapObj = extraDataMap.get(fieldName);
            if (!(extraDataMapObj instanceof LinkedHashMap)) {
                throw new InstanceClassMismatchException();
            }
            LinkedHashMap<String, Object> extraDataValueMap = (LinkedHashMap<String, Object>) extraDataMapObj;

            // In case of volunteer
            if (extraDataValueMap.containsKey(EXTRA_MAP_VOLUNTEER_EXP_FIELD)) {
                extraDataValueMap = (LinkedHashMap<String, Object>) extraDataValueMap.get(EXTRA_MAP_VOLUNTEER_EXP_FIELD);
            }

            if (!extraDataValueMap.containsKey(EXTRA_MAP_TOTAL_FIELD)) {
                throw new ExtraDataFieldNotPresentException(EXTRA_MAP_TOTAL_FIELD);
            }
            if (!extraDataValueMap.containsKey(EXTRA_MAP_VALUES_FIELD)) {
                throw new ExtraDataFieldNotPresentException(EXTRA_MAP_VALUES_FIELD);
            }
            if (!(extraDataValueMap.get(EXTRA_MAP_TOTAL_FIELD) instanceof Integer)) {
                throw new InstanceClassMismatchException();
            }

            Object extraDataValueObj = extraDataValueMap.get(EXTRA_MAP_VALUES_FIELD);
            if (!(extraDataValueObj instanceof ArrayList)) {
                throw new InstanceClassMismatchException();
            }

            return (ArrayList<T>) extraDataValueObj;

        } catch (Exception e) {
            return new ArrayList<T>();
        }

    }
}
