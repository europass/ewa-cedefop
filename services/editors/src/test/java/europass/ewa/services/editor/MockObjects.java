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

public final class MockObjects {

    public static final String json50
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
    public static final String json30
            = "{"
            + "\"SkillsPassport\" :{"
            + "\"Locale\" : \"en\","
            + "\"DocumentInfo\": { "
            + "\"DocumentType\" : \"ECV_ESP\""
            + "},"
            + "\"LearnerInfo\" : {"
            + "\"Identification\" : {"
            + "\"PersonName\" : {"
            + "\"FirstName\" : \"Μπάμπης\","
            + "\"Surname\" : \"Σουγιάς\""
            + "},"
            + "\"Demographics\" : {"
            + "\"Gender\" : {"
            + "\"Code\" : \"F\""
            + "},"
            + "\"Nationality\" : ["
            + "{\"Code\" : \"EL\"},"
            + "{\"Code\" : \"UK\"}"
            + "]"
            + "}"
            + "}"
            + "}"
            + "}"
            + "}";
    public static final String json30withAttachment
            = "{"
            + "\"SkillsPassport\" :{"
            + "\"Locale\" : \"el\","
            + "\"LearnerInfo\" : {"
            + "\"Identification\" : {"
            + "\"PersonName\" : {"
            + "\"FirstName\" : \"Μπάμπης\","
            + "\"Surname\" : \"Σουγιάς\""
            + "}"
            + "}"
            + "},"
            + "\"Attachment\": ["
            + "{"
            + "\"Id\": \"ATT_1272263651542\","
            + "\"Name\": \"Download.png\","
            + "\"MimeType\": \"image/png\","
            + "\"TempURI\": \"http://ewa.eu/file/test123\""
            + "}"
            + "]"
            + "}"
            + "}";

}
