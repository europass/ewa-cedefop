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

public class FileExceedsLimitException extends ApiException {

    private static final long serialVersionUID = -6194061824283010474L;

    private static final String MESSAGE_WITH_LIMIT = "The file exceeds the allowed limit of %d.";

    public FileExceedsLimitException() {
        super(String.format(ErrorMessageBundle.get(FileStatus.EXCEEDS_LIMIT.getDescription(), MESSAGE_WITH_LIMIT), 0));
        defaults();
    }

    public FileExceedsLimitException(int limit) {
        super(String.format(ErrorMessageBundle.get(FileStatus.EXCEEDS_LIMIT.getDescription(), MESSAGE_WITH_LIMIT), limit));
        defaults();
    }

    private void defaults() {
        this.setCode(FileStatus.EXCEEDS_LIMIT.getDescription());
        this.setStatus(Status.FORBIDDEN);
    }
}
