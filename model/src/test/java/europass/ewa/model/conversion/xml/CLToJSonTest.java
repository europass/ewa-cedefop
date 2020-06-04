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

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import europass.ewa.model.CLMockObject;
import europass.ewa.model.conversion.json.JsonMapperTest;

public class CLToJSonTest extends JsonMapperTest {

    @Override
    public ObjectMapper getMapper() {
        ObjectMapper mapper = super.getMapper();
        mapper.configure(SerializationFeature.INDENT_OUTPUT, false);
        return mapper;
    }

    ;
	@Test
    public void completePersonName() throws JsonProcessingException {
        String json = getMapper().writeValueAsString(CLMockObject.learner());

        Assert.assertNotNull("JSON produced - ", json);

        boolean exact
                = json.contains(CLMockObject.PERSONNAME_WITH_TITLE_JSON);
        Assert.assertThat("Cover Letter Holder PersonName", exact, CoreMatchers.is(true));
    }

    @Test
    public void addressee() throws JsonProcessingException {
        String json = getMapper().writeValueAsString(CLMockObject.addressee());

        Assert.assertNotNull("JSON produced - ", json);

        boolean exact = json.contains(CLMockObject.ADDRESSEE_JSON);
        Assert.assertThat("Cover Letter Addressee", exact, CoreMatchers.is(true));
    }

    // RELATED TO EWA-901
    @Test
    public void addressLine2() throws JsonProcessingException {
        String json = getMapper().writeValueAsString(CLMockObject.addresseeWithAddressLine2());

        Assert.assertNotNull("JSON produced - ", json);

        boolean exact = json.contains(CLMockObject.ADDRESSEE_WITH_ADDRESSLINE2_JSON);
        Assert.assertThat("Cover Letter Addressee", exact, CoreMatchers.is(true));

    }

    @Test
    public void letter() throws JsonProcessingException {
        String json = getMapper().writeValueAsString(CLMockObject.letter());

        Assert.assertNotNull("JSON produced - ", json);

        boolean exact = json.contains(CLMockObject.LETTER_JSON);
        Assert.assertThat("Cover Letter Letter", exact, CoreMatchers.is(true));
    }

    @Test
    public void documentation() throws JsonProcessingException {
        String json = getMapper().writeValueAsString(CLMockObject.documentation("en"));

        Assert.assertNotNull("JSON produced - ", json);

        boolean exact = json.contains(CLMockObject.DOCUMENTATION_JSON);
        Assert.assertThat("Cover Letter Encolsed Documents", exact, CoreMatchers.is(true));

    }

    @Test
    public void documentationDE() throws JsonProcessingException {
        String json = getMapper().writeValueAsString(CLMockObject.documentation("de"));
        String DOCUMENTATION_JSON = CLMockObject.DOCUMENTATION_JSON
                .replaceAll("\"Code\":\"enclosed\",\"Label\":\"Enclosed:\"", "\"Code\":\"attached\",\"Label\":\"Angebracht:\"");

        Assert.assertNotNull("JSON produced - ", json);

        boolean exact = json.contains(DOCUMENTATION_JSON);
        Assert.assertThat("Cover Letter Encolsed Documents", exact, CoreMatchers.is(true));

    }

}
