define(
        [
            'jquery',
            'underscore',
            'views/WizardStep',

            'hbs!templates/download/wizard/step2'
        ],
        function (
                $, _,
                WizardStep,
                Template
                ) {
            var FileFormatSelectionStep = WizardStep.extend({
                //This step need not be re-rendered to capture any changes in the model
                forceRerender: false,

                htmlTemplate: Template,

                events: _.extend({
                    "click label.formfield-label": "twoStepClick",
                    "click p.show_more_document_formats span": "displayAllFileFormats"
                }, WizardStep.prototype.events),

                /**
                 * @Override
                 * Prepare the context with which to render the step main template
                 */
                prepareContext: function () {
                    return {
                        formats: this.model.europassFileFormats()
                    };
                },

                displayAllFileFormats: function () {
                    $('.word').show();
                    $('.odt').show();
                    $('.xml').show();
                    $('.show_more_document_formats').hide();
                },

                /**
                 * Perform the two-phase click:
                 * 1) highlight choice
                 * 2) go to next
                 */
                twoStepClick: function (event) {
                    WizardStep.prototype.checkClickNext(event, this);
                },

                /**
                 * @Override
                 * Decide on the status of the buttons
                 */
                updateButtons: function () {
                    this.prevBtn.show();

                    this._updateNextButton();
                },
                _updateNextButton: function () {
                    var checked = this.$el.find(":radio:checked");
                    if (checked.length > 0) {
//				if ( this.model.LOCATION.LOCAL === checked.val() ){
//					this.finishBtn.show();
//					this.nextBtn.hide();
//				} else {
                        this.finishBtn.hide();
                        this.nextBtn.show();
                    }
                    return checked;
                },
                findCheckedInput: function () {
                    var checked = this.$el.find(":radio:checked");
                    return checked;
                },
                /**
                 * @Override
                 * Save changes to the model
                 */
                saveModel: function () {
                    var checked = this.$el.find(":radio:checked");
                    var fileFormat = checked.val();
                    this.model.setFileFormat(fileFormat);
                },
                findCheckedInput: function () {
                    var checkedNames = [];
                    this.$el.find(":radio:checked").each(function () {
                        checkedNames.push($(this).attr("value"));

                    });
                    return checkedNames;

                },
                /**
                 * Decide whether it can proceed to next step
                 */
                canProceed: function () {
                    return this.model.hasFileFormat();
                }

            });

            return FileFormatSelectionStep;
        }
);
