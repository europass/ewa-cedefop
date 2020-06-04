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
package europass.ewa.services.editor.files;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.google.inject.servlet.ServletModule;
import com.google.inject.servlet.ServletScopes;

import europass.ewa.services.annotation.EWAEditor;
import europass.ewa.services.editor.resources.FileResource;
import europass.ewa.services.files.FileRepository;
import europass.ewa.services.files.ModelFileManager;
import europass.ewa.services.files.SizeLimitation;

public class FileUploadsModule extends ServletModule {

    @SuppressWarnings("unused")
    private static final Logger LOG = LoggerFactory.getLogger(FileUploadsModule.class);

    public static final String FILE_TMP_REPOSITORY_PARAM = "europass-ewa-services.files.repository";

    public static final String FILE_ATTACHMENT_ALLOWED_TYPES = "europass-ewa-services.files.fileMimes";

    public static final String FILE_ATTACHMENT_ALLOWED_TYPES_LIST = "ewa.files.attachment.types";

    public static final String FILE_ATTACHMENT_ALLOWED_SIZE = "europass-ewa-services.files.fileMaxSize";

    public static final String FILE_PHOTO_ALLOWED_TYPES = "europass-ewa-services.files.photoMimes";

    public static final String FILE_PHOTO_ALLOWED_TYPES_LIST = "ewa.files.photo.types";

    public static final String FILE_PHOTO_ALLOWED_SIZE = "europass-ewa-services.files.photoMaxSize";

    public static final String FILE_SIGNATURE_ALLOWED_TYPES = "europass-ewa-services.files.signatureMimes";

    public static final String FILE_SIGNATURE_ALLOWED_TYPES_LIST = "ewa.files.signature.types";

    public static final String FILE_SIGNATURE_ALLOWED_SIZE = "europass-ewa-services.files.signatureMaxSize";

    public static final String FILE_ATTACHMENT_CUMULATIVE_SIZE = "europass-ewa-services.files.fileMaxSize.perSession";

    private List<MediaType> attachmentAllowedTypes = null;

    private List<MediaType> photoAllowedTypes = null;

    private List<MediaType> signatureAllowedTypes = null;

    @Override
    protected void configureServlets() {

        // --- File Manipulation ---
        bind(FileRepository.class).to(SessionDiskFileRepository.class).in(ServletScopes.REQUEST);

        bind(SizeLimitation.class).annotatedWith(EWAEditor.class).to(SessionSizeLimitation.class);
        bind(ModelFileManager.class).annotatedWith(EWAEditor.class).to(SessionDiskFileManager.class);

        // Jersey Resources
        bind(FileResource.class);

        // Session Listener
        // DO NOT DELETE THIS COMMENT:newSetBinder( binder(),
        // HttpSessionListener.class).addBinding().to(ServicesSessionListener.class);
    }

    @Provides
    @Singleton
    @Named(FILE_ATTACHMENT_ALLOWED_TYPES_LIST)
    List<MediaType> getAttachmentAllowedTypes(@Named(FileUploadsModule.FILE_ATTACHMENT_ALLOWED_TYPES) String attachmentMediaTypes) {
        if (attachmentAllowedTypes == null) {
            attachmentAllowedTypes = this.readToList(attachmentMediaTypes);
        }
        return attachmentAllowedTypes;
    }

    @Provides
    @Singleton
    @Named(FILE_PHOTO_ALLOWED_TYPES_LIST)
    List<MediaType> getPhotoAllowedTypes(@Named(FileUploadsModule.FILE_PHOTO_ALLOWED_TYPES) String photoMediaTypes) {
        if (photoAllowedTypes == null) {
            photoAllowedTypes = this.readToList(photoMediaTypes);
        }
        return photoAllowedTypes;
    }

    @Provides
    @Singleton
    @Named(FILE_SIGNATURE_ALLOWED_TYPES_LIST)
    List<MediaType> getSignatureAllowedTypes(@Named(FileUploadsModule.FILE_SIGNATURE_ALLOWED_TYPES) String signatureMediaTypes) {
        if (signatureAllowedTypes == null) {
            signatureAllowedTypes = this.readToList(signatureMediaTypes);
        }
        return signatureAllowedTypes;
    }

    // local utility
    private List<MediaType> readToList(String listStr) {
        List<MediaType> list = new ArrayList<MediaType>();

        if (listStr != null && !"".equals(listStr)) {
            String[] items = listStr.split(",");
            for (int i = 0; i < items.length; i++) {
                String item = items[i];
                if (item != null && !"".equals(item)) {
                    // split to "/"
                    String parts[] = item.split("/");
                    if (parts.length == 2) {
                        list.add(new MediaType(parts[0], parts[1]));
                    }
                }
            }
        }

        return list;
    }
}
