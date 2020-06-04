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

import europass.ewa.enums.LogFields;
import europass.ewa.enums.ServerModules;
import europass.ewa.logging.ExtraLogInfo;
import europass.ewa.services.editor.files.SessionDiskFileRepository;
import europass.ewa.services.exception.ApiException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This job is responsible for deleting all attached files whose life time
 * exceeds the time limit defined in config.properties
 * (europass-ewa-services.files.fileMaxLifeTime) When a user's folder is empty
 * of files, the job will try to delete it also.
 *
 * synchronized block (if folder is empty - delete folder) with creating a new
 * folder and moving a file into it i.e. saveOnDisk method of
 * SessionDiskFileRepository
 *
 * @author JK
 */
public class CleanUpUploadsJob implements Job {

    private static final Logger LOG = LoggerFactory.getLogger(CleanUpUploadsJob.class);
    private static final String GLUSTERFS_INTERNAL_OP_DIR_NAME = "internal_op";
    public static final String module = ServerModules.SERVICES_EDITORS.getModule();

    private long maxLifeTime;
    private String uploadsDir;

    public CleanUpUploadsJob() {
        LOG.debug("Job for deleting files and empty folders was created.");

    }

    @Override
    public void execute(final JobExecutionContext context) {

        setProperties(context);

        File root = new File(uploadsDir);
        File[] directoryList = root.listFiles();
        if (directoryList != null) {
            if (directoryList.length > 0) { // there is at least 1 upload
                deleteFilesAndFolders(directoryList);
            }
        } else {
            throw ApiException.addInfo(new ApiException("path: " + uploadsDir + "does not correspond to a directory"),
                    new ExtraLogInfo().add(LogFields.MODULE, module));
        }
    }

    /**
     * This method sets properties for uploads and maxLifeTime from the
     * respective JobDataMap that is expected to be set from
     * CleanUpUploadsJobActivator
     *
     * @param context
     */
    private void setProperties(JobExecutionContext context) {
        JobDataMap map = context.getJobDetail().getJobDataMap();
        if (map.get("uploads") != null) {
            setUploadsDir((String) map.get("uploads"));
        }
        if (map.get("maxLifeTime") != null) {
            setMaxLifeTime(Long.parseLong((String) map.get("maxLifeTime")));
        }
    }

    /**
     * This method iterates the EWA_UPLOAD_FILES directory(recursively for not
     * empty dirs) and deletes files whose last modified date exceeds the life
     * time limit. It also deletes the empty directories(synchronized)
     *
     * @param files
     */
    private void deleteFilesAndFolders(File[] files) {

        for (File file : files) {
            if (file.isDirectory()) { //directory
                if (file.listFiles() != null) {
                    if (file.listFiles().length == 0) { //empty directory
                        LOG.debug("outside synchronized block");
                        synchronized (SessionDiskFileRepository.filesLock) {
                            LOG.debug("in synchronized block");
                            deleteFile(file);
                        }
                    } else if (file.listFiles().length > 0) { //not empty directory
                        deleteFilesAndFolders(file.listFiles());
                        //check that the directory is empty after the last recursion
                        if (file.listFiles().length == 0) { //empty directory
                            LOG.debug("outside synchronized block");
                            synchronized (SessionDiskFileRepository.filesLock) {
                                LOG.debug("in synchronized block");
                                deleteFile(file);
                            }
                        }
                    }
                } else {
                    throw ApiException.addInfo(new ApiException("Failed to get a list of files for file: " + file.getName()),
                            new ExtraLogInfo().add(LogFields.MODULE, module));
                }
            } else { //file										
                Path path = file.toPath();

                BasicFileAttributes attrs = null;

                try {
                    attrs = Files.readAttributes(path, BasicFileAttributes.class);
                } catch (IOException ioe) {
                    LOG.error("Failed to read attributes of the file: " + file.getName(), ioe);
                }

                if (attrs != null) {
                    // EPAS-1995
                    // We previously used the last access time but this made a lot of files sit in the repo forever.
                    // Thus we'll now use the last modified time which, for some reason, is touched from the application when this displays the file.
                    // https://docs.oracle.com/javase/7/docs/api/java/nio/file/attribute/BasicFileAttributes.html#lastModifiedTime()
                    FileTime time = attrs.lastModifiedTime();
                    long modifiedFileTime = (System.currentTimeMillis() - time.toMillis()) / 1000;
                    if (modifiedFileTime > maxLifeTime) {
                        deleteFile(file);
                    }
                }
            }
        }
    }

    /**
     * This method deletes a file or a folder
     *
     * @param file
     */
    private void deleteFile(File file) {
        String type = file.isDirectory() ? "directory" : "file";

        // Fix for EPAS-1711 Files cleanup job attempts to delete glusterfs internal directory
        if (file.isDirectory() && file.getName().equals(GLUSTERFS_INTERNAL_OP_DIR_NAME)) {
            LOG.debug("skipping internal_op directory");
            return;
        }

        if (file.delete()) {
            LOG.debug(type + ": " + file.getName() + " was deleted successfully");
        } else {
            LOG.debug("failed to delete " + type + ": " + file.getName());
        }
    }

    /**
     * @param maxLifeTime the maxLifeTime to set
     */
    public void setMaxLifeTime(long maxLifeTime) {
        this.maxLifeTime = maxLifeTime;
    }

    /**
     * @param uploadsDir the uploadsDir to set
     */
    public void setUploadsDir(String uploadsDir) {
        this.uploadsDir = uploadsDir;
    }
}
