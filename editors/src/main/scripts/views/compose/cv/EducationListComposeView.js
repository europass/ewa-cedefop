define(
        [
            'jquery',
            'underscore',
            'views/interaction/ListView',
            'views/compose/ComposeView',
            'hbs!templates/compose/cv/educationlist'
        ],
        function ($, _, ListView, ComposeView, HtmlTemplate) {

            var EducationListComposeView = ComposeView.extend({

                htmlTemplate: HtmlTemplate

                , events: _.extend({
                    "click :button:not(.inactive).sort-move-up": "sortMoveUp",
                    "click :button:not(.inactive).sort-move-down": "sortMoveDown",
                    "click :button.sort-move-top": "sortMoveTop",
                    "click .autoSort.byDate": "autoSort"
                }, ComposeView.prototype.events)

                , onInit: function (options) {
                    this.model.bind("model:prefs:data:format:changed", this.dateFormatChanged, this);

                    ComposeView.prototype.onInit.apply(this, [options]);

                }
                , onClose: function () {
                    ComposeView.prototype.onClose.apply(this);

                    this.model.unbind("model:prefs:data:format:changed", this.dateFormatChanged);
                }

                , enableFunctionalities: function (model) {

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
                 * Re-render only if the  date format change event was initiated by a view other than the current.
                 */
                , dateFormatChanged: function (relSection) {
                    if (relSection.indexOf(this.section) === 0) {
                        return false;
                    } else {
                        this.reRender(this.section);
                    }
                }

                , autoSort: function (event) {

                    var eduJsonPath = "SkillsPassport.LearnerInfo.Education";
                    var educationexperiences = $(this.model.get(eduJsonPath));

                    if (educationexperiences.length <= 1)
                        return;

                    var data = {};
                    educationexperiences.each(function (idx, educationexperience) {

                        var from = (!_.isUndefined(educationexperience.Period)) ? educationexperience.Period.From : undefined;
                        var to = (!_.isUndefined(educationexperience.Period)) ? educationexperience.Period.To : undefined;

                        data[idx] = {"item": educationexperience, "from": from, "to": to};
                    });

                    $(this.el).trigger("europass:autosort:date", [data, eduJsonPath, this.el]);
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

            return EducationListComposeView;
        }
);