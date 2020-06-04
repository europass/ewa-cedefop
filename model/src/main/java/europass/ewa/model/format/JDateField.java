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

import static java.lang.Integer.parseInt;

import com.google.common.base.Strings;

import europass.ewa.model.JDate;

abstract class JDateField extends Field<JDate> {

    protected int length;

    protected JDateField(int length) {
        this.length = length;
    }

    static final class Year extends JDateField {

        static final char SYMBOL = 'y';

        Year(int length) {
            super(length);
        }

        @Override
        boolean format(StringBuilder into, JDate date) {
            Integer iYear = date.getYear();
            if (iYear == null || iYear < 1) {
                return false;
            }
            String year = iYear.toString();

            int l = year.length();
            if (l <= length) {
                year = Strings.padStart(year, length, '0');
            } else {
                year = year.substring(l - length, l);
            }
            into.append(year);
            return true;
        }
    }

    static final class Month extends JDateField {

        static final char SYMBOL = 'M';

        private final String[] shortName;
        private final String[] nameWithDay;
        private final String[] nameNoDay;
        private final boolean hasDayField;

        Month(int length, String[] shortName, String[] nameWithDay, String[] nameNoDay, boolean hasDayField) {
            super(length);
            this.shortName = shortName.clone();
            this.nameWithDay = nameWithDay.clone();
            this.nameNoDay = nameNoDay.clone();
            this.hasDayField = hasDayField;
        }

        @Override
        boolean format(StringBuilder into, JDate date) {
            Integer iMonth = date.getMonth();
            if (iMonth == null || iMonth < 1) {
                return false;
            }
            String month = iMonth.toString();

            String[] longNames = nameNoDay;
            if (hasDayField) {
                Integer iDate = date.getDay();
                if (iDate != null && iDate > 0) {
                    longNames = nameWithDay;
                }
            }

            switch (length) {
                case 1:
                    into.append(month);
                    return true;
                case 2:
                    into.append(Strings.padStart(month, 2, '0'));
                    return true;
                case 3:
                    into.append(shortName[parseInt(month) - 1]);
                    return true;
                default:
                    into.append(longNames[parseInt(month) - 1]);
                    return true;
            }
        }
    }

    static final class Day extends JDateField {

        static final char SYMBOL = 'd';

        private final String[] daySuffix;

        Day(int length) {
            //EWA-916: super(Math.min(length,2));
            super(length);
            this.daySuffix = new String[0];
        }

        Day(int length, String[] daySuffix) {
            //EWA-916: super(Math.min(length,2));
            super(length);
            this.daySuffix = daySuffix;
        }

        @Override
        boolean format(StringBuilder into, JDate date) {
            Integer iDay = date.getDay();
            if (iDay == null || iDay < 1) {
                return false;
            }

            String day = iDay.toString();

            switch (length) {
                case 2: {
                    day = Strings.padStart(day, 2, '0');
                    break;
                }
                case 3: {
                    if (daySuffix.length > 0) {
                        String suffix = daySuffix[iDay - 1];
                        day = day + (suffix != null ? suffix : "");
                    }
                }
            }
            into.append(day);

            return true;
        }
    }

    static final class Text extends JDateField {

        static final char SQUOTE = '\'';

        protected final String text;

        Text(String text) {
            super(text.length());
            this.text = text;
        }

        @Override
        boolean format(StringBuilder into, boolean previous, JDate date) {
            return previous && format(into, date);
        }

        @Override
        boolean format(StringBuilder into, JDate date) {
            into.append(text);
            return true;
        }
    }
}
