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
package europass.ewa.model.conversion.xml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;

import europass.ewa.model.PrintingPreference;

public class PrintingPreferencesDeserialiser extends JsonDeserializer<Map<String, List<PrintingPreference>>> {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(PrintingPreferencesDeserialiser.class);

    private static final String DEFAULT_DOCUMENT_TYPE = "ECV";

    @Override
    public Map<String, List<PrintingPreference>> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {

        FromXmlParser xp = (FromXmlParser) jp;

        Map<String, List<PrintingPreference>> map = new HashMap<String, List<PrintingPreference>>();
        List<PrintingPreference> list;
        String type = DEFAULT_DOCUMENT_TYPE;
        String name = null;
        String show = null;
        String order = null;
        String format = null;
        String position = null;
        String justify = null;
        String enableName = null;
        String pageBreaks = null;

        PrintingPreference pref = null;
        boolean withinDocument = false;
        boolean withinField = false;

        JsonToken jt = xp.nextToken();
        String currentName = xp.getCurrentName();
        String innerName = "";
        //while - OUTER
        while (jt != null) {
            currentName = xp.getCurrentName();

            if (((jt.compareTo(JsonToken.END_OBJECT) == 0) && "PrintingPreferences".equals(currentName))) {
                break;
            }
            //DOCUMENT ELEMENT - START
            if (jt.compareTo(JsonToken.START_OBJECT) == 0 && "Document".equals(currentName)) {
                withinDocument = true;
            }
            //DOCUMENT ELEMENT - END
            if (jt.compareTo(JsonToken.END_OBJECT) == 0 && "Document".equals(currentName)) {
                withinDocument = false;
            }
            //INSIDE DOCUMENT ELEMENT
            if (withinDocument && jt.compareTo(JsonToken.FIELD_NAME) == 0 && "type".equals(xp.getCurrentName())) {
                innerName = xp.getCurrentName();
                String value = xp.nextTextValue();
                if ("type".equals(innerName)) {
                    type = value;
                }
                //initialize a map
                map.put(type, new ArrayList<PrintingPreference>());
            }
            // FIELD ELEMENT - START
            if (jt.compareTo(JsonToken.START_OBJECT) == 0 && "Field".equals(currentName)) {
                withinField = true;
            }
            // FIELD ELEMENT - END
            if (jt.compareTo(JsonToken.END_OBJECT) == 0 && "Field".equals(currentName)) {
                pref = new PrintingPreference(name, show, order, format, position, justify, enableName, pageBreaks);

                if (map != null && !map.isEmpty()) {
                    list = map.get(type);
                    list.add(pref);
                }

                //reset
                name = null;
                show = null;
                format = null;
                order = null;
                position = null;
                justify = null;
                enableName = null;
                pageBreaks = null;

                withinField = false;
            }
            //INSIDE FIELD ELEMENT
            if (withinField && jt.compareTo(JsonToken.FIELD_NAME) == 0) {
                innerName = xp.getCurrentName();
                String value = xp.nextTextValue();
                if ("name".equals(innerName)) {
                    name = value;
                } else if ("show".equals(innerName)) {
                    show = value;
                } else if ("order".equals(innerName)) {
                    order = value;
                } else if ("format".equals(innerName)) {
                    format = value;
                } else if ("position".equals(innerName)) {
                    position = value;
                } else if ("justify".equals(innerName)) {
                    justify = value;
                } else if ("enableName".equals(innerName)) {
                    enableName = value;
                } else if ("pageBreaks".equals(innerName)) {
                    pageBreaks = value;
                }
            }
            //increment
            jt = xp.nextToken();
        }
        return map;
    }
}
