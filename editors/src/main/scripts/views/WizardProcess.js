define(
        [
            'jquery',
            'jqueryui',
            'underscore',
            'backbone',
            'Utils',
            'i18n!localization/nls/Notification',
            'views/help/HelpView',
            'europass/http/ServicesUri',
            'models/NavigationRoutesInstance',
            'views/main/JobPortalsView'
        ],
        function ($, jqueryui, _, Backbone, Utils, Notification, HelpView, ServicesUri, Router, JobPortalsView) {

            var WizardProcess = Backbone.View.extend({

                alreadyRendered: false,

                //The currently active step
                currentStep: null,
                //The model holding all necessary information
                info: null,
                //The steps
                steps: [],

                //The help per step
                helpView: null,
                jobPortalsView: null,
                helpSteps: {},

                events: {

                    //On Cancel - On hit Close
                    "click button.cancel:not(.disabled)": "cancelled",
                    "click button.close:not(.disabled) ": "cancelled",

                    //On complete
                    "click button.complete:not(.disabled) ": "completed",

                    //On connect
                    "click button.connect:not(.disabled) ": "connected",

                    //On Previous
                    "click button.previous:not(.disabled)": "goToPrevious",
                    //On Next
                    "click button.next:not(.disabled)": "goToNext",

                    //On Download/ Store-to
                    "click button.finish:not(.disabled)": "finished",

                    //When the process has completed successfully and the wizard may close.
                    "wizard:process:completed ": "closeAreaAndCleanup",

                    //Sharing to job portals
                    "click #shareDocBtn": "hideSuccessAndShowShare"
                },
                /**
                 * Clean up work when the form closes
                 */
                onCloseArea: function () {
                    if (_.isObject(this.info)) {
                        this.info.clear();

                        var feedbackArea = this.main.find(".feedback-area");
                        if (!feedbackArea.is(':empty'))
                            feedbackArea.empty();
                    }
//			console.log("close area");
                    this.currentStep = 0;
                },
                /**
                 * Configure the process and the steps
                 */
                initialize: function (options) {
                    //Main area
                    this.main = this.$el.find(".main");
                    //Aside
                    this.aside = this.$el.find(".side");

                    //Models
                    this.model = options.model;
//			width = $(window).width();
//			isDesktop = width >= 1121;
                    //TOBE OVERRIDEN the overriding objects must set the info property
                    //Skeleton template and Buttons template to be defined in this in instances
                },
                /**
                 * Render the skeleton, and -after- that request the 1st step to begin
                 * @param: stepIdx preferred step to open
                 * @param: stepParam
                 */
                render: function (stepIdx, stepParam) {
                    if (this.alreadyRendered === false) {
//				console.log("WizardProcess:render now");

                        var mainHtml = this.SKELETON_TPL();
                        this.main.html(mainHtml);

                        var btnObj = this.appendMenuButtonsObj();
                        var asideHtml = this.BUTTONS_TPL(btnObj);

                        this.aside.append(asideHtml);

                        this.alreadyRendered = true;

                        this.setupProcess();

                    }
                    this.renderFirstStep(stepIdx, stepParam);
//			console.log("WizardProcess: already rendered");
                },
                /**
                 * Setup the Process of steps
                 */
                setupProcess: function () {
                    //TOBE OVERRIDEN: the overriding classes must set the steps
                    //Help
                    this.helpView = new HelpView({
                        el: this.$el
                    });

                },
                renderFirstStep: function (stepIdx, stepParam) {
                    if (_.isUndefined(stepIdx) || !_.isNumber(stepIdx))
                        this.currentStep = 0;
                    else
                        this.currentStep = stepIdx;
//			console.log("First step to render is : " + this.currentStep );

                    this._updateStepInEl(this.currentStep);

                    var currentStepView = this.steps[ this.currentStep ];

                    if (_.isEmpty(stepParam)) {
                        currentStepView.render();
                    } else {
//				console.log("Render step view with param: " + stepParam);
                        currentStepView.render(stepParam);
                    }
                    this.renderHelp(this.currentStep);

                },
                /**
                 * Updates the "data-active-step" custom attribute of the top most fieldset.
                 * @param step
                 */
                _updateStepInEl: function (step) {
                    this.$el.find(".process").children(".step:not(.step" + (step + 1) + ")").hide();
                    this.$el.find(".process").attr("data-active-step", step + 1);
                },
                /**
                 * Ask the current step to go back
                 */
                goToPrevious: function () {
                    this.steps[ this.currentStep ].doPrevious(this._isPrevious, this);
                },
                _isPrevious: function () {
                    this.currentStep = this.currentStep - 1;
                    this._updateStepInEl(this.currentStep);
                    this.renderHelp(this.currentStep);
                    this.$el.trigger("europass:wizard:step:changed");
                },
                /**
                 * Ask the current step to go forth
                 */
                goToNext: function () {
                    this.steps[ this.currentStep ].doNext(this._isNext, this);
                },
                _isNext: function () {
                    this.currentStep = this.currentStep + 1;
                    this._updateStepInEl(this.currentStep);
                    this.renderHelp(this.currentStep);
                    this.$el.trigger("europass:wizard:step:changed");
                },
                /**
                 * Finish button is clicked
                 */
                finished: function () {
                    this.steps[ this.currentStep ].doFinish();
                },
                /**
                 * Complete button is clicked
                 */
                completed: function (event) {
                    if ($(event.target).attr("id")) {
                        var btnId = $(event.target).attr("id");
                        if (btnId === 'shareDocBtn') {
                            return;
                        }
                    }
                    var currentStep = this.steps[ this.currentStep ];
                    if (_.isObject(currentStep) && _.isFunction(currentStep.doComplete)) {
                        currentStep.doComplete();
                    }
                },
                /**
                 * Connect button is clicked
                 */
                connected: function () {
                    var currentStep = this.steps[ this.currentStep ];
                    if (_.isObject(currentStep) && _.isFunction(currentStep.doConnect)) {
                        currentStep.doConnect();
                    }
                },
                /**
                 * Wizard is cancelled
                 * @param ev
                 */
                cancelled: function (ev) {
                    this.steps[ this.currentStep ].doCancel();
                    this.closeArea();
                },
                /**
                 * Cleanup and close wizard
                 */
                closeAreaAndCleanup: function () {
//			console.log("WizardProcess:close area and cleanup");
                    this.closeArea();
                },
                /**
                 * Close the Wizard
                 */
                closeArea: function () {
//			console.log("WizardProcess:close area");
//			this.onCloseArea();

                    // Find Active Route and redirect to matching document section
                    var activeRoute = Router.findActiveView();
                    if (activeRoute.indexOf("upload") === 0) {

                        var parts = activeRoute.split(".");

                        // esp, lp, cl
                        if (!_.isUndefined(parts[1]) && parts[1] !== "") {
                            Router.navigateView("compose." + parts[1]);
                        } else {	// cv, cv-esp
                            Router.navigateView("compose.ecv");
                        }
                    }

                    var _that = this;
                    var overlay = this.$el;
                    var modal = (this.$el !== undefined ? this.$el.find(".modal").first() : undefined);
                    var children = (modal !== undefined ? modal.children() : undefined);
                    //Find if the overlay of the tablet-opened right menu exists
                    //This need to be found in order to be removed, in case the user slides the right side-bar and also opens a modal
                    //That happens when the user slides a hovered section
                    var overlayTopMid = $("body").find(".transition-overlay");

                    var blueNextBtn = (children !== undefined ? children.find("button.next#btn-export-wizard-step-1") : undefined);
                    if (children !== undefined) {
                        children.hide('slide', {direction: "right", easing: "easeInSine"}, 300);
                    }
                    if (modal !== undefined) {
                        modal.hide('slide', {direction: "right", easing: "easeInSine"}, 400);
                        modal.find("form.drawer").css("overflow-y", "hidden");
                        var currentStep = _that.steps[ _that.currentStep ];
                        if (_.isObject(currentStep) && _.isFunction(currentStep.cleanup)) {
                            currentStep.cleanup();
                        }
                        _that.onCloseArea();

                        overlay.animate({"background-color": "rgba(0,0,0, 0)"}, 400, function () {
                            $(this).hide();
                            if (overlayTopMid !== undefined && overlayTopMid !== null && overlayTopMid.length !== 0) {
                                $(overlayTopMid).remove();
                            }
                        });

                        blueNextBtn.removeClass("btn-next-bot").promise().done(function () {
                            blueNextBtn.css("margin-bottom", "84px");
                        });
                    }
                    //this.helpView.hideVisible();
                },
                /**
                 * Render the Help Area
                 * @param step
                 */
                renderHelp: function (step) {
//			console.log("Help for : " + (step+1) );
                    this.helpView.helpSection = this.WIZARD_TYPE + ".wizard.help.step" + (step + 1);

                    var helpStep = this.WIZARD_TYPE + step;

                    if (!(this.helpSteps[ helpStep] === true)) {
//				console.log("not already rendered, so render!");
                        this.helpView.render(true);
                        this.helpSteps[helpStep] = true;

                    } else {
//				console.log("already rendered, so manage help headers");
                        //Manage the header
                        //Hide any previous header help
                        this.helpView.findAllHeaders().hide();
                        //this.helpView.hideVisible();
                        this.helpView.findHeader().show();

                    }
                },
                hideSuccessAndShowShare: function () {
                    //complete and close wizard
                    var currentStep = this.steps[ this.currentStep ];
                    if (_.isObject(currentStep) && _.isFunction(currentStep.doComplete)) {
                        currentStep.doComplete();
                    }
                    //open share drawer
                    $("#share-document-btn").click();
                },
                appendMenuButtonsObj: function () {
                    var formName = this.$el.find("form").attr("id");
                    var c = formName.substr(formName.lastIndexOf(".") + 1);
                    var className = c.substr(0, c.indexOf("["));
                    if (className === '') {
                        className = c;
                    }
                    var buttonsObj = {
                        className: className,
                        formName: formName
                    };
                    return buttonsObj;
                }
            });

            return WizardProcess;
        }
);
