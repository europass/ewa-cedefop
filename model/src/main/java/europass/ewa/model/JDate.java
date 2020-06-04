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

import java.text.SimpleDateFormat;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import europass.ewa.model.format.JDateFormat;
import europass.ewa.model.format.JDateFormatBundle;

@JsonPropertyOrder({"year", "month", "day"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class JDate {

    public static final String DEFAULT_FORMAT = "text/short";

    public static final SimpleDateFormat getXMLMonthFormat() {
        return new SimpleDateFormat("--MM");
    }

    public static final SimpleDateFormat getJSONMonthFormat() {
        return new SimpleDateFormat("MM");
    }

    public static final SimpleDateFormat getXMLDayFormat() {
        return new SimpleDateFormat("---dd");
    }

    public static final SimpleDateFormat getJSONDayFormat() {
        return new SimpleDateFormat("dd");
    }

    private Integer year;
    private Integer month;
    private Integer day;

    public JDate() {
    }

    /*
	 * Remove custom constructors, as they resulted in initialising the Integer
	 * values with 0 when they were parsed as null.
     */
    @JsonProperty("Year")
    @JacksonXmlProperty(isAttribute = true, localName = "year")
    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    @JsonProperty("Month")
    @JacksonXmlProperty(isAttribute = true, localName = "month")
    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    @JsonProperty("Day")
    @JacksonXmlProperty(isAttribute = true, localName = "day")
    public Integer getDay() {
        return day;
    }

    public void setDay(Integer day) {
        this.day = day;
    }

    /**
     * Used by ODT Generator to format the date according to the selected format
     *
     * @return
     */
    @JsonIgnore
    public String format(String formatName, Locale locale) {

        JDateFormatBundle bundle = JDateFormatBundle.getBundle(locale);
        JDateFormat fmt = bundle
                .getJDateFormat(formatName != null ? formatName
                        : JDateFormatBundle.KEY_DEFAULT_FORMAT);
        return fmt.format(this);
    }

    /**
     * Does not override, rather is called when trying to conclude on whether
     * PrintableValue.T is empty
     *
     * @return
     */
    @JsonIgnore
    public boolean checkEmpty() {
        return (year == null && month == null && day == null);
    }
}
