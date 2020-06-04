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

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import joptsimple.internal.Strings;

import org.apache.commons.lang.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

import europass.ewa.enums.LogFields;
import europass.ewa.exception.TraceableException;
import europass.ewa.logging.ExtraLogInfo;

public class ApiException extends RuntimeException implements TraceableException {

    private static final Logger LOG = LoggerFactory.getLogger(ApiException.class);

    private static final long serialVersionUID = 8001793703707117443L;

    private static final int TRACE_LENGTH = 8;

    private static final String DEFAULT_CODE = "ewa.api.runtime.error";

    private static final String DEFAULT_TXT_MESSAGE = "EWA API Runtime Exception";

    private static final String DEFAULT_MESSAGE = ErrorMessageBundle.get(DEFAULT_CODE, DEFAULT_TXT_MESSAGE);

    private static final Status DEFAULT_HTTP_STATUS = Response.Status.INTERNAL_SERVER_ERROR;

    private final String trace;

    private String code;

    private Status status;

    private ExtraLogInfo extraInfo;

    private int statusCode;

    public ApiException() {
        super(DEFAULT_MESSAGE);
        this.trace = randomTrace();
        defaultInit();
    }

    public ApiException(String message, Throwable cause) {
        super(message, cause);
        this.trace = randomTrace();
        defaultInit();
        addInfo(this, new ExtraLogInfo().add(LogFields.MESSAGE, message));
    }

    public ApiException(String message, Throwable cause, String code, Status status) {
        super(message, cause);
        this.trace = randomTrace();
        this.code = code == null ? DEFAULT_CODE : code;
        this.status = status == null ? DEFAULT_HTTP_STATUS : status;
        this.statusCode = this.status.getStatusCode();
        addInfo(this, new ExtraLogInfo().add(LogFields.MESSAGE, message));
    }

    public ApiException(Throwable cause, String code, Status status) {
        super(ErrorMessageBundle.get(code, DEFAULT_TXT_MESSAGE), cause);
        this.trace = randomTrace();
        this.code = code == null ? DEFAULT_CODE : code;
        this.status = status == null ? DEFAULT_HTTP_STATUS : status;
        this.statusCode = this.status.getStatusCode();
        addInfo(this, new ExtraLogInfo().add(LogFields.MESSAGE, ErrorMessageBundle.get(code, DEFAULT_TXT_MESSAGE)));
    }

    public ApiException(String message, String code, Status status) {
        super(message);
        this.trace = randomTrace();
        this.code = code == null ? DEFAULT_CODE : code;
        this.status = status == null ? DEFAULT_HTTP_STATUS : status;
        this.statusCode = this.status.getStatusCode();
        addInfo(this, new ExtraLogInfo().add(LogFields.MESSAGE, message));
    }

    public ApiException(Throwable cause, String code, int statusCode) {
        super(DEFAULT_MESSAGE, cause);
        this.trace = randomTrace();
        this.code = code == null ? DEFAULT_CODE : code;
        this.statusCode = statusCode;
        this.status = Status.fromStatusCode(statusCode);
    }

    public ApiException(String message, String code, int statusCode) {
        super(message);
        this.trace = randomTrace();
        this.code = code == null ? DEFAULT_CODE : code;
        this.statusCode = statusCode;
        addInfo(this, new ExtraLogInfo().add(LogFields.MESSAGE, message));
    }

    public ApiException(String message) {
        super(message);
        this.trace = randomTrace();
        defaultInit();
        addInfo(this, new ExtraLogInfo().add(LogFields.MESSAGE, message));
    }

    public ApiException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
        this.trace = randomTrace();
        defaultInit();
    }

    /**
     * Generates a random alphanumeric
     *
     * @return
     */
    public static String randomTrace() {
        return RandomStringUtils.randomAlphanumeric(TRACE_LENGTH);
    }

    // --- GETTERS SETTERS
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
        this.statusCode = status == null ? DEFAULT_HTTP_STATUS.getStatusCode() : status.getStatusCode();
    }

    @Override
    public String getTrace() {
        return TRACE_PREFIX + trace;
    }

    public String getTraceOnly() {
        return trace;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public ExtraLogInfo getExtraLogInfo() {
        return extraInfo;
    }

    /**
     * Provides some default setting of necessary variables Used by constructors
     */
    private void defaultInit() {
        this.code = DEFAULT_CODE;
        this.status = DEFAULT_HTTP_STATUS;
        this.statusCode = this.status.getStatusCode();
    }

    /**
     * Converts the API exception to an Error object, useful for serialization
     * purposes
     *
     * @return
     */
    public ErrorMessage asError() {
        return new ErrorMessage(this);
    }

    public ErrorMessage asError(String trace) {
        return new ErrorMessage(this, TraceableException.TRACE_PREFIX + trace);
    }

    /**
     * Builder type method which augments the additional logging information
     * object with log fields
     *
     * @param ApiException e the exception instance to be augmented
     * @param LogFields field the type of the field to be inserted
     * @param String value	the value of the field
     * @returns augmented ApiException instance e
     */
    public static ApiException addInfo(ApiException e, LogFields field, String value) {

        if (!Strings.isNullOrEmpty(value)) {
            e.extraInfo.add(field, value);
        }

        return e;
    }

    public static ApiException addInfo(ApiException e, ExtraLogInfo info) {

        if (e.extraInfo == null) {
            e.extraInfo = info;
        } else {
            e.extraInfo.add(info);
        }

        return e;
    }

    /* public void log: Logs the exception together with (any) extra logging information (in JSON form) 
	 * @param String errCode in case we need to override the exception's error code
	 * */
    public void log() {

        String extraInfos = Strings.EMPTY;

        if (extraInfo != null) {

            //if ApiExceptionMapper hasn't set an ErrCode
            if (extraInfo.getLogEntry(LogFields.ERRCODE) == null) {
                addInfo(this, new ExtraLogInfo().add(LogFields.ERRCODE, this.getTraceOnly()));
            }

            try {
                extraInfos = extraInfo.getLogInfoAsJson();
            } catch (JsonProcessingException e) {
                extraInfos = extraInfo.toStringAsJson(); //fallback
            }
        }
        LOG.error(extraInfos, this);
    }
}
