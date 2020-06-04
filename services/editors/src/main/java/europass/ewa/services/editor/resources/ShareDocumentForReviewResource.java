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

import java.io.IOException;
import java.util.*;
import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import europass.ewa.mail.TemplateMerger;
import europass.ewa.services.annotation.Default;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.inject.name.Named;
import europass.ewa.mail.MailSender;
import europass.ewa.mail.SMTPSenderImplementation;
import europass.ewa.resources.JsonResourceBundle;
import europass.ewa.services.Paths;
import europass.ewa.services.enums.EmailStatus;

import com.google.common.base.Charsets;

@Path(Paths.PATH_SHARE_FOR_REVIEW)
public class ShareDocumentForReviewResource extends ShareFeedbackResource {

    private static final Logger LOG = LoggerFactory.getLogger(ShareDocumentForReviewResource.class);

    private final MailSender mailer;
    private final Locale defaultLocale;
    private final TemplateMerger templateMerger;

    private String sender;
    private String userIP;
    private String emailTemplate;

    @Inject
    public ShareDocumentForReviewResource(final @Default Locale defaultLocale,
            final @Named(EUROPASS_SHARE_DO_NOT_REPLY_ADDRESS) String doNotReplyAddress,
            final @Named(EUROPASS_SHARE_SMTP_SERVER) String smtpServer,
            final @Named(EUROPASS_SHARE_SMTP_PORT) String smtpPort,
            final @Named(EUROPASS_SHARE_SMTP_USER) String smtpUser,
            final @Named(EUROPASS_SHARE_SMTP_PASS) String smtpPassword,
            final @Named(EUROPASS_SHARE_SMTP_SSL) String smtpSSL,
            final @Named(EUROPASS_SHARE_SMTP_TLS) String smtpTLS,
            final @Named(EUROPASS_EMAIL_TEMPLATE_SHARE_DOCUMENT_FOR_REVIEW) String emailTemplate,
            final TemplateMerger templateMerger) {

        this.sender = doNotReplyAddress;
        this.defaultLocale = defaultLocale;
        this.mailer = new SMTPSenderImplementation(doNotReplyAddress, smtpServer, smtpPort, smtpUser, smtpPassword, smtpSSL, smtpTLS);
        this.emailTemplate = emailTemplate;
        this.templateMerger = templateMerger;
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_HTML)
    public Response sendShareForReviewEmail(final SharedCloudInfo contactInfo,
            final @HeaderParam(FORM_PARAM_USER_AGENT) String userAgent,
            final @Context HttpServletRequest request) {

        userIP = request.getRemoteAddr();

        return HtmlResponseReporting.report(this.sendByEmail(contactInfo, request));
    }

    /**
     *
     * @param contactInfo
     * @param request
     * @return EmailStatus of sent email
     */
    private EmailStatus sendByEmail(final SharedCloudInfo contactInfo, final HttpServletRequest request) {

        if (contactInfo == null) {
            LOG.error("Cannot send email! Provided contact info input is null");
            return EmailStatus.NOT_SENT;
        }

        final String recipient = Strings.isNullOrEmpty(contactInfo.getEmail()) ? StringUtils.EMPTY : contactInfo.getEmail();

        if (!mailer.isValidAddress(recipient)) {
            LOG.error("Cannot send email! The provided recipient email address is not valid");
            return EmailStatus.NOT_SENT;
        }

        final String userEmail = Strings.isNullOrEmpty(contactInfo.getCc()) ? null : contactInfo.getCc();
        final String fullName = Strings.isNullOrEmpty(contactInfo.getName()) ? null : contactInfo.getName();

        Locale locale = defaultLocale;
        String resourceSuffix = StringUtils.EMPTY;
        if (!Strings.isNullOrEmpty(contactInfo.getLocale())) {

            locale = Locale.forLanguageTag(contactInfo.getLocale());
            if (!locale.equals(defaultLocale)) {
                resourceSuffix = "_" + locale;
            }
        }

        final ResourceBundle bundle = ResourceBundle.getBundle("bundles/EmailText" + resourceSuffix, locale, new JsonResourceBundle.Control(new ObjectMapper()));
        final MimeMessage message = mailer.newMessage();

        try {

            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            if (userEmail != null) {
                message.addRecipient(Message.RecipientType.CC, new InternetAddress(userEmail));
            }

            if (!mailer.isValidAddress(sender)) {
                LOG.error("Cannot send email! The provided sender email address is not valid");
                return EmailStatus.NOT_SENT;
            }

            message.setFrom(new InternetAddress(sender));

            final Map model = new LinkedHashMap();
            model.put("--fullName--", fullName);
            final String emailSubject = templateMerger.merge(model, getEmailTextFromBundle(bundle, "email.share.for.review.subject"));
            final String subject = emailSubject;
            message.setSubject(subject, "UTF-8");

            if (userIP != null) {
                message.addHeader(X_ORIGINATING_IP_HEADER_NAME, userIP);
            }

            final MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(mergeEmailTemplateDynamicAttributes(contactInfo, bundle, request), "UTF-8", "html");
            final Multipart mp = new MimeMultipart();
            mp.addBodyPart(textPart);

            message.setContent(mp);
            if (!mailer.sendMail(message)) {
                LOG.error("Failed to send the email to user when sharing CV");
                return EmailStatus.NOT_SENT;
            }
        } catch (final Exception e) {
            LOG.error("Exception when trying to send email to user when sharing CV " + e.toString());
            return EmailStatus.NOT_SENT;
        }

        return EmailStatus.SENT;
    }

    private String mergeEmailTemplateDynamicAttributes(final SharedCloudInfo contactInfo, final ResourceBundle bundle, final HttpServletRequest request) {

        try {
            final String content = IOUtils.toString(request.getSession().getServletContext().getClassLoader().getResourceAsStream(emailTemplate), Charsets.UTF_8);

            final Map model = new LinkedHashMap();

            modelSetCommonEmailTemplateAttributes(model, bundle, contactInfo);
            model.put("$$template.senderName$$", contactInfo.getName());
            model.put("$$template.main.heading$$", getEmailTextFromBundle(bundle, "email.template.share.for.review.main.heading"));
            model.put("$$template.main.review.button.text$$", getEmailTextFromBundle(bundle, "email.template.share.for.review.button.text"));
            model.put("$$template.main.review.button.share.url$$", contactInfo.getLink());

            final String replacedContent = templateMerger.merge(model, content);

            return replacedContent;

        } catch (final IOException e) {
            LOG.error("Error during getting email template ", e.toString());
        }

        return null;
    }

}
