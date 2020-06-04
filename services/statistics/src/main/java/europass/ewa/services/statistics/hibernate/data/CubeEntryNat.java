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
@Table(name=ServicesStatisticsConstants.CUBE_ENTRY_NAT)
public class CubeEntryNat implements EntryCube, Serializable {

	private static final long serialVersionUID = 1L;

	int rec_count;
	int year_no;
	int month_no;
	int day_no;
	String doc_type;
	String doc_lang;
	String address_country;
	String gender_group;
	int age;
	int work_years;
	int educ_years;
	String nationality;
	
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="rec_count", unique = true, nullable = false)
	public int getRec_count() { return rec_count;	}
	public void setRec_count(int id) {	this.rec_count = id; }

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
	
	@Id
	@Column(name="gender_group")
	public String getGender_group() { return gender_group; }
	public void setGender_group(String gender_group) { this.gender_group = gender_group; }
	
	@Id
	@Column(name="age")
	public int getAge() { return age; }
	public void setAge(int age) { this.age = age; }
	
	@Id
	@Column(name="work_years")
	public int getWork_years() { return work_years; }
	public void setWork_years(int work_years) { this.work_years = work_years; }
	
	@Id
	@Column(name="educ_years")
	public int getEduc_years() { return educ_years; }
	public void setEduc_years(int educ_years) {	this.educ_years = educ_years; }	
	
	@Id
	@Column(name="nationality")
	public String getNationality() { return nationality; }
	public void setNationality(String nationality) { this.nationality = nationality; }
	
}
