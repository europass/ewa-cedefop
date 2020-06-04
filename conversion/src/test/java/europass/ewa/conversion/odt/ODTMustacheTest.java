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
package europass.ewa.conversion.odt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.io.FileUtils;
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

import europass.ewa.Utils;
import europass.ewa.conversion.modules.ConversionModule;
import europass.ewa.enums.EuropassDocumentType;
import europass.ewa.model.CLMockObject;
import europass.ewa.model.CV;
import europass.ewa.model.ESP;
import europass.ewa.model.FileData;
import europass.ewa.model.LPMockObjects;
import europass.ewa.model.MockObjects;
import europass.ewa.model.Namespace;
import europass.ewa.model.PrintingPreference;
import europass.ewa.model.SkillsPassport;
import europass.ewa.model.conversion.ModelModule;
import europass.ewa.resources.JsonResourceBundle;
import europass.ewa.resources.ResourceBundleMap;

public class ODTMustacheTest {

    private static Injector injector = null;

    private static Map<String, PrintingPreference> ecvBundleMap = null;
    private static Map<String, PrintingPreference> elpBundleMap = null;
    private static Map<String, PrintingPreference> eclBundleMap = null;

    private static ODTGenerator cvOdtGenerator = null;
    private static ODTGenerator espOdtGenerator = null;
    private static ODTGenerator elpOdtGenerator = null;
    private static ODTGenerator eclOdtGenerator = null;

    private boolean delete = true;

    @BeforeClass
    public static void initFactory() throws Exception {

        injector = Guice.createInjector(
                new AbstractModule() {
            @Override
            protected void configure() {
                bindConstant().annotatedWith(Names.named(Namespace.SCHEMA_LOCATION_DEFAULT_PARAM))
                        .to(Namespace.NAMESPACE + " http://europass.cedefop.europa.eu/xml/EuropassSchema_V3.0.xsd");

                bindConstant().annotatedWith(
                        Names.named(ConversionModule.ODT_BASE_PATH_CV)).to("odt/cv");
                bindConstant().annotatedWith(
                        Names.named(ConversionModule.ODT_BASE_PATH_ESP)).to("odt/esp");
                bindConstant().annotatedWith(
                        Names.named(ConversionModule.ODT_BASE_PATH_ELP)).to("odt/elp");
                bindConstant().annotatedWith(
                        Names.named(ConversionModule.ODT_BASE_PATH_ECL)).to("odt/ecl");
            }
        },
                new ModelModule(),
                new ConversionModule()
        );

        cvOdtGenerator
                = injector.getInstance(Key.get(ODTGenerator.class, CV.class));
        elpOdtGenerator
                = injector.getInstance(Key.get(ODTGenerator.class, ELP.class));
        espOdtGenerator
                = injector.getInstance(Key.get(ODTGenerator.class, ESP.class));
        eclOdtGenerator
                = injector.getInstance(Key.get(ODTGenerator.class, ECL.class));

        ecvBundleMap
                = injector.getProvider(Key.get(new TypeLiteral<Map<String, PrintingPreference>>() {
                },
                        Names.named(ModelModule.DEFAULT_CV_PREFS))).get();
        elpBundleMap
                = injector.getProvider(Key.get(new TypeLiteral<Map<String, PrintingPreference>>() {
                },
                        Names.named(ModelModule.DEFAULT_LP_PREFS))).get();
        eclBundleMap
                = injector.getProvider(Key.get(new TypeLiteral<Map<String, PrintingPreference>>() {
                },
                        Names.named(ModelModule.DEFAULT_CL_PREFS))).get();
    }

    @Test
    public void cv() throws Exception {
        Assert.assertNotNull(cvOdtGenerator);

        String filePath = System.getProperty("user.home") + File.separator + "junit-test-ewa-ecv.odt";

        FileOutputStream fos = new FileOutputStream(filePath);

        SkillsPassport esp = MockObjects.completeNoPrefs();

        esp.getAttachmentsInfo().setShowable(true);

        esp.activatePreferences("ECV", ecvBundleMap);
        esp.applyDefaultPreferences("ECV");

        cvOdtGenerator.generate(fos, esp);

        File doc = new File(filePath);
        Assert.assertThat("CV File exists: ", doc.exists(), CoreMatchers.is(true));
        Assert.assertThat("CV File and is non empty: ", doc.length() > 0, CoreMatchers.is(true));

        if (delete) {
            FileUtils.deleteQuietly(doc);
            Assert.assertThat("CV File is deleted: ", doc.exists(), CoreMatchers.is(false));
        }
    }

