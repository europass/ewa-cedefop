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
package europass.ewa.conversion;

import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import org.hamcrest.CoreMatchers;
import org.junit.BeforeClass;
import org.junit.Test;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

public class SimpleMustacheTest {

    private static Mustache mustache;

    @BeforeClass
    public static void prepare() {
        MustacheFactory mf = new DefaultMustacheFactory();
        mustache = mf.compile(new StringReader("<h1>Test</h1>{{#name}}Hello, {{name}}!{{/name}}"), "example");

    }

    @Test
    public void testIfExists() throws IOException {

        Example example = new Example();
        example.setName("Lalakis");

        Writer writer = new StringWriter();
        mustache.execute(writer, example);

        String result = writer.toString();
        writer.flush();

        String expected = "<h1>Test</h1>Hello, Lalakis!";
        assertThat(result, CoreMatchers.is(expected));
    }

    @Test
    public void testIfNotExists() throws IOException {

        Example example = new Example();

        Writer writer = new StringWriter();
        mustache.execute(writer, example);

        String result = writer.toString();
        writer.flush();

        String expected = "<h1>Test</h1>";
        assertThat(result, CoreMatchers.is(expected));
    }

    private class Example {

        private String name;

        @SuppressWarnings("unused")
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }
}
