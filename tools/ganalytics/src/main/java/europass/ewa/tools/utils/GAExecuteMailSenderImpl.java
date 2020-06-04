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

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.mail.internet.MimeMessage;

import org.joda.time.DateTime;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.name.Named;

import europass.ewa.mail.SMTPSenderImplementation;
import europass.ewa.tools.ga.enums.ErrorTypesRecommendations;
import europass.ewa.tools.ga.errors.GAJsonResponseError;

public class GAExecuteMailSenderImpl extends SMTPSenderImplementation {
	
	private Properties defaultProperties;
	private Map<String,GAJsonResponseError> gaErrorsMap;
	
	@Inject
	public GAExecuteMailSenderImpl(
			@Named("europass-ewa-tools-ganalytics.mail.message.charset") String charset,
			@Named("europass-ewa-tools-ganalytics.mail.to.recipients") String recipients,
			@Named("europass-ewa-tools-ganalytics.mail.sender") String mailSender, 
			@Named("europass-ewa-tools-ganalytics.mail.smtp.server") String smtpServer, 
			@Named("europass-ewa-tools-ganalytics.mail.smtp.port") String smtpPort, 
			@Named("europass-ewa-tools-ganalytics.mail.smtp.user") String smtpUser, 
			@Named("europass-ewa-tools-ganalytics.mail.smtp.password") String smtpPassword, 
			@Named("europass-ewa-tools-ganalytics.mail.smtp.ssl") String smtpSSL, 
			@Named("europass-ewa-tools-ganalytics.mail.smtp.tls") String smtpTLS) {
		
		super(mailSender,smtpServer,smtpPort,smtpUser,smtpPassword,smtpSSL,smtpTLS);
	
		this.gaErrorsMap = new HashMap<String,GAJsonResponseError>();
		
		defaultProperties = new Properties();
//		defaultProperties.setProperty("europass-ewa-tools-ganalytics.mail.message.charset", charset);
		defaultProperties.setProperty("europass-ewa-tools-ganalytics.mail.to.recipients", recipients);
		defaultProperties.setProperty("europass-ewa-tools-ganalytics.mail.sender", mailSender);
	}

	public Map<String, GAJsonResponseError> getErrorsMap() {
		return gaErrorsMap;
	}

	public void setErrorsMap(Map<String, GAJsonResponseError> errorList) {
		this.gaErrorsMap = errorList;
	}
	
	@Override
	public boolean sendStandardMail(MimeMessage message) {
		
		return super.sendMail(message);
	}
	
	@Override
	public MimeMessage newMessage() {
		return super.newMessage();
	}
	
	@Override
	public boolean isValidAddress(String address) {
		return super.isValidAddress(address);
	}
	
	public String constructFailureMessage(StringBuilder strBuilder){
		
		strBuilder.append("<table>");
		
		int index = 1;
		
		for(String key : this.getErrorsMap().keySet()){
			
			GAJsonResponseError error = this.getErrorsMap().get(key);
			String message = error.getMessage();
			String recommendation = error.getRecommendation();
			
			String bgColor = (index%2 == 0 ? "#EDF3F7" : "#D5DADE");
			
			strBuilder.append("<tr style=\"background-color:"+bgColor+"\">");
			strBuilder.append("<td colspan=\"2\" style=\"padding:2px 5px\"><i><u>Thrown exception</u>: "+key+"</i></td>");
			
			strBuilder.append("</tr><tr style=\"background-color:"+bgColor+"\">");
			strBuilder.append("<td style=\"padding:2px 5px\"><u>Code</u>:</td>");
			strBuilder.append("<td style=\"padding:2px 5px\">"+error.getCode()+"</td>");
			
			strBuilder.append("</tr><tr style=\"background-color:"+bgColor+"\">");
			strBuilder.append("<td style=\"padding:2px 5px\"><u>Type</u>:</td>");
			strBuilder.append("<td style=\"padding:2px 5px\">"+error.getType()+"</td>");
			strBuilder.append("</tr>");

			String nextTr = !Strings.isNullOrEmpty(message) ? 
					"</tr><tr style=\"background-color:"+bgColor+"\"><td style=\"padding:2px 5px\"><u>Message</u>:</td><td style=\"padding:2px 5px\">"+message+"</td></tr>"	: "";
					
			nextTr += !Strings.isNullOrEmpty(recommendation) ? 
					"</tr><tr style=\"background-color:"+bgColor+"\"><td style=\"padding:2px 5px\"><u>Recommended Action</u>:</td><td style=\"padding:2px 5px\">"+recommendation+"</td></tr>" : "";
			
			if(!Strings.isNullOrEmpty(nextTr))
				strBuilder.append(nextTr);
			
			index++;
		}
	
		strBuilder.append("</table>");
		return strBuilder.toString();
	}

