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
package europass.ewa.mail;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SMTPSenderImplementation implements MailSender {

    private static final Logger LOG = LoggerFactory.getLogger(SMTPSenderImplementation.class);

    private static Session mailSession;

    private String mailSender;

    private EmailValidator emailValidator;

    public SMTPSenderImplementation(String mailSender, String smtpServer, String smtpPort, String smtpUser, String smtpPassword, String smtpSSL, String smtpTLS) {

        this.emailValidator = EmailValidator.getInstance();

        this.mailSender = mailSender;

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");

        props.put("mail.smtp.host", smtpServer);
        props.put("mail.smtp.port", smtpPort);
        props.put("mail.smtp.socketFactory.port", smtpPort);

        if ("true".equals(smtpSSL)) {
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.fallback", "false");
        }

        if ("true".equals(smtpTLS)) {
            props.put("mail.smtp.starttls.enable", "true");
        }

        if (smtpPassword != null && !"".equals(smtpPassword)) {
            props.put("mail.smtp.auth", "true");
            mailSession = Session.getDefaultInstance(props, new MyAuthenticator(smtpUser, smtpPassword));
        } else {
            mailSession = Session.getDefaultInstance(props, null);
        }

    }

    @Override
    public boolean sendMail(MimeMessage message) {
        try {
            Transport.send(message);
            return true;
        } catch (Exception ex) {
            LOG.error("Error sending email ", ex);
            return false;
        }
    }

    @Override
    public boolean sendStandardMail(MimeMessage message) {
        try {
            //This resulted in not showing a From address : message.setSender(new InternetAddress(mailSender));
            message.setFrom(new InternetAddress(mailSender));
            return sendMail(message);
        } catch (Exception ex) {
            LOG.error("Could not send mail ", ex);
            return false;
        }

    }

    @Override
    public MimeMessage newMessage() {
        return new MimeMessage(mailSession);
    }

    @Override
    public boolean isValidAddress(String address) {
        return this.emailValidator.isValid(address);
    }

    static class MyAuthenticator extends Authenticator {

        private String myUser;
        private String myPassword;

        MyAuthenticator(String myUser, String myPassword) {
            this.myUser = myUser;
            this.myPassword = myPassword;
        }

        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(myUser, myPassword);
        }

    }

}
