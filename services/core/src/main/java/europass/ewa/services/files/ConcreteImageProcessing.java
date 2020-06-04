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

import java.awt.color.CMMException;
import java.awt.image.BufferedImage;
import java.awt.image.ImagingOpException;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.media.jai.JAI;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import europass.ewa.enums.LogFields;
import europass.ewa.enums.ServerModules;
import europass.ewa.logging.ExtraLogInfo;
import europass.ewa.model.ByteMetadata;
import europass.ewa.model.FileData;
import europass.ewa.model.Identification;
import europass.ewa.model.Metadata;
import europass.ewa.services.exception.ApiException;
import europass.ewa.services.exception.NonApplicableArgument;
import europass.ewa.services.exception.PhotoCroppingException;
import europass.ewa.services.exception.PhotoReadingException;
import europass.ewa.services.files.image.SimpleImageInfo;

public class ConcreteImageProcessing implements ImageProcessing {

    private static final Logger LOG = LoggerFactory.getLogger(ConcreteImageProcessing.class);

    private static final String module = ServerModules.SERVICES_CORE.getModule();

    /**
     * Modifies the given FileData with the Dimensions read by the first
     * signature bytes
     *
     * @throws NonApplicableArgument when bytes are null or empty, or when the
     * file is not an image
     * @throws PhotoReadingException when failing to read the bytes
     */
    @Override
    public void simpleSetDimensions(FileData photodata) throws NonApplicableArgument, PhotoReadingException {
        try {
            byte[] bytes = photodata.getData();

            if (bytes == null || bytes.length == 0) {
                throw ApiException.addInfo(new NonApplicableArgument("Cannot set dimensions of null or empty byte array."),
                        new ExtraLogInfo().add(LogFields.FILETYPE, photodata.getMimeType()).add(LogFields.MODULE, module));
            }
            // Get dimensions using a utility that reads dimension based onbytes
            SimpleImageInfo info = new SimpleImageInfo(bytes);

            // store dimensions as metadata
            if (info != null && info.hasDimension()) {
                String dimensions = info.dimensionToString();
                photodata.setInfo(Metadata.DIMENSION, dimensions);
            }
        } catch (Exception e) {
            //LOG.error("Failed to calculate dimension of image.");
            throw new PhotoReadingException("Failed to calculate dimension of image.", e);
        }
    }

    /**
     * Modifies the given FileData with the Dimensions read by the byte array
     *
     * @throws NonApplicableArgument when bytes are null or empty, or when the
     * file is not an image
     * @throws PhotoReadingException when failing to read the bytes as Buffered
     * Image
     */
    @Override
    public void setDimensions(FileData photodata) throws NonApplicableArgument, PhotoReadingException {
        byte[] bytes = photodata.getData();

        if (bytes == null || bytes.length == 0) {
            throw ApiException.addInfo(new NonApplicableArgument("Cannot set dimensions of null or empty byte array."),
                    new ExtraLogInfo().add(LogFields.FILETYPE, photodata.getMimeType()).add(LogFields.MODULE, module));
        }
        // get the mime type format
        ImageType imgType = ImageType.getImageType(photodata.getMimeType());
        if (ImageType.UNKNOWN.equals(imgType)) {
            throw ApiException.addInfo(new NonApplicableArgument("FileData is not an image"),
                    new ExtraLogInfo().add(LogFields.FILETYPE, photodata.getMimeType()).add(LogFields.MODULE, module));
        }

        BufferedImage srcImg = null;
        try {
            srcImg = asBufferedImage(bytes);

            String dimensions = srcImg.getWidth() + "x" + srcImg.getHeight();

            photodata.setInfo(Metadata.DIMENSION, dimensions);

        } finally {
            if (srcImg != null) {
                srcImg.flush();
            }
        }
    }

