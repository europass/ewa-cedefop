define(
        [
            'views/compose/ComposeView',
            'underscore',
            'hbs!templates/compose/cl/enclosed'
        ],
        function (ComposeView, _, HtmlTemplate) {
            var EnclosedComposeView = ComposeView.extend({
                htmlTemplate: HtmlTemplate

                , applyTooltip: function (model) {
                    var modelInfo = _.isFunction(this.model.info) ? this.model.info() : null;
                    modelInfo.isCLSectionEmpty(this.section) ?
                            this.$el.addClass("empty") :
                            this.$el.removeClass("empty");
                }
            });

            return EnclosedComposeView;
        }
);