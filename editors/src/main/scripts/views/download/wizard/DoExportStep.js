define(
        [
            'require',
            'jquery',
//		EWA-1811
//		 'underscore',
            'views/WizardDoActionStep'
        ],
        function (
                require,
                $,
//		EWA-1811
//		_, 
                WizardDoActionStep
                ) {
            var DoExportStep = function (options) {
                WizardDoActionStep.apply(this, [options]);
            };

            DoExportStep.prototype = {
                //This step need not be re-rendered to capture any changes in the model
                forceRerender: false,

                events: {
                    "europass:wizard:export:complete": "onComplete"
                },
                /**
                 * Retrieves the view path based on the selected location
                 */
                findViewPath: function (location) {
                    switch (location) {
                        case this.model.LOCATION.EMAIL:
                            return "views/download/RequestEmailView";

                        case this.model.LOCATION.DROPBOX:
                            return "views/download/DropboxStoreView";

                        case this.model.LOCATION.GOOGLEDRIVE:
                            return "views/download/GoogleDriveStoreView";

                        case this.model.LOCATION.ONEDRIVE:
                            return "views/download/OneDriveStoreView";

                        case this.model.LOCATION.LOCAL:
                            return "views/download/DownloadView";

                        case this.model.LOCATION.PARTNERS:
                            return "views/download/PartnersView";

                        case this.model.LOCATION.EURES:
                            return "views/download/JobPortalsView";

                        default :
                            return null;
                    }
                }
            };

            DoExportStep.prototype = $.extend(
                    //true, 
                            {},
                            WizardDoActionStep.prototype,
                            DoExportStep.prototype
                            );

                    return DoExportStep;
                }
        );