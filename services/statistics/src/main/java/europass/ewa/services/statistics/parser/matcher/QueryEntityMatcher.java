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
import java.util.Collection;
import java.util.List;

import europass.ewa.services.statistics.enums.request.ParameterNames;
import europass.ewa.services.statistics.enums.tables.EntityTablesProperties;
import europass.ewa.services.statistics.structures.NumberValueRange;
import europass.ewa.services.statistics.structures.QueryEntityMatch;
import europass.ewa.services.statistics.structures.QueryParameter;
import europass.ewa.services.statistics.structures.QueryProperties;

public abstract class QueryEntityMatcher {

	private QueryEntityMatch match;
	private QueryEntityMatcher next;
	
	public QueryEntityMatcher(QueryEntityMatcher next){
		this.setNext(next);
		configure();
	}
	
	protected abstract void configure();

	protected abstract void populateQueryEntity(QueryEntityMatch matcher, QueryProperties queryProperties);

	public void dispatch(QueryEntityMatch matcher, QueryProperties queryProperties) {

		if (matchesFieldsSelectList(matcher.getParameters(), match.getParameters())) {

			boolean matchCubesEntity = false;
			for (QueryParameter parameter : queryProperties.getParameterList()) {
				if (parameter.getParameterName().equals(ParameterNames.OLANGUAGE) && !parameter.getValueProperties().isEmpty()) {
					matchCubesEntity = true;
					break;
				}
			}

			if (matchCubesEntity && this.next != null) {
				this.next.dispatch(matcher, queryProperties);
			} else {
				populateQueryEntity(matcher, queryProperties);
			}
		} else if (this.next != null) {
			this.next.dispatch(matcher, queryProperties);
		} else {
			matcher.setEntityProperties(EntityTablesProperties.CUBE_ENTRY);
		}
	}

	@SuppressWarnings("unchecked")
	private boolean matchesFieldsSelectList(List<?> list1, List<?> list2) {

		List<?> compareList = list2;

		if (list1.contains(ParameterNames.GROUP_BY) || list1.contains(ParameterNames.TOP) || list1.contains(ParameterNames.ORDER_BY)) {

			// list1, the parameters given by the user (may contain groupby and top)
			List<ParameterNames> checkedParams = new ArrayList<>();

			if (list1.contains(ParameterNames.TOP)) {
				checkedParams.add(ParameterNames.TOP);
			}
			if (list1.contains(ParameterNames.GROUP_BY)) {
				checkedParams.add(ParameterNames.GROUP_BY);
			}
			if (list1.contains(ParameterNames.ORDER_BY)) {
				checkedParams.add(ParameterNames.ORDER_BY);
			}

			checkedParams.addAll((Collection<? extends ParameterNames>) list2);

			compareList = checkedParams;
		}

		if (list1.size() == compareList.size()) {
			if (list1.containsAll(compareList) && compareList.containsAll(list1)) {
				return true;
			}
		}
		return false;
	}

	protected boolean listContainsAllFromSublist(List<NumberValueRange> sublist, List<NumberValueRange> list) {

		boolean contained = false;

		for (NumberValueRange subValue : sublist) {

			contained = false;

			for (NumberValueRange value : list) {

				int subFrom = subValue.getFrom();
				int subTo = subValue.getTo();

				if (subFrom == value.getFrom() && subTo == value.getTo()) {
					contained = true;
					break;
				}
			}

			if (!contained) {
				break;
			}
		}

		return contained;
	}
	
	public QueryEntityMatch getMatch() {
		return match;
	}

	public void setMatch(QueryEntityMatch match) {
		this.match = match;
	}

	public QueryEntityMatcher getNext() {
		return next;
	}

	public void setNext(QueryEntityMatcher next) {
		this.next = next;
	}
	
	
}
