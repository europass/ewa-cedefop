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
package europass.ewa.services.files;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.jpedal.PdfDecoder;
import org.jpedal.fonts.FontMappings;

import com.itextpdf.text.pdf.PRAcroForm;
import com.itextpdf.text.pdf.PdfReader;

import europass.ewa.Utils;
import europass.ewa.collections.ListReverser;
import europass.ewa.enums.LogFields;
import europass.ewa.enums.PDFLibrary;
import europass.ewa.enums.ServerModules;
import europass.ewa.logging.ExtraLogInfo;
import europass.ewa.model.ByteMetadata;
import europass.ewa.services.exception.ApiException;
import europass.ewa.services.exception.FileNotParsableException;
import europass.ewa.services.exception.FileNotViewableException;
import europass.ewa.services.exception.PDFAsImageException;
import europass.ewa.services.exception.PDFLockedException;
import europass.ewa.services.exception.ThumbSavingException;

import static org.apache.pdfbox.rendering.ImageType.RGB;

public class PDFUtils {

    private PDFUtils() {
    }

    //http://www.biopdf.com/guide/pdf_permissions.php
    public static final int PDF_ASSEMBLE_DOCUMENT_BIT = 10;

    private static final int UNKNOWN_PERMISSIONS = 0;

    public static boolean isAssemblyAllowed(int value) {
        return Utils.isBitOn(value, PDF_ASSEMBLE_DOCUMENT_BIT);
    }

    private static ExtraLogInfo extraInfo = new ExtraLogInfo().add(LogFields.FILETYPE, "PDF").
            add(LogFields.MODULE, ServerModules.SERVICES_CORE.getModule());

    /**
     * when failing to read with iText AND failing to read with JPedal, throw
     * FileNotParsableException when iText say that it is Form, and Assembly is
     * allowed, then use PDFBox when iText says that Assembly is allowed, use
     * iText in all other cases where JPedal does not fail/ enabled through
     * config property (and iText may have failed, or assembly is disallowed)
     * JPedal is used.
     *
     * @param data
     * @return PDFLibrary
     */
    public static PDFLibrary decideLibrary(byte[] data, final String isJPedalEnabled) {

        Integer permissions = null;

        boolean isForm = false;
        //Initially consider the pdf to be ok
        boolean assemblyAllowed = true;
        boolean openedWithFullPermissions = true;

        try {
            PdfReader reader = new PdfReader(data);
            int perms = (int) reader.getPermissions();

            PRAcroForm acroForm = reader.getAcroForm();
            isForm = (acroForm != null);

            if (perms != UNKNOWN_PERMISSIONS) {
                assemblyAllowed = isAssemblyAllowed(perms);
            }

            openedWithFullPermissions = reader.isOpenedWithFullPermissions();
            permissions = Integer.valueOf(perms);

        } catch (final Throwable e) {
        }

        //iText failed to read permissions
        boolean iTextPDfBoxFailed = (permissions == null);

        if (!iTextPDfBoxFailed) {
            if (isForm && assemblyAllowed) {
                return PDFLibrary.PDFBox;
            }

            //Assembly is allowed, when permissions are 0, or when explicitly defined so.
            if (assemblyAllowed && openedWithFullPermissions) {
                return PDFLibrary.iText;
            }
        }

        return fallbackUsingJPedalLibrary(data, isJPedalEnabled);
    }

