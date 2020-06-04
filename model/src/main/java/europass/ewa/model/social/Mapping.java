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

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.common.base.Strings;

/**
 * <p>
 * Represents a mapping entry.</p>
 * <ul>
 * <li><strong>from:</strong>
 * <p>
 * Is an absolute or relative path that describes an object in the object
 * hierarchy.</p>
 * <p>
 * The text may be separated by "/" denoting a deeper object.</p>
 * <p>
 * The names used should correspond to existing class fields for which there is
 * an available getter.</p>
 * <ul>
 * <li><em>Starts with "/":</em> Starts from the Root object</li>
 * <li><em>Starts with "./":</em> Starts from the previous object</li>
 * <li><em>Starts with ".":</em> Maintains the same object</li>
 * </ul>
 * </li>
 * <li><strong>to:</strong><p>
 * The same rules for from apply here too</p></li>
 * <li><strong>through:</strong><p>
 * This is the name of a class implementing a Handler interface which dictates a
 * suitable handle method.</p></li>
 * <li><strong>params:</strong><p>
 * A space separated String which denotes an unspecified number of arguments
 * that may be passed to the aforementioned Handler implementation.</p></li>
 * <li><strong>mappingList:</strong><p>
 * Each Mapping element may accept a nested MappingList of Mapping
 * Elements.</p></li>
 * </ul>
 *
 * @author ekar
 *
 */
public class Mapping {

    @JacksonXmlProperty(isAttribute = true)
    private String from;

    @JacksonXmlProperty(isAttribute = true)
    private String to;

    @JacksonXmlProperty(isAttribute = true)
    private String through;

    @JacksonXmlProperty(isAttribute = true)
    private String params;

    @JsonIgnore
    private Locale locale;

    @JacksonXmlProperty(localName = "Mapping")
    @JacksonXmlElementWrapper(localName = "MappingList")
    private List<Mapping> mappingList;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getThrough() {
        return through;
    }

    public void setThrough(String through) {
        this.through = through;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public List<Mapping> getMappingList() {
        return mappingList;
    }

    public void setMappingList(List<Mapping> mappingList) {
        this.mappingList = mappingList;
    }

    public void setLocale(Locale locale) {
        if (locale == null) {
            return;
        }
        if (this.params == null) {
            this.params = "";
        }
        if (this.locale != null) {
            this.params = params.substring(0, params.length() - 3);
        }

        this.params += " " + locale.getLanguage();
        this.locale = locale;
    }

    public Locale getLocale() {
        return locale;
    }

    @JsonIgnore
    public List<String> getParamsAsList() {
        return Arrays.asList(this.getParamsAsArray());
    }

    @JsonIgnore
    public String[] getParamsAsArray() {
        if (Strings.isNullOrEmpty(this.params)) {
            return new String[]{};
        }
        return this.params.split(" ");
    }

    @Override
    public String toString() {
        return "from:" + this.from + "/to:" + this.to + "/through:" + this.through + "/params:" + this.params;
    }

}
