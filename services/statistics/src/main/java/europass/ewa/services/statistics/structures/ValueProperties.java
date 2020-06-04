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
package europass.ewa.services.statistics.structures;

import java.util.List;

import org.joda.time.DateTime;

import com.google.common.base.Strings;

import europass.ewa.services.statistics.enums.request.ValueOperators;
import europass.ewa.services.statistics.enums.request.ValueTypes;
import europass.ewa.services.statistics.enums.values.DateValueDepthEnum;

/**
 * ValueProperties
 * Builder that constructs the value type given
 * 
 * @author pgia
 *
 */
public class ValueProperties {

	private final ValueTypes valueType;
	private final ValueOperators valueOperator;
	
	// Used for one valued parameters
	private final String value;
	private final Integer integerValue;
	private final DateTime dateValue;
	
	//Used for determining the date value depth (YYYY, YYYY-MM, YYYY-MM-DD)
	private final DateValueDepthEnum dateValueDepth;
	
	// Used for multiple valued parameters along with operators
	private final List<String> strValueList;
	private final List<Integer> intValueList;	
	private final List<DateTime> dateValueList;	

	// Used for range valued parameters
	private final List<NumberValueRange> intValueRangeList;
	private final List<DateValueRange> dateValueRangeList;
	
	// Used to check if the value is empty 
	private final boolean empty;
	
	ValueProperties(ValuePropertiesBuilder builder){
		
		valueType = builder.getValueType();
		valueOperator = builder.getValueOperator();
		
		value = builder.getValue();
		integerValue = builder.getIntegerValue();
		dateValue = builder.getDateValue();
		
		strValueList = builder.getStrValueList();
		intValueList = builder.getIntegerValueList();
		dateValueList = builder.getDateValueList();
		
		intValueRangeList = builder.getIntegerValueRangeList();
		dateValueRangeList = builder.getDateValueRangeList();
		
		dateValueDepth = builder.getDateValueDepth();

		empty = (Strings.isNullOrEmpty(value) && integerValue == null && dateValue == null && 
				strValueList == null && intValueList == null && dateValueList == null && 
						intValueRangeList == null && dateValueRangeList == null ); 

	}

	public ValueTypes getValueType() {
		return valueType;
	}

	public ValueOperators getValueOperator(){
		return valueOperator;
	}
	
	public String getValue() {
		return value;
	}

	public Integer getIntegerValue(){
		return integerValue;
	}
	
	public int getIntValue(){
		return getIntegerValue() != null ? getIntegerValue() : 0;
	}	

	public DateTime getDateValue(){
		return dateValue;
	}	

	public List<String> getStrValueList(){
		return strValueList;
	}	
	
	public List<NumberValueRange> getIntValueRangeList(){
		return intValueRangeList;
	}

	public List<DateTime> getDateValueList() {
		return dateValueList;
	}

	public List<Integer> getIntValueList() {
		return intValueList;
	}

	public List<DateValueRange> getDateValueRangeList() {
		return dateValueRangeList;
	}

	public DateValueDepthEnum getDateValueDepth() {
		return dateValueDepth;
	}

	public boolean isEmpty() {
		return empty;
	}
}
