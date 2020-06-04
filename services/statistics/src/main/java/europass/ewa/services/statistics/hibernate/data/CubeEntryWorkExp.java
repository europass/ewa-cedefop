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
package europass.ewa.services.statistics.hibernate.data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import europass.ewa.services.statistics.constants.ServicesStatisticsConstants;
import europass.ewa.services.statistics.structures.NumberValueRange;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name=ServicesStatisticsConstants.CUBE_ENTRY_WORKEXP)
public class CubeEntryWorkExp implements EntryPivot, Serializable{

	private static final long serialVersionUID = 1L;

	int year_no;
	int month_no;
	int day_no;
	String doc_type;
	String doc_lang;
	String address_country;
	
	int none;
	int upto2;
	int from3to5;
	int from6to10;
	int from11to20;
	int from20plus;

	@Id
	@Column(name="year_no")
	@Override
	public int getYear_no() { return year_no; }
	public void setYear_no(int year_no) {	this.year_no = year_no; }
	
	@Id
	@Column(name="month_no")
	@Override
	public int getMonth_no() {	return month_no; }
	public void setMonth_no(int month_no) { this.month_no = month_no; }
	
	@Id
	@Column(name="day_no")
	@Override
	public int getDay_no() { return day_no; }
	public void setDay_no(int day_no) { this.day_no = day_no; }
	
	@Id
	@Column(name="doc_type")
	public String getDoc_type() { return doc_type; }
	public void setDoc_type(String doc_type) { this.doc_type = doc_type; }

	@Id
	@Column(name="doc_lang")
	public String getDoc_lang() { return doc_lang; }
	public void setDoc_lang(String doc_lang) { this.doc_lang = doc_lang; }

	@Id
	@Column(name="address_country")
	public String getAddress_country() { return address_country; }
	public void setAddress_country(String address_country) { this.address_country = address_country; }
	
	@Id
	@Column(name="none")
	public int getNone() { return none; }
	public void setNone(int none) { this.none = none; }
	
	@Id
	@Column(name="upto2")
	public int getUpto2() { return upto2; }
	public void setUpto2(int value) { this.upto2 = value; }

	@Id
	@Column(name="from3to5")
	public int getFrom3to5() { return from3to5; }
	public void setFrom3to5(int value) { this.from3to5 = value; }
		
	@Id
	@Column(name="from6to10")
	public int getFrom6to10() { return from6to10; }
	public void setFrom6to10(int value) { this.from6to10 = value; }
	
	@Id
	@Column(name="from11to20")
	public int getFrom11to20() { return from11to20; }
	public void setFrom11to20(int value) { this.from11to20 = value; }
	
	@Id
	@Column(name="from20plus")
	public int getFrom20plus() { return from20plus; }
	public void setFrom20plus(int value) { this.from20plus = value; }
	
	@Transient
	public static Map<String, NumberValueRange> getRangesList() {

		Map<String, NumberValueRange> list = new HashMap<>();
		list.put("none", new NumberValueRange(0, 0));
		list.put("upto2", new NumberValueRange(0, 2));
		list.put("from3to5", new NumberValueRange(3, 5));
		list.put("from6to10", new NumberValueRange(6, 10));
		list.put("from11to20", new NumberValueRange(11, 20));
		list.put("from20plus", new NumberValueRange(21, 100));
		return list;
	}
	
	@Transient
	public static List<NumberValueRange> getRangesListValues() {
		Map<String, NumberValueRange> map = new HashMap<>(getRangesList());
		map.remove("none");
		ArrayList<NumberValueRange> list = new ArrayList<>(map.values());
		return list;
	}
	
//	@Column(name="exact_date")
//	public Date getDate() { return exact_date; }
//	public void setDate(Date date) { this.exact_date = date; }
//	@PrePersist
//	protected void onCreate() { exact_date = new Date(); }
//	@PreUpdate
//	protected void onUpdate() {	exact_date = new Date(); }	
	
	
/*	@Transient
	public String printDetails(){
		
		return "\tDATE:"+this.getDay()+"/"+this.getMonth()+"/"+this.getYear()+
				"\tVOLUME:"+this.getVolume()+
				"\tISO_COUNTRY:"+this.getIso_country_code();
	}
*/	
}
