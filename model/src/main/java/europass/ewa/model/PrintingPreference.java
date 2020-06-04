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

import java.util.List;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import europass.ewa.model.conversion.ModelModule;

@JacksonXmlRootElement(localName = "Field", namespace = Namespace.NAMESPACE)
@JsonRootName("Field")
@JsonPropertyOrder({"name", "show", "format", "order", "position", "justify", "enableName", "pageBreaks"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class PrintingPreference {

    private static final int HASH_CODE_NUM_2 = 31;

    private static final int HASH_CODE_NUM_1 = 17;

    private static final String CL_JUSTIFICATION_PREFERENCE_NAME = "CoverLetter.Justification";
    private static final String CL_CLOSING_SALUTATION_ENABLED_NAME = "CoverLetter.SignatureName";

    @JacksonXmlProperty(isAttribute = true)
    private Object name;

    @JacksonXmlProperty(isAttribute = true)
    private boolean show;

    @JacksonXmlProperty(isAttribute = true)
    private String format;

    @JacksonXmlProperty(isAttribute = true)
    private String order;

    @JacksonXmlProperty(isAttribute = true)
    private String position;

    @JacksonXmlProperty(isAttribute = true)
    private Boolean justify;

    @JacksonXmlProperty(isAttribute = true)
    private Boolean enableName;

    @JacksonXmlProperty(isAttribute = true)
    private String pageBreaks;

    @JsonIgnore
    private boolean defaultPrefApplied = false;

    public PrintingPreference() {
    }

    public PrintingPreference(String name, boolean show) {
        this.name = name;
        this.show = show;
    }

    public PrintingPreference(String name, PrintingPreference src) {
        this.name = name;
        this.show = src == null ? false : src.show;
        this.format = src == null ? null : src.format;
        this.order = src == null ? null : src.order;
        this.position = src == null ? null : src.position;

        if (name.equals(CL_JUSTIFICATION_PREFERENCE_NAME)) {
            this.justify = src == null ? false : src.justify;
        }

        if (name.equals(CL_CLOSING_SALUTATION_ENABLED_NAME)) {
            this.enableName = src == null ? true : src.enableName;
        }

        this.pageBreaks = src == null ? null : src.pageBreaks;
    }

    public PrintingPreference(String name, boolean show2, String order, String format, String position) {
        this.name = name;
        this.show = show2;
        this.order = ("".equals(order)) ? null : order;
        this.format = ("".equals(format)) ? null : format;
        this.position = ("".equals(position)) ? null : position;

    }

    public PrintingPreference(String name, String show, String order, String format, String position) {
        boolean showAsBoolean = ModelModule.parseBoolean(show);
        this.name = name;
        this.show = showAsBoolean;
        this.order = ("".equals(order)) ? null : order;
        this.format = ("".equals(format)) ? null : format;
        this.position = ("".equals(position)) ? null : position;
    }

    public PrintingPreference(String name, String show, String order, String format, String position, String justify) {
        boolean showAsBoolean = ModelModule.parseBoolean(show);

        this.name = name;
        this.show = showAsBoolean;
        this.order = ("".equals(order)) ? null : order;
        this.format = ("".equals(format)) ? null : format;
        this.position = ("".equals(position)) ? null : position;

        if (name != null) {
            if (name.equals(CL_JUSTIFICATION_PREFERENCE_NAME)) {
                this.justify = ModelModule.parseBoolean(justify);
            }
        }

    }

    public PrintingPreference(String name, String show, String order, String format, String position, String justify, String enableName) {
        boolean showAsBoolean = ModelModule.parseBoolean(show);

        this.name = name;
        this.show = showAsBoolean;
        this.order = ("".equals(order)) ? null : order;
        this.format = ("".equals(format)) ? null : format;
        this.position = ("".equals(position)) ? null : position;

        if (name != null) {
            if (name.equals(CL_JUSTIFICATION_PREFERENCE_NAME)) {
                this.justify = ModelModule.parseBoolean(justify);
            }
            if (name.equals(CL_CLOSING_SALUTATION_ENABLED_NAME)) {
                this.enableName = ModelModule.parseBoolean(enableName);
            }
        }

    }

    public PrintingPreference(String name, String show, String order, String format, String position, String justify, String enableName, String pageBreaks) {

        this(name, show, order, format, position, justify, enableName);

        this.pageBreaks = ("".equals(pageBreaks)) ? null : pageBreaks;
    }

    public Object getName() {
        return name;
    }

    /**
     * ekar POST-Launch when converting JSON to POJO we noticed the problem of
     * the name attribute of printing preferences to be an array of String
     */
    public void setName(Object name) {
        if (name instanceof List) {
            List<?> list = (List<?>) name;
            if (list.size() > 0) {
                Object obj = list.get(0);
                if (obj != null) {
                    this.name = obj.toString();
                }
            }
        } else {
            this.name = name;
        }
    }

    public boolean getShow() {
        return show;
    }

    public void setShow(Boolean show) {
        this.show = show;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Boolean getJustify() {
        return justify != null ? justify.booleanValue() : null;
    }

    public void setJustify(boolean justified) {
        this.justify = justified;
    }

    public Boolean getEnableName() {
        return enableName != null ? enableName.booleanValue() : null;
    }

    public void setEnableName(boolean enabledName) {
        this.enableName = enabledName;
    }

    public String getPageBreaks() {
        return pageBreaks;
    }

    public void setPageBreaks(String pageBreaks) {
        this.pageBreaks = pageBreaks;
    }

    public boolean isDefaultPrefApplied() {
        return defaultPrefApplied;
    }

    public void setDefaultPrefApplied(boolean defaultPrefApplied) {
        this.defaultPrefApplied = defaultPrefApplied;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (name != null) {
            sb.append(name.toString());
        }
        sb.append("(" + show + ")");
        if (order != null) {
            sb.append("|" + order);
        }
        if (format != null) {
            sb.append("|" + format);
        }
        if (position != null) {
            sb.append("|" + position);
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) { // reference
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        // safe casting
        PrintingPreference rhs = (PrintingPreference) obj;

        /*
		 * This class provides methods to build a good equals method for any
		 * class. It follows rules laid out in Effective Java , by Joshua Bloch.
		 * In particular the rule for comparing doubles, floats, and arrays can
		 * be tricky. Also, making sure that equals() and hashCode() are
		 * consistent can be difficult.
         */
        return new EqualsBuilder().
                append(name, rhs.name).
                append(show, rhs.show).
                append(order, rhs.order).
                append(format, rhs.format).
                append(position, rhs.position).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(HASH_CODE_NUM_1, HASH_CODE_NUM_2). // two randomly chosen prime numbers
                append(name).
                append(show).
                append(order).
                append(format).
                append(position).toHashCode();
    }
}
