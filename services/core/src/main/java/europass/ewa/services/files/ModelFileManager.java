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

import java.util.List;

import europass.ewa.model.wrapper.Feedback;
import europass.ewa.model.wrapper.ModelContainer;

public interface ModelFileManager {

    /**
     * ****************** URI MANIPULATION (Store byte[] to disk) ************************************
     */
    /**
     * Augment a SkillsPassport by adding a TempURI to the FileData object and
     * cleaning up the bytes
     *
     * @param modelContainer model container
     * @param id - user's unique cookie id
     *
     * @return a list with Feedback messages about the process
     */
    List<Feedback> augmentWithURI(ModelContainer modelContainer, String id);

    /**
     * Augment a SkillsPassport by adding a TempURI the FileData object and
     * optionally cleaning up the bytes
     *
     * @param modelContainer model container
     * @param cleanup
     * @param id - user's unique cookie id boolean to indicate whether to remove
     * the data or not. default is true
     *
     * @return a list with Feedback messages about the process
     */
    List<Feedback> augmentWithURI(ModelContainer modelContainer, boolean cleanup, String id);

    /**
     * ****************** DATA MANIPULATION ************************************
     */
    /**
     * Augment a SkillsPassport by adding the bytes to the FileData object based
     * on the TempURI, and cleaning up the TempURI
     *
     * @param a model container
     *
     * @return a list with Feedback messages about the process
     */
    List<Feedback> augmentWithData(ModelContainer modelContainer);

    /**
     * Augment a SkillsPassport by adding the bytes to the FileData object based
     * on the TempURI, and optionally cleaning up the TempURI
     *
     * @param a model container
     * @param cleanup boolean to indicate whether to remove the TempURI or not.
     * Default is true
     *
     * @return a list with Feedback messages about the process
     */
    List<Feedback> augmentWithData(ModelContainer modelContainer, boolean cleanup);

    /**
     * Augment a SkillsPassport by adding the bytes to each of the Attachment
     * object based on the TempURI, and cleaning up the TempURI
     *
     * @param a model container
     *
     * @return a list with Feedback messages about the process
     */
    List<Feedback> augmentWithAttachmentData(ModelContainer modelContainer);

    /**
     * Augment a SkillsPassport by adding the bytes to each of the Attachment
     * object based on the TempURI, and optionally cleaning up the TempURI
     *
     * @param a model container
     * @param cleanup boolean to indicate whether to remove the TempURI or not.
     * Default is true
     *
     * @return a list with Feedback messages about the process
     */
    List<Feedback> augmentWithAttachmentData(ModelContainer modelContainer, boolean cleanup);

    /**
     * Augment a SkillsPassport by adding the bytes to the Photo object based on
     * the TempURI, and cleaning up the TempURI
     *
     * @param a model container
     * @param cleanup boolean to indicate whether to remove the TempURI or not.
     * Default is true
     *
     * @return a list with Feedback messages about the process
     */
    List<Feedback> augmentWithPhotoData(ModelContainer modelContainer);

    /**
     * Augment a SkillsPassport by adding the bytes to the Photo object based on
     * the TempURI, and optionally cleaning up the TempURI
     *
     * @param a model container
     * @param cleanup boolean to indicate whether to remove the TempURI or not.
     * Default is true
     *
     * @return a list with Feedback messages about the process
     */
    List<Feedback> augmentWithPhotoData(ModelContainer modelContainer, boolean cleanup);

    /**
     * Augment a SkillsPassport by adding the bytes to the Signature object
     * based on the TempURI, and cleaning up the TempURI
     *
     * @param a model container
     * @param cleanup boolean to indicate whether to remove the TempURI or not.
     * Default is true
     *
     * @return a list with Feedback messages about the process
     */
    List<Feedback> augmentWithSignatureData(ModelContainer modelContainer);

    /**
     * Augment a SkillsPassport by adding the bytes to the Signature object
     * based on the TempURI, and optionally cleaning up the TempURI
     *
     * @param a model container
     * @param cleanup boolean to indicate whether to remove the TempURI or not.
     * Default is true
     *
     * @return a list with Feedback messages about the process
     */
    List<Feedback> augmentWithSignatureData(ModelContainer modelContainer, boolean cleanup);

    /**
     * Removes the tempURI from all FileData objects in the model
     *
     * @param modelContainer
     * @param cleanup boolean to indicate whether to remove the TempURI or not.
     * Default is true
     *
     * @return Feedback
     */
    Feedback removeTempUri(ModelContainer modelContainer);
}
