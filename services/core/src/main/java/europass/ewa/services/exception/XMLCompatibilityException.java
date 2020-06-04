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
 * Custom exception to be thrown when the transformation to a new xsl fails
 *
 * @author ekar
 *
 */
public class XMLCompatibilityException extends ApiException {

    private static final long serialVersionUID = 8809286950442708406L;

    private static final String MESSAGE_TXT = "Failed to apply transformation to XML.";
    private static final String MESSAGE = ErrorMessageBundle.get(UploadStatus.XML_TRANSFORMATION_FAILURE.getDescription(), MESSAGE_TXT);

    public XMLCompatibilityException() {
        super(MESSAGE);
        defaults();
    }

    public XMLCompatibilityException(String message) {
        super(message + "\n" + MESSAGE);
        defaults();
    }

    public XMLCompatibilityException(Throwable cause) {
        super(MESSAGE, cause);
        defaults();
    }

    private void defaults() {
        this.setCode(UploadStatus.XML_TRANSFORMATION_FAILURE.getDescription());
        this.setStatus(Status.INTERNAL_SERVER_ERROR);
    }
}
