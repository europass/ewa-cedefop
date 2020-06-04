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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import europass.ewa.services.statistics.constants.ServicesStatisticsConstants;

@Entity
@Table(name=ServicesStatisticsConstants.CUBE_ENTRY_FLANG_SHORT)
public class CubeEntryFlangShort implements EntryPivot, Serializable{

	private static final long serialVersionUID = 1L;

	int year_no;
	int month_no;
	int day_no;
	String doc_type;
	String f_lang;
	int male;
	int female;
	int other;
	int total;
	
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
	@Column(name="f_lang")
	public String getF_lang() { return f_lang; }
	public void setF_lang(String f_lang) { this.f_lang = f_lang; }
	
	@Id
	@Column(name="male")
	public int getMale() { return male; }
	public void setMale(int age) { this.male = age; }
	
	@Id
	@Column(name="female")
	public int getFemale() { return female; }
	public void setFemale(int female) { this.female = female; }
	
	@Id
	@Column(name="other")
	public int getOther() { return other; }
	public void setOther(int other) {	this.other = other; }

	@Id
	@Column(name="total")
	public int getTotal() { return total; }
	public void setTotal(int total) {	this.total = total; }
}
