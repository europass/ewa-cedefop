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
package europass.ewa.services.statistics.enums.validation;

import com.fasterxml.jackson.annotation.JsonRootName;

import europass.ewa.services.statistics.enums.response.ResponseStatusMessage;

public class ResponseResult {

	ResponseStatusMessage details;
	String cause;
	
	public ResponseResult(ResponseStatusMessage details, String cause) {
		this.details = details;
		this.cause = cause;
	}
	
	public ResponseStatusMessage getDetails() {
		return details;
	}
	public void setDetails(ResponseStatusMessage details) {
		this.details = details;
	}
	public String getCause() {
		return cause;
	}
	public void setCause(String cause) {
		this.cause = cause;
	}
	
	public ResponseInfo getResponseInfo(){
		int code = Integer.parseInt(this.details.name().split("_")[1]);
		return new ResponseInfo(code, this.details.getStatus(), this.details.getMessage(), cause);
	}
	
	@JsonRootName(value = "error")
	public static class ResponseInfo{
		
		private int code;
		private String status;
		private String message;
		private String cause;
		
		public ResponseInfo(int code, String status, String message,
				String cause) {

			this.code = code;
			this.status = status;
			this.message = message;
			this.cause = cause;
		}
		public int getCode() {
			return code;
		}
		public String getStatus() {
			return status;
		}
		public String getMessage() {
			return message;
		}
		public String getCause() {
			return cause;
		}
		
		public static String getCSVHeaders(){
			return "error_code,error_status,error_message,error_cause";
		}
	}
	
}
