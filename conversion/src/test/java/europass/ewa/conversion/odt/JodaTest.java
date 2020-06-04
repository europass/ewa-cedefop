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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Locale;

import org.joda.time.LocalDate;
import org.joda.time.MonthDay;
import org.joda.time.YearMonth;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;

public class JodaTest {

    @Test
    public void writeYearMonth() {
        YearMonth ym = new YearMonth(2012, 7);
        assertThat(ym.toString(), is("2012-07"));
    }

    @Test
    public void writeMonthDay() {
        MonthDay md = new MonthDay(07, 12);
        assertThat(md.toString(), is("--07-12"));
    }

    @Test
    public void readYearMonth() {
        LocalDate partial = ISODateTimeFormat.yearMonth().parseLocalDate("2012-07");
        assertThat(partial.toString(), is("2012-07-01"));
    }

    /**
     * Commented out this irrelevant test which breaks in jdk 1.8. Previously it
     * gave "12 Ioulios" but with jdk 1.8 it gives "12 Iouliou" which is better
     * Greek.
     */
//	@Test
//	public void writeGreekMonthIsIncorrect() {
//		LocalDate date = new LocalDate(2012, 07, 12);
//		String str = DateTimeFormat.forStyle("L-").withLocale(new Locale("el")).print(date);
//		assertThat(str, is("12 Ιούλιος 2012"));
//	}
    @Test
    public void writeEnglishDateFirst() {
        LocalDate date = new LocalDate(2012, 07, 1);
        String str = DateTimeFormat.forStyle("L-").withLocale(Locale.ENGLISH).print(date);
        assertThat(str, is("July 1, 2012"));
    }
}
