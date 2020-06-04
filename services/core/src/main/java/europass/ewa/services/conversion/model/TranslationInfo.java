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
package europass.ewa.services.conversion.model;

import java.util.Locale;

public class TranslationInfo {

    private Locale locale;

    private boolean requiresTranslation;

    public TranslationInfo(Locale locale) {
        this.locale = locale == null ? Locale.ENGLISH : locale;
        this.requiresTranslation = true;
    }

    public TranslationInfo(Locale locale, boolean requiresTranslation) {
        this.locale = locale == null ? Locale.ENGLISH : locale;
        this.requiresTranslation = requiresTranslation;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public boolean isRequiresTranslation() {
        return requiresTranslation;
    }

    public void setRequiresTranslation(boolean requiresTranslation) {
        this.requiresTranslation = requiresTranslation;
    }

}
