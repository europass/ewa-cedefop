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
import javax.persistence.Transient;

import europass.ewa.services.statistics.constants.ServicesStatisticsConstants;

@Entity
@Table(name=ServicesStatisticsConstants.CUBE_ENTRY_FLANG_PIVOT)
public class CubeEntryFLangPivot implements EntryPivot, Serializable{

	private static final long serialVersionUID = 1L;

	int year_no;
	int month_no;
	String doc_type;
	int lang1;
	int lang2;
	int lang3;
	int lang4;
	int lang4plus;
	int total_count;

	@Id
	@Column(name="year_no")
	public int getYear_no() { return year_no; }
	public void setYear_no(int year_no) {	this.year_no = year_no; }
	
	@Id
	@Column(name="month_no")
	public int getMonth_no() {	return month_no; }
	public void setMonth_no(int month_no) { this.month_no = month_no; }
	
	@Transient
	@Override
	public int getDay_no() { return 0; }
	public void setDay_no(int day_no) {  }

	@Id
	@Column(name="doc_type")
	public String getDoc_type() { return doc_type; }
	public void setDoc_type(String doc_type) { this.doc_type = doc_type; }

	@Id
	@Column(name="lang1")
	public int getLang1() { return lang1; }
	public void setLang1(int lang1) { this.lang1 = lang1; }
	
	@Id
	@Column(name="lang2")
	public int getLang2() { return lang2; }
	public void setLang2(int lang2) { this.lang2 = lang2; }
	
	@Id
	@Column(name="lang3")
	public int getLang3() { return lang1; }
	public void setLang3(int lang3) { this.lang3 = lang3; }
	
	@Id
	@Column(name="lang4")
	public int getLang4() { return lang4; }
	public void setLang4(int lang4) { this.lang4 = lang4; }
	
	@Id
	@Column(name="lang4plus")
	public int getLang4plus() { return lang4plus; }
	public void setLang4plus(int lang4plus) { this.lang4plus = lang4plus; }
	
	@Id
	@Column(name="total_count")
	public int getTotal_count() { return total_count; }
	public void setTotal_count(int total_count) { this.total_count = total_count; }	
	
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
