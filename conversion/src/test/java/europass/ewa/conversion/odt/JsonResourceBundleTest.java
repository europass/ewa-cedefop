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
package europass.ewa.conversion.odt;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.ResourceBundle;

import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import europass.ewa.model.PrintingPreference;
import europass.ewa.model.format.JDateFormat;
import europass.ewa.resources.JsonResourceBundle;

public class JsonResourceBundleTest {

    @Test
    public void loadPostalCountryCodes() {
        ResourceBundle codes = ResourceBundle.getBundle("bundles/PostalCode", new JsonResourceBundle.Control());
        assertThat(codes, is(notNullValue()));
        assertThat(codes, is(instanceOf(JsonResourceBundle.class)));

        Object obj = codes.getObject("SE");
        assertThat(obj, instanceOf(String.class));
        assertThat(codes.getString("SE"), is("SE"));
    }

    @Test
    public void loadDefaultPrefs() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.addMixInAnnotations(Object.class, Mixin.class);
        ResourceBundle defaultPrefs = ResourceBundle.getBundle(
                "preferences/CVDefaultPrintingPreferences",
                new JsonResourceBundle.Control(mapper));

        assertNotNull(defaultPrefs);
        Object obj = defaultPrefs.getObject("LearnerInfo");
        assertThat(obj, is(instanceOf(PrintingPreference.class)));
    }

    @Test
    public void readPrefs() throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.addMixInAnnotations(Object.class, Mixin.class);

        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        InputStream in = cl.getResourceAsStream("preferences/CVDefaultPrintingPreferences.json");
        Map<String, Object> map = mapper.readValue(in, new TypeReference<Map<String, Object>>() {
        });
        Object obj = map.get("LearnerInfo");
        assertThat("PrintingPreference", obj, is(instanceOf(PrintingPreference.class)));

    }

    @JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "@type", defaultImpl = java.util.LinkedHashMap.class)
    @JsonSubTypes({
        @Type(name = "PrintingPreference", value = PrintingPreference.class)
        ,
		@Type(name = "JDateFormat", value = JDateFormat.class)
    })
    public static class Mixin {
    }
}
