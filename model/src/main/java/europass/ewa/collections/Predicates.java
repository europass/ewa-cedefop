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
package europass.ewa.collections;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.regex.Pattern;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;

import europass.ewa.model.PrintingPreference;
import europass.ewa.model.ReferenceTo;

public final class Predicates {

    public static final String CEFR_GRID_REF = "^LearnerInfo\\.CEFLanguageLevelsGrid$";

    public static final String CL_JUSTIFICATION_PREF = "^CoverLetter\\.Justification$";
    public static final String CL_CLOSING_SALUTATION_ENABLED_NAME_PREF = "^CoverLetter\\.SignatureName";

    public static final String ESP_PHOTO_PREF = "^LearnerInfo\\.Identification\\.Photo$";

    public static final String ESP_ALL_REFERENCETO_PREFS = "\\.ReferenceTo(\\[\\d+\\])?$";

    public static final String ESP_ALL_BUT_REFERENCETO_PREFS = "^LearnerInfo(?!(\\.)?ReferenceTo(\\[\\d+\\])?)";

    public static final String REFERENCETO_PREFS = "\\.ReferenceTo\\[XXX\\]";

    public static final String REFERENCETO_PREFS_ANY_DIGIT = "\\.ReferenceTo\\[\\d+\\]";

    private Predicates() {
    }

    public static Predicate<ReferenceTo> referencesId() {
        return new ReferencesIdPredicate();
    }

    public static Predicate<ReferenceTo> referencesId(String id) {
        return new ReferencesIdPredicate(id);
    }

    private static class ReferencesIdPredicate implements Predicate<ReferenceTo>, Serializable {

        private static final long serialVersionUID = -1581528408129600883L;

        private String id;

        ReferencesIdPredicate() {
        }

        ReferencesIdPredicate(String id) {
            this.id = id;
        }

        @Override
        public boolean apply(ReferenceTo ref) {
            if (ref == null) {
                return false;
            }

            String idref = ref.getIdref();
            if (Strings.isNullOrEmpty(idref)) {
                return false;
            }
            if (Strings.isNullOrEmpty(id)) {
                return true;
            }
            return id.compareTo(idref) == 0;
        }

    }

    public static Predicate<PrintingPreference> containsPattern(String pattern) {
        return new ContainsPatternPredicate(pattern);
    }

    private static class ContainsPatternPredicate implements Predicate<PrintingPreference>, Serializable {

        private static final long serialVersionUID = 7601752432029039550L;

        private final Pattern pattern;

        ContainsPatternPredicate(Pattern pattern) {
            this.pattern = checkNotNull(pattern);
        }

        ContainsPatternPredicate(String patternStr) {
            this(Pattern.compile(patternStr));
        }

        @Override
        public boolean apply(PrintingPreference pref) {
            String prefName = pref.getName().toString();
            if (Strings.isNullOrEmpty(prefName)) {
                return false;
            }
            return pattern.matcher(prefName).find();
        }

        @Override
        public int hashCode() {
            // Pattern uses Object.hashCode, so we have to reach
            // inside to build a hashCode consistent with equals.
            return Objects.hashCode(pattern.pattern(), pattern.flags());
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof ContainsPatternPredicate) {
                ContainsPatternPredicate that = (ContainsPatternPredicate) obj;
                // Pattern uses Object (identity) equality, so we have to reach
                // inside to compare individual fields.
                return Objects.equal(pattern.pattern(), that.pattern.pattern())
                        && Objects.equal(pattern.flags(), that.pattern.flags());
            }
            return false;
        }

        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                    .add("pattern", pattern)
                    .add("pattern.flags", Integer.toHexString(pattern.flags()))
                    .toString();
        }
    }
}
