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

import europass.ewa.services.statistics.constants.ServicesStatisticsConstants;
import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name=ServicesStatisticsConstants.CUBE_ENTRY_EMAIL_HASH)
public class CubeEntryEmailHash implements EntryCube, Serializable {

	private static final long serialVersionUID = 1L;
	
	int rec_count;
	int year_no;
	int month_no;
	String doc_type;
	String doc_lang;
	String address_country;
	String email_hash_code;
	
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
	
	public int getDay_no() { return 0; } // N/A
	public void setDay_no(int day_no) {}
	
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
	@Column(name="email_hash_code")
	public String getEmail_hash_code() { return email_hash_code; }
	public void setEmail_hash_code(String email_hash_code) { this.email_hash_code = email_hash_code; }

}
