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

import europass.ewa.model.FileData;
import europass.ewa.model.wrapper.FiledataWrapper;
import europass.ewa.services.exception.ApiException;
import europass.ewa.services.exception.FileAccessForbiddenException;
import europass.ewa.services.exception.FileNotFoundException;
import europass.ewa.services.exception.NonApplicableArgument;
import europass.ewa.services.exception.PhotoCroppingException;
import europass.ewa.services.exception.ThumbSavingException;

public interface FileRepository {

    String THUMB_SUFFIX = "_thumb";

    String THUMB_EXT = THUMB_SUFFIX + ".png";

    String PDF_TYPE = "application/pdf";

    FileData readPhotoData(FileData photodata) throws FileNotFoundException, FileAccessForbiddenException, PhotoCroppingException,
            ThumbSavingException;

    FileData readFile(String fileId) throws NonApplicableArgument, FileAccessForbiddenException, FileNotFoundException;

    FileData readThumb(String fileId) throws NonApplicableArgument, FileAccessForbiddenException, FileNotFoundException;

    boolean isOnRepository(String fileId);

    /**
     * Returns true if the file is found both in scope and on repository
     *
     * @param fileId
     * @return boolean
     * @throws FileNotFoundException when file not found on repository
     */
    boolean isFileOnRepository(String fileId) throws FileAccessForbiddenException, FileNotFoundException;

    boolean delete(String fileId) throws NonApplicableArgument;

    FiledataWrapper save(byte[] data, String mimeType, Types type, String uriPrefix, String id) throws ApiException;

    ImageProcessing imageProcessingBehavior();

    SizeLimitation sizeLimitationBehavior();

    enum Types {
        XML, ATTACHMENT, PHOTO, SIGNATURE;
    }

}
