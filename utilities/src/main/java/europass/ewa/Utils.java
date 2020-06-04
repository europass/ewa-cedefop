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
package europass.ewa;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import net.sf.uadetector.OperatingSystemFamily;
import net.sf.uadetector.UserAgentFamily;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Utils {

    private static final Logger LOG = LoggerFactory.getLogger(Utils.class);

    //Suppress default constructor for noninstantiability
    private Utils() {
        throw new AssertionError();
    }

    public static float toInches(float v) {
        return v / 72.00f;
    }

    public static String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }

    /**
     * Get the status of the bit bit of the int value.
     *
     * @param value
     * @param bit
     * @return
     */
    public static boolean isBitOn(int value, int bit) {
//		String str = Integer.toBinaryString( value );
//		if ( str.length() < bit )
//			return false;
//		char charAtBit = str.charAt( str.length() - bit );
//		return charAtBit == '1';
        return getBit(value, bit) == 1;
//		return (value & power(2, bit)) != 0 ;
    }

    /*
	 *  n
		100010101011101010 (example)
		n >> 5
		000001000101010111 (all bits are moved over 5 spots, therefore
		&				   the bit you want is at the end)
		000000000000000001 (0 means it will always be 0,
		=				   1 means that it will keep the old value)
		1 
     */
    public static int getBit(int n, int k) {
        return (n >> k) & 1;
    }

    public static int power(int a, int b) {
        int power = 1;
        for (int c = 0; c < b; c++) {
            power *= a;
        }
        return power;
    }

    /**
     * Removes punctuation marks from a string, and joins using title-case all
     * words in the string
     *
     * @param surname : The string to be prepared
     * @return the prepared string
     */
    public static String removePunctuation(String str) {
        String surname = str;

        // replace punctuation
        surname = surname.replaceAll("[\\p{Punct}]", " ").trim();
        // Punctuation:One of!"#$%&'()*+,-./:;<=>?@[\]^_`{|}~
        // Case more than one names convert to Title Case
        surname = WordUtils.capitalizeFully(surname);

        surname = surname.replaceAll("\\s", "");

        return surname;
    }

    /**
     * Prepares a string removing all punctuation marks and converts to
     * url-encode with specific encoding To be used for the download file as
     * part of the filename
     *
     * @param input The string to be encoded
     * @param enc A string with the desired encoding
     * @return
     */
    public static String urlEncode(String input, String enc) {
        String str = input;

        if (str == null || str.isEmpty()) {
            return str;
        }

        String encoding = enc;
        if (encoding == null || encoding.isEmpty()) {
            encoding = Constants.UTF8_ENCODING;
        }

        try {
            return URLEncoder.encode(str, encoding);
        } catch (UnsupportedEncodingException e) {
            return str;
        }
    }

    public static String urlEncode(String str) {
        return urlEncode(str, Constants.UTF8_ENCODING);
    }

    /**
     * Url decodes a string
     *
     * @param str
     * @return
     */
    public static String urlDecode(String str) {
        String decodedStr = null;
        try {
            if (isNullOrEmpty(str)) {
                return str;
            }

            decodedStr = URLDecoder.decode(str, Constants.UTF8_ENCODING);

            //There are cases we end up in this method but the str is not url-encoded.
            //To check whether the str was or not url-encoded compare str and decodedStr lengths. 
            //If the original string is larger than the decoded string, then the original was encoded.
            if (str.length() == decodedStr.length()) {
                decodedStr = str;
            }

        } catch (UnsupportedEncodingException ee) {
            LOG.error("Utils:urlDecode - Failed to decode string (UnsupportedEncodingException)", ee);
            return str;
        } catch (IllegalArgumentException ie) {
            LOG.error("Utils:urlDecode - Failed to decode string due to invalid input character", ie);
            return str;
        }
        return decodedStr;
    }

    /**
     * Reads a string as UTD8 from Eight Bit
     *
     * @param str
     * @return
     */
    public static String readAsUTF8(String str) {
        String newStr = null;
        if (isNullOrEmpty(str)) {
            return "";
        }
        try {
            byte[] bytes = str.getBytes(Constants.EIGHT_BIT);
            newStr = new String(bytes, Constants.UTF8_ENCODING);
        } catch (UnsupportedEncodingException ee) {
            LOG.error("Utils:urlDecode - Failed to decode string (UnsupportedEncodingException)", ee);
            return str;
        }
        return newStr;
    }

    /**
     * Prepare the Content Disposition header according to the browser
     *
     * @param agentFamily
     * @param agentOSFamily
     * @param fileName
     * @return
     */
    public static String getContentDisposition(UserAgentFamily agentFamily, OperatingSystemFamily agentOSFamily, String fileName, boolean preview) {
//		String defaultValue = "attachment; filename=\"" + urlEncode(fileName) + "\";";

        String contentDispositionType = "attachment";
        String contentDispositionFilename = "filename=\"" + urlEncode(fileName) + "\";";

        if (agentFamily == null) {
            return contentDispositionType + "; " + contentDispositionFilename;
        }

        // EXCEPTION FOR iOS OPERATING SYSTEM AND PREVIEW OF CV
        if (OperatingSystemFamily.IOS.equals(agentOSFamily) || preview) {
            contentDispositionType = "inline";
        }

        switch (agentFamily) {
            case FIREFOX: { // differentiate by browser
                return contentDispositionType + ";filename*=\"" + urlEncode(fileName) + "\";";
            }
            case SAFARI: {
                // fail (displays as UTF-8)
                return contentDispositionType + "; filename=\"" + fileName + "\""; // displays
                // Europass-CV-20120928-Foo%CE%A6%CE%BF%CE%BF-EN.pdf
                // for surname "foo φοο"
                // according to the RFC6266 doc test page this should work BUT IT
                // DOES NOT
                // http://greenbytes.de/tech/tc2231/#attwithasciifilename
                // attwithisofnplain
                // Content-Disposition: attachment; filename="foo-ä.html"
                // 'attachment', specifying a filename of foo-ä.html, using plain
                // ISO-8859-1
                // or
                // attwithutf8fnplain
                // Content-Disposition: attachment; filename="foo-Ã¤.html"
                // 'attachment', specifying a filename of foo-Ã¤.html, which happens
                // to be foo-ä.html using UTF-8 encoding.
                // UA should offer to download the resource as "foo-Ã¤.html".
                // Displaying "foo-ä.html" instead indicates that the UA tried to be
                // smart by detecting something that happens to look like UTF-8.
                // [vpol:BETTER THAN NOTHING]

                // value = "attachment; filename*=UTF-8''" + fileName ; //NOT
                // Supported saf5 : displays pdf.pdf
                // value = "attachment; filename*=iso-8859-1''" + fileName ; //NOT
                // Supported saf5: displays pdf.pdf
            }
            default: { // will work for all the rest browsers
                return contentDispositionType + "; filename=" + urlEncode(fileName);
            }
        }
    }

    private static boolean isNullOrEmpty(String str) {
        return ((str == null) || (str != null && "".equals(str)));
    }

    private static final String[] EMPTY_STR = {};

    public static String[] splitAtSlash(String str) {
        if (isNullOrEmpty(str)) {
            return EMPTY_STR;
        }
        return str.split("/");
    }

    /**
     * **************************
     */
    private static final Set<Class<?>> WRAPPER_TYPES = getWrapperTypes();

    public static boolean isLeafType(Class<?> clazz) {
        return (clazz.isPrimitive() || isWrapperType(clazz) || clazz.equals(String.class) || clazz.equals(Boolean.class));
    }

    public static boolean isWrapperType(Class<?> clazz) {
        return WRAPPER_TYPES.contains(clazz);
    }

    private static Set<Class<?>> getWrapperTypes() {
        Set<Class<?>> ret = new HashSet<Class<?>>();
        ret.add(Boolean.class);
        ret.add(Character.class);
        ret.add(Byte.class);
        ret.add(Short.class);
        ret.add(Integer.class);
        ret.add(Long.class);
        ret.add(Float.class);
        ret.add(Double.class);
        ret.add(Void.class);
        return ret;
    }

    /**
     * Will load a file based on the resourcePath from the given loader.
     *
     * @param loader
     * @param resourcePath
     * @param encoding
     * @return
     * @throws IOException
     */
    public static String readResourceAsString(ClassLoader loader, String resourcePath, String encoding) throws IOException {
        if (resourcePath == null) {
            throw new IllegalArgumentException("'resourcePath' argument can't be null");
        }
        if (loader == null) {
            throw new IllegalArgumentException("'classLoader' argument can't be null");
        }
        String name = resourcePath;
        if (name.startsWith("/")) {
            name = resourcePath.substring(1);
        }
        InputStream input = loader.getResourceAsStream(name);
        StringWriter writer = new StringWriter();
        IOUtils.copy(input, writer, encoding);
        return writer.toString();
    }

    public static File writeInputStreamToFile(InputStream in, String path) {

        OutputStream outputStream = null;
        File outputFile = new File(path);

        try {

            outputStream = new FileOutputStream(path);

            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = in.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (outputStream != null) {
                try {
                    // outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        return outputFile;
    }

    public static String readResourceAsString(ClassLoader loader, String resourceName) throws IOException {
        return readResourceAsString(loader, resourceName, "UTF-8");
    }

    public static String getRelativePath(File file, File folder) {
        String filePath = file.getAbsolutePath();
        String folderPath = folder.getAbsolutePath();
        if (filePath.startsWith(folderPath)) {
            return filePath.substring(folderPath.length() + 1);
        } else {
            return null;
        }
    }
}
