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
package europass.ewa.model.format;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import europass.ewa.model.Achievement;
import europass.ewa.model.GenericSkill;

public class ModelWithXSSHtmlTest {

    @Test
    public void htmlSkill() {
        GenericSkill communication = new GenericSkill();

        communication.setDescription("<a onmouseover=\"alert(document.cookie)\">xxs link</a>");

        assertThat("Communication ",
                communication.getDescription(),
                is("xxs link"));
    }

    @Test
    public void htmlAchievement() {
        Achievement ach = new Achievement();

        ach.setDescription("<ul><li>ok</li><li><a onmouseover=\"alert(document.cookie)\">xxs link</a></li></ul>");

        assertThat("Achievement ",
                ach.getDescription(),
                is("<ul><li>ok</li><li>xxs link</li></ul>"));
    }

    @Test
    public void htmlOkTag() {
        GenericSkill communication = new GenericSkill();

        communication.setDescription("Apple > Orange but < Banana");

        assertThat("Communication ",
                communication.getDescription(),
                is("Apple &gt; Orange but &lt; Banana"));
    }
}
