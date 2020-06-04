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
import static europass.ewa.services.editor.resources.DownloadDocumentResource.FORM_PARAM_DOWNLOAD_TOKEN;
import static europass.ewa.services.editor.resources.DownloadDocumentResource.FORM_PARAM_JSON;
import static europass.ewa.services.editor.resources.DownloadDocumentResource.FORM_PARAM_USER_AGENT;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import net.sf.uadetector.UserAgentFamily;

import org.hamcrest.CoreMatchers;
import org.junit.Test;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.path.xml.XmlPath;
import com.jayway.restassured.path.xml.XmlPath.CompatibilityMode;

import europass.ewa.enums.ContentTypes;
import europass.ewa.services.Paths;
import europass.ewa.services.enums.XmlVersion;

public class DownloadRestAssured {

    private final String BASE = "/api";

    @Test
    public void helloWorld() {
        expect()
                .statusCode(200)
                .contentType(ContentType.TEXT)
                .body(equalTo("Europass: Multipart Form Data Conversion Services"))
                .when()
                .get(BASE + Paths.CONVERSION_BASE);
    }

    @Test
    public void jsonToXml() {
        String xml
                = given()
                        .request()
                        .contentType("application/x-www-form-urlencoded" + Paths.UTF8_CHARSET)
                        .formParam(FORM_PARAM_JSON, MockObjects.json30)
                        .formParam(FORM_PARAM_USER_AGENT, UserAgentFamily.FIREFOX.getName())
                        .formParam(FORM_PARAM_DOWNLOAD_TOKEN, "TEST12345TOKEN")
                        .expect()
                        .statusCode(200)
                        .contentType(ContentType.XML + ";charset=utf-8")
                        .log().everything()
                        .when()
                        .post(BASE + Paths.CONVERSION_BASE + Paths.PATH_XML)
                        .asString();

        String surname = XmlPath.from(xml).get("SkillsPassport.LearnerInfo.Identification.PersonName.Surname");
        assertThat("Surname", surname.trim(), CoreMatchers.is("Σουγιάς"));

        String docType = XmlPath.from(xml).get("SkillsPassport.DocumentInfo.DocumentType");
        assertThat("DocumentType", docType.trim(), CoreMatchers.is("ECV_ESP"));

        String version = XmlPath.from(xml).get("SkillsPassport.DocumentInfo.XSDVersion");
        assertThat("Version", version.trim(), CoreMatchers.is(XmlVersion.LATEST.getCode()));

        String generator = XmlPath.from(xml).get("SkillsPassport.DocumentInfo.Generator");
        assertThat("Generator", generator.trim(), CoreMatchers.is("EWA"));

        String pref = XmlPath.from(xml).get("SkillsPassport.PrintingPreferences.Document.Field[0].@name");
        assertThat("First Prefs", pref, CoreMatchers.is("LearnerInfo.Identification.PersonName"));

        String genderCode = XmlPath.from(xml).get("SkillsPassport.LearnerInfo.Identification.Demographics.Gender.Code");
        assertThat("Gender Code", genderCode.trim(), CoreMatchers.is("F"));
    }

    @Test
    public void jsonToXmlNoStats() {
        String xml
                = given()
                        .request()
                        .contentType("application/x-www-form-urlencoded" + Paths.UTF8_CHARSET)
                        .formParam(FORM_PARAM_JSON, MockObjects.json30)
                        .formParam(FORM_PARAM_USER_AGENT, UserAgentFamily.FIREFOX.getName())
                        .formParam(FORM_PARAM_DOWNLOAD_TOKEN, "TEST12345TOKEN")
                        .queryParam("stats", "false")
                        .expect()
                        .statusCode(200)
                        .contentType(ContentType.XML + ";charset=utf-8")
                        .log().everything()
                        .when()
                        .post(BASE + Paths.CONVERSION_BASE + Paths.PATH_XML)
                        .asString();

        String surname = XmlPath.from(xml).get("SkillsPassport.LearnerInfo.Identification.PersonName.Surname");
        assertThat("Surname", surname.trim(), CoreMatchers.is("Σουγιάς"));
    }

    @Test
    public void jsonToXml2() {
        String xml
                = given()
                        .request()
                        .contentType("application/x-www-form-urlencoded" + Paths.UTF8_CHARSET)
                        .formParam(FORM_PARAM_JSON, MockObjects.json50)
                        .formParam(FORM_PARAM_USER_AGENT, UserAgentFamily.FIREFOX.getName())
                        .formParam(FORM_PARAM_DOWNLOAD_TOKEN, "TEST12345TOKEN")
                        .expect()
                        .statusCode(200)
                        .contentType(ContentType.XML + ";charset=utf-8")
                        .log().everything()
                        .when()
                        .post(BASE + Paths.CONVERSION_BASE + Paths.PATH_XML)
                        .asString();

        String certDate = XmlPath.from(xml).get("SkillsPassport.LearnerInfo.Skills.Linguistic.ForeignLanguageList.ForeignLanguage[0].VerifiedBy.Certificate[0].Date.@day");
        assertThat("Certificate Date Day", certDate.trim(), CoreMatchers.is("---15"));

        String certLevel = XmlPath.from(xml).get("SkillsPassport.LearnerInfo.Skills.Linguistic.ForeignLanguageList.ForeignLanguage[0].VerifiedBy.Certificate[0].Level");
        assertThat("Certificate Level", certLevel.trim(), CoreMatchers.is("C2"));

        String expCurrent = XmlPath.from(xml).get("SkillsPassport.LearnerInfo.Skills.Linguistic.ForeignLanguageList.ForeignLanguage[0].AcquiredDuring.Experience[0].Period.Current");
        assertThat("Experience Period", Boolean.valueOf(expCurrent), CoreMatchers.is(false));
    }

