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
package europass.ewa.services.conversion;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.inject.Singleton;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.itextpdf.text.exceptions.BadPasswordException;
import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfArray;
import com.itextpdf.text.pdf.PdfDictionary;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfString;

import europass.ewa.Constants;
import europass.ewa.enums.LogFields;
import europass.ewa.logging.ExtraLogInfo;
import europass.ewa.services.enums.UploadStatus;
import europass.ewa.services.exception.ApiException;
import europass.ewa.services.exception.ErrorMessageBundle;
import europass.ewa.services.exception.PDFLockedException;
import java.io.IOException;

@Singleton
public class PDFAttachmentExtractor {

    private static final Logger LOG = LoggerFactory.getLogger(PDFAttachmentExtractor.class);

    private static final ExtraLogInfo extraLogInfo = new ExtraLogInfo().add(LogFields.FILETYPE, "PDF").add(LogFields.ACTION, "XML Extraction from PDF");

    private PDFAttachmentExtractor() {
    }

    public static String extractAttachment(InputStream in) {
        return extractAttachment(in, null);
    }

    public static String extractAttachment(InputStream in, String userAgent) {
        String status = UploadStatus.PDF_XML_ATTACHMENT_FAILURE.getDescription();
        PdfReader reader = null;
        try {
            reader = new PdfReader(in);
            return extractAttachment(reader, userAgent);
        } catch (IOException e) {
            throw ApiException.addInfo(new ApiException(e, status, Status.BAD_REQUEST), extraLogInfo);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception tmp_ex) {
                LOG.error("Error in PDF Upload (pdf reader close)", tmp_ex);
            }
        }
    }

    public static String extractAttachment(byte[] bytes, String userAgent) {
        String status = UploadStatus.PDF_XML_ATTACHMENT_FAILURE.getDescription();
        PdfReader reader = null;
        try {
            reader = new PdfReader(bytes);
            return extractAttachment(reader, userAgent);
        } catch (IOException e) {
            throw ApiException.addInfo(new ApiException(e, status, Status.BAD_REQUEST), extraLogInfo);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception tmp_ex) {
                LOG.error("Error in PDF Upload (pdf reader close)", tmp_ex);
            }
        }
    }

    private static String extractAttachment(PdfReader reader, String userAgent) {
        String status = UploadStatus.PDF_XML_ATTACHMENT_FAILURE.getDescription();

        try {

            PdfDictionary root = reader.getCatalog();
            PdfDictionary documentnames = root.getAsDict(PdfName.NAMES);

            PdfDictionary embeddedfiles = null;
            if (documentnames != null) {
                embeddedfiles = documentnames.getAsDict(PdfName.EMBEDDEDFILES);
            }
            PdfArray filespecs = null;
            if (documentnames == null || embeddedfiles == null) {
                List<PdfObject> pdfFileSpecs = new ArrayList<PdfObject>();

                //It may be a PDF produced by the previous CV Editor
                for (int k = 1; k <= reader.getNumberOfPages(); ++k) {
                    PdfArray annots = (PdfArray) PdfReader.getPdfObject(reader
                            .getPageN(k).get(PdfName.ANNOTS));
                    if (annots == null) {
                        continue;
                    }
                    for (Iterator<PdfObject> i = annots.listIterator(); i.hasNext();) {
                        PdfDictionary annot = (PdfDictionary) PdfReader
                                .getPdfObject((PdfObject) i.next());
                        PdfName subType = (PdfName) PdfReader.getPdfObject(annot
                                .get(PdfName.SUBTYPE));
                        if (!PdfName.FILEATTACHMENT.equals(subType)) {
                            continue;
                        }
                        PdfDictionary filespec = (PdfDictionary) PdfReader
                                .getPdfObject(annot.get(PdfName.FS));

                        if (filespec == null) {
                            continue;
                        } else {
                            pdfFileSpecs.add(new PdfString("Legacy Attachment to PDF"));
                            pdfFileSpecs.add(filespec);
                        }
                    }
                }
                filespecs = new PdfArray(pdfFileSpecs);
            } else {
                //Proceed with the implementation that manages to get attachments from the PDF produced by the latest CV editor (CEDEFOP III - EWA)
                filespecs = embeddedfiles.getAsArray(PdfName.NAMES);
            }

            PdfDictionary filespec;
            PdfDictionary refs;

            PRStream stream;

            for (int i = 0; i < filespecs.size();) {

                filespecs.getAsString(i++); //this is the name of the file
                filespec = filespecs.getAsDict(i++); //this is the actual file

                refs = filespec.getAsDict(PdfName.EF);

                if (refs == null) {
                    break;
                }

                for (PdfName key : refs.getKeys()) {

                    String attachmentName = filespec.getAsString(key).toString();
                    //Check if the file name is supported (contained in the final static set of Attachment file names -note that the file name includes the .xml extension
                    if (Constants.PDF_XML_ATTACHMENT_NAMES.contains(attachmentName)) {
                        stream = (PRStream) PdfReader.getPdfObject(refs.getAsIndirectObject(key));
                        return new String(PdfReader.getStreamBytes(stream), Constants.UTF8_ENCODING);
                    }
                }
            }
            String producerMeta = reader.getInfo().get("Producer").toLowerCase();

            boolean invalidProducer = !producerMeta.contains("libreoffice 4.") || !producerMeta.contains("modified using itext"),
                    producedWithQuartz = producerMeta.contains("quartz"),
                    producedWithMSWord = producerMeta.contains("microsoft") && producerMeta.contains("word");

            if (producerMeta != null && !"".equals(producerMeta)) {

                status = invalidProducer ? UploadStatus.PDF_XML_ATTACHMENT_INVALID_HEADER.getDescription() : status;
                status = producedWithQuartz ? UploadStatus.PDF_XML_ATTACHMENT_INVALID_QUARTZ.getDescription() : status;
                status = producedWithMSWord ? UploadStatus.PDF_XML_ATTACHMENT_INVALID_WORD.getDescription() : status;
            }

            //​​Instead of relying on the PDF producer, fall-back to the user agent
            if (userAgent != null && invalidProducer && !producedWithQuartz) {
                if (userAgent.toLowerCase().contains("mac os x 10")) {
                    status = UploadStatus.PDF_XML_ATTACHMENT_FAILURE_MAC_OS.getDescription();
                }
            }

            //if reached here then we have failed to find an attachment
            String defaultMessage = "Failed to find an attachment that matches one of the expected names: " + Arrays.toString(Constants.PDF_XML_ATTACHMENT_NAMES_ARR), message = ErrorMessageBundle.get(status, defaultMessage);

            LOG.error(defaultMessage);

            throw ApiException.addInfo(new ApiException("UPLOAD PDF - Could not extract Europass XML Attachment.\n" + message, status, Status.BAD_REQUEST),
                    extraLogInfo);
        } catch (BadPasswordException e) {
            throw ApiException.addInfo(new PDFLockedException(), extraLogInfo);
        } catch (final Exception e) {
            throw ApiException.addInfo(new ApiException(e, status, Status.BAD_REQUEST), extraLogInfo);
        }
    }
}
