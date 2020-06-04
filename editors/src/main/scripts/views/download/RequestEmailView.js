define(
        [
            'jquery',
            'jqueryui',
            'underscore',
            'backbone',
            'HttpUtils',
            'Utils',
            'views/forms/DefaultsFormView',
            'views/download/DownloadController',
            'europass/http/Resource',
            'europass/http/MediaType',

            'hbs!templates/download/request_email',
            'hbs!templates/download/email_invalid_feedback',
            'hbs!templates/download/email_valid_feedback',

            'i18n!localization/nls/GuiLabel',
            'analytics/EventsController'
        ],
        function ($, jqueryui, _, Backbone, HttpUtils,
                Utils, DefaultsFormView, DownloadController, Resource, MediaType,
                Template, EmailInvalidTemplate, EmailValidTemplate,
                GuiLabel, Events) {

            var RequestEmailView = Backbone.View.extend({
                name: "email",

                alreadyRendered: false,
                event: new Events,
                events: {
                    /* email validation will be on clicking send
                     "keyup :input:not(:button)" : "checkEmailOnType",
                     "keydown :input:not(:button)" : "checkEmailOnType"
                     */
                    "click button.connect": "checkEmailValidity",

                    "keyup :input:not(:button)": "clearNotificationsOnType",
                    //"keydown :input:not(:button)" : "clearNotificationsOnType" ndim: why call it twice?			
                }

                , onClose: function () {
                    this.downloadController.cleanup();
                    delete this.downloadController;

                    this.alreadyRendered = false;
                }
                , initialize: function (options) {
                    this.emailInvalidTpl = EmailInvalidTemplate();
                    this.emailValidTpl = EmailValidTemplate();
                    this.emailContainer = options.emailContainer;
                    this.downloadController = new DownloadController({
                        relatedController: this,
                        messageContainer: options.messageContainer,
                        info: options.info
                    });
                    this.parent = options.parentView;
                }
                /**
                 * Render the View
                 */
                , render: function () {

                    if (this.alreadyRendered === false) {

                        var emailTo = this.model.get("SkillsPassport.LearnerInfo.Identification.ContactInfo.Email.Contact");
                        var html = Template({
                            Email: emailTo
                        });
                        $('#email_area').html(html);

                        this.alreadyRendered = true;

                        this.frm = this.$el.find("#document-recipient-email-form");

                        //Placeholder functionality!
                        this.defaultsFormView = new DefaultsFormView({
                            el: this.frm
                        });
                        this.defaultsFormView.render();

                    }

                    this.prepareSend();

                    /*var input = this.$el.find(":input:not(:button)") ;
                     if ( input.val() !== "" ){
                     this.checkEmailValidity( input );
                     }*/
                    this.clearFormAndFeedback(this.frm);
                }
                , getRecipientValue: function () {
                    return this.$el.find(":input[name=\"Email\"]").val();
                }
                /**
                 * The send email button is clicked
                 */
                , doConnect: function () {
                    //validate email				
                    var input = this.$el.find("input");//input#document-recipient-email	
                    console.log(this.$el)
                    //input#document-recipient-email
                    if (!this.checkEmailValidity(input)) {
                        return;
                    }

                    this.clearFormAndFeedback(this.frm);

                    this._doSend();

                },
                /**
                 * Do the server-side POST
                 */
                _doSend: function () {
                    console.log('email')
                    $("body").trigger("europass:waiting:indicator:show", [true]);

                    var model = this.model.conversion().toTransferable();

                    var emailTo = this.getRecipientValue();
                    var data = {
                        json: Utils.encodePlusCharPercent(model),
                        recipient: emailTo
                    };

                    var url = this.downloadController.decideUrl();
                    var httpResource = new Resource(url);
                    this.event.exportTo(url);
                    httpResource.contentType(MediaType.json);

                    var _that = this;
                    httpResource._post({
                        data: data
                    }, {
                        success: {
                            scope: _that,
                            callback: function (response) {
                                $("body").trigger("europass:waiting:indicator:hide");

                                var filename = $(response).filter("meta[name='filename']").attr("content");
                                if (_.isUndefined(filename))
                                    filename = "Europass CV";

                                this.$el.trigger("europass:wizard:export:complete");
                                this.hideSend();

                                this.downloadController.triggerMessageWithPathFileEmail("success.email.cv.sent", filename, emailTo, "success", false);
                            }
                        },
                        error: {
                            scope: _that,
                            callback: function (status, responseText) {
                                $("body").trigger("europass:waiting:indicator:hide");
//							this.downloadController.onUploadFailure( status, responseText, "\""+emailTo+"\"");
                                this.downloadController.onUploadFailure(status, responseText, "");
                                //this.enableSend();
                            }
                        }
                    });
                    this.parent.onClose();
                }
                /**
                 * The cancel button is clicked
                 */
                , doCancel: function (event) {
                    //clear input field value
                    this.clearFormAndFeedback(this.frm);
                }
                /**
                 * Clean up the form field and any other feedback
                 */
                , clearFormAndFeedback: function (frm) {
                    this.frm.find(":input:not(:button)").each(function () {
                        var input = $(this);
                        //clear feedback
                        input.siblings("span.validation-feedback").each(function () {
                            $(this).remove();
                        });
                    });
                }
                /**
                 * Perform email validation while typing
                 */
                , checkEmailOnType: function (event) {
                    var input = $(event.target);
                    console.log(input);
                    this.checkEmailValidity(input);
                }

                /**
                 * Clears email validation notifications while typing
                 */
                , clearNotificationsOnType: function (event) {
                    var input = $(event.target);
                    var parentDiv = input.parent(".placeholding");
                    var modalDiv = parentDiv.parents(" .modal,email ");

                    var invalidFeedback = parentDiv.next("span.validation-feedback.invalid");
                    var validFeedback = parentDiv.next("span.validation-feedback.valid");

                    if (invalidFeedback.length > 0) { //hide valid if now invalid
                        invalidFeedback.fadeOut('slow', function () {
                            $(this).remove();
                        });
                    }
                    if (validFeedback.length > 0) { //hide valid if now invalid
                        validFeedback.fadeOut('slow', function () {
                            $(this).remove();
                        });
                    }
                    //fade Previous image out, fade default image in

                    if ($(modalDiv).hasClass("error-status")) {

                        $(modalDiv).removeClass("error-status");
                        $(".modal .email span.icon").fadeOut("slow", function () {
                        }).fadeIn('slow', function () {});
                        //fade feedback-area out
                        $(".feedback-area").children().fadeOut('slow', function () {
                            $(this).remove();
                        });
                    }

                }

                /**
                 * Email validation
                 */
                , checkEmailValidity: function (input) {

                    var invalidMailMessage = {errorMessage: "error.code.email.invalid.recipient"};  //default value is for invalid mail
                    var emptyMailMessage = {errorMessage: "feedback.empty.email.error"};
                    var errorContext = invalidMailMessage;
                    console.log(input)
                    this.isEmailValid = Utils.isValidEmail(input.val());
                    this.isEmailEmpty = _.isEmpty(input.val());


                    var parentDiv = input.parent(".placeholding");

                    var invalidFeedback = parentDiv.next("span.validation-feedback.invalid");
                    var validFeedback = parentDiv.next("span.validation-feedback.valid");

                    if (this.isEmailEmpty === true) {
                        errorContext = emptyMailMessage;	  // set the errorMessage parameter accordingly
                        if (invalidFeedback.length === 0) { //add invalid if not already there
                            parentDiv.after(EmailInvalidTemplate(errorContext));
                        }
                        return false;
                    }

                    if (this.isEmailValid === false) {
                        if (validFeedback.length > 0) { //hide valid if now invalid
                            validFeedback.fadeOut('slow', function () {
                                $(this).remove();
                            });
                        }
                        if (invalidFeedback.length === 0) {//add invalid if not already there
                            errorContext = invalidMailMessage; 	 	//set the errorMessage accordingly
                            parentDiv.after(EmailInvalidTemplate(errorContext));
                        }

                        return false;

                    } else if (this.isEmailValid === true) {

                        if (invalidFeedback.length > 0) { //hide invalid if now invalid
                            invalidFeedback.fadeOut('slow', function () {
                                $(this).remove();
                            });
                        }
                        if (validFeedback.length === 0) { //add valid if not already there
                            parentDiv.after(EmailValidTemplate);
                        }


                        return true;
                    }
                },
                /**
                 * Enable/Show the connect button
                 */
                enableSend: function () {

                },
                /**
                 * Disable the connect button 
                 */
                disableSend: function () {

                },
                /**
                 * Set the text properly and disable it
                 */
                prepareSend: function () {
                    //remove any error or success status info				
                },
                /**
                 * Hide the send button, on a successful outcome
                 */
                hideSend: function () {
                    //add success status info				
                }
            });

            return RequestEmailView;
        }
);