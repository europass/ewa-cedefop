/*
 *
 *  Controller for One Drive logged in services
 *
 * */

define(
        [
            'require',
            'jquery',
            'module',
            'underscore',
            'backbone',
            'europass/http/WindowConfigInstance',
            'Utils',
            'routers/SkillsPassportRouterInstance',
            'models/NavigationRoutesInstance',
            'views/main/cloud/OneDriveAuthenticate',
            'i18n!localization/nls/EditorHelp',
            'i18n!localization/nls/GuiLabel',
            'HelperManageModelUtils'
        ],
        function (require, $, module, _, Backbone, WindowConfig, Utils, AppRouter, NavigationRoutes,
                OneDriveAuthenticate, EditorHelp, GuiLabel, HelperManageModelUtils) {

            var CloudOneDriveView = Backbone.View.extend({

                driveFolders: [],
                oneDriveURL: "https://api.onedrive.com/v1.0/drive/"
                , events: {
                    "europass:cloud:connect:onedrive": "doConnect",
                    "europass:cloud:onedrive:folder:ready": "createFolders",
                    "europass:cloud:onedrive:list": "getFiles",
                    "europass:cloud:onedrive:renameDocument": "renameDocument",
                    "europass:cloud:onedrive:cloneDocument": "cloneDocument",
                    "europass:cloud:onedrive:deleteDocument": "deleteDocument",
                    "europass:cloud:onedrive:postDeleteDocument": "postDelete",
                    "europass:cloud:onedrive:updateContent": "updateDocument",
                    "europass:cloud:onedrive:createDocument": "createDocument",
                    "europass:cloud:onedrive:getContent": "getContent"
                }

                , initialize: function () {
                    this.oneDriveCustomFacetMetadataName = "europassd_" + WindowConfig.onedriveAppkey.substring(8);
                    this.driveFolders = WindowConfig.cloudStorageFolder.split("/");
                    this.oneDriveAuthenticate = new OneDriveAuthenticate({});
                }

                , doConnect: function () {
                    this.oneDriveAuthenticate.doConnect();
                }

                , onConnectAndFoldersOkTriggers: function () {
                    $("body").trigger("europass:cloud-sign-in:drawer:hide");
                    $("body").trigger("europass:waiting:indicator:cloud:hide", true);
                    $("body").trigger("europass:cloud:manage:use:local:storage", true);
                    $("body").trigger("europass:cloud:manage:show:current:loaded");
                    this.manageUIContentOnConnect();

                    if (this.oneDriveAuthenticate.getAuthenticationMode() !== 'silentAuth')
                        this.loadEditableDocument();
                    $("body").trigger("europass:cloud:manage:handle:response:success", true);

                    var url = NavigationRoutes.findActiveRoute().replace(".", "/");
                    if (AppRouter !== undefined) {
                        AppRouter.navigate(url + '/cloud', {
                            trigger: false,
                            replace: !("pushState" in window.history) //To update the URL without creating an entry in the browser's history, set the replace option to true. 
                        });
                    }
                }

                , manageUIContentOnConnect: function () {
                    $("#top-ui-cloud-sign-in").hide();
                    $("#top-ui-cloud-connected").find('.cloud-connected-icon').addClass("onedriveIcon");
                    $(".top-manage-documents-section").find("small").addClass("image-onedrive");
                    $(".top-manage-documents-section").find("#connected-to-service").text(GuiLabel["skillspassport.import.cloud.onedrive"]);
                    $("#MyFilesListForm .myFilesList-info").removeClass("googledrive").addClass("onedrive");
                }

                , checkFoldersExist: function (accessToken) {
                    var _that = this;
                    var checkFolders = false;
                    $.ajax({
                        type: "GET",
                        async: false,
                        url: _that.oneDriveURL + "root:/" + _that.driveFolders[0] + "/" + _that.driveFolders[1] + "?access_token=" + accessToken,
                        headers: {"content-type": "application/json"},
                        success: function () {
                            checkFolders = true;
                        }
                    });
                    return checkFolders;
                }

                , createFolders: function (event, accessToken) {
                    var _that = this;
                    if (_that.checkFoldersExist(accessToken)) {
                        _that.onConnectAndFoldersOkTriggers();
                        return;
                    }

                    $.ajax({
                        type: "POST",
                        url: _that.oneDriveURL + "root/children?access_token=" + accessToken,
                        data: JSON.stringify({"name": _that.driveFolders[0], "folder": {}}),
                        headers: {"content-type": "application/json"},
                        success: function () {
                            $.ajax({
                                type: "POST",
                                url: _that.oneDriveURL + "root:/" + _that.driveFolders[0] + ":/children?access_token=" + accessToken,
                                data: JSON.stringify({"name": _that.driveFolders[1], "folder": {}}),
                                headers: {"content-type": "application/json"},
                                success: function () {
                                    _that.onConnectAndFoldersOkTriggers();
                                },
                                error: function (data) {
                                    Utils.triggerErrorWhenCloudLoginAction(data.status, data.statusText, "main");
                                    $("body").trigger("europass:cloud:manage:disconnect");
                                    $("body").trigger("europass:cloud-sign-in:drawer:hide");
                                }
                            });
                        },
                        error: function (data) {
                            Utils.triggerErrorWhenCloudLoginAction(data.status, data.statusText, "main");
                            $("body").trigger("europass:cloud:manage:disconnect");
                            $("body").trigger("europass:cloud-sign-in:drawer:hide");
                        }
                    });
                }

                , getFiles: function () {
                    var accessToken = Utils.readCookieByName(WindowConfig.cloudAccessToken);
                    var _that = this;
                    var currentDocuments = [];
                    $.ajax({
                        type: "GET",
                        cache: false,
                        url: _that.oneDriveURL + "root:/" + _that.driveFolders[0] + "/" + _that.driveFolders[1] +
                                ":/children?orderby=lastModifiedDateTime%20desc&filter=file%20ne%20null&access_token=" + accessToken
                                + "&select=*," + _that.oneDriveCustomFacetMetadataName,
                        headers: {"content-type": "application/json"},
                        success: function (response) {
                            for (var idx in response.value)
                            {
                                var fileObj = response.value[idx];
                                _that.loadDocumentsAttributes(fileObj, currentDocuments);
                            }
                            $("body").trigger("europass:cloud:manage:render", [currentDocuments, true]);
                            $("body").trigger("europass:cloud:manage:show:files:drawer");
                            $("body").trigger("europass:cloud:manage:handle:response:success", true);
                        },
                        error: function (data) {
                            Utils.triggerErrorWhenCloudLoginAction(data.status, data.responseText, "main");
                            $("body").trigger("europass:cloud:manage:disconnect");
                        }
                    });
                }

                , loadDocumentsAttributes: function (data, currentDocuments) {
                    var metadataProp = this.oneDriveCustomFacetMetadataName;
                    var locale, filetypes = [];
                    if (data.file.mimeType === 'application/json') {
                        if (_.isUndefined(data[metadataProp])) {
                            locale = module.config().locale.toUpperCase();
                            filetypeText = "";
                        } else {
                            locale = data[metadataProp].locale.toUpperCase();
                            filetypes = JSON.parse(data[metadataProp].filetypes);
                            filetypeText = "";
                            $.each(filetypes, function (i, type) {
                                if (i !== filetypes.length - 1) {
                                    filetypeText += type + ", ";
                                } else {
                                    filetypeText += type;
                                }
                            });
                        }
                        var displayName = Utils.getFilenameTitle(data.name) || data.name;
                        currentDocuments.push({"id": data.id, "name": displayName,
                            "date": new XDate(data.lastModifiedDateTime).toString('dd MMMM yyyy, HH:mm:ss'),
                            "size": data.size, "source": data["@content.downloadUrl"], "filename": data.name,
                            "locale": locale, "filetypes": filetypeText.trim()});
                    }

                    return currentDocuments;
                }

                , renameDocument: function (event, fileId, filename, oldFilename, elem, loaded) {
                    var _that = this;
                    var accessToken = Utils.readCookieByName(WindowConfig.cloudAccessToken);
                    $.ajax({
                        type: "PATCH",
                        url: _that.oneDriveURL + "items/" + fileId + "?access_token=" + accessToken,
                        headers: {"content-type": "application/json"},
                        data: JSON.stringify({"name": filename}),
                        success: function () {
                            $("body").trigger("europass:waiting:indicator:cloud:hide", true);
                            $("body").trigger("europass:cloud:manage:handle:response:success", true);
                            if (loaded === true) {
                                $("body").trigger("europass:cloud:manage:update:content:current", [{"title": Utils.getFilenameTitle(filename)}, true]);
                            }
                            $(elem).closest('.manage-document-container').find('.radio-cloud-documents').attr('data-filename', filename);
                        },
                        error: function (data) {
                            $("body").trigger("europass:cloud:manage:rename:error", [oldFilename, elem]);
                            var sameFilenameErrorCode = JSON.parse(data.responseText).error.code;
                            if (sameFilenameErrorCode === 'nameAlreadyExists') {
                                var customResponseText = {"error": {"code": sameFilenameErrorCode, "message": EditorHelp["cloudLogin.errors.service.onedrive.rename.samefilename"]}};
                                Utils.triggerErrorWhenCloudLoginAction(data.status, JSON.stringify(customResponseText), "drawer");
                            } else {
                                Utils.triggerErrorWhenCloudLoginAction(data.status, data.responseText, "drawer");
                            }
                        }
                    });
                }

                , cloneDocument: function (event, fileId, title, parentElem) {
                    var _that = this;
                    var accessToken = Utils.readCookieByName(WindowConfig.cloudAccessToken);
                    $.ajax({
                        type: "POST",
                        url: _that.oneDriveURL + "items/" + fileId + "/action.copy?access_token=" + accessToken,
                        headers: {"content-type": "application/json", "prefer": "respond-async"},
                        data: JSON.stringify({"parentReference":
                                    {"path": "/drive/root:/" + _that.driveFolders[0] + "/" + _that.driveFolders[1] + ":"},
                            "name": title}),
                        success: function (data, status, xhr) {
                            (function monitorResponseLocationCopyProgress() {
                                $.ajax({
                                    url: xhr.getResponseHeader('Location'),
                                    type: "GET",
                                    dataType: "json",
                                    success: function (data) {
                                        if (!_.isUndefined(data.id)) {
                                            $("body").trigger("europass:cloud:manage:clone:file:DOMelement",
                                                    [data.id, data.name, new XDate(data.lastModifiedDateTime).toString('dd MMMM yyyy, HH:mm:ss'),
                                                        data["@content.downloadUrl"], parentElem]);
                                            $("body").trigger("europass:cloud:manage:handle:response:success", true);
                                            //$("body").trigger("europass:waiting:indicator:cloud:hide", true);

                                            var filetypes = parentElem.find('.document-metadata .filetypes').text().trim().split(",");
                                            var locale = parentElem.find('.document-metadata .cvLocale').text().trim();

                                            var metadataCustomProperties = {"locale": locale, "lastUpdateDate": data.lastModifiedDateTime, "filetypes": filetypes};
                                            _that.addMetadata("clone", data, metadataCustomProperties);
                                        } else {
                                            monitorResponseLocationCopyProgress();
                                        }
                                    },
                                    error: function (data) {
                                        Utils.triggerErrorWhenCloudLoginAction(data.status, data.responseText, "drawer");
                                    }
                                });
                            })();
                        },
                        error: function (data) {
                            Utils.triggerErrorWhenCloudLoginAction(data.status, data.responseText, "drawer");
                        }
                    });
                }

                , deleteDocument: function (event, fileId, parentElem) {
                    var _that = this;
                    var accessToken = Utils.readCookieByName(WindowConfig.cloudAccessToken);
                    $.ajax({
                        type: "DELETE",
                        url: _that.oneDriveURL + "items/" + fileId + "?access_token=" + accessToken,
                        success: function () {
                            $("body").trigger("europass:cloud:manage:delete:fileInDom", [parentElem, fileId]);
                            $("body").trigger("europass:cloud:manage:handle:response:success", true);
                        },
                        error: function (data) {
                            Utils.triggerErrorWhenCloudLoginAction(data.status, data.responseText, "drawer");
                        }
                    });
                }

                , postDelete: function (event, nextState, filename, fileUrl, selectElem) {
                    var _that = this;
                    switch (nextState) {
                        case "createNewAfterDelete":
                            _that.createDocument(event, filename, undefined, undefined, undefined, "loadAlso");
                            break;
                        case "loadLatestAfterDelete":
                            _that.getContent(event, fileUrl, selectElem, true);
                            break;
                        default:
                            break;
                    }
                }

                , createDocument: function (event, filename, jsonContent, isUpdate, newFilename, loaded) {
                    var _that = this;
                    var currentDocuments = [];
                    var accessToken = Utils.readCookieByName(WindowConfig.cloudAccessToken);
                    var isBlankDocument = false;
                    var filetypes = [];
                    if (_.isUndefined(jsonContent) || jsonContent === null) {
                        jsonContent = Utils.getEmptySkillsPassportObject(module.config().locale, module.config().xsdversion,
                                module.config().generator, module.config().comment, module.config().europassLogo);
                        isBlankDocument = true;
                    }
                    var jsonAttr = _that.getJSONModelAttributes(jsonContent);

                    if (isBlankDocument !== true) {
                        filetypes = Utils.checkModelInfoTypesNonEmpty(this.model.info()).filetypes;
                    }
                    var metadataCustomProperties = {"locale": jsonAttr["locale"], "lastUpdateDate": jsonAttr["lastUpdateDate"], "filetypes": filetypes};

                    $.ajax({
                        type: "PUT",
                        url: _that.oneDriveURL + "items/root:/" + _that.driveFolders[0] + "/" + _that.driveFolders[1] + "/"
                                + filename + ":/content?access_token=" + accessToken,
                        headers: {"content-type": "application/json"},
                        data: jsonContent,
                        success: function (data) {
                            if (isUpdate) { // Update Action
                                if (!_.isUndefined(newFilename))
                                    _that.addMetadata("updateAndRename", data, metadataCustomProperties, '', newFilename);
                                else
                                    _that.addMetadata("update", data, metadataCustomProperties);
                            } else { // Create Action (then GET request to get all proper metadata(e.g downloadURL))
                                $.ajax({
                                    type: "GET",
                                    url: _that.oneDriveURL + "items/" + data.id + "?access_token=" + accessToken,
                                    headers: {"content-type": "application/json"},
                                    success: function (dataResponse) {
                                        _that.loadDocumentsAttributes(dataResponse, currentDocuments);
                                        _that.addMetadata("create", dataResponse, metadataCustomProperties, currentDocuments);
                                        if (loaded === 'loadAlso') {
                                            $("body").trigger("europass:cloud:manage:load:store:content:current",
                                                    [JSON.parse(jsonContent), dataResponse.id, Utils.getFilenameTitle(dataResponse.name), jsonAttr["lastUpdateDate"]]);
                                        }
                                    },
                                    error: function (dataResponse) {
                                        Utils.triggerErrorWhenCloudLoginAction(dataResponse.status, dataResponse.responseText, "drawer");
                                    }
                                });
                            }
                        },
                        error: function (data) {
                            Utils.triggerErrorWhenCloudLoginAction(data.status, data.responseText, "drawer");
                        }
                    });
                }

                , updateDocument: function (event, currentFilename, json, newFilename) {
                    this.createDocument(event, currentFilename, json, true, newFilename);
                }

                , getJSONModelAttributes: function (jsonContent) {
                    var jsonObj = JSON.parse(jsonContent);
                    var jsonAttr = {};
                    jsonAttr["locale"] = jsonObj.SkillsPassport.Locale;
                    jsonAttr["lastUpdateDate"] = jsonObj.SkillsPassport.DocumentInfo.LastUpdateDate;

                    return jsonAttr;
                }

                , addMetadata: function (typeOfAction, data, metadataCustomProperties, currentDocuments, filename) {
                    var _that = this;
                    var accessToken = Utils.readCookieByName(WindowConfig.cloudAccessToken);
                    var metadata = {};

                    var locale = metadataCustomProperties.locale;
                    var lastUpdateDate = metadataCustomProperties.lastUpdateDate;
                    var filetypes = metadataCustomProperties.filetypes;

                    var updateDate = new Date(lastUpdateDate).getTime().toString();
                    metadata[_that.oneDriveCustomFacetMetadataName] = {"updateDate": updateDate, "locale": locale, "filetypes": JSON.stringify(filetypes)};
                    if (!_.isUndefined(filename))
                        metadata["name"] = filename;

                    $.ajax({
                        type: "PATCH",
                        url: _that.oneDriveURL + "items/" + data.id + "?access_token=" + accessToken,
                        headers: {"content-type": "application/json"},
                        data: JSON.stringify(metadata),
                        success: function () {
                            $("body").trigger("europass:cloud:manage:handle:response:success", true);
                            switch (typeOfAction) {
                                case "update":
                                    $("body").trigger("europass:cloud:manage:update:content:current", [{"lastUpdate": lastUpdateDate, "locale": locale}, true]);
                                    break;
                                case "create":
                                    $("body").trigger("europass:cloud:manage:render", [currentDocuments, false]);
                                    break;
                                case "updateAndRename":
                                    $("body").trigger("europass:cloud:manage:update:content:current",
                                            [{"lastUpdate": lastUpdateDate, "title": Utils.getFilenameTitle(filename), "locale": locale}, true]);
                                    break;
                                case "clone":
                                    break;
                                default:
                                    break;
                            }
                        },
                        error: function (data) {
                            Utils.triggerErrorWhenCloudLoginAction(data.status, data.responseText, "drawer");
                        },
                        complete: function (data) {
                            $("body").trigger("europass:waiting:indicator:cloud:hide", true);
                        }
                    });
                }

                , getContent: function (event, sourceUrl, fileElem, keepOpen) {
                    $.get(sourceUrl).
                            done(function (data) {
                                $("body").trigger("europass:cloud:manage:load:content:select", [HelperManageModelUtils.syncPersonNameOrder(data), fileElem, keepOpen]);
                                $("body").trigger("europass:cloud:manage:handle:response:success", true);
                            }).
                            fail(function (data) {
                                Utils.triggerErrorWhenCloudLoginAction(data.status, data.responseText, "drawer");
                            });
                }

                , loadEditableDocument: function () {
                    var _that = this;
                    var accessToken = Utils.readCookieByName(WindowConfig.cloudAccessToken);
                    var localDocumentModel = _that.getCurrentModel();

                    $.ajax({
                        type: "GET",
                        cache: false,
                        url: _that.oneDriveURL + "root:/" + _that.driveFolders[0] + "/" + _that.driveFolders[1] +
                                ":/children?orderby=lastModifiedDateTime%20desc&filter=file%20ne%20null&access_token=" + accessToken +
                                "&select=*," + _that.oneDriveCustomFacetMetadataName,
                        headers: {"content-type": "application/json"},
                        success: function (respFiles) {
                            var recentCloudDoc = respFiles.value[0];
                            if (!_.isUndefined(recentCloudDoc) && _that.isValidEuropassCVJson(recentCloudDoc)) {
                                $.get(recentCloudDoc["@content.downloadUrl"]).
                                        done(function (dataJSON) {
                                            if (_.isUndefined(localDocumentModel)) {
                                                // US.03 (Local CV empty / Existing cloud
                                                // We need to compare page locale with CV's locale to avoid fallback to CVs locale (causing redirect issues)
                                                if (!_.isUndefined(localStorage.LoadedFile) &&
                                                        module.config().locale !== JSON.parse(localStorage.LoadedFile).locale) {

                                                    $("body").trigger("europass:cloud:manage:model:change");
                                                } else {
                                                    $("body").trigger("europass:cloud:manage:load:store:content:current",
                                                            [dataJSON, recentCloudDoc.id, Utils.getFilenameTitle(recentCloudDoc.name),
                                                                dataJSON.SkillsPassport.DocumentInfo.LastUpdateDate]);
                                                    $("body").trigger("europass:waiting:indicator:cloud:hide", true);
                                                }
                                            } else {
                                                var recentCloudObj = {"recentData": recentCloudDoc, "recentDataContent": dataJSON};
                                                _that.checkMatchingCV(respFiles.value, localDocumentModel, recentCloudObj);
                                            }
                                        }).
                                        fail(function () {
                                            $("body").trigger("europass:waiting:indicator:cloud:hide", true);
                                        });
                            }
                            // (US.01. US.02 - No returned results from cloud service)
                            else {
                                $("body").trigger("europass:cloud:manage:load:uploadCurrentDocument", [localDocumentModel, "loadAlso"]);
                                $("body").trigger("europass:waiting:indicator:cloud:hide", true);
                            }
                        },
                        error: function (data) {
                            $("body").trigger("europass:waiting:indicator:cloud:hide", true);
                        }
                    });
                }

                , checkMatchingCV: function (cloudDocuments, localDocumentModel, recentCloudObj) {
                    // US.04 - Matching CVs
                    var _that = this;
                    var localLastUpdateDate;
                    if (!_.isUndefined(localStorage.LoadedFile))
                        localLastUpdateDate = JSON.parse(localStorage.LoadedFile).lastUpdate;
                    else
                        localLastUpdateDate = localDocumentModel.SkillsPassport.DocumentInfo.LastUpdateDate;
                    var matched = false;
                    $.each(cloudDocuments, function (i, doc) {
                        if (!_.isUndefined(doc[_that.oneDriveCustomFacetMetadataName])
                                && doc[_that.oneDriveCustomFacetMetadataName].updateDate === new Date(localLastUpdateDate).getTime().toString()) {
                            matched = true;
                            if (doc[_that.oneDriveCustomFacetMetadataName].locale === ewaLocale) {
                                $("body").trigger("europass:cloud:manage:show:current:loaded");
                            } else {
                                $("body").trigger("europass:cloud:manage:model:change");
                            }
                            $("body").trigger("europass:waiting:indicator:cloud:hide", true);
                            return false;
                        }
                    });

                    // US.04 - Not Matching CVs
                    if (matched === false) {
                        $("body").trigger("europass:cloud:manage:modal:confirmation:load", recentCloudObj);
                        $("body").trigger("europass:waiting:indicator:cloud:hide", true);
                    }
                }

                , getCurrentModel: function () {
                    var model = JSON.parse(this.model.conversion().toTransferable());
                    if (!Utils.isSkillsPassportObjectEmpty(model)) {
                        var jsonContent = model;
                    }
                    return jsonContent;
                }

                , isValidEuropassCVJson: function (data) {

                    if (data.file.mimeType === 'application/json'
                            && !_.isEmpty(data[this.oneDriveCustomFacetMetadataName])
                            && !_.isEmpty(data[this.oneDriveCustomFacetMetadataName].updateDate)
                            && !_.isEmpty(data[this.oneDriveCustomFacetMetadataName].locale)
                            && !_.isEmpty(data[this.oneDriveCustomFacetMetadataName].filetypes)) {
                        return true;
                    }
                    return false;
                }

            });

            return CloudOneDriveView;
        }
);