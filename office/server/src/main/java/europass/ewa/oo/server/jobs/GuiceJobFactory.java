/*
 * GuiceJobFactory.java
 *
 * Created on May 31, 2016
 *            www.eworx.gr
 */

package europass.ewa.oo.server.jobs;

import com.google.inject.Injector;
import javax.inject.Inject;
import org.quartz.Job;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.spi.JobFactory;
import org.quartz.spi.TriggerFiredBundle;
import europass.ewa.oo.server.exception.JobRelatedException;

/**
 *
 * @author JK
 */
public class GuiceJobFactory implements JobFactory{
	private final Injector guice;
	
	@Inject
	public GuiceJobFactory(final Injector guice)
	{
		this.guice = guice;
	}
	
	@Override
	public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException
	{
		//Get the job detail so we can get the job class
		JobDetail jobDetail = bundle.getJobDetail();
		Class jobClass = jobDetail.getJobClass();
		
		try {
			// Get a new instance of that class from Guice so we can do dependency injection
			return (Job) guice.getInstance(jobClass);
		} catch (Exception e) {
			throw new JobRelatedException("Failed to get a new instance of Job class: "+jobClass.getName(), e);
		}
	}
	
}