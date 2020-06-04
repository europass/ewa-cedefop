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

public class FileNotFoundException extends ApiException {

    private static final long serialVersionUID = 6693507795066916891L;

    private static final String MESSAGE_TXT = "The file cannot be found in the repository.";
    private static final String MESSAGE = ErrorMessageBundle.get(FileStatus.NOT_FOUND.getDescription(), MESSAGE_TXT);

    public FileNotFoundException() {
        super(MESSAGE);
        defaults();
    }

    public FileNotFoundException(String message) {
        super(message + "\n" + MESSAGE);
        defaults();
    }

    public FileNotFoundException(Throwable cause) {
        super(MESSAGE, cause);
        defaults();
    }

    public FileNotFoundException(String message, Throwable cause) {
        super(message + "\n" + MESSAGE, cause);
        defaults();
    }

    private void defaults() {
        this.setCode(FileStatus.NOT_FOUND.getDescription());
        this.setStatus(Status.NOT_FOUND);
    }
}
