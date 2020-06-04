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

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class SimpleListTest {

    @Test
    public void writeXmlList() throws JsonProcessingException {
        XmlMapper m = new XmlMapper();

        TestList tl = new TestList();
        List<String> s = new ArrayList<String>();
        s.add("A1");
        s.add("B2");
        s.add("C2");
        tl.setTest(s);

        String xml = m.writeValueAsString(tl);

        assertNotNull(xml);
        //System.out.println(xml);
    }

    @XmlRootElement
    public static class TestList {

        private List<String> test;

        @JacksonXmlElementWrapper(localName = "description")
        @JacksonXmlProperty(localName = "licence")
        public List<String> getTest() {
            return test;
        }

        public void setTest(List<String> test) {
            this.test = test;
        }

    }
}
