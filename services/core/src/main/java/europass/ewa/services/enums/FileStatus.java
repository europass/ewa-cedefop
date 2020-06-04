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
package europass.ewa.services.enums;

public enum FileStatus {

    EXCEEDS_LIMIT("file.too.big"),
    EXCEEDS_CUMULATIVE_SIZE_LIMIT("file.exceeded.cumm.size.limit"),
    FORBIDDEN("file.forbidden"),
    UNDEFINED_CONTENT_TYPE("file.undefined.mime"),
    DISALLOWED_CONTENT_TYPE("file.invalid.mime"),
    NOT_FOUND("file.not.found"),
    MANAGE_PERMISSION("file.manage.permission"),
    NOT_VIEWABLE_FILE("file.view.disallowed"),
    PARSING("file.fail.parse"),
    PDF_AS_IMAGE("file.pdf.as.image"),
    PDF_THUMB("pdf.thumb"),
    PDF_PASSWORD_PROTECTED("pdf.password.protected"),
    OTHER("file.other.error");

    private String description;

    FileStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
