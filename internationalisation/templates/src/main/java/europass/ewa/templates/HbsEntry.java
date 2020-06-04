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
package europass.ewa.templates;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.commons.io.FileUtils;

import com.github.mustachejava.Mustache;

import europass.ewa.conversion.manifest.FileEntry;

public class HbsEntry {

    private final Mustache mustache;

    private final FileEntry manifestEntry;

    public HbsEntry(FileEntry manifestEntry, Mustache mustache) {
        this.mustache = mustache;
        this.manifestEntry = manifestEntry;
    }

    public void execute(String baseTargetDir, Object context) throws IOException {

        String path = baseTargetDir + File.separator + manifestEntry.getFullPath();
        File destFile = new File(path);

        if (!destFile.exists()) {
            File parent = destFile.getParentFile();
            if (!parent.exists()) {
                FileUtils.forceMkdir(parent);
            }
            destFile.createNewFile();
        }
        Writer writer = new OutputStreamWriter(new FileOutputStream(destFile), "UTF-8");
        mustache.execute(writer, context);
        writer.flush();
    }

    public FileEntry getManifestEntry() {
        return this.manifestEntry;
    }
}
