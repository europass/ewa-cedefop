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
package europass.ewa.services.editor;

import static com.jayway.restassured.RestAssured.*;

import java.io.File;

import org.junit.Test;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

import com.jayway.restassured.http.ContentType;

import europass.ewa.services.Paths;

public class isEuropassDocumentRestAssured {

    private final String BASE = "/api";

    private final String isNotEuro = "{\"isEuro\":false}";
    //	private final String isEuro = "{\"isEuro\":true}"; //TODO add tests to check if is euro

    //@Test TODO make this pass
    public void isEuropassDocUnreadableXml() {
        final File file = new File(getClass().getResource("/upload/unreadable-version.xml").getFile());
        assertNotNull(file);
        assertTrue(file.canRead());

        given()
                .log().everything()
                .multiPart(file)
                .expect()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .body(containsString(isNotEuro))
                .log().everything()
                .when()
                .post(BASE + Paths.LOAD_BASE + "/isEuroDoc");
    }

    @Test
    public void isEuropassDocEmptyPdf() {
        final File file = new File(getClass().getResource("/upload/empty.pdf").getFile());
        assertNotNull(file);
        assertTrue(file.canRead());

        given()
                .log().everything()
                .multiPart(file)
                .expect()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .body(containsString(isNotEuro))
                .log().everything()
                .when()
                .post(BASE + Paths.LOAD_BASE + "/isEuroDoc");
    }

    @Test
    public void isEuropassDocEmptyXml() {
        final File file = new File(getClass().getResource("/upload/empty.xml").getFile());
        assertNotNull(file);
        assertTrue(file.canRead());

        given()
                .log().everything()
                .multiPart(file)
                .expect()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .body(containsString(isNotEuro))
                .log().everything()
                .when()
                .post(BASE + Paths.LOAD_BASE + "/isEuroDoc");
    }

    //@Test TODO investigate this test returns true because it is detected as xml type and it is loaded, when it is called from the browser, it finds the content to be ZIP type
    //there are some differences in the mediatype detection of the bis (buffered input stream)
    public void isEuropassDoc() {
        final File file = new File(getClass().getResource("/upload/excel.xml").getFile());
        assertNotNull(file);
        assertTrue(file.canRead());

        given()
                .log().everything()
                .multiPart(file)
                .expect()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .body(containsString(isNotEuro))
                .log().everything()
                .when()
                .post(BASE + Paths.LOAD_BASE + "/isEuroDoc");
    }

}
