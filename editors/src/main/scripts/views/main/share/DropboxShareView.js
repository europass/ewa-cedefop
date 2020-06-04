define(
        [
            'jquery',
            'underscore',
            'backbone',
            'hbs!templates/download/dropbox',
            'europass/http/WindowConfigInstance',
            'europass/http/Resource',
            'europass/http/MediaType',
            'europass/http/ServicesUri',
            'views/download/DownloadController',
            'i18n!localization/nls/GuiLabel',
            'i18n!localization/nls/Notification',
            'dropboxSDK',
            'views/main/cloud/DropboxAuthenticate',
            'europass/http/SessionManagerInstance',
            'Utils',
            'europass/http/WindowConfigInstance',
            'analytics/EventsController'
        ],
        function ($, _, Backbone,
                Template,
                WindowConfig,
                Resource,
                MediaType,
                ServicesUri,
                DownloadController,
                GuiLabel,
                Notification,
                Dropbox,
                DropboxAuthenticate,
                Session,
                Utils,
                WindowConfig,
                Events
                ) {

            var DropboxShareView = Backbone.View.extend({
                name: "dropbox",
                alreadyRendered: false,
                accessToken: null,
                dbxClient: null,
                shareReadMe: "readMe.txt",
                currentFileName: null,
                currentSharedItems: [],
                event: new Events,
                events: {
                    "europass:share:dropbox": "shareFile",
                    "europass:share:dropbox:list": "getFiles",
                    "europass:share:dropbox:delete": "removeFile",
                    "europass:share:views:cleanup": "cleanUp"
                },

                /**
                 * Cleanup
                 */
                cleanUp: function () {
                    this.dbxClient = {};
                    delete this.accessToken;
                    delete this.currentFileName;
                    this.alreadyRendered = false;
                    this.currentSharedItems = [];
                },

                shareFile: function (event, data) {
                    this.event.postTo('Dropbox');
                    this.dropboxBtnID = data;
                    this.doConnect();
                },

                /**
                 * Initialize
                 * @param options
                 */
                initialize: function (options) {
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
                    this.doStore();
                },

                /**
                 * checkReadMe 
                 */
                checkReadMe: function () {

                    var _that = this;
                    //		var content = "PLEASE DO NOT MODIFY THIS FOLDER, NOR ADD OR DELETE FILES, IN ORDER FOR THE SHARE FUNCTIONALITY TO WORK PROPERLY";
                    var content = GuiLabel["share.cloud.readme.contents"];

                    dbxClient.filesUpload({path: "/" + WindowConfig.cloudShareFolder + "/" + this.shareReadMe, contents: content})
                            .then(function (response) {
                                _that.store(_that.accessToken);
                            })
                            .catch(function (error) {
                                return _that.dropboxShowError({status: "ERROR_UPLOADING_FILE"});
                            });
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
                        if (popup === null) {
                            return _that.dropboxShowError({status: "ERROR_UPLOADING_FILE"});
                        }

                        var timer = setInterval(checkAuthWindowClosed, 500);
                        function checkAuthWindowClosed() {
                            if (popup.closed) {
                                clearInterval(timer);

                                // move accessToken to property file??
                                _that.accessToken = Utils.readCookieByName("dropbox-access-token");

                                if (_.isEmpty(_that.accessToken) || _that.accessToken === false) {
                                    return _that.dropboxShowError({status: "EMPTY_CREDENTIALS"});
                                }

                                $("body").trigger("europass:waiting:indicator:show", true);
                                $("body").trigger("europass:share:user:logged", ["dropbox"]);

                                dbxClient = new Dropbox({accessToken: _that.accessToken});
                                dbxClient.usersGetCurrentAccount()
                                        .then(function (response) {
                                            $("body").trigger("europass:share:user:email", ["share-dropbox", response.email, response.name.display_name]);
                                            _that.checkReadMe();
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
                    var url = ServicesUri.document_conversion_to["dropbox_share"] + Session.urlappend();

                    var httpResource = new Resource(url);
                    httpResource.contentType(MediaType.json);

                    var dataToSend = {
                        json: Utils.encodePlusCharPercent(this.model.conversion().toTransferable()),
                        folder: WindowConfig.cloudShareFolder,
                        token: this.accessToken
                    };

                    var _that = this;
                    httpResource._post({
                        data: dataToSend
                    }, {
                        success: {
                            scope: _that,
                            callback: function (response) {

                                var filename = $(response).filter("meta[name='filename']").attr("content");
                                var shareUrl = $(response).filter("meta[name='shareUrlBase']").attr("content");
                                _that.currentFileName = "/" + filename;

                                var editorsUrl = window.location.origin + WindowConfig.getDefaultEwaEditorContext();

                                shareUrl += "?token=" + accessToken + "&fpath=" + _that.currentFileName
                                        + "&cloudProvider=dropbox";

                                $("body").trigger("europass:share:link:dropbox", [_that.dropboxBtnID, editorsUrl + shareUrl]);
                                $("body").trigger("europass:share:manage:folders", ["dropbox", WindowConfig.cloudShareFolder]);
                            }
                        },
                        error: {
                            scope: _that,
                            callback: function (status, responseText) {

                                var errCode = "";
                                try {
                                    if (!_.isUndefined(responseText)) {
                                        var json = $(responseText).filter("script[type=\"application/json\"]").html();
                                        var parsed = JSON.parse(json);
                                        if (parsed.Error)
                                            if (parsed.Error.trace)
                                                errCode = trace;
                                    }

                                } catch (e) {
                                    $("body").trigger("europass:waiting:indicator:hide");
                                }

                                $("body").trigger("europass:share:response:error", [status, errCode]);
                            }
                        },
                        complete: {
                            scope: _that,
                            callback: function (status, responseText) {
                                //ekar: 2014-04-28: 
                                //We must delete the local storage so long as we do not find a way to do this through the API 
                                //client.authDriver().BrowserBase.localStorage().forgetCredentials();
                                // var key = "dropbox-auth:default:" + (_that.client.appHash());
                                // try {
                                // 	delete window.localStorage [ key ];
                                // } catch (err) {
                                // }
                            }
                        }
                    });
                },

                getFiles: function (event, folder) {

                    if (this.currentSharedItems.length > 0) {
                        $("body").trigger("europass:share:manage:render", [this.currentSharedItems]);
                        $("body").trigger("europass:waiting:indicator:hide");
                        return;
                    }

                    var _that = this;
                    dbxClient.filesListFolder({path: "/" + folder})
                            .then(function (response) {
                                var contents = response.entries;
                                contents.sort().reverse();

                                for (var idx in contents) {
                                    var current = contents[idx];

                                    if (_that.shareReadMe !== current.name && current[".tag"] === 'file') {
                                        var revoke = (_that.currentFileName !== current.path_display ? true : false);
                                        var displayName = current.name.replace(/[0-9]{2}_[0-9]{2}_[0-9]{2}/g, function (arg1) {
                                            return arg1.replace(/_/g, ":");
                                        });

                                        _that.currentSharedItems.push({"id": current.path_display, "name": displayName,
                                            "date": current.client_modified, "size": current.size, "canRevoke": revoke});
                                    }
                                }

                                $("body").trigger("europass:share:manage:render", [_that.currentSharedItems]);
                                $("body").trigger("europass:waiting:indicator:hide");
                            })
                            .catch(function (error) {
                                return _that.dropboxShowError({status: "ERROR_GETTING_FILES"});
                            });
                },

                removeFile: function (event, path, parent) {
                    var _that = this;

                    dbxClient.filesDelete({path: path})
                            .then(function (response) {
                                parent.slideToggle("up", function () {
                                    _that.updateCurrentSharedItems(path);
                                    $(this).remove();
                                });

                            })
                            .catch(function (error) {
                                return _that.dropboxShowError({status: "ERROR_DELETE_FILE"});
                            });
                },

                updateCurrentSharedItems: function (revokedName) {

                    for (var idx in this.currentSharedItems) {
                        var item = this.currentSharedItems[idx];
                        if (item.id === revokedName) {
                            this.currentSharedItems.splice(idx, 1);
                            break;
                        }
                    }
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
//
// 				$("body").trigger("europass:share:response:error", [error.status, Notification[msgKey]]);
// 				$("body").trigger("europass:waiting:indicator:hide");
// 			}

            });

            return DropboxShareView;
        }
);