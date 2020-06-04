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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class XmlToMultipleByteTest {

    public static class Data {

        @JsonProperty
        byte[] bytes;
    }

    public static class TwoData {

        @JsonProperty
        Data data1;

        @JsonProperty
        Data data2;
    }

    @Test
    public void xmlReadTwoData() throws JsonParseException, JsonMappingException, IOException {
        String xml
                = "<TwoData>"
                + "<data1><bytes>" + Base64.encodeBase64String("Hello".getBytes()) + "</bytes></data1>"
                + "<data2><bytes>" + Base64.encodeBase64String("World".getBytes()) + "</bytes></data2>"
                + "</TwoData>";

        TwoData two = new XmlMapper().readValue(xml, TwoData.class);
        assertThat(new String(two.data1.bytes), is("Hello"));
        assertThat(new String(two.data2.bytes), is("World"));
    }

    @Test
    public void jsonReadTwoData() throws JsonParseException, JsonMappingException, IOException {
        String xml
                = "{"
                + "\"data1\":{\"bytes\":\"" + Base64.encodeBase64String("Hello".getBytes()) + "\"},"
                + "\"data2\":{\"bytes\":\"" + Base64.encodeBase64String("World".getBytes()) + "\"}"
                + "}";

        TwoData two = new ObjectMapper().readValue(xml, TwoData.class);
        assertThat(new String(two.data1.bytes), is("Hello"));
        assertThat(new String(two.data2.bytes), is("World"));
    }
}
