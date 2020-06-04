define(
        [
            'jquery',
            'underscore',
            'views/compose/ComposeView',
            'hbs!templates/compose/cv/headline'
        ],
        function ($, _, ComposeView, HtmlTemplate) {

            var HeadlineComposeView = ComposeView.extend({
                htmlTemplate: HtmlTemplate

                , onInit: function (options) {
                    this.model.bind("content:changed:LearnerInfo.Identification.Demographics.Gender.Code", this.genderChanged, this);

                    ComposeView.prototype.onInit.apply(this, [options]);
                }
                , onClose: function () {
                    ComposeView.prototype.onClose.call(this);

                    this.model.unbind("content:changed:LearnerInfo.Identification.Demographics.Gender.Code", this.genderChanged);
                }
                , genderChanged: function (event, key) {
                    var headDescriptionJsonPath = "SkillsPassport.LearnerInfo.Headline.Description";
                    var map = this.model.translation().chooseOccupationMap(key);

                    var headDescription = this.model.get(headDescriptionJsonPath);
                    if (headDescription === undefined || headDescription === null) {
                        return false;
                    }

                    var code = headDescription.Code;
                    if (code === undefined || code === null) {
                        return false;
                    }

                    var newLabel = map.get(code);

                    if (newLabel === undefined || newLabel === null || newLabel === "") {
                        return false;
                    }
                    var attr = headDescriptionJsonPath + ".Label";
                    this.model.set(attr, newLabel, {silent: true});
                    //Compose View needs to be updated. Therefore we trigger this event
                    this.model.trigger("model:content:changed", this.section);
                }

                , applyTooltip: function (model) {
                    var modelInfo = _.isFunction(this.model.info) ? this.model.info() : null;
                    var sectionKey = this.section.substring(this.section.indexOf("LearnerInfo.") + "LearnerInfo.".length, this.section.length);
                    modelInfo.isSectionEmpty(sectionKey) ?
                            this.$el.addClass("empty") :
                            this.$el.removeClass("empty");
                }
            });

            return HeadlineComposeView;
        }
);