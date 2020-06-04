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
import static europass.ewa.services.editor.resources.DownloadDocumentResource.FORM_PARAM_JSON;
import static europass.ewa.services.editor.resources.DownloadDocumentResource.FORM_PARAM_USER_AGENT;
import net.sf.uadetector.UserAgentFamily;

import org.junit.Test;

import europass.ewa.services.Paths;

public class RemoteUploadAssured {

    private final String BASE = "/editors";

    private final String json50
            = "{"
            + "\"SkillsPassport\" :{"
            + "\"Locale\" : \"en\","
            + "\"DocumentInfo\": { "
            + "\"DocumentType\" : \"ECL\""
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
            + "},"
            + "\"CoverLetter\":{"
            + "\"Addressee\":{"
            + "\"PersonName\":{"
            + "\"Title\": {"
            + "\"Code\":\"dr\","
            + "\"Label\":\"Dr.\""
            + "},"
            + "\"FirstName\": \"John\","
            + "\"Surname\": \"Stuart\""
            + "},"
            + "\"Position\":{"
            + "\"Code\": \"12332\","
            + "\"Label\" : \"Human resource manager\""
            + "},"
            + "\"Organisation\":{"
            + "\"Name\": \"Clipper emergency center\","
            + "\"ContactInfo\":{"
            + "\"Address\":{"
            + "\"Contact\":{"
            + "\"AddressLine\": \"Wall street 42\","
            + "\"PostalCode\": \"SW1P 3AT\","
            + "\"Municipality\": \"London\","
            + "\"Country\":{"
            + "\"Code\": \"UK\","
            + "\"Label\": \"United Kingdom\""
            + "}"
            + "}"
            + "}"
            + "}"
            + "}"
            + "},"
            + "\"Letter\":{"
            + "\"Localisation\" : {"
            + "\"Date\":{"
            + "\"Year\": 2013,"
            + "\"Month\": 10,"
            + "\"Day\": 15"
            + "},"
            + "\"Place\":{"
            + "\"Municipality\" : \"Birmingham\""
            + "}"
            + "},"
            + "\"SubjectLine\": \"Ref. IT support officer/2013/01/AD\","
            + "\"OpeningSalutation\":{"
            + "\"Salutation\":{"
            + "\"Label\": \"Dear Mr.\""
            + "},"
            + "\"PersonName\":{"
            + "\"Surname\": \"Stuart\""
            + "}"
            + "},"
            + "\"Body\":{"
            + "\"Opening\" :\"Test Opening Body\","
            + "\"MainBody\": \"Test Main Body\","
            + "\"Closing\": \"Test Closing Body\""
            + "},"
            + "\"ClosingSalutation\":{"
            + "\"Label\":\"Your\'s faithfully\""
            + "}"
            + "},"
            + "\"Documentation\":{"
            + "\"InterDocument\":["
            + "{"
            + "\"ref\" : \"ECV\""
            + "},"
            + "{"
            + "\"ref\" : \"ESP\""
            + "},"
            + "{"
            + "\"ref\" : \"ELP\""
            + "}"
            + "],"
            + "\"ExtraDocument\":["
            + "{"
            + "\"Description\" : \"List of Citations\","
            + "\"href\" : \"http://myblog.com/list-of-citations\""
            + "},"
            + "{"
            + "\"Description\" : \"Video CV\","
            + "\"href\" : \"http://myvideocv.com/jim.burnett\""
            + "}"
            + "]"
            + "}"
            + "}"
            + "}"
            + "}";

    @Test
    public void jsonToRemoteUpload() {

        String response
                = given()
                        .request()
                        .contentType("application/x-www-form-urlencoded" + Paths.UTF8_CHARSET)
                        .formParam(FORM_PARAM_JSON, json50)
                        .formParam(FORM_PARAM_USER_AGENT, UserAgentFamily.FIREFOX.getName())
                        .expect()
                        .statusCode(200)
                        //					.contentType( ContentType.HTML+";charset=utf-8" )
                        .log().everything()
                        .when()
                        .post(BASE + "/remote-upload").asString();

        System.out.println(response);
    }
}
