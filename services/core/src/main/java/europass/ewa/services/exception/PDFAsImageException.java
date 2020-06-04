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

public class PDFAsImageException extends ApiException {

    private static final long serialVersionUID = -1828931383870897274L;

    private static final String DEFAULT_MESSAGE = "The PDF file cannot be converted to image";

    private static String MESSAGE = ErrorMessageBundle.get(FileStatus.PDF_AS_IMAGE.getDescription(), DEFAULT_MESSAGE);

    public PDFAsImageException() {
        super(MESSAGE);
        defaults();
    }

    public PDFAsImageException(String message) {
        super(message);
        defaults();
    }

    public PDFAsImageException(String message, Throwable cause) {
        super(message, cause);
    }

    public PDFAsImageException(Throwable cause) {
        super(MESSAGE, cause);
    }

    private void defaults() {
        this.setCode(FileStatus.PDF_AS_IMAGE.getDescription());
        this.setStatus(Status.INTERNAL_SERVER_ERROR);
    }
}
