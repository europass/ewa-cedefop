/**
 * This view is bound to the parent division wrapping the three radio buttons controlling the Gender information.
 * The View is configured to receive:
 * 1. the el element (div.choice.#Control:LearnerInfo.Identification.Demographics.Gender)
 * 2. the map containing the translation of the gender codes
 * 
 * This is initiated during PersonalInfoFormView.enableFunctionalities();
 *  
 */
define(
        [
            'jquery',
            'jqueryui',
            'underscore',
            'backbone',
            'HttpUtils',
            'Utils',
            'models/ContactInfoModel',
            'views/forms/DefaultsFormView',
            'i18n!localization/nls/Notification',
            'i18n!localization/nls/EditorHelp',
            'europass/http/Resource',
            'europass/http/ServicesUri',
            'europass/http/MediaType',
            'hbs!templates/main/contactus'
//	,
//    'europass/TabletInteractionsView'
        ],
        function ($, jqueryui, _, Backbone, HttpUtils, Utils, ContactInfoModel, DefaultsFormView, Notification, EditorHelp,
                Resource, ServicesUri, MediaType, HtmlTemplate
//			, TabletInteractionsView
                ) {

            var SendFeedbackView = Backbone.View.extend({

                //el is the body

                htmlTemplate: HtmlTemplate

                , sectionEl: $("#contact-us")

                , alreadyRendered: false

                , scrollableSelector: "#SendFeedbackForm .sendfeedback-info .ui-sendfeedback-area"
                , feedbackFormSelector: "#SendFeedbackForm #FormSendFeedBack.isForm"
                , feedbackSelector: "#SendFeedbackForm .sendfeedback-info"
                , overlaySelector: "#SendFeedbackForm"
                , noficationSelector: "#SendFeedbackForm #sendfeedback-notification-area"

                , defaultEmailSection: "SkillsPassport.LearnerInfo.Identification.ContactInfo.Email.Contact"

                , events: {
                    //On click of the contact us button
                    "click #contact-us a.contact-us": "checkAndShowContactForm",

                    "europass:open:contact:form": "showContactForm",

                    //On click of controls inside the Contact Us Div element that expands/ collapses
                    "click #SendFeedbackForm button.close.contact": "hideContactForm",
                    "click #SendFeedbackForm button.cancel": "hideContactForm",
                    "click #SendFeedbackForm button.return": "hideContactForm",
                    "click #SendFeedbackForm button:not(.disabled).validate": "updateModel",
                    "click #SendFeedbackForm button.previous": "goToPreviousStep",
                    "click #SendFeedbackForm button:not(.disabled).sendmail": "finallySendEmail"

                }
                /**
                 * On initialize set the URL
                 */
                , initialize: function () {
                    this.postUrl = ServicesUri.contact;
                }
                /**
                 * On render, append the Contact Us Division to the Document Body
                 */
                , render: function (msg) {

                    if (this.alreadyRendered === false) {

                        var context = {};

                        //Get Email from model and set it to the temporary model:
                        var defaultEmail = this.getDefaultEmail();
                        this.infoModel = new ContactInfoModel();
                        this.infoModel.set("ContactInfo.Email", defaultEmail);
                        $.extend(true, context, this.infoModel.attributes);

                        //Html of the Contact Us Division - html is appended and it is hidden
                        var html = HtmlTemplate(context);
                        this.$el.append(html);

                        var textMessage = msg.children("em").html();
                        if (_.isUndefined(textMessage))
                            textMessage = "";

                        if (!Utils.isEmptyObject(msg) && textMessage.length !== 0)
                            this.$el.find("#feedback-msg-textaera").val(this.buildErrorMsg(msg));

                        //Find the Feedback Form
                        this.feedbackForm = this.$el.find(this.feedbackFormSelector);
                        //Add the DefaultsFormView to the Feedback form
                        this.defaultsFormView = new DefaultsFormView({
                            el: this.feedbackForm
                        });
                        this.defaultsFormView.render();

                        this.feedbackArea = this.$el.find(this.feedbackSelector);
                        this.scrollableArea = this.$el.find(this.scrollableSelector);
                        this.notificationArea = this.$el.find(this.noficationSelector).first();
                        this.overlay = this.$el.find(this.overlaySelector);

                        this.alreadyRendered = true;
                    } else {
                        this.setDefaults(msg);
                    }
                }

                /**
                 * Shows the Contact Us Form
                 * @param the triggered event
                 * 
                 */
                , showContactForm: function (event, data) {


                    // Close the modal if message's origin comes from one
                    var modal = $("body .overlay:visible > .modal:visible").first();
                    if (!_.isUndefined(modal)) {
                        var closeBtn = modal.find("button.close:not(.notification)");
                        closeBtn.click();
                    }

                    var element = $(event.target);

                    if (!_.isUndefined(data) && !_.isUndefined(data.message) && $(data.message).hasClass("message-area")) {
                        element = $(data.message);
                    }

                    this.render(element);

//				console.log("show");
                    var _that = this;
                    var _area = this.feedbackArea;
                    var _overlay = this.overlay;
                    var children = (_area !== undefined ? _area.children() : undefined);

                    _overlay.toggleClass("visible", function () {
                        $(this).animate({"background-color": "rgba(0,0,0, 0.7)"}, 400);
                        if (children !== undefined) {
                            children.addBack().show('slide', {direction: "left", easing: "easeInSine"}, 400, function () {
                                //Making the vertical overflow auto for overflow-y scrolling if needed, while the modal is open
                                _that.feedbackArea.find(".ui-sendfeedback-area").css("overflow-y", "auto");
                                _that.sectionEl.trigger("europass:drawer:opened");
                            });
                        }
                    });
                }

                /**
                 * Hides the Contact Form
                 * @param the triggerd event
                 * 
                 */
                , hideContactForm: function (event) {
                    this.clearContactInfo();

                    this.notificationArea.trigger("europass:message:clear");

                    var _that = this;
                    var _overlay = this.overlay;
                    var _area = this.feedbackArea;
                    var children = (_area !== undefined ? _area.children() : undefined);
                    if (children !== undefined) {
                        children.hide('slide', {direction: "left", easing: "easeInSine"}, 400);
                        //Making the vertical overflow hidden due to an unexpected scrollbar appearance during animation.
                        _that.feedbackArea.find(".ui-sendfeedback-area").css("overflow-y", "hidden");
                    }
                    _area.hide('slide', {direction: "left", easing: "easeInSine"}, 400);
//					console.log("set visible");
                    _overlay.animate({"background-color": "rgba(0,0,0,0)"}, 400, function () {
                        $(this).toggleClass("visible");
                        _that.feedbackArea.find("section[data-step-idx$=\"1\"]").removeClass("hidden");
                        _that.feedbackArea.find(".feedback-email").removeClass("hidden");
                        _that.feedbackArea.find("section[data-step-idx$=\"2\"]").addClass("hidden");
                        _that.feedbackArea.find("section[data-step-idx$=\"3\"]").addClass("hidden");
                    });
                }
                /**
                 * Set the place holders and the default e-mail 
                 * 
                 * 
                 */
                , setDefaults: function (msg) {

                    var textMessage = msg.children("em").html();
                    if (_.isUndefined(textMessage))
                        textMessage = "";

                    if (!Utils.isEmptyObject(msg) && textMessage.length !== 0)
                        this.$el.find("textarea").val(this.buildErrorMsg(msg));

                    var defaultEmail = this.getDefaultEmail();
                    this.feedbackForm.find("input[type$=\"email\"]").val(defaultEmail);
                    this.feedbackForm.find(".with-placeholder").each(function (idx, el) {
                        var input = $(el);
                        input.trigger("blur");
                    });

                    this.notificationArea = this.$el.find(this.noficationSelector).first();
                }

                /**
                 * Clears the ContactInfo model and sets the default values
                 * of the input fields. Also sets the placeholders where are needed.
                 * 
                 */
                , clearContactInfo: function () {
                    this.infoModel.clearModel();
                    this.feedbackForm.find("textarea.formfield").val(null);
                    this.feedbackForm.find("input[type$=\"checkbox\"]").prop("checked", true);
                }

                /**
                 * Updates the ContactInfo model with the passed input values.
                 * If the message is given, then depending on the existence of the email
                 * it proceeds to the next step or sends the e-mail in case is provided.
                 * Validation and emptiness checks trigger respective notifications.   
                 * @param the event
                 * 
                 */
                , updateModel: function (event) {

                    this.notificationArea.trigger("europass:message:clear");

                    var sender = this.feedbackForm.find("input[type$=\"email\"]");
                    var message = this.feedbackForm.find("textarea.formfield");
                    var info = this.feedbackForm.find("input[type$=\"checkbox\"]");
                    var botFilledinput = this.feedbackForm.find("input[type$=\"text\"]");

                    var validEmail = false;

                    var emptyMessage = _.isEmpty(message.val());
                    var emptyEmail = _.isEmpty(sender.val());

                    if (!emptyEmail) {
                        validEmail = Utils.isValidEmail(sender.val());
                    }

                    if (emptyMessage) {
                        this.notificationArea.trigger("europass:message:show", ["error", Notification["feedback.empty.email.msg"], false]);
                    }
                    if (emptyEmail) {
                        this.notificationArea.trigger("europass:message:show", ["error", EditorHelp["ContactInfo.Email.NoReply.Alert"], false])
                    } else if (!validEmail) {
                        this.notificationArea.trigger("europass:message:show", ["error", Notification["feedback.invalid.sender.email.address"], false]);
                    }

                    this.infoModel.set(sender.prop("name"), sender.val(), {silent: true});
                    this.infoModel.set(message.prop("name"), message.val(), {silent: true});

                    if (info.prop("checked")) {
                        this.infoModel.set("ContactInfo.IncludeInfo", true);
                        this.infoModel.populateEnvironmentInfo();
                    } else {
                        this.infoModel.set("ContactInfo.IncludeInfo", false);
                        this.infoModel.set("ContactInfo.EnvironmentInfo", {});
                    }

                    // If botFilledinput field not empty flag as spam
                    if (botFilledinput.attr("value") === "")
                        this.infoModel.set("ContactInfo.Spam", false);
                    else
                        this.infoModel.set("ContactInfo.Spam", true);

                    var btn = $(event.currentTarget);

                    if (validEmail && !emptyEmail && !emptyMessage) {
                        this.sendEmail(btn);
                    }
                }
                , finallySendEmail: function (event) {
                    this.sendEmail($(event.currentTarget));
                }

                , goToPreviousStep: function (event) {
                    this.sectionStep($(event.currentTarget));
                }
                /**
                 * Sends the POST request to the Server and returns a success or fail message. 
                 * @param the event
                 * 
                 */
                , sendEmail: function (sendButton) {
                    this.notificationArea.trigger("europass:message:clear");
                    this.notificationArea = this.$el.find(this.noficationSelector).last();

                    if (sendButton === undefined)
                        sendButton = this.feedbackArea.find("button.validate");

                    sendButton.addClass("disabled");

                    var data = this.infoModel.get("ContactInfo");
                    var httpResource = new Resource(this.postUrl);
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
                                this.feedbackArea.find("section[data-step-idx$=\"1\"]").addClass("hidden");
                                this.feedbackArea.find("section[data-step-idx$=\"2\"]").addClass("hidden");
                                this.feedbackArea.find(".feedback-email").addClass("hidden");
                                this.feedbackArea.find("section[data-step-idx$=\"3\"]").removeClass("hidden");
                                this.notificationArea.trigger("europass:message:show", ["success", Notification["feedback.success.info"], false]);
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

                                switch (errorCode) {
                                    case "email.invalid.recipient":
                                    {
                                        this.notificationArea.trigger("europass:message:show", ["error", Notification["error.code.email.invalid.recipient"], false]);
                                        break;
                                    }
                                    case "email.not.sent":
                                    {
                                        this.notificationArea.trigger("europass:message:show", ["error", Notification["error.code.email.notsent"], false]);
                                        break;
                                    }
                                    default:
                                    {
                                        this.notificationArea.trigger("europass:message:show", ["error", Notification["error.code.internal.server.error"], false]);
                                        break;
                                    }
                                }
                            }
                        },
                        complete: {
                            scope: _that,
                            callback: function (status, responseText) {
                                $("body").trigger("europass:waiting:indicator:hide");
                                sendButton.removeClass("disabled");
                                //Stop the wait indicator
//							console.log("hide waiting indicator");
                            }
                        }

                    });
                }

                /**
                 * Steps to the no-reply info when a e-mail address is not provided.
                 * @param the event
                 * 
                 */
                , sectionStep: function (btn) {

                    if (btn.is("button.previous") || btn.is("button.next")) {
                        this.feedbackArea.find("section[data-step-idx$=\"1\"]").toggleClass("hidden");
                        this.feedbackArea.find("section[data-step-idx$=\"2\"]").toggleClass("hidden");
                        this.feedbackArea.find(".feedback-email").toggleClass("hidden");
                        this.notificationArea = this.$el.find(this.noficationSelector).last();
                    }

                    if (btn.is("button.previous")) {
                        this.notificationArea.trigger("europass:message:clear");
                        this.notificationArea = this.$el.find(this.noficationSelector).first();
                    }
                }

                /**
                 * Get the contact e-mail from SkillsPassport
                 * @returns the e-mail
                 */
                , getDefaultEmail: function () {
                    return Utils.isEmptyObject(this.model.get(this.defaultEmailSection)) ? "" : this.model.get(this.defaultEmailSection);
                }

                /**
                 * Construct a proper text that explains the error and
                 * @returns the text
                 */
                , buildErrorMsg: function (msg) {
                    var errorCode = "\n" + msg.children("em").html(); //msg.children("em")[0].outerHTML

                    var specificErrorMsg = "";
                    if (msg.children("span").length) {
                        specificErrorMsg = "\n" + msg.children("span").text();
                    }

                    if (errorCode.toLowerCase().indexOf("errcode") === -1) // If there is no ErrCode available, dont show anything
                        return "";

                    var trimmedMsg = msg.text().trim();
                    var description = "\n" + trimmedMsg.substr(0, trimmedMsg.indexOf(".") + 1);

                    var defaultText = Notification["textarea.default.error.text"] || "\n\n\n\n (do not delete the text below) \n -----------------------------------------";

                    return defaultText + description + errorCode + specificErrorMsg;
                }
                /**
                 *check if tablet and emulate the hover effect  */
                , checkAndShowContactForm: function (event, data) {

                    var isTablet = (/(iPhone|iPod|iPad)/.test(navigator.userAgent) || (/Android/.test(navigator.userAgent)) || (/BlackBerry/.test(navigator.userAgent)));
                    if (isTablet) {

                        /**
                         * pgia: EWA-1815
                         * Load TabletInteractionsView via require on the tablets variable ONLY if isTablet == true
                         */

                        var handleTipSpotStatus = true;
                        require(['europass/TabletInteractionsView'], function (TabletInteractionsView) {
                            handleTipSpotStatus = TabletInteractionsView.handleTipSpot(event, "currentTarget");
                        }
                        );

                        if (!handleTipSpotStatus)
                            return false;
                    }
                    this.showContactForm(event, data);
                }
            });

            return SendFeedbackView;
        }
);