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
package europass.ewa.services.conversion.steps;

import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import javax.mail.util.ByteArrayDataSource;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;

import europass.ewa.Constants;
import europass.ewa.enums.ConversionFileType;
import europass.ewa.enums.LogFields;
import europass.ewa.enums.ServerModules;
import europass.ewa.logging.ExtraLogInfo;
import europass.ewa.mail.MailSender;
import europass.ewa.model.SkillsPassport;
import europass.ewa.resources.JsonResourceBundle;
import europass.ewa.services.annotation.Default;
import europass.ewa.services.conversion.model.ExportableModel;
import europass.ewa.services.enums.EmailStatus;
import europass.ewa.services.exception.ApiException;

public class EmailDocumentStep extends AbstractDocumentGenerationStep {

    private static final Logger LOG = LoggerFactory.getLogger(EmailDocumentStep.class);

    private static final String module = ServerModules.SERVICES_EDITORS.getModule();

    private final MailSender mailer;

    private final Locale defaultLocale;

    @Inject
    public EmailDocumentStep(@Default Locale defaultLocale, MailSender mailer) {

        this.defaultLocale = defaultLocale;

        this.mailer = mailer;
    }

    @Override
    public void doStep(ExportableModel model) {
        final long time = System.currentTimeMillis();

        String recipient = model.getRecipient();

        if (!Strings.isNullOrEmpty(recipient)) {
            sendByEmail(model, recipient);
        }

        LOG.debug("finished step " + this + " after " + (System.currentTimeMillis() - time) + "ms");
        super.doStep(model);
    }

    /**
     * Sent by email the Document as attachment
     *
     * @param modelContainer
     * @param recipient
     * @return
     */
    private EmailStatus sendByEmail(ExportableModel modelContainer, String recipient) {

        // Validate recipient's address
        if (!mailer.isValidAddress(recipient)) {
            throw ApiException.addInfo(new ApiException("The provided email is not a valid email address", EmailStatus.INVALID_EMAIL.getDescription(), Status.BAD_REQUEST),
                    new ExtraLogInfo().add(modelContainer.getExtraLogInfo()).add(LogFields.MODULE, module));
        }

        MimeMessage message = mailer.newMessage();

        SkillsPassport esp = modelContainer.getModel();
        // Get the localisable labels -
        // "Resource bundle instances created by the getBundle factory methods are cached by default"
        Locale locale = esp.getLocale();
        ResourceBundle bundle = ResourceBundle.getBundle("bundles/EmailText", locale == null ? defaultLocale : locale,
                new JsonResourceBundle.Control(new ObjectMapper()));
        try {
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            String acronym = modelContainer.getDocumentType().getAcronym();
            // 1. Subject
            String subject = null;
            try {
                subject = bundle.getString("email.subject." + acronym);
            } catch (Exception e) {
                subject = "Missing Text: Europass CV";
            }
            message.setHeader("X-Originating-IP", modelContainer.getRecipientIp());
            message.setSubject(subject, "UTF-8");

            // 2. Content
            MimeBodyPart textPart = new MimeBodyPart();
            String content = null;
            try {
                content = bundle.getString("email.body." + acronym);
            } catch (Exception e) {
                content = "Missing Text: Automatically generated email by Europass";
            }
            textPart.setText(content, "UTF-8", "html");

            ConversionFileType fileType = modelContainer.getFileType();
            // 3. Attachment
            MimeBodyPart attachFilePart = new MimeBodyPart();
            DataSource ds = new ByteArrayDataSource(modelContainer.asBytes(), fileType.getMimeType());
            attachFilePart.setDataHandler(new DataHandler(ds));
            attachFilePart.setHeader("Content-Type", ds.getContentType());
            attachFilePart.setHeader("Content-Transfer-Encoding", "BASE64");
            String filename = esp.getFilename();
            try {
                // encode the attachment file name
                filename = MimeUtility.encodeText(filename, Constants.UTF8_ENCODING, "Q");
            } catch (UnsupportedEncodingException ex) {
                LOG.error("UnsupportedEncodingException MimeUtility, Q");
                // filename without surname
                filename = esp.getSimpleFilename();
            }

            attachFilePart.setFileName(filename);

            Multipart mp = new MimeMultipart();
            mp.addBodyPart(textPart);
            mp.addBodyPart(attachFilePart);

            message.setContent(mp);
            if (!mailer.sendStandardMail(message)) {
                throw ApiException.addInfo(new ApiException("Failed to send the email", EmailStatus.NOT_SENT.getDescription(), Status.BAD_REQUEST),
                        new ExtraLogInfo().add(modelContainer.getExtraLogInfo()).add(LogFields.MODULE, module));
            }
        } catch (Exception e) {
            throw ApiException.addInfo(new ApiException(e, EmailStatus.NOT_SENT.getDescription(), Status.INTERNAL_SERVER_ERROR),
                    new ExtraLogInfo().add(modelContainer.getExtraLogInfo()).add(LogFields.MODULE, module));
        }

        return EmailStatus.SENT;
    }

}
