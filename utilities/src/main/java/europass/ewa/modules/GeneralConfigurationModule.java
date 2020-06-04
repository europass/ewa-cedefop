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
import java.util.Properties;
import javax.servlet.ServletContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author JK
 */
public class GeneralConfigurationModule {

    private String configFile;
    private static final Logger LOG = LoggerFactory
            .getLogger(GeneralConfigurationModule.class);

    public GeneralConfigurationModule(ServletContext ctx, String key) {
        this.configFile = ctx.getInitParameter(key + (key.endsWith(".") ? "" : ".") + "external.config.properties");
    }

    public Properties getPropertyFile() {
        Properties props = new Properties();
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(this.configFile);
            props.load(fin);
        } catch (FileNotFoundException e) {
            LOG.error("configuration file not found: ", e.getMessage());
        } catch (IOException e) {
            LOG.error("configuration file error: ", e.getMessage());
        } finally {
            try {
                if (fin != null) {
                    fin.close();
                }
            } catch (Exception e) {
            }
        }

        return props;
    }
}
