define(
        [
            'jquery',
            'underscore',
            'views/compose/ComposeView',
            'views/interaction/ListView',
            'hbs!templates/compose/cv/additionalinfo',
            'Utils'
        ],
        function ($, _, ComposeView, ListView, HtmlTemplate, Utils) {

            var AdditionalInfoComposeView = ComposeView.extend({

                htmlTemplate: HtmlTemplate

                , events: _.extend({
                    "click :button:not(.inactive).sort-move-up": "sortMoveUp",
                    "click :button:not(.inactive).sort-move-down": "sortMoveDown",
                    "click :button.sort-move-top": "sortMoveTop"
                }, ComposeView.prototype.events)

                , enableFunctionalities: function (model) {
                    var list = this.model.get(this.section);
                    this.numOfItems = _.isArray(list) ? list.length : 0;

                    ComposeView.prototype.enableFunctionalities.apply(this, [model]);

                    var that = this;
                    //List View for all section > compose-list
                    this.$el.find(".sortable.compose-list").each(function (idx, el) {
                        var list = $(el);
                        //Sortable when the list contains more than 1 item.
                        if (list.find("> li.list-item").length > 1) {
                            var listView = new ListView({
                                el: list,
                                model: model
                            });
                            that.addToViewsIndex(listView);
                        }
                    });
                }

                /**
                 * 	@Override method from views.compose.ComposeView
                 * 
                 * When this specific section requires re-rendering it will delegate to the parentView instead.
                 * This is done to accommodate the requirement that when:
                 * i) no content exists and new content is added the entire document view must re-render
                 *    in order to display the section, which is originally non-existent.
                 * ii) content exists but the last list-item is removed so it becomes empty. In this case
                 *     the entire document view must re-render in order to NOT display the section.
                 */
                , reRender: function (relSection, origin) {
                    var listSection = Utils.getListSection(relSection);

                    if (listSection !== null) {
                        relSection = listSection;
                    }
                    if (relSection === this.section) {
                        var doTransition = this.doTransition(origin);
                        if (relSection === "SkillsPassport.LearnerInfo.Achievement") {
                            if (arguments !== null && arguments !== undefined) {
                                if (arguments.length > 0) {
                                    //get argument for sort target position
                                    var targetListItem = arguments[2];
                                }
                            }
                        }

                        var list = this.model.get(this.section);
                        var isFirst = !_.isEmpty(list)
                                && _.isArray(list)
                                && list.length === 1
                                && (this.numOfItems === undefined || this.numOfItems === 0);
                        var isEmpty = _.isEmpty(list) && this.numOfItems === 1;

                        this.numOfItems = !_.isEmpty(list) ? list.length : 0;

                        if (this.numOfItems <= 1 && list[0] === undefined)
                            isEmpty = true;

                        //Added/Modified a list item, not the first, nor the last
                        if (!isFirst && !isEmpty) {
                            this.render(this.reRenderIndicator, [doTransition, targetListItem]);
                            return;
                        }
                        if (_.isObject(this.parentView) && (isFirst || isEmpty)) {
                            if (isFirst) {
                                this.parentView.setRenderIndicationTarget(this.options.el);
                            }
                            this.parentView.onReRendering(origin);
                        }
                    }
                }
                , sortMoveUp: function (event) {
                    this.$el.trigger("europass:sort:list:moveUp", [event.target]);
                }
                , sortMoveDown: function (event) {
                    this.$el.trigger("europass:sort:list:moveDown", [event.target]);
                }
                , sortMoveTop: function (event) {
                    this.$el.trigger("europass:sort:list:moveTop", [event.target]);
                }
            });



            return AdditionalInfoComposeView;
        }
);