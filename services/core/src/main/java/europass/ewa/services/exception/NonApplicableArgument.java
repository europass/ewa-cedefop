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

public class NonApplicableArgument extends ApiException {

    private static final long serialVersionUID = 3520517382571394172L;

    private static final String MSG_CODE = "error.code.bad.request";

    public NonApplicableArgument() {
        defaults();
    }

    public NonApplicableArgument(String message) {
        super(message);
        defaults();
    }

    public NonApplicableArgument(Throwable cause) {
        super(cause);
        defaults();
    }

    public NonApplicableArgument(String message, Throwable cause) {
        super(message, cause);
        defaults();
    }

    public NonApplicableArgument(String message, String code, Status status) {
        super(message, code, status);
    }

    private void defaults() {
        this.setCode(MSG_CODE);
        this.setStatus(Status.BAD_REQUEST);
    }
}
