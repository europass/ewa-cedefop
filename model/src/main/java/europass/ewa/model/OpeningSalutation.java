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
import java.util.ResourceBundle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.common.base.Strings;

import europass.ewa.resources.JsonResourceBundle;

@JsonPropertyOrder({"salutation", "personName"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpeningSalutation extends PrintableObject {

    private CodeLabel salutation;

    private PersonName personName;

    @JsonProperty("Salutation")
    @JacksonXmlProperty(localName = "Salutation", namespace = Namespace.NAMESPACE)
    public CodeLabel getSalutation() {
        return salutation;
    }

    @JsonIgnore
    String salutationTxt() {
        if (salutation == null) {
            return "";
        }
        return salutation.getLabel();
    }

    public void setSalutation(CodeLabel salutation) {
        this.salutation = salutation;
    }

    @JsonProperty("PersonName")
    @JacksonXmlProperty(localName = "PersonName", namespace = Namespace.NAMESPACE)
    public PersonName getPersonName() {
        return personName;
    }

    public void setPersonName(PersonName personName) {
        this.personName = personName;
    }

    @JsonIgnore
    String nameTxt() {
        if (personName == null) {
            return "";
        }
        return personName.getSurname();
    }

    @JsonIgnore
    public void translateTo(SkillsPassport esp, Locale locale) {
        salutation = translate(esp, locale, "OpeningSalutation", salutation);
    }

    @JsonIgnore
    public String openingSalutationTxt() {
        StringBuilder bld = new StringBuilder("");
        boolean prev = false;
        boolean replacementWithEllipsis = false;
        boolean replacementWithDots = false;
        String salutationTxt = salutationTxt();
        if (!Strings.isNullOrEmpty(salutationTxt)) {
            bld.append(salutationTxt);
            prev = true;
            if (salutationTxt.contains("…")) {
                replacementWithEllipsis = true;
            }
            if (salutationTxt.contains("...")) {
                replacementWithDots = true;
            }

        }
        String nameTxt = nameTxt();
        if (!Strings.isNullOrEmpty(nameTxt)) {
            if (prev) {
                if (!replacementWithDots && !replacementWithEllipsis) {
                    bld.append(" ");
                    bld.append(nameTxt);
                } else {
                    bld = replacementWithEllipsis ? bld.replace(bld.indexOf("…"), bld.indexOf("…") + 1, nameTxt) : bld;
                    bld = replacementWithDots ? bld.replace(bld.indexOf("..."), bld.indexOf("...") + 3, nameTxt) : bld;
                }
            } else {
                bld.append(nameTxt);
            }

        }
        if (!"".equals(bld.toString())) {
            String punctuation = readResourceVal(CoverLetter.OPENING_PUNCTUATION_KEY);
            bld.append(punctuation.trim());
        }
        return bld.toString();
    }

    @JsonIgnore
    @Override
    public boolean checkEmpty() {
        return ((salutation == null || (salutation != null && salutation.checkEmpty()))
                && (personName == null || (personName != null && personName.checkEmpty())));
    }

    @JsonIgnore
    public String readResourceVal(String key) {
        if (Strings.isNullOrEmpty(key)) {
            return "";
        } else {
            Locale locale = this.getDocument().getLocale();
            ResourceBundle bundle = ResourceBundle.getBundle(CoverLetter.DOCUMENT_CUSTOMIZATIONS_RESOURCE, locale,
                    new JsonResourceBundle.Control(new ObjectMapper()));
            try {
                return bundle.getString(key);
            } catch (final Exception e) {
                return "";
            }
        }

    }

}
