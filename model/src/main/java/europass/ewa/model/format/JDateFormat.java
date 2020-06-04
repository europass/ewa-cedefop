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

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Strings;

import europass.ewa.model.JDate;
import europass.ewa.model.format.JDateField.Day;
import europass.ewa.model.format.JDateField.Month;
import europass.ewa.model.format.JDateField.Text;
import europass.ewa.model.format.JDateField.Year;

public class JDateFormat extends Format<JDate> {

    public static final String FALLBACK_PATTERN = "dd/MM/yyyy";

    JDateFormat(List<JDateField> fields) {
        super(fields);
    }

    public static JDateFormat compile(String pattern, String[] nameShort, String[] nameWithDay, String[] nameNoDay, String[] daySuffix) {
        String datePattern = Strings.isNullOrEmpty(pattern) ? FALLBACK_PATTERN : pattern;
        return new Compiler(nameShort, nameWithDay, nameNoDay, daySuffix).compile(datePattern);
    }

    static class Compiler {

        private static final int INIT_FIELDS_ARR_LENGTH = 6;
        private final String[] nameShort;
        private final String[] nameWithDay;
        private final String[] nameNoDay;
        private final String[] daySuffix;

        public Compiler(String[] nameShort, String[] nameWithDay,
                String[] nameNoDay, String[] daySuffix) {
            super();
            this.nameShort = nameShort.clone();
            this.nameWithDay = nameWithDay.clone();
            this.nameNoDay = nameNoDay.clone();
            this.daySuffix = daySuffix.clone();
        }

        private int pl = 0;
        private int pos = 0;
        private boolean hasDayField = false;

        public JDateFormat compile(String pattern) {
            return new JDateFormat(compileFields(pattern));
        }

        List<JDateField> compileFields(String pattern) {
            hasDayField = (pattern.indexOf(Day.SYMBOL) >= 0);
            ArrayList<JDateField> fields = new ArrayList<JDateField>(INIT_FIELDS_ARR_LENGTH);
            pl = pattern.length();
            pos = 0;
            while (pos < pl) {
                char c = pattern.charAt(pos);
                switch (c) {
                    case Day.SYMBOL: {
                        fields.add(new Day(consumeField(Day.SYMBOL, pattern), daySuffix));
                        break;
                    }
                    case Month.SYMBOL: {
                        fields.add(new Month(consumeField(Month.SYMBOL, pattern), nameShort, nameWithDay, nameNoDay, hasDayField));
                        break;
                    }
                    case Year.SYMBOL: {
                        fields.add(new Year(consumeField(Year.SYMBOL, pattern)));
                        break;
                    }
                    case Text.SQUOTE: {
                        fields.add(new Text(consumeQuotedText(pattern)));
                        break;
                    }
                    default: {
                        fields.add(new Text(consumeText(pattern)));
                        break;
                    }
                }
            }
            return fields;
        }

        private int consumeField(char symbol, String pattern) {
            int length = 0;
            while (pos < pl) {
                char c = pattern.charAt(pos);
                if (c != symbol) {
                    return length;
                } else {
                    pos++;
                    length++;
                }
            }
            return length;
        }

        private String consumeQuotedText(String pattern) {
            pos++; //skip the quote

            if (pattern.charAt(pos) == Text.SQUOTE) {
                return "'";
            }

            StringBuilder text = new StringBuilder();
            while (pos < pl) {
                char c = pattern.charAt(pos);
                switch (c) {
                    case Text.SQUOTE:
                        //Check next char
                        pos++;
                        if (pattern.charAt(pos) == Text.SQUOTE) {
                            text.append(c);
                            pos++;
                        } else {
                            return text.toString();
                        }
                        break;
                    default:
                        text.append(c);
                        pos++;
                }
            }
            return text.toString();
        }

        private String consumeText(String pattern) {
            StringBuilder text = new StringBuilder();
            while (pos < pl) {
                char c = pattern.charAt(pos);
                switch (c) {
                    case Day.SYMBOL:
                    case Month.SYMBOL:
                    case Year.SYMBOL:
                        /*case Text.SQUOTE: */
                        return text.toString();
                    case Text.SQUOTE:
                        //Check next char
                        if (pattern.charAt(pos + 1) == Text.SQUOTE) {
                            text.append(c);
                            pos++;
                            pos++;
                        } else {
                            //We are the beginning of a quoted string
                            return text.toString();
                        }
                        break;
                    default:
                        text.append(c);
                        pos++;
                }
            }
            return text.toString();
        }

    }

}
