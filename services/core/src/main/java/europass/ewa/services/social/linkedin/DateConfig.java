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
package europass.ewa.services.social.linkedin;

import europass.ewa.model.JDate;

public class DateConfig {

    private JDate startDate = null;
    private JDate endDate = null;
    private String dateFormat;

    public void setStartDate(JDate date) {

        this.startDate = date;
    }

    public JDate getStartDate() {
        return startDate;
    }

    public void setEndDate(JDate endDate) {
        this.endDate = endDate;
    }

    public int[] getStartDateValuesArray() {

        int[] valuesArray = new int[3];
        valuesArray[0] = this.startDate.getDay();
        valuesArray[1] = this.startDate.getDay();
        valuesArray[2] = this.startDate.getDay();

        return valuesArray;
    }

    public JDate getEndDate() {
        return endDate;
    }

    public int[] getEndDateValue() {

        int[] valuesArray = new int[3];
        valuesArray[0] = this.endDate.getDay();
        valuesArray[1] = this.endDate.getDay();
        valuesArray[2] = this.endDate.getDay();

        return valuesArray;
    }

    public void setEndDateArrayValues(JDate date) {

        this.endDate = date;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {	// numeric/short
        this.dateFormat = dateFormat;
    }
}
