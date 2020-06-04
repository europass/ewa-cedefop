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
package europass.ewa.services.editor.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import europass.ewa.resources.JsonResourceBundle;
import org.apache.commons.lang.StringUtils;

import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public abstract class ShareFeedbackResource {

    protected static final String EUROPASS_SHARE_DO_NOT_REPLY_ADDRESS = "europass-ewa-services.share.mail.sender";
    protected static final String EUROPASS_SHARE_SMTP_SERVER = "europass-ewa-services.share.mail.smtp.server";
    protected static final String EUROPASS_SHARE_SMTP_PORT = "europass-ewa-services.share.mail.smtp.port";
    protected static final String EUROPASS_SHARE_SMTP_USER = "europass-ewa-services.share.mail.smtp.user";
    protected static final String EUROPASS_SHARE_SMTP_PASS = "europass-ewa-services.share.mail.smtp.password";
    protected static final String EUROPASS_SHARE_SMTP_SSL = "europass-ewa-services.share.mail.smtp.ssl";
    protected static final String EUROPASS_SHARE_SMTP_TLS = "europass-ewa-services.share.mail.smtp.tls";
    protected static final String EUROPASS_EMAIL_TEMPLATE_SHARE_DOCUMENT_FOR_REVIEW = "europass-ewa-services.mail.templates.share.document.for.review";
    protected static final String EUROPASS_EMAIL_TEMPLATE_SHARE_DOCUMENT_POSTBACK = "europass-ewa-services.mail.templates.share.document.postback";

    protected static final String X_ORIGINATING_IP_HEADER_NAME = "X-Originating-IP";
    protected static final String FORM_PARAM_USER_AGENT = "user-agent";

    protected static String getEmailTextFromBundle(final ResourceBundle bundle, final String propertyKey) {

        final Locale defaultLocale = Locale.getDefault();
        final ResourceBundle defaultBundle = ResourceBundle.getBundle("bundles/EmailText", defaultLocale, new JsonResourceBundle.Control(new ObjectMapper()));

        String value = StringUtils.EMPTY;
        try {
            value = bundle.getString(propertyKey);
        } catch (final MissingResourceException e) {
            value = defaultBundle.getString(propertyKey);
        }

        if (value == null) {
            value = StringUtils.EMPTY;
        }

        return value;
    }

    @JsonRootName(value = "data")
    protected static class SharedCloudInfo {

        @JsonProperty("Locale")
        private String locale;
        @JsonProperty("Sender")
        private String sender;
        @JsonProperty("Subject")
        private String subject;
        @JsonProperty("Link")
        private String link;
        @JsonProperty("Email")
        private String email;
        @JsonProperty("FullName")
        private String name;
        @JsonProperty("Cc")
        private String cc;
        @JsonProperty("Message")
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getSender() {
            return this.sender;
        }

        public void setSender(String sender) {
            this.sender = sender;
        }

        public String getLocale() {
            return this.locale;
        }

        public void setLocale(String locale) {
            this.locale = locale;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCc() {
            return cc;
        }

        public void setCc(String cc) {
            this.cc = cc;
        }
    }

    protected void modelSetCommonEmailTemplateAttributes(final Map model, final ResourceBundle bundle, final SharedCloudInfo contactInfo) {

        model.put("$$template.header.right.section.text$$", getEmailTextFromBundle(bundle, "email.template.main.header.right.section.text"));
        model.put("$$template.header.right.section.url$$", getEmailTextFromBundle(bundle, "email.template.main.header.right.section.url"));
        model.put("$$template.main.message.title$$", getEmailTextFromBundle(bundle, "email.template.share.for.review.main.message.title"));
        model.put("$$template.main.message.content$$", contactInfo.getMessage());
        model.put("$$template.main.redirect.text$$", getEmailTextFromBundle(bundle, "email.template.share.for.main.redirect.text"));
        model.put("$$template.main.disclaimer.text$$", getEmailTextFromBundle(bundle, "email.template.share.for.review.main.disclaimer.text"));
        model.put("$$template.footer.link.contact.name$$", getEmailTextFromBundle(bundle, "email.template.main.footer.link.contact.name"));
        model.put("$$template.footer.link.contact.url$$", getEmailTextFromBundle(bundle, "email.template.main.footer.link.contact.url"));
        model.put("$$template.footer.link.legalnotice.name$$", getEmailTextFromBundle(bundle, "email.template.main.footer.link.legal.notice.name"));
        model.put("$$template.footer.link.legalnotice.url$$", getEmailTextFromBundle(bundle, "email.template.main.footer.link.legal.notice.url"));
        model.put("$$template.footer.link.trademark.name$$", getEmailTextFromBundle(bundle, "email.template.main.footer.trademark.name"));
        model.put("$$template.footer.social.media.fb.url$$", getEmailTextFromBundle(bundle, "email.template.main.footer.social.media.facebook.link"));
        model.put("$$template.footer.social.media.twt.url$$", getEmailTextFromBundle(bundle, "email.template.main.footer.social.media.twitter.link"));
        model.put("$$template.footer.social.media.yt.url$$", getEmailTextFromBundle(bundle, "email.template.main.footer.social.media.youtube.link"));
        model.put("$$template.footer.social.media.pnt.url$$", getEmailTextFromBundle(bundle, "email.template.main.footer.social.media.pinterest.link"));
        model.put("$$template.footer.social.media.inst.url$$", getEmailTextFromBundle(bundle, "email.template.main.footer.social.media.instagram.link"));
    }

}
