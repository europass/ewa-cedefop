define(
        [
            'jquery',
            'underscore',
            'views/compose/ComposeView',
            'hbs!templates/compose/cv/motherTongue'
        ],
        function ($, _, ComposeView, HtmlTemplate) {

            var MotherTongueComposeView = ComposeView.extend({

                htmlTemplate: HtmlTemplate

                        /**
                         * 	@Override method from views.compose.ComposeView
                         * 
                         * When this specific section requires re-rendering it will delegate to the parentView instead.
                         * This is done to accommodate the requirement that when:
                         * i) no content exists and new content is added the entire document view must re-render
                         *    in order to display the section, which is originally non-existent.
                         * ii) content exists but now it is removed so it becomes empty. In this case
                         *     the entire document view must re-render in order to NOT display the section.
                         */
                , reRender: function (relSection, origin) {
                    if (relSection === this.section) {
                        if (_.isObject(this.parentView)) {
                            this.parentView.setRenderIndicationTarget(this.options.el);
                            this.parentView.onReRendering(origin);
                        }
                    }
                }

                , applyTooltip: function (model) {
                    var section = this.model.get(this.section);
                    section === undefined || section === null
                            || ($.isArray(section) && section.length === 0) ?
                            this.$el.addClass("empty") :
                            this.$el.removeClass("empty");
                }
            });

            return MotherTongueComposeView;
        }
);