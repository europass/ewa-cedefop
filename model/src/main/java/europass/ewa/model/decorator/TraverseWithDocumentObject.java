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

import java.util.List;

import europass.ewa.model.SkillsPassport;

/**
 * Concrete implementation of the @WithDocument interface.
 *
 * @author ekar
 *
 */
public class TraverseWithDocumentObject implements TraverseWithDocument {

    private WithDocument withDocument;

    public TraverseWithDocumentObject(WithDocument withDocument) {
        this.withDocument = withDocument;
    }

    @Override
    public <E extends WithDocument> E withDocument(E object, SkillsPassport esp) {
        if (object == null) {
            return object;
        }
        object.withDocument(esp);
        return object;
    }

    @Override
    public <E extends WithDocument> List<E> withDocument(List<E> list, SkillsPassport esp) {
        if (list == null) {
            return list;
        }
        WithDocumentList<E> aList = new WithDocumentList<E>(list);
        aList.withDocument(esp);
        return aList;
    }

    @Override
    public SkillsPassport getDocument() {
        return this.withDocument.getDocument();
    }

}
