define(
        [
            'jquery',
            'underscore',
            'Utils',
            'HttpUtils',
            'views/forms/FormView',
            'views/forms/attachment/AttachmentEditFormView',
            'views/forms/attachment/AttachmentCloudFormView',
            'hbs!templates/attachment/attachment',
            'hbs!templates/attachment/listattachmentactions',
            'hbs!templates/attachment/attachmentdelete',
            'europass/http/FileManager',
            'views/attachment/AttachmentManagerInstance',
            'europass/http/MediaType',
            'ModalFormInteractions'
        ],
        function ($,
                _,
                Utils,
                HttpUtils,
                FormView,
                AttachmentEditFormView,
                AttachmentCloudFormView,
                HtmlTemplate,
                ActionsTpl,
                DeleteTpl,
                FileManager,
                AttachmentManager,
                MediaType,
                ModalFormInteractions) {

            //augment the View with functions from the AttachmentEditFormView
            var AttachmentFormView = function (options) {
                AttachmentEditFormView.apply(this, [options]);
            };

            AttachmentFormView.prototype = {

                type: "Add"

                , withinAnnexes: true

                , uploaderSelector: "#fileupload"

                , messageContainerSelector: ".upload.feedback-area"

                , uploadedContainerSelector: ".uploaded-files"

                , dropZoneSelector: ".Upload.dropzone"

                , deleteContainerSelector: ".attachment-delete"

                        //Events of AttachmentEditFormView, AttachmentCloudFormView plus those here..
                , events: _.extend({
                    "europass:attachment:form:rerender": "onReRender",
                    "click .option.delete.toggle-expansion": "requestDeleteAttachment",
                    "click .attachment-delete :button.confirm": "deleteAttachment",
                    "click .attachment-delete :button.reject": "rejectAction"

                }, AttachmentEditFormView.prototype.events, AttachmentCloudFormView.prototype.events)

                , htmlTemplate: HtmlTemplate

                , actionsTemplate: ActionsTpl

                        /**
                         * @Override
                         */
                , onInit: function (options) {
                    AttachmentEditFormView.prototype.onInit.apply(this, [options]);

                    this.updateFormElAttributes();
                }

                , updateFormElAttributes: function () {
                    this.frm.attr("enctype", MediaType.multipart);
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
                        return AttachmentManager.capture().attributes;
                    }
                }
                /**
                 * @Override
                 */
                , enableFunctionalities: function () {
                    this.messageContainer = this.$el.find(this.messageContainerSelector);
                    this.uploadedContainer = this.$el.find(this.uploadedContainerSelector);

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
                    var filedata = json.FileData;
                    if (filedata === undefined || filedata === null) {
                        FileManager.displayError(this.messageContainer);
                        return false;
                    }
                    // Warning feedback
                    var feedback = json.Feedback;

                    if (!_.isUndefined(feedback) && !_.isNull(feedback)) {
                        //cast it to an array to be readable for http utils
                        feedback = [feedback];
                        if ($.isArray(feedback) && feedback.length > 0) {

                            var feedbackMsg = HttpUtils.readableFeedback(feedback);

                            this.messageContainer
                                    .trigger("europass:message:show",
                                            ["warning", feedbackMsg]);
                        }
                    }
                    AttachmentManager.enrich(this.uploadedContainer, "", filedata);

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
                    var btn = $(event.target);

                    var attachment = Utils.getRelatedSection(event);

                    var id = btn.attr("data-rel-id");
                    var index = btn.attr("data-rel-index");

                    if (attachment !== null && index !== null) {
                        this.toggleInAction(btn);

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
                    if (model) {
                        var attachInfo = model.documentation().attachmentById(id);
                        var attachment = {};
                        var index = null;
                        if (id !== null && attachInfo !== null) {
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
                }
                /**
                 * @Override
                 */
                , submitted: function (event) {
                    //console.log(" === ATTACHMENT FORM VIEW - SUBMIT ===");
                    //start the waiting indicator...
                    this.$el.trigger("europass:waiting:indicator:show");

                    //before saving to the live model...
                    //See if live model has Attachments or Photo: if true then do not trigger keep alive session
                    var modelInfo = _.isFunction(this.model.info) ? this.model.info() : null;

                    AttachmentManager.saveAll();

                    this.closeModal();

                    //Trigger global re-render
                    this.model.trigger("linked:attachment:changed", "click-origin-controls");
                }
                , modalClosed: function (event) {
                    var hasChanged = false;
                    if (AttachmentManager.internalModel !== null && AttachmentManager.internalModel !== undefined && 
                            !_.isEqual(AttachmentManager.model.get(AttachmentManager.ATTACHMENTS), AttachmentManager.internalModel.get(AttachmentManager.ATTACHMENTS))) {
                        hasChanged = true;
                    }

                    if (hasChanged) {
                        ModalFormInteractions.confirmSaveSection(event, this.frm.attr("id"));
                    } else {
                        this.closeModal();
                    }
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

            AttachmentFormView.prototype = $.extend(
                    //true, 
                            {},
                            AttachmentEditFormView.prototype,
                            AttachmentCloudFormView.prototype,
                            AttachmentFormView.prototype
                            );

                    return AttachmentFormView;
                }
        );