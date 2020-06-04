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

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Locale;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import europass.ewa.model.JDate;

public class JDateFormatBundleTest {

    @Test
    public void load() {
        JDateFormatBundle bundle = JDateFormatBundle.getBundle(Locale.ENGLISH);
        JDateFormat fmt = bundle.getJDateFormat("numeric/short");
        assertThat(fmt, CoreMatchers.is(notNullValue()));
        JDate date = new JDate();
        date.setYear(2011);
        date.setMonth(12);
        date.setDay(13);
        assertThat("Formatting", fmt.format(date), CoreMatchers.is("13/12/11"));
    }

    @Test
    public void load2() {
        JDateFormatBundle bundle = JDateFormatBundle.getBundle(Locale.ENGLISH);
        JDateFormat fmt = bundle.getJDateFormat("numeric/short");
        assertThat(fmt, CoreMatchers.is(notNullValue()));
        JDate date = new JDate();
        date.setYear(2011);
        date.setMonth(12);
        assertThat("Formatting", fmt.format(date), CoreMatchers.is("12/11"));
    }

    @Test
    public void load_el_text_short() {
        JDateFormatBundle bundle = JDateFormatBundle.getBundle(new Locale("el"));
        JDateFormat fmt = bundle.getJDateFormat("text/short");
        assertThat(fmt, CoreMatchers.is(notNullValue()));
        JDate date = new JDate();
        date.setYear(2011);
        date.setMonth(12);
        date.setDay(13);
        assertThat("Formatting", fmt.format(date), CoreMatchers.is("13 Δεκ 11"));
    }

    @Test
    public void load_el_text_short2() {
        JDateFormatBundle bundle = JDateFormatBundle.getBundle(new Locale("el"));
        JDateFormat fmt = bundle.getJDateFormat("text/short");
        assertThat(fmt, CoreMatchers.is(notNullValue()));
        JDate date = new JDate();
        date.setYear(2011);
        date.setMonth(12);
        assertThat("Formatting", fmt.format(date), CoreMatchers.is("Δεκ 11"));
    }

    @Test
    public void load_el_text_long() {
        JDateFormatBundle bundle = JDateFormatBundle.getBundle(new Locale("el"));
        JDateFormat fmt = bundle.getJDateFormat("text/long");
        assertThat(fmt, CoreMatchers.is(notNullValue()));
        JDate date = new JDate();
        date.setYear(2011);
        date.setMonth(12);
        date.setDay(13);
        assertThat("Formatting", fmt.format(date), CoreMatchers.is("13 Δεκεμβρίου 2011"));
    }

    @Test
    public void load_el_text_long2() {
        JDateFormatBundle bundle = JDateFormatBundle.getBundle(new Locale("el"));
        JDateFormat fmt = bundle.getJDateFormat("text/long");
        assertThat(fmt, CoreMatchers.is(notNullValue()));
        JDate date = new JDate();
        date.setYear(2011);
        date.setMonth(12);
        assertThat("Formatting", fmt.format(date), CoreMatchers.is("Δεκέμβριος 2011"));
    }

    @Test
    public void load_pl_text_short() {
        JDateFormatBundle bundle = JDateFormatBundle.getBundle(new Locale("pl"));
        JDateFormat fmt = bundle.getJDateFormat("text/short");
        assertThat(fmt, CoreMatchers.is(notNullValue()));
        JDate date = new JDate();
        date.setYear(2011);
        date.setMonth(12);
        date.setDay(13);
        assertThat("Formatting", fmt.format(date), CoreMatchers.is("13/12/2011"));
    }

    @Test
    public void load_lt_text_long() {
        JDateFormatBundle bundle = JDateFormatBundle.getBundle(new Locale("lt"));
        JDateFormat fmt = bundle.getJDateFormat("text/long");
        assertThat(fmt, CoreMatchers.is(notNullValue()));
        JDate date = new JDate();
        date.setYear(2011);
        date.setMonth(12);
        date.setDay(12);
        assertThat("Formatting", fmt.format(date), CoreMatchers.is("2011 m. gruodžio 12 d."));
    }

    @Test
    public void load_lt_text_long2() {
        JDateFormatBundle bundle = JDateFormatBundle.getBundle(new Locale("lt"));
        JDateFormat fmt = bundle.getJDateFormat("text/long");
        assertThat(fmt, CoreMatchers.is(notNullValue()));
        JDate date = new JDate();
        date.setYear(2011);
        date.setMonth(12);
        assertThat("Formatting", fmt.format(date), CoreMatchers.is("2011 m. gruodis"));
    }

    @Test
    public void load_en_text_long_suffix() {
        JDateFormatBundle bundle = JDateFormatBundle.getBundle(new Locale("en"));
        JDateFormat fmt = bundle.getJDateFormat("text/long/suffix");
        assertThat(fmt, CoreMatchers.is(notNullValue()));
        JDate date = new JDate();
        date.setYear(2011);
        date.setMonth(12);
        date.setDay(13);
        assertThat("Formatting", fmt.format(date), CoreMatchers.is("13th December 2011"));
    }

    @Test
    public void load_en_text_long_suffix_2() {
        JDateFormatBundle bundle = JDateFormatBundle.getBundle(new Locale("en"));
        JDateFormat fmt = bundle.getJDateFormat("text/long/suffix");
        assertThat(fmt, CoreMatchers.is(notNullValue()));
        JDate date = new JDate();
        date.setYear(2011);
        date.setMonth(12);
        date.setDay(21);
        assertThat("Formatting", fmt.format(date), CoreMatchers.is("21st December 2011"));
    }

    @Test
    public void load_en_text_long_suffix_3() {
        JDateFormatBundle bundle = JDateFormatBundle.getBundle(new Locale("en"));
        JDateFormat fmt = bundle.getJDateFormat("text/long/suffix");
        assertThat(fmt, CoreMatchers.is(notNullValue()));
        JDate date = new JDate();
        date.setYear(2011);
        date.setMonth(12);
        date.setDay(22);
        assertThat("Formatting", fmt.format(date), CoreMatchers.is("22nd December 2011"));
    }

    @Test
    public void load_en_text_long_suffix_4() {
        JDateFormatBundle bundle = JDateFormatBundle.getBundle(new Locale("en"));
        JDateFormat fmt = bundle.getJDateFormat("text/long/suffix");
        assertThat(fmt, CoreMatchers.is(notNullValue()));
        JDate date = new JDate();
        date.setYear(2011);
        date.setMonth(12);
        date.setDay(31);
        assertThat("Formatting", fmt.format(date), CoreMatchers.is("31st December 2011"));
    }

    @Test
    public void load_en_text_long_texts_fr() {
        JDateFormatBundle bundle = JDateFormatBundle.getBundle(new Locale("fr"));
        JDateFormat fmt = bundle.getJDateFormat("text/long/texts");
        assertThat(fmt, CoreMatchers.is(notNullValue()));
        JDate date = new JDate();
        date.setYear(2011);
        date.setMonth(12);
        date.setDay(31);
//	Wait until this date format is activated in French
        assertThat("Formatting", fmt.format(date), CoreMatchers.is("le 31 décembre 2011"));
//		assertThat("Formatting", fmt.format(date), CoreMatchers.is("31/12/2011"));
    }
}
