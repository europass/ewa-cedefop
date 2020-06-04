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
package europass.ewa.services.editor;

import com.google.inject.AbstractModule;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import europass.ewa.modules.*;
import europass.ewa.services.editor.modules.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;

import europass.ewa.conversion.modules.ConversionModule;
import europass.ewa.database.filter.HibernateSessionFilter;
import europass.ewa.database.guice.HibernateModule;
import europass.ewa.model.conversion.ModelModule;
import europass.ewa.model.social.SocialMappingsModule;
import europass.ewa.oo.client.module.OfficeClientModule;
import europass.ewa.services.compatibility.XmlCompatibilityModule;
import europass.ewa.services.editor.files.FileUploadsModule;
import europass.ewa.services.editor.jobs.CleanUpUploadsJobActivator;
import europass.ewa.services.editor.jobs.CreateZipFromExportedJsonJobActivator;
import europass.ewa.services.editor.jobs.CreateZipFromNotImportedFilesJobActivator;
import europass.ewa.services.editor.jobs.Quartz;
import europass.ewa.services.editor.jobs.QuartzModule;
import europass.ewa.services.modules.CoreServicesModule;
import europass.ewa.services.social.linkedin.LinkedInModule;
import europass.ewa.servlet.ServletUtils;
import europass.ewa.statistics.guice.StatisticsModule;

public class EditorServicesStartup extends GuiceServletContextListener implements ServletContextListener {

    private static final Logger LOG = LoggerFactory.getLogger(EditorServicesStartup.class);

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
    public void contextInitialized(ServletContextEvent event) {

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

        //Shutdown scheduler				
        this.injector.getInstance(Quartz.class).shutdown();

        this.servletContext = null;

        this.injector = null;

        super.contextDestroyed(event);
    }

    protected ModuleList getModules() {

        //String ds = "java:comp/env/jdbc/EwaStatisticsDS" ;				
        Properties hibernateProperties = new Properties();
        GeneralConfigurationModule configModule = new GeneralConfigurationModule(this.servletContext, "database-api");
        Properties prop = configModule.getPropertyFile();

        if (prop != null) {
            hibernateProperties.setProperty("hibernate.connection.datasource", prop.getProperty("hibernate.connection.datasource"));
            hibernateProperties.setProperty("hibernate.dialect", prop.getProperty("hibernate.dialect"));
            //hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "create");
            //hibernateProperties.setProperty("hibernate.archive.autodetection", "class");					
            hibernateProperties.setProperty("hibernate.connection.release_mode", prop.getProperty("hibernate.connection.release_mode"));
            //hibernateProperties.setProperty("hibernate.show_sql", "true");
        }

        ModuleList modules = modules();

        return modules.add(
                new ContextParametersModule(this.servletContext),//First in order to read the path to the config file

                new ExternalConfigurationModule(this.servletContext, "europass-ewa-services-editors"),
                new LogbackConfigurationModule("europass-ewa-services-editors"),
                new SupportedLocaleModule(),
                new XmlCompatibilityModule(),
                new ModelModule(),
                new ConversionModule(),
                new OfficeClientModule(), //office supported conversions to DOC and PDF 

                new CoreServicesModule(),
                //Social Mapping modules from model an services-core
                new SocialImportModule(),
                new SocialMappingsModule(),
                new LinkedInModule(),
                new PartnersResourceModule(),
                //Cloud Storage
                new CloudStorageModule(),
                //Cloud Share
                new CloudShareModule(),
                new EditorServicesModule(),
                new FileUploadsModule(), //needs to come after the services module because it needs the ObjectMapper etc.

                new StatisticsModule(), //will bind the Hibernate configuration Annotated classes

                new HibernateModule(hibernateProperties), //will provide a SessionFactory, a Session and a Transaction

                new ServletModule() {
            protected void configureServlets() {
                filter("/*").through(HibernateSessionFilter.class);
            }
        },
                new QuartzModule(),
                new AbstractModule() {
            @Override
            protected void configure() {
                bind(CleanUpUploadsJobActivator.class).asEagerSingleton();
                bind(CreateZipFromExportedJsonJobActivator.class).asEagerSingleton();
                bind(CreateZipFromNotImportedFilesJobActivator.class).asEagerSingleton();
            }
        }
        );
    }

    protected static ModuleList modules(Module... modules) {
        return new ModuleList().add(modules);
    }

    protected static class ModuleList implements Iterable<Module> {

        private List<Module> modules = new LinkedList<Module>();

        public ModuleList add(Module... modules) {
            for (Module module : modules) {
                this.modules.add(module);
            }
            return this;
        }

        public ModuleList add(Iterable<Module> modules) {
            for (Module module : modules) {
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
