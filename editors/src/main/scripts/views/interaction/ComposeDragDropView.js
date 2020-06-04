/* This module is responsible for mediating between its caller (CV/ESP compose view) 
 * and the callee (the respective modal dialogs and upload handlers which are chosen by configuration)
 * The compose views that use this, trigger a dragdrop:init event inside their init method
 * The global controller handles it by calling a new DragDropView with a config parameter
 * Same business for compose view's onClose
 * 
 * Operation: once called, it should initially call fileManager's prepareDropzone(). 
 * Once a file is dropped inside: if in CV, single file, check if its a europass doc, then
 * Then, depending on context, show the respective confirmation dialog 
 * and/or modal form ( the views should be loaded lazily from factory)
 * 
 * IMPORTANT:: europass document check operation :: 
 * TODO explain how it works, like a filemanager upload , callback handles it... 
 * 
 * TODO any future implementation (the task 1622 details that were not implemented)
 * should focus on adding logic in the before add callback in ECV SPECIFIC LOGIC point (in else ifs etc..)
 *
 * Once the upload operation is completed, the context should be re rendered 
 **/

define(
        [
            'jquery',
            'underscore',
            'backbone',
            'handlebars',
            'Utils',
//		EWA-1811
            'HttpUtils',
            'europass/http/WindowConfigInstance',
            'europass/http/ServicesUri',
            'europass/http/FileManager',
//		'i18n!localization/nls/Notification',
            'i18n!localization/nls/GuiLabel',
            'hbs!templates/attachment/dragndrop-progress',
            'hbs!templates/dialog/uploadconfirmation',
            'hbs!templates/dialog/photouploadconfirmation',
            'hbs!templates/dialog/fileimportconfirmation',
            'hbs!templates/dialog/importconfirmation',
            'views/upload/UploadController',
            'views/attachment/AttachmentManagerInstance',
            'analytics/EventsController',
            'HelperManageModelUtils'
        ],
        function ($, _, Backbone, handlebars, Utils, HttpUtils, Config, ServicesUri,
                FileManager, /*Notification,*/ GuiLabel, progressBarTpl, ConfirmationTpl, PhotoTpl, FileImportTpl, UploadTpl,
                UploadController, AttachmentManager, Events, HelperManageModelUtils) {
            var ComposeDragDropView = Backbone.View.extend({

                el: "body"

                , defaultMsgContainer: "#app-notifications"

                , dZMsgContainer: ".upload.feedback-area"

                , defaultAreaSelector: "#main-content-area"

                , uploadedContainerSelector: ".uploaded-files"

                , getDropzone: function () {
                    return $("#main-content-area > .dropzone");
                }

                , isAttachment: false //boolean value for type of download checks

                , isEditorEmpty: null

                , event: new Events

                , events: {
                    "click .dropzone .cancel": "cancelled",

                    "click :button.upload-confirm.confirm-cancel": "cancelConfirm",
                    "click :button.upload-confirm.confirm-submit": "proceedUploading"
                }

                /* Called after view is invoked. 
                 * @param options is the constructor's arguments
                 * */
                , initialize: function (options) {

                    //console.log( "dragdropview init");

                    //Reusable Upload Controller
                    this.uploadController = new UploadController({
                        relatedController: this,
                        messageContainer: this.messageContainer,
                        modelUpdateEvent: "model:uploaded:esp",
                        modelUpdateMsgKey: "success.upload.cv.saved"
                    });

                    this.isEditorEmpty = options.isEditorEmpty;
                    this.currentDocument = options.currentDocument;

                    this.hideDropzone = function (el) {
                        //console.log(">>>>>>>>> HIDE DZ");	
                        FileManager.dropZoneOutHover(el);
                    };
                }

                /**
                 * config:
                 * {
                 *   onDone: function to run after successfull upload,
                 *   onFail: function to run after failed upload,
                 *   messageContainer: the message container or the file upload area itself.
                 * }
                 */
                , enableDragDrop: function ($el) {

                    //console.log( "dragdropview enableDragDrop");

                    var dropZone = this.getDropzone();

                    dropZone.removeClass("disabled");
//				if ( dropZone.length == 0 )	//TODO get this from templae
//					dropZone = $( "<div>", {class:"dropzone",id:"fileupload"} ).prependTo( $el.closest( this.defaultAreaSelector ) );

                    //returns drop here template
                    var $dropHere = $(FileManager.getDropIndication()).prependTo(dropZone); //TODO careful of require.

                    var uploadHtml = $(progressBarTpl({}));

                    uploadHtml.hide();

                    $dropHere.append(uploadHtml);

                    this.prepare(dropZone);

                }

                , prepare: function (dropZone) {

                    FileManager.enableFileUpload(dropZone, {//wrapper

                        selector: ".upload-area",

                        isPhoto: false,

                        indexUploadBtn: 0,

                        relatedController: this,

                        messageContainer: $(this.defaultMsgContainer),

                        dropZone: dropZone/*this.$el.find( this.dropZoneSelector )*/,

                        beforeAdd: {
                            f: this.beforeAdd,
                            scope: this
                        },
                        onAdd: {
                            f: this.onAddCallback,
                            scope: this
                        },
                        onDone: {
                            showMessage: false,
                            f: this.onUploadSuccess,
                            scope: this
                        },
                        onFail: {
                            f: this.onUploadFail,
                            scope: this,
                            showMessage: false
                        }
                        //selector 
                    }, ".upload-area");
                }

                /** detach current filemanager and clear-disable dropzone 
                 * */
                , disableDragDrop: function ($el) {

                    //console.log("DragDropHandler disableDragDrop  "); //TODO remove this

                    var dropZone = $(this.defaultAreaSelector).find(".dropzone");

                    dropZone.addClass("disabled");
//				if ( dropZone.length == 0)
//					dropZone = $( "<div>", {class:"dropzone"} ).prependTo( $el.closest( this.defaultAreaSelector ) );

                    this.cancelled(); // abort pending uploads, hide dz

                    if (typeof jqXHR !== "undefined" && !$.isEmptyObject(jqXHR))
                        jqXHR = null; 	//release resource TODO careful of leaving hanging calls

                    FileManager.clearDropzone(dropZone, this.defaultAreaSelector);
                    FileManager.disableFileUpload(this.$el);// TODO merge these in one call
                }

                , showUploadArea: function () {
                    //console.log(">>>>>>>>> SHOW DZ UPLOAD");
                    var dz = this.getDropzone();

                    $(this.defaultAreaSelector).children().hide();

                    dz.show();

                    var $dropHere = dz.find(".drop.here");

                    $dropHere.show().
                            find(".dropMsg").hide()
                            .end()
                            .find(".upload-area,.text-uploading").show();
                }

                //#############   CALLBACKS FROM FILEMANAGER  ######################
                , beforeAdd: function (fileCount, fileExt) {

                    //console.log("DragDropHandler beforeAdd  callback, resets FileInfo "); //TODO remove this

                    var hasPhotoExt = Config.isAllowedFileExtension("photo", fileExt) === true;

                    var isXml = fileExt.toLowerCase().indexOf("xml") > 0; //TODO get programmatically
                    var hasDocExtension = Config.isAllowedFileExtension("esp", fileExt) === true || fileExt === "";//docTypes.indexOf( fileExt ) > 0;
                    var editorEmpty = this.isEditorEmpty();

                    var isEuropass = FileManager.fileInfo.isEuropass;

                    FileManager.resetFileInfo();

                    FileManager.fileInfo.filetype = "file"; //default value

                    this.showUploadArea();


                    if (this.currentDocument !== "ECV" || fileCount > 1 || isEuropass === false) {

                        if (fileCount > 1) {	//The message container for multiple files should be inside the dropzone upload area
                            return {messageContainer: this.getDropzone().find(this.dZMsgContainer)};
                        } else
                            return; 	//continue with attachment upload
                    }
                    //ECV SPECIFIC LOGIC ( check for photo/ Europass doc upload cases )
                    else if (fileCount === 1 && this.currentDocument === "ECV") {

                        // After manageable overwrite // EPAS-486 // Should not check for empty editor
                        if (isXml || hasDocExtension) {

                            FileManager.fileInfo.isDocumentUpload = true;
                            FileManager.fileInfo.filetype = "esp";
//						this.data  = data;

                            if (!isXml) {
                                FileManager.fileInfo.isEuroDocCheck = true;	//this will trigger Filemanager's send to check if it is a europass doc
                            }

                            if (isXml) {
                                this.showConfirmationModal("esp");
                                return false;
                            } else /*if ( hasDocExtension )*/
                                return; //continues with upload in Filemanager: add
                        } else if (hasPhotoExt) {
                            FileManager.fileInfo.filetype = "photo";
                            this.showConfirmationModal("photo");
                            return false;
                        }
                        return; //display any error checks
                    }
                }

                /**
                 *  File Add callback
                 */
                , onAddCallback: function () {

                    //console.log("DragDropHandler On add callback,files:"+ FileManager.getNoFiles() );//TODO remove this

                    //case the uploads finish with failure...
                    if (FileManager.getNoFiles() === 0 || typeof jqXHR === "undefined") {  //if you didnt get to send the data, there was an error
                        //disable waiting indicator , show err mesage
                        this.finalize();
                    } else {
                        var that = this;
                        setTimeout(function () {
                            that.showUploadArea();
                        }, 100);
                    }
                }
                /**
                 * This runs for each attachment
                 */
                , onUploadFail: function () {

                    //console.log("DragDropHandler Failure callback,files:"+ FileManager.getNoFiles() );//TODO remove this

                    if (FileManager.getNoFiles() === 0) { //case the uploads finish with failure...
                        //console.log("num of files => 0");TODO remove this
                        this.finalize();
                        FileManager.resetFileInfo();
                        //disable waiting indicator , show err mesage, Clear attached
                        //Fire event to re-render the form with the Temporary Model of the Attachment Manager!
                        //this.$el.trigger( "europass:attachment:temp:model:changed" );
                    }
                }

                /**
                 * as does this
                 */
                , onUploadSuccess: function (json) {
                    this.event.importFrom('Computer DragNDrop');
                    //console.log("DragDropHandler Success callback ,files:"+ FileManager.getNoFiles() ); //TODO remove this

                    json = json || {};

                    //when finished, if is document download , load the doc
                    if (FileManager.fileInfo.isEuroDocCheck === true) { 	//after successful check of whether single doc is europass

                        var isEuro = json.isEuro === "true" || json.isEuro === true; //convert to boolean

                        this.afterEuropassCheck(isEuro);
                        return;  //TODO !!!!! why not return false?
                    } else if (FileManager.fileInfo.isDocumentUpload === true) {
                        this.uploadController.onUploadSuccess(json);
                        this.uploadController.uploaded(true);
                        this.finalize();//return;
                        FileManager.resetFileInfo();
                        return;
                    }


                    var filedata = json.FileData;
                    if (filedata === undefined || filedata === null) {
                        FileManager.displayError($(this.defaultMsgContainer));
                        this.finalize();//return false;
                    }

                    // Warning feedback
                    var feedback = json.Feedback;

                    if (!_.isUndefined(feedback) && !_.isNull(feedback)) {
                        //cast it to an array to be readable for http utils
                        feedback = [feedback];
                        if ($.isArray(feedback) && feedback.length > 0) {

                            var feedbackMsg = HttpUtils.readableFeedback(feedback);

                            $(this.defaultMsgContainer)
                                    .trigger("europass:message:show",
                                            ["warning", feedbackMsg]);
                        }
                    }

                    // Prepare the attachment manager in order to obtain a clone of the live model
                    AttachmentManager.prepare();

                    //Update temp model
                    if (FileManager.fileInfo.isPhoto === true)
                        AttachmentManager.keepPhoto(filedata);		//update with photo
                    else
                        AttachmentManager.enrich($(this.uploadedContainerSelector), "", filedata);		//update with attachments

                    $(this.defaultAreaSelector).trigger("europass:attachment:temp:model:changed");

                    //save data in model
                    this.saveUpload();

                    if (FileManager.getNoFiles() === 0) { //case the uploads finish with failure...

                        this.finalize();
                    }
                }

                , afterEuropassCheck: function (isEuropass) {

                    //console.log("afterEuropassCheck, resets FileInfo");

                    var editorEmpty = this.isEditorEmpty();

                    FileManager.resetFileInfo();

                    if (isEuropass === false) {
                        FileManager.fileInfo.isEuropass = false;
                        FileManager.fileInfo.filetype = "file";
                        this.showConfirmationModal("file"/*, this.data*/); 	//should become file
                    } else if (isEuropass === true) {
                        FileManager.fileInfo.isEuropass = true;
                        FileManager.fileInfo.filetype = "esp";

                        // Previously upload was happening immediately when editor was empty !!
                        // After manageable overwrite that was changed !!
                        this.showConfirmationModal("esp"/*, this.data*/);///SHOULD become esp
                    }
                }

                //#############   CALLBACKS FROM FILEMANAGER END ######################

                , saveUpload: function ( ) {

                    //console.log("saveUpload, origin:"+(FileManager.fileInfo.filetype === "file")); //TODO remove this

                    //before saving to the live model...
                    //See if live model has Attachments or Photo: if true then do not trigger keep alive session
                    var modelInfo = _.isFunction(this.model.info) ? this.model.info() : null;

                    AttachmentManager.saveAll();

                    var currentFile = FileManager.fileInfo.filetype;
                    if (currentFile !== undefined) {
                        //Attachment(s) imported
                        if (currentFile === "file" || currentFile === null) {
                            //Trigger global re-render
                            this.model.trigger("linked:attachment:changed", "click-origin-controls");
                            //Photo imported	
                        } else if (currentFile === "photo") {
                            //Trigger photo re-render
                            this.model.trigger("content:changed", "SkillsPassport.LearnerInfo.Identification.Photo", "click-origin-compose");
                        }
                    }

                    FileManager.resetFileInfo();
                }

                //#############   MODAL INTERACTIONS  ######################
                /** shows the confirmation dialog TODO maybe impl in feedbackView?
                 *  @parameter fileType , should be "photo", "esp", or "file"
                 * */
                , showConfirmationModal: function (fileType/*, data*/) {

                    var _that = this;
                    require(
                            ['i18n!localization/nls/Notification'],
                            function (Notification) {

                                var context = {};
                                var docs = [{name: "ECL", title: GuiLabel["import.wizard.step3.doc.option.ECL"], empty: false},
                                    {name: "REST", title: GuiLabel["import.wizard.step3.doc.option.rest"], empty: false}];
                                context = {
                                    docs: docs
                                };
                                context.message = Notification[fileType + ".upload.confirmation.message"];

                                var html = ConfirmationTpl(context);

                                if (fileType === "esp")
                                    html = UploadTpl(context);
                                else if (fileType === "file")
                                    html = FileImportTpl(context)
                                else if (fileType === "photo")
                                    html = PhotoTpl(context);

                                var confirmModal = Utils.prepareModal("SkillsPassport." + fileType + ".Confirmation.Modal", html);
                                HelperManageModelUtils.customiseTemplateHtml(_that.$el, _that.model);

                                $(confirmModal).dialog("open");
                                _that.finalize();
                            }
                    );


                }

                /** When the upload modal is cancelled **/
                , cancelled: function () {

                    //console.log("DragDropHandler cancelled"); //TODO remove this

                    //FileManager.cancelSuccessUploads();
                    //FileManager.cleanupUploaded();
                    //$.proxy(FileManager.cancelFileUpload, FileManager);
                    if (typeof jqXHR !== "undefined" && !$.isEmptyObject(jqXHR) && $.isFunction(jqXHR.abort)) {
                        jqXHR.abort();
                        if (jqXHR.state() === "pending")
                            jqXHR = null;
                    }

                    this.finalize();
                }

                , proceedUploading: function (e, filetype, calledAfterEuroCheck) {

                    //console.log("DragDropHandler proceedUploading, success uploads: "+FileManager.successUploads+",num of files:"+FileManager.noFiles); //TODO remove this

                    if (FileManager.getNoFiles() > 0) {  //TODO Fix the bug where after changing from ESP to CV there are multiple executions of button click callbacks 
                        //console.log("proceedUploading exits (noFiles>0)");
                        return;
                    }
                    filetype = filetype || FileManager.fileInfo.filetype || "file"; //default is attachment download

                    //Infer action juding on the file type
                    if ("esp" === FileManager.fileInfo.filetype)
                        FileManager.fileInfo.isDocumentUpload = true;

                    else if ("photo" === FileManager.fileInfo.filetype)
                        FileManager.fileInfo.isPhoto = true;

                    var uploader = this.getDropzone().find(".upload-area");

                    var data = FileManager.getUploadData();

                    if (data) {

                        var file = data && data.files && data.files.length > 0 ? data.files[0] : null;

                        var fileCheck = FileManager.checkFile(file, $(this.defaultMsgContainer), filetype);

                        if (fileCheck !== false) {
                            //modalSaveBtn    				uploadBtn
                            FileManager.beforeSending($(""), uploader, uploader.find(".upload.button"));

                            FileManager.setDataURL(data);

                            jqXHR = null;

                            jqXHR = data.submit();
                        }
                    }

                    this.closeModal(filetype);

                    if (typeof jqXHR !== "undefined" && jqXHR !== null && jqXHR.state() === "pending")
                        this.showUploadArea();
                    else
                        this.finalize();
                }

                /** handler for when user clicks cancel,
                 *  when asking whether to add photo as attachment or import , it should add it as attachment
                 *  elsewise , it should just close the modal
                 * */
                , cancelConfirm: function (event) {

                    //console.log("DragDropHandler cancelConfirm, resets FileInfo"); //TODO remove this

                    if (FileManager.getNoFiles() > 0) { //TODO (properly) Fix the bug where after changing from ESP to CV there are multiple executions of button click callbacks
                        //console.log("cancelConfirm exits (noFiles>0)");
                        return;
                    }

                    var filetype = FileManager.fileInfo.filetype;

                    FileManager.resetFileInfo();

                    this.closeModal(filetype);

                    //This means the cancel button has been pressed, we interpret that the user wants to upload the file as an attachment instead of document/photo import 
                    if (["esp", "photo"].indexOf(filetype) > 0) {
                        FileManager.fileInfo.isPhoto = false;
                        FileManager.fileInfo.filetype = "file";

                        this.proceedUploading(null, filetype); //continue with upload
                    } else
                        this.finalize();
                }

                /** closes a specific confirmation modal
                 * @param filetype for the modal id 
                 * @result modal closed
                 * */
                , closeModal: function (filetype) {
                    var modal = $(Utils.jId("SkillsPassport." + filetype + ".Confirmation.Modal"));

                    modal.dialog("close");
                }

                /* after each interaction the wysiwyg should be left as it was, */
                , finalize: function () {
                    //console.log("DragDropHandler finalize"); //TODO remove this
                    var self = this;
                    var timeout = 100; //timeout in Millis
                    var uploadMsgArea = this.getDropzone().find(this.dZMsgContainer);
                    var pendingFiles = FileManager.getNoFiles();

                    if (pendingFiles === 0 && $(uploadMsgArea).find("section.notification:not(.success)").length > 0) { //if there are messages, give the user a 2 second break 
                        timeout = 2000;
                    }

                    setTimeout(function () {
                        uploadMsgArea.trigger("europass:message:clear");//Remove any previous error messages
                        self.hideDropzone($(self.defaultAreaSelector));
                        //console.log("dragdropview msg clear"); //TODO remove this
                    }, timeout);
                }
            });

            return ComposeDragDropView;
        }
);