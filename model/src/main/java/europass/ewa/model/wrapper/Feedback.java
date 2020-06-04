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
package europass.ewa.model.wrapper;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.base.Strings;

import europass.ewa.enums.DescriptionProperty;
import europass.ewa.exception.TraceableException;

@JsonPropertyOrder({"level", "code", "section"})
public class Feedback {

    private Level level;

    private Code code;

    private RelevantSection section;

    private String trace;

    public Feedback() {
    }

    public Feedback(Level level, Code code) {
        this.level = level;
        this.code = code;
    }

    public Feedback(Level level, Code code, String value) {
        this.level = level;
        this.code = code;
        this.section = new RelevantSection(value);
    }

    public Feedback withError(TraceableException e) {
        this.trace = e.getTrace();
        return this;
    }

    public Feedback withError(String trace) {
        this.trace = trace;
        return this;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public Code getCode() {
        return code;
    }

    public void setCode(Code code) {
        this.code = code;
    }

    public RelevantSection getSection() {
        return section;
    }

    public void setSection(RelevantSection section) {
        this.section = section;
    }

    public String getTrace() {
        return trace;
    }

    public static class RelevantSection {

        private static final String KEY_PREFIX = "[[";
        private static final String KEY_NAME = "section";
        private static final String KEY_SUFFIX = "]]";

        private String key;

        private String value;

        public RelevantSection() {
        }

        public RelevantSection(String value) {
            this.key = KEY_PREFIX + KEY_NAME + KEY_SUFFIX;
            this.value = value;
        }

        RelevantSection(String key, String value) {
            this.key = buildKey(key);
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        void setKey(String key) {
            this.key = buildKey(key);
        }

        public String getValue() {
            return value;
        }

        void setValue(String value) {
            this.value = value;
        }

        private String buildKey(String key) {
            String finalKey = Strings.isNullOrEmpty(key) ? KEY_NAME : key;

            if (!finalKey.startsWith(KEY_PREFIX)) {
                finalKey = KEY_PREFIX + finalKey;
            }
            if (!finalKey.endsWith(KEY_SUFFIX)) {
                finalKey = finalKey + KEY_SUFFIX;
            }
            return finalKey;
        }

    }

    public static enum Level {
        INFO,
        WARN,
        ERROR;
    }

    public static enum Code implements DescriptionProperty {
        OK("ok"),
        UPLOAD_PHOTO_RATIO("esp.upload.photo.ratio"),
        UPLOAD_SIGNATURE_RATIO("esp.upload.signature.ratio"),
        UPLOAD_PHOTO("esp.upload.no.photo"),
        UPLOAD_SIGNATURE("esp.upload.no.signature"),
        UPLOAD_ATTACHMENT("esp.upload.no.attachment"),
        UPLOAD_ATTACHMENT_THUMB("esp.upload.no.attachment.thumb"),
        DOWNLOAD_PHOTO("esp.download.no.photo"),
        DOWNLOAD_SIGNATURE("esp.download.no.signature"),
        DOWNLOAD_ATTACHMENT("esp.download.no.attachment"),
        DOWNLOAD_ALL_ATTACHMENT("esp.download.no.attachments"),
        DOWNLOAD_XML_ATTACHMENT("esp.download.no.xml"),
        NO_THUMB("file.upload.no.thumb");

        private String description;

        Code(String description) {
            this.description = description;
        }

        @Override
        public String getDescription() {
            return description;
        }
    }
}
