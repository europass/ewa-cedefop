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
package europass.ewa.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javaxt.io.Image;
import java.io.IOException;
import org.apache.commons.imaging.Imaging;
import org.apache.commons.imaging.common.IImageMetadata;
import org.apache.commons.imaging.formats.jpeg.JpegImageMetadata;
import org.apache.commons.imaging.ImageReadException;
import org.apache.commons.imaging.formats.tiff.TiffField;
import org.apache.commons.imaging.formats.tiff.constants.TiffTagConstants;

/**
 *
 * @author at
 */
public class PhotoUtils {

    private static final Logger LOG = LoggerFactory.getLogger(PhotoUtils.class);

    public static byte[] fixOrientationIfNecessary(byte[] input) {
        try {
            final IImageMetadata metadata = Imaging.getMetadata(input);

            if (metadata == null || !(metadata instanceof JpegImageMetadata)) {
                return input;
            }

            JpegImageMetadata jpegMetadata = (JpegImageMetadata) metadata;

            final TiffField orientationField = jpegMetadata.findEXIFValueWithExactMatch(TiffTagConstants.TIFF_TAG_ORIENTATION);

            if (orientationField == null) {
                return input;
            }

            Object orientationValue = orientationField.getValue();

            if (orientationValue == null
                    || ((Number) orientationValue).shortValue() == 1
                    || ((Number) orientationValue).shortValue() == 0) {
                return input;
            }

            Image image = new Image(input);
            image.rotate();

            double fraction = input.length / (double) image.getByteArray().length;
            image.resize((int) (image.getWidth() * fraction), (int) (image.getHeight() * fraction));

            return image.getByteArray();

        } catch (ImageReadException | IOException ex) {
            LOG.error("Error while trying to rotate image: ", ex);
            return input;
        }
    }
}
