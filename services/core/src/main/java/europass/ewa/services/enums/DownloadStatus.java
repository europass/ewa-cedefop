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

public enum DownloadStatus {

    INPUT_EMPTY("download.input.empty"), //input is empty or null

    INPUT_ENCODING("download.input.encoding"), //input json url decode

    INPUT_TO_MODEL("download.input.to.model"), //input json to model

    INPUT_OTHER("download.input.error"), //unspecified problem with input json

    MODEL_TO_XML("download.model.to.xml"),
    MODEL_TO_JSON("download.model.to.json"),
    MODEL_TO_BYTES("download.model.to.bytes"), //model to null or empty bytes

    CLOUD_REMOTE_REDIRECTION("skillspassport.export.cloud.error.redirect"),
    CLOUD_REMOTE_UNAUTHORIZED("skillspassport.export.cloud.error.unauthorized"),
    CLOUD_REMOTE_SERVERERROR("skillspassport.export.cloud.error.servererror"),
    CLOUD_GENERIC("skillspassport.export.cloud.error.generic"),
    OTHER("download.other.error"), //unspecified

    EXPORT_OTHER("export.other.error");

    private String description;

    DownloadStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
