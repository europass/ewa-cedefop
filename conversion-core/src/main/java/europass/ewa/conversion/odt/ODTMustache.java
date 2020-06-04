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

import java.io.IOException;
import java.util.zip.ZipOutputStream;

import javax.xml.bind.JAXBContext;

import com.google.common.collect.ObjectArrays;

import europass.ewa.conversion.manifest.FileEntry;
import europass.ewa.conversion.manifest.Manifest;

public class ODTMustache {

    private final JAXBContext jaxb;
    private final String name;

    private final ODTEntry[] entries;

    ODTMustache(JAXBContext jaxb, String name, ODTEntry... entries) {
        super();
        this.name = name;
        this.jaxb = jaxb;
        this.entries = entries;
    }

    public String getName() {
        return name;
    }

    public ODTMustache add(ODTEntry... newEntries) {

        ODTEntry[] allEntries = ObjectArrays.concat(entries, newEntries, ODTEntry.class);
        return new ODTMustache(jaxb, name, allEntries);
    }

    public void execute(ZipOutputStream zout, Object document) throws IOException {
        Manifest manifest = new Manifest();
        FileEntry baseEntry = new FileEntry("/", "application/vnd.oasis.opendocument.text");
        baseEntry.setVersion("1.2");
        manifest.getEntries().add(baseEntry);
        for (ODTEntry entry : entries) {
            FileEntry fileEntry = entry.getManifestEntry();
            if (fileEntry != null) {
                manifest.getEntries().add(fileEntry);
            }
            entry.execute(zout, document);
        }
        new ODTManifestEntry(jaxb, manifest).execute(zout, document);
    }
}
