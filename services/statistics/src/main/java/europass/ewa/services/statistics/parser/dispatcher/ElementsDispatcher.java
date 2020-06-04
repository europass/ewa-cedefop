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
package europass.ewa.services.statistics.parser.dispatcher;

import org.joda.time.DateTime;

import com.google.common.base.Strings;

import europass.ewa.services.statistics.enums.request.ValueOperators;
import europass.ewa.services.statistics.enums.request.ValueTypes;
import europass.ewa.services.statistics.structures.DateValueRange;
import europass.ewa.services.statistics.structures.ValidationResult;
import europass.ewa.services.statistics.structures.ValuePropertiesBuilder;

public abstract class ElementsDispatcher {

	protected String parameterName;
	protected ValueOperators operator;
	protected String value;
	protected ElementsDispatcher nextInChain;

	public ElementsDispatcher(ElementsDispatcher nextInChain, String parameterName, String value, ValueOperators operator) {

		this.nextInChain = nextInChain;

		this.parameterName = parameterName;
		this.value = value;
		this.operator = operator;
	}

	public ValidationResult dispatch(ValuePropertiesBuilder builder, ValueTypes type) {

		ValidationResult result = new ValidationResult.Builder(true).build();
		if (this.nextInChain != null) {
			result = this.nextInChain.dispatch(builder, type);
		}
		return result;
	}

	protected DateTime getDateTimeValue(String value, boolean isFrom) {

		String[] dateParts = value.split("\\.");
		int year, month, day;

		year = Integer.parseInt(dateParts[0]);
		if (dateParts.length > 1) {
			month = Integer.parseInt(dateParts[1]);
		} else {
			if (isFrom) {
				month = 1;
			} else {
				month = 12;
			}
		}

		if (dateParts.length > 2) {
			day = Integer.parseInt(dateParts[2]);
		} else {
			if (isFrom) {
				day = 1;
			} else {

				DateTime givenDate = new DateTime().withYear(year).withMonthOfYear(month);
				day = givenDate.dayOfMonth().withMaximumValue().getDayOfMonth();
			}
		}

		DateTime dateValue = null;
		try {
			dateValue = new DateTime(year, month, day, 0, 0);
		} catch (org.joda.time.IllegalFieldValueException e) {
		}

		return dateValue;
	}

	protected DateValueRange makeDateRange(String from, String to) {

		DateTime fromTo = DateTime.now();
		DateValueRange dateRange = new DateValueRange(fromTo, fromTo);

		//In case there is no from, value is empty, so construct a range from 2005(editor's lanch) to current year 
		if (Strings.isNullOrEmpty(from)) {
			dateRange = new DateValueRange(fromTo.withYear(2005), fromTo);
			return dateRange;
		}

		int yearFrom, monthFrom;
		int yearTo, monthTo;

		boolean hasFromOnly = Strings.isNullOrEmpty(to);

		String[] dateFromArray = from.split("\\.");
		String[] dateToArray = {};

		if (!hasFromOnly) {
			dateToArray = to.split("\\.");
		}

		try {

			/**
			 * AT - Note: 
			 * This method was converting  YYYY or YYYY.MM single dates to date ranges 
			 * (YYYY.01.01 - YYYY.12.31, YYYY.MM.01 - YYYY.MM.[28-31] respectively) 
			 * thus changing the type used in following methods to DATE RANGE.
			 * I changed this logic keeping YYYY and YYYY.MM 
			 * as dates as this way we seem to end up with simpler sql queries.
			 * 
			 */
			if (dateFromArray.length == 3) { //YYYY.MM.DD

				dateRange.setFrom(dateTimeFromStringParts(dateFromArray[0], dateFromArray[1], dateFromArray[2]));
				if (hasFromOnly) {
					// If has only from and is a full date it is an actual single date
					dateRange.setSingle(true);
				} else {
					if (dateToArray.length != 3) {
						dateRange.setValid(false);
						return dateRange;
					}
					dateRange.setTo(dateTimeFromStringParts(dateToArray[0], dateToArray[1], dateToArray[2]));
				}
			} else if (dateFromArray.length == 2) { //YYYY.MM 

				yearFrom = Integer.parseInt(dateFromArray[0]);
				DateTime dateYear = new DateTime().withYear(yearFrom);

				monthFrom = Integer.parseInt(dateFromArray[1]);

				// YYYY.MM.01
				dateRange.setFrom(dateYear.withMonthOfYear(monthFrom).dayOfMonth().withMinimumValue());

				if (hasFromOnly) {
					// YYYY.MM.[28-31]
					//dateRange.setTo(dateYear.withMonthOfYear(monthFrom).dayOfMonth().withMaximumValue());
					dateRange.setSingle(true);
				} else {
					if (dateToArray.length != 2) {
						dateRange.setValid(false);
						return dateRange;
					}
					yearTo = Integer.parseInt(dateToArray[0]);
					monthTo = Integer.parseInt(dateToArray[1]);
					DateTime dateYearMonth = new DateTime().withYear(yearTo).withMonthOfYear(monthTo);

					dateRange.setTo(dateYearMonth.dayOfMonth().withMaximumValue());
				}
			} else if (dateFromArray.length == 1) { //YYYY

				yearFrom = Integer.parseInt(dateFromArray[0]);
				DateTime dateYear = new DateTime().withYear(yearFrom);

				// YYYY.01.01
				dateRange.setFrom(dateYear.monthOfYear().withMinimumValue().dayOfMonth().withMinimumValue());

				if (hasFromOnly) {
					// YYYY.12.31
					//dateRange.setTo(dateYear.monthOfYear().withMaximumValue().dayOfMonth().withMaximumValue());
					dateRange.setSingle(true);
				} else {
					if (dateToArray.length != 1) {
						dateRange.setValid(false);
						return dateRange;
					}

					yearTo = Integer.parseInt(dateToArray[0]);
					dateRange.setTo(new DateTime().withYear(yearTo).monthOfYear().withMaximumValue().dayOfMonth().withMaximumValue());
				}
			}

		} catch (org.joda.time.IllegalFieldValueException e) {

			dateRange.setFrom(fromTo);
			dateRange.setTo(fromTo);
		}

		return dateRange;
	}

	private DateTime dateTimeFromStringParts(String year, String month, String day) {

		if (year == null || month == null || day == null) {
			return null;
		}

		return new DateTime()
			.withYear(Integer.parseInt(year))
			.withMonthOfYear(Integer.parseInt(month))
			.withDayOfMonth(Integer.parseInt(day));
	}

	public String getParameterName() {
		return parameterName;
	}

	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public ValueOperators getOperatorRegex() {
		return operator;
	}

	public String getValue() {
		return value;
	}

	public ElementsDispatcher getNextInChain() {
		return nextInChain;
	}

	public void setNextInChain(ElementsDispatcher nextInChain) {
		this.nextInChain = nextInChain;
	}

	public void setOperatorRegex(ValueOperators operator) {
		this.operator = operator;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
