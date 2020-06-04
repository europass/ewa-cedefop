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

import java.io.IOException;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import europass.ewa.model.Period;
import europass.ewa.model.SkillsPassport;

public class JsonToPojoBooleansTest extends JsonMapperTest {

    private static String prefJson(String showAttr) {
        return "{ \"SkillsPassport\" :{ \"PrintingPreferences\" : {\"ECV\" : [{ \"name\" : \"LearnerInfo\", \"show\" : " + showAttr + " } ] } } }";
    }

    @Test(expected = com.fasterxml.jackson.databind.JsonMappingException.class)
    public void prefOneAsBoolean() throws JsonParseException, JsonMappingException, IOException {
        String json = prefJson("1");
        SkillsPassport esp = getMapper().readValue(json, SkillsPassport.class);
        Assert.assertThat("Show ",
                esp.getDocumentPrintingPrefs().get("ECV").get(0).getShow(),
                CoreMatchers.is(true));
    }

    @Test(expected = com.fasterxml.jackson.databind.JsonMappingException.class)
    public void prefZeroAsBoolean() throws JsonParseException, JsonMappingException, IOException {
        String json = prefJson("0");
        SkillsPassport esp = getMapper().readValue(json, SkillsPassport.class);
        Assert.assertThat("Show ",
                esp.getDocumentPrintingPrefs().get("ECV").get(0).getShow(),
                CoreMatchers.is(false));
    }

    @Test
    public void prefTrue() throws JsonParseException, JsonMappingException, IOException {
        String json = prefJson("true");
        SkillsPassport esp = getMapper().readValue(json, SkillsPassport.class);
        Assert.assertThat("Show ",
                esp.getDocumentPrintingPrefs().get("ECV").get(0).getShow(),
                CoreMatchers.is(true));
    }

    @Test
    public void prefFalse() throws JsonParseException, JsonMappingException, IOException {
        String json = prefJson("false");
        SkillsPassport esp = getMapper().readValue(json, SkillsPassport.class);
        Assert.assertThat("Show ",
                esp.getDocumentPrintingPrefs().get("ECV").get(0).getShow(),
                CoreMatchers.is(false));
    }

    private static String periodJson(String currentStr) {
        return "{\"Period\": {\"From\": {\"Year\": 2002,\"Month\": 8},\"Current\": " + currentStr + "}}";
    }

    @Test(expected = com.fasterxml.jackson.databind.JsonMappingException.class)
    public void periodWithOneAsBoolean() throws JsonParseException, JsonMappingException, IOException {
        String json = periodJson("1");
        Period period = getMapper().readValue(json, Period.class);
        Assert.assertThat(period.getCurrent(), CoreMatchers.is(true));
    }

    @Test(expected = com.fasterxml.jackson.databind.JsonMappingException.class)
    public void periodWithZeroAsBoolean() throws JsonParseException, JsonMappingException, IOException {
        String json = periodJson("0");
        Period period = getMapper().readValue(json, Period.class);
        Assert.assertThat(period.getCurrent(), CoreMatchers.is(false));
    }

    @Test
    public void periodWithTrueBoolean() throws JsonParseException, JsonMappingException, IOException {
        String json = periodJson("true");
        Period period = getMapper().readValue(json, Period.class);
        Assert.assertThat(period.getCurrent(), CoreMatchers.is(true));
    }

    @Test
    public void periodWithFalseBoolean() throws JsonParseException, JsonMappingException, IOException {
        String json = periodJson("false");
        Period period = getMapper().readValue(json, Period.class);
        Assert.assertThat(period.getCurrent(), CoreMatchers.is(false));
    }
}
