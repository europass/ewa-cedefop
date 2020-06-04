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
package europass.ewa.model.conversion.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.google.common.base.Strings;

public class BooleanDeserialiser extends JsonDeserializer<Boolean> {

    @Override
    public Boolean deserialize(JsonParser jparser, DeserializationContext ctxt) throws IOException {
        String str = jparser.getText();

        if (Strings.isNullOrEmpty(str)) {
            return null;
        }
        try {
            Integer asInt = Integer.parseInt(str);
            switch (asInt) {
                case 0:
                case 1: {
                    throw new JsonMappingException("0/1 are not valid values for boolean according to JSON Schema");
                }
            }
        } catch (final NumberFormatException nfe) {
            return Boolean.valueOf(str);
        }

        return Boolean.valueOf(str);
    }

}
