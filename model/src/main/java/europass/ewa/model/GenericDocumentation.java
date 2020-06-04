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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;

import europass.ewa.enums.EuropassDocumentType;
import europass.ewa.resources.JsonResourceBundle;

@JsonPropertyOrder({"heading", "interDocumentList", "intraDocumentList", "extraDocumentList"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class GenericDocumentation extends PrintableObject implements ReferenceToReceptor {

    private CodeLabel heading;

    private List<ReferencedDocument> interDocumentList;

    private List<ReferenceTo> intraDocumentList;

    private List<ReferencedResource> extraDocumentList;

    @JsonProperty("Heading")
    @JacksonXmlProperty(localName = "Heading", namespace = Namespace.NAMESPACE)
    public CodeLabel getHeading() {
        return this.heading;
    }

    public void setHeading(CodeLabel heading) {
        this.heading = heading;
    }

    @JsonProperty("InterDocument")
    @JacksonXmlProperty(localName = "ReferencedDocument", namespace = Namespace.NAMESPACE)
    @JacksonXmlElementWrapper(localName = "InterDocument", namespace = Namespace.NAMESPACE)
    public List<ReferencedDocument> getInterDocumentList() {
        return interDocumentList;
    }

    public void setInterDocumentList(List<ReferencedDocument> interDocumentList) {
        this.interDocumentList = interDocumentList;
    }

    @JsonProperty("IntraDocument")
    @JacksonXmlProperty(localName = "ReferenceTo", namespace = Namespace.NAMESPACE)
    @JacksonXmlElementWrapper(localName = "IntraDocument", namespace = Namespace.NAMESPACE)
    public List<ReferenceTo> getIntraDocumentList() {
        return intraDocumentList;
    }

    public void setIntraDocumentList(List<ReferenceTo> intraDocumentList) {
        this.intraDocumentList = intraDocumentList;
    }

    @JsonProperty("ExtraDocument")
    @JacksonXmlProperty(localName = "ReferencedResource", namespace = Namespace.NAMESPACE)
    @JacksonXmlElementWrapper(localName = "ExtraDocument", namespace = Namespace.NAMESPACE)
    public List<ReferencedResource> getExtraDocumentList() {
        return extraDocumentList;
    }

    public void setExtraDocumentList(List<ReferencedResource> extraDocumentList) {
        this.extraDocumentList = extraDocumentList;
    }

    /**
     * This will ignore the Heading property to decide if the contents are
     * empty.
     *
     * @return
     */
    @JsonIgnore
    public boolean consideredEmpty() {
        return ((interDocumentList == null
                || (interDocumentList != null && checkEmptyList(interDocumentList)))
                && (intraDocumentList == null
                || (intraDocumentList != null && checkEmptyList(intraDocumentList)))
                && (extraDocumentList == null
                || (extraDocumentList != null && checkEmptyList(extraDocumentList))));
    }

    @JsonIgnore
    @Override
    public boolean checkEmpty() {
        return ((heading == null || (heading != null && heading.checkEmpty()))
                && consideredEmpty());
    }

    public static boolean checkEmptyList(List<? extends PrintableObject> list) {
        if (list == null) {
            return true;
        }
        if (list.isEmpty()) {
            return true;
        }
        for (PrintableObject item : list) {
            if (!item.checkEmpty()) {
                return false;
            }
        }
        return true;
    }

    @JsonIgnore
    @Override
    public List<ReferenceTo> listOfReferenceTo() {
        return intraDocumentList;
    }

    @JsonIgnore
    @Override
    public List<ReferenceTo> referenceToListWithIndex() {
        return withDocument(this.indexedList(intraDocumentList), getDocument());
    }

    @JsonIgnore
    public String coverLetterCommaList() {
        StringBuilder bld = new StringBuilder("");

        boolean prev = false;

        String interDocumentTxt = interDocumentTxt();
        if (!Strings.isNullOrEmpty(interDocumentTxt)) {
            bld.append(interDocumentTxt);
            prev = true;
        }
        String extraDocumentTxt = extraDocumentTxt();
        if (!Strings.isNullOrEmpty(extraDocumentTxt)) {
            if (prev) {
                bld.append(", ");
            }
            bld.append(extraDocumentTxt);
        }
        return bld.toString();
    }

    @JsonIgnore
    private String interDocumentTxt() {
        if (interDocumentList == null) {
            return "";
        }
        int size = interDocumentList.size();
        if (size == 0) {
            return "";
        }
        Locale locale = this.getDocument().getLocale();
        ResourceBundle bundle = ResourceBundle.getBundle("bundles/DocumentLabel", locale, new JsonResourceBundle.Control(new ObjectMapper()));

        List<String> labels = new ArrayList<>();
        for (ReferencedDocument doc : interDocumentList) {
            if (doc == null) {
                continue;
            }
            EuropassDocumentType ref = doc.getRef();
            if (ref == null || (ref != null && EuropassDocumentType.UNKNOWN.equals(ref))) {
                continue;
            }
            String label = bundle.getString(ref.getDocumentLabelKey());
            if (Strings.isNullOrEmpty(label)) {
                continue;
            }
            labels.add(label);
        }
        return Joiner.on(", ").join(labels);
    }

    @JsonIgnore
    private String extraDocumentTxt() {
        if (extraDocumentList == null) {
            return "";
        }
        int size = extraDocumentList.size();
        if (size == 0) {
            return "";
        }
        List<String> labels = new ArrayList<>();
        for (ReferencedResource res : extraDocumentList) {
            if (res == null) {
                continue;
            }
            String label = res.getDescription();
            if (Strings.isNullOrEmpty(label)) {
                continue;
            }
            labels.add(label);
        }
        return Joiner.on(", ").join(labels);
    }
}
