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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;

import europass.ewa.model.JDate;

public class JDateFieldTest {

    private String[] sm = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    private String[] lmWithDay = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    private String[] lmNoDay = new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
    private String[] daySuffix = new String[]{"st", "nd", "rd", "th", "th", "th", "th", "th", "th", "th", "th", "th", "th", "th", "th", "th", "th", "th", "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th", "th", "st"};

    private JDate date;

    private JDate date2;

    private JDate yearMonth;

    @Before
    public void prepareVariables() {
        date = new JDate();
        date.setYear(2012);
        date.setMonth(11);
        date.setDay(13);

        date2 = new JDate();
        date2.setYear(2012);
        date2.setMonth(07);
        date2.setDay(01);

        yearMonth = new JDate();
        yearMonth.setYear(2012);
        yearMonth.setMonth(07);
    }

    @Test
    public void d_OfDate() {
        JDateField field = new JDateField.Day(1);
        assertThat(field.format(date), is("13"));
    }

    @Test
    public void d_OfDate2() {
        JDateField field = new JDateField.Day(1);
        assertThat(field.format(date2), is("1"));
    }

    @Test
    public void dd_OfDate() {
        JDateField field = new JDateField.Day(2);
        assertThat(field.format(date), is("13"));
    }

    @Test
    public void dd_OfDate2() {
        JDateField field = new JDateField.Day(2);
        assertThat(field.format(date2), is("01"));
    }

    @Test
    public void dd_OfDate3_single() {
        JDateField field = new JDateField.Day(3, daySuffix);
        assertThat(field.format(date2), is("1st"));
    }

    @Test
    public void dd_OfDate3_double() {
        JDateField field = new JDateField.Day(3, daySuffix);
        assertThat(field.format(date), is("13th"));
    }

    @Test
    public void m_OfDate() {
        JDateField field = new JDateField.Month(1, sm, lmWithDay, lmNoDay, true);
        assertThat(field.format(date), is("11"));
    }

    @Test
    public void m_OfDate2() {
        JDateField field = new JDateField.Month(1, sm, lmWithDay, lmNoDay, true);
        assertThat(field.format(date2), is("7"));
    }

    @Test
    public void mm_OfDate() {
        JDateField field = new JDateField.Month(2, sm, lmWithDay, lmNoDay, true);
        assertThat(field.format(date), is("11"));
    }

    @Test
    public void mm_OfDate2() {
        JDateField field = new JDateField.Month(2, sm, lmWithDay, lmNoDay, true);
        assertThat(field.format(date2), is("07"));
    }

    @Test
    public void yy_OfDate() {
        JDateField field = new JDateField.Year(2);
        assertThat(field.format(date), is("12"));
    }

    @Test
    public void yyyy_OfDate() {
        JDateField field = new JDateField.Year(4);
        assertThat(field.format(date), is("2012"));
    }

    @Test
    public void d_ofYearMonth() {
        JDateField field = new JDateField.Day(1);
        assertThat(field.format(yearMonth), is(""));
    }

    @Test
    public void yearMonth() {
        JDateFormat fmt = new JDateFormat(Arrays.asList(
                new JDateField.Day(1),
                new JDateField.Text("/"),
                new JDateField.Month(1, sm, lmWithDay, lmNoDay, true),
                new JDateField.Text("/"),
                new JDateField.Year(2)
        ));
        StringBuilder sb = new StringBuilder();
        fmt.format(sb, yearMonth);
        assertThat(sb.toString(), CoreMatchers.is("7/12"));
    }

}
