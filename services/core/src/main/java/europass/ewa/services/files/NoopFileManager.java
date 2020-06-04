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
package europass.ewa.services.files;

import java.util.Collections;
import java.util.List;

import europass.ewa.model.wrapper.Feedback;
import europass.ewa.model.wrapper.ModelContainer;

public class NoopFileManager implements ModelFileManager {

    @Override
    public List<Feedback> augmentWithURI(ModelContainer modelContainer, String id) {
        return Collections.emptyList();
    }

    @Override
    public List<Feedback> augmentWithURI(ModelContainer modelContainer, boolean cleanup, String id) {
        return Collections.emptyList();
    }

    @Override
    public List<Feedback> augmentWithData(ModelContainer modelContainer) {
        return Collections.emptyList();
    }

    @Override
    public List<Feedback> augmentWithData(ModelContainer modelContainer, boolean cleanup) {
        return Collections.emptyList();
    }

    @Override
    public List<Feedback> augmentWithAttachmentData(ModelContainer modelContainer) {
        return Collections.emptyList();
    }

    @Override
    public List<Feedback> augmentWithAttachmentData(ModelContainer modelContainer, boolean cleanup) {
        return Collections.emptyList();
    }

    @Override
    public List<Feedback> augmentWithPhotoData(ModelContainer modelContainer) {
        return Collections.emptyList();
    }

    @Override
    public List<Feedback> augmentWithPhotoData(ModelContainer modelContainer, boolean cleanup) {
        return Collections.emptyList();
    }

    @Override
    public Feedback removeTempUri(ModelContainer modelContainer) {
        return null;
    }

    @Override
    public List<Feedback> augmentWithSignatureData(ModelContainer modelContainer) {
        return null;
    }

    @Override
    public List<Feedback> augmentWithSignatureData(
            ModelContainer modelContainer, boolean cleanup) {
        return null;
    }

}
