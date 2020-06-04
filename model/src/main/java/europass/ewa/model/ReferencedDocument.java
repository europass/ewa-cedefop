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

import europass.ewa.enums.EuropassDocumentType;

@JsonPropertyOrder({"ref"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReferencedDocument extends PrintableObject {

    private EuropassDocumentType ref;

    public ReferencedDocument() {
    }

    public ReferencedDocument(EuropassDocumentType ref) {
        this.ref = ref;
    }

    @JsonProperty("ref")
    @JacksonXmlProperty(isAttribute = true, localName = "ref")
    public EuropassDocumentType getRef() {
        return ref;
    }

    public void setRef(EuropassDocumentType ref) {
        this.ref = ref;
    }

    @Override
    public boolean checkEmpty() {
        return EuropassDocumentType.UNKNOWN.equals(ref);
    }

}
