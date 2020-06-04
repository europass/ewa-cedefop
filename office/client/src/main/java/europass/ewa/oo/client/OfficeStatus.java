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
package europass.ewa.oo.client;

public enum OfficeStatus {

    OFFICE_CONFIG("download.office.config"), //problem with office configuration
    OFFICE_AVAILABLE("download.office.available"), //no available office
    OFFICE_CONVERSION("download.office.convert"), //failed to convert odt to doc or pdf
    OFFICE_ATTACH("download.office.attach"), //no available office
    OTHER("office.other.error"); //unspecified

    private String description;

    OfficeStatus(String description) {
        this.description = description;
    }

    public String getDesription() {
        return description;
    }

}
