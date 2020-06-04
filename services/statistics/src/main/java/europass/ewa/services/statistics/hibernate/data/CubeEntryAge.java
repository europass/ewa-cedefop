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
@Table(name=ServicesStatisticsConstants.CUBE_ENTRY_AGE)
public class CubeEntryAge implements EntryPivot, Serializable{

	private static final long serialVersionUID = 6012317349008037139L;
	
	int total;
	int year_no;
	int month_no;
	int day_no;
	String doc_type;
	String doc_lang;
	String address_country;
	
	int upto20;
	int from21to25;
	int from26to30;
	int from31to35;
	int from35plus;
	
	
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	@Column(name="TOTAL")
	public int getTotal() {	return total; }
	public void setTotal( int total ) { this.total = total; }

	@Id
	@Column(name="year_no")
	public int getYear_no() { return year_no; }
	public void setYear_no(int year_no) {	this.year_no = year_no; }
	
	@Id
	@Column(name="month_no")
	public int getMonth_no() {	return month_no; }
	public void setMonth_no(int month_no) { this.month_no = month_no; }
	
	@Id
	@Column(name="day_no")
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
	
	@Column(name="upto20")
	public int getUpto20() { return upto20;	}
	public void setUpto20(int upto20) {	this.upto20 = upto20; }
	
	@Column(name="from21to25")
	public int getFrom21to25() { return from21to25;	}
	public void setFrom21to25(int from21to25) {	this.from21to25 = from21to25; }
	
	@Column(name="from26to30")
	public int getFrom26to30() { return from26to30; }
	public void setFrom26to30(int from26to30) {	this.from26to30 = from26to30; }
	
	@Column(name="from31to35")
	public int getFrom31to35() { return from31to35; }
	public void setFrom31to35(int from31to35) {	this.from31to35 = from31to35; }
	
	@Column(name="from35plus")
	public int getFrom35plus() { return from35plus; }
	public void setFrom35plus(int from35plus) {	this.from35plus = from35plus; }

	@Transient
	public static Map<String, NumberValueRange> getRangesList() {

		Map<String, NumberValueRange> list = new HashMap<>();
		list.put("upto20", new NumberValueRange(0, 20));
		list.put("from21to25", new NumberValueRange(21, 25));
		list.put("from26to30", new NumberValueRange(26, 30));
		list.put("from31to35", new NumberValueRange(31, 35));
		list.put("from35plus", new NumberValueRange(36, 100));
		return list;
	}
	
	@Transient
	public static List<NumberValueRange> getRangesListValues() {
		return new ArrayList<>(getRangesList().values());
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
