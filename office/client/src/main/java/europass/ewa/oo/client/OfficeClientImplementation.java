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
package europass.ewa.oo.client;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.core.MediaType;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.jpedal.PdfDecoder;
import org.jpedal.exception.PdfException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfFileSpecification;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.multipart.BodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.file.FileDataBodyPart;

import europass.ewa.collections.ListReverser;
import europass.ewa.enums.ConversionFileType;
import europass.ewa.enums.PDFLibrary;
import europass.ewa.model.Attachment;
import europass.ewa.model.wrapper.Feedback;
import europass.ewa.model.wrapper.FeedbackFactory;
import europass.ewa.oo.client.exception.AttachmentError;
import europass.ewa.oo.client.exception.ConversionError;
import europass.ewa.oo.client.exception.NoServerAvailable;
import europass.ewa.oo.client.exception.NoServerConfiguration;
import europass.ewa.oo.client.module.OfficeClientModule;
import java.util.Random;

public class OfficeClientImplementation implements OfficeClient {

    private static final float INCH_RATIO = 72.0f;

    private static final Logger LOG = LoggerFactory.getLogger(OfficeClientImplementation.class);

    private List<URI> activeOOServers;

    private Client client;

    // Temporary objects to cleanup
    private File tempPdf = null;
    private File tempPdf2 = null;

    // Temporary List of Warning Messages
    private List<Feedback> feedback = null;

    private String jPedalEnabledProp;

    private OfficeClientPipeThread oThread;

    @Inject
    public OfficeClientImplementation(@Named(OfficeClientModule.ACTIVE_OFFICE_CLIENT_SERVERS) List<URI> activeOOServers,
            @Named(OfficeClientModule.OFFICE_REST_CLIENT) Client client,
            @Named("europass-ewa-services.pdf.library.jpedal.enabled") String jPedalEnabledProp) {
        this.activeOOServers = activeOOServers;
        this.client = client;
        this.jPedalEnabledProp = jPedalEnabledProp;
    }

    /**
     * Steps for PipedInputStream / PipedOutputStream: a. Start with creating.
     * Connect pipes. b. Create a thread for reading in. It will create the
     * BodyPart (StreamDataBodyPart) c. final step is the one that closes the
     * stream , and upon thread release, submits it;
     *
     * @return an OutputStream to use for writing
     * @throws NoServerConfiguration , When no Server is configured
     * @throws ConversionError , Error in conversion procedure
     */
    @Override
    public OutputStream startConvert() {
        return startConvert(null);
    }

    public OutputStream startConvert(String requestId) throws NoServerConfiguration, ConversionError, NoServerAvailable {
        getFirstAvailable(requestId);
        // Make Pipes
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out;
        try {
            out = new PipedOutputStream(in);
        } catch (IOException e) {
            throw new ConversionError(e);
        }
        oThread = new OfficeClientPipeThread(in);
        oThread.start();
        return out;
    }

    /**
     * End of conversion
     *
     * @param data , the outputstream
     * @param fileType , ConversionFileType
     * @return result, an inputStream
     * @throws NoServerConfiguration , When no Server is configured
     * @throws NoServerAvailable , No Servers available
     * @throws ConversionError , Error in conversion procedure
     */
    @Override
    public InputStream endConvert(OutputStream data, ConversionFileType fileType) throws NoServerConfiguration, NoServerAvailable,
            ConversionError, AttachmentError {
        return endConvert(data, fileType, null, null);
    }

    public InputStream endConvert(OutputStream data, ConversionFileType fileType, Attachment xmlAttachment, List<Attachment> attachments)
            throws NoServerConfiguration, NoServerAvailable, ConversionError, AttachmentError {
        return endConvert(data, fileType, xmlAttachment, attachments, null);
    }

