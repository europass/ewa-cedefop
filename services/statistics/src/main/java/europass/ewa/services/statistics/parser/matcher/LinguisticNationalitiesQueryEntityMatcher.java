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

import java.util.ArrayList;
import java.util.List;

import europass.ewa.services.statistics.enums.request.ParameterNames;
import europass.ewa.services.statistics.enums.tables.EntityTablesProperties;
import europass.ewa.services.statistics.structures.QueryEntityMatch;
import europass.ewa.services.statistics.structures.QueryProperties;

public class LinguisticNationalitiesQueryEntityMatcher extends QueryEntityMatcher {

	private List<ParameterNames> parametersContained;

	public LinguisticNationalitiesQueryEntityMatcher(QueryEntityMatcher next) {
		super(next);
	}

	@Override
	protected void configure() {

		parametersContained = new ArrayList<ParameterNames>();

		List<ParameterNames> parametersList = new ArrayList<ParameterNames>();
		parametersList.add(ParameterNames.MLANGUAGE);
		parametersList.add(ParameterNames.OLANGUAGE);
		parametersList.add(ParameterNames.NATIONALITY);
		parametersList.add(ParameterNames.ONATIONALITY);
		parametersList.add(ParameterNames.GENDER);
		parametersList.add(ParameterNames.AGE);
		parametersList.add(ParameterNames.WORK_EXPERIENCE);
		parametersList.add(ParameterNames.EDUCATION_YEARS);
		super.setMatch(new QueryEntityMatch(EntityTablesProperties.CUBE_ENTRY, parametersList));
	}

	@Override
	public void dispatch(QueryEntityMatch matcher, QueryProperties queryProperties) {

		if (listContainsOneOf(matcher.getParameters(), this.getMatch().getParameters())) {
			this.populateQueryEntity(matcher, queryProperties);
		} else if (this.getNext() != null) {
			this.getNext().dispatch(matcher, queryProperties);
		} else {
			matcher.setEntityProperties(EntityTablesProperties.CUBE_ENTRY);
		}
	}

	@Override
	protected void populateQueryEntity(QueryEntityMatch matcher, QueryProperties queryProperties) {

		if (parametersContained.contains(ParameterNames.MLANGUAGE) 
			&& parametersContained.contains(ParameterNames.OLANGUAGE)
			&& (parametersContained.contains(ParameterNames.NATIONALITY) || parametersContained.contains(ParameterNames.ONATIONALITY))) {
			matcher.setEntityProperties(EntityTablesProperties.CUBE_ENTRY_NAT_LANGS);
		} else if (parametersContained.contains(ParameterNames.MLANGUAGE)
			&& (parametersContained.contains(ParameterNames.NATIONALITY) || parametersContained.contains(ParameterNames.ONATIONALITY))) {
			matcher.setEntityProperties(EntityTablesProperties.CUBE_ENTRY_NAT_MLANG);
		} else if (parametersContained.contains(ParameterNames.OLANGUAGE)
			&& (parametersContained.contains(ParameterNames.NATIONALITY) || parametersContained.contains(ParameterNames.ONATIONALITY))) {
			matcher.setEntityProperties(EntityTablesProperties.CUBE_ENTRY_NAT_FLANG);
		} else if (parametersContained.contains(ParameterNames.NATIONALITY) || parametersContained.contains(ParameterNames.ONATIONALITY)) {
			matcher.setEntityProperties(EntityTablesProperties.CUBE_ENTRY_NAT);
		} else if (parametersContained.contains(ParameterNames.MLANGUAGE) && parametersContained.contains(ParameterNames.OLANGUAGE)) {
			matcher.setEntityProperties(EntityTablesProperties.CUBE_ENTRY_LANGS);
		} else if (parametersContained.contains(ParameterNames.MLANGUAGE)) {
			matcher.setEntityProperties(EntityTablesProperties.CUBE_ENTRY_MLANG);
		} else if (parametersContained.contains(ParameterNames.OLANGUAGE)) {
			matcher.setEntityProperties(EntityTablesProperties.CUBE_ENTRY_FLANG);
		} else {
			matcher.setEntityProperties(EntityTablesProperties.CUBE_ENTRY);
		}
	}

	private boolean listContainsOneOf(List<ParameterNames> containerList, List<ParameterNames> subList) {

		boolean contained = false;

		for (ParameterNames subValue : subList) {
			if (containerList.contains(subValue)) {
				contained = true;
				parametersContained.add(subValue);
			}
		}

		return contained;
	}
}
