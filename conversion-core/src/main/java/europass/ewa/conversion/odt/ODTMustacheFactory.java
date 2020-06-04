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

import static java.lang.Thread.currentThread;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import europass.ewa.conversion.manifest.FileEntry;
import europass.ewa.conversion.manifest.Manifest;

public class ODTMustacheFactory {

    private static final int NUMBER_OF_ENTRIES = 9;
    private final MustacheFactory mf;
    private final JAXBContext jaxb;

    private String defaultName = null;

    @Inject
    public ODTMustacheFactory(MustacheFactory mf) throws JAXBException {
        this.mf = mf;
        this.jaxb = JAXBContext.newInstance(Manifest.class);
    }

    public ODTMustache create(String name) throws JAXBException,
            URISyntaxException, IOException {
        return create(name, false);
    }

    /**
     * Used to set a default name for this factory, in case the name supplied
     * {@link create} method does not exist as template
     *
     * @param defaultName
     */
    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
    }

    public ODTMustache create(String name, boolean keepMustacheExtensions)
            throws JAXBException, URISyntaxException, IOException {
        List<ODTEntry> entries = new ArrayList<ODTEntry>();
        ClassLoader cl = currentThread().getContextClassLoader();

        //Use the name as provided...
        URL url = cl.getResource(name + "/META-INF/manifest.xml");
        String baseName = name;

        //If no such template is found, use the default if it exists, or throw exception...
        if (url == null) {
            if (defaultName == null || defaultName.isEmpty()) {
                String msg = "ODTMustacheFactory:create - Failed to create ODTMustache factory because there is no template for the specific locale, '"
                        + name
                        + "', and also there is no template for the default locale.";
                throw new IllegalArgumentException(msg);
            } else {
                baseName = defaultName; //to make sure that the rest of the code works with the default template 
                url = cl.getResource(defaultName + "/META-INF/manifest.xml");
            }
        }

        Unmarshaller unmarshaller = jaxb.createUnmarshaller();
        Manifest manifest = (Manifest) unmarshaller.unmarshal(url);

        entries.add(new ODTMimeTypeEntry(NUMBER_OF_ENTRIES));
        int ec = 1;
        for (FileEntry entry : manifest.getEntries()) {
            String fp = entry.getFullPath();
            if (fp.equals("/") || fp.equals("mimetype")) {
                continue;
            }

            String entryPath = baseName + "/" + entry.getFullPath();
            URL entryURL = cl.getResource(entryPath);
            if (entryURL != null) {
                entries.add(new ODTResourceEntry(entry, baseName));
                ec++;
            } else {
                // strip possible extension
                int dotIdx = entryPath.lastIndexOf('.');
                if (dotIdx > 0) {
                    entryPath = entryPath.substring(0, dotIdx);
                }
                // append the mustache extension
                entryPath = entryPath + ".mustache";
                InputStream entryStream = cl.getResourceAsStream(entryPath);
                InputStreamReader entryReader = new InputStreamReader(
                        entryStream, "UTF-8");
                Mustache entryMustache = mf.compile(entryReader,
                        entry.getFullPath());
                if (keepMustacheExtensions) {
                    entries.add(new ODTMustacheEntry(entry, entryMustache,
                            ".mustache"));
                } else {
                    entries.add(new ODTMustacheEntry(entry, entryMustache));
                }
                ec++;
            }
        }

        return new ODTMustache(jaxb, baseName, entries.toArray(new ODTEntry[ec]));
    }
}
