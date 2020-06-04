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

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Strings;

public enum ImageType {

    PNG("png", ".png", "image/png,image/x-png"),
    JPG("jpg", ".jpg", "image/jpeg,image/pjpeg"),
    UNKNOWN("", "", "");

    private String description;

    private String extension;

    private List<String> mimeTypes;

    ImageType(String description, String extension, String mimeType) {
        this.description = description;
        this.extension = extension;
        this.mimeTypes = Arrays.asList(mimeType.split(","));
    }

    public String getDescription() {
        return description;
    }

    public List<String> getMimeTypes() {
        return mimeTypes;
    }

    public String getBasicMimeType() {
        return mimeTypes.get(0);
    }

    public String getExtension() {
        return extension;
    }

    public boolean matchedMimeType(String mime) {
        return mimeTypes.contains(mime);
    }

    public static boolean isImage(String mimeType) {
        boolean isImg = false;
        for (ImageType img : values()) {
            isImg = (isImg || img.matchedMimeType(mimeType));
        }
        return isImg;
    }

    public static ImageType getImageType(String mimeType) {
        if (Strings.isNullOrEmpty(mimeType)) {
            return UNKNOWN;
        }
        for (ImageType img : values()) {
            boolean matches = img.matchedMimeType(mimeType);
            if (matches) {
                return img;
            }
        }
        return UNKNOWN;
    }
}