    @Test
    public void jsonToXmlWithAttachment() {
        String xml
                = given()
                        .request()
                        .contentType("application/x-www-form-urlencoded" + Paths.UTF8_CHARSET)
                        .formParam(FORM_PARAM_JSON, MockObjects.json30withAttachment)
                        .formParam(FORM_PARAM_USER_AGENT, UserAgentFamily.FIREFOX.getName())
                        .formParam(FORM_PARAM_DOWNLOAD_TOKEN, "TEST12345TOKEN")
                        .expect()
                        .statusCode(200)
                        .contentType(ContentType.XML + ";charset=utf-8")
                        .log().everything()
                        .when()
                        .post(BASE + Paths.CONVERSION_BASE + Paths.PATH_XML)
                        .asString();

        String surname = XmlPath.from(xml).get("SkillsPassport.LearnerInfo.Identification.PersonName.Surname");
        assertThat("Surname", surname.trim(), CoreMatchers.is("Σουγιάς"));

        String docType = XmlPath.from(xml).get("SkillsPassport.DocumentInfo.DocumentType");
        assertThat("DocumentType", docType.trim(), CoreMatchers.is("ECV_ESP"));

        String version = XmlPath.from(xml).get("SkillsPassport.DocumentInfo.XSDVersion");
        assertThat("Version", version.trim(), CoreMatchers.is(XmlVersion.LATEST.getCode()));

        String generator = XmlPath.from(xml).get("SkillsPassport.DocumentInfo.Generator");
        assertThat("Generator", generator.trim(), CoreMatchers.is("EWA"));
    }

    @Test
    public void jsonToXmlError() {
        String html
                = given()
                        .request()
                        .contentType("application/x-www-form-urlencoded" + Paths.UTF8_CHARSET)
                        .formParam(FORM_PARAM_JSON, "{ \"Whatever\" : {} }")
                        .formParam(FORM_PARAM_USER_AGENT, UserAgentFamily.FIREFOX.getName())
                        .formParam(FORM_PARAM_DOWNLOAD_TOKEN, "TEST12345TOKEN")
                        .expect()
                        .statusCode(500)
                        .contentType(ContentType.HTML)
                        .when()
                        .post(BASE + Paths.CONVERSION_BASE + Paths.PATH_XML)
                        .asString();

        XmlPath xmlPath = new XmlPath(CompatibilityMode.HTML, html);

        String error = xmlPath.get("html.script");

//		boolean jsonToModelError = error.indexOf("json-to-model") > 0;
        boolean jsonToModelError = error.indexOf("com.fasterxml.jackson.databind.JsonMappingException") > 0;

        assertThat("Error", jsonToModelError, CoreMatchers.is(true));
    }

    @Test
    public void jsonToOdt() {
        given()
                .request()
                .contentType("application/x-www-form-urlencoded" + Paths.UTF8_CHARSET)
                .formParam(FORM_PARAM_JSON, MockObjects.json50)
                .formParam(FORM_PARAM_USER_AGENT, UserAgentFamily.FIREFOX.getName())
                .formParam(FORM_PARAM_DOWNLOAD_TOKEN, "TEST12345TOKEN")
                .expect()
                .statusCode(200)
                .contentType(ContentTypes.OPEN_DOC_CT)
                .log().everything()
                .when()
                .post(BASE + Paths.CONVERSION_BASE + Paths.PATH_OPEN_DOCUMENT);
    }

    @Test
    public void eclJsonToOdt() {
        given()
                .request()
                .contentType("application/x-www-form-urlencoded" + Paths.UTF8_CHARSET)
                .formParam(FORM_PARAM_JSON, MockObjects.json50)
                .formParam(FORM_PARAM_USER_AGENT, UserAgentFamily.FIREFOX.getName())
                .formParam(FORM_PARAM_DOWNLOAD_TOKEN, "TEST12345TOKEN")
                .expect()
                .statusCode(200)
                .contentType(ContentTypes.OPEN_DOC_CT)
                .log().everything()
                .when()
                .post(BASE + Paths.CONVERSION_BASE + Paths.PATH_OPEN_DOCUMENT);
    }

}
