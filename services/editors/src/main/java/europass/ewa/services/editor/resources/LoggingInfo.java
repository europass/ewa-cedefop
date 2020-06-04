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
package europass.ewa.services.editor.resources;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonRootName;

import europass.ewa.services.exception.ApiException;

@JsonPropertyOrder({"errorMessage", "specificErrorMessage", "userAgent", "errCode", "completeErrorCode"})
@JsonRootName("loggingInfo")
public class LoggingInfo {

    private String errorMessage;
    private String specificErrorMessage;
    private String userAgent;
    private String errCode;
    private String completeErrorCode;

    public LoggingInfo() {
        this.errCode = randomTrace();
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String asString() {
        return "User Agent: " + userAgent + " Error: " + errorMessage;
    }

    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        if (StringUtils.isEmpty(this.errCode)) {
            if (!StringUtils.isEmpty(this.errCode)) {
                this.errCode = errCode;
            } else {
                this.errCode = randomTrace();
            }
        }
    }

    private String randomTrace() {
        return ApiException.randomTrace();
        //return RandomStringUtils.randomAlphanumeric(TRACE_LENGTH);
        /*DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd.hhmmss-");
		Calendar cal = Calendar.getInstance();
		String value = dateFormat.format(cal.getTime()) + RandomStringUtils.randomAlphanumeric(5) ;
		return value ;*/
    }

    public String getCompleteErrorCode() {
        return completeErrorCode;
    }

    public void setCompleteErrorCode(String completeErrorCode) {
        this.completeErrorCode = completeErrorCode;
    }

    /**
     * @return the specificErrorMessage
     */
    public String getSpecificErrorMessage() {
        return specificErrorMessage;
    }

    /**
     * @param specificErrorMessage the specificErrorMessage to set
     */
    public void setSpecificErrorMessage(String specificErrorMessage) {
        this.specificErrorMessage = specificErrorMessage;
    }

}
