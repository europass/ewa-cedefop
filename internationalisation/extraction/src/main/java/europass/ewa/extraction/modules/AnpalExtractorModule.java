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
package europass.ewa.extraction.modules;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author at
 */
public class AnpalExtractorModule extends AbstractModule {

    public static final String ANPAL_SERVICE_TOKEN = "anpal.infocamere.service.token";
    public static final String IMAGES_REST_URL = "anpal.infocamere.images.url";
    public static final String TEXTS_REST_URL = "anpal.infocamere.texts.url";
    public static final String EDITORS_RESOURCES_PATH = "editors.resources.path";
    public static final String EDITORS_RESOURCES_IMAGES_PATH = "editors.resources.images.path";
    public static final String API_RESOURCES_PATH = "api.resources.path";

    @Override
    protected void configure() {
        try {
            Names.bindProperties(binder(), getProperties());
        } catch (FileNotFoundException e) {
            System.out.println("The configuration file cannot be found");
        } catch (IOException e) {
            System.out.println("I/O Exception during loading configuration");
        }
    }

    private Properties getProperties() throws IOException {
        Properties properties = new Properties();
        properties.load(this.getClass().getClassLoader().getResourceAsStream("config.properties"));
        return properties;
    }
}
