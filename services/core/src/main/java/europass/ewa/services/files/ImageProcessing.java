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

import java.io.IOException;

import org.jpedal.exception.PdfException;

import europass.ewa.model.ByteMetadata;
import europass.ewa.model.FileData;
import europass.ewa.services.exception.NonApplicableArgument;
import europass.ewa.services.exception.PhotoCroppingException;
import europass.ewa.services.exception.PhotoReadingException;

public interface ImageProcessing {

    /**
     * Crop the image to based on the top left point [x,y] and based on the
     * Photo Dimensions.
     *
     * The byte[] data will be changed to the cropped data The dimensions
     * metadata will be set
     *
     * @param FileData
     * @param TwoDimensionalPoints
     */
    void crop(FileData photodata, TwoDimensionalPoints points) throws NonApplicableArgument, PhotoReadingException, PhotoCroppingException;

    /**
     * Resize the byte[] of the given photo FileData object to the Europass
     * Dimensions Will return the original data if the mimeType is not an image,
     * or if the dimensions are already below the Europass Dimensions, or if the
     * resizing fails.
     *
     * @param photodata
     * @throws PdfException
     * @throws IOException
     */
    void applyEuropassRatio(FileData photodata, FileData.IMAGE imageType) throws NonApplicableArgument, PhotoReadingException, PhotoCroppingException;

    void setDimensions(FileData photodata) throws NonApplicableArgument, PhotoReadingException;

    void simpleSetDimensions(FileData photodata) throws NonApplicableArgument, PhotoReadingException;

    ByteMetadata resize(ByteMetadata byteMetadata, int maxWidth, int maxHeight);
}
