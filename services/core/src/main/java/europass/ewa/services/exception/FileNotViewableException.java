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

public class FileNotViewableException extends ApiException {

    private static final long serialVersionUID = -1828931383870897274L;

    private static final String DEFAULT_MESSAGE = "The file cannot be viewed";

    private static String MESSAGE = ErrorMessageBundle.get(FileStatus.NOT_VIEWABLE_FILE.getDescription(), DEFAULT_MESSAGE);

    public FileNotViewableException() {
        super(MESSAGE);
        defaults();
    }

    public FileNotViewableException(String message) {
        super(message);
        defaults();
    }

    public FileNotViewableException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileNotViewableException(Throwable cause) {
        super(MESSAGE, cause);
    }

    private void defaults() {
        this.setCode(FileStatus.NOT_VIEWABLE_FILE.getDescription());
        this.setStatus(Status.BAD_REQUEST);
    }
}
