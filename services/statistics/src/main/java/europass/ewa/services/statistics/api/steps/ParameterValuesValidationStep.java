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
package europass.ewa.services.statistics.api.steps;

import com.google.inject.Inject;

import europass.ewa.services.statistics.enums.validation.RequestValidationScopes;
import europass.ewa.services.statistics.structures.ValidationResult;
import europass.ewa.services.statistics.validators.Validator;
import europass.ewa.services.statistics.validators.factory.ValidatorFactory;

/**
 * ParameterValuesValidationStep
 *
 * Performs Validation of the query request regarding the validity of Given
 * Query Parameters Enumeration Values
 *
 * @author pgia
 *
 */
public class ParameterValuesValidationStep extends AbstractStatisticsApiStep {

	@Inject
	public ParameterValuesValidationStep(ValidatorFactory factory) {
		super(factory);
	}

	@Override
	public void setNext(AbstractStatisticsApiStep step) {
		super.setNext(step);
	}

	@Override
	public void doStep() {

		//super.setStatisticsApiInfo(info);
		super.info.setValidationResult(validateEnumValues());
		super.doStep();
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private ValidationResult validateEnumValues() {

		Validator validator = VALIDATOR_FACTORY.getValidator(RequestValidationScopes.PARAMETER_VALUE_ENUMS.getDescription());
		ValidationResult result = validator.validateAllAgainstValues(this.info.getQueryParametersValuesMap());

		return result;
	}
}
