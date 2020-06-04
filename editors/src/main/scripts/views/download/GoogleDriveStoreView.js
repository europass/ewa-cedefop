define(
        [
            'module',
            'jquery',
            'underscore',
            'backbone',
            'hbs!templates/download/googledrive',
            'europass/http/WindowConfigInstance',
            'europass/http/Resource',
            'europass/http/MediaType',
            'views/download/DownloadController',
            'views/upload/google/ApiManager',
            'i18n!localization/nls/GuiLabel',
            'analytics/EventsController'
        ],
        function (module, $, _, Backbone,
                Template,
                WindowConfig,
                Resource,
                MediaType,
                DownloadController,
                ApiManager,
                GuiLabel,
                Events
                ) {
            var GoogleDriveStoreView = Backbone.View.extend({

                name: "googledrive",
                alreadyRendered: false,
                scopes: "https://www.googleapis.com/auth/drive.apps.readonly " + "https://www.googleapis.com/auth/drive.file",
                pickerApiLoaded: false,
                accessToken: null,

                defaultStoreFolderID: 'root',
                event: new Events,
                events: {
                    "europass:popup:enabled": "doConnectLogin"
                },

                /**
                 * Cleanup
                 */
                onClose: function () {
                    delete this.accessToken;
                    delete this.pickerApiLoaded;
                    delete this.apiManager;
                    this.apiManager === null

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

                    this.parentView = options.parentView;

                    this.messageContainer = options.messageContainer;

                    //Reusable Upload Controller
                    this.downloadController = new DownloadController({
                        relatedController: this,
                        messageContainer: this.messageContainer,
                        info: options.info
                    });

                    this._url = this.downloadController.decideUrl();

                    this.appId = WindowConfig.googledriveAppId;
                    this.clientId = WindowConfig.googledriveClientId;
                    this.devKey = WindowConfig.googledriveDevKey;

                    this.apiManager = null;

                    this.googleApiRequestURL = "https://www.googleapis.com/drive/v2/files";

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
                    this.event.exportTo(this._url);
                    this.parentView.checkPopUp(this.$el);
                },

                doConnectLogin: function () {
                    $("body").trigger("europass:waiting:indicator:show", true);

                    this.apiManager = new ApiManager({
                        apiKey: this.appId,
                        clientId: this.clientId,
                        scopes: this.scopes,
                        callback: this.apiManagerCallback,
                        scope: this
                    });

                    this.apiManager.loadGapi();
                },
                apiManagerCallback: function (authResult) {
//			console.log( authResult );
//			$("body").trigger("europass:waiting:indicator:show");
                    this.accessToken = authResult.access_token;

                    gapi.load('picker', {'callback': $.proxy(this.onPickerApiLoad, this)});
                },
                /**
                 * Callback on picker loading
                 */
                onPickerApiLoad: function () {
//			$("body").trigger("europass:waiting:indicator:show");
//			console.log("picker api loaded");
                    this.pickerApiLoaded = true;

                    this.doCreatePicker();
                },
                doCreatePicker: function () {

                    if (WindowConfig.browserName === "MSIE 9.0") {
                        this.createPicker({
                            callback: $.proxy(this.storeCallback, this),
                            dialogTitle: (GuiLabel["skillspassport.export.cloud.dialog.title.googledrive"] || "GuiLabel[\"skillspassport.export.cloud.dialog.title.google\"]"),
                            mimeTypes: "application/vnd.google-apps.folder"
                        });
                    } else {
                        this.checkFolderExists(WindowConfig.cloudExportStorageFolder);
                    }
                },
                /**
                 * Create a Google Picker.
                 * This is reused by both
                 * @param config:
                 * {
                 *   callback    : function,
                 *   dialogTitle : string,
                 *   mimeTypes   : string of comma separated texts if multiple
                 * }
                 */
                createPicker: function (config) {
//			console.log("createPicker");
                    $("body").trigger("europass:waiting:indicator:hide");
                    if (this.pickerApiLoaded && this.accessToken) {
//				console.log("ok!!");

                        var callback = config.callback;
                        var title = config.dialogTitle;
                        var mimeTypes = config.mimeTypes;

                        var docsView = new google.picker.DocsView(google.picker.ViewId.FOLDERS)
                                .setParent('root')
//									.setMode(google.picker.DocsViewMode.LIST)
                                .setIncludeFolders(true)
                                .setSelectFolderEnabled(true);

                        var pickerBuilder = new google.picker.PickerBuilder()
                                .addView(docsView)
                                .setOrigin(window.location.protocol + "//" + window.location.host)
                                .setAppId(this.appId)
                                .setOAuthToken(this.accessToken)
                                .setDeveloperKey(this.devKey)
                                .setCallback(callback)
                                .disableFeature(google.picker.Feature.MULTISELECT_ENABLED)
                                .disableFeature(google.picker.Feature.NAV_HIDDEN)
                                .setSelectableMimeTypes(mimeTypes)
                                .setLocale(module.config().locale || "en")
                                .setTitle(title)
                                .setSize(751, 450);

                        var picker = pickerBuilder.build();

                        picker.setVisible(true);
                    } else {
//				console.log("authentication missing to create picker!");
                        $("body").trigger("europass:waiting:indicator:hide");
                        this.parentView.enableButton(this.parentView.connectBtn);
                        return;
                    }
                },
                /**
                 * Check if folder exeists
                 * PGIA: Construct the query for the request of elements that:
                 * 
                 * 1. Are of type 'folder'               ( mimeType = 'application/vnd.google-apps.folder' ) 
                 * 2. Their name is equal to given param ( title = {folderName} ) 
                 * 3. Are not in trash                   ( trashed = false )
                 * 
                 * Pass the query string as url-encoded
                 * @param folderName
                 */
                checkFolderExists: function (folderName) {

                    var _that = this;
                    var httpResource = new Resource(this.googleApiRequestURL);
                    httpResource.contentType(MediaType.json);
                    httpResource.header("Authorization", "Bearer " + this.accessToken + "");
                    httpResource._params = {"q": "mimeType = 'application/vnd.google-apps.folder' and title ='" + folderName + "' and trashed = false"};
                    httpResource._get({
                        success: {
                            scope: _that,
                            callback: function (response) {
                                if (_.isNull(response) || _.isUndefined(response.items))
                                    return;

                                var items = response.items;
                                if (!_.isArray(items) || _.isEmpty(items)) {

                                    // If no items were found, then create the folder
                                    _that.createDefaultFolder();
                                }

                                _that.createPicker({
                                    callback: $.proxy(_that.storeCallback, _that),
                                    dialogTitle: (GuiLabel["skillspassport.export.cloud.dialog.title.googledrive"] || "GuiLabel[\"skillspassport.export.cloud.dialog.title.google\"]"),
                                    mimeTypes: "application/vnd.google-apps.folder"
                                });

                            }
                        },
                        error: {
                            scope: _that,
                            callback: function (status, responseText) {
                                this.downloadController.onUploadFailure(status, responseText);
                                this.parentView.enableConnect(true);
                            }
                        },
                    });
                },

                /**
                 * Function that runs if the default store folder does not exist 
                 * 
                 */
                createDefaultFolder: function () {

                    var _that = this;
                    var httpResource = new Resource(this.googleApiRequestURL);
                    httpResource.contentType(MediaType.json);
                    httpResource.header("Authorization", "Bearer " + this.accessToken + "");

                    httpResource._post(_that.downloadController.prepareFolder(WindowConfig.cloudExportStorageFolder), {
                        success: {
                            scope: _that,
                            callback: function (response) {
                                console.log("Folder \"" + response.title + "\" (" + response.id + ") created successfully");
                            }
                        },
                        error: {
                            scope: _that,
                            callback: function (status, responseText) {
                                this.downloadController.onUploadFailure(status, responseText);
                                this.parentView.enableConnect(true);
                            }
                        },
                    });


                },

                /**
                 * Callback to run as soon as a folder is selected from Google Picker
                 * @param data
                 */
                storeCallback: function (data) {
                    var doc = null;
                    if (data[google.picker.Response.ACTION] == google.picker.Action.PICKED) {
                        doc = data[google.picker.Response.DOCUMENTS][0];
                    }
                    if (data[google.picker.Response.ACTION] == google.picker.Action.CANCEL) {

//				this.messageContainer
//				.trigger("europass:message:show", 
//					["warning", ( Notification["skillspassport.export.cloud.cancelled.googledrive"] || "Action Cancelled") , true]);
                        return;
                    }
                    if (doc === null) {
                        return;
                    } else {
                        $("body").trigger("europass:waiting:indicator:show", true);

                        var id = doc[google.picker.Document.ID];

                        var _that = this;

                        var httpResource = new Resource(_that._url);
                        httpResource.contentType(MediaType.json);

                        httpResource._post({
                            data: _that.prepareData(_that.accessToken, id)
                        }, {
                            success: {
                                scope: _that,
                                callback: function (response) {
                                    var filename = $(response).filter("meta[name='filename']").attr("content");
                                    if (_.isUndefined(filename))
                                        filename = "Europass CV";

                                    this.downloadController.triggerMessageWithPathFile("skillspassport.export.cloud.success.googledrive", doc["name"] + "/" + filename, "success", false);

                                    this.$el.trigger("europass:wizard:export:complete");

                                }
                            },
                            error: {
                                scope: _that,
                                callback: function (status, responseText) {
                                    this.downloadController.onUploadFailure(status, responseText);

                                }
                            },
                            complete: {
                                scope: _that,
                                callback: function (status, responseText) {
                                    $("body").trigger("europass:waiting:indicator:hide");
                                }
                            }
                        });
                    }
                }
            });

            return GoogleDriveStoreView;
        }
);