    @Test
    public void cvWithCl() throws Exception {
        Assert.assertNotNull(cvOdtGenerator);

        String filePath
                = System.getProperty("user.home") + File.separator
                + "junit-test-ewa-ecv-with-ecl.odt";

        FileOutputStream fos = new FileOutputStream(filePath);

        SkillsPassport esp = MockObjects.completeNoPrefs();
        esp.setCoverLetter(CLMockObject.fullCL().getCoverLetter());

        List<EuropassDocumentType> bundle = new ArrayList<>();
        bundle.add(EuropassDocumentType.ECL);
        esp.getDocumentInfo().setBundle(bundle);

        esp.getAttachmentsInfo().setShowable(true);

        esp.activatePreferences("ECV", ecvBundleMap);
        esp.applyDefaultPreferences("ECV");
        esp.activatePreferences("ECL", eclBundleMap);
        esp.applyDefaultPreferences("ECL");

        cvOdtGenerator.generate(fos, esp);

        File doc = new File(filePath);
        Assert.assertThat("CV+CL File exists: ", doc.exists(), CoreMatchers.is(true));
        Assert.assertThat("CV+CL File and is non empty: ", doc.length() > 0, CoreMatchers.is(true));

        if (delete) {
            FileUtils.deleteQuietly(doc);
            Assert.assertThat("CV File is deleted: ", doc.exists(), CoreMatchers.is(false));
        }
    }

    @Test
    public void lp() throws Exception {
        Assert.assertNotNull(elpOdtGenerator);

        String filePath = System.getProperty("user.home") + File.separator + "junit-test-ewa-elp.odt";

        FileOutputStream fos = new FileOutputStream(filePath);

        SkillsPassport esp = LPMockObjects.elpSkillsObj;

        esp.getAttachmentsInfo().setShowable(true);

        esp.activatePreferences("ELP", elpBundleMap);
        esp.applyDefaultPreferences("ELP");

        elpOdtGenerator.generate(fos, esp);

        File doc = new File(filePath);
        Assert.assertThat("LP File exists: ", doc.exists(), CoreMatchers.is(true));
        Assert.assertThat("LP File and is non empty: ", doc.length() > 0, CoreMatchers.is(true));

        if (delete) {
            FileUtils.deleteQuietly(doc);
            Assert.assertThat("LP File is deleted: ", doc.exists(), CoreMatchers.is(false));
        }
    }

    @Test
    public void esp() throws Exception {
        Assert.assertNotNull(espOdtGenerator);

        String filePath = System.getProperty("user.home") + File.separator + "junit-test-ewa-esp.odt";

        FileOutputStream fos = new FileOutputStream(filePath);

        SkillsPassport esp = MockObjects.completeNoPrefs();

        espOdtGenerator.generate(fos, esp);

        File doc = new File(filePath);
        Assert.assertThat("ESP File exists: ", doc.exists(), CoreMatchers.is(true));
        Assert.assertThat("ESP File and is non empty: ", doc.length() > 0, CoreMatchers.is(true));

        if (delete) {
            FileUtils.deleteQuietly(doc);
            Assert.assertThat("ESP File is deleted: ", doc.exists(), CoreMatchers.is(false));
        }
    }

    @Test
    public void ecl() throws Exception {
        eclWithLocale(Locale.ENGLISH, CLMockObject.ECL_OBJECTS.FULL, null);
    }

    @Test
    public void eclVariousContent() throws Exception {
        eclWithLocale(Locale.ENGLISH, CLMockObject.ECL_OBJECTS.NAME_ONLY, null);
        eclWithLocale(Locale.ENGLISH, CLMockObject.ECL_OBJECTS.NAME_ADDRESS, null);
        eclWithLocale(Locale.ENGLISH, CLMockObject.ECL_OBJECTS.NAME_CONTACT, null);
        eclWithLocale(Locale.ENGLISH, CLMockObject.ECL_OBJECTS.NAME_ADDRESS_CONTACT, null);
        eclWithLocale(Locale.ENGLISH, CLMockObject.ECL_OBJECTS.FULL, null);
    }

