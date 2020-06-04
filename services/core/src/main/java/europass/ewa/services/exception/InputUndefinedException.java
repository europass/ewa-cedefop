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

import europass.ewa.services.enums.DownloadStatus;

/**
 * Custom exception to be thrown when a specific input is undefined (null or the
 * empty string)
 */
public class InputUndefinedException extends ApiException {

    private static final long serialVersionUID = 2257621997609732692L;

    private static final String MESSAGE = "The input is undefined or empty.";

    public InputUndefinedException() {
        super(ErrorMessageBundle.get(DownloadStatus.INPUT_EMPTY.getDescription(), MESSAGE));
        defaults();
    }

    public InputUndefinedException(String message) {
        super(message + "\n" + ErrorMessageBundle.get(DownloadStatus.INPUT_EMPTY.getDescription(), MESSAGE));
        defaults();
    }

    private void defaults() {
        this.setCode(DownloadStatus.INPUT_EMPTY.getDescription());
        this.setStatus(Status.BAD_REQUEST);
    }
}
