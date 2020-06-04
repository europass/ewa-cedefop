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
package europass.ewa.model.conversion.json;

import java.io.IOException;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import com.fasterxml.jackson.databind.JsonMappingException;

import europass.ewa.enums.EuropassDocumentType;
import europass.ewa.model.Address;
import europass.ewa.model.CLMockObject;
import europass.ewa.model.SkillsPassport;

public class JsonToCLTest extends JsonMapperTest {

    @Test
    public void addressee() throws JsonMappingException, IOException {

        String json = CLMockObject.PREFIX_JSON + CLMockObject.PREFIX_COVERLETTER_JSON
                + CLMockObject.ADDRESSEE_JSON
                + CLMockObject.SUFFIX_COVERLETTER_JSON + CLMockObject.SUFFIX_JSON;

        SkillsPassport esp = getMapper().readValue(json, SkillsPassport.class);

        Assert.assertNotNull("Addressee:", esp.getCoverLetter().getAddressee());
        Assert.assertNotNull("Addressee PersonName:", esp.getCoverLetter().getAddressee().getPersonName());
        Assert.assertThat("Addresse Title Label:",
                esp.getCoverLetter().getAddressee().getPersonName().getTitle().getLabel(),
                CoreMatchers.is("Dr."));
    }

    // RELATED TO EWA-901
    @Test
    public void addresseeWithAddressLine2() throws JsonMappingException, IOException {

        String json = CLMockObject.PREFIX_JSON + CLMockObject.PREFIX_COVERLETTER_JSON
                + CLMockObject.ADDRESSEE_WITH_ADDRESSLINE2_JSON
                + CLMockObject.SUFFIX_COVERLETTER_JSON + CLMockObject.SUFFIX_JSON;

        SkillsPassport esp = getMapper().readValue(json, SkillsPassport.class);

        Address addr = esp.getCoverLetter().getAddressee().getOrganisation().getContactInfo().getAddress().getContact();

        Assert.assertNotNull("Addressee:", esp.getCoverLetter().getAddressee());
        Assert.assertNotNull("Addressee Organisation ContactInfo ContactAddress Address:", addr);
        Assert.assertThat("Addresse Title Label:",
                addr.getAddressLine2(),
                CoreMatchers.is("151 Culford Rd"));
    }

    @Test
    public void letter() throws JsonMappingException, IOException {

        String json = CLMockObject.PREFIX_JSON + CLMockObject.PREFIX_COVERLETTER_JSON
                + CLMockObject.LETTER_JSON
                + CLMockObject.SUFFIX_COVERLETTER_JSON + CLMockObject.SUFFIX_JSON;

        SkillsPassport esp = getMapper().readValue(json, SkillsPassport.class);

        Assert.assertNotNull("Letter:", esp.getCoverLetter().getLetter());
        Assert.assertNotNull("Letter Localisation:", esp.getCoverLetter().getLetter().getLocalisation());
        Assert.assertThat("Letter Date Year:",
                esp.getCoverLetter().getLetter().getLocalisation().getDate().getYear(),
                CoreMatchers.is(2013));
        Assert.assertThat("Letter Date Month:",
                esp.getCoverLetter().getLetter().getLocalisation().getDate().getMonth(),
                CoreMatchers.is(10));
        Assert.assertThat("Letter Date Day:",
                esp.getCoverLetter().getLetter().getLocalisation().getDate().getDay(),
                CoreMatchers.is(15));
        Assert.assertThat("Letter Place",
                esp.getCoverLetter().getLetter().getLocalisation().getPlace().getMunicipality(),
                CoreMatchers.is("Birmingham"));
        Assert.assertNotNull("Letter Subject:", esp.getCoverLetter().getLetter().getSubjectLine());
        Assert.assertNotNull("Letter Opening Salutation:", esp.getCoverLetter().getLetter().getOpeningSalutation());
        Assert.assertThat("Letter Opening Salutation Label:",
                esp.getCoverLetter().getLetter().getOpeningSalutation().getSalutation().getLabel(),
                CoreMatchers.is("Dear Mr."));
        Assert.assertNotNull("Letter Main Body:", esp.getCoverLetter().getLetter().getBody());
        Assert.assertNotNull("Letter Closing Salutation:", esp.getCoverLetter().getLetter().getClosingSalutation());
        Assert.assertThat("Letter Opening Salutation Label:",
                esp.getCoverLetter().getLetter().getClosingSalutation().getLabel(),
                CoreMatchers.is("Yours sincerelly"));

    }

    @Test
    public void documentation() throws JsonMappingException, IOException {

        String json = CLMockObject.PREFIX_JSON + CLMockObject.PREFIX_COVERLETTER_JSON
                + CLMockObject.DOCUMENTATION_JSON
                + CLMockObject.SUFFIX_COVERLETTER_JSON + CLMockObject.SUFFIX_JSON;

        SkillsPassport esp = getMapper().readValue(json, SkillsPassport.class);

        Assert.assertNotNull("Documentation:", esp.getCoverLetter().getDocumentation());
        Assert.assertNotNull("Inter Document References:", esp.getCoverLetter().getDocumentation().getInterDocumentList());
        Assert.assertThat("2nd Europass Document is ESP:",
                esp.getCoverLetter().getDocumentation().getInterDocumentList().get(1).getRef(),
                CoreMatchers.is(EuropassDocumentType.ESP));
        Assert.assertNotNull("Intra Document References:", esp.getCoverLetter().getDocumentation().getIntraDocumentList());
        Assert.assertThat("2nd Attachment is ATT_2:",
                esp.getCoverLetter().getDocumentation().getIntraDocumentList().get(1).getIdref(),
                CoreMatchers.is("ATT_2"));
        Assert.assertNotNull("External Document References:", esp.getCoverLetter().getDocumentation().getExtraDocumentList());
        Assert.assertThat("2nd External is Video CV:",
                esp.getCoverLetter().getDocumentation().getExtraDocumentList().get(1).getDescription(),
                CoreMatchers.is("Video CV"));
    }
}
