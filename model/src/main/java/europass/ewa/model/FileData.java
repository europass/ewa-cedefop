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
package europass.ewa.model;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.apache.commons.lang.ArrayUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.google.common.base.Strings;

/**
 * An FileData consists of the following:
 *
 * <ol>
 * <li><strong>Name: The name of the attached document.</strong></li>
 * <li><strong>Data: The actual bytes of the document and the
 * mimetype.</strong></li>
 * <li><strong>MimeType: The mimetype.</strong></li>
 * <li><strong>TmpUri: A URI on the server side were the bytes of the document
 * reside.</strong></li>
 * </ol>
 *
 * @author ekar
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FileData {

    private String name;

    private String mimeType;

    private byte[] data;

    private URI tmpuri;

    private List<Metadata> metadata;

    private static final Logger LOG = LoggerFactory.getLogger(FileData.class);

    public FileData() {
    }

    public FileData(String name, String mimeType, String tmpuri, byte[] data) {
        this.name = name;
        this.mimeType = mimeType;
        this.tmpuri = URI.create(tmpuri);
        this.data = ArrayUtils.clone(data);
    }

    public FileData(String mimeType, String tmpuri, byte[] data) {
        this.mimeType = mimeType;
        this.tmpuri = URI.create(tmpuri);
        this.data = ArrayUtils.clone(data);
    }

    public FileData(String name, String mimeType, String tmpuri) {
        this.name = name;
        this.mimeType = mimeType;
        this.tmpuri = URI.create(tmpuri);
    }

    @JsonProperty("Name")
    @JacksonXmlProperty(localName = "Name", namespace = Namespace.NAMESPACE)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("MimeType")
    @JacksonXmlProperty(localName = "MimeType", namespace = Namespace.NAMESPACE)
    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    @JsonProperty("Data")
    @JacksonXmlProperty(localName = "Data", namespace = Namespace.NAMESPACE)
    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @JsonProperty("TempURI")
    @JacksonXmlProperty(localName = "TempURI", namespace = Namespace.NAMESPACE)
    public URI getTmpuri() {
        return tmpuri;
    }

    public void setTmpuri(URI tmpuri) {
        this.tmpuri = tmpuri;
    }

    @JsonProperty("Metadata")
    @JacksonXmlProperty(localName = "Metadata", namespace = Namespace.NAMESPACE)
    @JacksonXmlElementWrapper(localName = "MetadataList", namespace = Namespace.NAMESPACE)
    public List<Metadata> getMetadata() {
        return metadata;
    }

    public void setMetadata(List<Metadata> metadata) {
        this.metadata = metadata;
    }

    /**
     * ************ METADATA SPECIFIC ****************
     */
    /**
     * Updates the map of metadata, with the object identified by the key. If it
     * already exists, then update it.
     *
     * @param key
     * @param value
     */
    @JsonIgnore
    public void setInfo(String key, String value) {

        if (Strings.isNullOrEmpty(key)) {
            return;
        }

        if (metadata == null) {
            metadata = new ArrayList<Metadata>();
        }

        int existingIndex = this.hasMetadata(key);

        //If it does not exist, add it
        if (existingIndex == -1) {
            metadata.add(new Metadata(key, value));
            return;
        }

        //Otherwise, replace it
        Metadata meta = metadata.get(existingIndex);
        meta.setValue(value);
    }

    /**
     * Returns the index of the Metadata object in the List, identified by the
     * given metaKey. Will return -1 if not found, or the list is null or empty.
     *
     * @param metaKey
     * @return
     */
    @JsonIgnore
    public int hasMetadata(String metaKey) {
        int index = -1;

        if (metadata == null || metadata.isEmpty()) {
            return index;
        }

        for (int i = 0; i < metadata.size(); i++) {
            Metadata meta = metadata.get(i);
            if (meta.getKey().equals(metaKey)) {
                index = i;
                break;
            }
        }
        return index;
    }

    /**
     * Returns the value of the Metadata object, identified by the given
     * metaKey. Will return null if list is null or empty or the key does not
     * exist.
     *
     * @param metaKey
     * @return
     */
    @JsonIgnore
    public String getMetadata(String metaKey) {
        String metaValue = null;

        if (metadata == null || metadata.isEmpty()) {
            return metaValue;
        }

        for (int i = 0; i < metadata.size(); i++) {
            Metadata meta = metadata.get(i);
            if (meta.getKey().equals(metaKey)) {
                metaValue = meta.getValue();
                break;
            }
        }
        return metaValue;
    }

    /**
     * Return the Metadata object of the list that corresponds to the metaKey.
     * Will return null if list is null or empty or no such key exists.
     *
     * @param metaKey
     * @return
     */
    @JsonIgnore
    public Metadata getMetadataObj(String metaKey) {
        Metadata obj = null;

        if (metadata == null || metadata.isEmpty()) {
            return obj;
        }

        for (int i = 0; i < metadata.size(); i++) {
            Metadata meta = metadata.get(i);
            if (meta.getKey().equals(metaKey)) {
                obj = meta;
                break;
            }
        }
        return obj;
    }

    /**
     * Removes the metadata in the list identified by the specific key. Will
     * return false, if the list is null or empty or the key does not exist.
     *
     * @param metaKey
     * @return
     */
    @JsonIgnore
    public boolean removeMetadata(String metaKey) {
        boolean deleted = false;

        if (metadata == null || metadata.isEmpty()) {
            return deleted;
        }

        for (int i = 0; i < metadata.size(); i++) {
            Metadata meta = metadata.get(i);
            if (meta.getKey().equals(metaKey)) {
                metadata.remove(i);
                deleted = true;
                break;
            }
        }
        return deleted;
    }

    @JsonIgnore
    public int[] getDimensions() {
        Metadata dimension = this.getMetadataObj(Metadata.DIMENSION);

        String dimensionsStr = null;

        if (dimension != null) {
            dimensionsStr = dimension.getValue();
        } else {
            return null;
        }

        String[] dimensions = dimensionsStr.split("x");
        switch (dimensions.length) {
            case 2:
                int x = Integer.parseInt(dimensions[0]);
                int y = Integer.parseInt(dimensions[1]);
                return new int[]{x, y};
            case 1:
                int v = Integer.parseInt(dimensions[0]);
                return new int[]{v, v};
            default:
                return new int[2];
        }
    }

    /**
     * ***********************************************
     */

    /**
     * @param fileuri
     * @return the part of the fileuri after the last occurrence of the "/" or
     * null in case of exception.
     */
    public String fileID() {
        if (tmpuri == null) {
            return null;
        }
        String path = tmpuri.toString();
        try {
            //Deprecated due to new API stateless architecture 
            //Get path after the last slash and until the beginnings of ;jsessionid			
            return path.substring((path.lastIndexOf('/') + 1), path.length());
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * ***********************************************
     */
    /**
     * ***** ODT NO OF PAGES PER FILEDAA *************
     */
    public List<PageInfo> odtNoPages() {
        int noPages = 1;

        if (metadata != null && !metadata.isEmpty()) {
            for (Metadata meta : metadata) {
                if (meta.getKey().equals(MetadataKey.NO_PAGES.toString())) {
                    noPages = Integer.parseInt(meta.getValue());
                    break;
                }
            }
        }
        List<PageInfo> pages = new ArrayList<PageInfo>();

        for (int i = 1; i <= noPages; i++) {
            pages.add(new PageInfo(i, (i == noPages)));
        }
        return pages;
    }

    public enum IMAGE {
        PHOTO,
        SIGNATURE;
    }

    static class PageInfo {

        private int pageNo;
        private boolean isLast;

        protected PageInfo(int pageNo, boolean isLast) {
            this.pageNo = pageNo;
            this.isLast = isLast;
        }

        protected int getPageNo() {
            return pageNo;
        }

        protected boolean getIsLast() {
            return isLast;
        }
    }

    /**
     * ***********************************************
     */
    @JsonIgnore
    public boolean isPDF() {
        return "application/pdf".equals(mimeType);
    }

    @JsonIgnore
    public boolean isImage() {
        return (mimeType != null && mimeType.indexOf("image") > -1);
    }

    @JsonIgnore
    public boolean nonEmpty() {
        return !checkEmpty();
    }

    @JsonIgnore
    public boolean checkEmpty() {
        return (data == null || (data != null && data.length == 0));
    }
}
