define(
        [
            'jquery',
            'underscore',
            'views/interaction/ListView',
            'views/compose/ComposeView',
            'hbs!templates/compose/cv/foreignLanguageList'
        ],
        function ($, _, ListView, ComposeView, HtmlTemplate) {

            var ForeignLanguageListComposeView = ComposeView.extend({

                htmlTemplate: HtmlTemplate

                , events: _.extend({
                    "click :button:not(.inactive).sort-move-up": "sortMoveUp",
                    "click :button:not(.inactive).sort-move-down": "sortMoveDown",
                    "click :button.sort-move-top": "sortMoveTop"
                }, ComposeView.prototype.events)

                , enableFunctionalities: function (model) {
                    ComposeView.prototype.enableFunctionalities.apply(this, [model]);

                    var that = this;
                    this.$el.find("table.languages > tbody").each(function (idx, el) {
                        var list = $(el);
                        //Sortable when the list contains more than 1 item.
                        if (list.find("> tr.list-item").length > 1) {
                            var listView = new ListView({
                                el: list,
                                model: model,
                                type: "tbody"
                            });
                            that.addToViewsIndex(listView);
                        }
                    });
                }
                //@Override
                , reRender: function (relSection, origin) {
                    //console.log("CV Foreign Langs Compose View ::: reRender '"+this.section+"'");
                    if (relSection === "SkillsPassport.LearnerInfo.Skills.Linguistic.ForeignLanguage") {
                        if (arguments !== null && arguments !== undefined) {
                            if (arguments.length > 0) {
                                //get argument for sort target position
                                var targetListItem = arguments[2];
                            }
                        }
                    }

                    if (relSection.indexOf(this.section) === 0) {
                        //console.log("CV Foreign Langs Compose View ::: do render view");
                        this.render(this.reRenderIndicator, [this.doTransition(origin), targetListItem]);
                    }
                }

                , applyTooltip: function (model) {
                    var modelInfo = _.isFunction(this.model.info) ? this.model.info() : null;
                    var sectionKey = this.section.substring(this.section.indexOf("LearnerInfo.") + "LearnerInfo.".length, this.section.length);
                    modelInfo.isSectionEmpty(sectionKey) ?
                            this.$el.addClass("empty") :
                            this.$el.removeClass("empty");
                }
                , sortMoveUp: function (event) {
                    this.$el.trigger("europass:sort:list:moveUp", [event.target, true]);
                }
                , sortMoveDown: function (event) {
                    this.$el.trigger("europass:sort:list:moveDown", [event.target, true]);
                }
                , sortMoveTop: function (event) {
                    this.$el.trigger("europass:sort:list:moveTop", [event.target, true]);
                }
            });

            return ForeignLanguageListComposeView;
        });