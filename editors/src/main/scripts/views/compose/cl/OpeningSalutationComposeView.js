define(
        [
            'views/compose/ComposeView',
            'underscore',
            'hbs!templates/compose/cl/openingSalutation'
        ],
        function (ComposeView, _, HtmlTemplate) {

            var OpeningSalutationComposeView = ComposeView.extend({

                htmlTemplate: HtmlTemplate

                , applyTooltip: function (model) {
                    var modelInfo = _.isFunction(this.model.info) ? this.model.info() : null;
                    modelInfo.isCLSectionEmpty(this.section) ?
                            this.$el.addClass("empty") :
                            this.$el.removeClass("empty");
                }
            });

            return OpeningSalutationComposeView;
        }
);