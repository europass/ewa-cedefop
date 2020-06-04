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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class JsonFilterWithXmlAttributeTest {

    @JsonFilter("test-filter")
    @JsonPropertyOrder({"id", "name"})
    abstract class TestFilterMixin {
    }

    @JsonPropertyOrder({"id", "name", "jsonProp"})
    abstract class TestNoFilterMixin {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class TestCls {

        private String id;
        private String name;
        private String jsonProp;

        public TestCls() {
        }

        public TestCls(String id, String name, String jsonProp) {
            this.id = id;
            this.name = name;
            this.jsonProp = jsonProp;
        }

        @JacksonXmlProperty(isAttribute = true)
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        @JsonProperty("name")
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @JsonProperty("jsonProp")
        public String getJsonProp() {
            return jsonProp;
        }

        public void setJsonProp(String jsonProp) {
            this.jsonProp = jsonProp;
        }

    }

    /*This test used to fail with previous versions of Jackson
	because of a bug in the use of Filters with XML attributes.*/
    @Test
    public void testFilter() throws JsonProcessingException {
        XmlMapper mapper = new XmlMapper();

        mapper.addMixInAnnotations(TestCls.class, TestFilterMixin.class);

        FilterProvider filters = new SimpleFilterProvider().addFilter("test-filter",
                SimpleBeanPropertyFilter.serializeAllExcept("jsonProp"));
        mapper.setFilters(filters);

        TestCls test = new TestCls("123", "tester", "jsonText");

        String xml = mapper.writeValueAsString(test);

        assertThat("xml", xml, is("<TestCls id=\"123\"><name>tester</name></TestCls>"));

    }

    @Test
    public void testNoFilter() throws JsonProcessingException {
        XmlMapper mapper = new XmlMapper();

        mapper.addMixInAnnotations(TestCls.class, TestNoFilterMixin.class);

        TestCls test = new TestCls("123", "tester", "jsonText");

        String xml = mapper.writeValueAsString(test);

        assertThat("xml", xml, is("<TestCls id=\"123\"><name>tester</name><jsonProp>jsonText</jsonProp></TestCls>"));

    }
}
