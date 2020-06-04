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
package europass.ewa.services.exception;

import javax.ws.rs.core.Response.Status;

import europass.ewa.services.enums.FileStatus;

/**
 * Custom exception to be thrown when a specific input is undefined (null or the
 * empty string)
 */
public class ThumbSavingException extends ApiException {

    private static final long serialVersionUID = 2257621997609732692L;

    private static final String MESSAGE_TXT = "Failed to store thumbnail for pdf attachment.";
    private static final String MESSAGE = ErrorMessageBundle.get(FileStatus.PDF_THUMB.getDescription(), MESSAGE_TXT);

    public ThumbSavingException() {
        super(MESSAGE);
        defaults();
    }

    public ThumbSavingException(String message) {
        super(message + "\n" + MESSAGE);
        defaults();
    }

    public ThumbSavingException(String message, Throwable cause) {
        super(message + "\n" + MESSAGE, cause);
        defaults();
    }

    public ThumbSavingException(Throwable cause) {
        super(MESSAGE, cause);
    }

    private void defaults() {
        this.setCode(FileStatus.PDF_THUMB.getDescription());
        this.setStatus(Status.INTERNAL_SERVER_ERROR);
    }
}
