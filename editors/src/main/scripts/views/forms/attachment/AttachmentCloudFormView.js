define(
        [
            'jquery',
            'underscore',
            'Utils',
            'views/forms/FormView',
            'i18n!localization/nls/Notification',
            'views/attachment/AttachmentManagerInstance',
            'europass/http/FileManager'
        ],
        function ($, _, Utils, FormView, Notification, AttachmentManager, FileManager) {

            var AttachmentCloudFormView = FormView.extend({

                currentView: null,
                currentViewPath: null,
                googleDriveCloudUploadViewPath: "views/forms/attachment/cloud/GoogleDriveUploadAttachmentsView",
                oneDriveCloudUploadViewPath: "views/forms/attachment/cloud/OneDriveUploadAttachmentsView",
                dropboxCloudUploadViewPath: "views/forms/attachment/cloud/DropboxUploadAttachmentsView",

                events: _.extend({

                    "click .button.upload.cloud-attach-action": "checkPopUp",

                    "europass:googledrive:poc:cloud:attachments": "downloadCloudAttachments"

                }, FormView.prototype.events),

                messageContainerSelector: ".upload.feedback-area",

                checkPopUp: function (event) {

                    var target = event.currentTarget;

                    var cloudProvider = $(target).data('attachCloud');
                    var isPhoto = $(target).closest('menu').data('photo');
                    var isSignature = $(target).closest('menu').data('signature');
                    this.indexUploadBtn = $(target).data('index');

                    var _that = this;
                    var timeOutMilis = 500;

                    setTimeout(function () {
                        setTimeout(function () {	//seems like FF needs another setTimeout in order to make tryOpeningPopUp() work

                            var allowed = Utils.tryOpeningPopUp();
                            if (allowed === false) {
                                _that.cleanupFeedback();
                                _that.getMessageContainer().trigger("europass:message:show", ["error", Notification["skillspassport.wizard.cloud.popup.blocked"]]);
                            } else {
                                _that.cloudConnectAction(cloudProvider, isPhoto, isSignature);
                            }
                        }, timeOutMilis);
                    }, timeOutMilis);
                },

                cloudConnectAction: function (cloudProvider, isPhoto, isSignature) {

                    var _that = this;
                    var viewPath;
                    switch (cloudProvider) {
                        case "googledrive":
                            viewPath = _that.googleDriveCloudUploadViewPath;
                            break;
                        case "onedrive":
                            viewPath = _that.oneDriveCloudUploadViewPath;
                            break;
                        case "dropbox":
                            viewPath = _that.dropboxCloudUploadViewPath;
                            break;
                        default:
                            break;
                    }

                    if (this.currentViewPath === null || this.currentViewPath !== viewPath) {

                        this.currentViewPath = viewPath;

                        require(
                                [viewPath, Notification],
                                //SUCCESS
                                        function (View) {
                                            if ($.isFunction(View)) {

                                                var view = new View({
                                                    el: _that.$el,
                                                    model: _that.model,
                                                    info: _that.model,
                                                    messageContainer: _that.getMessageContainer(),
                                                    parentView: _that
                                                });

                                                //Set to the new View!
                                                _that.currentView = view;
                                                _that.currentView.render();
                                                _that.currentView.doConnectLogin(isPhoto, isSignature);
                                            }
                                        },
                                        //Error
                                                function (args) {
                                                    _that.currentViewPath = null;
                                                    _that.getMessageContainer().trigger("europass:message:show", ["error", "Error during loading cloud attachment views"]);
                                                    $("body").trigger("europass:waiting:indicator:hide");
                                                }
                                        );

                                    } else {
                                this.currentView.render();
                                this.currentView.doConnectLogin(isPhoto, isSignature);

                                $("body").trigger("europass:waiting:indicator:hide");
                            }
                        }

                , initCloudFileUpload: function (file, isPhoto, isSignature, indexUploadBtn) {

                    var fileManagerInstance = FileManager.enableFileUpload(this.$el, {
                        selector: this.uploaderSelector,
                        isPhoto: isPhoto,
                        indexUploadBtn: indexUploadBtn,
                        messageContainer: this.getMessageContainer(),
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
                    this.fileManagerUploadAction(file, fileManagerInstance, isPhoto, isSignature);
                }

                , fileManagerUploadAction: function (file, fm, isPhoto, isSignature) {

                    var data = {
                        "files": [file],
                        "originalFiles": [file],
                        "submit": function () {
                            var formData = new FormData();
                            formData.append("file", file);
                            $.ajax({
                                type: "POST",
                                url: FileManager.getFileUploadUrl(isPhoto, isSignature),
                                contentType: false,
                                processData: false,
                                data: formData,
                                success: function (resp) {
                                    data.result = resp;
                                    FileManager.fileuploadDone(data, fm.isGlobalDragDrop, fm.filetype, fm.messageContainer,
                                            fm.uploader, fm.callbacks);

                                    data.loaded = 1;
                                    data.total = 1;
                                    FileManager.fileuploadProgressAll(data, fm.uploader, fm.modalSaveBtn, fm.uploadBtn);
                                },
                                error: function (resp) {
                                    FileManager.fileuploadFail(data, fm.isGlobalDragDrop,
                                            fm.messageContainer, fm.filetype, fm.callbacks, fm.uploader);

                                }
                            });
                        }
                    };
                    FileManager.fileuploadAdd(data, fm.isGlobalDragDrop, fm.messageContainer, fm.defaultMsgContainer,
                            fm.callbacks, fm.modalSaveBtn, fm.uploader, fm.filetype);
                }

                , downloadCloudAttachments: function (event, accessToken, url, filename, isPhoto, isSignature) {

                    var _that = this;

                    var xhr = new XMLHttpRequest();
                    xhr.open('GET', url);
                    xhr.responseType = "blob";
                    if (typeof accessToken !== "undefined") {
                        xhr.setRequestHeader('Authorization', 'Bearer ' + accessToken);
                    }

                    xhr.onload = function (a1) {
                        var blob = xhr.response;
                        var fileObj = new File([blob], filename, {type: blob.type});

                        _that.initCloudFileUpload(fileObj, isPhoto, isSignature, _that.indexUploadBtn);
                    };
                    xhr.onerror = function () {
                        _that.getMessageContainer().trigger("europass:message:show", ["error", "Error fetching attachment from cloud, try again."]);
                        return;
                    };
                    xhr.send();
                },

                cleanupFeedback: function () {
                    this.getMessageContainer().html("");
                },
                getMessageContainer: function () {
                    return this.$el.find(this.messageContainerSelector);
                }

            });
            return AttachmentCloudFormView;
        }
        );