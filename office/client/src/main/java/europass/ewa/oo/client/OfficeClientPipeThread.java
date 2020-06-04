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

import java.io.ByteArrayInputStream;
import java.io.PipedInputStream;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.IOUtils;

import com.sun.jersey.multipart.BodyPart;
import com.sun.jersey.multipart.file.StreamDataBodyPart;

public class OfficeClientPipeThread extends Thread {

    private PipedInputStream in;
    private boolean completed = false;
    private BodyPart bodyPart;

    public OfficeClientPipeThread(PipedInputStream in) {
        this.in = in;
        completed = false;
    }

    public void run() {
        try {
            // this is used, because StreamDatBodyPart does not actually 
            // reads something, leading to a blocking pipe
            byte[] input = IOUtils.toByteArray(in);
            ByteArrayInputStream iStream = new ByteArrayInputStream(input);
            bodyPart = new StreamDataBodyPart("file", iStream, "file.odt", MediaType.APPLICATION_OCTET_STREAM_TYPE);
            completed = true;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public BodyPart getBodyPart() {
        return bodyPart;
    }

    public boolean isCompleted() {
        return completed;
    }

}
