define(
        [
            'jquery',
            'underscore',
            'backbone',
            'hbs!templates/download/onedrive',
            'europass/http/WindowConfigInstance',
            'europass/http/Resource',
            'europass/http/MediaType',
            'europass/http/ServicesUri',
            'views/download/DownloadController',
            'i18n!localization/nls/Notification',
            'i18n!localization/nls/GuiLabel',
            'europass/http/SessionManagerInstance',
            'oneDriveAPI',
            'views/main/cloud/OneDriveAuthenticate',
            'Utils',
            'analytics/EventsController'
        ],
        function ($, _, Backbone,
                Template,
                WindowConfig,
                Resource,
                MediaType,
                ServicesUri,
                DownloadController,
                Notification,
                GuiLabel,
                Session,
                OneDriveAPI,
                OneDriveAuthenticate,
                Utils,
                Events) {

            var OneDriveShareView = Backbone.View.extend({

                name: "onedrive",
                alreadyRendered: false,
                redirectUri: window.location.href,
                oneDriveBtnID: "",
                oneDriveCookieAccessToken: "cloud-access-token",
                sharedFolderPathArray: [],
                sharedFolderPathStep: 0,
                sharedFolderId: null,
                currentFileName: "",
                currentSharedItems: [],
                shareReadMe: "readMe.txt",
                oneDriveURL: "https://api.onedrive.com/v1.0/drive/",
                event: new Events,

                events: {
                    "europass:share:onedrive": "shareFile",
                    "europass:share:onedrive:folder:ready": "uploadFile",
                    "europass:share:onedrive:folder:checked": "createSharedFolder",
                    "europass:share:onedrive:list": "getFiles",
                    "europass:share:onedrive:delete": "removeFile",
                    "europass:share:views:cleanup": "cleanUp"
                },

                /**
                 * Cleanup
                 */
                cleanUp: function () {
                    delete this.accessToken;
                    delete this.sharedFolderId;
                    delete this.currentFileName;
                    this.alreadyRendered = false;
                    this.sharedFolderPathStep = 0;
                    this.currentSharedItems = [];
                },
                /**
                 * Initialize
                 * @param options
                 */
                initialize: function (options) {

                    this.appKey = WindowConfig.onedriveAppkey;
                    this.sharedFolderPathArray = WindowConfig.cloudShareFolder.split("/");

                    //Reusable Upload Controller
                    this.downloadController = new DownloadController({
                        relatedController: this,
                        info: options.info
                    });

                    this.oneDriveAuthenticate = new OneDriveAuthenticate({});
                },

                shareFile: function (event, data) {
                    this.event.postTo('OneDrive');
                    this.oneDriveBtnID = data;
                    this.doConnect();
                },

                uploadFile: function (event, accessToken, folderId) {
                    this.uploadToDrive(folderId, accessToken);
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

                    var _that = this;

                    setTimeout(function () { //workaround for Safari 11 issue see EPAS-1072 
                        var popup = _that.oneDriveAuthenticate.genericAuthenticate();
                        if (popup === null) {
                            return;
                        }

                        var timer = setInterval(checkAuthWindowClosed, 500);

                        function checkAuthWindowClosed() {
                            if (popup.closed) {
                                clearInterval(timer);

                                _that.accessToken = Utils.readCookieByName(_that.oneDriveCookieAccessToken);

                                if (_.isEmpty(_that.accessToken) || _that.accessToken === false) {
                                    var errorDesc = "Error during oneDrive authentication, missing accessToken";
                                    _that.triggerError(400, errorDesc.replace(/\+/g, " "));
                                } else {

                                    // New OneDrive API is not supporting getting email information from account!!!
                                    // So we still need to use old API for that request.
                                    // https://github.com/OneDrive/onedrive-api-docs/issues/202
                                    $.ajax({
                                        type: "GET",
                                        async: false,
                                        url: "https://apis.live.net/v5.0/me" + "?select=*&access_token=" + _that.accessToken,
                                        success: function (response) {
                                            if (!response.error && response.emails !== undefined && response.emails.account !== undefined) {
                                                $("body").trigger("europass:share:user:email", ["share-onedrive", response.emails.account, response.name]);
                                                $("body").trigger("europass:share:user:logged", ["onedrive"]);

                                                if (_that.sharedFolderPathArray.length > _that.sharedFolderPathStep) {
                                                    var folderName = _that.sharedFolderPathArray[_that.sharedFolderPathStep];
                                                    _that.createFolder(_that.accessToken, folderName);
                                                }
                                            }
                                        },
                                        error: function (response) {
                                            var errorDesc = JSON.parse(response.responseText).error.message;
                                            _that.triggerError(400, errorDesc.replace(/\+/g, " "));
                                        }
                                    });
                                }
                            }
                        }
                    }, 500);
                },

                /**
                 * Create the default folder
                 */
                createFolder: function (accessToken, folder) {

                    $("body").trigger("europass:waiting:indicator:show", true);

                    var _that = this;

                    $.ajax({
                        type: "POST",
                        url: _that.oneDriveURL + "root/children?access_token=" + accessToken,
                        data: JSON.stringify({"name": _that.sharedFolderPathArray[0], "folder": {},
                            "description": "Default Europass Document " + folder + " Folder"}),
                        headers: {"content-type": "application/json"},
                        success: function (data) {
                            $.ajax({
                                type: "POST",
                                url: _that.oneDriveURL + "root:/" + _that.sharedFolderPathArray[0] + ":/children?access_token=" + accessToken,
                                data: JSON.stringify({"name": _that.sharedFolderPathArray[1], "folder": {},
                                    "description": "Default Europass Document " + folder + " Folder"}),
                                headers: {"content-type": "application/json"},
                                success: function (response) {
                                    _that.uploadToDrive(response.id, accessToken);
                                },
                                error: function (response) {
                                    var errorDesc = JSON.parse(response.responseText).error.message;
                                    _that.triggerError(400, errorDesc.replace(/\+/g, " "));
                                    $("body").trigger("europass:waiting:indicator:hide");
                                }
                            });
                        },
                        error: function (response) {
                            var errorDesc = JSON.parse(response.responseText).error.message;
                            _that.triggerError(400, errorDesc.replace(/\+/g, " "));
                            $("body").trigger("europass:waiting:indicator:hide");
                        }
                    });
                },

                /**
                 * Successful callback after a folder is selected
                 * @param folderId : oneDrive folder id
                 * @param accessToken : token after successful auth
                 */
                uploadToDrive: function (folderId, accessToken) {

                    $("body").trigger("europass:share:manage:folders", ["onedrive", folderId]);

                    var url = ServicesUri.document_conversion_to["onedrive_share"] + Session.urlappend();
                    var httpResource = new Resource(url);
                    httpResource.contentType(MediaType.json);

                    var _that = this;
                    httpResource._post({
                        data: this.prepareData(accessToken, folderId)
                    }, {
                        success: {
                            scope: this,
                            callback: function (response) {

                                var filename = $(response).filter("meta[name='filename']").attr("content");
//						var jsessionid = $(response).filter("meta[name='jsessionid']").attr("content");
                                var shareUrl = $(response).filter("meta[name='shareUrlBase']").attr("content");
                                var fileUrl = $(response).filter("meta[name='fileUrl']").attr("content");

                                if (filename === undefined || shareUrl === undefined || fileUrl === undefined) {
                                    _that.triggerError(400, "");
                                }

                                _that.currentFileName = filename;

                                var escapedFileDownloadUrl = encodeURIComponent(fileUrl);
                                shareUrl += "?url=" + escapedFileDownloadUrl + "&token=" + accessToken + "&cloudProvider=onedrive";// + Session.urlappend();

                                var editorsUrl = window.location.origin + WindowConfig.getDefaultEwaEditorContext();

                                $("body").trigger("europass:share:link:onedrive", [_that.oneDriveBtnID, editorsUrl + shareUrl]);
                            }
                        },
                        error: {
                            scope: this,
                            callback: function (status, responseText) {

                                var parsed = "";
                                try {
                                    var error = $(responseText).filter("script[type='application/json']").html();
                                    if (!_.isUndefined(responseText)) {
                                        parsed = JSON.parse(error);
                                    }

                                } catch (e) {
                                    $("body").trigger("europass:waiting:indicator:hide");
                                    _that.triggerError(status, "");
                                }

                                var errMessage = "";

                                if (!_.isUndefined(parsed.Error)) {
                                    if (!_.isUndefined(parsed.Error.message) && !_.isNull(parsed.Error.message))
                                        errMessage = parsed.Error.message;
                                    if (!_.isUndefined(parsed.Error.trace) && !_.isNull(parsed.Error.trace))
                                        errMessage += " " + parsed.Error.trace;
                                }

                                _that.triggerError(status, errMessage);
                            }
                        },
                        complete: {
                            scope: this,
                            callback: function (status, responseText) {
                                _that.checkReadmeFile(folderId, accessToken);
                                $("body").trigger("europass:waiting:indicator:hide");
                            }
                        }
                    });
                },

                checkReadmeFile: function (folderId, accessToken) {
                    var _that = this;
                    var content = GuiLabel["share.cloud.readme.contents"];

                    $.ajax({
                        type: "PUT",
                        url: _that.oneDriveURL + "items/root:/" + _that.sharedFolderPathArray[0] + "/" + _that.sharedFolderPathArray[1] + "/"
                                + _that.shareReadMe + ":/content?access_token=" + accessToken,
                        headers: {"content-type": "application/json"},
                        data: content,
                        success: function (data) {
                            console.log('yes..');
                        },
                        error: function (response) {
                            var errorDesc = JSON.parse(response.responseText).error.message;
                            _that.triggerError(400, errorDesc);
                        }
                    });

                },

                getFiles: function (event) {

                    if (this.currentSharedItems.length > 0) {
                        $("body").trigger("europass:share:manage:render", [this.currentSharedItems]);
                        $("body").trigger("europass:waiting:indicator:hide");
                        return;
                    }

                    var _that = this;
                    var accessToken = Utils.readCookieByName(_that.oneDriveCookieAccessToken);
                    $.ajax({
                        type: "GET",
                        cache: false,
                        url: _that.oneDriveURL + "root:/" + _that.sharedFolderPathArray[0] + "/" + _that.sharedFolderPathArray[1] +
                                ":/children?orderby=lastModifiedDateTime%20desc&filter=file%20ne%20null&access_token=" + accessToken,
                        headers: {"content-type": "application/json"},
                        success: function (response) {
                            for (var idx in response.value) {
                                var fileObj = response.value[idx];
                                if (_that.shareReadMe !== fileObj.name) {
                                    var revoke = (_that.currentFileName !== fileObj.name ? true : false);
                                    var displayName = fileObj.name.replace(/[0-9]{2}_[0-9]{2}_[0-9]{2}/g, function (arg1) {
                                        return arg1.replace(/_/g, ":");
                                    });

                                    _that.currentSharedItems.push({"id": fileObj.id, "name": displayName, "date": fileObj.createdDateTime, "size": fileObj.size, "canRevoke": revoke});
                                }
                            }
                            $("body").trigger("europass:share:manage:render", [_that.currentSharedItems]);
                            $("body").trigger("europass:waiting:indicator:hide");
                        },
                        error: function (response) {
                            var errorDesc = JSON.parse(response.responseText).error.message;
                            _that.triggerError(400, errorDesc);
                        }
                    });
                },

                removeFile: function (event, fileId, parent) {

                    var _that = this;
                    var accessToken = Utils.readCookieByName(_that.oneDriveCookieAccessToken);
                    $.ajax({
                        type: "DELETE",
                        url: _that.oneDriveURL + "items/" + fileId + "?access_token=" + accessToken,
                        success: function () {
                            parent.slideToggle("up", function () {
                                _that.updateCurrentSharedItems(fileId);
                                $(this).remove();
                            });
                        },
                        error: function (response) {
                            var errorDesc = JSON.parse(response.responseText).error.message;
                            _that.triggerError(400, errorDesc);
                        }
                    });
                }

                , updateCurrentSharedItems: function (revokedId) {

                    for (var idx in this.currentSharedItems) {
                        var item = this.currentSharedItems[idx];
                        if (item.id === revokedId) {
                            this.currentSharedItems.splice(idx, 1);
                            break;
                        }
                    }
                }

                , triggerError: function (status, responseText) {
                    $("body").trigger("europass:share:response:error", [status, responseText]);
                }

            });

            return OneDriveShareView;
        }
);