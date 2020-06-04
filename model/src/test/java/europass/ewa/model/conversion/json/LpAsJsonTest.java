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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import europass.ewa.model.LPMockObjects;

public class LpAsJsonTest extends JsonMapperTest {

    @Test
    public void lpCertificates() throws JsonProcessingException {
        ObjectMapper mapper = this.getMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, false);
        String json = mapper.writeValueAsString(LPMockObjects.elpCerts);

        assertNotNull(json);

        String expected
                = "{\"SkillsPassport\":"
                + "{\"LearnerInfo\":"
                + "{\"Skills\":"
                + "{\"Linguistic\":"
                + "{\"ForeignLanguage\":"
                + "["
                + "{\"Certificate\":"
                + "["
                + "{\"Level\":\"A1\"},"
                + "{\"Level\":\"A2\"}"
                + "]"
                + "}"
                + "]"
                + "}"
                + "}"
                + "}"
                + "}"
                + "}";
        assertThat("ELP Certificates ", json, CoreMatchers.is(expected));
    }
}