    /**
     * End of conversion. Flushes and closes outputstream, and returns an
     * InputStream
     *
     * @param data , the outputstream
     * @param fileType , ConversionFileType
     * @param xmlAttachment
     * @param attachments , array of Attachment containing files to attach to a
     * generated pdf
     * @param requestId
     *
     * @return result, an inputStream
     *
     * @throws NoServerConfiguration , When no Server is configured
     * @throws NoServerAvailable , No Servers available
     * @throws ConversionError , Error in conversion procedure
     * @throws AttachmentError
     */
    @Override
    public InputStream endConvert(OutputStream data, ConversionFileType fileType, Attachment xmlAttachment, List<Attachment> attachments, String requestId)
            throws NoServerConfiguration, NoServerAvailable, ConversionError, AttachmentError {

        if (data != null) {
            try {
                data.flush();
                data.close();
            } catch (IOException e) {
                log("IO Exception while trying to close the under-conversion data output stream.", requestId, e);
                throw new ConversionError(e);
            }
        }

        // Waiting....
        while (oThread.isAlive() && !oThread.isInterrupted() && !oThread.isCompleted()) {
            try {
                Thread.sleep(10);
            } catch (Exception ex) {
                // Sleep yields execution time
            }
        }

        // Prepare Multipart to be sent to Office Server
        FormDataMultiPart transientForm = new FormDataMultiPart();
        try {
            BodyPart bpart = null;
            if (oThread.isCompleted()) {
                bpart = oThread.getBodyPart();
                if (bpart == null) {
                    throw new ConversionError("Could not create a valid BodyPart ( body part is null)");
                }
            } else {
                throw new ConversionError("Could not create a valid BodyPart (process failed to complete)");
            }

            transientForm.bodyPart(bpart);

            ClientResponse response = requestConversion(fileType, transientForm, requestId);

            return considerAttachments(fileType, response.getEntityInputStream(), xmlAttachment, attachments);

        } finally {
            try {
                transientForm.close();
            } catch (IOException e) {
                log("OfficeClientimplementation:endConvert Failed to close form data multi part", requestId, e);
            }
        }
    }

    /**
     * Finalize the conversion
     *
     * @param fileType
     * @param inStream
     * @param xmlAttachment
     * @param attachments
     * @return
     */
    private InputStream considerAttachments(ConversionFileType fileType, InputStream inStream, Attachment xmlAttachment,
            List<Attachment> attachments) {
        // No attachments available OR DOC conversion requested
        boolean simpleConvert = (ConversionFileType.WORD_DOC.equals(fileType));
        if (simpleConvert) {
            return inStream;
        }
        // Add attachments to PDF
        FileInputStream ret = addAttachments(inStream, xmlAttachment, attachments);
        return ret;
    }

    /**
     * Request a conversion from office server based on a Specific Conversion
     * type
     *
     * @param fileType
     * @param form
     * @return
     * @throws ConversionError
     */
    private ClientResponse requestConversion(ConversionFileType fileType, FormDataMultiPart form, String requestId) throws ConversionError {
        URI server = getFirstAvailable(requestId);

        WebResource res = client.resource(server).path("/convert/" + fileType.getDescription());

        ClientResponse response = res.header("X-Request-ID", requestId).type(MediaType.MULTIPART_FORM_DATA).post(ClientResponse.class, form);
        int status = response.getStatusInfo().getStatusCode();
        if (status >= 400) {
            throw new ConversionError("Error " + status + " retrieving data "
                    + server.getHost() + ":" + server.getPort());
        }
        return response;
    }

    /**
     * Convert a File
     *
     * @param in , the File to convert
     * @param fileType
     * @return result,an inputStream
     * @throws NoServerConfiguration ,When no Server is configured
     * @throws NoServerAvailable ,No Servers available
     * @throws ConversionError ,Error in conversion procedure
     */
    @Override
    public InputStream convert(File in, ConversionFileType fileType) throws NoServerAvailable, ConversionError, NoServerConfiguration,
            AttachmentError {
        return convert(in, fileType, null, null, false);
    }

    /**
     * Convert a File
     *
     * @param file , the File to convert
     *
     * @param fileType
     * @param xmlAttachment , The Attachment of the CV as XML
     *
     * @param attachments , array of Attachment containing files to attach to a
     * generated pdf
     *
     * @param append , Append pdf pages at the end of the document
     *
     * @return an inputStream
     *
     * @throws NoServerConfiguration , When no Server is configured
     * @throws NoServerAvailable , No Servers available
     * @throws ConversionError , Error in conversion procedure
     * @throws AttachmentError
     */
    @Override
    public InputStream convert(File file, ConversionFileType fileType, Attachment xmlAttachment, List<Attachment> attachments,
            boolean append) throws NoServerAvailable, ConversionError, NoServerConfiguration, AttachmentError {

        if (!file.isFile() || !file.exists()) {
            throw new ConversionError("File does not exist or is directory");
        }

        // Output from Client Response
        FormDataMultiPart form = new FormDataMultiPart();
        BodyPart bpart = new FileDataBodyPart("file", file, MediaType.APPLICATION_OCTET_STREAM_TYPE);
        form.bodyPart(bpart);
        ClientResponse response = requestConversion(fileType, form, null);

        return considerAttachments(fileType, response.getEntityInputStream(), xmlAttachment, attachments);
    }

