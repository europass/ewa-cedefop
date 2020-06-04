define(
        [
            'module',
            'jquery',
            'backbone',
            'Utils',
            'europass/http/WindowConfigInstance',
            'i18n!localization/nls/Notification',
            'oneDriveAPI',
            'views/main/cloud/OneDriveAuthenticate'
        ],
        function (module, $, Backbone, Utils, WindowConfig, Notification, OneDriveAPI, OneDriveAuthenticate) {

            var OneDriveUploadAttachmentsView = Backbone.View.extend({
                redirectUri: window.location.href,
                oneDriveCookieAccessToken: "cloud-access-token",

                initialize: function (options) {

                    this.appKey = WindowConfig.onedriveAppkey;
                    this.pickerAppID = WindowConfig.onedriveFilePickerAppId;
                    this.oneDrivePickerPage = WindowConfig.onedriveFilePickerCallbackUrl;
                    this.parentView = options.parentView;

                    this.oneDriveAuthenticate = new OneDriveAuthenticate({});
                },

                render: function () {
                    this.parentView.cleanupFeedback();
                    this.importViewOneDrive = this.parentView.currentView.cid;
                },

                doConnectLogin: function (isPhoto, isSignature) {

                    if (this.importViewOneDrive !== this.parentView.currentView.cid)
                        return false;

                    var _that = this;

                    $("body").trigger("europass:waiting:indicator:show");

                    setTimeout(function () { //workaround for Safari 11 issue see EPAS-1072
                        var popup = _that.oneDriveAuthenticate.genericAuthenticate();
                        if (popup === null && Utils.tryOpeningPopUp() === false) {
                            _that.parentView.getMessageContainer().trigger("europass:message:show", ["warning", Notification["skillspassport.wizard.cloud.popup.blocked"]]);
                            $("body").trigger("europass:waiting:indicator:hide");
                            return;
                        } else if (popup === undefined || popup === null) {
                            _that.parentView.getMessageContainer().trigger("europass:message:show", ["warning", Notification["skillspassport.import.cloud.nofile.onedrive"]]);
                            $("body").trigger("europass:waiting:indicator:hide");
                            return;
                        }

                        var timer = setInterval(checkAuthWindowClosed, 500);
                        function checkAuthWindowClosed() {
                            if (popup.closed) {
                                clearInterval(timer);
                                _that.accessToken = Utils.readCookieByName(_that.oneDriveCookieAccessToken);
                                if (_.isEmpty(_that.accessToken) || _that.accessToken === false) {
                                    _that.parentView.getMessageContainer().trigger("europass:message:show", ["error", "OneDrive Authentication error. Try again."]);
                                    return;
                                } else {
                                    var odOptions = {
                                        clientId: _that.pickerAppID,
                                        action: "download",
                                        multiSelect: false,
                                        advanced: {
                                            redirectUri: window.location.protocol + "//" + window.location.hostname + _that.oneDrivePickerPage,
                                            endpointHint: "api.onedrive.com",
                                            accessToken: _that.accessToken,
                                            filter: _that.getAllowedExtensions(isPhoto, isSignature)
                                        },
                                        success: function (files) {
                                            _that.downloadFromOneDrive(files, _that.accessToken, isPhoto, isSignature);
                                        },
                                        cancel: function () {
                                            return;
                                        },
                                        error: function (e) {
                                            _that.parentView.getMessageContainer().trigger("europass:message:show", ["error", "OneDrive import error. Try again."]);
                                            return;
                                        }
                                    };
                                    OneDriveAPI.open(odOptions);
                                }
                            }
                        }
                    }, 500);
                },

                /**
                 * Successfull callback after a file is chosen
                 */
                downloadFromOneDrive: function (files, accessToken, isPhoto, isSignature) {

                    var _that = this;

                    if (files.value.length > 0) {

                        var url = files.value[0]["@content.downloadUrl"];

                        _that.$el.trigger("europass:googledrive:poc:cloud:attachments",
                                [accessToken, url, files.value[0].name, isPhoto, isSignature]);

                    }
                },

                getAllowedExtensions: function (isPhoto, isSignature) {

                    var allowedExtensions = "folder,.pdf,.jpeg,.jpg,.png";
                    if (isSignature || isPhoto) {
                        allowedExtensions = "folder,.jpeg,.jpg,.png";
                    }

                    return allowedExtensions;
                }

            });
            return OneDriveUploadAttachmentsView;
        }
);