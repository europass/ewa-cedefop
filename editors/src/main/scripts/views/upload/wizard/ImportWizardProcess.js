define(
        [
            'jquery', // 'underscore',
            'views/WizardProcess',
            'hbs!templates/upload/wizard/skeleton',
            'hbs!templates/upload/wizard/buttons',
            'views/upload/wizard/ImportInfo',
            'views/upload/wizard/LocationSelectionStep',
            'views/upload/wizard/DoImportStep',
            'views/upload/wizard/DocTypeSelectionStep'
        ],
        function ($, WizardProcess, SkeletonTpl, ButtonsTpl, ImportInfo, LocationSelectionStep, DoImportStep, DocTypeSelectionStep) {

            var ImportWizardProcess = WizardProcess.extend({

                WIZARD_FORM_ID: "#ImportWizard",

                WIZARD_TYPE: "import",

                SKELETON_TPL: SkeletonTpl,
                BUTTONS_TPL: ButtonsTpl,

                /**
                 * @Override
                 */
                onClose: function () {
                    this.model.unbind("model:uploaded:cloud", this.closeAreaAndCleanup);
                    this.model.unbind("model:uploaded:social", this.closeAreaAndCleanup);
                    this.model.unbind("model:uploaded:esp", this.closeAreaAndCleanup);
                    this.model.unbind("model:loaded:cloud:document", this.closeAreaAndCleanup);
                },
                /**
                 * @Override
                 * Configure the process and the steps
                 */
                initialize: function (options) {
                    WizardProcess.prototype.initialize.apply(this, [options]);
                    //Models
                    this.model = options.model;
                    this.info = new ImportInfo();
                    this.info.setSkillsPassport(this.model);

                    this.model.bind("model:uploaded:cloud", this.closeAreaAndCleanup, this);
                    this.model.bind("model:uploaded:social", this.closeAreaAndCleanup, this);
                    this.model.bind("model:uploaded:esp", this.closeAreaAndCleanup, this);
                    this.model.bind("model:loaded:cloud:document", this.closeAreaAndCleanup, this);
                },
                /**
                 * @Override
                 * Setup the Process of steps
                 */
                setupProcess: function () {

                    //Process
                    var step1 = new LocationSelectionStep({
                        el: $("#import-wizard-step-1"),
                        main: this.main,
                        aside: this.aside,
                        model: this.info,
                        previous: null
                    });
                    var step2 = new DoImportStep({
                        el: $("#import-wizard-step-2"),
                        main: this.main,
                        aside: this.aside,
                        model: this.info,
                        previous: step1
                    });
                    var step3 = new DocTypeSelectionStep({
                        el: $("#import-wizard-step-3"),
                        main: this.main,
                        aside: this.aside,
                        model: this.info,
                        previous: step2
                    });
                    step1.next = step2;
                    step2.next = step3;

                    this.steps = [step1, step2, step3];

                    WizardProcess.prototype.setupProcess.call(this);
                }
            });

            return ImportWizardProcess;
        }
);