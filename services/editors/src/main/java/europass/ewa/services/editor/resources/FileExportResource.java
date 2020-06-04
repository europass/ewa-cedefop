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
package europass.ewa.services.editor.resources;

import com.google.inject.name.Named;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.inject.Inject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author at
 */
public class FileExportResource {

    private static final Logger LOG = LoggerFactory.getLogger(FileExportResource.class);

    private final String repoPath;

    @Inject
    public FileExportResource(@Named("europass-ewa-services.files.not.imported.repository.path") String repoPath) {
        this.repoPath = repoPath;
    }

    public void exportFile(byte[] input, final String fileExtension, final String cookieId) {

        if (!StringUtils.isEmpty(cookieId)) {

            final String fileId = cookieId + "-"
                    + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
                    + "." + fileExtension;

            final String filename = repoPath + File.separator + fileId;
            final File fi = new File(filename);

            if (!fi.exists()) {
                try {
                    FileUtils.writeByteArrayToFile(new File(filename), input);
                } catch (final IOException e) {
                    LOG.error("IO Exception during writing file ...", e);
                }
            }
        }
    }

}
