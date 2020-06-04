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

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import europass.ewa.collections.Predicates;
import europass.ewa.model.ReferenceTo;

public class IdRefSafeList implements List<ReferenceTo> {

    private final List<ReferenceTo> list;

    public IdRefSafeList(List<ReferenceTo> list) {
        this.list = list;
    }

    @Override
    public boolean add(ReferenceTo e) {
        if (e == null) {
            return false;
        }

        String idRef = e.getIdref();
        if (Strings.isNullOrEmpty(idRef)) {
            return false;
        }

        return list.add(e);

    }

    @Override
    public ReferenceTo get(int index) {
        return list.get(index);
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return list.contains(o);
    }

    @Override
    public Iterator<ReferenceTo> iterator() {
        return list.iterator();
    }

    @Override
    public Object[] toArray() {
        return list.toArray();
    }

    @SuppressWarnings("hiding")
    @Override
    public <ReferenceTo> ReferenceTo[] toArray(ReferenceTo[] a) {
        return list.toArray(a);
    }

    @Override
    public boolean remove(Object o) {
        return list.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return list.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends ReferenceTo> c) {
        return list.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends ReferenceTo> c) {
        return list.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return list.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return list.retainAll(c);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public ReferenceTo set(int index, ReferenceTo element) {
        return list.set(index, element);
    }

    @Override
    public void add(int index, ReferenceTo element) {
        list.add(index, element);
    }

    @Override
    public ReferenceTo remove(int index) {
        return list.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return list.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return list.lastIndexOf(o);
    }

    @Override
    public ListIterator<ReferenceTo> listIterator() {
        return list.listIterator();
    }

    @Override
    public ListIterator<ReferenceTo> listIterator(int index) {
        return list.listIterator(index);
    }

    @Override
    public List<ReferenceTo> subList(int fromIndex, int toIndex) {
        return list.subList(fromIndex, toIndex);
    }

    public static IdRefSafeList getInstance(List<ReferenceTo> list) {
        if (list == null) {
            return new IdRefSafeList(Collections.<ReferenceTo>emptyList());
        }
        return new IdRefSafeList(Lists.newArrayList(Iterables.filter(list, Predicates.referencesId())));
    }

    public boolean checkEmpty() {
        if (list == null) {
            return true;
        }
        if (list.isEmpty()) {
            return true;
        }
        for (ReferenceTo item : list) {
            if (!item.checkEmpty()) {
                return false;
            }
        }
        return true;
    }
}
