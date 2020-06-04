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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import europass.ewa.enums.EuropassDocumentType;

public class EnumerationTest extends XmlMapperTest {

    static DocumentInfo docInfo;

    static final String XML
            = "<DocumentInfo>"
            + "<Bundle>"
            + "<Document>ECL</Document>"
            + "<Document>ECV_ESP</Document>"
            + "<Document>ESP</Document>"
            + "<Document>ECV</Document>"
            + "<Document>ELP</Document>"
            + "<Document>BABIS</Document>"
            + "</Bundle>"
            + "</DocumentInfo>";

    @BeforeClass
    public static void prepare() {
        List<EuropassDocumentType> bundle = new ArrayList<>();
        bundle.add(EuropassDocumentType.ECL);
        bundle.add(EuropassDocumentType.ECV_ESP);
        bundle.add(EuropassDocumentType.ESP);
        bundle.add(EuropassDocumentType.ECV);
        bundle.add(EuropassDocumentType.ELP);
        bundle.add(EuropassDocumentType.UNKNOWN);

        docInfo = new DocumentInfo();
        docInfo.setBundle(bundle);
    }

    @Test
    public void fromXML() throws JsonParseException, JsonMappingException, IOException {
        XmlMapper m = this.getMapper();

        DocumentInfo actualDocInfo = m.readValue(XML, DocumentInfo.class);

        Assert.assertNotNull(actualDocInfo.getBundle());
        Assert.assertThat(actualDocInfo.getBundle().size(), CoreMatchers.is(6));
        Assert.assertThat(actualDocInfo.getBundle().get(0), CoreMatchers.is(EuropassDocumentType.ECL));
        Assert.assertThat(actualDocInfo.getBundle().get(1), CoreMatchers.is(EuropassDocumentType.ECV_ESP));
        Assert.assertThat(actualDocInfo.getBundle().get(4), CoreMatchers.is(EuropassDocumentType.ELP));
        Assert.assertThat(actualDocInfo.getBundle().get(5), CoreMatchers.is(EuropassDocumentType.UNKNOWN));
    }

    @Test
    public void toXML() throws JsonProcessingException {
        XmlMapper m = this.getMapper();

        String actualXML = m.writeValueAsString(docInfo);

        Assert.assertNotNull(actualXML);

        String expected = XML.replaceAll("BABIS", "UNKNOWN");
        Assert.assertThat(actualXML, CoreMatchers.is("<?xml version='1.0' encoding='UTF-8'?>" + expected));

    }

    @JsonRootName("DocumentInfo")
    @JsonPropertyOrder({"bundle"})
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class DocumentInfo {

        List<EuropassDocumentType> bundle;

        @JsonProperty("Document")
        @JacksonXmlProperty(localName = "Document")
        @JacksonXmlElementWrapper(localName = "Bundle")
        public List<EuropassDocumentType> getBundle() {
            return bundle;
        }

        public void setBundle(List<EuropassDocumentType> bundle) {
            this.bundle = bundle;
        }

    }
}
