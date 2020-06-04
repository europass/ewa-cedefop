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
package europass.ewa.model;

import java.util.List;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

public class CLModelTest {

    @Test
    public void noContacts() {
        SkillsPassport esp = CLMockObject.learner();
        esp.setCoverLetter(new CoverLetter());
        esp.withPreferences("ECL", esp.getCoverLetter(), "CoverLetter");

        boolean actual = esp.getCoverLetter().printContacts();

        Assert.assertThat("No Contacts print", actual, CoreMatchers.is(false));

    }

    @Test
    public void onlyEmail() {
        SkillsPassport esp = withContact(0, 0);

        boolean actual = esp.getCoverLetter().printContacts();

        Assert.assertThat("Only Email, yes print", actual, CoreMatchers.is(true));
    }

    @Test
    public void emailAndTel() {
        SkillsPassport esp = withContact(1, 0);

        boolean actual = esp.getCoverLetter().printContacts();

        Assert.assertThat("Email and Tel, yes print", actual, CoreMatchers.is(true));
    }

    @Test
    public void emailAndIm() {
        SkillsPassport esp = withContact(0, 1);

        boolean actual = esp.getCoverLetter().printContacts();

        Assert.assertThat("Email and IM, yes print", actual, CoreMatchers.is(true));
    }

    @Test
    public void reducedContactInfo() {
        int initTelSize = 4;
        int initIMSize = 3;

        SkillsPassport esp = withContact(initTelSize, initIMSize);

        List<ContactMethod> clTels = esp.getCoverLetter().ReducedTelephoneList();
        List<ContactMethod> clIms = esp.getCoverLetter().ReducedInstantMessagingList();
        int clTelSize = clTels.size();
        int clImSize = clIms.size();

        Assert.assertThat("Telephones are fewer", clTelSize < initTelSize, CoreMatchers.is(true));
        Assert.assertThat("Telephones are " + CoverLetter.TEL_LIMIT, clTelSize, CoreMatchers.is(CoverLetter.TEL_LIMIT));
        Assert.assertThat("IM are fewer", clImSize < initIMSize, CoreMatchers.is(true));
        Assert.assertThat("IM are " + CoverLetter.IM_LIMIT, clImSize, CoreMatchers.is(CoverLetter.IM_LIMIT));
    }

    @Test
    public void exactLimitsContactInfo() {
        int initTelSize = 2;
        int initIMSize = 1;

        SkillsPassport esp = withContact(initTelSize, initIMSize);

        List<ContactMethod> clTels = esp.getCoverLetter().ReducedTelephoneList();
        List<ContactMethod> clIms = esp.getCoverLetter().ReducedInstantMessagingList();
        int clTelSize = clTels.size();
        int clImSize = clIms.size();

        Assert.assertThat("Telephones are same", clTelSize == initTelSize, CoreMatchers.is(true));
        Assert.assertThat("Telephones are " + CoverLetter.TEL_LIMIT, clTelSize, CoreMatchers.is(CoverLetter.TEL_LIMIT));
        Assert.assertThat("IM are same", clImSize == initIMSize, CoreMatchers.is(true));
        Assert.assertThat("IM are " + CoverLetter.IM_LIMIT, clImSize, CoreMatchers.is(CoverLetter.IM_LIMIT));
    }

    @Test
    public void lowerThanLimitsContactInfo() {
        int initTelSize = 1;
        int initIMSize = 0;

        SkillsPassport esp = withContact(initTelSize, initIMSize);

        List<ContactMethod> clTels = esp.getCoverLetter().ReducedTelephoneList();
        int clTelSize = clTels.size();

        Assert.assertThat("Telephones are same", clTelSize == initTelSize, CoreMatchers.is(true));
        Assert.assertThat("Telephones are as many as they were: " + initTelSize, clTelSize, CoreMatchers.is(initTelSize));
        Assert.assertNull("IM are null", esp.getCoverLetter().ReducedInstantMessagingList());
    }

    private SkillsPassport withContact(int telLimit, int imLimit) {
        SkillsPassport esp = CLMockObject.learnerWithContact(telLimit, imLimit, true, true);

        CoverLetter cl = new CoverLetter();

        esp.setCoverLetter(cl);

        esp.withPreferences("ECL", esp.getCoverLetter(), "CoverLetter");

        return esp;
    }
}
