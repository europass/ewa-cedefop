/*
 * CleanUpTempOfficeFilesJobActivator.java
 *
 * Created on May 31, 2016
 *            www.eworx.gr
 */

package europass.ewa.oo.server.jobs;

import com.google.inject.name.Named;
import javax.inject.Inject;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author JK
 */
public class CleanUpTempOfficeFilesJobActivator {
	
	private static final Logger LOG = LoggerFactory.getLogger(CleanUpTempOfficeFilesJobActivator.class);
	
	@Inject
	public CleanUpTempOfficeFilesJobActivator(final Quartz q, @Named("europass-ewa-services.oo.repository")String officeTmpFilesDir,
			@Named("europass-ewa-services.oo.quartz.cron")String cron,
			@Named("europass-ewa-services.oo.files.fileMaxLifeTime")String maxLifeTime) throws SchedulerException {
		
		JobDetail jobDetail = JobBuilder.newJob(CleanUpTempOfficeFilesJob.class)
										.withIdentity("myCleanUpJob")
										.build();
		
		jobDetail.getJobDataMap().put("tempFiles", officeTmpFilesDir);
		jobDetail.getJobDataMap().put("maxLifeTime", maxLifeTime);
		
		CronTrigger cronTrigger = TriggerBuilder.newTrigger()
	                           .withIdentity("crontrigger","crontriggergroup1")
	                           .withSchedule(CronScheduleBuilder.cronSchedule(cron))
	                           .build();
		
		LOG.debug("just before scheduling job");
		q.getScheduler().scheduleJob(jobDetail, cronTrigger);
	}
	
}