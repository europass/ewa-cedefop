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
package europass.ewa.conversion;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ECLHtmlToOdtTest {

    private Transformer transformer;

    @Before
    public void getTransformer() throws TransformerConfigurationException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Source xslt = new StreamSource(getClass().getResourceAsStream("/xslt/html2odt.xsl"));
        Assert.assertNotNull("XSLT file ", xslt);
        transformer = factory.newTransformer(xslt);
    }

    @Test
    public void simpleLink() throws TransformerException {
        Source text = new StreamSource(getClass().getResourceAsStream("/xslt/ecl/html_link.html"));

        Assert.assertNotNull("HTML file ", text);

        StringWriter writer = new StringWriter();
        Result result = new StreamResult(writer);

        transformer.transform(text, result);

        String odt = writer.toString();

        Assert.assertThat("Text Link: ", odt,
                CoreMatchers.is("<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_SectionDetails_ECL\">"
                        + "<text:a xmlns:xlink=\"http://www.w3.org/1999/xlink\" "
                        + "xlink:type=\"simple\" "
                        + "xlink:href=\"http://test.gr\">"
                        + "This is a simple test"
                        + "</text:a>"
                        + "</text:p>"));
    }

    @Test
    public void bold() throws TransformerException {
        Source text = new StreamSource(new ByteArrayInputStream("<div class=\"dummy-root ECL\"><b>bold text</b></div>".getBytes()));
        StringWriter writer = new StringWriter();
        Result result = new StreamResult(writer);

        transformer.transform(text, result);

        String odt = writer.toString();

        Assert.assertThat("Bold: ", odt,
                CoreMatchers.is(
                        "<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" "
                        + "text:style-name=\"europass_5f_SectionDetails_ECL\">"
                        + "<text:span text:style-name=\"europass_5f_Text_5f_Bold\">bold text</text:span>"
                        + "</text:p>"));
    }

    @Test
    public void underlineAndBold() throws TransformerException {
        Source text = new StreamSource(new ByteArrayInputStream("<div class=\"dummy-root ECL\"><u><b>text</b></u></div>".getBytes()));
        StringWriter writer = new StringWriter();
        Result result = new StreamResult(writer);

        transformer.transform(text, result);

        String odt = writer.toString();

        Assert.assertThat("Underline And Bold: ", odt,
                CoreMatchers.is("<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_SectionDetails_ECL\">"
                        + "<text:span text:style-name=\"europass_5f_Text_5f_Underline\">"
                        + "<text:span text:style-name=\"europass_5f_Text_5f_Bold_5f_And_5f_Underline\">text</text:span>"
                        + "</text:span>"
                        + "</text:p>"));
    }

    @Test
    public void blockquote() throws TransformerException {
        Source text = new StreamSource(getClass().getResourceAsStream("/xslt/ecl/html_blockquote.html"));

        Assert.assertNotNull("HTML file ", text);

        StringWriter writer = new StringWriter();
        Result result = new StreamResult(writer);

        transformer.transform(text, result);

        String odt = writer.toString();

        Assert.assertThat("Blockquote: ", odt,
                CoreMatchers.is("<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_SectionDetails_ECL\">11</text:p>"
                        + "<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_paragraph_indent1_ECL\">22"
                        + "<text:line-break/><text:tab/>33"
                        + "<text:line-break/><text:tab/>44"
                        + "</text:p>"
                        + "<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_paragraph_indent1_ECL\">22b</text:p>"
                ));
    }

    @Test
    public void simpleText() throws TransformerException {
        Source text = new StreamSource(getClass().getResourceAsStream("/xslt/ecl/html_simple.html"));

        Assert.assertNotNull("HTML file ", text);

        StringWriter writer = new StringWriter();
        Result result = new StreamResult(writer);

        transformer.transform(text, result);

        String odt = writer.toString();

        Assert.assertThat("Simple Text: ", odt,
                CoreMatchers.is(
                        "<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_SectionDetails_ECL\">In charge of</text:p>"
                        + "<text:line-break xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\"/>"
                        + "<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_SectionDetails_ECL\">Monthly week</text:p>"
                        + "<text:line-break xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\"/>"
                        + "<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_SectionDetails_ECL\">20 conferences</text:p>"
                        + "<text:line-break xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\"/>"
                        + "<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_SectionDetails_ECL\">6 rooms</text:p>"
                        + "<text:line-break xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\"/>"));
    }

    @Test
    public void textWithChar() throws TransformerException {
        Source text = new StreamSource(getClass().getResourceAsStream("/xslt/ecl/html_weird_chars.html"));

        Assert.assertNotNull("HTML file ", text);

        StringWriter writer = new StringWriter();
        Result result = new StreamResult(writer);

        transformer.transform(text, result);

        String odt = writer.toString();

        Assert.assertThat("Simple Text: ", odt,
                CoreMatchers.is("<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_SectionDetails_ECL\">covers – Controlling</text:p>"
                        + "<text:line-break xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\"/>"
                        + "<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_SectionDetails_ECL\">analysis – Strategic</text:p>"
                ));
    }

    @Test
    public void paragraphWithIndent() throws TransformerException {
        Source text = new StreamSource(getClass().getResourceAsStream("/xslt/ecl/html_paragraph_indent.html"));

        Assert.assertNotNull("HTML file ", text);

        StringWriter writer = new StringWriter();
        Result result = new StreamResult(writer);

        transformer.transform(text, result);

        String odt = writer.toString();

        Assert.assertThat("Simple Text: ", odt,
                CoreMatchers.is(
                        "<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_paragraph_indent1_ECL\">Είναι πλέον κοινά παραδεκτό</text:p>"
                        + "<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_paragraph_indent2_ECL\">Εδώ θα μπει κείμενο</text:p>"
                        + "<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_paragraph_indent3_ECL\">Διάφορες εκδοχές έχουν προκύψει</text:p>"
                ));
    }

    @Test
    public void paragraphWithTextAlign() throws TransformerException {
        Source text = new StreamSource(getClass().getResourceAsStream("/xslt/ecl/html_paragraph_justify.html"));

        Assert.assertNotNull("HTML file ", text);

        StringWriter writer = new StringWriter();
        Result result = new StreamResult(writer);

        transformer.transform(text, result);

        String odt = writer.toString();

        Assert.assertThat("Simple Text: ", odt,
                CoreMatchers.is(
                        "<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_paragraph_align_justify_ECL\">It is a long established fact that a reader will be distracted by the  readable content of a page when looking at its layout. </text:p>"));
    }

    @Test
    public void paragraphWithIndentAndTextAlign() throws TransformerException {
        Source text = new StreamSource(getClass().getResourceAsStream("/xslt/ecl/html_paragraph_indent_justify.html"));

        Assert.assertNotNull("HTML file ", text);

        StringWriter writer = new StringWriter();
        Result result = new StreamResult(writer);

        transformer.transform(text, result);

        String odt = writer.toString();

        Assert.assertThat("Simple Text: ", odt,
                CoreMatchers.is(
                        "<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_paragraph_indent1_justify_ECL\">The point of  using Lorem Ipsum is that it has a more-or-less normal distribution of  letters...</text:p>"
                        + "<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_paragraph_indent1_justify_ECL\">The point of  using Lorem Ipsum is that it has a more-or-less normal distribution of  letters...</text:p>"
                        + "<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_paragraph_indent2_justify_ECL\">The point of  using Lorem Ipsum is that it has a more-or-less normal distribution of  letters...</text:p>"
                        + "<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_paragraph_indent2_justify_ECL\">The point of  using Lorem Ipsum is that it has a more-or-less normal distribution of  letters...</text:p>"
                        + "<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_paragraph_indent3_justify_ECL\">The point of  using Lorem Ipsum is that it has a more-or-less normal distribution of  letters...</text:p>"
                        + "<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_paragraph_indent3_justify_ECL\">The point of  using Lorem Ipsum is that it has a more-or-less normal distribution of  letters...</text:p>"
                ));
    }

    @Test
    public void simpleUnorderedList() throws TransformerException {
        Source text = new StreamSource(getClass().getResourceAsStream("/xslt/ecl/html_unordered_list.html"));

        Assert.assertNotNull("HTML file ", text);

        StringWriter writer = new StringWriter();
        Result result = new StreamResult(writer);

        transformer.transform(text, result);

        String odt = writer.toString();

        Assert.assertThat("Unordered List: ", odt,
                CoreMatchers.is("<text:list xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" "
                        + "text:style-name=\"europass_5f_bulleted_5f_list_ECL\">"
                        + "<text:list-item>"
                        + "<text:p text:style-name=\"europass_5f_bulleted_5f_list_ECL\"><text:span>alpha</text:span></text:p>"
                        + "</text:list-item>"
                        + "<text:list-item>"
                        + "<text:p text:style-name=\"europass_5f_bulleted_5f_list_ECL\"><text:span>beta</text:span></text:p>"
                        + "</text:list-item>"
                        + "<text:list-item>"
                        + "<text:p text:style-name=\"europass_5f_bulleted_5f_list_ECL\"><text:span>gamma</text:span></text:p>"
                        + "</text:list-item>"
                        + "</text:list>"));
    }

    @Test
    public void simpleNumberedList() throws TransformerException {
        Source text = new StreamSource(getClass().getResourceAsStream("/xslt/ecl/html_numbered_list.html"));

        Assert.assertNotNull("HTML file ", text);

        StringWriter writer = new StringWriter();
        Result result = new StreamResult(writer);

        transformer.transform(text, result);

        String odt = writer.toString();

        Assert.assertThat("Numbered List: ", odt,
                CoreMatchers.is("<text:list xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" "
                        + "text:style-name=\"europass_5f_numbered_5f_list_ECL\">"
                        + "<text:list-item>"
                        + "<text:p text:style-name=\"europass_5f_numbered_5f_list_ECL\"><text:span>alpha</text:span></text:p>"
                        + "</text:list-item>"
                        + "<text:list-item>"
                        + "<text:p text:style-name=\"europass_5f_numbered_5f_list_ECL\"><text:span>beta</text:span></text:p>"
                        + "</text:list-item>"
                        + "<text:list-item>"
                        + "<text:p text:style-name=\"europass_5f_numbered_5f_list_ECL\"><text:span>gamma</text:span></text:p>"
                        + "</text:list-item>"
                        + "</text:list>"));
    }
}
