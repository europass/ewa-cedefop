define(
        [
            'module',
            'jquery',
            'underscore',
            'Utils',
            'routers/SkillsPassportRouterInstance',
            'europass/GlobalDocumentInstance'
        ]
        , function (module, $, _, Utils, SkillsPassportRouter, GlobalDocument) {

            /**
             * Constructor
             * @param relatedController: the BackboneView which includes this Controller, e.g. OneDriveView
             * @param messageContainer: jQuery element
             */
            var LoadDocumentController = function (config) {
                this.relatedController = config.relatedController;
                this.modelUpdateEvent = config.modelUpdateEvent;
                this.modelUpdateMsgKey = config.modelUpdateMsgKey;
            };

            /**
             * The user has confirmed the load of the imported model
             */
            LoadDocumentController.prototype.load = function (json) {
                //Reload only if there is a locale!
                var locale = null;
                try {
                    locale = json.SkillsPassport.Locale;
                } catch (err) {
                }

                var decideRouteF = this.relatedController.decideAfterView;
                if (!_.isFunction(decideRouteF)) {
                    decideRouteF = function () {
                        return SkillsPassportRouter.decideView(this.relatedController.model, GlobalDocument.get());
                    };
                }

                if ((locale === undefined || locale === null || locale === "")
                        || (module.config().locale === locale)) {
                    //true: do translation!
                    this.relatedController.model.conversion().fromTransferable(json, true, true);


                    //Navigate -update the URL without creating an entry in the browser's history,
                    //set the replace option to true
                    //Router will not actually perform a navigation if the current route is the same as the one noted by this url
                    var navigateTo = decideRouteF.call(this);
                    SkillsPassportRouter.navigate(navigateTo, {trigger: true, replace: true});
                    this.relatedController.model.trigger(this.modelUpdateEvent, this.modelUpdateMsgKey);
                } else {
                    //Replace the existing model with the one loaded
                    //false: do not do translation, it will be done during reload!
                    this.relatedController.model.conversion().fromTransferable(json, false, true);
                    this.relatedController.updateConfigLocale(locale, decideRouteF.call(this));
                }
            };

            return LoadDocumentController;
        }
);