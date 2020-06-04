define(
        [
            'jquery',
            'underscore',
            'backbone',
            'hbs!templates/upload/wizard/step3',
            'i18n!localization/nls/GuiLabel',
            'views/WizardStep',
            'HelperManageModelUtils'
        ],
        function ($, _, Backbone, Template, GuiLabel, WizardStep, HelperManageModelUtils) {

            var DocTypeSelectionStep = WizardStep.extend({

                htmlTemplate: Template,
                events: _.extend({

                }, WizardStep.prototype.events),

                /**
                 * @Override
                 */
                canProceed: function () {
                    return true;
                },

                /**
                 * @Override
                 */
                render: function () {

                    WizardStep.prototype.render.call(this);

                    HelperManageModelUtils.customiseTemplateHtml(this.$el, this.model.model);
                },
                /**
                 * @Override
                 * Prepare the context with which to render the step main template
                 * {
                 *   docs: [ ... ],
                 * }
                 */
                prepareContext: function () {

                    var docs = [{name: "ECL", title: GuiLabel["import.wizard.step3.doc.option.ECL"], empty: false},
                        {name: "REST", title: GuiLabel["import.wizard.step3.doc.option.rest"], empty: false}];
                    var context = {
                        docs: docs
                    };

                    return context;
                }

            });

            return DocTypeSelectionStep;

        }
);
