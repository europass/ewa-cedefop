define(
        [
            'require',
            'jquery',
            'underscore',
            'views/WizardDoActionStep'
        ],
        function (
                require,
                $, _,
                WizardDoActionStep
                ) {
            var DoImportStep = function (options) {
                WizardDoActionStep.apply(this, [options]);
            };

            DoImportStep.prototype = {
                events: {
                    "europass:wizard:import:complete": "onComplete"
                },
                /**
                 * Retrieves the view path based on the selected location
                 */
                findViewPath: function (location) {
                    switch (location) {
                        case this.model.LOCATION.LOCAL:
                            return "views/upload/LocalFileUploadView";

                        case this.model.CLOUD_STORAGE.DROPBOX:
                            return "views/upload/DropboxUploadView";

                        case this.model.CLOUD_STORAGE.GOOGLEDRIVE:
                            return "views/upload/GoogleDriveUploadView";

                        case this.model.CLOUD_STORAGE.ONEDRIVE:
                            return "views/upload/OneDriveUploadView";

                        case this.model.LOCATION.LINKEDIN:
                            return "views/upload/LinkedInUploadView";

                        default :
                            return null;
                    }
                },
                /**
                 * As soon as the complete button is hit
                 */
                doCancel: function () {
                    if (_.isObject(this.currentView) && _.isFunction(this.currentView.doCancel))
                        this.currentView.doCancel();

                    WizardDoActionStep.prototype.doCancel.call(this);
                },
                /**
                 * @Override
                 * As soon as the complete button is hit, delegate to the child view
                 */
                doComplete: function () {
                    this.currentView.doComplete();
                },

                canProceed: function () {
                    return true;
                },

                /**
                 * Prepare the context for rendering the child views
                 */
                getContext: function () {
                    var esp = this.model.getSkillsPassport();

                    var isEmpty = esp.info().isCompletelyEmpty();
//			console.log("isEmpty: " + isEmpty );
                    if (isEmpty === false)
                        return {dataExists: true};
                    else
                        return {};

                },
                /**
                 * @Override
                 */
                cleanup: function () {
                    this.enableConnect();
                    if ($.isFunction(this.currentView.clearDropzone))
                        this.currentView.clearDropzone();

                    WizardDoActionStep.prototype.cleanup.call(this);
                }
            };

            DoImportStep.prototype = $.extend(
                    //true, 
                            {},
                            WizardDoActionStep.prototype,
                            DoImportStep.prototype
                            );

                    return DoImportStep;
                }
        );