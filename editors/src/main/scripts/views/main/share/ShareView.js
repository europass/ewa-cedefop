define(
        [
            'jquery',
            'underscore',
            'backbone',
            'Utils',
            'cookie',
            'HttpUtils',
            'i18n!localization/nls/Notification',
            'i18n!localization/nls/GuiLabel',
            'hbs!templates/share/shareForm',
            'europass/http/Resource',
            'europass/http/ServicesUri',
            'europass/http/WindowConfigInstance',
            'views/main/share/ManageSharesView',
            'models/ShareCloudInfoModel',
            'europass/http/MediaType'
        ],
        function ($, _, Backbone, Utils, cookie, HttpUtils,
                Notification, GuiLabel, Template, Resource, ServicesUri,
                WindowConfig, ManageSharesView, ShareCloudInfoModel, MediaType) {

            var ShareView = Backbone.View.extend({

                sectionEl: $("#share-document-btn")
                , scrollableSelector: "#PostToJobPortalsForm .job-portals-info .ui-main-area"
                , optionsSelector: "#PostToJobPortalsForm .job-portals-info"
                , overlaySelector: "#PostToJobPortalsForm"
                , defaultSelector: "#main-area"
                , successSelector: "#job-portal-success"
                , errorSelector: "#job-portal-error"
                , errorMsgSelector: "#job-portal-warning-msg"
                , shareFormErrorMsgSelector: "#share-warning-msg"
                , shareFormSelector: "#share-for-review"
                , closeShareSelector: "#btn-cancel-share"


                , formShareInfoSelector: "#PostToJobPortalsForm .job-portals-info"

                , alreadyRendered: false

                , googleShareUrl: ""
                , dropboxShareUrl: ""
                , oneDriveShareUrl: ""

                , googleUserEmail: ""
                , dropboxUserEmail: ""
                , onedriveUserEmail: ""

                , events: {
                    "click #btn-share-googledrive:not(.disabled)": "googleDriveShare",
                    "click #btn-share-dropbox:not(.disabled)": "dropboxShare",
                    "click #btn-share-onedrive:not(.disabled)": "oneDriveShare",

                    "click #PostToJobPortalsForm button.close": "resetShare",

                    "europass:share:link:google": "renderShareForm",
                    "europass:share:link:dropbox": "renderShareForm",
                    "europass:share:link:onedrive": "renderShareForm",

                    "europass:share:user:email": "setUserEmail",

                    "click #btn-send-review": "sendEmailForReview",

                    "click #copy-link-btn": "selectAll",
                    "click .button.cancel": "resetShareFormError"

                },

                /**
                 * Initialize
                 * @param options
                 */
                initialize: function (options) {
                    this.contextRoot = WindowConfig.getDefaultEwaEditorContext();
                    this.render();
                }

                , resetShare: function () {

                    $("body").trigger("europass:share:manage:folders:reset");

                    this.googleShareUrl = "";
                    this.dropboxShareUrl = "";
                    this.oneDriveShareUrl = "";

                    this.googleUserEmail = "";
                    this.dropboxUserEmail = "";
                    this.onedriveUserEmail = "";

                    this.googleUserEmail = "";

                    this.resetShareFormError();
                }

                , resetShareFormError: function () {
                    this.shareFormErrorMsg.trigger("europass:message:clear");
                }

                , render: function () {

                    //Html of the Settings Modal - html is appended and it is hidden
                    var html = Template();
                    this.$el.find("#PostToJobPortalsForm .ui-main-area").append(html);

                    this.optionsArea = this.$el.find(this.optionsSelector);
                    this.scrollableArea = this.$el.find(this.scrollableSelector);
                    this.overlay = this.$el.find(this.overlaySelector);

                    this.errorDrawer = this.$el.find(this.errorSelector);
                    this.successDrawer = this.$el.find(this.successSelector);
                    this.defaultDrawer = this.$el.find(this.defaultSelector);
                    this.errorMsg = this.$el.find(this.errorMsgSelector);
                    this.shareFormErrorMsg = this.$el.find(this.shareFormErrorMsgSelector).last();
                    this.shareFormArea = this.$el.find(this.formShareInfoSelector);
                    this.closeShareFrmBtn = this.$el.find(this.closeShareSelector);

                    this.resetShareFormError();
                }

                , setUserEmail: function (event, type, email, name) {

//					this.userEmail = email;

                    this.displayName = name;

                    if (type === "share-googledrive")
                        this.googleUserEmail = email;
                    if (type === "share-dropbox")
                        this.dropboxUserEmail = email;
                    if (type === "share-onedrive")
                        this.onedriveUserEmail = email;

                }

                , checkPopupBlocked: function (elemId) {

                    var _that = this;
                    var timeOutMilis = 500;

                    setTimeout(function () {
                        setTimeout(function () {	//seems like FF needs another setTimeout in order to make tryOpeningPopUp() work

                            var allowed = Utils.tryOpeningPopUp();

                            if (allowed === false) {
                                $("body").trigger("europass:share:response:error", [undefined, Notification["skillspassport.wizard.cloud.popup.blocked"]]);
//								self.cleanupFeedback();
//								self.getMessageContainer().trigger("europass:message:show", ["error",  Notification["skillspassport.wizard.cloud.popup.blocked"]]);
//								self.enableConnect();
                            } else {

                                if (elemId == "btn-share-googledrive")
                                    $("body").trigger("europass:share:google", [elemId]);
                                if (elemId == "btn-share-dropbox")
                                    $("body").trigger("europass:share:dropbox", [elemId]);
                                if (elemId == "btn-share-onedrive")
                                    $("body").trigger("europass:share:onedrive", [elemId]);

                            }

                        }, timeOutMilis);
                    }, timeOutMilis);

                }

                , googleDriveShare: function (event, elemId) {

                    var elem = $(event.currentTarget);
                    var elemId = elem.attr("id");

                    if (this.googleShareUrl !== undefined && this.googleShareUrl !== null && this.googleShareUrl !== "")
                        this.renderShareForm(event, elemId, this.googleShareUrl);
                    else
                        this.checkPopupBlocked(elemId);

                }

                , dropboxShare: function (event, elemId) {

                    var elem = $(event.currentTarget);
                    var elemId = elem.attr("id");

                    if (this.dropboxShareUrl !== undefined && this.dropboxShareUrl !== null && this.dropboxShareUrl !== "")
                        this.renderShareForm(event, elemId, this.dropboxShareUrl);
                    else
                        this.checkPopupBlocked(elemId);
                }

                , oneDriveShare: function (event, elemId) {

                    var elem = $(event.currentTarget);
                    var elemId = elem.attr("id");

                    if (this.oneDriveShareUrl !== undefined && this.oneDriveShareUrl !== null && this.oneDriveShareUrl !== "")
                        this.renderShareForm(event, elemId, this.oneDriveShareUrl);
                    else
                        this.checkPopupBlocked(elemId);
                }

                , renderShareForm: function (event, id, url) {

                    var type = id.slice(4);
                    this.closeShareFrmBtn.addClass("non-visible");
                    this.$el.find("#share-cv-btn").attr("name", "btn-" + type).addClass("blue-pressed");
                    this.$el.find("#manage-shares-btn").attr("name", "btn-" + type).removeClass("blue-pressed");
                    this.hideTemp("first");
                    this.$el.find("#share-for-review").children().addBack().css("display", "block");
                    this.arrangeTypes(type);

                    this.$el.find("#" + type).val(url);

                    var email = "";
                    if (type === "share-googledrive") {
                        email = this.googleUserEmail;
                        if (this.googleShareUrl === "")
                            this.googleShareUrl = url;
                    }
                    if (type === "share-dropbox") {
                        email = this.dropboxUserEmail;
                        if (this.dropboxShareUrl === "")
                            this.dropboxShareUrl = url;
                    }
                    if (type === "share-onedrive") {
                        email = this.onedriveUserEmail;
                        if (this.oneDriveShareUrl === "")
                            this.oneDriveShareUrl = url;
                    }

                    if (email !== "")
                        this.$el.find("input#share-user-email").val(email);

                    $("body").trigger("europass:waiting:indicator:hide", true);

                }

                , selectAll: function (event) {
                    var ev = $(event.currentTarget);
                    var par = ev.closest(".share-option");
                    var frmField = par.find(".formfield.share-cloud-link");

                    var copySuccess = true;
                    var selection = "";
                    var el = frmField[0];

                    frmField.select();

                    try {
                        document.execCommand('copy');//execute copy command

                        //get value from input and compare with window.selected value
                        if (el !== null && el !== undefined && el !== '') {
                            var val = el.value;

                            if (window.getSelection) {
                                try {
                                    selection = el.value.substring(el.selectionStart, el.selectionEnd);

                                    if (val !== selection) {
                                        copySuccess = false;
                                    }
                                } catch (e) {
                                    copySuccess = false;
                                }
                            }
                            // For IE
                            /*if (document.selection && document.selection.type != "Control") {
                             selection = document.selection.createRange().text;
                             }*/
                        }
                    } catch (err) {
                        copySuccess = false;
                    }
                    this.handleCopyLinkResult(this, copySuccess);
                }

                , handleCopyLinkResult: function (el, copySuccess) {
                    if (copySuccess) {
                        this.model.trigger("share:link:copy:clipboard");
                    } else {
                        //this is a fast fix for safari browser
                        this.model.trigger("share:link:copy:clipboard:manual");
                    }
                }

                , hideTemp: function (temp) {
                    if (temp === "first") {
                        this.$el.find(".ui-main-area.default").hide();
                        this.$el.find(".mid-divider").hide();
                        this.$el.find(".ui-cloud-area").hide();
                    }
                    if (temp === "second") {
                        this.$el.find("#share-for-review").hide();
                    }
                }
                , arrangeTypes: function (tp) {
                    var lbl = tp.slice(6);
                    this.$el.find(".share-option").find("input.formfield").attr("id", tp);
                    this.$el.find(".share-link-label").html(GuiLabel["share.cloud.link.label." + lbl]);
                }
                /**
                 * Triggered when SEND FOR REVIEW button is clicked 
                 */
                , sendEmailForReview: function (event) {

                    this.resetShareFormError();

                    var reviewer = this.shareFormArea.find("input.email").val();
                    if (_.isUndefined(reviewer) || _.isEmpty(reviewer)) {
                        this.shareFormErrorMsg.trigger("europass:message:show", ["error", Notification["europass.shared.reviewer.email.empty"], false]);
                        return;
                    }

                    if (!Utils.isValidEmail(reviewer)) {
                        this.shareFormErrorMsg.trigger("europass:message:show", ["error", Notification["europass.shared.reviewer.email.invalid"], false]);
                        return;
                    }

                    this.errorMsg.trigger("europass:message:clear");
                    this.errorMsg = this.$el.find(this.errorMsgSelector).last();

                    var sendButton = $(event.target);
                    sendButton.addClass("disabled");

                    var infoModel = new ShareCloudInfoModel();
                    var defaultEmailSection = "SkillsPassport.LearnerInfo.Identification.ContactInfo.Email.Contact";
                    var defaultEmail = Utils.isEmptyObject(this.model.get(defaultEmailSection)) ? "" : this.model.get(defaultEmailSection);

                    infoModel.set("ShareInfo.Sender", defaultEmail);

                    var link = this.shareFormArea.find("input.share-cloud-link").val();

                    var message = this.shareFormArea.find("textarea.formfield").val().replace(/(?:\r\n|\r|\n)/g, '<br />');
                    var userEmail = this.shareFormArea.find("input#share-user-email").val();

                    infoModel.set("ShareInfo.Locale", this.model.get("SkillsPassport.Locale"));
                    infoModel.set("ShareInfo.Link", link);
                    infoModel.set("ShareInfo.Email", reviewer);
                    infoModel.set("ShareInfo.FullName", this.displayName || "");
                    infoModel.set("ShareInfo.Cc", (userEmail || ""));
                    infoModel.set("ShareInfo.Message", message);

                    var data = infoModel.get("ShareInfo");
                    var httpResource = new Resource(ServicesUri.shareReview);
                    httpResource.contentType(MediaType.json);

                    var _that = this;
                    //start the waiting indicator...
                    $("body").trigger("europass:waiting:indicator:show");
                    httpResource._post({
                        data: data
                    }, {
                        success: {
                            scope: _that,
                            callback: function (response) {
                                $("body").trigger("europass:share:response:success");
                            }
                        },
                        error: {
                            scope: _that,
                            callback: function (status, responseText) {
                                var response = HttpUtils.readHtmlErrorResponse(responseText);

                                var errorCode =
                                        (response.msg !== undefined) ?
                                        (response.msg.Error !== undefined) ?
                                        response.msg.Error.code !== undefined ? response.msg.Error.code : "unknown"
                                        : "unknown"
                                        : "unknown";

                                $("body").trigger("europass:share:response:error", [status, errorCode]);

                                switch (errorCode) {
                                    case "email.invalid.recipient":
                                    {
                                        this.errorMsg.trigger("europass:message:show", ["error", Notification["error.code.email.invalid.recipient"], false]);
                                        break;
                                    }
                                    case "email.not.sent":
                                    {
                                        this.errorMsg.trigger("europass:message:show", ["error", Notification["error.code.email.notsent"], false]);
                                        break;
                                    }
                                    default:
                                    {
                                        this.errorMsg.trigger("europass:message:show", ["error", Notification["error.code.internal.server.error"], false]);
                                        break;
                                    }
                                }
                            }
                        },
                        complete: {
                            scope: _that,
                            callback: function (status, responseText) {
                                $("body").trigger("europass:waiting:indicator:hide", true);
                                sendButton.removeClass("disabled");
                            }
                        }

                    });
                }
            });

            return ShareView;
        }
);