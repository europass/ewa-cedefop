define(
        [
            'module',
            'jquery',
//		'underscore',
            'backbone',

            'hbs!templates/upload/localfile',

            'europass/http/FileManager',
            'europass/http/MediaType',
            'europass/http/ServicesUri',
            'views/upload/UploadController',

            'i18n!localization/nls/GuiLabel',
            'i18n!localization/nls/Notification',
            'analytics/EventsController'
        ],
        function (
                module, $,
//		EWA-1811
//		_,
                Backbone,
                Template,
                FileManager, MediaType, ServicesUri, UploadController,
                GuiLabel,
                Notification,
                Events

                ) {

            var LocalFileUploadView = Backbone.View.extend({
                name: "local",
                event: new Events,
                events: {
                    "click .upload": "onBrowse"
                },
                onClose: function () {
                    this.uploadController.cleanup();
                    delete this.uploadController;
                },
                initialize: function (options) {

                    this.serviceUrl = ServicesUri.document_upload;

                    this.parentView = options.parentView;

                    this.messageContainer = options.messageContainer;

                    //Reusable Upload Controller
                    this.uploadController = new UploadController({
                        relatedController: this,
                        messageContainer: this.messageContainer,
                        modelUpdateEvent: "model:uploaded:esp",
                        modelUpdateMsgKey: "success.upload.cv.saved"
                    });
                    this.form = options.form;
                },
                /**
                 * Render the View
                 */
                render: function () {
                    //This view must be rendered each time to capture changes in the model

                    var html = Template(this.parentView.getContext());
                    $('#import-local-file').html(html);

                    //File Manager Specifics
                    this.dropZone = this.$el.closest(".modal").find(".dropzone");

                    this.uploadingContainer = this.$el.find(".uploading-container").first();

                    this.uploadBtn = this.$el.find("span.button.upload>span.title").first();
                    this.prepareBrowse();

                    //Required by FileManager
                    this.$el.closest("form").attr("enctype", MediaType.multipart);

                    //Enable the file uploading functionality
                    this.enableFileUpload();
                },
                onBrowse: function () {
                    this.event.importFrom('Computer Button');
                },
                /**
                 * Complete the upload. Use the uploaded profile to populate the editor.
                 */
                doComplete: function () {
                    this.uploadController.uploaded();
                    this.form.onClose();
                },
                /**
                 * Use the FileManager plugin to allow file upload 
                 */
                enableFileUpload: function () {
                    FileManager.enableFileUpload(this.$el, {

                        uploadDocument: true,

                        messageContainer: this.messageContainer,

                        indexUploadBtn: 0,

                        dropZone: this.dropZone,

                        onAdd: {
                            f: this.onAddCallback,
                            scope: this
                        },
                        onDone: {
                            f: this.onUploadSuccess,
                            scope: this,
                            showMessage: true,
                            message: Notification["success.upload.cv"],
                            blinkMessage: false,
                            appendFileName: true
                        },
                        onFail: {
                            f: this.onUploadFail,
                            scope: this
                        }

                    });
                },

                /**
                 * Add callback
                 */
                onAddCallback: function () {
//			console.log("Add callback");
                    this.form.onClose();
                },
                /**
                 * Upload success callback
                 */
                onUploadSuccess: function (json) {

                    FileManager.removeDropzone(this.$el);  //disable the file manager and the dropzone

                    this.uploadController.onUploadSuccess(json, this.parentView);

                    this.hideBrowse();

                    this.$el.trigger("europass:wizard:import:complete");
                    this.doComplete();
                },
                /**
                 * Upload failed
                 */
                onUploadFail: function () {
//			console.log("Failure callback");
                    if (FileManager.getNoFiles() === 0) { //case the uploads finish with failure...

                        FileManager.cleanupUploaded(this.$el);

                        this.enableBrowse();
                    }
                },
                /**
                 * Enable/Show the Browse button
                 */
                enableBrowse: function () {
                    //add error status info
                    this.parentView.setModalFeedbackClass("error");
                },
                /**
                 * Set the text properly and disable it
                 */
                prepareBrowse: function () {
                    this.uploadBtn.text(GuiLabel["text.browse.file"] || "BROWSE FILE");
                    //remove any error or success status info				
                },
                /**
                 * Hide the Browse button, on a successful outcome
                 */
                hideBrowse: function () {
                    //add success status info
                    this.parentView.setModalFeedbackClass("success");
                    this.$el.find("#fileupload").hide();
                },
                clearDropzone: function () {
                    FileManager.removeDropzone(this.$el);
                }
            });

            return LocalFileUploadView;
        }
);
