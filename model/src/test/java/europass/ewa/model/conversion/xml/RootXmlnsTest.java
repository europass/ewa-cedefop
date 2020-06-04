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

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

public class RootXmlnsTest {

    private static final String NS = "http://www.mytest.com";

    @JacksonXmlRootElement(localName = "testRoot", namespace = NS)
    static class TestCls {

        private String thisOne;

        private String thatOne;

        public TestCls() {
        }

        public TestCls(String one, String two) {
            this.thisOne = one;
            this.thatOne = two;
        }

        @JacksonXmlProperty(isAttribute = true, localName = "thisone", namespace = NS)
        public String getThisOne() {
            return thisOne;
        }

        public void setThisOne(String thisOne) {
            this.thisOne = thisOne;
        }

        @JsonProperty("thatone")
        @JacksonXmlProperty(namespace = NS)
        public String getThatOne() {
            return thatOne;
        }

        public void setThatOne(String thatOne) {
            this.thatOne = thatOne;
        }
    }

    @Test(expected = java.lang.AssertionError.class)
    public void testXmlns() throws JsonProcessingException {
        XmlMapper mapper = new XmlMapper();

        TestCls t = new TestCls("one", "two");

        String got = mapper.writeValueAsString(t);

        assertThat("xml", got, is("<testRoot xmlns=\"http://www.mytest.com\" thisone=\"one\"><thatone>two</thatone></testRoot>"));

    }
}
