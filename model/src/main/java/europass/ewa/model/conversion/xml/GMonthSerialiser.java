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
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import europass.ewa.model.JDate;

public class GMonthSerialiser extends JsonSerializer<Integer> {

    private static final Logger LOG = LoggerFactory.getLogger(GMonthSerialiser.class);

    @Override
    public void serialize(Integer value, JsonGenerator jgen, SerializerProvider provider) throws IOException {

        try {
            Date monthDate = JDate.getJSONMonthFormat().parse(String.valueOf(value));

            jgen.writeString(JDate.getXMLMonthFormat().format(monthDate));

        } catch (Exception ioe) {
            LOG.error("GMonthSerialiser:serliaze - Could not serialize gmonth: " + value, ioe);
        }
    }

}
