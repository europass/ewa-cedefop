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
package europass.ewa.oo.client;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import europass.ewa.enums.ConversionFileType;
import europass.ewa.model.Attachment;
import europass.ewa.model.wrapper.Feedback;
import europass.ewa.oo.client.exception.AttachmentError;
import europass.ewa.oo.client.exception.ConversionError;
import europass.ewa.oo.client.exception.NoServerAvailable;
import europass.ewa.oo.client.exception.NoServerConfiguration;

public interface OfficeClient {

    InputStream convert(File in, ConversionFileType type, Attachment xmlAttachment, List<Attachment> files, boolean append) throws NoServerConfiguration, ConversionError, NoServerAvailable, AttachmentError;

    InputStream convert(File in, ConversionFileType type) throws NoServerConfiguration, ConversionError, NoServerAvailable, AttachmentError;

    void release();

    void release(String requestId);

    List<Feedback> getFeedback();

    OutputStream startConvert() throws NoServerConfiguration, ConversionError, NoServerAvailable;

    OutputStream startConvert(String requestId) throws NoServerConfiguration, ConversionError, NoServerAvailable;

    InputStream endConvert(OutputStream data, ConversionFileType type) throws NoServerConfiguration, NoServerAvailable, ConversionError, AttachmentError;

    InputStream endConvert(OutputStream data, ConversionFileType type, Attachment xmlAttachment, List<Attachment> attachments) throws NoServerConfiguration, NoServerAvailable, ConversionError, AttachmentError;

    InputStream endConvert(OutputStream data, ConversionFileType type, Attachment xmlAttachment, List<Attachment> attachments, String requestId) throws NoServerConfiguration, NoServerAvailable, ConversionError, AttachmentError;
}
