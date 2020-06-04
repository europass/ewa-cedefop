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
package europass.ewa.services.conversion.steps;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import europass.ewa.enums.ConversionFileType;
import europass.ewa.enums.EuropassDocumentType;
import europass.ewa.model.Attachment;
import europass.ewa.model.AttachmentVisitor;
import europass.ewa.model.AttachmentsInfo;
import europass.ewa.model.SkillsPassport;
import europass.ewa.services.ODT;
import europass.ewa.services.PDF;
import europass.ewa.services.WORD;
import europass.ewa.services.conversion.model.ExportableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttachmentsVisibilityStep extends AbstractDocumentGenerationStep {

    private static final Logger LOG = LoggerFactory.getLogger(AttachmentsVisibilityStep.class);

    @Inject
    @ODT
    private static AttachmentVisitor odtAttachmentVisitor;

    @Inject
    @WORD
    private static AttachmentVisitor wordAttachmentVisitor;

    @Inject
    @PDF
    private static AttachmentVisitor pdfAttachmentVisitor;

    @Override
    public void doStep(ExportableModel model) {
        final long time = System.currentTimeMillis();

        SkillsPassport esp = model.getModel();

        boolean includeAttachments = false;

        // Continue if there are attachments in the model
        if (esp.hasAttachments()) {
            EuropassDocumentType document = model.getDocumentType();

            includeAttachments = document.equals(EuropassDocumentType.ECV_ESP) || document.equals(EuropassDocumentType.ESP);
        }
        //Prepare the list of Attachments to be added to the document
        if (includeAttachments) {
            ConversionFileType fileType = model.getFileType();

            AttachmentsInfo info = esp.getAttachmentsInfo();

            boolean skip = false;
            AttachmentVisitor visitor = null;
            switch (fileType) {
                case OPEN_DOC: {
                    visitor = odtAttachmentVisitor;
                }
                case WORD_DOC: {
                    visitor = visitor == null ? wordAttachmentVisitor : visitor;
                    //Attachments to be included during Mustache execution
                    info.setIncludeInline(true);
                    break;
                }
                case PDF: {
                    visitor = pdfAttachmentVisitor;
                    //Attachments to be included during PDF post-processing by the OfficeClient
                    info.setIncludeInline(false);
                    break;
                }
                default: {
                    skip = true;
                    break;
                }
            }
            if (!skip) {
                List<Attachment> attachments = prepareAttachments(esp, visitor);

                boolean hasAttachments = attachments.size() > 0;
                info.setShowable(hasAttachments);
                if (hasAttachments) {
                    info.setVisibleAttachments(attachments);
                }
            }
        }
        LOG.debug("finished step " + this + " after " + (System.currentTimeMillis() - time) + "ms");
        super.doStep(model);
    }

    /**
     * Include in the list only the visible attachments.
     *
     * Depending on the File type and the type of the document, pre-process the
     * document: 1. case ODT/WORD: i) case PDF convert each page to image using
     * jPedal or PDFBox ii) resize properly the image attachments, or the PDF
     * converted images 2. case PDF: i) do nothing
     *
     * @param esp
     * @param visitor
     * @return
     */
    private List<Attachment> prepareAttachments(SkillsPassport esp, AttachmentVisitor visitor) {
        // Prepare the list of attachments to be sent to the ODT to PDF converter
        List<Attachment> attachmentList = new ArrayList<Attachment>();
        List<Attachment> includedAttachments = esp.getOrderedAttachmentList();

        if (includedAttachments != null && includedAttachments.size() > 0) {

            for (Attachment includedAttachment : includedAttachments) {

                // Ignore files that do not have data! (in case of problem the data wont have been included at previous step
                byte[] filedata = includedAttachment.getData();
                if (filedata != null && filedata.length > 0) {

                    Attachment addThis = new Attachment(includedAttachment);
                    addThis.accept(visitor);

                    //If ok, add to list...
                    if (addThis.isParseable()) {
                        attachmentList.add(addThis);
                    }
                }
            }
        }
        return attachmentList;
    }

}
