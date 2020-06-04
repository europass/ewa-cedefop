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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.common.base.Strings;

@JsonPropertyOrder({"code", "label"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class CodeLabel extends PrintableObject {

    private String code;

    private String label;

    public CodeLabel() {
    }

    public CodeLabel(String code) {
        this.code = code;
    }

    public CodeLabel(String code, String label) {
        this.code = ("".equals(code)) ? null : code;
        this.label = label;
    }

    @JsonProperty("Code")
    @JacksonXmlProperty(localName = "Code", namespace = Namespace.NAMESPACE)
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @JsonProperty("Label")
    @JacksonXmlProperty(localName = "Label", namespace = Namespace.NAMESPACE)
    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @JsonIgnore
    @Override
    public boolean checkEmpty() {
        return (Strings.isNullOrEmpty(code) && Strings.isNullOrEmpty(label));
    }
}
