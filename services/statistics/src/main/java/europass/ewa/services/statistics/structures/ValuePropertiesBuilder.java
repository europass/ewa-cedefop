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

import europass.ewa.services.statistics.enums.request.ValueOperators;
import europass.ewa.services.statistics.enums.request.ValueTypes;
import europass.ewa.services.statistics.enums.values.DateValueDepthEnum;

public class ValuePropertiesBuilder {

	private ValueTypes valueType;
	private ValueOperators valueOperator;
	
	private String value;
	private Integer integerValue;
	private DateTime dateValue;
	
	private DateValueDepthEnum dateValueDepth;
	
	private List<String> strValueList;

	private List<Integer> integerValueList;	
	private List<DateTime> dateValueList;	
	
	private List<NumberValueRange> integerValueRangeList;	
	private List<DateValueRange> dateValueRangeList;
	
	// --- GETTERS ---	
	public ValueTypes getValueType() {
		return valueType;
	}

	public ValueOperators getValueOperator() {
		return valueOperator;
	}

	public String getValue() {
		return value;
	}

	public Integer getIntegerValue() {
		return integerValue;
	}
	
	public int getIntValue() {
		return getIntegerValue() != null ? getIntegerValue().intValue() : 0;
	}

	public DateTime getDateValue() {
		return dateValue;
	}
	
	public List<String> getStrValueList() {
		return strValueList;
	}

	public List<Integer> getIntegerValueList() {
		return integerValueList;
	}
	
	public List<DateTime> getDateValueList() {
		return dateValueList;
	}

	public List<NumberValueRange> getIntegerValueRangeList() {
		return integerValueRangeList;
	}
	
	public List<DateValueRange> getDateValueRangeList() {
		return dateValueRangeList;
	}
	
	public DateValueDepthEnum getDateValueDepth() {
		return dateValueDepth;
	}

	// --- withVALUE METHODS ---
	
	public ValuePropertiesBuilder withValueType(ValueTypes value){
		this.valueType = value;
		return this;
	}

	public ValuePropertiesBuilder withValueOperator(ValueOperators value){
		this.valueOperator = value;
		return this;
	}
	
	public ValuePropertiesBuilder withValue(String value){
		this.value = value;
		return this;
	}

	public ValuePropertiesBuilder withIntegerValue(Integer value){
		this.integerValue = value;
		return this;
	}

	public ValuePropertiesBuilder withDateValue(DateTime value) {
		this.dateValue = value;
		return this;
	}	

	public ValuePropertiesBuilder withDateValueDepth(DateValueDepthEnum value) {
		this.dateValueDepth = value;
		return this;
	}	
		
	public ValuePropertiesBuilder withStrValueList(List<String> value){
		this.strValueList = value;
		return this;
	}

	public ValuePropertiesBuilder withIntegerValueList(List<Integer> value){
		this.integerValueList = value;
		return this;
	}

	public ValuePropertiesBuilder withDateValueList(List<DateTime> value){
		this.dateValueList = value;
		return this;
	}
	
	public ValuePropertiesBuilder withIntegerValueRangeList(List<NumberValueRange> value){
		this.integerValueRangeList = value;
		return this;
	}
	
	public ValuePropertiesBuilder withDateValueRangeList(List<DateValueRange> value){
		this.dateValueRangeList = value;
		return this;
	}

	public ValueProperties build(){
		return new ValueProperties(this);
	}
}
