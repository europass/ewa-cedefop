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
package europass.ewa.statistics.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidationUtils {

    private static final Logger LOG = LoggerFactory.getLogger(ValidationUtils.class);

    public static String validateSetterStringLength(String fieldName, String fieldValue, int length) {

        if (fieldValue != null) {
            if (fieldValue.length() > length) {

                LOG.warn("field " + fieldName + "( " + length + " ): value '" + fieldValue + "' exceeds default size. Returning cropped value ( sized " + length + " )");
                return fieldValue.substring(0, length - 1);
            }
        }

        return fieldValue;
    }

    /*	

//	Validation using ConstraintViolation
//	needs javax.validation classes (Persistence Validation API)
	
	public static <E> void validateSetterArgument(E hibernateObj, String fieldName){
		
	    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
	    Validator validator = factory.getValidator();
	    Set<ConstraintViolation<E>> constraintViolations = validator.validateProperty(hibernateObj, fieldName);

	    if (constraintViolations.size() != 0) {
	        throw new IllegalArgumentException("Invalid "+fieldName+"; cropping it's size to much column size");
	    }		
	}
	
     */
}
