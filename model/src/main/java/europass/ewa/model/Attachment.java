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

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ResourceBundle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.common.base.Strings;

import europass.ewa.enums.PDFLibrary;
import europass.ewa.model.format.OdtDisplayableUtils;
import europass.ewa.resources.JsonResourceBundle;

@JsonPropertyOrder({"id", "name", "mimeType", "data", "tmpuri", "metadata", "description"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Attachment extends FileData {

    public static final int ATT_WIDTH_PIXELS = 522;

    public static final int ATT_WIDTH_INCHES = 7;
    public static final int ATT_HEIGHT_INCHES = 8;

    private String id;

    private String description;

    @JsonIgnore
    private HashSet<String> relatedSections;

    @JsonIgnore
    private List<ByteMetadata> byteMetadaList;

    @JsonIgnore
    private boolean parseable = true;

    @JsonIgnore
    private PDFLibrary pdfLibrary;

    public Attachment() {
    }

    public Attachment(Attachment other) {
        this.id = other.id;
        this.setName(other.getName());
        this.setMimeType(other.getMimeType());
        this.setTmpuri(other.getTmpuri());
        this.setData(other.getData());
        this.description = other.description;
    }

    public Attachment(String id, String description, FileData fd) {
        super(fd.getName(), fd.getMimeType(), fd.getTmpuri() == null ? "" : fd.getTmpuri().toString(), fd.getData());
        this.id = id;
        this.description = description;
    }

    public Attachment(String id, String description, String name, String mimeType, String tmpuri, byte[] data) {
        super(name, mimeType, tmpuri, data);
        this.id = id;
        this.description = description;
    }

    @JsonProperty("Id")
    @JacksonXmlProperty(isAttribute = true, localName = "id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("Description")
    @JacksonXmlProperty(localName = "Description", namespace = Namespace.NAMESPACE)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // --- REFERENCED BY ---
    /**
     * Get the first inserted key of related section
     *
     * @return
     */
    @JsonIgnore
    public String relatedSection() {
        if (relatedSections.isEmpty()) {
            return null;
        }
        return relatedSections.iterator().next();
    }

    @JsonIgnore
    public void setRelatedSection(String refKey) {
        if (Strings.isNullOrEmpty(refKey)) {
            return;
        }
        if (relatedSections == null) {
            relatedSections = new LinkedHashSet<String>();
        }
//		String parentRefKey = refKey;
//		if ( !parentRefKey.startsWith("LearnerInfo.ReferenceTo") && parentRefKey.indexOf('.') > 0 ) {
//			parentRefKey = parentRefKey.substring(0, parentRefKey.lastIndexOf('.'));
//		}
        if (!relatedSections.contains(refKey)) {
            relatedSections.add(refKey);
        }
    }

    public List<ByteMetadata> getByteMetadaList() {
        return byteMetadaList;
    }

    public void setByteMetadataList(List<ByteMetadata> byteMetadaList) {
        this.byteMetadaList = byteMetadaList;
    }

    public PDFLibrary getPdfLibrary() {
        return pdfLibrary;
    }

    public boolean isParseable() {
        return parseable;
    }

    public void setParseable(boolean parseable) {
        this.parseable = parseable;
    }

    public void setPdfLibrary(PDFLibrary pdfLibrary) {
        this.pdfLibrary = pdfLibrary;
    }

    @JsonIgnore
    public String descriptionXml() {
        return OdtDisplayableUtils.escapeForXml(description);
    }

    @JsonIgnore
    public String odt() {
        //Broken Attachment, show a Preview Image
        if (!this.isParseable()) {
            ResourceBundle noPreviewImage = ResourceBundle.getBundle("bundles/DocumentLabel", new JsonResourceBundle.Control());
            return "<text:p ><draw:frame svg:width=\"" + Attachment.ATT_WIDTH_INCHES + "in\" svg:height=\"" + Attachment.ATT_HEIGHT_INCHES + "in\">"
                    + "<draw:image xlink:href=\"Pictures/no-preview.png\" xlink:type=\"simple\" xlink:show=\"embed\" xlink:actuate=\"onLoad\"/>"
                    + "<svg:title>" + noPreviewImage.getString("Accessibility.NoPreview.Image") + "</svg:title></draw:frame></text:p>";
        }
        if (this.isImage()) {

            float width = ATT_WIDTH_INCHES;
            float height = ATT_HEIGHT_INCHES;

            if (this.getByteMetadaList() != null && this.getByteMetadaList().size() == 1) {
                ByteMetadata byteMeta = this.getByteMetadaList().get(0);
                width = byteMeta.getWidthAsInches();
                height = byteMeta.getHeightAsInches();
            }

            return "<text:p ><draw:frame "
                    + "svg:width=\"" + width + "in\" "
                    + "svg:height=\"" + height + "in\">"
                    + "<draw:image xlink:href=\"Pictures/" + this.getId()
                    + "\" xlink:type=\"simple\" xlink:show=\"embed\" xlink:actuate=\"onLoad\"/>"
                    + "<svg:title>" + this.descriptionXml() + "</svg:title></draw:frame></text:p>";
        }

        if (this.isPDF()) {
            List<ByteMetadata> byteMetas = this.getByteMetadaList();
            if (byteMetas == null) {
                return "";
            }

            String odtFrames = "";
            for (int page = 0; page < byteMetas.size(); page++) {

                ByteMetadata byteMeta = byteMetas.get(page);

                odtFrames = odtFrames
                        + "<text:p ><draw:frame "
                        + "svg:width=\"" + byteMeta.getWidthAsInches() + "in\" "
                        + "svg:height=\"" + byteMeta.getHeightAsInches() + "in\">"
                        + "<draw:image "
                        + "xlink:href=\"AttachmentsPDF/" + this.getId().concat("_PDF" + page) + "\" "
                        + "xlink:type=\"simple\" xlink:show=\"embed\" xlink:actuate=\"onLoad\"/>"
                        + "<svg:title>" + this.descriptionXml() + "</svg:title></draw:frame>" + "</text:p>";
            }
            return odtFrames;
        }
        return "";
    }

    @JsonIgnore
    public void accept(AttachmentVisitor v) {
        v.visit(this);
    }
}
