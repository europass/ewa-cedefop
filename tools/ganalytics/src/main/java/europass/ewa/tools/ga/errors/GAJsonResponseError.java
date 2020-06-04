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
package europass.ewa.tools.ga.errors;

import europass.ewa.tools.ga.enums.ErrorTypesRecommendations;

public class GAJsonResponseError{
	
	String code;
	
	ErrorTypesRecommendations type;
	String message;
	String recommendation;
	
	String dbException;
	
	public GAJsonResponseError(String code,ErrorTypesRecommendations type,String message){
		setCode(code);
		setMessage(message);
		setType(type);
		setRecommendation(type.getRecommendation());
	}

	public void configure(String code,ErrorTypesRecommendations type,String message){
		setCode(code);
		setMessage(message);
		setType(type);
		setRecommendation(type.getRecommendation());
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	public ErrorTypesRecommendations getType() {
		return type;
	}

	public void setType(ErrorTypesRecommendations type) {
		this.type = type;
	}

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public String getRecommendation() {
		return recommendation;
	}

	public void setRecommendation(String recommendation) {
		this.recommendation = recommendation;
	}
	
	public String getDbException() {
		return dbException;
	}

	public void setDbException(String dbException) {
		this.dbException = dbException;
	}
	
}