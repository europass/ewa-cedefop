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
package europass.ewa.statistics;

public enum DocumentGenerator {

    EWA_EDITOR("EWA"),
    EWA_EDITOR_CLOUD("EWA_CLOUD"),
    WEB_SERVICES_REST("REST_WS"),
    WEB_SERVICES_REST_UPGRADE("REST_WS_UPGRADE"),
    WEB_SERVICES_SOAP("SOAP_WS"),
    UNKNOWN("NA");

    private String description;

    DocumentGenerator(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
