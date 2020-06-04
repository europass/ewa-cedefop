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

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

import europass.ewa.services.statistics.constants.ServicesStatisticsConstants;
import europass.ewa.services.statistics.enums.request.ParameterNames;
import europass.ewa.services.statistics.enums.request.ValueTypes;
import europass.ewa.services.statistics.enums.tables.EntityTablesFieldNames;
import europass.ewa.services.statistics.enums.tables.EntityTablesProperties;
import europass.ewa.services.statistics.enums.values.DocumentTypeEnum;
import europass.ewa.services.statistics.enums.values.GenderGroupEnum;
import europass.ewa.services.statistics.structures.QueryParameter;
import europass.ewa.services.statistics.structures.ValueProperties;

public class StringValueFieldResolver extends EntityFieldsResolver {

	private List<ValueTypes> stringValueTypes;
	private EntityTablesProperties entityProperties;

	public StringValueFieldResolver(StringValueFieldResolver next) {
		super(next);
	}

	@Override
	protected void configure() {
		stringValueTypes = new ArrayList<>();
		stringValueTypes.add(ValueTypes.VALUE);
		stringValueTypes.add(ValueTypes.VALUE_OR);
		stringValueTypes.add(ValueTypes.VALUE_NOT);
		stringValueTypes.add(ValueTypes.VALUE_AND);
	}

