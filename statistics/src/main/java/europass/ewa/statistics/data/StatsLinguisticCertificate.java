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

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

import com.google.common.base.Strings;

import europass.ewa.statistics.utils.ValidationUtils;
import javax.persistence.Embeddable;

@Embeddable
public class StatsLinguisticCertificate {

    private Long id;

    private String title;
    private DateTime issueDate;
    private String cefrLevel;

    @Column(name = "title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = ValidationUtils.validateSetterStringLength("title", title, 255);
    }

    @Column(name = "issue_date")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(DateTime issueDate) {
        this.issueDate = issueDate;
    }

    @Column(name = "cefr_level")
    public String getCefrLevel() {
        return cefrLevel;
    }

    public void setCefrLevel(String cefrLevel) {
        this.cefrLevel = ValidationUtils.validateSetterStringLength("cefrLevel", cefrLevel, 255);
    }

    public boolean checkEmpty() {

        if (!Strings.isNullOrEmpty(title)) {
            return false;
        }
        if (issueDate != null) {
            return false;
        }
        if (!Strings.isNullOrEmpty(cefrLevel)) {
            return false;
        }

        return true;
    }
}
