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
package europass.ewa;

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

import europass.ewa.module.EditorsModule;
import europass.ewa.modules.EditorsConfigurationModule;
import europass.ewa.modules.ExternalConfigurationModule;
import europass.ewa.modules.LogbackConfigurationModule;
import europass.ewa.modules.SupportedLocaleModule;

public class EditorsStartupListener extends GuiceServletContextListener implements ServletContextListener {

    private static final Logger LOG = LoggerFactory.getLogger(EditorsStartupListener.class);

    private ServletContext servletContext;

    protected Injector injector;

    @Override
    protected Injector getInjector() {
        Injector injector = Guice.createInjector(
                Stage.DEVELOPMENT,
                getModules());

        this.injector = injector;

        return injector;
    }

    /**
     * Context Initialised
     */
    @Override
    public void contextInitialized(ServletContextEvent event) {

        LOG.info("EditorsStartupListener:contextInitialized - Initialising context...");

        this.servletContext = event.getServletContext();

        super.contextInitialized(event);
    }

    /**
     * Context Destroyed
     */
    @Override
    public void contextDestroyed(ServletContextEvent event) {

        LOG.info("EditorsStartupListener:contextDestroyed - Destroying context...");

        this.servletContext = null;

        this.injector = null;

        super.contextDestroyed(event);
    }

    /**
     * Get Modules
     *
     * @return
     */

    protected ModuleList getModules() {
        ModuleList modules = modules();

        return modules.add(
                new ExternalConfigurationModule(servletContext, "europass-ewa-editors"),
                new EditorsConfigurationModule(servletContext, "europass-ewa-editors"),
                new ExternalConfigurationModule(servletContext, "europass-ewa-services-remote-upload-postback"),
                new EditorsConfigurationModule(servletContext, "europass-ewa-services-remote-upload-postback"),
                new LogbackConfigurationModule("europass-ewa-editors"),
                new SupportedLocaleModule(),
                new EditorsModule(servletContext)
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
