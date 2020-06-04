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
package europass.ewa.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import europass.ewa.model.format.NumericUtils;

@JsonPropertyOrder({"personName", "contactInfo", "demographics", "photo"})
@JsonIgnoreProperties(ignoreUnknown = true)
public class Identification extends PrintableObject {

    public static final int PHOTO_WIDTH = 95;
    public static final int PHOTO_HEIGHT = 110;
    public static final int SIGNATURE_WIDTH = 300;
    public static final int SIGNATURE_HEIGHT = 100;

    private static final int RATIO_PRECISION = 2;
    private static final double PHOTO_WIDTH_CM = 2.513542;
    private static final double PHOTO_HEIGHT_CM = 2.910417;
    private static final double SIGNATURE_WIDTH_CM = 7.9375;
    private static final double SIGNATURE_HEIGHT_CM = 2.645833333;

    private PersonName personName;

    private ContactInfo contactInfo;

    private Demographics demographics;

    private FileData photo;

    private FileData signature;

    public Identification() {
    }

    @JsonProperty("PersonName")
    @JacksonXmlProperty(localName = "PersonName", namespace = Namespace.NAMESPACE)
    public PersonName getPersonName() {
        return withPreferences(personName, "PersonName");
    }

    public void setPersonName(PersonName personname) {
        this.personName = personname;
    }

    @JsonProperty("ContactInfo")
    @JacksonXmlProperty(localName = "ContactInfo", namespace = Namespace.NAMESPACE)
    public ContactInfo getContactInfo() {
        return withPreferences(contactInfo, "ContactInfo");
    }

    public void setContactInfo(ContactInfo contactinfo) {
        this.contactInfo = contactinfo;
    }

    @JsonProperty("Demographics")
    @JacksonXmlProperty(localName = "Demographics", namespace = Namespace.NAMESPACE)
    public Demographics getDemographics() {
        return withPreferences(demographics, "Demographics");
    }

    public void setDemographics(Demographics demographics) {
        this.demographics = demographics;
    }

    @JsonProperty("Photo")
    @JacksonXmlProperty(localName = "Photo", namespace = Namespace.NAMESPACE)
    public FileData getPhoto() {
        return photo;
    }

    public void setPhoto(FileData photo) {
        this.photo = photo;
    }

    @JsonProperty("Signature")
    @JacksonXmlProperty(localName = "Signature", namespace = Namespace.NAMESPACE)
    public FileData getSignature() {
        return signature;
    }

    public void setSignature(FileData signature) {
        this.signature = signature;
    }

    /**
     * *** UTILS ***
     */
    @JsonIgnore
    protected CodeLabel personGender() {
        if (demographics == null) {
            return null;
        }
        return demographics.getGender();
    }

    /**
     * ************ PHOTO ****************
     */
    @JsonIgnore
    private boolean fileDataEmpty(FileData data) {
        return (data == null
                || (data != null && data.getData() == null)
                || (data != null && data.getData() != null && data.getData().length == 0));
    }

    @JsonIgnore
    public boolean photoEmpty() {
        return fileDataEmpty(photo);
    }

    @JsonIgnore
    public boolean signatureEmpty() {
        return fileDataEmpty(signature);
    }

    /**
     * Method used to get dimensions of the Photo. If there is no photo, or no
     * specific dimensions, the default Europass dimensions in cm will be
     * returned.
     *
     * @return
     */
    @JsonIgnore
    private Dimensions fileDataDimensions(final FileData data, final boolean isSignature,
            final double WIDTH_CM, final double HEIGHT_CM,
            final int WIDTH, final int HEIGHT) {

        if (data == null || data.getData() == null) {
            return new Dimensions(WIDTH_CM, HEIGHT_CM);
        }

        final String dimensionsStr = data.getMetadata(Metadata.DIMENSION);
        if (dimensionsStr == null || dimensionsStr.isEmpty()) {
            return new Dimensions(WIDTH_CM, HEIGHT_CM);
        }

        final String[] dimensions = dimensionsStr.split("x");
        final int width = Integer.valueOf(dimensions[0]).intValue();
        final int height = Integer.valueOf(dimensions[1]).intValue();

        if (isSignature && (width < WIDTH && height < HEIGHT)) {
            return new Dimensions(Dimensions.pixelToCm(width), Dimensions.pixelToCm(height));
        }

        return calculateDimensions(HEIGHT, WIDTH, height, width);
    }

    private Dimensions calculateDimensions(final int HEIGHT, final int WIDTH, int height, int width) {

        if (height < HEIGHT && width < WIDTH) {

            if (WIDTH > HEIGHT) {
                height = Float.valueOf(WIDTH * ((float) height / (float) width)).intValue();
                width = WIDTH;
            } else {
                width = Float.valueOf(HEIGHT * ((float) width / (float) height)).intValue();
                height = HEIGHT;
            }
        } else {
            if (width > WIDTH) {
                width = WIDTH;
            }

            height = Float.valueOf(((float) HEIGHT / (float) WIDTH) * width).intValue();
        }

        return new Dimensions(Dimensions.pixelToCm(width), Dimensions.pixelToCm(height));
    }

    @JsonIgnore
    public Dimensions photoDimensions() {
        return fileDataDimensions(photo, false, PHOTO_WIDTH_CM, PHOTO_HEIGHT_CM, PHOTO_WIDTH, PHOTO_HEIGHT);
    }

    @JsonIgnore
    public Dimensions signatureDimensions() {
        return fileDataDimensions(signature, true, SIGNATURE_WIDTH_CM, SIGNATURE_HEIGHT_CM, SIGNATURE_WIDTH, SIGNATURE_HEIGHT);
    }

