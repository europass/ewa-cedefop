define(
        [
            'jquery',
            'underscore',
            'backbone',
// 'europass/TabletInteractionsView',		
            'hbs!templates/upload/wizard/step1b'
        ],
        function (
                $, _, Backbone,
//		TabletInteractionsView,		
                ImportTmpl
                ) {
            var WizardStep = Backbone.View.extend({

                tmplImStep: ImportTmpl,

                alreadyRendered: false,

                extraAlreadyRendered: false,

                previous: null,
                next: null,

                htmlTemplate: null,

                /**
                 * Initialization
                 * this.model is an instance of ExportInfo
                 * this.aside is an HTML element of the side area where help and buttons reside
                 * this.next and this.previous are also Step Controllers or null
                 * @param options
                 */
                initialize: function (options) {
                    this.main = options.main;
                    this.aside = options.aside;
                    this.previous = options.previous;
                    this.next = options.next;
                    this.prevBtn = this.aside.find("button.previous");
                    this.nextBtn = this.aside.find("button.next");
                    this.finishBtn = this.aside.find("button.finish");
//				this.cancelBtn = this.aside.find("button.cancel");
                    this.jobPortals = this.main.find("#PostToJobPortals");
                    this.exportWizardLineBreak = this.main.find("#shareLineBreak");
                    this.completeBtn = this.main.find("button.complete");
                    this.connectBtn = this.main.find("button.connect");

                    width = $(window).width();
                    isDesktop = width >= 1121;
                },
                /**
                 * Render the main step area
                 * Decide on the display of buttons
                 */
                render: function () {
                    if (this.alreadyRendered === false || this.forceRerender === true) {
//				console.log("WizardStep:render now");
                        //Main area
                        if (_.isFunction(this.htmlTemplate)) {

                            var context = this.prepareContext();

                            var html = this.htmlTemplate(context);

                            this.$el.html(html);

                            this.alreadyRendered = true;

                            this.extraAlreadyRendered = false;
                        }

                    }

                    //console.log("WizardStep:render already rendered");
                    //console.log("show");
                    this.$el.show();


                    if (this.lastCloudStep("wizard") !== null) {
                        var checked = this.$el.find("input[id$='Loc_cloud']");
                        if (checked !== undefined && checked !== null && checked.length > 0) {
                            checked.prop("checked", true);
                        }
                    }

                    this.exportWizardLineBreak.hide();
                    //Aside area
                    this.updateButtons();

                    this.updateNextBtnId();

                    this.styleNextBtn();

                },
                /**
                 * Render the extra cloud options
                 * Decide on the display of buttons
                 * @param: wizType (type of Wizard to render the options, 1."import" 2."export" )
                 */
                renderExtraStep: function (wizType) {
                    var extraContext = this.prepareExtraContext();
                    if (this.extraAlreadyRendered === false) {
                        if (wizType === "import" && _.isFunction(this.tmplImStep)) {
                            var extraImpHtml = this.tmplImStep(extraContext);
                            this.$el.html(extraImpHtml);
                        }
                        this.extraAlreadyRendered = true;
                        this.alreadyRendered = false;
                    }
                    this.$el.show();
                    //Find and check the previously selected cloud option
                    if (this.lastCloudStep("cloudLoc") !== null) {
                        var checked = this.$el.find("input[id$='Loc_" + this.lastCloudStep("cloudLoc") + "']");
                        if (checked !== undefined && checked !== null && checked.length > 0) {
                            checked.prop("checked", true);
                        }
                    }
                    //Aside area
                    this.updateButtons();

                    this.updateNextBtnId();

                    this.styleNextBtn();

                },
                /**
                 * Prepare the context with which to render the step main template
                 */
                prepareContext: function () {
                    //TO BE OVERRIDEN
                },
                /**
                 * Prepare the context with which to render the step that contains the extra cloud options
                 */
                prepareExtraContext: function () {
                    return {
                        cloudOptions: this.model.europassCloudOptions()
                    };
                },
                /**
                 * Decide on the status of the buttons
                 */
                updateButtons: function () {
                    //TO BE OVERRIDEN
                },
                /**
                 * Adds different id on the next button depending on the step
                 */
                updateNextBtnId: function () {
                    if (this.$el[0] !== undefined) {
                        var id = this.$el[0].getAttribute('id');
                        if (id !== undefined && id !== null && id !== '')
                            this.nextBtn.attr('id', "btn-" + id);
                    }
                },

                /**
                 * Style the blue next button properly at each wizard, according to the needs of each wizard step
                 */
                styleNextBtn: function () {
                    if (isDesktop) {
                        if (this.aside !== undefined) {
                            var firstNextBtn = this.aside.find("button.next[id$='wizard-step-1']");
                            var scndNextBtn = this.aside.find("button.next#btn-export-wizard-step-2");
                            if (firstNextBtn.length > 0) {
                                if (this.extraAlreadyRendered && this.$el.attr("id") === "import-wizard-step-1") {
                                    $(firstNextBtn).css("margin-bottom", "0px");
                                } else {
                                    $(firstNextBtn).css("margin-bottom", "84px");
                                }
                            }
                            if (scndNextBtn.length > 0) {
                                $(scndNextBtn).css("margin-bottom", "0px");
                            }

                        }
                    }
                },

                /**
                 * Save changes to the model
                 */
                saveModel: function () {
                    //TO BE OVERRIDEN
                },

                checkClickNext: function (event, ctx) {
                    var elem = $(event.currentTarget);
                    var checked = elem.siblings("input[type='radio']:checked");
                    if (checked !== undefined && checked.length > 0) {
                        var buttonNext = ctx.aside.find("button.next:not(.disabled)");
                        buttonNext.click();
                    }
                },

                /**
                 * Go back to the previous step
                 */
                doPrevious: function (callback, scope) {

                    this.saveModel();

                    this._doPrevious(callback, scope);
                },
                /**
                 * Proceed to the next step after saving changes
                 */
                doNext: function (callback, scope) {

                    this.saveModel();

                    this._doNext(callback, scope);
                },
                /**
                 * Proceed to the next step
                 */
                _doNext: function (callback, scope) {
                    if (this.next === null || typeof this.next === 'undefined')
                        return;

                    if (!this.canProceed()) {
                        alert("cannot proceed to next step");
                    }

                    this.$el.hide();

                    this.next.render();

                    if (_.isFunction(callback) && _.isObject(scope))
                        callback.call(scope);

                },
                /**
                 * Proceed to the previous step
                 */
                _doPrevious: function (callback, scope) {
                    this.$el.hide();
                    if (this.lastCloudStep("wizard") !== null) {
                        this.previous.renderExtraStep(this.lastCloudStep());
                    } else {
                        this.previous.render();
                    }
                    if (_.isFunction(callback) && _.isObject(scope))
                        callback.call(scope);
                },
                /** Check if the selected location is a cloud option
                 * Return the specific cloud location (this may need adaptations)
                 * Otherwise, return null **/
                lastCloudStep: function (type) {
                    var loc = this.model.getLocation();
                    if (((loc === "dropbox") || (loc === "onedrive") || (loc === "googledrive"))) {
                        //TODO keep this code lines for possible future use (in case the extra step's re-rendering is needed)
                        if (type === "wizard") {
                            if (this.$el.attr("id") === "export-wizard-step-4") {
                                return "export";
                            } else if (this.$el.attr("id") === "import-wizard-step-2") {
                                return "import";
                            }
                        }
                        if (type === "cloudLoc") {
                            return loc;
                        }
                    } else {
                        return null;
                    }
                },
                /**
                 * Decides when it is ok to move to the next step
                 */
                canProceed: function () {
                    //TO BE OVERRIDEN
                },
                /**
                 * Complete the wizard
                 */
                doFinish: function () {
                    //TO BE OVERRIDEN
                },
                /**
                 * Complete and close the wizard
                 */
                doComplete: function () {
                    //TO BE OVERRIDEN if necessary
//			console.log("WizardStep:doComplete");
                    this.$el.trigger("wizard:process:completed");
                },
                /**
                 * Connect to a service
                 */
                doConnect: function () {
                    //TO BE OVERRIDEN
                },
                /**
                 * Cancel the wizard
                 */
                doCancel: function () {
                    //TO BE OVERRIDEN
                },
                /**
                 * Return the HTML element of the message container
                 */
                getMessageContainer: function () {
                    return this.$el.closest(".process").next(".feedback-area");
                },
                /**
                 * Cleanup the area
                 */
                cleanupFeedback: function () {
                    this.getMessageContainer().html("");
                },
                /**
                 * Disable button
                 * @param btn
                 */
                disableButton: function (btn) {
                    btn.addClass("disabled");
                },
                /**
                 * Enable button
                 * @param btn
                 */
                enableButton: function (btn) {
                    btn.removeClass("disabled");
                },
                /**
                 * Toggle the display of an error/success class in the modal
                 * which will allow any necessary styling, like
                 * - changing the social/cloud provider icon
                 * - hiding texts
                 * - etc.
                 */
                setModalFeedbackClass: function (status) {
                    var modal = this.$el.closest(".modal");
                    switch (status) {
                        case "error":
                        {
                            modal.addClass("error-status").removeClass("success-status");
                            break;
                        }
                        case "success":
                        {
                            //modal.removeClass("error-status").addClass("success-status");
                            break;
                        }
                        default:
                        {
                            modal.removeClass("error-status").removeClass("success-status");
                            break;
                        }
                    }
                }
            });

            return WizardStep;
        }
);