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
import europass.ewa.services.statistics.enums.validation.ValidationErrors;
import europass.ewa.services.statistics.structures.ValidationResult;
import europass.ewa.services.statistics.validators.Validator;
import europass.ewa.services.statistics.validators.factory.ValidatorFactory;

/**
 * RequestValidationStep
 *
 * Performs Validation of the query request regarding the validity of:
 * - Response Format
 * - Query Prefix
 * - Query Parameter Names
 * - Given Query Parameters versus Query Prefix
 * - Given Query Parameters Names versus allowed Value Types
 *
 * @author pgia
 *
 */
public class RequestValidationStep extends AbstractStatisticsApiStep {

	@Inject
	public RequestValidationStep(ValidatorFactory factory) {
		super(factory);
	}

	@Override
	public void setNext(AbstractStatisticsApiStep step) {
		super.setNext(step);
	}

	@Override
	public void doStep() {

		super.info.setValidationResult(validateRequest());
		super.doStep();
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private ValidationResult validateRequest() {

		ValidationResult result = new ValidationResult.Builder(false)
				.withValidationErrors(ValidationErrors.UKNOWN_ERROR)
				.build();

		for (RequestValidationScopes scope : RequestValidationScopes.values()) {

			String scopeStr = scope.getDescription();
			Validator validator = VALIDATOR_FACTORY.getValidator(scopeStr);

			switch (scope.name()) {
				case "FORMAT":
					result = validator.validate(this.info.getResponseFormat());
					break;
				case "PREFIX":
					result = validator.validate(this.info.getQueryPrefix());
					break;
				case "PARAMETER":
					result = validator.validateAll(this.info.getQueryParameterNames());
					break;
				case "PARAMETER_PREFIX":
					result = validator.validateAllAgainst(this.info.getQueryParameterNames(), this.info.getQueryPrefix());
					break;
				case "PARAMETER_VALUE_TYPES":
					result = validator.validateAllAgainstValues(this.info.getQueryParametersValuesMap());
					break;
			}

			// avoid looping in early loops failure
			if (!result.getSuccess()) {
				return result;
			}
		}

		return result;
	}
}
