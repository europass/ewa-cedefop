define(
        [
            'jquery',
            'underscore',
            'views/WizardStep',
            'hbs!templates/upload/wizard/step1',
        ],
        function ($, _, WizardStep, Template) {
            var LocationSelectionStep = WizardStep.extend({

                downloadView: null,

                extraAlreadyRendered: false,

                htmlTemplate: Template,

                events: _.extend({
                    "change :radio": "changed",
                    "click label.formfield-label[for!='Import_Option_Loc_cloud']": "twoStepClick",
                    "click label.formfield-label[for='Import_Option_Loc_cloud']": "adaptStep"
                }, WizardStep.prototype.events),
                /**
                 * @Override
                 * Prepare the context with which to render the step main template
                 */
                prepareContext: function () {
                    return {
                        locations: this.model.europassLocations()
                    };
                },
                /**
                 * @Override
                 * Prepare the context with which to render the step main template
                 */
                prepareExtraContext: function () {
                    return {
                        cloudOptions: this.model.europassCloudOptions()
                    };
                },

                /**
                 * Perform the two-phase click:
                 * 1) highlight choice
                 * 2) go to next
                 */
                twoStepClick: function (event) {
                    WizardStep.prototype.checkClickNext(event, this);
                },

                adaptStep: function (event) {
                    var elem = $(event.currentTarget);
                    var checked = elem.siblings("input[type='radio']:checked");
                    if (checked !== undefined && checked.length > 0) {
                        this.renderExtraStep("import");
                    }
                },
                /**
                 * @Override
                 * @param callback
                 * @param scope
                 */
                doPrevious: function (callback, scope) {
                    if (this.extraAlreadyRendered == true) {
                        this.saveModel();
                        this.render();
                        var checked = this.$el.find("#Import_Option_Loc_cloud");
                        if (checked !== undefined && checked !== null && checked.length > 0) {
                            checked.prop("checked", true);
                        }
                    } else {
                        //Clean up feedback area
                        this.cleanupFeedback();
                        this.finishBtn.hide();
                        WizardStep.prototype.doPrevious.apply(this, [callback, scope]);
                    }
                },

                /**
                 * @Override
                 * @param callback
                 * @param scope
                 */
                doNext: function (callback, scope) {
                    //Clean up feedback area
//			this.$el.closest(".process").find("legend").first().hide();
                    if (this.extraAlreadyRendered === false && this.checkCloudOption() === true) {
                        this.renderExtraStep("import");
                    } else {
                        this.cleanupFeedback();
                        WizardStep.prototype.doNext.apply(this, [callback, scope]);
                    }
                },

                checkCloudOption: function () {
                    var checked = this.$el.find("#Import_Option_Loc_cloud");
                    if (checked !== undefined && checked !== null && checked.length > 0 && checked.prop("checked") === true) {
                        return true;
                    }
                },

                /**
                 * @Override
                 * Decide on the status of the buttons
                 */
                updateButtons: function () {
                    if (this.extraAlreadyRendered) {
                        this.prevBtn.show();
                    } else {
                        this.prevBtn.hide();
                    }

                    this._updateButtons();
                },
                _updateButtons: function () {
                    this.finishBtn.hide();
                    this.nextBtn.show();
                },
                /**
                 * @Override
                 * Save changes to the model
                 */
                saveModel: function () {
                    var checked = this.$el.find(":radio:checked");
                    var location = checked.val();
                    this.model.setLocation(location);
                    return checked;
                },
                /**
                 * Decide whether it can proceed to next step
                 */
                canProceed: function () {
                    return this.model.hasLocation();
                },
                /**
                 * Location option changed
                 */
                changed: function (event) {
                    this._updateButtons();
                }
            });

            return LocationSelectionStep;
        }
);