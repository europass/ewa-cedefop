define(
        [
            'module',
            'jquery', //'underscore',
            'backbone',
            'Utils',
            'hbs!templates/upload/onedrive',
            'europass/http/WindowConfigInstance',
            'europass/http/ServicesUri',
            'europass/http/MediaType',
            'europass/http/Resource',
            'views/upload/UploadController',
            'i18n!localization/nls/Notification', //,'i18n!localization/nls/GuiLabel'
            'oneDriveAPI',
            'views/main/cloud/OneDriveAuthenticate',
            'analytics/EventsController',
            'europass/GlobalDocumentInstance'
        ],
        function (module, $, Backbone, Utils, Template, WindowConfig, ServicesUri, MediaType, Resource, UploadController, Notification,
                OneDriveAPI, OneDriveAuthenticate, Events, GlobalDocument) {

            var OneDriveUploadView = Backbone.View.extend({
                name: "onedrive",
                redirectUri: window.location.href,
                oneDriveCookieAccessToken: "cloud-access-token",
                event: new Events,
                events: {
                    "europass:popup:enabled": "doConnectLogin"
                },

                onClose: function () {
                    this.uploadController.cleanup();
                    delete this.uploadController;
                    this.undelegateEvents();
                    this.unbind();
                },
                initialize: function (options) {
                    this.appKey = WindowConfig.onedriveAppkey;
                    this.pickerAppID = WindowConfig.onedriveFilePickerAppId;
                    this.oneDrivePickerPage = WindowConfig.onedriveFilePickerCallbackUrl;
                    this.parentView = options.parentView;
                    this.messageContainer = options.messageContainer;

                    //Reusable Upload Controller
                    this.uploadController = new UploadController({
                        relatedController: this,
                        messageContainer: this.messageContainer,
                        modelUpdateEvent: "model:uploaded:cloud",
                        modelUpdateMsgKey: "skillspassport.import.cloud.success.onedrive"
                    });

                    this.oneDriveAuthenticate = new OneDriveAuthenticate({});
                },
                /**
                 * Render the View
                 */
                render: function () {
                },
                /**
                 * Complete the upload. Use the uploaded profile to populate the editor.
                 */
                doComplete: function () {
//			console.log("do complete");
                    this.uploadController.uploaded();
                },
                /**
                 * Do Connect
                 */
                doConnect: function () {
                    this.event.importFrom('OneDrive');
                    this.parentView.checkPopUp(this.$el);
                },

                doConnectLogin: function () {
                    var _that = this;

                    $("body").trigger("europass:waiting:indicator:show");

                    setTimeout(function () { //workaround for Safari 11 issue see EPAS-1072 
                        var popup = _that.oneDriveAuthenticate.genericAuthenticate();
                        if (popup === null && Utils.tryOpeningPopUp() === false) {
                            _that.onPickerCancel("warning", Notification["skillspassport.wizard.cloud.popup.blocked"]);
                            $("body").trigger("europass:waiting:indicator:hide");
                            return;
                        } else if (popup === undefined || popup === null) {
                            _that.onPickerCancel("warning", Notification["skillspassport.import.cloud.nofile.onedrive"]);
                            $("body").trigger("europass:waiting:indicator:hide");
                            return;
                        }

                        var timer = setInterval(checkAuthWindowClosed, 500);
                        function checkAuthWindowClosed() {
                            if (popup.closed) {
                                clearInterval(timer);

                                _that.accessToken = Utils.readCookieByName(_that.oneDriveCookieAccessToken);
                                if (_.isEmpty(_that.accessToken) || _that.accessToken === false) {
                                    // Maybe can use more proper notification message!
                                    _that.onPickerCancel("warning", Notification["skillspassport.import.cloud.nofile.onedrive"]);
                                } else {
                                    var odOptions = {
                                        clientId: _that.pickerAppID,
                                        action: "download",
                                        multiSelect: false,
                                        advanced: {
                                            redirectUri: window.location.protocol + "//" + window.location.hostname + _that.oneDrivePickerPage,
                                            endpointHint: "api.onedrive.com",
                                            accessToken: _that.accessToken,
                                            filter: "folder,.pdf,.xml"
                                        },
                                        success: function (files) {
                                            _that.downloadFromOneDrive(files, _that.accessToken);
                                        },
                                        cancel: function () {
                                            _that.onPickerCancel("warning", Notification["skillspassport.import.cloud.nofile.onedrive"]);
                                        },
                                        error: function (e) {
                                            require(
                                                    ['i18n!localization/nls/Notification'],
                                                    function (Notification) {
                                                        $("body").trigger("europass:waiting:indicator:show");
                                                        _that.onPickerFailure("warning", Notification["skillspassport.import.cloud.unauthorized"]);
                                                    }
                                            );
                                        }
                                    };
                                    OneDriveAPI.open(odOptions);
                                }
                            }
                        }
                    }, 500);
                },
                /**
                 * Picker failure callback
                 */
                onPickerFailure: function (level, message) {
                    $("body").trigger("europass:waiting:indicator:hide");
                    this.messageContainer.trigger("europass:message:show", [level, message, true]);
                },
                /**
                 * Picker cancel callback
                 */
                onPickerCancel: function (level, message) {
                    $("body").trigger("europass:waiting:indicator:hide");
                    this.messageContainer.trigger("europass:message:show", [level, message, true]);
                    this.onClose();
                },
                /**
                 * Successfull callback after a file is chosen
                 * @param donwloadResponse
                 */
                downloadFromOneDrive: function (files, accessToken) {

                    //start the waiting indicator...
                    $("body").trigger("europass:waiting:indicator:show", true);

                    //pass user-cookie id
                    var cookieId = '';
                    if (Utils.readCookie()) { //user-cookie exists
                        cookieId = Utils.readCookie();
                    }

                    var permissionToKeepNotImportedCv = $('body').find(":input[type=\"checkbox\"]#keep-not-imported-cv-permission:checked").length > 0;
                    var postUrl = ServicesUri.document_upload_from_cloud + "?id=" + cookieId + "&keepCv=" + permissionToKeepNotImportedCv;

                    if (files.value.length > 0) {

                        //this is the View
                        var _that = this;

                        var httpResource = new Resource(postUrl);
                        httpResource.contentType(MediaType.json);

                        httpResource._post(
                                {data: {
                                        url: files.value[0]["@content.downloadUrl"],
                                        token: accessToken
                                    }},
                                {
                                    success: {
                                        scope: _that,
                                        callback: function (data) {
                                            $("body").trigger("europass:waiting:indicator:show", true);
                                            this.uploadController.uploadedCallback(data, files.value[0].name, this.parentView);
                                            this.$el.trigger("europass:wizard:import:complete");
                                        }
                                    },
                                    error: {
                                        scope: _that,
                                        callback: function (status, responseText) {
                                            this.uploadController.onUploadFailure(status, responseText);
                                        }
                                    },
                                    complete: {
                                        scope: _that,
                                        callback: function (status, responseText) {
                                            $("body").trigger("europass:waiting:indicator:hide");
                                            _that.onClose();
                                        }
                                    }
                                });
                    }
                }
            });
            return OneDriveUploadView;
        }
);