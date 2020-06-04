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
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import com.github.mustachejava.Mustache;

import europass.ewa.conversion.manifest.FileEntry;

public class ODTMustacheEntry implements ODTEntry {

    private final Mustache mustache;

    private final FileEntry manifestEntry;

    private final String extension;

    public ODTMustacheEntry(FileEntry manifestEntry, Mustache mustache, String extension) {
        super();
        this.mustache = mustache;
        this.manifestEntry = manifestEntry;

        if (extension != null && !extension.startsWith(".")) {
            this.extension = "." + extension;
        } else {
            this.extension = extension;
        }
    }

    public ODTMustacheEntry(FileEntry manifestEntry, Mustache mustache) {
        this(manifestEntry, mustache, null);
    }

    @Override
    public FileEntry getManifestEntry() {
        return manifestEntry;
    }

    @Override
    public void execute(ZipOutputStream zout, Object document) throws IOException {
        ZipEntry entry = new ZipEntry(zipEntryName());
        zout.putNextEntry(entry);
        Writer writer = new OutputStreamWriter(zout, "UTF-8");
        mustache.execute(writer, document);
        writer.flush();
        zout.closeEntry();
    }

    protected String zipEntryName() {
        String name = manifestEntry.getFullPath();
        if (extension == null) {
            return name;
        }

        int dotIdx = name.lastIndexOf('.');
        if (dotIdx > 0) {
            name = name.substring(0, dotIdx);
        }
        name += extension;
        return name;
    }

}
