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

import org.junit.Test;

import com.jayway.restassured.http.ContentType;

import europass.ewa.services.Paths;

public class FeedbackEmailRestAssured {

    private final String BASE = "/api";

    private final String data = "{ \"data\" : { \"Message\": \"fadsfdasfads \","
            + "\"IncludeInfo\": true,"
            + "\"EnvironmentInfo\":{ "
            + "\"Javascript\":\"Yes\","
            + "\"Cookies\" : true ,"
            + "\"Language\":\"en\","
            + "\"Screen_Depth\":\"1440x900\","
            + "\"Color_Depth\": 24,"
            + "\"Browser_Screen\":\"1440x199\","
            + "\"Java_Enabled\": true },"
            + "\"Email\":\"kkav@treger.gr\"} }";

    private final String dataError = "{ \"data\" : { \"Message\": \"fadsfdasfads \","
            + "\"IncludeInfo\": true,"
            + "\"EnvironmentInfo\":{ "
            + "\"Javascript\":\"Yes\","
            + "\"Cookies\" : true ,"
            + "\"Language\":\"en\","
            + "\"Screen_Depth\":\"1440x900\","
            + "\"ColorDepth\": 24,"
            + "\"Browser_Screen\":\"1440x199\","
            + "\"Java_Enabled\": true },"
            + "\"Email\":\"kkav@treger.gr\"} }";

    static final String EMAILTO = "kkav@qnr.com.gr";

    @Test
    public void jsonRequest() {
        given()
                .request()
                .contentType(ContentType.JSON + Paths.UTF8_CHARSET)
                .body(data)
                .expect()
                .statusCode(200)
                .contentType(ContentType.HTML)
                .log().everything()
                .when()
                .post(BASE + Paths.PATH_CONTACT);
    }

    @Test
    public void jsonBadRequest() {
        given()
                .request()
                .contentType(ContentType.JSON + Paths.UTF8_CHARSET)
                .body(dataError)
                .expect()
                .statusCode(500)
                .contentType(ContentType.HTML)
                .log().everything()
                .when()
                .post(BASE + Paths.PATH_CONTACT);
    }

}
