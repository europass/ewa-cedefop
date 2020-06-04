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
import java.util.Arrays;
import java.util.List;

import europass.ewa.model.Address;

public class AddressFormat extends Format<Address> {

    private static final int ADDRESS_FIELDS_SIZE = 13;
    static final List<Character> CHARS = Arrays.asList(new Character[]{'s', 'p', 'z', 'm', 'c', ' '});

    AddressFormat(List<AddressField> fields) {
        super(fields);
    }

    static boolean checkEmpty(String input) {
        String str = input;
        return str == null || (str != null && str.isEmpty());
    }

    static String replaceCharAt(String input, int pos, char c) {
        String s = input;
        return s.substring(0, pos) + c + s.substring(pos + 1);
    }

    static String adjust(String modifiedPattern, int l, int i) {
        return adjust(modifiedPattern, l, i, CHARS);
    }

    static String adjust(String pattern, int l, int i, List<Character> chars) {
        String modifiedPattern = pattern;

        if (modifiedPattern.charAt(i) == '#') {
            return modifiedPattern;
        }

        modifiedPattern = replaceCharAt(modifiedPattern, i, '#');

        if (i > 0 && i < l) {
            int prev = i - 1;
            char prevChar;
            while (prev >= 0) {
                prevChar = modifiedPattern.charAt(prev);
                if (prevChar != '#' && !CHARS.contains(prevChar)) {
                    modifiedPattern = replaceCharAt(modifiedPattern, prev, '#');
                    prev = prev - 1;
                    prevChar = modifiedPattern.charAt(prev);
                } else {
                    break;
                }
            }
        }
        if (i >= 0 && i < l) {
            int next = i + 1;
            char nextChar;
            while (next < l) {
                nextChar = modifiedPattern.charAt(next);
                if (nextChar != '#' && !CHARS.contains(nextChar)) {
                    modifiedPattern = replaceCharAt(modifiedPattern, next, '#');
                    next = next + 1;
                } else {
                    break;
                }
            }
        }
        return modifiedPattern;
    }

    static String adjustPattern(String pattern, Address address, String postalCountryCode) {
        String modifiedPattern = pattern;

        int l = pattern.length();

        boolean emptyZipCode = checkEmpty(address.getPostalCode());

        for (int i = 0; i < l; i++) {

            char c = pattern.charAt(i);

            switch (c) {
                case 's': {
                    if (checkEmpty(address.getAddressLine())) {
                        modifiedPattern = adjust(modifiedPattern, l, i);
                    }
                    break;
                }
                case 'z': {
                    if (emptyZipCode) {
                        modifiedPattern = adjust(modifiedPattern, l, i);
                    }
                    break;
                }
                case 'm': {
                    if (checkEmpty(address.getMunicipality())) {
                        modifiedPattern = adjust(modifiedPattern, l, i);
                    }
                    break;
                }
                case 'c': {
                    if (address.getCountry() == null || address.getCountry() != null && checkEmpty(address.getCountry().getLabel())) {
                        modifiedPattern = adjust(modifiedPattern, l, i);
                    }
                    break;
                }
                case 'p': {
                    if (checkEmpty(postalCountryCode)) {
                        modifiedPattern = adjust(modifiedPattern, l, i);
                    }
                    break;
                }
                default:
                    break;
            }
        }
        if (emptyZipCode) {
            modifiedPattern = modifiedPattern.replaceAll("p", "#");
        }
        modifiedPattern = modifiedPattern.replaceAll("#", "");

        // EWA-1609: fix for removing extra commas - the pattern applies to formats like ", (" so we need to remove the comma before the left parenthesis
        modifiedPattern = modifiedPattern.replaceAll("\\,( )+\\(", " \\(");

        return modifiedPattern = modifiedPattern.trim();
    }

    public static AddressFormat compile(String pattern, Address address, String postalCountryCode) {
        if (pattern.startsWith("text/") || pattern.startsWith("numeric/")) {
            pattern = Address.DEFAULT_ADDRESS_FORMAT;
        }
        String modifiedPattern = adjustPattern(pattern, address, postalCountryCode);

        List<AddressField> fields = new ArrayList<AddressField>(ADDRESS_FIELDS_SIZE);

        int l = modifiedPattern.length();
        TextBuilder text = new TextBuilder(fields);

        for (int i = 0; i < l; i++) {

            char c = modifiedPattern.charAt(i);

            switch (c) {
                case 's':
                    text.check();
                    fields.add(new AddressField.Street());
                    break;
                case 'z':
                    text.check();
                    fields.add(new AddressField.PostalCode());
                    break;
                case 'm':
                    text.check();
                    fields.add(new AddressField.Municipality());
                    break;
                case 'c':
                    text.check();
                    fields.add(new AddressField.Country());
                    break;
                case 'p':
                    text.check();
                    fields.add(new AddressField.Text(postalCountryCode));
                    break;
                case '\n':
                    text.check();
                    fields.add(new AddressField.NewLine());
                    break;
                default:
                    text.push(c);
            }
        }
        //Trailing text
        text.check();
        return new AddressFormat(fields);
    }

    private static final class TextBuilder {

        private final List<AddressField> fields;

        private TextBuilder(List<AddressField> fields) {
            this.fields = fields;
        }

        private StringBuilder text = null;

        public boolean check() {
            if (text != null) {
                fields.add(new AddressField.Text(text.toString()));
                text = null;
                return true;
            }

            return false;
        }

        public TextBuilder push(char c) {
            if (text == null) {
                text = new StringBuilder();
            }
            text.append(c);
            return this;
        }
    }
}
