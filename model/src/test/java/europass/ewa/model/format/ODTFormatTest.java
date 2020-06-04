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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import europass.ewa.model.CLMockObject;
import europass.ewa.model.Namespace;
import europass.ewa.model.PrintingPreference;
import europass.ewa.model.SkillsPassport;
import europass.ewa.model.conversion.ModelModule;

public class ODTFormatTest {

    private static Injector injector = null;
    private static Map<String, PrintingPreference> eclBundleMap = null;

    private static SkillsPassport espFull = CLMockObject.letterLocalisation();
    private static SkillsPassport espOnlyDate = CLMockObject.letterLocalisation();
    private static SkillsPassport espOnlyPlace = CLMockObject.letterLocalisation();

    @BeforeClass
    public static void init() throws Exception {

        injector = Guice.createInjector(
                new AbstractModule() {
            @Override
            protected void configure() {
                bindConstant().annotatedWith(Names.named(Namespace.SCHEMA_LOCATION_DEFAULT_PARAM))
                        .to(Namespace.NAMESPACE + " http://europass.cedefop.europa.eu/xml/EuropassSchema_V3.0.xsd");
            }
        },
                new ModelModule()
        );

        eclBundleMap
                = injector.getProvider(Key.get(new TypeLiteral<Map<String, PrintingPreference>>() {
                },
                        Names.named(ModelModule.DEFAULT_CL_PREFS))).get();

        espFull = CLMockObject.letterLocalisation();
        espOnlyDate = CLMockObject.letterLocalisation();
        espOnlyDate.getCoverLetter().getLetter().getLocalisation().setPlace(null);
        espOnlyPlace = CLMockObject.letterLocalisation();
        espOnlyPlace.getCoverLetter().getLetter().getLocalisation().setDate(null);
    }
    private static final String DATE = "15 Oct 2013";
    private static final String DATE_PLACE = "15 Oct 2013, Birmingham";
    private static final String DATE_PLACE_PREF = "Date Place";
    private static final String PLACE = "Birmingham";
    private static final String PLACE_DATE = "Birmingham, 15 Oct 2013";
    private static final String PLACE_DATE_PREF = "Place Date";

    @Test
    public void fullLetterLocalisation() {
        String txt = espFull.getCoverLetter().getLetter().getLocalisation().localisationTxt();
        Assert.assertThat(txt, CoreMatchers.is(PLACE_DATE));
    }

    @Test
    public void fullLetterLocalisationWithPref1() {
        addPreferenceOrder(espFull, DATE_PLACE_PREF);
        String txt = espFull.getCoverLetter().getLetter().getLocalisation().localisationTxt();
        Assert.assertThat(txt, CoreMatchers.is(DATE_PLACE));
    }

    @Test
    public void fullLetterLocalisationWithPref2() {
        addPreferenceOrder(espFull, PLACE_DATE_PREF);
        String txt = espFull.getCoverLetter().getLetter().getLocalisation().localisationTxt();
        Assert.assertThat(txt, CoreMatchers.is(PLACE_DATE));
    }

    @Test
    public void onlyDateLetterLocalisation() {
        String txt = espOnlyDate.getCoverLetter().getLetter().getLocalisation().localisationTxt();
        Assert.assertThat(txt, CoreMatchers.is(DATE));
    }

    @Test
    public void onlyDateLetterLocalisationWithPref1() {
        addPreferenceOrder(espOnlyDate, DATE_PLACE_PREF);
        String txt = espOnlyDate.getCoverLetter().getLetter().getLocalisation().localisationTxt();
        Assert.assertThat(txt, CoreMatchers.is(DATE));
    }

    @Test
    public void onlyDateLetterLocalisationWithPref2() {
        addPreferenceOrder(espOnlyDate, PLACE_DATE_PREF);
        String txt = espOnlyDate.getCoverLetter().getLetter().getLocalisation().localisationTxt();
        Assert.assertThat(txt, CoreMatchers.is(DATE));
    }

    @Test
    public void onlyPlaceLetterLocalisation() {
        String txt = espOnlyPlace.getCoverLetter().getLetter().getLocalisation().localisationTxt();
        Assert.assertThat(txt, CoreMatchers.is(PLACE));
    }

    @Test
    public void onlyPlaceLetterLocalisationWithPref1() {
        addPreferenceOrder(espOnlyPlace, DATE_PLACE_PREF);
        String txt = espOnlyPlace.getCoverLetter().getLetter().getLocalisation().localisationTxt();
        Assert.assertThat(txt, CoreMatchers.is(PLACE));
    }

    @Test
    public void onlyPlaceLetterLocalisationWithPref2() {
        addPreferenceOrder(espOnlyPlace, PLACE_DATE_PREF);
        String txt = espOnlyPlace.getCoverLetter().getLetter().getLocalisation().localisationTxt();
        Assert.assertThat(txt, CoreMatchers.is(PLACE));
    }

    private void addPreferenceOrder(SkillsPassport esp, String order) {
        List<PrintingPreference> prefs = new ArrayList<PrintingPreference>();
        prefs.add(new PrintingPreference("CoverLetter.Letter.Localisation", true, order, null, null));
        Map<String, List<PrintingPreference>> map = new HashMap<String, List<PrintingPreference>>();
        map.put("ECL", prefs);
        esp.setDocumentPrintingPrefs(map);

        esp.activatePreferences("ECL", eclBundleMap);
        esp.applyDefaultPreferences("ECL");
    }
}
