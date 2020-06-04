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
package europass.ewa.model.custom.deserializers;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class CustomJsonDateDeserializer extends JsonDeserializer<DateTime> {

    private static final Logger LOG = LoggerFactory.getLogger(CustomJsonDateDeserializer.class);

    private static final String[] DATE_FORMATS = {
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
        "yyyy-MM-dd'T'HH:mm:ss.SSS XXX",
        "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
        "yyyy-MM-dd'T'HH:mm:ss.SSS Z",
        "yyyy-MM-dd'T'HH:mm:ss.SSS HH:mm",
        "yyyy-MM-dd'T'HH:mm:ss.SSS HHmm"
    };

    @Override
    public DateTime deserialize(JsonParser jsonparser, DeserializationContext deserializationcontext) throws IOException, JsonProcessingException {

        SimpleDateFormat output = new SimpleDateFormat(DATE_FORMATS[0]);
        output.setTimeZone(TimeZone.getTimeZone("UTC"));
        String dateStr = jsonparser.getText();

        for (String format : DATE_FORMATS) {
            try {
                SimpleDateFormat parser = new SimpleDateFormat(format);
                parser.setTimeZone(TimeZone.getTimeZone("UTC"));
                Date date = parser.parse(dateStr);
                String formattedDate = output.format(date);

                return DateTime.parse(formattedDate).toDateTimeISO();
            } catch (ParseException e) {
                //e.printStackTrace();
                LOG.info("date format is not " + format);
                continue;
            }
        }

        return DateTime.now();
    }
}
