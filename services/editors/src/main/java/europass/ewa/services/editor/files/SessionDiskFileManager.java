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
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import europass.ewa.model.Attachment;
import europass.ewa.model.CleanupUtils;
import europass.ewa.model.FileData;
import europass.ewa.model.Identification;
import europass.ewa.model.LearnerInfo;
import europass.ewa.model.SkillsPassport;
import europass.ewa.model.wrapper.Feedback;
import europass.ewa.model.wrapper.FeedbackFactory;
import europass.ewa.model.wrapper.FiledataWrapper;
import europass.ewa.model.wrapper.ModelContainer;
import europass.ewa.services.Paths;
import europass.ewa.services.exception.ApiException;
import europass.ewa.services.exception.NonApplicableArgument;
import europass.ewa.services.exception.PhotoCroppingException;
import europass.ewa.services.exception.PhotoReadingException;
import europass.ewa.services.files.FileRepository;
import europass.ewa.services.files.FileRepository.Types;
import europass.ewa.services.files.ModelFileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessionDiskFileManager implements ModelFileManager {

    private final FileRepository fileRepository;
    private static final Logger LOG = LoggerFactory.getLogger(SessionDiskFileManager.class);

    @Inject
    public SessionDiskFileManager(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    /**
     * ****************** URI MANIPULATION (Store byte[] to disk) ************************************
     */
    /* (non-Javadoc)
	 * @see europass.ewa.services.files.ModelFileBinder#augmentWithURI(europass.ewa.model.wrapper.ModelContainer)
     */
    @Override
    public List<Feedback> augmentWithURI(ModelContainer modelContainer, String cookieId) {
        return this.augmentWithURI(modelContainer, true, cookieId);
    }

    /* (non-Javadoc)
	 * @see europass.ewa.services.files.ModelFileBinder#augmentWithURI(europass.ewa.model.wrapper.ModelContainer, boolean)
     */
    @Override
    public List<Feedback> augmentWithURI(ModelContainer modelContainer, boolean cleanup, String cookieId) {
        List<Feedback> feedback = new ArrayList<Feedback>();

        SkillsPassport esp = modelContainer.getModel();
        // Photo
        FileData photo = this.getPhoto(esp);

        if (photo != null) {
            LOG.debug("photo retrieved from esp is not null");
            if (photo.getData() == null) {
                LOG.debug("photo data is null");
                //Remove the photo element and hide the corresponding printing preference
                CleanupUtils.disablePhoto(esp);
                feedback.add(FeedbackFactory.photoToURI());
            } else {
                LOG.debug("photo data is not null");
                Feedback photoFeedback = this.manageImageURI(photo, Types.PHOTO, Paths.PHOTO_BASE, FileData.IMAGE.PHOTO, cleanup, cookieId);
                if (!Feedback.Code.OK.equals(photoFeedback.getCode())) {
                    LOG.debug("photo feedback code is not OK - " + photoFeedback.getCode());
                    CleanupUtils.disablePhoto(esp);
                }
                LOG.debug("ready to add feedback photo");
                feedback.add(photoFeedback);
            }
        }
        // Signature
        FileData signature = this.getSignature(esp);
        if (signature != null) {
            if (signature.getData() == null) {
                //Remove the photo element and hide the corresponding printing preference
                CleanupUtils.disableSignature(esp);
                feedback.add(FeedbackFactory.signatureToURI());
            } else {
                Feedback signatureFeedback = this.manageImageURI(signature, Types.SIGNATURE, Paths.SIGNATURE_BASE, FileData.IMAGE.SIGNATURE, cleanup, cookieId);
                if (!Feedback.Code.OK.equals(signatureFeedback.getCode())) {
                    CleanupUtils.disableSignature(esp);
                }
                feedback.add(signatureFeedback);
            }
        }

        //remove unresolved references
        CleanupUtils.unresolvedAttachmentRefs(esp);

        // Attachments
        List<Attachment> attachments = this.getAttachments(esp);
        if (attachments != null) {
            Iterator<Attachment> it = attachments.iterator();
            while (it.hasNext()) {
                Attachment att = it.next();
                if (att.getData() == null) {
                    it.remove();
                    //Delete Annex ReferenceTo list item and related preference
                    //Delete Section ReferenceTo list item and related preference
                    CleanupUtils.disableAttachment(esp, att);
                    feedback.add(FeedbackFactory.attachmentToURI(att.getName()));
                    continue;
                }
                Feedback attFeedback = this.augmetedWithURI(att, Types.ATTACHMENT, Paths.ATTACHMENT_BASE, cleanup, cookieId);
                Feedback.Code ok = Feedback.Code.OK,
                        noThumb = Feedback.Code.UPLOAD_ATTACHMENT_THUMB,
                        current = attFeedback.getCode();
                //thumbnail creation failure is not blocking
                if (!ok.equals(current) && !noThumb.equals(current)) {
                    it.remove();
                    CleanupUtils.disableAttachment(esp, att);
                }
                feedback.add(attFeedback);
            }
        }

        return feedback;
    }

    private Feedback augmetedWithURI(FileData filedata, Types type, String tempUriPrefix, boolean cleanup, String cookieId) {
        LOG.debug("augmetedWithURI - cookie id: " + cookieId);

        try {
            LOG.debug("filedata mimetype: " + filedata.getMimeType());
            byte[] bytes = filedata.getData();

            LOG.debug("before saving to disk");
            FiledataWrapper fdWrap = fileRepository.save(bytes, filedata.getMimeType(), type, tempUriPrefix, cookieId);

            FileData fd = fdWrap.getFiledata();

            filedata.setTmpuri(fd.getTmpuri());

            // remove the data, as this will be consumed by a JSON client such as the editor
            if (cleanup) {
                LOG.debug("ready to remove the data as this will be consumed by a JSON client such as the editor");
                filedata.setData(null);
            }
            //Recovery from ThumbSavingException
            if (fdWrap.getFeedback() != null) {
                LOG.debug("recovery from ThumbSavingException");
                if (Feedback.Code.UPLOAD_ATTACHMENT_THUMB.equals(fdWrap.getFeedback().getCode())) {
                    String trace = fdWrap.getFeedback().getTrace();
                    return FeedbackFactory.attachmentToThumb(filedata.getName()).withError(trace);
                }
            }

        } catch (ApiException e) {
            LOG.debug("augmetedWithURI exception: " + e.getMessage());
            e.log();
            return FeedbackFactory.attachmentToURI(filedata.getName()).withError(e);
        }
        return FeedbackFactory.ok();
    }

    private Feedback manageImageURI(FileData imagedata, Types type, String tempURIPrefix, FileData.IMAGE imageType, boolean cleanup, String cookieId) {
        LOG.debug("inside manageImageURI");
        //Apply europass ratio
        try {
            fileRepository.imageProcessingBehavior().applyEuropassRatio(imagedata, imageType);

            return this.augmetedWithURI(imagedata, type, tempURIPrefix, cleanup, cookieId);
        } catch (NonApplicableArgument | PhotoReadingException | PhotoCroppingException e) {
            LOG.debug("NonApplicableArgument, PhotoReadingException, PhotoCroppingException: " + e.getMessage());
            if (imageType.equals(FileData.IMAGE.SIGNATURE)) {
                return FeedbackFactory.signatureRatio().withError(e);
            }

            return FeedbackFactory.photoRatio().withError(e);
        }

    }

    /**
     * ****************** DATA MANIPULATION ************************************
     */
    /* (non-Javadoc)
	 * @see europass.ewa.services.files.ModelFileBinder#augmentWithData(europass.ewa.model.wrapper.ModelContainer)
     */
    @Override
    public List<Feedback> augmentWithData(ModelContainer modelContainer) {
        return this.augmentWithData(modelContainer, true);
    }

    /* (non-Javadoc)
	 * @see europass.ewa.services.files.ModelFileBinder#augmentWithData(europass.ewa.model.wrapper.ModelContainer, boolean)
     */
    @Override
    public List<Feedback> augmentWithData(ModelContainer modelContainer, boolean cleanup) {
        List<Feedback> feedback = new ArrayList<Feedback>();

        SkillsPassport esp = modelContainer.getModel();

        // Photo
        FileData photo = this.getPhoto(esp);
        if (photo != null) {
            feedback.add(this.managePhotoData(photo, cleanup));
        }

        // Signature
        FileData signature = this.getSignature(esp);
        if (signature != null) {
            feedback.add(this.manageSignatureData(signature, cleanup));
        }

        // Attachments
        List<Attachment> attachments = this.getAttachments(esp);
        if (attachments != null) {
            for (Attachment attachment : attachments) {
                feedback.add(this.augmentedWithData(attachment, cleanup));
            }
        }
        return feedback;
    }

    /* (non-Javadoc)
	 * @see europass.ewa.services.files.ModelFileBinder#augmentWithAttachmentData(europass.ewa.model.wrapper.ModelContainer)
     */
    @Override
    public List<Feedback> augmentWithAttachmentData(ModelContainer modelContainer) {
        return this.augmentWithAttachmentData(modelContainer, true);
    }

    /* (non-Javadoc)
	 * @see europass.ewa.services.files.ModelFileBinder#augmentWithAttachmentData(europass.ewa.model.wrapper.ModelContainer, boolean)
     */
    @Override
    public List<Feedback> augmentWithAttachmentData(ModelContainer modelContainer, boolean cleanup) {
        List<Feedback> feedback = new ArrayList<Feedback>();

        SkillsPassport esp = modelContainer.getModel();

        // Attachments
        List<Attachment> attachments = this.getAttachments(esp);
        if (attachments != null) {
            for (Attachment attachment : attachments) {
                feedback.add(augmentedWithData(attachment, cleanup));
            }
        }
        return feedback;
    }

    /* (non-Javadoc)
	 * @see europass.ewa.services.files.ModelFileBinder#augmentWithPhotoData(europass.ewa.model.wrapper.ModelContainer)
     */
    @Override
    public List<Feedback> augmentWithPhotoData(ModelContainer modelContainer) {
        return this.augmentWithPhotoData(modelContainer, true);
    }

    /* (non-Javadoc)
	 * @see europass.ewa.services.files.ModelFileBinder#augmentWithPhotoData(europass.ewa.model.wrapper.ModelContainer, boolean)
     */
    @Override
    public List<Feedback> augmentWithPhotoData(ModelContainer modelContainer, boolean cleanup) {
        List<Feedback> feedback = new ArrayList<Feedback>();

        SkillsPassport esp = modelContainer.getModel();

        FileData photo = this.getPhoto(esp);
        if (photo != null) {
            feedback.add(managePhotoData(photo, cleanup));
        }
        return feedback;
    }

    /* (non-Javadoc)
	 * @see europass.ewa.services.files.ModelFileBinder#augmentWithSignatureData(europass.ewa.model.wrapper.ModelContainer)
     */
    @Override
    public List<Feedback> augmentWithSignatureData(ModelContainer modelContainer) {
        return this.augmentWithSignatureData(modelContainer, true);
    }

    /* (non-Javadoc)
	 * @see europass.ewa.services.files.ModelFileBinder#augmentWithSignatureData(europass.ewa.model.wrapper.ModelContainer, boolean)
     */
    @Override
    public List<Feedback> augmentWithSignatureData(ModelContainer modelContainer, boolean cleanup) {
        List<Feedback> feedback = new ArrayList<Feedback>();

        SkillsPassport esp = modelContainer.getModel();

        FileData signature = this.getSignature(esp);
        if (signature != null) {
            feedback.add(manageSignatureData(signature, cleanup));
        }
        return feedback;
    }

    /**
     *
     * @param photodata, the photo FileData object
     * @param cleanup, boolean to indicate whether to remove the Temp URI or not
     */
    private Feedback managePhotoData(FileData photodata, boolean cleanup) {
        try {

            fileRepository.readPhotoData(photodata);

            if (cleanup) {
                photodata.setTmpuri(null);
            }

            return FeedbackFactory.ok();

        } catch (ApiException e) {
            e.log();
            return FeedbackFactory.photoData().withError(e);
        }
    }

    /**
     *
     * @param photodata, the photo FileData object
     * @param cleanup, boolean to indicate whether to remove the Temp URI or not
     */
    private Feedback manageSignatureData(FileData signaturedata, boolean cleanup) {
        try {

            fileRepository.readPhotoData(signaturedata);

            if (cleanup) {
                signaturedata.setTmpuri(null);
            }

            return FeedbackFactory.ok();

        } catch (ApiException e) {
            e.log();
            return FeedbackFactory.signatureData().withError(e);
        }
    }

    /**
     * Returns an augmented FileData object as this is updated by the FileData
     * returned by the FileRepository for the specified fileId
     *
     * @param filedata
     * @return
     */
    private Feedback augmentedWithData(FileData filedata, boolean cleanup) {

        final String fileId = filedata.fileID();
        final FileData augmentedData = fileRepository.readFile(fileId);

        if (augmentedData != null) {
            filedata.setData(augmentedData.getData());
            filedata.setMimeType(augmentedData.getMimeType());
            LOG.debug("Augmented filedata mimetype is: " + filedata.getMimeType());
            // The temporary uri should be set to null
            if (cleanup) {
                filedata.setTmpuri(null);
            }

            return FeedbackFactory.ok();
        }

        return FeedbackFactory.attachmentData(filedata.getName()).withError("Error returning augmented file data by file repository.");
    }

    /* (non-Javadoc)
	 * @see europass.ewa.services.files.ModelFileBinder#removeTempUri(europass.ewa.model.wrapper.ModelContainer)
     */
    @Override
    public Feedback removeTempUri(ModelContainer modelContainer) {
        LOG.debug("Inside removeTempUri");
        SkillsPassport esp = modelContainer.getModel();

        // Photo
        FileData photo = this.getPhoto(esp);
        if (photo != null) {
            photo.setTmpuri(null);
        }

        // Attachments
        List<Attachment> attachments = this.getAttachments(esp);
        if (attachments != null) {
            for (Attachment attachment : attachments) {
                attachment.setTmpuri(null);
            }
        }
        return FeedbackFactory.ok();
    }

    /**
     * Fetches a FileData object out of the ESP that corresponds to the Photo.
     *
     * @param esp
     * @return
     */
    private FileData getPhoto(SkillsPassport esp) {

        LearnerInfo learnerinfo = esp.getLearnerInfo();
        if (learnerinfo == null) {
            return null;
        }

        Identification identification = learnerinfo.getIdentification();
        if (identification == null) {
            return null;
        }

        return identification.getPhoto();
    }

    /**
     * Fetches a FileData object out of the ESP that corresponds to the
     * Signature.
     *
     * @param esp
     * @return
     */
    private FileData getSignature(SkillsPassport esp) {

        LearnerInfo learnerinfo = esp.getLearnerInfo();
        if (learnerinfo == null) {
            return null;
        }

        Identification identification = learnerinfo.getIdentification();
        if (identification == null) {
            return null;
        }

        return identification.getSignature();
    }

    /**
     * Fetches a list of Attachments out of the ESP that corresponds to the
     * Attachments
     *
     * @param esp
     * @return
     */
    private List<Attachment> getAttachments(SkillsPassport esp) {
        List<Attachment> attachments = esp.getAttachmentList();
        if (attachments != null && attachments.isEmpty()) {
            return null;
        }
        return attachments;
    }

}
