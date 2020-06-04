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
package europass.ewa.services.statistics.parser.matcher;

import europass.ewa.services.statistics.enums.request.ParameterNames;
import europass.ewa.services.statistics.enums.request.ValueTypes;
import europass.ewa.services.statistics.enums.tables.EntityTablesProperties;
import europass.ewa.services.statistics.hibernate.data.CubeEntryAge;
import europass.ewa.services.statistics.structures.NumberValueRange;
import europass.ewa.services.statistics.structures.QueryEntityMatch;
import europass.ewa.services.statistics.structures.QueryParameter;
import europass.ewa.services.statistics.structures.QueryProperties;
import europass.ewa.services.statistics.structures.ValueProperties;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *
 * @author at
 */
public class AgeQueryEntityMatcher extends QueryEntityMatcher {

	private List<NumberValueRange> ageCubeRangesList;

	public AgeQueryEntityMatcher(QueryEntityMatcher next) {
		super(next);
		configureValueRanges();
	}

	@Override
	protected void configure() {

		List<ParameterNames> parametersList = new ArrayList<>();
		parametersList.add(ParameterNames.AGE);
		parametersList.add(ParameterNames.DOCUMENT_TYPE);
		parametersList.add(ParameterNames.DATE);
		parametersList.add(ParameterNames.LANGUAGE);
		parametersList.add(ParameterNames.COUNTRY);

		super.setMatch(new QueryEntityMatch(EntityTablesProperties.CUBE_ENTRY_AGE, parametersList));
	}

	@Override
	public void dispatch(QueryEntityMatch matcher, QueryProperties queryProperties) {

		if (matcher.getParameters().contains(ParameterNames.AGE)
			&& matchesFieldsSelectList(matcher.getParameters(), this.getMatch().getParameters())) {
			this.populateQueryEntity(matcher, queryProperties);
		} else if (this.getNext() != null) {
			this.getNext().dispatch(matcher, queryProperties);
		} else {
			matcher.setEntityProperties(EntityTablesProperties.CUBE_ENTRY);
		}
	}

	/**
	 *
	 * @param queryParamsList, the parameters given by the user (may contain
	 * groupby and top)
	 * @param matcherParamsList
	 * @return
	 */
	private boolean matchesFieldsSelectList(List<ParameterNames> queryParamsList, List<ParameterNames> matcherParamsList) {

		List<ParameterNames> compareList = matcherParamsList;

		if (queryParamsList.contains(ParameterNames.GROUP_BY)
			|| queryParamsList.contains(ParameterNames.TOP)
			|| queryParamsList.contains(ParameterNames.ORDER_BY)) {

			List<ParameterNames> checkedParams = new ArrayList<>();

			if (queryParamsList.contains(ParameterNames.TOP)) {
				checkedParams.add(ParameterNames.TOP);
			}
			if (queryParamsList.contains(ParameterNames.GROUP_BY)) {
				checkedParams.add(ParameterNames.GROUP_BY);
			}
			if (queryParamsList.contains(ParameterNames.ORDER_BY)) {
				checkedParams.add(ParameterNames.ORDER_BY);
			}

			checkedParams.addAll((Collection<? extends ParameterNames>) matcherParamsList);

			compareList = checkedParams;
		}

		boolean matches = true;

		for (ParameterNames queryParam : queryParamsList) {
			if (!compareList.contains(queryParam)) {
				matches = false;
			}
		}

		return matches;
	}

	@Override
	protected void populateQueryEntity(QueryEntityMatch matcher, QueryProperties queryProperties) {

		QueryParameter ageParamater = queryProperties.getByParameterName(ParameterNames.AGE);
		ValueProperties valueProperties = ageParamater.getValueProperties();

		// In case of number range
		if (valueProperties.getValueType().equals(ValueTypes.NUMBER_RANGE)) {

			List<NumberValueRange> intValueRangeList = valueProperties.getIntValueRangeList();

			if (listContainsAllFromSublist(intValueRangeList, ageCubeRangesList)) {
				matcher.setEntityProperties(EntityTablesProperties.CUBE_ENTRY_AGE);
			} else {
				matcher.setEntityProperties(EntityTablesProperties.CUBE_ENTRY);
			}
		} else {
			matcher.setEntityProperties(EntityTablesProperties.CUBE_ENTRY);
		}
	}

	private void configureValueRanges() {
		ageCubeRangesList = CubeEntryAge.getRangesListValues();
	}

}
