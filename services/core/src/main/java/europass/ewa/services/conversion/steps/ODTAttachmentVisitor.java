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

import com.google.inject.name.Named;
import europass.ewa.database.guice.AbstractModelModule;
import europass.ewa.enums.PDFLibrary;
import europass.ewa.model.Attachment;
import europass.ewa.model.AttachmentVisitor;
import europass.ewa.model.ByteMetadata;
import europass.ewa.model.Metadata;
import europass.ewa.services.files.ImageProcessing;
import europass.ewa.services.files.PDFUtils;

public class ODTAttachmentVisitor implements AttachmentVisitor {

    private static final int ATT_HEIGHT_ODT_REST_PAGES_PIXELS = 770;
    private static final int ATT_HEIGHT_ODT_FIRST_PAGE_PIXELS = 600;

    private final ImageProcessing imageProcessing;
    private final String isJPedalEnabled;

    @Inject
    public ODTAttachmentVisitor(ImageProcessing imageProcessing, @Named("europass-ewa-services.pdf.library.jpedal.enabled") String isJPedalEnabled) {
        this.imageProcessing = imageProcessing;
        this.isJPedalEnabled = isJPedalEnabled;
    }

    @Override
    public void visit(Attachment a) {
        byte[] data = a.getData();

        if (data == null) {
            return;
        }

        try {
            if (a.isImage()) {
                handleImage(a);
                return;
            }
            if (!a.isPDF()) {
                return;
            }

            //PDF...
            handlePDF(a);
            return;

        } catch (final Exception e) {
            //problem with processing the file...
            a.setParseable(false);
            return;
        }

    }

    /**
     * 1. Decide on the PDFLibrary 2. Convert all pages to images according to
     * the preferred library 3. Resize the converted images if necessary
     *
     * @param a
     */
    private void handlePDF(Attachment a) {
        byte[] data = a.getData();

        PDFLibrary library = a.getPdfLibrary();
        if (library == null) {
            library = PDFUtils.decideLibrary(data, isJPedalEnabled);
            a.setPdfLibrary(library);
        }
        //do not use reverted order!
        List<ByteMetadata> byteMetas = PDFUtils.toImage(data, library, false);

        int pages = byteMetas.size();

        List<ByteMetadata> resized = new ArrayList<>(pages);

        //Resize for ODT/WORD
        for (int page = 0; page < pages; page++) {
            ByteMetadata byteMeta = byteMetas.get(page);
            resized.add(resize(byteMeta, page));
        }

        a.setByteMetadataList(resized);
    }

    private ByteMetadata resize(ByteMetadata byteMeta, int page) {
        return this.imageProcessing.resize(byteMeta, Attachment.ATT_WIDTH_PIXELS, getAtachmentHeight(page));
    }

    /**
     * 1. Resize the image for ODT/WORD if necessary 2. Set the ByteMetadata
     * info 3. Set the Dimension Metadata if not already set
     *
     * @param a
     */
    private void handleImage(Attachment a) {

        //Prepare byteMeta
        ByteMetadata byteMeta = new ByteMetadata();
        byteMeta.setData(a.getData());
        //set Dimensions if not already set.
        Metadata dimension = a.getMetadataObj(Metadata.DIMENSION);
        if (dimension == null) {
            this.imageProcessing.simpleSetDimensions(a);
        }
        int[] xy = a.getDimensions();
        byteMeta.setWidth(xy[0]);
        byteMeta.setHeight(xy[1]);

        //Resizing only calculates the new x and y. Does not affect the bytes
        ByteMetadata resized = resize(byteMeta, 0);

        List<ByteMetadata> byteMetas = new ArrayList<>(1);
        byteMetas.add(resized);
        a.setByteMetadataList(byteMetas);

    }

    @Override
    public int getAtachmentHeight(int page) {
        return page == 0 ? ATT_HEIGHT_ODT_FIRST_PAGE_PIXELS : ATT_HEIGHT_ODT_REST_PAGES_PIXELS;
    }

}
