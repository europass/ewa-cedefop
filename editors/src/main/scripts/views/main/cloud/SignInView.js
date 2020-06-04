/**
 * Sign In View (drawer) for Cloud services (Google Drive/ One Drive/ Dropbox)
 *
 *
 */
define(
        [
            'jquery',
            'jqueryui',
            'underscore',
            'backbone',
            'hbs!templates/main/cloud-login/signIn',
            'Utils',
            'views/main/cloud/CloudViewHelper',
            'analytics/EventsController',
            'i18n!localization/nls/Notification',
            'europass/http/WindowConfigInstance'
        ],
        function ($, jqueryui, _, Backbone, HtmlTemplate, Utils, CloudViewHelper, Events, Notification, WindowConfig) {

            var CloudSignInView = Backbone.View.extend({

                htmlTemplate: HtmlTemplate
                , sectionEl: $("#top-ui-cloud-sign-in")
                , scrollableSelector: "#CloudSignInForm .cloudSignIn-info .ui-settings-area"
                , optionsSelector: "#CloudSignInForm .cloudSignIn-info"
                , overlaySelector: "#CloudSignInForm"
                , event: new Events
                , events: {
                    "click #top-ui-cloud-sign-in a.current-cloud-sign-in": "showCloudSignInForm",
                    "click #cloud-login-feature-tooltip .tooltip-sign-in": "showCloudSignInForm",
                    "click #CloudSignInForm button.close": "hideCloudSignInForm",

                    "click #btn-cloud-sign-googledrive": "cloudServiceSignIn",
                    "click #btn-cloud-sign-onedrive": "cloudServiceSignIn",

                    "europass:cloud-sign-in:drawer:hide": "hideCloudSignInForm"
                }
                , initialize: function () {
                    this.render();
                }
                , render: function () {

                    if (WindowConfig.showCloudLogin === true) {
                        var context = {};
                        //Html of the Cloud Sign in Modal - html is appended and it is hidden
                        var html = HtmlTemplate(context);
                        this.$el.append(html);

                        this.optionsArea = this.$el.find(this.optionsSelector);
                        this.scrollableArea = this.$el.find(this.scrollableSelector);
                        this.overlay = this.$el.find(this.overlaySelector);

                        this.cloudViewHelper = new CloudViewHelper({"overlay": this.overlay, "area": this.optionsArea,
                            "sectionEl": this.sectionEl});
                    }
                }

                , showCloudSignInForm: function (event) {
                    if ($("#cloud-login-tooltip").is(":visible")) {
                        $("#cloud-login-tooltip").trigger("europass:cloud:tooltip:store");
                    }
                    this.event.enterCloudLogin();
                    this.cloudViewHelper.showCloudDrawer();
                }
                , hideCloudSignInForm: function (event) {
                    this.cloudViewHelper.hideCloudDrawer();
                }

                , cloudServiceSignIn: function (event) {
                    var elem = $(event.currentTarget);
                    var elemId = elem.attr("id");

                    if (elemId === "btn-cloud-sign-onedrive")
                        this.event.oneDriveCloud();
                    else
                        this.event.googleDriveCloud();

                    this.checkPopupBlocked(elemId);
                }

                , checkPopupBlocked: function (elemId) {
                    var _that = this;
                    var timeOutMilis = 500;
                    setTimeout(function () {
                        setTimeout(function () {
                            var allowed = Utils.tryOpeningPopUp();
                            if (allowed === false) {
                                Utils.triggerErrorWhenCloudLoginAction("status", Notification["skillspassport.wizard.cloud.popup.blocked"], "main");
                            } else {
                                if (elemId === "btn-cloud-sign-googledrive") {
                                    $("body").trigger("europass:cloud:connect:googledrive", false);
                                } else if (elemId === "btn-cloud-sign-onedrive") {
                                    $("body").trigger("europass:cloud:connect:onedrive");
                                }
                            }
                        }, timeOutMilis);
                    }, timeOutMilis);
                }
            });

            return CloudSignInView;
        }
);