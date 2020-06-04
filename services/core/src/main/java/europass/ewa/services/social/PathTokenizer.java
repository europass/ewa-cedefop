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
package europass.ewa.services.social;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.ParseException;

import com.google.common.base.Strings;

import europass.ewa.services.social.Token.TokenFactory;

public class PathTokenizer {

    private final List<Token> tokens;

    protected PathTokenizer(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Token> tokens() {
        return tokens;
    }

    public Token resolve(Object root, Object current) {
        return this.resolve(root, current, null);
    }

    public Token resolve(Object root, Object current, Token prevToken) {

        Object nextCurrent = current;

        for (Token token : tokens) {
            prevToken = token.resolve(root, nextCurrent, prevToken);
            nextCurrent = prevToken.getObj();
        }

        return prevToken;
    }

    public static PathTokenizer compile(String path) throws ParseException {
        return new Compiler().compile(path);
    }

    /**
     * ***********************************************************
     */
    static class Compiler {

        public PathTokenizer compile(String path) throws ParseException {
            return new PathTokenizer(compileTokens(path));
        }

        private int pos = 0;
        private int pl = 0;

        List<Token> compileTokens(String path) throws ParseException {

            ArrayList<Token> tokens = new ArrayList<>();

            pl = path.length();
            pos = 0;

            if (Strings.isNullOrEmpty(path)) {
                //Path is the empty string. This mean to return the current object.
                tokens.add(TokenFactory.getCurrent());
            }
            //Non empty
            while (pos < pl) {
                char c = path.charAt(pos);
                switch (c) {
                    case Token.HERE: {
                        tokens.add(TokenFactory.getCurrent());
                        //Next char, if any... can only by "/"
                        consumeHere(path);
                        break;
                    }
                    case Token.ROOT: {
                        tokens.add(TokenFactory.getRoot());
                        break;
                    }
                    default: {
                        if (pos == 0) {
                            //random text without . or /, so add a current token
                            tokens.add(TokenFactory.getCurrent());
                        }
                        tokens.add(new Token(consumeText(path)));
                        break;
                    }
                }
                pos++;

            }
            return tokens;
        }

        private void consumeHere(String path) throws ParseException {
            int posLookup = pos + 1;
            if (posLookup < pl) {
                char c = path.charAt(posLookup);
                switch (c) {
                    case Token.DASH:
                        pos++;
                        break;
                    default:
                        throw new ParseException("A \".\" token cannot be followed by a char other than '/'");
                }
            }
        }

        private String consumeText(String path) {
            StringBuilder text = new StringBuilder();
            while (pos < pl) {
                char c = path.charAt(pos);
                switch (c) {
                    case Token.DASH:
                        return text.toString();
                    default:
                        text.append(c);
                        pos++;
                }
            }
            return text.toString();
        }

    }

}
