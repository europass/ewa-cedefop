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
import europass.ewa.Constants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author jos Created on 7/18/2017
 */
public class JSONExportResource {

    private static final Logger LOG = LoggerFactory.getLogger(JSONExportResource.class);

    private final String repoPath;

    public static final String JSON_EXTENSION_FILETYPE = ".json";

    @Inject
    public JSONExportResource(@Named("europass-ewa-services.files.export.repository.json.path") String repoPath) {
        this.repoPath = repoPath;
    }

    public void exportJSON(final String json, final String cookieUserID) {

        if (!StringUtils.isEmpty(cookieUserID)) {

            final String fileId = cookieUserID + "-"
                    + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date())
                    + JSON_EXTENSION_FILETYPE;

            final String filename = repoPath + File.separator + fileId;
            final File fi = new File(filename);

            if (!fi.exists()) {
                try {
                    final String jsonDecoded = java.net.URLDecoder.decode(json, Constants.UTF8_ENCODING);
                    final byte[] jsonBytes = jsonDecoded.getBytes(Constants.UTF8_ENCODING);
                    FileUtils.writeByteArrayToFile(new File(filename), jsonBytes);
                } catch (final UnsupportedEncodingException e) {
                    LOG.error("Not supported encoding during writing JSON ...", e);
                } catch (final IOException e) {
                    LOG.error("IO Exception during writing JSON ...", e);
                }
            }
        }
    }
}
