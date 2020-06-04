/**
 * My files list (drawer) when connected to Cloud services (Google Drive/ One Drive/ Dropbox)
 *
 *
 */
define(
        [
            'jquery',
            'jqueryui',
            'underscore',
            'backbone',
            'Utils',
            'HttpUtils',
            'routers/SkillsPassportRouterInstance',
            'models/NavigationRoutesInstance',
            'i18n!localization/nls/Notification',
            'i18n!localization/nls/EditorHelp',
            'i18n!localization/nls/GuiLabel',
            'europass/http/WindowConfigInstance',
            'hbs!templates/main/cloud-login/filesList',
            'hbs!templates/main/cloud-login/manageDocument',
            'views/main/cloud/LoadDocumentController',
            'hbs!templates/dialog/cloudloaddocumentconfirmation',
            'views/main/cloud/CloudViewHelper',
            'views/main/ModelLocalStoreView',
            'europass/GlobalLocalStoreOptionInstance',
            'analytics/EventsController',
            'models/ShareCloudInfoModel',
            'europass/http/ServicesUri',
            'europass/http/MediaType',
            'europass/http/Resource'
        ],
        function ($, jqueryui, _, Backbone, Utils, HttpUtils, AppRouter, NavigationRoutes,
                Notification, EditorHelp, GuiLabel, WindowConfig, HtmlTemplate,
                ManageFilesTemplate, LoadDocumentController,
                ConfirmationTpl, CloudViewHelper, ModelLocalStoreView, GlobalLocalStoreOption,
                Events, ShareCloudInfoModel, ServicesUri, MediaType, Resource) {

            var ManageDocumentsView = Backbone.View.extend({
                sectionEl: $("#top-ui-cloud-connected")
                , optionsSelector: "#MyFilesListForm .myFilesList-info"
                , overlaySelector: "#MyFilesListForm"
                , localStorageSelector: "#store-data-locally-control"
                , errorMsgSelector: "#my-cloud-files-error"
                , shareReviewNoficationSelector: ".share-cloud-notification-area"
                , googleDriveFolder: ""
                , accessToken: ""
                , cloudProvider: ""
                , oldFilename: ""
                , extension: ".json"
                , filenamePrefix: ["Europass", "-CV-"]
                , currentLoadedFile: null
                , renameCSS: "image-edit", renameConfirmCSS: "image-confirm"
                , recentCloudObj: {}
                , event: new Events
                , document_selected: null
                , events: {
                    "click #top-ui-cloud-connected a.current-cloud-connected": "toggleManageFiles",
                    "click #MyFilesListForm button.close": "hideCloudFilesListForm",
                    "click #cloud-disconnect": "disconnect",
                    "click #MyFilesListForm .myFilesList-info #confirm-cloud-load-document": "onSelectLoadDocument",
                    "dblclick #MyFilesListForm .myFilesList-info .manage-document-tile .cloud-document-filename.rename-selected": "onDoubleClickInputRename",
                    "click #MyFilesListForm .myFilesList-info .btn-cloud-rename": "renameDocumentInDom",
                    "click #MyFilesListForm .myFilesList-info .btn-cloud-rename-confirm": "confirmRenameDocument",
                    "keydown #MyFilesListForm .myFilesList-info input.cloud-document-filename": "onKeyDownRename",
                    "focus #MyFilesListForm .myFilesList-info input.cloud-document-filename": "onSelectingInputRename",
                    "click #MyFilesListForm .myFilesList-info .btn-cloud-copy": "cloneDocument",

                    "click #MyFilesListForm .myFilesList-info .btn-cloud-delete": "toggleDeleteConfirm",
                    "click #MyFilesListForm .myFilesList-info #cloudManageDocumentsRadioForm .confirm-delete-action": "deleteDocument",
                    "click #MyFilesListForm .myFilesList-info #cloudManageDocumentsRadioForm .reject-delete-action": "rejectDeleteAction",

                    "click #MyFilesListForm .myFilesList-info .btn-cloud-share": "toggleShareConfirm",
                    "click #MyFilesListForm .myFilesList-info #cloudManageDocumentsRadioForm .confirm-share-action": "shareDocument",
                    "click #MyFilesListForm .myFilesList-info #cloudManageDocumentsRadioForm .manage-action.link-share": "shareDocumentStopPropagation",
                    "click #MyFilesListForm .myFilesList-info #cloudManageDocumentsRadioForm .reject-share-action": "rejectShareAction",
                    "click #MyFilesListForm .myFilesList-info #cloudManageDocumentsRadioForm .stop-sharing-document": "stopShareAction",
                    "europass:cloud:manage:share:postAction": "postShareAction",
                    "europass:cloud:manage:stopSharing:postAction": "postStopSharingAction",

                    "click .radio-cloud-documents": "handleClickRadio",
                    "click #MyFilesListForm .myFilesList-info .add-new-document-section #addBlankDocument": "uploadDocument",
                    "click #confirmCloudLoadDocumentModal .confirm-dialog .confirm-save.load-cv-document": "confirmSaveLocalCVNewVersion",
                    "click #confirmCloudLoadDocumentModal .confirm-dialog .confirm-discard-load.load-cv-document": "confirmDiscardLocalCVNewVersion",
                    "click .manage-document-tile:not('.cloud-file-selected')": "selectMyDocument",
                    "europass:cloud:manage:disconnect": "disconnect",
                    "europass:cloud:manage:show:files:drawer": "showCloudFilesListForm",
                    "europass:cloud:manage:hide:files:drawer": "hideCloudFilesListForm",
                    "europass:cloud:manage:folders": "toggleFolders",
                    "europass:cloud:manage:render": "renderDocuments",
                    "europass:cloud:manage:clone:file:DOMelement": "cloneDocumentInDom",
                    "europass:cloud:manage:rename:error": "renameError",
                    "europass:cloud:manage:delete:fileInDom": "deleteDocumentInDom",
                    "europass:cloud:manage:delete:postAction": "postDeleteAction",
                    "europass:cloud:manage:response:error": "triggerResponseError",
                    "europass:cloud:manage:load:content:select": "loadContentWhenSelectDocument",
                    "europass:cloud:manage:load:store:content:current": "loadAndStoreCurrentDocumentLocalStorage",
                    "europass:cloud:manage:update:content:current": "updateCurrentDocumentLocalStorage",
                    "europass:cloud:manage:load:uploadCurrentDocument": "uploadLocalDocumentToCloud",
                    "europass:cloud:manage:load:uploadExistingDocument": "uploadExistingDocumentToCloud",
                    "europass:cloud:manage:handle:connection:errors": "handleDOMWhenSignOut",
                    "europass:cloud:manage:handle:response:success": "triggerResponseSuccess",
                    "europass:cloud:manage:show:current:loaded": "showCurrentLoaded",
                    "europass:cloud:manage:erase:update": "eraseAndUpdate",
                    "europass:cloud:manage:modal:confirmation:load": "confirmLoadDocumentAfterConnect",
                    "europass:cloud:manage:model:change": "onModelChangeUploadCV",
                    "europass:cloud:manage:use:local:storage": "toggleUseLocalStorage"
                }
                , initialize: function (options) {
                    this.htmlRender();
                    this.manageFilesWrapper = $(this.$el.find("#cloudManageDocumentsRadioForm"));
                    this.connectCloudServices();
                    this.loadController = new LoadDocumentController({
                        relatedController: this,
                        modelUpdateEvent: "model:loaded:cloud:document",
                        modelUpdateMsgKey: "success.upload.cv.saved"
                    });
                    try {
                        this.currentLoadedFile = JSON.parse(window.localStorage.getItem('LoadedFile'));
                        //check if it is offline and a locale change has occurred
                        if ((this.cloudProvider === "" || !Utils.isHostReachable()) && (!_.isNull(this.currentLoadedFile) && ewaLocale !== this.currentLoadedFile.locale)) {
                            var lastModifiedDate = JSON.parse(this.model.conversion().toTransferable()).SkillsPassport.DocumentInfo.LastUpdateDate;
                            this.updateCurrentDocumentLocalStorage(null, {"lastUpdate": lastModifiedDate, "locale": ewaLocale}, false);
                            if (!Utils.isHostReachable()) {//disconnect
                                this.disconnect();
                            }
                        }
                        //Listen to update model events
                        this.listenUpdateModel();
                    } catch (err) {
                    }
                }

                , listenUpdateModel: function () {
                    this.listenTo(this.model, "prefs:order:changed", this.onModelChangeUploadCV);
                    this.listenTo(this.model, "prefs:data:format:changed", this.onModelChangeUploadCV);
                    this.listenTo(this.model, "model:content:changed", this.onModelChangeUploadCV);
                    this.listenTo(this.model, "list:sort:change", this.onModelChangeUploadCV);
                    this.listenTo(this.model, "linked:attachment:changed", this.onModelChangeUploadCV);
                    this.listenTo(this.model, "europassLogo:changed", this.onModelChangeUploadCV);
                }

                , htmlRender: function () {

                    if (WindowConfig.showCloudLogin === true) {
                        var context = {};
                        var html = HtmlTemplate(context);
                        this.$el.append(html);
                        this.optionsArea = this.$el.find(this.optionsSelector);
                        this.overlay = this.$el.find(this.overlaySelector);
                        this.errorMsg = this.$el.find(this.errorMsgSelector);
                        this.cloudViewHelper = new CloudViewHelper({"overlay": this.overlay, "area": this.optionsArea,
                            "sectionEl": this.sectionEl, "errorMsgElem": this.errorMsg});
                        this.modelLocalStoreView = new ModelLocalStoreView({
                            model: this.model
                        });
                        this.triggerResponseSuccess(true);
                    }
                }

                , toggleUseLocalStorage: function (event, isConnected) {
                    if (isConnected) {
                        var isStorable = GlobalLocalStoreOption.isStorable();
                        //check if store to local storage is disabled
                        if (!isStorable) {
                            this.modelLocalStoreView.toggleStoring(true);
                        }
                        $("#store-data-locally-control").attr('disabled', 'disabled');
                    } else {
                        $("#store-data-locally-control").removeAttr("disabled");
                    }
                }

                , renderDocuments: function (event, items, remove) {
                    var context = {"links": items};
                    var html = ManageFilesTemplate(context);
                    if (remove === true) {
                        $(".manage-document-tile").remove();
                        this.manageFilesWrapper.append(html);
                        $(".manage-document-tile").first().hide().show('slow');
                        this.checkRadioOnLoadedFile();
                    } else {
                        this.manageFilesWrapper.prepend(html);
                        $(".manage-document-tile").first().hide().show('slow');
                        if ($(".manage-document-tile").length === 1)
                            this.checkFirstRadio();
                    }
                    this.document_selected = $(".manage-document-tile").find("input:checked").attr('data-document-id');
                }

                , connectCloudServices: function () {
                    var cloudProviderCookie = Utils.readCookieByName(WindowConfig.cloudCookieId);
                    this.checkCloudConnection(cloudProviderCookie);
                    if (cloudProviderCookie) {
                        switch (cloudProviderCookie) {
                            case "gdrive":
                                $("body").trigger("europass:cloud:connect:googledrive", true);
                                break;
                            case "onedrive":
                                $("body").trigger("europass:cloud:connect:onedrive");
                                break;
                            default:
                                break;
                        }
                    } else { //no cloud cookie is set
                        this.checkCloudConnection("");
                    }
                }

                , checkCloudConnection: function (value) {
                    switch (value) {
                        case "gdrive":
                            this.cloudProvider = "googledrive";
                            $("#top-ui-cloud-sign-in").hide();
                            break;
                        case "onedrive":
                            this.cloudProvider = "onedrive";
                            $("#top-ui-cloud-sign-in").hide();
                            break;
                        case "":
                            this.handleNotConnectedCloud();
                            this.cloudProvider = "";
                            break;
                        default:
                            break;
                    }
                }

                , hideDeleteBubbles: function () {
                    $(".manage-action.link-delete").each(function () {
                        if ($(this).is(':visible')) {
                            $(this).fadeToggle("slow", function () {
                                $(this).hide();
                            });
                        }
                    });
                }

                // We need to toggle rename action each time there is a document selected or created
                , toggleRenameAction: function () {
                    var _that = this;
                    $("input.cloud-document-filename").each(function () {
                        var container = $(this).closest(".manage-document-container");
                        var inputRadio = $(container).find("input.radio-cloud-documents");
                        var filename = $(inputRadio).data('filename');
                        $(this).replaceWith("<span class='cloud-document-filename'>" +
                                Utils.getFilenameTitle(filename) + "</span>");
                        $(container).find('.btn-cloud-rename-confirm').removeClass('btn-cloud-rename-confirm').addClass('btn-cloud-rename').
                                removeClass(_that.renameConfirmCSS).addClass(_that.renameCSS);
                    });
                }

                , toggleSelection: function () {
                    $(".manage-document-tile").removeClass("cloud-file-selected");
                    $(".manage-document-tile").each(function () {
                        if ($(this).find("input.radio-cloud-documents").is(':checked')) {
                            $(this).addClass("cloud-file-selected");
                        }
                    });
                }

                , showCloudFilesListForm: function (event) {
                    this.event.cloud_open_drawer_button();
                    this.cloudViewHelper.showCloudDrawer();
                }
                , hideCloudFilesListForm: function () {
                    this.cloudViewHelper.hideCloudDrawer();
                }

                , selectMyDocument: function (event) {
                    var target = $(event.currentTarget);
                    //check that it is not about rename of a non-selected item
                    if (target.find(".rename-selected").length > 0) {
                        return;
                    }
                    target.find("input.radio-cloud-documents:radio").prop("checked", true);
                    this.toggleSelection();
                    this.toggleMainActions();
                    target.addClass("cloud-file-selected");
                }

                , disconnect: function () {
                    console.log('Disconnect', this.cloudProvider)
                    this.event.cloud_signout_button();
                    switch (this.cloudProvider) {
                        case "googledrive":
                            $("body").trigger("europass:cloud:google:deleteToken");
                            break;
                        default:
                            break;
                    }
                    $("body").trigger("europass:cloud:manage:use:local:storage", false);
                    this.hideCloudDisconnect();
                    this.cleanupDomElements();
                    this.hideCloudFilesListForm();
                    this.handleDOMWhenSignOut();
                    this.cloudProvider = "";

                    if (AppRouter !== undefined) {
                        var url = NavigationRoutes.findActiveRoute().split(".")[0];
                        AppRouter.navigate(url + "/compose", {
                            trigger: false,
                            replace: !("pushState" in window.history) //To update the URL without creating an entry in the browser's history, set the replace option to true. 
                        });
                    }
                }

                , toggleFolders: function (event, type, folder, accessToken) {
                    this.cloudProvider = type;
                    switch (this.cloudProvider) {
                        case "googledrive":
                            this.googleDriveFolder = folder;
                            break;
                        case "onedrive":
                            this.accessToken = accessToken;
                            break;
                        default:
                            break;
                    }
                }

                , toggleManageFiles: function (event) {
                    switch (this.cloudProvider) {
                        case "googledrive":
                            $("body").trigger("europass:cloud:google:list", [this.googleDriveFolder]);
                            break;
                        case "onedrive":
                            $("body").trigger("europass:cloud:onedrive:list", [this.accessToken]);
                            break;
                        default:
                            break;
                    }
                }

                , onDoubleClickInputRename: function (event) {
                    event.stopPropagation();
                }

                , onDoubleClickLoadDocument: function (event) {
                    var target = $(event.currentTarget);
                    var selElem = $(target).find("input.radio-cloud-documents");

                    if (this.document_selected !== selElem.attr('data-document-id')) {
                        this.event.cloud_document_ok_button_other_document();
                    } else {
                        this.event.cloud_document_ok_button_same_document();
                    }

                    this.getDocumentContent(selElem);
                }

                , onSelectLoadDocument: function (event) {
                    var target = $(event.currentTarget);
                    var selElem = $(target).parent().find("input:checked");

                    if (this.document_selected !== selElem.attr('data-document-id')) {
                        this.event.cloud_document_ok_button_other_document();
                    } else {
                        this.event.cloud_document_ok_button_same_document();
                    }

                    this.getDocumentContent(selElem);
                }

                , getDocumentContent: function (selElem) {
                    if (selElem.length === 0)
                        return;
                    var cloudDocumentSource = $(selElem).data('source-url');

                    $("body").trigger("europass:waiting:indicator:cloud:show", true);
                    switch (this.cloudProvider) {
                        case "googledrive":
                            $("body").trigger("europass:cloud:google:getContent", [cloudDocumentSource, selElem]);
                            break;
                        case "onedrive":
                            $("body").trigger("europass:cloud:onedrive:getContent", [cloudDocumentSource, selElem]);
                            break;
                        default:
                            break;
                    }
                }

                , loadContentWhenSelectDocument: function (event, data, currentSelectedElem, keepOpenDrawer) {
                    var fileId = $(currentSelectedElem).data('document-id');
                    var title = Utils.getFilenameTitle($(currentSelectedElem).data('filename'));

                    if (!_.isNull(fileId) && !_.isNull(title)
                            && !_.isUndefined(fileId) && !_.isUndefined(title)
                            && !_.isNull(fileId)) {
                        var type = Utils.getObjectType(data);
                        if (type !== null && !_.isUndefined(data.SkillsPassport) && !_.isUndefined(data.SkillsPassport.DocumentInfo)
                                && !_.isUndefined(data.SkillsPassport.DocumentInfo.LastUpdateDate)) {
                            var lastUpdateDate = data.SkillsPassport.DocumentInfo.LastUpdateDate;
                            //check if cv to load is empty
                            if (Utils.isSkillsPassportObjectEmpty(data)) {
                                window.localStorage.removeItem("temporary.europass.ewa.skillspassport.v3");
                            }
                            this.loadAndStoreCurrentDocumentLocalStorage(event, data, fileId, title, lastUpdateDate);
                            if (!keepOpenDrawer)
                                this.hideCloudFilesListForm();
                        } else {
                            //TODO LOC
                            this.cloudViewHelper.triggerResponseError("errCode", "not supported file type", "drawer");
                        }
                    } else {
                        //TODO LOC 
                        //this.triggerResponseError("errCode","errorMessage","drawer");
                        this.cloudViewHelper.triggerResponseError("", "error after getting content", "drawer");
                    }
                }

                /**
                 * Sets the LoadedFile Local Storage key when file is loaded locally
                 * @param {type} event
                 * @param {Object} data
                 * @param {String} fileID
                 * @param {String} filename
                 * @param {String} lastUpdate
                 * @returns void
                 */
                , loadAndStoreCurrentDocumentLocalStorage: function (event, data, fileID, filename, lastUpdate) {
                    this.loadController.load(data);
                    var surname = "";
                    if (!_.isUndefined(data.SkillsPassport.LearnerInfo) && !_.isUndefined(data.SkillsPassport.LearnerInfo.Identification) && !_.isUndefined(data.SkillsPassport.LearnerInfo.Identification.PersonName)
                            && !_.isUndefined(data.SkillsPassport.LearnerInfo.Identification.PersonName.Surname)) {
                        surname = data.SkillsPassport.LearnerInfo.Identification.PersonName.Surname;
                    }
                    var file = {"id": fileID, "title": filename, "lastUpdate": lastUpdate, "surname": surname, "locale": data.SkillsPassport.Locale};
                    window.localStorage.setItem('LoadedFile', JSON.stringify(file));
                    this.currentLoadedFile = file;
                    this.showCurrentLoaded();
                }

                /**
                 * Updates the LoadedFile in Local Storage given a key and a value
                 * @param {type} event
                 * @returns {undefined}
                 */
                , updateCurrentDocumentLocalStorage: function (event, obj, isOnline) {
                    //check if being offline
                    if (_.isNull(this.currentLoadedFile)) {
                        this.currentLoadedFile = {};
                    }
                    for (var key in obj) {
                        this.currentLoadedFile[key] = obj[key];
                    }
                    window.localStorage.setItem('LoadedFile', JSON.stringify(this.currentLoadedFile));
                    if (isOnline) {
                        this.showCurrentLoaded();
                    }
                }

                , hideCloudDisconnect: function () {
                    $(".top-manage-documents-section").find("small").removeClass();
                    $(".top-manage-documents-section").find("#connected-to-service").text('');
                    $("#top-ui-cloud-connected").hide();
                }

                , cleanupDomElements: function () {
                    $("#top-ui-cloud-current-document").hide();
                    $("#top-ui-cloud-current-document .cloud-selected-icon").text("");
                }

                , eraseCurrentLoadedFile: function (event) {
                    window.localStorage.removeItem('LoadedFile');
                    window.localStorage.removeItem('temporary.europass.ewa.skillspassport.v3');
                    this.cleanupDomElements();
                }

                , showCurrentLoaded: function (event) {
                    if (!_.isNull(this.currentLoadedFile)) {
                        if (!_.isUndefined(this.currentLoadedFile.title) && !_.isNull(this.currentLoadedFile.title)) {
                            $("#top-ui-cloud-current-document").show();
                            $("#top-ui-cloud-current-document .cloud-selected-icon").text(this.currentLoadedFile.title);
                            $("#top-ui-cloud-connected").show();
                        }
                    }
                }

                , handleDOMWhenSignOut: function (event) {
                    Utils.createOrSetCookieByName(WindowConfig.cloudCookieId, "");
                    this.handleNotConnectedCloud();
                }

                , handleNotConnectedCloud: function () {
                    $("#top-ui-cloud-connected").find('.cloud-connected-icon').removeClass("googledriveIcon");
                    $("#top-ui-cloud-connected").find('.cloud-connected-icon').removeClass("onedriveIcon");
                    $("#top-ui-cloud-connected").hide();
                    $("#top-ui-cloud-sign-in").show();
                }

                /*
                 * This is triggered when click of Rename button. The actual renaming is happening during confirmRenameDocument
                 * (confirmRenameDocument)
                 * */
                , renameDocumentInDom: function (event) {
                    this.event.cloud_rename_button();
                    var _that = this;
                    var target = $(event.currentTarget);
                    this.toggleMainActions();
                    var parentElem = $(target).closest(".manage-document-tile");
                    var filenameElem = $(parentElem).find(".cloud-document-filename");
                    var filenameParentElem = filenameElem.closest(".manage-document-link");
                    this.oldFilename = $(filenameElem).text();
                    $(filenameElem).replaceWith("<input class='cloud-document-filename'>");
                    filenameParentElem.find(".cloud-document-filename").addClass("rename-selected");
                    $(parentElem).find(".cloud-document-filename").val(this.oldFilename).select();
                    $(target).removeClass('btn-cloud-rename').addClass('btn-cloud-rename-confirm')
                            .removeClass(_that.renameCSS).addClass(_that.renameConfirmCSS);
                    event.stopPropagation();
                }

                , confirmRenameDocument: function (event) {
                    $("body").trigger("europass:waiting:indicator:cloud:show", true);
                    var _that = this;
                    var target = $(event.currentTarget);
                    var parentElem = $(target).closest(".manage-document-tile");
                    var cloudDocumentId = $(parentElem).find(".radio-cloud-documents").data('document-id');
                    var inputRenamed = $(parentElem).find('.cloud-document-filename');
                    var newFilename = Utils.htmlEscaping($.trim($(inputRenamed).val()));
                    // check empty filename:
                    if (newFilename === '') {
                        _that.toggleRenameAction();
                        return;
                    }

                    $(inputRenamed).replaceWith("<span class='cloud-document-filename'>" + newFilename + "</span>");
                    var elem = $(parentElem).find('.cloud-document-filename');
                    $(target).removeClass('btn-cloud-rename-confirm').addClass('btn-cloud-rename')
                            .removeClass(_that.renameConfirmCSS).addClass(_that.renameCSS);

                    //check if the file to rename is the current loaded file
                    if (!_.isNull(this.currentLoadedFile) && cloudDocumentId === this.currentLoadedFile.id)
                        var loaded = true;

                    switch (this.cloudProvider) {
                        case "googledrive":
                            $("body").trigger("europass:cloud:google:renameDocument", [cloudDocumentId, newFilename + this.extension,
                                this.oldFilename, elem, loaded]);
                            break;
                        case "onedrive":
                            $("body").trigger("europass:cloud:onedrive:renameDocument", [cloudDocumentId, newFilename + this.extension,
                                this.oldFilename, elem, loaded]);
                            break;
                        default:
                            break;
                    }
                    event.stopPropagation();
                }

                , onKeyDownRename: function (event) {
                    var code = (event.keyCode ? event.keyCode : event.which);
                    var inputElement = $(event.currentTarget);
                    if (code === 27) { // ESC key
                        this.toggleRenameAction();
                    } else if (code === 13) { // ENTER key
                        inputElement.closest('.manage-document-container').find('.btn-cloud-rename-confirm').click();
                        return false;
                    }
                }

                , onSelectingInputRename: function (event) {
                    event.stopPropagation();
                }

                /**
                 * Function to clone a manage-document-tile in DOM, based on the new filename and 
                 * the id of the file
                 */
                , cloneDocumentInDom: function (event, fileId, filename, modifiedDate, sourceUrl, parentElem) {
                    var clonedElem = $(parentElem).clone();
                    $(clonedElem).find('.post-share-action').remove();
                    $(clonedElem).find('.btn-cloud-share').prop('disabled', false);

                    var title = Utils.getFilenameTitle(filename);

                    var inputRadioElem = clonedElem.find("input.radio-cloud-documents");
                    if (inputRadioElem.is(':checked')) {
                        inputRadioElem.prop('checked', false);
                    }
                    inputRadioElem.attr("data-document-id", fileId);
                    inputRadioElem.attr("data-filename", filename);
                    inputRadioElem.attr("data-source-url", sourceUrl);

                    clonedElem.find("span.cloud-document-filename").html(title);
                    clonedElem.find("span.lastModifiedDate").html(modifiedDate + ", ");
                    clonedElem.hide().insertBefore($(".manage-document-tile").first()).show('slow');

                    this.toggleSelection();
                }

                , renameError: function (event, oldFilename, element) {
                    $(element).text(oldFilename);
                }

                , eraseAndUpdate: function (event) {
                    var fileId = this.currentLoadedFile.id;
                    switch (this.cloudProvider) {
                        case "googledrive":
                            $("body").trigger("europass:cloud:google:updateContent", [fileId, this.googleDriveFolder, JSON.parse(this.model.conversion().toTransferable())]);
                            break;
                        case "onedrive":
                            $("body").trigger("europass:cloud:onedrive:createDocument", [this.currentLoadedFile.title + this.extension, this.model.conversion().toTransferable(), true]);
                            break;
                        default: //offline
                            var lastModifiedDate = JSON.parse(this.model.conversion().toTransferable()).SkillsPassport.DocumentInfo.LastUpdateDate;
                            $("body").trigger("europass:cloud:manage:update:content:current", {"lastUpdate": lastModifiedDate});
                            break;
                    }
                }

                /**
                 * Function to actually clone a file in the cloud service file system (using default filename)
                 * @param {type} event
                 * @returns {undefined}
                 */
                , cloneDocument: function (event) {
                    $("body").trigger("europass:waiting:indicator:cloud:show", true);
                    this.event.cloud_duplicate_button();
                    this.toggleMainActions();
                    var target = $(event.currentTarget);
                    var parentElem = $(target).closest(".manage-document-tile");
                    var cloudDocumentId = $(parentElem).find(".manage-document-radio input").data('document-id');
                    var locale = $(parentElem).find(".manage-document-radio input").data('locale');
                    var cloudDocumentName = this.getDefaultFilename(locale) + this.extension;
                    switch (this.cloudProvider) {
                        case "googledrive":
                            $("body").trigger("europass:cloud:google:cloneDocument", [cloudDocumentId, cloudDocumentName, parentElem]);
                            break;
                        case "onedrive":
                            $("body").trigger("europass:cloud:onedrive:cloneDocument", [cloudDocumentId, cloudDocumentName, parentElem]);
                            break;
                        default:
                            break;
                    }
                    event.stopPropagation();
                }

                , toggleMainActions: function () {
                    this.toggleRenameAction();
                    this.toggleDeleteAction();
                    this.toggleShareAction();
                }

                , toggleShareConfirm: function (event) {
                    this.event.cloud_share_button();
                    var shareBtn = $(event.currentTarget);
                    var containerElem = $(shareBtn).closest('.manage-document-container');
                    var confirmBubble = shareBtn.closest(containerElem).siblings(".manage-action.link-share");
                    if (confirmBubble.length === 0)
                        return;
                    this.toggleMainActions();
                    confirmBubble.toggle();
                    shareBtn.toggleClass("in-action");
                    containerElem.find("button").prop('disabled', true);
                    event.stopPropagation();
                }

                , toggleShareAction: function () {
                    this.$el.find(this.shareReviewNoficationSelector).trigger("europass:message:clear");
                    var otherActivePrompt = $("#cloudManageDocumentsRadioForm").find('.btn-cloud-share.in-action');
                    if (otherActivePrompt.length > 0) {
                        var otherActiveOption = otherActivePrompt.closest(".manage-document-container").siblings(".manage-action.link-share");
                        otherActiveOption.hide();
                        otherActivePrompt.closest(".manage-document-container").find("button").prop('disabled', false);
                        otherActivePrompt.toggleClass("in-action");
                    }
                }
                , rejectShareAction: function (event) {
                    var ev = $(event.target);
                    var bubble = ev.closest(".manage-action.link-share");
                    var containerElem = bubble.closest(".manage-document-tile").find(".manage-document-container").first();
                    containerElem.find("button").prop('disabled', false);
                    if (bubble.length === 0)
                        return;
                    bubble.fadeToggle("slow", function () {
                        $(this).hide();
                    });
                    event.stopPropagation();
                }

                , shareDocumentStopPropagation: function (event) {
                    event.stopPropagation();
                }

                , shareDocument: function (event) {
                    var target = $(event.currentTarget);
                    var parentElem = $(target).closest(".manage-document-tile");
                    var cloudDocumentId = $(parentElem).find(".manage-document-radio input").data('document-id');

                    this.$el.find(this.shareReviewNoficationSelector).trigger("europass:message:clear");
                    var sender = $(parentElem).find("input[type$=\"email\"]").val();
                    var validEmail = false;
                    var emptyEmail = _.isEmpty(sender);
                    if (!emptyEmail) {
                        validEmail = Utils.isValidEmail(sender);
                    }
                    if (emptyEmail) {
                        $(parentElem).find('.share-cloud-notification-area').trigger("europass:message:show", ["error", EditorHelp["ContactInfo.Email.NoReply.Alert"], false])
                    } else if (!validEmail) {
                        $(parentElem).find('.share-cloud-notification-area').trigger("europass:message:show", ["error", Notification["feedback.invalid.sender.email.address"], false]);
                    }

                    if (validEmail && !emptyEmail) {
                        switch (this.cloudProvider) {
                            case "googledrive":
                                $("body").trigger("europass:waiting:indicator:cloud:show", true);
                                $("body").trigger("europass:cloud:google:shareDocument", [target, cloudDocumentId]);
                                break;
                            case "onedrive":
                                console.log('Not supported for now !!');
                                break;
                            default:
                                break;
                        }
                    }
                    event.stopPropagation();
                }

                , postShareAction: function (event, target, permissionID, clonedocumentid) {
                    var shareBtn = $(target);
                    var containerElem = $(shareBtn).closest('.manage-document-tile');
                    var confirmBubble = $(containerElem).find('.post-share-action');
                    if (confirmBubble.length === 0)
                        return;
                    this.toggleMainActions();
                    confirmBubble.toggle();
                    shareBtn.toggleClass("in-action");

                    // disable also share button !!
                    $(containerElem).find("button.btn-cloud-share").prop('disabled', true);
                    var reviewerEmail = $(containerElem).find(".share-cloud-recipient-email-input").val();
                    $(confirmBubble).find(".post-share-recipient-email").html(Utils.replaceKey(GuiLabel["cloudLogin.drawer.connected.document.share.post.message.action.title"],
                            "--reviewerEmail--", reviewerEmail));
                    $(confirmBubble).find(".post-share-recipient-email").data('permissionid', permissionID);
                    $(confirmBubble).find(".post-share-recipient-email").data('clonedocumentid', clonedocumentid);

                    var senderEmail = $("#top-ui-cloud-sign-in-section").data('emailAccount');
                    var messageEmail = $(containerElem).find(".share-cloud-message-textarea").val()
                            .replace(new RegExp('<script(.|\n)*</script>', 'g'), '')
                            .replace(/(?:\r\n|\r|\n)/g, '<br />');
                    this.sendEmailForReview(event, reviewerEmail, senderEmail, messageEmail, clonedocumentid, permissionID);

                    event.stopPropagation();
                }

                , sendEmailForReview: function (event, reviewerEmail, senderEmail, messageEmail, fileId, permissionID) {

                    var infoModel = new ShareCloudInfoModel();

                    var editorsUrl = window.location.origin + WindowConfig.getDefaultEwaEditorContext();
                    var cloudAppendUrl = '';
                    switch (this.cloudProvider) {
                        case "googledrive":
                            cloudAppendUrl = 'googledrive';
                            break;
                        case "onedrive":
                            // Not supported for now
                            break;
                        default:
                            break;
                    }
                    var shareUrl = "/shareForReview/" + ewaLocale + "/" + cloudAppendUrl + "?language=" + ewaLocale + "&fileId=" + fileId +
                            "&reviewerEmail=" + reviewerEmail + "&senderEmail=" + senderEmail + "&permissionId=" + permissionID;

                    infoModel.set("ShareInfo.Sender", senderEmail);
                    infoModel.set("ShareInfo.Email", reviewerEmail);
                    infoModel.set("ShareInfo.Locale", this.model.get("SkillsPassport.Locale"));
                    infoModel.set("ShareInfo.Link", editorsUrl + shareUrl);
                    infoModel.set("ShareInfo.FullName", $("#top-ui-cloud-sign-in-section").data('displayName') || "");
                    infoModel.set("ShareInfo.Message", messageEmail);

                    var data = infoModel.get("ShareInfo");
                    var httpResource = new Resource(ServicesUri.shareReview);
                    httpResource.contentType(MediaType.json);

                    var _that = this;
                    httpResource._post({
                        data: data
                    }, {
                        success: {
                            scope: _that,
                            callback: function (response) {

                                var elem = $("<div id='share-send-email-response-status'></div>").append(response);
                                var responseStr = elem.find("script[type=\"application/json\"]").html();

                                if (responseStr === undefined || responseStr === null || responseStr === "") {
                                    _that.cloudViewHelper.triggerResponseError("", "Error no response!", "drawer");
                                    return;
                                }
                                var respStatus = $.parseJSON(responseStr);
                                if (respStatus.EmailStatus !== "SENT") {
                                    _that.cloudViewHelper.triggerResponseError("", "Error sending email!", "drawer");
                                    return;
                                }

                                $("body").trigger("europass:share:response:success");
                            }
                        },
                        complete: {
                            scope: _that,
                            callback: function (status, responseText) {
                                $("body").trigger("europass:waiting:indicator:cloud:hide", true);
                            }
                        }
                    });
                }

                , stopShareAction: function (event) {
                    $("body").trigger("europass:waiting:indicator:cloud:show", true);
                    var target = $(event.target);
                    var parentElem = $(target).closest(".manage-document-tile");
                    var permissionId = $(parentElem).find(".post-share-recipient-email").data('permissionid');
                    var cloudDocumentId = $(parentElem).find(".post-share-recipient-email").data('clonedocumentid');
                    var originalDocumentId = $(parentElem).find(".manage-document-radio input").data('document-id');

                    switch (this.cloudProvider) {
                        case "googledrive":
                            $("body").trigger("europass:cloud:google:stopSharingDocument", [target, cloudDocumentId, permissionId, originalDocumentId]);
                            break;
                        case "onedrive":
                            console.log('Not supported for now !!');
                            break;
                        default:
                            break;
                    }
                    event.stopPropagation();
                }
                , postStopSharingAction: function (event, target) {
                    var bubble = $(target).closest(".post-share-action");
                    var containerElem = $(bubble).closest('.manage-document-tile');
                    containerElem.find("button").prop('disabled', false);
                    if (bubble.length === 0)
                        return;
                    bubble.fadeToggle("slow", function () {
                        $(this).hide();
                    });

                    event.stopPropagation();
                }

                , toggleDeleteConfirm: function (event) {
                    this.event.cloud_delete_button();
                    var deleteBtn = $(event.currentTarget);
                    var containerElem = $(deleteBtn).closest('.manage-document-container');
                    var confirmBubble = deleteBtn.closest(containerElem).siblings(".manage-action.link-delete");
                    if (confirmBubble.length === 0)
                        return;

                    this.toggleMainActions();
                    confirmBubble.toggle();
                    deleteBtn.toggleClass("in-action");
                    containerElem.find("button").prop('disabled', true);
                    event.stopPropagation();
                }

                , toggleDeleteAction: function () {
                    var otherActivePrompt = $("#cloudManageDocumentsRadioForm").find('.btn-cloud-delete.in-action');
                    if (otherActivePrompt.length > 0) {
                        var otherActiveOption = otherActivePrompt.closest(".manage-document-container").siblings(".manage-action.link-delete");
                        otherActiveOption.hide();
                        otherActivePrompt.closest(".manage-document-container").find("button").prop('disabled', false);
                        otherActivePrompt.toggleClass("in-action");
                    }
                }

                , rejectDeleteAction: function (event) {
                    var ev = $(event.target);
                    var bubble = ev.closest(".manage-action.link-delete");
                    var containerElem = bubble.closest(".manage-document-tile").find(".manage-document-container").first();
                    containerElem.find("button").prop('disabled', false);
                    if (bubble.length === 0)
                        return;
                    bubble.fadeToggle("slow", function () {
                        $(this).hide();
                    });
                    event.stopPropagation();
                }

                , deleteDocument: function (event) {
                    $("body").trigger("europass:waiting:indicator:cloud:show", true);
                    var target = $(event.currentTarget);
                    var parentElem = $(target).closest(".manage-document-tile");
                    var cloudDocumentId = $(parentElem).find(".manage-document-radio input").data('document-id');
                    switch (this.cloudProvider) {
                        case "googledrive":
                            $("body").trigger("europass:cloud:google:deleteDocument", [cloudDocumentId, parentElem]);
                            break;
                        case "onedrive":
                            $("body").trigger("europass:cloud:onedrive:deleteDocument", [cloudDocumentId, parentElem]);
                            break;
                        default:
                            break;
                    }
                    event.stopPropagation();
                }
                , deleteDocumentInDom: function (event, parentElem, fileId) {
                    //check if the item to delete is already selected
                    var _that = this;

                    $(parentElem).hide('slow', function () {
                        $(parentElem).remove();
                        if (parentElem.hasClass("cloud-file-selected")) {
                            _that.checkFirstRadio();
                        } else {
                            _that.toggleSelection();
                        }
                        if (fileId === _that.currentLoadedFile.id) {
                            _that.postDeleteAction(event);
                        } else {
                            $("body").trigger("europass:waiting:indicator:cloud:hide", true);
                        }
                    });
                }

                , postDeleteAction: function (event) {
                    //check that the document to delete is the current loaded cv
                    if (!_.isNull(this.currentLoadedFile)) {
                        this.eraseCurrentLoadedFile();
                        var filename = this.getDefaultFilename(ewaLocale.toUpperCase()) + this.extension;
                        var nextState;
                        if ($(".radio-cloud-documents").length === 0)
                            nextState = "createNewAfterDelete";
                        else {
                            nextState = "loadLatestAfterDelete";
                            var nextDocumentId = $(".radio-cloud-documents").data('document-id');
                            var nextFileUrl = $(".radio-cloud-documents").data('source-url');
                            var lastSelElem = $(".radio-cloud-documents")[0];
                        }
                    }
                    switch (this.cloudProvider) {
                        case "googledrive":
                            $("body").trigger("europass:cloud:google:postDeleteDocument", [nextState, this.googleDriveFolder, filename, lastSelElem]);
                            break;
                        case "onedrive":
                            $("body").trigger("europass:cloud:onedrive:postDeleteDocument", [nextState, filename, nextFileUrl, lastSelElem]);
                            break;
                        default:
                            break;
                    }
                }

                , uploadDocument: function (event) {
                    $("body").trigger("europass:waiting:indicator:cloud:show", true);
                    this.event.cloud_create_new_file();
                    var defaultFilename = this.getDefaultFilename(ewaLocale.toUpperCase()) + this.extension;
                    this.toggleMainActions();
                    switch (this.cloudProvider) {
                        case "googledrive":
                            $("body").trigger("europass:cloud:google:createDocument", [defaultFilename, this.googleDriveFolder]);
                            break;
                        case "onedrive":
                            $("body").trigger("europass:cloud:onedrive:createDocument", [defaultFilename]);
                            break;
                        default:
                            break;
                    }
                }

                , uploadLocalDocumentToCloud: function (event, json, loaded) {
                    var defaultFilename = this.getDefaultFilename(ewaLocale.toUpperCase()) + this.extension;
                    if (!_.isUndefined(json) && !Utils.isSkillsPassportObjectEmpty(json)) { //not empty cv
                        if (!_.isUndefined(json.SkillsPassport.LearnerInfo.Identification) &&
                                !_.isUndefined(json.SkillsPassport.LearnerInfo.Identification.PersonName)
                                && !_.isUndefined(json.SkillsPassport.LearnerInfo.Identification.PersonName.Surname)) {
                            defaultFilename = this.filenamePrefix[0] + "-" + json.SkillsPassport.LearnerInfo.Identification.PersonName.Surname + "-"
                                    + Utils.getCurrentTimestamp().date + "-" + ewaLocale.toUpperCase() + "-" + Utils.getCurrentTimestamp().time + this.extension;
                        }
                    }
                    switch (this.cloudProvider) {
                        case "googledrive":
                            $("body").trigger("europass:cloud:google:createDocument", [defaultFilename, this.googleDriveFolder, json, loaded]);
                            break;
                        case "onedrive":
                            $("body").trigger("europass:cloud:onedrive:createDocument", [defaultFilename, JSON.stringify(json), , , loaded]);
                            break;
                        default:
                            break;
                    }
                }

                , uploadExistingDocumentToCloud: function (event, json) {
                    var newTitle = this.buildCVTitleFromSurname(json);
                    if (!_.isUndefined(newTitle)) {
                        var filename = newTitle + this.extension;
                    }
                    var fileId = _.isNull(this.currentLoadedFile) ? null : this.currentLoadedFile.id;
                    var fileNameCurrent = _.isNull(this.currentLoadedFile) ? null : this.currentLoadedFile.title + this.extension;
                    this.uploadGeneric(filename, fileId, json, undefined, fileNameCurrent);
                }

                , uploadGeneric: function (filenameNew, fileId, json, loaded, fileNamePrevious) {
                    switch (this.cloudProvider) {
                        case "googledrive":
                            $("body").trigger("europass:cloud:google:updateContent", [fileId, this.googleDriveFolder, JSON.parse(json), filenameNew]);
                            break;
                        case "onedrive":
                            $("body").trigger("europass:cloud:onedrive:updateContent", [fileNamePrevious, json, filenameNew]);
                            break;
                        default: //offline
                            var lastModifiedDate = JSON.parse(json).SkillsPassport.DocumentInfo.LastUpdateDate;
                            $("body").trigger("europass:cloud:manage:update:content:current", {"lastUpdate": lastModifiedDate, "title": filenameNew});
                            break;
                    }
                }

                , getDefaultFilename: function (locale) {
                    return this.filenamePrefix[0] + this.filenamePrefix[1] +
                            Utils.getCurrentTimestamp().date + "-" + locale + "-" + Utils.getCurrentTimestamp().time;
                }

                , handleClickRadio: function () {
                    this.toggleSelection();
                    this.hideDeleteBubbles();
                    this.toggleMainActions();
                }

                , checkFirstRadio: function (event) {
                    $(".manage-document-tile").first().find("input.radio-cloud-documents").prop("checked", true);
                    this.toggleSelection();
                }

                , checkRadioOnLoadedFile: function (event) {
                    var queryTitle = this.currentLoadedFile.title;
                    var loadedElem = $(".manage-document-container").find("span.cloud-document-filename").filter(function () {
                        return $(this).text() === queryTitle;
                    });
                    if (loadedElem.length === 0) {
                        this.checkFirstRadio(event);
                        return;
                    }
                    $(loadedElem).closest('.manage-document-container').find("input.radio-cloud-documents").prop("checked", true);
                    this.toggleSelection();
                }

                , triggerResponseError: function (event, errCode, errMessage, messageLocation) {
                    this.cloudViewHelper.triggerResponseError(errCode, errMessage, messageLocation);
                }

                , triggerResponseSuccess: function (event, success) {
                    this.cloudViewHelper.triggerResponseSuccess(success);
                }

                , onModelChangeUploadCV: function (event) {
                    //check if there is a current loaded file
                    var json = this.model.conversion().toTransferable();
                    var newTitle = this.buildCVTitleFromSurname(json);
                    if (!_.isNull(this.currentLoadedFile)) {
                        if (!_.isUndefined(this.currentLoadedFile.id) && !_.isNull(this.currentLoadedFile.id)) {

                            if (_.isUndefined(newTitle) && !_.isUndefined(this.currentLoadedFile.title)) {
                                newTitle = Utils.updateCVTitleFromLocale(this.currentLoadedFile.title, ewaLocale);
                            }
                            switch (this.cloudProvider) {
                                case "googledrive":
                                    $("body").trigger("europass:cloud:google:updateContent", [this.currentLoadedFile.id, this.googleDriveFolder, JSON.parse(json), newTitle + this.extension]);
                                    break;
                                case "onedrive":
                                    $("body").trigger("europass:cloud:onedrive:updateContent", [this.currentLoadedFile.title + this.extension, json, newTitle + this.extension]);
                                    break;
                                default: //offline
                                    var lastModifiedDate = JSON.parse(json).SkillsPassport.DocumentInfo.LastUpdateDate;
                                    $("body").trigger("europass:cloud:manage:update:content:current", {"lastUpdate": lastModifiedDate, "title": newTitle});
                                    break;
                            }
                        }
                    }
                }

                , buildCVTitleFromSurname: function (json) {
                    var jsonModel = JSON.parse(json);
                    if (jsonModel.SkillsPassport.LearnerInfo) {
                        if (!_.isUndefined(jsonModel.SkillsPassport.LearnerInfo.Identification) &&
                                !_.isUndefined(jsonModel.SkillsPassport.LearnerInfo.Identification.PersonName)) {
                            var cvSurname = jsonModel.SkillsPassport.LearnerInfo.Identification.PersonName.Surname;
                        }
                        if (!_.isUndefined(cvSurname) && this.currentLoadedFile !== null && typeof this.currentLoadedFile.title !== 'undefined'
                                && cvSurname !== this.currentLoadedFile.surname)
                        {
                            var partToReplace = "";
                            if (this.currentLoadedFile.surname === "") { //blank document
                                partToReplace = this.filenamePrefix[0] + this.filenamePrefix[1];
                            } else {
                                partToReplace = this.filenamePrefix[0] + "-" + this.currentLoadedFile.surname + "-";
                            }
                            this.currentLoadedFile.surname = cvSurname;
                            return this.currentLoadedFile.title.replace(partToReplace,
                                    this.filenamePrefix[0] + '-' + cvSurname + '-');
                        }
                    }
                }


                , confirmLoadDocumentAfterConnect: function (event, recentCloudObj) {
                    var html = ConfirmationTpl(context);
                    var confirmModal = Utils.prepareModal("confirmCloudLoadDocumentModal", html, undefined, undefined, true);
                    $(confirmModal).dialog("option", "closeOnEscape", false);
                    $(confirmModal).dialog("open");
                    this.recentCloudObj = recentCloudObj;
                }

                , confirmSaveLocalCVNewVersion: function (event) {
                    this.event.cloud_upload_keep_existing_document();
                    var model = JSON.parse(this.model.conversion().toTransferable());
                    this.uploadLocalDocumentToCloud(event, model, "loadAlso");
                    $("#confirmCloudLoadDocumentModal").dialog("close");
                }

                , confirmDiscardLocalCVNewVersion: function (event) {
                    this.event.cloud_upload_discard_existing_document();
                    switch (this.cloudProvider) {
                        case "googledrive":
                            this.loadAndStoreCurrentDocumentLocalStorage(event, this.recentCloudObj.recentDataContent,
                                    this.recentCloudObj.recentData.id,
                                    Utils.getFilenameTitle(this.recentCloudObj.recentData.filename),
                                    this.recentCloudObj.recentDataContent.SkillsPassport.DocumentInfo.LastUpdateDate);
                            break;
                        case "onedrive":
                            this.loadAndStoreCurrentDocumentLocalStorage(event, this.recentCloudObj.recentDataContent,
                                    this.recentCloudObj.recentData.id,
                                    Utils.getFilenameTitle(this.recentCloudObj.recentData.name),
                                    this.recentCloudObj.recentDataContent.SkillsPassport.DocumentInfo.LastUpdateDate);
                            break;
                        default:
                            break;
                    }
                    $("#confirmCloudLoadDocumentModal").dialog("close");
                    $("body").trigger("europass:waiting:indicator:cloud:hide", true);
                }

            });

            return ManageDocumentsView;
        }
);