    /**
     *
     * when JPedal says that it is encrypted, throw
     * FileManagePermissionException when JPedal says that it is not viewable,
     * throw FileNotViewableException
     *
     *
     */
    private static PDFLibrary fallbackUsingJPedalLibrary(final byte[] data, final String isJPedalEnabled) {

        if (Boolean.parseBoolean(isJPedalEnabled)) {

            //At this point, iText, PDFBox may have failed, or assembly may be disallowed
            //Use JPedal
            PdfDecoder decoder_pdf = null;
            try {
                decoder_pdf = new PdfDecoder(true);
                /**
                 * set mappings for non-embedded fonts to use
                 */
                FontMappings.setFontReplacements();

                decoder_pdf.openPdfArray(data);

            } catch (final Exception e) {
                //Failed to parse with JPedal
                throw ApiException.addInfo(new FileNotParsableException(), extraInfo);
            }

            boolean isFileViewable = decoder_pdf.isFileViewable();

            if (!isFileViewable) {
                boolean isEncrypted = decoder_pdf.isEncrypted();
                if (isEncrypted) {
                    throw ApiException.addInfo(new PDFLockedException(), extraInfo);
                } else {
                    throw ApiException.addInfo(new FileNotViewableException(), extraInfo);
                }
            }
            //Finally...
            return PDFLibrary.JPedal;
        }

        return PDFLibrary.PDFBox;
    }

    /**
     * Will create a thumb for the given pdf file, using the lbirary specified
     *
     * @param pdf
     * @param targetFullPath
     * @param library
     * @return
     */
    public static boolean createThumb(File pdf, String targetFullPath, PDFLibrary library, final String isJPedalEnabled) {
        switch (library) {
            case iText: {
            }
            case PDFBox: {
                return thumbWithPDFBox(pdf, targetFullPath, isJPedalEnabled);
            }
            default: {
                //JPedal
                return thumbWithJPedal(pdf, targetFullPath);
            }
        }
    }

    /**
     * Will generate an image of the 1st page of the given PDF with JPedal
     *
     * @param pdf
     * @param targetFullPath
     * @throws ThumbSavingException
     * @return
     */
    private static boolean thumbWithJPedal(File pdf, String targetFullPath) {
        PdfDecoder decode_pdf = new PdfDecoder(true);
        try {

            InputStream fileIn = new FileInputStream(pdf);

            decode_pdf.openPdfFileFromInputStream(fileIn, false);

            decode_pdf.setExtractionMode(0, 1f);

            //get the first page
            BufferedImage rendImage = decode_pdf.getPageAsImage(1);

            ImageIO.write(rendImage, "png", new File(targetFullPath));

            return true;
            //Attention, this may yield a RuntimeException
            //e.g. java.lang.RuntimeException: JPeg 2000 Images needs the VM parameter -Dorg.jpedal.jai=true switch turned on
        } catch (final Exception e) {
            throw ApiException.addInfo(new ThumbSavingException("Failed to create a thumb image with JPedal out of PDF's first page ", e),
                    extraInfo);
        } catch (final Error e) {
            throw ApiException.addInfo(new ThumbSavingException("Failed to create a thumb image with JPedal out of PDF's first page ", e),
                    extraInfo);
        }
    }

