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
package europass.ewa.services.enums;

public enum XmlVersion {

    VERSION_1_0("V1.0"),
    VERSION_1_1("V1.1"),
    VERSION_1_2("V1.2"),
    VERSION_2_0("V2.0"),
    VERSION_3_0("V3.0"),
    VERSION_3_1("V3.1"),
    VERSION_3_2("V3.2"),
    VERSION_3_3("V3.3"),
    VERSION_3_4("V3.4"),
    LATEST("V3.4"),
    UNKNOWN("VX.X");

    private String code;

    XmlVersion(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static XmlVersion match(String code) {
        if (code == null) {
            return UNKNOWN;
        }
        for (XmlVersion version : values()) {
            if (version.code.equals(code)) {
                return version;
            }
        }
        return UNKNOWN;
    }

}
