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

/**
 * Token options.
 *
 * @author ndim
 */
public class TokenOptions {

    static final String DEFAULT_SPECIAL_CHARACTERS = "&%_$";
    static final int DEFAULT_LENGTH = 20;

    enum KeygenAlgorithm {
        RANDOM, MD5
    }

    KeygenAlgorithm algo;

    private String specialCharacters;
    private int length;
    private boolean containsUppercase;

    /**
     * Default constructor.
     */
    TokenOptions() {
        specialCharacters = DEFAULT_SPECIAL_CHARACTERS;
        length = DEFAULT_LENGTH;
        containsUppercase = true;
        algo = KeygenAlgorithm.MD5;
    }

    /**
     * Parametrized constructor.
     *
     * @param specialCharacters A string of special characters generated
     * (defaults is characters are &%_$)
     * @param length The token length (default is 20)
     * @param containsUppercase	(default is true)
     */
    TokenOptions(String specialCharacters, int length, boolean containsUppercase) {
        super();
        this.specialCharacters = specialCharacters;
        this.length = length;
        this.containsUppercase = containsUppercase;
    }

    /**
     * @return specialCharacters a string of special characters allowed
     */
    public String getSpecialCharacters() {
        return specialCharacters;
    }

    /**
     * @param specialCharacters a string of special characters allowed
     */
    public void setSpecialCharacters(String specialCharacters) {
        this.specialCharacters = specialCharacters;
    }

    /**
     * @return the length
     */
    public int getLength() {
        return length;
    }

    /**
     * @param length the length to set
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * @return the containsUppercase
     */
    public boolean isContainsUppercase() {
        return containsUppercase;
    }

    /**
     * @param containsUppercase the containsUppercase to set
     */
    public void setContainsUppercase(boolean containsUppercase) {
        this.containsUppercase = containsUppercase;
    }

}
