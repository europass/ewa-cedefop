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
package europass.ewa.services.enums;

public enum SocialErrorStatus {
    //data retrieval error
    data_retrieval(400, "social-data-retrieval-error"),
    //europas app failed to get authenticated
    app_authentication_error(401, "social-app-authorization-error"),
    // The user denied your request
    access_denied(303, "social-user-denied-request"),
    token_expired(401, "social-token-expired"),
    invalid_token(401, "social-invalid-token"),
    throttle_limit(401, "social-throttle-limit"),
    page_not_found(404, "social-page-not-found"),
    // bad request
    bad_request(400, "social-bad-request"),
    invalid_scope(400, "social-invalid-scope"),
    // Other error
    server_error(500, "social-server-error"),
    parsing_error(501, "social-parsing-error"),
    // Used for logging
    illegal_character_uri(500, "Error in URI constructing");

    private int httpCode;
    private String errorKey;

    SocialErrorStatus(int httpCode, String errorKey) {

        this.httpCode = httpCode;
        this.errorKey = errorKey;
    }

    public static SocialErrorStatus match(Throwable e) {
        switch (e.getClass().getSimpleName()) {
            case "SocialServiceDataRetrievalException":
                return data_retrieval;
            case "SocialServiceAuthenticationException":
                return app_authentication_error;
            case "ParseException":
                return parsing_error;
            default:
                return server_error;
        }
    }

    public String getErrorKey() {
        return errorKey;
    }

    public int getHttpCode() {
        return httpCode;
    }

}
