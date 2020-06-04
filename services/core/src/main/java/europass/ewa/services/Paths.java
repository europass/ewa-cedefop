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
package europass.ewa.services;

public final class Paths {

    public static final String UTF8_CHARSET = ";charset=utf-8";

    public static final String DOCUMENT_BASE = "/document";

    public static final String CONVERSION_BASE = DOCUMENT_BASE + "/to";
    public static final String EMAIL_BASE = DOCUMENT_BASE + "/email";
    public static final String CLOUD_BASE = DOCUMENT_BASE + "/cloud";

    public static final String CLOUD_STORAGE_BASE = "/cloud/storage";
    public static final String CLOUD_STORAGE_CALLBACK = "/callback";
    public static final String CLOUD_STORAGE_CALLBACK_FINAL = "/callback/final";
    public static final String CLOUD_STORAGE_PROVIDER_GDRIVE = "googledrive";
    public static final String CLOUD_STORAGE_PROVIDER_DROPBOX = "dropbox";
    public static final String CLOUD_STORAGE_PROVIDER_SKYDRIVE = "skydrive";

    public static final String SHARE = "/share";

    public static final String CLOUD_STORAGE_GDRIVE_LOAD = "/gdrive/load";

    public static final String SOCIAL_IMPORT_BASE = "/social/import";
    public static final String SOCIAL_IMPORT_CALLBACK = "/callback";
    public static final String SOCIAL_IMPORT_CALLBACK_FINAL = "/callback/final";
    public static final String SOCIAL_IMPORT_PROVIDER_LINKEDIN = "linkedin";
    public static final String SOCIAL_IMPORT_HELPER = "/helper";

    public static final String LOGGING_BASE = "/logging";

    public static final String NOT_AVAILABLE = "/not-available";

    public static final String FILES_BASE = "/files";
    public static final String ATTACHMENT_BASE = "/file";
    public static final String ATTACHMENT_THUMB_PATH = "/thumb";
    public static final String PHOTO_BASE = "/photo";
    public static final String SIGNATURE_BASE = "/signature";

    public static final String LOAD_BASE = "/load";

    public static final String PATH_JSON = "/json";
    public static final String PATH_JSON_CV_ONLY = "/json-cv";
    public static final String PATH_JSON_ESP_ONLY = "/json-esp";

    public static final String PATH_XML = "/xml";
    public static final String PATH_XML_CV_ONLY = "/xml-cv";
    public static final String PATH_XML_ESP_ONLY = "/xml-esp";

    public static final String PATH_PDF = "/pdf";
    public static final String PATH_PDF_CV_ONLY = "/pdf-cv";
    public static final String PATH_PDF_ESP_ONLY = "/pdf-esp";

    public static final String PATH_WORD = "/word";
    public static final String PATH_WORD_CV_ONLY = "/word-cv";
    public static final String PATH_WORD_ESP_ONLY = "/word-esp";

    public static final String PATH_OPEN_DOCUMENT = "/opendoc";
    public static final String PATH_OPEN_DOCUMENT_CV_ONLY = "/opendoc-cv";
    public static final String PATH_OPEN_DOCUMENT_ESP_ONLY = "/opendoc-esp";

    public static final String EXTRACT_XML_ATTCH_BASE = "/document/extraction";

    public static final String UPGRADE_BASE = "/document/upgrade";

    public static final String SUPPORTED_LANGUAGES_BASE = "/languages";

    public static final String ACCEPT_LANGUAGE_HTTP_HEADER = "Accept-Language";

    public static final String PATH_CONTACT = "/contact/email";

    public static final String PATH_SHARE_FOR_REVIEW = SHARE + "/email/review";
    public static final String PATH_SHARE_FOR_REVIEW_POSTBACK = SHARE + "/email/postback";

    public static final String PATH_PARTNER_DOWNLOAD_PROXY = "/proxy-xml";

    public static final String PATH_POST_EURES = "/eures";
    public static final String PATH_POST_XING = "/xing";
    public static final String PATH_CHECK_CV_SIZE = "/checkCVSize";
    public static final String PATH_POST_MONSTER = "/monster";
    public static final String PATH_POST_CV_LIBRARY = "/cvLibrary";
    public static final String PATH_POST_ANPAL = "/anpal";
    public static final String PATH_POST_INDEED = "/indeed";

    //Suppress default constructor for noninstantiability
    private Paths() {
        throw new AssertionError();
    }

}
