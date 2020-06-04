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

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

public class MutableListTest {

    @Test
    public void addNew() {
        FileData data = new FileData("file.pdf", "application/pdf", "http://test.com/file/123");

        data.setInfo("dimension", "80x40");

        assertThat(data.getMetadata().size(), CoreMatchers.is(1));
    }

    @Test
    public void addExisting() {
        FileData data = new FileData("file.pdf", "application/pdf", "http://test.com/file/123");

        data.setInfo("dimension", "80x40");

        data.setInfo("dimension", "100x100");

        assertThat(data.getMetadata().size(), CoreMatchers.is(1));

        assertThat(data.getMetadata().get(0).getValue(), CoreMatchers.is("100x100"));
    }

    @Test
    public void getNonExisting() {
        FileData data = new FileData("file.pdf", "application/pdf", "http://test.com/file/123");

        assertNull(data.getMetadata("dimension"));
    }

    @Test
    public void removeExisting() {
        FileData data = new FileData("file.pdf", "application/pdf", "http://test.com/file/123");

        data.setInfo("dimension", "80x40");

        data.setInfo("cropping", "{ \"width\" : \"150px\",  \"height\" : \"100px\", \"x\" : \"10px\", \"y\" : \"10px\" } ");

        assertThat(data.getMetadata().size(), CoreMatchers.is(2));

        boolean deleted = data.removeMetadata("dimension");

        assertThat(deleted, CoreMatchers.is(true));

        assertThat(data.getMetadata().size(), CoreMatchers.is(1));

        assertThat(data.getMetadata().get(0).getKey(), CoreMatchers.is("cropping"));
    }

    @Test
    public void removeNonExisting() {
        FileData data = new FileData("file.pdf", "application/pdf", "http://test.com/file/123");

        data.setInfo("dimension", "80x40");

        data.setInfo("cropping", "{ \"width\" : \"150px\",  \"height\" : \"100px\", \"x\" : \"10px\", \"y\" : \"10px\" } ");

        assertThat(data.getMetadata().size(), CoreMatchers.is(2));

        boolean deleted = data.removeMetadata("scaling");

        assertThat(deleted, CoreMatchers.is(false));

        assertThat(data.getMetadata().size(), CoreMatchers.is(2));

        assertThat(data.getMetadata().get(1).getKey(), CoreMatchers.is("cropping"));
    }
}
