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

import com.google.inject.name.Named;
import europass.ewa.enums.PDFLibrary;
import europass.ewa.model.Attachment;
import europass.ewa.model.AttachmentVisitor;
import europass.ewa.services.files.PDFUtils;

import javax.inject.Inject;

public class PDFAttachmentVisitor implements AttachmentVisitor {

    private static final int ATT_HEIGHT_PDF_REST_PAGES_PIXELS = 770;
    private static final int ATT_HEIGHT_PDF_FIRST_PAGE_PIXELS = 600;

    private final String isJPedalEnabled;

    @Inject
    public PDFAttachmentVisitor(@Named("europass-ewa-services.pdf.library.jpedal.enabled") String isJPedalEnabled) {
        this.isJPedalEnabled = isJPedalEnabled;
    }

    @Override
    public void visit(Attachment a) {
        byte[] data = a.getData();

        if (data == null) {
            return;
        }

        try {
            if (!a.isPDF()) {
                return;
            }

            handlePDF(a);
            return;

        } catch (final Exception e) {
            //problem with processing the file...
            a.setParseable(false);
            return;
        }
    }

    /**
     * Decide on the PDFLibrary, if not already decided
     *
     * @param a
     */
    private void handlePDF(Attachment a) {
        PDFLibrary library = a.getPdfLibrary();
        if (library == null) {
            library = PDFUtils.decideLibrary(a.getData(), isJPedalEnabled);
            a.setPdfLibrary(library);
        }
    }

    @Override
    public int getAtachmentHeight(int page) {
        return page == 0 ? ATT_HEIGHT_PDF_FIRST_PAGE_PIXELS : ATT_HEIGHT_PDF_REST_PAGES_PIXELS;
    }

}
