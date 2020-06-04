/*
 *
 *  Controller for Google Drive logged in services
 *
 * */

define(
        [
            'jquery',
            'module',
            'underscore',
            'backbone',
            'Utils',
            'routers/SkillsPassportRouterInstance',
            'models/NavigationRoutesInstance',
            'europass/http/Resource',
            'europass/http/MediaType',
            'europass/http/WindowConfigInstance',
            'views/upload/google/ApiManager',
            'i18n!localization/nls/Notification',
            'i18n!localization/nls/GuiLabel',
            'i18n!localization/nls/EditorHelp',
            'HelperManageModelUtils'
        ],
        function ($, module, _, Backbone, Utils, AppRouter, NavigationRoutes, Resource, MediaType, WindowConfig, ApiManager,
                Notification, GuiLabel, EditorHelp, HelperManageModelUtils) {
            var GoogleDriveView = Backbone.View.extend({
                scopes: "https://www.googleapis.com/auth/drive.apps.readonly " + "https://www.googleapis.com/auth/drive.file",
                googleBtnID: "",
                defaultStoreFolderID: 'root',
                accessToken: null,
                isAutoConnect: false,
                isDisconnected: true,
                isRefreshToken: false,
                boundary: "europass-document",
                folderId: "",
                events: {
                    "europass:cloud:connect:googledrive": "connect",
                    "europass:cloud:google:list": "getAndRenderFilesFirstCheckToken",
                    "europass:cloud:google:deleteDocument": "removeFile",
                    "europass:cloud:google:postDeleteDocument": "postDelete",
                    "europass:cloud:google:renameDocument": "renameFile",
                    "europass:cloud:google:cloneDocument": "cloneFile",
                    "europass:cloud:google:deleteToken": "deleteToken",
                    "europass:cloud:google:getContent": "getContent",
                    "europass:cloud:google:updateContent": "checkAndUpdateFileContent",
                    "europass:cloud:google:createDocument": "uploadFile",
                    "europass:cloud:google:shareDocument": "shareDocument",
                    "europass:cloud:google:stopSharingDocument": "stopSharingDocument"
                },
                /**
                 * Initialize
                 * @param options
                 */
                initialize: function (options) {

                    this.appId = WindowConfig.googledriveAppId;
                    this.clientId = WindowConfig.googledriveClientId;
                    this.devKey = WindowConfig.googledriveDevKey;
                    this.clientServiceAccountEmail = WindowConfig.googledriveClientEmail;
                    this.apiManager = null;
                    this.googleApiRequestURL = "https://www.googleapis.com/drive/v2/files";
                    this.googleApiUploadRequestURL = "https://www.googleapis.com/upload/drive/v2/files";
                    this.googleApiUploadMultipartURL = "https://www.googleapis.com/upload/drive/v2/files?uploadType=multipart";

                    // every 50 (60- 10) minutes on get files/save model changes we are doing re-authorisation..
                    this.extraAuthTime = 600 * 1000;
                    this.expiredAtTime = null;
                }

                , connect: function (event, autoconnect) {
                    $("body").trigger("europass:waiting:indicator:cloud:show", true);
                    if (this.apiManager === null) {
                        this.apiManager = new ApiManager({
                            apiKey: this.appId,
                            clientId: this.clientId,
                            scopes: this.scopes,
                            callback: this.apiGDriveManagerCallback,
                            source: "cloud",
                            scope: this
                        });
                    }
                    if (autoconnect) {
                        this.isAutoConnect = true;
                        this.isDisconnected = false; //immediate=true
                    } else {
                        this.isAutoConnect = false;
                    }
                    this.apiManager.loadGapi(this.isDisconnected);
                },
                apiGDriveManagerCallback: function (authResult, isRefreshToken) {
                    //$("iframe[src^='https://accounts.google.com/o/oauth2/auth?client_id=']").remove();
                    this.isRefreshToken = isRefreshToken;

                    // Let s not use for now to avoid any issues ::: Cannot read property 'postMessage' of null
                    // if (isRefreshToken) {
                    // 	$("iframe").last().remove();
                    // }

                    //check if we got an access token
                    if (!_.isNull(authResult) && !_.isUndefined(authResult)
                            && !_.isNull(authResult.access_token) && !_.isUndefined(authResult.access_token)) {
                        this.accessToken = authResult.access_token;
                    } else { //bad response from ApiManager						
                        $("body").trigger("europass:cloud:manage:handle:connection:errors");
                        $("body").trigger("europass:cloud-sign-in:drawer:hide");
                        Utils.triggerErrorWhenCloudLoginAction("", "Invalid Token retrieved", "main");
                        return;
                    }
                    this.cloudStorageFolder = WindowConfig.cloudStorageFolder.split("/");
                    var _that = this;
                    _that.checkFolderExists(_that.defaultStoreFolderID, 0);
                },
                /**
                 * Checks if folder exists
                 * @param {type} parentId
                 * @param {type} partIdx
                 * @returns void
                 */
                checkFolderExists: function (parentId, partIdx) {
                    var folderName = this.cloudStorageFolder[partIdx++];
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
                                    _that.deleteToken();
                                    $("body").trigger("europass:cloud:manage:handle:connection:errors");
                                    $("body").trigger("europass:cloud-sign-in:drawer:hide");
                                    Utils.triggerErrorWhenCloudLoginAction("", "More than one " + folderName + " directories", "main");
                                    return;
                                }

                                if (!_.isUndefined(folderName)) {
                                    // If no items were found, then create the folder
                                    if (!_.isArray(items) || _.isEmpty(items)) {
                                        parentId = _that.createDefaultFolder(parentId, folderName);
                                    } else { //required folder exists										
                                        parentId = items[0].id;
                                    }
                                    _that.checkFolderExists(parentId, partIdx);
                                } else {
                                    if (partIdx === 3) {//connected
                                        _that.isDisconnected = false;
                                        _that.folderId = parentId;
                                        $("body").trigger("europass:cloud:manage:folders", ["googledrive", _that.folderId]);

                                        this.getAccountInfo();
                                        //folder check completed
                                        _that.onConnectTriggers();
                                    }
                                }
                            }
                        },
                        error: {
                            scope: _that,
                            callback: function (status, responseText) {

                                _that.deleteToken();
                                $("body").trigger("europass:cloud:manage:handle:connection:errors");
                                $("body").trigger("europass:cloud-sign-in:drawer:hide");
                                Utils.triggerErrorWhenCloudLoginAction(status, responseText, "main");
                            }
                        }
                    });
                },

                getAccountInfo: function () {
                    var _that = this;
                    var httpResourceInfo = new Resource('https://www.googleapis.com/drive/v2/about');
                    httpResourceInfo.contentType(MediaType.json);
                    httpResourceInfo.header("Authorization", "Bearer " + this.accessToken);
                    httpResourceInfo._get({
                        success: {
                            scope: _that,
                            callback: function (response) {
                                $("#top-ui-cloud-sign-in-section").data('emailAccount', response.user.emailAddress);
                                $("#top-ui-cloud-sign-in-section").data('displayName', response.user.displayName);
                            }
                        },
                        error: {
                            scope: _that,
                            callback: function (status, responseText) {
                                $("#top-ui-cloud-sign-in-section").data('emailAccount', '');
                                $("#top-ui-cloud-sign-in-section").data('displayName', '');
                            }
                        },
                        complete: {
                            scope: _that,
                            callback: function (status, responseText) {
                                var cookieShareReviewPostbackValue = Utils.readCookieByName("share-review-postback");
                                if (cookieShareReviewPostbackValue) {
                                    Utils.deleteCookieByName("share-review-postback");
                                    $(".current-cloud-connected").click();
                                }
                            }
                        }
                    });
                },

                /**
                 * triggers events for closing the drawer + hide waiting indicator
                 * @param {type} parentId
                 * @param {type} folderName
                 * @returns void
                 */
                onConnectTriggers: function () {
                    $("body").trigger("europass:waiting:indicator:cloud:hide");
                    if (!this.isAutoConnect) {
                        $("body").trigger("europass:cloud-sign-in:drawer:hide");
                    }
                    if (!this.isRefreshToken) {
                        $("#top-ui-cloud-sign-in").hide();
                        $("#top-ui-cloud-connected").find('.cloud-connected-icon').addClass("googledriveIcon");
                        $(".top-manage-documents-section").find("small").addClass("image-googledrive");
                        $(".top-manage-documents-section").find("#connected-to-service").text(GuiLabel["skillspassport.import.cloud.googledrive"]);
                        $("#MyFilesListForm .myFilesList-info").removeClass("onedrive").addClass("googledrive");
                        this.handleCvLoadCases();
                        $("body").trigger("europass:cloud:manage:show:current:loaded");
                        $("body").trigger("europass:cloud:manage:use:local:storage", true);
                    }
                    this.expiredAtTime = gapi.auth2.getAuthInstance().currentUser.get().getAuthResponse().expires_at;
                    var url = NavigationRoutes.findActiveRoute().replace(".", "/");
                    if (AppRouter !== undefined) {
                        AppRouter.navigate(url + '/cloud', {
                            trigger: false,
                            replace: !("pushState" in window.history) //To update the URL without creating an entry in the browser's history, set the replace option to true. 
                        });
                    }

                },
                /**
                 * function to handle all cv automatic load scenarios
                 */
                handleCvLoadCases: function () {
                    //check if there is something on local storage/model					
                    var existingModelObj = JSON.parse(this.model.conversion().toTransferable());
                    if (this.folderId !== "") {
                        var myFiles = this.getFiles(this.folderId);
                        if (Utils.isSkillsPassportObjectEmpty(existingModelObj)) {//no cv locally - US2 and US3 #EPAS-517
                            if (myFiles.length > 0) {//cloud files exist US3
                                if (!_.isUndefined(localStorage.LoadedFile) && JSON.parse(localStorage.LoadedFile).locale !== ewaLocale) { //change of locale
                                    $("body").trigger("europass:cloud:manage:model:change");
                                } else {
                                    //get most recent cv - files are ordered by modified date, so 1st in array is the most recent
                                    var mostRecentCV = myFiles[0];
                                    var contentToLoad = this.getContent(null, mostRecentCV.source);
                                    if (Utils.getObjectType(contentToLoad) !== null && !_.isUndefined(contentToLoad.SkillsPassport) && !_.isUndefined(contentToLoad.SkillsPassport.DocumentInfo)) {
                                        $("body").trigger("europass:cloud:manage:load:store:content:current", [contentToLoad, mostRecentCV.id, Utils.getFilenameTitle(mostRecentCV.filename), mostRecentCV.lastUpdate]);
                                    } else {
                                        Utils.triggerErrorWhenCloudLoginAction("status", "error on retrieving content from Google Drive", "main");
                                    }
                                }
                            } else {//US2
                                //upload new blank document
                                $("body").trigger("europass:cloud:manage:load:uploadCurrentDocument", [, true]);
                            }
                        } else {//cv exists locally - US1 and US4 #EPAS-517
                            if (myFiles.length > 0) {//US4
                                var mostRecentCV = myFiles[0];
                                var contentToLoad = this.getContent(null, mostRecentCV.source);
                                this.checkMatchingCV(myFiles, contentToLoad, existingModelObj);
                            } else {//US1
                                $("body").trigger("europass:cloud:manage:load:uploadCurrentDocument", [existingModelObj, true]);
                            }
                        }
                    } else {//error on authentication
                        Utils.triggerErrorWhenCloudLoginAction("status", "error on retrieving folder", "main");
                    }
                },
                /**
                 * Function that performs a matching control based on lastUpdate between cloud files
                 * and existing file (local)
                 * @param {Array} myFiles
                 * @param {JSON} existingModelObj
                 * @returns {undefined}
                 */
                checkMatchingCV: function (myFiles, mostRecentCVContent, existingModelObj) {
                    var localLastUpdateDate;
                    if (!_.isUndefined(localStorage.LoadedFile)) {
                        localLastUpdateDate = JSON.parse(localStorage.LoadedFile).lastUpdate;
                    } else {
                        localLastUpdateDate = existingModelObj.SkillsPassport.DocumentInfo.LastUpdateDate;
                    }
                    var selectedCloudFile = null;
                    myFiles.some(function (myFile) {
                        if (myFile.lastUpdate === localLastUpdateDate) {
                            selectedCloudFile = myFile;
                            return true;
                        }
                    });
                    if (!_.isNull(selectedCloudFile)) { //match
                        //identify if locale change has occurred												
                        if (selectedCloudFile.locale.toLowerCase() !== ewaLocale) { //Locale change						 
                            $("body").trigger("europass:cloud:manage:model:change");
                        }

                    } else {
                        if (Utils.getObjectType(mostRecentCVContent) !== null && !_.isUndefined(mostRecentCVContent.SkillsPassport) && !_.isUndefined(mostRecentCVContent.SkillsPassport.DocumentInfo)) {
                            var mostRecentCVObj = {"recentData": myFiles[0], "recentDataContent": mostRecentCVContent};
                            $("body").trigger("europass:cloud:manage:modal:confirmation:load", mostRecentCVObj);
                        } else {
                            Utils.triggerErrorWhenCloudLoginAction("status", "error on retrieving content from Google Drive", "main");
                        }
                        $("body").trigger("europass:waiting:indicator:cloud:hide", true);
                    }
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
                                id = response.id;
                            }
                        },
                        error: {
                            scope: _that,
                            callback: function (status, responseText) {
                                _that.deleteToken();
                                $("body").trigger("europass:cloud:manage:handle:connection:errors");
                                $("body").trigger("europass:cloud-sign-in:drawer:hide");
                                Utils.triggerErrorWhenCloudLoginAction(status, responseText, "main");
                            }
                        },
                    });
                    return id;
                },
                /**
                 * Function to retrieve filenames of the given Gdrive folder 
                 * and returns the files array
                 * @param {String} folderId
                 * @returns array with files
                 */
                getFiles: function (folderId) {
                    var _that = this;
                    var myFiles = [];
                    var paramq = encodeURIComponent('"' + folderId + '" in parents and trashed=false');
                    var paramOrder = encodeURIComponent('modifiedDate desc');
                    var paramFields = encodeURIComponent('items(id,modifiedDate,title,downloadUrl,properties)');
                    var getFilesUrl = this.googleApiRequestURL + "?q=" + paramq + "&orderBy=" + paramOrder + "&fields=" + paramFields;
                    var httpResource = new Resource(getFilesUrl);
                    httpResource.header("Authorization", "Bearer " + this.accessToken + "");
                    httpResource._get({
                        async: false,
                        success: {
                            scope: _that,
                            callback: function (response) {
                                for (var idx in response.items) {
                                    var current = response.items[idx];
                                    var originalFilename = current.title;
                                    var locale = "", lastUpdate = "", filetypes = "", filetypeText, permissionId = '',
                                            clonedocumentid = '', sharedemail = '', isShared = false, isEnabled = true;
                                    if (current.properties) {
                                        current.properties.forEach(function (property) {
                                            if (property.key === "myCustomProperties") {
                                                var myCustomProperty = JSON.parse(property.value);
                                                locale = myCustomProperty.locale.toUpperCase();
                                                lastUpdate = myCustomProperty.lastUpdate;
                                                filetypes = myCustomProperty.filetypes;
                                                filetypeText = "";
                                                $.each(filetypes, function (i, type) {
                                                    if (i !== filetypes.length - 1) {
                                                        filetypeText += type + ", ";
                                                    } else {
                                                        filetypeText += type;
                                                    }
                                                });
                                            }
                                            if (property.key === "myCustomSharingProperties") {
                                                var myCustomProperty = JSON.parse(property.value);
                                                permissionId = myCustomProperty.prm;
                                                clonedocumentid = myCustomProperty.shrid;

                                            }
                                            if (property.key === "sharingPropertiesEmail") {
                                                var myCustomProperty = JSON.parse(property.value);
                                                sharedemail = myCustomProperty.email;
                                            }
                                            if (property.key === "sharingFlagProperty") {
                                                var prop = JSON.parse(property.value);
                                                isShared = prop.sharingFlag;
                                            }
                                            if (property.key === "sharingDocument") {
                                                var prop = JSON.parse(property.value);
                                                isEnabled = prop.enabledWhenSharing;
                                            }
                                        });
                                    }
                                    //validation check 1. json type, 2. Europass json
                                    var suffix = current.title.split('.').pop();
                                    if (suffix !== 'json') {
                                        continue;
                                    } else {
                                        // Shared document not reviewed!!
                                        if (isEnabled == false) {
                                            continue;
                                        }
                                        if (_.isEmpty(locale) && _.isEmpty(lastUpdate) && _.isEmpty(filetypes)) {
                                            continue;
                                        }
                                    }
                                    var displayName = originalFilename;
                                    //extract suffix
                                    displayName = displayName.substr(0, displayName.lastIndexOf('.')) || displayName;
                                    myFiles.push({"id": current.id, "name": displayName,
                                        "date": new XDate(current.modifiedDate).toString('dd MMMM yyyy, HH:mm:ss'),
                                        "size": "", "source": current.downloadUrl, "filename": originalFilename,
                                        "locale": locale, "lastUpdate": lastUpdate, "filetypes": filetypeText,
                                        "permissionId": permissionId,
                                        "clonedocumentid": clonedocumentid,
                                        "sharedEmailWithText": Utils.replaceKey(GuiLabel["cloudLogin.drawer.connected.document.share.post.message.action.title"], "--reviewerEmail--", sharedemail),
                                        "shared": isShared});
                                }

                                // EXTRA CHECK FOR SHARED DOCUMENT THAT HAS BEEN REVOKED & OVERRIDING !!!!
                                // WE ARE NOT SHOWING ORIGINAL DOCUMENTS THAT HAVE BEEN REVOKED ON CloudShareReviewUploadServlet
                                for (var i = 0; i < myFiles.length; i++) {
                                    for (var j = 0; j < myFiles.length; j++) {
                                        if (i == j)
                                            continue;
                                        if (myFiles[i].clonedocumentid === myFiles[j].id) {
                                            myFiles[i].shared = false;
                                            _that.updateSharedFlagProperty(myFiles[i].id, false);
                                            break;
                                        }
                                    }
                                }
                            }
                        },
                        error: {
                            scope: _that,
                            callback: function (status, responseText) {
                                if (status === 0 && _.isUndefined(responseText)) { //network error
                                    responseText = "Network connection error";
                                    $("body").trigger("europass:cloud:manage:disconnect");
                                }
                                Utils.triggerErrorWhenCloudLoginAction(status, responseText, "main");
                            }
                        }
                    });
                    return myFiles;
                },

                getAndRenderFilesFirstCheckToken: function (event, folderId) {
                    var _that = this;

                    // TODO REFACTOR USING CALLBACKS
                    var currentTime = (new Date()).getTime();
                    if (currentTime > _that.expiredAtTime - _that.extraAuthTime) {
                        $("body").trigger("europass:waiting:indicator:cloud:show", true);
                        //console.log('during get and render files...');
                        var relAuthRespObj = gapi.auth2.getAuthInstance().currentUser.get().reloadAuthResponse();
                        relAuthRespObj.then(function (authResult) {
                            _that.apiManager.setAccessToken(authResult);
                            _that.accessToken = authResult.access_token;
                            //console.log('authresult when getAndRenderFilesFirstCheckToken');
                            //console.log(authResult);
                        })
                    }
                    _that.getAndRenderFiles(event, folderId);
                },

                /**
                 * Function that gets the files and renders them on drawer
                 * @param {type} event
                 * @param {String} folderId
                 * @returns void
                 */
                getAndRenderFiles: function (event, folderId) {
                    $("body").trigger("europass:cloud:manage:handle:response:success", true);
                    // get files
                    var currentFileItems = this.getFiles(folderId);
                    if (!_.isUndefined(currentFileItems) && currentFileItems.length > 0) {
                        $("body").trigger("europass:cloud:manage:show:files:drawer");
                    }
                    $("body").trigger("europass:cloud:manage:render", [currentFileItems, true]);
                },
                /**
                 * Function to remove selected file from Google Drive
                 * + remove respective dom element
                 * @returns void
                 */
                removeFile: function (event, fileId, parentElem) {
                    var request = gapi.client.drive.files.delete({
                        'fileId': fileId
                    });
                    var _that = this;

                    request.execute(function (resp) {
                        if (!_.isUndefined(resp.error) &&
                                !_.isUndefined(resp.error.code) &&
                                !_.isUndefined(resp.error.message)) {

                            Utils.triggerErrorWhenCloudLoginAction(resp.error.code, resp.error.message, "drawer");
                            return;
                        }

                        if (parentElem) {
                            $("body").trigger("europass:cloud:manage:delete:fileInDom", [parentElem, fileId]);
                            $("body").trigger("europass:cloud:manage:handle:response:success", true);
                        }
                    });
                },
                /**
                 * Post delete action after current loaded document is deleted.
                 *
                 */
                postDelete: function (event, nextState, folderID, defaultFilename, lastSelElem) {
                    if (nextState === 'createNewAfterDelete') {
                        //console.log('create new blank document ');
                        this.uploadFile(event, defaultFilename, folderID, undefined, true);
                    } else if (nextState === 'loadLatestAfterDelete') {
                        //console.log('load latest modified document after delete...');
                        var source = $(lastSelElem).data("source-url");
                        var id = $(lastSelElem).data("document-id");
                        var filename = $(lastSelElem).data("filename");
                        var lastUpdate = $(lastSelElem).data("last-modified");
                        var contentToLoad = this.getContent(null, source);
                        if (Utils.getObjectType(contentToLoad) !== null && !_.isUndefined(contentToLoad.SkillsPassport) && !_.isUndefined(contentToLoad.SkillsPassport.DocumentInfo)) {
                            $("body").trigger("europass:cloud:manage:load:store:content:current", [contentToLoad, id, Utils.getFilenameTitle(filename), lastUpdate]);
                        } else {
                            //TODO LOC
                        }
                    }
                    $("body").trigger("europass:waiting:indicator:cloud:hide", true);
                },
                /**
                 * Rename a file.
                 *
                 * @param {String} fileId ID of the file to rename
                 * @param {String} New title for the file
                 */
                renameFile: function (event, fileId, filename, oldFilename, elem, loaded) {
                    var allElements = $('.manage-document-container').not($(elem)
                            .closest('.manage-document-container')).find('.radio-cloud-documents');
                    var foundName = false;
                    $(allElements).each(function () {
                        if (filename === $(this).data('filename')) {
                            $("body").trigger("europass:cloud:manage:rename:error", [oldFilename, elem]);

                            var customResponseText = {"error": {"code": "nameAlreadyExists", "message": EditorHelp["cloudLogin.errors.service.onedrive.rename.samefilename"]}};
                            Utils.triggerErrorWhenCloudLoginAction(409, JSON.stringify(customResponseText), "drawer");
                            foundName = true;
                            return false;
                        }
                    });

                    if (foundName == false) {

                        var body = {'title': filename};
                        var request = gapi.client.drive.files.patch({
                            'fileId': fileId,
                            'resource': body
                        });
                        request.execute(function (resp) {
                            if (!_.isUndefined(resp.error) &&
                                    !_.isUndefined(resp.error.code) &&
                                    !_.isUndefined(resp.error.message)) {
                                $("body").trigger("europass:cloud:manage:rename:error", [oldFilename, elem]);
                                Utils.triggerErrorWhenCloudLoginAction(resp.error.code, resp.error.message, "drawer");
                                return;
                            }
                            $("body").trigger("europass:cloud:manage:handle:response:success", true);
                            $(elem).closest('.manage-document-container').find('.radio-cloud-documents').attr('data-filename', filename);
                            if (loaded === true) {
                                $("body").trigger("europass:cloud:manage:update:content:current", [{"title": Utils.getFilenameTitle(filename)}, true]);
                            }
                            $("body").trigger("europass:waiting:indicator:cloud:hide", true);
                        });
                    }
                },
                /**
                 * Clone an existing file.
                 *
                 * @param {String} originFileId ID of the origin file to copy.
                 * @param {String} copyTitle Title of the copy.
                 */
                cloneFile: function (event, originFileId, copyTitle, parentElem, cloneDuringSharing) {
                    var body = {'title': copyTitle};
                    var request = gapi.client.drive.files.copy({
                        'fileId': originFileId,
                        'resource': body
                    });
                    var _that = this;
                    request.execute(function (resp) {
                        if (!_.isUndefined(resp.error) &&
                                !_.isUndefined(resp.error.code) &&
                                !_.isUndefined(resp.error.message)) {
                            Utils.triggerErrorWhenCloudLoginAction(resp.error.code, resp.error.message, "drawer");
                            return;
                        }

                        var locale = $(parentElem).find('.document-metadata .cvLocale').text().trim();
                        var filetypes = parentElem.find('.document-metadata .filetypes').text().replace(/ /g, '').split(",");
                        var email = $(parentElem).find('.share-cloud-recipient-email-input').val().trim();

                        if (!cloneDuringSharing) {
                            var metadataCustomProperties = {"locale": locale, "lastUpdate": resp.modifiedDate, "filetypes": filetypes};
                            _that.updateFileProperties(resp.id, metadataCustomProperties, false, undefined, true);
                            $("body").trigger("europass:cloud:manage:clone:file:DOMelement", [resp.id, resp.title, new XDate(resp.modifiedDate).toString('dd MMMM yyyy, HH:mm:ss'),
                                resp.downloadUrl, parentElem]);
                            $("body").trigger("europass:cloud:manage:handle:response:success", true);
                        }

                        // During Sharing we are also cloning document so to actually share the copy!!
                        // TIMEOUTS TO AVOID ERRORS ON GOOGLE DRIVE API FOR MULTIPLE PROPERTY INSERT REQUESTS!!
                        else {
                            $("body").trigger("europass:waiting:indicator:cloud:show", true);
                            _that.updateSharedFlagProperty(originFileId, true);
                            setTimeout(function () {
                                _that.updateSharingIsEnabledDocument(resp.id, false);
                                setTimeout(function () {
                                    _that.postCloneSharingAction(event, resp.id, originFileId, email, parentElem);
                                    setTimeout(function () {
                                        var metadataCustomProperties = {"locale": locale, "lastUpdate": resp.modifiedDate, "filetypes": filetypes};
                                        _that.updateFileProperties(resp.id, metadataCustomProperties, false, undefined, true);
                                    }, 3000);
                                }, 3000);
                            }, 3000);
                        }
                    });
                },
                /**
                 * Function that deletes accessToken both
                 * on GoogleDriveView as well as in ApiManager
                 * @returns void
                 */
                deleteToken: function () {
                    this.accessToken = null;
                    this.isDisconnected = true;
                    this.isRefreshToken = false;
                    this.apiManager.clearTokenTimeout();
                },
                /**
                 * Function that gets content from the downloadUrl of the selected file from Google Drive "My files"
                 * and triggers loadContent of ManageDocumentsView
                 * @param {type} event
                 * @param {type} downloadUrl
                 * @param {type} currentSelected
                 * @param {type} currentSelectedName
                 * @returns void
                 */
                getContent: function (event, downloadUrl, currentSelectedElem) {
                    var _that = this;
                    if (!_.isNull(downloadUrl) && !_.isNull(this.accessToken)
                            && !_.isUndefined(downloadUrl) && !_.isUndefined(this.accessToken)) {
                        var content;
                        var xhr = new XMLHttpRequest();
                        xhr.open('GET', downloadUrl, false);
                        xhr.setRequestHeader('Authorization', 'Bearer ' + this.accessToken);
                        xhr.onload = function () {
                            var jsonToImport = HelperManageModelUtils.syncPersonNameOrder(JSON.parse(xhr.responseText));
                            if (currentSelectedElem) {
                                $("body").trigger("europass:cloud:manage:load:content:select", [jsonToImport, currentSelectedElem]);
                            } else {
                                content = jsonToImport;
                            }
                        };
                        xhr.onerror = function () {
                            Utils.triggerErrorWhenCloudLoginAction("status", xhr.responseText, "drawer");
                            return;
                        };
                        xhr.send();
                        return content;
                    } else {
                        Utils.triggerErrorWhenCloudLoginAction("status", "errormessage", "drawer");
                    }
                }

                /**
                 * Function to update the file's content
                 * @param {type} fileId
                 * @param {type} folderId
                 * @param {type} json
                 * @param {type} title
                 * @param {type} erase
                 * @returns {undefined}
                 */
                , updateFileContent: function (fileId, folderId, json, title, erase) {
                    var _that = this;
                    var metadata = {
                        'mimeType': 'application/json',
                        'parents': [{
                                'kind': 'drive#fileLink',
                                'id': folderId
                            }]
                    };
                    if (!_.isUndefined(title)) {
                        metadata.title = title;
                    }
                    var delimiter = "\r\n--" + this.boundary + "\r\n";
                    var close_delim = "\r\n--" + this.boundary + "--";

                    if (_.isUndefined(json)) {
                        json = JSON.parse(Utils.getEmptySkillsPassportObject(module.config().locale, module.config().xsdversion,
                                module.config().generator, module.config().comment, module.config().europassLogo));
                    }
                    var locale = json.SkillsPassport.Locale;
                    var lastModifiedDate = json.SkillsPassport.DocumentInfo.LastUpdateDate;

                    var base64Data = btoa(unescape(encodeURIComponent(JSON.stringify(json))));
                    var multipartRequestBody = delimiter
                            + 'Content-Type: application/json\r\n\r\n'
                            + JSON.stringify(metadata)
                            + delimiter
                            + 'Content-Type: application/json; charset=utf-8\r\n'
                            + 'Content-Transfer-Encoding: base64\r\n'
                            + '\r\n'
                            + base64Data
                            + close_delim;

                    var request = gapi.client.request({
                        'path': '/upload/drive/v2/files/' + fileId,
                        'method': 'PUT',
                        'params': {'uploadType': 'multipart', 'alt': 'json'},
                        'headers': {'Content-Type': 'multipart/mixed; boundary="' + this.boundary + '"'},
                        'body': multipartRequestBody
                    });

                    var callback = function (file) {
                        if (!_.isUndefined(file.error) && !_.isNull(file.error)) {
                            if (!_.isUndefined(file.error.code) && !_.isUndefined(file.error.message)) {
                                Utils.triggerErrorWhenCloudLoginAction(file.error.code, file.error.message, "main");
                            }
                        } else {//success																
                            //update custom properties
                            var metadataCustomProperties = {
                                "locale": locale,
                                "lastUpdate": lastModifiedDate,
                                "filetypes": Utils.checkModelInfoTypesNonEmpty(_that.model.info()).filetypes
                            };

                            if (!_.isUndefined(title)) {
                                _that.updateFileProperties(fileId, metadataCustomProperties, false, Utils.getFilenameTitle(title), false);
                            } else {
                                _that.updateFileProperties(fileId, metadataCustomProperties, false, undefined, false);
                            }
                        }
                    };

                    // TODO REFACTOR USING CALLBACKS
                    var currentTime = (new Date()).getTime();
                    if (currentTime > _that.expiredAtTime - _that.extraAuthTime) {
                        $("body").trigger("europass:waiting:indicator:cloud:show", true);
                        //console.log('during save model changes...');
                        var relAuthRespObj = gapi.auth2.getAuthInstance().currentUser.get().reloadAuthResponse();
                        relAuthRespObj.then(function (authResult) {
                            _that.apiManager.setAccessToken(authResult);
                            _that.accessToken = authResult.access_token;
                            //console.log('authresult when updateFileContent');
                            //console.log(authResult);
                        })
                    } else {
                        request.execute(callback);
                    }
                }
                /**
                 * Function to check if the file to update is trashed and then update its content
                 * @param {String} fileId
                 * @param {String} json
                 * @returns void
                 */
                , checkAndUpdateFileContent: function (event, fileId, folderId, json, title, erase) {
                    var _that = this;
                    if (typeof gapi.client.drive === 'undefined') {
                        gapi.client.setApiKey(null);
                        gapi.client.load('drive', 'v2', function () {
                            _that.checkFileContent(fileId, folderId, json, title, erase);
                        });
                    } else {
                        _that.checkFileContent(fileId, folderId, json, title, erase);
                    }
                }

                , checkFileContent: function (fileId, folderId, json, title, erase) {
                    var _that = this;
                    var request = gapi.client.drive.files.get({
                        'fileId': fileId
                    });
                    if (Utils.isHostReachable()) {
                        request.execute(function (resp) {
                            if (resp.labels && resp.labels.trashed) {
                                Utils.triggerErrorWhenCloudLoginAction("error", "Cannot update content", "main");
                            } else {
                                _that.updateFileContent(fileId, folderId, json, title, erase);
                            }
                        });
                    } else {//network error
                        $("body").trigger("europass:cloud:manage:disconnect");
                        Utils.triggerErrorWhenCloudLoginAction("error", "Network connection error", "main");
                    }
                }

                /**
                 * This function sets custom properties of locale and lastModifiedTime
                 * @param {type} fileId
                 * @returns void
                 */
                , updateFileProperties: function (fileId, metadataCustomProperties, isNewFile, title, notChangeLocalStorageLoadedFile) {
                    var prop = Utils.setGDriveProperty("myCustomProperties", JSON.stringify(metadataCustomProperties), "PRIVATE");
                    var errorMessageLocation = (isNewFile) ? "drawer" : "main";
                    var updateRequest = gapi.client.drive.properties.insert({
                        'fileId': fileId,
                        'resource': prop
                    });
                    var callback = function (file) {
                        if (_.isUndefined(file.error) || _.isNull(file.error)) {
                            if (!isNewFile) {
                                if (!_.isUndefined(title)) {
                                    $("body").trigger("europass:cloud:manage:update:content:current",
                                            [{"lastUpdate": metadataCustomProperties.lastUpdate, "title": title, "locale": metadataCustomProperties.locale}, true]);
                                } else {
                                    if (!notChangeLocalStorageLoadedFile) {
                                        $("body").trigger("europass:cloud:manage:update:content:current",
                                                [{"lastUpdate": metadataCustomProperties.lastUpdate, "locale": metadataCustomProperties.locale}, true]);
                                    }
                                }
                            }
                        } else {
                            Utils.triggerErrorWhenCloudLoginAction("status", file.error.message, errorMessageLocation);
                        }
                    };

                    updateRequest.execute(callback);
                    $("body").trigger("europass:waiting:indicator:cloud:hide", true);
                }

                , updateSharedFlagPropertyAction: function (origFileId, flag) {
                    var prop = Utils.setGDriveProperty("sharingFlagProperty", JSON.stringify({"sharingFlag": flag}));
                    var updateRequest = gapi.client.drive.properties.insert({
                        'fileId': origFileId,
                        'resource': prop
                    });
                    var callback = function (resp) {
                        if (_.isUndefined(resp.error) || _.isNull(resp.error)) {
                            //
                        } else {
                            Utils.triggerErrorWhenCloudLoginAction("status", resp.error.message, "main");
                        }
                    };
                    updateRequest.execute(callback);
                }

                , updateSharedFlagProperty: function (origFileId, flag) {
                    var _that = this;
                    if (typeof gapi.client.drive === 'undefined') {
                        gapi.client.setApiKey(null);
                        gapi.client.load('drive', 'v2', function () {
                            _that.updateSharedFlagPropertyAction(origFileId, flag);
                        });
                    } else {
                        _that.updateSharedFlagPropertyAction(origFileId, flag);
                    }
                }

                , updateSharingIsEnabledDocument: function (clonefileID, isEnabledDocument) {

                    var prop = Utils.setGDriveProperty("sharingDocument", JSON.stringify({"enabledWhenSharing": isEnabledDocument}), "PUBLIC");
                    var updateRequest = gapi.client.drive.properties.insert({
                        'fileId': clonefileID,
                        'resource': prop
                    });
                    var callback = function (resp) {
                        if (_.isUndefined(resp.error) || _.isNull(resp.error)) {
                            //
                        } else {
                            Utils.triggerErrorWhenCloudLoginAction("status", resp.error.message, "main");
                        }
                    };
                    updateRequest.execute(callback);
                }

                , updatePropertiesForSharingEmail: function (fileId, metadataPropertiesEmail) {

                    var prop = Utils.setGDriveProperty("sharingPropertiesEmail", JSON.stringify(metadataPropertiesEmail));
                    var req = gapi.client.drive.properties.insert({
                        'fileId': fileId,
                        'resource': prop
                    });
                    var callback = function (resp) {
                        if (_.isUndefined(resp.error) || _.isNull(resp.error)) {
                            //
                        } else {
                            // error
                            Utils.triggerErrorWhenCloudLoginAction("status", resp.error.message, "main");
                        }
                    };
                    req.execute(callback);
                }

                , updatePropertiesForSharing: function (fileId, metadataCustomProperties, metadataPropertiesEmail) {
                    var _that = this;
                    var prop = Utils.setGDriveProperty("myCustomSharingProperties", JSON.stringify(metadataCustomProperties));
                    var updateRequest = gapi.client.drive.properties.insert({
                        'fileId': fileId,
                        'resource': prop
                    });
                    var callback = function (resp) {
                        if (_.isUndefined(resp.error) || _.isNull(resp.error)) {
                            // Set also custom properties for email !!!
                            _that.updatePropertiesForSharingEmail(fileId, metadataPropertiesEmail);
                        } else {
                            Utils.triggerErrorWhenCloudLoginAction("status", resp.error.message, "main");
                        }
                        $("body").trigger("europass:waiting:indicator:cloud:hide", true);
                    };
                    updateRequest.execute(callback);
                }

                /**
                 * Function that creates a new file in Google Drive
                 * @param {type} event
                 * @param {type} defaultFilename
                 * @returns {undefined}
                 */
                , uploadFile: function (event, defaultFilename, folderId, json, autoLoad) {
                    var _that = this;
                    var metadata = {
                        'title': defaultFilename,
                        'mimeType': 'application/json',
                        'parents': [{
                                'kind': 'drive#fileLink',
                                'id': folderId
                            }]
                    };
                    var jsonToPost = {};
                    this.jsonIsEmptyCV = false;
                    if (_.isUndefined(json)) { //blank document
                        jsonToPost = JSON.parse(Utils.getEmptySkillsPassportObject(module.config().locale, module.config().xsdversion,
                                module.config().generator, module.config().comment, module.config().europassLogo));
                        this.jsonIsEmptyCV = true;
                    } else {
                        jsonToPost = json;
                    }
                    this.jsonTemp = jsonToPost;

                    var lastUpdate = jsonToPost.SkillsPassport.DocumentInfo.LastUpdateDate;
                    var delimiter = "\r\n--" + this.boundary + "\r\n";
                    var close_delim = "\r\n--" + this.boundary + "--";
                    var base64Data = btoa(unescape(encodeURIComponent(JSON.stringify(jsonToPost))));

                    var multipartRequestBody = delimiter
                            + 'Content-Type: application/json\r\n\r\n'
                            + JSON.stringify(metadata)
                            + delimiter
                            + 'Content-Type: application/json; charset=utf-8\r\n'
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


                    var callback = function (file) {
                        if (!_.isUndefined(file.error) && !_.isNull(file.error)) {
                            if (!_.isUndefined(file.error.code) && !_.isUndefined(file.error.message)) {
                                Utils.triggerErrorWhenCloudLoginAction(file.error.code, file.error.message, "drawer");
                                $("body").trigger("europass:waiting:indicator:cloud:hide", true);
                            }
                        } else {
                            var filetypes = [];
                            if (_that.jsonIsEmptyCV !== true) {
                                filetypes = Utils.checkModelInfoTypesNonEmpty(_that.model.info()).filetypes;
                            }
                            var metadataCustomProperties = {"locale": module.config().locale, "lastUpdate": lastUpdate, "filetypes": filetypes};
                            _that.updateFileProperties(file.id, metadataCustomProperties, true);

                            var currentDocuments = [];
                            _that.addNewBlankDocumentInDom(file, currentDocuments, module.config().locale, lastUpdate);
                            $("body").trigger("europass:cloud:manage:render", [currentDocuments, false]);
                            if (autoLoad) {
                                $("body").trigger("europass:cloud:manage:load:store:content:current", [_that.jsonTemp, file.id, Utils.getFilenameTitle(file.title), lastUpdate]);
                            }
                        }
                    };

                    request.execute(callback);

                }

                , addNewBlankDocumentInDom: function (file, currentDocuments, locale, lastUpdate) {
                    var displayName = Utils.getFilenameTitle(file.title) || file.title;

                    currentDocuments.push({"id": file.id, "name": displayName,
                        "date": new XDate(file.modifiedDate).toString('dd MMMM yyyy, HH:mm:ss'),
                        "size": "", "source": file.downloadUrl, "filename": file.title,
                        "locale": locale.toUpperCase(), "lastUpdate": lastUpdate,
                        "shared": false});
                }

                , shareDocument: function (event, target, fileId) {
                    var newfilename = target.closest(".manage-document-tile").find(".cloud-document-filename").html() + "-" + (new Date()).getTime() + ".json";
                    this.cloneFile(event, fileId, newfilename, target.closest(".manage-document-tile"), true);
                }

                , postCloneSharingAction: function (event, fileId, originalFileId, email, target) {
                    var _that = this;
                    var bodyPermission = {
                        'type': 'user',
                        'role': 'writer',
                        'value': _that.clientServiceAccountEmail
                    };
                    var request = gapi.client.drive.permissions.insert({
                        'fileId': fileId,
                        'resource': bodyPermission
                    });
                    var callback = function (resp) {
                        if (!_.isUndefined(resp.error) && !_.isNull(resp.error) &&
                                !_.isUndefined(resp.error.code) && !_.isUndefined(resp.error.message)) {

                            Utils.triggerErrorWhenCloudLoginAction(resp.error.code, resp.error.message, "drawer");
                        } else {
                            _that.updatePropertiesForSharing(originalFileId, _that.getMetadataForSharing(resp.id, fileId),
                                    _that.getMetadataForSharingEmail(email));
                            $("body").trigger("europass:cloud:manage:share:postAction", [target, resp.id, fileId]);
                        }
                    };
                    request.execute(callback);
                }

                , stopSharingDocument: function (event, target, fileId, permissionId, originalFileId) {
                    var _that = this;
                    var request = gapi.client.drive.permissions.delete({
                        'fileId': fileId,
                        'permissionId': permissionId
                    });
                    var callback = function (resp) {
                        if (!_.isUndefined(resp.error) && !_.isNull(resp.error) &&
                                !_.isUndefined(resp.error.code) && !_.isUndefined(resp.error.message)) {
                            Utils.triggerErrorWhenCloudLoginAction(resp.error.code, resp.error.message, "drawer");
                        } else {
                            _that.updateSharedFlagProperty(originalFileId, false);
                            _that.removeFile(event, fileId);
                            $("body").trigger("europass:cloud:manage:stopSharing:postAction", target);
                            $("body").trigger("europass:waiting:indicator:cloud:hide", true);
                        }
                    };
                    request.execute(callback);
                }

                , getMetadataForSharing: function (permissionId, id) {
                    return {
                        "prm": permissionId,
                        "shrid": id
                    };
                }
                , getMetadataForSharingEmail: function (email) {
                    return {
                        "email": email
                    };
                }
            });
            return GoogleDriveView;
        }
);