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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.io.IOUtils;

import europass.ewa.enums.ConversionFileType;
import europass.ewa.model.Attachment;
import europass.ewa.oo.client.OfficeClient;

public class TestClientThread extends Thread {

    public OfficeClient oc = null;
    public int idx = 0;

    private ArrayList<Attachment> att = new ArrayList<Attachment>();

    public TestClientThread(OfficeClient oc, int idx) {
        this.oc = oc;
        this.idx = idx;
    }

    public void run() {
        try {
            System.out.println("START >> " + new Date());

            File infile = new File("/Users/vbla/Projects/cedefop/files/test" + idx + ".odt");
            if (!infile.exists()) {
                infile = new File("/Users/vbla/Projects/cedefop/files/Europass_new_CV-4.0_draft_CK.odt");
            }

            Attachment f = new Attachment();
            f.setTmpuri(new URI("http://test/1111.doc"));
            f.setName("test.doc");
            f.setMimeType("application/msword");
            att.add(f);

            Attachment f2 = new Attachment();
            f2.setTmpuri(new URI("http://test/11112.sql"));
            f2.setName("test7.sql");
            f2.setMimeType("text");
            att.add(f2);

            Attachment f3 = new Attachment();
            f3.setTmpuri(new URI("http://test/11112.pdf"));
            f3.setName("att.pdf");
            f3.setMimeType("application/pdf");
            att.add(f3);

            Attachment xml = new Attachment();
            xml.setTmpuri(new URI("http://test/temp-cv-xml.pdf"));
            xml.setName("europass-cv.xml");
            xml.setMimeType("text/xml");

            OutputStream out = oc.startConvert();
            IOUtils.copy(new FileInputStream(infile), out);
            InputStream generated = oc.endConvert(out, ConversionFileType.PDF, xml, att);
            oc.release();

            // Use idx in new file name
            File newFile = new File("/Users/vbla/Projects/cedefop/files/test" + idx + ".pdf");
            IOUtils.copy(generated, new FileOutputStream(newFile));

            System.out.println("END >> " + new Date());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
