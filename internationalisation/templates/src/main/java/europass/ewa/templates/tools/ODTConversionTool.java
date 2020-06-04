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
package europass.ewa.templates.tools;

import com.google.inject.AbstractModule;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.xml.bind.JAXBException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;

import europass.ewa.conversion.modules.ConversionModule;
import europass.ewa.model.Namespace;
import europass.ewa.model.conversion.ModelModule;
import europass.ewa.templates.OnlineTemplateTranslator;
import europass.ewa.templates.TemplateTranslator;
import java.util.ArrayList;
import java.util.List;

public class ODTConversionTool {

    private static final int ARRAY_SIZE = 1024;

    private static CommandLine cli;

    private static final String PATH = "C:/test-cedefop/";

    /**
     * @param args
     * @throws URISyntaxException
     * @throws JAXBException
     *
     */
    public static void main(String[] args) throws JAXBException, URISyntaxException {

        // parse the locales arguments
        Locale[] locales = getLocales();

        Injector injector = Guice.createInjector(
                new AbstractModule() {
            @Override
            protected void configure() {
                bindConstant().annotatedWith(Names.named(Namespace.SCHEMA_LOCATION_DEFAULT_PARAM))
                        .to(Namespace.NAMESPACE + " http://europass.cedefop.europa.eu/xml/EuropassSchema_V3.0.xsd");
                bindConstant().annotatedWith(
                        Names.named(ConversionModule.ODT_BASE_PATH_CV)).to("C:\\projects\\epas\\europass-editors\\conversion\\src\\main\\resources\\odt\\cv");
                bindConstant().annotatedWith(
                        Names.named(ConversionModule.ODT_BASE_PATH_ESP)).to("C:\\projects\\epas\\europass-editors\\conversion\\src\\main\\resources\\odt\\esp");
                bindConstant().annotatedWith(
                        Names.named(ConversionModule.ODT_BASE_PATH_ELP)).to("C:\\projects\\epas\\europass-editors\\conversion\\src\\main\\resources\\odt\\elp");
                bindConstant().annotatedWith(
                        Names.named(ConversionModule.ODT_BASE_PATH_ECL)).to("C:\\projects\\epas\\europass-editors\\conversion\\src\\main\\resources\\odt\\ecl");
            }
        },
                new ModelModule(),
                new ConversionModule());

        TemplateTranslator translator = injector.getInstance(OnlineTemplateTranslator.class);

        try {
            for (Locale locale : locales) {
                System.out.println("Generating template for locale " + locale.toString());

                translator.translate(locale);

                System.out.println("Unzipping resources from " + PATH + "result_" + locale.toString() + ".jar"
                        + " to " + PATH + "odt/cv/" + locale.toString());
                unZipIt(PATH + "result_" + locale.toString() + ".jar",
                        PATH + "odt/cv/" + locale.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static Locale[] getLocales() {
        //String contentLocalePropertiesUrl = getResourceUrl("ContentLocale", defaultLocale);

        List availableLocales = new ArrayList();
//		TODO: Waiting for all files to be imported to zanata
//		try (InputStream in = new URL(contentLocalePropertiesUrl).openStream()) {
//			Properties properties = new Properties();
//			properties.load(in);
//			for (Iterator iterator = properties.keySet().iterator(); iterator.hasNext();) {
//				String language = (String) iterator.next();
//				availableLocales.add(new Locale(language));
//			}
//		} catch (IOException ex) {
//			LOG.error("Exception reading properties file from " + contentLocalePropertiesUrl
//				+ " - " + ex.getMessage());
//			availableLocales = new HashSet<>();
//		}

        //for testing.. 
        availableLocales.add(new Locale("en", "UK"));
        availableLocales.add(new Locale("fr", "FR"));
        availableLocales.add(new Locale("es", "ES"));
        availableLocales.add(new Locale("fi", "FI"));

        Locale[] stockArr = new Locale[availableLocales.size()];
        stockArr = (Locale[]) availableLocales.toArray(stockArr);
        return stockArr;
    }

    /**
     * Unzip it
     *
     * @param zipFile input zip file
     * @param outputFolder zip file output folder
     */
    public static void unZipIt(String zipFile, String outputFolder) {

        byte[] buffer = new byte[ARRAY_SIZE];

        try {

            // create output directory is not exists
            File folder = new File(outputFolder);
            if (!folder.exists()) {
                folder.mkdir();
            }

            // get the zip file content
            ZipInputStream zis = new ZipInputStream(
                    new FileInputStream(zipFile));
            // get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();

            while (ze != null) {

                String fileName = ze.getName();
                File newFile = new File(outputFolder + File.separator
                        + fileName);

                System.out.println("file unzip : " + newFile.getAbsoluteFile());

                // create all non exists folders
                // else you will hit FileNotFoundException for compressed folder
                new File(newFile.getParent()).mkdirs();

                FileOutputStream fos = new FileOutputStream(newFile);

                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }

                fos.close();
                ze = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();

            System.out.println("Done");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
