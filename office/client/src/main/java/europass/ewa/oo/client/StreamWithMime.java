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
package europass.ewa.oo.client;

import java.io.FileInputStream;

public class StreamWithMime {

    private FileInputStream stream;
    private String mimeType;
    private String filePath;

    public StreamWithMime(FileInputStream stream, String filePath, String mimeType) {
        this.stream = stream;
        this.mimeType = mimeType;
        this.filePath = filePath;
    }

    public StreamWithMime(FileInputStream stream, String mimeType) {
        this.stream = stream;
        this.mimeType = mimeType;
        this.filePath = null;
    }

    public StreamWithMime(String filePath, String mimeType) {
        this.mimeType = mimeType;
        this.filePath = filePath;
    }

    public FileInputStream getStream() {
        return stream;
    }

    public void setStream(FileInputStream stream) {
        this.stream = stream;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

}
