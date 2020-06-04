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

import javax.xml.bind.annotation.XmlType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import europass.ewa.model.format.JDateFormat;
import europass.ewa.model.format.JDateFormatBundle;

//Not sure but the use of XmlType enforced order more consistently than JsonPropertyOrder
@XmlType(propOrder = {"from", "to", "current"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Period extends PrintableObject {

    private JDate from;

    private JDate to;

    private boolean current;

    public Period() {
    }

    public Period(JDate from, JDate to) {
        this.from = from;
        this.to = to;
    }

    public Period(JDate from, boolean current) {
        this.from = from;
        this.current = current;
    }

    @JsonProperty("From")
    @JacksonXmlProperty(localName = "From", namespace = Namespace.NAMESPACE)
    public JDate getFrom() {
        return from;
    }

    public void setFrom(JDate from) {
        this.from = from;
    }

    @JsonProperty("To")
    @JacksonXmlProperty(localName = "To", namespace = Namespace.NAMESPACE)
    public JDate getTo() {
        return to;
    }

    public void setTo(JDate to) {
        this.to = to;
    }

    @JsonProperty("Current")
    @JacksonXmlProperty(localName = "Current", namespace = Namespace.NAMESPACE)
    public boolean getCurrent() {
        return current;
    }

    public void setCurrent(Boolean current) {
        this.current = current;
    }

    public String odt() {
        PrintingPreference pref = pref();
        String formatName = (pref == null ? formatName = "numeric/short" : pref.getFormat());

        JDateFormatBundle bundle = JDateFormatBundle.getBundle(locale());
        JDateFormat fmt = bundle.getJDateFormat(formatName != null ? formatName : "numeric/short");

        StringBuilder sb = new StringBuilder();
        if (from != null) {
            fmt.format(sb, from);
            if (to != null || current) {
                // the en dash character
                sb.append(PrintableObject.EN_DASH_CHARACTER_CHAR);
            }
        }
        //if to exists and current is false
        if (to != null && !current) {
            fmt.format(sb, to);
        } //if current is true or to does not exist and current is false
        else if (current) {
            sb.append(bundle.getString(JDateFormatBundle.KEY_CURRENT));
        }

        String txt = sb.toString();

        return this.escapeForXml(txt);
    }

    @JsonIgnore
    @Override
    public boolean checkEmpty() {
        return ((from == null || (from != null && from.checkEmpty()))
                && (to == null || (to != null && to.checkEmpty())));
    }

}
