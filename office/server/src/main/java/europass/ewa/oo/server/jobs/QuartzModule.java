/*
 * QuartzModule.java
 *
 * Created on May 31, 2016
 *            www.eworx.gr
 */

package europass.ewa.oo.server.jobs;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

/**
 *
 * @author JK
 */
public class QuartzModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(SchedulerFactory.class).to(StdSchedulerFactory.class).in(Scopes.SINGLETON);
		bind(GuiceJobFactory.class).in(Scopes.SINGLETON);
		bind(Quartz.class).in(Scopes.SINGLETON);
	}
}