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
package europass.ewa.services.statistics.parser.hsqlconstruct;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

import europass.ewa.services.statistics.enums.request.ValueOperators;
import europass.ewa.services.statistics.enums.request.ValueTypes;
import europass.ewa.services.statistics.enums.tables.EntityTablesFieldNames;
import europass.ewa.services.statistics.enums.tables.EntityTablesProperties;
import europass.ewa.services.statistics.enums.values.DateValueDepthEnum;
import europass.ewa.services.statistics.structures.DateValueRange;
import europass.ewa.services.statistics.structures.QueryParameter;
import europass.ewa.services.statistics.structures.ValueProperties;

public class DateValueFieldResolver extends EntityFieldsResolver {

	private List<ValueTypes> dateValueTypes;
	private EntityTablesProperties entityProperties;

	public DateValueFieldResolver(EntityFieldsResolver next) {
		super(next);
	}

	@Override
	protected void configure() {

		dateValueTypes = new ArrayList<>();

		dateValueTypes.add(ValueTypes.DATE);
		dateValueTypes.add(ValueTypes.DATE_AND);
		dateValueTypes.add(ValueTypes.DATE_OR);
		dateValueTypes.add(ValueTypes.DATE_NOT);
		dateValueTypes.add(ValueTypes.DATE_RANGE);
	}

	@Override
	protected void populateHSQL(HSQLParts hsqlParts, QueryParameter queryParamater, EntityTablesProperties entityProperties) {

		ValueProperties parameterProperties = queryParamater.getValueProperties();
		ValueTypes type = parameterProperties.getValueType();

		// leave the builder intact if parameter is not in dateValueTypes
		if (!dateValueTypes.contains(type)) {
			return;
		}

		// Adding: from <entity name> e
//		String from = entityProperties.getDescription()+ " e";
//		hsqlParts.setSingleEntityFrom(from);
		// Adding: e.<field name>
		String fieldsPrefix = "e.";
		String year_field;
		String month_field;
		String day_field;

		if (entityProperties.equals(EntityTablesProperties.STAT_VISITS)
			|| entityProperties.equals(EntityTablesProperties.STAT_DOWNLOADS)) {
			year_field = fieldsPrefix + EntityTablesFieldNames.YEAR.getDescription();
			month_field = fieldsPrefix + EntityTablesFieldNames.MONTH.getDescription();
			day_field = null;
		} else {
			year_field = fieldsPrefix + EntityTablesFieldNames.YEAR_NO.getDescription();
			month_field = fieldsPrefix + EntityTablesFieldNames.MONTH_NO.getDescription();
			day_field = fieldsPrefix + EntityTablesFieldNames.DAY_NO.getDescription();
		}

		hsqlParts.addToFieldClause(year_field);
		hsqlParts.addToGroupBy(year_field);

		if (entityProperties.equals(EntityTablesProperties.CUBE_ENTRY_NAT_RANK)) {
			month_field = "";
			day_field = "";
		} else if (entityProperties.equals(EntityTablesProperties.CUBE_ENTRY_DOCS)
			|| entityProperties.equals(EntityTablesProperties.CUBE_ENTRY_FLANG_PIVOT)
			|| entityProperties.equals(EntityTablesProperties.CUBE_ENTRY_DOCS_LANGS)
			|| entityProperties.equals(EntityTablesProperties.CUBE_ENTRY_NAT_RANK)) {

			if (parameterProperties.getDateValueDepth() != null) {

				if (parameterProperties.getDateValueDepth().equals(DateValueDepthEnum.YEAR)) {
					month_field = "";
				} else if (parameterProperties.getDateValueDepth().equals(DateValueDepthEnum.YEAR_MONTH) 
					|| parameterProperties.getDateValueDepth().equals(DateValueDepthEnum.YEAR_MONTH_DAY)) {

					hsqlParts.addToFieldClause(month_field);
					hsqlParts.addToGroupBy(month_field);
				}
			} else {
				hsqlParts.addToFieldClause(month_field);
				hsqlParts.addToGroupBy(month_field);
			}

			day_field = "";
		} else if (parameterProperties.getDateValueDepth() != null) {

			if (parameterProperties.getDateValueDepth().equals(DateValueDepthEnum.YEAR)) {
				month_field = "";
				day_field = "";
			} else if (parameterProperties.getDateValueDepth().equals(DateValueDepthEnum.YEAR_MONTH)) {

				hsqlParts.addToFieldClause(month_field);
				hsqlParts.addToGroupBy(month_field);
				day_field = "";
			} else if (parameterProperties.getDateValueDepth().equals(DateValueDepthEnum.YEAR_MONTH_DAY)) {

				hsqlParts.addToFieldClause(month_field);
				hsqlParts.addToGroupBy(month_field);

				hsqlParts.addToFieldClause(day_field);
				hsqlParts.addToGroupBy(day_field);
			}
		} else {
			hsqlParts.addToFieldClause(month_field);
			hsqlParts.addToGroupBy(month_field);

			hsqlParts.addToFieldClause(day_field);
			hsqlParts.addToGroupBy(day_field);
		}

		if (type.equals(ValueTypes.DATE)) {

			String where = makeDateWhereClause(year_field, month_field, day_field, parameterProperties.getDateValue(), "=");
			hsqlParts.addToWhereClause(where);
		} else if (type.equals(ValueTypes.DATE_AND) || type.equals(ValueTypes.DATE_OR)) {

			List<DateTime> values = parameterProperties.getDateValueList();
			String where = "";
			if (values.size() == 1) {
				where = makeDateWhereClause(year_field, month_field, day_field, values.get(0), "=");
			} else {
				Joiner joiner = Joiner.on(" " + parameterProperties.getValueOperator().name() + " ");

				List<String> whereValues = new ArrayList<>();
				for (DateTime value : values) {

					String whereRange = makeDateWhereClause(year_field, month_field, day_field, value, "=");
					whereValues.add(whereRange);
				}

				where = joiner.join(whereValues);
			}

			hsqlParts.addToWhereClause("( " + where + " )");

		} else if (type.equals(ValueTypes.DATE_NOT)) {

			List<DateTime> values = parameterProperties.getDateValueList();
			String where = "";
			if (values.size() == 1) {
				where = makeDateWhereClause(year_field, month_field, day_field, values.get(0), "!=");
			} else {
				Joiner joiner = Joiner.on(" " + parameterProperties.getValueOperator().name() + " ");

				List<String> whereValues = new ArrayList<>();
				for (DateTime value : values) {

					String whereRange = makeDateWhereClause(year_field, month_field, day_field, value, "!=");
					whereValues.add(whereRange);
				}

				where = joiner.join(whereValues);
			}

			hsqlParts.addToWhereClause(where);
		} else if (type.equals(ValueTypes.DATE_RANGE)) {

			List<DateValueRange> ranges = parameterProperties.getDateValueRangeList();

			Joiner joiner = Joiner.on(" " + ValueOperators.OR.name() + " ");
			List<String> rangeWhereList = new ArrayList<>();

			String where = "";
			for (DateValueRange range : ranges) {

				DateTime fromDate = range.getFrom();
				DateTime toDate = range.getTo();

				rangeWhereList.add(makeDateRangeWhereClause(year_field, month_field, day_field, fromDate, toDate));
			}

			if (rangeWhereList.size() > 1) {
				where += joiner.join(rangeWhereList);
			} else {
				where += rangeWhereList.get(0);
			}

			hsqlParts.addToWhereClause("(" + where + ")");
		}
	}

