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
package europass.ewa.model;

import javax.inject.Inject;
import javax.inject.Named;

public final class Namespace {

    public static final String NAMESPACE = "http://europass.cedefop.europa.eu/Europass";

    public static final String XSI_NAMESPACE = "http://www.w3.org/2001/XMLSchema-instance";

    public static final String XSI_NAMESPACE_PREFIX = "xsi";

    public static final String SCHEMA_LOCATION_DEFAULT = "http://europass.cedefop.europa.eu/xml/EuropassSchema_V3.0.xsd";

    public static final String SCHEMA_LOCATION_DEFAULT_PARAM = "europass.xml.schema.location";

    private Namespace() {
    }

    @Inject
    @Named(SCHEMA_LOCATION_DEFAULT_PARAM)
    private static String schemaLocation;

    public static String getSchemaLocation() {
        return schemaLocation;
    }

}
