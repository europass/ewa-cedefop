/*
 * CleanUpTempOfficeFilesJob.java
 *
 * Created on May 31, 2016
 *            www.eworx.gr
 */

package europass.ewa.oo.server.jobs;

import europass.ewa.oo.server.exception.JobRelatedException;
import java.io.File;
import java.util.Date;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This job is responsible for deleting all Office Temporary files that are located
 * under europass-ewa-services.oo.repository and their life time exceeds the time limit
 * defined in config.properties.
 * @author JK
 */
public class CleanUpTempOfficeFilesJob implements Job{
	private static final Logger LOG = LoggerFactory.getLogger(CleanUpTempOfficeFilesJob.class);
	
	private long maxLifeTime;
	private String officeTempFilesDir;

	public CleanUpTempOfficeFilesJob() {
		LOG.debug("Job for deleting temporary office files was created.");		
	}
	
	@Override
	public void execute(final JobExecutionContext context) {
		setProperties(context);
		File tmpOfficeDir = new File(officeTempFilesDir);
		File[] tempFiles = tmpOfficeDir.listFiles();
			if (tempFiles != null) {
				if (tempFiles.length > 0) {
					deleteFiles(tempFiles);
				}
			} else {
				throw new JobRelatedException("path: " + officeTempFilesDir + "does not correspond to a directory");
			}
		
	}
	
	/**
	 * This method iterates the europass-ewa-services.oo.repository directory's files 
	 * (it is expected to have a number of files but no directories) and deletes files whose last modified date
	 * exceeds the life time limit.
	 * @param files 
	 */
	private void deleteFiles(File[] files) {
		for (File file : files) {
			try {
				if (file.isFile()) {
															
					if (((System.currentTimeMillis() - file.lastModified()) / 1000) > maxLifeTime) {
						deleteFile(file);
					}
				}
			} catch (Exception e) {
			}	
		}
	}
	
	/**
	 * This method deletes a file or a folder 
	 * @param file 
	 */
	private void deleteFile(File file) {
		if (file.delete()) {
			LOG.debug("file " + file.getName() + " was deleted successfully");
		} else {
			LOG.debug("failed to delete file " + file.getName());
		}
	}
	
	/**
	 * This method sets properties for uploads and maxLifeTime
	 * from the respective JobDataMap that is expected to be set
	 * from CleanUpUploadsJobActivator
	 * @param context 
	 */
	private void setProperties(JobExecutionContext context) {
		JobDataMap map = context.getJobDetail().getJobDataMap();
		if (map.get("tempFiles") != null) {
			setOfficeTempFilesDir((String)map.get("tempFiles"));
		} 
		if (map.get("maxLifeTime") != null) {
			setMaxLifeTime(Long.parseLong((String)map.get("maxLifeTime")));
		}		
	}
	
	/**
	 * @param maxLifeTime the maxLifeTime to set
	 */
	public void setMaxLifeTime(long maxLifeTime) {
		this.maxLifeTime = maxLifeTime;
	}

	/**
	 * @param officeTempFilesDir the officeTempFilesDir to set
	 */
	public void setOfficeTempFilesDir(String officeTempFilesDir) {
		this.officeTempFilesDir = officeTempFilesDir;
	}

	
}