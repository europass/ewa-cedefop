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
package europass.ewa.model.wrapper;

import java.util.AbstractList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonRootName;

@JsonRootName("Feedback")
public class FeedbackList extends AbstractList<Feedback> {

    private final List<Feedback> list;

    public FeedbackList(List<Feedback> list) {
        this.list = list;
    }

    @Override
    public boolean add(Feedback feedback) {
        return this.list.add(feedback);
    }

    @Override
    public Feedback get(int index) {
        return this.list.get(index);
    }

    @Override
    public int size() {
        return this.list.size();
    }

}
