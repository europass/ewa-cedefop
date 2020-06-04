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
import java.io.UnsupportedEncodingException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import europass.ewa.conversion.manifest.FileEntry;

public class ODTMimeTypeEntry implements ODTEntry {

    private static final byte[] MIMETYPE = utf8Bytes("application/vnd.oasis.opendocument.text");

    static byte[] utf8Bytes(String string) {
        try {
            return string.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            assert false;
        }
        return null;
    }
    private int restoreLevel;

    public ODTMimeTypeEntry(int restoreLevel) {
        this.restoreLevel = restoreLevel;
    }

    @Override
    public FileEntry getManifestEntry() {
        return null;
    }

    @Override
    public void execute(ZipOutputStream zout, Object document)
            throws IOException {
        zout.setLevel(0);
        ZipEntry mimetype = new ZipEntry("mimetype");
        zout.putNextEntry(mimetype);
        zout.write(MIMETYPE);
        zout.closeEntry();

        zout.setLevel(restoreLevel);
    }

}
