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

import java.io.CharConversionException;

import javax.ws.rs.WebApplicationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ctc.wstx.exc.WstxException;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import europass.ewa.services.enums.HttpErrorCodeLabels;

@JsonPropertyOrder({
    "trace", "code", "message"
})
@JsonRootName("Error")
public class ErrorMessage {

    private static final Logger LOG = LoggerFactory.getLogger(ErrorMessage.class);

    private static final ObjectMapper MAPPER = new ObjectMapper().configure(SerializationFeature.WRAP_ROOT_VALUE, true);

    private static final String DEFAULT_CODE = "internal.server.error";

    private static final String DEFAULT_TRACE = "ErrCode:XXX";

    public static final String DEFAULT_ERROR_JSON
            = "{ \"Error\" : { \"trace\": \"" + DEFAULT_TRACE + "\", \"code\" : \"" + DEFAULT_CODE + "\", \"message\" : \"Non-specified internal server error\" } }";

    private final String trace;
    private final Object message;
    private final String code;

    public ErrorMessage(Exception exception) {
        this.trace = ApiException.randomTrace();
        this.code = DEFAULT_CODE;
        this.message = exception.getMessage();
    }

    public ErrorMessage(Exception exception, String trace) {
        this.trace = trace;
        this.code = DEFAULT_CODE;
        this.message = exception.getMessage();
    }

    public ErrorMessage(ApiException exception) {
        this.trace = exception.getTrace();
        this.code = exception.getCode();
        this.message = exception.getMessage();
    }

    public ErrorMessage(ApiException exception, String traceWithPrefix) {
        this.trace = traceWithPrefix;
        this.code = exception.getCode();
        this.message = exception.getMessage();
    }

    public ErrorMessage(String trace, Object message, String code) {
        this.trace = trace;
        this.message = message;
        this.code = code;
    }

    public ErrorMessage(WebApplicationException exception, String trace) {
        this.trace = trace;
        this.code = exception.getResponse() != null ? HttpErrorCodeLabels.getMatchingLabel(exception.getResponse().getStatus()) : DEFAULT_CODE;
        this.message = ErrorMessageBundle.get(this.code);
    }

    public ErrorMessage(WstxException exception, String trace) {
        this.trace = trace;
        this.code = "xml.stream.exception";
        this.message = ErrorMessageBundle.get(this.code);
    }

    public ErrorMessage(CharConversionException exception, String trace) {
        this.trace = trace;
        this.code = "unsupported.media.type";
        this.message = ErrorMessageBundle.get(this.code);
    }

    public ErrorMessage(JsonProcessingException exception, String trace) {
        this.trace = trace;
        this.code = "parse.exception";
        this.message = ErrorMessageBundle.get(this.code);
    }

    public String getTrace() {
        return trace;
    }

    public Object getMessage() {
        return message;
    }

    public String getCode() {
        return code;
    }

    public String asJsonString() {
        String str = "";
        try {
            str = MAPPER.writeValueAsString(this);
        } catch (Exception e) {
            str = DEFAULT_ERROR_JSON;
            LOG.info("Failed to write Error object as string.");
        }
        return str;
    }
}
