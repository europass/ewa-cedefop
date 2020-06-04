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

import com.google.common.base.Strings;

/**
 * Custom exception to be thrown when Europass fails to retrieve data from the
 * 3rd party service
 *
 * @author ekar
 *
 */
public class SocialServiceDataRetrievalException extends ApiException {

    private static final long serialVersionUID = 4739445091860759622L;

    private static final String MESSAGE = "Europass failed to retrieve data from social service.";

    private static final String CODE = "social.service.import.data.error";

    public SocialServiceDataRetrievalException(Throwable cause) {
        super(ErrorMessageBundle.get(CODE, MESSAGE), cause);
        defaults();
    }

    public SocialServiceDataRetrievalException(String msg) {
        super(Strings.isNullOrEmpty(msg) ? ErrorMessageBundle.get(CODE, MESSAGE) : msg);
        defaults();
    }

    private void defaults() {
        this.setCode(CODE);
        this.setStatus(Status.BAD_REQUEST);
    }
}
