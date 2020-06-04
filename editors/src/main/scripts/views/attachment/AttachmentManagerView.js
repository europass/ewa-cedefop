define(
        [
            'jquery',
            'underscore',
            'backbone',
            'Utils',
            'europass/GlobalDocumentInstance',
            'models/SkillsPassport',
            'models/PrintingPreferencesModel',
            'hbs!templates/attachment/justuploaded'
        ],
        function ($, _, Backbone, Utils, GlobalDocument, SkillsPassport, PrintingPreferencesModel, JustUploadedTpl) {

            var AttachmentManagerView = Backbone.View.extend({

                SKILLSPASSPORT: "SkillsPassport",
                PREFERENCES: "SkillsPassport.PrintingPreferences",
                ATTACHMENTS: "SkillsPassport.Attachment",
                ANNEXES: "SkillsPassport.LearnerInfo.ReferenceTo",
                PHOTO: "SkillsPassport.LearnerInfo.Identification.Photo",
                SECTIONS: [this.ATTACHMENTS, this.ANNEXES],

                initialize: function () {
                    this.internalModel = null;
                    this.currentDocument = GlobalDocument.getPrefDocument();
                    this.toBeDeletedList = [];
                    this.justuploadedTpl = JustUploadedTpl;
                },
                /**
                 * Prepares the html of the just uploaded file
                 * @param the json holding the filedata info
                 * @return the html with info about the uploaded file
                 */
                uploadedFile: function (filedata) {
                    var html = this.justuploadedTpl(filedata);
                    return html;
                },
                /**
                 * Initializes/Prepares the internal model, used to track the changes related to file uploads.
                 * The internal model is always initiated as a clone of the main model 
                 * 
                 * @param section, Json Path to the Section which may accept reference to attachments
                 * 
                 * @return void
                 */
                prepare: function (section) {
                    this.internalModel = new SkillsPassport($.extend(true, {}, this.model.attributes));
                },

                /**
                 * Destroys the internal model. ( actually Backbone Model destroy performs a DELETE to the server, so it not exactly what we need.)
                 * @return void
                 */
                throwaway: function () {
                    this.internalModel = null;
                    this.toBeDeletedList = [];
                },

                /**
                 * Reset photo and attachments
                 */
                resetPhotoAttachment: function () {
                    if (this.internalModel === null) {
                        return false;
                    }

                    this.internalModel.reset(this.model.PHOTO, true);
                    this.internalModel.reset(this.model.SIGNATURE, true);
                    this.internalModel.documentation().updateAllRemovedAttachments(true);
                },
                /**
                 * Updates the internal model based on the file data of the file uploaded.
                 * @param element uploadedContainer, append the html of the just uploaded file here.
                 * @param filedata
                 * @param section 
                 * 
                 * @return void
                 */
                enrich: function (uploadedContainer, section, filedata) {
                    if (this.internalModel !== null) {
                        var referenceData = {};

                        //1. Attachments - update model
                        /*
                         * We need to assign a unique id. We will use the hashCode of the file name, augmented by a random number.
                         * 
                         * Using the length of files is not safe, as deletions might have taken place.
                         * var files = this.internalModel.get( this.ATTACHMENTS );
                         * var index = ( files !== undefined && $.isArray(files) ) ? files.length : 0 ;
                         */
                        var index = Utils.hashCode(filedata.Name) + "" + Utils.randomInK();

                        if (filedata.Id === undefined) {
                            var attachIndex = "ATT_" + index;
                            filedata.Id = attachIndex;
                            filedata.Description = filedata.Name;
                            referenceData.idref = attachIndex;
                        }
                        //View: update uploadedContainer - NOT FROM THIS CONTROLLER - See FileManager
                        this.insertToArray(this.ATTACHMENTS, filedata);
                        //update file properties
                        var file = uploadedContainer.find("a[name=\"" + filedata.Name + "\"]");
                        file.attr("href", filedata.TempURI);
                        file.attr("target", "_blank");
                        file.attr("data-rel-attachment", filedata.Id);

                        //2. LearnerInfo.ReferenceTo
                        this.insertToArray(this.ANNEXES, referenceData);

                        //3. Section
                        if (this.isReferenceReceptor(section)) {
                            section = this.internalModel.documentation().referenceToPath(section);
                            this.insertToArray(section, referenceData);
                        }
                    }
                },
                /**
                 * Delete an Attachment
                 * 
                 * @param attachment the jsonpath to the attachment to be deleted
                 */
                deleteAttachment: function (referenceToPath, attachId, index) {
                    if (referenceToPath.indexOf(this.SKILLSPASSPORT) === -1) {
                        referenceToPath = this.SKILLSPASSPORT + "." + referenceToPath;
                    }
                    /*
                     * ATTENTION:
                     * 
                     * Given that nothing is saved to the live model until the user hits 'Save' explicitly,
                     * this deletion need not be recorded, if no such attachment already exists in the live model.
                     * 
                     * We will simply remove this from the internal model to facilitate the re-rendering of the form.
                     */
                    if (this.model.get(referenceToPath) !== undefined) {
                        this.toBeDeletedList.push(referenceToPath);
                    }

                    //Delete it from the internal model for the re-rendering to work
                    if (attachId !== undefined && index !== undefined) {
                        //First, delete Attachment
                        this.internalModel.remove(this.internalModel.ATTACHMENT_SECTION + "[" + index + "]", {silently: true});

                        //Also remove the links to this attachment from other sections
                        var refs = this.internalModel.documentation().findReferencesTo(attachId);

                        for (var i = 0; i < refs.length; i++) {
                            var ref = refs[i];
                            this.internalModel.remove(ref, {silent: true});
                        }
                    }
                },
                /**
                 * Rename an Attachment
                 * 
                 * @param attachment the jsonpath to the attachment to be renamed
                 * @param the new name for the attachment
                 */
                rename: function (attachment, name) {
                    var json = {};
                    if (attachment.indexOf(this.SKILLSPASSPORT) === -1) {
                        attachment = this.SKILLSPASSPORT + "." + attachment;
                    }
                    json[ attachment ] = name;
                    this.internalModel.set(json);
                },
                /**
                 * Returns the internal model
                 * 
                 * @return Backbone.NestedModel
                 */
                capture: function () {
                    return this.internalModel;
                },
                /**
                 * Stores the changes recorded to the internal model back to the live model.
                 * This means that the respective sections of the live model are first removed and then set according to the internal model.
                 * The involved sections are this.SECTIONS + the section as given
                 * 
                 *  @return void
                 */
                save: function (section) {
                    //console.log("==== ATTACHMENT MANAGER - SAVE ====");
                    //1. Attachments
                    this.syncToLive(this.ATTACHMENTS);
                    //2. LearnerInfo.ReferenceTo
                    this.syncToLive(this.ANNEXES);
                    //3. section
                    if (this.isReferenceReceptor(section)) {
                        this.syncToLive(this.internalModel.documentation().referenceToPath(section));
                    }
                    //Destroy the internal model
                    this.throwaway();
                },
                /**
                 * Save All sections that have to do with ReferenceTo
                 *  @return void
                 */
                saveAllReferenceTo: function () {
                    //console.log("==== ATTACHMENT MANAGER - SAVE ALL REFERENCE TO====");
                    //1. Attachments
                    this.syncToLive(this.ATTACHMENTS);
                    //2. LearnerInfo.ReferenceTo
                    this.syncToLive(this.ANNEXES);

                    //3. All other ReferenceTo
                    if (this.internalModel !== undefined && this.internalModel !== null) {
                        var otherReferenceTo = this.internalModel.documentation().findReferencesToSections(true); //true to exclude annexes
                        var that = this;
                        $(otherReferenceTo).each(function (idx, otherRef) {
                            that.syncToLive(that.internalModel.documentation().referenceToPath(otherRef));
                        });
                    }

                    //Destroy the internal model
                    this.throwaway();
                },
                /**
                 * Save all
                 * Syncs the entire Internal Model with the Live Model
                 */
                saveAll: function () {
                    //console.log("==== ATTACHMENT MANAGER - SAVE ALL ====");
                    //1. This needs to come before syncing, as it removes Attachments from the live model 
                    //   (which are already removed from the internal model)
                    if (this.toBeDeletedList.length > 0) {
                        //perform deletions
                        var that = this;
                        $(this.toBeDeletedList).each(function (idx, path) {
                            that.model.documentation().restRemoveAttachment(path, true);
                        });
                    }
                    //2. 
                    this.syncToLive(this.SKILLSPASSPORT);

                    this.throwaway();
                },
                /**
                 * Utility function used when saving the internal model back to the live model 
                 */
                syncToLive: function (path) {
                    if (path === undefined || path === null || path === "") {
                        return false;
                    }
                    if (this.internalModel === undefined || this.internalModel === null) {
                        return false;
                    }
                    if (this.model.has(path)) {
                        this.model.set(path, "", {silent: true});
                    }
                    //ii. Live Set from internal
                    var updated = this.internalModel.get(path);

                    if (updated === undefined || updated === null) {
                        return false;
                    }
                    this.model.set(path, updated/*, {silent:true}*/);
                },
                /**
                 * Utility function to add element to an array in the internal object, using add
                 * Add acts like set(), but appends the item to the nested array.
                 * 
                 * @return the index of the added item
                 */
                insertToArray: function (arrayPath, object) {
                    if (this.internalModel === null) {
                        return false;
                    }

                    var array = this.internalModel.get(arrayPath);

                    if (array === undefined || array === null) {
                        //it does not exist, let's add an empty array so that we may continue
                        var emptyArray = {};
                        emptyArray[ arrayPath ] = [];
                        this.internalModel.set(emptyArray);
                        //update the reference to the array
                        array = this.internalModel.get(arrayPath);
                    }

                    if (!$.isArray(array)) {
                        return false;
                    }

                    this.internalModel.add(arrayPath, object);

                    return array.length;
                },
                /**
                 * Remove from array
                 * @param arrayPath
                 * @param index: index of object to be removed in the array
                 * @param object
                 * @returns
                 */
                removeFromArray: function (arrayPath, index, object) {
                    if (this.internalModel === null) {
                        return false;
                    }

                    var array = this.internalModel.get(arrayPath);

                    if (_.isEmpty(array)) {
                        return -1;
                    }

                    if (!_.isArray(array)) {
                        return -1;
                    }
//				console.log("remove path " + arrayPath +"["+index+"]" );
                    this.internalModel.remove(arrayPath + "[" + index + "]");

                    return array.length;
                },
                /**
                 * Utility function to check whether the section is among this.SECTION
                 * @return boolean, true if the section is NOT one of this.SECTION
                 */
                isReferenceReceptor: function (section) {
                    if (section === undefined || section === null || section === "") {
                        return false;
                    }
                    return ($.inArray(section, this.SECTIONS) === -1);
                },
                /**
                 * Retrieves a list of attachment ids - based on the idref- of the ReferenceTo array of the given section
                 * @param the current section to search
                 */
                existingLinks: function (section) {
                    var ids = [];

                    if (this.internalModel === undefined || this.internalModel === null) {
                        return ids;
                    }

                    if (section === undefined || section === null || section === "") {
                        return ids;
                    }

                    var refSection = this.internalModel.documentation().referenceToPath(section);

                    var refs = this.internalModel.get(refSection);

                    if (refs === undefined || !$.isArray(refs)) {
                        return ids;
                    }

                    $(refs).each(function (idx, ref) {
                        var idref = ref.idref;
                        if (idref !== undefined && idref !== null && idref !== "") {
                            ids.push(idref);
                        }
                    });
                    return ids;
                },
                /**
                 * Add or remove a ReferenceTo link
                 */
                toggleLink: function (attachId, section) {
                    if (attachId === undefined || attachId === null || attachId === "") {
                        return false;
                    }

                    var result = this.internalModel.documentation().findReferenceTo(attachId, section);
                    var index = result.index;

                    var refSection = this.internalModel.documentation().referenceToPath(section);

                    if (index === null) {
                        return false;
                    }
                    switch (index) {
                        case -3:
                        {//not array
                            //feedback message handled by AttachmentEditForm.unLink()
                            return false;
                        }
                        case -2:
                        { //no section, thus continue to next in order to CREATE section
                        }
                        case -1:
                        { //reference to does not exist, thus LINK
                            this.insertToArray(refSection, {"idref": attachId});
                            break;
                        }
                        default:
                        {//reference to, exists, thus UNLINK - remove section
                            this.removeFromArray(refSection, index, {"idref": attachId});
                        }
                    }
                    return true;
                },
                /**
                 * Unlink specific section
                 */
                unlink: function (reference) {
                    this.internalModel.reset(reference, true);
                    return true;
                },

                /************* PHOTO RELATED *******************************/
                keepPhoto: function (photoData) {
                    if (this.internalModel !== null) {
                        //Save PhotoData
                        this.internalModel.set(this.model.PHOTO, photoData);
                    }
                },
                savePhoto: function (cropConfig, scaleConfig) {

                    if (cropConfig !== undefined) {
                        if (cropConfig === null) {
                            //console.log("SAVE: remove cropping");
                            Utils.removeMetadata(this.internalModel, this.model.PHOTO, "cropping");
                        } else {
                            //console.log("SAVE: set cropping to " + JSON.stringify( cropConfig) );
                            //Set the crop data to the photodata of the internal model
                            Utils.setMetadata(this.internalModel, this.model.PHOTO, "cropping", cropConfig);
                        }
                    }

                    if (scaleConfig !== undefined) {
                        if (scaleConfig === null) {
                            //console.log("SAVE: remove cropping");
                            Utils.removeMetadata(this.internalModel, this.model.PHOTO, "scaling");
                        } else {
                            //console.log("SAVE: set cropping to " + JSON.stringify( cropConfig) );
                            //Set the crop data to the photodata of the internal model
                            Utils.setMetadata(this.internalModel, this.model.PHOTO, "scaling", scaleConfig);
                        }
                    }

                    this.syncToLive(this.model.PHOTO);

                    this.throwaway();
                },

                /************* SIGNATURE RELATED *******************************/
                keepSignature: function (signatureData) {
                    if (this.internalModel !== null) {
                        //Save PhotoData
                        this.internalModel.set(this.internalModel.SIGNATURE, signatureData);
                    }
                },
                saveSignature: function (cropConfig, scaleConfig) {

                    if (cropConfig !== undefined) {
                        if (cropConfig === null) {
                            //console.log("SAVE: remove cropping");
                            Utils.removeMetadata(this.internalModel, this.model.SIGNATURE, "cropping");
                        } else {
                            //console.log("SAVE: set cropping to " + JSON.stringify( cropConfig) );
                            //Set the crop data to the photodata of the internal model
                            Utils.setMetadata(this.internalModel, this.model.SIGNATURE, "cropping", cropConfig);
                        }
                    }
                    
                    if (scaleConfig !== undefined) {
                        if (scaleConfig === null) {
                            Utils.removeMetadata(this.internalModel, this.model.SIGNATURE, "scaling");
                        } else {
                            Utils.setMetadata(this.internalModel, this.model.SIGNATURE, "scaling", scaleConfig);
                        }
                    }
                    this.syncToLive(this.model.SIGNATURE);

                    this.throwaway();
                },

                /** moved from Utils, used in AttachmentEditForm */
                getButtonRelatedSection: function (btn) {
                    var rel = btn.attr("data-rel-section");
                    if (rel === undefined || rel === null || rel === "") {
                        return null;
                    }
                    return rel;
                },
                /** moved from Utils, used in AttachmentEditForm */
                getButtonRelatedSubForm: function (btn) {
                    var rel = btn.attr("data-rel-subform");
                    var section = $(Utils.jId(rel));
                    return section;
                },
                /** moved from Utils, used in AttachmentEditForm,  AttachmentFormView, LinkedAttachmentFormView */
                getRelatedSubForm: function (event) {
                    var btn = $(event.target);
                    return this.getButtonRelatedSubForm(btn);
                }
            });
            return AttachmentManagerView;
        }
);