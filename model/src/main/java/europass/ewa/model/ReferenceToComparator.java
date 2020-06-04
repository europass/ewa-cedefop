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
package europass.ewa.model;

import java.util.Comparator;

class ReferenceToComparator implements Comparator<Attachment> {

    private final LearnerInfo learnerInfo;

    ReferenceToComparator(LearnerInfo learnerInfo) {
        this.learnerInfo = learnerInfo;
    }

    @Override
    public int compare(Attachment att1, Attachment att2) {
        if (learnerInfo == null) {
            return 0;
        }

        ReferenceTo ref1 = learnerInfo.resolveReferenceTo(att1);
        ReferenceTo ref2 = learnerInfo.resolveReferenceTo(att2);

        if (ref1 == null && ref2 == null) {
            return 0;
        }

        if (ref1 == null && ref2 != null) {
            return 1;
        }

        if (ref1 != null && ref2 == null) {
            return -1;
        }

        int index1 = learnerInfo.getDocumentation().indexOf(ref1);
        int index2 = learnerInfo.getDocumentation().indexOf(ref2);

        if (index1 == index2) {
            return 0;
        }

        if (index1 > index2) {
            return 1;
        }

        if (index1 < index2) {
            return -1;
        }

        return 0;
    }
}
