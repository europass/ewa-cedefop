define(
        [
            'require',
            'jquery',
//		EWA-1811
//		'underscore',
            'views/WizardController',
            'views/upload/wizard/OneStepImportWizard',
            'hbs!templates/upload/importwizard',
        ],
        function (
                require, $,
                WizardController,
                OneStepImportWizard,
                htmlTemplate) {

            var ImportWizardController = WizardController.extend({

                events: {
                    "click #import-wizard-init-btn": "importWizard"
                },
                WIZARD_REQUIRE_PATH: "views/upload/wizard/ImportWizardProcess",

                WIZARD_FORM_ID: "ImportWizardForm",

                WIZARD_TYPE: "import",

                render: function () {
                    this.$el.html(htmlTemplate());
                },

                importWizard: function () {
                    $('body').addClass('modal_overlay_open');
                    var formEl = this.findWizardForm();
                    var overlay = formEl.closest("div.overlay");
                    var importWizard = new OneStepImportWizard({
                        el: overlay,
                        model: this.model
                    });
                    importWizard.render();
                },

            });

            return ImportWizardController;
        }
);