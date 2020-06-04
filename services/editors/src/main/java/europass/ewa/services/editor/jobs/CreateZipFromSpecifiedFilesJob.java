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

import europass.ewa.enums.ServerModules;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.FilenameUtils;
import org.quartz.Job;

/**
 *
 * @author at
 */
public abstract class CreateZipFromSpecifiedFilesJob implements Job {

    public static final String module = ServerModules.SERVICES_EDITORS.getModule();

    protected static final String ZIP_FILETYPE = "zip";
    protected static final String ZIP_EXTENSION_FILETYPE = "." + ZIP_FILETYPE;
    protected static final String FILENAME_SEPARATOR = "-";

    private static final Long TIME_GAP_AVAILABLE_FILE_FOR_COMPRESSION_IN_MINUTES = 2L;

    protected File[] getFilesForCompression(final File folder) {
        return getFilesForCompression(folder, null);
    }

    /**
     * Get only files that are not modified latest 2 minutes!!
     *
     * @param folder
     * @param fileType
     * @return
     */
    protected File[] getFilesForCompression(final File folder, String fileType) {

        final long timeNow = new Date().getTime();

        final List fileListForCompression = new ArrayList<File>();

        for (final File file : folder.listFiles()) {

            final long diffTime = timeNow - file.lastModified();

            boolean isOfSpecifiedFileType = fileType != null && fileType.equalsIgnoreCase(FilenameUtils.getExtension(file.getName()));
            boolean isOtherNonZipFile = fileType == null && !"zip".equalsIgnoreCase(FilenameUtils.getExtension(file.getName()));

            if (file.isFile()
                    && (isOfSpecifiedFileType || isOtherNonZipFile)
                    && diffTime > TimeUnit.MINUTES.toMillis(TIME_GAP_AVAILABLE_FILE_FOR_COMPRESSION_IN_MINUTES)) {
                fileListForCompression.add(file);
            }
        }

        return (File[]) fileListForCompression.toArray(new File[fileListForCompression.size()]);
    }

    protected void createZipFileFromExportedDocuments(final ZipOutputStream zout, final File[] files) throws IOException {

        for (final File file : files) {
            final ZipEntry ze = new ZipEntry(file.getName());
            zout.putNextEntry(ze);

            final FileInputStream inputStr = new FileInputStream(file.getAbsolutePath());
            final byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStr.read(buffer)) > 0) {
                zout.write(buffer, 0, len);
            }

            inputStr.close();
            zout.closeEntry();
        }
        zout.finish();
        zout.close();
    }

    protected void cleanupExtractedDocuments(final File[] files) {
        for (final File file : files) {
            file.delete();
        }
    }

    protected void cleanupOldZipFiles(final String timeInDays, final File folder) {

        final long timeNow = new Date().getTime();

        for (final File file : folder.listFiles()) {
            final long diffTime = timeNow - file.lastModified();

            if (file.isFile()
                    && ZIP_FILETYPE.equalsIgnoreCase(FilenameUtils.getExtension(file.getName()))
                    && diffTime > TimeUnit.DAYS.toMillis(Long.parseLong(timeInDays))) {
                file.delete();
            }
        }
    }

}
