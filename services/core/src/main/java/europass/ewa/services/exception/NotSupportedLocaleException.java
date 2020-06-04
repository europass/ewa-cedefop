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

import java.util.List;
import java.util.Locale;

import javax.ws.rs.core.Response.Status;

import com.google.common.base.Joiner;

/**
 * Custom exception to be thrown when the given locale is not included within a
 * list of expected locale
 */
public class NotSupportedLocaleException extends ApiException {

    private static final long serialVersionUID = -6863464309807110926L;

    private static final String MESSAGE = "Locale %s is not supported by Europass.";

    private static final String CODE = "unsupported.locale";

    public NotSupportedLocaleException(List<Locale> requestedLocales) {
        super((String.format(ErrorMessageBundle.get(CODE, MESSAGE), Joiner.on(",").join(requestedLocales))));
        defaults();
    }

    private void defaults() {
        this.setCode(CODE);
        this.setStatus(Status.BAD_REQUEST);
    }
}