	public String constructDBFailureMessage(){
		
		StringBuilder strBuilder = new StringBuilder("<table>");
		
		strBuilder.append("<tr style=\"background-color:#C8C3D9\">");
		strBuilder.append("<td style=\"padding:2px 5px\"><u>Code</u>: 800</td>");
		strBuilder.append("<td style=\"padding:2px 5px\"><u>Type</u>: "+ErrorTypesRecommendations.databaseError+"</td>");
		strBuilder.append("</tr>");
		
		int index = 1;
		
		for(String key : this.getErrorsMap().keySet()){
			
			GAJsonResponseError error = this.getErrorsMap().get(key);
			String message = error.getMessage();
			String recommendation = error.getRecommendation();
			String dbException = error.getDbException();
			
			String bgColor = (index%2 == 0 ? "#EDF3F7" : "#D5DADE");
			
			strBuilder.append("<tr style=\"background-color:"+bgColor+"\">");
			strBuilder.append("<td colspan=\"2\" style=\"padding:2px 5px\">Thrown exception: "+dbException+"</td>");
			strBuilder.append("</tr><tr style=\"background-color:"+bgColor+"\">");
			strBuilder.append("<td colspan=\"2\" style=\"padding:2px 5px\">"+message+"</td>");

			String nextTr = !Strings.isNullOrEmpty(recommendation) ? 
					"</tr><tr style=\"background-color:"+bgColor+"\"><td style=\"padding:2px 5px\"><u>Recommended Action</u>:</td><td style=\"padding:2px 5px\">"+recommendation+"</td></tr>" : "";
			
			strBuilder.append(nextTr);
			strBuilder.append("</tr>");
			
			index++;
		}
		
		strBuilder.append("</table>");
		return strBuilder.toString();
	}
	
	public String[] constructNotificationMessageSubjectBody(String period, String message){
		
		String[] parts = {
				"[STATS] Attention, failed to fetch data from GA for "+period, 
				"<p><b>Details:</b></p>" + message 
			};
		
		return parts;
	}

	public boolean constructAndSendStandardMailWithPeriod(int year, int month, int day, String subject, String message){
		
		String period = day+"/"+month+"/"+year;
		
		// period
		if(day == 0){
			
			int endDay = (DateTime.now().withYear(year).withMonthOfYear(month)).dayOfMonth().withMaximumValue().getDayOfMonth();
			
			period = "1/"+month+"/"+year+" - "+endDay+"/"+month+"/"+year;
			
		}
		
		return constructAndSendStandardMail(subject + period , "<p><b>Details:</b></p>" + message, defaultProperties);
	}
	
	public boolean constructAndSendStandardMail(String subject, String body){
		
		return constructAndSendStandardMail(subject, body, defaultProperties);
	}

	public boolean constructAndSendStandardMail(String subject, String body, Properties properties){
		
		MimeMessage mimeMessage = this.newMessage();
		
		new EmailMessage.Builder(properties,mimeMessage,subject,body).subject(subject).body(body).build();
		
		return this.sendStandardMail(mimeMessage);
	}
}
