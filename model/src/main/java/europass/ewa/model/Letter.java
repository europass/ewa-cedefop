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

import org.apache.commons.text.StringEscapeUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.common.base.Strings;

import europass.ewa.resources.JsonResourceBundle;

@JsonPropertyOrder({"localisation", "subjectLine", "openingSalutation", "body", "closingSalutation"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Letter extends PrintableObject {

    private LetterLocalisation localisation;

    private String subjectLine;

    private OpeningSalutation openingSalutation;

    private LetterBody body;

    private CodeLabel closingSalutation;

    @JsonProperty("Localisation")
    @JacksonXmlProperty(localName = "Localisation", namespace = Namespace.NAMESPACE)
    public LetterLocalisation getLocalisation() {
        return withPreferences(localisation, "Localisation");
    }

    public void setLocalisation(LetterLocalisation localisation) {
        this.localisation = localisation;
    }

    @JsonProperty("SubjectLine")
    @JacksonXmlProperty(localName = "SubjectLine", namespace = Namespace.NAMESPACE)
    public String getSubjectLine() {
        return subjectLine;
    }

    public void setSubjectLine(String subjectLine) {
        this.subjectLine = subjectLine;
    }

    @JsonIgnore
    public String subjectLineWithBold() {
        /* EWA 1536, ndim: changing escapeHtml with escapeXml for correct escaping of subjectLine*/

        if (Strings.isNullOrEmpty(subjectLine)) {
            return "";
        }

        String subject = this.subjectLine;
        String result = this.subjectLine;

        String key = "CoverLetter.Letter.SubjectLine",
                label = "";

        final String HTML_ROOT_WRAPPER_START = "<text:span text:style-name=\"_CL_Section_Subject_Line_Bold\">";
        final String HTML_ROOT_WRAPPER_END = "</text:span>";

        try {	//get the subject line label from the DocumentLabel bundle
            Locale locale = locale();
            //locale.setDefault(Locale.ENGLISH);
            ResourceBundle bundle = ResourceBundle.getBundle("bundles/DocumentLabel", locale, new JsonResourceBundle.Control(new ObjectMapper()));
            label = bundle.getString(key);
        } catch (final Exception e) {
            //Load default label if it does not exist in the current locale
            label = ResourceBundle.getBundle("bundles/DocumentLabel", Locale.getDefault(), new JsonResourceBundle.Control(new ObjectMapper())).getString(key);;
        }
        //if label found, then check if this label exists into the subject line in the format Subject: ....

        subject = StringEscapeUtils.escapeXml10(subject);

        if (!Strings.isNullOrEmpty(label)) {
            label = StringEscapeUtils.escapeXml10(label);
            if (subject.trim().startsWith(label + ":")) {

                String replacement = HTML_ROOT_WRAPPER_START + label + HTML_ROOT_WRAPPER_END;

                result = subject.replaceFirst(label, replacement);
                return result;

            }
        }
        return StringEscapeUtils.escapeXml10(result);
    }

    @JsonProperty("OpeningSalutation")
    @JacksonXmlProperty(localName = "OpeningSalutation", namespace = Namespace.NAMESPACE)
    public OpeningSalutation getOpeningSalutation() {
        return withDocument(openingSalutation, getDocument());
    }

    public void setOpeningSalutation(OpeningSalutation openingSalutation) {
        this.openingSalutation = openingSalutation;
    }

    @JsonProperty("Body")
    @JacksonXmlProperty(localName = "Body", namespace = Namespace.NAMESPACE)
    public LetterBody getBody() {
        return withDocument(body, getDocument());
    }

    public void setBody(LetterBody body) {
        this.body = body;
    }

    @JsonProperty("ClosingSalutation")
    @JacksonXmlProperty(localName = "ClosingSalutation", namespace = Namespace.NAMESPACE)
    public CodeLabel getClosingSalutation() {
        return withPreferences(closingSalutation, "ClosingSalutation");
    }

    public void setClosingSalutation(CodeLabel closingSalutation) {
        this.closingSalutation = closingSalutation;
    }

    @JsonIgnore
    String getClosingSalutationLabel() {
        if (closingSalutation == null) {
            return "";
        }
        return closingSalutation.getLabel();
    }

    @JsonIgnore
    public void translateTo(SkillsPassport esp, Locale locale) {
        closingSalutation = translate(esp, locale, "ClosingSalutation", closingSalutation);
    }

    @JsonIgnore
    @Override
    public boolean checkEmpty() {
        return ((localisation == null || (localisation != null && localisation.checkEmpty()))
                && Strings.isNullOrEmpty(subjectLine)
                && (openingSalutation == null || (openingSalutation != null && openingSalutation.checkEmpty()))
                && (body == null || (body != null && body.checkEmpty()))
                && (closingSalutation == null || (closingSalutation != null && closingSalutation.checkEmpty())));
    }

    @Override
    public void applyDefaultPreferences(List<PrintingPreference> newPrefs) {
        applyDefaultPreferences(getLocalisation(), LetterLocalisation.class, "Localisation", newPrefs);

        //no pref for subject-line, opening salutation and body
        applyDefaultPreferences(getClosingSalutation(), CodeLabel.class, "ClosingSalutation", newPrefs);

        super.applyDefaultPreferences(newPrefs);
    }

    @JsonIgnore
    public String closingSalutationTxt() {
        StringBuilder bld = new StringBuilder("");

        String salutationTxt = getClosingSalutationLabel();
        if (!Strings.isNullOrEmpty(salutationTxt)) {
            bld.append(salutationTxt);
        }

        if (!"".equals(bld.toString())) {
            String punctuation = readResourceVal(CoverLetter.CLOSING_PUNCTUATION_KEY);
            bld.append(punctuation.trim());
        }
        return bld.toString();
    }

    @JsonIgnore
    public boolean closingSalutationEmpty() {
        CodeLabel closing = this.getClosingSalutation();
        if (closing != null) {
            return closing.checkEmpty();
        }
        return true;
    }

    @JsonIgnore
    public String readResourceVal(String key) {
        if (Strings.isNullOrEmpty(key)) {
            return "";
        } else {
            Locale locale = locale();
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
