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
package europass.ewa.model.format;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

public class JDateFormatCompilerTest {

    private String[] sm = new String[0];
    private String[] lm = new String[0];
    private String[] ds = new String[0];

    @Test
    public void compile_d() {
        JDateFormat.Compiler compiler = new JDateFormat.Compiler(sm, lm, lm, ds);
        List<JDateField> fields = compiler.compileFields("d");
        assertThat("fields", fields.size(), is(1));
        assertThat("field 0 type", fields.get(0), instanceOf(JDateField.Day.class));
        assertThat("field 0 length", fields.get(0).length, is(1));
    }

    @Test
    public void compile_dd() {
        JDateFormat.Compiler compiler = new JDateFormat.Compiler(sm, lm, lm, ds);
        List<JDateField> fields = compiler.compileFields("dd");
        assertThat("fields", fields.size(), is(1));
        assertThat("field 0 type", fields.get(0), instanceOf(JDateField.Day.class));
        assertThat("field 0 length", fields.get(0).length, is(2));
    }

    @Test
    public void compile_M() {
        JDateFormat.Compiler compiler = new JDateFormat.Compiler(sm, lm, lm, ds);
        List<JDateField> fields = compiler.compileFields("M");
        assertThat("fields", fields.size(), is(1));
        assertThat("field 0 type", fields.get(0), instanceOf(JDateField.Month.class));
        assertThat("field 0 length", fields.get(0).length, is(1));
    }

    @Test
    public void compile_MM() {
        JDateFormat.Compiler compiler = new JDateFormat.Compiler(sm, lm, lm, ds);
        List<JDateField> fields = compiler.compileFields("MM");
        assertThat("fields", fields.size(), is(1));
        assertThat("field 0 type", fields.get(0), instanceOf(JDateField.Month.class));
        assertThat("field 0 length", fields.get(0).length, is(2));
    }

    @Test
    public void compile_yy() {
        JDateFormat.Compiler compiler = new JDateFormat.Compiler(sm, lm, lm, ds);
        List<JDateField> fields = compiler.compileFields("yy");
        assertThat("fields", fields.size(), is(1));
        assertThat("field 0 type", fields.get(0), instanceOf(JDateField.Year.class));
        assertThat("field 0 length", fields.get(0).length, is(2));
    }

    @Test
    public void compile_yyyy() {
        JDateFormat.Compiler compiler = new JDateFormat.Compiler(sm, lm, lm, ds);
        List<JDateField> fields = compiler.compileFields("yyyy");
        assertThat("fields", fields.size(), is(1));
        assertThat("field 0 type", fields.get(0), instanceOf(JDateField.Year.class));
        assertThat("field 0 length", fields.get(0).length, is(4));
    }

    @Test
    public void compile_yyyyMMdd() {
        JDateFormat.Compiler compiler = new JDateFormat.Compiler(sm, lm, lm, ds);
        List<JDateField> fields = compiler.compileFields("yyyyMMdd");
        assertThat("fields", fields.size(), is(3));

        assertThat("field 0 type", fields.get(0), instanceOf(JDateField.Year.class));
        assertThat("field 0 length", fields.get(0).length, is(4));

        assertThat("field 1 type", fields.get(1), instanceOf(JDateField.Month.class));
        assertThat("field 1 length", fields.get(1).length, is(2));

        assertThat("field 2 type", fields.get(2), instanceOf(JDateField.Day.class));
        assertThat("field 2 length", fields.get(2).length, is(2));
    }

    @Test
    public void compile_numeric_long() {
        JDateFormat.Compiler compiler = new JDateFormat.Compiler(sm, lm, lm, ds);
        List<JDateField> fields = compiler.compileFields("dd/MM/yyyy");
        assertThat("fields", fields.size(), is(5));

        assertThat("field 0 type", fields.get(0), instanceOf(JDateField.Day.class));
        assertThat("field 0 length", fields.get(0).length, is(2));

        assertThat("field 1 type", fields.get(1), instanceOf(JDateField.Text.class));
        assertThat("field 1 length", fields.get(1).length, is(1));

        assertThat("field 2 type", fields.get(2), instanceOf(JDateField.Month.class));
        assertThat("field 2 length", fields.get(2).length, is(2));

        assertThat("field 3 type", fields.get(3), instanceOf(JDateField.Text.class));
        assertThat("field 3 length", fields.get(3).length, is(1));

        assertThat("field 4 type", fields.get(4), instanceOf(JDateField.Year.class));
        assertThat("field 4 length", fields.get(4).length, is(4));

    }

