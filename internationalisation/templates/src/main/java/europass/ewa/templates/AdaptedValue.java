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
package europass.ewa.templates;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.apache.commons.text.StringEscapeUtils;

public class AdaptedValue {

    private Transformer htmlTransformer = null;

    private static Pattern HTML_TAG_PATTERN = Pattern.compile("<(/)?([^<>]+)(/)?>");

    private final Object obj;

    public AdaptedValue(Object obj) {
        this.obj = obj;
    }

    public AdaptedValue(Object obj, Transformer htmlTransformer) {
        this.obj = obj;
        this.htmlTransformer = htmlTransformer;
    }

    @Override
    public String toString() {
        //We need to make sure that no invalid characters are entered, e.g. &
        String str = this.obj != null ? this.obj.toString() : "";
        return StringEscapeUtils.escapeXml10(str);
    }

    /**
     * vpol created so the ict levels grid will display the <br> like
     * <text:line-break/>
     * <br>1. convert br to text:line-break
     * <br>2. escapeXml
     * <br>3. convert escaped text:line-break to regular text:line-break with
     * tags
     * <br><b>Notice!!!</b>: the br transformation to <text:line-break/> will be
     * applied to all bundles used in the odt generation
     */
    public String withLineBreaks() {
        //We need to make sure that no invalid characters are entered, e.g. &
        String str1 = "";//this.obj.toString();
        str1 = this.obj.toString().replaceAll("<[\\s/]*(br|BR)[\\s/]*>", "<text:line-break/>");
        String str2 = StringEscapeUtils.escapeXml10(str1);
        String str3 = str2.replaceAll("<[\\s/]*(&lt;text:line-break/&gt;)[\\s/]*>", "<text:line-break/>");
        return str3;
    }

    public String capitalize() {
        String str = this.obj.toString();
        return upperCase(str);
    }

    /**
     * Capitalises the text and adds a suitable text:p with style of heading
     *
     * @return
     */
    public String capitalizeHeading() {
        return this.htmlToOdt(this.capitalize(), "left-heading");
    }

    /**
     * Capitalises the text and adds a suitable text:p with style of subheading
     *
     * @return
     */
    public String capitalizeSubHeading() {
        return this.htmlToOdt(this.capitalize(), "left-subheading");
    }

    /**
     * Adds a suitable text:p with style of heading
     *
     * @return
     */
    public String heading() {
        return this.transform("left-heading");
    }

    /**
     * Adds a suitable text:p with style of sub heading
     *
     * @return
     */
    public String subheading() {
        return this.transform("left-subheading");
    }

    /**
     * converts html to odt
     *
     * @return
     */
    public String odt() {
        return this.transform("dummy-root");
    }

    private static String upperCase(String str) {
        String toUp = str;
        //remove accents
        toUp = Normalizer.normalize(toUp, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        toUp = toUp.toUpperCase();
        //
        return lowercaseHtmlTags(toUp);
    }

    private static String lowercaseHtmlTags(String html) {
        String newhtml = html;
        Matcher m = HTML_TAG_PATTERN.matcher(html);
        StringBuffer output = new StringBuffer();
        while (m.find()) {
            m.appendReplacement(output,
                    "<" + (m.group(1) == null ? "" : m.group(1))
                    + m.group(2).toLowerCase()
                    + (m.group(3) == null ? "" : m.group(3))
                    + ">");
        }
        m.appendTail(output);
        newhtml = output.toString().trim();
        return newhtml;
    }

    private String transform(String className) {
        String text = this.obj.toString();

        return this.htmlToOdt(text, className);
    }

    private String htmlToOdt(String text, String className) {
        if (htmlTransformer == null) {
            return text;
        }

        String odtTxt = text;

        //Replace all <br />
        odtTxt = odtTxt.replaceAll("<[\\s/]*(br|BR)[\\s/]*>", "<br/>");

        String cls = className;
        if (cls == null || (cls != null && cls.isEmpty())) {
            cls = "dummy-root";
        }
        String html = "<div class=\"" + cls + "\">".concat(odtTxt.concat("</div>"));

        try {
            Source odt = new StreamSource(new ByteArrayInputStream(html.getBytes("UTF-8")));
            StringWriter writer = new StringWriter();
            Result result = new StreamResult(writer);

            htmlTransformer.transform(odt, result);

            return writer.toString();
        } catch (Exception e) {
            return odtTxt;
        }
    }
}
