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
package europass.ewa.services.editor.messages;

public interface HtmlWrapper {

    String META_SUCCESS_STATUS = "success";

    String META_ERROR_STATUS = "error";

    String IE_FRIENDLY_MESSAGE_BUSTER = "<!--  "
            + "512 bytes of padding to suppress Internet Explorer's \"Friendly error messages\""
            + "From: HOW TO: Turn Off the Internet Explorer 5.x and 6.x \"Show Friendly HTTP Error Messages\" Feature on the Server Side"
            + "      http://support.microsoft.com/kb/294807"
            + "Several frequently-seen status codes have \"friendly\" error messages "
            + "that Internet Explorer 5.x displays and that effectively mask the "
            + "actual text message that the server sends."
            + "However, these \"friendly\" error messages are only displayed if the "
            + "response that is sent to the client is less than or equal to a "
            + "specified threshold."
            + "For example, to see the exact text of an HTTP 500 response, "
            + "the content length must be greater than 512 bytes."
            + "-->";

    /**
     * Static utility to simply wrap a string within an html document.
     *
     * @param message
     * @return
     */
    String htmlWrap(String message);

    /**
     * Wrap an error object to an html response including the specific status
     *
     * @param error
     * @param status
     * @return
     */
    String htmlWrap(europass.ewa.services.exception.ErrorMessage message, String status);

    /**
     * Static utility to simply wrap a string within an html document and also
     * include a status code as a meta element.
     *
     * @param message
     * @param status
     * @return
     */
    String htmlWrap(String message, String status);

}
