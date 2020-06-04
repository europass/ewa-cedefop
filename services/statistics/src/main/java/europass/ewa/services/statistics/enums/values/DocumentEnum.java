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
package europass.ewa.services.statistics.enums.values;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 *
 * @author at
 */
public enum DocumentEnum {

	INVALID("invalid-document-value"),
	EM("EM"),
	ELP("ELP"),
	EDS("EDS"),
	ECV("ECV"),
	ECS("ECS"),
	
	ALL_TYPES_OR("(" + EM.getDescription()
		+ "+" + ELP.getDescription()
		+ "+" + EDS.getDescription()
		+ "+" + ECV.getDescription()
		+ "+" + ECS.getDescription()
		+ ")"),
	VALUE_OR(ALL_TYPES_OR.getDescription() + "(\\+" + ALL_TYPES_OR.getDescription() + ")+"),
	VALUE_AND(ALL_TYPES_OR.getDescription() + "(," + ALL_TYPES_OR.getDescription() + ")+");

	private String description;

	DocumentEnum(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return description;
	}

	public String getDescription() {
		return description;
	}

	public static EnumSet<DocumentEnum> getSet() {
		return EnumSet.allOf(DocumentEnum.class);
	}

	public static DocumentEnum match(String str) {
		if (str == null) {
			return INVALID;
		}

		for (DocumentEnum param : values()) {

			if (str.equals(param.description) || str.matches(param.description)) {
				return param;
			}
		}
		return INVALID;
	}

	public static List<DocumentEnum> getSingleValues() {

		List<DocumentEnum> list = new ArrayList<>();
		list.add(DocumentEnum.EM);
		list.add(DocumentEnum.ELP);
		list.add(DocumentEnum.EDS);
		list.add(DocumentEnum.ECV);
		list.add(DocumentEnum.ECS);

		return list;
	}
}
