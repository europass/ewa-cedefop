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
package europass.ewa.services.statistics.parser.hsqlconstruct;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.joda.time.DateTime;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author dev-at
 */
public class DateValueFieldResolverTest {

    public DateValueFieldResolverTest() {
    }

    @Test
    public void testMakeDateRangeWhereClause() throws NoSuchMethodException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        //Using reflection to test private method
        Method method = DateValueFieldResolver.class.getDeclaredMethod("makeDateRangeWhereClause", new Class[]{String.class, String.class, String.class, DateTime.class, DateTime.class});
        method.setAccessible(true);

        //date=2015-2016
        DateTime dateFrom = new DateTime().withYear(2015);
        DateTime dateTo = new DateTime().withYear(2016).monthOfYear().withMaximumValue().dayOfMonth().withMaximumValue();
        assertEquals("e.year_no >= 2015 and e.year_no <= 2016",
                (String) method.invoke(new DateValueFieldResolverTest(), "e.year_no", "", "", dateFrom, dateTo));

        //date=2015.01-2016.12
        dateFrom = new DateTime().withYear(2015).withMonthOfYear(01).dayOfMonth().withMinimumValue();
        dateTo = new DateTime().withYear(2016).withMonthOfYear(12).dayOfMonth().withMaximumValue();
        assertEquals("(e.year_no > 2015 or (e.year_no = 2015 and e.month_no >= 1)) and (e.year_no < 2016 or (e.year_no = 2016 and e.month_no <= 12))",
                (String) method.invoke(new DateValueFieldResolverTest(), "e.year_no", "e.month_no", "", dateFrom, dateTo));

        //date=2015.01.01-2016.12.31
        dateFrom = new DateTime().withYear(2015).withMonthOfYear(06).withDayOfMonth(1);
        dateTo = new DateTime().withYear(2016).withMonthOfYear(06).withDayOfMonth(30);
        assertEquals("(e.year_no > 2015 or (e.year_no = 2015 and (e.month_no > 6 or (e.month_no = 6 and e.day_no >= 1)))) and (e.year_no < 2016 or (e.year_no = 2016 and (e.month_no < 6 or (e.month_no = 6 and e.day_no <= 30))))",
                (String) method.invoke(new DateValueFieldResolverTest(), "e.year_no", "e.month_no", "e.day_no", dateFrom, dateTo));

        //date=2015-2015
        dateFrom = new DateTime().withYear(2015);
        dateTo = new DateTime().withYear(2015).monthOfYear().withMaximumValue().dayOfMonth().withMaximumValue();
        assertEquals("e.year_no = 2015",
                (String) method.invoke(new DateValueFieldResolverTest(), "e.year_no", "", "", dateFrom, dateTo));

        //date=2015.01-2015.12
        dateFrom = new DateTime().withYear(2015).withMonthOfYear(01).dayOfMonth().withMinimumValue();
        dateTo = new DateTime().withYear(2015).withMonthOfYear(12).dayOfMonth().withMaximumValue();
        assertEquals("e.year_no = 2015 and (e.month_no >= 1 and e.month_no <= 12)",
                (String) method.invoke(new DateValueFieldResolverTest(), "e.year_no", "e.month_no", "", dateFrom, dateTo));

        //date=2015.01.01-2015.01.15
        dateFrom = new DateTime().withYear(2015).withMonthOfYear(01).withDayOfMonth(1);
        dateTo = new DateTime().withYear(2015).withMonthOfYear(12).withDayOfMonth(15);
        assertEquals("e.year_no = 2015 and ((e.month_no > 1 or (e.month_no = 1 and e.day_no >= 1)) and (e.month_no < 12 or (e.month_no = 12 and e.day_no <= 15)))",
                (String) method.invoke(new DateValueFieldResolverTest(), "e.year_no", "e.month_no", "e.day_no", dateFrom, dateTo));
    }

}
