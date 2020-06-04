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

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.http.HttpServletRequest;

import com.google.inject.name.Named;

import europass.ewa.enums.LogFields;
import europass.ewa.enums.ServerModules;
import europass.ewa.logging.ExtraLogInfo;
import europass.ewa.services.exception.ApiException;
import europass.ewa.services.exception.FileExceedsCummulativeLimitException;
import europass.ewa.services.exception.FileExceedsLimitException;
import europass.ewa.services.files.FileRepository.Types;
import europass.ewa.services.files.SizeLimitation;

public class SessionSizeLimitation implements SizeLimitation {

    private static final String CUMULATIVE_SIZE_PER_SESSION_ATTR = "uploads.cumulative.size.per.session";

    private final Provider<HttpServletRequest> httpRequest;

    private final int sessionUploadsLimit;
    private final int fileMaxSize;
    private final int photoMaxSize;
    private final int signatureMaxSize;

    @Inject
    public SessionSizeLimitation(
            Provider<HttpServletRequest> httpRequest,
            @Named(FileUploadsModule.FILE_ATTACHMENT_ALLOWED_SIZE) int fileMaxSize,
            @Named(FileUploadsModule.FILE_PHOTO_ALLOWED_SIZE) int photoMaxSize,
            @Named(FileUploadsModule.FILE_SIGNATURE_ALLOWED_SIZE) int signatureMaxSize,
            @Named(FileUploadsModule.FILE_ATTACHMENT_CUMULATIVE_SIZE) int sessionUploadsLimit) {

        this.httpRequest = httpRequest;

        this.fileMaxSize = fileMaxSize;
        this.photoMaxSize = photoMaxSize;
        this.signatureMaxSize = signatureMaxSize;
        this.sessionUploadsLimit = sessionUploadsLimit;
    }

    @Override
    public boolean isWithinLimits(int newSize, Types type, int cumulativeSize) throws FileExceedsLimitException, FileExceedsCummulativeLimitException {

        String fileType = null;
        if (type != null) {
            fileType = type.name();
        }

        // TODO: switch()
        int individualLimit
                = Types.PHOTO.equals(type) ? photoMaxSize : fileMaxSize;

        individualLimit
                = Types.SIGNATURE.equals(type) ? signatureMaxSize : individualLimit;

        // check specific size
        if (newSize > individualLimit) {
            throw ApiException.addInfo(new FileExceedsLimitException(individualLimit),
                    new ExtraLogInfo().add(LogFields.FILETYPE, fileType).add(LogFields.MODULE, ServerModules.SERVICES_EDITORS.getModule()));
        }

        // check cumulative size
        if (!isWithinSizeLimit(newSize, cumulativeSize)) {
            throw ApiException.addInfo(new FileExceedsCummulativeLimitException(sessionUploadsLimit),
                    new ExtraLogInfo().add(LogFields.FILETYPE, fileType).add(LogFields.MODULE, ServerModules.SERVICES_EDITORS.getModule()));
        }

        return true;
    }

    @Override
    public boolean isWithinSizeLimit(int newSize, int cumulativeSize) {
        //int cumulativeSize = this.getCurrentSize();		
        return ((newSize + cumulativeSize) <= sessionUploadsLimit);
    }

}
