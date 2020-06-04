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
package europass.ewa.model.conversion.json;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.net.URI;
import java.net.URISyntaxException;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;

import europass.ewa.model.FileData;

public class FileDataAsJsonTest extends JsonMapperTest {

    @Test
    public void asJson() throws URISyntaxException, JsonProcessingException {
        FileData f = new FileData();
        f.setData("data".getBytes());
        f.setMimeType("application/pdf");
        f.setName("test");
        f.setTmpuri(new URI("http://www.uri.com"));

        String json = this.getMapper().writeValueAsString(f);

        assertNotNull(json);

        assertThat("FileData XML", json.indexOf("TempURI") > 0, CoreMatchers.is(true));
    }
}
