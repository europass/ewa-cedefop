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

import javax.mail.internet.MimeMessage;

public interface MailSender {

    /**
     * Checks the validity of an email address. No actual email is sent
     */
    boolean isValidAddress(String address);

    /**
     * Initiates an empty mime message from the current session
     *
     * @return the new mime message
     */
    MimeMessage newMessage();

    /**
     * Sends a message. The sender should be given in the message
     *
     * @param message
     * @return true if ok, false if failed
     */
    boolean sendMail(MimeMessage message);

    /**
     * Sends a message, including a standard sender
     *
     * @param message
     * @return true if ok, false if failed
     */
    boolean sendStandardMail(MimeMessage message);
}
