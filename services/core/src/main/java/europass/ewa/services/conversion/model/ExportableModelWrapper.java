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
package europass.ewa.services.conversion.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.google.common.base.Strings;

import europass.ewa.enums.ConversionFileType;
import europass.ewa.enums.EuropassDocumentType;
import europass.ewa.enums.ExportDestination;
import europass.ewa.enums.LogFields;
import europass.ewa.logging.ExtraLogInfo;
import europass.ewa.model.DocumentInfo;
import europass.ewa.model.Metadata;
import europass.ewa.model.SkillsPassport;
import europass.ewa.model.wrapper.Feedback;
import europass.ewa.model.wrapper.FeedbackList;
import europass.ewa.statistics.DocumentGenerator;

public class ExportableModelWrapper implements ExportableModel {

    private SkillsPassport esp;

    private DocumentGenerator generator;

    private EuropassDocumentType documentType;

    private ConversionFileType fileType;

    private byte[] documentBytes;

    private String xmlRepresentation;

    private String jsonRepresentation;

    private TranslationInfo translationInfo;

    private String recipient;

    private FeedbackList feedback;

    private String recipientIp;

    private Boolean keepstats;

    private ExportDestination exportDestination;

    private ExtraLogInfo extraLogInfo;

    @Override
    public SkillsPassport getModel() {
        return esp;
    }

    @Override
    public void setModel(SkillsPassport esp) {

        this.esp = esp;
        if (this.keepstats == null) {
            this.keepstats = Boolean.TRUE;
        }
    }

    @Override
    public void setModel(SkillsPassport esp, EuropassDocumentType documentType, ConversionFileType fileType) {

        DocumentInfo documentInfo = esp.getDocumentInfo();
        if (documentInfo == null) {
            documentInfo = new DocumentInfo();
        }
        documentInfo.setDocumentType(documentType);

        this.esp = esp;
        this.documentType = documentType;
        this.fileType = fileType;
        if (this.keepstats == null) {
            this.keepstats = Boolean.TRUE;
        }
    }

    @Override
    public void setModel(SkillsPassport esp, ConversionFileType fileType) {

        this.esp = esp;
        this.decideDocumentType(fileType);
        this.fileType = fileType;
        if (this.keepstats == null) {
            this.keepstats = Boolean.TRUE;
        }
    }

    @Override
    public DocumentGenerator getGenerator() {
        return generator;
    }

    @Override
    public void setGenerator(DocumentGenerator generator) {
        this.generator = generator;
    }

    @Override
    public EuropassDocumentType getDocumentType() {
        return documentType;
    }

    @Override
    public void setDocumentType(EuropassDocumentType documentType) {
        this.documentType = documentType;
    }

    /**
     * Decides on the Document Type - if none is given - or if the file type is
     * JSON or XML, where
     *
     * @param fileType
     */
    private void decideDocumentType(ConversionFileType fileType) {
        //1. Get esp (model)
        SkillsPassport esp = this.getModel();

        //2. Read the 
        EuropassDocumentType documentType = esp.returnDocumentType();

        esp.updateDocumentType(documentType);
        this.documentType = documentType;
    }

    @Override
    public ConversionFileType getFileType() {
        return fileType;
    }

    @Override
    public void setFileType(ConversionFileType fileType) {
        this.fileType = fileType;
    }

    @Override
    public byte[] asBytes() {
        return documentBytes;
    }

    @Override
    public void setBytes(byte[] documentBytes) {
        this.documentBytes = documentBytes;
    }

    @Override
    public String jsonRepresentation() {
        return jsonRepresentation;
    }

    @Override
    public void setJsonRepresentation(String json) {
        this.jsonRepresentation = json;
    }

    @Override
    public String xmlRepresentation() {
        return xmlRepresentation;
    }

    @Override
    public void setXmlRepresentation(String xml) {
        this.xmlRepresentation = xml;
    }

    @Override
    public TranslationInfo getTranslationInfo() {
        if (translationInfo == null && esp.getLocale() != null) {
            translationInfo = new TranslationInfo(esp.getLocale(), false);
        }
        return translationInfo;
    }

