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
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

import europass.ewa.services.statistics.enums.request.EntitiesFieldsNames;
import europass.ewa.services.statistics.enums.request.ParameterNames;
import europass.ewa.services.statistics.enums.request.ValueOperators;
import europass.ewa.services.statistics.enums.tables.EntityTablesFieldNames;
import europass.ewa.services.statistics.enums.tables.EntityTablesProperties;

/**
 * HSQLParts
 * Produces the hsql query String by constructing and assembling the parts:
 * - SELECT
 * - FROM
 * - WHERE
 * - GROUPBY (optional)
 * - ORDERBY (optional)
 *
 * @author pgia
 *
 */
public class HSQLParts {

	public static final String SELECT = "select ";
	public static final String FROM = " from ";
	public static final String WHERE = " where ";
	public static final String GROUPBY = " group by ";
	public static final String ORDERBY = " order by ";

	private static int QUERY_PARAMETERS_NUMBER;

	protected EntityTablesProperties entityProperties;
	private String singleEntityFrom;
	private String countField;
	private String countDistinctField;

	private int top;

	private List<String> headersList;

	private List<String> fieldsPartsList;
	private List<String> fromPartsList;
	private List<String> wherePartsList;
	private List<String> groupByList;
	private List<String> orderByList;
	private List<String> orderList;
	
	private boolean isVisitsOrDownloadsEntity;

	public HSQLParts() {
		QUERY_PARAMETERS_NUMBER = 0;
		this.init();
	}

	public HSQLParts(EntityTablesProperties entityProperties, int queryParametersNumber) {

		QUERY_PARAMETERS_NUMBER = queryParametersNumber;
		this.init();

		// Adding: from <entity name> e
		if (isVisitsTable(entityProperties) || isDownloadsTable(entityProperties)) {
			this.setCountField(EntityTablesFieldNames.VOLUME.getDescription());
			addOrder("SUM(e." + EntityTablesFieldNames.VOLUME.getDescription() + ")", "DESC");
			QUERY_PARAMETERS_NUMBER ++;
			
			isVisitsOrDownloadsEntity = true;
			
		} else if (isEmailHashTable(entityProperties)) {
			
			this.setCountDistinctField(EntityTablesFieldNames.EMAIL_HASH_CODE.getDescription());
			
		} else if (isGeneralCube(entityProperties)) {
			this.setCountField(EntityTablesFieldNames.REC_COUNT.getDescription());

			// Also add the rec_count field to hsqlOrderList by DESC
			//addOrder("SUM(e." + EntityTablesFieldNames.REC_COUNT.getDescription() + ")", "DESC");

			// If it is the CUBE_ENTRY_NAT_RANK entity, add rank to order and group by clause
			if (entityProperties.equals(EntityTablesProperties.CUBE_ENTRY_NAT_RANK)) {
				addOrder(EntityTablesFieldNames.RANK_NO.getDescription(), "DESC");
				groupByList.add(EntityTablesFieldNames.RANK_NO.getDescription());
			}

			//Also add to group by clause
//			groupByList.add(EntityTablesFieldNames.REC_COUNT.getDescription());
//			orderByList.add(EntityTablesFieldNames.REC_COUNT.getDescription());
		}

		this.setSingleEntityFrom(entityProperties.getDescription() + " e");

	}

	protected void init() {
		fieldsPartsList = new ArrayList<>();
		fromPartsList = new ArrayList<>();
		wherePartsList = new ArrayList<>();
		groupByList = new ArrayList<>();
		orderByList = new ArrayList<>();
		orderList = new ArrayList<>();
		headersList = new LinkedList<>();
		top = 0;
	}

	public String getSingleEntityFrom() {
		return singleEntityFrom;
	}

	public void setSingleEntityFrom(String singleEntityFrom) {
		this.singleEntityFrom = singleEntityFrom;
	}

	public List<String> getFieldsPartsList() {
		return fieldsPartsList;
	}

	public void setFieldsPartsList(List<String> fieldsPartsList) {
		this.fieldsPartsList = fieldsPartsList;
	}

	public List<String> getFromPartsList() {
		return fromPartsList;
	}

	public void setFromPartsList(List<String> fromPartsList) {
		this.fromPartsList = fromPartsList;
	}

	public List<String> getWherePartsList() {
		return wherePartsList;
	}

	public void setWherePartsList(List<String> wherePartsList) {
		this.wherePartsList = wherePartsList;
	}

