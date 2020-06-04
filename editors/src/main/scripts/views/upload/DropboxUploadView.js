define(
        [
            'module',
            'jquery',
//		EWA-1811
//		'underscore',
            'backbone',

            'Utils',
            'hbs!templates/upload/dropbox',

            'europass/http/ServicesUri',
            'europass/http/WindowConfigInstance',
            'europass/http/Resource',
            'europass/http/MediaType',
            'views/upload/UploadController',
            'analytics/EventsController',
//		EWA-1811
//		'i18n!localization/nls/GuiLabel',
//		'i18n!localization/nls/Notification',

            'i18n!localization/nls/GuiLabel',
            'i18n!localization/nls/Notification',

            'dropin'
        ],
        function (
                module, $,
//		EWA-1811
//		_,
                Backbone,
                Utils,
                Template,
                ServicesUri, WindowConfig, Resource, MediaType, UploadController, Events, GuiLabel, Notification
                //	Dropbox
                ) {

            var DropboxUploadView = Backbone.View.extend({
                name: "dropbox",

                btnId: "dropbox-upload",

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
                    this.appKey = WindowConfig.dropboxAppKey;
                    this.callbackUrl = WindowConfig.dropboxCallbackUrl;

                    this.serviceUrl = ServicesUri.document_upload_from_cloud;

                    this.parentView = options.parentView;

                    this.messageContainer = options.messageContainer;

                    //Reusable Upload Controller
                    this.uploadController = new UploadController({
                        relatedController: this,
                        messageContainer: this.messageContainer,
                        modelUpdateEvent: "model:uploaded:cloud",
                        modelUpdateMsgKey: "skillspassport.import.cloud.success.dropbox"
                    });

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
                    this.event.importFrom('Dropbox');
                    this.parentView.checkPopUp(this.$el);
                },

                doConnectLogin: function () {
                    $("body").trigger("europass:waiting:indicator:show", true);

                    //Append a hidden button in the form
                    var btn = this.$el.find("#" + this.btnId);
                    if (btn.length === 0) {
                        $("<a id=\"" + this.btnId + "\" class=\"cloud dropbox\" type=\"button\" ></a>").appendTo(this.$el);
                    }

                    //Trigger programmatically the click on the hidden button
                    var _that = this;
                    setTimeout(function () {

                        try {
                            _that.prepareDropboxChooser(_that.btnId,
                                    ['.pdf', '.xml'],
                                    $.proxy(_that.uploadCallback, _that));

                            var btn = _that.$el.find("#" + _that.btnId);
                            if (btn.length > 0) {
                                //					btn.prop("type", "button");
                                btn.trigger('click');
                                $("body").trigger("europass:waiting:indicator:show", true);
                            }

                        } catch (e) {
                            if (Utils.tryOpeningPopUp() === false) {
                                $("body").trigger("europass:waiting:indicator:hide");
                                _that.messageContainer.trigger("europass:message:show",
                                        ["error", (Notification["skillspassport.wizard.cloud.popup.blocked"]), false]);
                            } else {
                                $("body").trigger("europass:waiting:indicator:hide");
                                _that.messageContainer.trigger("europass:message:show",
                                        ["warning", (Notification["skillspassport.import.cloud.error"]), true]);
                            }
                        }
                    }, 1000);
                },
                /**
                 * Callback as soon as the person has selected something from the cloud storage
                 * @param data
                 */
                uploadCallback: function (files) {
//				console.log( "uploadCallback" );
                    //start the waiting indicator...
                    $("body").trigger("europass:waiting:indicator:show", true);

                    var file = files[0];
                    var url = file.link;

                    //pass user-cookie id	
                    var cookieId = '';
                    if (Utils.readCookie()) { //user-cookie exists
                        cookieId = Utils.readCookie();
                    }

//			console.log("file url: " + url+ "\nservice url: "+this.serviceUrl );	
                    var permissionToKeepNotImportedCv = $('body').find(":input[type=\"checkbox\"]#keep-not-imported-cv-permission:checked").length > 0;
                    var httpResource = new Resource(this.serviceUrl + "?id=" + cookieId + "&keepCv=" + permissionToKeepNotImportedCv);
                    httpResource.contentType(MediaType.json);

                    var _that = this;
                    httpResource._post({
                        data: {
                            url: url
                        }
                    }, {
                        success: {
                            scope: _that,
                            callback: function (response) {
//						$("body").trigger("europass:waiting:indicator:show");
                                this.uploadController.uploadedCallback(response, file.name, this.parentView);
                                this.$el.trigger("europass:wizard:import:complete");
                            }
                        },
                        error: {
                            scope: _that,
                            callback: function (status, responseText) {
                                this.uploadController.onUploadFailure(status, responseText);
//						$("body").trigger("europass:waiting:indicator:hide");
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

//			$("body").trigger("europass:waiting:indicator:hide");
                },
                /**
                 * Prepare the chooser button
                 * @param elId
                 * @param allowedExtensions
                 * @param successCallback
                 */
                prepareDropboxChooser: function (elId, allowedExtensions, successCallback) {

                    var _that = this;

                    Dropbox.appKey = this.appKey;

                    var options = {
                        // Required. Called when a user selects an item in the Chooser.
                        success: successCallback,
                        // Optional. Called when the user closes the dialog without selecting a file
                        // and does not include any parameters.
                        cancel: function () {
                            $("body").trigger("europass:waiting:indicator:hide");
                            _that.onClose();
//					_that.messageContainer
//						.trigger("europass:message:show", 
//							["warning", ( Notification["skillspassport.import.cloud.cancelled.dropbox"] || "Action Cancelled") , true]);

                        },
                        // Optional. "preview" (default) is a preview link to the document for sharing,
                        // "direct" is an expiring link to download the contents of the file. For more
                        // information about link types, see Link types below.
                        linkType: "direct", // "preview" or "direct"
                        // Optional. A value of false (default) limits selection to a single file, while
                        // true enables multiple file selection.
                        multiselect: false, // or true
                        // Optional. This is a list of file extensions. If specified, the user will
                        // only be able to select files with these extensions. You may also specify
                        // file types, such as "video" or "images" in the list. For more information,
                        // see File types below. By default, all extensions are allowed.
                        extensions: allowedExtensions
                    };

                    Dropbox.choose(options);
                }
            });

            return DropboxUploadView;
        }
);