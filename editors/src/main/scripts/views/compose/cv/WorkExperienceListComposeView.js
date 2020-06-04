define(
        [
            'jquery',
            'underscore',
            'views/interaction/ListView',
            'views/compose/ComposeView',
            'hbs!templates/compose/cv/workexperiencelist'
        ],
        function ($, _, ListView, ComposeView, HtmlTemplate) {

            var WorkExperienceListComposeView = ComposeView.extend({
                htmlTemplate: HtmlTemplate

                , events: _.extend({
                    "click :button:not(.inactive).sort-move-up": "sortMoveUp",
                    "click :button:not(.inactive).sort-move-down": "sortMoveDown",
                    "click :button.sort-move-top": "sortMoveTop",
                    "click .autoSort.byDate": "autoSort"
                }, ComposeView.prototype.events)

                , onInit: function (options) {
                    this.model.bind("model:prefs:data:format:changed", this.dateFormatChanged, this);

                    this.model.bind("content:changed:LearnerInfo.Identification.Demographics.Gender.Code", this.genderChanged, this);

                    ComposeView.prototype.onInit.apply(this, [options]);

                }
                , onClose: function () {
                    ComposeView.prototype.onClose.apply(this);

                    this.model.unbind("model:prefs:data:format:changed", this.dateFormatChanged);

                    this.model.unbind("content:changed:LearnerInfo.Identification.Demographics.Gender.Code", this.genderChanged);
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

                    /*this.$el.find(".sortable.compose-list").each ( function( idx, el){
                     var list = $(el);
                     //Sortable when the list contains more than 1 item.
                     if ( list.find("> li.list-item").length > 1 ){
                     var sortView = new SortView({
                     el : list,
                     model : model
                     });
                     that.addToViewsIndex( sortView );
                     }
                     });*/
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

                , genderChanged: function (event, key) {
                    var workJsonPath = "SkillsPassport.LearnerInfo.WorkExperience";
                    var map = this.model.translation().chooseOccupationMap(key);

                    var workexperiences = $(this.model.get(workJsonPath));
                    var length = workexperiences.length;
                    var that = this;
                    workexperiences.each(function (idx, workexperience) {
                        if (workexperience === undefined || workexperience === null) {
                            return false;
                        }
                        var position = workexperience.Position;
                        if (position === undefined || position === null) {
                            return false;
                        }

                        var code = position.Code;
                        if (code === undefined || code === null) {
                            return false;
                        }

                        var newLabel = map.get(code);
                        if (newLabel === undefined || newLabel === null || newLabel === "") {
                            return false;
                        }

                        var attr = workJsonPath + "[" + idx + "].Position.Label";
                        that.model.set(attr, newLabel, (idx === length - 1) ? null : {silent: true});
                        //Compose View needs to be updated. Therefore we trigger this event
                        that.model.trigger("model:content:changed", that.section);
                    });
                }

                , autoSort: function (event) {

                    var workJsonPath = "SkillsPassport.LearnerInfo.WorkExperience";
                    var workexperiences = $(this.model.get(workJsonPath));

                    if (workexperiences.length <= 1)
                        return;

                    var data = {};
                    workexperiences.each(function (idx, workexperience) {

                        var from = (!_.isUndefined(workexperience.Period)) ? workexperience.Period.From : undefined;
                        var to = (!_.isUndefined(workexperience.Period)) ? workexperience.Period.To : undefined;

                        data[idx] = {"item": workexperience, "from": from, "to": to};
                    });

                    $(this.el).trigger("europass:autosort:date", [data, workJsonPath, this.el]);
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
            return WorkExperienceListComposeView;
        }
);