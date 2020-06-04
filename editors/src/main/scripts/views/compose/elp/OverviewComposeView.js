define(
        [
            'jquery',
            'underscore', //'Utils',
            'views/compose/ComposeView',
            'hbs!templates/compose/elp/overview',
            'views/prefs/PrintingPreferencesView',
            'ModalFormInteractions'//'europass/TabletInteractionsView','i18n!localization/nls/Notification'	
        ],
        function ($, _, ComposeView, HtmlTemplate, PrintingPreferencesView, ModalFormInteractions) {

            var OverviewComposeView = ComposeView.extend({

                htmlTemplate: HtmlTemplate

                , tablets: {}

                , events: _.extend({
                    "click 		:button.names.switch": "handlePersonNameOrder"
                            //"touchstart :button.names.switch": "handlePersonNameOrder"
                }, ComposeView.prototype.events)

                , prefsView: null

                , onInit: function (options) {
                    this.prefsView = new PrintingPreferencesView({
                        model: this.model
                    });
                    ComposeView.prototype.onInit.apply(this, [options]);
                }
                , openModalForm: function (event) {
                    ModalFormInteractions.openForm(event);
                }
                , onClose: function () {
                    ComposeView.prototype.onClose.apply(this);
                    if (_.isObject(this.prefsView) && _.isFunction(this.prefsView.close))
                        this.prefsView.close();
                    delete this.prefsView;
                }

                //update the Preferences
                , handlePersonNameOrder: function (event) {
                    if (this.prefsView !== undefined && this.prefsView !== null) {

                        // TODO CLEAN UP COMMENT On mobile not using 2 step tooltip anymore !!
                        // if ((this.isTablet && _.isFunction(this.tablets.handleTipSpot)) && this.tablets.handleTipSpot(event, "target") === false) {
                        // 	return false;
                        // }
                        this.switchPersonNameOrder();
                    }
                    return false;
                }
                , switchPersonNameOrder: function () {
                    //start the waiting indicator...
                    this.$el.trigger("europass:waiting:indicator:show");

                    this.triggerPrefsOrderChange("ELP");

                    // Switch person name ALSO for other document types !!
                    this.triggerPrefsOrderChange("ECL");
                    this.triggerPrefsOrderChange("ECV");
                }
                , triggerPrefsOrderChange: function (documentType) {

                    this.prefsView.prefsDocument = documentType;

                    var switched = this.prefsView.switchPersonNames(documentType);
                    if (switched === true) {
                        this.model.trigger("prefs:order:changed", this.section);
                    }
                }
            });

            return OverviewComposeView;
        }
);