    @Test
    public void compile_numeric_short() {
        JDateFormat.Compiler compiler = new JDateFormat.Compiler(sm, lm, lm, ds);
        List<JDateField> fields = compiler.compileFields("d/M/yy");
        assertThat("fields", fields.size(), is(5));

        assertThat("field 0 type", fields.get(0), instanceOf(JDateField.Day.class));
        assertThat("field 0 length", fields.get(0).length, is(1));

        assertThat("field 1 type", fields.get(1), instanceOf(JDateField.Text.class));
        assertThat("field 1 length", fields.get(1).length, is(1));
        assertThat("field 1 text", ((JDateField.Text) fields.get(1)).text, is("/"));

        assertThat("field 2 type", fields.get(2), instanceOf(JDateField.Month.class));
        assertThat("field 2 length", fields.get(2).length, is(1));

        assertThat("field 3 type", fields.get(3), instanceOf(JDateField.Text.class));
        assertThat("field 3 length", fields.get(3).length, is(1));
        assertThat("field 3 text", ((JDateField.Text) fields.get(3)).text, is("/"));

        assertThat("field 4 type", fields.get(4), instanceOf(JDateField.Year.class));
        assertThat("field 4 length", fields.get(4).length, is(2));
    }

    @Test
    public void compile_de_text_long() {
        JDateFormat.Compiler compiler = new JDateFormat.Compiler(sm, lm, lm, ds);
        List<JDateField> fields = compiler.compileFields("dd. MMMM yyyy");
        assertThat("fields", fields.size(), is(5));

        assertThat("field 0 type", fields.get(0), instanceOf(JDateField.Day.class));
        assertThat("field 0 length", fields.get(0).length, is(2));

        assertThat("field 1 type", fields.get(1), instanceOf(JDateField.Text.class));
        assertThat("field 1 length", fields.get(1).length, is(2));
        assertThat("field 1 text", ((JDateField.Text) fields.get(1)).text, is(". "));

        assertThat("field 2 type", fields.get(2), instanceOf(JDateField.Month.class));
        assertThat("field 2 length", fields.get(2).length, is(4));

        assertThat("field 3 type", fields.get(3), instanceOf(JDateField.Text.class));
        assertThat("field 3 length", fields.get(3).length, is(1));
        assertThat("field 3 text", ((JDateField.Text) fields.get(3)).text, is(" "));

        assertThat("field 4 type", fields.get(4), instanceOf(JDateField.Year.class));
        assertThat("field 4 length", fields.get(4).length, is(4));
    }

    @Test
    public void compile_lv_text_short() {
        JDateFormat.Compiler compiler = new JDateFormat.Compiler(sm, lm, lm, ds);
        List<JDateField> fields = compiler.compileFields("yy'.gada 'd'.'MMM");
        assertThat("fields", fields.size(), is(5));

        assertThat("field 0 type", fields.get(0), instanceOf(JDateField.Year.class));
        assertThat("field 0 length", fields.get(0).length, is(2));

        assertThat("field 1 type", fields.get(1), instanceOf(JDateField.Text.class));
        assertThat("field 1 length", fields.get(1).length, is(6));
        assertThat("field 1 text", ((JDateField.Text) fields.get(1)).text, is(".gada "));

        assertThat("field 4 type", fields.get(2), instanceOf(JDateField.Day.class));
        assertThat("field 4 length", fields.get(2).length, is(1));

        assertThat("field 5 type", fields.get(3), instanceOf(JDateField.Text.class));
        assertThat("field 5 length", fields.get(3).length, is(1));
        assertThat("field 5 text", ((JDateField.Text) fields.get(3)).text, is("."));

        assertThat("field 6 type", fields.get(4), instanceOf(JDateField.Month.class));
        assertThat("field 6 length", fields.get(4).length, is(3));
    }

    @Test
    public void compile_fun() {
        JDateFormat.Compiler compiler = new JDateFormat.Compiler(sm, lm, lm, ds);
        List<JDateField> fields = compiler.compileFields("MMM d ''yy");
        assertThat("fields", fields.size(), is(5));

        assertThat("field 0 type", fields.get(0), instanceOf(JDateField.Month.class));
        assertThat("field 0 length", fields.get(0).length, is(3));

        assertThat("field 1 type", fields.get(1), instanceOf(JDateField.Text.class));
        assertThat("field 1 length", fields.get(1).length, is(1));
        assertThat("field 1 text", ((JDateField.Text) fields.get(1)).text, is(" "));

        assertThat("field 2 type", fields.get(2), instanceOf(JDateField.Day.class));
        assertThat("field 2 length", fields.get(2).length, is(1));

        assertThat("field 3 type", fields.get(3), instanceOf(JDateField.Text.class));
        assertThat("field 3 length", fields.get(3).length, is(2));
        assertThat("field 3 text", ((JDateField.Text) fields.get(3)).text, is(" \'"));

        assertThat("field 4 type", fields.get(4), instanceOf(JDateField.Year.class));
        assertThat("field 4 length", fields.get(4).length, is(2));
    }

