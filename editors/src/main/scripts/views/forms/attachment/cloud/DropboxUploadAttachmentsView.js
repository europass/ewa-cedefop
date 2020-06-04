define(
        [
            'module',
            'jquery',
            'backbone',
            'Utils',
            'europass/http/ServicesUri',
            'europass/http/WindowConfigInstance',
            'i18n!localization/nls/Notification',
            'dropin'
        ],
        function (module, $, Backbone, Utils, ServicesUri, WindowConfig, Notification) {

            var DropboxUploadAttachmentsView = Backbone.View.extend({

                btnId: "dropbox-upload-attachment",

                initialize: function (options) {
                    this.appKey = WindowConfig.dropboxAppKey;
                    this.callbackUrl = WindowConfig.dropboxCallbackUrl;

                    this.parentView = options.parentView;
                    $("#" + this.btnId).remove();
                },

                render: function () {
                    this.parentView.cleanupFeedback();
                    this.importViewDropBox = this.parentView.currentView.cid;
                },

                doConnectLogin: function (isPhoto, isSignature) {

                    var _that = this;

                    $("body").trigger("europass:waiting:indicator:show", true);

                    if (this.importViewDropBox !== this.parentView.currentView.cid)
                        return false;

                    //Append a hidden button in the form
                    var btn = _that.$el.find("#" + _that.btnId);
                    if (btn.length === 0) {
                        $("<a id=\"" + _that.btnId + "\" class=\"cloud dropbox\" type=\"button\" ></a>").appendTo(_that.$el);
                    }
                    //Trigger programmatically the click on the hidden button
                    setTimeout(function () {

                        try {
                            _that.prepareDropboxChooser(_that.getAllowedExtensions(isPhoto, isSignature),
                                    $.proxy(_that.uploadCallback, _that, isPhoto, isSignature));

                            var btn = _that.$el.find("#" + _that.btnId);
                            if (btn.length > 0) {
                                btn.trigger('click');
                            }

                        } catch (e) {
                            if (Utils.tryOpeningPopUp() === false) {
                                _that.parentView.getMessageContainer().trigger("europass:message:show",
                                        ["warning", Notification["skillspassport.wizard.cloud.popup.blocked"]]);
                                $("body").trigger("europass:waiting:indicator:hide");
                            } else {
                                _that.parentView.getMessageContainer().trigger("europass:message:show",
                                        ["warning", (Notification["skillspassport.import.cloud.error"]), true]);
                                $("body").trigger("europass:waiting:indicator:hide");
                            }
                        }
                    }, 1000);
                },
                /**
                 * Callback as soon as the person has selected something from the cloud storage
                 */
                uploadCallback: function (isPhoto, isSignature, files) {

                    var _that = this;

                    var file = files[0];
                    var url = file.link;

                    _that.$el.trigger("europass:googledrive:poc:cloud:attachments",
                            [undefined, url, file.name, isPhoto, isSignature]);

                    $("body").trigger("europass:waiting:indicator:hide");
                },

                /**
                 * Prepare the chooser button
                 */
                prepareDropboxChooser: function (allowedExtensions, successCallback) {

                    Dropbox.appKey = this.appKey;

                    var options = {
                        success: successCallback,
                        cancel: function () {
                            $("body").trigger("europass:waiting:indicator:hide", true);
                        },
                        linkType: "direct",
                        multiselect: false,
                        extensions: allowedExtensions
                    };

                    Dropbox.choose(options);
                },

                getAllowedExtensions: function (isPhoto, isSignature) {

                    var allowedExtensions = ['.pdf', '.jpeg', '.jpg', '.png'];
                    if (isSignature || isPhoto) {
                        allowedExtensions = ['.jpeg', '.jpg', '.png'];
                    }

                    return allowedExtensions;
                }

            });

            return DropboxUploadAttachmentsView;
        }
);