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
package europass.ewa.mail;

import junit.framework.TestCase;
import org.junit.Ignore;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author jc
 */
@Ignore
public class TemplateMergerTest extends TestCase {

    public TemplateMergerTest(String testName) {
        super(testName);
    }

    public void testMerge() throws Exception {

        final Map model = new HashMap();

        model.put("FOO", "foo");
        model.put("BAR", "bar");
        model.put("random", "random");

        assertEquals("hello foo there bar", new TemplateMerger().merge(model, "hello FOO there BAR"));
        assertEquals("hello there", new TemplateMerger().merge(model, "hello there"));
        assertEquals("hello there foofoo", new TemplateMerger().merge(model, "hello there FOOFOO"));
    }

    public void testMergeWithComplexPlaceholdersOrValues() throws Exception {

        final Map model = new HashMap();

        model.put("{{FOO}}", "123");
        model.put("BAR", "{{BAR}}");
        model.put("\\", "\n");

        assertEquals("test 123 end\nnewline {{BAR}}", new TemplateMerger().merge(model, "test {{FOO}} end\\newline BAR"));
    }

    public void testMergeWithNullValue() throws Exception {

        final Map model = new HashMap();

        model.put("{{FOO}}", "foo");
        model.put("{{BAR}}", null);

        assertEquals("foo and ", new TemplateMerger().merge(model, "{{FOO}} and {{BAR}}"));
    }
}
