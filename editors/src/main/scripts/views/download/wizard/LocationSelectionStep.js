define(
        [
            'jquery',
            'underscore',
            'views/WizardStep',
            'hbs!templates/download/wizard/step3'//,'hbs!templates/download/wizard/step3b'
        ],
        function ($, _, WizardStep, Template) {

            var LocationSelectionStep = WizardStep.extend({
                //This step need not be re-rendered to capture any changes in the model
                forceRerender: false,
                extraAlreadyRendered: false,

                downloadView: null,

                htmlTemplate: Template,

                events: _.extend({
                    "change :radio": "changed",
                    "click label.formfield-label[for!='Export_Option_Loc_cloud']": "twoStepClick",
                    "click label.formfield-label[for='Export_Option_Loc_cloud']": "adaptStep"
                            //On extra cloud
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

                prepareExtraContext: function () {
                    return {
                        cloudOptions: this.model.europassCloudOptions()
                    };
                },
                /**
                 * @Override
                 * Decide on the status of the buttons
                 */
                updateButtons: function () {
                    this.prevBtn.show();
                    this._updateButtons();
                },
                _updateButtons: function () {
                    var checked = this.$el.find(":radio:checked");
                    if (checked.length > 0) {
//				if ( this.model.LOCATION.LOCAL === checked.val() ){
//					this.finishBtn.show();
//					this.nextBtn.hide();
//				} else {
                        this.finishBtn.hide();
                        this.nextBtn.show();
//				}
                    }
                    return checked;
                },
                /**
                 * @Override
                 * Save changes to the model
                 */

                saveModel: function () {
                    var checked = this.$el.find(":radio:checked");
                    var location = checked.val();


                    this.model.setLocation(location);
                    this.model.setCloudStorage(location);
                },
                /**
                 * Decide whether it can proceed to next step
                 */
                canProceed: function () {
                    return this.model.hasLocation();
                    return this.model.hasCloudStorage();
                },
                /**
                 * Location option changed
                 */
                changed: function (event) {
                    this._updateButtons();
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
                        this.renderExtraStep("export");
                    }
                },
                checkCloudOption: function () {
                    var checked = this.$el.find("#Export_Option_Loc_cloud");
                    if (checked !== undefined && checked !== null && checked.length > 0 && checked.prop("checked") == true) {
                        return true;
                    }
                },

                /**
                 * The finish / Download button is clicked now
                 * We need to do a download to the local pc
                 * 
                 * @param event
                 */
                /*		doFinish: function( event ){
                 this.model.setLocation("local");
                 
                 //Disable btn for as long as the download lasts
                 this.disableButton( this.finishBtn );
                 //Enable the waiting indicator
                 
                 if ( this.downloadView === null ){
                 var _that = this;
                 require(
                 [ "views/download/DownloadView" ], 
                 //SUCCESS
                 function( DownloadView ){
                 if ( $.isFunction( DownloadView ) ){
                 
                 var downloadView = new DownloadView({
                 el: _that.$el,
                 model: _that.model.getSkillsPassport(),
                 info : _that.model,
                 messageContainer: _that.getMessageContainer(),
                 parentView: _that
                 });
                 //Set to the new View!
                 _that.downloadView = downloadView;
                 _that.downloadView.render();
                 _that.downloadView.doFinish();
                 }
                 },
                 //ERROR
                 function( args ){
                 _that.getMessageContainer().trigger("europass:message:show", 
                 [ "error", 
                 ( Notification["export.wizard.loading.step.failed"] || "There was a problem proceeding with the download. Please refresh your browser and try again. If the problem persists, contact Europass Team")
                 ]
                 );
                 }
                 );
                 } else {
                 this.downloadView.doFinish();
                 }
                 },*/
                /**
                 * @Override
                 * @param callback
                 * @param scope
                 */
                doPrevious: function (callback, scope) {
                    if (this.extraAlreadyRendered === true) {
                        this.saveModel();
                        this.render();
                        var checked = this.$el.find("#Export_Option_Loc_cloud");
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
                        this.renderExtraStep("export");
                    } else {
                        this.cleanupFeedback();
                        WizardStep.prototype.doNext.apply(this, [callback, scope]);
                    }
                }
            });

            return LocationSelectionStep;
        }
);
