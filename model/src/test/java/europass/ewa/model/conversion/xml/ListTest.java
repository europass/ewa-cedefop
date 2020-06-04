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

import java.io.StringWriter;

import javax.xml.namespace.QName;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

public class ListTest extends XmlMapperTest {

    @SuppressWarnings("deprecation")
    @Test
    public void xmlMapOfListWrapped() throws Throwable {
        StringWriter out = new StringWriter();
        JsonFactory factory = new XmlFactory();
        JsonGenerator jgen = factory.createJsonGenerator(out);
        ToXmlGenerator xgen = (ToXmlGenerator) jgen;

        xgen.setNextName(new QName("PrintingPreferences"));
        xgen.writeStartObject();
        xgen.writeFieldName("Document");
        xgen.writeStartArray();
        xgen.writeStartObject();
        xgen.setNextIsAttribute(true);
        xgen.writeFieldName("type");
        xgen.writeString("ecv");
        xgen.setNextIsAttribute(false);
        xgen.writeFieldName("Field");
        xgen.writeStartArray();
        xgen.writeString("Test1");
        xgen.writeString("Test2");
        xgen.writeEndArray();
        xgen.writeEndObject();
        xgen.writeStartObject();
        xgen.setNextIsAttribute(true);
        xgen.writeFieldName("type");
        xgen.writeString("elp");
        xgen.writeEndObject();
        xgen.writeEndArray();
        xgen.writeEndObject();

        xgen.close();
        assertThat(out.toString(), is("<PrintingPreferences><Document type=\"ecv\"><Field>Test1</Field><Field>Test2</Field></Document><Document type=\"elp\"/></PrintingPreferences>"));

    }

}
