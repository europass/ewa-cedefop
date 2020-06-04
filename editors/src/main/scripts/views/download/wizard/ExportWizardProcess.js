define(
        [
            'jquery',
//		EWA-1811
//		'underscore',

            'views/WizardProcess',

            'hbs!templates/download/wizard/skeleton',
            'hbs!templates/download/wizard/buttons',

            'views/download/wizard/ExportInfo',

            'views/download/wizard/DocTypeSelectionStep',
            'views/download/wizard/FileFormatSelectionStep',
            'views/download/wizard/LocationSelectionStep',
            'views/download/wizard/DoExportStep'

        ],
        function (
                $,
//		EWA-1811
//		_, 
                WizardProcess,
                SkeletonTpl,
                ButtonsTpl,
                ExportInfo,
                DocTypeSelectionStep,
                FileFormatSelectionStep,
                LocationSelectionStep,
                DoExportStep
                ) {

            var ExportWizardProcess = WizardProcess.extend({
                WIZARD_FORM_ID: "#ExportWizard",

                WIZARD_TYPE: "export",

                SKELETON_TPL: SkeletonTpl,
                BUTTONS_TPL: ButtonsTpl,

                /**
                 * @Override
                 * Configure the process and the steps
                 */
                initialize: function (options) {
                    WizardProcess.prototype.initialize.apply(this, [options]);
                    //Models
                    this.model = options.model;
                    this.info = new ExportInfo();
                    this.info.setSkillsPassport(this.model);

                },
                /**
                 * @Override
                 * Setup the Process of steps
                 */
                setupProcess: function () {

                    //Process
                    var step1 = new DocTypeSelectionStep({
                        el: $("#export-wizard-step-1"),
                        main: this.main,
                        aside: this.aside,
                        model: this.info,
                        previous: null
                    });
                    var step2 = new FileFormatSelectionStep({
                        el: $("#export-wizard-step-2"),
                        main: this.main,
                        aside: this.aside,
                        model: this.info,
                        previous: step1
                    });
                    var step3 = new LocationSelectionStep({
                        el: $("#export-wizard-step-3"),
                        main: this.main,
                        aside: this.aside,
                        model: this.info,
                        previous: step2
                    });
                    var step4 = new DoExportStep({
                        el: $("#export-wizard-step-4"),
                        main: this.main,
                        aside: this.aside,
                        model: this.info,
                        previous: step3
                    });
                    step1.next = step2;
                    step2.next = step3;
                    step3.next = step4;

                    this.steps = [step1, step2, step3, step4];

                    WizardProcess.prototype.setupProcess.call(this);
                }
            });

            return ExportWizardProcess;
        }
);