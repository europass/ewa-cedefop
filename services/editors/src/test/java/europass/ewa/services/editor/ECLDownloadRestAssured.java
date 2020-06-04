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
import static europass.ewa.services.editor.resources.DownloadDocumentResource.FORM_PARAM_DOWNLOAD_TOKEN;
import static europass.ewa.services.editor.resources.DownloadDocumentResource.FORM_PARAM_JSON;
import static europass.ewa.services.editor.resources.DownloadDocumentResource.FORM_PARAM_USER_AGENT;

import java.io.File;
import java.io.IOException;

import net.sf.uadetector.UserAgentFamily;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.jayway.restassured.http.ContentType;

import europass.ewa.enums.ContentTypes;
import europass.ewa.services.Paths;

public class ECLDownloadRestAssured {

    private final String BASE = "/api";

    String clJson = null;

    @Before
    public void prepare() throws IOException {
        final File file = new File(getClass().getResource("/cl.json").getFile());
        clJson = FileUtils.readFileToString(file);
        Assert.assertNotNull(clJson);
    }

    @Test
    public void jsonToXml() {

        given()
                .request()
                .contentType("application/x-www-form-urlencoded" + Paths.UTF8_CHARSET)
                .formParam(FORM_PARAM_JSON, clJson)
                .formParam(FORM_PARAM_USER_AGENT, UserAgentFamily.FIREFOX.getName())
                .formParam(FORM_PARAM_DOWNLOAD_TOKEN, "TEST12345TOKEN")
                .expect()
                .statusCode(200)
                .contentType(ContentType.XML + ";charset=utf-8")
                .log().everything()
                .when()
                .post(BASE + Paths.CONVERSION_BASE + Paths.PATH_XML);

        //System.out.println(xml);
    }

    @Test
    public void jsonToOdt() {
        given()
                .request()
                .contentType("application/x-www-form-urlencoded" + Paths.UTF8_CHARSET)
                .formParam(FORM_PARAM_JSON, clJson)
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
    public void jsonToPDF() {
        given()
                .request()
                .contentType("application/x-www-form-urlencoded" + Paths.UTF8_CHARSET)
                .formParam(FORM_PARAM_JSON, clJson)
                .formParam(FORM_PARAM_USER_AGENT, UserAgentFamily.FIREFOX.getName())
                .formParam(FORM_PARAM_DOWNLOAD_TOKEN, "TEST12345TOKEN")
                .expect()
                .statusCode(200)
                .contentType(ContentTypes.PDF_CT)
                .log().everything()
                .when()
                .post(BASE + Paths.CONVERSION_BASE + Paths.PATH_PDF);
    }

    @Test
    public void jsonToDoc() {
        given()
                .request()
                .contentType("application/x-www-form-urlencoded" + Paths.UTF8_CHARSET)
                .formParam(FORM_PARAM_JSON, clJson)
                .formParam(FORM_PARAM_USER_AGENT, UserAgentFamily.FIREFOX.getName())
                .formParam(FORM_PARAM_DOWNLOAD_TOKEN, "TEST12345TOKEN")
                .expect()
                .statusCode(200)
                .contentType(ContentTypes.WORD_DOC_CT)
                .log().everything()
                .when()
                .post(BASE + Paths.CONVERSION_BASE + Paths.PATH_WORD);
    }
}
