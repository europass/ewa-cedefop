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

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

public class BytesToXmlTest {

    @Test
    public void write1KBData() throws JsonProcessingException {
        testWriteData(1);
    }

    @Test
    public void write100KBData() throws JsonProcessingException {
        testWriteData(100);
    }

    @Test
    public void write256KBData() throws JsonProcessingException {
        testWriteData(256);
    }

    @Test
    public void write257KBData() throws JsonProcessingException {
        testWriteData(257);
    }

    //OK with version 2.0.5 : @Test( expected=JsonMappingException.class )
    public void write384KBData() throws JsonProcessingException {
        testWriteData(384);
    }

    //OK with version 2.0.5 : @Test( expected=JsonMappingException.class )
    public void write512KBData() throws JsonProcessingException {
        testWriteData(512);
    }

    //OK with version 2.0.5 : @Test( expected=JsonMappingException.class )
    public void write1MBData() throws JsonProcessingException {
        testWriteData(1024);
    }

    public void testWriteData(int kb) throws JsonProcessingException {
        Random r = new Random(1);
        Data data = new Data();
        data.bytes = new byte[1024 * kb];
        r.nextBytes(data.bytes);
        String xml = new XmlMapper().writeValueAsString(data);
        assertThat(xml, CoreMatchers.is(notNullValue()));
    }

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
    public void writeData() throws JsonProcessingException {
        Data data = new Data();
        data.bytes = "HelloWorld".getBytes();
        String xml = new XmlMapper().writeValueAsString(data);
        assertThat(xml, CoreMatchers.is(
                "<Data><bytes>"
                + Base64.encodeBase64String(data.bytes)
                + "</bytes></Data>"
        ));

    }

    @Test
    public void readData() throws JsonParseException, JsonMappingException, IOException {
        String xml = "<Data><bytes>" + Base64.encodeBase64String("Hello".getBytes()) + "</bytes></Data>";
        Data data = new XmlMapper().readValue(xml, Data.class);
        assertThat(new String(data.bytes), CoreMatchers.is("Hello"));
    }

}
