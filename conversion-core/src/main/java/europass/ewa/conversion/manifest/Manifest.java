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
package europass.ewa.conversion.manifest;

import static javax.xml.bind.annotation.XmlAccessType.FIELD;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "manifest")
@XmlAccessorType(FIELD)
public class Manifest {

    public static final String VERSION_1_2 = "1.2";

    @XmlAttribute
    private String version;

    @XmlElement(name = "file-entry")
    private List<FileEntry> entries = new ArrayList<FileEntry>();

    public Manifest(String version) {
        this.version = version;
    }

    public Manifest() {
        this(VERSION_1_2);
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<FileEntry> getEntries() {
        return entries;
    }

    public Manifest fileEntry(String mediaType, String fullPath) {
        FileEntry entry = new FileEntry();
        entry.setMediaType(mediaType);
        entry.setFullPath(fullPath);
        entry.setVersion(version);
        entries.add(entry);
        return this;
    }
}
