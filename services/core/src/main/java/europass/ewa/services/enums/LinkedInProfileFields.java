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
package europass.ewa.services.enums;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;

public enum LinkedInProfileFields {

    /**
     * THE FIELDS FOLLOW THE REQUIREMENTS OF THE LIINKED IN PROFILE MAPPING
     */
    // IDENTIFICATION  START >>
    ID("id"),
    FIRSTNAME("first-name"),
    LASTNAME("last-name"),
    // CONTACT INFO START >>

    LOCATION("location"),
    MAIN_ADDRESS("main-address"),
    EMAIL_ADDRESS("email-address"),
    IM_ACCOUNTS("im-accounts"),
    PHONE_NUMBERS("phone-numbers"),
    PUBLIC_PROFILE_URL("public-profile-url"),
    TWITTER_ACCOUNTS("twitter-accounts"),
    MEMBER_URL_RESOURCES("member-url-resources"),
    // << CONTACT INFO END

    // DEMOGRAPHICS
    DATE_OF_BIRTH("date-of-birth"),
    // PROFILE PICTURE
    PROFILE_PICTURE_URL("picture-urls::(original)"),
    // << IDENTIFICATION END

    // HEADLINE  START >>
    HEADLINE("headline"),
    SUMMARY("summary"),
    // << HEADLINE END

    // WORK EXPERIENCE START >>
    POSITIONS("positions"),
    // << WORK EXPERIENCE END

    // EDUCATION EXPERIENCE START >>
    EDUCATIONS("educations:(id,school-name,field-of-study,start-date,end-date,degree,activities,notes)"),
    // << EDUCATION EXPERIENCE END

    // SKILLS >>

    // LANGUAGES
    LANGUAGES("languages:(id,language,proficiency:(name,level))"),
    // OTHER SKILLS >>
    INTERESTS("interests"),
    SKILLS("skills:(id,skill:(name))"),
    // << OTHER SKILLS

    // << SKILLS	

    // ADDITIONAL INFORMATION >>
    HONORS_AWARDS("honors-awards"),
    RECOMMENDATIONS("recommendations-received:(id,recommendation-type,recommendation-text,recommender)"),
    COURSES("courses:(id,name,number)"),
    PATENTS("patents:(id,title,summary,number,status:(id,name),office:(name),inventors:(name),date,url)"),
    PUBLICATIONS("publications:(id,title,summary,publisher:(name),authors:(id,name),date,url)"),
    VOLUNTEER("volunteer"),
    CERTIFICATIONS("certifications"),
    PROJECTS("projects:(id,name,description,url,start-date,end-date,is-single-date)") // << ADDITIONAL INFORMATION
    //	FIRSTNAME(""),
    //	FIRSTNAME(""),
    //	FIRSTNAME(""),
    //	FIRSTNAME(""),
    //	FIRSTNAME("")
    ;

    private String description;

    LinkedInProfileFields(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public static String fieldsRequest() {

        Joiner joiner = Joiner.on(",");

        List<String> fieldsList = new ArrayList<String>();

        for (LinkedInProfileFields fieldEnum : LinkedInProfileFields.values()) {

//			if(!fieldEnum.equals(PROFILE_PICTURE_URL))
            fieldsList.add(fieldEnum.getDescription());
        }

        String fieldsCommaSep = joiner.join(fieldsList);
        return ":(" + fieldsCommaSep + ")";
    }
}