    @Test
    public void compile_lt_text_long() {
        JDateFormat.Compiler compiler = new JDateFormat.Compiler(sm, lm, lm, ds);
        List<JDateField> fields = compiler.compileFields("yyyy 'm. 'MMMM d 'd'.");
        assertThat("fields", fields.size(), is(9));

        assertThat("field 0 type", fields.get(0), instanceOf(JDateField.Year.class));
        assertThat("field 0 length", fields.get(0).length, is(4));

        assertThat("field 1 type", fields.get(1), instanceOf(JDateField.Text.class));
        assertThat("field 1 length", fields.get(1).length, is(1));
        assertThat("field 1 text", ((JDateField.Text) fields.get(1)).text, is(" "));

        assertThat("field 2 type", fields.get(2), instanceOf(JDateField.Text.class));
        assertThat("field 2 length", fields.get(2).length, is(3));
        assertThat("field 2 text", ((JDateField.Text) fields.get(2)).text, is("m. "));

        assertThat("field 3 type", fields.get(3), instanceOf(JDateField.Month.class));
        assertThat("field 3 length", fields.get(3).length, is(4));

        assertThat("field 4 type", fields.get(4), instanceOf(JDateField.Text.class));
        assertThat("field 4 length", fields.get(4).length, is(1));
        assertThat("field 4 text", ((JDateField.Text) fields.get(4)).text, is(" "));

        assertThat("field 5 type", fields.get(5), instanceOf(JDateField.Day.class));
        assertThat("field 5 length", fields.get(5).length, is(1));

        assertThat("field 6 type", fields.get(6), instanceOf(JDateField.Text.class));
        assertThat("field 6 length", fields.get(6).length, is(1));
        assertThat("field 6 text", ((JDateField.Text) fields.get(6)).text, is(" "));

        assertThat("field 7 type", fields.get(7), instanceOf(JDateField.Text.class));
        assertThat("field 7 length", fields.get(7).length, is(1));
        assertThat("field 7 text", ((JDateField.Text) fields.get(7)).text, is("d"));

        assertThat("field 8 type", fields.get(8), instanceOf(JDateField.Text.class));
        assertThat("field 8 length", fields.get(8).length, is(1));
        assertThat("field 8 text", ((JDateField.Text) fields.get(8)).text, is("."));
    }

    @Test
    public void compile_en_text_long_suffix() {
        JDateFormat.Compiler compiler = new JDateFormat.Compiler(sm, lm, lm, ds);
        List<JDateField> fields = compiler.compileFields("ddd MMMM yyyy");
        assertThat("fields", fields.size(), is(5));

        assertThat("field 0 type", fields.get(0), instanceOf(JDateField.Day.class));
        assertThat("field 0 length", fields.get(0).length, is(3));

        assertThat("field 1 type", fields.get(1), instanceOf(JDateField.Text.class));
        assertThat("field 1 length", fields.get(1).length, is(1));
        assertThat("field 1 text", ((JDateField.Text) fields.get(1)).text, is(" "));

        assertThat("field 2 type", fields.get(2), instanceOf(JDateField.Month.class));
        assertThat("field 2 length", fields.get(2).length, is(4));

        assertThat("field 3 type", fields.get(3), instanceOf(JDateField.Text.class));
        assertThat("field 3 length", fields.get(3).length, is(1));
        assertThat("field 3 text", ((JDateField.Text) fields.get(3)).text, is(" "));

        assertThat("field 4 type", fields.get(4), instanceOf(JDateField.Year.class));
        assertThat("field 4 length", fields.get(4).length, is(4));
    }

    @Test
    public void compile_en_text_long_texts_fr() {
        JDateFormat.Compiler compiler = new JDateFormat.Compiler(sm, lm, lm, ds);
        List<JDateField> fields = compiler.compileFields("el dd MMMM yyyy");
        assertThat("fields", fields.size(), is(6));

        assertThat("field 0 type", fields.get(0), instanceOf(JDateField.Text.class));
        assertThat("field 0 length", fields.get(0).length, is(3));

        assertThat("field 1 type", fields.get(1), instanceOf(JDateField.Day.class));
        assertThat("field 1 length", fields.get(1).length, is(2));

        assertThat("field 2 type", fields.get(2), instanceOf(JDateField.Text.class));
        assertThat("field 2 length", fields.get(2).length, is(1));
        assertThat("field 2 text", ((JDateField.Text) fields.get(2)).text, is(" "));

        assertThat("field 3 type", fields.get(3), instanceOf(JDateField.Month.class));
        assertThat("field 3 length", fields.get(3).length, is(4));

        assertThat("field 4 type", fields.get(4), instanceOf(JDateField.Text.class));
        assertThat("field 4 length", fields.get(4).length, is(1));
        assertThat("field 4 text", ((JDateField.Text) fields.get(4)).text, is(" "));

        assertThat("field 5 type", fields.get(5), instanceOf(JDateField.Year.class));
        assertThat("field 5 length", fields.get(5).length, is(4));
    }
}
