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

import javax.inject.Singleton;

import europass.ewa.model.wrapper.Feedback.Code;
import europass.ewa.model.wrapper.Feedback.Level;
import europass.ewa.model.wrapper.Feedback.RelevantSection;

@Singleton
public class FeedbackFactory {

    private FeedbackFactory() {
        throw new AssertionError();
    }

    private static RelevantSection photoRelevantSection() {
        RelevantSection rel = new RelevantSection();
        rel.setKey("photo");
        return rel;
    }

    public static Feedback photoToURI() {
        Feedback f = new Feedback(Level.WARN, Code.UPLOAD_PHOTO);
        f.setSection(photoRelevantSection());
        return f;
    }

    public static Feedback photoData() {
        Feedback f = new Feedback(Level.WARN, Code.DOWNLOAD_PHOTO);
        f.setSection(photoRelevantSection());
        return f;
    }

    public static Feedback photoRatio() {
        Feedback f = new Feedback(Level.WARN, Code.UPLOAD_PHOTO_RATIO);
        f.setSection(photoRelevantSection());
        return f;
    }

    private static RelevantSection signatureRelevantSection() {
        RelevantSection rel = new RelevantSection();
        rel.setKey("signature");
        return rel;
    }

    public static Feedback signatureToURI() {
        Feedback f = new Feedback(Level.WARN, Code.UPLOAD_SIGNATURE);
        f.setSection(signatureRelevantSection());
        return f;
    }

    public static Feedback signatureData() {
        Feedback f = new Feedback(Level.WARN, Code.DOWNLOAD_SIGNATURE);
        f.setSection(signatureRelevantSection());
        return f;
    }

    public static Feedback signatureRatio() {
        Feedback f = new Feedback(Level.WARN, Code.UPLOAD_SIGNATURE_RATIO);
        f.setSection(signatureRelevantSection());
        return f;
    }

    public static Feedback attachmentToURI(String attachmentName) {
        return new Feedback(Level.WARN, Code.UPLOAD_ATTACHMENT, attachmentName);
    }

    public static Feedback attachmentToThumb(String attachmentName) {
        return new Feedback(Level.WARN, Code.UPLOAD_ATTACHMENT_THUMB, attachmentName);
    }

    public static Feedback attachmentData(String attachmentName) {
        return new Feedback(Level.WARN, Code.DOWNLOAD_ATTACHMENT, attachmentName);
    }

    public static Feedback attachmentInDocument(String attachmentName) {
        return new Feedback(Level.WARN, Code.DOWNLOAD_ATTACHMENT, attachmentName);
    }

    public static Feedback allAttachmentInDocument() {
        return new Feedback(Level.WARN, Code.DOWNLOAD_ALL_ATTACHMENT);
    }

    public static Feedback xmlAttachment() {
        return new Feedback(Level.WARN, Code.DOWNLOAD_XML_ATTACHMENT);
    }

    public static Feedback ok() {
        return new Feedback(Level.INFO, Code.OK, "ok");
    }
}
