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

import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.common.base.Strings;

@JsonPropertyOrder({
    "title",
    "firstName",
    "surname"
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonName extends PrintableObject {

    public static final String FALLBACK_PERSONAMES_ORDER = "FirstName Surname";

    public static final String FALLBACK_PERSONAMES_WITH_TITLE_ORDER = "Title FirstName Surname";

    private CodeLabel title;

    private String firstName;

    private String surname;

    public PersonName() {
    }

    public PersonName(CodeLabel title, String firstname, String surname) {
        this.title = title;
        this.firstName = firstname;
        this.surname = surname;
    }

    public PersonName(String firstname, String surname) {
        this.firstName = firstname;
        this.surname = surname;
    }

    @JsonProperty("Title")
    @JacksonXmlProperty(localName = "Title", namespace = Namespace.NAMESPACE)
    public CodeLabel getTitle() {
        return title;
    }

    public void setTitle(CodeLabel title) {
        this.title = title;
    }

    @JsonProperty("FirstName")
    @JacksonXmlProperty(localName = "FirstName", namespace = Namespace.NAMESPACE)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstname) {
        this.firstName = firstname;
    }

    @JsonProperty("Surname")
    @JacksonXmlProperty(localName = "Surname", namespace = Namespace.NAMESPACE)
    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @Override
    public String toString() {
        PrintingPreference pref = pref();

        String order = pref == null ? FALLBACK_PERSONAMES_ORDER : pref.getOrder();
        if (order.toLowerCase().startsWith("first")) {
            return nullSafe(firstName) + " " + nullSafe(surname);
        } else {
            return nullSafe(surname) + " " + nullSafe(firstName);
        }
    }

    /**
     * ********************************************************************
     */
    @JsonIgnore
    public void translateTo(SkillsPassport esp, Locale locale) {
        title = translate(esp, locale, "Title", title);
    }

    /**
     * Surname is surrounded by text:span
     *
     * @return
     */
    @JsonIgnore
    public String odt() {
        return applyOrder(
                " <text:span text:style-name=\"T6\">", " </text:span>",
                " <text:span text:style-name=\"T6\">", " </text:span>");
    }

    /**
     * Surname is surrounded by spaces.
     *
     * @return
     */
    @JsonIgnore
    public String inOrder() {
        return applyOrder(" ", "", "", " ");
    }

    @JsonIgnore
    public String fullName() {
        String default_order = this.getTitle() != null ? FALLBACK_PERSONAMES_WITH_TITLE_ORDER : FALLBACK_PERSONAMES_ORDER;
        boolean isTitleFirst = false;
        PrintingPreference pref = pref();
        String order = pref == null ? default_order
                : Strings.isNullOrEmpty(pref.getOrder()) ? default_order : pref.getOrder();

        if (order != null && order.toUpperCase().startsWith("title".toUpperCase())) {
            isTitleFirst = true;
        }

        StringBuilder bld = new StringBuilder();

        boolean prev = false;
        if (title != null && !Strings.isNullOrEmpty(title.getLabel()) && isTitleFirst) {
            bld.append(escapeForXml(title.getLabel()));
            prev = true;
        }

        String name = inOrder();
        if (!Strings.isNullOrEmpty(name)) {
            if (prev) {
                bld.append(" ");
            }
            bld.append(name);
        }

        if (title != null && !Strings.isNullOrEmpty(title.getLabel()) && !isTitleFirst) {
            bld.append(" ");
            bld.append(escapeForXml(title.getLabel()));
        }

        return bld.toString();
    }

    /**
     * Used internally when having to format the person name according to the
     * preferred order. The function accepts the texts that surround the
     * surname.
     *
     * @param firstStart
     * @param firstEnd
     * @param lastStart
     * @param lastEnd
     * @return
     */
    private String applyOrder(String firstStart, String firstEnd, String lastStart, String lastEnd) {
        PrintingPreference pref = pref();
        String order = pref == null ? FALLBACK_PERSONAMES_ORDER
                : Strings.isNullOrEmpty(pref.getOrder()) ? FALLBACK_PERSONAMES_ORDER : pref.getOrder();

        order = order.replace("Title", "").trim();

        String firstSafe = this.escapeForXml(nullSafe(firstName));
        String lastSafe = this.escapeForXml(nullSafe(surname));

        if (order != null && order.toUpperCase().startsWith("first".toUpperCase())) {
            return firstSafe + (!lastSafe.isEmpty() ? (firstStart + lastSafe + firstEnd) : "");
        } else {
            return (!lastSafe.isEmpty() ? (lastStart + lastSafe + lastEnd) : "") + firstSafe;
        }
    }

    private static String nullSafe(String str) {
        return str != null ? str : "";
    }

    @JsonIgnore
    @Override
    public boolean checkEmpty() {
        return (Strings.isNullOrEmpty(firstName)
                && Strings.isNullOrEmpty(surname)
                && (title == null
                || title.checkEmpty()));
    }
}