    @Override
    public void applyEuropassRatio(FileData photodata, FileData.IMAGE imageType) throws NonApplicableArgument, PhotoReadingException, PhotoCroppingException {
        LOG.debug("inside applyEuropassRatio");
        byte[] bytes = photodata.getData();

        if (bytes == null || bytes.length == 0) {
            throw ApiException.addInfo(new NonApplicableArgument("Cannot set dimensions of null or empty byte array."),
                    new ExtraLogInfo().add(LogFields.FILETYPE, photodata.getMimeType()).add(LogFields.MODULE, module));
        }

        // get the mime type format
        ImageType imgType = ImageType.getImageType(photodata.getMimeType());
        if (ImageType.UNKNOWN.equals(imgType)) {
            throw ApiException.addInfo(new NonApplicableArgument("FileData is not an image"),
                    new ExtraLogInfo().add(LogFields.FILETYPE, photodata.getMimeType()).add(LogFields.MODULE, module));
        }

        BufferedImage srcImg = null;
        try {
            srcImg = asBufferedImage(bytes);
            if (srcImg == null) {
                LOG.debug("srcImg is null");
                return;
            }

            int srcWidth = srcImg.getWidth();
            int srcHeight = srcImg.getHeight();
            if (srcWidth <= Identification.getDefaultWidth(imageType)
                    && srcHeight <= Identification.getDefaultHeight(imageType)) {
                LOG.debug("photo src width and src height are less than Identification defaults");
                /**
                 * pgia: EWA-1763 set dimensions for older versioned CVs taken
                 * from image bytes
                 */

                String dimensionMetadata = photodata.getMetadata(Metadata.DIMENSION);
                if (!Strings.isNullOrEmpty(dimensionMetadata)) {
                    LOG.debug("dimenstion metadata is not null or empty");
                    String dimensions[] = dimensionMetadata.split("x");

                    if (dimensions.length == 2) {
                        int width = Integer.valueOf(dimensions[0]);
                        int height = Integer.valueOf(dimensions[1]);

                        if (width != srcWidth || height != srcHeight) {
                            photodata.setInfo(Metadata.DIMENSION, srcWidth + "x" + srcHeight);
                        }
                    }
                }

                // no need to proceed
                return;
            }

            if (Identification.isRatioCompatible(srcWidth, srcHeight, imageType)) {
                // no need to proceed
                LOG.debug("Identification is RatioCompatible with photo");
                return;
            }

            //photodata.setData( asByteArray( srcImg, imgType.getDescription() ) ); 
            photodata.setInfo(Metadata.DIMENSION, srcWidth + "x" + srcHeight);
            LOG.debug("set data and info for photodata object");
        } catch (Exception e) {
            LOG.debug("Failed to crop the bytes in order to comply to europass ratio.", e);
            throw ApiException.addInfo(new PhotoCroppingException(e),
                    new ExtraLogInfo().add(LogFields.FILETYPE, photodata.getMimeType()).add(LogFields.MODULE, module));
        } finally {
            if (srcImg != null) {
                LOG.debug("src img is not null, ready to flush");
                srcImg.flush();
            }
        }
    }

    @Override
    public ByteMetadata resize(ByteMetadata byteMeta, int maxWidth, int maxHeight) {

        ByteMetadata resized = new ByteMetadata(byteMeta);

        float srcWidth = byteMeta.getWidth();
        float srcHeight = byteMeta.getHeight();

        //Both initial width and height are within the boundaries
        if (srcWidth <= maxWidth && srcHeight <= maxHeight) {
            return resized;
        } else {
            float imageAspect = ((float) srcWidth) / ((float) srcHeight);
            float destAspect = ((float) maxWidth) / ((float) maxHeight);

            if (imageAspect > destAspect) {
                float resizeHeight = maxWidth / imageAspect;
                resized.setWidth(maxWidth);
                resized.setHeight(resizeHeight);
            } else {
                float resizeWidth = maxHeight * imageAspect;
                resized.setWidth(resizeWidth);
                resized.setHeight(maxHeight);
            }
        }
        return resized;
    }

