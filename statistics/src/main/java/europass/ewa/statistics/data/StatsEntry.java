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
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import europass.ewa.statistics.utils.ValidationUtils;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;

@Entity
@Table(name = "ewa_stats_entry")
public class StatsEntry implements Serializable {

    private static final long serialVersionUID = -7079601656704014809L;

    private Long id;

    private String docLanguage;
    private Integer age;
    private Character gender;
    private String postalAddressCountry;
    private Integer educationYears;
    private Integer workExperienceYears;
    private Integer emailHashCode;
    private String documentType;

    private String fileFormat;
    private DateTime creationDate;
    private String generatedBy;
    private String exportTo;

    private boolean isNew;
    private DateTime dateOfBirth;

    private Long relatedEntryId;

    private List<StatsMotherLanguage> motherLangs;
    private List<StatsNationality> nationalities;
    private List<StatsForeignLanguages> foreignLanguages;
    private List<StatsWorkExperience> workExperiences;
    private List<StatsEducation> educationExperiences;
    private List<StatsAchievement> achievements;
    private String headlineType;
    private String headlineDescription;
    private StatsDetails details;

    private String skillsDriving;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "docLanguage")
//	@Length(max = 255, message = "Column docLanguage maximum length is 255")
    public String getLanguage() {
        return docLanguage;
    }

    public void setLanguage(String docLanguage) {
        this.docLanguage = ValidationUtils.validateSetterStringLength("docLanguage", docLanguage, 255);
    }

    /*
	 * akar : This is a potential point for improvement.
	 * By specifying the @JoinColumn on both models (StasEntry & StatsMotherLanguage)
	 * you don't have a two way relationship, but two one way relationships instead
	 * and a confusing mapping which results in three queries when attempting to save. 
	 * Two INSERTs and another one for UPDATING the foreign key,
	 * The optimal solution would be two INSERTs from the beginning.
     */
    @ElementCollection
    @CollectionTable(name = "stats_mother_language", joinColumns = @JoinColumn(name = "statsEntry_id"))
    public List<StatsMotherLanguage> getMotherLangs() {
        return motherLangs;
    }

    public void setMotherLangs(List<StatsMotherLanguage> motherLangs) {
        this.motherLangs = motherLangs;
    }

    @ElementCollection
    @CollectionTable(name = "stats_nationality", joinColumns = @JoinColumn(name = "statsEntry_id"))
    public List<StatsNationality> getNationalities() {
        return nationalities;
    }

    public void setNationalities(List<StatsNationality> nationalities) {
        this.nationalities = nationalities;
    }

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "statsEntry_id")
    public List<StatsForeignLanguages> getForeignLanguages() {
        return foreignLanguages;
    }

    public void setForeignLanguages(List<StatsForeignLanguages> foreignLanguages) {
        this.foreignLanguages = foreignLanguages;
    }

    @ElementCollection
    @CollectionTable(name = "stats_work_experience", joinColumns = @JoinColumn(name = "statsEntry_id"))
    public List<StatsWorkExperience> getWorkExperiences() {
        return workExperiences;
    }

    public void setWorkExperiences(List<StatsWorkExperience> workExperiences) {
        this.workExperiences = workExperiences;
    }

    @ElementCollection
    @CollectionTable(name = "stats_education", joinColumns = @JoinColumn(name = "statsEntry_id"))
    public List<StatsEducation> getEducationExperiences() {
        return educationExperiences;
    }

    public void setEducationExperiences(List<StatsEducation> educationExperiences) {
        this.educationExperiences = educationExperiences;
    }

    @ElementCollection
    @CollectionTable(name = "stats_achievement", joinColumns = @JoinColumn(name = "statsEntry_id"))
    public List<StatsAchievement> getAchievements() {
        return achievements;
    }

    public void setAchievements(List<StatsAchievement> achievements) {
        this.achievements = achievements;
    }

