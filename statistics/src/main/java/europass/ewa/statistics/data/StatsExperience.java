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
import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;

@MappedSuperclass
public class StatsExperience implements Serializable {

    private DateTime periodFrom;
    private DateTime periodTo;
    private Integer duration;

    @Column(name = "period_from")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getPeriodFrom() {
        return periodFrom;
    }

    public void setPeriodFrom(DateTime periodFrom) {
        this.periodFrom = periodFrom;
    }

    @Column(name = "period_to")
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    public DateTime getPeriodTo() {
        return periodTo;
    }

    public void setPeriodTo(DateTime periodTo) {
        this.periodTo = periodTo;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public boolean checkEmpty() {

        if (periodFrom != null) {
            return false;
        }
        if (periodTo != null) {
            return false;
        }
        if (duration != null) {
            return false;
        }

        return true;
    }
}
