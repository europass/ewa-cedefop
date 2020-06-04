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
import org.junit.Ignore;
import org.junit.Test;

public class HtmlToOdtTest {

    private Transformer transformer;

    @Before
    public void getTransformer() throws TransformerConfigurationException {
        TransformerFactory factory = TransformerFactory.newInstance();
        Source xslt = new StreamSource(getClass().getResourceAsStream("/xslt/html2odt.xsl"));
        Assert.assertNotNull("XSLT file ", xslt);
        transformer = factory.newTransformer(xslt);
    }

    @Ignore
    @Test
    public void paragraphInsideLi() throws TransformerException {
        Source text = new StreamSource(getClass().getResourceAsStream("/xslt/html_pInLi.html"));

        Assert.assertNotNull("HTML file ", text);

        StringWriter writer = new StringWriter();
        Result result = new StreamResult(writer);

        transformer.transform(text, result);

        String odt = writer.toString();

        Assert.assertThat("Paragraph inside Li: ",
                odt,
                CoreMatchers.is("<text:list xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_bulleted_5f_list\"><text:list-item><text:p text:style-name=\"europass_5f_bulleted_5f_list\">paragraph 1</text:p></text:list-item><text:list-item><text:p text:style-name=\"europass_5f_bulleted_5f_list\"> paragraph 2</text:p><text:list text:style-name=\"europass_5f_bulleted_5f_list\"><text:list-item><text:p text:style-name=\"europass_5f_bulleted_5f_list\"> nested paragraph 3</text:p></text:list-item></text:list></text:list-item></text:list>"));
    }

    @Ignore
    @Test
    public void paragraphsInsideLi() throws TransformerException {
        Source text = new StreamSource(getClass().getResourceAsStream("/xslt/html_psInLi.html"));

        Assert.assertNotNull("HTML file ", text);

        StringWriter writer = new StringWriter();
        Result result = new StreamResult(writer);

        transformer.transform(text, result);

        String odt = writer.toString();

        Assert.assertThat("Paragraph inside Li: ",
                odt,
                CoreMatchers.is("<text:list xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_bulleted_5f_list\"><text:list-item><text:p text:style-name=\"europass_5f_bulleted_5f_list\">List Item</text:p><text:list text:style-name=\"europass_5f_bulleted_5f_list\"><text:list-item><text:p text:style-name=\"europass_5f_bulleted_5f_list\"><text:span>Nested List Item 1</text:span></text:p></text:list-item><text:list-item><text:p text:style-name=\"europass_5f_bulleted_5f_list\"><text:span>Nested List Item 2</text:span></text:p></text:list-item></text:list></text:list-item></text:list>"));
    }

    @Test
    public void simpleLink() throws TransformerException {
        Source text = new StreamSource(getClass().getResourceAsStream("/xslt/html_link.html"));

        Assert.assertNotNull("HTML file ", text);

        StringWriter writer = new StringWriter();
        Result result = new StreamResult(writer);

        transformer.transform(text, result);

        String odt = writer.toString();

        Assert.assertThat("Text Link: ", odt,
                CoreMatchers.is("<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_SectionDetails\">"
                        + "<text:a xmlns:xlink=\"http://www.w3.org/1999/xlink\" "
                        + "xlink:type=\"simple\" "
                        + "xlink:href=\"http://test.gr\">"
                        + "This is a simple test"
                        + "</text:a>"
                        + "</text:p>"));
    }

    @Test
    public void simpleNumberedList() throws TransformerException {
        Source text = new StreamSource(getClass().getResourceAsStream("/xslt/html_numbered_list.html"));

        Assert.assertNotNull("HTML file ", text);

        StringWriter writer = new StringWriter();
        Result result = new StreamResult(writer);

        transformer.transform(text, result);

        String odt = writer.toString();

        Assert.assertThat("Numbered List: ", odt,
                CoreMatchers.is("<text:list xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" "
                        + "text:style-name=\"europass_5f_numbered_5f_list\">"
                        + "<text:list-item>"
                        + "<text:p text:style-name=\"europass_5f_numbered_5f_list\"><text:span>alpha</text:span></text:p>"
                        + "</text:list-item>"
                        + "<text:list-item>"
                        + "<text:p text:style-name=\"europass_5f_numbered_5f_list\"><text:span>beta</text:span></text:p>"
                        + "</text:list-item>"
                        + "<text:list-item>"
                        + "<text:p text:style-name=\"europass_5f_numbered_5f_list\"><text:span>gamma</text:span></text:p>"
                        + "</text:list-item>"
                        + "</text:list>"));
    }

