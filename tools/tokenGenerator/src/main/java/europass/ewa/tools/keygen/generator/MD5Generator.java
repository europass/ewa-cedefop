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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Generator {

    private String text = null;
    private String hash = null;

    private MD5Generator() {
    }

    /**
     * @param text
     */
    public MD5Generator(String text) {
        this.text = text;
    }

    /**
     * @return
     */
    public String getMD5() {
        return getMD5(text);
    }

    /**
     * @param text
     * @return
     */
    public String getMD5(String text) {
        this.text = text;

        if (text == null) {
            return null;
        } else {
            return makeMD5();
        }
    }

    /**
     * @return
     */
    private String makeMD5() {
        MessageDigest md = null;
        byte[] encryptMsg = null;

        try {
            md = MessageDigest.getInstance("MD5");		// getting a 'MD5-Instance'
            encryptMsg = md.digest(text.getBytes());		// solving the MD5-Hash
        } catch (NoSuchAlgorithmException e) {
            System.out.println("No Such Algorithm Exception!");
        }

        String swap = "";										// swap-string for the result
        String byteStr = "";									// swap-string for current hex-value of byte
        StringBuffer strBuf = new StringBuffer();

        for (int i = 0; i <= encryptMsg.length - 1; i++) {

            byteStr = Integer.toHexString(encryptMsg[i]);	// swap-string for current hex-value of byte

            switch (byteStr.length()) {
                case 1:											// if hex-number length is 1, add a '0' before
                    swap = "0" + Integer.toHexString(encryptMsg[i]);
                    break;

                case 2:											// correct hex-letter
                    swap = Integer.toHexString(encryptMsg[i]);
                    break;

                case 8:											// get the correct substring
                    swap = (Integer.toHexString(encryptMsg[i])).substring(6, 8);
                    break;
            }
            strBuf.append(swap);							// appending swap to get complete hash-key
        }
        hash = strBuf.toString();							// String with the MD5-Hash

        return hash;										// returns the MD5-Hash
    }

    /**
     * @param text The text to set.
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * @return Returns the text.
     */
    public String getText() {
        return text;
    }

    /**
     * @return Returns the hash.
     */
    public String getHash() {
        return hash;
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        MD5Generator test = new MD5Generator("Test");			// Testen der Umwandlung des Strings 'Test'
        System.out.println(test.getMD5());
    }
}
