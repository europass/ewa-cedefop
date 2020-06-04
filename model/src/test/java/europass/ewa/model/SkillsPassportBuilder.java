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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SkillsPassportBuilder {

    private final Builder b;

    private SkillsPassportBuilder(Builder b) {
        this.b = b;
    }

    public SkillsPassport get() {
        SkillsPassport esp = new SkillsPassport();
        if (b.learnerInfo != null) {
            esp.setLearnerInfo(b.learnerInfo);
        }
        if (b.documentInfo != null) {
            esp.setDocumentInfo(b.documentInfo);
        }
        if (b.attachmentList != null) {
            esp.setAttachmentList(b.attachmentList);
        }
        if (b.documentPrintingPrefs != null) {
            esp.setDocumentPrintingPrefs(b.documentPrintingPrefs);
        }
        if (b.locale != null) {
            esp.setLocale(b.locale);
        }
        return esp;
    }

    public static class Builder {

        private Locale locale;

        private DocumentInfo documentInfo;

        private Map<String, List<PrintingPreference>> documentPrintingPrefs;

        private LearnerInfo learnerInfo;

        private List<Attachment> attachmentList;

        public Builder() {
        }

        public Builder withLocale(Locale locale) {
            this.locale = locale;
            return this;
        }

        public Builder withLearnerInfo(LearnerInfo learnerInfo) {
            this.learnerInfo = learnerInfo;
            return this;
        }

        public Builder withDocumentInfo(DocumentInfo documentInfo) {
            this.documentInfo = documentInfo;
            return this;
        }

        public Builder withAttachment(Attachment attachment) {
            boolean isNew = false;
            if (this.attachmentList == null) {
                this.attachmentList = new ArrayList<Attachment>();
                isNew = true;
            }
            if (!isNew && this.attachmentList.contains(attachment)) {
                return this;
            }
            this.attachmentList.add(attachment);
            return this;
        }

        public Builder withPrintingPrefs(String document, PrintingPreference pref) {
            if (this.documentPrintingPrefs == null) {
                this.documentPrintingPrefs = new HashMap<String, List<PrintingPreference>>();
            }
            List<PrintingPreference> prefs = this.documentPrintingPrefs.get(document);
            boolean isNew = false;
            if (prefs == null) {
                prefs = new ArrayList<PrintingPreference>();
                isNew = true;
            }
            if (!isNew && prefs.contains(pref)) {
                return this;
            }
            prefs.add(pref);

            this.documentPrintingPrefs.put(document, prefs);
            return this;
        }

        public SkillsPassportBuilder build() {
            return new SkillsPassportBuilder(this);
        }
    }

}
