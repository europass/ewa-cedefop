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
package europass.ewa.services.statistics.structures;

import europass.ewa.services.statistics.enums.validation.ValidationErrors;

/**
 * ValidationResult with builder class
 * 
 * Configures case-based validation results and store information used in validation error messages
 * Uses builder to introduce different validation results information
 * 
 * @author pgia
 */

public class ValidationResult {
	
	private final boolean success;
	private final ValidationErrors error;
	private final String failedOn;
	private final String valueType;	
	private final String against;
	private final ValueProperties valueProperties;
	
	ValidationResult(Builder builder){
		
		this.success = builder.success;
		this.error = builder.error;
		this.failedOn = builder.failedOn;
		this.valueType = builder.valueType;
		this.against = builder.against;
		this.valueProperties = builder.valueProperties;
	}

	public boolean getSuccess() {
		return success;
	}

	public ValidationErrors getValidationError() {
		return error;
	}

	public String getFailedOn() {
		return failedOn;
	}

	public String getValueType() {
		return valueType;
	}

	public String getAgainst() {
		return against;
	}

	public ValueProperties getValueProperties() {
		return valueProperties;
	}
	
	public String resultMessage() {

		StringBuilder sb = new StringBuilder();
		
		if(success){
			sb.append("Validation OK");
		}else{
			
			if(failedOn != null){
				if(against != null){
					if(valueType != null){
						sb.append(error.mismatchTypeErrorOn(failedOn, valueType, against));
					}else
						sb.append(error.mismatchErrorOn(failedOn, against));
				}
				else
					sb.append(error.invalidErrorOn(failedOn));
			}else
				sb.append(ValidationErrors.UKNOWN_ERROR.getDescription());
		}
		
		return sb.toString();
	}

	public static class Builder{
		
		private final boolean success;
		private ValidationErrors error;
		private String failedOn;
		private String valueType;
		private String against;
		private ValueProperties valueProperties;
		
		public Builder(boolean success) {
			this.success = success;
		}
		
		public Builder withValidationErrors(ValidationErrors error){
			this.error = error;
			return this;
		}
		
		public Builder withFailedOn(String failedOn){
			this.failedOn = failedOn;
			return this;
		}

		public Builder withAgainst(String against){
			this.against = against;
			return this;
		}
		
		public Builder withValueType(String valueType) {
			this.valueType = valueType;
			return this;
		}

		public Builder withValueProperties(ValueProperties valueProperties) {
			this.valueProperties = valueProperties;
			return this;
		}

		public ValidationResult build(){
			return new ValidationResult(this);
		}

	}

}
