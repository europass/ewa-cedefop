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
package europass.ewa.tools.ga.manager.data;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name="stat_downloads")
public class HibernateDownloads implements HibernateInfo, Serializable{
	
	private static final long serialVersionUID = -991102311145368349L;
	
	int id;
	String document;
	String type;
	String iso_language_code;
	String iso_country_code;
	String ip_country;
	
	@Transient String fileURL;
	
	int year;
	int month;
	int day;
	int volume;
	Date exact_date;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id", unique = true, nullable = false)	
	public int getId() { return id; }
	public void setId(int id) {	this.id = id; }
	
	@Column(name="document")
	public String getDocument() { return document; }
	public void setDocument(String document) { this.document = document; }
	
	@Column(name="type")		
	public String getType() { return type; }
	public void setType(String type) { this.type = type; }
	
	@Column(name="iso_language_code")	
	public String getIso_language_code() { return iso_language_code; }
	public void setIso_language_code(String iso_language_code) { this.iso_language_code = iso_language_code; }
	
	@Column(name="iso_country_code")	
	public String getIso_country_code() { return iso_country_code; }	
	public void setIso_country_code(String iso_country_code) { this.iso_country_code = iso_country_code; }

	@Column(name="ip_country")	
	public String getIp_country() { return ip_country; }	
	public void setIp_country(String ip_country) { this.ip_country = ip_country; }
	
	@Column(name="exact_date")
	public Date getDate() { return exact_date; }
	public void setDate(Date date) { this.exact_date = date; }
	@PrePersist
	protected void onCreate() { exact_date = new Date(); }
	@PreUpdate
	protected void onUpdate() {	exact_date = new Date(); }	
	
	@Column(name="year")
	public int getYear() { return year; }
	public void setYear(int year) { this.year = year; }
	
	@Column(name="month")
	public int getMonth() { return month; }
	public void setMonth(int month) { this.month = month; }
	
	@Column(name="day")
	public int getDay() { return day; }
	public void setDay(int day) { this.day = day; }
	
	@Column(name="volume")	
	public int getVolume() { return volume; }
	public void setVolume(int volume) { this.volume = volume; }
	
	@Transient
	public String getFileURL() { return fileURL; }
	@Transient
	public void setFileURL(String fileURL) { this.fileURL = fileURL; }

	@Transient
	@Override
	public String printDetails(){
		
		return "\tDATE:"+this.getDay()+"/"+this.getMonth()+"/"+this.getYear()+
				"\tVOLUME:"+this.getVolume()+
				"\tDOCUMENT:"+this.getDocument()+
				"\tTYPE:"+this.getType()+
				"\tISO_LANGUAGE:"+this.getIso_language_code()+
				"\tISO_COUNTRY:"+this.getIso_country_code()+
				"\tGEOLOCATION COUNTRY:"+this.getIp_country();
	}

	
}
