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

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.annotation.Nullable;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import europass.ewa.enums.EuropassDocumentType;
import europass.ewa.model.custom.deserializers.CustomJsonDateDeserializer;
import europass.ewa.model.translation.LocalisableImpl;

@JsonPropertyOrder({"documentType", "bundle", "creationDate", "lastUpdateDate", "xsdversion", "generator", "comment", "EuropassLogo"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentInfo extends LocalisableImpl {

    private EuropassDocumentType documentType;

    private List<EuropassDocumentType> bundle;

    private DateTime creationDate;

    private DateTime lastUpdateDate;

    private String xsdversion;

    private String generator;

    private String comment;

    private String europassLogo;

    public DocumentInfo() {
    }

    public DocumentInfo(EuropassDocumentType documentType, DateTime creationDate, DateTime lastUpdateDate, String xsdversion, String generator, String comment, String europassLogo) {
        this.documentType = documentType;
        this.creationDate = creationDate;
        this.lastUpdateDate = lastUpdateDate;
        this.xsdversion = xsdversion;
        this.generator = generator;
        this.comment = comment;
        this.europassLogo = europassLogo;
    }

    @JsonProperty("DocumentType")
    @JacksonXmlProperty(localName = "DocumentType", namespace = Namespace.NAMESPACE)
    public EuropassDocumentType getDocumentType() {
        return documentType;
    }

    public void setDocumentType(EuropassDocumentType documentType) {
        this.documentType = documentType;
    }

    @JsonProperty("Document")
    @JacksonXmlProperty(localName = "Document", namespace = Namespace.NAMESPACE)
    @JacksonXmlElementWrapper(localName = "Bundle", namespace = Namespace.NAMESPACE)
    public List<EuropassDocumentType> getBundle() {
        return bundle;
    }

    public void setBundle(List<EuropassDocumentType> bundle) {
        this.bundle = bundle;
    }

    @JsonProperty("CreationDate")
    @JacksonXmlProperty(localName = "CreationDate", namespace = Namespace.NAMESPACE)
    @JsonDeserialize(using = CustomJsonDateDeserializer.class)
    public DateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(DateTime creationDate) {
        this.creationDate = creationDate;
    }

    @JsonProperty("LastUpdateDate")
    @JacksonXmlProperty(localName = "LastUpdateDate", namespace = Namespace.NAMESPACE)
    @JsonDeserialize(using = CustomJsonDateDeserializer.class)
    public DateTime getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(DateTime lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    @JsonProperty("XSDVersion")
    @JacksonXmlProperty(localName = "XSDVersion", namespace = Namespace.NAMESPACE)
    public String getXsdversion() {
        return xsdversion;
    }

    public void setXsdversion(String xsdversion) {
        this.xsdversion = xsdversion;
    }

    @JsonProperty("Generator")
    @JacksonXmlProperty(localName = "Generator", namespace = Namespace.NAMESPACE)
    public String getGenerator() {
        return generator;
    }

    public void setGenerator(String generator) {
        this.generator = generator;
    }

    @JsonProperty("Comment")
    @JacksonXmlProperty(localName = "Comment", namespace = Namespace.NAMESPACE)
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @JsonProperty("EuropassLogo")
    @JacksonXmlProperty(localName = "EuropassLogo", namespace = Namespace.NAMESPACE)
    public boolean getEuropassLogo() {
        return Strings.isNullOrEmpty(europassLogo) ? true : Boolean.parseBoolean(europassLogo);
    }

    public void setEuropassLogo(String europassLogo) {
        this.europassLogo = Strings.isNullOrEmpty(europassLogo) ? "true" : europassLogo;
    }

    @JsonIgnore
    public String documentTypeMetadata() {
        if (documentType == null) {
            return "";
        }
        return documentType.getMetadata();
    }

    @JsonIgnore
    public List<EuropassDocumentType> getBundleDocuments() {
        if (bundle == null) {
            return Collections.emptyList();
        }
        List<EuropassDocumentType> bundleDocuments
                = Lists.newArrayList(Collections2.filter(bundle, new Predicate<EuropassDocumentType>() {
                    @Override
                    public boolean apply(@Nullable EuropassDocumentType doc) {
                        if (doc == null) {
                            return false;
                        }
                        if (EuropassDocumentType.UNKNOWN.equals(doc)) {
                            return false;
                        }
                        return !doc.equals(documentType);
                    }
                }));
        return bundleDocuments;
    }

    /**
     * Used by ODT Generator to format the lastUpdateDate according to the
     * numeric/short format
     *
     * @return
     */
    @JsonIgnore
    public String formatLastUpdateDate() {
        if (this.lastUpdateDate != null) {

            //"numeric/short":"d.M.yy"
            String format = "numeric/short";
            JDate newJDate = new JDate();
            newJDate.setDay(this.lastUpdateDate.getDayOfMonth());
            newJDate.setMonth(this.lastUpdateDate.getMonthOfYear());
            newJDate.setYear(this.lastUpdateDate.getYear());
            //Locale docLocale = Locale.ENGLISH;
            Locale docLocale = super.getLocale();
            return newJDate.format(format, docLocale);
        } else {
            return "";
        }
    }

    @JsonIgnore
    public boolean withCoverLetter() {
        return inBundle(EuropassDocumentType.ECL, getDocumentType());
    }

    @JsonIgnore
    public boolean withLanguagePassport() {
        return inBundle(EuropassDocumentType.ELP, getDocumentType());
    }

    @JsonIgnore
    public boolean withSkillsPassport() {
        return inBundle(EuropassDocumentType.ESP, getDocumentType());
    }

    /*
	 * Utility to decide on the position of the extra document in respect to the main document.
	 * Jackson implementation will always respect the order of appearance of Document elements under the Bundle element.
	 * Therefore it is safe to conclude on the order by checking the indexOf the list.
     */
    private boolean inBundle(EuropassDocumentType extra, EuropassDocumentType main) {
        if (bundle == null || (bundle != null && bundle.isEmpty())) {
            return false;
        }

        if (extra == null || (extra != null && EuropassDocumentType.UNKNOWN.equals(extra))) {
            return false;
        }

        //When main is the same with extra, return false;
        if (main != null && extra.equals(main)) {
            return false;
        }

        int extraPos = bundle.indexOf(extra);
        if (extraPos == -1) {
            return false;
        }

        //In Bundle
        return true;
    }

}
