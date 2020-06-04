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

import europass.ewa.enums.LogFields;

/**
 * Custom exception to be thrown when the given MediaType is not defined (
 * usually null or empty string)
 *
 * @author ekar
 *
 */
public class UndefinedMediaTypeException extends ApiException {

    private static final long serialVersionUID = -878634426096506621L;

    private static final String MESSAGE = "The media type is not defined for this file.";

    private static final String CODE = "content.type.not.defined";

    public UndefinedMediaTypeException() {
        super(ErrorMessageBundle.get(CODE, MESSAGE));
        defaults();
    }

    public UndefinedMediaTypeException(String message) {
        super(message + "\n" + ErrorMessageBundle.get(CODE, MESSAGE));
        defaults();
    }

    private void defaults() {
        this.setCode(CODE);
        this.setStatus(Status.BAD_REQUEST);
        addInfo(this, LogFields.ACTION, "Content Detection");
    }
}
