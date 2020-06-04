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

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;

import europass.ewa.model.Period;
import europass.ewa.model.PrintingPreference;
import europass.ewa.model.SkillsPassport;
import europass.ewa.model.wrapper.Feedback;

public class EWAJsonMapper {

    private EWAJsonMapper() {
        throw new AssertionError();
    }

    public static ObjectMapper get() {
        ObjectMapper mapper = new ObjectMapper();

        //JSON Serialization
        mapper.setSerializationInclusion(Include.NON_EMPTY);
        mapper.setSerializationInclusion(Include.NON_NULL);

        mapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, true);
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);

        //JSON Deserialization
        //Requires that the root class is annotated with @JsonRootName
        mapper.configure(DeserializationFeature.UNWRAP_ROOT_VALUE, true);
        //See feedback of failure because the empty string was added in a list
        mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);

        mapper.addMixInAnnotations(SkillsPassport.class, SkillsPassportMixin.class);

        mapper.addMixInAnnotations(Feedback.class, FeedbackMixin.class);

        //EWA-934
        mapper.addMixInAnnotations(Period.class, PeriodMixin.class);
        mapper.addMixInAnnotations(PrintingPreference.class, PrintingPreferenceMixin.class);

        mapper.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);

        mapper.registerModule(new JodaModule());
        mapper.configure(
                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        return mapper;
    }
}