	public void addToFieldClause(String field) {
		fieldsPartsList.add(field);
	}

	public void addToFromClause(String field) {
		fromPartsList.add(field);
	}

	public void addToWhereClause(String field) {
		wherePartsList.add(field);
	}

	public void addToGroupBy(String field) {
		groupByList.add(field);
	}

	public String getCountField() {
		return countField;
	}

	public void setCountField(String countField) {
		this.countField = countField;
	}

	public String getCountDistinctField() {
		return countDistinctField;
	}

	public void setCountDistinctField(String countDistinctField) {
		this.countDistinctField = countDistinctField;
	}

	public List<String> getGroupByList() {
		return groupByList;
	}

	public void setGroupByList(List<String> groupByList) {
		this.groupByList = groupByList;
	}

	public List<String> getOrderByList() {
		return orderByList;
	}

	public void setOrderByList(List<String> orderByList) {
		this.orderByList = orderByList;
	}
	
	public List<String> getOrderList() {
		return orderList;
	}

	public void setOrderList(List<String> order) {
		this.orderList = order;
	}

	public void addOrder(String orderBy, String order) {
		orderByList.add(orderBy);
		orderList.add(order);
	}

	public void addOrder(int index, String orderBy, String order) {
		orderByList.add(index, orderBy);
		orderList.add(index, order);
	}

	public void removeOrder(String orderBy) {
		int orderingIndex = orderByList.indexOf(orderBy);
		orderByList.remove(orderingIndex);
		orderList.remove(orderingIndex);
	}

	public List<String> getHeaders() {
		return headersList;
	}

