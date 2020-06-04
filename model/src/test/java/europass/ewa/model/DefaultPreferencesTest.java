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

import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.hamcrest.CoreMatchers;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import europass.ewa.enums.EuropassDocumentType;
import europass.ewa.model.conversion.ModelModule;
import europass.ewa.resources.JsonResourceBundle;
import europass.ewa.resources.ResourceBundleMap;

public class DefaultPreferencesTest {

    static Map<String, PrintingPreference> cvBundleMap;

    static int cvSize;

    static Map<String, PrintingPreference> lpBundleMap;

    static int lpSize;

    static Map<String, PrintingPreference> clBundleMap;

    static int clSize;

    static Map<EuropassDocumentType, Map<String, PrintingPreference>> defaultPrefs;

    private static Injector injector = null;

    @BeforeClass
    public static void setDefaultPrefs() {
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

        cvBundleMap
                = injector.getProvider(Key.get(new TypeLiteral<Map<String, PrintingPreference>>() {
                },
                        Names.named(ModelModule.DEFAULT_CV_PREFS))).get();
        lpBundleMap
                = injector.getProvider(Key.get(new TypeLiteral<Map<String, PrintingPreference>>() {
                },
                        Names.named(ModelModule.DEFAULT_LP_PREFS))).get();

        clBundleMap
                = injector.getProvider(Key.get(new TypeLiteral<Map<String, PrintingPreference>>() {
                },
                        Names.named(ModelModule.DEFAULT_CL_PREFS))).get();

        defaultPrefs = new HashMap<EuropassDocumentType, Map<String, PrintingPreference>>();
        defaultPrefs.put(EuropassDocumentType.ECV, cvBundleMap);
        defaultPrefs.put(EuropassDocumentType.ELP, lpBundleMap);
        defaultPrefs.put(EuropassDocumentType.ECL, clBundleMap);

        cvSize = cvBundleMap.keySet().size();

        lpSize = lpBundleMap.keySet().size();

        //Attention CoverLetter.Documentation is only used to decide whether to use a simple input fields or a drop down in the list of headings for the "Enclosed" field.
        //The XSD does not define that such a preference name should exist.
        //Therefore the Java Model would not populate a Preference for CoverLetter.GenericDocumentation
        clSize = clBundleMap.keySet().size() - 1;
    }

    @Test
    public void cvWithoutDocType() throws Exception {

        SkillsPassport model = new SkillsPassport();
        Map<String, List<PrintingPreference>> prefs = new HashMap<String, List<PrintingPreference>>();
        model.setDocumentPrintingPrefs(prefs);

        model.activatePreferences("ECV", cvBundleMap);
        model.applyDefaultPreferences("ECV");

        assertThat("Preferences Per Document",
                model.getDocumentPrintingPrefs().keySet().size(),
                CoreMatchers.is(1));
        assertThat("Preferences Size: ",
                model.getDocumentPrintingPrefs().get("ECV").size(),
                CoreMatchers.is(cvSize));
    }

    @Test
    public void cv() throws Exception {

        SkillsPassport model = new SkillsPassport();

        DocumentInfo docInfo = new DocumentInfo();
        docInfo.setDocumentType(EuropassDocumentType.ECV);
        model.setDocumentInfo(docInfo);

        Map<String, List<PrintingPreference>> prefs = new HashMap<String, List<PrintingPreference>>();
        model.setDocumentPrintingPrefs(prefs);

        model.activatePreferences("ECV", cvBundleMap);
        model.applyDefaultPreferences();

        assertThat("Preferences Per Document",
                model.getDocumentPrintingPrefs().keySet().size(),
                CoreMatchers.is(1));
        assertThat("Preferences Size: ",
                model.getDocumentPrintingPrefs().get("ECV").size(),
                CoreMatchers.is(cvSize));
    }

    @Test
    public void lp() throws Exception {

        SkillsPassport model = new SkillsPassport();
        Map<String, List<PrintingPreference>> prefs = new HashMap<String, List<PrintingPreference>>();
        model.setDocumentPrintingPrefs(prefs);

        model.activatePreferences("ELP", lpBundleMap);
        model.applyDefaultPreferences("ELP");

        assertThat("Preferences Per Document",
                model.getDocumentPrintingPrefs().keySet().size(),
                CoreMatchers.is(1));
        assertThat("Preferences Size: ",
                model.getDocumentPrintingPrefs().get("ELP").size(),
                CoreMatchers.is(lpSize));
    }

    @Test
    public void cvDocWithPrefs() throws Exception {
        SkillsPassport model = new SkillsPassport();

        DocumentInfo docInfo = new DocumentInfo();
        docInfo.setDocumentType(EuropassDocumentType.ECV);
        model.setDocumentInfo(docInfo);

        LearnerInfo learnerinfo = new LearnerInfo();
        model.setLearnerInfo(learnerinfo);

        Map<String, List<PrintingPreference>> prefs = new HashMap<String, List<PrintingPreference>>();
        model.setDocumentPrintingPrefs(prefs);

        model.activatePreferences(defaultPrefs);

        assertThat("Preferences Per Document",
                model.getDocumentPrintingPrefs().keySet().size(), CoreMatchers.is(3));
        assertThat("ECV Preferences Size: ",
                model.getDocumentPrintingPrefs().get("ECV").size(),
                CoreMatchers.is(cvSize));
        assertThat("ELP Preferences Size: ",
                model.getDocumentPrintingPrefs().get("ELP").size(),
                CoreMatchers.is(lpSize));
        assertThat("ECL Preferences Size: ",
                model.getDocumentPrintingPrefs().get("ECL").size(),
                CoreMatchers.is(clSize));

        assertThat("LearnerInfo.order",
                model.getLearnerInfo().pref().getOrder(),
                CoreMatchers.is("Identification Headline WorkExperience Education Skills Achievement ReferenceTo"));
    }