    @Override
    public void applyDefaultPreferences(List<PrintingPreference> newPrefs) {

        applyDefaultPreferences(getPersonName(), PersonName.class,
                "PersonName", newPrefs);

        applyDefaultPreferences(getContactInfo(), ContactInfo.class,
                "ContactInfo", newPrefs);

        applyDefaultPreferences(getDemographics(), Demographics.class,
                "Demographics", newPrefs);

        super.applyDefaultPreferences(newPrefs);

    }

    /**
     * ****** RATIO RELATED *************
     */
    @JsonIgnore
    public static int photoRatioInt() {
        return NumericUtils.asAugmentedInt((double) PHOTO_WIDTH / PHOTO_HEIGHT, RATIO_PRECISION);
    }

    @JsonIgnore
    public static int signatureRatioInt() {
        return NumericUtils.asAugmentedInt((double) SIGNATURE_WIDTH / SIGNATURE_HEIGHT, RATIO_PRECISION);
    }

    @JsonIgnore
    public static double photoRatio() {
        return ((double) PHOTO_WIDTH / PHOTO_HEIGHT);
    }

    @JsonIgnore
    public static double signatureRatio() {
        return ((double) SIGNATURE_WIDTH / SIGNATURE_HEIGHT);
    }

    @JsonIgnore
    public static boolean isRatioCompatible(int width, int height, FileData.IMAGE image) {
        switch (image) {
            case PHOTO:
                return isPhotoRatioCompatible(width, height);
            case SIGNATURE:
                return isSignatureRatioCompatible(width, height);
            default:
                return true;
        }
    }

    @JsonIgnore
    public static int getDefaultWidth(FileData.IMAGE image) {
        switch (image) {
            case SIGNATURE:
                return SIGNATURE_WIDTH;
            default:
                return PHOTO_WIDTH;
        }
    }

    @JsonIgnore
    public static int getDefaultHeight(FileData.IMAGE image) {
        switch (image) {
            case SIGNATURE:
                return SIGNATURE_HEIGHT;
            default:
                return PHOTO_HEIGHT;
        }
    }

    @JsonIgnore
    public static boolean isPhotoRatioCompatible(int width, int height) {
        int ratio = NumericUtils.asAugmentedInt(((double) width / height), RATIO_PRECISION);

        return (ratio == Identification.photoRatioInt());
    }

    @JsonIgnore
    public static boolean isSignatureRatioCompatible(int width, int height) {
        int ratio = NumericUtils.asAugmentedInt(((double) width / height), RATIO_PRECISION);

        return (ratio == Identification.signatureRatioInt());
    }

    @JsonIgnore
    private static int[] asCompatible(int[] dimensions, int WIDTH, int HEIGHT) {
        int srcW = dimensions[0];
        int srcH = dimensions[1];

        int w = srcW;
        int h = srcH;

//		if(w <= WIDTH && h <= HEIGHT ){
//			int[] rs = { w , h } ;
//			return rs;
//		}
        if (w >= h) {
            w = NumericUtils.asInt((double) (h * WIDTH) / HEIGHT);
        } // w < h
        else {
            h = NumericUtils.asInt((double) (w * HEIGHT) / WIDTH);
        }
        if (w > srcW) {
            w = srcW;
        }
        if (h > srcH) {
            h = srcH;
        }

        int[] rs = {w, h};

        return rs;
    }

    public static int[] asCompatiblePhoto(int[] dimensions) {
        return asCompatible(dimensions, PHOTO_WIDTH, PHOTO_HEIGHT);
    }

    public static int[] asCompatibleSignature(int[] dimensions) {
        return asCompatible(dimensions, SIGNATURE_WIDTH, SIGNATURE_HEIGHT);
    }

    /**
     * ****** UTILS for getting info from model *************
     */
    @JsonIgnore
    protected String personSurname() {
        if (personName == null) {
            return "";
        }
        String surName = personName.getSurname();
        return (surName == null ? "" : surName);
    }

    static class Dimensions {

        private double width;
        private double height;

        static final double PIXEL_MULTIPLIER = 0.02645833;
        static final int PIXEL_DIV = 1000000;

        Dimensions(double width, double height) {
            this.width = width;
            this.height = height;
        }

        public double getWidth() {
            return width;
        }

        public double getHeight() {
            return height;
        }

        /**
         * Returns pixels to cm with 6 digit precision
         *
         * @param pixel
         * @return
         */
        static double pixelToCm(int pixel) {
            return (double) Math.round((pixel * PIXEL_MULTIPLIER) * PIXEL_DIV) / PIXEL_DIV;
        }
    }

    @JsonIgnore
    public boolean PhotoContactInfoNonEmpty() {

        boolean photoContactInfoEmpty
                = (contactInfo == null || (contactInfo != null && contactInfo.checkEmpty()))
                && (demographics == null || (demographics != null && demographics.checkEmpty()))
                && (photo == null || (photo != null && photo.checkEmpty()));

        return !photoContactInfoEmpty;
    }

    @JsonIgnore
    public boolean PhotoAddressNonEmpty() {

        boolean photoAddressEmpty
                = (photo == null || (photo != null && photo.checkEmpty()))
                && (contactInfo == null
                || (contactInfo != null && contactInfo.checkEmpty())
                || (contactInfo != null && (contactInfo.getAddress() == null || (contactInfo.getAddress() != null && contactInfo.getAddress().checkEmpty()))));

        return !photoAddressEmpty;
    }

    @JsonIgnore
    @Override
    public boolean checkEmpty() {
        return ((personName == null || (personName != null && personName.checkEmpty()))
                && (contactInfo == null || (contactInfo != null && contactInfo.checkEmpty()))
                && (demographics == null || (demographics != null && demographics.checkEmpty()))
                && (photo == null || (photo != null && photo.checkEmpty()))
                && (signature == null || (signature != null && signature.checkEmpty())));
    }
}
