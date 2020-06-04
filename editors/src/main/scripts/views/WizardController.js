define(
        [
            'require',
            'jquery',
            'jqueryui', //'underscore',
            'backbone',

            'hbs!templates/main/drawer'
//	, 'i18n!localization/nls/Notification'
        ],
        function (require, $, jqueryui, Backbone, DrawerTpl) { //, Notification

            var WizardController = Backbone.View.extend({
                wizard: null,

                onClose: function () {
                    if (this.wizard)
                        this.wizard.onClose();
                    delete this.wizard;
                },
                initialize: function (options) {
//			var width = $(window).width();
//			var isDesktop = width >= 1121;
                    this.render();
                },
                /**
                 * On click of the export button the wizard is instantiated
                 */
                initiateWizard: function (event, stepIdx, stepParam) {
                    //$("body").trigger("europass:waiting:indicator:show");

                    var formEl = this.findWizardForm();

                    var overlay = formEl.closest("div.overlay");

                    if (this.wizard === null) {
//				console.log("WizardController: wizard is null");
//				console.log(this.wizard);
                        //Dynamic require!
                        var _that = this;

                        var bundlePath = "assembly/" + this.WIZARD_TYPE + "Wizard";
//				console.log("Wizard bundle path: " + bundlePath );
                        require(
                                [bundlePath, 'i18n!localization/nls/Notification'],
                                function () {
                                    require(
                                            [_that.WIZARD_REQUIRE_PATH], // Notification ], 
                                            //SUCCESS
                                                    function (WizardProcess) {
                                                        if ($.isFunction(WizardProcess)) {

                                                            var wizard = new WizardProcess({
                                                                el: overlay,
                                                                model: _that.model
                                                            });
                                                            _that.wizard = wizard;

                                                            _that.wizard.render(stepIdx, stepParam);

                                                            //Show the Drawer after rendering is done!
                                                            _that.openWizard(overlay);
                                                        }
                                                    },
                                                    //ERROR
                                                            function (args) {
                                                                $("body").trigger("europass:waiting:indicator:hide");

                                                                $("body").trigger("europass:message:show",
                                                                        ["error",
                                                                            (Notification[_that.WIZARD_TYPE + ".wizard.loading.failed"] || "There was a problem loading the Wizard. Please refresh your browser and try again. If the problem persists, contact Europass Team")
                                                                        ]
                                                                        );
                                                            }
                                                    );
                                                }
                                        );

                                    } else {
//				console.log("WizardController: wizard is not null, so render");
//				console.log(this.wizard);
                                this.wizard.render(stepIdx, stepParam);
                                //Show the Drawer after rendering is done!
                                this.openWizard(overlay);
                            }
                        },
                        /**
                         * Use the drawer effect to open the wizard
                         * @param overlay
                         */
                        openWizard: function (overlay) {
                            $('body').addClass('modal_overlay_open');
                            var modal = (overlay !== undefined ? overlay.children(".modal") : undefined);
                            var children = (modal !== undefined ? modal.children() : undefined);
                            var blueNextBtn = (children !== undefined ? children.find("button.next#btn-export-wizard-step-1") : undefined);

                            overlay.show(function () {
                                $(this).animate({"background-color": "rgba(0,0,0, 0.7)"}, 400);
                                modal.find("form.drawer").css("overflow-y", "scroll");
                                if (children !== undefined && modal !== undefined) {
                                    children.addBack().show('slide', {direction: "right", easing: "easeInSine"}, 400, function () {
                                        $("body").trigger("europass:drawer:opened").promise().done(function () {
                                            blueNextBtn.css("bottom", "0px");
                                            blueNextBtn.addClass("btn-next-bot");
                                        });
                                    });
                                }
                                $("body").trigger("europass:waiting:indicator:hide");
                            });
                        },
                        /**
                         * Searches the DOM for the HTML element of the Form.
                         * If it does not exist, it must be created.
                         * @returns HTML <form> element
                         */
                        findWizardForm: function () {
                            var formEl = $("body").find("#" + this.WIZARD_FORM_ID);

                            if (formEl.length === 0) {
                                var context = {
                                    id: this.WIZARD_FORM_ID,
                                    data_rel_section: "SkillsPassport"
                                };
                                var html = DrawerTpl(context);
                                var el = $(html);
                                el.appendTo("body");

                                formEl = el.find("form.main");
                            }
                            return formEl;
                        }
                    });

                    return WizardController;
                }
        );
