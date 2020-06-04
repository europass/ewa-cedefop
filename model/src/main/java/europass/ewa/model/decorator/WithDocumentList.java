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
package europass.ewa.model.decorator;

import java.util.AbstractList;
import java.util.List;
import java.util.Locale;

import com.fasterxml.jackson.annotation.JsonIgnore;

import europass.ewa.model.SkillsPassport;

public class WithDocumentList<E extends WithDocument> extends AbstractList<E> implements WithDocument {

    protected final List<E> wrapped;

    private SkillsPassport document;

    // -----------------------------------------//
    public WithDocumentList(List<E> wrapped) {
        this.wrapped = wrapped;
    }

    // ------------- Abstract List ------------//
    @Override
    public E get(int index) {
        E e = wrapped.get(index);
        if (e == null) {
            return null;
        }
        e.withDocument(document);
        return e;
    }

    @Override
    public int size() {
        if (wrapped == null) {
            return 0;
        }
        return wrapped.size();
    }

    @Override
    public E set(int index, E element) {
        return wrapped.set(index, element);
    }

    @Override
    public boolean add(E e) {
        return wrapped.add(e);
    }

    @Override
    public void add(int index, E element) {
        wrapped.add(index, element);
    }

    @Override
    public E remove(int index) {
        return wrapped.remove(index);
    }

    @Override
    public void withDocument(SkillsPassport document) {
        this.document = document;
    }

    @Override
    public SkillsPassport getDocument() {
        return this.document;
    }

    @Override
    public Locale getLocale() {
        if (this.document == null) {
            return DEFAULT_LOCALE;
        }

        Locale locale = this.document.getLocale();

        if (locale == null) {
            return DEFAULT_LOCALE;
        }

        return locale;
    }

    /**
     * Overrides checkEmpty from WithDocument interface
     */
    @Override
    public boolean checkEmpty() {
        if (wrapped == null) {
            return true;
        }
        if (wrapped.isEmpty()) {
            return true;
        }
        for (E item : wrapped) {
            if (item == null) {
                continue;
            }
            if (!item.checkEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean nonEmpty() {
        if (wrapped == null) {
            return false;
        }
        if (wrapped.isEmpty()) {
            return false;
        }
        for (E item : wrapped) {
            if (item == null) {
                continue;
            }
            if (item.nonEmpty()) {
                return true;
            }
        }
        return false;
    }

    @JsonIgnore
    public boolean hasOnlyOne() {
        return this.size() == 1;
    }

}
