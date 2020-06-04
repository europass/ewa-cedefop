define(
        [
            'module',
            'jquery',
            'underscore',
            'backbone',

            'Utils',
            'hbs!templates/upload/linkedin',

            'europass/GlobalDocumentInstance',
            'europass/http/ServicesUri',
            'routers/SkillsPassportRouterInstance',
            'views/upload/UploadController',
//		EWA-1811
//		'i18n!localization/nls/GuiLabel',
            'i18n!localization/nls/Notification',
            'analytics/EventsController'
        ],
        function (module, $, _, Backbone, Utils,
                Template,
                GlobalDocument, ServicesUri, SkillsPassportRouter, UploadController,
//		EWA-1811
//		GuiLabel,
                Notification,
                Events) {

            var LinkedInUploadView = Backbone.View.extend({
                name: "linkedin",

                parentWindow: window,
                event: new Events,
                events: {
                    "europass:social:upload:linkedin #linkedin-import-area": "onRequestImport"
                },
                onClose: function () {
                    this.uploadController.cleanup();
                    delete this.uploadController;
                },
                initialize: function (options) {

                    this.uploadedEsp = {};

                    if (_.isObject(ServicesUri.social_import_services)
                            && _.isObject(ServicesUri.social_import_helpers)) {

                        //pass user-cookie id
                        var cookieId = '';
                        if (Utils.readCookie()) { //user-cookie exists
                            cookieId = Utils.readCookie();
                        }

                        this.serviceUrl = ServicesUri.social_import_services.linkedIn + "?id=" + cookieId;

                        this.importHelperUrl = ServicesUri.social_import_helpers.linkedIn;
                    }

                    this.parentView = options.parentView;

                    this.messageContainer = options.messageContainer;

                    //Reusable Upload Controller
                    this.uploadController = new UploadController({
                        relatedController: this,
                        messageContainer: this.messageContainer,
                        modelUpdateEvent: "model:uploaded:social",
                        modelUpdateMsgKey: "social.linkedin.profile.uploaded"
                    });

                    //Search the ESP model to see if it comes from 
                    var esp = this.model;
                    var linkedinInfo = esp.espModelInfo;
                    if (!_.isEmpty(linkedinInfo) && _.isObject(linkedinInfo)) {

                        delete esp.espModelInfo;
                        this.parentView.model.setLocation("linkedin");

                        //1. LinkedIn successful upload
                        var linkedInJsessionid = linkedinInfo.linkedInJsessionid;
                        if (!_.isEmpty(linkedInJsessionid)) {
//						console.log("Return from successful linkedin");
                            this.onUpload(linkedInJsessionid);
                        }
                        //2. LinkedIn failed upload
                        var error = linkedinInfo.linkedInErrorInfo;
                        if (!_.isEmpty(error)) {
//						console.log("Return from failed linkedIn");
                            this.onError(error);
                        }
                    }
                },
                /**
                 * Render the View
                 */
                render: function () {
                    var html = Template(this.parentView.getContext());
                    this.$el.html(html);

                    this.parentView.prepareConnect();

                    this.parentView.cleanupFeedback();

                    this.parentView.completeBtn.hide();

                },
                /**
                 * The action is cancelled
                 */
                doCancel: function () {
                    SkillsPassportRouter.navigate(this.getDefaultComposeUrl(), {trigger: false});
                },
                /**
                 * Complete the upload. Use the uploaded profile to populate the editor.
                 */
                doComplete: function () {
//				console.log("do complete");
                    this.uploadController.uploaded();
                },

                /**
                 * Do Connect
                 * 
                 * Change the URL in order to have a suitable referrer URL.
                 * 
                 * The new URL is caught by the router and the NavigationRoutes.navigateUploadLinkedIn is executed.
                 * This in turn fires a ".process .note.linkedin" event
                 * @param event
                 */
                doConnect: function () {
//				console.log("setRedirect");
                    this.event.importFrom('LinkedIn');

                    this.parentView.cleanupFeedback();

                    this.parentView.disableConnect();

                    SkillsPassportRouter.navigate("social/linkedin", {trigger: true});
                },
                /**
                 * When the control to request an import is activated
                 */
                onRequestImport: function (event) {
                    //console.log("onRequestImport");
                    this.importData();
                },
                /**
                 * When Linkedin has return with a success message
                 * @param jsessionid
                 */
                onUpload: function (jsessionid) {

                    SkillsPassportRouter.navigate(this.getDefaultUrl(), {trigger: false});

//				console.log("on upload");
                    this.showData(jsessionid);
                },
                /**
                 * When LinkedIn has returned with an error
                 * @param error
                 */
                onError: function (error) {

                    SkillsPassportRouter.navigate(this.getDefaultUrl(), {trigger: false});

                    var key = error.errorKey;
                    var msg;
                    if ("social-user-denied-request" === key) {
                        msg = null;
                    } else {
                        msg = Notification[key];
                        if (_.isUndefined(msg))
                            msg = Notification["social-server-error"];

                        msg += "<em class=\"trace-code\">" + error.trace + "</em>";
                    }

                    this.parentView.enableConnect(true);

                    this.messageContainer
                            .trigger("europass:message:show",
                                    ["error", (msg || "There was a problem uploading from LinkedIn")]);
                },
                /**
                 * Authenticate with LinkedIn and try to retrieve data
                 */
                importData: function () {
                    //console.log("importData");
                    var that = this;
                    //console.log("url: " + that.serviceUrl);
                    // fire off the request				
                    request = $.ajax({
                        url: that.serviceUrl,
                        type: "GET",
                        async: false,
                        data: ""
                    })
                            // callback handler that will be called on success
                            .done(function (response, textStatus, jqXHR) {
                                window.open(response, "_blank");
                            })
                            .then(function (response, textStatus, jqXHR) {
                                if (textStatus !== undefined && textStatus === "success") {
                                    that.parentView.hideConnect();
                                    SkillsPassportRouter.navigate(that.getDefaultUrl(), {trigger: true});
                                }
                            })
                            // callback handler that will be called on failure of our API
                            .fail(function (jqXHR, textStatus, errorThrown) {
                                var httpStatus = parseInt(jqXHR.status);
                                if (httpStatus === 401) {
                                    textStatus = "app.authorization.error";
                                }
                                that.handleFailure(jqXHR, textStatus, errorThrown);
                            })
                            .then(function (jqXHR, textStatus, errorThrown) {
                                if (textStatus !== undefined && textStatus !== "success")
                                    that.parentView.enableConnect(true);
                            });
//				.always(function(){
////					console.log("always");
//					that.parentView.enableConnect();
//					
//					SkillsPassportRouter.navigate( that.getDefaultUrl(), {trigger: true});
//				});
                },
                /**
                 * Calls our API which handles the entire authentication and callback cycle
                 * @param jsessionid
                 */
                showData: function (jsessionid) {
                    var state = jsessionid;
                    var that = this;
                    // fire off the request
                    request = $.ajax({
                        url: that.importHelperUrl + ";jsessionid=" + state,
                        type: "GET",
                        data: ""
                    })
                            // callback handler that will be called on success
                            .done(function (response, textStatus, jqXHR) {
//					console.log("Succeeded to fetch stored profile per session from the server-side");
                                that.uploadController.uploadedCallback(response, undefined, that.parentView);
                                //This will display the complete button
                                that.$el.trigger("europass:wizard:import:complete");
                            })
                            // callback handler that will be called on failure
                            .fail(function (jqXHR, textStatus, errorThrown) {
//					console.log("Failed to fetch stored profile per session from the server-side");
                                that.handleFailure(textStatus);
                            })
                            .complete(function () {
                                that.parentView.hideConnect();
                            });
                }
                /**
                 * Handle failure by preparing a message
                 */
                , handleFailure: function (jqXHR, textStatus, errorThrown) {
                    var msg = Notification["social.service.import." + textStatus];

                    if (msg === undefined || msg === null) {
                        msg = "Importing profile from LinkedIn failed.";
                    }

                    msg += " (" + errorThrown + ")";

                    this.parentView.enableConnect(true);

                    this.messageContainer
                            .trigger("europass:message:show",
                                    ["error", (msg || "There was a problem communicating with LinkedIn")]);
                }
                /**
                 * Decide which view to show after the upload of the existing CV
                 */
                , decideAfterView: function () {
                    //Check if it is ESP only and in that case redirect to ESP compose
                    return SkillsPassportRouter.defaultViewUrl;
                }
                , getDefaultUrl: function () {
                    return GlobalDocument.getUrlDocument() + "/upload";
                }
                , getDefaultComposeUrl: function () {
                    return GlobalDocument.getUrlDocument() + "/compose";
                }
            });

            return LinkedInUploadView;
        }
);