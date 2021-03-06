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

import javax.persistence.Column;

import com.google.common.base.Strings;

import europass.ewa.statistics.utils.ValidationUtils;
import javax.persistence.Embeddable;

@Embeddable
public class StatsEducation extends StatsExperience {

    private Long id;
    private String organisationCountry;
    private String qualification;
    private String qualificationLevel;
    private String educationalField;

    @Column(name = "organisation_country")
    public String getOrganisationCountry() {
        return organisationCountry;
    }

    public void setOrganisationCountry(String organisationCountry) {
        this.organisationCountry = ValidationUtils.validateSetterStringLength("organisationCountry", organisationCountry, 255);
    }

    @Column(name = "qualification")
    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = ValidationUtils.validateSetterStringLength("qualification", qualification, 255);
    }

    @Column(name = "qualification_level")
    public String getQualificationLevel() {
        return qualificationLevel;
    }

    public void setQualificationLevel(String qualificationLevel) {
        this.qualificationLevel = ValidationUtils.validateSetterStringLength("qualificationLevel", qualificationLevel, 255);
    }

    @Column(name = "educational_field")
    public String getEducationalField() {
        return educationalField;
    }

    public void setEducationalField(String educationalField) {
        this.educationalField = ValidationUtils.validateSetterStringLength("educationalField", educationalField, 255);
    }

    @Override
    public boolean checkEmpty() {

        if (!Strings.isNullOrEmpty(organisationCountry)) {
            return false;
        }
        if (!Strings.isNullOrEmpty(qualification)) {
            return false;
        }
        if (!Strings.isNullOrEmpty(qualificationLevel)) {
            return false;
        }
        if (!Strings.isNullOrEmpty(educationalField)) {
            return false;
        }

        return super.checkEmpty();
    }

}
