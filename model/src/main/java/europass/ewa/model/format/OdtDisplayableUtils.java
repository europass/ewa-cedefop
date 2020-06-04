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

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.text.Normalizer;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.validator.routines.RegexValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import europass.ewa.enums.EuropassDocumentType;

public class OdtDisplayableUtils {

    private static final Logger LOG = LoggerFactory.getLogger(OdtDisplayableUtils.class);

    private static final String HTML_ROOT_WRAPPER_START = "<div class=\"dummy-root\">";
    private static final String HTML_ROOT_WRAPPER_END = "</div>";
    private static final String HTML_ROOT_WRAPPER_EXTRA_CLASS_REGEXP = "\"(dummy-root)\"";

    private OdtDisplayableUtils() {
        throw new AssertionError();
    }

    /**
     * Transforms a valid html to open document xml. Requires that the html is
     * well formatted and that there exists one root element.
     *
     * @return open document compatible text
     */
    public static String richtext(Transformer transformer, String str, EuropassDocumentType docType, String textStyle) {
        if (transformer == null) {
            return "";
        }
        if (!Strings.isNullOrEmpty(str)) {
            try {

                //HTML wrap
                String html = prepareHtmlWrap(str, docType);
                //Transform - Attention use UTF-8 when getting the bytes!
                Source text = new StreamSource(new ByteArrayInputStream(html.getBytes("UTF-8")));
                StringWriter writer = new StringWriter();
                Result result = new StreamResult(writer);

                synchronized (transformer) {
                    transformer.transform(text, result);
                }

                String richText = writer.toString();

                //Replace style only for text:p
                if (!Strings.isNullOrEmpty(textStyle)) {
                    richText = richText.replaceAll(
                            "<text:p xmlns:text=\"(([a-zA-Z]|:|\\.|\\d)+){1}\" text:style-name=\"([a-zA-Z]|_|\\d)+\"",
                            "<text:p xmlns:text=\"$1\" text:style-name=\"" + textStyle + "\"");

                    richText = richText.replaceAll(
                            "<text:list-item><text:p text:style-name=\"(([a-zA-Z]|_|[0-9])+){1}\"><text:span>",
                            "<text:list-item><text:p text:style-name=\"" + textStyle + "\"><text:span text:style-name=\"$1\">");
                }

                return richText;

            } catch (Exception e) {
                LOG.error("Failed to transform HTML to ODT ", e);
                return "";
            }
        }
        return "";
    }

    protected static String prepareHtmlWrap(String str, EuropassDocumentType docType) {
        //1. NOT HTML TEXT IS USED!
        String html = str;
//		html = html.replaceAll("&", "&amp;");//vpol removed because of EWA-1372-Escaped HTML characters appear verbatim in generated document 
//		html = StringEscapeUtils.escapeXml(html);//this was added by pgia for fix on EWA-1304, but later commented 

        //2. Wrap if the given String if not already wrapped...
        if (!html.startsWith(HTML_ROOT_WRAPPER_START)) {
            //wrap...
            html = HTML_ROOT_WRAPPER_START + html + HTML_ROOT_WRAPPER_END;
        }
        //3. Add extra class - if a docType is provided
        if (docType != null && !EuropassDocumentType.UNKNOWN.equals(docType)) {
            html = html.replaceAll(HTML_ROOT_WRAPPER_EXTRA_CLASS_REGEXP, "\"$1 " + docType.getAcronym() + "\"");
        }

        return html;
    }

    /**
     * Shows are list of booleans starting from the show in question and moving
     * backwards from the closest to the farthest show.
     *
     * E.g. Nationality - > Birthdate -> Sex
     *
     * @param shows
     * @return
     */
    public static boolean printPipe(boolean... shows) {
        //the first show
        boolean firstShow = shows[0];

        for (int i = 1; (firstShow == true && i < shows.length); i++) {
            boolean show = shows[i];
            if (show) {
                //ok the immediate previous is showable, so show pipe!
                return true;
            }
        }
        return false;
    }

    /**
     * Escape text in order to be included in the XML
     *
     * @param txt
     * @return
     */
    public static String escapeForXml(String txt) {
        return StringEscapeUtils.escapeXml10(txt);
    }

    /**
     * Replace all new line characters with HTML new line
     *
     * @param str
     * @return
     */
    public static String escapeNewLineCharacters(String str) {

        if (Strings.isNullOrEmpty(str)) {
            return str;
        }

        return str.replaceAll("\n", "<br/>");
    }

    /**
     * Converts a string to uppercase, after having removed all punctuation
     * marks
     *
     * @param s
     * @return
     */
    public static String toUpperNormalized(String s) {
        String stripped = "";
        if (s != null && !"".equals(s)) {
            stripped = Normalizer.normalize(s.toUpperCase(), Normalizer.Form.NFD);
            stripped = stripped.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
            //org.apache.commons.lang3.StringUtils ?? 
        } else {
            //do nothing
            return s;
        }
        return stripped;
    }

    public static String formatWebsiteLinks(String webLink) {
        if (webLink.isEmpty()) {
            return "";
        }
        //escape special characters before processing
        webLink = escapeForXml(webLink);

        final String LINK_WRAPPER_START = "<text:a text:style-name=\"Internet_20_link\" xmlns:xlink=\"http://www.w3.org/1999/xlink\" xlink:type=\"simple\" xlink:show=\"new\" xlink:href=\"", //europass_5f_Text_5f_Underline
                LINK_WRAPPER_MIDDLE = "\">",
                LINK_WRAPPER_END = "</text:a>";

        String regex = "^(?:(?:https?://|ftp://|www.\\w))(?:\\S+(?::\\S*)?@)?(?:|(?:(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)(?:\\.(?:[a-z\\u00a1-\\uffff0-9]-*)*[a-z\\u00a1-\\uffff0-9]+)*(?:\\.(?:[a-z\\u00a1-\\uffff]{2,})))(?::\\d{2,5})?(?:/\\S*)?$";
        RegexValidator validator = new RegexValidator(regex, false);
        StringBuilder href, url = new StringBuilder(webLink),
                result = new StringBuilder("");

        //check if website is a valid url
        //if (validator.isValid(webLink)){
        //prepend http:// if the URL does not begin with http for links to work, e.g. begins with www. 
        href = url.toString().startsWith("http") ? url : new StringBuilder("http://" + url.toString());
        String wrappedLink = LINK_WRAPPER_START + href + LINK_WRAPPER_MIDDLE + url + LINK_WRAPPER_END;
        result.append(wrappedLink);
        //}else
        //result.append(url);

        return result.toString();
    }
}
