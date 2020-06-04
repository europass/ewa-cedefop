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

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.google.common.base.Strings;

import europass.ewa.statistics.utils.ValidationUtils;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;

@Entity
@Table(name = "stats_foreign_languages")
public class StatsForeignLanguages {

    private Long id;

    private StatsEntry statsEntry;
    private String languageType;
    private String listeningLevel;
    private String readingLevel;
    private String spokenInteractionLevel;
    private String spokenProductionLevel;
    private String writingLevel;

    private List<StatsLinguisticCertificate> certificates;

    public StatsForeignLanguages() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "statsEntry_id")
    public StatsEntry getStatsEntry() {
        return statsEntry;
    }

    public void setStatsEntry(StatsEntry statsEntry) {
        this.statsEntry = statsEntry;
    }

    public String getLanguageType() {
        return languageType;
    }

    public void setLanguageType(String languageType) {
        this.languageType = ValidationUtils.validateSetterStringLength("languageType", languageType, 255);
    }

    public String getListeningLevel() {
        return listeningLevel;
    }

    public void setListeningLevel(String listeningLevel) {
        this.listeningLevel = ValidationUtils.validateSetterStringLength("listeningLevel", listeningLevel, 10);
    }

    public String getReadingLevel() {
        return readingLevel;
    }

    public void setReadingLevel(String readingLevel) {
        this.readingLevel = ValidationUtils.validateSetterStringLength("readingLevel", readingLevel, 10);
    }

    public String getSpokenInteractionLevel() {
        return this.spokenInteractionLevel;
    }

    public void setSpokenInteractionLevel(String spokenInteractionLevel) {
        this.spokenInteractionLevel = ValidationUtils.validateSetterStringLength("spokenInteractionLevel", spokenInteractionLevel, 10);
    }

    public String getSpokenProductionLevel() {
        return this.spokenProductionLevel;
    }

    public void setSpokenProductionLevel(String spokenProductionLevel) {
        this.spokenProductionLevel = ValidationUtils.validateSetterStringLength("spokenProductionLevel", spokenProductionLevel, 10);
    }

    public String getWritingLevel() {
        return this.writingLevel;
    }

    public void setWritingLevel(String writingLevel) {
        this.writingLevel = ValidationUtils.validateSetterStringLength("writingLevel", writingLevel, 10);
    }

    @ElementCollection
    @CollectionTable(name = "stats_linguistic_certificate", joinColumns = @JoinColumn(name = "stats_language_id"))
    public List<StatsLinguisticCertificate> getCertificates() {
        return certificates;
    }

    public void setCertificates(List<StatsLinguisticCertificate> certificates) {
        this.certificates = certificates;
    }

    public boolean checkEmpty() {

        if (!Strings.isNullOrEmpty(languageType)) {
            return false;
        }
        if (!Strings.isNullOrEmpty(listeningLevel)) {
            return false;
        }
        if (!Strings.isNullOrEmpty(readingLevel)) {
            return false;
        }
        if (!Strings.isNullOrEmpty(spokenInteractionLevel)) {
            return false;
        }
        if (!Strings.isNullOrEmpty(spokenProductionLevel)) {
            return false;
        }
        if (!Strings.isNullOrEmpty(writingLevel)) {
            return false;
        }

        return true;
    }
}
