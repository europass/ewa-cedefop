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
package europass.ewa.services.editor.files;

import java.io.Serializable;

import europass.ewa.enums.ConversionFileType;
import europass.ewa.model.FileData;
import europass.ewa.services.files.ImageType;
import europass.ewa.services.files.TwoDimensionalPoints;

public class FileBean implements Serializable {

    private static final long serialVersionUID = 1428385985646090669L;

    private String id;
    private String mimeType;
    private int size;
    /*
	 * the cropped property set for the current session file bean and controls that a specific file has already been cropped.
     */
    private boolean cropped = false;

    private String cropInfo = "";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public boolean isCropped() {
        return cropped;
    }

    public void setCropped(boolean cropped) {
        this.cropped = cropped;
    }

    /**
     * Whether the photo is already cropped or not. Takes into consideration the
     * given dimensions.
     *
     * @param TwoDimensionalPoints points
     * @return
     */
    public boolean isCroppedTo(TwoDimensionalPoints points) {

        return (isCropped() && (cropInfo != null && cropInfo.equals(points.asString())));

    }

    public String getCropInfo() {
        return cropInfo;
    }

    public void setCropInfo(String cropInfo) {
        this.cropInfo = cropInfo;
    }

    /**
     * Checks if the file is image
     *
     * @return
     */
    public boolean isImage() {
        boolean isImg = false;
        for (ImageType img : ImageType.values()) {
            isImg = (isImg || img.matchedMimeType(mimeType));
        }
        return isImg;
    }

    /**
     * Find the ImageType that matches the specific mimeType
     *
     * @return the ImageType or null
     */
    public ImageType getImageType() {
        for (ImageType img : ImageType.values()) {
            boolean matches = img.matchedMimeType(mimeType);
            if (matches) {
                return img;
            }
        }
        return null;
    }

    /**
     * Checks if the file is PDF
     *
     * @return
     */
    public boolean isPDF() {
        return mimeType.equals(ConversionFileType.PDF.getMimeType());
    }

    /**
     * Returns a FileData object out of this FileBean
     */
    public FileData toFileData() {
        FileData filedata = new FileData();
        filedata.setMimeType(mimeType);
        return filedata;
    }
}
