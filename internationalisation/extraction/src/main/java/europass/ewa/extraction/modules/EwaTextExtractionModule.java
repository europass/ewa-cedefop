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
public class EwaTextExtractionModule extends AbstractModule {

    public static final String ZANATA_REST_URL = "zanata.rest.url";
    public static final String ZANATA_FILES_PATH = "zanata.files.path";
    public static final String ZANATA_LOCALES_PATH = "zanata.locales.path";
    public static final String ZANATA_DOCUMENTS_PATH = "zanata.documents.path";

    public static final String DEFAULT_LANGUAGE = "default.locale";

    public static final String EDITORS_RESOURCES_PATH = "editors.resources.path";
    public static final String CONVERSION_RESOURCES_PATH = "conversion.resources.path";
    public static final String MODEL_RESOURCES_PATH = "model.resources.path";
    public static final String MODEL_PREFERENCES_PATH = "model.preferences.path";
    public static final String TEMPLATES_RESOURCES_PATH = "templates.resources.path";
    public static final String API_RESOURCES_PATH = "api.resources.path";
    public static final String CORE_RESOURCES_PATH = "core.resources.path";
    public static final String REST_RESOURCES_PATH = "rest.resources.path";
    public static final String EDITORS_EXTRA_RESOURCES_PATH = "editors-extra.resources.path";

    public static final String CONVERSION_RESOURCES = "conversion.resources";
    public static final String MODEL_RESOURCES = "model.resources";
    public static final String MODEL_PREFERENCES = "model.preferences";
    public static final String TEMPLATES_RESOURCES = "templates.resources";
    public static final String API_RESOURCES = "api.resources";
    public static final String CORE_RESOURCES = "core.resources";
    public static final String REST_RESOURCES = "rest.resources";
    public static final String EDITORS_EXTRA_RESOURCES = "editors-extra.resources";

    public static final String RETAIN_EMPTY_KEYS_RESOURCES = "retain.empty.keys.resources";

    @Override
    protected void configure() {
        try {
            Names.bindProperties(binder(), getProperties());
        } catch (FileNotFoundException e) {
            System.out.println("The configuration file Test.properties can not be found");
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
