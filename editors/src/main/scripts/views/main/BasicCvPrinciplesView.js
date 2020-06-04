/**
 * This view is bound to the parent division wrapping the three radio buttons controlling the Gender information.
 * The View is configured to receive:
 * 1. the el element (div.choice.#Control:LearnerInfo.Identification.Demographics.Gender)
 * 2. the map containing the translation of the gender codes
 * 
 * This is initiated during PersonalInfoFormView.enableFunctionalities();
 *  
 */
define(
        [
            'jquery',
            'jqueryui',
//		EWA-1811
//		'underscore',
            'backbone',
            'hbs!templates/main/basicprinciples'

        ],
        function ($, jqueryui,
//			EWA-1811
//			_,
                Backbone, HtmlTemplate) {

            var BasicCvPrinciples = Backbone.View.extend({

                htmlTemplate: HtmlTemplate

                , sectionEl: $("#basic-principles-btn")

                , scrollableSelector: "#BasicCvPrinciplesForm .basic-principles-content .ui-cv-principles-area"
                , contentSelector: "#BasicCvPrinciplesForm .basic-principles-content"
                , overlaySelector: "#BasicCvPrinciplesForm"


                , events: {
                    "click #basic-principles-btn": "showPrinciplesForm",
                    "click #BasicCvPrinciplesForm button.close": "hideSettingsForm",
                    "click #BasicCvPrinciplesForm button.principles-bot-btn": "hideSettingsForm"
                }
                , initialize: function () {
                    this.render();
                }
                , onClose: function () {
//				this.clickableAreaView.close();
                    this.close();
                }
                , render: function () {
//				var that = this;
                    var context = {};
                    //Html of the Settings Modal - html is appended and it is hidden
                    var html = HtmlTemplate(context);
                    this.$el.append(html);

                    this.contentArea = this.$el.find(this.contentSelector);
                    this.scrollableArea = this.$el.find(this.scrollableSelector);
                    this.overlay = this.$el.find(this.overlaySelector);

                }

                /**
                 * Show the modal where the local storage and the europass logo inclusion can be switched
                 * Include a drawer effect, sliding from left to right
                 */
                , showPrinciplesForm: function (event) {
//				console.log("show");
//               this.arrangeLocalStorage();

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
                                _area.find(".ui-cv-principles-area").css("overflow-y", "auto");
                                _that.sectionEl.trigger("europass:drawer:opened");
                            });
                        }
                        $("body").trigger("europass:waiting:indicator:hide");
                    });
                }
                /**
                 * Hide the settings modal
                 * Include a drawer effect, sliding from right to left
                 */
                , hideSettingsForm: function (event) {
//				console.log("hide");
                    var _overlay = this.overlay;
                    var _area = this.contentArea;
                    var children = (_area !== undefined ? _area.children() : undefined);

                    if (children !== undefined) {
                        children.hide('slide', {direction: "left", easing: "easeInSine"}, 400);
                    }
//				console.log("set visible");
                    _area.hide('slide', {direction: "left", easing: "easeInSine"}, 400);
                    _area.find(".ui-cv-principles-area").css("overflow-y", "hidden");
                    _overlay.animate({"background-color": "rgba(0,0,0,0)"}, 400, function () {
                        $(this).toggleClass("visible");
                    });
                }

            });

            return BasicCvPrinciples;
        }
);