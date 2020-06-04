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
package europass.ewa.tools.keygen.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class URLKeyGenerator implements TokenGenerator {

    /**
     * Default constructor.
     */
    public URLKeyGenerator() {
        super();
    }

    @Override
    public ArrayList<String> generateToken(List<?> input) {
        return generateToken(input, new TokenOptions());
    }

    @Override
    public ArrayList<String> generateToken(List<?> input, TokenOptions options) {

        if ((input == null || input.size() == 0 || input.isEmpty()) || (options == null || (options.getLength() <= 0))) {
            throw new IllegalArgumentException("TokenGenerator.generateToken: data is empty or no options are set.");
        }

        //parse,validate,Handle   options
        List<String> tokens = new ArrayList<>();

        try {
            String token = new String();

            for (Object element : input) {
                switch (options.algo) {

                    case MD5:
                    default:
                        //prepare the token by appending URL to system time
                        token = element.toString() + String.valueOf(System.currentTimeMillis());
                        token = computeMD5Token(token, options);
                        break;

                    case RANDOM:
                        token = computeRandomToken(element.toString(), options);
                        if (token.length() > options.getLength()) {
                            throw new IllegalArgumentException("TokenGenerator.generateToken: Generated token is too long. The token cannot be longer than" + options.getLength() + " bytes.\n content was: " + token);
                        }
                }
                tokens.add(token);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return (ArrayList<String>) tokens;
    }

    /**
     * computeMD5Token: computes the md5 token of a given string
	 *
     */
    private String computeMD5Token(String element, TokenOptions options) {

        MD5Generator md5gen = new MD5Generator(element);

        return md5gen.getMD5();
    }

    /**
     * computeRandomToken:
     *
     * @param element
	 *
     */
    private String computeRandomToken(String element, TokenOptions options) {
        final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        int tokenLength = options.getLength();

        StringBuilder vocabulary = new StringBuilder("abcdefghijklmnopqrstuvwxyz0123456789");
        vocabulary.append(options.getSpecialCharacters());
        vocabulary.append(options.isContainsUppercase() ? UPPERCASE : "");

        StringBuilder token = new StringBuilder();

        for (int index = 0; index < tokenLength; index++) {
            //		ThreadLocalRandom.current().setSeed(System.currentTimeMillis());
            int seed = ThreadLocalRandom.current().nextInt(vocabulary.length());
            token.append(vocabulary.charAt(seed));
        }

        return token.toString();
    }
}
