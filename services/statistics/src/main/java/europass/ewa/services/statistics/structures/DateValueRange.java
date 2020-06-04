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

public class DateValueRange {
	private DateTime from;
	private DateTime to;
	boolean isSingle;
	boolean isValid;
	
	public DateValueRange(DateTime from, DateTime to){
		this.from = from;
		this.to = to;
		this.isSingle = false;
		this.isValid = true;
	}

	public DateTime getFrom() {
		return from;
	}

	public void setFrom(DateTime from) {
		this.from = from;
	}

	public DateTime getTo() {
		return to;
	}

	public void setTo(DateTime to) {
		this.to = to;
	}
	
	public boolean isSingle(){
		return isSingle;
	}

	public void setSingle(boolean single){
		isSingle = single;
	}

	public boolean isValid(){
		return ( isValid || isRangeValid() );
	}

	public void setValid(boolean valid){
		isValid = valid;
	}
	
	public static boolean isSingleDateValueList(List<DateValueRange> dateList){
		
		for(DateValueRange d : dateList){
			if(!(d.isSingle))
				return false;
		}
		
		return true;
	}
	
	boolean isRangeValid(){
		return ( from != null && to != null && ( to.isBeforeNow() && to.isAfter(from) ));
	}
	
	public DateTime[] toArray(){
		DateTime[] array = {this.from,this.to};
		return array;
	}
}