	public void setHeaders(List<String> headers) {
		this.headersList = headers;
	}

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}

	/**
	 * Construction and assembling of the HSQL parts to form the query String
	 *
	 * @return String the query
	 */
	public String getHSQLQuery() {

		Joiner commaJoiner = Joiner.on(", ");
		Joiner andJoiner = Joiner.on(" " + ValueOperators.AND.name() + " ");
		String fields = (Strings.isNullOrEmpty(countField)
			? Strings.isNullOrEmpty(countDistinctField) ? "" : "COUNT(DISTINCT e." + countDistinctField + "), "
			: "SUM(e." + countField + "), ") + commaJoiner.join(fieldsPartsList);
		String[] fieldsArray = fields.split(", ");

		// Ensure the fields order, so we use a linked list for the header names
		// Also use another linked list for the order by case of top
		List<String> topOrderList = new LinkedList<>();

		// Also fill the order by list (if required) for the order by case
		
		// Order by handling
		boolean orderByDate = orderByList.contains("e." + ParameterNames.DATE.getDescription());
		String dateOrder = null;

		for (int i = 0; i < fieldsArray.length; i++) {
			String field = fieldsArray[i];

			/* if(i ==0 && fieldsArray[0].startsWith("SUM(")){
			 headersList.add(0, "Records Number");
			 }*/
			if (field.equals("COUNT(DISTINCT e.email_hash_code)")) {
				
				headersList.add("rec_count");
				
			} else if (field.startsWith("ISNULL(SUM(")) {

				String[] sumFieldsArray = field.split(", ");

				for (String sumField : sumFieldsArray) {
					headersList.add(sumField.replaceAll("ISNULL\\(SUM\\(e\\.", "").replaceAll("\\),0\\)",""));
				}
			} else if (field.startsWith("SUM(")) {

				String[] sumFieldsArray = field.split(",");

				for (String sumField : sumFieldsArray) {
					headersList.add(sumField.replaceAll("SUM\\(e\\.([a-zA-Z]+((-|_)[a-zA-Z]+)?)\\)", "$1"));
				}
			} else {
				headersList.add(field.replaceAll("^.*\\.", ""));

				// Check if rec_count field already exists in orderby list  
/*				if(field.equals("SUM(e."+EntityTablesFieldNames.REC_COUNT.getDescription()+")")){
				 if(!orderByList.contains("SUM(e."+EntityTablesFieldNames.REC_COUNT.getDescription()+")"))
				 topOrderList.add(field+" "+(!Strings.isNullOrEmpty(order) ? order : "DESC"));
				 }else{
				 topOrderList.add(field+" "+(!Strings.isNullOrEmpty(order) ? order : "DESC"));
				 }*/
			}

			//Order by handling
			//Check if field is inside the fields list
			if (orderByDate) {
				boolean isDateField = false;
				if (isVisitsOrDownloadsEntity) {
					isDateField = field.equals("e." + EntityTablesFieldNames.YEAR.getDescription())
						|| field.equals("e." + EntityTablesFieldNames.MONTH.getDescription());
				} else {
					isDateField = field.equals("e." + EntityTablesFieldNames.YEAR_NO.getDescription())
						|| field.equals("e." + EntityTablesFieldNames.MONTH_NO.getDescription())
						|| field.equals("e." + EntityTablesFieldNames.DAY_NO.getDescription());
				}
				if (isDateField) {
					if (orderByList.contains("e." + ParameterNames.DATE.getDescription())) {
						dateOrder = orderList.get(orderByList.indexOf("e." + ParameterNames.DATE.getDescription()));
						removeOrder("e." + ParameterNames.DATE.getDescription());
					}
					int index = !orderByList.isEmpty() ? orderByList.size() - 1 : orderByList.size();
					addOrder(index, field, dateOrder); //add before the last
				}
			}
		}

		String fromPart = (Strings.isNullOrEmpty(singleEntityFrom) ? commaJoiner.join(fromPartsList) : singleEntityFrom);

		String fieldsPrefix = "e.";
		String isoNatAlias = " n";
		String isoNatPrefix = isoNatAlias + ".";
		String isoCountryAlias = " c";
		String isoCountryPrefix = isoCountryAlias + ".";

		if (fieldsPartsList.contains(fieldsPrefix + EntitiesFieldsNames.NATIONALITY.getDescription())) {
			fromPart += (", " + EntityTablesProperties.ISO_NATIONALITY.getDescription() + isoNatAlias);
			
			wherePartsList.add(isoNatPrefix + EntitiesFieldsNames.ISO_COUNTRY_CODE.getDescription() 
				+ " = " 
				+ fieldsPrefix + EntitiesFieldsNames.NATIONALITY.getDescription());
		}
		
		if (fieldsPartsList.contains(fieldsPrefix + EntitiesFieldsNames.ADDRESS_COUNTRY.getDescription())) {
			fromPart += (", " + EntityTablesProperties.ISO_COUNTRY.getDescription() + isoCountryAlias);
			
			wherePartsList.add(isoCountryPrefix + EntitiesFieldsNames.ISO_COUNTRY_CODE.getDescription() 
				+ " = " 
				+ fieldsPrefix + EntitiesFieldsNames.ADDRESS_COUNTRY.getDescription());
		}

		String grouby = "";
		if (!groupByList.isEmpty() && QUERY_PARAMETERS_NUMBER >= 1) {
			grouby = GROUPBY + commaJoiner.join(groupByList);
		}
		
		StringBuilder sb = new StringBuilder(SELECT + fields + FROM + fromPart + (!wherePartsList.isEmpty() ? WHERE + andJoiner.join(wherePartsList) : "") + grouby);

		// Order by list not empty			
		if (!orderByList.isEmpty() && !orderList.isEmpty()) {
			// set order to DESC or ASC
			List hsqlOrderList = new ArrayList();
			for (int i = 0; i < orderByList.size(); i++) {
				String orderByField = orderByList.get(i);
				String order = orderList.get(i);
				if (!orderByField.equals("e." + ParameterNames.DATE.getDescription())) { // case when orderby=date exists but year, month are not in the select or groupby clause
					hsqlOrderList.add(orderByField + " " + order);
				}
			}
			if (!hsqlOrderList.isEmpty()) {
				sb.append(ORDERBY);
				sb.append(commaJoiner.join(hsqlOrderList.toArray()));
			}

			if (top > 0 && topOrderList.size() > 0) {
				sb.append(", " + commaJoiner.join(topOrderList));
			}

		}

		return sb.toString();
	}

	public static String getVisitsDownloadsEntityFieldbyParameter(ParameterNames parameter) {

		if (parameter.equals(ParameterNames.COUNTRY)) {
			return EntitiesFieldsNames.COUNTRY_CODE.getDescription();
		}
		if (parameter.equals(ParameterNames.LANGUAGE)) {
			return EntitiesFieldsNames.LANGUAGE_CODE.getDescription();
		}
		if (parameter.equals(ParameterNames.DOCUMENT_FORMAT)) {
			return EntitiesFieldsNames.DOCUMENT_FORMAT.getDescription();
		}
		if (parameter.equals(ParameterNames.DOCUMENT)) {
			return EntitiesFieldsNames.DOCUMENT.getDescription();
		}
		
		return EntitiesFieldsNames.INVALID.getDescription();
	}
	
	public static String getEntityFieldbyParameter(ParameterNames parameter) {

		// STRING VALUED
		if (parameter.equals(ParameterNames.DOCUMENT_TYPE)) {
			return EntitiesFieldsNames.DOC_TYPE.getDescription();
		}

//		TODO: waiting for views
//		if(parameter.equals(ParameterNames.DOCUMENT_FORMAT))
//			return EntitiesFieldsNames.DOC_TYPE.getDescription();
//		if(parameter.equals(ParameterNames.EXAMPLES_FORMAT))
//			return EntitiesFieldsNames.DOC_TYPE.getDescription();
		if (parameter.equals(ParameterNames.COUNTRY)) {
			return EntitiesFieldsNames.ADDRESS_COUNTRY.getDescription();
		}
		if (parameter.equals(ParameterNames.LANGUAGE)) {
			return EntitiesFieldsNames.DOC_LANG.getDescription();
		}
		if (parameter.equals(ParameterNames.MLANGUAGE)) {
			return EntitiesFieldsNames.MLANGUAGE.getDescription();
		}
		if (parameter.equals(ParameterNames.OLANGUAGE)) {
			return EntitiesFieldsNames.OLANGUAGE.getDescription();
		}
		if (parameter.equals(ParameterNames.NATIONALITY)) {
			return EntitiesFieldsNames.NATIONALITY.getDescription();
		}
		if (parameter.equals(ParameterNames.ONATIONALITY)) {
			return EntitiesFieldsNames.NATIONALITY.getDescription();
		}

		// NUMBER VALUED
		if (parameter.equals(ParameterNames.AGE)) {
			return EntitiesFieldsNames.AGE.getDescription();
		}
		if (parameter.equals(ParameterNames.GENDER)) {
			return EntitiesFieldsNames.GENDER.getDescription();
		}
		if (parameter.equals(ParameterNames.WORK_EXPERIENCE)) {
			return EntitiesFieldsNames.WORK_YEARS.getDescription();
		}
		if (parameter.equals(ParameterNames.EDUCATION_YEARS)) {
			return EntitiesFieldsNames.EDUC_YEARS.getDescription();
		}
		
		return EntitiesFieldsNames.INVALID.getDescription();
	}

	public static boolean isGeneralCube(EntityTablesProperties entity) {

		if (entity.equals(EntityTablesProperties.CUBE_ENTRY_LANGS)
			|| entity.equals(EntityTablesProperties.CUBE_ENTRY_MLANG)
			|| entity.equals(EntityTablesProperties.CUBE_ENTRY_FLANG)
			|| entity.equals(EntityTablesProperties.CUBE_ENTRY)
			|| entity.equals(EntityTablesProperties.CUBE_ENTRY_SHORT)
			|| entity.equals(EntityTablesProperties.CUBE_ENTRY_NAT)
			|| entity.equals(EntityTablesProperties.CUBE_ENTRY_NAT_LANGS)
			|| entity.equals(EntityTablesProperties.CUBE_ENTRY_NAT_MLANG)
			|| entity.equals(EntityTablesProperties.CUBE_ENTRY_NAT_FLANG)
			|| entity.equals(EntityTablesProperties.CUBE_ENTRY_NAT_RANK)) {
			return true;
		}

		return false;
	}
	
	public static boolean isEmailHashTable(EntityTablesProperties entity) {
		return entity.equals(EntityTablesProperties.CUBE_ENTRY_EMAIL_HASH);
	}
	
	public static boolean isVisitsTable(EntityTablesProperties entity) {
		return entity.equals(EntityTablesProperties.STAT_VISITS);
	}
	
	public static boolean isDownloadsTable(EntityTablesProperties entity) {
		return entity.equals(EntityTablesProperties.STAT_DOWNLOADS);
	}

	public void addDefaultOrderConditionally(EntityTablesProperties entityProperties) {
		if (isGeneralCube(entityProperties)) {
			// Add the rec_count field to hsqlOrderList by DESC
			addOrder("SUM(e." + EntityTablesFieldNames.REC_COUNT.getDescription() + ")", "DESC");
		}
	}
}
