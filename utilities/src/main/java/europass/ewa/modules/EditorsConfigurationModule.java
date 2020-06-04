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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class EditorsConfigurationModule extends AbstractModule {

    private static final Logger LOG = LoggerFactory
            .getLogger(EditorsConfigurationModule.class);

    private final ServletContext ctx;

    private final String key;

    private Properties props = new Properties();

    public EditorsConfigurationModule(ServletContext ctx, String key) {
        super();
        this.ctx = ctx;
        this.key = key;
    }

    @Override
    protected void configure() {

        FileInputStream fin = null;
        try {
            String configFile = ctx
                    .getInitParameter(key + (key.endsWith(".") ? "" : ".") + "external.config.properties");
            fin = new FileInputStream(configFile);
            props.load(fin);

            @SuppressWarnings("rawtypes")
            Enumeration paramNames = props.propertyNames();
            while (paramNames.hasMoreElements()) {
                String paramName = (String) paramNames.nextElement();
                if (paramName.startsWith("context.")) {
                    this.ctx.setAttribute(paramName, props.getProperty(paramName));
                    bindConstant().annotatedWith(Names.named(paramName)).to(props.getProperty(paramName));
                }
            }

        } catch (FileNotFoundException e) {
            LOG.error("Config.properties file not found: ", e);
        } catch (IOException e) {
            LOG.error("Config.properties file error: ", e);
        } finally {
            try {
                if (fin != null) {
                    fin.close();
                }
            } catch (Exception e) {
            }
        }
    }

    public Properties getProps() {
        return props;
    }

}
