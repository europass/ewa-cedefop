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
import europass.ewa.services.statistics.enums.request.QueryPrefixes;
import europass.ewa.services.statistics.enums.request.ValueTypes;
import europass.ewa.services.statistics.enums.tables.EntityTablesProperties;
import europass.ewa.services.statistics.structures.QueryEntityMatch;
import europass.ewa.services.statistics.structures.QueryParameter;
import europass.ewa.services.statistics.structures.QueryProperties;
import europass.ewa.services.statistics.structures.ValuePropertiesBuilder;

public class CubesQueryEntityMatcher extends QueryEntityMatcher {

	private List<ParameterNames> parametersContained;

	public CubesQueryEntityMatcher(QueryEntityMatcher next) {
		super(next);
	}

	@Override
	protected void configure() {

		parametersContained = new ArrayList<>();

		List<ParameterNames> parametersList = new ArrayList<>();
		parametersList.add(ParameterNames.DOCUMENT_TYPE);
		parametersList.add(ParameterNames.DATE);
		parametersList.add(ParameterNames.LANGUAGE);
		parametersList.add(ParameterNames.MLANGUAGE);
		parametersList.add(ParameterNames.OLANGUAGE);
		parametersList.add(ParameterNames.COUNTRY);
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

		//Matching general cube entities according to parameters - default CUBE_ENTRY
		EntityTablesProperties matchingEntity = EntityTablesProperties.CUBE_ENTRY;

		// In any case of DOCUMENT_TYPE or DATE parameter initially match CUBE_ENTRY_DOCS
		if (parametersContained.contains(ParameterNames.DOCUMENT_TYPE) || parametersContained.contains(ParameterNames.DATE)) {
			matchingEntity = EntityTablesProperties.CUBE_ENTRY_DOCS;
		}

		// In any case of DOCUMENT_TYPE and GENDER parameter initially match CUBE_ENTRY_GENDER
		if (parametersContained.contains(ParameterNames.DOCUMENT_TYPE) && parametersContained.contains(ParameterNames.GENDER)) {
			matchingEntity = EntityTablesProperties.CUBE_ENTRY_GENDER;
		}

		// In any case of LANGUAGE initially match CUBE_ENTRY_DOCS_LANGS. If GENDER then match CUBE_ENTRY
		if (parametersContained.contains(ParameterNames.LANGUAGE)) {
			if (parametersContained.contains(ParameterNames.GENDER)) {
				matchingEntity = EntityTablesProperties.CUBE_ENTRY;
			} else {
				matchingEntity = EntityTablesProperties.CUBE_ENTRY_DOCS_LANGS;

				if (!parametersContained.contains(ParameterNames.DOCUMENT_TYPE)) {
					QueryParameter docTypeParameter = new QueryParameter(ParameterNames.DOCUMENT_TYPE, QueryPrefixes.GENERATED);
					docTypeParameter.setValueProperties(new ValuePropertiesBuilder().withValue("").withValueType(ValueTypes.VALUE).build());
					queryProperties.getParameterList().add(docTypeParameter);
				}

			}
		}

		if (parametersContained.contains(ParameterNames.COUNTRY)) {
			matchingEntity = EntityTablesProperties.CUBE_ENTRY;
		}

		if (parametersContained.contains(ParameterNames.MLANGUAGE)) {
			matchingEntity = EntityTablesProperties.CUBE_ENTRY_MLANG;
		}
		
		if (parametersContained.contains(ParameterNames.OLANGUAGE)) {
			matchingEntity = EntityTablesProperties.CUBE_ENTRY_FLANG;
		}
		
		if (parametersContained.contains(ParameterNames.MLANGUAGE) && parametersContained.contains(ParameterNames.OLANGUAGE)) {
			matchingEntity = EntityTablesProperties.CUBE_ENTRY_LANGS;
		}

		if (parametersContained.contains(ParameterNames.AGE) || parametersContained.contains(ParameterNames.WORK_EXPERIENCE) || parametersContained.contains(ParameterNames.EDUCATION_YEARS)) {
			if (matchingEntity.equals(EntityTablesProperties.CUBE_ENTRY_DOCS_LANGS)) {
				matchingEntity = EntityTablesProperties.CUBE_ENTRY_LANGS;
			} else if (matchingEntity.equals(EntityTablesProperties.CUBE_ENTRY_DOCS) || matchingEntity.equals(EntityTablesProperties.CUBE_ENTRY_GENDER)) {
				matchingEntity = EntityTablesProperties.CUBE_ENTRY;
			}
		}

		// In any case of ONATIONALITY or ONATIONALITY parameter initially match CUBE_ENTRY_NAT_LANGS
		if (parametersContained.contains(ParameterNames.NATIONALITY) || parametersContained.contains(ParameterNames.ONATIONALITY)) {
			if (parametersContained.contains(ParameterNames.MLANGUAGE) && parametersContained.contains(ParameterNames.OLANGUAGE)) {
				matchingEntity = EntityTablesProperties.CUBE_ENTRY_NAT_LANGS;
			} else if (parametersContained.contains(ParameterNames.MLANGUAGE)) {
				matchingEntity = EntityTablesProperties.CUBE_ENTRY_NAT_MLANG;
			} else if (parametersContained.contains(ParameterNames.OLANGUAGE)) {
				matchingEntity = EntityTablesProperties.CUBE_ENTRY_NAT_FLANG;
			} else {
				matchingEntity = EntityTablesProperties.CUBE_ENTRY_NAT;
			}
		}

		matcher.setEntityProperties(matchingEntity);
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
