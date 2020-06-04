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
package europass.ewa.logging;

import java.util.Arrays;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang.StringEscapeUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

import europass.ewa.enums.LogFields;
import static europass.ewa.enums.LogFields.*;

public class ExtraLogInfo {

    private SortedMap<LogFields, String> logInfoMap;

    private static final LogFields[] listFields = {MESSAGE, DOCTYPE, ACTION}; //these fields will be augmented each type a value is added to them

    public ExtraLogInfo() {
        logInfoMap = new TreeMap<>();
    }

    public ExtraLogInfo add(LogFields field, String value) {

        if (!Strings.isNullOrEmpty(value)) {

            if (FILETYPE.equals(field) || EXTENSION.equals(field)) {
                value = value.indexOf(".") == 0 ? value.toUpperCase() : "." + value.toUpperCase(); // enforce same formatting of file extension/type ( uppercase preceded by ".")
            }
            if (Arrays.binarySearch(listFields, field) > 0) {
                String val = logInfoMap.get(field);
                if (val != null) {
                    value += ", " + val;
                }
            }
            logInfoMap.put(field, StringEscapeUtils.escapeJavaScript(value));
        }
        return this;
    }

    public ExtraLogInfo add(ExtraLogInfo fields) {
        if (fields != null) {
            logInfoMap.putAll(fields.logInfoMap);
        }
        return this;
    }

    public String getLogInfoAsJson() throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(logInfoMap);
    }

    public String getLogEntry(LogFields field) {
        return logInfoMap.get(field);
    }

    /* formatText sorts and fortmats the additional info list data 
	 * @param logInfoMap
	 * @return JSON representation 
	 * */
    public String toStringAsJson() {

        StringBuilder sb = new StringBuilder("{");
        String prefix = "\"", infix = "\":\"", suffix = "\",\n";

        for (LogFields field : logInfoMap.keySet()) {
            sb.append(prefix).append(field.getDescription()).append(infix).
                    append(logInfoMap.get(field)).
                    append(suffix);
        }
        sb.setLength(sb.length() - suffix.length() + 1);	//remove the last suffix
        return sb.toString() + "}";
    }
}
