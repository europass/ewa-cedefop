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

import java.util.List;

public class AttachmentsInfo {

    private boolean showable;

    private boolean includeInline;

    private List<Attachment> visibleAttachments;

    public boolean isShowable() {
        return showable;
    }

    public void setShowable(boolean showable) {
        this.showable = showable;
    }

    public boolean isIncludeInline() {
        return includeInline;
    }

    public void setIncludeInline(boolean includeInline) {
        this.includeInline = includeInline;
    }

    public List<Attachment> getVisibleAttachments() {
        return visibleAttachments;
    }

    public void setVisibleAttachments(List<Attachment> visibleAttachments) {
        this.visibleAttachments = visibleAttachments;
    }

}
