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
package europass.ewa.tools.utils;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailMessage {

	private String subject;
	private String body;
	
	private EmailMessage(Builder builder){

		subject = builder.subject;
		body = builder.body;
	}

	public String getSubject() {
		return subject;
	}

	public String getBody() {
		return body;
	}

	public static class Builder{
		
		private final MimeBodyPart textPart;
		private String subject;
		private String body;

		private final static String MESSAGE_RECIPIENTS_PROPERTY = "europass-ewa-tools-ganalytics.mail.to.recipients";
		private final static String MESSAGE_CHARSET_PROPERTY = "europass-ewa-tools-ganalytics.mail.message.charset";
		private final static String MESSAGE_SENDER_PROPERTY = "europass-ewa-tools-ganalytics.mail.sender";
		
		public Builder(Properties props, MimeMessage message, String subject, String body){

			textPart = new MimeBodyPart();
			
			try {

				String recipients = props.getProperty(MESSAGE_RECIPIENTS_PROPERTY);
				String charset = props.getProperty(MESSAGE_CHARSET_PROPERTY);
				String sender = props.getProperty(MESSAGE_SENDER_PROPERTY);
				
//				message.setHeader("X-Originating-IP", "");
				message.setFrom(new InternetAddress(sender));
				message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
				message.setSubject(subject, charset);
				
				textPart.setText(body, charset, "html");
				
				Multipart mp = new MimeMultipart();
				mp.addBodyPart(textPart);

				message.setContent(mp);
				
				
			} catch (MessagingException e) {
				e.printStackTrace();
			}
		}
		
		public Builder subject(String subject){
			this.subject = subject;
			return this;
		}

		public Builder body(String body){
			this.body = body;
			return this;			
		}

		public EmailMessage build(){
			return new EmailMessage(this);
		}
		
	}
}
