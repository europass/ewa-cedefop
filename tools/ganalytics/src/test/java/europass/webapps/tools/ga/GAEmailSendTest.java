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
package europass.webapps.tools.ga;

import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import europass.ewa.tools.utils.GAExecuteMailSenderImpl;

@Ignore
public class GAEmailSendTest {

    private Properties mailProps;

    private GAExecuteMailSenderImpl sender;

    public GAEmailSendTest() {

        mailProps = new Properties();

        mailProps.setProperty("europass-ewa-tools-ganalytics.mail.message.charset", "UTF-8");
        mailProps.setProperty("europass-ewa-tools-ganalytics.mail.to.recipients", "pgia@qnr.com.gr,pgianelos@gmail.com");
        mailProps.setProperty("europass-ewa-tools-ganalytics.mail.sender", "europass-team@instore.gr");

        sender = new GAExecuteMailSenderImpl("", "", "europass-team@instore.gr", "corfu.instore.gr", "25", "", "", "", "");
    }

    @Test
    public void sendStandarMailTest() throws AddressException, MessagingException {

        String subject = "[STATS] Attention, failed to fetch data from GA for dd/mm/yyyy!";
        String body = "This is a message from [STATS] describing what went wrong...";

        boolean success = sender.constructAndSendStandardMail(subject, body, mailProps);
        Assert.assertThat("Mail was sent", success, CoreMatchers.is(true));
    }
}