    /**
     * Resize the bytes to the given dimensions. Modifies the given FileData
     * with the resized data and the dimension info.
     *
     * Will return false when the dimension is less than Europass dimensions
     *
     * @throws NonApplicableArgument when bytes are null or empty, or when the
     * file is not an image
     * @throws PhotoCroppingException in case of any IO error
     *
     * @param FileData, photodata
     * @param points , object holding x, top left width, y, top left height, x2,
     * bottom right width, y2, bottom right height
     */
    @Override
    public void crop(final FileData photodata, final TwoDimensionalPoints points) throws NonApplicableArgument, PhotoReadingException, PhotoCroppingException {

        byte[] bytes = photodata.getData();

        ImageType imgType = ImageType.getImageType(photodata.getMimeType());
        if (ImageType.UNKNOWN.equals(imgType)) {
            throw ApiException.addInfo(new NonApplicableArgument("FileData is not an image"),
                    new ExtraLogInfo().add(LogFields.FILETYPE, photodata.getMimeType()).add(LogFields.MODULE, module));
        }

        Metadata croppingObj = photodata.getMetadataObj(Metadata.CROPPING);
        JSONObject cropping = new JSONObject(croppingObj.getValue());

        int x = cropping.getInt(Metadata.CROPPING_X);
        int y = cropping.getInt(Metadata.CROPPING_Y);
        int x2 = 0;
        int y2 = 0;

        double proportionX = points.getX1() == 0 ? 0 : (double) x / (double) points.getX1();
        double proportionY = points.getY1() == 0 ? 0 : (double) y / (double) points.getY1();

        if (proportionX == 0 && proportionY == 0 && points.getX1() != 0 && points.getY1() != 0) {
            photodata.getMetadata().remove(cropping);
            return;
        }

        int spaceAfterX2 = cropping.getInt(Metadata.CROPPING_WIDTH) - points.getX2();
        int spaceAfterY2 = cropping.getInt(Metadata.CROPPING_HEIGHT) - points.getY2();

        if (proportionX == 0 && points.getX2() != 0) {

            double factorX = (double) cropping.getInt(Metadata.CROPPING_X2) / (double) cropping.getInt(Metadata.CROPPING_WIDTH);
            int computedSpaceX = (int) (spaceAfterX2 * factorX);

            x2 = cropping.getInt(Metadata.CROPPING_X2) - computedSpaceX;
        } else {
            x2 = cropping.getInt(Metadata.CROPPING_X2) - (int) (proportionX * spaceAfterX2);
        }

        if (proportionY == 0 && points.getY2() != 0) {

            double factorY = (double) cropping.getInt(Metadata.CROPPING_Y2) / (double) cropping.getInt(Metadata.CROPPING_HEIGHT);
            int computedSpaceY = (int) (spaceAfterY2 * factorY);

            y2 = cropping.getInt(Metadata.CROPPING_Y2) - computedSpaceY;
        } else {
            y2 = cropping.getInt(Metadata.CROPPING_Y2) - (int) (proportionY * spaceAfterY2);
        }

        int startX = (x < 0) ? 0 : x;
        int startY = (y < 0) ? 0 : y;

        int endX = (x < 0) ? (x2 + (0 - x)) : x2;
        int endY = (y < 0) ? (y2 + (0 - y)) : y2;

        BufferedImage srcImg = null;

        try {
            srcImg = asBufferedImage(bytes);

            int imageWidth = srcImg.getWidth();
            int imageHeight = srcImg.getHeight();

            int cropWidth = endX - startX;
            int cropHeight = endY - startY;

            photodata.setData(asByteArray(srcImg, imgType.getDescription()));
            photodata.setInfo(Metadata.DIMENSION, imageWidth + "x" + imageHeight);

            //Cropping attributes
            String croppingValues = "{ "
                    + "\"" + Metadata.CROPPING_WIDTH + "\":" + cropWidth + ", "
                    + "\"" + Metadata.CROPPING_HEIGHT + "\":" + cropHeight + ", "
                    + "\"" + Metadata.CROPPING_START_X + "\":" + startX + ", "
                    + "\"" + Metadata.CROPPING_START_Y + "\":" + startY + ", "
                    + "\"" + Metadata.CROPPING_START_X2 + "\":" + startX + ", "
                    + "\"" + Metadata.CROPPING_START_Y2 + "\":" + endY + ", "
                    + "\"" + Metadata.CROPPING_X + "\":" + endX + ", "
                    + "\"" + Metadata.CROPPING_Y + "\":" + startY + ", "
                    + "\"" + Metadata.CROPPING_X2 + "\":" + endX + ", "
                    + "\"" + Metadata.CROPPING_Y2 + "\":" + endY
                    + "}";

            // Used for the proper cropping on the export procedure so the image is shown properly - will be removed to conform to the xml schema
            Metadata croppingMeta = new Metadata(Metadata.CROPPING_EXPORT, croppingValues);
            photodata.getMetadata().add(croppingMeta);

        } catch (IllegalArgumentException | ImagingOpException | IOException e) {
            LOG.error("Failed to crop image ", e);
            throw ApiException.addInfo(new PhotoCroppingException(e),
                    new ExtraLogInfo().add(LogFields.FILETYPE, photodata.getMimeType()).add(LogFields.ACTION, "Photo Crop").add(LogFields.MODULE, module));

        } finally {

//			 * NOTE: This class does not call Image.flush() on any of
//			 * the source images passed in by calling code; it is up to
//			 * the original caller to dispose of their source images
//			 * when they are no longer needed so the VM can most
//			 * efficiently GC them. Ï
            if (srcImg != null) {
                srcImg.flush();
            }
        }

    }

    // ---- UTILITIES
    /**
     * Read bytes as BufferedImage If ImageIO fails, use JAI
     *
     * @param bytes
     * @return
     */
    private static BufferedImage asBufferedImage(byte[] bytes) throws PhotoReadingException {
        BufferedImage srcImg = null;

        try {
            // We try it with ImageIO
            srcImg = ImageIO.read(new BufferedInputStream(new ByteArrayInputStream(bytes)));

        } catch (CMMException ex) {
            // If we failed...use JAI
            srcImg = JAI.create("stream", new BufferedInputStream(new ByteArrayInputStream(bytes))).getAsBufferedImage();
        } catch (Exception e) {
            LOG.error("Failed to read bytes as image", e);
            throw ApiException.addInfo(new PhotoReadingException(e), LogFields.MODULE, module);
        }

        return srcImg;
    }

    /**
     * Reads a Buffered Image as array of byte
     *
     * @param img
     * @param imgType
     * @return
     * @throws IOException
     */
    private static byte[] asByteArray(BufferedImage img, String imgType) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, imgType, baos);
        baos.flush();
        byte[] data = baos.toByteArray();
        baos.close();

        return data;
    }

}
