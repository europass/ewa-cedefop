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

public class FileAccessForbiddenException extends ApiException {

    private static final long serialVersionUID = -686045171217991690L;

    private static final String DEFAULT_MESSAGE_TXT = "Access to the file is forbidden.";
    private static final String DEFAULT_MESSAGE = ErrorMessageBundle.get(FileStatus.FORBIDDEN.getDescription(), DEFAULT_MESSAGE_TXT);

    public FileAccessForbiddenException() {
        super(DEFAULT_MESSAGE);
        defaults();
    }

    public FileAccessForbiddenException(String message, Throwable cause) {
        super(message + "\n" + DEFAULT_MESSAGE, cause);
        defaults();
    }

    public FileAccessForbiddenException(String message) {
        super(message + "\n" + DEFAULT_MESSAGE);
        defaults();
    }

    public FileAccessForbiddenException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
        defaults();
    }

    private void defaults() {
        this.setCode(FileStatus.FORBIDDEN.getDescription());
        this.setStatus(Status.FORBIDDEN);
    }
}
