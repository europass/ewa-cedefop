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

import javax.inject.Inject;

import com.google.inject.name.Named;
import europass.ewa.services.files.ImageProcessing;

public class WORDAttachmentVisitor extends ODTAttachmentVisitor {

    private static final int ATT_HEIGHT_WORD_REST_PAGES_PIXELS = 650;
    private static final int ATT_HEIGHT_WORD_FIRST_PAGE_PIXELS = 550;

    @Inject
    public WORDAttachmentVisitor(ImageProcessing imageProcessing,
            @Named("europass-ewa-services.pdf.library.jpedal.enabled") final String isJPedalEnabled) {
        super(imageProcessing, isJPedalEnabled);
    }

    @Override
    public int getAtachmentHeight(int page) {
        return page == 0 ? ATT_HEIGHT_WORD_FIRST_PAGE_PIXELS : ATT_HEIGHT_WORD_REST_PAGES_PIXELS;
    }
}