    @Test
    public void lpDocWithPrefsCvAndLp() throws Exception {
        SkillsPassport model = new SkillsPassport();

        DocumentInfo docInfo = new DocumentInfo();
        docInfo.setDocumentType(EuropassDocumentType.ELP);
        model.setDocumentInfo(docInfo);

        LearnerInfo learnerinfo = new LearnerInfo();
        model.setLearnerInfo(learnerinfo);

        Map<String, List<PrintingPreference>> prefs = new HashMap<String, List<PrintingPreference>>();
        model.setDocumentPrintingPrefs(prefs);

        model.activatePreferences(defaultPrefs);

        assertThat("Preferences Per Document",
                model.getDocumentPrintingPrefs().keySet().size(), CoreMatchers.is(3));
        assertThat("ECV Preferences Size: ",
                model.getDocumentPrintingPrefs().get("ECV").size(),
                CoreMatchers.is(cvSize));
        assertThat("ELP Preferences Size: ",
                model.getDocumentPrintingPrefs().get("ELP").size(),
                CoreMatchers.is(lpSize));
        assertThat("ECL Preferences Size: ",
                model.getDocumentPrintingPrefs().get("ECL").size(),
                CoreMatchers.is(clSize));

        assertThat("LearnerInfo.order", model.getLearnerInfo().pref().getOrder(), CoreMatchers.is("Identification Skills ReferenceTo"));
    }

    @Test
    public void cl() throws Exception {

        SkillsPassport model = CLMockObject.addressee();

        Map<String, List<PrintingPreference>> prefs = new HashMap<String, List<PrintingPreference>>();
        model.setDocumentPrintingPrefs(prefs);

        model.activatePreferences("ECL", clBundleMap);
        model.applyDefaultPreferences("ECL");

        assertThat("Preferences Per Document",
                model.getDocumentPrintingPrefs().keySet().size(),
                CoreMatchers.is(1));
        //Size is not exactly equal to the prefs of ECL, because the CoverLetter.Documentation is not included
        assertThat("Preferences Size: ",
                model.getDocumentPrintingPrefs().get("ECL").size(),
                CoreMatchers.is(clSize));

        assertThat("CoverLetter.Addressee.PersonName.order", model.getCoverLetter().getAddressee().getPersonName().pref().getOrder(), CoreMatchers.is("Title FirstName Surname"));
        assertThat("CoverLetter.Justification.justify", model.getCoverLetter().isCoverLetterJustified(), CoreMatchers.is(false));

    }

    @Test
    public void clWithAddresseePersonNameDefPrefs() throws Exception {

        SkillsPassport model = CLMockObject.fullCL();

        Map<String, List<PrintingPreference>> prefs = new HashMap<String, List<PrintingPreference>>();
        model.setDocumentPrintingPrefs(prefs);

        model.activatePreferences("ECL", clBundleMap);
        model.applyDefaultPreferences("ECL");

        assertThat("Preferences Per Document",
                model.getDocumentPrintingPrefs().keySet().size(),
                CoreMatchers.is(1));
        //Size is not exactly equal to the prefs of ECL, because the CoverLetter.Documentation is not included
        assertThat("Preferences Size: ",
                model.getDocumentPrintingPrefs().get("ECL").size(),
                CoreMatchers.is(clSize));

        assertThat("CoverLetter.Addressee.PersonName.order", model.getCoverLetter().getAddressee().getPersonName().pref().getOrder(), CoreMatchers.is("Title FirstName Surname"));

        assertThat("Addressee nameTxt method has been properly adjusted", model.getCoverLetter().getAddressee().nameTxt(), CoreMatchers.is("Dr. John Stuart"));

    }

    @Test
    public void clWithAddresseePersonNamePrefsInLV() throws Exception {

        SkillsPassport model = CLMockObject.fullCL();
        model.setLocale(new Locale("lv"));

        ResourceBundle bundle = ResourceBundle.getBundle("preferences/CLDefaultPrintingPreferences", model.getLocale(),
                new JsonResourceBundle.Control(ModelModule.bundleObjectMapper()));

        Map<String, PrintingPreference> localeBundle = new ResourceBundleMap<PrintingPreference>(bundle);

        model.activatePreferences("ECL", localeBundle);
        model.applyDefaultPreferences("ECL");

        assertThat("Preferences Per Document",
                model.getDocumentPrintingPrefs().keySet().size(),
                CoreMatchers.is(1));
        //Size is not exactly equal to the prefs of ECL, because the CoverLetter.Documentation is not included
        assertThat("Preferences Size: ",
                model.getDocumentPrintingPrefs().get("ECL").size(),
                CoreMatchers.is(clSize));

        assertThat("CoverLetter.Addressee.PersonName.order", model.getCoverLetter().getAddressee().getPersonName().pref().getOrder(), CoreMatchers.is("FirstName Surname Title"));

        assertThat("Addressee nameTxt method has been properly adjusted in LV", model.getCoverLetter().getAddressee().nameTxt(), CoreMatchers.is("John Stuart Dr."));

    }

}
