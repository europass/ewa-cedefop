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

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import europass.ewa.model.Namespace;
import europass.ewa.model.Period;
import europass.ewa.model.SkillsPassport;

public class XmlToPojoBooleansTest extends XmlMapperTest {

    private static String prefXml(String showAttr) {
        return "<?xml version='1.0' encoding='UTF-8'?>"
                + "<SkillsPassport xmlns=\"" + Namespace.NAMESPACE + "\" xmlns:xsi=\"" + Namespace.XSI_NAMESPACE + "\" xsi:schemaLocation=\"" + Namespace.NAMESPACE + " " + Namespace.SCHEMA_LOCATION_DEFAULT + "\">"
                + "<PrintingPreferences>"
                + "<Document type=\"ECV\">"
                + "<Field name=\"Address\" show=\"" + showAttr + "\"></Field>"
                + "</Document>"
                + "</PrintingPreferences>"
                + "</SkillsPassport>";
    }

    @Test
    public void prefOneAsBoolean() throws JsonParseException, JsonMappingException, IOException {
        String xml = prefXml("1");
        SkillsPassport esp = getMapper().readValue(xml, SkillsPassport.class);
        Assert.assertThat("Show ",
                esp.getDocumentPrintingPrefs().get("ECV").get(0).getShow(),
                CoreMatchers.is(true));
    }

    @Test
    public void prefZeroAsBoolean() throws JsonParseException, JsonMappingException, IOException {
        String xml = prefXml("0");
        SkillsPassport esp = getMapper().readValue(xml, SkillsPassport.class);
        Assert.assertThat("Show ",
                esp.getDocumentPrintingPrefs().get("ECV").get(0).getShow(),
                CoreMatchers.is(false));
    }

    @Test
    public void prefTrue() throws JsonParseException, JsonMappingException, IOException {
        String xml = prefXml("true");
        SkillsPassport esp = getMapper().readValue(xml, SkillsPassport.class);
        Assert.assertThat("Show ",
                esp.getDocumentPrintingPrefs().get("ECV").get(0).getShow(),
                CoreMatchers.is(true));
    }

    @Test
    public void prefFalse() throws JsonParseException, JsonMappingException, IOException {
        String xml = prefXml("false");
        SkillsPassport esp = getMapper().readValue(xml, SkillsPassport.class);
        Assert.assertThat("Show ",
                esp.getDocumentPrintingPrefs().get("ECV").get(0).getShow(),
                CoreMatchers.is(false));
    }

    private static String periodXml(String currentStr) {
        return "<Period><From year=\"2008\" month=\"--10\"/><To/><Current>" + currentStr + "</Current></Period>";
    }

    @Test
    public void periodWithOneAsBoolean() throws JsonParseException, JsonMappingException, IOException {
        String xml = periodXml("1");
        Period period = getMapper().readValue(xml, Period.class);
        Assert.assertThat(period.getCurrent(), CoreMatchers.is(true));
    }

    @Test
    public void periodWithZeroAsBoolean() throws JsonParseException, JsonMappingException, IOException {
        String xml = periodXml("0");
        Period period = getMapper().readValue(xml, Period.class);
        Assert.assertThat(period.getCurrent(), CoreMatchers.is(false));
    }

    @Test
    public void periodWithTrueBoolean() throws JsonParseException, JsonMappingException, IOException {
        String xml = periodXml("true");
        Period period = getMapper().readValue(xml, Period.class);
        Assert.assertThat(period.getCurrent(), CoreMatchers.is(true));
    }

    @Test
    public void periodWithFalseBoolean() throws JsonParseException, JsonMappingException, IOException {
        String xml = periodXml("false");
        Period period = getMapper().readValue(xml, Period.class);
        Assert.assertThat(period.getCurrent(), CoreMatchers.is(false));
    }
}
