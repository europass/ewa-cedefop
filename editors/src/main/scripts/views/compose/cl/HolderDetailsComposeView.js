define(
        [
            'jquery',
            'underscore',
            'views/compose/ComposeView',
            'hbs!templates/compose/cl/holderDetails',
            'views/prefs/PrintingPreferencesView'//	'europass/TabletInteractionsView'
        ],
        function ($, _, ComposeView, HtmlTemplate, PrintingPreferencesView) {//, TabletInteractionsView

            var HolderDetailsComposeView = ComposeView.extend({
                htmlTemplate: HtmlTemplate

                , events: _.extend({
                    "click 		:button.names.switch": "handlePersonNameOrder"
                            //"touchstart :button.names.switch": "handlePersonNameOrder"  //prevent .opens-modal-form listener to fire first on tablet touch
                }, ComposeView.prototype.events)

                , prefsView: null

                , onInit: function (options) {
                    this.prefsView = new PrintingPreferencesView({
                        model: this.model
                    });

                    ComposeView.prototype.onInit.apply(this, [options]);

                }
                , onClose: function () {
                    ComposeView.prototype.onClose.apply(this);
                    if (_.isObject(this.prefsView) && _.isFunction(this.prefsView.close))
                        this.prefsView.close();
                    delete this.prefsView;
                }
                //check if tablet then emulate hover effects, otherwise run swap normally
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

                    this.triggerPrefsOrderChange("ECL");

                    // Switch person name ALSO for other document types !!
                    this.triggerPrefsOrderChange("ECV");
                    this.triggerPrefsOrderChange("ELP");
                }
                , triggerPrefsOrderChange: function (documentType) {

                    this.prefsView.prefsDocument = documentType;

                    var switched = this.prefsView.switchPersonNames(documentType);
                    if (switched === true) {
                        this.model.trigger("prefs:order:changed", this.section);
                    }
                }
                , applyTooltip: function (model) {
                    var modelInfo = _.isFunction(this.model.info) ? this.model.info() : null;
                    //var sectionKey = this.section.substring(this.section.indexOf("LearnerInfo.")+"LearnerInfo.".length,this.section.length);
                    modelInfo.isIdentificationEmpty() ?
                            this.$el.addClass("empty").find(".edit").remove() :
                            this.$el.removeClass("empty");
                }
            });

            return HolderDetailsComposeView;
        }
);