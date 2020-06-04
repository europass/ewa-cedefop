define(
        [
            'jquery',
            'underscore',
            'backbone',

            'europass/http/WindowConfigInstance',
            'europass/http/Resource',
            'europass/http/MediaType',
            'europass/http/ServicesUri',

            'views/upload/google/ApiManager',

            'i18n!localization/nls/GuiLabel',
            'Utils',
            'analytics/EventsController'
        ],
        function ($, _, Backbone,
                WindowConfig,
                Resource,
                MediaType,
                ServicesUri,
                ApiManager,
                GuiLabel,
                Utils,
                Events
                ) {
            var GoogleShareView = Backbone.View.extend({

                name: "googledrive",

                boundary: "europass-document",

                alreadyRendered: false,

                scopes: ["https://www.googleapis.com/auth/drive.apps.readonly", "https://www.googleapis.com/auth/drive.file", "email", "profile"],
                pickerApiLoaded: false,
                accessToken: null,

                defaultStoreFolderID: 'root',

                googleBtnID: "",

                currentFileId: null,

                currentSharedItems: [],

                //TODO: Configurable
                shareReadMe: "readMe.txt",
                event: new Events,
                events: {
                    "europass:share:google": "shareFile",
                    "europass:share:google:list": "getFiles",
                    "europass:share:google:delete": "removeFile",
                    "europass:share:google:refresh": "refreshGapi",

                    "europass:share:views:cleanup": "cleanUp"
                },

                /**
                 * Cleanup
                 */
                cleanUp: function () {
                    delete this.accessToken;
                    delete this.currentFileId;
                    this.currentSharedItems = [];
                    this.alreadyRendered = false;
                },
                /**
                 * Initialize
                 * @param options
                 */
                initialize: function (options) {

                    this.appId = WindowConfig.googledriveAppId;
                    this.clientId = WindowConfig.googledriveClientId;
                    this.devKey = WindowConfig.googledriveDevKey;

                    this.apiManager = null;
                    this.googleApiRequestURL = "https://www.googleapis.com/drive/v2/files";
                },
                /**
                 * Triggered when the share to google drive button is pressed 
                 */
                shareFile: function (event, data) {
                    this.event.postTo('GoogleDrive');
                    this.googleBtnID = data;
                    this.doConnect();
                },
                /**
                 * Do Connect
                 */
                doConnect: function () {

                    $("body").trigger("europass:waiting:indicator:show", true);
                    if (this.apiManager === null) {
                        this.apiManager = new ApiManager({
                            apiKey: this.appId,
                            clientId: this.clientId,
                            scopes: this.scopes,
                            callback: this.apiManagerCallback,
                            scope: this
                        });
                    }
                    this.apiManager.loadGapi();
                },
                apiManagerCallback: function (authResult) {
                    this.accessToken = authResult.access_token;
                    this.cloudStorageFolderParts = WindowConfig.cloudShareFolder.split("/");

                    var _that = this;
                    var request = gapi.client.drive.about.get();
                    request.execute(function (resp) {
                        if (resp !== undefined) {
                            if (resp.user !== undefined && resp.user.emailAddress !== undefined && resp.user.emailAddress !== "") {
                                $("body").trigger("europass:share:user:email", ["share-googledrive", resp.user.emailAddress, resp.user.displayName]);
                                $("body").trigger("europass:share:user:logged", ["googledrive"]);
                            }

                            if (resp.code !== undefined && resp.code !== 200)
                                _that.triggerError(resp.code, JSON.stringify(resp));
                            else
                                _that.checkFolderExists(_that.defaultStoreFolderID, 0);

                        } else
                            _that.triggerError(500, "");
                    });

                },

                refreshGapi: function (event) {
                    this.apiManager.loadGapi();
                    this.loaded = true;
                },

                /**
                 * Check if folder exists
                 * PGIA: Construct the query for the request of elements that:
                 * 
                 * 1. Are of type 'folder'               ( mimeType = 'application/vnd.google-apps.folder' ) 
                 * 2. Their name is equal to given param ( title = {folderName} ) 
                 * 3. Are not in trash                   ( trashed = false )
                 * 
                 * Pass the query string as url-encoded
                 * @param folderName
                 */
                checkFolderExists: function (parentId, partIdx) {

                    var folderName = this.cloudStorageFolderParts[partIdx++];

                    var _that = this;
                    var httpResource = new Resource(this.googleApiRequestURL);
                    httpResource.contentType(MediaType.json);
                    httpResource.header("Authorization", "Bearer " + this.accessToken);

                    httpResource._params = {"q": "mimeType = 'application/vnd.google-apps.folder' and title ='" + folderName + "' and '" + parentId + "' in parents and trashed = false"};

                    httpResource._get({
                        async: false,
                        success: {
                            scope: _that,
                            callback: function (response) {
                                if (_.isNull(response) || _.isUndefined(response.items))
                                    return;

                                var items = response.items;
                                if (items.length > 1) {//Google drive supports more than one directories with same name									
                                    //TODO LOC notification error
                                    return;
                                }

                                if (!_.isUndefined(folderName)) {
                                    // If no items were found, then create the folder

                                    if (!_.isArray(items) || _.isEmpty(items))
                                        parentId = _that.createDefaultFolder(parentId, folderName);
                                    else
                                        parentId = items[0].id;
                                    _that.checkFolderExists(parentId, partIdx);
                                } else {

                                    $("body").trigger("europass:share:manage:folders", ["googledrive", parentId]);

                                    _that.checkReadMe(parentId);

                                    var data = {
                                        json: Utils.encodePlusCharPercent(_that.model.conversion().toTransferable()),
                                        folder: parentId,
                                        token: _that.accessToken
                                    };
                                    _that.storeCallback(data);

                                }
                            }
                        },
                        error: {
                            scope: _that,
                            callback: function (status, responseText) {
                                _that.triggerError(status, responseText);
                            }
                        },
                        complete: {
                            scope: _that,
                            callback: function (status, responseText) {
//						$("body").trigger("europass:waiting:indicator:hide");
                            }
                        }
                    });

//			return folderId;
                },

                /**
                 * Function that runs if the default store folder does not exist 
                 * 
                 */
                createDefaultFolder: function (parentId, folderName) {

                    var _that = this;
                    var httpResource = new Resource(this.googleApiRequestURL);
                    httpResource.contentType(MediaType.json);
                    httpResource.header("Authorization", "Bearer " + this.accessToken + "");

                    var data = {"title": folderName, "parents": [{"id": parentId}], "mimeType": "application/vnd.google-apps.folder"};

                    var id;

                    httpResource._post(data, {
                        async: false,
                        success: {
                            scope: _that,
                            callback: function (response) {
                                //console.log("Folder \"" + response.title + "\" (" + response.id + ") created successfully");
                                id = response.id;
                            }
                        },
                        error: {
                            scope: _that,
                            callback: function (status, responseText) {
                                _that.triggerError(status, responseText);
                            }
                        }
                    });

                    return id;
                },

                checkReadMe: function (parentId) {

                    var _that = this;
                    var httpResource = new Resource(this.googleApiRequestURL);
                    httpResource.contentType('text/html');
                    httpResource.header("Authorization", "Bearer " + this.accessToken + "");
                    httpResource._params = {"q": "mimeType != 'application/vnd.google-apps.folder' and title ='" + this.shareReadMe + "' and trashed = false and '" + parentId + "' in parents"};

                    httpResource._get({
                        async: false,
                        success: {
                            scope: _that,
                            callback: function (response) {
                                if (_.isNull(response) || _.isUndefined(response.items))
                                    return;

                                var items = response.items;
                                if (!_.isArray(items) || _.isEmpty(items)) {

//							var content = "PLEASE DO NOT MODIFY THIS FOLDER, NOR ADD OR DELETE FILES, IN ORDER FOR THE SHARE FUNCTIONALITY TO WORK PROPERLY";
                                    var content = GuiLabel["share.cloud.readme.contents"];

                                    var request = _that.insertFile(this.shareReadMe, parentId, 'text/html', content);

                                    request.execute(function (file) {
                                        //console.log("File \"" + file.title + "\" (" + file.id + ") created successfully");
                                    });
                                }

                            }
                        },
                        error: {
                            scope: _that,
                            callback: function (status, responseText) {
                                _that.triggerError(status, responseText);
                            }
                        },
                        complete: {
                            scope: _that,
                            callback: function (status, responseText) {
//						$("body").trigger("europass:waiting:indicator:hide");
                            }
                        }
                    });

//			return folderId;
                },

                /**
                 * function that handles the permissions
                 */

                insertPermission: function (fileId, email) {
                    var body = {
                        'value': email,
                        'type': 'user',
                        'role': 'reader'
                    };
                    var request = gapi.client.drive.permissions.insert({
                        'fileId': fileId,
                        'sendNotificationEmails': false,
                        'resource': body
                    });
                    request.execute(function (resp) {
                        //console.log(resp);
                    });
                },

                /**
                 * Callback to run as soon as a folder is selected from Google Picker
                 * @param data
                 */
                storeCallback: function (fileData) {

                    var url = ServicesUri.document_conversion_to["googledrive_share"];

                    var httpResource = new Resource(url);
                    httpResource.contentType(MediaType.json);

                    _that = this;

                    httpResource._post({
                        data: fileData
                    }, {
                        success: {
                            scope: _that,
                            callback: function (response) {

                                var shareUrl = $(response).filter("meta[name='shareUrlBase']").attr("content");
                                var filename = $(response).filter("meta[name='filename']").attr("content");
//						var jsessionid = $(response).filter("meta[name='jsessionid']").attr("content");
                                var content = $(response).filter("iframe[id='xml-content']");

                                var xmlContent = Utils.htmlUnescape(content.html());

                                xmlContent = xmlContent.replace(/&amp;amp;/g, "&amp;").replace(/--RTE_HTML_ESCAPE_LT--/g, "&lt;");

                                if (_.isUndefined(filename))
                                    filename = "CV_Europass.xml";

                                var request = _that.insertFile(filename, fileData.folder, MediaType.xml, xmlContent);

                                request.execute(function (file) {

                                    _that.currentFileId = file.id;

                                    var editorsUrl = window.location.origin + WindowConfig.getDefaultEwaEditorContext();

                                    var escapedFileDownloadUrl = encodeURIComponent(file.downloadUrl);
                                    shareUrl += "?url=" + escapedFileDownloadUrl + "&token=" + _that.accessToken;// + Session.urlappend();

                                    $("body").trigger("europass:share:link:google", [_that.googleBtnID, editorsUrl + shareUrl]);
                                });
                            }
                        },
                        error: {
                            scope: _that,
                            callback: function (status, responseText) {
                                _that.triggerError(status, responseText);
                            }
                        },
                        complete: {
                            scope: _that,
                            callback: function (status, responseText) {
//						$("body").trigger("europass:waiting:indicator:hide");
                            }
                        }
                    });
                },

                getFile: function (downloadUrl) {

                    var _that = this;
                    var httpResource = new Resource(downloadUrl);
                    httpResource.contentType(MediaType.json);
                    httpResource.header("Authorization", "Bearer " + this.accessToken + "");

                    var contents = "";

                    httpResource._get({
                        async: false,
                        success: {
                            scope: _that,
                            callback: function (response) {
                                contents = response;
                            }
                        },
                        error: {
                            scope: _that,
                            callback: function (status, responseText) {}
                        },
                        complete: {
                            scope: _that,
                            callback: function (status, responseText) {}
                        }
                    });

                    return contents;
                },

                /**
                 * Create the request for the insertion of a new file and its content
                 * 
                 * filename:
                 * folder:
                 * boundary:
                 * content:
                 */

                insertFile: function (filename, folder, contentType, content) {

                    var metadata = {
                        'title': filename,
                        'mimeType': contentType,
                        'parents': [{
                                'kind': 'drive#fileLink',
                                'id': folder
                            }]
                    };

                    var delimiter = "\r\n--" + this.boundary + "\r\n";
                    var close_delim = "\r\n--" + this.boundary + "--";

                    var base64Data = btoa(unescape(encodeURIComponent(content)));

                    var multipartRequestBody = delimiter
                            + 'Content-Type: application/json\r\n\r\n'
                            + JSON.stringify(metadata)
                            + delimiter
                            + 'Content-Type: ' + contentType + '; charset=utf-8\r\n'
                            + 'Content-Transfer-Encoding: base64\r\n'
                            + '\r\n'
                            + base64Data
                            + close_delim;

                    var request = gapi.client.request({
                        'path': '/upload/drive/v2/files',
                        'method': 'POST',
                        'params': {'uploadType': 'multipart'},
                        'headers': {'Content-Type': 'multipart/related; boundary="' + this.boundary + '"'},
                        'body': multipartRequestBody
                    });

                    return request;
                }

                , getFiles: function (event, folderId) {

                    if (this.currentSharedItems.length > 0) {
                        $("body").trigger("europass:share:manage:render", [this.currentSharedItems]);
                        $("body").trigger("europass:waiting:indicator:hide", true);
                        return;
                    }

                    var request = gapi.client.drive.files.list({
                        'fields': 'items(id,createdDate,fileSize,title)',
                        'q': '"' + folderId + '" in parents and title != "' + this.shareReadMe + '" and trashed = false',
                        'orderBy': 'createdDate desc'
                    });

                    var _that = this;
                    request.execute(function (response) {

                        if (!_.isUndefined(response.error) &&
                                !_.isUndefined(response.error.errors) &&
                                !_.isUndefined(response.error.errors[0].reason) &&
                                response.error.errors[0].reason === "keyInvalid" &&
                                !_.isUndefined(response.error.code) &&
                                response.error.code === 400) {

                            $("body").trigger("europass:share:google:refresh");
                            $("body").trigger("europass:share:google:list", folderId);
                        }

                        for (var idx in response.items) {
                            var current = response.items[idx];
                            var revoke = (_that.currentFileId !== current.id ? true : false);

                            var displayName = current.title.replace(/[0-9]{2}_[0-9]{2}_[0-9]{2}/g, function (arg1) {
                                return arg1.replace(/_/g, ":");
                            });

                            _that.currentSharedItems.push({"id": current.id, "name": displayName, "date": current.createdDate, "size": current.fileSize, "canRevoke": revoke});
                        }

                        $("body").trigger("europass:share:manage:render", [_that.currentSharedItems]);
                        $("body").trigger("europass:waiting:indicator:hide", true);

                    });
                }

                , removeFile: function (event, fileId, parent) {

                    var request = gapi.client.drive.files.delete({
                        'fileId': fileId
                    });

                    var _that = this;
                    request.execute(function (resp) {
                        parent.slideToggle("up", function () {
                            _that.updateCurrentSharedItems(fileId);
                            $(this).remove();
                        });
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

                    //Extract the JSON response from the response text
                    var parsed = {};

                    try {
                        if (!_.isUndefined(responseText)) {
                            parsed = JSON.parse(responseText);
                        }

                    } catch (e) {
                        $("body").trigger("europass:waiting:indicator:hide", true);
                    }

                    var errMessage = "";

                    if (!_.isUndefined(parsed.error)) {
                        if (!_.isUndefined(parsed.error.message))
                            errMessage = parsed.error.message;
                    }

                    $("body").trigger("europass:waiting:indicator:show", true);
                    $("body").trigger("europass:share:response:error", [status, errMessage]);
                }
            });
            return GoogleShareView;
        }
);
