package europass.ewa.oo.server;

import com.google.inject.AbstractModule;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.google.inject.servlet.GuiceServletContextListener;

import europass.ewa.modules.ContextParametersModule;
import europass.ewa.modules.ExternalConfigurationModule;
import europass.ewa.modules.LogbackConfigurationModule;
import europass.ewa.oo.server.impl.OOConverterWithJod;
import europass.ewa.oo.server.jobs.CleanUpTempOfficeFilesJobActivator;
import europass.ewa.oo.server.jobs.Quartz;
import europass.ewa.oo.server.jobs.QuartzModule;
import europass.ewa.oo.server.modules.OOModule;
import europass.ewa.oo.server.servlet.ServletUtils;

public class OOStartup extends GuiceServletContextListener implements ServletContextListener{
	
	private static final Logger LOG  = LoggerFactory.getLogger(OOStartup.class);
	
	private ServletContext servletContext;
	
	private Injector injector;
	 
	@Override
	protected synchronized Injector getInjector() {
		
		if (this.injector!=null) return this.injector;
		
		this.injector = Guice.createInjector(
				ServletUtils.getParameter(servletContext, "guice.stage", Stage.DEVELOPMENT),
				getModules());
		
		return this.injector;
	}
	/**
	 * Context Initialised
	 */
	@Override
	public void contextInitialized(ServletContextEvent event){
		
		LOG.info("ServicesStartup:contextInitialized - Initialising context...");
		
		this.servletContext = event.getServletContext();
		
		// initialize jod to eagerly get the office instances
		getInjector().getInstance(OOConverterWithJod.class);

		super.contextInitialized(event);
	}
	/**
	 * Context Destroyed
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		
		LOG.info("ServicesStartup:contextDestroyed - Destroying context...");
		
		//Shutdown scheduler				
		getInjector().getInstance(Quartz.class).shutdown();
		
		this.servletContext = null;
		
		// shut down jod to release office instances gracefully
		getInjector().getInstance(OOConverterWithJod.class).shutdown();
		
		this.injector = null;
		
		super.contextDestroyed(event);
	}
	
	protected ModuleList getModules(){
		ModuleList modules = modules();
		
		return modules.add(
				new ContextParametersModule( this.servletContext ),
				
				new ExternalConfigurationModule( this.servletContext, "europass-ewa-oo-server" ),
				
				new LogbackConfigurationModule("europass-ewa-oo-server"),
				
				new OOModule(),
				
				new QuartzModule(),
				
				new AbstractModule(){
					@Override
					protected void configure() {
						bind(CleanUpTempOfficeFilesJobActivator.class).asEagerSingleton();
					}
				}
		);
	}
	
	protected static ModuleList modules(Module... modules) {
		return new ModuleList().add(modules);
	}
	
	protected static class ModuleList implements Iterable<Module> {
		
		private List<Module> modules = new LinkedList<Module>();
		
		public ModuleList add(Module...modules) {
			for ( Module module: modules ) {
				this.modules.add(module);
			}
			return this;
		}
		
		public ModuleList add(Iterable<Module> modules) {
			for ( Module module: modules ) {
				this.modules.add(module);
			}
			return this;
		}
		
		@Override
		public Iterator<Module> iterator() {
			return modules.iterator();
		}
	}
	
	
}
