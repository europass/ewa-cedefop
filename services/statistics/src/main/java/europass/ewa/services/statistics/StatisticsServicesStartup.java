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
package europass.ewa.services.statistics;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

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
import com.google.inject.servlet.ServletModule;

import europass.ewa.database.filter.HibernateSessionFilter;
import europass.ewa.database.guice.HibernateModule;
import europass.ewa.modules.ContextParametersModule;
import europass.ewa.modules.ExternalConfigurationModule;
import europass.ewa.modules.GeneralConfigurationModule;
import europass.ewa.modules.LogbackConfigurationModule;
import europass.ewa.services.statistics.modules.StatisticsServicesHibernateModule;
import europass.ewa.services.statistics.modules.StatisticsServicesModule;
import europass.ewa.servlet.ServletUtils;

public class StatisticsServicesStartup extends GuiceServletContextListener implements ServletContextListener{
	
	private static final Logger LOG  = LoggerFactory.getLogger(StatisticsServicesStartup.class);
	
	private ServletContext servletContext;
	
	protected Injector injector;
	 
	@Override
	protected Injector getInjector() {
		
		Injector injector = Guice.createInjector(
				ServletUtils.getParameter(servletContext, "guice.stage", Stage.DEVELOPMENT),
				getModules());
		
		this.injector = injector;
		
		return injector;
	}
	/**
	 * Context Initialised
	 */
	@Override
	public void contextInitialized(ServletContextEvent event){
		
		LOG.info("ServicesStartup:contextInitialized - Initialising context...");
		
		this.servletContext = event.getServletContext();
		
		super.contextInitialized(event);
	}
	/**
	 * Context Destroyed
	 */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		
		LOG.info("ServicesStartup:contextDestroyed - Destroying context...");
		
		this.servletContext = null;
		
		this.injector = null;
		
		super.contextDestroyed(event);
	}
	
	protected ModuleList getModules(){
				
                Properties hibernateProperties = new Properties();
                GeneralConfigurationModule configModule = new GeneralConfigurationModule(this.servletContext, "database-stats");
                Properties prop = configModule.getPropertyFile();
                
                if (prop != null) {
                    hibernateProperties.setProperty("hibernate.connection.datasource", prop.getProperty("hibernate.connection.datasource"));
                    hibernateProperties.setProperty("hibernate.dialect", prop.getProperty("hibernate.dialect"));                    
                    hibernateProperties.setProperty("hibernate.connection.release_mode", prop.getProperty("hibernate.connection.release_mode"));		
                }
		ModuleList modules = modules();
		
		return modules.add(
				new ContextParametersModule( this.servletContext ),//First in order to read the path to the config file
				
				new ExternalConfigurationModule( this.servletContext, "europass-ewa-services-statistics" ),
				
				new LogbackConfigurationModule("europass-ewa-services-statistics"),
				
				new StatisticsServicesModule(),

				new StatisticsServicesHibernateModule(), //will bind the Hibernate configuration Annotated classes
				
				new HibernateModule( hibernateProperties ), //will provide a SessionFactory, a Session and a Transaction
				
				new ServletModule(){
				   protected void configureServlets(){
					   filter("/*").through(HibernateSessionFilter.class);
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
