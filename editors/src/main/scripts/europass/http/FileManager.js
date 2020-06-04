define(
        [
            'jquery',
            'underscore',
            'jqueryui',
            'fileupload',
            'HttpUtils',
            'europass/http/UploadUtils',
            'Utils',
            'europass/http/WindowConfigInstance',
            'europass/http/Verb',
            'europass/http/Resource',
            'europass/http/ServicesUri',
            'europass/http/SessionManagerInstance',
            'i18n!localization/nls/Notification',
            'hbs!templates/attachment/drophere',
            'hbs!templates/attachment/upload-progress-bar'
        ],
        function ($, _, jqueryui, fileupload, HttpUtils, UploadUtils, Utils, Config, HttpVerb, Resource, ServicesUri, SessionManager, LabelsValidation, DropHereTpl, UploadingTpl) {
            var FileManager = {};

            var defaultSuccessMessage = LabelsValidation["success.code.upload.default"];
            if (defaultSuccessMessage === undefined || defaultSuccessMessage === null || defaultSuccessMessage === "") {
                defaultSuccessMessage = "The file has been successfully uploaded.";
            }
            var defaultFailMessage = LabelsValidation["error.code.upload.default"];
            if (defaultFailMessage === undefined || defaultFailMessage === null || defaultFailMessage === "") {
                defaultFailMessage = "There was a problem uploading the file.";
            }

            FileManager.getFileUploadUrl = function (isPhoto, isSignature) {
                //pass user-cookie id
                var cookieId = '';
                if (Utils.readCookie()) { //user-cookie exists
                    cookieId = Utils.readCookie();
                }
                var url = ServicesUri.file_post;
                if (isPhoto === true) {
                    url = ServicesUri.photo_post;
                } else if (isSignature === true) {
                    url = ServicesUri.signature_post;
                }
                return  url + "?id=" + cookieId;
            };

            /**
             * 
             * @returns url
             */
            FileManager.getUploadUrl = function ( ) {
                //pass user-cookie id
                var cookieId = '';
                if (Utils.readCookie()) { //user-cookie exists
                    cookieId = Utils.readCookie();
                }
                var url = ServicesUri.document_upload;
                if (FileManager.fileInfo.isEuroDocCheck === true)
                    url = ServicesUri.is_file_Europass;

                var permissionToKeepNotImportedCv = $('body').find(":input[type=\"checkbox\"]#keep-not-imported-cv-permission:checked").length > 0;
                return  url + "?id=" + cookieId + "&keepCv=" + permissionToKeepNotImportedCv;
            };

            /** This Object acts as a status sharing scheme between the ComposeDragDropView and this moudle
             *  
             * */
            FileManager.fileInfo = {
                isPhoto: null,
                isSignature: null,
                isDocumentUpload: null,
                isEuroDocCheck: null,
                filetype: null
            };

            /** resets all FileManager.fileInfo the values to null 
             */
            FileManager.resetFileInfo = function () {
                for (key in FileManager.fileInfo)
                    FileManager.fileInfo[key] = null;
            };

            FileManager.getUploadData = function () {
                return FileManager.uploadData;
            };

            FileManager.setUploadData = function (data) {
                FileManager.uploadData = $.extend(true, {}, data);
            };

            /**
             * config:
             * {
             *   onDone: function to run after successfull upload,
             *   onFail: function to run after failed upload,
             *   isPhoto: boolean,
             *   isSignature: boolean,   
             *   messageContainer: the message container or the file upload area itself.
             * }
             */
            FileManager.enableFileUpload = function (wrapper, config, selector) {

                if (selector === undefined || selector === null || selector === "") {
                    selector = "#fileupload";
                }

                var callbacks = FileManager.prepareCallback(config, wrapper.find(selector));
                var obj = {};

                wrapper.find(selector).each(function (idx, el) {

                    var uploader = $(el);

                    var uploadBtn = FileManager.getUploadBtn(uploader, config.indexUploadBtn);

                    var modalSaveBtn = uploader.closest(".drawer").siblings(".side").find("button.save");

                    var parentView = config && config.relatedController ? config.relatedController : null;

                    var isGlobalDragDrop = parentView && $(parentView.el).length > 0 ? $(parentView.el).is("body") : false;

                    //Message Container
                    var messageContainer = (config.messageContainer === undefined) ? uploader : config.messageContainer;

                    var defaultMsgContainer = messageContainer;

                    //Photo upload - useful for tailoring messages
                    var isPhoto = (config.isPhoto === undefined) ? false : config.isPhoto;
                    //Signature upload - useful for tailoring messages
                    var isSignature = (config.isSignature === undefined) ? false : config.isSignature;

                    //DropZone
                    var dropZone;//( config.dropZone === undefined ) ? uploader : config.dropZone;
                    //TODO get dropzoneContainer from config , else uploder
                    var dz = uploader.closest(".modal").find(".dropzone");

                    if (dz.length > 0)
                        dropZone = dz;
                    else if ($(config.dropZone).length > 0)
                        dropZone = config.dropZone;
                    else
                        dropZone = $("<div>", {class: "dropzone"}).prependTo(uploader.closest(".modal"));

                    //TODO get dropzoneContainer from config , else uploder
                    //CSS Transitions for Dropzone
                    FileManager.prepareDropZone(dropZone);

                    var isDocumentUpload = (config.uploadDocument === true) ? true : false;

                    var filetype = "file";
                    if (isPhoto) {
                        filetype = "photo";
                    }
                    if (isSignature) {
                        filetype = "signature";
                    }
                    if (isDocumentUpload) {
                        filetype = "esp";
                    }

                    var updateFileType = function () {
                        filetype = FileManager.fileInfo.filetype || filetype;
                    };

                    obj = {
                        "isGlobalDragDrop": isGlobalDragDrop,
                        "messageContainer": messageContainer,
                        "defaultMsgContainer": defaultMsgContainer,
                        "callbacks": callbacks,
                        "modalSaveBtn": modalSaveBtn,
                        "uploader": uploader,
                        "uploadBtn": uploadBtn,
                        "filetype": filetype
                    };

                    uploader.fileupload({

                        type: HttpVerb._post
                                //The minimum time interval in milliseconds to calculate and trigger progress events.
                        , progressInterval: 10
                                //The minimum time interval in milliseconds to calculate progress bitrate.
                        , bitrateInterval: 500
                        , sequentialUploads: true
                                //USE FOR TESTING IFRAME TRANSPORT WITH BROWSERS, OTHER THAN IE...
                                //,forceIframeTransport: true
                        , dropZone: dropZone
                        , pasteZone: null

                                // add: is invoked as soon as files are added to the fileupload widget - via file input selection, drag & drop or add API call
                        , add: function (e, data) {
                            FileManager.fileuploadAdd(data, isGlobalDragDrop, messageContainer, defaultMsgContainer, callbacks, modalSaveBtn, uploader, filetype);
                        }
                        // Callback for the start of each file upload request.
                        , send: function (e, data) {
                            FileManager.fileuploadSend(data, isDocumentUpload, isPhoto, isSignature);
                        }
                        // Callback for successful upload requests. This callback is the equivalent to the success callback provided by jQuery ajax() and will also be called if the server returns a JSON response with an error property
                        , done: function (e, data) {
                            FileManager.fileuploadDone(data, isGlobalDragDrop, filetype, messageContainer, uploader, callbacks);
                        }
                        // Callback for failed (abort or error) upload requests. This callback is the equivalent to the error callback provided by jQuery ajax() and will not be called if the server returns a JSON response with an error property
                        , fail: function (e, data) {
                            FileManager.fileuploadFail(data, isGlobalDragDrop, messageContainer, filetype, callbacks, uploader);
                        }
                        //submit: Callback for the submit event of each file upload. If this callback returns false, the file upload request is not started.
                        //always: Callback for completed (success, abort or error) upload requests. This callback is the equivalent to the complete callback provided by jQuery ajax().
                        //progress: Callback for upload progress events.
                        //progressall: Callback for global upload progress events.
                        //progressall is called for overall progress of all running file uploads. If you want the same data as add/done, use the progress callback instead.
                        , progressall: function (e, data) {
                            FileManager.fileuploadProgressAll(data, uploader, modalSaveBtn, uploadBtn);
                        }
                        //start: Callback for uploads start, equivalent to the global ajaxStart event (but for file upload requests only).
                        //stop: Callback for uploads stop, equivalent to the global ajaxStop event (but for file upload requests only).
                        //change: Callback for change events of the fileInput collection.
                        //paste: Callback for paste events to the dropZone collection.
                        //drop: Callback for drop events of the dropZone collection.
                        //dragover: Callback for dragover events of the dropZone collection.
                    });
                });

                return obj;
            };

            FileManager.fileuploadAdd = function (data, isGlobalDragDrop, messageContainer, defaultMsgContainer,
                    callbacks, modalSaveBtn, uploader, filetype) {
                //console.log("file manager upload add");
                if (isGlobalDragDrop) {
                    FileManager.setUploadData(data);
                    //if already uploading in wysiwyg drag and drop, leave  //TODO careful of this as it might have insufficient checking and might disable adding multiple files

                    //!!FileManager.getNoFiles() === false, means that I dont want pending files to be 0 or undefined, e.g. this to stop the first upload
                    if (!!FileManager.getNoFiles() === false && FileManager.uploadStatus() === "pending") {
                        ////console.log("FILEMAN returning false from on add check");
                        return false;
                    }
                }

                var fileCount = data && data.originalFiles && data.originalFiles.length ? data.originalFiles.length : 0;

                //Remove any previous error messages
                messageContainer.trigger("europass:message:clear");

                var isApiAvailable = HttpUtils.checkApiAvailability();
                if (!isApiAvailable) {
                    FileManager.displayError(messageContainer, 500);
                    return false;
                }

                $(data.files).each(function (i, file) {

                    var fileName = file.name;
                    var fileExt = !$.isEmptyObject(fileName) && file.name.lastIndexOf(".") > 0 ? fileName.substring(file.name.lastIndexOf(".")) : "";

                    //Callback before uploading file, used(mainly) when dragdrop from wysiwyg / CV
                    var bfAdd = callbacks.beforeAdd;
                    var bfAddResult = {};

                    //before adding file callback todo add file count, if one file add the file
                    if (!$.isEmptyObject(bfAdd) && $.isFunction(bfAdd.f))
                        bfAddResult = callbacks.beforeAdd.f.apply(callbacks.beforeAdd.scope, [fileCount, fileExt]);

                    if (bfAddResult === false) {
                        return false;
                    } else if (typeof bfAddResult === "object" && !$.isEmptyObject(bfAddResult)) {
                        if (!$.isEmptyObject(bfAddResult.messageContainer) && $(bfAddResult.messageContainer).length > 0)
                            messageContainer = bfAddResult.messageContainer || messageContainer; //TODO what happens when it is off?
                    } else
                        messageContainer = defaultMsgContainer;

                    if (isGlobalDragDrop)  //if called from WYSIWYG drag n drop, check for filetype changes on each upload
                        //updateFileType();
                        filetype = FileManager.fileInfo.filetype || filetype;

                    //Check file name extension and size here
                    if (FileManager.checkFile(file, messageContainer, filetype) === false)
                        return false;

                    var uploadBtn = FileManager.getUploadBtn(uploader, FileManager.getUploadBtnIndex());

                    FileManager.beforeSending(modalSaveBtn, uploader, uploadBtn);

                    jqXHR = data.submit();

                    FileManager.setJqXHR(FileManager.getNoFiles(), jqXHR);
                });

                var onAdd = callbacks.onAdd;	//Add callback
                if ($.isFunction(onAdd.f)) {
                    onAdd.f.apply(onAdd.scope);
                }
            };

            FileManager.fileuploadSend = function (data, isDocumentUpload, isPhoto, isSignature) {
                //console.log("file manager upload send ");
                FileManager.setDataURL(data, isDocumentUpload, isPhoto, isSignature);
            };

            FileManager.fileuploadDone = function (data, isGlobalDragDrop, filetype, messageContainer, uploader, callbacks) {
                //console.log("file manager upload done");

                //Consume this file..
                FileManager.decreaseNoFiles();

                if (isGlobalDragDrop)  //if called from WYSIWYG drag n drop, check for filetype changes on each upload
                    filetype = FileManager.fileInfo.filetype || filetype;

                var json = FileManager.uploadSuccess(data, messageContainer, filetype); //this also handles errors for the case of IE

                if (json === false) { // it might return error even on done (see ie...)
                    FileManager.completeProgressBar(data, uploader);
                } else {
                    //Keep record of the successfully uploaded files
                    FileManager.updateSuccessUploads(json);

                    //For each file
                    var onDone = callbacks.onDone;
                    //If there is an onDone message specified, display it
                    if (onDone.showMessage === true) {
                        var msg = onDone.message;

                        if (onDone.appendFileName) {
                            var filenames = "";
                            $(data.files).each(function (idx, data) {
                                filenames += "<span class=\"file-name\">" + data.name + "</span>";
                            });
                            msg = "<div class=\"file-names\">" + filenames + "</div>" + msg;
                        }

                        messageContainer.trigger("europass:message:show", ["success", msg, onDone.blinkMessage]);
                    }

                    if ($.isFunction(onDone.f)) {
                        onDone.f.apply(onDone.scope, [json]);
                    }

                    //Only when there are no pending files to manage
                    var pendingFiles = FileManager.getNoFiles();
                    if (pendingFiles === undefined || pendingFiles === 0) {

                        FileManager.completeProgressBar(data, uploader);
                        FileManager.clearSuccessUploads();//vpol added to fix EWA-1431 yet, probably not the optimal solution
                        //[EWA REVAMP] Photo drawer can't be closed after import
                        //after importing cv with photo, open photo, cancel not working
                        //can not exit the FileManager.cancelSuccessUploads because successUploadsCancelled and successUploads are not null/undefined
                    }
                }
            };

            FileManager.fileuploadFail = function (data, isGlobalDragDrop, messageContainer, filetype, callbacks, uploader) {

                //console.log("file manager upload fail");

                if (isGlobalDragDrop)  //if called from WYSIWYG drag n drop, check for filetype changes on each upload
                    filetype = FileManager.fileInfo.filetype || filetype;

                //Consume this file..
                FileManager.decreaseNoFiles();

                FileManager.uploadFail(data, messageContainer, filetype);
                //Callback specified by config
                var onFail = callbacks.onFail;

                //If there is an onDone message specified, display it
                if (onFail.showMessage === true) {
                    messageContainer.trigger("europass:message:show", ["error", onFail.message, false, false, "error.code.status500"]);
                }

                if ($.isFunction(onFail.f)) {
                    onFail.f.apply(onFail.scope, [data]);
                }

                //Only when there aare no pending files to manage
                var pendingFiles = FileManager.getNoFiles();
                if (pendingFiles === undefined || pendingFiles === 0) {

                    FileManager.completeProgressBar(data, uploader);
                }
            };

            FileManager.fileuploadProgressAll = function (data, uploader, modalSaveBtn, uploadBtn) {

                var bar = uploader.find(".progress-bar");

                var progress = parseInt(data.loaded / data.total * 100, 10);

                bar.attr("aria-valuenow", progress);
                bar.animate({width: progress + "%"}, 10); /*(1/100 of a second)*/

                //when done sending, check after 2 seconds if the upload is completed (server has responded etc..)
                if (progress == 100 && $(modalSaveBtn).length > 0) {
                    setTimeout(function () {

                        var checkIfUploaded = setInterval(function () {
                            //console.log("check if uploaded");
                            if (!$(uploadBtn).hasClass("uploading")) {

                                //console.log("enable save");
                                clearInterval(checkIfUploaded);
                                modalSaveBtn.removeClass("disabled");
                            }//else, check every 100ms if the uploading status is removed
                        }, 100);
                        //console.log("remove dissabled");
                    }, 2000);
                }
            };

            FileManager.setDataURL = function (data, isDocumentUpload, isPhoto, isSignature) {
                var isDoc = FileManager.fileInfo.isDocumentUpload === true || isDocumentUpload;
                var isPic = FileManager.fileInfo.isPhoto || isPhoto;
                var isSig = FileManager.fileInfo.isSignature || isSignature;

                //Performed here to make sure that we fetch the jsessionid
                data.url = (isDoc) ? FileManager.getUploadUrl() : FileManager.getFileUploadUrl(isPic, isSig);
            };

            /*checks if file has valid extension/size, given the filetype and 
             * container to display an error message when invalid
             * @param file , the jq fileupload data file
             * @param filetype : "esp","photo","file","signature"
             * @param messageContainer the element to show the error notification to
             * @returns false for invalid files
             */
            FileManager.checkFile = function (file, messageContainer, filetype) {

                // File name
                var fileName = (file.name != undefined && file.name != undefined) ? file.name : undefined;

                if (fileName !== undefined && fileName !== null && fileName !== "" && fileName.indexOf(".") > 0) {
                    var ext = fileName.substr(fileName.lastIndexOf("."));
                    if (ext !== undefined && ext !== null && ext !== "" && Config.isAllowedFileExtension(filetype, ext) === false) {
                        //show error after a while
                        setTimeout(function () {
                            messageContainer.trigger("europass:message:fileUploadError", true);
                            messageContainer.trigger("europass:message:show",
                                    ["error", FileManager.invalidMimeNotification(filetype), false, false, "error.code." + filetype + ".invalidmimetype"]);
                        }, 1000);
                        //abort!
                        return false;
                    }
                }

                //Check Size

                var fileSize = (file.size != undefined && file.size != undefined) ? file.size : undefined;

                if (fileSize !== undefined && fileSize !== null) {
                    var sizeInt = parseInt(fileSize);
                    if (sizeInt !== NaN) {
                        if (sizeInt == 0) {
                            //show error after a while
                            setTimeout(function () {
                                messageContainer.trigger("europass:message:fileUploadError", true);
                                messageContainer.trigger("europass:message:show",
                                        ["error", FileManager.zerosizeNotification(filetype), false, false, "error.code." + filetype + ".empty"]);
                            }, 1000);
                            //abort!
                            return false;
                        }
                        //Check max size only for photo and attachments
                        else if (filetype !== "esp" && sizeInt > HttpUtils.unFormatBytes(Config.allowedMaxSize(filetype))) {
                            //show error after a while
                            setTimeout(function () {
                                messageContainer.trigger("europass:message:fileUploadError", true);
                                messageContainer.trigger("europass:message:show",
                                        ["error", FileManager.bigsizeNotification(filetype), false, false, "error.code." + filetype + ".filetoobig"]);
                            }, 1000);
                            //abort!
                            return false;
                        }
                    }
                }
                messageContainer.trigger("europass:message:fileUploadError", false);
            };

            FileManager.beforeSending = function (modalSaveBtn, uploader, uploadBtn) {

                //when file size is OK...
                FileManager.increaseNoFiles();

                //disabling save button during file upload
                if (!modalSaveBtn.hasClass("disabled"))
                    modalSaveBtn.addClass("disabled");

                var bar = uploader.find(".progress-bar");
                if (bar.length === 0) {
                    //console.log("add a progress bar");
                    $(UploadingTpl()).appendTo(uploadBtn);
                    uploadBtn.addClass("uploading");
                }
            };

            //current Upload Status getter
            FileManager.uploadStatus = function () {
                var uploadState = typeof jqXHR !== "undefined" && jqXHR !== null && $.isFunction(jqXHR.state) ? jqXHR.state() : null;
                return uploadState;
            };

            /*** RECORD OF SUCCESSFUL UPLOADS ******/
            FileManager.updateSuccessUploads = function (json) {
                if (FileManager.successUploads === undefined)
                    FileManager.successUploads = [];
                FileManager.successUploads.push(json);
            };

            FileManager.clearSuccessUploads = function () {
                delete FileManager.successUploads;
            };

            FileManager.cancelSuccessUploads = function (callback, scope, args) {

                if (FileManager.successUploads === undefined ||
                        FileManager.successUploads === null ||
                        $.isArray(FileManager.successUploads) === false) {
                    if ($.isFunction(callback) === true) {
                        callback.apply(scope, args);
                    } else
                        return false;
                }

                var size = (FileManager.successUploads === undefined) ? 0 : FileManager.successUploads.length;

                FileManager.successUploadsCancelled = size;

                if (size === 0) {
                    if ($.isFunction(callback) === true) {
                        callback.apply(scope, args);
                    } else
                        return false;
                }

                for (var i = 0; i < size; i++) {
                    var json = FileManager.successUploads[i];
                    FileManager.removeFile(json);
                }

                //Wait until all uploads have been processed...
                var timer = window.setInterval(function () {
                    //console.log("are we there yet?");
                    if (FileManager.successUploadsCancelled === undefined ||
                            (FileManager.successUploadsCancelled !== undefined
                                    && FileManager.successUploadsCancelled === 0)) {
                        //console.log("we are indeed there!");
                        window.clearInterval(timer);
                        delete FileManager.successUploadsCancelled;

                        if ($.isFunction(callback) === true) {
                            callback.apply(scope, args);
                        }
                    }
                }, 500);
            };

            /******** CANCEL MODAL FORM **********/
            FileManager.removeFile = function (json) {
                console.log('inside removeFile');
                if (json == null || json.FileData == null || json.FileData.TempURI == null) {
                    return false;
                }
                var resource = new Resource(json.FileData.TempURI + SessionManager.urlappend());
                resource._delete({
                    success: {
                        scope: this,
                        callback: function () {
                            if (FileManager.successUploadsCancelled !== undefined) {
                                FileManager.successUploadsCancelled = FileManager.successUploadsCancelled - 1;
                            }
                            //console.log("Delete Attachment successful");
                        }
                    },
                    error: {
                        scope: this,
                        callback: function (statusObj) {
                            if (FileManager.successUploadsCancelled !== undefined) {
                                FileManager.successUploadsCancelled = FileManager.successUploadsCancelled - 1;
                            }
                            //console.log("Delete Attachment failed");
                        }
                    }
                });
            };

            /************** CANCEL UPLOAD **********/
            FileManager.cancelFileUpload = function (event) {
                //console.log("about to cancel upload !");
                var btn = $(event.target);

                var index = btn.attr("data-rel-file-index");

                if (index === undefined || index === null || index === "") {
                    return false;
                }
                var jqXHR = FileManager.getJqXHR(index);

                if (jqXHR !== undefined && jqXHR !== null) {
                    jqXHR.abort();
                }
            };

            /************** CLEANUP ACTIONS OF THE UPLOADED AREA **********/
            FileManager.cleanFeedback = function (feedbackContainer) {
                if (feedbackContainer !== null && feedbackContainer.length > 0) {
                    feedbackContainer.html("");
                }
            };

            FileManager.cleanupUploaded = function (container) {

                FileManager.clearNoFiles();

            };

            /************* PROGRESS BAR SPECIFICS ********************/
            FileManager.completeProgressBar = function (data, uploader) {

                //console.log("complete progress bar");

                var bar = uploader.find(".progress-bar");
                var button = bar.parent();

                bar.removeClass("error")
                        .addClass("completed")
                        .attr("aria-valuenow", 100);
                bar.animate({width: "100%"}, 100); /*(1/10 of a second)*/
                bar.siblings(".uploading").addClass("completed");

                //Wait 2 secs and then remove
                setTimeout(function () {
                    button.removeClass("uploading");
                    bar.remove();
                    uploader.find(".text-uploading").remove();
                }, 2000);

                // When completing progress bar reset upload button index ! Cloud services are overriding that during
                // fileManagerInstance = FileManager.enableFileUpload(this.$el ...)
                FileManager.setUploadBtnIndex(0);
            };

            /************* jqXHR per upload *************/
            FileManager.clearJqXHR = function () {
                delete FileManager.jqXHR;
            };

            FileManager.setJqXHR = function (index, jqXHR) {
                if (FileManager.jqXHR === undefined)
                    FileManager.jqXHR = {};
                FileManager.jqXHR[ index ] = jqXHR;
            };

            FileManager.getJqXHR = function (index) {
                if (FileManager.jqXHR === undefined)
                    return null;

                return FileManager.jqXHR[ index ];
            };

            /************* NUMBER OF FILES FOR SIMULTANEOUS UPLOAD *************/
            FileManager.clearNoFiles = function () {
                delete FileManager.noFiles;
            };

            FileManager.setNoFiles = function (size) {
                FileManager.noFiles = size;
            };

            FileManager.getNoFiles = function () {
                var size = FileManager.noFiles;
                return size;
            };

            FileManager.increaseNoFiles = function () {

                if (FileManager.getNoFiles() === undefined) {
                    FileManager.setNoFiles(0);
                }
                var prevV = FileManager.getNoFiles();
                var newV = prevV + 1;
                FileManager.setNoFiles(newV);
                //console.log("INCREASE no files to :"+FileManager.getNoFiles());
            };

            FileManager.decreaseNoFiles = function () {
                var prevV = FileManager.getNoFiles();
                if (prevV > 0) {
                    var newV = prevV - 1;
                    FileManager.setNoFiles(newV);
                }
            };

            FileManager.setUploadBtnIndex = function (uploadBtnIndex) {
                FileManager.uploadBtnIndex = uploadBtnIndex;
            };

            FileManager.getUploadBtnIndex = function () {
                return FileManager.uploadBtnIndex;
            };

            FileManager.getUploadBtn = function (uploader, index) {
                var indexUplBtn = parseInt(index, 10);
                FileManager.setUploadBtnIndex(indexUplBtn);
                var uploadBtn = uploader.find("span.button.upload").eq(indexUplBtn);

                return uploadBtn;
            };


            /********************** MESSAGES ********************/
            /**
             * Handle File Upload Failure
             * 
             * First check the response status in order to prepare a suitable error message.
             * Specifically if the status is 500, we try to read the response and investigate further the problem.
             * 
             * @param data, data object as set from the jquery file upload plugin (contains response and file info)
             * @param messageContainer, the parent object whose html to update
             * @param filetype, text ["photo", "file", "signature", "esp"] used to get an appropriate feedback text.
             * 
             * @return nothing (just an update to the parent html) or false
             * 
             */
            FileManager.uploadFail = function (data, messageContainer, filetype) {

                UploadUtils.uploadFail(data, messageContainer, filetype, this.fileUploadAbortMessage, this.handleFailureResponse);

            };

            /**
             * Displays a suitable messge when an upload is aborted.
             * @param messageContainer
             * @param file
             * @param filetype
             * @returns {String}
             */
            FileManager.fileUploadAbortMessage = function (messageContainer, file, filetype) {

                var msg = "";
                var msgCode = "error.code.upload.aborted";

                msg = LabelsValidation[ msgCode ];

                messageContainer.trigger("europass:message:show", ["error", msg, false, false, msgCode]);

                return false;
            };

            /**
             * Displays a suitable error depending on the Error JSON parsed from the HTML response.
             * 
             * Delegates to fileUploadErrorMessage which displays a proper message depending on the JSON Error.code
             * 
             * @param messageContainer, the parent object whose html to update
             * @param html, the html response from which to read various elements
             * @param filedata, the file which was uploaded
             * @param filetype, text ["photo", "file", "esp"] used to get an appropriate feedback text.
             * 
             * @return false, but also updates the parent html.
             */
            FileManager.handleFailureResponse = function (messageContainer, html, filedata, filetype) {
                //EVEN IN CASE OF ERROR IFRAME UPLOAD WILL ARRIVE HERE...
                //SO WE RELY ON THE HTML RESPONSE TO FIGURE IT OUT.
                // Handle error case
                var responseStr = html.find("script[type=\"application/json\"]").html();
                if (responseStr === undefined || responseStr === null || responseStr === "") {
                    FileManager.displayError(messageContainer);
                    return false;
                }
                var response = $.parseJSON(responseStr);
                if (!$.isPlainObject(response) || $.isEmptyObject(response)) {
                    FileManager.displayError(messageContainer);
                    return false;
                }
                var errorMsg = FileManager.fileUploadErrorMessage(response, filedata, filetype);

                messageContainer.trigger("europass:message:show", ["error", errorMsg, false, false]); //TODO here add the error code for unique error message display

                return false;
            };

            /**
             * Successful upload.
             * The response is text/html. The HTML response contains:
             *  - a status code in a meta element named 'status' / in case this is error, it delegated to "handleFailureResponse".
             *  - the session id in a meta element named 'jsessionid'
             *  - the JSON response object (either FileData or Error) inside a script element.
             * 
             * @param data, data object as set from the jquery file upload plugin (contains response and file info)
             * @param messageContainer, the parent object whose html to update
             * @param filetype, text ["photo", "file", "esp"] used to get an appropriate feedback text.
             * 
             * @return the filedata JSON or false, as well as updates accordingly the parent html with error messages.
             */
            FileManager.uploadSuccess = function (data, messageContainer, filetype) {

                return UploadUtils.uploadSuccess(data, messageContainer, filetype, this.handleFailureResponse);

            };

            /**
             * Display an error
             * @param htmlwrapper, the parent object whose html to update
             * @param status, nothing or the status code
             */
            FileManager.displayError = function (messageContainer, status) {
                HttpUtils.statusResponse((status !== undefined) ? status : 505, messageContainer);
            };

            /**
             * Returns an html element containing the error text according to the error code, as this is defined by the FileResource
             * @param response, a JSON object containing an Error.code and Error.message
             * @param file, a JSON object containing information about the uploaded File
             * @param filetype, text ["photo", "file", "esp"] used to get an appropriate feedback text.
             */
            FileManager.fileUploadErrorMessage = function (response, file, filetype) {

                if (response === undefined || response === null) {
                    return null;
                }

                var error = response.Error;
                if (error === undefined || error === null) {
                    return null;
                }

                var errorCode = error.code;

                var msg = "";

                var knownIssue = false;
                if (errorCode === "file.too.big") {
                    //File Too Big
                    var notification = FileManager.bigsizeNotification(filetype);
                    msg += notification;
                    knownIssue = true;
                } else if (errorCode === "file.exceeded.cumm.size.limit") {
                    var notification = Utils.replaceKey(
                            LabelsValidation["error.code.cumulative.size.limit.exceeded"], //"+filetype+". 
                            "[[file.max.cumulative.size]]",
                            Config.allowedMaxSize("cumulative"));
                    msg += notification;
                    knownIssue = true;
                } else if (errorCode === "file.undefined.mime") {
                    //Undefined Mime Type
                    var notification = FileManager.undefinedMimeNotification(filetype);
                    msg += notification;
                    knownIssue = true;
                } else if (errorCode === "file.invalid.mime") {
                    //Invalid Mime Type
                    var notification = FileManager.invalidMimeNotification(filetype);
                    msg += notification;
                    knownIssue = true;
                } else if (errorCode === "content.type.not.allowed") {
                    //Unallowed Mime Type
                    var errorMsg = error.message;
                    var notification = FileManager.unallowedMimeNotification(filetype, file.name, errorMsg);
                    msg += notification;
                } else {
                    var messageInfo = HttpUtils.msgFromErrCodeFailedUpload(errorCode);
                    if ((messageInfo.message !== undefined || messageInfo.message !== null) && "" !== messageInfo.message)
                        msg += messageInfo.message;
//			if( messageInfo.known !== undefined && messageInfo.known !== null )
//				knownIssue = messageInfo.known;
                }

                if (error.trace && knownIssue === false) {
                    msg += "<em class=\"trace-code\">" + error.trace + "</em>";
                }

                /*		if ( file !== undefined && file !== null ) {
                 msg += "<div class=\"file-names\"><span class=\"file-name\">" + file.name + " (" + HttpUtils.formatBytes(file.size) + ")</span></div>";
                 //			msg = ( "<em class=\""+file.type+"\">" + file.name + " (" + HttpUtils.formatBytes(file.size) + ")</em>" );
                 }
                 */
                return msg;
            };

            FileManager.undefinedMimeNotification = function (filetype) {
                return Utils.replaceKey(
                        LabelsValidation["error.code." + filetype + ".undefinedmimetype"],
                        "[[" + filetype + ".valid.types]]",
                        Config.allowedFileType(filetype));
            };

            FileManager.invalidMimeNotification = function (filetype) {
                return Utils.replaceKey(
                        LabelsValidation["error.code." + filetype + ".invalidmimetype"],
                        "[[" + filetype + ".valid.types]]",
                        Config.allowedFileType(filetype));
            };

            FileManager.bigsizeNotification = function (filetype) {
                return Utils.replaceKey(
                        LabelsValidation["error.code." + filetype + ".filetoobig"],
                        "[[" + filetype + ".max.size]]",
                        Config.allowedMaxSize(filetype));
            };

            FileManager.zerosizeNotification = function (filetype) {
                return Utils.replaceKey(
                        LabelsValidation["error.code." + filetype + ".empty"],
                        "[[" + filetype + ".max.size]]",
                        Config.allowedMaxSize(filetype));
            };

            FileManager.unallowedMimeNotification = function (filetype, file, errorMsg) {
                //falsy check
                file = "string" === typeof file && file ? file : {};
                errorMsg = "string" === typeof errorMsg && errorMsg ? errorMsg : {};

                filetype = filetype === "esp" ? "file" : filetype;

                var fileExt = file.substring(file.lastIndexOf(".") + 1).toUpperCase();
                var txt = LabelsValidation[filetype + ".content.type.unallowed"] || "Invalid file format. Please upload a file in one of the accepted formats: [[filetypes]]";

                var matchesExtension = errorMsg.toLowerCase().match(/\/[a-z,-]*/); //Extract the detected MIME type from the error message
                var foundExt = "";
                
                if (matchesExtension !== null) {
                    foundExt = matchesExtension.toString();
                    if (foundExt.charAt(0) === "/") {
                       foundExt = foundExt.toString().substring(1);
                    }
                }

                return txt.replace("[[fileext]]", fileExt).replace("[[ext]]", foundExt).
                        replace("[[filetypes]]", Config.allowedFileType(filetype));
            };

            /************* DROPZONE INITIALISATION // BINDINGS // RENDERING 	***********/

            FileManager.prepareDropZone = function (dropZone) {
                //dropZone.addClass("dropzone");
                var html = FileManager.getDropIndication();

                if ($(dropZone).find(".drop.here").length == 0)
                    $(dropZone).html(html);

                FileManager.dropHereHover(dropZone);

                FileManager.dropTransition(dropZone);
            };

            /** 
             * dropTransition binds dragover event to the application window 
             **/
            FileManager.dropTransition = function (dropZone) {

                var $container = $(dropZone).parent();  //the modal

                $(document).bind('dragover', function (e) {
                    var timeout = window.dropZoneTimeout;

                    var dataTransfer = e.dataTransfer || e.originalEvent.dataTransfer;

                    var dragType = "";
                    var textTypes = ["text/plain", "Text", "text/_moz_htmlcontext"]; //possible drag types, depending on browser
                    var isTextDrag = null;

                    //Check if text is being dragged
                    if (typeof dataTransfer !== "undefined" && !_.isUndefined(dataTransfer.types) && typeof dataTransfer === "object" /*&& dataTransfer.types.hasOwnProperty("length") && dataTransfer.types.length > 0*/) {
                        dragType = dataTransfer.types;
                        //mozilla text drag 
                        if (dragType instanceof Array && textTypes.indexOf(dragType[0]) > -1 /*|| ( typeof dragType === "DOMStringList" && dragType.contains("text/_moz_htmlcontext") ) */)		//TODO careful of [0] element access
                            isTextDrag = true;
                    }

                    //drag should work only for files, not ,	when an upload is not pending  	and when the dropzone is not disabled
                    if (isTextDrag === true || FileManager.uploadStatus() === "pending" || dropZone.hasClass("disabled")) {
                        //console.log( /*isFileDrag?"isFileDrag":"" + */isTextDrag?"isTextDrag":"");
                        return;
                    }

                    if (!timeout) {
                        dropZone.addClass('in');
                    } else {
                        clearTimeout(timeout);
                    }

                    if ($(e.target).closest($container).length > 0 && !dropZone.hasClass('hover')) { //TODO ndim careful of overlay and e.target overlapping $(e.target).hasClass("dropzone") || $container.find( e.target ).length > 0
                        dropZone.addClass('hover');
                        // console.log("in, tar:" + $(e.target.attributes["class"]).val() + ", dz: "+ dropZone.attr("class"));

                        FileManager.dropZoneHover(dropZone);

                    } else if ($(e.target).closest($container).length === 0 && dropZone.hasClass('hover')) {
                        //console.log("out, tar:" + $(e.target.attributes["class"]).val() + ", dz: "+ dropZone.attr("class"));
                        dropZone.removeClass('hover');

                        FileManager.dropZoneOutHover($container);
                    } else {
                        //console.log("else, to:"+timeout);//sleep
                    }

                    //make sure that the dropzone is hidden after dropping file, by calling dropzoneOut after 100ms
                    window.dropZoneTimeout = setTimeout(function () {
                        //console.log("DZ timeout ,"+ dropZone.attr("class")); //TODO remove this!@@ 
                        window.dropZoneTimeout = null;
                        dropZone.removeClass('in hover');

                        FileManager.dropZoneOutHover($container);

                    }, 100);
                });
                /*$(document).bind('drop dragover', function (e) {
                 e.preventDefault();
                 }); //moved to globalcontroller */
            };

            FileManager.dropHereHover = function (dropZone) {
                dropZone.find(".drop").hover(function () {
                    //console.log("drop here hover");

                    $(this).siblings(":not(legend)").hide();

                });
            };

            FileManager.dropZoneHover = function (dropZone) {
                //console.log("dropzone hover");

                dropZone.show();

                dropZone.siblings("#notifications-area,.main-area,.modal form:not(legend)").hide();

                var dropHere = dropZone.find(".drop");

                dropHere.show();
                dropHere.find(".dropMsg").show();
                dropHere.find(".upload-area").hide();
            };

            FileManager.dropZoneOutHover = function ($container) {
                $container.children().show();

                $container.find(".dropzone").hide();

                $($container.find(".drop")[0]).hide();
            };

            /**
             * Return html for the dropzone.
             * This is added dynamically when the browser is NOT IE
             */
            FileManager.getDropIndication = function () {
                var html = DropHereTpl({});
                return html;
            };

            /**
             * Destroy the File Uploader
             */
            FileManager.disableFileUpload = function (wrapper, selector) {
                if (selector === undefined || selector === null || selector === "") {
                    selector = "#fileupload";
                }

                FileManager.clearSuccessUploads();

                wrapper.find(selector).each(function (idx, el) {
                    var uploader = $(el);
                    if (uploader.data('fileupload')) {
                        uploader.fileupload('destroy');
                    }
                });

                FileManager.clearDropzone(wrapper);
//			//Disable button cancel
//			$(document).off("click.ewa.upload.cancel",
//					".file-uploading .attachment.uploading button.cancel-upload",
//					$.proxy(FileManager.cancelFileUpload, FileManager));
            };

            //ndim manually remove the dropzone area, now that it is a child of the modal element, avoiding unwanted behavior
            //looks for a .dropzone inside the closest .modal ,sadly the classes are hardcoded
            FileManager.clearDropzone = function (el, container) {

                var wrapper = container;

                if ($.isEmptyObject(container))
                    wrapper = ".modal";

                var dropZone = $(el).closest(wrapper).find(".dropzone");

                //console.log("clear dz");
                if ($(el).length > 0) {
                    dropZone.html("");//remove();
                }
            };

            FileManager.removeDropzone = function (el, container) {

                var wrapper = container;

                if ($.isEmptyObject(container))
                    wrapper = ".modal";

                var dropZone = $(el).closest(wrapper).find(".dropzone");

                dropZone.remove();
            };

            /** CALLBACKS
             * Prepare on done and on fail callback functions and scopes
             */
            FileManager.prepareCallback = function (config, defaultScope) {
                var callbacks = {
                    beforeAdd: {},
                    onDone: {},
                    onFail: {},
                    onAdd: {}
                };
                /***** ON DONE *****/
                if ($.isFunction(config.onDone)) {
                    callbacks.onDone.f = config.onDone;
                    callbacks.onDone.scope = defaultScope;
                    callbacks.onDone.showMessage = false;
                    callbacks.onDone.message = defaultSuccessMessage;
                    callbacks.onDone.blinkMessage = true;
                } else if (!$.isEmptyObject(config.onDone)) {
                    callbacks.onDone = config.onDone || {};
                    callbacks.onDone.scope = config.onDone.scope || defaultScope || window;
                    callbacks.onDone.showMessage = (config.onDone.showMessage != null) ? config.onDone.showMessage : false;
                    callbacks.onDone.message = (config.onDone.message !== undefined && config.onDone.message !== null && config.onDone.message !== "") ? config.onDone.message : defaultSuccessMessage;
                    callbacks.onDone.blinkMessage = (config.onDone.blinkMessage === undefined || config.onDone.blinkMessage === null) ? true : config.onDone.blinkMessage;
                }
                /***** BEFORE ADD *****/
                if ($.isFunction(config.beforeAdd)) {
                    callbacks.beforeAdd.f = config.beforeAdd;
                    callbacks.beforeAdd.scope = defaultScope;
                } else if (!$.isEmptyObject(config.beforeAdd)) {
                    callbacks.beforeAdd = config.beforeAdd || {};
                    callbacks.beforeAdd.scope = config.beforeAdd.scope || defaultScope || window;
                }
                /***** ON ADD *****/
                if ($.isFunction(config.onAdd)) {
                    callbacks.onAdd.f = config.onAdd;
                    callbacks.onAdd.scope = defaultScope;
                } else if (!$.isEmptyObject(config.onAdd)) {
                    callbacks.onAdd = config.onAdd || {};
                    callbacks.onAdd.scope = config.onAdd.scope || defaultScope || window;
                }
                /***** ON FAIL *****/
                if ($.isFunction(config.onFail)) {
                    callbacks.onFail.f = config.onFail;
                    callbacks.onFail.scope = defaultScope;
                    callbacks.onFail.showMessage = false;
                    callbacks.onFail.message = defaultFailMessage;
                    callbacks.onFail.blinkMessage = true;
                } else if (!$.isEmptyObject(config.onFail)) {
                    callbacks.onFail = config.onFail || {};
                    callbacks.onFail.scope = config.onFail.scope || defaultScope || window;
                    callbacks.onFail.showMessage = (config.onFail.showMessage != null) ? config.onFail.showMessage : false;
                    callbacks.onFail.message = (config.onFail.message !== undefined && config.onFail.message !== null && config.onFail.message !== "") ? config.onFail.message : defaultFailMessage;

                    callbacks.onFail.blinkMessage = (config.onFail.blinkMessage === undefined || config.onFail.blinkMessage === null) ? true : config.onDone.blinkMessage;
                }
                return callbacks;
            };
            return FileManager;
        }
);
