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

import org.apache.commons.lang.ArrayUtils;

import com.google.common.io.ByteStreams;

import europass.ewa.conversion.manifest.FileEntry;

public class ODTFileDataEntry implements ODTEntry {

    private FileEntry manifestEntry;

    private byte[] bytes;

    public ODTFileDataEntry(String name, String mediaType, byte[] bytes) {
        this.manifestEntry = new FileEntry(name, mediaType);
        this.bytes = ArrayUtils.clone(bytes);
    }

    @Override
    public FileEntry getManifestEntry() {
        return manifestEntry;
    }

    @Override
    public void execute(ZipOutputStream zout, Object document) throws IOException {
        if (zout == null) {
            return;
        }
        if (bytes == null) {
            return;
        }
        if (manifestEntry == null) {
            return;
        }
        ZipEntry entry = new ZipEntry(manifestEntry.getFullPath());
        zout.putNextEntry(entry);
        ByteStreams.copy(ByteStreams.newInputStreamSupplier(bytes), zout);
        zout.closeEntry();
    }

}
