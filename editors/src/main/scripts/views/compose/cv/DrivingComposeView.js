define(
        [
            'jquery',
            'underscore',
            'views/compose/ComposeView',
            'hbs!templates/compose/cv/driving'
        ],
        function ($, _, ComposeView, HtmlTemplate) {

            var DrivingComposeView = ComposeView.extend({

                htmlTemplate: HtmlTemplate,

                enableFunctionalities: function (model) {
                    var list = this.model.get(this.section + ".Description");
                    this.numOfItems = _.isArray(list) ? list.length : 0;

                    ComposeView.prototype.enableFunctionalities.apply(this, [model]);
                },
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
                reRender: function (relSection, origin) {
                    if (relSection === this.section) {
                        var doTransition = this.doTransition(origin);

                        var driving = this.model.get(this.section);
                        var listDescription = this.model.get(this.section + ".Description");

                        var wasEmpty = !_.isEmpty(driving) && (this.numOfItems === undefined || this.numOfItems === 0);

                        this.numOfItems = _.isArray(listDescription) ? listDescription.length : 0;

                        var isEmpty = _.isEmpty(driving) && this.numOfItems >= 0 && _.isEmpty(listDescription);

                        if (!wasEmpty && !isEmpty) {
                            this.render(this.reRenderIndicator, [doTransition]);
                            return;
                        }

                        if (_.isObject(this.parentView) && (wasEmpty || isEmpty)) {
                            if (wasEmpty) {
                                this.parentView.setRenderIndicationTarget(this.options.el);
                            }
                            this.parentView.onReRendering(origin);
                        }
                    }
                }

                , applyTooltip: function (model) {
                    var modelInfo = _.isFunction(this.model.info) ? this.model.info() : null;
                    var sectionKey = this.section.substring(this.section.indexOf("LearnerInfo.") + "LearnerInfo.".length, this.section.length);
                    modelInfo.isSectionEmpty(sectionKey) ?
                            this.$el.addClass("empty") :
                            this.$el.removeClass("empty");
                }
            });

            return DrivingComposeView;
        }
);