    @Test
    public void simpleUnorderedList() throws TransformerException {
        Source text = new StreamSource(getClass().getResourceAsStream("/xslt/html_unordered_list.html"));

        Assert.assertNotNull("HTML file ", text);

        StringWriter writer = new StringWriter();
        Result result = new StreamResult(writer);

        transformer.transform(text, result);

        String odt = writer.toString();

        Assert.assertThat("Unrdered List: ", odt,
                CoreMatchers.is("<text:list xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_bulleted_5f_list\">"
                        + "<text:list-item><text:p text:style-name=\"europass_5f_bulleted_5f_list\"><text:span>alpha</text:span></text:p></text:list-item>"
                        + "<text:list-item><text:p text:style-name=\"europass_5f_bulleted_5f_list\"><text:span>beta</text:span></text:p></text:list-item>"
                        + "<text:list-item><text:p text:style-name=\"europass_5f_bulleted_5f_list\"><text:span>gamma</text:span></text:p></text:list-item></text:list>"));
    }

    @Test
    public void unorderedListWithStyle() throws TransformerException {
        Source text = new StreamSource(getClass().getResourceAsStream("/xslt/html_unordered_list_with_style.html"));

        Assert.assertNotNull("HTML file ", text);

        StringWriter writer = new StringWriter();
        Result result = new StreamResult(writer);

        transformer.transform(text, result);

        String odt = writer.toString();

        Assert.assertThat("Unordered List With Style: ", odt,
                CoreMatchers.is("<text:list xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_bulleted_5f_list\">"
                        + "<text:list-item><text:p text:style-name=\"europass_5f_bulleted_5f_list\"><text:span text:style-name=\"europass_5f_Text_5f_Bold\">alpha</text:span></text:p></text:list-item>"
                        + "<text:list-item><text:p text:style-name=\"europass_5f_bulleted_5f_list\"><text:span text:style-name=\"europass_5f_Text_5f_Italics\">beta</text:span></text:p></text:list-item>"
                        + "<text:list-item><text:p text:style-name=\"europass_5f_bulleted_5f_list\"><text:span text:style-name=\"europass_5f_Text_5f_Underline\">gamma</text:span></text:p></text:list-item></text:list>"));
    }

    @Test
    public void unorderedListWithStyles() throws TransformerException {
        Source text = new StreamSource(getClass().getResourceAsStream("/xslt/html_unordered_list_styles.html"));

        Assert.assertNotNull("HTML file ", text);

        StringWriter writer = new StringWriter();
        Result result = new StreamResult(writer);

        transformer.transform(text, result);

        String odt = writer.toString();

        Assert.assertThat("Unordered List With Styles: ", odt,
                CoreMatchers.is("<text:list xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_bulleted_5f_list\">"
                        + "<text:list-item><text:p text:style-name=\"europass_5f_bulleted_5f_list\"><text:span>This is a hyperlink test in a list: </text:span>"
                        + "<text:a xmlns:xlink=\"http://www.w3.org/1999/xlink\" xlink:type=\"simple\" xlink:href=\"http://www.bbc.com/news\">http://www.bbc.com/news</text:a></text:p></text:list-item>"
                        + "<text:list-item><text:p text:style-name=\"europass_5f_bulleted_5f_list\"><text:span>This is a bold hyperlink test in a list: </text:span>"
                        + "<text:a xmlns:xlink=\"http://www.w3.org/1999/xlink\" xlink:type=\"simple\" xlink:href=\"http://www.bbc.com/news\">"
                        + "<text:span text:style-name=\"europass_5f_Text_5f_Bold\">http://www.bbc.com/news</text:span></text:a></text:p></text:list-item>"
                        + "<text:list-item><text:p text:style-name=\"europass_5f_bulleted_5f_list\"><text:span>This text has </text:span>"
                        + "<text:span text:style-name=\"europass_5f_Text_5f_Superscript\">superscript</text:span><text:span> and </text:span>"
                        + "<text:span text:style-name=\"europass_5f_Text_5f_Subscript\">subscript</text:span></text:p></text:list-item></text:list>"));
    }

