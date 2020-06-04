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
package europass.ewa.model.conversion.xml;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public abstract class DateMixin {

    public DateMixin(@JsonProperty("Month") int month, @JsonProperty("Day") int day) {
    }

    @JsonProperty("Month")
    @JacksonXmlProperty(isAttribute = true, localName = "month")
    @JsonSerialize(using = GMonthSerialiser.class, as = Integer.class)
    abstract Integer getMonth();

    @JsonDeserialize(using = GMonthDeserialiser.class, as = Integer.class)
    abstract void setMonth(Integer month);

    @JsonProperty("Day")
    @JacksonXmlProperty(isAttribute = true, localName = "day")
    @JsonSerialize(using = GDaySerialiser.class, as = Integer.class)
    abstract Integer getDay();

    @JsonDeserialize(using = GDayDeserialiser.class, as = Integer.class)
    abstract void setDay(Integer day);

}
