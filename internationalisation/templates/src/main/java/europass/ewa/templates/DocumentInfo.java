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
package europass.ewa.templates;

import europass.ewa.enums.EuropassDocumentType;

public class DocumentInfo {

    private EuropassDocumentType type;

    private String odtPath;

    private String cmsPath;

    public DocumentInfo() {
    }

    public DocumentInfo(EuropassDocumentType type, String odtPath) {
        this.type = type;
        this.odtPath = odtPath;
    }

    public DocumentInfo(EuropassDocumentType type, String odtPath, String cmsPath) {
        this.type = type;
        this.odtPath = odtPath;
        this.cmsPath = cmsPath;
    }

    public EuropassDocumentType getType() {
        return type;
    }

    public void setType(EuropassDocumentType type) {
        this.type = type;
    }

    public String getOdtPath() {
        return odtPath;
    }

    public void setOdtPath(String odtPath) {
        this.odtPath = odtPath;
    }

    public String getCmsPath() {
        return cmsPath;
    }

    public void setCmsPath(String cmsPath) {
        this.cmsPath = cmsPath;
    }

}
