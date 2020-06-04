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

import europass.ewa.services.enums.UploadStatus;

/**
 * Custom exception to be thrown when xml is not well formed and cannot be read
 *
 */
public class XMLInvalidFromWordException extends XMLInvalidException {

    private static final long serialVersionUID = -6988497094151204996L;
    private static final String MESSAGE_TXT = "Xml is not valid because it was saved from Word.";
    private static final String MESSAGE = ErrorMessageBundle.get(UploadStatus.XML_INVALID_WORD.getDescription(), MESSAGE_TXT);

    public XMLInvalidFromWordException() {
        super(MESSAGE);
        defaults();
    }

    public XMLInvalidFromWordException(String message) {
        super(message);
        defaults();
    }

    public XMLInvalidFromWordException(Throwable cause) {
        super(cause);
        defaults();
    }

    private void defaults() {
        this.setCode(UploadStatus.XML_INVALID_WORD.getDescription());
        this.setStatus(Status.BAD_REQUEST);
    }
}
