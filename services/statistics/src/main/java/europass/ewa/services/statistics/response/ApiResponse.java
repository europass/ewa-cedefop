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
package europass.ewa.services.statistics.response;

public class ApiResponse {

	private final int code;
	private final String status;
	private final String description;
	
	ApiResponse(ApiResponseBuilder builder){
		
		this.code = builder.code;
		this.status = builder.status;
		this.description = builder.description;
	}

	public int getErrorCode() {
		return code;
	}

	public String getStatus() {
		return status;
	}

	public String getDescription() {
		return description;
	}

	public static class ApiResponseBuilder{
		
		private final int code;
		private final String status;
		private String description;
		
		public ApiResponseBuilder(int code, String status) {
			this.code = code;
			this.status = status;
		}
		
		public ApiResponseBuilder withDescription(String description){
			this.description = description;
			return this;
		}
		
		public ApiResponse build(){
			return new ApiResponse(this);
		}
	}
}
