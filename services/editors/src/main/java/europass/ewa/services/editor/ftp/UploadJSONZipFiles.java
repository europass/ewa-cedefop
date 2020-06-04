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
package europass.ewa.services.editor.ftp;

import com.google.inject.name.Named;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.*;

public class UploadJSONZipFiles {

    private static final Logger LOG = LoggerFactory.getLogger(UploadJSONZipFiles.class);

    private static final String ZIP_FILETYPE = "zip";

    private String ftpUrl;
    private String ftpPort;
    private String username;
    private String password;
    private String folderPath;

    @Inject
    public UploadJSONZipFiles(final @Named("europass-ewa-services.files.export.ftp.server.url") String ftpUrl,
            final @Named("europass-ewa-services.files.export.ftp.server.port") String ftpPort,
            final @Named("europass-ewa-services.files.export.ftp.server.username") String username,
            final @Named("europass-ewa-services.files.export.ftp.server.password") String password,
            final @Named("europass-ewa-services.files.export.ftp.server.folderPath") String folderPath) {

        this.ftpUrl = ftpUrl;
        this.ftpPort = ftpPort;
        this.username = username;
        this.password = password;
        this.folderPath = folderPath;
    }

    public void uploadZipToFtpServer(final File file) {

        final FTPClient ftpClient = new FTPClient();

        if (StringUtils.isEmpty(ftpUrl)) {
            return;
        }

        try {
            ftpClient.connect(ftpUrl, Integer.valueOf(ftpPort));
            ftpClient.login(username, password);
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftpClient.enterLocalPassiveMode();

            //for (final File file : files) {
            if (file.isFile() && ZIP_FILETYPE.equalsIgnoreCase(FilenameUtils.getExtension(file.getName()))) {

                final FileInputStream bis = new FileInputStream(file);
                final String fileToSend = folderPath + file.getName();

                boolean done = ftpClient.storeFile(fileToSend, bis);
                bis.close();

                if (done) {
                    LOG.debug("FTP upload of zip was successful ...");

                    // Need to delete zip files..
                    file.delete();
                }
            }
            //}

            ftpClient.logout();

        } catch (final IOException e) {
            LOG.error("IO Exception during uploading zip file to FTP");
        }
    }
}