	@Override
	protected void populateHSQL(HSQLParts hsqlParts, QueryParameter queryParamater, EntityTablesProperties entityProperties) {

		ParameterNames parameterName = queryParamater.getParameterName();
		ValueProperties parameterProperties = queryParamater.getValueProperties();
		String fieldsPrefix = "e.";

		// Handle groupby parameter
		if (parameterName.equals(ParameterNames.GROUP_BY)) {

			String groupByValue = parameterProperties.getValue();

			if (!(groupByValue).equals(ParameterNames.DATE.getDescription())) {
				if (!(hsqlParts.getGroupByList().contains(fieldsPrefix + groupByValue)) && HSQLParts.isGeneralCube(entityProperties)) {
					ParameterNames groupByParameter = ParameterNames.match(groupByValue);
					hsqlParts.addToGroupBy(fieldsPrefix + HSQLParts.getEntityFieldbyParameter(groupByParameter));
				}
			}

			return;
		}

		// Handle orderby parameter
		if (parameterName.equals(ParameterNames.ORDER_BY)) {

			if (!(hsqlParts.getGroupByList().contains(fieldsPrefix + parameterName.getDescription()))) {
				String orderByValue = parameterProperties.getValue();
				String[] orderByClause = orderByValue.split("\\.");
				
				String orderBy = orderByClause[0];
				String order = orderByClause[1];
				
				ParameterNames orderByParameter = ParameterNames.match(orderBy);

				if (!orderByParameter.equals(ParameterNames.INVALID) || orderBy.equals(ServicesStatisticsConstants.ORDER_BY_RESULTS)) {

					// In case of date, set order by date and handle it during the hsql building
					// In case of ORDER_BY_RESULTS check if the order by is set to rec_count
					// In any other case locate the entity filed name via the parameter
					
					if (orderByParameter.equals(ParameterNames.DATE)) {
						hsqlParts.addOrder(fieldsPrefix + orderByParameter.getDescription(), order);
					} else if (orderBy.equals(ServicesStatisticsConstants.ORDER_BY_RESULTS)) {

						if (!hsqlParts.getOrderByList().contains(EntityTablesFieldNames.REC_COUNT.getDescription())) {
							if (!HSQLParts.isGeneralCube(entityProperties)) {
								hsqlParts.addOrder(fieldsPrefix + HSQLParts.getEntityFieldbyParameter(orderByParameter), order);
							}
						}
					} else {
						hsqlParts.addOrder(fieldsPrefix + HSQLParts.getEntityFieldbyParameter(orderByParameter), order);
					}
				}
			}

			return;
		}
		
		// Handle unique users parameter
		if (parameterName.equals(ParameterNames.UNIQUE_USERS)) {
			return;
		}

		// leave the builder intact if parameter is not a string value 
		ValueTypes type = parameterProperties.getValueType();
		if (!stringValueTypes.contains(type)) {
			return;
		}

		// Adding: e.<field name>
		String fieldClause = fieldsPrefix;
		String groupByField = "";

		if (type.equals(ValueTypes.VALUE)) {

			if (entityProperties.getDescription().equals(EntityTablesProperties.CUBE_ENTRY_DOCS.getDescription())
				|| entityProperties.getDescription().equals(EntityTablesProperties.CUBE_ENTRY_DOCS_LANGS.getDescription())) {
				if (parameterName.equals(ParameterNames.DOCUMENT_TYPE)) {

					if (parameterProperties.isEmpty()) {

						List<DocumentTypeEnum> documentTypeEnumList = DocumentTypeEnum.getSingleValues();

						Joiner joiner = Joiner.on(",");
						List<String> fieldsList = new ArrayList<>();

						for (DocumentTypeEnum value : documentTypeEnumList) {
							fieldsList.add("ISNULL(SUM(" + fieldsPrefix + value.getDescription() + "),0)");
						}

						fieldClause = joiner.join(fieldsList);

					} else {
						fieldClause += parameterProperties.getValue();
						fieldClause = "ISNULL(SUM(" + fieldClause + "),0)";
					}
				} else {
					fieldClause += HSQLParts.getEntityFieldbyParameter(parameterName);

					if (!Strings.isNullOrEmpty(parameterProperties.getValue())) {

						String where = fieldClause;
						
							where += " = '" + parameterProperties.getValue() + "'";
						
						hsqlParts.addToWhereClause(where);
					}

					groupByField = fieldClause;
				}
			} else if (entityProperties.getDescription().equals(EntityTablesProperties.CUBE_ENTRY_GENDER.getDescription())) {

				if (parameterName.equals(ParameterNames.GENDER)) {
					if (parameterProperties.isEmpty()) {

						Joiner joiner = Joiner.on(", ");

						List<GenderGroupEnum> genderList = GenderGroupEnum.getSingleValues();
						List<String> genderFields = new ArrayList<>();
						for (GenderGroupEnum gender : genderList) {
							genderFields.add("ISNULL(SUM(" + fieldClause + gender + "),0)");
						}

						fieldClause = joiner.join(genderFields);

					} else {
						fieldClause += parameterProperties.getValue();
						fieldClause = "ISNULL(SUM(" + fieldClause + "),0)";
					}
				} else {

					fieldClause += HSQLParts.getEntityFieldbyParameter(parameterName);

					if (!parameterProperties.isEmpty()) {

						String where = fieldClause;

						where += " = '" + parameterProperties.getValue() + "'";

						hsqlParts.addToWhereClause(where);
					}

					groupByField = fieldClause;
				}

			} else if (entityProperties.getDescription().equals(EntityTablesProperties.CUBE_ENTRY_FLANG_PIVOT.getDescription())) {

				Joiner joiner = Joiner.on(", ");

				List<String> flangFields = new ArrayList<>();
				flangFields.add("SUM(" + fieldsPrefix + "lang1)");
				flangFields.add("SUM(" + fieldsPrefix + "lang2)");
				flangFields.add("SUM(" + fieldsPrefix + "lang3)");
				flangFields.add("SUM(" + fieldsPrefix + "lang4)");
				flangFields.add("SUM(" + fieldsPrefix + "lang4plus)");

				fieldClause = joiner.join(flangFields);
				//groupByField = fieldClause;
			} else if (entityProperties.getDescription().equals(EntityTablesProperties.STAT_VISITS.getDescription())
				|| entityProperties.getDescription().equals(EntityTablesProperties.STAT_DOWNLOADS.getDescription())) {

				fieldClause = fieldsPrefix + HSQLParts.getVisitsDownloadsEntityFieldbyParameter(parameterName);

				if (!parameterProperties.isEmpty()) {

					String where = fieldClause;

					where += " = '" + parameterProperties.getValue() + "'";
					
					hsqlParts.addToWhereClause(where);
				}

				groupByField = fieldClause;

			} else {
				fieldClause = fieldsPrefix + HSQLParts.getEntityFieldbyParameter(parameterName);

				if (!parameterProperties.isEmpty()) {

					String where = fieldClause;

					if (parameterName.equals(ParameterNames.MLANGUAGE)
						|| parameterName.equals(ParameterNames.OLANGUAGE)) {
						where += " LIKE '%" + parameterProperties.getValue() + "%'";
					} else {
						where += " = '" + parameterProperties.getValue() + "'";
					}

					//String where = fieldClause+" = '"+parameterProperties.getValue()+"'";
					hsqlParts.addToWhereClause(where);
				}

				groupByField = fieldClause;
			}

		} else if (type.equals(ValueTypes.VALUE_AND) || type.equals(ValueTypes.VALUE_OR)) {

			List<String> values = parameterProperties.getStrValueList();

			if (entityProperties.getDescription().equals(EntityTablesProperties.CUBE_ENTRY_DOCS.getDescription())
				|| entityProperties.getDescription().equals(EntityTablesProperties.CUBE_ENTRY_DOCS_LANGS.getDescription())) {

				Joiner joiner = null;
				if (type.equals(ValueTypes.VALUE_AND)) {
					joiner = Joiner.on(", ");
				}
				if (type.equals(ValueTypes.VALUE_OR)) {
					joiner = Joiner.on(" + ");
				}

				List<String> queryFieldsList = new ArrayList<>();

				if (parameterName.equals(ParameterNames.DOCUMENT_TYPE)) {

					// Iterate document-type valid values
					for (String strValue : values) {
						for (DocumentTypeEnum enumValue : DocumentTypeEnum.values()) {
							if (enumValue.name().equals(strValue)) {
								queryFieldsList.add("ISNULL(SUM(" + fieldClause + strValue + "),0)");
							}
						}
					}

				} else {

					String fieldName = fieldClause + HSQLParts.getEntityFieldbyParameter(parameterName);
					queryFieldsList.add(fieldName);

					if (!parameterProperties.isEmpty()) {

						String where = fieldClause;

						Joiner joinerORvalues = Joiner.on(" OR ");

						if (parameterName.equals(ParameterNames.COUNTRY)
							|| parameterName.equals(ParameterNames.LANGUAGE)
							|| parameterName.equals(ParameterNames.MLANGUAGE)
							|| parameterName.equals(ParameterNames.OLANGUAGE)
							|| parameterName.equals(ParameterNames.NATIONALITY)
							|| parameterName.equals(ParameterNames.ONATIONALITY)) {

							List<String> whereValuesList = new ArrayList<>();

							for (String val : parameterProperties.getStrValueList()) {
								whereValuesList.add(fieldName + " = '" + val + "'");
							}

							where = joinerORvalues.join(whereValuesList);
							hsqlParts.addToWhereClause("(" + where + ")");
						}
					}

					groupByField = fieldName;
				}

				fieldClause = joiner.join(queryFieldsList);
				joiner = Joiner.on(", ");

				//groupByField = joiner.join(originalFieldsList);
			} else if (entityProperties.getDescription().equals(EntityTablesProperties.CUBE_ENTRY_GENDER.getDescription())) {

				Joiner joiner = null;
				if (type.equals(ValueTypes.VALUE_AND)) {
					joiner = Joiner.on(", ");
				}
				if (type.equals(ValueTypes.VALUE_OR)) {
					joiner = Joiner.on(" + ");
				}

				List<String> queryFieldsList = new ArrayList<>();

				if (parameterName.equals(ParameterNames.GENDER)) {
					for (String strValue : values) {
						for (GenderGroupEnum enumValue : GenderGroupEnum.values()) {
							if (enumValue.getDescription().equals(strValue)) {
								queryFieldsList.add("ISNULL(SUM(" + fieldClause + strValue + "),0)");
							}
						}
					}
					fieldClause = joiner.join(queryFieldsList);

					joiner = Joiner.on(", ");

				} else if (parameterName.equals(ParameterNames.DOCUMENT_TYPE) || parameterName.equals(ParameterNames.COUNTRY)) {

					fieldClause = fieldsPrefix + HSQLParts.getEntityFieldbyParameter(parameterName);

					joiner = Joiner.on(" " + parameterProperties.getValueOperator().name() + " ");
					List<String> whereValues = new ArrayList<>();
					for (String value : values) {

						String where = fieldClause;

						where += " = '" + value + "'";

						whereValues.add(where);
					}

					String where = joiner.join(whereValues);
					hsqlParts.addToWhereClause("( " + where + " )");

					groupByField = fieldClause;
				} 

			} else if (entityProperties.getDescription().equals(EntityTablesProperties.STAT_VISITS.getDescription())
				|| entityProperties.getDescription().equals(EntityTablesProperties.STAT_DOWNLOADS.getDescription())) {

				fieldClause = fieldsPrefix + HSQLParts.getVisitsDownloadsEntityFieldbyParameter(parameterName);

				Joiner joiner = Joiner.on(" OR ");
				List<String> whereValues = new ArrayList<>();
				for (String value : values) {
					String where = fieldClause + " = '" + value + "'";
					whereValues.add(where);
				}

				String where = joiner.join(whereValues);
				hsqlParts.addToWhereClause("( " + where + " )");

				groupByField = fieldClause;

			} else {
				fieldClause = fieldsPrefix + HSQLParts.getEntityFieldbyParameter(parameterName);

				Joiner joiner = Joiner.on(" OR ");
				List<String> whereValues = new ArrayList<>();
				for (String value : values) {

					String where = fieldClause;

					if (parameterName.equals(ParameterNames.MLANGUAGE)
						|| parameterName.equals(ParameterNames.OLANGUAGE)) {
						where += " LIKE '%" + value + "%'";
					} else {
						where += " = '" + value + "'";
					}

					whereValues.add(where);
				}

				String where = joiner.join(whereValues);
				hsqlParts.addToWhereClause("( " + where + " )");

					groupByField = fieldClause;
			}
		} else if (type.equals(ValueTypes.VALUE_NOT)) {

			List<String> values = parameterProperties.getStrValueList();

			//Eliminate ! from all values
			if (entityProperties.getDescription().equals(EntityTablesProperties.CUBE_ENTRY_DOCS.getDescription())) {

				Joiner joiner = Joiner.on(", ");

				List<String> fieldsList = new ArrayList<>();

				for (DocumentTypeEnum enumValue : DocumentTypeEnum.getSingleValues()) {
					String otherValue = enumValue.name();

					if (!values.contains(("!" + otherValue)) && !fieldsList.contains(fieldClause + otherValue)) {
						fieldsList.add(fieldClause + otherValue);
					}
				}

				/* // Iterate document-type valid values
				 for(String strValue : values){
					
				 String realValue = strValue.replaceAll("!", "");
					
				 for ( DocumentTypeEnum enumValue : DocumentTypeEnum.getSingleValues() ){
				 String otherValue = enumValue.name();
				 if(!(otherValue.equals(realValue)) && !fieldsList.contains(fieldClause+otherValue)){
				 fieldsList.add(fieldClause+otherValue);
				 }
				 }
				 }
				 */
				if (!fieldsList.isEmpty()) {
					fieldClause = joiner.join(fieldsList);
				}
			} else {

				fieldClause = fieldsPrefix + HSQLParts.getEntityFieldbyParameter(parameterName);
				String where = "";

				if (values.size() == 1) {
					String value = values.get(0).replaceAll("!", "");
					where = fieldClause + " != '" + value + "'";
				} else {
					Joiner joiner = Joiner.on(" " + parameterProperties.getValueOperator().name() + " ");

					List<String> whereValues = new ArrayList<>();
					for (String value : values) {
						whereValues.add(fieldClause + " != '" + value.replaceAll("!", "") + "'");
					}

					where = joiner.join(whereValues);
				}

				hsqlParts.addToWhereClause(where);
			}

			groupByField = fieldClause;
		}
		if (!((HSQLParts.isGeneralCube(entityProperties) || entityProperties.equals(EntityTablesProperties.CUBE_ENTRY_WORKEXP) || entityProperties.equals(EntityTablesProperties.CUBE_ENTRY_AGE))
			&& type.equals(ValueTypes.VALUE_OR))) {
			hsqlParts.addToFieldClause(fieldClause);
		}
		if (!Strings.isNullOrEmpty(groupByField) && !hsqlParts.getGroupByList().contains(groupByField)) {
			if (!((HSQLParts.isGeneralCube(entityProperties) || entityProperties.equals(EntityTablesProperties.CUBE_ENTRY_WORKEXP) || entityProperties.equals(EntityTablesProperties.CUBE_ENTRY_AGE))
			&& type.equals(ValueTypes.VALUE_OR))) {
				hsqlParts.addToGroupBy(groupByField);
			}
		}
	}

	public EntityTablesProperties getEntityProperties() {
		return entityProperties;
	}

	public void setEntityProperties(EntityTablesProperties entityProperties) {
		this.entityProperties = entityProperties;
	}
}
