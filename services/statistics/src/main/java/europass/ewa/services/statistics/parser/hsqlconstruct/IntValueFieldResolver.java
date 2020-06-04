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
import java.util.Map;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

import europass.ewa.services.statistics.enums.request.ParameterNames;
import europass.ewa.services.statistics.enums.request.ValueOperators;
import europass.ewa.services.statistics.enums.request.ValueTypes;
import europass.ewa.services.statistics.enums.tables.EntityTablesProperties;
import europass.ewa.services.statistics.hibernate.data.CubeEntryAge;
import europass.ewa.services.statistics.hibernate.data.CubeEntryWorkExp;
import europass.ewa.services.statistics.structures.NumberValueRange;
import europass.ewa.services.statistics.structures.QueryParameter;
import europass.ewa.services.statistics.structures.ValueProperties;

public class IntValueFieldResolver extends EntityFieldsResolver {

	private List<ValueTypes> intValueTypes;
	private EntityTablesProperties entityProperties;

	public IntValueFieldResolver(EntityFieldsResolver next) {
		super(next);
	}

	@Override
	protected void configure() {

		intValueTypes = new ArrayList<>();

		intValueTypes.add(ValueTypes.NUMBER);
		intValueTypes.add(ValueTypes.NUMBER_AND);
		intValueTypes.add(ValueTypes.NUMBER_OR);
		intValueTypes.add(ValueTypes.NUMBER_NOT);
		intValueTypes.add(ValueTypes.NUMBER_RANGE);
	}

	@Override
	protected void populateHSQL(HSQLParts hsqlParts, QueryParameter queryParamater, EntityTablesProperties entityProperties) {

		ParameterNames parameterName = queryParamater.getParameterName();
		ValueProperties parameterProperties = queryParamater.getValueProperties();
		String fieldsPrefix = "e.";

		// Handle top parameter
		if (parameterName.equals(ParameterNames.TOP)) {
			hsqlParts.setTop(parameterProperties.getIntegerValue() != null ? parameterProperties.getIntegerValue() : 0);
			return;
		}

		ValueTypes type = parameterProperties.getValueType();
		// leave the builder intact if parameter is not in intValueTypes
		if (!intValueTypes.contains(type)) {
			return;
		}

		// Adding: e.<field name>
		String field = fieldsPrefix;
		String groupByField = "";

		// dispatch type
		if (type.equals(ValueTypes.NUMBER)) {
			field += HSQLParts.getEntityFieldbyParameter(parameterName);
			
			if (!parameterProperties.isEmpty()) {
				String where = field + " = " + parameterProperties.getIntegerValue();
				hsqlParts.addToWhereClause(where);
			}

			groupByField = field;
		} else if (type.equals(ValueTypes.NUMBER_AND) || type.equals(ValueTypes.NUMBER_OR)) {
			field += HSQLParts.getEntityFieldbyParameter(parameterName);
			List<Integer> values = parameterProperties.getIntValueList();

			if (!values.isEmpty()) {

				Joiner joiner = Joiner.on(" " + parameterProperties.getValueOperator().name() + " ");

				List<String> whereValues = new ArrayList<>();
				for (Integer value : values) {
					whereValues.add(field + " = " + value.toString());
				}

				String where = joiner.join(whereValues);
				hsqlParts.addToWhereClause("( " + where + " )");
			}

			groupByField = field;
		} else if (type.equals(ValueTypes.NUMBER_NOT)) {

			List<Integer> values = parameterProperties.getIntValueList();
			String where = "";
			if (values.size() == 1) {
				where = field + " != " + values.get(0).toString();
			} else {
				Joiner joiner = Joiner.on(" " + parameterProperties.getValueOperator().name() + " ");

				List<String> whereValues = new ArrayList<>();
				for (Integer value : values) {

					whereValues.add(field + " != " + value.toString());
				}

				where = joiner.join(whereValues);
			}

			hsqlParts.addToWhereClause(where);
			field += HSQLParts.getEntityFieldbyParameter(parameterName);

			groupByField = field;
		} else if (type.equals(ValueTypes.NUMBER_RANGE)) {

			List<NumberValueRange> values = parameterProperties.getIntValueRangeList();

			if (entityProperties.getDescription().equals(EntityTablesProperties.CUBE_ENTRY_WORKEXP.getDescription())
				|| entityProperties.getDescription().equals(EntityTablesProperties.CUBE_ENTRY_AGE.getDescription())) {

				Joiner joiner = Joiner.on(", ");
				List<String> rangesFields = new ArrayList<>();
				Map<String, NumberValueRange> rangesList = CubeEntryWorkExp.getRangesList();

				if (entityProperties.getDescription().equals(EntityTablesProperties.CUBE_ENTRY_AGE.getDescription())) {
					rangesList = CubeEntryAge.getRangesList();
				}

				for (NumberValueRange rangeValue : values) {

					int from = rangeValue.getFrom();
					int to = rangeValue.getTo();

					for (String key : rangesList.keySet()) {

						NumberValueRange current = rangesList.get(key);

						if (current.getFrom() == from && current.getTo() == to) {
							rangesFields.add("SUM(" + field + key + ")");
							hsqlParts.addOrder("SUM(" + field + key + ")", "DESC");
						}
					}

				}

				field = joiner.join(rangesFields);
			} else {

				field += HSQLParts.getEntityFieldbyParameter(parameterName);

				String where = "";
				if (values.size() == 1) {

					NumberValueRange range = values.get(0);
					where = makeRangeWhereClause(field, range);
				} else {
					Joiner joiner = Joiner.on(" " + ValueOperators.OR.name() + " ");

					List<String> whereValues = new ArrayList<>();
					for (NumberValueRange range : values) {

						String whereRange = makeRangeWhereClause(field, range);

						whereValues.add("(" + whereRange + ")");
					}

					where = joiner.join(whereValues);
				}

				hsqlParts.addToWhereClause("(" + where + ")");
				groupByField = field;
			}
		}
		hsqlParts.addToFieldClause(field);
		if (!Strings.isNullOrEmpty(groupByField)) {
			hsqlParts.addToGroupBy(groupByField);
		}
	}

	private String makeRangeWhereClause(String field, NumberValueRange range) {

		if (range.getFrom() == -1) {
			int minValue = 0; //TODO: get from entity
			return field + " >= " + minValue + " " + ValueOperators.AND.name() + " " + field + " <= " + range.getTo() + " ";
		} else if (range.getTo() == -1) {
			int maxValue = 100; //TODO: get from entity
			return field + " >= " + range.getFrom() + " " + ValueOperators.AND.name() + " " + field + " <= " + maxValue + " ";
		} else {
			return field + " >= " + range.getFrom() + " " + ValueOperators.AND.name() + " " + field + " <= " + range.getTo() + " ";
		}

	}

	public EntityTablesProperties getEntityProperties() {
		return entityProperties;
	}

	public void setEntityProperties(EntityTablesProperties entityProperties) {
		this.entityProperties = entityProperties;
	}

}