	private static String makeDateWhereClause(String yearField, String monthField, String dayField, DateTime dt, String comparator) {

		String yearWhere = yearField + " " + comparator + " " + dt.getYear();
		String monthWhere = monthField + " " + comparator + " " + dt.getMonthOfYear();

		if (Strings.isNullOrEmpty(monthField) && Strings.isNullOrEmpty(dayField)) {
			return yearWhere;
		}
		if (Strings.isNullOrEmpty(dayField)) {
			return "(" + yearWhere + " " + ValueOperators.AND.name() + " " + monthWhere + ") ";
		}

		String dayWhere = dayField + " " + comparator + " " + dt.getDayOfMonth();
		return "(" + yearWhere + " " + ValueOperators.AND.name() + " " + monthWhere + " " + ValueOperators.AND.name() + " " + dayWhere + ") ";
	}
	
	private static String makeDateRangeWhereClause(String yearField, String monthField, String dayField, DateTime from, DateTime to) {
		String whereClause;
		if (from.getYear() == to.getYear()) {
			if (monthField.equals("")) {      //DateValueDepthEnum.YEAR
				whereClause = yearField + " = " + from.getYear();
			} else if (dayField.equals("")) { //DateValueDepthEnum.YEAR_MONTH
				whereClause = yearField + " = " + from.getYear() + " and (" + monthField + " >= " + from.getMonthOfYear() + " and " + monthField + " <= " + to.getMonthOfYear() + ")";
			} else {                          //DateValueDepthEnum.YEAR_MONTH_DAY
				whereClause = yearField + " = " + from.getYear() + " and ("
					+ "(" + monthField + " > " + from.getMonthOfYear() + " or (" + monthField + " = " + from.getMonthOfYear() + " and " + dayField + " >= " + from.getDayOfMonth() + "))"
					+ " and (" + monthField + " < " + to.getMonthOfYear() + " or (" + monthField + " = " + to.getMonthOfYear() + " and " + dayField + " <= " + to.getDayOfMonth() + "))"
					+ ")";
			}
		} else {
			if (monthField.equals("")) {      //DateValueDepthEnum.YEAR
				whereClause = yearField + " >= " + from.getYear()
					+ " and " + yearField + " <= " + to.getYear();
			} else if (dayField.equals("")) { //DateValueDepthEnum.YEAR_MONTH
				whereClause = "(" + yearField + " > " + from.getYear() + " or (" + yearField + " = " + from.getYear() + " and " + monthField + " >= " + from.getMonthOfYear() + "))"
					+ " and (" + yearField + " < " + to.getYear() + " or (" + yearField + " = " + to.getYear() + " and " + monthField + " <= " + to.getMonthOfYear() + "))";
			} else {                          //DateValueDepthEnum.YEAR_MONTH_DAY
				whereClause = "(" + yearField + " > " + from.getYear() + " or (" + yearField + " = " + from.getYear() + " and (" + monthField + " > " + from.getMonthOfYear() + " or (" + monthField + " = " + from.getMonthOfYear() + " and " + dayField + " >= " + from.getDayOfMonth() + "))))"
					+ " and (" + yearField + " < " + to.getYear() + " or (" + yearField + " = " + to.getYear() + " and (" + monthField + " < " + to.getMonthOfYear() + " or (" + monthField + " = " + to.getMonthOfYear() + " and " + dayField + " <= " + to.getDayOfMonth() + "))))";
			}
		}

		return whereClause;
	}

	public EntityTablesProperties getEntityProperties() {
		return entityProperties;
	}

	public void setEntityProperties(EntityTablesProperties entityProperties) {
		this.entityProperties = entityProperties;
	}

}