    /**
     * Will generate an image of the 1st page of the given PDF with PDFBox
     *
     * @param pdf
     * @param targetFullPath
     * @throws PDFAsImageException
     * @return
     */
    private static boolean thumbWithPDFBox(File pdf, String targetFullPath, final String isJPedalEnabled) {
        PDDocument document = null;
        try {
            document = PDDocument.load(pdf);

            // get 1st page of pdf document
            final PDFRenderer pdfRenderer = new PDFRenderer(document);
            final BufferedImage image = pdfRenderer.renderImageWithDPI(0, 96, RGB);
            ImageIO.write(image, "png", new File(targetFullPath));

            return true;
        } catch (final IOException e) {
            if (Boolean.parseBoolean(isJPedalEnabled) && e.getCause().toString().contains("javax.crypto.IllegalBlockSizeException")) {
                return thumbWithJPedal(pdf, targetFullPath);
            }
            throw ApiException.addInfo(new ThumbSavingException("Failed to create a thumb image with PDFBox out of PDF's first page ", e), extraInfo);
        } catch (final Exception e) {
            throw ApiException.addInfo(new ThumbSavingException("Failed to create a thumb image with PDFBox out of PDF's first page ", e), extraInfo);
        } catch (final Error e) {
            throw ApiException.addInfo(new ThumbSavingException("Failed to create a thumb image with PDFBox out of PDF's first page ", e), extraInfo);
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * Converts the byte[] of a pdf to a List of ByteMetadata, each one
     * containing the image bytes
     *
     * @param data
     * @param library
     * @param revertOrder boolean according to whether the pages needs to be
     * reversed or not
     * @return
     */
    public static List<ByteMetadata> toImage(byte[] data, PDFLibrary library, boolean revertOrder) {
        switch (library) {

            case iText: {
            }
            case PDFBox: {
                return toImageWithPDFBox(data, revertOrder);
            }
            default: {
                return toImageWithJPedal(data, revertOrder);
            }
        }
    }

    private static List<ByteMetadata> toImageWithPDFBox(byte[] data, boolean revertOrder) {

        PDDocument document = null;

        try (ByteArrayInputStream in = new ByteArrayInputStream(data);) {

            document = PDDocument.load(in);

            final List<PDPage> pages = new ArrayList<>();
            for (final PDPage page : document.getPages()) {
                pages.add(page);
            }

            @SuppressWarnings("unchecked")
            final List<PDPage> includedPages = revertOrder ? (List<PDPage>) new ListReverser<PDPage>(pages) : pages;
            final List<ByteMetadata> byteMetas = new ArrayList<>(pages.size());

            final PDFRenderer pdfRenderer = new PDFRenderer(document);
            int pageCounter = 0;
            for (final PDPage page : includedPages) {

                final BufferedImage rendImage = pdfRenderer.renderImageWithDPI(pageCounter++, 96, RGB);
                final ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(rendImage, "png", baos);

                baos.flush();

                final ByteMetadata byteMeta = new ByteMetadata();
                byteMeta.setData(baos.toByteArray());
                byteMeta.setWidth(rendImage.getWidth());
                byteMeta.setHeight(rendImage.getHeight());

                byteMetas.add(byteMeta);
            }

            return byteMetas;

        } catch (final Exception e) {
            throw ApiException.addInfo(new PDFAsImageException(), extraInfo);
        } catch (final Error e) {
            throw ApiException.addInfo(new PDFAsImageException(), extraInfo);
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private static List<ByteMetadata> toImageWithJPedal(byte[] data, boolean revertOrder) {
        try {
            /**
             * instance of PdfDecoder to convert PDF into image
             */
            PdfDecoder decode_pdf = new PdfDecoder(true);

            /**
             * set mappings for non-embedded fonts to use
             */
            FontMappings.setFontReplacements();

            /**
             * open the PDF file - can also be a URL or a byte array
             */
            decode_pdf.openPdfArray(data);

            decode_pdf.setExtractionMode(0, 1f); //do not save images

            int pages = decode_pdf.getPageCount();

            List<ByteMetadata> byteMetas = new ArrayList<>(pages);
            int start = revertOrder ? pages : 1;
            int end = revertOrder ? 1 : pages;
            int step = revertOrder ? -1 : 1;

            for (int i = start; i <= end; i = i + step) {
                BufferedImage rendImage = decode_pdf.getPageAsImage(i);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                ImageIO.write(rendImage, "png", baos);

                baos.flush();

                ByteMetadata byteMeta = new ByteMetadata();
                byteMeta.setData(baos.toByteArray());
                byteMeta.setWidth(rendImage.getWidth());
                byteMeta.setHeight(rendImage.getHeight());

                byteMetas.add(byteMeta);

            }
            /**
             * close the pdf file
             */
            decode_pdf.closePdfFile();

            return byteMetas;
        } catch (final Exception e) {
            throw ApiException.addInfo(new PDFAsImageException(), extraInfo);
        } catch (final Error e) {
            throw ApiException.addInfo(new PDFAsImageException(), extraInfo);
        }
    }

}
