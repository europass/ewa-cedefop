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
package europass.ewa.model.conversion;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.io.SegmentedStringWriter;
import com.fasterxml.jackson.core.util.BufferRecycler;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonWriterTest {

    /*@Test
	public void staxWrite() throws Exception {
		StringWriter out = new StringWriter();
		XMLOutputFactory xof = XMLOutputFactory.newInstance();
		XMLStreamWriter writer = xof.createXMLStreamWriter(out);
		writer.writeCharacters("<test/>");
		assertThat(out.toString(), is("&lt;test/&gt;"));
	}

	@Test
	public void stax2Write() throws Exception {
		StringWriter out = new StringWriter();
		XMLOutputFactory xof = XMLOutputFactory.newInstance();
		XMLStreamWriter writer = xof.createXMLStreamWriter(out);
		StreamWriterDelegate delegate = new StreamWriterDelegate(writer);
		
		delegate.writeCharacters("<test>");
		assertThat(out.toString(), is("&lt;test&gt;"));
	}*/
    @SuppressWarnings("deprecation")
    @Test
    public void readXmlString() throws Exception {
        JsonParser jp = new ObjectMapper().getJsonFactory().createJsonParser("{\"test\":\"<test/>\"}");
        while (jp.nextToken() != JsonToken.VALUE_STRING) {
        };
        String test = jp.getText();
        assertThat(test, is("<test/>"));
    }

    @Test
    public void segementedWrite() {
        BufferRecycler br = new BufferRecycler();
        SegmentedStringWriter segw = new SegmentedStringWriter(br);
        segw.append("<test>");

        assertThat(segw.getAndClear(), is("<test>"));
        segw.close();
    }

    /*@Test
	public void writeXmlString() throws JsonGenerationException, JsonMappingException, IOException {
		TestString test = new TestString();
		test.test = "<test/>";
		String xml = new XmlMapper().writeValueAsString(test);
		assertThat(xml, is("<TestString xmlns=\"\"><test>&lt;test/&gt;</test></TestString>"));
	}*/

    public static class TestString {

        @JsonProperty
        private String test;
    }
}
