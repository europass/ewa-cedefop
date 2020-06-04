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

public class ListItem {

    private int index;
    private boolean isFirst;
    private boolean isLast;
//	private boolean isLastShowable;

//	public ListItem( int index, boolean isFirst, boolean isLast, boolean isLastShowable){
//		this.index = index;
//		this.isFirst = isFirst;
//		this.isLast = isLast;
//		this.isLastShowable = isLastShowable;
//	}
    public ListItem(int index, boolean isFirst, boolean isLast) {
        this.index = index;
        this.isFirst = isFirst;
        this.isLast = isLast;
//		this.isLastShowable = isLastShowable; 
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setFirst(boolean isFirst) {
        this.isFirst = isFirst;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean isLast) {
        this.isLast = isLast;
    }
//	
//	public boolean isLastShowable() {
//		return isLastShowable;
//	}
//	public void setLastShowable(boolean isLastShowable) {
//		this.isLastShowable = isLastShowable;
//	}

}