    @Test
    public void unorderedListWithLevelsAndStyleCkeditor() throws TransformerException {
        Source text = new StreamSource(getClass().getResourceAsStream("/xslt/html_unordered_list_levels_style_ckeditor.html"));

        Assert.assertNotNull("HTML file ", text);

        StringWriter writer = new StringWriter();
        Result result = new StreamResult(writer);

        transformer.transform(text, result);

        String odt = writer.toString();

        Assert.assertThat("Unordered List With Levels And Style (Ckeditor): ", odt,
                CoreMatchers.is("<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_SectionDetails\">This is a list:</text:p>"
                        + "<text:list xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_bulleted_5f_list\">"
                        + "<text:list-item><text:p text:style-name=\"europass_5f_bulleted_5f_list\"><text:span>This is </text:span><text:span text:style-name=\"europass_5f_Text_5f_Bold\">bold</text:span></text:p></text:list-item>"
                        + "<text:list-item><text:p text:style-name=\"europass_5f_bulleted_5f_list\"><text:span>This is </text:span><text:span text:style-name=\"europass_5f_Text_5f_Italics\"><text:span text:style-name=\"europass_5f_Text_5f_Bold_5f_And_5f_Italics\">bold and italic</text:span></text:span></text:p>"
                        + "<text:list text:style-name=\"europass_5f_bulleted_5f_list\"><text:list-item><text:p text:style-name=\"europass_5f_bulleted_5f_list\"><text:span>This is nested and </text:span><text:span text:style-name=\"europass_5f_Text_5f_Underline\">underlined</text:span></text:p></text:list-item></text:list>"
                        + "</text:list-item></text:list>"));
    }

    @Test
    public void lineBreak() throws TransformerException {
        Source text = new StreamSource(new ByteArrayInputStream("<br/>".getBytes()));
        StringWriter writer = new StringWriter();
        Result result = new StreamResult(writer);

        transformer.transform(text, result);

        String odt = writer.toString();

        Assert.assertThat("Line Break: ", odt,
                CoreMatchers.is("<text:line-break xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\"/>"));
    }

    @Test
    public void bold() throws TransformerException {
        Source text = new StreamSource(new ByteArrayInputStream("<div class=\"dummy-root\"><b>bold text</b></div>".getBytes()));
        StringWriter writer = new StringWriter();
        Result result = new StreamResult(writer);

        transformer.transform(text, result);

        String odt = writer.toString();

        Assert.assertThat("Bold: ", odt,
                CoreMatchers.is("<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_SectionDetails\">"
                        + "<text:span text:style-name=\"europass_5f_Text_5f_Bold\">bold text</text:span>"
                        + "</text:p>"));
    }

    @Test
    public void superscirpt() throws TransformerException {
        Source text = new StreamSource(new ByteArrayInputStream("<div class=\"dummy-rrot\"><p>hallo<sup>superscript</sup></p></div>".getBytes()));
        StringWriter writer = new StringWriter();
        Result result = new StreamResult(writer);

        transformer.transform(text, result);

        String odt = writer.toString();

        Assert.assertThat("Test Superscript: ", odt,
                CoreMatchers.is("<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_SectionDetails\">"
                        + "hallo<text:span text:style-name=\"europass_5f_Text_5f_Superscript\">superscript</text:span></text:p>")
        );
    }

    @Test
    public void underlineAndBold() throws TransformerException {
        Source text = new StreamSource(new ByteArrayInputStream("<div class=\"dummy-root\"><u><b>text</b></u></div>".getBytes()));
        StringWriter writer = new StringWriter();
        Result result = new StreamResult(writer);

        transformer.transform(text, result);

        String odt = writer.toString();

        Assert.assertThat("Underline And Bold: ", odt,
                CoreMatchers.is(
                        "<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_SectionDetails\">"
                        + "<text:span text:style-name=\"europass_5f_Text_5f_Underline\">"
                        + "<text:span text:style-name=\"europass_5f_Text_5f_Bold_5f_And_5f_Underline\">text</text:span>"
                        + "</text:span>"
                        + "</text:p>"));
    }

    @Test
    public void blockquote() throws TransformerException {
        Source text = new StreamSource(getClass().getResourceAsStream("/xslt/html_blockquote.html"));

        Assert.assertNotNull("HTML file ", text);

        StringWriter writer = new StringWriter();
        Result result = new StreamResult(writer);

        transformer.transform(text, result);

        String odt = writer.toString();

        Assert.assertThat("Blockquote: ", odt,
                CoreMatchers.is("<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_SectionDetails\">11</text:p>"
                        + "<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_paragraph_indent1\">22"
                        + "<text:line-break/><text:tab/>33"
                        + "<text:line-break/><text:tab/>44"
                        + "</text:p>"
                        + "<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_paragraph_indent1\">22b</text:p>"
                ));
    }

