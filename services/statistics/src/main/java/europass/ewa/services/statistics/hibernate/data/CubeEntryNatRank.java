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
@Table(name=ServicesStatisticsConstants.CUBE_ENTRY_NAT_RANK)
public class CubeEntryNatRank implements EntryCube, Serializable{

	private static final long serialVersionUID = 1L;

	int rec_count;
	int year_no;
	String doc_type;
	String doc_lang;
	String nationality;
	int rank_no;
	
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="rec_count", unique = true, nullable = false)
	public int getRec_count() { return rec_count;	}
	public void setRec_count(int id) {	this.rec_count = id; }

	@Id
	@Column(name="year_no")
	public int getYear_no() { return year_no; }
	public void setYear_no(int year_no) {	this.year_no = year_no; }
	
	@Transient
	@Column(name="month_no")
	public int getMonth_no() {	return 0; }
	public void setMonth_no(int month_no) {  }
	
	@Transient
	@Column(name="day_no")
	public int getDay_no() { return 0; }
	public void setDay_no(int day_no) {  }
	
	@Id
	@Column(name="doc_type")
	public String getDoc_type() { return doc_type; }
	public void setDoc_type(String doc_type) { this.doc_type = doc_type; }

	@Id
	@Column(name="doc_lang")
	public String getDoc_lang() { return doc_lang; }
	public void setDoc_lang(String doc_lang) { this.doc_lang = doc_lang; }
	
	@Id
	@Column(name="nationality")
	public String getNationality() { return nationality; }
	public void setNationality(String nationality) { this.nationality = nationality; }
	
	@Column(name="rank_no")
	public int getRank_no() { return rank_no; }
	public void setRank_no(int rank_no) {	this.rank_no = rank_no; }
	
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
