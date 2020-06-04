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

import static com.jayway.restassured.RestAssured.expect;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Test;

import com.jayway.restassured.http.ContentType;

import europass.ewa.services.Paths;

public class EmailRestAssured {

    private final String BASE = "/api";

    private final String json30
            = "{"
            + "\\\"SkillsPassport\\\" :{"
            + "\\\"Locale\\\" : \\\"en\\\","
            + "\\\"DocumentInfo\\\": {"
            + "\\\"DocumentType\\\": \\\"ECV_ESP\\\""
            + "},"
            + "\\\"LearnerInfo\\\" : {"
            + "\\\"Identification\\\" : {"
            + "\\\"PersonName\\\" : {"
            + "\\\"FirstName\\\" : \\\"Μπάμπης\\\","
            + "\\\"Surname\\\" : \\\"Σουγιάς\\\""
            + "}"
            + "}"
            + "}"
            + "}"
            + "}";
    private final String jsonCVWithCL
            = "{"
            + "\\\"SkillsPassport\\\" :{"
            + "\\\"Locale\\\" : \\\"en\\\","
            + "\\\"DocumentInfo\\\": {"
            + "\\\"DocumentType\\\": \\\"ECV\\\","
            + "\\\"Document\\\":["
            + "\\\"ECL\\\""
            + "]"
            + "},"
            + "\\\"LearnerInfo\\\" : {"
            + "\\\"Identification\\\" : {"
            + "\\\"PersonName\\\" : {"
            + "\\\"FirstName\\\" : \\\"Μπάμπης\\\","
            + "\\\"Surname\\\" : \\\"Σουγιάς\\\""
            + "}"
            + "}"
            + "},"
            + "\\\"CoverLetter\\\":{"
            + "\\\"Addressee\\\":{"
            + "\\\"PersonName\\\":{"
            + "\\\"Title\\\": {"
            + "\\\"Code\\\": \\\"dr\\\","
            + "\\\"Label\\\": \\\"Dr.\\\""
            + "},"
            + "\\\"FirstName\\\": \\\"John\\\","
            + "\\\"Surname\\\": \\\"Stuart\\\""
            + "}"
            + "},"
            + "\\\"Letter\\\":{"
            + "\\\"Localisation\\\" : {"
            + "\\\"Date\\\":{\\\"Year\\\": 2013,\\\"Month\\\": 10,\\\"Day\\\": 15},"
            + "\\\"Place\\\":{\\\"Municipality\\\" : \\\"Birmingham\\\"}"
            + "}"
            + "}"
            + "}"
            + "}"
            + "}";

    static final String EMAILTO = "pgia@qnr.com.gr";

    @Test
    public void helloWorld() {
        expect()
                .statusCode(200)
                .contentType(ContentType.TEXT)
                .body(equalTo("Europass: Multipart Form Data Email Services"))
                .when()
                .get(BASE + Paths.EMAIL_BASE);
    }

    private String prepareData(String json, String recipient) {
        return "{\"data\":{\"json\":\"" + json + "\",\"recipient\":\"" + recipient + "\"}}";
    }

    @Test
    public void jsonToXml() {
        String data = prepareData(json30, EMAILTO);
        given()
                .request()
                .contentType(ContentType.JSON + Paths.UTF8_CHARSET)
                .body(data)
                .expect()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .log().everything()
                .when()
                .post(BASE + Paths.EMAIL_BASE + Paths.PATH_XML);
    }

    @Test
    public void jsonToXmlNoStats() {
        String data = prepareData(json30, EMAILTO);
        given()
                .request()
                .contentType(ContentType.JSON + Paths.UTF8_CHARSET)
                .body(data)
                .queryParam("stats", "false")
                .expect()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .log().everything()
                .when()
                .post(BASE + Paths.EMAIL_BASE + Paths.PATH_XML);
    }

    @Test
    public void jsonToOdt() {
        String data = prepareData(json30, EMAILTO);
        given()
                .request()
                .contentType(ContentType.JSON + Paths.UTF8_CHARSET)
                .body(data)
                .expect()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .log().everything()
                .when()
                .post(BASE + Paths.EMAIL_BASE + Paths.PATH_OPEN_DOCUMENT);
    }

    @Test
    public void jsonToDoc() {
        String data = prepareData(json30, EMAILTO);
        given()
                .request()
                .contentType(ContentType.JSON + Paths.UTF8_CHARSET)
                .body(data)
                .expect()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .log().everything()
                .when()
                .post(BASE + Paths.EMAIL_BASE + Paths.PATH_WORD);
    }

    @Test
    public void jsonToPdf() {
        String data = prepareData(json30, EMAILTO);
        given()
                .request()
                .contentType(ContentType.JSON + Paths.UTF8_CHARSET)
                .body(data)
                .expect()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .log().everything()
                .when()
                .post(BASE + Paths.EMAIL_BASE + Paths.PATH_PDF);
    }

    //EWA-983
    @Test
    public void jsonCVWithCLToPdf() {
        String data = prepareData(jsonCVWithCL, EMAILTO);
        given()
                .request()
                .contentType(ContentType.JSON + Paths.UTF8_CHARSET)
                .body(data)
                .expect()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .log().everything()
                .when()
                .post(BASE + Paths.EMAIL_BASE + Paths.PATH_PDF);
    }
    //EWA-983

    @Test
    public void jsonCVWithCLToOdt() {
        String data = prepareData(jsonCVWithCL, EMAILTO);
        given()
                .request()
                .contentType(ContentType.JSON + Paths.UTF8_CHARSET)
                .body(data)
                .expect()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .log().everything()
                .when()
                .post(BASE + Paths.EMAIL_BASE + Paths.PATH_OPEN_DOCUMENT);
    }
}
