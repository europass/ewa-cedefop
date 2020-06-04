define(
        [
            'module',
            'jquery',
            'underscore',
            'backbone',
            'Utils',
            'i18n!localization/nls/GuiLabel',
            'europass/http/WindowConfigInstance',
            'views/upload/google/ApiManager'
        ],
        function (module, $, _, Backbone, Utils, GuiLabel, WindowConfig, ApiManager) {

            var GoogleDriveUploadAttachmentsView = Backbone.View.extend({
                scopes: "https://www.googleapis.com/auth/drive.apps.readonly " + "https://www.googleapis.com/auth/drive.file",
                pickerApiLoaded: false,
                isPhoto: false,
                isSignature: false,
                oauthToken: null,

                onClose: function () {
                    delete this.pickerApiLoaded;
                    delete this.oauthToken;
                    delete this.apiManager;
                    this.apiManager = null;
                },

                initialize: function (options) {
                    this.appId = WindowConfig.googledriveAppId;
                    this.clientId = WindowConfig.googledriveClientId;
                    this.devKey = WindowConfig.googledriveDevKey;

                    // WHAT TO DO WITH THE PARENT VIEW ??? TODO
                    this.parentView = options.parentView;
                    this.apiManager = null;
                },

                render: function () {
                    this.parentView.cleanupFeedback();
                    this.importViewGoogleDrive = this.parentView.currentView.cid;
                },

                doConnectLogin: function (isPhoto, isSignature) {

                    this.isPhoto = isPhoto;
                    this.isSignature = isSignature;

                    if (this.importViewGoogleDrive !== this.parentView.currentView.cid)
                        return false;

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
                        this.parentView.getMessageContainer().trigger("europass:message:show", ["error", "Google Authentication error. Try again."]);
                        $("body").trigger("europass:waiting:indicator:hide", true);
                    }

                    this.oauthToken = authResult.access_token;

                    if (this.onPickerApiLoad === true) {
                        this.doCreatePicker(this.isPhoto, this.isSignature);
                    } else {
                        gapi.load('picker', {'callback': $.proxy(this.onPickerApiLoad, this, this.isPhoto, this.isSignature)});
                    }
                },
                /**
                 * Callback on picker loading
                 */
                onPickerApiLoad: function (isPhoto, isSignature) {
                    this.pickerApiLoaded = true;

                    this.doCreatePicker(isPhoto, isSignature);
                },
                doCreatePicker: function (isPhoto, isSignature) {
                    var _that = this;

                    this.createPicker({
                        callback: $.proxy(this.uploadCallback, this, isPhoto, isSignature),
                        dialogTitle: (GuiLabel["SkillsPassport.CV.Upload"] || "Upload attachment"),
                        mimeTypes: _that.getAllowedExtensions(isPhoto, isSignature)
                    });
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
                                .setMimeTypes(mimeTypes);

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
                 * Callback as soon as the person has selected something from the cloud storage
                 * @param data
                 */
                uploadCallback: function (isPhoto, isSignature, data) {

                    var _that = this;
                    var doc = null;

                    if (data[google.picker.Response.ACTION] === google.picker.Action.PICKED) {
                        doc = data[google.picker.Response.DOCUMENTS][0];
                    }
                    if (doc === null || data[google.picker.Response.ACTION] === google.picker.Action.CANCEL) {
                        return;
                    }

                    var id = doc[google.picker.Document.ID];
                    var request = window.gapi.client.drive.files.get({
                        fileId: id
                    });

                    request.execute(function (file) {

                        // force the use of doc.url if cannot be retrieved by file
                        var url = (_.isUndefined(file.downloadUrl) ? doc.url : file.downloadUrl);

                        _that.$el.trigger("europass:googledrive:poc:cloud:attachments",
                                [_that.oauthToken, url, file.title, isPhoto, isSignature]);
                    });
                },

                getAllowedExtensions: function (isPhoto, isSignature) {

                    var mimeTypes = "application/pdf,image/jpeg,image/png";
                    if (isSignature || isPhoto) {
                        mimeTypes = "image/jpeg,image/png"
                    }

                    return mimeTypes;
                }
            });

            return GoogleDriveUploadAttachmentsView;
        }
);