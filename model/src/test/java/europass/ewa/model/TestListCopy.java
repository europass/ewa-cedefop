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

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TestListCopy {

    @Test
    public void testcopy() {
        @SuppressWarnings("unused")
        List<String> a;

        List<String> b = new ArrayList<>(3);
        b.add("one");
        b.add("two");
        b.add("three");

        a = b;

    }

    @SuppressWarnings("hiding")
    static class ListOf<String> extends AbstractList<String> {

        List<String> ls;

        public ListOf(List<String> ls) {
            this.ls = ls;
        }

        @Override
        public String get(int index) {
//			System.out.println( "get" );
            return ls.get(index);
        }

        @Override
        public int size() {
            return ls.size();
        }

    }
}
