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
package europass.ewa.model.conversion.exception;

public enum JacksonConversionStatus {

    ODT_TEMPLATE("download.odt.template"), //missing or malformed odt template

    MODEL_TO_ODT("download.model.to.odt"), //failure to assemple odt

    XML_READ_TO_POJO("xml-to-model"),
    JSON_READ_TO_POJO("json-to-model"),
    POJO_TO_JSON("model-to-json"),
    POJO_TO_XML("model-to-xml"),
    OTHER("conversion-other-error");

    private String description;

    JacksonConversionStatus(String description) {
        this.description = description;
    }

    public String getDesription() {
        return description;
    }
}
