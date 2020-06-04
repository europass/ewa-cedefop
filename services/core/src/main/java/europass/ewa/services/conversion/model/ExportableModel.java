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

import java.util.List;

import europass.ewa.enums.ConversionFileType;
import europass.ewa.enums.EuropassDocumentType;
import europass.ewa.enums.ExportDestination;
import europass.ewa.enums.LogFields;
import europass.ewa.logging.ExtraLogInfo;
import europass.ewa.model.Metadata;
import europass.ewa.model.SkillsPassport;
import europass.ewa.model.wrapper.Feedback;
import europass.ewa.model.wrapper.FeedbackList;
import europass.ewa.statistics.DocumentGenerator;

public interface ExportableModel {

    SkillsPassport getModel();

    void setModel(SkillsPassport esp);

    void setModel(SkillsPassport esp, EuropassDocumentType documentType, ConversionFileType fileType);

    void setModel(SkillsPassport esp, ConversionFileType fileType);

    DocumentGenerator getGenerator();

    void setGenerator(DocumentGenerator generator);

    EuropassDocumentType getDocumentType();

    void setDocumentType(EuropassDocumentType documentType);

    ConversionFileType getFileType();

    void setFileType(ConversionFileType fileType);

    byte[] asBytes();

    void setBytes(byte[] documentBytes);

    String jsonRepresentation();

    void setJsonRepresentation(String json);

    String xmlRepresentation();

    void setXmlRepresentation(String xml);

    TranslationInfo getTranslationInfo();

    void setTranslationInfo(TranslationInfo translationInfo);

    //Idea - Recipient may be an email or any other string e.g. a cooperating partner
    String getRecipient();

    void setRecipient(String recipient);

    FeedbackList getFeedback();

    void setFeedback(FeedbackList feedback);

    void addFeedback(List<Feedback> feedback);

    String getRecipientIp();

    void setRecipientIp(String recipientIp);

    //Set statistics recording state
    Boolean getKeepStats();

    void setKeepStats(Boolean stats);

    //Set the document export destination
    ExportDestination getExportDestination();

    void setExportDestination(ExportDestination exportDestination);

    /* adds the input parameters to the existing log info object **/
    void augmentLogInfo(ExtraLogInfo additionalInfo);

    void augmentLogInfo(LogFields field, String value);

    Metadata croppingExportPhotoMeta();

    Metadata croppingExportSignatureMeta();

    void cleanUpCropping();

    /* @returns the log info object augmented with more log field data from the model's wrapper*/
    ExtraLogInfo getExtraLogInfo();

    /* returns the 
	 * @param Log field to be retrieved
	 * @returns null if no info or no field data exists
	 * @returns the ExtraLogInfo object with the field requested */
    ExtraLogInfo getExtraLogField(LogFields field);

}
