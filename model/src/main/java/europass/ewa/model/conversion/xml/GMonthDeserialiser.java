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
import java.text.ParseException;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonLocation;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import europass.ewa.model.JDate;

public class GMonthDeserialiser extends JsonDeserializer<Integer> {

    private static final Logger LOG = LoggerFactory.getLogger(GMonthDeserialiser.class);

    @Override
    public Integer deserialize(JsonParser jparser, DeserializationContext ctxt) throws IOException {
        String str = jparser.getText();

        try {
            Date monthDate = JDate.getXMLMonthFormat().parse(str);

            String fMonth = JDate.getJSONMonthFormat().format(monthDate);

            return Integer.valueOf(fMonth);

        } catch (ParseException e) {
            String msg = String.format("GMonthDeserialiser:deserialize - Invalid String '%s' found in JSON:", str);
            LOG.error(msg);
            throw new JsonParseException(msg, JsonLocation.NA, e);
        }
    }

}
