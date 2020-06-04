define(
        [
            'jquery',
            'underscore',
            'backbone',
            'hbs!templates/download/onedrive',
            'europass/http/WindowConfigInstance',
            'europass/http/Resource',
            'europass/http/MediaType',
            'views/download/DownloadController',
            'analytics/EventsController',
            'oneDriveAPI',
            'views/main/cloud/OneDriveAuthenticate',
            'Utils'
                    , 'i18n!localization/nls/Notification'
//		',i18n!localization/nls/GuiLabel'
        ],
        function ($, _, Backbone,
                Template,
                WindowConfig,
                Resource,
                MediaType,
                DownloadController,
                Events,
                OneDriveAPI,
                OneDriveAuthenticate,
                Utils,
                Notification
//		  ,GuiLabel
                ) {
            var OneDriveStoreView = Backbone.View.extend({

                name: "onedrive",
                alreadyRendered: false,
                redirectUri: window.location.href,
                accessToken: null,
                oneDriveCookieAccessToken: "cloud-access-token",
                oneDriveAPIPath: "https://api.onedrive.com/v1.0/drive/",
                event: new Events,
                events: {
                    "europass:popup:enabled": "doConnectLogin"
                },

                /**
                 * Cleanup
                 */
                onClose: function () {
                    delete this.accessToken;
                    this.downloadController.cleanup();
                    delete this.downloadController;
                    this.undelegateEvents();
                    this.unbind();
                },
                /**
                 * Initialize
                 * @param options
                 */
                initialize: function (options) {

                    this.appKey = WindowConfig.onedriveAppkey;
                    this.pickerAppID = WindowConfig.onedriveFilePickerAppId;
                    this.oneDrivePickerPage = WindowConfig.onedriveFilePickerCallbackUrl;
                    this.cloudExportStorageFolder = WindowConfig.cloudExportStorageFolder;
                    this.parentView = options.parentView;
                    this.messageContainer = options.messageContainer;

                    //Reusable Upload Controller
                    this.downloadController = new DownloadController({
                        relatedController: this,
                        messageContainer: this.messageContainer,
                        info: options.info
                    });
                    this._url = this.downloadController.decideUrl();
                    this.oneDriveAuthenticate = new OneDriveAuthenticate({});
                },
                /**
                 * Render the View
                 */
                render: function () {

                    if (this.alreadyRendered === false) {

                        var html = Template();
                        this.$el.html(html);

                        this.alreadyRendered = true;

                    }

                },

                /**
                 * Prepare the data to post to the API
                 * @param accessToken
                 * @param folderId
                 */
                prepareData: function (accessToken, folderId) {
                    return this.downloadController.prepareData(accessToken, folderId);
                },
                /**
                 * Do Connect
                 */
                doConnect: function () {
                    var url = this.downloadController.decideUrl();
                    this.event.exportTo(url);
                    this.parentView.checkPopUp(this.$el);
                },

                doConnectLogin: function () {
                    var _that = this;

                    $("body").trigger("europass:waiting:indicator:show");


                    setTimeout(function () { //workaround for Safari 11 issue see EPAS-1072 
                        var popup = _that.oneDriveAuthenticate.genericAuthenticate();
                        if (popup === null && Utils.tryOpeningPopUp() === false) {
                            _that.onPickerFailure("warning", Notification["skillspassport.wizard.cloud.popup.blocked"]);
                            return;
                        } else if (popup === null || popup === undefined) {
                            _that.onPickerFailure("warning", Notification["skillspassport.export.cloud.nofolder.onedrive"]);
                            return;
                        }

                        var timer = setInterval(checkAuthWindowClosed, 500);
                        function checkAuthWindowClosed() {
                            if (popup.closed) {
                                clearInterval(timer);
                                _that.accessToken = Utils.readCookieByName(_that.oneDriveCookieAccessToken);
                                if (_.isEmpty(_that.accessToken) || _that.accessToken === false) {
                                    _that.onPickerFailure("warning", Notification["skillspassport.export.cloud.nofolder.onedrive"]);
                                } else {

                                    //Check if default cloud folder exists, and if not create it
                                    var errorWhenCheckFolders = _that.checkFolderExistsAndError(_that.accessToken, _that.cloudExportStorageFolder);
                                    if (errorWhenCheckFolders === true) {
                                        return;
                                    }

                                    var odOptions = {
                                        clientId: _that.pickerAppID,
                                        action: "query",
                                        multiSelect: false,
                                        advanced: {
                                            redirectUri: window.location.protocol + "//" + window.location.hostname + _that.oneDrivePickerPage,
                                            endpointHint: "api.onedrive.com",
                                            accessToken: _that.accessToken
                                        },
                                        success: function (folder) {
                                            if (folder.value.length > 0) {
                                                _that.uploadToDrive(folder.value[0], _that.accessToken);
                                            }
                                        },
                                        cancel: function () {
                                            _that.onPickerFailure("warning", Notification["skillspassport.export.cloud.nofolder.onedrive"]);
                                        },
                                        error: function (e) {
                                            require(
                                                    ['i18n!localization/nls/Notification'],
                                                    function (Notification) {
                                                        $("body").trigger("europass:waiting:indicator:show");
                                                        _that.onPickerFailure("warning", Notification["skillspassport.export.cloud.error.unauthorized"]);
                                                    }
                                            );
                                        }
                                    };
                                    OneDriveAPI.save(odOptions);
                                }
                            }
                        }
                    }, 500);
                },
                /**
                 * List files request
                 * 
                 */
                checkFolderExistsAndError: function (accessToken, folderName) {

                    var _that = this;
                    var _check_error = false;
                    $.ajax({
                        type: "GET",
                        async: false,
                        url: _that.oneDriveAPIPath + "root:/" + folderName + "?access_token=" + accessToken,
                        headers: {"content-type": "application/json"},
                        success: function (response) {
                            if (_.isNull(response))
                                return true;
                            if (response.name === folderName) {
                                this.cloudStorageFolderID = response.id;
                            }
                        },
                        error: function (response, arg1, arg2) {
                            // If no default folder is found then should create it.
                            if (response.status == 404) {
                                _that.createDefaultFolder(accessToken);
                            } else {
                                _that.downloadController.onUploadFailure(response.status, response.responseText);
                                _that.parentView.enableConnect(true);
                                _check_error = true;
                            }
                        }
                    });

                    return _check_error;
                },

                /**
                 * Create the default folder
                 * 
                 */
                createDefaultFolder: function (accessToken) {

                    var _that = this;
                    $.ajax({
                        type: "POST",
                        url: _that.oneDriveAPIPath + "root/children?access_token=" + accessToken,
                        data: JSON.stringify({"name": _that.cloudExportStorageFolder, "folder": {},
                            "description": "Default Europass Document Store Folder"}),
                        headers: {"content-type": "application/json"},
                        success: function (response) {
                            _that.cloudStorageFolderID = response.id;
                        },
                        error: function (data) {
                            _that.downloadController.onUploadFailure(data.status, data.responseText);
                            _that.parentView.enableConnect(true);
                        }
                    });
                },

                /**
                 * Picker failure callback.
                 * Called also when the user cancels the action.
                 */
                onPickerFailure: function (level, message) {

                    $("body").trigger("europass:waiting:indicator:hide");
                    this.messageContainer.trigger("europass:message:show", [level, message, true]);
                    $("body").trigger("europass:waiting:indicator:hide");
                },
                /**
                 * Successful callback after a folder is selected
                 * @param response
                 */
                uploadToDrive: function (folder, accessToken) {
                    var filePath = folder.id;

                    var httpResource = new Resource(this._url);
                    httpResource.contentType(MediaType.json);

                    $("body").trigger("europass:waiting:indicator:show", true);
                    httpResource._post({
                        data: this.prepareData(accessToken, filePath)
                    }, {
                        success: {
                            scope: this,
                            callback: function (response) {
                                $("body").trigger("europass:waiting:indicator:show", true);

                                var filename = $(response).filter("meta[name='filename']").attr("content");
                                if (_.isUndefined(filename))
                                    filename = "Europass CV";

                                this.downloadController.triggerMessageWithPathFile("skillspassport.export.cloud.success.onedrive",
                                        folder.name + "/" + filename, "success", false);

                                this.$el.trigger("europass:wizard:export:complete");

                            }
                        },
                        error: {
                            scope: this,
                            callback: function (status, responseText) {
                                this.downloadController.onUploadFailure(status, responseText);
                            }
                        },
                        complete: {
                            scope: this,
                            callback: function (status, responseText) {
                                $("body").trigger("europass:waiting:indicator:hide");
                            }
                        }
                    });
                }
            });

            return OneDriveStoreView;
        }
);