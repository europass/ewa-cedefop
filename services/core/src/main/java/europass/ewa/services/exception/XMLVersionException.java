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
 * Custom exception to be thrown when a problem occurs while trying to read the
 * xsd version from xml
 *
 * @author ekar
 *
 */
public class XMLVersionException extends ApiException {

    private static final long serialVersionUID = 3667996718886147828L;

    private static final String MESSAGE_TXT = "Failed to retrieve the XSD version from XML.";
    private static final String MESSAGE = ErrorMessageBundle.get(UploadStatus.XML_UNDEFINED_VERSION.getDescription(), MESSAGE_TXT);

    public XMLVersionException() {
        super(MESSAGE);
        defaults();
    }

    public XMLVersionException(String message) {
        super(message);
        defaults();
    }

    public XMLVersionException(Throwable cause) {
        super(MESSAGE, cause);
        defaults();
    }

    private void defaults() {
        this.setCode(UploadStatus.XML_UNDEFINED_VERSION.getDescription());
        this.setStatus(Status.BAD_REQUEST);
    }
}
