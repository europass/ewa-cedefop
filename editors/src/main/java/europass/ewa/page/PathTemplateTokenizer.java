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
package europass.ewa.page;

import java.text.ParseException;

public class PathTemplateTokenizer {

    public enum TokenType {
        TOKEN(""),
        TEXT(""),
        PATH_ELEMENT("/"),
        EXTENSION_ELEMENT("."),
        OPTIONAL_ELEMENT_BEGIN("["),
        OPTIONAL_ELEMENT_END("]");

        private String defaultValue;

        TokenType(String defaultValue) {
            this.defaultValue = defaultValue;
        }

        public String defaultValue() {
            return defaultValue;
        }
    };

    private int L = 0;

    private final PathTemplateParser parser;

    public PathTemplateTokenizer(PathTemplateParser listener) {
        this.parser = listener;
    }

    void parse(CharSequence template) throws ParseException {
        L = template.length();
        int index = 0;
        while (index < L) {
            index = next(template, index);
        }
    }

    private int next(CharSequence template, int idx) throws ParseException {
        int index = idx;
        char c = template.charAt(index);
        switch (c) {
            case '[': {
                return optionalElement(template, ++index);
            }
            case '/': {
                return pathElement(template, index);
            }
            case '.': {
                return extensionElement(template, index);
            }
            case ';': {
                return staticText(template, index);
            }
            default: {
                throw new ParseException(String.format("Unsupported character '%s' at %d", c, index), index);
            }
        }

    }

    private int optionalElement(CharSequence template, int idx) throws ParseException {
        int index = idx;
        parser.parse(TokenType.OPTIONAL_ELEMENT_BEGIN, "[", index);
        index = next(template, index);
        if (index < L) {
            int c = template.charAt(index);
            if (c != ']') {
                throw new ParseException("Unterminated optional extension", index);
            }
            parser.parse(TokenType.OPTIONAL_ELEMENT_END, "]", index);
            return ++index;
        } else {
            throw new ParseException("Unterminated optional extension", index);
        }
    }

    private int pathElement(CharSequence template, int index) throws ParseException {
        parser.parse(TokenType.PATH_ELEMENT, "/", index);
        char c = template.charAt(index);
        if (c != '/') {
            throw new ParseException("path element must start with a / character", index);
        }
        return token(template, ++index);
    }

    private int token(CharSequence template, int idx) throws ParseException {
        int index = idx;
        char c = template.charAt(index);
        if (c != '{') {
            throw new ParseException("token must start with a { character", index);
        }
        StringBuilder token = new StringBuilder();
        while (index < L) {
            index++;
            c = template.charAt(index);
            if (c != '}') {
                token.append(c);
            } else {
                parser.parse(TokenType.TOKEN, token.toString(), index);
                return ++index;
            }
        }
        throw new ParseException("unterminated token", index);
    }

    private int extensionElement(CharSequence template, int index) throws ParseException {
        char c = template.charAt(index);
        if (c != '.') {
            throw new ParseException("Extension must start with .", index);
        }
        parser.parse(TokenType.EXTENSION_ELEMENT, ".", index);
        ++index;
        c = template.charAt(index);
        if (c == '{') {
            return token(template, index);
        } else {
            return staticText(template, index);
        }
    }

    private int staticText(CharSequence template, int index) throws ParseException {
        StringBuilder text = new StringBuilder();
        while (index < L) {
            char c = template.charAt(index);
            if (c != '/' && c != '.' && c != '{' && c != ']') {
                text.append(c);
            } else {
                parser.parse(TokenType.TEXT, text.toString(), index);
                if (c == '{') {
                    return token(template, index);
                } else {
                    return index;
                }
            }
            index++;
        }

        //We've reached the end of the template. Report the static text and return.
        parser.parse(TokenType.TEXT, text.toString(), index);
        return index;
    }

}
