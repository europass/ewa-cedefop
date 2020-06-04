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

import java.util.HashMap;
import java.util.Locale;

import org.junit.Assert;
import org.junit.Test;

public class OpeningSalutationTest {

    @Test
    public void salutationLabel_NullpersonNameSurname_Null() {
        SkillsPassport esp = CLMockObject.openingSalutation(null, null);

        String actual = esp.getCoverLetter().getLetter().getOpeningSalutation().openingSalutationTxt();

        Assert.assertEquals("The openingSalutationTxt is just the empty string", actual, "");
    }

    @Test
    public void salutation_NullpersonNameSurname_Null() {
        SkillsPassport esp = CLMockObject.nullOpeningSalutation();

        String actual = esp.getCoverLetter().getLetter().getOpeningSalutation().openingSalutationTxt();

        Assert.assertEquals("The openingSalutationTxt is just the empty string", actual, "");
    }

    @Test
    public void salutationLabelNoReplace_personNameSurname() {
        SkillsPassport esp = CLMockObject.openingSalutation();

        esp.setLocale(Locale.FRENCH);
        esp.activatePreferences("ECL", new HashMap<String, PrintingPreference>());
        esp.applyDefaultPreferences("ECL");

        String actual = esp.getCoverLetter().getLetter().getOpeningSalutation().openingSalutationTxt();

        Assert.assertEquals("The openingSalutationTxt is Dear Mr. Stuart", actual, "Dear Mr. Stuart,");
    }

    @Test
    public void salutationLabelWithDots_personNameSurname() {
        SkillsPassport esp = CLMockObject.openingSalutation("Dear Mr. ...", "Stuart");

        esp.setLocale(Locale.FRENCH);
        esp.activatePreferences("ECL", new HashMap<String, PrintingPreference>());
        esp.applyDefaultPreferences("ECL");

        String actual = esp.getCoverLetter().getLetter().getOpeningSalutation().openingSalutationTxt();

        Assert.assertEquals("The openingSalutationTxt is Dear Mr. Stuart", actual, "Dear Mr. Stuart,");
    }

    @Test
    public void salutationLabelWithDots2_personNameSurname() {
        SkillsPassport esp = CLMockObject.openingSalutation("Dear Mr. ... King", "Stuart");

        esp.setLocale(Locale.FRENCH);
        esp.activatePreferences("ECL", new HashMap<String, PrintingPreference>());
        esp.applyDefaultPreferences("ECL");

        String actual = esp.getCoverLetter().getLetter().getOpeningSalutation().openingSalutationTxt();

        Assert.assertEquals("The openingSalutationTxt is Dear Mr. Stuart", actual, "Dear Mr. Stuart King,");
    }

    @Test
    public void salutationLabelNull_personNameSurnameWithEllipsis() {
        SkillsPassport esp = CLMockObject.openingSalutation(null, "Stuart…");

        esp.setLocale(Locale.FRENCH);
        esp.activatePreferences("ECL", new HashMap<String, PrintingPreference>());
        esp.applyDefaultPreferences("ECL");

        String actual = esp.getCoverLetter().getLetter().getOpeningSalutation().openingSalutationTxt();

        Assert.assertEquals("The openingSalutationTxt is  'Stuart…'", actual, "Stuart…,");
    }

    @Test
    public void salutationLabel_personNameSurnameWithEllipsis() {
        SkillsPassport esp = CLMockObject.openingSalutation("Dear Mr.", "Stuart…");

        esp.setLocale(Locale.FRENCH);
        esp.activatePreferences("ECL", new HashMap<String, PrintingPreference>());
        esp.applyDefaultPreferences("ECL");

        String actual = esp.getCoverLetter().getLetter().getOpeningSalutation().openingSalutationTxt();

        Assert.assertEquals("The openingSalutationTxt is  'Dear Mr. Stuart…,'", actual, "Dear Mr. Stuart…,");
    }

    @Test
    public void salutationLabelWithEllipsis_personNameSurnameNull() {
        SkillsPassport esp = CLMockObject.openingSalutation("Dear Mr. …", null);

        esp.setLocale(Locale.FRENCH);
        esp.activatePreferences("ECL", new HashMap<String, PrintingPreference>());
        esp.applyDefaultPreferences("ECL");

        String actual = esp.getCoverLetter().getLetter().getOpeningSalutation().openingSalutationTxt();

        Assert.assertEquals("The openingSalutationTxt should be 'Dear Mr. …,'", actual, "Dear Mr. …,");
    }

    @Test
    public void salutationLabelWithEllipsis_personNameSurname() {
        SkillsPassport esp = CLMockObject.openingSalutation("Dear Mr. …", "Stuart");

        esp.setLocale(Locale.FRENCH);
        esp.activatePreferences("ECL", new HashMap<String, PrintingPreference>());
        esp.applyDefaultPreferences("ECL");

        String actual = esp.getCoverLetter().getLetter().getOpeningSalutation().openingSalutationTxt();

        Assert.assertEquals("The openingSalutationTxt is Dear Mr. Stuart", actual, "Dear Mr. Stuart,");
    }

    @Test
    public void salutationLabelWithEllipsis2_personNameSurname() {
        SkillsPassport esp = CLMockObject.openingSalutation("Dear Mr. … King", "Stuart");

        esp.setLocale(Locale.FRENCH);
        esp.activatePreferences("ECL", new HashMap<String, PrintingPreference>());
        esp.applyDefaultPreferences("ECL");

        String actual = esp.getCoverLetter().getLetter().getOpeningSalutation().openingSalutationTxt();

        Assert.assertEquals("The openingSalutationTxt is Dear Mr. Stuart", actual, "Dear Mr. Stuart King,");
    }

    @Test
    public void salutationLabelNull_personNameSurnameWithDots() {
        SkillsPassport esp = CLMockObject.openingSalutation(null, "Stuart...");

        esp.setLocale(Locale.FRENCH);
        esp.activatePreferences("ECL", new HashMap<String, PrintingPreference>());
        esp.applyDefaultPreferences("ECL");

        String actual = esp.getCoverLetter().getLetter().getOpeningSalutation().openingSalutationTxt();

        Assert.assertEquals("The openingSalutationTxt is  'Stuart...'", actual, "Stuart...,");
    }

    @Test
    public void salutationLabel_personNameSurnameWithDots() {
        SkillsPassport esp = CLMockObject.openingSalutation("Dear Mr.", "Stuart...");

        esp.setLocale(Locale.FRENCH);
        esp.activatePreferences("ECL", new HashMap<String, PrintingPreference>());
        esp.applyDefaultPreferences("ECL");

        String actual = esp.getCoverLetter().getLetter().getOpeningSalutation().openingSalutationTxt();

        Assert.assertEquals("The openingSalutationTxt is  'Dear Mr. Stuart...,'", actual, "Dear Mr. Stuart...,");
    }

    @Test
    public void salutationLabelWithDots_personNameSurnameNull() {
        SkillsPassport esp = CLMockObject.openingSalutation("Dear Mr. ...", null);

        esp.setLocale(Locale.FRENCH);
        esp.activatePreferences("ECL", new HashMap<String, PrintingPreference>());
        esp.applyDefaultPreferences("ECL");

        String actual = esp.getCoverLetter().getLetter().getOpeningSalutation().openingSalutationTxt();

        Assert.assertEquals("The openingSalutationTxt should be 'Dear Mr. ...,'", actual, "Dear Mr. ...,");
    }

}
