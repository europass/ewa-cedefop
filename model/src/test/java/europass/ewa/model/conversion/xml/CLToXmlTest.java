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
package europass.ewa.model.conversion.xml;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import europass.ewa.model.CLMockObject;

public class CLToXmlTest extends XmlMapperTest {

    @Test
    public void completePersonName() throws JsonProcessingException {
        String xml = getMapper().writeValueAsString(CLMockObject.learner());

        Assert.assertNotNull("XML produced - ", xml);

        boolean exactXML
                = xml.contains(CLMockObject.PERSONNAME_WITH_TITLE_XML);
        Assert.assertThat("Cover Letter Holder PersonName XML", exactXML, CoreMatchers.is(true));
    }

    @Test
    public void addressee() throws JsonProcessingException {
        String xml = getMapper().writeValueAsString(CLMockObject.addressee());

        Assert.assertNotNull("XML produced - ", xml);

        boolean exactXML = xml.contains(CLMockObject.ADDRESSEE_XML);
        Assert.assertThat("Cover Letter Addressee XML", exactXML, CoreMatchers.is(true));

    }

    // RELATED TO EWA-901
    @Test
    public void addresseeWithAddressLine2() throws JsonProcessingException {
        String xml = getMapper().writeValueAsString(CLMockObject.addresseeWithAddressLine2());

        Assert.assertNotNull("XML produced - ", xml);

        boolean exactXML = xml.contains(CLMockObject.ADDRESSEE_WITH_ADDRESSLINE2_XML);
        Assert.assertThat("Cover Letter Addressee XML", exactXML, CoreMatchers.is(true));

    }

    @Test
    public void letter() throws JsonProcessingException {
        String xml = getMapper().writeValueAsString(CLMockObject.letter());

        Assert.assertNotNull("XML produced - ", xml);

        boolean exactXML = xml.contains(CLMockObject.LETTER_XML);
        Assert.assertThat("Cover Letter Letter XML", exactXML, CoreMatchers.is(true));
    }

    @Test
    public void documentation() throws JsonProcessingException {
        String xml = getMapper().writeValueAsString(CLMockObject.documentation("en"));

        Assert.assertNotNull("XML produced - ", xml);

        boolean exactXML = xml.contains(CLMockObject.DOCUMENTATION_XML);
        Assert.assertThat("Cover Letter Encolsed Documents XML", exactXML, CoreMatchers.is(true));

    }

    @Test
    public void documentationDE() throws JsonProcessingException {
        String xml = getMapper().writeValueAsString(CLMockObject.documentation("de"));
        String DOCUMENTATION_XML = CLMockObject.DOCUMENTATION_XML
                .replaceAll("<Code>enclosed</Code><Label>Enclosed:</Label>", "<Code>attached</Code><Label>Angebracht:</Label>");

        System.out.println(DOCUMENTATION_XML);
        System.out.println(xml);
        Assert.assertNotNull("XML produced - ", xml);

        boolean exactXML = xml.contains(DOCUMENTATION_XML);
        Assert.assertThat("Cover Letter Encolsed Documents XML", exactXML, CoreMatchers.is(true));

    }

}