    /**
     * ****************************************************************
     */
    /**
     * ****************************************************************
     */
    /**
     * ************** INCLUDE DIGITAL ATTACHMENT TO PDF ****************
     */
    /**
     * Prepares the PDF document 1. Adds the CV XML as digital attachment 2.
     * Appends the attachments as extra pages in the PDF
     *
     * @param inStream
     * @param xmlAttachment
     * @param attachments
     * @return
     * @throws AttachmentError
     */
    private FileInputStream addAttachments(InputStream inStream, Attachment xmlAttachment, List<Attachment> attachments)
            throws AttachmentError {
        try {
            // Step 1 Add XML - Affects the tempPdf
            addXmlAttachment(new PdfReader(inStream), xmlAttachment);

            // Step 2 Add Attachments - Affects the tempPdf2
            if (attachments != null && attachments.size() > 0) {
                appendAttachment(attachments);
                return new FileInputStream(tempPdf2);
            }
            return new FileInputStream(tempPdf);

        } catch (Exception e) {
            throw new AttachmentError(e);
        }

    }

    /**
     * ************** INCLUDE ATTACHMENTS AS PDF PAGES ****************
     */
    /**
     * Appends the attachmens as pages to the last pages of the PDF
     *
     * @param attachments
     */
    private void appendAttachment(List<Attachment> attachments) {
        FileOutputStream fos = null;
        FileInputStream in = null;
        PdfReader reader = null;
        PdfStamper stamper = null;

        try {
            tempPdf2 = File.createTempFile("tempPdf2", ".pdf");
            fos = new FileOutputStream(tempPdf2);

            in = new FileInputStream(tempPdf);

            reader = new PdfReader(in);

            stamper = new PdfStamper(reader, fos);

            int totalPages = reader.getNumberOfPages();
            int currentPage = totalPages;
            Rectangle sizes = reader.getPageSize(1);

            // Traverse attachments end to start
            for (int idx = attachments.size() - 1; idx >= 0; idx--) {

                Attachment attachment = attachments.get(idx);

                if (attachment == null) {
                    continue;
                }

                List<Image> extraPages = new ArrayList<>();

                if (attachment.isPDF()) {
                    PDFLibrary library = attachment.getPdfLibrary();
                    if (library == null) {
                        library = PDFLibrary.JPedal;
                    }

                    switch (library) {
                        case JPedal: {
                            extraPages.addAll(attachPDFWithJPedal(attachment.getData(), sizes, attachment.getName()));
                            break;
                        }
                        case PDFBox: {
                            extraPages.addAll(attachPDFWithPDFBox(attachment.getData(), sizes, attachment.getName()));
                            break;
                        }
                        // iText
                        default: {
                            extraPages.addAll(attachPDFWithIText(stamper, attachment, sizes));
                            break;
                        }
                    }

                } else if (attachment.isImage()) {
                    extraPages.addAll(attachImage(attachment, sizes));
                }

                for (Image extraPage : extraPages) {
                    PdfContentByte content = stamper.getOverContent(currentPage);
                    content.addImage(extraPage);
                    currentPage--;
                }

            }
        } catch (Exception e) {
            LOG.error("OfficeClient:: Failed to append attachment pages to PDF", e);
            this.addFeedback(FeedbackFactory.allAttachmentInDocument());
        } finally {
            if (stamper != null) {
                try {
                    stamper.close();
                } catch (Exception e) {
                }
            }
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                }
            }
            /*
			 *  Changed closing sequence. The stamper should first be closed,and the the reader.
             */
            if (reader != null) {
                reader.close();
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * ****************** Attach Image in PDF Alternatives ******************
     */
    /**
     * Used during the normal flow of attaching PDF
     *
     * @param attachment
     * @param sizes
     * @return
     */
    private List<Image> attachImage(Attachment attachment, Rectangle sizes) {
        return this.attachImage(attachment.getData(), sizes, attachment.getName());
    }

    /**
     * Include a PDF read as Image as attachment
     *
     * @param data
     * @param sizes
     * @param attachmentName
     * @return
     */
    private List<Image> attachImage(byte[] data, Rectangle sizes, String attachmentName) {
        try {
            Image img = Image.getInstance(data);
            return this.attachImage(img, sizes, attachmentName, 1);
        } catch (Exception e) {
            LOG.error("Failed to include PDF as image attachment in Europass PDF.", e);
            this.addFeedback(FeedbackFactory.attachmentInDocument(attachmentName));
        }
        return Collections.emptyList();
    }

    /**
     * Scale if necessary an image in order to include in PDF
     *
     * @param img
     * @param sizes
     * @param attachmentName
     * @param pageNo
     * @return
     */
    private List<Image> attachImage(Image img, Rectangle sizes, String attachmentName, int pageNo) {
        List<Image> pdfPages = new ArrayList<>();
        try {
            float topLeftPosition = getTopPosition(pageNo);
            float downcorner = getDownCorner(sizes, img, pageNo);
            img.setAbsolutePosition(topLeftPosition, downcorner);

            pdfPages.add(img);

        } catch (Exception e) {
            LOG.error("Failed to include image attachment in PDF.", e);
            this.addFeedback(FeedbackFactory.attachmentInDocument(attachmentName));
        }
        return pdfPages;
    }

    /**
     * JPedal
     *
     * @param data
     * @param sizes
     * @param attachmentName
     * @return
     * @throws IOException
     * @throws BadElementException
     * @throws PdfException
     */
    private List<Image> attachPDFWithJPedal(byte[] data, Rectangle sizes, String attachmentName) throws IOException, BadElementException,
            PdfException {
        // using JPedal
        PdfDecoder decode_pdf = new PdfDecoder(true);
        List<Image> pdfPages = new ArrayList<>();

        decode_pdf.openPdfArray(data);

        int pages = decode_pdf.getPageCount();

        for (int pagesNo = pages; pagesNo > 0; pagesNo--) {

            BufferedImage bufferImage = decode_pdf.getPageAsImage(pagesNo);
            Image pdfImage = Image.getInstance(bufferImage, null);
            List<Image> image = this.attachImage(pdfImage, sizes, attachmentName, pagesNo);
            pdfPages.add(image.get(0));
        }

        return pdfPages;
    }

    /**
     * PDFBox
     *
     * @param data
     * @param sizes
     * @param attachmentName
     * @return
     * @throws IOException
     * @throws BadElementException
     * @throws PdfException
     */
    private List<Image> attachPDFWithPDFBox(byte[] data, Rectangle sizes, String attachmentName) throws IOException, BadElementException,
            PdfException {
        PDDocument document = null;
        List<Image> pdfPages = new ArrayList<>();

        try (ByteArrayInputStream in = new ByteArrayInputStream(data);) {

            document = PDDocument.load(in);

            final List<PDPage> pages = new ArrayList<>();
            for (final PDPage page : document.getPages()) {
                pages.add(page);
            }
            int pageNo = pages.size();
            final PDFRenderer pdfRenderer = new PDFRenderer(document);

            for (final PDPage page : new ListReverser<PDPage>(pages)) {

                final BufferedImage bufferImage = pdfRenderer.renderImageWithDPI(pageNo - 1, 96, ImageType.RGB);
                final Image pdfImage = Image.getInstance(bufferImage, null);
                // call attachImage to attach the BufferedImage as PDF Image Page
                final List<Image> image = this.attachImage(pdfImage, sizes, attachmentName, pageNo);
                pdfPages.add(image.get(0));
                pageNo--;
            }
        } catch (final Exception e) {
            if (Boolean.parseBoolean(jPedalEnabledProp) && e.getCause().toString().contains("javax.crypto.IllegalBlockSizeException")) {
                return attachPDFWithJPedal(data, sizes, attachmentName);
            }
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                }
            }
        }
        return pdfPages;
    }

    private static float getTopPosition(int pageNo) {
        return (pageNo == 1) ? Constants.FIRST_P_LEFT * INCH_RATIO : Constants.P_LEFT * INCH_RATIO;
    }

    private static float getDownCorner(Rectangle sizes, Image img, int pageNo) {
        float width = sizes.getWidth();
        float height = sizes.getHeight();

        if (pageNo == 1) {
            // first page of the document
            width = width - (Constants.FIRST_P_LEFT + Constants.FIRST_P_RIGHT) * INCH_RATIO;
            height = height - (Constants.FIRST_P_TOP + Constants.FIRST_P_BOTTOM) * INCH_RATIO;
        } else {
            // rest pages
            width = width - (Constants.P_LEFT + Constants.P_RIGHT) * INCH_RATIO;
            height = height - (Constants.P_TOP + Constants.P_BOTTOM) * INCH_RATIO;
        }

        float downcorner = 0;
        if (img.getWidth() > width || img.getHeight() > height) {
            img.scaleToFit(width, height);
            if (pageNo == 1) {
                downcorner = height - img.getScaledHeight() + Constants.FIRST_P_BOTTOM * INCH_RATIO;
            } else {
                downcorner = height - img.getScaledHeight() + Constants.P_BOTTOM * INCH_RATIO;
            }
        } else {
            float tmpConstant = Constants.P_BOTTOM;
            if (pageNo == 1) {
                tmpConstant = Constants.FIRST_P_BOTTOM;
                downcorner = height - img.getHeight() + Constants.FIRST_P_BOTTOM * INCH_RATIO;
            }
            downcorner = height - img.getHeight() + tmpConstant * INCH_RATIO;
        }
        return downcorner;
    }

    /**
     * Include the pages of a PDF attachment using iText
     *
     * @param stamper
     * @param attachment
     * @param sizes
     * @return
     */
    private List<Image> attachPDFWithIText(PdfStamper stamper, Attachment attachment, Rectangle sizes) {

        List<Image> pdfPages = new ArrayList<>();
        PdfReader newReader = null;
        try {
            newReader = new PdfReader(attachment.getData());

            for (int i = newReader.getNumberOfPages(); i >= 1; i--) {

                // import the page from source pdf
                PdfImportedPage page = stamper.getImportedPage(newReader, i);
                Image img = Image.getInstance(page);

                /* EWA-860 Attachments having wrong orientation (90 or 180 deegres rotated)
				 * if PdfStamper introduced rotation to the document, revert it
				 * page.getRotation() gets the rotation by itext and not the page's original rotation */
                if (page.getRotation() != 0) {
                    int rotation = (4 - page.getRotation() / 90) * 90;
                    img.setRotationDegrees(rotation);
                }

                float topLeftPosition = getTopPosition(i);
                float downcorner = getDownCorner(sizes, img, i);
                img.setAbsolutePosition(topLeftPosition, downcorner);

                pdfPages.add(img);
            }

        } catch (Exception e) {
            LOG.error("Failed to include PDF attachment in PDF.", e);
            this.addFeedback(FeedbackFactory.attachmentInDocument(attachment.getName()));
        } catch (Error e) {
            this.addFeedback(FeedbackFactory.attachmentInDocument(attachment.getName()));
        } finally {
            /*
			 *  It seems that with the new iText thread safe implementations / generation ( 5.5.1 ) 
			 *  the reader should not be closed, and the stamper should do the cleanup
			 *  This is the case also when more than one readers are attached on the stamper.
			 *  This might also indicate a bug on the iText side.
			 *  Since the readers are based on byte arrays and not on actual streams, there is no cleanup problem
             */
        }
        return pdfPages;
    }

    /**
     * Adds the CV XML as digital attachment
     *
     * @param reader
     * @param xmlAttachment
     */
    private void addXmlAttachment(PdfReader reader, Attachment xmlAttachment) {
        FileOutputStream fos = null;
        PdfStamper stamper = null;
        try {
            tempPdf = File.createTempFile("tempPdf", ".pdf");
            fos = new FileOutputStream(tempPdf);

            stamper = new PdfStamper(reader, fos);

            // Include XML Attachment
            if (xmlAttachment != null) {
                // Include only the XML as digital attachment
                addAttachment(stamper.getWriter(), xmlAttachment.getData(), xmlAttachment.getName(), xmlAttachment.getDescription(),
                        xmlAttachment.getMimeType());
            }
        } catch (DocumentException | IOException e) {
            LOG.error("OfficeClient:: Failed to add XML attachment ", e);
            this.addFeedback(FeedbackFactory.xmlAttachment());
        } finally {
            if (stamper != null) {
                try {
                    stamper.close();
                } catch (Exception e) {
                }
            }
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * Utility method to add attachment
     *
     * @param writer
     * @param data
     * @param name
     * @param description
     * @param fileType
     * @throws IOException
     */
    private void addAttachment(PdfWriter writer, byte[] data, String name, String description, String fileType) throws IOException {
        // writer - the PdfWriter
        // filePath - the file path
        // fileDisplay - the file information that is presented to the user
        // fileStore - the byte array with the file. If it is not null it takes
        // precedence over filePath
        // compress - sets the compression on the data. Multimedia content will
        // benefit little from compression
        // mimeType - the optional mimeType
        // fileParameter - the optional extra file parameters such as the
        // creation or modification date

        // TODO: Why cannot the attachment be opened? - Acrobat says it is so
        // according to attachment settings
        PdfFileSpecification fs = PdfFileSpecification.fileEmbedded(writer, null, name, data, true, fileType, null);
        writer.addFileAttachment(description, fs);
    }

    /**
     * ****************************************************************
     */
    /**
     * ****************************************************************
     */
    /**
     * ****************** OFFICE SERVER AVAILABILITY ******************
     */
    /**
     * Checks that the configuration is ok
     *
     * @return
     */
    private boolean isConfigured() {
        return (activeOOServers.size() > 0 && client != null);
    }

    /**
     * Returns the URI of the first available office server
     *
     * @return
     */
    private URI getFirstAvailable(String requestId) throws NoServerConfiguration, NoServerAvailable {
        if (!isConfigured()) {
            throw new NoServerConfiguration("There is no available office server configured.");
        }
        // Check if server available Recheck on endConvert
        URI server;
        try {
            server = decide(requestId);
        } catch (NoServerAvailable e) {
            throw new NoServerAvailable("There are no office servers available. " + e.getMessage());
        }
        return server;
    }

    /**
     * Decide which office server to use
     *
     * @return
     */
    private URI decide(String requestId) {
        if (activeOOServers == null || activeOOServers.size() < 1) {
            return null;
        }
        // EPAS-8 simply choose a random office server to use for conversion. Maybe in the future this is exchanged with haproxy level load balancing.
        Random randomizer = new Random();
        URI selected = activeOOServers.get(randomizer.nextInt(activeOOServers.size()));

        if (selected != null) {
            LOG.debug("SELECTED server=" + selected.toString());
            return selected;
        }
        //else, no servers were found
        throw new NoServerAvailable("");
    }

    /**
     * ****************************************************************
     */
    /**
     * ****************************************************************
     */
    /**
     * ****************** LIST OF FEEDBACK MESSAGE *******************
     */
    @Override
    public List<Feedback> getFeedback() {
        if (feedback == null) {
            return Collections.emptyList();
        } else {
            List<Feedback> gatheredFeedback = ImmutableSet.copyOf(feedback).asList();
            feedback.clear();
            return gatheredFeedback;
        }
    }

    private void addFeedback(Feedback info) {
        if (feedback == null) {
            feedback = new ArrayList<Feedback>();
        }
        feedback.add(info);
    }

    /**
     * ****************************************************************
     */
    /**
     * ****************************************************************
     */
    /**
     * ******************** CLEAN UP OF TEMP FILES ********************
     */
    /**
     * Cleanup . Run it at the end of the conversion process
     */
    @Override
    public void release() {
        release(null);
    }

    public void release(String requestId) {
        try {
            if (tempPdf != null && tempPdf.exists()) {
                tempPdf.delete();
            }
        } catch (Exception ex) {
            log("Error in OOClient Release ", requestId, ex);
        }
        try {
            if (tempPdf2 != null && tempPdf2.exists()) {
                tempPdf2.delete();
            }
        } catch (Exception ex) {
            log("Error in OOClient Release.2 ", requestId, ex);
        }
    }
    //utility method to log error messages and request Id (EWA 1520) in json format

    private void log(String message, String requestId, Exception e) {

        StringBuilder msg = new StringBuilder("\"Message\":\"" + message + "\"");
        if (requestId != null) {
            msg.append(",\"RequestId\":\"" + requestId + "\"");
        }
        LOG.error(msg.toString(), e);
    }
}