//  === Option 1: One-To-One Bidirectional ====
//	http://www.javacodegeeks.com/2013/03/bidirectional-onetoone-primary-key-association.html
// 	
//	Option 2: One-To-One based on Foreign Key
//	http://docs.jboss.org/hibernate/annotations/3.5/reference/en/html_single/#entity-mapping-association
    @OneToOne(mappedBy = "statsEntry", cascade = CascadeType.ALL)
    public StatsDetails getDetails() {
        return details;
    }

    public void setDetails(StatsDetails details) {
        this.details = details;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Character getGender() {
        return gender;
    }

    public void setGender(Character gender) {
        this.gender = gender;
    }

    public String getPostalAddressCountry() {
        return postalAddressCountry;
    }

    public void setPostalAddressCountry(String postalAddressCountry) {
        this.postalAddressCountry = ValidationUtils.validateSetterStringLength("postalAddressCountry", postalAddressCountry, 255);

    }

    public Integer getEducationYears() {
        return educationYears;
    }

    public void setEducationYears(Integer educationYears) {
        this.educationYears = educationYears;
    }

    public Integer getWorkExperienceYears() {
        return workExperienceYears;
    }

    public void setWorkExperienceYears(Integer workExperienceYears) {
        this.workExperienceYears = workExperienceYears;
    }

    public String getDocumentType() {
        return documentType;
    }

    public String getFileFormat() {
        return fileFormat;
    }

    @Column
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getCreationDate() {
        return creationDate;
    }

    public String getGeneratedBy() {
        return generatedBy;
    }

    public String getExportTo() {
        return exportTo;
    }

    public void setExportTo(String exportTo) {
        this.exportTo = ValidationUtils.validateSetterStringLength("exportTo", exportTo, 50);
    }

    public void setDocumentType(String documentType) {
        this.documentType = ValidationUtils.validateSetterStringLength("documentType", documentType, 50);
    }

    public void setFileFormat(String fileFormat) {
        this.fileFormat = ValidationUtils.validateSetterStringLength("fileFormat", fileFormat, 50);
    }

    public void setCreationDate(DateTime dateTime) {
        this.creationDate = dateTime;
    }

    public void setGeneratedBy(String generatedBy) {
        this.generatedBy = ValidationUtils.validateSetterStringLength("generatedBy", generatedBy, 50);
    }

    @Column(name = "is_new", columnDefinition = "TINYINT")
    @Type(type = "org.hibernate.type.NumericBooleanType")
    public boolean getIsNew() {
        return isNew;
    }

    public void setIsNew(boolean isNew) {
        this.isNew = isNew;
    }

    @Column(name = "date_of_birth")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(DateTime dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Column(name = "related_entry_id")
    public Long getRelatedEntryId() {
        return relatedEntryId;
    }

    public void setRelatedEntryId(Long relatedEntryId) {
        this.relatedEntryId = relatedEntryId;
    }

    @Column(name = "headline_type")
    public String getHeadlineType() {
        return headlineType;
    }

    public void setHeadlineType(String headlineType) {
        this.headlineType = ValidationUtils.validateSetterStringLength("headlineType", headlineType, 255);
    }

    @Column(name = "headline_description")
    public String getHeadlineDescription() {
        return headlineDescription;
    }

    public void setHeadlineDescription(String headlineDescription) {
        this.headlineDescription = ValidationUtils.validateSetterStringLength("headlineDescription", headlineDescription, 255);
    }

    @Column(name = "skills_driving")
    public String getSkillsDriving() {
        return skillsDriving;
    }

    public void setSkillsDriving(String skillsDriving) {
        this.skillsDriving = ValidationUtils.validateSetterStringLength("skillsDriving", skillsDriving, 50);
    }

    /**
     * @return the emailHashCode
     */
    public Integer getEmailHashCode() {
        return emailHashCode;
    }

    /**
     * @param emailHashCode the emailHashCode to set
     */
    public void setEmailHashCode(Integer emailHashCode) {
        this.emailHashCode = emailHashCode;
    }

}
