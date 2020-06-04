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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.common.base.Strings;

import europass.ewa.enums.EuropassDocumentType;
import europass.ewa.model.format.HtmlSanitizer;

@JsonPropertyOrder({"opening", "mainBody", "closing"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class LetterBody extends PrintableObject {

    private String opening;

    private String mainBody;

    private String closing;

    private static final String JUSTIFICATION_STYLE = "europass_5f_paragraph_align_justify_ECL";

    @JsonProperty("Opening")
    @JacksonXmlProperty(localName = "Opening", namespace = Namespace.NAMESPACE)
    public String getOpening() {
        return opening;
    }

    @JsonIgnore
    public String openingTxt() {

        if (this.getDocument() != null) {
            if (this.getDocument().getCoverLetter() != null) {
                if (this.getDocument().getCoverLetter().isCoverLetterJustified()) {
                    return this.asODT(opening, true);
                }
            }
        }
        return this.asODT(opening, false);
    }

    public void setOpening(String opening) {
        this.opening = escapeNewLineCharacters(HtmlSanitizer.sanitize(opening));
    }

    @JsonProperty("MainBody")
    @JacksonXmlProperty(localName = "MainBody", namespace = Namespace.NAMESPACE)
    public String getMainBody() {
        return mainBody;
    }

    @JsonIgnore
    public String mainBodyTxt() {

        if (this.getDocument() != null) {
            if (this.getDocument().getCoverLetter() != null) {
                if (this.getDocument().getCoverLetter().isCoverLetterJustified()) {
                    return this.asODT(mainBody, true);
                }
            }
        }
        return this.asODT(mainBody, false);
    }

    public void setMainBody(String mainBody) {
        this.mainBody = escapeNewLineCharacters(HtmlSanitizer.sanitize(mainBody));
    }

    @JsonProperty("Closing")
    @JacksonXmlProperty(localName = "Closing", namespace = Namespace.NAMESPACE)
    public String getClosing() {
        return closing;
    }

    @JsonIgnore
    public String closingTxt() {

        if (this.getDocument() != null) {
            if (this.getDocument().getCoverLetter() != null) {
                if (this.getDocument().getCoverLetter().isCoverLetterJustified()) {
                    return this.asODT(closing, true);
                }
            }
        }
        return this.asODT(closing, false);
    }

    public void setClosing(String closing) {
        this.closing = escapeNewLineCharacters(HtmlSanitizer.sanitize(closing));
    }

    @JsonIgnore
    @Override
    public boolean checkEmpty() {
        return (Strings.isNullOrEmpty(opening)
                && Strings.isNullOrEmpty(mainBody)
                && Strings.isNullOrEmpty(closing));
    }

    @JsonIgnore
    private String asODT(String str, boolean justified) {
        return this.asRichText(str, EuropassDocumentType.ECL, (justified ? JUSTIFICATION_STYLE : ""));
    }
}
