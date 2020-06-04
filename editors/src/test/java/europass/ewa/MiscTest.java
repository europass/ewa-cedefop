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

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import static org.junit.Assert.assertThat;

import europass.ewa.servlets.RemoteUploadServlet;

public class MiscTest {

    @Test
    public void cvesp() {

        String docType = "ECV_ESP";

        String adjusted = RemoteUploadServlet.getValidDocType(docType);

        String expected = "cv-esp";

        assertThat(docType, adjusted, CoreMatchers.is(expected));
    }

    @Test
    public void cv() {

        String docType = "ECV";

        String adjusted = RemoteUploadServlet.getValidDocType(docType);

        String expected = "cv";

        assertThat(docType, adjusted, CoreMatchers.is(expected));
    }

    @Test
    public void lp() {

        String docType = "ELP";

        String adjusted = RemoteUploadServlet.getValidDocType(docType);

        String expected = "lp";

        assertThat(docType, adjusted, CoreMatchers.is(expected));
    }

    @Test
    public void ex() {

        String docType = "EX";

        String adjusted = RemoteUploadServlet.getValidDocType(docType);

        String expected = "ex";

        assertThat(docType, adjusted, CoreMatchers.is(expected));
    }

    @Test
    public void esp() {

        String docType = "ESP";

        String adjusted = RemoteUploadServlet.getValidDocType(docType);

        String expected = "esp";

        assertThat(docType, adjusted, CoreMatchers.is(expected));
    }
}
