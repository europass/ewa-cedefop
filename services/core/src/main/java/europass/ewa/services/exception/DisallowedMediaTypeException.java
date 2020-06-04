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

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response.Status;

import europass.ewa.enums.LogFields;

/**
 * Custom exception to be thrown when the given MediaType is not included within
 * a list of allowed MediaTypes
 *
 * @author ekar
 *
 * The difference of this Exception is that it that its status is 422 which is
 * NOT represented by Jersey <2.x Jax RS implementation. So we introduced a
 * statusCode to the ApiException, which although usually goes hand in hand with
 * the Status, in this case it does not. The Status object in this case is the
 * default.
 */
public class DisallowedMediaTypeException extends ApiException {

    private static final long serialVersionUID = -9083935640482254037L;

    private static final String MESSAGE = "The media type is not allowed.";

    private static final String CODE = "content.type.not.allowed";

    public DisallowedMediaTypeException() {
        super(String.format(ErrorMessageBundle.get(CODE, MESSAGE), "", "").replaceAll("\\s{2,}", " "));
        defaults();
    }

    public DisallowedMediaTypeException(MediaType mt) {
        this(mt, "");
    }

    public DisallowedMediaTypeException(MediaType mt, String extension) {
        super(String.format(ErrorMessageBundle.get(CODE, MESSAGE), mt, extension));
        this.addInfo(this, LogFields.FILETYPE, mt.getSubtype());
        defaults();
    }

    private void defaults() {
        this.setCode(CODE);
        this.setStatus(Status.UNSUPPORTED_MEDIA_TYPE);
        addInfo(this, LogFields.ACTION, "Content Detection");
    }
}
