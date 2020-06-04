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
public class StatsWorkExperience extends StatsExperience {

    private String position;
    private String employerCountry;
    private String employerSector;

    public StatsWorkExperience() {
    }

    @Column(name = "position")
    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = ValidationUtils.validateSetterStringLength("position", position, 255);
    }

    @Column(name = "employer_country")
    public String getEmployerCountry() {
        return employerCountry;
    }

    public void setEmployerCountry(String employerCountry) {
        this.employerCountry = ValidationUtils.validateSetterStringLength("employer_country", employerCountry, 255);
    }

    @Column(name = "employer_sector")
    public String getEmployerSector() {
        return employerSector;
    }

    public void setEmployerSector(String employerSector) {
        this.employerSector = ValidationUtils.validateSetterStringLength("employer_sector", employerSector, 255);
    }

    @Override
    public boolean checkEmpty() {

        if (!Strings.isNullOrEmpty(position)) {
            return false;
        }
        if (!Strings.isNullOrEmpty(employerCountry)) {
            return false;
        }
        if (!Strings.isNullOrEmpty(employerSector)) {
            return false;
        }

        return super.checkEmpty();
    }
}
