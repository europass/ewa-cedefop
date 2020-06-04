define(
        [
            'jquery',
            'jqueryui',
            'underscore',
            'Utils',
            'views/forms/FormView',
            'views/forms/attachment/AttachmentEditFormView',
            'views/forms/attachment/AttachmentCloudFormView',
            'hbs!templates/attachment/listlinkedattachmentactions',
            'hbs!templates/attachment/attachmentdelete',
            'europass/http/FileManager',
            'views/attachment/AttachmentManagerInstance',
            'europass/http/MediaType',
            'europass/structures/PreferencesSchema'
        ],
        function ($, jqueryui, _,
                Utils,
                FormView,
                AttachmentEditFormView,
                AttachmentCloudFormView,
                ActionsTpl,
                DeleteTpl,
                FileManager,
                AttachmentManager,
                MediaType,
                //EWA-1811?
                PreferencesSchema) {

            var LinkedAttachmentFormView = function (options) {
                AttachmentEditFormView.apply(this, [options]);
            };
            LinkedAttachmentFormView.prototype = {

                withinAnnexes: true

                , uploaderSelector: "#fileupload"

                , messageContainerSelector: ".upload.feedback-area"

                , uploadedContainerSelector: ".uploaded-files"

                , dropZoneSelector: ".Upload.dropzone"

                , deleteContainerSelector: ".attachment-delete"

                , linkedCls: "linked"

                , unlinkedCls: "unlinked"

                        //Events of AttachmentEditFormView, AttachmentCloudFormView plus those here..
                , events: _.extend({
                    "europass:attachment:form:rerender": "onReRender",
                    "click .option.delete.toggle-expansion": "requestDeleteAttachment",
                    "click .attachment-delete :button.confirm": "deleteAttachment",
                    "click .attachment-delete :button.reject": "rejectAction",
                    "click .option.attach": "toggleLink"

                }, AttachmentEditFormView.prototype.events, AttachmentCloudFormView.prototype.events)

                , actionsTemplate: ActionsTpl

                        /**
                         * @Override
                         */
                , onInit: function (options) {

                    this.type = options.section.substr(options.section.lastIndexOf(".") + 1);

                    AttachmentEditFormView.prototype.onInit.apply(this, [options]);

                    this.updateFormElAttributes();
                }
                , updateFormElAttributes: function () {
                    this.frm.attr("enctype", MediaType.multipart);
                }
                /**
                 * On clicking of the link/unlink button
                 */
                , toggleLink: function (event) {
                    var btn = $(event.target);

                    var attachId = btn.attr("data-rel");

                    var toggle = AttachmentManager.toggleLink(attachId, this.section);
                    var messageContainer = this.$el.find(this.messageContainerSelector);
                    if (toggle) {
                        var li = btn.closest("li");
                        li.toggleClass(this.linkedCls);
                        li.toggleClass(this.unlinkedCls);
                        btn.hide();
                        btn.siblings("a.attach").show();
                    } else {
                        require(
                                ['i18n!localization/nls/Notification'],
                                function (Notification) {
                                    var errorMsg = "<em>" + Notification["error.attachment.link.general"] + "</em>";
                                    messageContainer.trigger("europass:message:show", ["error", errorMsg, false]);
                                }
                        );
                    }
                }
                /**
                 * @Override
                 * 
                 * Relevant model here is the entire model.
                 * When adding attachments, the relevant part is the entire model and specifically the list of attachments
                 */
                , relevantModelSection: function (attachmentInfo) {
                    var capturedModel = AttachmentManager.capture();
                    if (capturedModel === undefined || capturedModel === null) {
                        return {};
                    } else {
                        var model = AttachmentManager.capture().attributes;
                        model.subsection = this.type;
                        return model;
                    }
                }
                /**
                 * @Override
                 */
                , enableFunctionalities: function () {

                    // Prepare the attachment manager in order to obtain a clone of the live model
                    AttachmentManager.prepare();

                    this.messageContainer = this.$el.find(this.messageContainerSelector);
                    this.uploadedContainer = this.$el.find(this.uploadedContainerSelector);

                    this.applyLinking();

                    // Enable File Uploading
                    FileManager.enableFileUpload(this.$el, {

                        selector: this.uploaderSelector,

                        isPhoto: false,

                        indexUploadBtn: 0,

                        messageContainer: this.messageContainer,

                        dropZone: this.$el.find(this.dropZoneSelector),

                        onAdd: {
                            f: this.onAddCallback,
                            scope: this
                        },
                        onDone: {
                            f: this.onUploadSuccess,
                            scope: this
                        },
                        onFail: {
                            f: this.onUploadFail,
                            scope: this
                        }
                    });

                    //call parent enable functionalities
                    AttachmentEditFormView.prototype.enableFunctionalities.call(this);
                    // this includes AttachmentManager.prepare()

                }
                /**
                 * Apply the class names and hide the corresponding buttons
                 */
                , applyLinking: function () {
                    // Prepare the class names and tooltip texts of the link/unlink buttons
                    var linked = AttachmentManager.existingLinks(this.section);
                    var that = this;
                    //For each of the linked attachment id, update the li class to be 'linked'

                    $(linked).each(function (idx, link) {
                        var btn = that.frm.find("a.link.attach[data-rel=\"" + link + "\"]");
                        btn.closest("li").addClass(that.linkedCls);
                        btn.hide();
                    });
                    this.$el.find("ul.existing-attachment > li:not(." + this.linkedCls + ")").each(function (idx, el) {
                        var li = $(el);
                        li.addClass(that.unlinkedCls);
                        li.find("a.attach.unlink").hide();
                    });
                }
                , onAddCallback: function () {
                    this.updateMenuAvailability(false);
                }
                , onUploadFail: function () {
                    if (FileManager.getNoFiles() === 0) { //case the uploads finish with failure...

                        //Clear attached
                        //Fire event to re-render the form with the Temporary Model of the Attachment Manager!
                        this.$el.trigger("europass:attachment:temp:model:changed");

                        //Save should be available now...
                        this.updateMenuAvailability(true);
                    }
                }
                /**
                 * This runs for each attachment
                 */
                , onUploadSuccess: function (json) {

                    var section = this.frm.attr("data-rel-section");

                    var filedata = json.FileData;
                    if (filedata === undefined || filedata === null) {
                        FileManager.displayError(this.messageContainer);
                        return false;
                    }
                    AttachmentManager.enrich(this.uploadedContainer, section, filedata);

                    //Re-render with Temp Model once all uploads are done...
                    if (FileManager.getNoFiles() === 0) { //case uploads finish with success...

                        //Clear attached
                        //Fire event to re-render the form with the Temporary Model of the Attachment Manager!
                        this.$el.trigger("europass:attachment:temp:model:changed");

                        //Save should be available now...
                        this.updateMenuAvailability(true);
                    }
                }
                /**
                 * Re-rendering the attachment details...
                 * Perform any cleanup, such as clearing up the uploaded section
                 */
                , onReRender: function (event) {
                    FileManager.cleanupUploaded(this.$el);
                }
                /**
                 * @Override
                 * Re-render the specific updatable part based on Attachment Manager's Temporary Model
                 */
                , reRender: function (event, attachmentInfo) {
                    //Get the relevant model to re-render against it the template
                    var model = this.relevantModelSection();
                    var html = this.actionsTemplate(model);

                    var el = this.$el.find(this.updateableContainerSelector);
                    if (el.length > 0) {
                        var that = this;
                        el.fadeOut('slow', function () {
                            el.html(html);

                            //Class names and hide buttons
                            that.applyLinking();

                            el.fadeIn('slow');
                        });
                    }
                    this.$el.trigger("europass:attachment:form:rerender");

                }
                /**
                 * Request to show the template of deleting an attachment 
                 */
                , requestDeleteAttachment: function (event) {
                    //if not empty...
                    var section = AttachmentManager.getRelatedSubForm(event);
                    if (section.html() === "") {
                        this.prepareDeleteContext(section);
                    }

                    this.hideOptions(event);
                }
                /**
                 * Confirm the deletion of the attachment
                 */
                , deleteAttachment: function (event) {
                    var attachment = Utils.getRelatedSection(event);

                    var btn = $(event.target);
                    var id = btn.attr("data-rel-id");
                    var index = btn.attr("data-rel-index");

                    if (attachment != null && index != null) {

                        AttachmentManager.deleteAttachment(attachment, id, index);

                        //Fire event to re-render the form with the Temporary Model of the Attachment Manager!
                        this.$el.trigger("europass:attachment:temp:model:changed");
                    } else {
                        this.rejectAction(event);
                    }

                }
                /**
                 * Prepare the template to delete the current attachment
                 */
                , prepareDeleteContext: function (section) {

                    var id = (section.length > 0) ? section.attr("data-rel-id") : null;

                    var model = AttachmentManager.capture();
                    var attachInfo = model.documentation().attachmentById(id);

                    var attachment = {};
                    var index = null;
                    if (id != null && attachInfo != null) {
                        //find Attachment based on this Id
                        attachment = attachInfo.attachment;
                        index = attachInfo.index;
                    }
                    var context = {};
                    context["Attachment"] = attachment;
                    context["att_index"] = index;
                    context["subsection"] = (this.docType === undefined ? "" : (this.docType + "_")) + this.type;

                    var html = DeleteTpl(context);

                    section.html(html);
                }
                , doSubmit: function () {
                    //before saving to the live model...
                    //See if live model has Attachments or Photo: if true then do not trigger keep alive session
                    var modelInfo = _.isFunction(this.model.info) ? this.model.info() : null;

                    var triggerAttachmentsChanged = false;
                    /*vpol:why keep alive mess up re-rendering
                     * steps to reproduce problem
                     * 1. hit erase CV editor
                     * 2. add new content on one of the skills, for example Communication Skills and hit Save
                     * 3. The section is rerendered twice causing the save-yellow-effect to be non visible. This happens only in the 1st save. If model is not empty then it is not happening.
                     * The reason is that the keepAlive event triggers the "model:binaries:reset" event, which in turn causes the SkillsPassportComposeView.onReRendering method, that resets all cv sections!
                     * The SkillsPassportComposeView.onReRendering redraws the whole CV sections (removes and redraws) 
                     * The effect is not visible when the save is done on work-exp, or headline, or personal info sections, because they are evaluated first and the have more time until the final re-render is performed. So the save-yellow-effect completes before onReRendering
                     */

                    // Get Link / Unlink Attachment in current Section (this.section) before the changes applied
                    var currentLinkedAttachements = this.model.get(this.section + ".ReferenceTo");
                    if (currentLinkedAttachements == undefined) {
                        currentLinkedAttachements = [];
                    }

                    // Get model's attachments before the changes applied
                    var currentModelAttachments = this.model.get("SkillsPassport.Attachment");
                    if (currentModelAttachments == undefined) {
                        currentModelAttachments = [];
                    }

                    AttachmentManager.saveAllReferenceTo();

                    // 1st case: check if any attachment has been linked to or unlinked from the current section  
                    var liveLinkedAttachments = this.model.get(this.section + ".ReferenceTo");

                    if (liveLinkedAttachments !== undefined && $.isArray(liveLinkedAttachments)) {

                        //TODO: make utility function
                        var currentReferenceIDsArray = [];
                        for (var i = 0; i < currentLinkedAttachements.length; i++) {
                            currentReferenceIDsArray.push(currentLinkedAttachements[i].idref);
                        }
                        var liveReferenceIDsArray = [];
                        for (var i = 0; i < liveLinkedAttachments.length; i++) {
                            liveReferenceIDsArray.push(liveLinkedAttachments[i].idref);
                        }

                        if (!Utils.compareArray(currentReferenceIDsArray, liveReferenceIDsArray))
                            triggerAttachmentsChanged = true;

                    }

                    var liveModelAttachments = this.model.get("SkillsPassport.Attachment");

                    if (liveModelAttachments !== undefined && $.isArray(liveModelAttachments)) {

                        // 2nd case: check if any attachement was edited (renamed, added, deleted)

                        // Colect the id's and descriptions for comparing
                        var currentIdArray = [];
                        var currentDescriptionArray = [];
                        for (var i = 0; i < currentModelAttachments.length; i++) {
                            currentIdArray.push(currentModelAttachments[i].Id);
                            currentDescriptionArray.push(currentModelAttachments[i].Description);
                        }

                        var liveIdArray = [];
                        var liveDescriptionArray = [];
                        for (var i = 0; i < liveModelAttachments.length; i++) {
                            liveIdArray.push(liveModelAttachments[i].Id);
                            liveDescriptionArray.push(liveModelAttachments[i].Description);
                        }

                        // Check if any attachment has been renamed
                        if (!Utils.compareArray(currentDescriptionArray, liveDescriptionArray))
                            triggerAttachmentsChanged = true;

                        // Check if any attachment has been added or deleted
                        if (!Utils.compareArray(currentIdArray, liveIdArray))
                            triggerAttachmentsChanged = true;
                    }

                    if (triggerAttachmentsChanged) {
                        this.model.trigger("linked:attachment:changed");
                        //triggered for the generic skill
                        if (this.section.lastIndexOf("SkillsPassport.LearnerInfo.Skills", 0) === 0)
                            this.model.setActiveSkillsSection(this.section);
                    }


                }
                , doModalClosed: function() {
                    if (AttachmentManager.internalModel === undefined || AttachmentManager.internalModel === null) {
                        return false;
                    }
                    return !_.isEqual(AttachmentManager.model.get(AttachmentManager.ATTACHMENTS), AttachmentManager.internalModel.get(AttachmentManager.ATTACHMENTS)) ||
                            !_.isEqual(AttachmentManager.model.get(this.section + ".ReferenceTo"), AttachmentManager.internalModel.get(this.section + ".ReferenceTo"));
                }
                /** When the modal is closed **/
                , onModalClose: function () {
                    FileManager.disableFileUpload(this.$el);
                }
                /** When the modal is cancelled **/
                , cancelled: function () {
                    FileManager.cancelSuccessUploads(
                            FormView.prototype.cancelled, this, []
                            );
                }

            };

            LinkedAttachmentFormView.prototype = $.extend(
                    //true, 
                            {},
                            AttachmentEditFormView.prototype,
                            AttachmentCloudFormView.prototype,
                            LinkedAttachmentFormView.prototype
                            );

                    return LinkedAttachmentFormView;
                }
        );