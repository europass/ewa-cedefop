/*
 * Quartz.java
 *
 * Created on May 31, 2016
 *            www.eworx.gr
 */

package europass.ewa.oo.server.jobs;

import javax.inject.Inject;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author JK
 */
public class Quartz {
	private final Scheduler scheduler;	
	
	private static final Logger LOG = LoggerFactory.getLogger(Quartz.class);

	@Inject
	public Quartz(final SchedulerFactory factory, final GuiceJobFactory jobFactory) throws SchedulerException
	{
		scheduler = factory.getScheduler();
		scheduler.setJobFactory(jobFactory);
		scheduler.start();
	}
	
	public final Scheduler getScheduler() {		
		return scheduler;
	}
	
	public void shutdown() {
		try {
			scheduler.shutdown();
		} catch (SchedulerException e) {
			LOG.error("Failed to shut down scheduler");
		}
	}
}