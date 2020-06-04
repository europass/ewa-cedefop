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
package europass.ewa.tools.ga.exceptions;

public abstract class AbstractGAStepExecuteException extends Exception {

	private static final long serialVersionUID = 1L;

	AbstractGAStepExecuteException(Exception e){
		super(e);
	}
	
	/**
	 * Returns the actual class name aof th exceptions cause, or empty if no cause found
	 * @return the actual class name of the throwable
	 */
	
	public String getThrowableName(boolean causeName){

//		String causeClassName = this.getClass().getCanonicalName();
		
		Throwable ex = this;
		if(causeName)
			ex = this.getCause();
		
		String causeClassName = ex.getClass().getCanonicalName();
		return causeClassName.substring(causeClassName.lastIndexOf(".") + 1, causeClassName.length());
	}
	
	public String getThrowableCauseMessage(){
		
//		String causeClassName = this.getClass().getCanonicalName();
		Throwable cause = this.getCause();
		
		if(cause == null)
			return "";
		
		return cause.getMessage();
	}
	
}
