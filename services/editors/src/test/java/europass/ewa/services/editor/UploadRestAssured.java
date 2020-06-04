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
import static org.hamcrest.Matchers.*;

import java.io.File;

import org.junit.Test;
import static org.junit.Assert.*;

import com.jayway.restassured.http.ContentType;

import europass.ewa.services.Paths;

public class UploadRestAssured {

    private final String BASE = "/api";

    @Test
    public void helloWorld() {
        expect()
                .statusCode(200)
                .contentType(ContentType.TEXT)
                .body(equalTo("Hello World!"))
                .when()
                .get(BASE + Paths.LOAD_BASE);
    }

    @Test
    public void uploadTxt() {
        final File file = new File(getClass().getResource("/upload/text.txt").getFile());
        assertNotNull(file);
        assertTrue(file.canRead());

        given()
                .multiPart(file)
                .expect()
                .statusCode(400)
                .contentType(ContentType.HTML)
                .log().everything()
                .when()
                .post(BASE + Paths.LOAD_BASE);
    }

    @Test
    public void uploadExcelPDF() {
        final File file = new File(getClass().getResource("/upload/excel.pdf").getFile());
        assertNotNull(file);
        assertTrue(file.canRead());

        given()
                .multiPart(file)
                .expect()
                .statusCode(415)
                .contentType(ContentType.HTML)
                .log().everything()
                .when()
                .post(BASE + Paths.LOAD_BASE);
    }

    @Test
    public void emptyPDF() {
        final File file = new File(getClass().getResource("/upload/empty.pdf").getFile());
        assertNotNull(file);
        assertTrue(file.canRead());

        given()
                .multiPart(file)
                .expect()
                .statusCode(400)
                .contentType(ContentType.HTML)
                .log().everything()
                .when()
                .post(BASE + Paths.LOAD_BASE);
    }

    @Test
    public void emptyXML() {
        final File file = new File(getClass().getResource("/upload/empty.xml").getFile());
        assertNotNull(file);
        assertTrue(file.canRead());

        given()
                .multiPart(file)
                .expect()
                .statusCode(400)
                .contentType(ContentType.HTML)
                .log().everything()
                .when()
                .post(BASE + Paths.LOAD_BASE);
    }

    @Test
    public void noAttachmentPDF() {
        final File file = new File(getClass().getResource("/upload/sample.pdf").getFile());
        assertNotNull(file);
        assertTrue(file.canRead());

        given()
                .multiPart(file)
                .expect()
                .statusCode(400)
                .contentType(ContentType.HTML)
                .log().everything()
                .when()
                .post(BASE + Paths.LOAD_BASE);
    }

    @Test
    public void otherXML() {
        final File file = new File(getClass().getResource("/upload/other.xml").getFile());
        assertNotNull(file);
        assertTrue(file.canRead());

        given()
                .multiPart(file)
                .expect()
                .statusCode(400)
                .contentType(ContentType.HTML)
                .log().everything()
                .when()
                .post(BASE + Paths.LOAD_BASE);
    }

    @Test
    public void unreadableVersionXML() {
        final File file = new File(getClass().getResource("/upload/unreadable-version.xml").getFile());
        assertNotNull(file);
        assertTrue(file.canRead());

        given()
                .log().everything()
                .multiPart(file)
                .expect()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .log().everything()
                .when()
                .post(BASE + Paths.LOAD_BASE);
    }

}
