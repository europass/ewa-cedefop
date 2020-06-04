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
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import europass.ewa.conversion.manifest.FileEntry;
import europass.ewa.conversion.manifest.Manifest;

public class ODTManifestEntry implements ODTEntry {

    private final JAXBContext jaxb;
    private final Manifest manifest;

    public ODTManifestEntry(JAXBContext jaxb, Manifest manifest) {
        this.jaxb = jaxb;
        this.manifest = manifest;
    }

    @Override
    public FileEntry getManifestEntry() {
        return null;
    }

    @Override
    public void execute(ZipOutputStream zout, Object document)
            throws IOException {
        ZipEntry entry = new ZipEntry("META-INF/manifest.xml");
        zout.putNextEntry(entry);
        try {
            Marshaller marshaller = jaxb.createMarshaller();
            marshaller.marshal(manifest, zout);
        } catch (JAXBException e) {
            throw new IOException("Failed to marshal manifest", e);
        }
        zout.closeEntry();
    }

}
