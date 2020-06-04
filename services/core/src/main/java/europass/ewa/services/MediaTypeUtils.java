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
package europass.ewa.services;

import java.io.BufferedInputStream;
import java.io.Serializable;
import java.net.URLConnection;
import java.util.Collection;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;

import eu.medsea.mimeutil.MimeException;
import eu.medsea.mimeutil.MimeType;
import eu.medsea.mimeutil.MimeUtil;
import eu.medsea.mimeutil.MimeUtil2;
import eu.medsea.mimeutil.detector.MagicMimeMimeDetector;
import europass.ewa.enums.LogFields;
import europass.ewa.services.exception.ApiException;
import europass.ewa.services.exception.DisallowedMediaTypeException;
import europass.ewa.services.exception.UndefinedMediaTypeException;

public final class MediaTypeUtils {

    private static final Logger LOG = LoggerFactory.getLogger(MediaTypeUtils.class);

    public static final MediaType APPLICATION_PDF = new MediaType("application", "pdf");
    public static final MediaType APPLICATION_X_PDF = new MediaType("application", "x-pdf");

    //Suppress default constructor for noninstantiability
    private MediaTypeUtils() {
        throw new AssertionError();
    }

    /**
     * Uses URLConnection in order to guess the content type of the given input
     * stream
     *
     * @param in a file's input stream
     * @return the string representation of the inferred content type.
     * @return null in case of an IOException
     */
    public static String inferContentType(BufferedInputStream bis) {
        try {
            return URLConnection.guessContentTypeFromStream(bis);
        } catch (Exception e) {
            LOG.error("Utils:inferContentType - IOException while trying to guess contentType from input stream", e);
            return null;
        } finally {
            try {
                if (bis != null) {
                    bis.reset();
                }
            } catch (Exception ex) {
                LOG.error("Error in retrieving media type while trying to reset the buffered inputstream", ex);
            }
        }
    }

    /**
     * Uses MimeUtil and Magic Headers to infer the mime type of the given input
     * stream
     *
     * @param in a file's input stream
     * @return the string representation of a mime type
     * @return null in case of a MimeException, or if no mimeTypes are inferred
     * for this input stream or if all the inferred types are the unknown mime
     * types
     */
    public static String inferMimeType(BufferedInputStream bis) {
        if (MimeUtil.getMimeDetector(MagicMimeMimeDetector.class.getName()) == null) {
            MimeUtil.registerMimeDetector(MagicMimeMimeDetector.class.getName());
        }
        try {
            // If no matching mime types are found the returned Collection will
            // contain the default UNKNOWN_MIME_TYPE, which is
            // application/octet-stream;
            @SuppressWarnings("unchecked")
            Collection<MimeType> mimeTypes = MimeUtil.getMimeTypes(bis);
            if (mimeTypes.size() > 0) {
                for (MimeType mime : mimeTypes) {
                    // does not match the unknown mime type type
                    if (!MimeUtil2.UNKNOWN_MIME_TYPE.equals(mime)) {
                        return mime.toString();
                    }
                }
                // no match found - all entries are the unknown
                return null;
            } else {
                // no mimeTypes found for this inputstream
                return null;
            }
        } catch (MimeException me) {
            LOG.error("Utils:inferMimeType - MimeException while trying to infer mime type from input stream", me);
            return null;
        } finally {
            try {
                if (bis != null) {
                    //reset the inputstream
                    bis.reset();
                }

            } catch (Exception ex) {
                LOG.error("Error in retrieving media type while trying to reset the buffered inputstream", ex);
            }
        }
    }

    public static MediaType readMediaType(BufferedInputStream bis,
            List<MediaType> allowedTypes) {
        return readMediaType(null, null, bis, allowedTypes);
    }

