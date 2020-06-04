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

import europass.ewa.Utils;

public class ByteMetadata {

    private byte[] data;

    private float width;

    private float height;

    public ByteMetadata() {
    }

    public ByteMetadata(byte[] data, float width, float height) {
        this.data = data;
        this.width = width;
        this.height = height;
    }

    public ByteMetadata(ByteMetadata other) {
        super();
        if (other != null) {
            this.data = other.getData();
            this.width = other.getWidth();
            this.height = other.getHeight();
        }
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public float getWidth() {
        return width;
    }

    public float getWidthAsInches() {
        return Utils.toInches(width);
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public float getHeightAsInches() {
        return Utils.toInches(height);
    }

    public void setHeight(float height) {
        this.height = height;
    }

}
