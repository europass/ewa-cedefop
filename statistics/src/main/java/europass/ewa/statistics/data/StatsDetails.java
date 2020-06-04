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
package europass.ewa.statistics.data;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.google.common.base.Strings;

import europass.ewa.statistics.utils.ValidationUtils;

@Entity
@Table(name = "stats_details")
public class StatsDetails implements Serializable {

    private static final long serialVersionUID = 9197169403177815186L;

    private Long id;

    private String telephoneTypes;

    private String imTypes;

    private String typeOfFiles;

    private Long cumulativeSize;

    private Integer numberOfFiles;

    private StatsEntry statsEntry;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

//  === Option 1: One-To-One Bidirectional ===
//	http://www.javacodegeeks.com/2013/03/bidirectional-onetoone-primary-key-association.html
//	@Id
//	@Column(name="statsEntry_id")
//	@GeneratedValue(generator="foreignKeyGenerator")
//	@GenericGenerator(name="foreignKeyGenerator", strategy="foreign", parameters=@Parameter(name="property", value="statsEntry"))
//	public Long getStatsEntryId() {
//		return statsEntryId;
//	}
//	public void setStatsEntryId(Long statsEntryId) {
//		this.statsEntryId = statsEntryId;
//	}
//	@OneToOne
//	@PrimaryKeyJoinColumn
//	public StatsEntry getStatsEntry() {
//		return statsEntry;
//	}
//	public void setStatsEntry( StatsEntry statsEntry ) {
//		this.statsEntry = statsEntry;
//	}
//	Option 2: One-To-One based on Foreign Key
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "statsEntry_id")
    public StatsEntry getStatsEntry() {
        return statsEntry;
    }

    public void setStatsEntry(StatsEntry statsEntry) {
        this.statsEntry = statsEntry;
    }

    @Column(name = "telephone_types")
    public String getTelephoneTypes() {
        return telephoneTypes;
    }

    public void setTelephoneTypes(String telephoneTypes) {
        this.telephoneTypes = ValidationUtils.validateSetterStringLength("telephone_types", telephoneTypes, 255);
    }

    @Column(name = "im_types")
    public String getImTypes() {
        return imTypes;
    }

    public void setImTypes(String imTypes) {
        this.imTypes = ValidationUtils.validateSetterStringLength("im_types", imTypes, 255);
    }

    @Column(name = "type_of_files")
    public String getTypeOfFiles() {
        return typeOfFiles;
    }

    public void setTypeOfFiles(String typeOfFiles) {
        this.typeOfFiles = ValidationUtils.validateSetterStringLength("type_of_files", typeOfFiles, 255);
    }

    @Column(name = "cumulative_size")
    public Long getCumulativeSize() {
        return cumulativeSize;
    }

    public void setCumulativeSize(Long cumulativeSize) {
        this.cumulativeSize = cumulativeSize;
    }

    @Column(name = "number_of_files")
    public Integer getNumberOfFiles() {
        return numberOfFiles;
    }

    public void setNumberOfFiles(Integer numberOfFiles) {
        this.numberOfFiles = numberOfFiles;
    }

    boolean checkEmpty() {

        if (!Strings.isNullOrEmpty(telephoneTypes)) {
            return false;
        }
        if (!Strings.isNullOrEmpty(imTypes)) {
            return false;
        }
        if (!Strings.isNullOrEmpty(typeOfFiles)) {
            return false;
        }
        if (cumulativeSize != null) {
            return false;
        }
        if (numberOfFiles != null) {
            return false;
        }

        return true;
    }
}
