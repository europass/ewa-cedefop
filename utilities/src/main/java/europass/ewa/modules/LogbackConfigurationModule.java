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
package europass.ewa.modules;

import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.ILoggerFactory;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;

import com.google.inject.Key;
import com.google.inject.PrivateModule;
import com.google.inject.name.Names;

public class LogbackConfigurationModule extends PrivateModule {

    private final String key;

    public LogbackConfigurationModule(String key) {
        super();
        this.key = key;
    }

    @Override
    protected void configure() {
        bind(String.class)
                .annotatedWith(Names.named("logback.xml"))
                .to(Key.get(String.class, Names.named(key + ".logback.xml")));
        bind(LogbackConfiguration.class).asEagerSingleton();
    }

    /*
	 * This is a programmatic configuration of the logger.
	 * 
	 * Usually when no specific configuration is given logback will:
	 * - try to load a file named "logback.xml" found in classpath;
	 * - start with a Basic Configuration which outputs logs to the console;
     */
    static final class LogbackConfiguration {

        @Inject
        public LogbackConfiguration(@Named("logback.xml") String configFilePath) {

            ILoggerFactory factory = LoggerFactory.getILoggerFactory();
            LoggerContext lc = (LoggerContext) factory;
            try {
                JoranConfigurator configurator = new JoranConfigurator();
                configurator.setContext(lc);
                // Call context.reset() to clear any previous configuration, e.g. default 
                // configuration. For multi-step configuration, omit calling context.reset().
                lc.reset();
                configurator.doConfigure(configFilePath);
            } catch (JoranException je) {

            }
            StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
        }
    }
}
