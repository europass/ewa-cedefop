define(
        [
            'require',
            'jquery',
            'underscore',
            'views/WizardStep',
            'i18n!localization/nls/GuiLabel',
            'i18n!localization/nls/Notification',
            'europass/http/WindowConfigInstance',
            'Utils',
            'europass/http/ServicesUri'
        ],
        function (
                require,
                $, _,
                WizardStep,
                GuiLabel,
                Notification,
                WindowConfig,
                Utils,
                ServicesUri
                ) {
            var WizardDoActionStep = WizardStep.extend({

                currentView: null,

                currentViewPath: null,

                //TO BE OVERRIDEN events
                //e.g "europass:wizard:export:complete" : "onComplete"
                /**
                 * @Override
                 */
                render: function (locationParam) {
                    WizardStep.prototype.render.call(this);

                    this.renderView(locationParam);
                },
                /**
                 * @Override
                 * Decide on the status of the buttons
                 */
                updateButtons: function () {
                    this.prevBtn.show();
                    this.nextBtn.hide();

                    this._updateButtons();
                },
                _updateButtons: function () {
                    if (this.isComplete === true && !Utils.isPartnerAvailable()) {
                        //Show Finish
                        this.exportWizardLineBreak.show();
                        this.completeBtn.show();
                        this.jobPortals.show();
                        this.jobPortals.children().show();
                    }
                },
                renderView: function (locationParam) {
                    $("body").trigger("europass:waiting:indicator:show");

                    var location = this.model.getLocation();
//			console.log("DoActionStep: location is: "+location);
                    if (!_.isUndefined(locationParam) && !_.isUndefined(location) && locationParam !== location) {
                        location = locationParam;
//				console.log("But we will render with location: " + location);
                    }

                    //Set class name to the upper modal
                    this.$el.closest(".modal").addClass(location);
                    //Hide the legend
                    this.$el.closest(".process").find("legend").first().hide();

                    var viewPath = this.findViewPath(location);

                    if (_.isEmpty(viewPath))
                        return;

                    if (this.currentViewPath === null || this.currentViewPath !== viewPath) {
//				console.log("previous view was : " + this.currentViewPath );
//				console.log("So... render a new view : " + viewPath );
                        this.currentViewPath = viewPath;

                        var _that = this;

                        require(
                                [viewPath, Notification],
                                //SUCCESS
                                        function (View) {
                                            if ($.isFunction(View)) {

                                                var view = new View({
                                                    el: _that.$el,
                                                    model: _that.model.getSkillsPassport(),
                                                    info: _that.model,
                                                    messageContainer: _that.getMessageContainer(),
                                                    parentView: _that
                                                });

                                                //Close the previous view!
                                                if (_that.currentView !== null)
                                                    _that.currentView.onClose();

                                                //Set to the new View!
                                                _that.currentView = view;

                                                _that.currentView.render();

                                                $("body").trigger("europass:waiting:indicator:hide");

                                                _that.showMacOsWarning();
                                            }
                                        },
                                        //ERROR
                                                function (args) {
                                                    $("body").trigger("europass:waiting:indicator:hide");

                                                    _that.getMessageContainer().trigger("europass:message:show",
                                                            ["error",
                                                                (Notification["export.wizard.loading.step.failed"] || "There was a problem loading this step of the Wizard. Please refresh your browser and try again. If the problem persists, contact Europass Team")
                                                            ]
                                                            );
                                                }
                                        );
                                    } else {
                                this.currentView.render();

                                $("body").trigger("europass:waiting:indicator:hide");
                            }

                        },
                /**
                 * Retrieves the view path based on the selected location
                 */
                findViewPath: function (location) {
                    //TO BE OVERRIDEN
                },
                /**
                 * Clean up the specifics of this tep
                 * @param callback
                 * @param scope
                 */
                cleanup: function () {
//			console.log("WizardDoAction: cleanup");
                    var location = this.model.getLocation();

                    //Reset the complete status
                    this.isComplete = false;
                    //Hide the job portals fieldset
                    this.jobPortals.hide();
                    //Hide the complete button
                    this.completeBtn.hide();
                    //Hide the connect button
                    this.connectBtn.hide();
                    //Clean up feedback area
                    this.cleanupFeedback();
                    //remove any error or success status info
                    this.setModalFeedbackClass(null);
                    //Remove class name to the upper modal
                    this.$el.closest(".modal").removeClass(location);
                    //Show the legend
                    this.$el.closest(".process").find("legend").first().show();
                    //Reset the enctype of the form
                    this.$el.closest("form").removeAttr("enctype");
                },
                /**
                 * @Override
                 * Reset the class name for the form.name
                 * @param callback
                 * @param scope
                 */
                doPrevious: function (callback, scope) {
//			console.log("WizardDoAction: doPrevious");
                    this.cleanup();

                    WizardStep.prototype.doPrevious.apply(this, [callback, scope]);
                },
                /**
                 * @Override
                 * Ask the rendered view to finish
                 */
                doFinish: function () {
                    if (!_.isEmpty(this.currentView) && _.isFunction(this.currentView.doFinish))
                        this.currentView.doFinish();
                },
                /**
                 * @Override
                 * Ask the rendered view to cancel
                 */
                doCancel: function () {
                    this.cleanup();
                    if (!_.isEmpty(this.currentView) && _.isFunction(this.currentView.doCancel))
                        this.currentView.doCancel();
                },
                /**
                 * As soon as the connect button is hit, delegate to the child view
                 */
                doConnect: function () {
                    if (!_.isEmpty(this.currentView) && _.isFunction(this.currentView.doConnect))
                        this.currentView.doConnect();
                },
                /**
                 * The child view has notified that it has successfully completed the process.
                 */
                onComplete: function () {
//			console.log("WizardDocAction:onComplete");
                    this.isComplete = true;
                    this._updateButtons();
                    this.showSuccessWarnings();
                },
                /**
                 * Enable/Show the connect button, when an error occurs
                 */
                enableConnect: function (error) {
                    this.enableButton(this.connectBtn);
                    this.connectBtn.show();
                    //add error status info
                    if (!_.isUndefined(error) && error === true)
                        this.setModalFeedbackClass("error");
                },
                /**
                 * Enable/Show the connect button, when an error occurs
                 */
                enableConnectBtn: function () {
                    this.enableButton(this.connectBtn);
                    this.connectBtn.show();
                },
                /**
                 * Disable the connect button 
                 */
                disableConnect: function () {
                    this.disableButton(this.connectBtn);
                },
                /**
                 * Set the text properly and disable it
                 */
                prepareConnect: function () {
                    this.connectBtn.html("<span>" + (GuiLabel["buttons.wizard.connect"] || "Connect to service") + "</span>");
                    this.enableConnect();
                    //remove any error or success status info
                    this.setModalFeedbackClass(null);
                },
                /**
                 * Hide the connect button, on a successful outcome
                 */
                hideConnect: function () {
                    //add success status info
                    this.setModalFeedbackClass("success");
                    this.connectBtn.hide();

                },
                showSuccessWarnings: function () {

                    var isExport = this.currentViewPath.toLowerCase().indexOf("views/download/") === 0;
                    var partnerViewPath = this.currentView.parentView.findViewPath(this.model.LOCATION.PARTNERS); //TODO add null checks

                    if (isExport) {
                        if (WindowConfig.survey === "true" && this.currentViewPath !== partnerViewPath) //EWA 1654 Add survey link in export wizard
                            this.getMessageContainer().trigger("europass:message:show", ["warning", Notification["export.wizard.success.survey"]]);
                    }
                },

                showMacOsWarning: function () {
                    if (Utils.isMacOS() && this.$el.attr("id") === "export-wizard-step-4" &&
                            this.model.attributes.FileFormat === "pdf") {
                        this.getMessageContainer().trigger("europass:message:show",
                                ["warning", Notification["export.wizard.pdf.macos.warning"]]);
                    }
                },

                checkPopUp: function (view) {
                    var self = this;
                    var timeOutMilis = 500;

                    setTimeout(function () {
                        setTimeout(function () {	//seems like FF needs another setTimeout in order to make tryOpeningPopUp() work



//			$.ajax({
//				
//				url: "",
//				async: false,
//				data:{},
//				success: function(response){
                            var allowed = Utils.tryOpeningPopUp();

                            if (allowed === false) {
                                $("body").trigger("europass:waiting:indicator:hide");
                                self.cleanupFeedback();
                                self.getMessageContainer().trigger("europass:message:show", ["error", Notification["skillspassport.wizard.cloud.popup.blocked"]]);
//						$( view ).trigger("europass:popup:disabled");
                                self.enableConnect();
                            } else {
                                $(view).trigger("europass:popup:enabled");
                            }
//				}
//			});
//			
//			return allowed;
                        }, timeOutMilis);
                    }, timeOutMilis);
                }
            });

            return WizardDoActionStep;
        }
        );