    @Test
    public void simpleText() throws TransformerException {
        Source text = new StreamSource(getClass().getResourceAsStream("/xslt/html_simple.html"));

        Assert.assertNotNull("HTML file ", text);

        StringWriter writer = new StringWriter();
        Result result = new StreamResult(writer);

        transformer.transform(text, result);

        String odt = writer.toString();

        Assert.assertThat("Simple Text: ", odt,
                CoreMatchers.is("<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_SectionDetails\">In charge of</text:p>"
                        + "<text:line-break xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\"/>"
                        + "<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_SectionDetails\">Monthly week</text:p>"
                        + "<text:line-break xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\"/>"
                        + "<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_SectionDetails\">20 conferences</text:p>"
                        + "<text:line-break xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\"/>"
                        + "<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_SectionDetails\">6 rooms</text:p>"
                        + "<text:line-break xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\"/>"));
    }

    @Test
    public void textWithChar() throws TransformerException {
        Source text = new StreamSource(getClass().getResourceAsStream("/xslt/html_weird_chars.html"));

        Assert.assertNotNull("HTML file ", text);

        StringWriter writer = new StringWriter();
        Result result = new StreamResult(writer);

        transformer.transform(text, result);

        String odt = writer.toString();

        Assert.assertThat("Simple Text: ", odt,
                CoreMatchers.is("<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_SectionDetails\">covers – Controlling</text:p>"
                        + "<text:line-break xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\"/>"
                        + "<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_SectionDetails\">analysis – Strategic</text:p>"
                ));
    }

    @Test
    public void paragraphWithIndent() throws TransformerException {
        Source text = new StreamSource(getClass().getResourceAsStream("/xslt/html_paragraph_indent.html"));

        Assert.assertNotNull("HTML file ", text);

        StringWriter writer = new StringWriter();
        Result result = new StreamResult(writer);

        transformer.transform(text, result);

        String odt = writer.toString();

        Assert.assertThat("Simple Text: ", odt,
                CoreMatchers.is("<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_paragraph_indent1\">Είναι πλέον κοινά παραδεκτό</text:p>"
                        + "<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_paragraph_indent2\">Εδώ θα μπει κείμενο</text:p>"
                        + "<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_paragraph_indent3\">Διάφορες εκδοχές έχουν προκύψει</text:p>"
                ));
    }

    @Test
    public void spanTab() throws TransformerException {
        Source text = new StreamSource(getClass().getResourceAsStream("/xslt/html_span_to_textTab.html"));

        Assert.assertNotNull("HTML file ", text);

        StringWriter writer = new StringWriter();
        Result result = new StreamResult(writer);

        transformer.transform(text, result);

        String odt = writer.toString();

        Assert.assertThat("Simple Text: ", odt,
                CoreMatchers.is("<text:tab xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\"/>Είναι πλέον κοινά παραδεκτό ότι ένας αναγνώστης "
                        + "αποσπάται από το περιεχόμενο που διαβάζει"));
    }

    @Test
    public void paragraphWithTextAlign() throws TransformerException {
        Source text = new StreamSource(getClass().getResourceAsStream("/xslt/html_paragraph_justify.html"));

        Assert.assertNotNull("HTML file ", text);

        StringWriter writer = new StringWriter();
        Result result = new StreamResult(writer);

        transformer.transform(text, result);

        String odt = writer.toString();

        Assert.assertThat("Simple Text: ", odt,
                CoreMatchers.is("<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_paragraph_align_justify\">It is a long established fact that a reader will be distracted by the  readable content of a page when looking at its layout. </text:p>"));
    }

    @Test
    public void paragraphWithIndentAndTextAlign() throws TransformerException {
        Source text = new StreamSource(getClass().getResourceAsStream("/xslt/html_paragraph_indent_justify.html"));

        Assert.assertNotNull("HTML file ", text);

        StringWriter writer = new StringWriter();
        Result result = new StreamResult(writer);

        transformer.transform(text, result);

        String odt = writer.toString();

        Assert.assertThat("Simple Text: ", odt,
                CoreMatchers.is("<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_paragraph_indent1_justify\">The point of  using Lorem Ipsum is that it has a more-or-less normal distribution of  letters...</text:p><text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_paragraph_indent1_justify\">The point of  using Lorem Ipsum is that it has a more-or-less normal distribution of  letters...</text:p>"
                        + "<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_paragraph_indent2_justify\">The point of  using Lorem Ipsum is that it has a more-or-less normal distribution of  letters...</text:p>"
                        + "<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_paragraph_indent2_justify\">The point of  using Lorem Ipsum is that it has a more-or-less normal distribution of  letters...</text:p>"
                        + "<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_paragraph_indent3_justify\">The point of  using Lorem Ipsum is that it has a more-or-less normal distribution of  letters...</text:p>"
                        + "<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_paragraph_indent3_justify\">The point of  using Lorem Ipsum is that it has a more-or-less normal distribution of  letters...</text:p>"
                ));
    }
}