    /**
     * Returns a JaxRS MediaType object first by consulting the mediatype of the
     * Form Data Body Part, then using Mime-Util and looking for Magic Headers,
     * then using java's URLConnection and finally trying to guess from the file
     * name extension.
     *
     * Every type a valid (non-unknown) MediaType is found it is checked whether
     * it is one of the Allowed Types
     *
     * @param bp
     * @param disposition
     * @param in
     * @return a MediaType or null
     * @throws DisallowedMediaTypeException
     * @throws UndefinedMediaTypeException
     */
    public static MediaType readMediaType(FormDataBodyPart bp, FormDataContentDisposition disposition, BufferedInputStream bis,
            List<MediaType> allowedTypes) {

        try {
            MediaType mt = null;

            boolean failedGuess = false;

            //Try to infer the mime type based on the magic headers
            String mediaType = inferMimeType(bis);

            if (mediaType == null) {
                failedGuess = true;
            } else {
                mt = parse(mediaType);
            }
            //if failed to guess
            //or if managed to guess, but the guess type is undefined
            if (failedGuess || (!failedGuess && isUndefined(mt))) {
                //reset..
                failedGuess = false;
                // Otherwise, try to guess the content type using java's URLConnection
                mediaType = inferContentType(bis);
                if (mediaType == null) {
                    failedGuess = true;
                } else {
                    mt = parse(mediaType);
                }

                //if failed to guess or if managed to guess, but the guess type is undefined 
                if ((failedGuess || (!failedGuess && isUndefined(mt))) && disposition != null) {
                    //Finally, try to figure out the media type from extension
                    String fname = disposition.getFileName();
                    String extension = ((fname != null && fname.lastIndexOf('.') >= 0) ? fname.substring(fname.lastIndexOf('.')) : null);

                    if (extension != null && !"".equals(extension)) {
                        //in case the extension is null a UndefinedMediaTypeException will be thrown
                        //in case no match is found a DisallowedMediaTypeException will be thrown
                        mt = matchAllowedMediaType(extension, allowedTypes);
                    }
                }
            }
            //finally if media type remain undefined, throw an exception
            if (isUndefined(mt)) {

                throw new UndefinedMediaTypeException("Could not decide on this media type, the media type is null or the empty string.");
            }
            //or if the media type is not allowed, throw an exception
            if (!isAllowedMediaType(mt, allowedTypes)) {
                String extension = "";
                if (disposition != null && !Strings.isNullOrEmpty(disposition.getFileName())) {
                    extension = disposition.getFileName().lastIndexOf(".") >= 0 ? disposition.getFileName().substring(disposition.getFileName().lastIndexOf(".")) : "";
                    extension = !Strings.isNullOrEmpty(extension) ? String.format("(extension:%s)", extension) : "";
                }
                throw new DisallowedMediaTypeException(mt, extension);
            }
            //We will arrive here if the media type is properly defined and it matched one of the allowed types
            return mt;
        } //Any uncaught null pointer...
        catch (final NullPointerException e) {
            throw new UndefinedMediaTypeException("Could not decide on this media type by extension, the extension is null or the empty string.");
        }
    }

    /**
     * Checks whether a mediatype is included in a list of allowed media types
     *
     * @param type
     * @param allowedTypes
     * @return true or false according to whether the media type is included in
     * the list of allowed types
     */
    private static boolean isAllowedMediaType(MediaType type, List<MediaType> allowedTypes) {
        if (isUndefined(type)) {
            return false;
        }
        if (allowedTypes.size() == 0) {
            return true;//everything is allowed...
        }
        return allowedTypes.contains(type); //check if type is contained - uses .equals

    }

    /**
     * Uses an extension to find a matching MediaType from a list of allowed
     * media types
     *
     * @param extension
     * @param allowedTypes
     * @return
     * @throws DisallowedMediaTypeException
     * @throws UndefinedMediaTypeException
     */
    private static MediaType matchAllowedMediaType(String extension, List<MediaType> allowedTypes) {
        if (Strings.isNullOrEmpty(extension)) {
            throw new UndefinedMediaTypeException("Could not decide on this media type by extension, the extension is null or the empty string.");
        }
        if (allowedTypes.size() == 0) {
            return MediaType.WILDCARD_TYPE;
        }
        List<MediaType> matchedList = Lists.newArrayList(Collections2.filter(
                allowedTypes, new MediaExtensionPredicate(extension)));

        int matches = matchedList.size();

        if (matches > 0 && !isUndefined(matchedList.get(0))) {
            return matchedList.get(0);
        } else {
            //the extension does not match
            throw ApiException.addInfo(new DisallowedMediaTypeException(), LogFields.FILETYPE, extension);
        }
    }

    /**
     * Predicate to match a MediaType based on some known extensions
     */
    static class MediaExtensionPredicate implements Predicate<MediaType>, Serializable {

        private static final long serialVersionUID = -8681819133741985142L;

        final String extension;

        MediaExtensionPredicate(String extension) {
            this.extension = extension;
        }

        @Override
        public boolean apply(MediaType type) {
            String ext = mediaTypeExtension(type);
            return ext.equals(extension);
        }
    }

    /**
     * Parses a string into a MediaType
     *
     * @param str
     * @return the MediaType or null if it failed.
     */
    public static MediaType parse(String str) {
        if (Strings.isNullOrEmpty(str)) {
            return null;
        }
        String[] parts = str.split("/");
        switch (parts.length) {
            case 2: {
                return new MediaType(parts[0], parts[1]);
            }
            default: {
                return null; // the media type text does not contain a "/" or
                // contains more than one
            }
        }
    }

    /**
     * Checks whether a MediaType is defined (to be define it must be non null
     * and its string to not be equal to the empty string
     *
     * @param mt
     * @return
     */
    private static boolean isUndefined(MediaType mt) {
        if (mt == null) {
            return true;
        }
        return "".equals(mt.toString());
    }

    /**
     * Tries to get the extension from one of the known media types
     *
     * @param media
     * @return
     */
    private static String mediaTypeExtension(MediaType media) {

        if (isUndefined(media)) {
            return "";
        }
        String subType = media.getSubtype();
        if (subType.endsWith("pdf")) {
            return ".pdf";
        }
        if (subType.endsWith("jpeg")) {
            return ".jpg";
        }
        if (subType.endsWith("png")) {
            return ".png";
        }
        if (subType.endsWith("xml")) {
            return ".xml";
        }

        return "";
    }
}
