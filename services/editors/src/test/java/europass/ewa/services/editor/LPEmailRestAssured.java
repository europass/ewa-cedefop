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

import static com.jayway.restassured.RestAssured.given;
import static europass.ewa.services.editor.resources.EmailDocumentResource.FORM_PARAM_EMAIL_RECIPIENT;
import static europass.ewa.services.editor.resources.EmailDocumentResource.FORM_PARAM_JSON;

import org.junit.Ignore;
import org.junit.Test;

import com.jayway.restassured.http.ContentType;

import europass.ewa.services.Paths;

@Ignore
public class LPEmailRestAssured {

    private final String BASE = "/api";

    private final String json50
            = "{"
            + "\"SkillsPassport\" :{"
            + "\"Locale\" : \"en\","
            + "\"DocumentInfo\": { "
            + "\"DocumentType\" : \"ELP\""
            + "},"
            + "\"LearnerInfo\" : {"
            + "\"Skills\" : {"
            + "\"Linguistic\" : {"
            + "\"MotherTongue\" : [ {"
            + "\"Description\" : {"
            + "\"Code\" :\"el\","
            + "\"Label\" :\"Greek\""
            + "}"
            + "} ],"
            + "\"ForeignLanguage\" : [ {"
            + "\"Description\" : {"
            + "\"Code\" :\"en\","
            + "\"Label\" :\"English\""
            + "},"
            + "\"ProficiencyLevel\" : {"
            + "\"Listening\" :\"C1\","
            + "\"Reading\" :\"C2\","
            + "\"SpokenInteraction\" :\"B2\","
            + "\"SpokenProduction\" :\"B2\","
            + "\"Writing\" :\"B1\""
            + "},"
            + "\"Experience\" : [ {"
            + "\"Period\" : {"
            + "\"From\" : {"
            + "\"Year\" : 2000,"
            + "\"Month\" : 6,"
            + "\"Day\" : 10"
            + "},"
            + "\"To\" : {"
            + "\"Year\" : 2001,"
            + "\"Month\" : 8,"
            + "\"Day\" : 15"
            + "},"
            + "\"Current\" : false"
            + "},"
            + "\"Description\" :\"Summer English courses that help me to improve my spoken interaction level\""
            + "}],"
            + "\"Certificate\" : [ {"
            + "\"Title\" :\"CPE (short title)\","
            + "\"AwardingBody\":\"British Council\","
            + "\"Date\" : {"
            + "\"Year\" : 2013,"
            + "\"Month\" : 10,"
            + "\"Day\" : 15"
            + "},"
            + "\"Level\" : \"C2\""
            + "}]"
            + "} ]"
            + "}"
            + "}"
            + "}"
            + "}"
            + "}";

    @Test
    public void jsonToXml() {
        given()
                .request()
                .contentType("application/x-www-form-urlencoded" + Paths.UTF8_CHARSET)
                .formParam(FORM_PARAM_JSON, json50)
                .formParam(FORM_PARAM_EMAIL_RECIPIENT, "pgia@qnr.com.gr")
                .expect()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .log().everything()
                .when()
                .post(BASE + Paths.EMAIL_BASE + Paths.PATH_XML);
    }

    @Test
    public void jsonToXmlNoStats() {
        given()
                .request()
                .contentType("application/x-www-form-urlencoded" + Paths.UTF8_CHARSET)
                .formParam(FORM_PARAM_JSON, json50)
                .formParam(FORM_PARAM_EMAIL_RECIPIENT, "kkav@qnr.com.gr")
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
        given()
                .request()
                .contentType("application/x-www-form-urlencoded" + Paths.UTF8_CHARSET)
                .formParam(FORM_PARAM_JSON, json50)
                .formParam(FORM_PARAM_EMAIL_RECIPIENT, "pgia@qnr.com.gr")
                .expect()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .log().everything()
                .when()
                .post(BASE + Paths.EMAIL_BASE + Paths.PATH_OPEN_DOCUMENT);
    }

    @Test
    public void jsonToDoc() {
        given()
                .request()
                .contentType("application/x-www-form-urlencoded" + Paths.UTF8_CHARSET)
                .formParam(FORM_PARAM_JSON, json50)
                .formParam(FORM_PARAM_EMAIL_RECIPIENT, "ekar@qnr.com.gr")
                .expect()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .log().everything()
                .when()
                .post(BASE + Paths.EMAIL_BASE + Paths.PATH_WORD);
    }

    @Test
    public void jsonToPdf() {
        given()
                .request()
                .contentType("application/x-www-form-urlencoded" + Paths.UTF8_CHARSET)
                .formParam(FORM_PARAM_JSON, json50)
                .formParam(FORM_PARAM_EMAIL_RECIPIENT, "ekar@qnr.com.gr")
                .expect()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .log().everything()
                .when()
                .post(BASE + Paths.EMAIL_BASE + Paths.PATH_PDF);
    }

}
