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
@Table(name=ServicesStatisticsConstants.CUBE_ENTRY_DOCS)
public class CubeEntryDocs implements EntryPivot, Serializable{

	private static final long serialVersionUID = 1L;
	
	int total;
	int year_no;
	int month_no;
	
	int CL;
	int CV;
	int ECV_ESP;
	int LP;
	int ESP;
	
	
//	@Id
//	@GeneratedValue(strategy = GenerationType.IDENTITY)
	
	@Column(name="TOTAL")
	public int getTotal() {	return total; }
	public void setTotal( int total ) { this.total = total; }

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
	
	@Transient
	@Override
	public int getDay_no() { return 0; }
	public void setDay_no(int day_no) {  }

	@Id
	@Column(name="CL")
	public int getCL() { return CL; }
	public void setCL(int ecl) { this.CL = ecl; }

	@Id
	@Column(name="CV")
	public int getCV() { return CV; }
	public void setCV(int cv) { this.CV = cv; }

	@Id
	@Column(name="ECV_ESP")
	public int getECV_ESP() { return ECV_ESP; }
	public void setECV_ESP(int ecv_esp) { this.ECV_ESP = ecv_esp; }

	@Id
	@Column(name="LP")
	public int getLP() { return LP; }
	public void setLP(int lp) { this.LP = lp; }

	@Id
	@Column(name="ESP")
	public int getESP() { return ESP; }
	public void setESP(int esp) { this.ESP = esp; }
	
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
