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
package europass.ewa.templates;

import com.google.common.base.Strings;

import europass.ewa.model.PrintingPreference;

public class PreferenceValue {

    private PrintingPreference obj;

    public PreferenceValue(Object obj) {
        try {
            this.obj = (PrintingPreference) obj;
        } catch (final Exception e) {
        }
    }

    private final String LEFT_ALIGNED = "left-align";
    private final String MIDDLE_ALIGNED = "middle-align";
    private final String RIGHT_ALIGNED = "right-align";

    private final String LOCALISATION_ORDER_DATE_FIRST = "Date Place";

    private final String LETTER_ORDER_ADDRESSEE_FIRST_REGEXP = "^Addressee.*$";

    private final String LETTER_ORDER_LOCALISATION_FIRST_REGEXP = "^Letter\\.Localisation.*$";

    private final String LETTER_ORDER_SUBJECTLINE_FIRST_REGEXP = "^Letter\\.SubjectLine.*$";

    private final String LOCALISATION_BEFORE_SUBJECTLINE_REGEXP = "(?:.*)Letter\\.Localisation(?:.*)Letter\\.SubjectLine(?:.*)";

    private final String LOCALISATION_IMMEDIATELY_BEFORE_SUBJECTLINE_REGEXP = "(?:.*)Letter\\.Localisation Letter\\.SubjectLine(?:.*)";

    private final String LOCALISATION_IMMEDIATELY_AFTER_SUBJECTLINE_REGEXP = "(?:.*)Letter\\.SubjectLine Letter\\.Localisation(?:.*)";

    private final String LOCALISATION_IMMEDIATELY_BEFORE_CLOSINGSALUTATION_REGEXP = "(?:.*)Letter\\.Localisation Letter\\.ClosingSalutation(?:.*)";

    private final String ADDRESSEE_TITLE_FIRST_REGEXP = "^Title(?:.*)";

    private final String ADDRESSEE_FIRSTNAME_FIRST_REGEXP = "^Firstname(?:.*)";

    private final String ADDRESSEE_SURNAME_FIRST_REGEXP = "^Surname(?:.*)";

    private final String ADDRESSEE_SURNAME_AFTER_FIRSTNAME_REGEXP = "(?:.*)Firstname Surname(?:.*)";

//	private final String IS_TEXT = "(?:.*)Documentation(\\s*{\\s*)\"format\"(\\s*:\\s*)\"text\"(?:.*)";
    private final String IS_TEXT = "^text$";

    public boolean hasPosition() {
        if (this.obj == null) {
            return false;
        }
        String position = this.obj.getPosition();
        return !Strings.isNullOrEmpty(position);
    }

    public boolean show() {
        if (this.obj == null) {
            return false;
        }
        return this.obj.getShow();
    }

    public boolean isLeftAligned() {
        return checkAlignment(LEFT_ALIGNED);
    }

    public boolean isRightAligned() {
        return checkAlignment(RIGHT_ALIGNED);
    }

    public boolean isMiddleAligned() {
        return checkAlignment(MIDDLE_ALIGNED);
    }

    private boolean checkAlignment(String alignment) {
        if (this.obj == null) {
            return true;
        }
        String position = this.obj.getPosition();
        if (Strings.isNullOrEmpty(position)) {
            return true;
        }
        return position.equalsIgnoreCase(alignment);
    }

    public boolean isTitleFirst() {
        return checkOrder(ADDRESSEE_TITLE_FIRST_REGEXP);
    }

    public boolean isNameFirst() {
        return checkOrder(ADDRESSEE_FIRSTNAME_FIRST_REGEXP);
    }

    public boolean isSurnameFirst() {
        return checkOrder(ADDRESSEE_SURNAME_FIRST_REGEXP);
    }

    public boolean isSurnameAfterFirstName() {
        return checkOrder(ADDRESSEE_SURNAME_AFTER_FIRSTNAME_REGEXP);
    }

    public boolean isLocalisationDateFirst() {
        return checkOrder(LOCALISATION_ORDER_DATE_FIRST);
    }

    public boolean isAddresseeFirst() {
        return checkOrder(LETTER_ORDER_ADDRESSEE_FIRST_REGEXP);
    }

    public boolean isLocalisationFirst() {
        return checkOrder(LETTER_ORDER_LOCALISATION_FIRST_REGEXP);
    }

    public boolean isSubjectLineFirst() {
        return checkOrder(LETTER_ORDER_SUBJECTLINE_FIRST_REGEXP);
    }

    public boolean isLocalisationBeforeSubjectLine() {
        return checkOrder(LOCALISATION_BEFORE_SUBJECTLINE_REGEXP);
    }

    public boolean isLocalisationImmediatelyBeforeSubjectLine() {
        return checkOrder(LOCALISATION_IMMEDIATELY_BEFORE_SUBJECTLINE_REGEXP);
    }

    public boolean isLocalisationImmediatelyAfterSubjectLine() {
        return checkOrder(LOCALISATION_IMMEDIATELY_AFTER_SUBJECTLINE_REGEXP);
    }

    public boolean isLocalisationImmediatelyBeforeClosingSalutation() {
        return checkOrder(LOCALISATION_IMMEDIATELY_BEFORE_CLOSINGSALUTATION_REGEXP);
    }

    public boolean localizationNoMarginStyle() {

        if (isLocalisationImmediatelyAfterSubjectLine()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean subjectLineNoMarginStyle() {

        if (isLocalisationImmediatelyBeforeSubjectLine()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isText() {
        return checkFormat(IS_TEXT);
    }

    private boolean checkFormat(String matching) {
        if (this.obj == null) {
            return false;
        }
        String compared = this.obj.getFormat();

        return this.checkRegexp(compared, matching);
    }

    private boolean checkOrder(String matching) {
        if (this.obj == null) {
            return false;
        }
        String compared = this.obj.getOrder();

        return this.checkRegexp(compared, matching);
    }

    private boolean checkRegexp(String compared, String matching) {

        if (Strings.isNullOrEmpty(compared) || matching == null) {
            return false;
        }

        return compared.matches(matching);
    }

}
