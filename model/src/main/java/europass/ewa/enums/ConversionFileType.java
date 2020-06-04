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
package europass.ewa.enums;

/**
 * Supported File Types by the Conversion API
 *
 * @author ekar
 *
 */
public enum ConversionFileType {

    PDF("pdf", ".pdf", ContentTypes.PDF_CT),
    WORD_DOC("doc", ".doc", ContentTypes.WORD_DOC_CT),
    WORD_OPEN_XML("docx", ".docx", ContentTypes.WORD_OPEN_XML_CT),
    OPEN_DOC("odt", ".odt", ContentTypes.OPEN_DOC_CT),
    HTML("html", ".html", "text/html"),
    XML("xml", ".xml", "application/xml"),
    JSON("json", ".json", "application/json"),
    UNKNOWN("unknown", ".txt", "application/text");

    private String description;

    private String extension;

    private String mimeType;

    ConversionFileType(String description, String extension, String mimeType) {
        this.description = description;
        this.extension = extension;
        this.mimeType = mimeType;
    }

    public String getDescription() {
        return description;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getExtension() {
        return extension;
    }

    /**
     * Checks if the file is PDF
     *
     * @return
     */
    public static boolean isPDF(String mime) {
        return mime.equals(PDF.getMimeType());
    }
}
