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
package europass.ewa.services.editor.jobs;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.zip.ZipOutputStream;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author at
 */
public class CreateZipFromNotImportedFilesJob extends CreateZipFromSpecifiedFilesJob {

    private static final Logger LOG = LoggerFactory.getLogger(CreateZipFromNotImportedFilesJob.class);

    private static final String FILENAME_ZIP_FORMAT = "Europass-Not-Imported-Files";

    private String repoPath;
    private String cleanupTime;
    private String hostID;

    public CreateZipFromNotImportedFilesJob() {
        LOG.debug("Job for creating compressed file from all the not imported CV files");
    }

    @Override
    public void execute(final JobExecutionContext context) throws JobExecutionException {

        LOG.debug("Compressing not imported CV files ...");

        setProperties(context);

        try {
            final String zipFileFormat = FILENAME_ZIP_FORMAT + FILENAME_SEPARATOR
                    + hostID + FILENAME_SEPARATOR
                    + new SimpleDateFormat("yyyyMMdd").format(new Date())
                    + ZIP_EXTENSION_FILETYPE;

            final File folder = new File(repoPath);
            folder.mkdirs();
            final File f = new File(repoPath, zipFileFormat);
            final ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(f));

            final File[] filesToCompress = getFilesForCompression(folder);

            createZipFileFromExportedDocuments(zout, filesToCompress);
            cleanupExtractedDocuments(filesToCompress);

            LOG.debug("Deleting ZIP files that are older than {} days", cleanupTime);
            cleanupOldZipFiles(cleanupTime, folder);

        } catch (FileNotFoundException e) {
            LOG.error("File not exception ", e);
        } catch (IOException e) {
            LOG.error("IO Error exception", e);
        }
    }

    private void setProperties(final JobExecutionContext context) {
        final JobDataMap map = context.getJobDetail().getJobDataMap();
        if (map.get("repoPath") != null) {
            setRepoPath((String) map.get("repoPath"));
        }
        if (map.get("cleanupTime") != null) {
            setCleanupTime((String) map.get("cleanupTime"));
        }
        if (map.get("hostID") != null) {
            setHostID((String) map.get("hostID"));
        }
    }

    private void setRepoPath(final String repoPath) {
        this.repoPath = repoPath;
    }

    private void setCleanupTime(final String cleanupTime) {
        this.cleanupTime = cleanupTime;
    }

    private void setHostID(final String hostID) {
        this.hostID = hostID;
    }
}
