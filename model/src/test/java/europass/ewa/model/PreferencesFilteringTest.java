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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Test;

import europass.ewa.collections.Predicates;

public class PreferencesFilteringTest {

    @Test
    public void chechDocumentPrefType() {
        SkillsPassport esp = MockObjects.referenceToESP();

        assertThat(esp.getPrefDocumentName(), is("ECV"));
    }

    @Test
    public void filteroutAllReferenceTo() {
        SkillsPassport esp = MockObjects.referenceToESP();

        CleanupUtils.updatePreferenceVisibility(esp, Predicates.ESP_ALL_REFERENCETO_PREFS, false);

        //assert that the preferences remain as many as they where
        List<PrintingPreference> prefs = esp.getDocumentPrintingPrefs().get("ECV");

        assertNotNull(prefs);

        assertThat(prefs.size(), is(9));

        //Non referenceTo is shown
        for (int i = 0; i <= 3; i++) {
            assertThat(prefs.get(i).getName().toString(), prefs.get(i).getShow(), is(true));
        }
        //All referenceTo is hidden
        for (int i = 4; i <= 8; i++) {
            assertThat(prefs.get(i).getName().toString(), prefs.get(i).getShow(), is(false));
        }

    }

    @Test
    public void filteroutAllButReferenceTo() {

        SkillsPassport esp = MockObjects.referenceToESP();

        CleanupUtils.updatePreferenceVisibility(esp, Predicates.ESP_ALL_BUT_REFERENCETO_PREFS, false);

        //assert that the preferences remain as many as they where
        List<PrintingPreference> prefs = esp.getDocumentPrintingPrefs().get("ECV");

        assertNotNull(prefs);

        assertThat(prefs.size(), is(9));

        //CV is hidden
        for (int i = 0; i <= 5; i++) {
            assertThat(prefs.get(i).getName().toString(), prefs.get(i).getShow(), is(false));
        }
        //ReferenceTo is shown
        for (int i = 6; i <= 8; i++) {
            assertThat(prefs.get(i).getName().toString(), prefs.get(i).getShow(), is(true));
        }

    }
}
