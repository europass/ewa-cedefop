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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.namespace.QName;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator.Feature;

import europass.ewa.model.Namespace;
import europass.ewa.model.PrintingPreference;

public class PrintingPreferencesSerialiser extends JsonSerializer<Map<String, List<PrintingPreference>>> {

    private static final Logger LOG = LoggerFactory.getLogger(PrintingPreferencesSerialiser.class);

    @Override
    public void serialize(Map<String, List<PrintingPreference>> value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        if (!value.isEmpty()) {
            ToXmlGenerator xgen = (ToXmlGenerator) jgen;
            try {
                //Otherwise the XML declaration is repeated every time the .writeObject method is used
                xgen = xgen.configure(Feature.WRITE_XML_DECLARATION, false);

                QName prefsQName = new QName(Namespace.NAMESPACE, "PrintingPreferences");
                QName docQName = new QName(Namespace.NAMESPACE, "Document");
                QName fieldQName = new QName(Namespace.NAMESPACE, "Field");
                QName typeQName = new QName("type");

                //start wrap of Document
                xgen.startWrappedValue(prefsQName, docQName);

                int docs = value.entrySet().size();
                int counter = 0;
                for (Entry<String, List<PrintingPreference>> e : value.entrySet()) {

                    //start wrap of Field
                    xgen.startWrappedValue(docQName, fieldQName);

                    //Type attribute
                    xgen.setNextName(typeQName);
                    xgen.setNextIsAttribute(true);
                    String key = e.getKey();
                    xgen.writeString(key);
                    xgen.setNextIsAttribute(false);
                    //end type attribute

                    //Fields as objects
                    for (PrintingPreference pref : e.getValue()) {
                        xgen.setNextName(fieldQName);
                        xgen.writeFieldName("Field");
                        xgen.writeObject(pref);
                    }
                    //end fields

                    //end wrap of Field
                    xgen.finishWrappedValue(docQName, fieldQName);

                    //prepare type field name for next iteration ATTENTION! only if there will be a next iteration...
                    //otherwise this leads to an exception, as after writing a fieldName a value is expected.
                    counter++;
                    if (counter < docs) {
                        xgen.writeFieldName("type");
                    }
                }
                //end wrap of Document
                xgen.finishWrappedValue(prefsQName, docQName);

                xgen.configure(Feature.WRITE_XML_DECLARATION, true);

            } catch (Exception ioe) {
                LOG.error("PrintingPreferencesSerialiser:serialize - Could not serialize!", ioe);
            }
        }
    }
}
