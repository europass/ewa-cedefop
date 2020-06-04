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
package europass.ewa.model.decorator;

import java.util.Locale;

import europass.ewa.model.SkillsPassport;

/**
 * Concrete implementation of the @WithDocument interface.
 *
 * @author ekar
 *
 */
public class WithDocumentObject implements WithDocument {

    protected SkillsPassport document;

    private final WithDocument obj;

    public WithDocumentObject(WithDocument obj) {
        this.obj = obj;
    }

    @Override
    public void withDocument(SkillsPassport document) {
        this.document = document;
    }

    @Override
    public SkillsPassport getDocument() {
        return this.document;
    }

    @Override
    public Locale getLocale() {
        if (this.document == null) {
            return DEFAULT_LOCALE;
        }

        Locale locale = this.document.getLocale();

        if (locale == null) {
            return DEFAULT_LOCALE;
        }

        return locale;
    }

    /**
     * Overrides nonEmpty from WithDocument interface
     */
    @Override
    public boolean nonEmpty() {
        if (this.obj == null) {
            return true;
        }
        return this.obj.nonEmpty();
    }

    /**
     * Overrides checkEmpty from WithDocument interface
     */
    @Override
    public boolean checkEmpty() {
        if (this.obj == null) {
            return true;
        }
        return this.obj.checkEmpty();
    }
}
