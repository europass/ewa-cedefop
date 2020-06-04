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
package europass.ewa.services.social.linkedin;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Singleton;

import org.apache.commons.io.FileUtils;

import europass.ewa.model.FileData;
import europass.ewa.services.MediaTypeUtils;
import europass.ewa.services.social.InstanceClassMismatchException;
import europass.ewa.services.social.Transformer;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class PhotoHandler implements Transformer {

    private static final String EXTRA_PICTURE_URLS_KEY = "pictureUrls";
    private static final Logger LOG = LoggerFactory.getLogger(PhotoHandler.class);

    @SuppressWarnings("unchecked")
    @Override
    public Object transform(Object from, Object to, Object... params)
            throws InstanceClassMismatchException {

        if (!(from instanceof HashMap)) {
            throw new InstanceClassMismatchException();
        }
        if (!(to instanceof FileData)) {
            throw new InstanceClassMismatchException();
        }
        //Get cookie id from params	
        String cookieId = "linkedInProfileImage";
        if (params.length == 3 && params[2] != null && params[2] instanceof String) {
            cookieId += "-" + params[2].toString();
        } else {
            LOG.error("cookie id parameter has not be passed in params");
        }
        LOG.debug("cookie id retrieved from params is: " + cookieId);
        try {

            ArrayList<String> photoUrlValues = LinkedInUtilities.extraDataFieldValues(from, EXTRA_PICTURE_URLS_KEY);

            if (photoUrlValues.size() == 1) {

                URL imageURL = new URL(photoUrlValues.get(0));

                FileData photoData = (FileData) to;

                // get file url bytes				
                byte[] data = this.readBytesFromURL(imageURL, cookieId);
                photoData.setData(data);

                // get file's mime type
                HttpURLConnection conn = (HttpURLConnection) imageURL.openConnection();
                BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
                String IMAGE_MIME_TYPE = MediaTypeUtils.inferMimeType(in);

                photoData.setMimeType(IMAGE_MIME_TYPE);
                return photoData;
            }

            return to;

        } catch (final Exception e) {
            LOG.error("There is an error while trying to retrieve and save the linkedin picture. See the stacktrace: ", e);
            return to;
        }
    }

    private byte[] readBytesFromURL(URL imageURL, String cookieId) throws IOException {
        LOG.debug("ready to save the image temporarily to path: " + System.getProperty("java.io.tmpdir"));
        File destination = null;

        String prefix = computeImageId(cookieId);

        //create the destination file
        destination = File.createTempFile(prefix, null);

        FileUtils.copyURLToFile(imageURL, destination);
        byte[] data = FileUtils.readFileToByteArray(destination);
        FileUtils.forceDelete(destination);

        return data;
    }

    private String computeImageId(String cookieId) {
        String imageId = cookieId + "." + (new Date()).getTime();
        return imageId;
    }

    public void storeToFile(byte[] data, String path, String type) {

        OutputStream out;
        try {
            out = new FileOutputStream(path + "data." + type);
            out.write(data);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
