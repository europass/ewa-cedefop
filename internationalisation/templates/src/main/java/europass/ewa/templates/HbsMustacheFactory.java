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

import static java.lang.Thread.currentThread;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

import europass.ewa.Utils;
import europass.ewa.conversion.manifest.FileEntry;

public class HbsMustacheFactory {

    static final String BASE_DIR_NAME = "hbs";

    private final MustacheFactory mf;

    private String defaultName = null;

    final static String DEFAULT_HBS_EXTENSION = ".hbs";

    @Inject
    public HbsMustacheFactory(MustacheFactory mf) {
        this.mf = mf;
    }

    public HbsMustache create(String targetName, String baseName) throws URISyntaxException, IOException {
        return create(targetName, baseName, false);
    }

    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
    }

    public HbsMustache create(String baseTargetDir, String baseDir, boolean keepMustacheExtensions) throws URISyntaxException, IOException {
        List<HbsEntry> entries = new ArrayList<>();

        ClassLoader cl = currentThread().getContextClassLoader();

        //Use the name as provided...
        URL url = cl.getResource(baseDir);
        //If no such dir is found, use the default if it exists, or throw exception...
        if (url == null) {
            if (defaultName == null || defaultName.isEmpty()) {
                String msg = "HbsMustacheFactory:create - Failed to create HbsMustache factory because there is no structure for the specific path, '"
                        + baseDir;
                throw new IllegalArgumentException(msg);
            } else {
                url = cl.getResource(defaultName);
            }
        }

        int ec = 0;

        //Use name to get base directory
        URI baseUri = url.toURI();
        File base = new File(baseUri);
        if (!base.isDirectory()) {
            throw new IllegalArgumentException("The provided base directory at uri:" + baseUri + " is not a directory");
        }
        //Loop to find mustache resources
        File initBase = getParentHbs(base);
        addEntries(initBase, base, entries);

        return new HbsMustache(baseTargetDir, entries.toArray(new HbsEntry[ec]));
    }

    private void addEntries(File initBase, File base, List<HbsEntry> entries) throws FileNotFoundException, UnsupportedEncodingException {
        for (File f : base.listFiles()) {
            if (f.isDirectory()) {
                addEntries(initBase, f, entries);
            } else {
                InputStream entryStream = new FileInputStream(f);
                InputStreamReader entryReader = new InputStreamReader(entryStream, "UTF-8");

                FileEntry entry = new FileEntry(entryName(f, initBase), "text/html");

                Mustache entryMustache = mf.compile(entryReader, f.getName());
                entries.add(new HbsEntry(entry, entryMustache));
            }
        }
    }

    static String entryName(File f, File b) {
        String path = Utils.getRelativePath(f, b);

        return path.substring(0, path.lastIndexOf(".")) + DEFAULT_HBS_EXTENSION;
    }

    static File getParentHbs(File base) {
        if (base.isDirectory() && BASE_DIR_NAME.equals(base.getName())) {
            return base;//.getParentFile();
        } else {
            File parent = getParentHbs(base.getParentFile());
            if (parent != null) {
                return parent;
            }
        }
        return null;
    }

}
