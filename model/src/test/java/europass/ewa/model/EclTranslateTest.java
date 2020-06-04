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

import java.util.Locale;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import europass.ewa.model.reflection.ReflectionUtils;

public class EclTranslateTest {

    static SkillsPassport ecl;

    @BeforeClass
    public static void prepare() {

        ecl = new SkillsPassport();

        CoverLetter coverLetter = new CoverLetter();
        Addressee addressee = new Addressee();
        PersonName pn = new PersonName();
        pn.setTitle(new CodeLabel("mr", "Mr."));
        addressee.setPersonName(pn);
        coverLetter.setAddresse(addressee);

        Letter letter = new Letter();
        OpeningSalutation opSal = new OpeningSalutation();
        opSal.setSalutation(new CodeLabel("opening-salut-2"));
        letter.setOpeningSalutation(opSal);
        coverLetter.setLetter(letter);

        letter.setClosingSalutation(new CodeLabel("closing-salut-1"));

        coverLetter.setLetter(letter);
        ecl.setCoverLetter(coverLetter);

    }

    @Test
    public void translateEclTaxonomies() throws IllegalArgumentException, IllegalAccessException {
        ReflectionUtils.deepTranslateTo(ecl, ecl, new Locale("el"));

        String title = ecl.getCoverLetter().getAddressee().getPersonName().getTitle().getLabel();
        Assert.assertThat(title, CoreMatchers.is("Κος"));

        String openingSalutation = ecl.getCoverLetter().getLetter().getOpeningSalutation().getSalutation().getLabel();
        Assert.assertThat(openingSalutation, CoreMatchers.is("Αξιότιμη κυρία"));

        String closingSalutation = ecl.getCoverLetter().getLetter().getClosingSalutation().getLabel();
        Assert.assertThat(closingSalutation, CoreMatchers.is("Με εκτίμηση"));

    }

}
