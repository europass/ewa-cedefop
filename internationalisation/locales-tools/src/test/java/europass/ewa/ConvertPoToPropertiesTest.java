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
package europass.ewa;

import org.fedorahosted.tennera.jgettext.Catalog;
import org.fedorahosted.tennera.jgettext.Message;
import org.fedorahosted.tennera.jgettext.catalog.parse.ExtendedCatalogParser;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This test class consists of conversion tool from zanata .po to .properties
 * files
 *
 */
@Ignore
public class ConvertPoToPropertiesTest {

    private static final String PO_FOLDER_PATH = "/po";
    private static final String PROPERTIES_KEY_VALUE_SEPARATOR = "=";

    private static final String PROPERTIES_FOLDER_PATH = "C:\\tmp\\";

    @Test
    @Ignore
    public void testConvertAllPoFilesIntoProperties() throws Exception {
        final List<String> posToConvert = getFilenames();

        for (final String po : posToConvert) {
            convertPoToProperties(po);
        }
    }

    private void convertPoToProperties(final String pathPo) throws Exception {

        final File poFile = new File(getClass().getResource(PO_FOLDER_PATH + "/" + pathPo).toURI());

        new File(PROPERTIES_FOLDER_PATH).mkdirs();
        final File propertiesFile = new File(PROPERTIES_FOLDER_PATH + pathPo.substring(0, pathPo.lastIndexOf(".")));

        System.out.println("Converting po file " + pathPo + " into " + propertiesFile.getName());

        final PrintWriter writer = new PrintWriter(propertiesFile, "UTF-8");
        final ExtendedCatalogParser parser = new ExtendedCatalogParser(new Catalog(), poFile);
        parser.catalog();
        final Catalog catalog = parser.getCatalog();

        int entryCount = 0;
        for (final Message m : catalog) {
            entryCount++;

            if (entryCount > 1) {
                writer.println(m.getMsgctxt() + PROPERTIES_KEY_VALUE_SEPARATOR + m.getMsgstr());
            }
        }
        writer.close();
    }

    private List<String> getFilenames() throws IOException {

        final List<String> filenames = new ArrayList();
        final InputStream in = getResourceAsStream(PO_FOLDER_PATH);
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
        String resource;
        while ((resource = bufferedReader.readLine()) != null) {
            filenames.add(resource);
        }

        return filenames;
    }

    private InputStream getResourceAsStream(final String resource) {

        final InputStream in = getContextClassLoader().getResourceAsStream(resource);

        return in == null ? getClass().getResourceAsStream(resource) : in;
    }

    private ClassLoader getContextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

}
