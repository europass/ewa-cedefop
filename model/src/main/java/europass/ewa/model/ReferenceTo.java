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

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.common.base.Strings;

@JsonPropertyOrder({"idref"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReferenceTo extends PrintableObject {

    private String idref;

    @JsonIgnore
    private String refKey;

    public ReferenceTo() {
    }

    public ReferenceTo(String idref) {
        this.idref = idref;
    }

    @JsonProperty("idref")
    @JacksonXmlProperty(isAttribute = true, localName = "idref")
    public String getIdref() {
        return idref;
    }

    public void setIdref(String idref) {
        this.idref = idref;
    }

    /**
     * *********************************************************
     */
    public Attachment attachment() {
        return getDocument().resolve(this);
    }

    @Override
    public boolean checkEmpty() {
        return Strings.isNullOrEmpty(idref);
    }

    @JsonIgnore
    public String refKey() {
        if (refKey == null) {
            refKey = UUID.randomUUID().toString();
        }
        return refKey;
    }

    /**
     * Instead of overriding equals, use this to an alternative by excluding the
     * refKey.
     *
     * @param other
     * @return
     */
    public boolean matches(ReferenceTo obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != this.getClass()) {
            return false;
        }

        ReferenceTo other = (ReferenceTo) obj;
        return idref == other.idref;

    }
}
