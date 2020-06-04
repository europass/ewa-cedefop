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
import java.util.Locale;
import java.util.ResourceBundle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.common.base.Strings;

import europass.ewa.model.format.HtmlSanitizer;
import europass.ewa.model.reflection.ReflectionUtils;
import europass.ewa.resources.JsonResourceBundle;

@JsonPropertyOrder({
    "type",
    "description"
})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Headline extends PrintableObject {

    private CodeLabel type;
    private CodeLabel description;

    public static final String HEADLINE_CODE_STATS_EXCLUDED = "personal_statement";

    public Headline() {
    }

    public Headline(CodeLabel type, CodeLabel description) {
        this.type = type;
        this.description = description;
    }

    @JsonProperty("Type")
    @JacksonXmlProperty(localName = "Type", namespace = Namespace.NAMESPACE)
    public CodeLabel getType() {
        return type;
    }

    public void setType(CodeLabel type) {
        this.type = type;
    }

    @JsonProperty("Description")
    @JacksonXmlProperty(localName = "Description", namespace = Namespace.NAMESPACE)
    public CodeLabel getDescription() {
        return description;
    }

    @JsonIgnore
    public void setDescription(CodeLabel description) {
        CodeLabel type = this.getType();

        if (description != null && type != null) {
            String code = type.getCode();

            if (!Strings.isNullOrEmpty(code) && "personal_statement".equals(code)) {

                CodeLabel newDescription = new CodeLabel();

                String descCode = description.getCode();
                if (!Strings.isNullOrEmpty(descCode)) {
                    newDescription.setCode(descCode);
                }
                newDescription.setLabel(escapeNewLineCharacters(HtmlSanitizer.sanitize(description.getLabel())));

                this.description = newDescription;
                return;
            }
        }

        this.description = description;

    }

    /**
     * *********************************************************************
     */
    @JsonIgnore
    public void translateTo(SkillsPassport esp, Locale locale) {
        type = translate(esp, locale, "HeadlineType", type);
        description = translate(esp, locale, "OccupationalField", description, ReflectionUtils.ADJUST_OCCUPATION_LABEL, null);
    }

    @JsonIgnore
    public String typeOdt() {
        if (type == null) {
            return "";
        }

        String label = type.getLabel();
        if (Strings.isNullOrEmpty(label)) {
            Locale locale = locale();
            ResourceBundle bundle = ResourceBundle.getBundle("bundles/HeadlineType", locale,
                    new JsonResourceBundle.Control(new ObjectMapper()));
            try {
                return bundle.getString(type.getCode());
            } catch (final Exception e) {
                //case of NullPointer, MissingResource, ClassCast Exceptions
                return bundle.getString("job_applied_for");
            }

        }

        return this.escapeForXml(label);
    }

    /**
     * Returns boolean according to whether the Headline Type is Personal
     * Statement
     *
     * @param idx
     * @return
     */
    @JsonIgnore
    public boolean isPersonalStatement() {
        if (type == null) {
            return false;
        } else {
            if (type.getCode() != null && "personal_statement".equals(type.getCode())) {
                return true;
            } else {
                return false;
            }
        }
    }

    @JsonIgnore
    public String personalStatementOdt() {
        String personalStatement = "";
        String pattern = "<text:p xmlns:text=\"urn:oasis:names:tc:opendocument:xmlns:text:1.0\" text:style-name=\"europass_5f_SectionDetails\">";
        String replacement = "<text:p text:style-name=\"_5f_ECV_5f_PersonalStatement\">";
        if (this.description != null && !Strings.isNullOrEmpty(this.description.getLabel())) {

            personalStatement = this.asRichText(escapeNewLineCharacters(HtmlSanitizer.sanitize(this.description.getLabel())));

            personalStatement = personalStatement.replaceAll(pattern, replacement);
        }
        return personalStatement;
    }

    @JsonIgnore
    public void applyDefaultPreferences(List<PrintingPreference> newPrefs) {
        super.applyDefaultPreferences(newPrefs);
    }

    @JsonIgnore
    @Override
    public boolean checkEmpty() {
        return ((type == null || (type != null && type.checkEmpty()))
                || (description == null || (description != null && description.checkEmpty())));
    }
}
