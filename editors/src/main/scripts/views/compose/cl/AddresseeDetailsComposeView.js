define(
        [
            'views/compose/ComposeView',
            'underscore',
            'hbs!templates/compose/cl/addressee'
        ],
        function (ComposeView, _, HtmlTemplate) {

            var AddresseeDetailsComposeView = ComposeView.extend({
                htmlTemplate: HtmlTemplate,

                applyTooltip: function (model) {
                    var modelInfo = _.isFunction(this.model.info) ? this.model.info() : null;
                    modelInfo.isCLSectionEmpty(this.section) ?
                            this.$el.addClass("empty") :
                            this.$el.removeClass("empty");
                }
            });
            return AddresseeDetailsComposeView;
        }
);