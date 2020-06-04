define(
        [
            'jquery',
            'underscore',
            'backbone',
            'hbs!templates/download/dropbox',
            'europass/http/WindowConfigInstance',
            'europass/http/Resource',
            'europass/http/MediaType',
            'views/download/DownloadController', //'i18n!localization/nls/GuiLabel', 'i18n!localization/nls/Notification',
            'dropboxSDK',
            'views/main/cloud/DropboxAuthenticate',
            'analytics/EventsController',
            'Utils'
        ],
        function ($, _, Backbone, Template, WindowConfig, Resource, MediaType, DownloadController, Dropbox, DropboxAuthenticate,
                Events, Utils) {

            var DropboxStoreView = Backbone.View.extend({
                name: "dropbox",
                alreadyRendered: false,
                accessToken: null,
                DEFAULT_FOLDER: "/dropbox/Europass",
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
                    this.parentView = options.parentView;
                    this.messageContainer = options.messageContainer;

                    //Reusable Upload Controller
                    this.downloadController = new DownloadController({
                        relatedController: this,
                        messageContainer: this.messageContainer,
                        info: options.info
                    });
                    this._url = this.downloadController.decideUrl();
                    this.dropboxAuthenticate = new DropboxAuthenticate({});
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
                 * Do Connect
                 */
                doConnect: function () {
                    var url = this.downloadController.decideUrl();
                    this.event.exportTo(url);
                    this.parentView.checkPopUp(this.$el);
                },

                doConnectLogin: function () {
                    $("body").trigger("europass:waiting:indicator:show");

                    this.doStore();
                },
                /**
                 * Prepare the data to post to the API
                 * @param accessToken
                 */
                prepareData: function (accessToken) {
                    return this.downloadController.prepareData(accessToken, this.DEFAULT_FOLDER);
                },

                /**
                 * 1. Authenticate, if not already authenticated
                 * 2. Check dropbox account info
                 * 3. Proceed with storing
                 */
                doStore: function () {
                    var _that = this;
                    setTimeout(function () { //workaround for Safari 11 issue see EPAS-1072 
                        var popup = _that.dropboxAuthenticate.authenticate();
                        if (popup === null && Utils.tryOpeningPopUp() === false) {
                            return _that.dropboxShowError({status: "POPUPS_BLOCKED"});
                        } else if (popup === null || popup === undefined) {
                            return _that.dropboxShowError({status: "ERROR_UPLOADING_FILE"});
                        }

                        var timer = setInterval(checkAuthWindowClosed, 500);
                        function checkAuthWindowClosed() {
                            if (popup.closed) {
                                clearInterval(timer);

                                _that.accessToken = Utils.readCookieByName("dropbox-access-token");
                                if (_.isEmpty(_that.accessToken) || _that.accessToken === false) {
                                    return _that.dropboxShowError({status: "EMPTY_CREDENTIALS"});
                                }

                                var dbxClient = new Dropbox({accessToken: _that.accessToken});
                                dbxClient.usersGetCurrentAccount()
                                        .then(function (response) {
                                            _that.store(_that.accessToken);
                                        })
                                        .catch(function (error) {
                                            return _that.dropboxShowError({status: "ERROR_GETTING_ACCOUNT"});
                                        });
                            }
                        }
                    }, 500);
                },
                /**
                 * Delegate to the API for server-side upload.
                 * 
                 * @param accessToken
                 */
                store: function (accessToken) {
                    $("body").trigger("europass:waiting:indicator:show");

                    var httpResource = new Resource(this._url);
                    httpResource.contentType(MediaType.json);

                    var _that = this;
                    httpResource._post({
                        data: this.prepareData(accessToken)
                    }, {
                        success: {
                            scope: _that,
                            callback: function (response) {
                                $("body").trigger("europass:waiting:indicator:show", true);

                                var filename = $(response).filter("meta[name='filename']").attr("content");
                                if (_.isUndefined(filename))
                                    filename = "Europass CV";

                                this.downloadController.triggerMessageWithPathFile("skillspassport.export.cloud.success.dropbox", this.DEFAULT_FOLDER + "/" + filename, "success", false);

                                this.$el.trigger("europass:wizard:export:complete");
                            }
                        },
                        error: {
                            scope: _that,
                            callback: function (status, responseText) {
                                this.downloadController.onUploadFailure(status, responseText);
                                this.parentView.enableConnect(true);
                            }
                        },
                        complete: {
                            scope: _that,
                            callback: function (status, responseText) {
                                $("body").trigger("europass:waiting:indicator:hide");
                                //ekar: 2014-04-28: 
                                //We must delete the local storage so long as we do not find a way to do this through the API 
                                //client.authDriver().BrowserBase.localStorage().forgetCredentials();
                                // var key = "dropbox-auth:default:" + (_that.client.appHash());
                                // try {
                                // 	delete window.localStorage [ key ];
                                // } catch (err) {
                                // }
//					console.log("Dropbox - Document uploading completed. Key is cleared");
                            }
                        }
                    });
                },

                dropboxShowError: function (error) {
                    var msgKey = "skillspassport.export.cloud.error";
                    switch (error.status) {
                        case "POPUPS_BLOCKED":
                            msgKey = "skillspassport.wizard.cloud.popup.blocked";
                            break;
                        case "EMPTY_CREDENTIALS":
                        case "ERROR_UPLOADING_FILE":
                        default:
                            msgKey = "skillspassport.export.cloud.error.jserror.dropbox";
                            break;
                    }
                    this.downloadController.triggerMessage(msgKey, "error", false);
                    $("body").trigger("europass:waiting:indicator:hide");
                },

                /**
                 * Error cases
                 * @param error
                 */
// 			dropboxError: function (error) {
// //		console.log( error.status );
// 				var msgKey = "skillspassport.export.cloud.error";
//
// 				switch (error.status) {
// 					// the user token expired.
// 					case Dropbox.ApiError.INVALID_TOKEN:
// 						msgKey = "skillspassport.export.cloud.error.invalidtoken.dropbox";
// 						break;
// 						// The file or folder you tried to access is not in the user's Dropbox.
// //		  case Dropbox.ApiError.NOT_FOUND:  break;
// 						// The user is over their Dropbox quota.
// //		  case Dropbox.ApiError.OVER_QUOTA:  break;
// 						// Too many API requests. Tell the user to try again later.
// 					case Dropbox.ApiError.RATE_LIMITED:
// 						msgKey = "skillspassport.export.cloud.error.ratelimit.dropbox";
// 						break;
// 						// An error occurred at the XMLHttpRequest layer.
// 					case Dropbox.ApiError.NETWORK_ERROR:
// 						msgKey = "skillspassport.export.cloud.error.networkerror.dropbox";
// 						break;
// 						// Caused by a bug in dropbox.js, in your application, or in Dropbox.
// 						// Tell the user an error occurred, ask them to refresh the page.
// 					case Dropbox.ApiError.INVALID_PARAM:
// 					case Dropbox.ApiError.OAUTH_ERROR:
// 					case Dropbox.ApiError.INVALID_METHOD:
// 					case "EMPTY_CREDENTIALS":
// 					default:
// 						msgKey = "skillspassport.export.cloud.error.jserror.dropbox";
// 						break;
// 				}
// 				this.downloadController.triggerMessage(msgKey, "error", false);
//
// 				$("body").trigger("europass:waiting:indicator:hide");
// 			}

            });
            return DropboxStoreView;
        }
);