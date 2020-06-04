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
package europass.ewa.model.social;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JsonPropertyOrder({"provider", "providerVersion", "europassVersion", "mapping"})
@JsonIgnoreProperties({"xsiNamespace", "noNamespaceSchemaLocation"})
@JacksonXmlRootElement(localName = "MappingListRoot")
public class MappingListRoot {

    @JacksonXmlProperty(isAttribute = true)
    private String provider;

    @JacksonXmlProperty(isAttribute = true)
    private String providerVersion;

    @JacksonXmlProperty(isAttribute = true)
    private String europassVersion;

    @JacksonXmlProperty(localName = "Mapping")
    @JacksonXmlElementWrapper(localName = "MappingList")
    private List<Mapping> mappingList;

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getProviderVersion() {
        return providerVersion;
    }

    public void setProviderVersion(String providerVersion) {
        this.providerVersion = providerVersion;
    }

    public String getEuropassVersion() {
        return europassVersion;
    }

    public void setEuropassVersion(String europassVersion) {
        this.europassVersion = europassVersion;
    }

    public List<Mapping> getMappingList() {
        return mappingList;
    }

    public void setMappingList(List<Mapping> mappingList) {
        this.mappingList = mappingList;
    }

}