    @Override
    public void setTranslationInfo(TranslationInfo translationInfo) {
        this.translationInfo = translationInfo;
        //Set the locale of ESP too
        Locale transLocale = translationInfo.getLocale();
        if (esp != null && transLocale != null) {
            esp.setLocale(transLocale);
        }
    }

    @Override
    public String getRecipient() {
        return recipient;
    }

    @Override
    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    @Override
    public FeedbackList getFeedback() {
        if (feedback == null) {
            feedback = new FeedbackList(new ArrayList<Feedback>());
        }
        return feedback;
    }

    @Override
    public void setFeedback(FeedbackList furtherFeedback) {
        getFeedback().addAll(furtherFeedback);
    }

    @Override
    public void addFeedback(List<Feedback> furtherFeedback) {
        getFeedback().addAll(furtherFeedback);
    }

    @Override
    public String getRecipientIp() {
        return recipientIp;
    }

    @Override
    public void setRecipientIp(String recipientIp) {
        this.recipientIp = recipientIp;
    }

    @Override
    public Boolean getKeepStats() {
        return keepstats;
    }

    @Override
    public void setKeepStats(Boolean stats) {
        this.keepstats = Boolean.valueOf(stats);
    }

    @Override
    public ExportDestination getExportDestination() {
        return exportDestination;
    }

    @Override
    public void setExportDestination(ExportDestination exportDestination) {
        this.exportDestination = exportDestination;
    }

    public void augmentLogInfo(ExtraLogInfo log) {

        if (extraLogInfo == null) {
            extraLogInfo = log;
        } else {
            extraLogInfo.add(log);
        }
    }

    public void augmentLogInfo(LogFields field, String value) {
        if (!Strings.isNullOrEmpty(value)) {
            if (extraLogInfo == null) {
                extraLogInfo = new ExtraLogInfo().add(field, value);
            } else {
                extraLogInfo.add(field, value);
            }
        }
    }

    public ExtraLogInfo getExtraLogInfo() {

        if (extraLogInfo == null) {
            extraLogInfo = new ExtraLogInfo();
        }

        if (this.getFileType() != null && this.getFileType().name() != null) /*extraLogInfo.get(LogFields.FILETYPE*/ {
            extraLogInfo.add(LogFields.FILETYPE, this.getFileType().name());
        }

        if (this.getDocumentType() != null && this.getDocumentType().getDesription() != null) {
            extraLogInfo.add(LogFields.DOCTYPE, this.getDocumentType().getDesription());
        }

        if (this.getExportDestination() != null && this.getExportDestination().getDescription() != null) {
            extraLogInfo.add(LogFields.LOCATION, this.getExportDestination().getDescription());
        }

        return extraLogInfo;
    }

    /* getExtraLogField 
	 * @returns requested LogField (if not empty)
	 * @returns null if infomap or Field are empty
	 * **/
    public ExtraLogInfo getExtraLogField(LogFields field) {
        return (extraLogInfo != null && extraLogInfo.getLogEntry(field) != null
                ? new ExtraLogInfo().add(field, extraLogInfo.getLogEntry(field)) : null);
    }

    @Override
    public void cleanUpCropping() {

        if (this.esp != null) {
            if (this.esp.personalPhoto() != null) {
                if (!this.esp.personalPhoto().checkEmpty()) {
                    this.esp.personalPhoto().removeMetadata(Metadata.CROPPING_EXPORT);
                }
            }
            if (this.esp.personalSignature() != null) {
                if (!this.esp.personalSignature().checkEmpty()) {
                    this.esp.personalSignature().removeMetadata(Metadata.CROPPING_EXPORT);
                }
            }
        }
    }

    @Override
    public Metadata croppingExportPhotoMeta() {
        if (this.esp != null) {
            if (this.esp.personalPhoto() != null) {
                if (!this.esp.personalPhoto().checkEmpty()) {
                    return this.esp.personalPhoto().getMetadataObj(Metadata.CROPPING_EXPORT);
                }
            }
        }
        return null;
    }

    @Override
    public Metadata croppingExportSignatureMeta() {
        if (this.esp != null) {
            if (this.esp.personalSignature() != null) {
                if (!this.esp.personalSignature().checkEmpty()) {
                    return this.esp.personalSignature().getMetadataObj(Metadata.CROPPING_EXPORT);
                }
            }
        }
        return null;
    }

}
