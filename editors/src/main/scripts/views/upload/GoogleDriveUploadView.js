define(
        [
            'module',
            'jquery',
            'underscore',
            'backbone',

            'Utils',

            'hbs!templates/upload/googledrive',

            'europass/http/ServicesUri',
            'europass/http/WindowConfigInstance',
            'routers/SkillsPassportRouterInstance',
            'europass/http/Resource',
            'europass/http/MediaType',
            'views/upload/UploadController',

            'i18n!localization/nls/GuiLabel',

            'views/upload/google/ApiManager',
            'analytics/EventsController',
            'europass/GlobalDocumentInstance'
        ],
        function (
                module, $, _, Backbone, Utils,
                Template,
                ServicesUri, WindowConfig, SkillsPassportRouter, Resource, MediaType, UploadController,
                GuiLabel,
                ApiManager, Events, GlobalDocument) {

            var GoogleDriveUploadView = Backbone.View.extend({
                name: "googledrive",
                scopes: "https://www.googleapis.com/auth/drive.apps.readonly " + "https://www.googleapis.com/auth/drive.file",
                pickerApiLoaded: false,
                oauthToken: null,
                event: new Events,
                events: {
                    "europass:popup:enabled": "doConnectLogin"
                },

                onClose: function () {
                    delete this.pickerApiLoaded;
                    delete this.oauthToken;
                    delete this.apiManager;
                    this.apiManager = null;

                    this.uploadController.cleanup();
                    delete this.uploadController;
                    this.undelegateEvents();
                    this.unbind();
                },
                initialize: function (options) {
                    this.appId = WindowConfig.googledriveAppId;
                    this.clientId = WindowConfig.googledriveClientId;
                    this.devKey = WindowConfig.googledriveDevKey;

                    this.serviceUrl = ServicesUri.document_upload_from_cloud;

                    this.parentView = options.parentView;

                    this.messageContainer = options.messageContainer;

                    // Reusable Upload Controller
                    this.uploadController = new UploadController({
                        relatedController: this,
                        messageContainer: this.messageContainer,
                        modelUpdateEvent: "model:uploaded:cloud",
                        modelUpdateMsgKey: "skillspassport.import.cloud.success.googledrive"
                    });
                    this.apiManager = null;

                    this.googleApiRequestURL = "https://www.googleapis.com/drive/v2/files";
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
                    this.uploadController.uploaded();
                },
                /**
                 * Do Connect
                 */
                doConnect: function () {
                    this.event.importFrom('GoogleDrive');
                    this.parentView.checkPopUp(this.$el);
                },

                doConnectLogin: function () {
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
                    if (_.isUndefined(authResult) || authResult.error) {

                        if (this.downloadController)
                            this.downloadController.onUploadFailure("500", "error");
                    }

                    this.oauthToken = authResult.access_token;

                    if (this.onPickerApiLoad === true) {
                        this.doCreatePicker();
                    } else {
                        gapi.load('picker', {'callback': $.proxy(this.onPickerApiLoad, this)});
                    }
                },
                /**
                 * Callback on picker loading
                 */
                onPickerApiLoad: function () {
                    this.pickerApiLoaded = true;

                    if (WindowConfig.browserName === "MSIE 9.0") {
                        this.createPicker({
                            callback: $.proxy(this.uploadCallback, this),
                            dialogTitle: (GuiLabel["skillspassport.import.cloud.dialog.title.google"] || "Select a Europass PDF+XML or XML file"),
                            mimeTypes: "application/pdf,application/xml"
                        });
                    } else {
                        this.doCreatePicker();
                    }
                },
                doCreatePicker: function () {
                    this.checkDefaultCloudFolderExists(WindowConfig.cloudExportStorageFolder);
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
                    $("body").trigger("europass:waiting:indicator:hide");
                    if (this.pickerApiLoaded && this.oauthToken) {
                        var callback = config.callback;
                        var title = config.dialogTitle;
                        var mimeTypes = config.mimeTypes;

                        var docsView = new google.picker.DocsView()
                                .setParent('root')
                                .setIncludeFolders(true)
                                .setSelectFolderEnabled(false)
                                .setMimeTypes("application/xml,application/pdf");

                        var pickerBuilder = new google.picker.PickerBuilder()
                                .addView(docsView)
                                .setOrigin(window.location.protocol + "//" + window.location.host)
                                .setAppId(this.appId)
                                .setOAuthToken(this.oauthToken)
                                .setDeveloperKey(this.devKey)
                                .setCallback(callback)
                                .disableFeature(google.picker.Feature.MULTISELECT_ENABLED)
                                .setSelectableMimeTypes(mimeTypes)
                                .setLocale(module.config().locale || "en")
                                .setTitle(title)
                                .setSize(751, 450);

                        var picker = pickerBuilder.build();
                        picker.setVisible(true);
                    } else {
                        return;
                    }
                },
                /**
                 * Check if folder exists and return true|false
                 * Construct the query for the request of elements that:
                 * 
                 * 1. Are of type 'folder'               ( mimeType = 'application/vnd.google-apps.folder' ) 
                 * 2. Their name is equal to given param ( title = {folderName} ) 
                 * 3. Are not in trash                   ( trashed = false )
                 * 
                 * Pass the query string as url-encoded
                 * @param folderName
                 */
                checkDefaultCloudFolderExists: function (folderName) {
                    var _that = this;
                    var httpResource = new Resource(this.googleApiRequestURL);
                    httpResource.contentType(MediaType.json);
                    httpResource.header("Authorization", "Bearer " + this.oauthToken + "");
                    httpResource._params = {"q": "mimeType = 'application/vnd.google-apps.folder' and title ='" + folderName + "' and trashed = false"};
                    httpResource._get({
                        success: {
                            scope: _that,
                            callback: function (response) {
                                if (_.isNull(response) || _.isUndefined(response.items))
                                    return;

                                var items = response.items;
                                if (_.isArray(items) && !_.isEmpty(items)) {
                                    _that.defaultCloudFolderID = items["0"].id;
                                }

                                _that.createPicker({
                                    callback: $.proxy(_that.uploadCallback, _that),
                                    dialogTitle: (GuiLabel["skillspassport.import.cloud.dialog.title.google"] || "Select a Europass PDF+XML or XML file"),
                                    mimeTypes: "application/pdf,application/xml"
                                });
                            }
                        },
                        error: {
                            scope: _that,
                            callback: function (status, responseText) {
                                this.uploadController.onUploadFailure(status, responseText);
                            }
                        }
                    });
                },

                /**
                 * Callback as soon as the person has selected something from the cloud storage
                 * @param data
                 */
                uploadCallback: function (data) {
                    if (data.action === 'cancel') {
                        this.onClose();
                    }
                    var doc = null;

                    if (data[google.picker.Response.ACTION] === google.picker.Action.PICKED) {
                        doc = data[google.picker.Response.DOCUMENTS][0];
                    }
                    if (data[google.picker.Response.ACTION] === google.picker.Action.CANCEL) {
                        return;
                    }
                    if (doc === null) {
                        return;
                    } else {
                        // start the waiting indicator...
                        $("body").trigger("europass:waiting:indicator:show", true);

                        var id = doc[google.picker.Document.ID];

                        var request = window.gapi.client.drive.files.get({
                            fileId: id
                        });

                        var _that = this;
                        request.execute(function (file) {
                            // EPAS-2086 broken code - force the use of doc.url if cannot be retrieved by file
                            // var url = (_.isUndefined(file.downloadUrl) ? doc.url : file.downloadUrl);

                            var url = "https://www.googleapis.com/drive/v2/files/" + id + "?alt=media&source=downloadUrl";

                            // pass user-cookie id
                            var cookieId = '';
                            if (Utils.readCookie()) { // user-cookie exists
                                cookieId = Utils.readCookie();
                            }
                            var permissionToKeepNotImportedCv = $('body').find(":input[type=\"checkbox\"]#keep-not-imported-cv-permission:checked").length > 0;
                            var httpResource = new Resource(_that.serviceUrl + "?id=" + cookieId + "&keepCv=" + permissionToKeepNotImportedCv);
                            httpResource.header("Authorization", "Bearer " + _that.oauthToken + "");
                            httpResource._params = {"approval_prompt": "force"};
                            httpResource.contentType(MediaType.json);

                            httpResource._post({
                                data: {
                                    url: url,
                                    token: _that.oauthToken
                                }
                            }, {
                                success: {
                                    scope: _that,
                                    callback: function (response) {
                                        this.uploadController.uploadedCallback(response, doc.name, this.parentView);
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
                        });
                    }
                }
            });

            return GoogleDriveUploadView;
        }
);