    @Test
    public void eclFullVariousLocales() throws Exception {
        eclWithLocale(Locale.FRENCH, CLMockObject.ECL_OBJECTS.FULL, null);
        eclWithLocale(Locale.GERMAN, CLMockObject.ECL_OBJECTS.FULL, null);
        eclWithLocale(Locale.ITALIAN, CLMockObject.ECL_OBJECTS.FULL, null);
    }

    @Test
    public void eclFullVariousLocalesNoDefaults() throws Exception {
        eclWithLocaleNoDefaultPrefs(new Locale("lv"), CLMockObject.ECL_OBJECTS.FULL, null);
    }

    private void eclWithLocale(Locale locale, CLMockObject.ECL_OBJECTS content, List<PrintingPreference> prefs) throws Exception {
        Assert.assertNotNull(eclOdtGenerator);

        String filePath = System.getProperty("user.home") + File.separator + "junit-test-ewa-ecl-" + locale.getLanguage() + "-" + content + ".odt";

        FileOutputStream fos = new FileOutputStream(filePath);
        SkillsPassport esp = CLMockObject.eclObj(content);
        esp.setLocale(locale);

        esp.activatePreferences("ECL", eclBundleMap);
        esp.applyDefaultPreferences("ECL");
//		espSignature(esp);

        eclOdtGenerator.generate(fos, esp);

        File doc = new File(filePath);
        Assert.assertThat("ECL File exists: ", doc.exists(), CoreMatchers.is(true));
        Assert.assertThat("ECL File and is non empty: ", doc.length() > 0, CoreMatchers.is(true));

        if (delete) {
            FileUtils.deleteQuietly(doc);
            Assert.assertThat("ECL File is deleted: ", doc.exists(), CoreMatchers.is(false));
        }
    }

    private void eclWithLocaleNoDefaultPrefs(Locale locale, CLMockObject.ECL_OBJECTS content, List<PrintingPreference> prefs) throws Exception {
        Assert.assertNotNull(eclOdtGenerator);

        String filePath = System.getProperty("user.home") + File.separator + "junit-test-ewa-ecl-" + locale.getLanguage() + "-" + content + "-noDefaultPrefs.odt";

        FileOutputStream fos = new FileOutputStream(filePath);
        SkillsPassport esp = CLMockObject.eclObj(content);
        esp.setLocale(locale);

        ResourceBundle bundle = ResourceBundle.getBundle("preferences/CLDefaultPrintingPreferences", esp.getLocale(),
                new JsonResourceBundle.Control(ModelModule.bundleObjectMapper()));

        Map<String, PrintingPreference> localeBundle = new ResourceBundleMap<PrintingPreference>(bundle);

        esp.activatePreferences("ECL", localeBundle);
        esp.applyDefaultPreferences("ECL");

        eclOdtGenerator.generate(fos, esp);

        File doc = new File(filePath);
        Assert.assertThat("ECL File exists: ", doc.exists(), CoreMatchers.is(true));
        Assert.assertThat("ECL File and is non empty: ", doc.length() > 0, CoreMatchers.is(true));

        if (delete) {
            FileUtils.deleteQuietly(doc);
            Assert.assertThat("ECL File is deleted: ", doc.exists(), CoreMatchers.is(false));
        }
    }

    @SuppressWarnings("unused")
    private void espSignature(SkillsPassport esp) {
        try {

            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

            InputStream inStream = classLoader.getResourceAsStream("images/signature.png");

            URL url = classLoader.getResource("images/signature.png");

            File signatureFile = Utils.writeInputStreamToFile(inStream, url.getPath());

            byte[] contents = FileUtils.readFileToByteArray(signatureFile);
            String path = signatureFile.getAbsolutePath();

            String thePath = "file://" + path;
            thePath = URLEncoder.encode(thePath, "UTF-8");

            FileUtils.forceDelete(signatureFile);

            FileData fd = new FileData("image/png", thePath, contents);
            esp.getLearnerInfo().getIdentification().setSignature(fd);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
