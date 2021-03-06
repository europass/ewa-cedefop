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
package europass.ewa.enums;

public enum LogFields {

    MESSAGE("Message", 1),
    ERRCODE("ErrCode", 2),
    TIMESTAMP("Timestamp", 3),
    REQ_ID("RequestID", 4),
    DOCTYPE("DocumentType", 5),
    FILETYPE("FileType", 6),
    FILESIZE("FileSize", 7),
    DIMENSIONS("Dimensions", 8),
    EXTENSION("Extension", 9),
    LOCATION("Location", 10),
    ACTION("Action", 11),
    UA("UserAgent", 12),
    MODULE("Module", 13);

    private String description;

    private int index;

    private LogFields(String description, int index) {
        this.description = description;
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return this.getDescription();
    }
}
