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

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

@JsonPropertyOrder({
    "key", "value"
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Metadata {

    public static final String DIMENSION = "dimension";
    public static final String NO_OF_PAGES = "number-of-pages";
    public static final String SCALING = "scaling";
    public static final String CROPPING = "cropping";
    public static final String CROPPING_EXPORT = "cropping_export";	// Used for the proper cropping on the export procedure so the image is shown properly (IS NOT PART OF THE XML SCHEMA) 
    public static final String CROPPING_WIDTH = "width";
    public static final String CROPPING_HEIGHT = "height";
    public static final String CROPPING_X = "ox";
    public static final String CROPPING_Y = "oy";
    public static final String CROPPING_X2 = "ox2";
    public static final String CROPPING_Y2 = "oy2";
    public static final String CROPPING_START_X = "x";
    public static final String CROPPING_START_Y = "y";
    public static final String CROPPING_START_X2 = "x2";
    public static final String CROPPING_START_Y2 = "y2";

    private String key;
    private String value;

    public Metadata() {
    }

    public Metadata(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @JsonProperty("Key")
    @JacksonXmlProperty(isAttribute = true, localName = "key")
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @JsonProperty("Value")
    @JacksonXmlProperty(isAttribute = true, localName = "value")
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
