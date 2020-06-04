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
package europass.ewa;

import static org.junit.Assert.assertThat;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

public class RandomTest {

    @Test
    public void splitsAtDash1() {

        String[] p = Utils.splitAtSlash("/skillsPassport/learnerInfo");

        assertThat(p.length, CoreMatchers.is(3));

        assertThat(p[0], CoreMatchers.is(""));
    }

    @Test
    public void splitsAtDash2() {

        String[] p = Utils.splitAtSlash("./identification/personName");

        assertThat(p.length, CoreMatchers.is(3));

        assertThat(p[0], CoreMatchers.is("."));
    }

    @Test
    public void splitsAtDash3() {

        String[] p = Utils.splitAtSlash(".");

        assertThat(p.length, CoreMatchers.is(1));

        assertThat(p[0], CoreMatchers.is("."));
    }

    @Test
    public void splitsAtDash4() {

        String[] p = Utils.splitAtSlash("");

        assertThat(p.length, CoreMatchers.is(0));

    }

    @Test
    public void splitsAtDash5() {

        String[] p = Utils.splitAtSlash("/");

        assertThat(p.length, CoreMatchers.is(0));

    }

    @Test
    public void splitsAtDash6() {

        String[] p = Utils.splitAtSlash("babis");

        assertThat(p.length, CoreMatchers.is(1));

        assertThat(p[0], CoreMatchers.is("babis"));
    }

    @Test
    public void parseBooleanEmptyString() {
        boolean actual = Boolean.parseBoolean("");

        assertThat(actual, CoreMatchers.is(false));
    }

    @Test
    public void parseBooleanNullString() {
        boolean actual = Boolean.parseBoolean(null);

        assertThat(actual, CoreMatchers.is(false));
    }
}
