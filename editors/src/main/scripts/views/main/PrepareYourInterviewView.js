define(
        [
            'jquery',
            'jqueryui',
            'backbone',
            'hbs!templates/main/prepareYourInterview',
            'analytics/EventsController'

        ],
        function ($, jqueryui, Backbone, HtmlTemplate, Events) {

            var PrepareYourInterview = Backbone.View.extend({

                htmlTemplate: HtmlTemplate

                , sectionEl: $("#prepare-your-interview-btn")

                , scrollableSelector: "#PrepareYourInterviewForm .prepare-your-interview-content .ui-cv-interview-area"
                , contentSelector: "#PrepareYourInterviewForm .prepare-your-interview-content"
                , overlaySelector: "#PrepareYourInterviewForm"
                , event: new Events

                , events: {
                    "click #prepare-your-interview-btn": "showInterviewForm",
                    "click #PrepareYourInterviewForm button.close": "hideSettingsForm",
                    "click #PrepareYourInterviewForm button.interview-bot-btn": "hideSettingsForm"
                }
                , initialize: function () {
                    this.render();
                }
                , onClose: function () {
                    this.close();
                }
                , render: function () {
                    var context = {};
                    var html = HtmlTemplate(context);
                    this.$el.append(html);

                    this.contentArea = this.$el.find(this.contentSelector);
                    this.scrollableArea = this.$el.find(this.scrollableSelector);
                    this.overlay = this.$el.find(this.overlaySelector);
                }

                /**
                 * Show the modal
                 * Include a drawer effect, sliding from left to right
                 */
                , showInterviewForm: function (event) {
                    this.event.openPrepareYourInterviewModal();
                    var _that = this;
                    var _overlay = this.overlay;
                    var _area = this.contentArea;
                    var children = (_area !== undefined ? _area.children() : undefined);

                    $("body").trigger("europass:waiting:indicator:show");

                    _overlay.toggleClass("visible", function () {
                        $(this).animate({"background-color": "rgba(0,0,0,0.7)"}, 400);
                        if (children !== undefined) {
                            children.addBack().show('slide', {direction: "left", easing: "easeInSine"}, 400, function () {
                                //Making the vertical overflow auto for overflow-y scrolling if needed, while the modal is open
                                _area.find(".ui-cv-interview-area").css("overflow-y", "auto");
                                _that.sectionEl.trigger("europass:drawer:opened");
                            });
                        }
                        $("body").trigger("europass:waiting:indicator:hide");
                    });
                }
                /**
                 * Hide the modal
                 * Include a drawer effect, sliding from right to left
                 */
                , hideSettingsForm: function (event) {

                    var _overlay = this.overlay;
                    var _area = this.contentArea;
                    var children = (_area !== undefined ? _area.children() : undefined);

                    if (children !== undefined) {
                        children.hide('slide', {direction: "left", easing: "easeInSine"}, 400);
                    }

                    _area.hide('slide', {direction: "left", easing: "easeInSine"}, 400);
                    _area.find(".ui-cv-interview-area").css("overflow-y", "hidden");
                    _overlay.animate({"background-color": "rgba(0,0,0,0)"}, 400, function () {
                        $(this).toggleClass("visible");
                    });
                }

            });

            return PrepareYourInterview;
        }
);